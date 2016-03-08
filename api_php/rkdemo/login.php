<?php
/**
 *云视互动测试app：登录流程
 *需要传的参数为：
 *	account:用户名(必填)
 *	pwd：密码(必填)
 *
 *返回值：
 *	oper_result：对应的错误码
 *				0：成功
 *				1002:用户名密码错误
 *				9998:系统错误
 *				9999:参数错误
 *	result--oper_result=0时有此项。内容为json串格式，里面包含的信息如下：
 *				ss:session
 *				sdk_pwd:云视互动账号对应的密码
 *				name:姓名
 *				address:地址
 *				type:用户类型  1：普通用户  2：企业用户
 *				email:邮箱
 *				mobile:手机号码
 *				sex:性别  0：无 1：男  2：女
 *				permission:是否需要验证 1：需要验证 2：不需要验证
 *				info_version:个人信息版本号
 *				avatar_version:用户头像版本号
**/ 
include(dirname(__FILE__) . "/common/inc.php");
$logger = Logger::getLogger(basename(__FILE__));

$account = isset($_POST['account']) ? trim($_POST['account']) : "";
$pwd = isset($_POST['pwd']) ? trim($_POST['pwd']) : "";

//参数格式校验
if(!CheckCommonFuns::checkAccount($account) || !CheckCommonFuns::checkPwd($pwd)){
	ErrCode::echoErr(ErrCode::API_ERR_MISSED_PARAMATER);
}

//数据库链接
$databaseManager = new DatabaseManager();
$database = $databaseManager->getConn();
if(!$database){
	$logger->error(sprintf("Database connect fail."));
	ErrCode::echoErr(ErrCode::SYSTEM_ERR);
}

//登录
$login = $databaseManager->login($account,$pwd);
$databaseManager->destoryConn();
if(is_array($login)){
	ErrCode::echoOkArr("SUCCESS", array("result"=>json_encode($login)));
}else if(-1==$login || -2==$login){
	ErrCode::echoErr(ErrCode::API_ERR_ACCOUNT_OR_PASSWD);
}else{
	ErrCode::echoErr(ErrCode::SYSTEM_ERR);
}	
?>