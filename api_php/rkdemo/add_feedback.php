<?php
/**
 *云视互动测试app：问题反馈
 *需要传的参数为：
 *	ss:用户session(必填)
 *  type:类型(必填) 1：软件问题 2：界面问题 3：其他问题
 *  content：内容(必填)
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
$type = isset($_POST['type']) ? trim($_POST['type']) : "";
$content = isset($_POST['content']) ? trim($_POST['content']) : "";

//校验参数格式
if(!CheckCommonFuns::checkSession($session) || !CheckCommonFuns::isFeedbackType($_POST['type']) || (0==strlen($content) || strlen($content)>500 || CheckCommonFuns::containSpecChars($content))){
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

// 添加
$result = $databaseManager->addFeedback($sessionInfo['account'], $type, $content);
$databaseManager->destoryConn();
if($result){
	ErrCode::echoOk("SUCCESS");
}else{
	ErrCode::echoErr(ErrCode::SYSTEM_ERR);
}
?>