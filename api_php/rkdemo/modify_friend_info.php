<?php
/**
 *云视互动测试app：修改好友信息，主要是指备注信息
 *需要传的参数为：
 *	ss:用户session(必填)
 *  account:好友账号(必填)
 *  remark:备注信息
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
$friend_account = isset($_POST['account']) ? trim($_POST['account']) : "";
$remark = isset($_POST['remark']) ? trim($_POST['remark']) : "";

//参数格式校验
if(!CheckCommonFuns::checkSession($session) || !CheckCommonFuns::checkAccount($friend_account) || (!$remark && (strlen($remark)>50 || CheckCommonFuns::containSpecChars($remark)))){
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

// 修改
$result = $databaseManager->modifyFriendInfo($sessionInfo['account'], $friend_account, $remark);
$databaseManager->destoryConn();
if($result){
	ErrCode::echoOk("SUCCESS");
}else{
	ErrCode::echoErr(ErrCode::SYSTEM_ERR);
}
?>