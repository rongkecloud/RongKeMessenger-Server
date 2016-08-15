package com.rongketong.api;

import java.io.IOException;
import java.util.HashMap;

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
 *云视互动测试app：登录流程
 *需要传的参数为：
 *	account:用户名(必填)
 *	pwd：密码(必填)
 *
 *返回值：
 *	oper_result：对应的错误码
 *				0：成功
 *				1002:用户名密码错误
 *				9998:系统错误
 *				9999:参数错误
 *	result--oper_result=0时有此项。内容为json串格式，里面包含的信息如下：
 *				ss:session
 *				sdk_pwd:云视互动账号对应的密码
 *				name:姓名
 *				address:地址
 *				type:用户类型  1：普通用户  2：企业用户
 *				email:邮箱
 *				mobile:手机号码
 *				sex:性别  0：无 1：男  2：女
 *				permission:是否需要验证 1：需要验证 2：不需要验证
 *				info_version:个人信息版本号
 *				avatar_version:用户头像版本号
**/ 
@WebServlet("/login.php")
public class Login extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger m_logger = Logger.getLogger(Login.class);    
	private static enum osType {android,ios,windows,webim,mac,ipad,apad};

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
    	params.put("account", true); //用户名
    	params.put("pwd", true); //登录密码
    	params.put("os", true); //登录密码
    	CheckParameters check = new CheckParameters(request.getParameterMap(),params);	
    	postParams = check.postParamsToHashMap();
    	if(!check.paramCheckAndRetRes()){
    		m_logger.info(String.format("FAILED params=%s %s",postParams,"check parameters error"));
    		String ret=ApiErrorCode.echoErr(ApiErrorCode.API_ERR_MISSED_PARAMATER);			
    		response.getWriter().write(ret);
    		return;
    	}
    	String accountName = postParams.get("account");
    	String userPwd = postParams.get("pwd");
    	String os = postParams.get("os");
		//参数格式校验
    	if(!Tools.checkAccount(accountName) || !Tools.checkPwd(userPwd)){
    		m_logger.info(String.format("FAILED params=%s %s",postParams,"check parameters error"));
    		response.getWriter().write(ApiErrorCode.echoErr(ApiErrorCode.API_ERR_MISSED_PARAMATER));
    		return;
    	}
    	try{
    		osType.valueOf(os);
    	}
    	catch(Exception e){
    		m_logger.info(String.format("FAILED params=%s %s",postParams,"check parameters error:os type"));
    		response.getWriter().write(ApiErrorCode.echoErr(ApiErrorCode.API_ERR_MISSED_PARAMATER));
    		return;
    	}
    	HashMap<String, String> userMap = MysqlBaseManager.appLogin(accountName, userPwd,os);
    	if(userMap.get("check_result") != null){
    		if(userMap.get("check_result").equals("-1") || userMap.get("check_result").equals("-2")){
    			response.getWriter().write(ApiErrorCode.echoErr(ApiErrorCode.API_ERR_ACCOUNT_OR_PASSWD));
        		return;
    		}else{
    			m_logger.info(String.format("System Login error,FAILED params =%s",userMap));
    			response.getWriter().write(ApiErrorCode.echoErr(ApiErrorCode.SYSTEM_ERR));
        		return;
    		}
    	}else{
    		response.setContentType("text/html;charset=utf-8");
    		String userJson = JSONObject.fromObject(userMap).toString();
        	response.getWriter().write(ApiErrorCode.echoOkArr("result="+userJson.toString()));
     		return;
    	}
	}

}
