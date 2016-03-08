<?php
/****************************************
 *@package:common
 *@author:lion.wei
 *@verision:
 *@date:9:51 2008-9-9
 * $Id:
 ****************************************/

/**
 * 重定向浏览器到指定的 URL
 *
 * @param string $url 要重定向的 url
 */
function redirect($url)
{
	header("Location: {$url}");
}

/**
 * 通过javascript回退到上一界面*
 */
function historyBack()
{
	echo '
	   <script>
	   		window.history.back();
	   </script>';
}
/**
 * 转换 HTML 特殊字符，等同于 htmlspecialchars()
 * @param string $text
 * @return string
 */
function h($text)
{
	return htmlspecialchars($text);
}

/**
 * 转换 HTML 特殊字符以及空格和换行符
 *
 * 空格替换为 &nbsp; ，换行符替换为 <br />。
 *
 * @param string $text
 *
 * @return string
 */
function t($text)
{
	return nl2br(str_replace(' ', '&nbsp;', htmlspecialchars($text)));
}

/**
 * 通过 JavaScript 脚本显示提示对话框，并关闭窗口或者重定向浏览器
 *
 * 用法：
 * <code>
 * js_alert('Dialog message', '', $url);
 * // 或者
 * js_alert('Dialog message', 'window.close();');
 * </code>
 *
 * @param string $message 要显示的消息
 * @param string $after_action 显示消息后要执行的动作
 * @param string $url 重定向位置
 */
function js_alert($message = '', $after_action = '', $url = '')
{
	$out = "<script language=\"javascript\" type=\"text/javascript\">\n";
	if (!empty($message)) {
		$out .= "alert(\"";
		$out .= str_replace("\\\\n", "\\n", t2js(addslashes($message)));
		$out .= "\");\n";
	}
	if (!empty($after_action)) {
		$out .= $after_action . "\n";
	}
	if (!empty($url)) {
		$out .= "document.location.href=\"";
		$out .= $url;
		$out .= "\";\n";
	}
	$out .= "</script>";
	echo $out;
	exit;
}

/**
 * 将任意字符串转换为 JavaScript 字符串（不包括首尾的"）
 *
 * @param string $content
 *
 * @return string
 */
function t2js($content)
{
	return str_replace(array("\r", "\n"), array('', '\n'), addslashes($content));
}
/**
 * 入库时对特殊字符进行转义
 *
 * @param string $str
 *
 * @return string
 */
function a($str){
	return addslashes($str);
}
/**
 * safe_file_put_contents() 一次性完成打开文件，写入内容，关闭文件三项工作，并且确保写入时不会造成并发冲突
 *
 * @param string $filename
 * @param string $content
 * @param int $flag
 *
 * @return boolean
 */
function safe_file_put_contents($filename, & $content)
{
	$fp = fopen($filename, 'ab');
	if ($fp) {
		flock($fp, LOCK_EX);
		fwrite($fp, $content);
		flock($fp, LOCK_UN);
		fclose($fp);
		return true;
	} else {
		return false;
	}
}

/**
 * safe_file_get_contents() 用共享锁模式打开文件并读取内容，可以避免在并发写入造成的读取不完整问题
 *
 * @param string $filename
 *
 * @return mixed
 */
function safe_file_get_contents($filename)
{
	$fp = fopen($filename, 'rb');
	if ($fp) {
		flock($fp, LOCK_SH);
		clearstatcache();
		$filesize = filesize($filename);
		if ($filesize > 0) {
			$data = fread($fp, $filesize);
		} else {
			$data = false;
		}
		flock($fp, LOCK_UN);
		fclose($fp);
		return $data;
	} else {
		return false;
	}
}

/**
 * 输出变量的内容，通常用于调试
 *
 * @package Core
 *
 * @param mixed $vars 要输出的变量
 * @param string $label
 * @param boolean $return
 */
