<?php
	define("COMMON_REAL_PATH",dirname(__FILE__));
	include(COMMON_REAL_PATH . "/DatabaseManager.php");
	include(COMMON_REAL_PATH . "/Config.php");
	include(COMMON_REAL_PATH . "/Filter.php");	
	include(COMMON_REAL_PATH . "/ErrCode.php");
	include(COMMON_REAL_PATH . "/log4php/Logger.php");		
	include(COMMON_REAL_PATH . "/Check_Common.php");
	Logger::configure(dirname(__FILE__).'/../config/log4php.properties');
?>