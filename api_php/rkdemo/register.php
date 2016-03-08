<?php
/**
 *云视互动测试app：注册流程
 *需要传的参数为：
 *	account:用户名(必填)
 *	pwd：密码(必填)
 *	type: 用户类型(必填)
 *
 *返回值：
 *	oper_result：对应的错误码
 *				0：成功
 *				1004:账号已存在
 *				9998:系统错误
 *				9999:参数错误
**/  
include(dirname(__FILE__) . "/common/inc.php");
include(dirname(__FILE__) . "/rkcloud/RkCloudApi.php");
$logger = Logger::getLogger(basename(__FILE__));

$account = isset($_POST['account']) ? trim($_POST['account']) : "";
$pwd = isset($_POST['pwd']) ? trim($_POST['pwd']) : "";
$type = isset($_POST['type']) ? trim($_POST['type']) : "";

//参数格式校验
if(!CheckCommonFuns::checkAccount($account) || !CheckCommonFuns::checkPwd($pwd) || !CheckCommonFuns::checkUserType($type)){
	ErrCode::echoErr(ErrCode::API_ERR_MISSED_PARAMATER);
}

//数据库链接
$databaseManager = new DatabaseManager();
$database = $databaseManager->getConn();	
if(!$database){
	$logger->error(sprintf("Database connect fail."));
	ErrCode::echoErr(ErrCode::SYSTEM_ERR);
}
	
//判断用户名是否已存在
$isAccountExist = $databaseManager->isAccountExist($account);
if($isAccountExist){
	$databaseManager->destoryConn();
	ErrCode::echoErr(ErrCode::API_ERR_ACCOUNT_EXIST);
}

//生成云视互动密码----addUser到云视互动中
$sdkPwd = getRandomID(8);
//调用注册逻辑方法
$register = $databaseManager->register($account,$pwd,$type,$sdkPwd);
if(!$register){
	$databaseManager->destoryConn();
	ErrCode::echoErr(ErrCode::SYSTEM_ERR);
	return;
}

/*初始化开发者服务器秘钥*/
// 向云视互动中添加账号
$obj = new RkCloudApi();
$res = $obj->addUser($account, $sdkPwd);
if($res){
	$databaseManager->destoryConn();
	ErrCode::echoOk($register);
}else{
	$del = $databaseManager->deleteAccount($account);
	$databaseManager->destoryConn();
	ErrCode::echoErr(ErrCode::SYSTEM_ERR);
}
?>
