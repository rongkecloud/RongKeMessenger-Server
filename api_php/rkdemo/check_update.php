<?php
/**
 *云视互动测试app：检查更新
 *需要传的参数为：
 *  os: OS类型，如android, iphone(必填)
 *  cv: 客户端软件版本号(必填)
 *
 *返回值：
 *	oper_result：对应的错误码
 *				0：成功
 *				1001:无效的session
 *				1006:无需更新
 *				9998:系统错误
 *				9999:参数错误
**/
include(dirname(__FILE__) . "/common/inc.php");

$os_type = isset($_POST['os']) ? $_POST['os'] : "";
$client_version = isset($_POST['cv']) ? $_POST['cv'] : "";


if(!in_array($os_type, array("android", "iphone"))){
	ErrCode::echoErr(ErrCode::API_ERR_MISSED_PARAMATER);
}
$databaseManager = new DatabaseManager();
$ipphone_db_link = $databaseManager->getConn();
//数据库链接失败
if(!$ipphone_db_link){
	ErrCode::echoErr(ErrCode::SYSTEM_ERR);
}
$result = $databaseManager->checkUpdate($os_type);
$databaseManager->destoryConn();
if($result){
	if(version_compare($result['update_version'],$client_version) > 0){
		ErrCode::echoOkArr("SUCCESS", $result);
	}else{
		ErrCode::echoErr(ErrCode::API_ERR_NO_UPDATE);
	}
}else{
	ErrCode::echoErr(ErrCode::SYSTEM_ERR);
}
?>