# RongKeMessenger-Server
RongKeMessenger for Server（融科通Server端源码）

[Home Page(官方主页)](http://www.rongkecloud.com) | [Doc(文档手册)](http://www.rongkecloud.com/download/rongketong/doc.zip) | [CHANGELOG(更新历史)](https://github.com/rongkecloud/RongKeMessenger-Server/blob/master/CHANGELOG.md)

## 功能介绍
融科通是基于云视互动打造的前、后完全开源APP，除完善的APP框架和后台设计外，还涵盖了注册、登录、通讯录管理、单聊、群聊、音视频通话、多人语音聊天室等即时通讯互动功能，陆续融科通还会逐步推出朋友圈、附近的人、多媒体客服等高级功能，旨在帮助广大开发者能够基于融科通开源代码上最低成本快速实现自身的产品。
融科通下载支持的电子市场有：豌豆荚、360手机助手、应用宝、百度手机助手、91助手、安卓市场。

## 基于开源框架融科通开发Server说明：

根据您的需要下载Server端Java版本、PHP版本融科通开源代码，使用Eclipse开发工具打开。

### PHP代码配置说明 <br>
#### 1、修改sysConfig.ini配置文件<br/>
修改config包下的sysConfig.ini文件中的数据库信息为用户自己的数据库信息

#### 2、修改融科通服务端秘钥 <br>
修改RkCloudApi包下的RkCloud_Config.ini文件中的rkcloudapi_serverkey为您在开发者中心中创建应用的服务器密钥。

#### 3、配置文件修改
修改API配置文件rkdemo/sysconfig/sysConfig.ini
MASTER_HOST=127.0.0.1   //DB服务器 ip地址
MASTER_USER=username   //DB用户
MASTER_PASS=password    //DB访问密码
MASTER_NAME=rkcloud_test //DB名称
MASTER_CHARSET=utf8

### 4、PHP代码部署<br>
将PHP版本的源码rkdemo解压到HTTP根目录，保证rkdemo目录及文件访问权限。
在浏览器访问：http://your-server-ip/rkdemo/login.php
返回:ret_code=9999，说明api部署成功。

### Java代码配置说明 <br>
####1、导入sql
创建融科通使用的数据库，导入融科通.sql，创建对应用户；
#### 2、注册开发者账号<br/>
去云视互动官网注册开发者账号，登录后创建应用，获得客户端及服务端密钥。
注册开发者流程文档,请访问：http://www.rongkecloud.com/document/
开发者中心登录请访问：http://developer.rongkecloud.com/

#### 3、修改配置文件
修改WEB-INF/config/servlet.conf配置文件，需要修改的部分：
#Mysql connection Info
mysql.base.info = db_ip:3306,db_name,db_user,db_pwd
 其中： db_ip替换为需要使用的数据库ip地址
        db_name替换为该应用所使用的数据库名称
        db_user 替换为连接数据库的用户名
        db_pwd 替换为连接数据库的密码
#应用在云视互动平台中的”服务器端密钥”，通过在云视互动官网注册开发者账号，创建应用后获得
rkcloudapi_serverkey= 您注册的serverkey
#图片上传临时目录，配置后，请给予可读可写权限，
#如果遇见头像上传失败，请确认改目录是否配置以及是否有读写权限。
image_temp=/upload

#### 4、Java代码部署<br>
将rkdemo项目生成.war文件，将此文件部署到tomcat服务器的webapps目录下，保证rkdemo目录及文件访问权限。
在浏览器访问：http://your-server-ip:8080/rkdemo/login.php
返回:ret_code=9999，说明api部署成功。

