package com.rongketong.dispatcher;

/**
 * 保存配置文件中的 Key 值
 * 
 */
public interface ConfigKey {
	
	/**
	 * 定义一个唯一的SERVLET API服务器，保证产生全局唯一的消息ID号
	 */
	public static final String KEY_SERVLET_ID = "servlet.id";
	
	/**
	 * 基础MYSQL数据库
	 * 需要从该数据库获取分片信息
	 */
	public static final String KEY_MYSQL_BASE_INFO = "mysql.base.info";
	

	public static final String KEY_POOL_MIN_SIZE = "c3p0.min.size";
	public static final int V_POOL_MIN_SIZE = 5;

	public static final String KEY_POOL_MAX_SIZE = "c3p0.max.size";
	public static final int V_POOL_MAX_SIZE = 20;

	public static final String KEY_POOL_TIMEOUT = "c3p0.timeout";
	public static final int V_POOL_TIMEOUT = 3600;

	public static final String KEY_POOL_MAX_SQL = "c3p0.max.statements";
	public static final int V_POOL_MAX_SQL = 100;

	public static final String KEY_POOL_VALIDATE = "c3p0.validate";
	
	public static final boolean V_POOL_VALIDATE = false;
	
	public static final String VERSION = "1.0"; 

}