package com.rongketong.utils;

import java.beans.PropertyVetoException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.mysql.jdbc.ResultSetMetaData;
import com.rongketong.dispatcher.Config;
import com.rongketong.dispatcher.ConfigKey;

/**
 * DB 访问辅助类
 * 该类访问基本数据库，全局共享配置数据都在这里
 * 
 *
 */
public class MysqlBaseUtil {
	private static final Logger m_logger = Logger.getLogger(MysqlBaseUtil.class);

	/**
	 * DB 连接池
	 */
	public static ComboPooledDataSource m_connectionPool = null;

	/**
	 * 初始化数据库连接池
	 * 
	 * @throws PropertyVetoException
	 */
	public static void intializeConnectionPool() throws Exception {
		
		String[] baseMysqlInfo = Config.getInstance().getString(ConfigKey.KEY_MYSQL_BASE_INFO).split(",");
		String jdbcUrl="";
		if(baseMysqlInfo.length==4){
			jdbcUrl = "jdbc:mysql://"+ baseMysqlInfo[0]+ "/"+ baseMysqlInfo[1]+"?characterEncoding=utf-8";
			m_logger.info("Initialize base MysqlPool: " + jdbcUrl);
		}else
		{
			StringBuffer sb = new StringBuffer();
			for(int i = 0; i < baseMysqlInfo.length; i++){ sb.append(baseMysqlInfo[i]);}
			throw new IllegalArgumentException("Mysql base info parameter is: "+sb.toString());
		}

		m_connectionPool = new ComboPooledDataSource();
		m_connectionPool.setDriverClass("com.mysql.jdbc.Driver");
		m_connectionPool.setJdbcUrl(jdbcUrl);
		m_connectionPool.setUser(baseMysqlInfo[2]);
		m_connectionPool.setPassword(baseMysqlInfo[3]);
		m_connectionPool.setMinPoolSize(Config.getInstance().getInt(ConfigKey.KEY_POOL_MIN_SIZE));
		m_connectionPool.setMaxPoolSize(Config.getInstance().getInt(ConfigKey.KEY_POOL_MAX_SIZE));
		m_connectionPool.setMaxIdleTime(Config.getInstance().getInt(ConfigKey.KEY_POOL_TIMEOUT));
		m_connectionPool.setMaxStatements(Config.getInstance().getInt(ConfigKey.KEY_POOL_MAX_SQL));
		
	}
	

	/**
	 * 关闭数据库连接池
	 */
	public static void shutdownConnectionPool() {
		try {
			if (m_connectionPool != null)
				m_connectionPool.close();
		} catch (Exception ex) {
			m_logger.warn("Close Connection Pool failed", ex);
		}
	}
	

	/**
	 * 执行一个SQL语句。
	 * 
	 * @param sql
	 *            SQL 语句
	 * @param params
	 *            输入参数，为字符串数组。注意参数和表的 field 的类型必须对应，否则会引起查询效率的降低
	 * @return
	 * @throws SQLException
	 */
	public static ArrayList<HashMap<String, String>> querySql(String sql,String[] params) throws SQLException {
		if (m_logger.isDebugEnabled()) {
			m_logger.debug(MysqlBaseUtil.getPreparedSQL(sql, params));
		}
		Connection conn =null;
		PreparedStatement ps = null;
		ResultSet rs=null;
		try{
			conn = MysqlBaseUtil.m_connectionPool.getConnection();
			ps = (PreparedStatement) conn.prepareStatement(sql);
			if(params!=null && params.length>0){
				for (int i = 1; i <= params.length; i++) {
					ps.setString(i, params[i - 1]);
				}
			}
			// 获取结果集
			rs = ps.executeQuery();
			// 循环读数据
			ArrayList<HashMap<String, String>> rows = new ArrayList<HashMap<String, String>>();
			ResultSetMetaData rsmd = (ResultSetMetaData) rs.getMetaData();
			while (rs.next()) {
				HashMap<String, String> row = new HashMap<String, String>();
				for (int i = 1; i <= rsmd.getColumnCount(); i++) {
					String colName = rsmd.getColumnLabel(i);
					String colValue = rs.getString(i);
					row.put(colName, colValue);
				}
				rows.add(row);
			}
			return rows;
		}finally{
			safeClose(rs);
			safeClose(ps);
			safeClose(conn);
		}		
	}
	
