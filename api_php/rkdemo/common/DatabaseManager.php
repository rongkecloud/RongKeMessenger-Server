<?php
	include(dirname(__FILE__) . "/DataBase_Mysql.php");
	include(dirname(__FILE__) . "/Tools.php");
class DatabaseManager{
	private $conn;
	public $monitor_conn;
	private $callCenterConn;
	private $logger;
	public function __construct(){
		$this->logger = Logger::getLogger(basename(__FILE__));
	}
	
	//取得数据库配置信息
	private function getMasterDbConfig(){
		$config = new Config();
		$databaseConfigArr = array(
			"connmethod" => "",
			"driver" => "mysql",
			"host" => $config->getConfig("MASTER_HOST"),
			"username" => $config->getConfig("MASTER_USER"),
			"password" => $config->getConfig("MASTER_PASS"),
			"dbname" => $config->getConfig("MASTER_NAME"),
			"dbcharacter" => $config->getConfig("MASTER_CHARSET")
		);	
		return 	$databaseConfigArr;
	}
 
	public function getMConn(){
		$database = new DataBase_Mysql();
		$isConnected = $database->connect($this->getMasterDbConfig());
		if($isConnected){
			$this->monitor_conn = $database;
			return $this->monitor_conn;
		} else {
			return null;
		}
	}
    public function destoryMConn(){
        $this->monitor_conn->disConnect();
    }

	//返回数据库链接
	public function getConn(){
		$database = new DataBase_Mysql();
		$isConnected = $database->connect($this->getMasterDbConfig());
		if($isConnected){
			$this->conn = $database;
			return $this->conn;
		} else {
			return null;
		}
	}

	//销毁数据库连接
	public function destoryConn(){
		$this->conn->disConnect();
	}
	
	/**
	 * 校验session合法性并返回相应结果集
	 */
	public function checkSession($session){
		$sql = "SELECT session,user_account account, user_pwd FROM user_accounts WHERE session='$session'";		
		$rel = $this->conn->getRow($sql);
		if($rel){
			return $rel;
		}else{
			return false;
		}
	}
	///////////////////////////////////////////云视互动测试demo api接口逻辑处理////////////////////////////////////////////
	/**
	 * 注册时判断用户名是否已存在
	 */
	public function isAccountExist($account){
		//将用户名转换为小写
		$account = strtolower($account);
		$sql = "SELECT * FROM user_accounts WHERE user_account = '$account'";
		return $this->conn->getRow($sql);
	} 
	/**
	 * 注册
	 */
	public function register($account, $pwd, $type, $sdkPwd){
		//将用户名转换为小写
		$account = strtolower($account);
		$md5Pwd = md5($pwd);
		$sql = "INSERT INTO user_accounts(user_account, user_pwd, user_type, sdk_pwd,created,updated)
				VALUES('$account', '$md5Pwd', $type,'$sdkPwd', now(), now())";
		return $this->conn->execute($sql);
	}
	/**
	 * 删除用户
	 */
	public function deleteAccount($account){
		//将用户名转换为小写
		$account = strtolower($account);
		$sql = "delete from user_accounts where user_account = '$account'";
		$this->logger->debug("===========================$sql");
		$result = $this->conn->execute($sql);
		return $result;
	}
	
	/**
	 * 登录
	 */
	public function login($account,$pwd){
		$ret_array = array();
		//将用户名转换为小写
		$account = strtolower($account);
		//校验用户名是否已存在
		$sql = "SELECT * FROM user_accounts WHERE user_account = '$account'";
		$result = $this->conn->getRow($sql);
		if(!$result)
			return -1;//账号不存在
		
		//校验密码是否正确
		$pwd = MD5($pwd);
		if($result['user_pwd'] != $pwd){
			return -2;//账号密码错误
		}

		//写session并保证session不重复，且每个用户名在表中只存在一条
		$session = getRandomID(30);
		$sql = "UPDATE user_accounts SET session='$session',updated=now() WHERE user_account= '$account'";
		$ret = $this->conn->execute($sql);				
		//对一些为NULL的数据进行处理
		$ret_array['ss'] = $session;
		$ret_array['sdk_pwd'] = $result['sdk_pwd'];
		$ret_array['name'] = (NULL == $result['name']) ? "" : $result['name'];
		$ret_array['address'] = (NULL == $result['address']) ? "" : $result['address'];
		$ret_array['sex'] = (NULL == $result['sex']) ? "" : $result['sex'];
		$ret_array['mobile'] = (NULL == $result['mobile']) ? "" : $result['mobile'];
		$ret_array['email'] = (NULL == $result['email']) ? "" : $result['email'];
		$ret_array['type'] = (NULL == $result['user_type']) ? "" : $result['user_type'];
		$ret_array['permission'] = $result['permission_validation'];		
		$ret_array['info_version'] = $result['info_version'];
		$ret_array['avatar_version'] = $result['avatar_version'];
		return $ret_array;
	}
	
