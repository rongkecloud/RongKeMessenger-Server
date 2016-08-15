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
import com.yunshihudong.sdk.server.YsServerSDK;

/**
 *云视互动测试app：确认添加好友
 *需要传的参数为：
 *	ss:用户session(必填)
 *  account:好友账号(必填)
 *
 *返回值：
 *	oper_result：对应的错误码
 *				0：成功
 *				1001:无效的session
 *				9998:系统错误
 *				9999:参数错误
**/
@WebServlet("/confirm_add_friend.php")
public class Confirm_add_friend extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger m_logger = Logger.getLogger(Confirm_add_friend.class);   
       

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	@SuppressWarnings("unchecked")
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		// 参数校验
		HashMap<String,String> postParams = new HashMap<String,String>();	
    	HashMap<String,Boolean> params = new HashMap<String,Boolean>();
    	params.put("ss", true); //APP Session
    	params.put("account", true); //好友账号
    	CheckParameters check = new CheckParameters(request.getParameterMap(),params);	
    	postParams = check.postParamsToHashMap();
    	if(!check.paramCheckAndRetRes()){
    		m_logger.info(String.format("FAILED params=%s %s",postParams,"check parameters error"));
    		String ret=ApiErrorCode.echoErr(ApiErrorCode.API_ERR_MISSED_PARAMATER);			
    		response.getWriter().write(ret);
    		return;
    	}
    	String appSession = postParams.get("ss");
    	String accountName = postParams.get("account");
		//参数格式校验
    	if(!Tools.checkSession(appSession) || !Tools.checkAccount(accountName)){
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
			// TODO Auto-generated catch block
			m_logger.info(String.format("FAILED params=%s %s",appSession,"check session error,error cause:"+e.getCause()));
			response.getWriter().write(ApiErrorCode.echoErr(ApiErrorCode.SYSTEM_ERR));
    		return;
		}
    	/**
    	 * 判断用户是否为好友，如果是，直接返回
    	 */
    	if(MysqlBaseManager.is_friend(accountInfo.get("user_account"), accountName)){
    		response.getWriter().write(ApiErrorCode.echoOk());
			return;
    	}
    	/**
    	 * 添加好友
    	 */
    	if(MysqlBaseManager.confirm_add_friend(accountInfo.get("user_account"), accountName)){
    		//发送通知(此处需要调用云视互动服务推送消息)
        	YsServerSDK client = Tools.geYsServerAPI();
        	try {
        		Integer resultCode = client.SendMessage(accountInfo.get("user_account"), accountName, "add_confirm,isActivited");
        		if(resultCode == 0){
        			response.getWriter().write(ApiErrorCode.echoOk());
        			return;
        		}else{
        			m_logger.info(String.format("Confirm AddFriend sendMessage fail,resultCode"+resultCode));
        			response.getWriter().write(ApiErrorCode.echoErr(ApiErrorCode.SYSTEM_ERR));
            		return;
        		}
			} catch (Exception e) {
				m_logger.info(String.format("AddFriend sendMessage exception,error cause:"+e.getCause()));
				response.getWriter().write(ApiErrorCode.echoErr(ApiErrorCode.SYSTEM_ERR));
        		return;
			}
    	}else{
    		response.getWriter().write(ApiErrorCode.echoErr(ApiErrorCode.SYSTEM_ERR));
    		return;
    	}
    	
	}

}
