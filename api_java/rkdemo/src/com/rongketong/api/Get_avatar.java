package com.rongketong.api;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
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
 *云视互动测试app：获取头像
 *需要传的参数为：
 *	ss:用户session(必填)
 *  type:头像类型(必填) 1、缩略图，2、大图
 *  account：需要获取头像的用户账号(必填)
 *
 *返回值：
 *	oper_result：对应的错误码
 *				0：成功
 *				1001:无效的session
 *				1011:图片下载失败
 *				9998:系统错误
 *				9999:参数错误
**/
@WebServlet("/get_avatar.php")
public class Get_avatar extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger m_logger = Logger.getLogger(Get_avatar.class);    


	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setHeader("Content-type", "text/plain");
		// 参数校验
		HashMap<String,String> postParams = new HashMap<String,String>();	
    	HashMap<String,Boolean> params = new HashMap<String,Boolean>();
    	params.put("ss", true); //APP Session
    	params.put("type", true); //头像类型 1、缩略图  2、大图
    	params.put("account", true); //需要获取头像的用户账号
    	CheckParameters check = new CheckParameters(request.getParameterMap(),params);	
    	postParams = check.postParamsToHashMap();
    	if(!check.paramCheckAndRetRes()){
    		m_logger.info(String.format("FAILED params=%s %s",postParams,"check parameters error"));
    		String ret=ApiErrorCode.echoErr(ApiErrorCode.API_ERR_MISSED_PARAMATER);			
    		response.getWriter().write(ret);
    		return;
    	}
    	String appSession = postParams.get("ss");
    	String type = postParams.get("type");
    	String accountName = postParams.get("account");
		m_logger.info(String.format("get_avatar start,account is "+accountName));
		//参数格式校验
    	if(!Tools.checkSession(appSession) || !Arrays.asList(new String[]{"1", "2"}).contains(type) || !Tools.checkAccount(accountName)){
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
    	 * 获取二进制流图像信息
    	 */
    	InputStream is = null;
    	ServletOutputStream outStream = null;
		try {
			is = MysqlBaseManager.get_avatar(accountName, type);
			if(is == null){
				response.getWriter().write(ApiErrorCode.echoErr(ApiErrorCode.FILE_DOWN_ERR));
	    		return;
			}
			byte[] data = new byte[1024*1024];
			response.setContentType("application/octet-stream");
			response.setHeader("Accept-Ranges","bytes");
			response.setContentLength(is.available());
			response.setHeader("Content-Disposition:attachment;","filename="+ Tools.getRandFileName());
			int len = 0;
			outStream = response.getOutputStream();
			while ((len = is.read(data)) != -1) {
				outStream.write(data, 0, len);
			}
			outStream.write(data[0]);
		} catch (SQLException e) {
			m_logger.info(String.format("receive get_avatar SQLException,error cause:"+e.getCause()));
			response.getWriter().write(ApiErrorCode.echoErr(ApiErrorCode.SYSTEM_ERR));
    		return;
		}finally{
			is.close();
			outStream.flush();
			outStream.close();
		}
    	
	}

}
