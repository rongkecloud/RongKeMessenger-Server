package com.rongketong.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.rongketong.api.Register;
import com.yunshihudong.sdk.server.YsServerSDK;

public class MysqlBaseManager {
	
	private static final Logger m_logger = Logger.getLogger(Register.class);
	
	/**
	 * 注册时判断用户名是否已存在
	 * @throws SQLException 
	 */
	public static boolean isAccountExist(String account) throws SQLException{
		String sql = "SELECT user_account FROM user_accounts WHERE user_account = ?";
		HashMap<String,String> ret = MysqlBaseUtil.getOneRow(sql,new String[]{account});
		return ret == null ? true:false;
	} 
	
	/**
	 * 注册新用户
	 * @param account 用户账号
	 * @param pwd    用户密码
	 * @param type   用户类型  
	 * @param sdkpwd 云视互动SDK密码
	 * @return
	 * @throws SQLException
	 * @throws IOException 
	 * ret 1007  key认证失败
	 *     1008    用户名密码格式错误 
	 *     1002    用户名重复
	 *     9999    参数错误 
	 */
	public static int register(String account,String pwd,String type,String sdkpwd) throws SQLException, IOException{
		int ret = 0;
		String md5pwd = Tools.md5(pwd);
		Connection conn=null;
		try {
			String sql = String.format("INSERT INTO user_accounts(user_account, user_pwd, user_type, sdk_pwd,created,updated)"
					+ "VALUES('%s','%s','%s','%s',now(),now())", account,md5pwd,type,sdkpwd);
			conn = MysqlBaseUtil.getBaseMysqlConn();	
			conn.setAutoCommit(false);
			Statement stmt=conn.createStatement();
			stmt.executeUpdate(sql);
			//发送通知
	    	YsServerSDK client = Tools.geYsServerAPI(); 
			/*添加用户到云视互动*/
			ret = client.addUser(account, sdkpwd);
			m_logger.info(String.format("adduser ret=%d",ret));
			if(ret == 0){
				//添加成功
				conn.commit();
				stmt.close();				
				conn.close();
				return ret;
			}
			else{
/*				if(ret == 1007){
					//key认证失败
				}else if(ret == 1008){
					//用户名密码格式错误
				}else if(ret == 1002){
					//用户名重复 
				}else if(ret == 9999){
					//参数出错
				}*/
				conn.rollback();			
				conn.close();
				return ret;
			}
		} catch (SQLException e) {
			try {
				conn.rollback();			
				conn.close();
			} catch (SQLException e1) {
				
			}
			return ret;
		} 		
	}
	/**
	 * 校验session合法性并返回相应结果集
	 * @throws SQLException 
	 */
	public static Map<String,String> checkSession(String session) throws SQLException{
		Map<String,String> resultList = new HashMap<String,String>();
		String sql = "SELECT session,user_account, user_pwd FROM user_accounts WHERE `session` = ?";
		resultList = MysqlBaseUtil.getOneRow(sql,new String[]{session});
		return resultList;
	}
	
