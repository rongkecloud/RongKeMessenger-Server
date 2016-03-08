<?php
/**
 *云视互动测试app：删除好友
 *需要传的参数为：
 *	ss:用户session(必填)
 *  account:好友账号(必填)
 *
 *返回值：
 *	oper_result：对应的错误码
 *				0：成功
 *				1001:无效的session
 *				9998:系统错误
 *				9999:参数错误
**/
include(dirname(__FILE__) . "/common/inc.php");
include(dirname(__FILE__) . "/rkcloud/RkCloudApi.php");

$logger = Logger::getLogger(basename(__FILE__));

$session = isset($_POST['ss']) ? trim($_POST['ss']) : "";
$friend_account = isset($_POST['account']) ? trim($_POST['account']) : "";

//参数格式校验
if(!CheckCommonFuns::checkSession($session) || !CheckCommonFuns::checkAccount($friend_account)){
	ErrCode::echoErr(ErrCode::API_ERR_MISSED_PARAMATER);
}
	
// 数据库连接
$databaseManager = new DatabaseManager();
$database = $databaseManager->getConn();
//数据库链接失败
if(!$database){
	$logger->error(sprintf("Database connect fail."));
	ErrCode::echoErr(ErrCode::SYSTEM_ERR);
}

// 检查session
$sessionInfo = $databaseManager->checkSession($session);
if(!$sessionInfo){
	$databaseManager->destoryConn();
	ErrCode::echoErr(ErrCode::API_ERR_INVALID_SESSION);
}
	
//判断用户是否是好友，如果不是，直接返回
$isFriend = $databaseManager->is_friend($sessionInfo['account'], $friend_account);
if(!$isFriend){
	$databaseManager->destoryConn();
	ErrCode::echoOk("SUCCESS");
}

// 删除用户
$delResult = $databaseManager->delFriend($sessionInfo['account'], $friend_account);
$databaseManager->destoryConn();
if($delResult){
	// 发送通知
	$rkcloudapiObj = new RkCloudApi();
	$notifyRes = $rkcloudapiObj->sendUserMsg($sessionInfo['account'], $friend_account, "delete_friend");
	ErrCode::echoOk("SUCCESS");
}else{
	ErrCode::echoErr(ErrCode::SYSTEM_ERR);
}
?>