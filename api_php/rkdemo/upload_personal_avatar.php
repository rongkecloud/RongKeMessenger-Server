<?php
/**
 *云视互动测试app：上传头像
 *需要传的参数为：
 *	ss:用户session(必填)
 *	file:上传的文件
 *
 *返回值：
 *	oper_result：对应的错误码
 *				0：成功
 *				1001:无效的session
 *				1010:图片上传失败
 *				9998:系统错误
 *				9999:参数错误
**/
include(dirname(__FILE__) . "/common/inc.php");
include(dirname(__FILE__) . "/common/Thumb.php");
$logger = Logger::getLogger(basename(__FILE__));

$session = isset($_POST['ss']) ? trim($_POST['ss']) : "";
//参数格式校验
if(!CheckCommonFuns::checkSession($session)){
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
	
if(isset($_FILES['file'])){
	$srcFile = $_FILES['file']['tmp_name'];	
	$thumbWidth = 100;
	$thumbHeight = 100;
	$thumb = new Thumb();
	$flag = $thumb->load(realpath($srcFile));		
	if(!$flag){
		ErrCode::echoErr(ErrCode::PARAM_ERR);
	}
	$imgWidth = $thumb->getWidth();
	$imgHeight = $thumb->getHeight();
	if($imgWidth >= $imgHeight){
		if($imgWidth > $thumbWidth){
			$thumb->resizeToWidth($thumbWidth);
		}else{
			$thumb->resizeToWidth($imgWidth);
		}
	}else{
		if($imgHeight > $thumbHeight){
			$thumb->resizeToHeight($thumbHeight);
		}else{
			$thumb->resizeToHeight($imgHeight);
		}
	}		
	$thumbFileName = $sessionInfo['account']."-thumb";
	$thumbFile = dirname(__FILE__) . "/temp/" . $thumbFileName;
	$savaFlag = $thumb->save($thumbFile);
	if(! $savaFlag){
		ErrCode::echoErr(ErrCode::FILE_SEND_ERR);
	}
	$result = $databaseManager->saveAvatar($sessionInfo['account'],$srcFile,$thumbFile);
	$databaseManager->destoryConn();
	if($result){
		ErrCode::echoOkArr("SUCCESS", array("result"=>json_encode($result)));
	}else{
		ErrCode::echoErr(ErrCode::SYSTEM_ERR);
	}
	
}else{
	ErrCode::echoErr(ErrCode::FILE_SEND_ERR);
}
?>