	/**
	 * 修改密码
	 * @throws SQLException 
	 */
	public static boolean modifyPwd(String account, String newPwd) throws SQLException{
		String sql = "UPDATE user_accounts SET user_pwd= MD5(?),updated=now() WHERE user_account=?";
		int eff_count = MysqlBaseUtil.executeUpdateSql(sql,new String[]{newPwd,account});
		if(eff_count <=0){
			return false;
		}
		return true;
	}
	/**
	 * 修改好友备注信息
	 * @throws SQLException 
	 */
	public static boolean  modifyFriendInfo(String account,String friendAccount, String remark) throws SQLException {
		String sql  = "update user_friends set friend_remark = ? where user_account=? and friend_account=?";
		int	eff_count = MysqlBaseUtil.executeUpdateSql(sql,new String[]{remark,account,friendAccount});
		if(eff_count < 0){
			return false;
		}
		return true;
	}
	/**
	 * 修改好友分组(此方法需要事务处理)
	 * @return 
	 * @throws SQLException 
	 */
	public static boolean modify_friends_group(String user_account,String accounts,Integer gid) throws SQLException{
		if(gid>0){
			String sql = "select user_account from user_groups where user_account = ? and gid = "+gid;
			Map<String,String>  mapResult = MysqlBaseUtil.getOneRow(sql, new String[]{user_account});
			if(mapResult == null){
				return false;
			}
		}
//		String synchroSql = "update user_friends set gid = 0 where user_account = ?";
		String tempAccountList = "'"+accounts.replaceAll(",","','")+"'";
		String updateSql = "UPDATE user_friends SET gid = "+gid+" WHERE user_account = ? AND friend_account IN("+tempAccountList+");";
		/*int eff_update_count;
		int eff_delete_count;
		Connection conn = null;
		PreparedStatement update_ps = null;
		PreparedStatement delete_ps = null;*/
		int eff_count = MysqlBaseUtil.executeUpdateSql(updateSql,new String[]{user_account});
		if(eff_count <=0){
			return false;
		}
		return true;
/*		String updateSql = "update user_friends set gid = 0 where gid="+groupId+" and user_account = ?";
		String delete_sql = "delete from user_groups where gid = "+groupId+" and user_account = ?";
		int eff_update_count;
		int eff_delete_count;
		Connection conn = null;
		PreparedStatement update_ps = null;
		PreparedStatement delete_ps = null;
		try {
			conn = MysqlBaseUtil.getBaseMysqlConn();
			conn.setAutoCommit(false);
			update_ps  = conn.prepareStatement(updateSql);
			update_ps.setString(1,account);
			eff_update_count = update_ps.executeUpdate();
			if(eff_update_count < 0){
				eff_update_count =  -1;
			}
			eff_delete_count = MysqlBaseUtil.executeUpdateSql(delete_sql,new String[]{account});
			delete_ps = conn.prepareStatement(delete_sql);
			delete_ps.setString(1,account);
			eff_delete_count = delete_ps.executeUpdate();
			if(eff_delete_count < 0){
				eff_delete_count =  -1;
			}
			if(eff_update_count!=-1 && eff_delete_count!=-1){
				return true;
			}
		} catch (SQLException e) {
			m_logger.info(String.format("FAILED params=%s","error cause:"+e.getCause()));
			return false;
		}finally{
			try {
				conn.commit();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			if(update_ps!=null){
				try {
					update_ps.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(delete_ps!=null){
				try {
					delete_ps.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(conn!=null){
				try {
					conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return false;*/
	}
	/**
	 * 添加组
	 * -1 分组已存在
	 *  1 程序正常执行
	 * -2 系统错误
	 * @throws SQLException 
	 */
	public static Integer addGroup(String account,String groupName) throws SQLException{
		String existSql = "select gid from user_groups where user_account = ? and group_name = ?";
		Map<String,String>  mapResult = MysqlBaseUtil.getOneRow(existSql, new String[]{account,groupName});
		if(mapResult!=null){
			return -1;
		}
		String insertSql = "insert into user_groups(user_account,group_name, created, updated) values(?,?,now(),now())";
		int eff_count;
		eff_count = MysqlBaseUtil.executeUpdateSql(insertSql,new String[]{account,groupName});
		if(eff_count <= 0){
			m_logger.error("add group Exception,Result Code:"+eff_count);
			return -2;
		}
		Map<String,String> resultList = new HashMap<String,String>();
		String lastSql = "SELECT max(gid) as lastGid from user_groups where user_account = ?";
		resultList = MysqlBaseUtil.getOneRow(lastSql,new String[]{account});
		if(resultList.size()>0){
			return Integer.parseInt(resultList.get("lastGid"));
		}else{
			m_logger.info(String.format("Receive LastGid fail,pamars=%s",resultList.get("lastGid")));
			return -2;
		}
	}
	/**
	 * 修改组名称
	 * @throws SQLException 
	 */
	public static Integer modifyGroup(String account,Integer groupId,String groupName) throws SQLException{
		String existSql = "select gid from user_groups where user_account = ? and group_name = ? and gid <> "+groupId;
		Map<String,String>  mapResult = MysqlBaseUtil.getOneRow(existSql, new String[]{account,groupName});
		if(mapResult!=null){
			return -1;
		}
		String updateSql = "update user_groups set group_name = ?,updated = now() where user_account = ? and gid = "+groupId;
		int eff_count;
		eff_count = MysqlBaseUtil.executeUpdateSql(updateSql,new String[]{groupName,account});
		if(eff_count <=0){
			return -2;
		}
		return 1;
	}
	/**
	 * 删除组(删除操作此处需要事务控制)
	 */
	public static boolean deleteGroup(String account,Integer groupId){
		String updateSql = "update user_friends set gid = 0 where gid="+groupId+" and user_account = ?";
		String delete_sql = "delete from user_groups where gid = "+groupId+" and user_account = ?";
		int eff_update_count;
		int eff_delete_count;
		Connection conn = null;
		PreparedStatement update_ps = null;
		PreparedStatement delete_ps = null;
		try {
			conn = MysqlBaseUtil.getBaseMysqlConn();
			conn.setAutoCommit(false);
			update_ps  = conn.prepareStatement(updateSql);
			update_ps.setString(1,account);
			eff_update_count = update_ps.executeUpdate();
			if(eff_update_count < 0){
				eff_update_count =  -1;
			}
			eff_delete_count = MysqlBaseUtil.executeUpdateSql(delete_sql,new String[]{account});
			delete_ps = conn.prepareStatement(delete_sql);
			delete_ps.setString(1,account);
			eff_delete_count = delete_ps.executeUpdate();
			if(eff_delete_count < 0){
				eff_delete_count =  -1;
			}
			if(eff_update_count!=-1 && eff_delete_count!=-1){
				return true;
			}
		} catch (SQLException e) {
			m_logger.info(String.format("FAILED params=%s","error cause:"+e.getCause()));
			return false;
		}finally{
			try {
				conn.commit();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				m_logger.info(String.format("Connection Commit fail,Exception cause:"+e1.getCause()+",error Msg:"+e1.getMessage()));
			}
			if(update_ps!=null){
				try {
					update_ps.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					m_logger.info(String.format("update_ps close fail,Exception cause:"+e.getCause()+",error Msg:"+e.getMessage()));
				}
			}
			if(delete_ps!=null){
				try {
					delete_ps.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					m_logger.info(String.format("delete_ps close fail,Exception cause:"+e.getCause()+",error Msg:"+e.getMessage()));
				}
			}
			if(conn!=null){
				try {
					conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					m_logger.info(String.format("conn close fail,Exception cause:"+e.getCause()+",error Msg:"+e.getMessage()));
				}
			}
		}
		return false;
	}
	/**
	 * 操作个人信息
	 * @throws SQLException 
	 */
	public static String operation_personal_info(String accountName,String key,String value) throws SQLException{
		StringBuffer update_sql = new StringBuffer();
		update_sql.append("update user_accounts set ");
//		if(key.equals("permission_validation") || key.equals("sex")){
//			update_sql.append(""+key+" = "+value);
//		}else{
//			update_sql.append(""+key+" = ?");
//		}
		update_sql.append(key+" = ?");
		update_sql.append(",info_version=info_version+1,updated = now() where user_account = ?");
		int eff_update_count = MysqlBaseUtil.executeUpdateSql(update_sql.toString(),new String[]{value,accountName});
		if(eff_update_count < 0){
			return "fail";
		}
		String select_sql = "select info_version from user_accounts where user_account = ?";
		String infoVersion = MysqlBaseUtil.getOneColumnByRow(select_sql, new String[]{accountName});
		return infoVersion; 
	}
	/**
	 * 搜索通讯录数据
	 * @throws SQLException 
	 */
	public static List<HashMap<String, String>> search_contact_infos(String account) throws SQLException{
		String select_sql = "select user_account account from user_accounts where user_account like '%"+account+"%' ORDER BY user_account ASC";
		List<HashMap<String, String>> userInfoList = MysqlBaseUtil.querySql(select_sql,new String[]{});
		return userInfoList;
	}
	/**
	 * 批量获取个人信息
	 * @throws SQLException 
	 */
	public static List<HashMap<String,String>> get_personal_infos(String accounts) throws SQLException{
		String tempAccountList = "'"+accounts.replaceAll(",","','")+"'";
		String select_sql = "SELECT a.user_account account,IFNULL(a.name,'') `name`,IFNULL(a.address,'') address,a.sex sex, a.info_version,a.avatar_version,"+
						     "IFNULL(a.email,'') email,a.user_type `type`,IFNULL(a.mobile,'') mobile"+
						     " FROM user_accounts a WHERE a.user_account in("+tempAccountList+")";
			List<HashMap<String, String>> userInfoList = MysqlBaseUtil.querySql(select_sql,new String[]{});
			return userInfoList;
	}
	/**
	 * 获取分组信息
	 * @throws SQLException 
	 */
	public static List<HashMap<String,String>> get_group_infos(String user_account) throws SQLException{
		String select_sql = "select gid,group_name gname from user_groups where user_account = ?";	
		List<HashMap<String, String>> userInfoList = MysqlBaseUtil.querySql(select_sql,new String[]{user_account});
		return userInfoList; 
	}
	/**
	 * 获取好友信息
	 * @throws SQLException 
	 */
	public static List<HashMap<String,String>> get_friend_infos(String user_account) throws SQLException {
		String select_sql="SELECT a.gid,a.friend_remark remark,b.user_account account, IFNULL(b.name,'') name, IFNULL(b.address,'') address,"+ 
				"b.user_type type,b.sex,IFNULL(b.mobile,'') mobile,IFNULL(b.email,'') email,b.info_version,b.avatar_version"+
				" FROM user_friends a, user_accounts b WHERE a.friend_account=b.user_account AND a.user_account=?";
		List<HashMap<String, String>> userInfoList = MysqlBaseUtil.querySql(select_sql,new String[]{user_account});
		return userInfoList;

	}
	/**
	 * 获取用户信息
	 */
	public static HashMap<String,String> get_account_info(String user_account){
		String select_sql = "select user_account,permission_validation from user_accounts where user_account = ?";
		try {
			HashMap<String, String> userInfoList = MysqlBaseUtil.getOneRow(select_sql,new String[]{user_account});
			return userInfoList;
		} catch (SQLException e) {
			m_logger.info(String.format("FAILED params=%s","error cause:"+e.getCause()+",error SQL:"+select_sql));
			return null;
		}
	}
	
