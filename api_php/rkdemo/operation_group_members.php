<?php
/**
 *云视互动测试app：选定用户到指定的组中
 *需要传的参数为：
 *	ss:用户session(必填)
 *	gid:组id(必填)
 *	accounts:选定的账号，多个之间用半角逗号分隔
 *
 *返回值：
 *	oper_result：对应的错误码
 *				0：成功
 *				1001:无效的session
 *				9998:系统错误
 *				9999:参数错误
**/ 
include(dirname(__FILE__) . "/common/inc.php");
$logger = Logger::getLogger(basename(__FILE__));

$session = isset($_POST['ss']) ? trim($_POST['ss']) : "";
$groupId = isset($_POST['gid']) ? trim($_POST['gid']) : "";
$friend_accounts = isset($_POST['accounts']) ? trim($_POST['accounts']) : "";

//参数格式校验
if(!CheckCommonFuns::checkSession($session)){
	ErrCode::echoErr(ErrCode::API_ERR_MISSED_PARAMATER);
}

if(0==strlen($groupId) || ($groupId!="0" && !CheckCommonFuns::isDigits2($groupId))){
	ErrCode::echoErr(ErrCode::API_ERR_MISSED_PARAMATER);
}

if(0==strlen($friend_accounts)){
	ErrCode::echoErr(ErrCode::API_ERR_MISSED_PARAMATER);
}
$arrAccounts = explode(",", $friend_accounts);
foreach($arrAccounts as $account){
	if(!CheckCommonFuns::checkAccount(trim($account))){
		ErrCode::echoErr(ErrCode::API_ERR_MISSED_PARAMATER);
	}
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

// 操作
$result = $databaseManager->modify_friends_group($sessionInfo['account'], $friend_accounts, $groupId);
$databaseManager->destoryConn();
if($result){
	ErrCode::echoOk("SUCCESS");
}else{
	ErrCode::echoErr(ErrCode::SYSTEM_ERR);
}
?>