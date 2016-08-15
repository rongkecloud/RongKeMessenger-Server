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
 *云视互动测试app：添加好友
 *需要传的参数为：
 *	ss:用户session(必填)
 *  account:好友账号(必填)
 *  content：验证内容，当好友需要验证时，该内容不允许为空
 *
 *返回值：
 *	oper_result：对应的错误码
 *				0：成功
 *				1001:无效的session
 *				1021:添加好友需要验证信息
 *				1022:添加好友等待对端验证
 *				9998:系统错误
 *				9999:参数错误
**/ 
@WebServlet("/add_friend.php")
public class Add_friend extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger m_logger = Logger.getLogger(Add_friend.class);      


	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	@SuppressWarnings("unchecked")
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		// 参数校验
		HashMap<String,String> postParams = new HashMap<String,String>();	
    	HashMap<String,Boolean> params = new HashMap<String,Boolean>();
    	params.put("ss", true); //Session
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
    	String friendAccount = postParams.get("account");
		//参数格式校验
    	if(!Tools.checkSession(appSession) || !Tools.checkAccount(friendAccount)){
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
    	/**
    	 * 判断添加好友是否为本身
    	 */
    	if(accountInfo.get("user_account").equals(friendAccount)){
    		m_logger.info(String.format("Add Friend is SELF error params=%s %s",accountInfo.get("user_account"),friendAccount));
    		response.getWriter().write(ApiErrorCode.echoErr(ApiErrorCode.API_ERR_FRIENT_SELF));
			return;
    	}
    	/**
    	 * 判断用户是否已经成为好友，如果是，直接返回
    	 */
    	if(MysqlBaseManager.is_friend(accountInfo.get("user_account"), friendAccount)){
    		response.getWriter().write(ApiErrorCode.echoOk());
			return;
    	}
    	/**
    	 * 获取用户信息
    	 */
    	HashMap<String, String> userList = MysqlBaseManager.get_account_info(friendAccount);
    	if(userList == null || userList.size() <= 0){
    		response.getWriter().write(ApiErrorCode.echoErr(ApiErrorCode.API_ERR_MISSED_PARAMATER));	
     		return;
    	}
    	Integer resultCode = 0;
    	YsServerSDK client = Tools.geYsServerAPI();
    	//加我为好友的权限验证：1.需要验证,2.不需要验证
    	if(Integer.parseInt(userList.get("permission_validation")) == 1){
        	String content = postParams.get("content");
        	if(content == null || content.trim().equals("")){
        		response.getWriter().write(ApiErrorCode.echoErr(ApiErrorCode.API_ADDFRIEND_NEEDVERIFY));	
         		return;
        	}
        	try {
        		resultCode = client.SendMessage(accountInfo.get("user_account"), friendAccount, "add_request,"+content);
			} catch (Exception e) {
				m_logger.info(String.format("AddFriend sendMessage exception,error cause:"+e.getCause()));
				response.getWriter().write(ApiErrorCode.echoErr(ApiErrorCode.SYSTEM_ERR));
        		return;
			}
    	}else{
    		if(MysqlBaseManager.confirm_add_friend(accountInfo.get("user_account"), friendAccount)){
    			//发送通知(此处需要调用云视互动服务推送消息)
    			try {
    				resultCode = client.SendMessage(accountInfo.get("user_account"), friendAccount, "add_confirm,isNotActivited");
    				m_logger.info("sendMessage return Code:"+resultCode);
    			} catch (Exception e) {
    				m_logger.info(String.format("AddFriend sendMessage exception,error cause:"+e.getCause()));
    				response.getWriter().write(ApiErrorCode.echoErr(ApiErrorCode.SYSTEM_ERR));
            		return;
    			}
        	}else{
        		m_logger.info(String.format("System error,add Friend is fail"));
        		response.getWriter().write(ApiErrorCode.echoErr(ApiErrorCode.SYSTEM_ERR));
        		return;
        	}
    	}
    	if(resultCode == 0){
    		if(Integer.parseInt(userList.get("permission_validation")) == 1){
    			response.getWriter().write(ApiErrorCode.echoErr(ApiErrorCode.API_ADDFRIEND_WAITVERIFY));
        		return;
    		}else{
    			response.getWriter().write(ApiErrorCode.echoOk());
        		return;
    		}
    	}else{
    		m_logger.info(String.format("Send Add Friend Message fail,params=%s",resultCode));
    		response.getWriter().write(ApiErrorCode.echoErr(ApiErrorCode.SYSTEM_ERR));
    		return;
    	}	
	}
}