	/**
	 * 修改密码
	 */
	public function modifyPwd($account, $pwd){
		$md5Pwd = md5($pwd);
		$sql = "UPDATE user_accounts SET user_pwd='$md5Pwd',updated=now() WHERE user_account='$account'";
		return $this->conn->execute($sql);	
	}
	
	/**
	 * 获取分组信息
	 */
	public function get_group_infos($account) {
		$sql="SELECT gid, group_name gname FROM user_groups WHERE user_account='$account'";
		return $this->conn->getAll($sql);
	}
	
	/**
	 * 获取好友信息
	 */
	public function get_friend_infos($account) {
		$sql="SELECT a.gid, 
					 a.friend_remark remark,
					 b.user_account account, 
					 IFNULL(b.name,'') name, 
					 IFNULL(b.address,'') address, 
					 b.user_type type,
					 b.sex,
					 IFNULL(b.mobile,'') mobile, 
					 IFNULL(b.email,'') email,
					 b.info_version,
					 b.avatar_version
				FROM user_friends a, user_accounts b
				WHERE a.friend_account=b.user_account AND a.user_account='$account'";
		return $this->conn->getAll($sql);
	}
	
	/**
	 * 批量获取个人信息
	 */
	public function get_personal_infos($account,$accounts) {
		$tempAccountList = "'".str_replace(",","','",$accounts)."'";
		$sql = "SELECT a.user_account account,IFNULL(a.name,'') `name`,IFNULL(a.address,'') address,a.sex sex, a.info_version,a.avatar_version,
							IFNULL(a.email,'') email,a.user_type `type`,IFNULL(a.mobile,'') mobile
					  FROM user_accounts a WHERE a.user_account in($tempAccountList)";
		return $this->conn->getAll($sql);
	}

	/**
	 * 添加组
	 */
	public function addGroup($account, $groupName){
		$existSql = "SELECT * FROM user_groups WHERE user_account='$account' AND group_name='$groupName'";
		$existRes = $this->conn->getRow($existSql);
		if($existRes){
			return -1;// 已存在
		}
		$sql = "insert into user_groups(user_account,group_name, created, updated) values('$account','$groupName', now(), now())";
		$this->conn->execute($sql);
		return $this->conn->getInsertID();
	}
	
	/**
	 * 修改组名称
	 */
	public function modifyGroup($account, $groupId, $groupName){
		$existSql = "SELECT * FROM user_groups WHERE user_account='$account' AND group_name='$groupName' AND gid!=$groupId";
		$existRes = $this->conn->getRow($existSql);
		if($existRes){
			return -1;// 已存在
		}
		$sql = "update user_groups set group_name = '$groupName',updated=now() where user_account='$account' AND gid = $groupId";
		$this->conn->execute($sql);
		return $this->conn->execute($sql) ? 1 : 0;
	}
	
	/**
	 * 删除组
	 */	
	public function deleteGroup($account,$groupId){
		$this->conn->startTrans();
		$sql1 = "update user_friends set gid = 0 where gid = $groupId and user_account='$account'";
		$sql2 = "delete from user_groups where gid = $groupId and user_account='$account'";
		if($this->conn->execute($sql1) && $this->conn->execute($sql2)){
			$this->conn->completeTrans(true);
			return true;
		}else{
			$this->conn->completeTrans(false);
			return false;
		}		
	}
	
	/**
	 * 修改好友备注信息 
	 */
	public function modifyFriendInfo($account,$friendAccount, $remark) {
		$sql = "update user_friends set friend_remark = '$remark' where user_account='$account' and friend_account='$friendAccount'";
		return $this->conn->execute($sql);
	}
	
