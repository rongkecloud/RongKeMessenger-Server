package com.rongketong.api;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
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
 *云视互动测试app：问题反馈
 *需要传的参数为：
 *	ss:用户session(必填)
 *  type:类型(必填) 1：软件问题 2：界面问题 3：其他问题
 *  content：内容(必填)
 *
 *返回值：
 *	oper_result：对应的错误码
 *				0：成功
 *				1001:无效的session
 *				9998:系统错误
 *				9999:参数错误
**/
@WebServlet("/add_feedback.php")
public class Add_feedback extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger m_logger = Logger.getLogger(Add_feedback.class);  


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
    	params.put("type", true); //反馈类型
    	params.put("content", true); //反馈内容
    	CheckParameters check = new CheckParameters(request.getParameterMap(),params);	
    	postParams = check.postParamsToHashMap();
    	if(!check.paramCheckAndRetRes()){
    		m_logger.info(String.format("FAILED params=%s %s",postParams,"check parameters error"));
    		String ret=ApiErrorCode.echoErr(ApiErrorCode.API_ERR_MISSED_PARAMATER);			
    		response.getWriter().write(ret);
    		return;
    	}
    	String appSession = postParams.get("ss");
    	String feedBackType = postParams.get("type");
    	String content = postParams.get("content");
		//参数格式校验
    	if(!Tools.checkSession(appSession) || !Arrays.asList(new String[]{"1", "2","3"}).contains(feedBackType) || (content.length() == 0 || content.length() > 150 || !Tools.containSpecChars(content))){
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
			m_logger.error(String.format("FAILED params=%s %s",appSession,"check session error,error cause:"+e.getCause()));
			response.getWriter().write(ApiErrorCode.echoErr(ApiErrorCode.SYSTEM_ERR));
    		return;
		}
    	//反馈信息
		try {
			if(MysqlBaseManager.addFeedback(accountInfo.get("user_account"), feedBackType, content)){
				response.getWriter().write(ApiErrorCode.echoOk());
				return;
			}else{
				response.getWriter().write(ApiErrorCode.echoErr(ApiErrorCode.SYSTEM_ERR));
				return;
			}
		} catch (SQLException e) {
			response.getWriter().write(ApiErrorCode.echoErr(ApiErrorCode.SYSTEM_ERR));
			m_logger.error(String.format("FAILED params=%s addFeedback,error cause:",params,e.getCause()));
			return;
		}	
	}
}
