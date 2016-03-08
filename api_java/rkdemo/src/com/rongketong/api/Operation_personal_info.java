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

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;

import com.rongketong.utils.ApiErrorCode;
import com.rongketong.utils.CheckParameters;
import com.rongketong.utils.MysqlBaseManager;
import com.rongketong.utils.Tools;

/**
 *云视互动测试app：修改个人信息流程
 *需要传的参数为：
 *	ss:用户session(必填)
 *	key：修改的信息标识(必填)，包含的内容如下：
 *			name:姓名
 *			sex:性别
 *			address:地址
 *			mobile:手机号码
 *			email:邮箱
 *			permission:加好友权限
 *	cotent: 修改的信息内容(必填)
 *
 *返回值：
 *	oper_result：对应的错误码
 *				0：成功
 *				1001:无效的session
 *				9998:系统错误
 *				9999:参数错误
 *	result--oper_result=0时有此项。内容为json串格式，里面包含的信息如下：
 *				info_version:个人信息版本号				
**/  
@WebServlet("/operation_personal_info.php")
public class Operation_personal_info extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger m_logger = Logger.getLogger(Operation_personal_info.class);   
       

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		// 参数校验
		HashMap<String,String> postParams = new HashMap<String,String>();	
    	HashMap<String,Boolean> params = new HashMap<String,Boolean>();
    	params.put("ss", true); //账号
    	params.put("key", true);//传递参数
    	params.put("content", true);//修改内容信息 	
    	CheckParameters check = new CheckParameters(request.getParameterMap(),params);	
    	postParams = check.postParamsToHashMap();
    	/*if(!check.paramCheckAndRetRes()){
    		m_logger.info(String.format("FAILED params=%s %s",postParams,"check parameters error"));
    		String ret=ApiErrorCode.echoErr(ApiErrorCode.API_ERR_MISSED_PARAMATER);			
    		response.getWriter().write(ret);
    		return;
    	}*/
    	String appSession = postParams.get("ss");
    	String param_key = postParams.get("key");
    	String content = postParams.get("content");
    	String key = "";
    	if(param_key.equals("name")){
    		key = "name";
    		if(!Tools.checkName(content)){
    			String ret=ApiErrorCode.echoErr(ApiErrorCode.API_ERR_MISSED_PARAMATER);			
        		response.getWriter().write(ret);
        		return;
    		}
    	}else if(param_key.equals("sex")){
    		key = "sex";
    		if(!content.equals("1") && !content.equals("2")){		
        		response.getWriter().write(ApiErrorCode.echoErr(ApiErrorCode.API_ERR_MISSED_PARAMATER));
        		return;
    		}
    	}else if(param_key.equals("address")){
    		key = "address";
    		if(!Tools.containSpecChars(content)){		
        		response.getWriter().write(ApiErrorCode.echoErr(ApiErrorCode.API_ERR_MISSED_PARAMATER));
        		return;
    		}
    	}else if(param_key.equals("mobile")){
    		key = "mobile";
    		if(!Tools.checkMobileFormat(content)){	
        		response.getWriter().write(ApiErrorCode.echoErr(ApiErrorCode.API_ERR_MISSED_PARAMATER));
        		return;
    		}
    	}else if(param_key.equals("email")){
    		key = "email";
    		if(!Tools.isEmail(content)){	
        		response.getWriter().write(ApiErrorCode.echoErr(ApiErrorCode.API_ERR_MISSED_PARAMATER));
        		return;
    		}
    	}else if(param_key.equals("permission")){
    		key = "permission_validation";
    		if(!Tools.checkPermission(content)){		
        		response.getWriter().write(ApiErrorCode.echoErr(ApiErrorCode.API_ERR_MISSED_PARAMATER));
        		return;
    		}
    	}else{
    		m_logger.info(String.format("FAILED params=%s","param_key error:"+param_key));
    		response.getWriter().write(ApiErrorCode.echoErr(ApiErrorCode.API_ERR_MISSED_PARAMATER));
    		return;
    	}
    	/*if(key.equals("")){		
    		response.getWriter().write(ApiErrorCode.echoErr(ApiErrorCode.API_ERR_MISSED_PARAMATER));
    		return;
    	}*/
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
			m_logger.info(String.format("FAILED params=%s","check session error,error cause:"+e.getCause()+",err msg:"+e.getMessage()));
			response.getWriter().write(ApiErrorCode.echoErr(ApiErrorCode.SYSTEM_ERR));
    		return;
		}
    	String infoVersion;
		try {
			infoVersion = MysqlBaseManager.operation_personal_info(accountInfo.get("user_account"), key, content);
	    	if(!infoVersion.equals("fail")){
	    		JSONObject VersionJson = new JSONObject();
	    		VersionJson.put("info_version", Integer.parseInt(infoVersion));
	    		response.getWriter().write(ApiErrorCode.echoOkArr("result="+VersionJson.toString()));
	    		return;
	    	}
		} catch (SQLException e) {
			m_logger.info(String.format("FAILED SQLException,error cause:"+e.getCause()+",error Msg:"+e.getMessage()));
		}
		response.getWriter().write(ApiErrorCode.echoErr(ApiErrorCode.SYSTEM_ERR));
	}
}
