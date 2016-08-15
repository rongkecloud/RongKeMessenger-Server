package com.rongketong.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CheckParameters {
	
	private HashMap<String,Boolean> checkParameterList = new HashMap<String,Boolean>();
	private Map<String,String[]> postParamerList = null;
	
	/**
	 * 
	 * @param postParameters	 post提交参数列表
	 * @param checkParameters	需要校验的参数列表
	 */
	public CheckParameters(Map<String,String[]> postParameters,HashMap<String,Boolean> checkParameters){
		checkParameterList = checkParameters;
		postParamerList = postParameters;
	};
	
	public boolean paramCheckAndRetRes(){
		/*
		 * 循环遍历所有的POST参数
		 * 1 有遗漏参数返回错误
		 * 2 有多余参数返回错误
		 * 3 参数值null返回错误
		 */	
		Set<String> checkParams=checkParameterList.keySet();
		for(String param:checkParams){
			//非必填参数，不进行参数校验
			if (checkParameterList.get(param)==false){
				continue;
			}else{
				//判断提交的参数是否存在
				if(postParamerList.containsKey(param)){
					String pl = postParamerList.get(param)[0].toString();
					if(pl==null || pl.equals("")){
						return false;
					}				
				}else{
					return false;
				}						 
			}			
		}
		return true;	

	}
	
	public HashMap<String,String> postParamsToHashMap(Map<String,String[]> postParamsMap){
		HashMap<String,String> paramsMap = new HashMap<String,String>();		
		Set<String> paramKeys =postParamsMap.keySet();		
		for(String key:paramKeys){
			String[] values = postParamsMap.get(key);
			String valueStr="";
			for(String value:values){
				valueStr+=value;
			}
			paramsMap.put(key, valueStr);
		}
		return paramsMap;
	}
	
	public HashMap<String,String> postParamsToHashMap(){
		HashMap<String,String> paramsMap = new HashMap<String,String>();		
		Set<String> paramKeys =postParamerList.keySet();		
		for(String key:paramKeys){
			String[] values = postParamerList.get(key);
			String valueStr="";
			for(String value:values){
				valueStr+=value;
			}
			paramsMap.put(key, valueStr);
		}
		return paramsMap;
	}

}
