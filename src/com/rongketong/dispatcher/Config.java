package com.rongketong.dispatcher;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import org.apache.log4j.Logger;


/**
 * <br>
 * Config 类，singleton。使用方法： <br>
 * 1、初始化：Config.getInstance().loadConfig(); <br>
 * 2、读取：Config.getInstance().getXXX(); <br>
 * 3、保存：Config.getInstance().save();
 * 
 */
public class Config implements ConfigKey {
	/**
	 * singleton模式中的唯一实例
	 */
	private static Config m_instance = null;

	/**
	 * 读取到的配置数据
	 */
	private ArrayList<Property> m_contents = new ArrayList<Property>();

	/**
	 * 读取到的配置数据，按照key-Property的形式存储
	 */
	private HashMap<String, Property> m_nvmap = new HashMap<String, Property>();

	public static Logger logger = Logger.getLogger(Config.class);

	public static Config getInstance() {
		if (m_instance == null) {
			m_instance = new Config();
		}
		return m_instance;
	}

	/**
	 * 读入配置文件
	 * 
	 * @param is
	 * @throws Exception
	 */
	public Config() {
		FileInputStream is;
		try {
			is = new FileInputStream(Startup.getInstance().getServletConfigPath());
			load(is);
		} catch (FileNotFoundException e) {
			logger.error("config file not found:" + e.getMessage());
		} catch (IOException e) {
			logger.error("write file failure:" + e.getMessage());
		}
	}

	/**
	 * 从指定文件中读取出配置数据
	 * 
	 * @param is
	 * @throws IOException
	 */
	private void load(FileInputStream is) throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(is));
		String line = null;
		int lineNumber = 0;
		while ((line = in.readLine()) != null) {
			Property pl = new Property(line, lineNumber);
			m_contents.add(pl);
			if (pl.hasKeyValue()) {
				m_nvmap.put(pl.getKey(), pl);
			}
			lineNumber++;
		}
	}

	/**
	 * 保存配置文件
	 * 
	 * @throws IOException
	 */
	public void save(String file) {
		FileOutputStream os;
		try {
			os = new FileOutputStream(file);
			for (Property pl : m_contents) {
				String strToWrite = pl.toString() + "\n";
				os.write(strToWrite.getBytes());
			}
		} catch (FileNotFoundException e) {
			logger.error("config file not found:" + e.getMessage());
		} catch (IOException e) {
			logger.error("write file failure:" + e.getMessage());
		}

	}

	/**
	 * 保存配置
	 * 
	 * @param key
	 * @param value
	 */
	private void put(String key, String value) {
		Property pl = m_nvmap.get(key);
		if (pl != null) {
			pl.setValue(value);
		} else {
			pl = new Property(String.format("%s = %s", key, value),
					m_contents.size());
			m_contents.add(pl);
			m_nvmap.put(key, pl);
		}
	}

	/**
	 * 获取一个值
	 * 
	 * @param key
	 * @return
	 */
	private String getValue(String key) {
		Property pl = m_nvmap.get(key);
		if (pl != null)
			return pl.getValue();
		else
			return null;
	}

	/**
	 * 更新一个 key - value 对，会保存 value.toString() 的值
	 * 
	 * @param key
	 * @param value
	 */
	public void update(String key, Object value) {
		put(key, value.toString());
	}

	/**
	 * 设置一个 key - value 对，会保存 value.toString() 的值
	 * 
	 * @param key
	 * @param value
	 */
	public void set(String key, Object value) {
		update(key, value);
	}

	/**
	 * 设置一个 key - value 对
	 * 
	 * @param key
	 * @param value
	 */
	public void update(String key, int value) {
		put(key, String.valueOf(value));
	}

	/**
	 * 设置一个 key - int 对
	 * 
	 * @param key
	 * @param value
	 */
	public void setInt(String key, int value) {
		update(key, value);
	}

	/**
	 * 设置一个 key - value 对
	 * 
	 * @param key
	 * @param value
	 */
	public void update(String key, double value) {
		put(key, String.valueOf(value));
	}

	/**
	 * 设置一个 key - value 对
	 * 
	 * @param key
	 * @param value
	 */
	public void setDouble(String key, double value) {
		update(key, value);
	}

	/**
	 * 设置一个 key - value 对
	 * 
	 * @param key
	 * @param value
	 */
	public void update(String key, String value) {
		put(key, value);
	}

	/**
	 * 设置一个 key - value 对
	 * 
	 * @param key
	 * @param value
	 */
	public void setString(String key, String value) {
		update(key, value);
	}

	/**
	 * 设置一个 key - value 对
	 * 
	 * @param key
	 * @param value
	 */
	public void update(String key, boolean value) {
		put(key, String.valueOf(value ? 1 : 0));
	}

	/**
	 * 设置一个 key - value 对
	 * 
	 * @param key
	 * @param value
	 */
	public void setBoolean(String key, boolean value) {
		update(key, value);
	}

	/**
	 * 获取指定key对应的value
	 * 
	 * @param key
	 * @return
	 */
	public Object get(String key) {
		return getValue(key);
	}

	/**
	 * 获取指定key对应的value，String类型，如果获取不到，返回指定的默认值
	 * 
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public String getString(String key, String defaultValue) {
		Object o = getValue(key);
		if (o == null)
			return defaultValue;
		else
			return o.toString();
	}

	/**
	 * 获取指定key对应的value值，boolean类型. 在存储时，用 1、true(不区分大小写) 来表示 Boolean.True，用
	 * 0、false 来表示 Boolean.False；如果未设置或者是非法值，也是 false
	 * 
	 * @param key
	 * @return
	 */
	public boolean getBoolean(String key) {
		return getBoolean(key, false);
	}

	/**
	 * 获取指定key对应的value，boolean类型，如果获取不到，返回指定的默认值
	 * 
	 * 在存储时，用 1、true(不区分大小写) 来表示 Boolean.True，用 0、false 来表示
	 * Boolean.False；如果未设置或者是非法值，用 defaultValue
	 * 
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public boolean getBoolean(String key, boolean defaultValue) {
		String v = getString(key, "");
		if (v == null)
			return defaultValue;

		v = v.trim();
		if (v.equals("1") || (v.compareToIgnoreCase("true") == 0))
			return true;
		else if (v.equals("0") || (v.compareToIgnoreCase("false") == 0))
			return false;
		else
			return defaultValue;
	}

	/**
	 * 获取指定key对应的value值，double类型
	 * 
	 * @param key
	 * @return
	 */
	public double getDouble(String key) {
		return getDouble(key, 0.0);
	}

	/**
	 * 获取指定key对应的value，double类型，如果获取不到，返回指定的默认值
	 * 
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public double getDouble(String key, double defaultValue) {
		Object o = getValue(key);
		if (o == null)
			return defaultValue;
		else {
			try {
				return Double.parseDouble(o.toString());
			} catch (NumberFormatException e) {
				return defaultValue;
			}
		}
	}

	/**
	 * 获取指定key对应的value，int类型，如果获取不到，返回指定的默认值
	 * 
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public int getInt(String key, Integer defaultValue) {
		Object o = getValue(key);
		if (o == null)
			return defaultValue;
		else {
			try {
				return Integer.parseInt(o.toString());
			} catch (NumberFormatException e) {
				return defaultValue;
			}
		}
	}

	/**
	 * 获取指定key对应的value，String类型
	 * 
	 * @param key
	 * @return
	 */
	public String getString(String key) {
		return getString(key, "");
	}

	/**
	 * 获取指定key对应的value值，int类型
	 * 
	 * @param key
	 * @return
	 */
	public int getInt(String key) {
		return getInt(key, 0);
	}

}

