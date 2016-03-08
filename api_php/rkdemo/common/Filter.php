<?php

class Filter{
	
	/**
	 * 
	 * 校验提交的数据是否参数完整，如果完整，返回结果数组
	 * @param mix $_POST
	 * @param Array $pamList
	 */
	public static function paramCheckAndRetRes($post,$pamList){
		$result = array();
		$checkFlag = true;
		foreach($pamList as $param){
		    $paramT = "";
		    $blankCheck = false;
		    if(gettype($param) == "array"){
		        $paramT = $param[0];
		        $blankCheck = $param[1];
		    } else {
		        $paramT = $param;
		    }
		    if($blankCheck){
				if(isset($post[$paramT])){			    
					$result[$paramT] = trim($post[$paramT]);
				} else {
					$checkFlag = false;
					break;
				}
		    }else{
		    	if(isset($post[$paramT])){			    
					$result[$paramT] = trim($post[$paramT]);
				}
		    }
			if($blankCheck && trim($result[$paramT]) == ""){
			    $checkFlag = false;
			    break;
			}
		}
		if($checkFlag){
			return $result;
		} else {
			return false;
		}      
	}
	/**
	 * 
	 * 校验参数，返回结果数组
	 * @param mix $_POST
	 * @param Array $pamList
	 */
	public static function paramCheck($post,$pamList){
		$result = array();
		foreach($pamList as $param){
			if(isset($post[$param])){
				$result[$param]= $post[$param];
			} else {
				$result[$param]= null;
			}
		}
		return $result;
	}
}
?>