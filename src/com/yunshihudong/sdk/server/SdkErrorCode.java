package com.yunshihudong.sdk.server;

/**
 * SDK 错误代码
 *
 */
public class SdkErrorCode {
	public static final int SUCCESS                      		= 0;   	  //成功
	public static final int SYSTEM_ERR                      	= 9998;   //系统错误
	public static final int SDK_ERR_MISSED_PARAMATER        	= 9999;   //参数错误
	public static final int SDK_ERR_USERNAME_EXIST		  		= 1002;   //用户名已经存在
	public static final int SDK_ERR_SERVERKEY_NOT_EXIST  	 	= 1007;   //serverKey不存在
	public static final int SDK_ERR_NAME_PWD_FORMAT_LEN_ERROR  	= 1008;   //用户名，密码格式，长度不对
	public static final int SDK_ERR_MORE_THAN_MAX_USERS_ERROR  	= 1009;   //添加用户大于最大允许上线100
	public static final int SDK_ERR_MESSAGE_TOO_LONG		  	= 1033;   //消息长度大于最大长度
	public static final int SDK_ERR_MESSAGE_SEND_ERROR	  		= 2002;	  //消息发送失败
	public static final int SDK_ERR_REQEST_TOO_FAST	  			= 9997;	  //请求太快了
}
