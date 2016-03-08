package com.rongketong.dispatcher;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import com.rongketong.utils.MysqlBaseUtil;

/**
 * SERVLET
 * @author Jason.Liu
 *
 */
@WebServlet("/Startup")
public class Startup extends HttpServlet
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final Logger m_logger = Logger.getLogger(Startup.class);
	
	/**
	 * singleton模式中的唯一实例
	 */
	private static Startup m_instance = new Startup();
	
	
	public static Startup getInstance()
	{
		return m_instance;
	}
	
	
	private String m_servletConfigPath;
	
	public String getServletConfigPath(){
		return m_servletConfigPath;
	}
	
	/**
	 * 这个SERVER服务器节点配置
	 */
	private int m_servletId;
	
	public int getServletId(){
		return m_servletId;
	}
	
	/**
	 * 整个SEVLET API框架的数据初始化主要入口
	 */
	public void init(ServletConfig config) throws ServletException {
		/**
		 * 启动后台服务
		 */
		Startup.getInstance().startup(config);
	}
	
	//定义LOG4J配置文件路径
	private void initLog4jConfigLocation(ServletConfig config) throws Exception{ 
		String prefix = config.getServletContext().getRealPath("/"); 
		String file = config.getInitParameter("log4jConfigLocation");
		String logPath = prefix+config.getInitParameter("log4jLogPath");
		System.setProperty ("WORKDIR",logPath);//定义当前工作路径
		PropertyConfigurator.configure(prefix+file); 
	} 

	//定义SERVLET服务配置文件路径
	private void initServletConfigLocation(ServletConfig config) throws Exception{  

		String prefix = config.getServletContext().getRealPath("/"); 
		String file = config.getInitParameter("servletConfigLocation"); 
		m_servletConfigPath = prefix+file;
	} 
	
	
	/**
	 * 目前针对后台消息服务处理流程进行调整
	 * 采用SERVLET技术进行API整合
	 * SERVLET+TOMCAT可以多台部署，
	 */
	public void startup(ServletConfig config)
	{
		try
		{
			initLog4jConfigLocation(config);
			initServletConfigLocation(config);
			m_servletId = Config.getInstance().getInt(ConfigKey.KEY_SERVLET_ID);
			m_logger.warn("***************************************************");
			m_logger.warn("*******  Start RongkeTong API Server v1.0  ***********");
			m_logger.warn("***************************************************");

			//1初始化基本数据库信息
			MysqlBaseUtil.intializeConnectionPool();
			
			//开始初始化BaseConfig配置数据
			/*GlobalSet gs = new GlobalSet();
			Thread td = new Thread(gs);
			td.start();
*/
			m_logger.warn("***************************************************");
			m_logger.warn("******** Start RongkeTong API Server startup! ********");
			m_logger.warn("***************************************************");
		}
		catch (Exception e)
		{
			m_logger.fatal("Start Message Server failed, exit."+ e);
			m_logger.warn("***************************************************");
			m_logger.warn("*******Start RongkeTong API Server failed, exit.******");
			m_logger.warn("***************************************************");
			MysqlBaseUtil.shutdownConnectionPool();
			System.exit(-2);
		}
	}
	
	
	/**
	 * 停止服务
	 */
	public void shutdown()
	{
		try
		{
			m_logger.warn("Shutdown Message Server");
			MysqlBaseUtil.shutdownConnectionPool();
			m_logger.warn("System exit");
		}
		catch (Exception ex)
		{
			m_logger.warn("Shutdown failed, force to exit", ex);
		}
		finally
		{
			System.exit(1);
		}
	}
	
	
}
