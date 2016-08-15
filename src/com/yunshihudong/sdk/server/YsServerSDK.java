package com.yunshihudong.sdk.server;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.sf.json.JSONObject;

import com.rongketong.dispatcher.Config;

public class YsServerSDK {

	public final static int ADD_SUCCESS = 1;
	public final static int ADD_FAILED = 0;
	
	private static String AddUserAPI = "addUser.do";
	private static String SendUserMsgAPI = "sendUserMsg.do";
	private static String BatchAddUserAPI = "batchAddUser.do";
	
	private static String sdk_key = "";
	private static String YS_URL = "http://api.rongkecloud.com:8080/3.0/";
	/**
	 * init url and server key
	 * @param key
	 * @param YSURL
	 */
	public YsServerSDK(String key){
		sdk_key = key;
	}

	/**
	 * Add user to YunShiHudong system
	 * @param uname
	 * @param pwd
	 * @return
	 * @throws IOException
	 */
	public int addUser(String uname,String pwd) throws IOException  {
		int ret = -1;
		String wholeURL = YS_URL+AddUserAPI;
		HashMap<Object,Object> params = new HashMap<Object,Object>();
		params.put("key", sdk_key);
		params.put("username", uname);
		params.put("pwd", pwd);
		String result = null;
		try {
			result = doPost(wholeURL,params);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println(e);
		}
		if(null == result){
			return ret;
		}
		JSONObject json= JSONObject.fromObject(result);
		ret = json.getInt("errcode");
		return ret;
	}
	
	public HashMap<String,String> BatchAddUser(String user_info_json)  throws IOException {
		String wholeURL = YS_URL+BatchAddUserAPI;
		HashMap<String,String> params = new HashMap<String,String>();
		params.put("key", sdk_key);
		params.put("userinfo", user_info_json);
		HashMap<String,String> result = httpRequest(wholeURL,params,"POST");
		
		return result;
	}
	/**
	 * Send user message to other device or app client
	 * @param srcUname
	 * @param dstUname
	 * @param content
	 * @return
	 * @throws IOException
	 */
	@SuppressWarnings("deprecation")
	public int SendMessage(String srcUname,String dstUname,String content) throws IOException  {
		int ret = -1;
		String URL_PATH = YS_URL+SendUserMsgAPI;
		HashMap<Object,Object> params = new HashMap<Object,Object>();
        params.put("key", sdk_key);
        params.put("username", srcUname);
        params.put("dest", dstUname);
        params.put("text", URLEncoder.encode(content));
        String returnCode = null;
        try {
			returnCode = doPost(URL_PATH, params);
			if(null == returnCode){
				return ret;
			}
			/*String [] ValueArray = returnCode.split("=");
			ret = Integer.parseInt(ValueArray[1]);
			return ret;*/
			JSONObject json= JSONObject.fromObject(returnCode);
			ret = json.getInt("errcode");
			return ret;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return ret;
		}
	}
	
	
    /**
	 * 模拟doPost请求
	 * @param urlStr
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
    public static String  doPost(String urlStr,HashMap<Object,Object> paramMap) throws Exception{
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        conn.setRequestMethod("POST");
        String paramStr = null;
        if(paramMap!=null){
        	paramStr = prepareParam(paramMap);
        }
        conn.setRequestProperty("content-type", "application/x-www-form-urlencoded;charset=UTF-8");
        conn.setDoInput(true);
        conn.setDoOutput(true);
        OutputStream os = conn.getOutputStream();
        if(paramMap!=null){
        	os.write(paramStr.toString().getBytes("utf-8"));
        }
        os.close(); 
        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line ;
        String result ="";
        while( (line =br.readLine()) != null ){
            result += line;
        }
        br.close();
        return result;
    }
    
    /**
     * 参数解析
     * @param url
     * @param charset
     * @return
     */
    private static String prepareParam(Map<Object,Object> paramMap){
        StringBuffer sb = new StringBuffer();
        if(paramMap.isEmpty()){
            return "" ;
        }else{
            for(Object key: paramMap.keySet()){
                String value = (String)paramMap.get(key);
                if(sb.length()<1){
                    sb.append(key).append("=").append(value);
                }else{
                    sb.append("&").append(key).append("=").append(value);
                }
            }
            return sb.toString();
        }
    }
    
	/**
	 * http 请求，
	 * @param InURL
	 * @param params  "key":"value"
	 * @param InMethod  方法 “GET”,"POST"
	 * @return
	 * @throws IOException 
	 */
	public static HashMap<String,String> httpRequest(String InURL,HashMap<String,String> params,String InMethod) throws IOException{
		if(InURL==null || InMethod==null || InURL.isEmpty() || InMethod.isEmpty())
			return null;
		StringBuffer content = new StringBuffer(1024);
		// get params
		if(null != params){
			Iterator<String> it=params.keySet().iterator();
			int i = 0;
			while(it.hasNext()){
			    String key;  
			    String value;  
			    key=it.next().toString();  
			    value=params.get(key);
			    if(i==0)
					content.append(key+"=").append(value);
			    else
			    	content.append("&"+key+"=").append(value);
			    i++;
			}
		}
		if(InMethod.equals("GET") && !content.toString().isEmpty()){
			InURL = InURL+"?"+content.toString();
		}
		URL postUrl = new URL(InURL); 
		HttpURLConnection connection = (HttpURLConnection) postUrl.openConnection(); 
		connection.setDoOutput(true);                 
		connection.setDoInput(true); 
		connection.setRequestMethod(InMethod); 
		connection.setRequestProperty("Charset", "UTF-8");
		connection.setRequestProperty("Connection", "Keep-Alive");
		connection.setUseCaches(false); 
		connection.setInstanceFollowRedirects(true); 
		connection.setRequestProperty("content-type", "application/x-www-form-urlencoded;charset=UTF-8");
		
		//开始实际连接
		connection.connect();
		if(InMethod.equals("POST")){
			//发送请求参数
			DataOutputStream out = new DataOutputStream(connection.getOutputStream()); 
			out.writeBytes(content.toString()); 
			out.flush(); 
			out.close(); 
		}
		
		HashMap<String,String> result = new HashMap<String,String>();
		if(connection.getResponseCode() == 200){
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream())); 
			String line; 
			
			while((line=reader.readLine()) != null){
					String[] resultArray = line.split("=");
					result.put(resultArray[0], resultArray[1]);
//					ret = ret+"\n"+line.toString();
			}
			reader.close(); 
		}
		connection.disconnect();
		
		return result.isEmpty()?null:result;
	}
}