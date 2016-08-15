package com.rongketong.api;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.rongketong.api.Register;
import com.rongketong.utils.ApiErrorCode;
import com.rongketong.utils.CheckParameters;
import com.rongketong.utils.MysqlBaseManager;
import com.rongketong.utils.Tools;

/**
 *云视互动测试app：注册流程
 *需要传的参数为：
 *	account:用户名(必填)
 *	pwd：密码(必填)
 *	type: 用户类型(必填)
 *
 *返回值：
 *	oper_result：对应的错误码
 *				0：成功
 *				1004:账号已存在
 *				9998:系统错误
 *				9999:参数错误
**/ 
@WebServlet("/register.php")
public class Register extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger m_logger = Logger.getLogger(Register.class);


	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	@SuppressWarnings("unchecked")
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		//1 参数校验
		HashMap<String,String> postParams = new HashMap<String,String>();	
    	HashMap<String,Boolean> params = new HashMap<String,Boolean>();
    	params.put("account", true); //用户账号
    	params.put("pwd", true);//密码
    	params.put("type", true);//类型
    	
    	CheckParameters check = new CheckParameters(request.getParameterMap(),params);	
    	m_logger.info(String.format("INPUT params=%s", request.getParameterMap()));
    	postParams = check.postParamsToHashMap();
    	if(!check.paramCheckAndRetRes()){
    		m_logger.info(String.format("FAILED params=%s %s",postParams,"check parameters error"));
    		String ret=ApiErrorCode.echoErr(ApiErrorCode.API_ERR_ACCOUNT_EXIST);			
    		response.getWriter().write(ret);
    		return;
    	}
    	
    	String account = postParams.get("account");
    	String pwd = postParams.get("pwd");
    	String type = postParams.get("type");
    	String sdkpwd = Tools.getRandomStr(8);
    	account = account.toLowerCase();
    	try {
			if(MysqlBaseManager.isAccountExist(account)==false){
				m_logger.info(String.format("FAILED params=%s %s",postParams,"User already existed"));			
	    		response.getWriter().write(ApiErrorCode.echoErr(ApiErrorCode.API_ERR_ACCOUNT_EXIST));
	    		return;
			}
		} catch (SQLException e) {
			m_logger.info(String.format("FAILED params=%s","SQLException cause:"+e.getCause()));		
    		response.getWriter().write(ApiErrorCode.echoErr(ApiErrorCode.SYSTEM_ERR));
    		return;
		}
    	
    	try {
			MysqlBaseManager.register(account, pwd, type, sdkpwd);
			String ret=ApiErrorCode.echoOk();			
    		response.getWriter().write(ret);
    		return;
		} catch (SQLException e) {
			m_logger.info(String.format("FAILED params=%s","SQLException cause:"+e.getCause()));		
    		response.getWriter().write(ApiErrorCode.echoErr(ApiErrorCode.SYSTEM_ERR));
		}
	}

}
