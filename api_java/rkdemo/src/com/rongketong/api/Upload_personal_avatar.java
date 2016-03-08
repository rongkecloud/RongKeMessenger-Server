package com.rongketong.api;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.sf.json.JSONObject;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.Logger;
import com.rongketong.utils.ApiErrorCode;
import com.rongketong.utils.ImageCompress;
import com.rongketong.utils.MysqlBaseManager;
import com.rongketong.utils.Tools;

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
@WebServlet("/upload_personal_avatar.php")
public class Upload_personal_avatar extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger m_logger = Logger.getLogger(Upload_personal_avatar.class);
	/**
	 * 临时目录
	 */
	private File tempPath;

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		DiskFileItemFactory factory = new DiskFileItemFactory();
		// 设置内存缓冲区，超过后写入临时文件
		factory.setSizeThreshold(1024*1024);
		// 设置临时文件存储位置
		tempPath = new File(this.getServletConfig().getServletContext().getRealPath("/")+"/upload");
		if (!tempPath.exists()) {
			tempPath.mkdir();
		}
		if(tempPath == null){
			m_logger.debug(String.format("create file error,tempPath is null"));
    		String ret=ApiErrorCode.echoErr(ApiErrorCode.SYSTEM_ERR);			
    		response.getWriter().write(ret);
    		return;
		}else{
			factory.setRepository(tempPath);
		}
		ServletFileUpload upload = new ServletFileUpload(factory);
		// 设置单个文件的最大上传值
		upload.setFileSizeMax(1024*1024);
		// 设置整个request的最大值
		upload.setSizeMax(1024*1024);
		upload.setHeaderEncoding("UTF-8");
		try {
			List<?> items = upload.parseRequest(request);
			Iterator<?> iter = items.iterator();
			FileItem item = (FileItem) iter.next();
	    	Map<String,String> accountInfo = null;
			for (int i = 0 ;i < items.size(); i++){
				item = (FileItem) items.get(i);
				if(item.isFormField()){
					// 参数校验
			    	if(item.getString() == null || item.getString().equals("")){
			    		m_logger.info(String.format("FAILED params=%s %s",item.getString(),"check parameters error"));
			    		String ret=ApiErrorCode.echoErr(ApiErrorCode.API_ERR_MISSED_PARAMATER);			
			    		response.getWriter().write(ret);
			    		return;
			    	}
			    	String appSession = item.getString();
			    	//参数格式校验
			    	if(!Tools.checkSession(appSession)){
			    		m_logger.info(String.format("FAILED params=%s %s",item.getString(),"check parameters error"));
			    		response.getWriter().write(ApiErrorCode.echoErr(ApiErrorCode.API_ERR_MISSED_PARAMATER));
			    		return;
			    	}
			    	/**
			    	 * 检查Session
			    	 */

			    	try {
			    		accountInfo = MysqlBaseManager.checkSession(appSession);
						if(accountInfo == null){
							response.getWriter().write(ApiErrorCode.echoErr(ApiErrorCode.API_ERR_INVALID_SESSION));
							return;
						}
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						m_logger.info(String.format("FAILED params=%s %s",appSession,"check session error,error cause:"+e.getCause()));
						response.getWriter().write(ApiErrorCode.echoErr(ApiErrorCode.SYSTEM_ERR));
			    		return;
					}
				}
				if(item.getName()!=null){
					//判断上传图片大小，默认1M
					if(item.getSize()>1024*1024){
						response.getWriter().write(ApiErrorCode.echoErr(ApiErrorCode.FILE_SEND_ERR));
			    		return;
					}
					String fileName = tempPath + File.separator + item.getName();
					item.write(new File(fileName));
					FileInputStream fis = new FileInputStream(tempPath+File.separator+item.getName());
					BufferedImage buffImg  = ImageCompress.zoom(tempPath+File.separator+item.getName());
					String msDocType = item.getName().substring(item.getName().indexOf(".")+1);   //上传图片后缀
					ByteArrayOutputStream os = new ByteArrayOutputStream();  
			        ImageIO.write(buffImg,msDocType, os);  
			        InputStream avatarThumb = new ByteArrayInputStream(os.toByteArray());  
			        HashMap<String,String> result = MysqlBaseManager.saveAvatar(accountInfo.get("user_account"), fis,avatarThumb);
			        if(result.get("fail")!=null && result.get("fail").equals(ApiErrorCode.SYSTEM_ERR)){
			        	response.getWriter().write(ApiErrorCode.echoErr(ApiErrorCode.SYSTEM_ERR));
			    		return;
			        }else{
			        	String userJson = JSONObject.fromObject(result).toString();
			        	response.getWriter().write(ApiErrorCode.echoOkArr("result="+userJson.toString()));
			        	buffImg.flush();
			        	avatarThumb.close();
			        	fis.close();
			        	Tools.deleteFile(fileName);
			     		return;
			        }
					
				}
			}
		} catch (FileUploadException e) {
			m_logger.info(String.format("FAILED params=%s","check file error,error cause:"+e.getCause()));
			response.getWriter().write(ApiErrorCode.echoErr(ApiErrorCode.SYSTEM_ERR));
    		return;
		} catch (Exception e) {
			m_logger.info(String.format("FAILED params=%s","system error,error cause:"+e.getCause()));
			response.getWriter().write(ApiErrorCode.echoErr(ApiErrorCode.SYSTEM_ERR));
    		return;
		}
	}

}
