<?php
/**
 *云视互动测试app：获取头像
 *需要传的参数为：
 *	ss:用户session(必填)
 *  type:头像类型(必填) 1、缩略图，2、大图
 *  account：需要获取头像的用户账号(必填)
 *
 *返回值：
 *	oper_result：对应的错误码
 *				0：成功
 *				1001:无效的session
 *				1011:图片下载失败
 *				9998:系统错误
 *				9999:参数错误
**/
 
header("content-type:text/html; charset=utf-8");
include(dirname(__FILE__) . "/common/inc.php");
$logger = Logger::getLogger(basename(__FILE__));

$session = isset($_POST['ss']) ? trim($_POST['ss']) : "";
$type = isset($_POST['type']) ? trim($_POST['type']) : "";
$account = isset($_POST['account']) ? trim($_POST['account']) : "";

//参数格式校验
if(!CheckCommonFuns::checkSession($session) || !in_array($type, array("1", "2")) || !CheckCommonFuns::checkAccount($account)){
	ErrCode::echoErr(ErrCode::API_ERR_MISSED_PARAMATER);
}

// 数据库连接
$databaseManager = new DatabaseManager();
$database = $databaseManager->getConn();
if(!$database){
	$logger->error(sprintf("Database connect fail."));
	ErrCode::echoErr(ErrCode::SYSTEM_ERR);
}

// 检查session
$sessionCheck = $databaseManager->checkSession($session);
if(!$sessionCheck){
	$databaseManager->destoryConn();
    ErrCode::echoErr(ErrCode::API_ERR_INVALID_SESSION);
}
	
$fileObj = $databaseManager->get_avatar($account,$type);
$databaseManager->destoryConn();

if(!$fileObj || strlen($fileObj) == 0){
	ErrCode::echoErr(ErrCode::FILE_DOWN_ERR);
}
Header("Content-type: application/octet-stream");
Header("Accept-Ranges: bytes");
Header("Accept-Length: ".strlen($fileObj));
Header("Content-Disposition: attachment; filename=" . rand(123456789,999999999));
echo $fileObj;
?>