	/**
	 * 获取一行中一个字段值
	 * @param sql
	 * @param params
	 * @return
	 * @throws SQLException
	 */
	public static String getOneColumnByRow(String sql,String[] params) throws SQLException {
		if (m_logger.isDebugEnabled()) {
			m_logger.debug(MysqlBaseUtil.getPreparedSQL(sql, params));
		}
		Connection conn =null;
		PreparedStatement ps = null;
		ResultSet rs=null;
		try{
			conn = MysqlBaseUtil.m_connectionPool.getConnection();
			ps = (PreparedStatement) conn.prepareStatement(sql);
			for (int i = 1; i <= params.length; i++) {
				ps.setString(i, params[i - 1]);
			}
			// 获取结果集
			rs = ps.executeQuery();
			String result=null;
			while(rs.next()){
				result = rs.getString(1);				
			}
			return result;
		}finally{
			safeClose(rs);
			safeClose(ps);
			safeClose(conn);
		}
	}
	/**
	 * 获取图像信息
	 * @param sql
	 * @param params
	 * @return
	 * @throws SQLException
	 * @throws IOException 
	 */
	public static Blob getAuatarOneColumnByRow(String sql,String[] params) throws SQLException, IOException {
		if (m_logger.isDebugEnabled()) {
			m_logger.debug(MysqlBaseUtil.getPreparedSQL(sql, params));
		}
		Connection conn =null;
		PreparedStatement ps = null;
		ResultSet rs=null;
		Blob image=null;
		try{
			conn = MysqlBaseUtil.m_connectionPool.getConnection();
			ps = (PreparedStatement) conn.prepareStatement(sql);
			for (int i = 1; i <= params.length; i++) {
				ps.setString(i, params[i - 1]);
			}
			// 获取结果集
			rs = ps.executeQuery();
			
			while(rs.next()){
				image = rs.getBlob(1);
			}
		}finally{
			safeClose(rs);
			safeClose(ps);
			safeClose(conn);
		}
		return image;
	}
	/**
	 * 获取一列值
	 * @param sql
	 * @param params
	 * @return
	 * @throws SQLException
	 */
	public static ArrayList<String> getOneColumns(String sql,String[] params) throws SQLException {
		if (m_logger.isDebugEnabled()) {
			m_logger.debug(MysqlBaseUtil.getPreparedSQL(sql, params));
		}
		Connection conn =null;
		PreparedStatement ps = null;
		ResultSet rs=null;
		try{
			conn = MysqlBaseUtil.m_connectionPool.getConnection();
			ps = (PreparedStatement) conn.prepareStatement(sql);
			for (int i = 1; i <= params.length; i++) {
				ps.setString(i, params[i - 1]);
			}
			// 获取结果集
			rs = ps.executeQuery();
			ArrayList<String> result=new ArrayList<String>();
			while(rs.next()){
				result.add(rs.getString(1));
			}
			return result;
		}finally{
			safeClose(rs);
			safeClose(ps);
			safeClose(conn);
		}		
	}
	
	public static HashMap<String,String> getOneRow(String sql,String[] params) throws SQLException {
//		if (m_logger.isDebugEnabled()) {
//			m_logger.debug(MysqlBaseUtil.getPreparedSQL(sql, params));
//		}
		Connection conn =null;
		PreparedStatement ps = null;
		ResultSet rs=null;
		try{
			conn = MysqlBaseUtil.m_connectionPool.getConnection();
			ps = (PreparedStatement) conn.prepareStatement(sql);
			if(params!=null && params.length>0){
				for (int i = 1; i <= params.length; i++) {
					ps.setString(i, params[i - 1]);
				}
			}
			// 获取结果集
			rs = ps.executeQuery();
			ResultSetMetaData rsmd = (ResultSetMetaData) rs.getMetaData();
			HashMap<String,String> row=new HashMap<String,String>();
			while(rs.next()){			
				for (int i = 1; i <= rsmd.getColumnCount(); i++) {
					String colName = rsmd.getColumnLabel(i);
					String colValue = rs.getString(i);
					row.put(colName, colValue);
				}
			}
			if(row.size()==0)
				return null;
			else
				return row;
		}finally{
			safeClose(rs);
			safeClose(ps);
			safeClose(conn);
		}
	}
	
