/*
SQLyog Ultimate v11.24 (32 bit)
MySQL - 5.5.45 : Database - rkcloud_test
*********************************************************************
*/


/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`rkcloud_test` /*!40100 DEFAULT CHARACTER SET utf8 */;

USE `rkcloud_test`;

/*Table structure for table `feedbacks` */

DROP TABLE IF EXISTS `feedbacks`;

CREATE TABLE `feedbacks` (
  `type` int(11) NOT NULL DEFAULT '0' COMMENT '��������id��1.������⣬2.�������⣬3.��������',
  `user_account` varchar(50) NOT NULL DEFAULT '' COMMENT '�˺�����',
  `content` varchar(500) CHARACTER SET utf8mb4 NOT NULL DEFAULT '' COMMENT '�����������',
  `created` datetime NOT NULL COMMENT '����ʱ��'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `updateinfo` */

DROP TABLE IF EXISTS `updateinfo`;

CREATE TABLE `updateinfo` (
  `os_type` varchar(50) NOT NULL DEFAULT '' COMMENT '����ϵͳ���ͣ��磺android��iphone',
  `download_url` varchar(100) DEFAULT NULL COMMENT '���ص�ַ',
  `update_version` varchar(10) NOT NULL DEFAULT '' COMMENT '�����汾��',
  `update_description` varchar(255) DEFAULT NULL COMMENT '������������',
  `upload_date` datetime DEFAULT NULL COMMENT '��������',
  `min_version` varchar(10) DEFAULT NULL COMMENT '��Ͱ汾��Ҫ��',
  `file_name` varchar(100) DEFAULT NULL COMMENT '�ļ�����',
  `file_size` int(10) unsigned DEFAULT NULL COMMENT '�ļ���С����λ���ֽ�',
  PRIMARY KEY (`os_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='�汾���±�';

/*Table structure for table `user_accounts` */

DROP TABLE IF EXISTS `user_accounts`;

CREATE TABLE `user_accounts` (
  `user_account` varchar(20) NOT NULL DEFAULT '' COMMENT '�˺�����',
  `user_pwd` varchar(50) NOT NULL DEFAULT '' COMMENT '����',
  `sdk_pwd` varchar(50) NOT NULL COMMENT '���ӻ�����¼����',
  `session` varchar(50) DEFAULT NULL COMMENT '��¼session',
  `user_type` int(11) NOT NULL DEFAULT '1' COMMENT '�û����� 1:normal 2:enterprise',
  `name` varchar(50) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '����',
  `sex` int(11) DEFAULT '0' COMMENT '�Ա� 1:�� 2��Ů',
  `address` varchar(100) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '��ַ',
  `email` varchar(50) DEFAULT NULL COMMENT '����',
  `mobile` varchar(20) DEFAULT NULL COMMENT '�ֻ�����',
  `permission_validation` int(11) DEFAULT '1' COMMENT '����Ϊ���ѵ�Ȩ����֤��1.��Ҫ��֤��2.����Ҫ��֤',
  `info_version` int(11) DEFAULT '0' COMMENT '��Ϣ�汾��',
  `avatar_version` int(11) DEFAULT '0' COMMENT 'ͷ��汾��',
  `created` timestamp NULL DEFAULT NULL COMMENT '����ʱ��',
  `updated` datetime DEFAULT NULL COMMENT '����ʱ��',
  PRIMARY KEY (`user_account`),
  UNIQUE KEY `session` (`session`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `user_avatars` */

DROP TABLE IF EXISTS `user_avatars`;

CREATE TABLE `user_avatars` (
  `user_account` varchar(50) NOT NULL DEFAULT '' COMMENT '�˺�����',
  `user_avatar` mediumblob COMMENT 'ͷ��',
  `user_avatar_thumb` blob COMMENT 'ͷ������ͼ',
  `updated` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_account`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `user_friends` */

DROP TABLE IF EXISTS `user_friends`;

CREATE TABLE `user_friends` (
  `user_account` varchar(50) NOT NULL DEFAULT '' COMMENT '�˺�����',
  `friend_account` varchar(50) NOT NULL DEFAULT '' COMMENT '�����˺�����',
  `gid` int(11) NOT NULL DEFAULT '0' COMMENT '��id',
  `friend_remark` varchar(50) CHARACTER SET utf8mb4 NOT NULL DEFAULT '' COMMENT '���ѱ�ע',
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_account`,`friend_account`),
  KEY `gid` (`user_account`,`gid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `user_groups` */

DROP TABLE IF EXISTS `user_groups`;

CREATE TABLE `user_groups` (
  `gid` int(11) NOT NULL AUTO_INCREMENT COMMENT '����id',
  `user_account` varchar(50) NOT NULL DEFAULT '' COMMENT '�˺�����',
  `group_name` varchar(50) CHARACTER SET utf8mb4 NOT NULL DEFAULT '' COMMENT '������',
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '����ʱ��',
  `updated` datetime DEFAULT NULL COMMENT '����ʱ��',
  PRIMARY KEY (`gid`),
  KEY `user_account` (`user_account`)
) ENGINE=InnoDB AUTO_INCREMENT=467 DEFAULT CHARSET=utf8;

/* Trigger structure for table `user_avatars` */

DELIMITER $$

USE `rkcloud_test`$$

DROP TRIGGER /*!50032 IF EXISTS */ `trg_user_avatar_insert`$$
CREATE
    TRIGGER `trg_user_avatar_insert` AFTER INSERT ON `user_avatars` 
    FOR EACH ROW BEGIN
	UPDATE user_accounts SET avatar_version=avatar_version+1 WHERE user_account = new.user_account;
    END;
$$
DELIMITER ;

/* Trigger structure for table `user_avatars` */

DELIMITER $$

USE `rkcloud_test`$$

DROP TRIGGER /*!50032 IF EXISTS */ `trg_user_avatar_update`$$

CREATE
    TRIGGER `trg_user_avatar_update` AFTER UPDATE ON `user_avatars` 
    FOR EACH ROW BEGIN
	UPDATE user_accounts SET avatar_version=avatar_version+1 WHERE user_account = old.user_account;
    END;
$$
DELIMITER ;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