	/**
	 * 删除好友
	 */
	public function delFriend($currAccount, $friendAccount){
		$sql = "delete from user_friends where (user_account='$currAccount' and friend_account='$friendAccount') or (user_account='$friendAccount' and friend_account='$currAccount')";
		return $this->conn->execute($sql);
	}
	
	/**
	 * 判断是否是好友
	 */
	public function is_friend($user_account, $friend_account){
		$sql = "select * from user_friends where user_account = '$user_account' and friend_account = '$friend_account'";
		$result = $this->conn->getRow($sql);
		return $result;
	}
	
	/**
	 * 获取用户信息
	 */
	public function get_account_info($account){
		$sql = "select * from user_accounts where user_account = '$account'";
		$result = $this->conn->getRow($sql);
		return $result;
	}
	
	/**
	 * 确认添加好友
	 */
	public function confirm_add_friend($account, $faccount) {
		$sql = "insert into user_friends(user_account,friend_account,gid) values('$account','$faccount',0),('$faccount','$account',0)";
		return $this->conn->execute($sql);
	}
	
	/**
	 * 操作个人信息
	 */
	public function operation_personal_info($account,$key,$value) {	
		$sql = "update user_accounts set ";
		if($key == 'permission_validation' || $key=='sex'){
			$sql .= "$key=$value";
		}else{
			$sql .= "$key='$value'";
		}
			
		$sql .= ", info_version=info_version+1, updated=now() where user_account = '$account'";
		$result = $this->conn->execute($sql);
		if(!$result){
			return false;
		}
		$sql = "select info_version from user_accounts where user_account = '$account'";
		$ret_array = array();
		$ret_array['info_version'] = $this->conn->getOne($sql);
		return $ret_array;
	}
	
	/**
	 * 修改好友分组
	 * @return 
	 */
	public function modify_friends_group($user_account, $accounts, $gid) {
		if($gid > 0){
			$sql = "select * from user_groups where user_account='$user_account' and gid = $gid";
			$result = $this->conn->getRow($sql);
			if(!$result){
				return false;
			}
		}
		
		$tempAccountList = "'".str_replace(",","','",$accounts)."'";
		$updateSql = "UPDATE user_friends SET gid=$gid WHERE user_account = '$user_account' AND friend_account IN($tempAccountList);";
		return $this->conn->execute($updateSql);
	}
	
	/**
	 * 获取头像路径
	 */
	public function get_avatar($account,$type) {
		  //展示：
		
		if($type == 1){
			$sql = "SELECT user_avatar_thumb data FROM user_avatars WHERE user_account='$account'";
		}else{
			$sql = "SELECT user_avatar data FROM user_avatars WHERE user_account='$account'";
		}
		$result = $this->conn->queryBlob($sql);
        return $result;
	}
	
	/**
	 * 保存头像路径
	 */
	public function saveAvatar($account,$file,$thumb){
		$ret_array = array();
		$fileSize = filesize($file);
		$myAvatar = addslashes(fread(fopen($file, "r"),$fileSize)); 
		$thumbSize = filesize($thumb);
		$thumbAvatar = addslashes(fread(fopen($thumb, "r"),$thumbSize)); 
		$sql = "INSERT INTO user_avatars VALUES('$account','$myAvatar','$thumbAvatar',null) 
		        ON DUPLICATE KEY UPDATE  user_avatar='$myAvatar',user_avatar_thumb='$thumbAvatar',updated=NOW()";
		$result = $this->conn->saveBlob($sql);
		if($result){
			$sql = "select avatar_version from user_accounts where user_account='$account'";
			$ret_array['avatar_version'] = $this->conn->getOne($sql);
		}
		return $ret_array;
	}
	/**
	 * 搜索通讯录数据
	 */
	public function search_contact_infos($currAccount, $account) {
		$sql="SELECT user_account account FROM user_accounts WHERE user_account LIKE'$account%' and user_account!='$currAccount'";
		return $this->conn->getAll($sql);
	}
	/**
	 * 添加问题反馈
	 */
	public function addFeedback($account,$type,$content) {
		$sql="insert into feedbacks(user_account,type,content,created) values('$account',$type,'$content',now());";
		return $this->conn->execute($sql);
	}
	/**
	 * 检查更新
	 */
	public function checkUpdate($os_type){
		$sql = "select * from updateinfo where os_type = '$os_type'";
		return $this->conn->getRow($sql);
	}
///---------------------------------------------------------Class end-------------------------------------------------///
}
?>
