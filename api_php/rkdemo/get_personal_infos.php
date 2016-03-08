<?php
/**
 *云视互动测试app：获取用户信息
 *需要传的参数为：
 *	ss:用户session(必填)
 *  accounts:选定的账号，多个之间用半角逗号分隔
 *
 *返回值：
 *	oper_result：对应的错误码
 *				0：成功
 *				1001:无效的session
 *				9998:系统错误
 *				9999:参数错误
 *	result--oper_result=0时有此项。内容为json串数组格式，每条信息里面包含内容如下：
 *				account: 账户名称
 *				name: 用户姓名
 *				address: 地址	
 *				type: 用户类型
 *				sex: 性别
 *				mobile: 手机号码
 *				email: 邮箱
 *				info_version: 信息版本号
 *				avatar_version: 头像版本号	
**/ 
include(dirname(__FILE__) . "/common/inc.php");
$logger = Logger::getLogger(basename(__FILE__));

$session = isset($_POST['ss']) ? trim($_POST['ss']) : "";
$accounts = isset($_POST['accounts']) ? trim($_POST['accounts']) : "";

//参数格式校验	
if(!CheckCommonFuns::checkSession($session)){
	ErrCode::echoErr(ErrCode::API_ERR_MISSED_PARAMATER);
}
if(0==strlen($accounts)){
	ErrCode::echoErr(ErrCode::API_ERR_MISSED_PARAMATER);
}

$arrAccounts = explode(",", $accounts);
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

// 获取数据
$result = $databaseManager->get_personal_infos($sessionInfo['account'], $accounts);
$databaseManager->destoryConn();

if(is_array($result)){//查询成功
	ErrCode::echoOkArr("SUCCESS",array("result"=>json_encode($result)));
}else{//查询失败
	ErrCode::echoErr(ErrCode::SYSTEM_ERR);
}
?>