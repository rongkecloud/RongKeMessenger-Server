<?php
	
class DataBase_Mysql {
	
	/**
	 * 所有 SQL 查询的日志
	 *
	 * @private array
	 */
	private $log = array();	

	/**
	 * 数据库驱动
	 *
	 * @private string
	 */	
	private $dbDriver;

	/**
	 * 数据库主机名
	 *
	 * @private string
	 */	
	private $dbHost;

	/**
	 * 数据库用户名
	 *
	 * @private string
	 */	
	private $dbUser;

	/**
	 * 数据库密码
	 *
	 * @private string
	 */		
	private $dbpass;

	/**
	 * 数据库名
	 *
	 * @private string
	 */		
	private $dbName;

	/**
	 * 数据库链接对象
	 *
	 * @private string
	 */		
	private $dbConnection = null;

	/**
	 * 数据库链接方式
	 *
	 * @private string
	 */		
	private $dbConnMethod;

	/**
	 * mysql链接方式对象
	 *
	 * @private string
	 */		
	private $connection_function = null;

	/**
	 * 数据库字符集
	 *
	 * @private string
	 */		
	private $dbCharset;
	
    /**
     * 指示事务是否提交
     *
     * @var boolean
     */
    private $_transCommit = false;
    
    /**
     * DB链接方法
     *
     * @return boolean
     */	 
    
    /**
	 * 数据库操作失败次数统计
	 *
	 * @private string
	 */		
	private $_dbErrCount = 0;
	
	 /**
	 * 出错的SQL语句
	 *
	 * @private array
	 */		
	private $_errSql = array();
    
	public function connect($arr = null)
	{
		$this->__init($arr);
		//判断链接是否为空		
		if ($this->dbConnection) 
		{
			return true; 
		}
		//链接方式：：持续链接
		if ($this->dbConnMethod == "pconnect") 
		{
			$connection_function = 'mysql_pconnect';
		}
		//链接方式：：非持续链接
		else 
		{
			$connection_function = 'mysql_connect';
		}
		
		//正确执行db链接		
		//参数正确条件下链接方式
		if ($this->dbHost && $this->dbUser && $this->dbpass && $this->dbName) 
		{
			$this->dbConnection = $connection_function($this->dbHost,$this->dbUser,$this->dbpass);
			//mysql_select_db($this->dbName);
		}
		else if ($this->dbHost && $this->dbUser && $this->dbName)
		{
			$this->dbConnection = $connection_function($this->dbHost,$this->dbUser);
			//mysql_select_db($this->dbName);		
		}
		else if ($this->dbHost && $this->dbName)
		{
			$this->dbConnection = $connection_function($this->dbHost);
			//mysql_select_db($this->dbName);			
		}
		else {				
			return false;
		}
		
		if($this->dbConnection){
			mysql_select_db($this->dbName);
			//字符集设定
	        //$version = $this->getOne('SELECT VERSION()');//获得db版本
	        if ($this->dbCharset != '') 
	        {
	            $charset = $this->dbCharset;
	            $this->_query("SET NAMES '" . $charset . "'");
	        } else 
	        {
	            $charset = DB_CHARSET;
	        }
	        //if ($version >= '4.1' && $charset != '') 
	       // {
	            //$this->_query("SET NAMES '" . $charset . "'");
	       // }
	        return true;
		} else {
			return false;
		}
		
	}
	
    /**
     * 分析数据库配置文件
     *
     * @return boolean
     */
	function __init($arr) 
	{		
			if(isset($arr['connmethod']))
				$this->dbConnMethod = $arr['connmethod'];
			if(isset($arr['driver']))
				$this->dbDriver = $arr['driver'];
			if(isset($arr['host']))
				$this->dbHost = $arr['host'];
			if(isset($arr['username']))
				$this->dbUser = $arr['username'];
			if(isset($arr['password']))
				$this->dbpass = $arr['password'];
			if(isset($arr['dbname']))
				$this->dbName = $arr['dbname'];
			if(isset($arr['dbcharacter']))
				$this->dbCharset = $arr['dbcharacter'];
	}
	
    /**
     * 关闭链接---当链接方式为持续链接时关闭
     *
     */	 
	function disConnect() 
	{
		try 
		{
			if (!$this->dbConnection)
			{
				mysql_close($this->dbConnection);
			}
			$this->dbConnection = null;						
		}
		//异常系处理
		catch (Exception $e) 
		{
			throw $e;
		}
	}


    /**
     * 执行查询，返回第一条记录的第一个字段
     *
     * @param string|resource $sql
     *
     * @return mixed
     */
    function getOne($sql)
    {
    	try 
    	{
	        if (is_resource($sql)) 
	        {
	            $res = $sql;
	        } 
	        else 
	        {
	            $res = $this->_query($sql);
	        }
	        $row = mysql_fetch_row($res);
	        mysql_free_result($res);
	        return isset($row[0]) ? $row[0] : null;
    	}
    	catch (Exception $e) {
			throw $e;
    	}

    }

    /**
     * 执行查询，返回第一条记录
     *
     * @param string|resource $sql
     *
     * @return mixed
     */
    function getRow($sql) 
    {
    	try 
    	{
	        if (is_resource($sql)) 
	        {
	            $res = $sql;
	        }
	        else 
	        {
	            $res = $this->_query($sql);
	        }
	        $row = mysql_fetch_assoc($res);
	        mysql_free_result($res);    		
    	}
		catch (Exception $e) 
		{
			throw $e;
		}
        return $row;
    }

