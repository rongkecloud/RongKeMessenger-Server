package com.rongketong.api;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
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
 *云视互动测试app：检查更新
 *需要传的参数为：
 *  os: OS类型，如android, iphone(必填)
 *  cv: 客户端软件版本号(必填)
 *
 *返回值：
 *	oper_result：对应的错误码
 *				0：成功
 *				1001:无效的session
 *				1006:无需更新
 *				9998:系统错误
 *				9999:参数错误
**/
@WebServlet("/check_update.php")
public class Check_update extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger m_logger = Logger.getLogger(Check_update.class);   
       

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
    	params.put("os", true); //客户端类型
    	params.put("cv", true); //客户端软件版本号
    	CheckParameters check = new CheckParameters(request.getParameterMap(),params);	
    	postParams = check.postParamsToHashMap();
    	if(!check.paramCheckAndRetRes()){
    		m_logger.info(String.format("FAILED params=%s %s",postParams,"check parameters error"));
    		String ret=ApiErrorCode.echoErr(ApiErrorCode.API_ERR_MISSED_PARAMATER);			
    		response.getWriter().write(ret);
    		return;
    	}
    	String appType = postParams.get("os");
    	String appCV = postParams.get("cv");
		//参数格式校验
    	if(!Arrays.asList(new String[]{"android", "iphone"}).contains(appType)){
    		m_logger.info(String.format("FAILED params=%s %s",postParams,"check parameters error"));
    		response.getWriter().write(ApiErrorCode.echoErr(ApiErrorCode.API_ERR_MISSED_PARAMATER));
    		return;
    	}
    	HashMap<String, String> updateInfoList = MysqlBaseManager.checkUpdate(appType);
    	if(updateInfoList!=null && updateInfoList.size()>0){
    		if(Tools.compareVersion(updateInfoList.get("update_version"), appCV)>0){
    			response.setContentType("text/html;charset=utf-8");
    			StringBuffer check_update_info = new StringBuffer();
    			check_update_info.append("os_type="+updateInfoList.get("os_type")+"\n");
    			check_update_info.append("download_url="+updateInfoList.get("download_url")+"\n");
    			check_update_info.append("upload_date="+updateInfoList.get("upload_date")+"\n");
    			check_update_info.append("update_version="+updateInfoList.get("update_version")+"\n");
    			check_update_info.append("update_description="+updateInfoList.get("update_description")+"\n");
    			check_update_info.append("min_version="+updateInfoList.get("min_version")+"\n");
    			check_update_info.append("file_name="+updateInfoList.get("file_name")+"\n");
    			check_update_info.append("file_size="+updateInfoList.get("file_size"));
            	response.getWriter().write(ApiErrorCode.echoOkArr(check_update_info.toString()));
         		return;
    		}else{
    			response.getWriter().write(ApiErrorCode.echoErr(ApiErrorCode.API_ERR_NO_UPDATE));
    		}
    	}else{
    		if(updateInfoList == null){
    			m_logger.info(String.format("check Update fail,params=%s",appType));
    			response.getWriter().write(ApiErrorCode.echoErr(ApiErrorCode.SYSTEM_ERR));
    		}else{
    			response.getWriter().write(ApiErrorCode.echoErr(ApiErrorCode.API_ERR_MISSED_PARAMATER));	
    		}
    		return;
    	}
    	
	}

}