	/**
	 * 获取头像路径
	 * @throws SQLException 
	 * @throws IOException 
	 */
	public static InputStream get_avatar(String user_account,String type) throws SQLException, IOException {
		  //展示：
		String select_sql = "";
		if(type.equals("1")){
			select_sql = "SELECT user_avatar_thumb data FROM user_avatars WHERE user_account = ?";
		}else{
			select_sql = "SELECT user_avatar data FROM user_avatars WHERE user_account = ?";
		}
		Blob image = null;
		try {
			image = MysqlBaseUtil.getAuatarOneColumnByRow(select_sql,new String[]{user_account});
		} catch (SQLException | IOException e) {
			// TODO Auto-generated catch block
			m_logger.info(String.format("FAILED params=%s","error cause:"+e.getCause()+",error SQL:"+select_sql));
			return null;
		}
		if(image == null){
			return null;
		}else{
			return image.getBinaryStream();
		}
	}
	/**
	 * 判断是否是好友
	 */
	public static boolean is_friend(String user_account, String friend_account){
		String select_sql = "select user_account from user_friends where user_account = ? and friend_account = ?";
		String select_result;
		try {
			select_result = MysqlBaseUtil.getOneColumnByRow(select_sql, new String[]{user_account,friend_account});
			if(select_result == null || select_result.equals("")){
				return false;
			}
		} catch (SQLException e) {
			m_logger.info(String.format("FAILED params=%s","error cause:"+e.getCause()+",error SQL:"+select_sql));
			return false;
		}
		return true;
	}
	/**
	 * 删除好友
	 * @throws SQLException 
	 */
	public static boolean  delFriend(String user_account, String friend_account) throws SQLException{
		String delete_sql = "delete from user_friends where (user_account = '"+user_account+"' and friend_account = '"+friend_account+"') or (user_account = ? and friend_account = ?)";
		int eff_delete_count;
		eff_delete_count = MysqlBaseUtil.executeUpdateSql(delete_sql.toString(),new String[]{friend_account,user_account});
		if(eff_delete_count < 0){
			return false;
		}
		return true;
	}
	/**
	 * 确认添加好友
	 */
	public static boolean confirm_add_friend(String user_account, String friend_account) {
		String insert_sql = "insert into user_friends(user_account,friend_account,gid) values('"+user_account+"','"+friend_account+"',0),(?,?,0)";
		int eff_delete_count;
		try {
			eff_delete_count = MysqlBaseUtil.executeUpdateSql(insert_sql.toString(),new String[]{friend_account,user_account});
			if(eff_delete_count < 0){
				return false;
			}
			return true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			m_logger.info(String.format("FAILED params=%s","error cause:"+e.getCause()+",error SQL:"+insert_sql));
			return false;
		}
	}
	/**
	 * 检查更新
	 */
	public static HashMap<String,String> checkUpdate(String os_type){
		String select_sql = "select * from updateinfo where os_type = ?";
		try {
			HashMap<String, String> updateInfoList = MysqlBaseUtil.getOneRow(select_sql,new String[]{os_type});
			return updateInfoList;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			m_logger.info(String.format("FAILED params=%s","error cause:"+e.getCause()+",error SQL:"+select_sql));
			return null;
		}
	}
	/**
	 * 添加问题反馈
	 * @throws SQLException 
	 */
	public static boolean  addFeedback(String user_account,String type,String content) throws SQLException {
		String select_sql = "insert into feedbacks(user_account,type,content,created) values(?,?,?,now())";		
		int	eff_delete_count = MysqlBaseUtil.executeUpdateSql(select_sql.toString(),new String[]{user_account,type,content});
		if(eff_delete_count < 0){
			return false;
		}
		return true;
	}
	/**
	 * 系统登录
	 */
	public static HashMap<String,String> appLogin(String user_account,String user_pwd){
		HashMap<String,String> userMap = new HashMap<String,String>();
//		//校验用户名是否已存在
		String check_sql = "SELECT user_account,user_pwd,sdk_pwd,IFNULL(user_type,'') as type,IFNULL(name,'') name,IFNULL(sex,'') sex,IFNULL(address,'') address,IFNULL(mobile,'') mobile,IFNULL(email,'') email,permission_validation permission,info_version,avatar_version FROM user_accounts WHERE user_account = ?";
		HashMap<String, String>  check_result = null;
		try {
			check_result = MysqlBaseUtil.getOneRow(check_sql, new String[]{user_account.toLowerCase()});
			if(check_result == null || check_result.size()<=0){
				 userMap.put("check_result", "-1"); //账号不存在
				 return userMap;
			}
		} catch (SQLException e) {
 			m_logger.info(String.format("FAILED params=%s %s",user_account,"error cause:"+e.getCause()));
			userMap.put("check_result", ApiErrorCode.SYSTEM_ERR); //系统内部错误
			return userMap;
		}
		//校验密码是否正确
		if(!check_result.get("user_pwd").equals(Tools.md5(user_pwd))){
			 userMap.put("check_result", "-2"); //密码错误
			 return userMap;
		}
		//写session并保证session不重复，且每个用户名在表中只存在一条
		String appSession = Tools.getRandomStr(ApiErrorCode.RANDOMID_LENGTH);
		String update_sql = "update user_accounts set session = ?,updated = now() where user_account = ?";
		int eff_delete_count;
		try {
			eff_delete_count = MysqlBaseUtil.executeUpdateSql(update_sql,new String[]{appSession,user_account});
			if(eff_delete_count < 0){
				userMap.put("check_result", ApiErrorCode.SYSTEM_ERR); //系统内部错误
				return userMap;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			m_logger.info(String.format("FAILED params=%s","error cause:"+e.getCause()));
			userMap.put("check_result", ApiErrorCode.SYSTEM_ERR); //系统内部错误
			return userMap;
		}
		check_result.put("ss", appSession);
		return check_result;
	}
	/**
	 * 保存头像路径
	 * inStream  图像信息
	 * 
	 * @throws IOException 
	 */
	public static HashMap<String,String> saveAvatar(String user_account,FileInputStream inStream,InputStream avatarThumb){
		String check_sql = "select user_account from user_avatars where user_account = ?";
		HashMap<String, String>  check_result = null;
		HashMap<String, String>  saveAvatar_result = new HashMap<String,String>();
		String insert_sql = "";
		try {
			check_result = MysqlBaseUtil.getOneRow(check_sql, new String[]{user_account.toLowerCase()});
			if(check_result == null || check_result.size()<=0){
				insert_sql = "INSERT INTO user_avatars VALUES('"+user_account+"',?,?,null)";
			}else{
				insert_sql = "update user_avatars set user_avatar = ?,user_avatar_thumb = ?,updated = now() where user_account = '"+user_account+"'";
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			m_logger.info(String.format("FAILED params=%s","error cause:"+e.getCause()+",error SQL"+insert_sql));
			saveAvatar_result.put("fail", ApiErrorCode.SYSTEM_ERR); //系统内部错误
			return saveAvatar_result;
		}
		int eff_delete_count;
		try {
			eff_delete_count = MysqlBaseUtil.executeUpdateAvatar(insert_sql,inStream,avatarThumb);
			if(eff_delete_count < 0){
				saveAvatar_result.put("fail", ApiErrorCode.SYSTEM_ERR); //系统内部错误
				return saveAvatar_result;
			}else{
				String select_user_sql = "select avatar_version from user_accounts where user_account = ?";
				saveAvatar_result.put("avatar_version",MysqlBaseUtil.getOneColumnByRow(select_user_sql, new String[]{user_account}));
			}
		} catch (SQLException | IOException e) {
			// TODO Auto-generated catch block
			m_logger.info(String.format("FAILED params=%s","error cause:"+e.getCause()));
			saveAvatar_result.put("fail", ApiErrorCode.SYSTEM_ERR); //系统内部错误
			return saveAvatar_result;
		}
		return saveAvatar_result;
	}
}
