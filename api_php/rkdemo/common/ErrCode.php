<?php
/**
* 错误代码定义
*/ 
class ErrCode{	

	const SYSTEM_ERR                      = "9998";   //系统错误 
	const API_ERR_MISSED_PARAMATER        = "9999";   //参数错误 
	const API_ERR_BANNED_USER			  = "9997";   //被禁用户
	
	const API_ERR_INVALID_SESSION         = "1001";   //无效的session	
	const API_ERR_ACCOUNT_OR_PASSWD       = "1002";	  //用户名或密码错误
	const API_ERR_ACCOUNT_NOT_EXISTS      = "1003";   //账号不存在 
	const API_ERR_ACCOUNT_EXIST 		  = "1004";   //账号已存在
	const API_ERR_PASSWD			      = "1005";	  //密码错误	
	const API_ERR_NO_UPDATE               = "1006";   //没有发现更新
	
	const FILE_SEND_ERR					  = "1010";   //图片上传失败
	const FILE_DOWN_ERR					  = "1011";   //图片下载失败
	
	const API_ERR_FRIEND_EXIST		  	  = "1020";   //好友已经存在
	const API_ADDFRIEND_NEEDVERIFY		  = "1021";   //添加好友需要验证信息
	const API_ADDFRIEND_WAITVERIFY		  = "1022";   //添加好友等待对端验证
	
	const API_GROPNAME_HASEXIST		  	  = "1030";   //组名已存在
		
	public static function echoErr($errCode){
		echo "oper_result=" . $errCode . "\n";
		exit;
	}
	public static function echoOk($result){
		echo "oper_result=0";
		exit;
	}
    public static function echoOkArr($result,$resultArr){
        echo "oper_result=0\n";
        if(gettype($resultArr) == "array"){
            foreach($resultArr as $key=>$value){
                echo "$key=" . $value . "\n";
            }
        } else {
            echo $resultArr;
        }
		exit;
	}
    public static function echoErrArr($errCode,$resultArr){
		echo "oper_result=" . $errCode . "\n";
		if(gettype($resultArr) == "array"){
            foreach($resultArr as $key=>$value){
                echo "$key=" . $value . "\n";
            }
        } else {
            echo $resultArr;
        }
		exit;
	}
}
?>