	/**
	 * 执行一个 UPDATE 的 SQL语句，返回值是影响到的行数。
	 * 
	 * @param sql
	 * @param params
	 * @return UPDATE 影响到的行数，如果 <=0，说明 Update 失败
	 * @throws SQLException
	 */
	public static int executeUpdateSql(String sql, String[] params) throws SQLException {
		if (m_logger.isDebugEnabled()) {
			m_logger.debug(MysqlBaseUtil.getPreparedSQL(sql, params));
		}
		Connection conn =null;
		PreparedStatement ps = null;
		try{
			conn = MysqlBaseUtil.m_connectionPool.getConnection();
			ps = (PreparedStatement) conn.prepareStatement(sql);
			for (int i = 1; i <= params.length; i++) {
				ps.setString(i, params[i - 1]);
			}
			// 获取结果集
			int result = ps.executeUpdate();
			if (result <= 0) {
				m_logger.warn("Result is a ResultSet, this function is for UPDAT. SQL="
						+ getPreparedSQL(sql, params));
			}
			return result;
		}
		finally{
			safeClose(ps);
			safeClose(conn);
		}		
	}
	
	public static Connection getBaseMysqlConn() throws SQLException {
		return m_connectionPool.getConnection();
	}

	/**
	 * 执行一个存储过程。目前参数仅支持字符串、整型；返回值也只支持整型。
	 * 
	 * @param conn
	 *            db 连接
	 * @param sql
	 *            存储过程的 SQL，如："{ call p_nwc_exit(?, ?, ?) }"
	 * @param params
	 *            输入参数。
	 * @return 返回值，整数
	 * @throws SQLException
	 */
	public static int executeStoreProcedure(String sql,Object[] params,int retNum,HashMap<Integer,String> out) throws SQLException {
		if (m_logger.isDebugEnabled()) {
			StringBuffer sb = new StringBuffer();
			sb.append("Execute Stored Precedure: ").append(sql).append(", Params: ");
			for (int i = 0; i < params.length; i++) {
				sb.append(params[i].toString());
				if (i < (params.length - 1))
					sb.append(",");
			}
			m_logger.debug(sb.toString());
		}
		Connection conn =null;
		CallableStatement cs = null;
		try{
			conn = m_connectionPool.getConnection();		
			int ret = 0;
			cs = conn.prepareCall(sql);
			// 设置 SP 的输入参数，暂时只支持字符串和整型
			for (int i = 0; i < params.length; i++) {
				if (params[i] instanceof String) {
					cs.setString(i + 1, params[i].toString());
				} else if (params[i] instanceof Integer) {
					cs.setInt(i + 1, new Integer(params[i].toString()));
				} else {
					m_logger.warn("Unexpected param type: " + params[i]);
					return ret;
				}
			}
			// 设置 SP 的输出，暂时只支持整型
			for(int i=0;i<retNum;i++){
				cs.registerOutParameter(params.length + i+1, java.sql.Types.VARCHAR);
			}
			if (!cs.execute()) {
				for(int i=0;i<retNum;i++){
					out.put(i+1,cs.getString(params.length + i+1)); // 返回值
				}
			}
			if (m_logger.isDebugEnabled()) {
				m_logger.debug("Stored Precedure: " + sql + " returns: " + ret);
			}
			return ret;
			}finally{
				safeClose(cs);
				safeClose(conn);
			}		
	}

	/**
	 * 安全关闭结果集
	 * 
	 * @param rs
	 */
	public static void safeClose(ResultSet rs) {
		try {
			if (rs != null)
				rs.close();
		} catch (Exception ex) {
			m_logger.warn("Close ResultSet failed.", ex);
		}
	}

	/**
	 * 安全关闭 PreparedStatement
	 * 
	 * @param stat
	 */
	public static void safeClose(PreparedStatement stat) {
		try {
			if (stat != null)
				stat.close();
		} catch (Exception ex) {
			m_logger.warn("Close ResultSet failed.", ex);
		}
	}

