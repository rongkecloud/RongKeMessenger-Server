package com.rongketong.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import com.rongketong.dispatcher.Config;
import com.yunshihudong.sdk.server.YsServerSDK;

public class Tools {

	public static String md5(String stext){
		MessageDigest md=null; 
		StringBuffer sb = null;
		try {
			md=MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			return null;
		} 
		md.update(stext.getBytes()); //MD5加密算法只是对字符数组而不是字符串进行加密计算，得到要加密的对象 
		byte[] bs=md.digest();   //进行加密运算并返回字符数组 
		sb=new StringBuffer(); 
		for(int i=0;i<bs.length;i++){    //字节数组转换成十六进制字符串，形成最终的密文 
			int v=bs[i]&0xff; 
			if(v<16){ 
				sb.append(0); 
			} 
			sb.append(Integer.toHexString(v)); 
		} 
		return sb.toString();
	}
	
	private static final String allChar = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
	public static String getRandomStr(int length)
	{
		StringBuffer sb = new StringBuffer(); 
		Random random = new Random(); 
		for (int i = 0; i < length; i++) {
			sb.append(allChar.charAt(random.nextInt(allChar.length()))); 
		} 
		return sb.toString(); 
	}
	/**
	 * 手机号码格式校验
	 * @param mobile
	 * @return
	 */
	public static boolean checkMobileFormat(String mobile){
		if(mobile == null || mobile.equals("")){
			return true;
		}
    	Pattern regx = Pattern.compile("^1[35678]\\d{9}$");
		Matcher m = regx.matcher(mobile);
		while(m.find()){
			return true;
		}
		return false;
	}
	public static ByteArrayOutputStream InputStreamCopy(InputStream inStream) throws IOException{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();  
		byte[] buffer = new byte[1024];  
		int len;  
		while ((len = inStream.read(buffer)) > -1 ) {  
			baos.write(buffer, 0, len);  
		}  
		baos.flush();
		return baos;
		//return new ByteArrayInputStream(baos.toByteArray());
	}
	
	
	public static String hashMapToJson(HashMap<String,String> map) {  
	    String string = "{";  
	    for (Iterator<Entry<String, String>> it = map.entrySet().iterator(); it.hasNext();) {  
	        Entry<String, String> e = it.next();  
	        string += "'" + e.getKey() + "':";  
	        string += "'" + e.getValue() + "',";  
	    }  
	    string = string.substring(0, string.lastIndexOf(","));  
	    string += "}";  
	    return string;  
	} 
	/**
	 * Hex编码.
	 */
	public static String encodeHex(byte[] input) {
		return Hex.encodeHexString(input);
	}
	/**
	 * Hex解码.
	 */
	public static byte[] decodeHex(String input) {
		try {
			return Hex.decodeHex(input.toCharArray());
		} catch (DecoderException e) {
			throw Exceptions.unchecked(e);
		}
	}
	/**
	 * UTF-8转换
	 * @param str
	 * @return
	 */
	 public static String changeCharsetUTF8(String str){
		 if (str != null) {
			 try {
				 String xmString = new String(str.getBytes("UTF-8"));  
				 String xmlUTF8 = URLEncoder.encode(xmString, "UTF-8"); 
				 return xmlUTF8;
			 } catch (UnsupportedEncodingException e) {
				 // TODO Auto-generated catch block
				 e.printStackTrace();
			 }
		 }
		 else 
			 return null;
		return "";
	 }
	 /**
		 *@function 判断Session是否符合要求
		 *@param: string str 校验的字符串
		 *@boolean:
	 **/
	 public static boolean checkSession(String accountName){
		 if(accountName.equals("")||accountName==null){
				return false;
			}
			String exp = "(^[0-9a-zA-Z]*$)";
			if(Pattern.matches(exp, accountName) && accountName.length()>0 && accountName.length()<=100){
				return true;
			}
			return false;
	 }
	 /**
		 *@function 判断用户名是否符合要求
		 *@param: string str 校验的字符串
		 *@boolean:
	 **/
	 public static boolean checkAccount(String accountName){
		 if(accountName.equals("")||accountName==null){
				return false;
			}
			String exp = "(^[0-9a-zA-Z]{6,20}$)";
			if(Pattern.matches(exp, accountName)){
				return true;
			}
			return false;
	}
	/**
	 *@function 判断是否包含特殊字符
	 *@param: string str 校验的字符串
	 *@boolean:
	 **/
	 public	static boolean containSpecChars(String str){
			String exp = "/[\\|<>&%$\\\"\']/";
			if(!Pattern.matches(exp,str)){
				return true;
			}
			return false;		
	}
	/**
	 *@function 判断是否是纯数字，并且不以0开头
	 *@param: string str 校验的字符串
	 *@boolean:
	 **/
	public static boolean isDigits2(String str){
			if(str == null || str.equals("")){
				return false;
			}
			String exp = "(^[1-9]\\d*$)";
			if(!Pattern.matches(exp, str)){
				return false;
			}
			return true;
	}
	/**
	 *@function 判断是否是Email
	 *@param: string checkEmail 校验的字符串
	 *@boolean: true: is email  false: not email 
	**/
	public static boolean isEmail(String checkEmail){
		if(checkEmail == null || checkEmail.equals("")){
			return true;
		}
		String exp = "(^(([0-9a-zA-Z]+)|([0-9a-zA-Z]+[_.0-9a-zA-Z-]*[0-9a-zA-Z]+))@([a-zA-Z0-9-]+[.])+([a-zA-Z]{2}|net|NET|com|COM|gov|GOV|mil|MIL|org|ORG|edu|EDU|int|INT)$)";
		if(Pattern.matches(exp, checkEmail)){
			return true;		
		}	
		return false;	
	}
	/**
	 *@function 判断加好友权限是否符合要求
	 *@param: string str 校验的字符串
	 *@boolean:
	 **/
	public static boolean checkPermission(String str){
		if(str == null || str.equals("")){
			return false;
		}
		if(str.equals("1") || str.equals("2")){
			return true;
		}
		return false;
	}
	/**
	 *@function 判断密码是否符合要求
	 *@param: string str 校验的字符串
	 *@boolean:
	 **/
	 public static boolean checkPwd(String accountPwd){
			if(accountPwd.equals("")||accountPwd==null){
				return false;
			}
			String exp = "(^[0-9a-zA-Z]{6,20}$)";
			if(Pattern.matches(exp, accountPwd)){
				return true;
			}
			return false;
	}
	 /**
		 *@function 判断姓名是否符合要求
		 *@param: string checkName 校验的字符串
		 *@boolean:
	 **/
	 public	static boolean checkName(String checkName){
			if(checkName == null || checkName.equals("")){
				return true;
			}
			if(checkName.length() <= 0 || checkName.length() > 30 || !Tools.containSpecChars(checkName)){
				return false;
			}
			return true;
	}
	 /**
		 *@function 判断是否是mobile号码
		 *@param: string checkMobile 校验的字符串
		 *@boolean:
	 **/
	public static boolean isMobile(String checkMobile){
			if(checkMobile == null || checkMobile.equals("")){
				return false;
			}
			String exp = "/^1[35789]\\d{9}$/";
			if(Pattern.matches(exp, checkMobile)){
				return true;
			}
			return false;
	}
	/**
	 * 比较版本号的大小,前者大则返回一个正数,后者大返回一个负数,相等则返回0
	 * @param version1
	 * @param version2
	 * @return
	 */
	public static int compareVersion(String version1, String version2){
		if (version1 == null || version2 == null) {
			return -1;
		}
		String[] versionArray1 = version1.split("\\.");//注意此处为正则匹配，不能用"."；
		String[] versionArray2 = version2.split("\\.");
		int idx = 0;
		int minLength = Math.min(versionArray1.length, versionArray2.length);//取最小长度值
		int diff = 0;
		while (idx < minLength
				&& (diff = versionArray1[idx].length() - versionArray2[idx].length()) == 0//先比较长度
				&& (diff = versionArray1[idx].compareTo(versionArray2[idx])) == 0) {//再比较字符
			++idx;
		}
		//如果已经分出大小，则直接返回，如果未分出大小，则再比较位数，有子版本的为大；
		diff = (diff != 0) ? diff : versionArray1.length - versionArray2.length;
		return diff;
	}
    /**
     * 获取云视互动服务实例
     */
	public static YsServerSDK geYsServerAPI(){
    	String YSKEY = Config.getInstance().getString("rkcloudapi_serverkey");
    	return new YsServerSDK(YSKEY);
	}
	/*********************************
	 * @function: getRandomID 随机数生成方法
	 * @param int len 随机数长度
	 * @param string  type 获取随机数类型 1：数字和字母组成  2：字母组成  3：纯数字组成
	 * @return string
	 **********************************/
	public static String getRandomID(Integer len, String type)
	{
		String[] numArr = new String[]{ "0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
		String[] upperArr = new String[]{"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
		String[] lowerArr = new String[]{"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z" };
		String randStr = "";
		String[] randArr = null;
		if(type.equals("1")){
			randArr = concatAll(numArr, upperArr, lowerArr);
		}else if(type.equals("2")){
			randArr = concatAll(upperArr,lowerArr);
		}else if(type.equals("3")){
			randArr = numArr;
		}
		Random random = new Random();
		for(int i = 0; i < len; i++ ){
			randStr += randArr[random.nextInt(randArr.length-1)];
		}
		return randStr;
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T[] concatAll(T[] first, T[]... rest) {
	  int totalLength = first.length;
	  for (T[] array : rest) {
	    totalLength += array.length;
	  }
	  T[] result = Arrays.copyOf(first, totalLength);
	  int offset = first.length;
	  for (T[] array : rest) {
	    System.arraycopy(array, 0, result, offset, array.length);
	    offset += array.length;
	  }
	  return result;
	}
	/**
	   * 输出图片
       * @param inputStream
	   * @param path
	*/
    public static void readBlob(InputStream inputStream, String path) {
         try {
             FileOutputStream fileOutputStream = new FileOutputStream(path);
            byte[] buf = new byte[1024*1024];
            int len = 0;
             while ((len = inputStream.read(buf)) != -1) {
                 fileOutputStream.write(buf, 0, len);
              }
             inputStream.close();
             fileOutputStream.close();
         } catch (FileNotFoundException e) {
            e.printStackTrace();
         } catch (IOException e) {
             e.printStackTrace();
         }
    }
    /**
     * 生成随机文件名
     */
    public static String getRandFileName(){
    	long fileName = 123456789+(int)(Math.random()*999999999);
    	return String.valueOf(fileName);
    }
	/**
	 * 
	 * 删除单个文件
	 * 
	 * @param fileName 被删除的文件名
	 * @return 如果删除成功，则返回true，否则返回false
	 */
	public static boolean deleteFile(String fileName) {
		File file = new File(fileName);
		if (file.exists() && file.isFile()) {
			if (file.delete()) {
				return true;
			} else {
				return false;
			}
		} else {
			return true;
		}
	}
}
