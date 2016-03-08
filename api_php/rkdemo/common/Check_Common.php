<?php
/****************************************
 *@package:bgcommon
 *@function: 常规格式校验类
 *@author:jessica.yang
 *@verision:
 *@date:2012-04-14
 * $Id: 
 ****************************************/
class CheckCommonFuns{
	/**
	 *@function 判断是否包含全角字符
	 *@param: string str 校验的字符串
	 *@boolean:
	 **/
	static function containWords($str){
		$exp = "/[^\x00-\xff]/";
		if(preg_match($exp, $str)){
			return true;
		}
		return false;		
	}
		
	/**
	 *@function 判断是否包含特殊字符
	 *@param: string str 校验的字符串
	 *@boolean:
	 **/
	static function containSpecChars($str){
		$exp = "/[\|<>&%$\\\"\']/";
		if(preg_match($exp, $str)){
			return true;
		}
		return false;		
	}	
	
	/**
	 *@function 判断用户名是否符合要求
	 *@param: string str 校验的字符串
	 *@boolean:
	 **/
	static function checkAccount($str){
		if(!$str){
			return false;
		}
		$exp = "/^[0-9a-zA-Z]*$/";
		if(preg_match($exp, $str) && 6 <= strlen($str) && 20 >= strlen($str)){
			return true;
		}
		return false;
	}
	
	/**
	 *@function 判断Session是否符合要求
	 *@param: string str 校验的字符串
	 *@boolean:
	 **/
	static function checkSession($str){
		if(!$str){
			return false;
		}
		$exp = "/^[0-9a-zA-Z]*$/";
		if(preg_match($exp, $str) && strlen($str)>0 && strlen($str)<=100){
			return true;
		}
		return false;
	}
	
	/**
	 *@function 判断密码是否符合要求
	 *@param: string str 校验的字符串
	 *@boolean:
	 **/
	static function checkPwd($str){
		if(!$str){
			return false;
		}
		$exp = "/^[0-9a-zA-Z]*$/";
		if(preg_match($exp, $str) && 6 <= strlen($str) && 20 >= strlen($str)){
			return true;
		}
		return false;
	}
	
	/**
	 *@function 判断用户类型是否符合要求
	 *@param: string str 校验的字符串
	 *@boolean:
	 **/
	static function checkUserType($str){
		if(!$str){
			return false;
		}
		$exp = "/^\d*$/";
		if(preg_match($exp, $str) && ($str == "1" || $str == "2")){
			return true;
		}
		return false;
	}
	
	/**
	 *@function 判断姓名是否符合要求
	 *@param: string str 校验的字符串
	 *@boolean:
	 **/
	static function checkName($str){
		if(!$str){
			return false;
		}
		if(strlen($str)<=0 || strlen($str)>30 || CheckCommonFuns::containSpecChars($str)){
			return false;
		}
		return true;
	}
	
	/**
	 *@function 判断姓名是否符合要求
	 *@param: string str 校验的字符串
	 *@boolean:
	 **/
	static function checkSex($str){
		if(!$str){
			return false;
		}
		if($str == "1" || $str == "2"){
			return true;
		}
		return false;
	}
	
	/**
	 *@function 判断加好友权限是否符合要求
	 *@param: string str 校验的字符串
	 *@boolean:
	 **/
	static function checkPermission($str){
		if(!$str){
			return false;
		}
		if($str == "1" || $str == "2"){
			return true;
		}
		return false;
	}

	/**
	 *@function 判断是否是Email
	 *@param: string str 校验的字符串
	 *@boolean: true: is email  false: not email 
	**/
	static function isEmail($str){
		if(!$str){
			return false;
		}
		$exp = "/^(([0-9a-zA-Z]+)|([0-9a-zA-Z]+[_.0-9a-zA-Z-]*[0-9a-zA-Z]+))@([a-zA-Z0-9-]+[.])+([a-zA-Z]{2}|net|NET|com|COM|gov|GOV|mil|MIL|org|ORG|edu|EDU|int|INT)$/";
		if(!preg_match($exp, $str)){
			return false;		
		}	
		return true;	
	}	
		
	/**
	 *@function 判断是否是Url
	 *@param: string str 校验的字符串
	 *@boolean: true: is url  false: not url 
	 **/
	static function isUrl($str){
		if(!$str){
			return false;
		}
		$patrn = "^((https|http|ftp|rtsp|mms)?://)"; 
		$patrn .= "?(([0-9a-z_!~*'().&=+$%-]+: )?[0-9a-z_!~*'().&=+$%-]+@)?";//ftp的user@ 
		$patrn .= "(([0-9]{1,3}\.){3}[0-9]{1,3}"; // IP形式的URL- 199.194.52.184
		$patrn .= "|"; // 允许IP和DOMAIN（域名）
		$patrn .= "([0-9a-z_!~*'()-]+\.)*"; // 域名- www. 
		$patrn .= "([0-9a-z][0-9a-z-]{0,61})?[0-9a-z]\."; // 二级域名 
		$patrn .= "[a-z]{2,6})"; // first level domain- .com or .museum 
		$patrn .= "(:[0-9]{1,4})?"; // 端口- :80 
		$patrn .= "((/?)|"; // a slash isn't required if there is no file name 
		$patrn .= "(/[0-9a-z_!~*'().;?:@&=+$,%#-]+)+/?)$"; 
		
        if(!eregi($patrn, $str)){
			return false;		
		}	
		return true;	
	}
	
	/**
	 *@function 判断是否是纯数字
	 *@param: string str 校验的字符串
	 *@boolean: 
	**/	
	static function isDigits($str){
		if(!$str){
			return false;
		}
		$exp = "/^\d*$/";
		if(!preg_match($exp, $str)){
			return false;		
		}	
		return true;	
	}

	/**
	 *@function 判断是否是纯数字，并且不以0开头
	 *@param: string str 校验的字符串
	 *@boolean:
	 **/
	static function isDigits2($str){
		if(!$str){
			return false;
		}
		$exp = "/^[1-9]\d*$/";
		if(!preg_match($exp, $str)){
			return false;
		}
		return true;
	}
			
	/**
	 *@function 判断是否是mobile号码
	 *@param: string str 校验的字符串
	 *@boolean:
	 **/
	static function isMobile($str){
		if(!$str){
			return false;
		}
		$exp = "/^1[35789]\d{9}$/";
		if(!preg_match($exp, $str)){
			return false;
		}
		return true;
	}
	
	/**
	 *@function 判断是否是电话号码
	 *@param: string str 校验的字符串
	 *@boolean:
	 **/
	static function isTelephone($str){
		if(!$str){
			return false;
		}
		$exp = "/^(0[1-9]\d{1,2}-)?[1-9]\d{6,7}$/";
		if(!preg_match($exp, $str)){
			return false;
		}
		return true;
	}
	
	/**
	 *@function 判断问题反馈类型是否正确
	 *@param: string str 校验的字符串
	 *@boolean:
	 **/
	static function isFeedbackType($str){
		if(!$str){
			return false;
		}
		if($str == "1" || $str == "2" || $str == "3"){
			return true;
		}
		return false;
	}
		
	/**
	 *@function 判断是否是数字和字母组成
	 *@param: string str 校验的字符串
	 *@boolean:
	 **/
	static function makeDigitAndAlpha($str){
		if(!$str){
			return false;
		}
		$exp = "/^[0-9a-zA-Z]*$/";
		if(!preg_match($exp, $str)){
			return false;
		}
		return true;
	}	
	
}
?>