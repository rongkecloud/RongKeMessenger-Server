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
  `type` int(11) NOT NULL DEFAULT '0' COMMENT '问题类型id：1.软件问题，2.界面问题，3.其他问题',
  `user_account` varchar(50) NOT NULL DEFAULT '' COMMENT '账号名称',
  `content` varchar(500) CHARACTER SET utf8mb4 NOT NULL DEFAULT '' COMMENT '意见反馈内容',
  `created` datetime NOT NULL COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `updateinfo` */

DROP TABLE IF EXISTS `updateinfo`;

CREATE TABLE `updateinfo` (
  `os_type` varchar(50) NOT NULL DEFAULT '' COMMENT '操作系统类型，如：android、iphone',
  `download_url` varchar(100) DEFAULT NULL COMMENT '下载地址',
  `update_version` varchar(10) NOT NULL DEFAULT '' COMMENT '升级版本号',
  `update_description` varchar(255) DEFAULT NULL COMMENT '升级内容描述',
  `upload_date` datetime DEFAULT NULL COMMENT '升级日期',
  `min_version` varchar(10) DEFAULT NULL COMMENT '最低版本号要求',
  `file_name` varchar(100) DEFAULT NULL COMMENT '文件名称',
  `file_size` int(10) unsigned DEFAULT NULL COMMENT '文件大小，单位：字节',
  PRIMARY KEY (`os_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='版本更新表';

/*Table structure for table `user_accounts` */

DROP TABLE IF EXISTS `user_accounts`;

CREATE TABLE `user_accounts` (
  `user_account` varchar(20) NOT NULL DEFAULT '' COMMENT '账号名称',
  `user_pwd` varchar(50) NOT NULL DEFAULT '' COMMENT '密码',
  `sdk_pwd` varchar(50) NOT NULL COMMENT '云视互动登录密码',
  `session` varchar(50) DEFAULT NULL COMMENT '登录session',
  `user_type` int(11) NOT NULL DEFAULT '1' COMMENT '用户类型 1:normal 2:enterprise',
  `name` varchar(50) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '姓名',
  `sex` int(11) DEFAULT '0' COMMENT '性别 1:男 2：女',
  `address` varchar(100) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '地址',
  `email` varchar(50) DEFAULT NULL COMMENT '邮箱',
  `mobile` varchar(20) DEFAULT NULL COMMENT '手机号码',
  `permission_validation` int(11) DEFAULT '1' COMMENT '加我为好友的权限验证：1.需要验证，2.不需要验证',
  `info_version` int(11) DEFAULT '0' COMMENT '信息版本号',
  `avatar_version` int(11) DEFAULT '0' COMMENT '头像版本号',
  `created` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  `updated` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`user_account`),
  UNIQUE KEY `session` (`session`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `user_avatars` */

DROP TABLE IF EXISTS `user_avatars`;

CREATE TABLE `user_avatars` (
  `user_account` varchar(50) NOT NULL DEFAULT '' COMMENT '账号名称',
  `user_avatar` mediumblob COMMENT '头像',
  `user_avatar_thumb` blob COMMENT '头像缩略图',
  `updated` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_account`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `user_friends` */

DROP TABLE IF EXISTS `user_friends`;

CREATE TABLE `user_friends` (
  `user_account` varchar(50) NOT NULL DEFAULT '' COMMENT '账号名称',
  `friend_account` varchar(50) NOT NULL DEFAULT '' COMMENT '好友账号名称',
  `gid` int(11) NOT NULL DEFAULT '0' COMMENT '组id',
  `friend_remark` varchar(50) CHARACTER SET utf8mb4 NOT NULL DEFAULT '' COMMENT '好友备注',
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_account`,`friend_account`),
  KEY `gid` (`user_account`,`gid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `user_groups` */

DROP TABLE IF EXISTS `user_groups`;

CREATE TABLE `user_groups` (
  `gid` int(11) NOT NULL AUTO_INCREMENT COMMENT '分组id',
  `user_account` varchar(50) NOT NULL DEFAULT '' COMMENT '账号名称',
  `group_name` varchar(50) CHARACTER SET utf8mb4 NOT NULL DEFAULT '' COMMENT '组名称',
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated` datetime DEFAULT NULL COMMENT '更新时间',
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
