<?php
/**
 *云视互动测试app：操作组信息
 *需要传的参数为：
 *	ss:用户session(必填)
 *  type:操作类型(必填) 1.添加 2.修改 3.删除
 *  gid：组id
 *  gname:组名称
 *
 *返回值：
 *	oper_result：对应的错误码
 *				0：成功
 *				1001:无效的session
 *				1030：组名已存在
 *				9998:系统错误
 *				9999:参数错误
 *	result--oper_result=0&&type==1时有此项。内容为json串格式，里面包含内容如下：
 *				gid: 组ID
**/
include(dirname(__FILE__) . "/common/inc.php");
$logger = Logger::getLogger(basename(__FILE__));

$session = isset($_POST['ss']) ? trim($_POST['ss']) : "";
$type = isset($_POST['type']) ? trim($_POST['type']) : "";
$groupId = isset($_POST['gid']) ? trim($_POST['gid']) : "";
$groupName = isset($_POST['gname']) ? trim($_POST['gname']) : "";

//参数格式校验
if(!CheckCommonFuns::checkSession($session) || !in_array($type, array("1", "2", "3"))){
	ErrCode::echoErr(ErrCode::API_ERR_MISSED_PARAMATER);
}
if("1" == $type){
	if(0==strlen($groupName) || strlen($groupName)>50 || CheckCommonFuns::containSpecChars($groupName)){
		ErrCode::echoErr(ErrCode::API_ERR_MISSED_PARAMATER);
	}
	
}else if("2" == $type){
	if(0==strlen($groupId) || ("0"!=$groupId && !CheckCommonFuns::isDigits2($groupId))){
		ErrCode::echoErr(ErrCode::API_ERR_MISSED_PARAMATER);
	}
	if(0==strlen($groupName) || strlen($groupName)>50 || CheckCommonFuns::containSpecChars($groupName)){
		ErrCode::echoErr(ErrCode::API_ERR_MISSED_PARAMATER);
	}
	
}else if("3" == $type){
	if(0==strlen($groupId) || ("0"!=$groupId && !CheckCommonFuns::isDigits2($groupId))){
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

// 操作
if("1" == $type){
	$result = $databaseManager->addGroup($sessionInfo['account'], $groupName);
	$databaseManager->destoryConn();
	if($result > 0){
		ErrCode::echoOkArr("SUCCESS",array("result"=>json_encode(array('gid'=>$result))));
	}else if(-1 == $result){
		ErrCode::echoErr(ErrCode::API_GROPNAME_HASEXIST);
	}else{
		ErrCode::echoErr(ErrCode::SYSTEM_ERR);
	}
}else if("2" == $type){
	$result = $databaseManager->modifyGroup($sessionInfo['account'], $groupId, $groupName);
	$databaseManager->destoryConn();
	if($result > 0){
		ErrCode::echoOk("SUCCESS");
	}else if(-1 == $result){
		ErrCode::echoErr(ErrCode::API_GROPNAME_HASEXIST);
	}else{
		ErrCode::echoErr(ErrCode::SYSTEM_ERR);
	}
	
}else if("3" == $type){
	$result = $databaseManager->deleteGroup($sessionInfo['account'], $groupId);
	$databaseManager->destoryConn();
	if($result){
		ErrCode::echoOk("SUCCESS");
	}else{
		ErrCode::echoErr(ErrCode::SYSTEM_ERR);
	}
}
?>