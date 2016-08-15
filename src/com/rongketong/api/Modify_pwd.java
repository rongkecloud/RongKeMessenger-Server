package com.rongketong.api;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.rongketong.utils.ApiErrorCode;
import com.rongketong.utils.CheckParameters;
import com.rongketong.utils.MysqlBaseManager;
import com.rongketong.utils.Tools;

/**
 *云视互动测试app：修改密码流程
 *需要传的参数为：
 *	ss:用户session(必填)
 *	oldpwd：旧密码(必填)
 *	newpwd: 新密码(必填)
 *
 *返回值：
 *	oper_result：对应的错误码
 *				0：成功
 *				1001:无效的session
 *				1005:密码错误
 *				9998:系统错误
 *				9999:参数错误
**/  
@WebServlet("/modify_pwd.php")
public class Modify_pwd extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger m_logger = Logger.getLogger(Modify_pwd.class);   


	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	@SuppressWarnings("unchecked")
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		// 参数校验
		HashMap<String,String> postParams = new HashMap<String,String>();	
    	HashMap<String,Boolean> params = new HashMap<String,Boolean>();
    	params.put("ss", true); //账号
    	params.put("oldpwd", true);//老密码
    	params.put("newpwd", true);//新密码
    	
    	CheckParameters check = new CheckParameters(request.getParameterMap(),params);	
    	postParams = check.postParamsToHashMap();
    	if(!check.paramCheckAndRetRes()){
    		m_logger.info(String.format("FAILED params=%s %s",postParams,"check parameters error"));		
    		response.getWriter().write( ApiErrorCode.echoErr(ApiErrorCode.API_ERR_MISSED_PARAMATER));
    		return;
    	}
    	String appSession = postParams.get("ss");
    	String oldpwd = postParams.get("oldpwd");
    	String newpwd = postParams.get("newpwd");
    	//参数格式校验
    	if(!Tools.checkSession(appSession) || !Tools.checkPwd(oldpwd) || !Tools.checkPwd(newpwd)){
    		m_logger.info(String.format("FAILED params=%s %s",postParams,"check parameters error"));
    		response.getWriter().write(ApiErrorCode.echoErr(ApiErrorCode.API_ERR_MISSED_PARAMATER));
    		return;
    	}
    	/**
    	 * 检查Session
    	 */
    	Map<String,String> accountInfo = null;
    	try {
    		accountInfo = MysqlBaseManager.checkSession(appSession);
			if(accountInfo == null){
				response.getWriter().write(ApiErrorCode.echoErr(ApiErrorCode.API_ERR_INVALID_SESSION));
				return;
			}
		} catch (SQLException e) {
			m_logger.info(String.format("FAILED params=%s %s",appSession,"check session error,error cause:"+e.getCause()));
			response.getWriter().write(ApiErrorCode.echoErr(ApiErrorCode.SYSTEM_ERR));
    		return;
		}
    	// 旧密码不一致时返回
    	if(!Tools.md5(oldpwd).equals(accountInfo.get("user_pwd"))){
    		response.getWriter().write(ApiErrorCode.echoErr(ApiErrorCode.API_ERR_PASSWD));
    		return;
    	}
    	
    	try {
			if(MysqlBaseManager.modifyPwd(accountInfo.get("user_account"), newpwd)){
				response.getWriter().write(ApiErrorCode.echoOk());
				return;
			}
		} catch (SQLException e) {
			m_logger.info(String.format("FAILED SQLException,error cause:"+e.getCause()));
		}
    	response.getWriter().write(ApiErrorCode.echoErr(ApiErrorCode.SYSTEM_ERR));
	}

}
