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
 *云视互动测试app：选定用户到指定的组中
 *需要传的参数为：
 *	ss:用户session(必填)
 *	gid:组id(必填)
 *	accounts:选定的账号，多个之间用半角逗号分隔
 *
 *返回值：
 *	oper_result：对应的错误码
 *				0：成功
 *				1001:无效的session
 *				9998:系统错误
 *				9999:参数错误
**/ 
@WebServlet("/operation_group_members.php")
public class Operation_group_members extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger m_logger = Logger.getLogger(Operation_group_members.class);   


	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		// 参数校验
		HashMap<String,String> postParams = new HashMap<String,String>();	
    	HashMap<String,Boolean> params = new HashMap<String,Boolean>();
    	params.put("ss", true); //session
    	params.put("gid", true);//分组id
    	params.put("accounts", true);//需要操作的好友账号
    	
    	CheckParameters check = new CheckParameters(request.getParameterMap(),params);	
    	postParams = check.postParamsToHashMap();
    	if(!check.paramCheckAndRetRes()){
    		m_logger.info(String.format("FAILED params=%s %s",postParams,"check parameters error"));
    		String ret = ApiErrorCode.echoErr(ApiErrorCode.API_ERR_ACCOUNT_EXIST);			
    		response.getWriter().write(ret);
    		return;
    	}
    	String ss = postParams.get("ss");
    	Integer gid = Integer.parseInt(postParams.get("gid"));
    	String friend_accounts = postParams.get("accounts");
		/**
		 * 校验参数格式
		 */
    	if(!Tools.checkSession(ss) || (gid!=0 && !Tools.isDigits2(gid.toString())) || friend_accounts.length() == 0){
    		m_logger.info(String.format("FAILED params=%s %s",postParams,"check parameters error"));
    		response.getWriter().write(ApiErrorCode.echoErr(ApiErrorCode.API_ERR_MISSED_PARAMATER));
    		return;
    	}
		String[] arrAccounts = friend_accounts.split(",");
		for(String account : arrAccounts) {
			if(!Tools.checkAccount(account.trim())){
				response.getWriter().write(ApiErrorCode.echoErr(ApiErrorCode.API_ERR_MISSED_PARAMATER));
	    		return;
			}
		}
		/**
    	 * 检查Session
    	 */
    	Map<String,String> accountInfo = null;
    	try {
    		accountInfo = MysqlBaseManager.checkSession(ss);
			if(accountInfo == null || accountInfo.size()<=0){
				response.getWriter().write(ApiErrorCode.echoErr(ApiErrorCode.API_ERR_INVALID_SESSION));
				return;
			}
		} catch (SQLException e) {
			m_logger.info(String.format("FAILED params=%s %s",ss,"check session error,error cause:"+e.getCause()));
			response.getWriter().write(ApiErrorCode.echoErr(ApiErrorCode.SYSTEM_ERR));
    		return;
		}
    	// 选择用户到指定组
    	try {
			if( MysqlBaseManager.modify_friends_group(accountInfo.get("user_account"), friend_accounts, gid)){
				response.getWriter().write(ApiErrorCode.echoOk());
				return;
			}
		} catch (SQLException e) {
			m_logger.info(String.format("FAILED SQLException,error cause:"+e.getCause()));
		}
    	response.getWriter().write(ApiErrorCode.echoErr(ApiErrorCode.SYSTEM_ERR));
	}
}
