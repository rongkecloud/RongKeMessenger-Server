<?php
/**
 *云视互动测试app：修改个人信息流程
 *需要传的参数为：
 *	ss:用户session(必填)
 *	key：修改的信息标识(必填)，包含的内容如下：
 *			name:姓名
 *			sex:性别
 *			address:地址
 *			mobile:手机号码
 *			email:邮箱
 *			permission:加好友权限
 *	cotent: 修改的信息内容(必填)
 *
 *返回值：
 *	oper_result：对应的错误码
 *				0：成功
 *				1001:无效的session
 *				9998:系统错误
 *				9999:参数错误
 *	result--oper_result=0时有此项。内容为json串格式，里面包含的信息如下：
 *				info_version:个人信息版本号				
**/  
include(dirname(__FILE__) . "/common/inc.php");
$logger = Logger::getLogger(basename(__FILE__));

$session = isset($_POST['ss']) ? trim($_POST['ss']) : "";
$param_key = isset($_POST['key']) ? trim($_POST['key']) : "";
$content = isset($_POST['content']) ? trim($_POST['content']) : "";

//参数格式校验
if(!CheckCommonFuns::checkSession($session) || !$param_key){
	ErrCode::echoErr(ErrCode::API_ERR_MISSED_PARAMATER);
}

$key = "";
$value = "";
if("name" == $param_key){
	$key = "name";
	if(!CheckCommonFuns::checkName($content)){
		ErrCode::echoErr(ErrCode::API_ERR_MISSED_PARAMATER);
	}
}else if("sex" == $param_key){
	$key = "sex";
	if(!CheckCommonFuns::checkSex($content)){
		ErrCode::echoErr(ErrCode::API_ERR_MISSED_PARAMATER);
	}
}else if("address" == $param_key){
	$key = "address";
	if(CheckCommonFuns::containSpecChars($content)){
		ErrCode::echoErr(ErrCode::API_ERR_MISSED_PARAMATER);
	}
}else if("mobile" == $param_key){
	$key = "mobile";
	if(!CheckCommonFuns::isMobile($content)){
		ErrCode::echoErr(ErrCode::API_ERR_MISSED_PARAMATER);
	}
}else if("email" == $param_key){
	$key = "email";
	if(!CheckCommonFuns::isEmail($content)){
		ErrCode::echoErr(ErrCode::API_ERR_MISSED_PARAMATER);
	}
}else if("permission" == $param_key){
	$key = "permission_validation";
	if(!CheckCommonFuns::checkPermission($content)){
		ErrCode::echoErr(ErrCode::API_ERR_MISSED_PARAMATER);
	}
}

if(!$key){
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

// 修改操作
$result = $databaseManager->operation_personal_info($sessionInfo['account'],$key,$content);
$databaseManager->destoryConn();
if(is_array($result)){
	ErrCode::echoOkArr("SUCCESS",array("result"=>json_encode($result)));
}else{
	ErrCode::echoErr(ErrCode::SYSTEM_ERR);
}
?>