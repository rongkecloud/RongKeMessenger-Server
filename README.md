# RongKeMessenger-Server
RongKeMessenger for Server（融科通Server端源码）

[Home Page(官方主页)](http://www.rongkecloud.com) | [Doc(文档手册)](http://www.rongkecloud.com/download/rongketong/doc.zip) | [CHANGELOG(更新历史)](https://github.com/rongkecloud/RongKeMessenger-Server/blob/master/CHANGELOG.md)

## 功能介绍
    融科通是基于云视互动打造的前、后完全开源APP，除完善的APP框架和后台设计外，还涵盖了注册、登录、通讯录管理、单聊、群聊、音视频通话、多人语音聊天室等即时通讯互动功能，陆续融科通还会逐步推出朋友圈、附近的人、多媒体客服等高级功能，旨在帮助广大开发者能够基于融科通开源代码上最低成本快速实现自身的产品。<br/>
融科通下载支持的电子市场有：豌豆荚、360手机助手、应用宝、百度手机助手、91助手、安卓市场。

## 基于开源框架融科通开发Server说明：

    根据您的需要下载Server端Java版本、PHP版本融科通开源代码，使用Eclipse开发工具打开。

### 代码配置说明 <br>
#### 1、导入sql(MySql数据库)
    创建融科通使用的数据库，导入融科通.sql，创建对应用户；
#### 2、注册开发者账号<br/>
    去云视互动官网注册开发者账号，登录后创建应用，获得客户端及服务端密钥。<br>
    注册开发者流程文档,请访问：http://www.rongkecloud.com/document/<br>
    开发者中心登录请访问：http://developer.rongkecloud.com/<br>
#### 1、修改配置文件<br/>

##### PHP代码配置文件修改<br>
    修改config包下的rkdemo/sysconfig/sysConfig.ini文件中的数据库信息为用户自己的数据库信息<br>
    MASTER_HOST=127.0.0.1   //DB服务器 ip地址<br>
    MASTER_USER=username   //DB用户<br>
    MASTER_PASS=password    //DB访问密码<br>
    MASTER_NAME=rkcloud_test //DB名称<br>
    MASTER_CHARSET=utf8<br>

##### Java代码配置文件修改<br>
    修改WEB-INF/config/servlet.conf配置文件，需要修改的部分：<br>
    mysql.base.info = db_ip:3306,db_name,db_user,db_pwd<br>
    db_ip替换为需要使用的数据库ip地址<br>
    db_name替换为该应用所使用的数据库名称<br>
    db_user 替换为连接数据库的用户名<br>
    db_pwd 替换为连接数据库的密码<br>
    应用在云视互动平台中的”服务器端密钥”，通过在云视互动官网注册开发者账号，创建应用后获得<br>
    rkcloudapi_serverkey= 您注册的serverkey<br>
    图片上传临时目录，配置后，请给予可读可写权限。<br>
    如果遇见头像上传失败，请确认改目录是否配置以及是否有读写权限。<br>
    image_temp=/upload<br>

#### 2、修改融科通服务端秘钥 <br>
##### PHP代码修改
    修改RkCloudApi包下的RkCloud_Config.ini文件中的rkcloudapi_serverkey为您在开发者中心中创建应用的服务器密钥。<br>

### 3、代码部署<br>
#### PHP代码部署<br>
    将PHP版本的源码rkdemo解压到HTTP根目录，保证rkdemo目录及文件访问权限。<br>
    在浏览器访问：http://your-server-ip/rkdemo/login.php<br>
    返回:ret_code=9999，说明api部署成功。<br>

#### Java代码部署<br>
    将rkdemo项目生成.war文件，将此文件部署到tomcat服务器的webapps目录下，保证rkdemo目录及文件访问权限。<br>
    在浏览器访问：http://your-server-ip:8080/rkdemo/login.php<br>
    返回:ret_code=9999，说明api部署成功。<br>

