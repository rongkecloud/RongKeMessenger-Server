package com.rongketong.utils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

/**
 * API接口返回给client端的错误代码
 *
 */
public class ApiErrorCode {
	
	public static final String API_ERR_BANNED_USER       			= "9997";   //被禁用户
	public static final String SYSTEM_ERR                      		= "9998";   //系统错误
	public static final String API_ERR_MISSED_PARAMATER        		= "9999";   //参数错误(名称错误，或者参数缺失)
	
	public static final String API_ERR_INVALID_SESSION		  		= "1001";   //无效的session
	public static final String API_ERR_ACCOUNT_OR_PASSWD	  		= "1002";   //用户名密码验证错误
	public static final String API_ERR_ACCOUNT_NOT_EXISTS	  		= "1003";   //账号不存在
	public static final String API_ERR_ACCOUNT_EXIST				= "1004";   //账号已存在
	public static final String API_ERR_PASSWD	  					= "1005";   //密码错误
	public static final String API_ERR_NO_UPDATE  					= "1006";   //没有发现更新
	public static final String FILE_SEND_ERR       					= "1010";	//图片上传失败
	public static final String FILE_DOWN_ERR        	 			= "1011";   //图片下载失败
	

	public static final String API_ERR_FRIEND_EXIST					= "1020";   //好友已经存在
	public static final String API_ADDFRIEND_NEEDVERIFY				= "1021";   //添加好友需要验证信息
	public static final String API_ADDFRIEND_WAITVERIFY				= "1022";   //添加好友等待对端验证
	public static final String API_ERR_FRIENT_SELF                  = "1023";   //添加好友为自身  
	public static final String API_GROPNAME_HASEXIST				= "1030";	//组名已存在
	
	public static final Integer RANDOMID_LENGTH                     = 30; //App Session随机长度

	public static String echoErr(String errCode){
		return "oper_result="+errCode+"\n";		
	}
	public static String echoOk(){
		return "oper_result=0\n";
	}
	public static String echoOkArr(HashMap<String,String> result){
		return "oper_result=0"+mapToString(result);
	}
	public static String echoOkArr(String result){
		return "oper_result=0\n"+result;
	}
	
	public static String echoErrArr(String errCode,String resultArr){
		return "ret_code="+errCode+"\n"+resultArr;
	}
	
	public static String mapToString(HashMap<String,String> map){
		Iterator<Entry<String, String>> iter = map.entrySet().iterator();
		StringBuffer sb = new StringBuffer();
		while (iter.hasNext()) {
			Entry<String, String> entry = iter.next();			
			sb.append("\n").append(entry.getKey()).append("=").append(entry.getValue());
		}
		return sb.toString();
	}
}