function dump($vars, $label = '', $return = false)
{
	if (ini_get('html_errors')) {
		$content = "<pre>\n";
		if ($label != '') {
			$content .= "<strong>{$label} :</strong>\n";
		}
		$content .= htmlspecialchars(print_r($vars, true));
		$content .= "\n</pre>\n";
	} else {
		$content = $label . " :\n" . print_r($vars, true);
	}
	if ($return) { return $content; }
	echo $content;
	return null;
}
function returnToArray($result){
	$temp_arr = array();
	$resArray = explode("\n",rawurldecode($result));
	for($i=0;$i<count($resArray);$i++){
		if($resArray[$i]!=""){
			if(strpos($resArray[$i],"=")!==false){
				$key = substr($resArray[$i],0,strpos($resArray[$i],"="));
				$value = substr($resArray[$i],strpos($resArray[$i],"=")+1);
				if($key!="" && $value!=""){
					$temp_arr[$key] = $value;
				}
			}
			
		}
	}
	return $temp_arr;
}
/**
 * 获取当前毫秒数（浮点数格式）
 *
 * @param mixed $time
 *
 * @return float
 */
function getMicrotime_backup($time = null)
{
	list($usec, $sec) = explode(' ', $time ? $time : microtime());
	return ((float)$usec + (float)$sec);
}

/**
 * 取得当前时间字符串
 *
 * @param mixed $time
 *
 * @return float
 */
function __writeTime(){
	$dayA = array(
		"1" =>LanguageTools::SLan("monday"),
		"2" =>LanguageTools::SLan("tuesday"),
		"3" =>LanguageTools::SLan("wednesday"),
		"4" =>LanguageTools::SLan("thursday"),
		"5" =>LanguageTools::SLan("friday"),
		"6" =>LanguageTools::SLan("saturday"),
		"7" =>LanguageTools::SLan("sunday")
	);
	$datetime = date("Y-m-d H:i N");
	$datetimeT = split(" ",$datetime);
	$date = split("-",$datetimeT[0]);
	$time = $datetimeT[1];
	$day = $datetimeT[2];
	echo $date[0] . LanguageTools::SLan("year") . $date[1] . LanguageTools::SLan("month") . $date[2] . LanguageTools::SLan("day") .  "    " . $dayA[$day] . "    " . "    " . $time ;
}

/**
 * 查询前对特殊字符进行转义
 *
 * @param String $str
 *
 * @return String
 */
function s($str){
	$str = str_replace("%","\%",$str);
	$str = str_replace("_","\_",$str);
	$str = str_replace("\\\\","\\\\\\\\",$str);
	return $str;
}

/**
 * @function csubstr 字符串截取
 * @param $str 要截取
 * @param $start 开始位置
 * @return $len 要截取的长度
 */
function showShort($str,$len){
	mb_internal_encoding("utf-8");
	$length = mb_strlen($str);

	$strArray = array();
	for($i = 0 ; $i < $length ;$i++){
		$strArray[] = mb_substr($str, $i, 1);
	}
	$tempstr = "";
	$legth = 0;
	for($i = 0 ; $i < count($strArray) ;$i++){
		if(ord($strArray[$i]) > 0xa0){
			$legth += 2;
		} else {
			$legth ++;
		}
		if($legth > $len){
			break;
		} else {
			$tempstr .= $strArray[$i];
		}
	}
	if($tempstr != $str){
		return $tempstr . "...";
	} else {
		return $str;
	}
}

function doPost($url,$params){
	$formElement = "";
	if($params){
		foreach($params as $key => $value){
			$formElement .= "<input type=\"hidden\" name=\"" . $key ."\" value=\"" . h($value) . "\">";
		}
	}
	echo <<<EOT
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
</head>
<form name="formT" action="{$url}" method="post" >
{$formElement}
</form>
</html>
<script>document.formT.submit();</script>
EOT;
}

function doGet($url,$params){
	if(!strpos($url,"?")){
		$url .= "?";
	}
	$paramsArray = array();
	foreach($params as $key => $value){
		array_push($paramsArray,$key . '=' . $value );
	}
	redirect($url ."&" . join($paramsArray,"&"));
}
/**
 * 此函数执行一个模拟的HTTP请求，并返回HTTP请求的返回值
 *
 * @param String $url
 * @param Array $params
 * @param String $method
 * @return Mixed
 */
