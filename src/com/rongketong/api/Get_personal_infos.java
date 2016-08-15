package com.rongketong.api;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;

import org.apache.log4j.Logger;

import com.rongketong.utils.ApiErrorCode;
import com.rongketong.utils.CheckParameters;
import com.rongketong.utils.MysqlBaseManager;
import com.rongketong.utils.Tools;

/**
 *云视互动测试app：获取用户信息
 *需要传的参数为：
 *	ss:用户session(必填)
 *  accounts:选定的账号，多个之间用半角逗号分隔
 *
 *返回值：
 *	oper_result：对应的错误码
 *				0：成功
 *				1001:无效的session
 *				9998:系统错误
 *				9999:参数错误
 *	result--oper_result=0时有此项。内容为json串数组格式，每条信息里面包含内容如下：
 *				account: 账户名称
 *				name: 用户姓名
 *				address: 地址	
 *				type: 用户类型
 *				sex: 性别
 *				mobile: 手机号码
 *				email: 邮箱
 *				info_version: 信息版本号
 *				avatar_version: 头像版本号	
**/ 
@WebServlet("/get_personal_infos.php")
public class Get_personal_infos extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	private static final Logger m_logger = Logger.getLogger(Get_personal_infos.class);  
	
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doPost(request, response);
	}
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
    	params.put("accounts", true);//传递参数
    	
    	CheckParameters check = new CheckParameters(request.getParameterMap(),params);	
    	postParams = check.postParamsToHashMap();
    	if(!check.paramCheckAndRetRes()){
    		m_logger.info(String.format("FAILED params=%s %s",postParams,"check parameters error"));
    		String ret=ApiErrorCode.echoErr(ApiErrorCode.API_ERR_MISSED_PARAMATER);			
    		response.getWriter().write(ret);
    		return;
    	}
    	String appSession = postParams.get("ss");
    	String friend_accounts = postParams.get("accounts");
    	/**
		 * 校验参数格式
		 */
    	if(!Tools.checkSession(appSession)){
    		m_logger.info(String.format("FAILED params=%s %s",postParams,"check parameters error"));
    		response.getWriter().write(ApiErrorCode.echoErr(ApiErrorCode.API_ERR_MISSED_PARAMATER));
    		return;
    	}
		String[] arrAccounts = friend_accounts.split(",");
		for(String account : arrAccounts) {
			if(!Tools.checkAccount(account.trim())){
				m_logger.info(String.format("FAILED params=%s %s",account,"check parameters error"));
				response.getWriter().write(ApiErrorCode.echoErr(ApiErrorCode.API_ERR_MISSED_PARAMATER));
	    		return;
			}
		}
		/**
    	 * 检查Session
    	 */
    	Map<String,String> accountInfo = null;
    	try {
    		accountInfo = MysqlBaseManager.checkSession(appSession);
			if(accountInfo == null || accountInfo.size()<=0){
				response.getWriter().write(ApiErrorCode.echoErr(ApiErrorCode.API_ERR_INVALID_SESSION));
				return;
			}
		} catch (SQLException e) {
			m_logger.info(String.format("FAILED params=%s %s",appSession,"check session error,error cause:"+e.getCause()));
			response.getWriter().write(ApiErrorCode.echoErr(ApiErrorCode.SYSTEM_ERR));
    		return;
		}
    	List<HashMap<String, String>> userList;
		try {
			userList = MysqlBaseManager.get_personal_infos(friend_accounts);
	    	if(userList!=null){
	    		response.setContentType("text/html;charset=utf-8");
		    	String userJson = JSONArray.fromObject(userList).toString();
		    	response.getWriter().write(ApiErrorCode.echoOkArr("result="+userJson.toString()));
		 		return;
	    	}
		} catch (SQLException e) {
			m_logger.info(String.format("FAILED SQLException,error cause:"+e.getCause()));
		}
    	response.getWriter().write(ApiErrorCode.SYSTEM_ERR);
	}

}
