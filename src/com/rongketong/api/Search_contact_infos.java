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
 *云视互动测试app：搜索用户信息
 *需要传的参数为：
 *	ss:用户session(必填)
 *  filter:搜索条件(必填)
 *
 *返回值：
 *	oper_result：对应的错误码
 *				0：成功
 *				1001:无效的session
 *				9998:系统错误
 *				9999:参数错误
 *	result--oper_result=0时有此项。内容为json串数组格式，每条信息里面包含内容如下：
 *				account: 账户名称
**/
@WebServlet("/search_contact_infos.php")
public class Search_contact_infos extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger m_logger = Logger.getLogger(Search_contact_infos.class);  


	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	@SuppressWarnings("unchecked")
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		// 参数校验
		HashMap<String,String> postParams = new HashMap<String,String>();	
    	HashMap<String,Boolean> params = new HashMap<String,Boolean>();
    	params.put("ss", true); //用户session
    	params.put("filter", true);//传递参数
    	
    	CheckParameters check = new CheckParameters(request.getParameterMap(),params);	
    	postParams = check.postParamsToHashMap();
    	if(!check.paramCheckAndRetRes()){
    		m_logger.info(String.format("FAILED params=%s %s",postParams,"check parameters error"));		
    		response.getWriter().write(ApiErrorCode.echoErr(ApiErrorCode.API_ERR_MISSED_PARAMATER));
    		return;
    	}
    	String appSession = postParams.get("ss");
    	String filter = postParams.get("filter");
		//参数格式校验
    	if(!Tools.checkSession(appSession) || (filter.length() == 0 || !Tools.checkSession(filter))){
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
    	List<HashMap<String, String>> userList;
		try {
			userList = MysqlBaseManager.search_contact_infos(filter);
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
