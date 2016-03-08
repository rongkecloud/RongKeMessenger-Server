<?php
/**
 *云视互动测试app：修改密码流程
 *需要传的参数为：
 *	ss:用户session(必填)
 *	oldpwd：旧密码(必填)
 *	newpwd: 新密码(必填)
 *
 *返回值：
 *	oper_result：对应的错误码
 *				0：成功
 *				1001:无效的session
 *				1005:密码错误
 *				9998:系统错误
 *				9999:参数错误
**/  
include(dirname(__FILE__) . "/common/inc.php");
$logger = Logger::getLogger(basename(__FILE__));

$session = isset($_POST['ss']) ? $_POST['ss'] : "";
$oldpwd = isset($_POST['oldpwd']) ? $_POST['oldpwd'] : "";
$newpwd = isset($_POST['newpwd']) ? $_POST['newpwd'] : "";

//参数格式校验
if(!CheckCommonFuns::checkSession($session) || !CheckCommonFuns::checkPwd($oldpwd) || !CheckCommonFuns::checkPwd($newpwd)){
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

// 旧密码不一致时返回
if(md5($oldpwd) != $sessionInfo['user_pwd']){
	$databaseManager->destoryConn();
	ErrCode::echoErr(ErrCode::API_ERR_PASSWD);
}

$result = $databaseManager->modifyPwd($sessionInfo['account'], $newpwd);
$databaseManager->destoryConn();
if($result){
	ErrCode::echoOk("SUCCESS");
}else{
	ErrCode::echoErr(ErrCode::SYSTEM_ERR);
}
?>