    /**
     * 执行查询，返回结果集的第一列
     *
     * @param string|resource $sql
     *
     * @return mixed
     */
    function getCol($sql) 
    {
    	try
    	{
	        if (is_resource($sql)) 
	        {
	            $res = $sql;
	        }
	        else 
	        {
	            $res = $this->_query($sql);
	        }
	        $data = array();
	        while ($row = mysql_fetch_assoc($res)) {
	            $data[] = reset($row);
	        }
	        mysql_free_result($res);    		
    	}
		catch (Exception $e)
		{
			throw $e;
		}
        return $data;
    }
        
    /**
     * 查询方法，根据查询结果决定是否显示错误信息
     *
     * @param string $sql
     * @param boolean $throw
     *
     * @return mixed
     */
    function _query($sql) {
    	try
    	{
			$this->log[] = $sql;
	        $result = mysql_query($sql, $this->dbConnection);
	        if ($result !== FALSE) 
	        {
	            return $result;
	        } else {
	        	if($this->_transCommit){//如果已经启动事务，则统计出错次数	        		
	        		$this->_errSql[] = array(
	        			"sql=" => $sql,
	        			"sql_err=" => mysql_error($this->dbConnection)
	        		);        		
	        		$this->_dbErrCount ++;//事务处理中出现问题，则记录出错次数
	        	}
	        }
    	}
		catch (Exception $e)
		{
			throw $e;
		}
        return false;
    }  

	
	function queryBlob($sql) {
    	try
    	{
			$this->log[] = $sql;
			$result=mysql_query($sql);
	        $row= mysql_fetch_object($result);
            return $row->data;
    	}
		catch (Exception $e)
		{
			throw $e;
		}
        return false;
    } 

	function saveBlob($sql) {
    	try
    	{
			$result=mysql_query($sql);
            return $result;
    	}
		catch (Exception $e)
		{
			throw $e;
		}
        return false;
    }
    

    /**
     * 执行一个查询，返回查询结果记录集
     *
     * @param string|resource $sql
     *
     * @return array
     */
    function getAll($sql) 
    {
    	try
    	{
	        if (is_resource($sql)) 
	        {
	            $res = $sql;
	        }
	        else 
	        {
	            $res = $this->_query($sql);
	        }
	        $data = array();
	        while ($row = mysql_fetch_assoc($res)) {
	            $data[] = $row;
	        }
	        mysql_free_result($res);    		
    	}
		catch (Exception $e)
		{
			throw $e;
		}
        return $data;
    }
    
    /**
     * 执行一个查询，返回一个 resource 或者 boolean 值
     *
     * @param string $sql
     * @param array $inputarr
     * @param boolean $throw 指示查询出错时是否抛出异常
     *
     * @return resource|boolean
     */
    function execute($sql) 
    {
    	try
    	{
	        return $this->_query($sql);    		
    	}
    	catch (Exception $e)
    	{
    		throw $e;
    	}

    }    

    /**
     * 启动事务
     */
    function startTrans()
    {
        //$this->_transCount += 1;
       // if ($this->_transCount == 1)
       // {
       //     $this->execute('SET AUTOCOMMIT=0');
            $this->execute('BEGIN');
            $this->_transCommit = true;//表示要开始进行事务处理
			$this->_dbErrCount = 0; //重置事务处理错误统计值
			$this->_errSql = array();//重置出错SQl数组
      //  }
    }    
 
    /**
     * 完成事务，根据查询是否出错决定是提交事务还是回滚事务
     *
     * 如果 $commitOnNoErrors 参数为 true，当事务中所有查询都成功完成时，则提交事务，否则回滚事务
     * 如果 $commitOnNoErrors 参数为 false，则强制回滚事务
     *
     * @param $commitOnNoErrors 指示在没有错误时是否提交事务
     */
    function completeTrans($commitOnNoErrors = true)
    {
        /*if ($this->_transCount < 1) { return; }
        if ($this->_transCount > 1)
        {
            $this->_transCount -= 1;
            return;
        }
        $this->_transCount = 0;*/

        if ($commitOnNoErrors)
        {
            $this->execute('COMMIT');            
        } 
        else 
        {
            $this->execute('ROLLBACK');           
        }
        //$this->execute('SET AUTOCOMMIT=1');            
        $this->_transCommit = false;//事务处理已经完成
    }
    
    /**
     * 进行限定记录集的查询
     *
     * @param string $sql
     * @param int $length
     * @param int $offset
     *
     * @return resource
     */
    function selectLimit($sql, $length = null, $offset = null)
    {
        if ($offset !== null) 
        {
            $sql .= ' LIMIT ' . (int)$offset;
            if ($length !== null)
            {
                $sql .= ', ' . (int)$length;
            } else {
                $sql .= ', 4294967294';
            }
        }
        else if ($length !== null)
        {
            $sql .= ' LIMIT ' . (int)$length;
        }
        return $this->execute($sql);
    }
    
     /**
     * 强制指示在调用 completeTrans() 时回滚事务
     */
    function failTrans()
    {
        $this->_transCommit = false;
    }

    /**
     * 判断事务是否失败的状态
     */
    function hasFailedTrans()
    {
        if ($this->_dbErrCount > 0){
            return true;
        }
        return false;
    } 
    /**
     * 返回一个事务中出错的SQL
     */
    function getErrSql()
    {      
        return $this->_errSql;
    } 
	
	/**
	*返回上次插入获得的id
	*/
	function getInsertID()
	{
		return $this->getOne("SELECT LAST_INSERT_ID()");
	}
}
	
?>