function httpRequest($url,$params = array(),$method = "get"){
	$ch = curl_init($url);
	//curl_setopt($ch, CURLOPT_URL, $url);

	if($params){
		$paramsArray = array();
		foreach ($params as $key=>$value){
                        if($method == "get")
                                $paramsArray[] = $key . "=" . urlencode($value);
                        else
                                $paramsArray[] = $key . "=" . $value;

		}
		if($method){
			if(strtolower($method) == "post"){
				curl_setopt($ch, CURLOPT_POSTFIELDS, implode("&",$paramsArray));
			} else {
				curl_setopt($ch, CURLOPT_CUSTOMREQUEST, implode("&",$paramsArray));
			}
		}
	}
	curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
	curl_setopt($ch, CURLOPT_TIMEOUT, 15);	
	$result = curl_exec($ch);
	$errMessage = curl_error($ch);
	$http_code = curl_getinfo($ch, CURLINFO_HTTP_CODE);
 
	//echo "fdsfds:" . $http_code;
	curl_close($ch);
	if($errMessage != "" || $http_code >= 400){
	    return false;
	} else {
	    return $result;
	}
}

/*********************************
 * @function: getRandomID 随机数生成方法
 * @param int len 随机数长度
 * @param string  type 获取随机数类型 1：数字和字母组成  2：字母组成  3：纯数字组成
 * @return string
 **********************************/
function getRandomID($len, $type="1")
{
	$numArr = array( '0', '1', '2', '3', '4', '5', '6', '7', '8', '9');
	$upperArr = array('A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' );
	$lowerArr = array('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z' );
	$randStr = "";
	if($type == "1")
	$randArr = array_merge($numArr, $upperArr, $lowerArr);
	else if($type == "2")
	$randArr = array_merge($upperArr, $lowerArr);
	else if($type == "3")
	$randArr = $numArr;
	$cnt = count($randArr);
	for( $i = 0; $i < $len; $i++ )
	$randStr .= $randArr[mt_rand(0, $cnt-1)];
	return $randStr;
}

/*********************************
 * @function: getTimeZoneInterval 获取本地时间与格林威治时间相差的时间差
 * @param
 * @return string 如：+08:00
 **********************************/
function getTimeZoneInterval(){
	$zone = date("O");
	$res = substr($zone, 0, 3).":".substr($zone, 3, 2);
	return $res;
}

function getTimeZone(){
	return "GMT".getTimeZoneInterval()." ";
}

/**
 * @function showTimeSelect 生成半点时间下拉框
 * @param string selectName 下拉框名称
 * @param string selectId 下拉框ID
 * @param string defaultSelect 默认选中项，默认为空
 * @param int interval 每小时内的间隔分钟数，必须能被60整除，默认为30分钟

 * return
 **/
function showTimeSelect($selectName, $selectId, $defaultSelect="", $interval=30){
	$select = "<select";
	if($selectName)
	$select .= " name='$selectName'";
	if($selectId)
	$select .= " id='$selectId'";
	$select .= ">";
	for($i=0; $i<24; $i++){
		$index = $i < 10 ? "0".$i : $i;
		for($j=0; $j<60/$interval; $j++){
			$index_0 = $j*$interval;
			$index_0 = $index_0<10 ? "0".$index_0 : $index_0;
			$value = $index.":".$index_0;
			$selected = "";
			if($defaultSelect!="" && $defaultSelect==$value)
			$selected = "selected";
			$select .= "<option value='$value' $selected> $value </option>";
		}
	}

	$select .= '</select>';
	echo $select;
}
/**
 * Null值转换为空值
 */
function n2b($params){
	return isset($params)?$params:"";
}

function arrayAddSlash(&$array){
    foreach($array as &$line){
        $line = "'" . $line . "'";
    }
}

function getIP(){
	if(!empty($_SERVER["HTTP_CLIENT_IP"]))
		 $cip = $_SERVER["HTTP_CLIENT_IP"];
	else if(!empty($_SERVER["HTTP_X_FORWARDED_FOR"])) 
		$cip = $_SERVER["HTTP_X_FORWARDED_FOR"];
	else if(!empty($_SERVER["REMOTE_ADDR"]))
		$cip = $_SERVER["REMOTE_ADDR"];
	else 
		$cip = false;  
	return $cip;  
}  

function v($object){
	return var_export($object,true);
}

//将系统级错误以error级别记录在工程log中
function sysErrorHandler($errno, $errstr, $errfile, $errline){ 
	if(error_reporting()>0){
		$logger = Logger::getLogger(basename(__FILE__));
		$logger->error(" [$errno] $errstr  _File: $errfile  line: $errline ");
	}
}
$handler = set_error_handler("sysErrorHandler",E_ALL&~E_DEPRECATED);

?>
