# RongKeMessenger-Server
RongKeMessenger for Server���ڿ�ͨServer��Դ�룩

![���ӻ���](http://www.rongkecloud.com/skin/simple/img/logo-small.png)

[Home Page(�ٷ���ҳ)](http://www.rongkecloud.com) | [Doc(�ĵ��ֲ�)](http://www.rongkecloud.com/download/rongketong/doc.zip) |  [CHANGELOG(������ʷ)](https://github.com/rongkecloud/RongKeMessenger-Server/blob/master/CHANGELOG.md)

## ���ܽ���
  �ڿ�ͨ�ǻ������ӻ��������ǰ������ȫ��ԴAPP�������Ƶ�APP��ܺͺ�̨����⣬��������ע�ᡢ��¼��ͨѶ¼�������ġ�Ⱥ�ġ�����Ƶͨ�����������������ҵȼ�ʱͨѶ�������ܣ�½���ڿ�ͨ�������Ƴ�����Ȧ���������ˡ���ý��ͷ��ȸ߼����ܣ�ּ�ڰ�����󿪷����ܹ������ڿ�ͨ��Դ��������ͳɱ�����ʵ������Ĳ�Ʒ��<br/>
�ڿ�ͨ����֧�ֵĵ����г��У��㶹�ԡ�360�ֻ����֡�Ӧ�ñ����ٶ��ֻ����֡�91���֡���׿�г���

## ���ڿ�Դ����ڿ�ͨ����Server˵����

    ����������Ҫ����Server��Java�汾��PHP�汾�ڿ�ͨ��Դ���룬ʹ��Eclipse�������ߴ򿪡�

### ��������˵�� <br>
#### 1������sql(MySql���ݿ�)
    �����ڿ�ͨʹ�õ����ݿ⣬�����ڿ�ͨ.sql��������Ӧ�û���
#### 2��ע�Ὺ�����˺�<br/>
    ȥ���ӻ�������ע�Ὺ�����˺ţ���¼�󴴽�Ӧ�ã���ÿͻ��˼��������Կ��<br>
    ע�Ὺ���������ĵ�,����ʣ�http://www.rongkecloud.com/document/<br>
    ���������ĵ�¼����ʣ�http://developer.rongkecloud.com/<br>
#### 1���޸������ļ�<br/>

##### PHP���������ļ��޸�<br>
    �޸�config���µ�rkdemo/sysconfig/sysConfig.ini�ļ��е����ݿ���ϢΪ�û��Լ������ݿ���Ϣ<br>
    MASTER_HOST=127.0.0.1   //DB������ ip��ַ<br>
    MASTER_USER=username   //DB�û�<br>
    MASTER_PASS=password    //DB��������<br>
    MASTER_NAME=rkcloud_test //DB����<br>
    MASTER_CHARSET=utf8<br>

##### Java���������ļ��޸�<br>
    �޸�WEB-INF/config/servlet.conf�����ļ�����Ҫ�޸ĵĲ��֣�<br>
    mysql.base.info = db_ip:3306,db_name,db_user,db_pwd<br>
    db_ip�滻Ϊ��Ҫʹ�õ����ݿ�ip��ַ<br>
    db_name�滻Ϊ��Ӧ����ʹ�õ����ݿ�����<br>
    db_user �滻Ϊ�������ݿ���û���<br>
    db_pwd �滻Ϊ�������ݿ������<br>
    Ӧ�������ӻ���ƽ̨�еġ�����������Կ����ͨ�������ӻ�������ע�Ὺ�����˺ţ�����Ӧ�ú���<br>
    rkcloudapi_serverkey= ��ע���serverkey<br>
    ͼƬ�ϴ���ʱĿ¼�����ú������ɶ���дȨ�ޡ�<br>
    �������ͷ���ϴ�ʧ�ܣ���ȷ�ϸ�Ŀ¼�Ƿ������Լ��Ƿ��ж�дȨ�ޡ�<br>
    image_temp=/upload<br>

#### 2���޸��ڿ�ͨ�������Կ <br>
##### PHP�����޸�
    �޸�RkCloudApi���µ�RkCloud_Config.ini�ļ��е�rkcloudapi_serverkeyΪ���ڿ����������д���Ӧ�õķ�������Կ��<br>

#### 3�����벿��<br>
##### PHP���벿��<br>
    ��PHP�汾��Դ��rkdemo��ѹ��HTTP��Ŀ¼����֤rkdemoĿ¼���ļ�����Ȩ�ޡ�<br>
    ����������ʣ�http://your-server-ip/rkdemo/login.php<br>
    ����:ret_code=9999��˵��api����ɹ���<br>

##### Java���벿��<br>
    ��rkdemo��Ŀ����.war�ļ��������ļ�����tomcat��������webappsĿ¼�£���֤rkdemoĿ¼���ļ�����Ȩ�ޡ�<br>
    ����������ʣ�http://your-server-ip:8080/rkdemo/login.php<br>
    ����:ret_code=9999��˵��api����ɹ���<br>

[Service Agreement(���ӻ���������ƽ̨����Э��)](http://www.rongkecloud.com/tecinfo/28.html)

[Contact us(��ϵ����)][serviceLink]

[serviceLink]: http://kefu.rongkecloud.com/RKServiceClientWeb/index.html?ek=6f2683bb7f9b98aa09283fd8b47f4086aec37b56&ct=1&bg=3&gd=143 "���߿ͷ�"