/**
 * 
 * Defines a property line, format: name = value # comments
 * 
 * @author Liu Jiong
 */
class Property {
	/**
	 * 行号
	 */
	private int m_lineNumber = -1;

	/**
	 * 一行的内容
	 */
	private String m_contents = null;

	/**
	 * 一行内容被=分隔开的左半部分
	 */
	private String m_key = null;

	/**
	 * 一行内容被=和#包括的部分
	 */
	private String m_value = null;

	/**
	 * 一行内容被#分隔开的右半部分
	 */
	private String m_comments = "";

	public Property(String line, int lineNumber) {
		m_contents = line;
		m_lineNumber = lineNumber;
		int idx = -1;
		if ((idx = line.indexOf('#')) >= 0) {
			m_comments = line.substring(idx); // m_comments includes '#'
			line = line.substring(0, idx);
		}

		if ((idx = line.indexOf('=')) > 0) {
			m_key = line.substring(0, idx).trim();
			if (line.length() > (idx + 1))
				m_value = line.substring(idx + 1).trim();

			if (isEmpty(m_value))
				m_value = null;
		}
	}

	/**
	 * 判定给定字符串是否为空
	 * 
	 * @param str
	 * @return
	 */
	private boolean isEmpty(String str) {
		return (str == null) || (str.length() == 0);
	}

	public String toString() {
		return m_contents;
	}

	/**
	 * 判定key是否为空
	 * 
	 * @return
	 */
	public boolean hasKeyValue() {
		return !isEmpty(m_key);
	}

	/**
	 * 获取value
	 * 
	 * @return
	 */
	public String getValue() {
		return m_value;
	}

	/**
	 * 设置value
	 * 
	 * @param value
	 */
	public void setValue(String value) {
		m_value = value;

		if ((m_comments == null) || (m_comments.length() == 0)) {
			m_contents = String.format("%s = %s", m_key, m_value);
		} else {
			m_contents = String.format("%s = %s \t%s", m_key, m_value,
					m_comments);
		}
	}

	/**
	 * 获取行号
	 * 
	 * @return
	 */
	public int getLineNumber() {
		return m_lineNumber;
	}

	/**
	 * 获取本行的内容
	 * 
	 * @return
	 */
	public String getContents() {
		return m_contents;
	}

	/**
	 * 获取key
	 * 
	 * @return
	 */
	public String getKey() {
		return m_key;
	}

	/**
	 * 获取comments
	 * 
	 * @return
	 */
	public String getComments() {
		return m_comments;
	}
}
