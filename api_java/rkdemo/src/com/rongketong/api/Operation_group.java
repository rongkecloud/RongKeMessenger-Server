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
import net.sf.json.JSONObject;

import org.apache.log4j.Logger;

import com.rongketong.utils.ApiErrorCode;
import com.rongketong.utils.CheckParameters;
import com.rongketong.utils.MysqlBaseManager;
import com.rongketong.utils.Tools;

/**
 *云视互动测试app：操作组信息
 *需要传的参数为：
 *	ss:用户session(必填)
 *  type:操作类型(必填) 1.添加 2.修改 3.删除
 *  gid：组id
 *  gname:组名称
 *
 *返回值：
 *	oper_result：对应的错误码
 *				0：成功
 *				1001:无效的session
 *				1030：组名已存在
 *				9998:系统错误
 *				9999:参数错误
 *	result--oper_result=0&&type==1时有此项。内容为json串格式，里面包含内容如下：
 *				gid: 组ID
**/
@WebServlet("/operation_group.php")
public class Operation_group extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	private static final Logger m_logger = Logger.getLogger(Operation_group.class);   


	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		// 参数校验
		HashMap<String,String> postParams = new HashMap<String,String>();	
    	HashMap<String,Boolean> params = new HashMap<String,Boolean>();
    	params.put("ss", true); //session
    	params.put("type", true);//操作类型
    	if(!request.getParameter("type").equals("1")){
    		params.put("gid", true);//组ID
    	}
    	if(!request.getParameter("type").equals("3")){
    		params.put("gname",true); //组名称
    	}
    	CheckParameters check = new CheckParameters(request.getParameterMap(),params);	
    	postParams = check.postParamsToHashMap();
    	if(!check.paramCheckAndRetRes()){
    		m_logger.info(String.format("FAILED params=%s %s",postParams,"check parameters error"));
    		String ret=ApiErrorCode.echoErr(ApiErrorCode.API_ERR_ACCOUNT_EXIST);			
    		response.getWriter().write(ret);
    		return;
    	}
    	String ss = postParams.get("ss");
    	String type = postParams.get("type");
    	Integer groupId = 0;
    	if(!type.equals("1")){
    		groupId = Integer.parseInt(postParams.get("gid"));
    	}
    	String groupName = null;
    	if(!type.equals("3")){
    		groupName = postParams.get("gname");
    	}
    	//参数格式校验
    	if(!Tools.checkSession(ss) || !Arrays.asList(new String[]{"1", "2", "3"}).contains(type)){
    		m_logger.info(String.format("FAILED params=%s %s",postParams,"check parameters error"));
    		response.getWriter().write(ApiErrorCode.echoErr(ApiErrorCode.API_ERR_MISSED_PARAMATER));
    		return;
    	}
    	if(type.equals("1")){
    		if(groupName.length() == 0 || groupName.length()>50 || !Tools.containSpecChars(groupName)){
    			response.getWriter().write(ApiErrorCode.echoErr(ApiErrorCode.API_ERR_MISSED_PARAMATER));
        		return;
    		}
    	}else if(type.equals("2") || type.equals("3")){
    		if(groupId == 0 || !Tools.isDigits2(groupId.toString())){
    			response.getWriter().write(ApiErrorCode.echoErr(ApiErrorCode.API_ERR_MISSED_PARAMATER));
        		return;
    		}
    		if(type.equals("2")){
    			if(groupName.length() == 0 || groupName.length() > 50 || !Tools.containSpecChars(groupName)){ 
    				response.getWriter().write(ApiErrorCode.echoErr(ApiErrorCode.API_ERR_MISSED_PARAMATER));
            		return;
    			}
    		}
    	}
		/**
    	 * 检查Session
    	 */
    	Map<String,String> accountInfo = null;
    	try {
    		accountInfo = MysqlBaseManager.checkSession(ss);
			if(accountInfo == null){
				response.getWriter().write(ApiErrorCode.echoErr(ApiErrorCode.API_ERR_INVALID_SESSION));
				return;
			}
		} catch (SQLException e) {

			m_logger.info(String.format("FAILED params=%s %s",ss,"check session error,error cause:"+e.getCause()));
			response.getWriter().write(ApiErrorCode.echoErr(ApiErrorCode.SYSTEM_ERR));
    		return;
		}
    	Integer resultInfo = 0;
    	try {
	    	if(type.equals("1")){
	    		resultInfo = MysqlBaseManager.addGroup(accountInfo.get("user_account"), groupName);
	    		if(resultInfo > 0){
	    			JSONObject groupJson = new JSONObject();
	    			groupJson.put("gid", resultInfo);
	    			response.getWriter().write(ApiErrorCode.echoOkArr("result="+groupJson.toString()));
	        		return;
	    		}	    		
	    	}else if(type.equals("2")){
	    		resultInfo = MysqlBaseManager.modifyGroup(accountInfo.get("user_account"), groupId, groupName);
	    		if(resultInfo > 0){
	    			response.getWriter().write(ApiErrorCode.echoOk());
	        		return;
	    		}
	    	}else if(type.equals("3")){
	    		if(MysqlBaseManager.deleteGroup(accountInfo.get("user_account"), groupId)){
	    			response.getWriter().write(ApiErrorCode.echoOk());
	        		return;
	    		}
	    	}else{
	    		m_logger.info(String.format("FAILED params=%s %s",type,"type is error"));
	    		response.getWriter().write(ApiErrorCode.echoErr(ApiErrorCode.API_ERR_MISSED_PARAMATER));
	    		return;
	    	}
    		if(resultInfo == -1){
    			m_logger.info(String.format("FAILED params=%s %s",resultInfo,"this group "+groupName+" is exists"));
				response.getWriter().write(ApiErrorCode.echoErr(ApiErrorCode.API_GROPNAME_HASEXIST));
	    		return;
			}else{
				m_logger.info(String.format("FAILED params=%s %s",resultInfo,"system_err"));
				response.getWriter().write(ApiErrorCode.echoErr(ApiErrorCode.SYSTEM_ERR));
			}
		} catch (SQLException e) {
			m_logger.info(String.format("FAILED params=%s","SQLException is error,error cause"+e.getCause()));
			response.getWriter().write(ApiErrorCode.echoErr(ApiErrorCode.SYSTEM_ERR));
		}
	}
}