	/**
	 * 安全关闭 Connection
	 * 
	 * @param conn
	 */
	public static void safeClose(Connection conn) {
		try {
			if (conn != null)
				conn.close();
		} catch (Exception ex) {
			m_logger.warn("Close db connection failed.", ex);
		}
	}

	/**
	 * 获得PreparedStatement向数据库提交的SQL语句
	 */
	public static String getPreparedSQL(String sql, String[] params) {
		if (1 > params.length)
			return sql;
		StringBuffer returnSQL = new StringBuffer();
		String[] subSQL = sql.split("\\?");
		for (int i = 0; i < params.length; i++) {
			returnSQL.append(subSQL[i]).append(" '").append(params[i])
					.append("' ");
		}
		if (subSQL.length > params.length) {
			returnSQL.append(subSQL[subSQL.length - 1]);
		}
		return returnSQL.toString();
	}
	
	/**
	 * 获得PreparedStatement向数据库提交的SQL语句
	 */
	public static String getPreparedSQL(String sql, ArrayList<Object> params) {
		if (1 > params.size())
			return sql;
		StringBuffer returnSQL = new StringBuffer();
		String[] subSQL = sql.split("\\?");
		for (int i = 0; i < params.size(); i++) {
			if(params.get(i) instanceof String){
				returnSQL.append(subSQL[i]).append("'").append(params.get(i)).append("'");
			}
			else{
				returnSQL.append(subSQL[i]).append(params.get(i));
			}				
		}
		if (subSQL.length > params.size()) {
			returnSQL.append(subSQL[subSQL.length - 1]);
		}
		return returnSQL.toString();
	}
	
	public static int updateSQL(String sql, ArrayList<Object> params)throws SQLException {
		if (m_logger.isDebugEnabled()) {
			m_logger.debug(MysqlBaseUtil.getPreparedSQL(sql, params));
		}
		Connection conn =null;
		PreparedStatement ps = null;
		try{
			conn = m_connectionPool.getConnection();
			ps = (PreparedStatement) conn.prepareStatement(sql);
			for (int i = 0; i < params.size(); i++) {
				if(params.get(i) instanceof String){
					ps.setString(i+1, params.get(i).toString());
				}else if(params.get(i) instanceof Integer){
					ps.setInt(i+1, (int) params.get(i));
				}else if(params.get(i) instanceof Long){
					ps.setLong(i+1, (long) params.get(i));
				}else if(params.get(i) instanceof Double){
					ps.setDouble(i+1, (double) params.get(i));
				}else if(params.get(i) instanceof Float){
					ps.setFloat(i+1, (float) params.get(i));
				}else if(params.get(i)==null){
					ps.setNull(i+1,i+1);				
				}else{
					ps.setString(i+1, params.get(i).toString());				
				}
			}
			// 获取影响结果行
			int updateCount = ps.executeUpdate();
			return updateCount;
		}finally{
			safeClose(ps);
			safeClose(conn);
		}		
	}
	/**
	 * 存储个人头像信息
	 */
	/**
	 * 执行一个 UPDATE 的 SQL语句，返回值是影响到的行数。
	 * 
	 * @param sql
	 * @param params
	 * @return UPDATE 影响到的行数，如果 <=0，说明 Update 失败
	 * @throws SQLException
	 * @throws IOException 
	 */
	public static int executeUpdateAvatar(String sql,FileInputStream inStream,InputStream avatarThumb) throws SQLException, IOException {
		Connection conn =null;
		PreparedStatement ps = null;
		try{
			conn = MysqlBaseUtil.m_connectionPool.getConnection();
			ps = (PreparedStatement) conn.prepareStatement(sql);
			ps.setBinaryStream(1, inStream, inStream.available());
			ps.setBinaryStream(2, avatarThumb, avatarThumb.available());
			// 获取结果集
			boolean result = ps.execute();
			int updateCount = -1;
			if (!result) {
				updateCount = ps.getUpdateCount();
			} else {
				m_logger.warn("Result is a ResultSet, this function is for UPDAT. SQL="+sql);
			}
			return updateCount;
		}
		finally{
			safeClose(ps);
			safeClose(conn);
		}		
	}
	

}
