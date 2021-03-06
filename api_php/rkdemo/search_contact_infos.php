<?php
/**
 *云视互动测试app：搜索用户信息
 *需要传的参数为：
 *	ss:用户session(必填)
 *  filter:搜索条件(必填)
 *
 *返回值：
 *	oper_result：对应的错误码
 *				0：成功
 *				1001:无效的session
 *				9998:系统错误
 *				9999:参数错误
 *	result--oper_result=0时有此项。内容为json串数组格式，每条信息里面包含内容如下：
 *				account: 账户名称
**/
include(dirname(__FILE__) . "/common/inc.php");
$logger = Logger::getLogger(basename(__FILE__));

$session = isset($_POST['ss']) ? trim($_POST['ss']) : "";
$filter = isset($_POST['filter']) ? trim($_POST['filter']) : "";

//参数格式校验
if(!CheckCommonFuns::checkSession($session) || (0==strlen($filter) || !CheckCommonFuns::makeDigitAndAlpha($filter))){
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

// 获取数据
$resultArr = $databaseManager->search_contact_infos($sessionInfo['account'], $filter);
$databaseManager->destoryConn();
if(is_array($resultArr)){
	ErrCode::echoOkArr("SUCCESS",array("result"=>json_encode($resultArr)));
}else{
	ErrCode::echoErr(ErrCode::SYSTEM_ERR);
}

?>