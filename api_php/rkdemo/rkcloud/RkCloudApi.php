<?php
class RkCloudApi{
	private $url;
	private $serverKey;
	/**
	 * 构造函数
	 */
	public function __construct(){
		$config = parse_ini_file(dirname(__FILE__) . "/RkCloud_Config.ini");
		$this->url = $config['rkcloudapi_url'];
		$this->serverKey = $config['rkcloudapi_serverkey'];
	}
	
	/**
	 * 添加一个用户
	 * return:
	 * 0：success
	 * 9999:参数错误
	 * 1007: key 认证失败
	 * 1002：username 已经存在
	 * 1008: 用户名和密码格式有误
	 */
	public function addUser($username,$secret){
		if(strlen($this->url)==0 || strlen($this->serverKey)==0){
			return false;
		}
		$url = $this->url."addUser.php";
		$params = array("key"=>$this->serverKey,"username"=>$username,"pwd"=>$secret);
		$result = $this->parseResponse($this->httpRequest($url, $params,"post"));
		return $result['ret_code']==0;
	}
	/**
	 * 删除一个用户
	 * return:
	 * 0：success
	 * 9999:参数错误
	 * 1007: key 认证失败
	 */
	public function delUser($username){
		if(strlen($this->url)==0 || strlen($this->serverKey)==0){
			return false;
		}
		$url = $this->url."delUser.php";
		$params = array("key"=>$this->serverKey,"username"=>$username);
		$result = $this->parseResponse($this->httpRequest($url, $params,"post"));
		return $result['ret_code'] == 0;
	}
	/**
	 * 发送自定义消息
	 * return :
	 * 0:success
	 * 9999:参数错误
	 * 1007: key 认证失败
	 */
	public function sendUserMsg($username,$dst,$msg){
		if(strlen($this->url)==0 || strlen($this->serverKey)==0){
			return false;
		}
		$url = $this->url."sendUserMsg.php";
		$params = array("key"=>$this->serverKey,"username"=>$username,"dst"=>$dst,"msg"=>$msg);
		$result = $this->parseResponse($this->httpRequest($url, $params,"post"));
		return $result['ret_code'] == 0;
	}
	
	/**
	 * 此函数执行一个模拟的HTTP请求，并返回HTTP请求的返回值
	 * @param String $url
	 * @param Array $params
	 * @param String $method
	 * @return Mixed
	 */
	private function httpRequest($url,$params = array(),$method = "get"){
		$ch = curl_init($url);
		if($params){
			$paramsArray = array();
			foreach ($params as $key=>$value){
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
		// 设置header头
		$header = array("content-type: application/x-www-form-urlencoded; charset=UTF-8");
		curl_setopt($ch, CURLOPT_HTTPHEADER, $header);
		curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
		curl_setopt($ch, CURLOPT_TIMEOUT, 15);
		$result = curl_exec($ch);
		$errMessage = curl_error($ch);
		$http_code = curl_getinfo($ch, CURLINFO_HTTP_CODE);
	
		curl_close($ch);
		if($errMessage != "" || $http_code >= 400){
			return false;
		} else {
			return $result;
		}
	}
	
	private function parseResponse($responseStr){
		$returnArr = array();
		$parseArr = explode("\r\n", $responseStr);
		for($i=0; $i<count($parseArr); $i++){
			$arr = explode("=", $parseArr[$i]);
			if(2 == count($arr)){
				$returnArr[trim($arr[0])] = trim($arr[1]);
			}
		}
		return $returnArr;
	}
}
?>