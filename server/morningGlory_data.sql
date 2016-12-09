/*
SQLyog 企业版 - MySQL GUI v8.14 
MySQL - 5.1.71 : Database - morningGlory_data_0.4.6
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`morningGlory_data` /*!40100 DEFAULT CHARACTER SET utf8 */;

USE `morningGlory_data`;

/*Table structure for table `Gmmail` */

DROP TABLE IF EXISTS `Gmmail`;

CREATE TABLE `Gmmail` (
  `mailId` varchar(36) COLLATE utf8_bin NOT NULL DEFAULT '',
  `title` varchar(32) COLLATE utf8_bin DEFAULT NULL,
  `content` varchar(1024) COLLATE utf8_bin DEFAULT NULL,
  `gold` int(4) DEFAULT NULL COMMENT '元宝',
  `coin` int(4) DEFAULT NULL,
  `item` varchar(1024) COLLATE utf8_bin DEFAULT NULL,
  `time` bigint(64) DEFAULT NULL,
  `minlevel` int(4) DEFAULT NULL,
  `maxlevel` int(4) DEFAULT NULL,
  `begintime` bigint(64) DEFAULT NULL,
  `endtime` bigint(64) DEFAULT NULL,
  `bindGold` int(4) DEFAULT NULL,
  PRIMARY KEY (`mailId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin ROW_FORMAT=COMPRESSED KEY_BLOCK_SIZE=16;

/*Table structure for table `blacklist` */

DROP TABLE IF EXISTS `blacklist`;

CREATE TABLE `blacklist` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `playerId` varchar(36) NOT NULL COMMENT '角色ID',
  `identityId` varchar(36) NOT NULL COMMENT '账号ID',
  `identityName` varchar(36) NOT NULL COMMENT '账号名',
  `name` varchar(36) NOT NULL COMMENT '角色名',
  `blockTime` date NOT NULL COMMENT '屏蔽时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPRESSED KEY_BLOCK_SIZE=16;

/*Table structure for table `game_auction` */

DROP TABLE IF EXISTS `game_auction`;

CREATE TABLE `game_auction` (
  `id` varchar(36) NOT NULL COMMENT '拍卖物id',
  `playerId` varchar(36) NOT NULL COMMENT '玩家playerId',
  `price` int(11) NOT NULL COMMENT '价格',
  `startTime` bigint(64) NOT NULL COMMENT '开始时间',
  `endTime` bigint(64) NOT NULL COMMENT '结束时间',
  `item` mediumblob NOT NULL COMMENT '拍卖物',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPRESSED KEY_BLOCK_SIZE=16;

/*Table structure for table `game_chatfriend` */

DROP TABLE IF EXISTS `game_chatfriend`;

CREATE TABLE `game_chatfriend` (
  `id` varchar(36) NOT NULL COMMENT '玩家id',
  `tempFriend` mediumblob COMMENT '临时好友',
  `friend` mediumblob COMMENT '好友',
  `black` mediumblob COMMENT '黑名单',
  `enmey` mediumblob COMMENT '仇人',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPRESSED KEY_BLOCK_SIZE=16;

/*Table structure for table `game_funstep` */

DROP TABLE IF EXISTS `game_funstep`;

CREATE TABLE `game_funstep` (
  `playerId` varchar(36) NOT NULL COMMENT '玩家Id',
  `funstep` mediumblob COMMENT '玩家新手记录',
  PRIMARY KEY (`playerId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPRESSED KEY_BLOCK_SIZE=16;

/*Table structure for table `game_giftcode` */

DROP TABLE IF EXISTS `game_giftcode`;

CREATE TABLE `game_giftcode` (
  `name` varchar(12) NOT NULL DEFAULT '' COMMENT '角色名',
  `keyCode` varchar(12) NOT NULL DEFAULT '' COMMENT '礼包码',
  `groupId` varchar(12) DEFAULT NULL,
  PRIMARY KEY (`name`,`keyCode`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPRESSED KEY_BLOCK_SIZE=16;

/*Table structure for table `game_instance` */

DROP TABLE IF EXISTS `game_instance`;

CREATE TABLE `game_instance` (
  `id` varchar(36) NOT NULL COMMENT '角色ID(uuid)',
  `identityId` varchar(60) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '账号ID',
  `refId` varchar(36) NOT NULL COMMENT '副本RefId',
  `data` blob COMMENT '玩家副本数据',
  PRIMARY KEY (`id`,`refId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPRESSED KEY_BLOCK_SIZE=16;

/*Table structure for table `game_ladder` */

DROP TABLE IF EXISTS `game_ladder`;

CREATE TABLE `game_ladder` (
  `id` varchar(36) NOT NULL COMMENT '天梯成员ID（UUID）',
  `memberData` mediumblob COMMENT '成员信息',
  `isDelete` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPRESSED KEY_BLOCK_SIZE=16;

/*Table structure for table `game_oldplayer` */

DROP TABLE IF EXISTS `game_oldplayer`;

CREATE TABLE `game_oldplayer` (
  `identityId` varchar(60) DEFAULT NULL COMMENT '玩家id'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPRESSED KEY_BLOCK_SIZE=16;

/*Table structure for table `game_resdownload` */

DROP TABLE IF EXISTS `game_resdownload`;

CREATE TABLE `game_resdownload` (
  `identityId` varchar(60) NOT NULL COMMENT '玩家identityId',
  `resDownload` mediumblob COMMENT '分包下载',
  PRIMARY KEY (`identityId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `game_sortboardData` */

DROP TABLE IF EXISTS `game_sortboardData`;

CREATE TABLE `game_sortboardData` (
  `id` int(8) NOT NULL COMMENT '排行榜ID',
  `sortboardData` mediumblob COMMENT '排行榜数据',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPRESSED KEY_BLOCK_SIZE=16;

/*Table structure for table `game_storeLimit` */

DROP TABLE IF EXISTS `game_storeLimit`;

CREATE TABLE `game_storeLimit` (
  `id` int(8) NOT NULL COMMENT 'id',
  `saveTime` mediumtext COMMENT '存储时间',
  `allLimitData` mediumblob COMMENT '全服限购',
  `personalLimitData` mediumblob COMMENT '全服限购',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPRESSED KEY_BLOCK_SIZE=16;

/*Table structure for table `game_union` */

DROP TABLE IF EXISTS `game_union`;

CREATE TABLE `game_union` (
  `id` varchar(36) NOT NULL COMMENT '公会id',
  `unionData` mediumblob NOT NULL COMMENT '公会数据',
  `memberData` mediumblob NOT NULL COMMENT '公会成员数据',
  `applyData` mediumblob NOT NULL COMMENT '申请列表数据',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPRESSED KEY_BLOCK_SIZE=16;

/*Table structure for table `mail` */

DROP TABLE IF EXISTS `mail`;

CREATE TABLE `mail` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `playerId` varchar(36) NOT NULL DEFAULT '' COMMENT '接受者id',
  `mailId` varchar(36) NOT NULL DEFAULT '',
  `Content` varchar(1024) DEFAULT NULL COMMENT '邮件内容',
  `gold` int(4) DEFAULT NULL COMMENT '元宝',
  `coin` int(4) DEFAULT NULL COMMENT '铜钱',
  `item` varchar(1024) DEFAULT NULL COMMENT '物品',
  `isRead` tinyint(1) DEFAULT NULL COMMENT '是否已读',
  `relateMailId` varchar(36) DEFAULT NULL,
  `time` bigint(64) DEFAULT NULL COMMENT '发送时间',
  `mailType` tinyint(4) DEFAULT NULL COMMENT '邮件类型',
  `bindGold` int(4) DEFAULT NULL COMMENT '绑定元宝',
  `title` varchar(32) DEFAULT NULL,
  `itemIns` mediumblob,
  PRIMARY KEY (`id`),
  KEY `mail_playerID` (`playerId`) USING BTREE,
  KEY `mail_mailId` (`mailId`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=234 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPRESSED KEY_BLOCK_SIZE=16;

/*Table structure for table `monitor` */

DROP TABLE IF EXISTS `monitor`;

CREATE TABLE `monitor` (
  `playerId` varchar(36) NOT NULL,
  `data` mediumblob COMMENT '玩家发送过来的消息字节流',
  PRIMARY KEY (`playerId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPRESSED KEY_BLOCK_SIZE=16;

/*Table structure for table `no_delay_player` */

DROP TABLE IF EXISTS `no_delay_player`;

CREATE TABLE `no_delay_player` (
  `id` varchar(36) NOT NULL COMMENT '角色ID(uuid)',
  `identityId` varchar(60) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '账号ID',
  `identityName` varchar(60) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '账号名',
  `name` varchar(12) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL COMMENT '昵称',
  `talismanData` mediumblob COMMENT '法宝',
  `achievementData` mediumblob COMMENT '成就',
  `buffData` mediumblob COMMENT 'buff',
  `states` blob COMMENT '状态（封号，禁言）',
  `vipData` blob COMMENT 'VIP',
  `activityData` mediumblob COMMENT '活动(签到，在线计时)',
  `digsData` mediumblob COMMENT '挖宝',
  `castleData` blob COMMENT '攻城战',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPRESSED KEY_BLOCK_SIZE=16;

/*Table structure for table `offline_ai` */

DROP TABLE IF EXISTS `offline_ai`;

CREATE TABLE `offline_ai` (
  `playerid` varchar(36) NOT NULL COMMENT '角色ID(uuid)',
  `dbData` mediumblob COMMENT '离线数据',
  PRIMARY KEY (`playerid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `player` */

DROP TABLE IF EXISTS `player`;

CREATE TABLE `player` (
  `id` varchar(36) NOT NULL COMMENT '角色ID(uuid)',
  `identityId` varchar(60) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '账号ID',
  `identityName` varchar(60) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '账号名称',
  `name` varchar(12) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '角色名称',
  `propertyData` blob NOT NULL COMMENT '角色属性',
  `itemBagData` mediumblob COMMENT '背包',
  `questData` mediumblob COMMENT '任务数据',
  `equipData` mediumblob COMMENT '装备',
  `skillData` mediumblob COMMENT '技能',
  `dailyQuestData` mediumblob COMMENT '日常任务',
  `mountData` mediumblob COMMENT '坐骑',
  `wingData` mediumblob COMMENT '翅膀',
  `peerageData` mediumblob COMMENT '爵位',
  `qdCode1` int(11) NOT NULL DEFAULT '5' COMMENT '主渠道',
  `qdCode2` int(11) NOT NULL DEFAULT '5' COMMENT '副渠道',
  `birthday` bigint(64) NOT NULL DEFAULT '0',
  `lastLoginTime` bigint(64) NOT NULL DEFAULT '0',
  `lastLogoutTime` bigint(64) NOT NULL DEFAULT '0',
  `level` int(11) NOT NULL,
  `depotData` mediumblob,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_name` (`name`),
  KEY `idx_identityId` (`identityId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPRESSED KEY_BLOCK_SIZE=16;

/* Procedure structure for procedure `newMail` */

/*!50003 DROP PROCEDURE IF EXISTS  `newMail` */;

DELIMITER $$

/*!50003 CREATE DEFINER=`root`@`%` PROCEDURE `newMail`(IN playerId VARCHAR(256),IN	mailId VARCHAR(256),IN sendername VARCHAR(64),IN title VARCHAR(32),IN Content VARCHAR(1024),IN gold INT(4),IN coin INT(4),IN item  VARCHAR(1024),IN isRead TINYINT(1),IN relateMailId INT(4),IN `TIME` BIGINT(64),IN isDelete TINYINT(1),IN isSystem TINYINT(1))
BEGIN
	INSERT INTO mail VALUES(playerId, mailId ,sendername, title  ,Content, gold ,coin, item ,isRead, relateMailId, `TIME`, isDelete, isSystem);
    END */$$
DELIMITER ;

/* Procedure structure for procedure `newNoDelayPlayer` */

/*!50003 DROP PROCEDURE IF EXISTS  `newNoDelayPlayer` */;

DELIMITER $$

/*!50003 CREATE DEFINER=`root`@`%` PROCEDURE `newNoDelayPlayer`(IN id VARCHAR(36), IN identityId VARCHAR(60), IN identityName VARCHAR(60),IN `name` VARCHAR(12), IN talismanData MEDIUMBLOB, IN achievementData MEDIUMBLOB , IN buffData MEDIUMBLOB, IN states BLOB, IN vipData BLOB, IN activityData MEDIUMBLOB,IN digsData MEDIUMBLOB,in castleData blob)
BEGIN
	INSERT INTO no_delay_player VALUES(id,identityId,identityName,`name`,talismanData, achievementData, buffData,states,vipData, activityData,digsData,castleData);
    END */$$
DELIMITER ;

/* Procedure structure for procedure `newOffline_ai` */

/*!50003 DROP PROCEDURE IF EXISTS  `newOffline_ai` */;

DELIMITER $$

/*!50003 CREATE DEFINER=`root`@`%` PROCEDURE `newOffline_ai`(IN playerid VARCHAR(36), IN dbData MEDIUMBLOB)
BEGIN
	INSERT INTO offline_ai VALUES(playerid,dbData);
    END */$$
DELIMITER ;

/* Procedure structure for procedure `newPlayer` */

/*!50003 DROP PROCEDURE IF EXISTS  `newPlayer` */;

DELIMITER $$

/*!50003 CREATE DEFINER=`root`@`%` PROCEDURE `newPlayer`(IN id VARCHAR(36), IN identityId VARCHAR(60), IN identityName VARCHAR(60), IN `name` VARCHAR(12),IN qdCode1 INT(11),IN qdCode2 INT(11),IN birthday BIGINT,IN lastLoginTime BIGINT,IN lastLogoutTime BIGINT,IN `level` INT(11) ,IN propertyData BLOB, IN itemBagData MEDIUMBLOB, IN questData MEDIUMBLOB, IN equipData MEDIUMBLOB, IN skillData BLOB, IN dailyQuestData MEDIUMBLOB, IN mountData MEDIUMBLOB, IN wingData MEDIUMBLOB, IN peerageData MEDIUMBLOB, IN depotData MEDIUMBLOB)
BEGIN
	INSERT INTO player(id,identityId, identityName, `name`, propertyData, itemBagData, questData, equipData, skillData, dailyQuestData, mountData, wingData, peerageData, qdCode1, qdCode2, birthday, lastLoginTime, lastLogoutTime, `level`, depotData) VALUES(id,identityId, identityName, `name`, propertyData, itemBagData, questData, equipData, skillData, dailyQuestData, mountData, wingData, peerageData, qdCode1, qdCode2, birthday, lastLoginTime, lastLogoutTime,`level`, depotData);
    END */$$
DELIMITER ;

/* Procedure structure for procedure `offline_ai` */

/*!50003 DROP PROCEDURE IF EXISTS  `offline_ai` */;

DELIMITER $$

/*!50003 CREATE DEFINER=`root`@`%` PROCEDURE `offline_ai`(IN playerid VARCHAR(36), IN dbData MEDIUMBLOB)
BEGIN
	UPDATE offline_ai SET dbData = dbData WHERE offline_ai.playerid = `playerid`;
    END */$$
DELIMITER ;

/* Procedure structure for procedure `showall` */

/*!50003 DROP PROCEDURE IF EXISTS  `showall` */;

DELIMITER $$

/*!50003 CREATE DEFINER=`root`@`%` PROCEDURE `showall`()
BEGIN
	select * from player;
    END */$$
DELIMITER ;

/* Procedure structure for procedure `updateNoDelayPlayer` */

/*!50003 DROP PROCEDURE IF EXISTS  `updateNoDelayPlayer` */;

DELIMITER $$

/*!50003 CREATE DEFINER=`root`@`%` PROCEDURE `updateNoDelayPlayer`(IN `id` VARCHAR(36), IN identityId VARCHAR(60), IN identityName VARCHAR(60),IN `name` VARCHAR(12), IN talismanData MEDIUMBLOB, IN achievementData MEDIUMBLOB, IN buffData MEDIUMBLOB, IN states BLOB,IN vipData BLOB,IN activityData MEDIUMBLOB,IN digsData MEDIUMBLOB,IN castleData BLOB)
BEGIN
	DECLARE number INT DEFAULT 0;
	SELECT COUNT(*) INTO number FROM no_delay_player WHERE no_delay_player.id = `id` LIMIT 1;
	IF number = 0 THEN
		INSERT INTO no_delay_player VALUES(id,identityId,identityName,`name`,talismanData, achievementData, buffData,states,vipData, activityData,digsData,castleData);	
	ELSE
		UPDATE no_delay_player SET  talismanData = talismanData, achievementData = achievementData,buffData = buffData,states = states,vipData = vipData , activityData = activityData, digsData = digsData ,castleData = castleData WHERE no_delay_player.id = `id`;
	END IF;
    END */$$
DELIMITER ;

/* Procedure structure for procedure `updateOffline_ai` */

/*!50003 DROP PROCEDURE IF EXISTS  `updateOffline_ai` */;

DELIMITER $$

/*!50003 CREATE DEFINER=`root`@`%` PROCEDURE `updateOffline_ai`(IN dbData MEDIUMBLOB,IN playerid VARCHAR(36))
BEGIN
	UPDATE offline_ai SET  dbData = dbData WHERE offline_ai.playerid = playerid;
END */$$
DELIMITER ;

/* Procedure structure for procedure `updatePlayer` */

/*!50003 DROP PROCEDURE IF EXISTS  `updatePlayer` */;

DELIMITER $$

/*!50003 CREATE DEFINER=`root`@`%` PROCEDURE `updatePlayer`(IN `id` VARCHAR(36), IN identityId VARCHAR(60), IN identityName VARCHAR(60), IN `name` VARCHAR(12),IN qdCode1 INT(11),IN qdCode2 INT(11),IN birthday BIGINT,IN lastLoginTime BIGINT,IN lastLogoutTime BIGINT,IN `level` INT(11) , IN propertyData BLOB, IN itemBagData MEDIUMBLOB, IN questData MEDIUMBLOB, IN equipData MEDIUMBLOB, IN skillData BLOB, IN dailyQuestData MEDIUMBLOB, IN mountData MEDIUMBLOB, IN wingData MEDIUMBLOB, IN peerageData MEDIUMBLOB, IN depotData MEDIUMBLOB)
BEGIN
	UPDATE player SET propertyData = propertyData, itemBagData = itemBagData, questData = questData, equipData = equipData, skillData = skillData , dailyQuestData = dailyQuestData, mountData = mountData, wingData = wingData, peerageData = peerageData, qdCode1 = qdCode1, qdCode2 = qdCode2, birthday = birthday, lastLoginTime = lastLoginTime, lastLogoutTime = lastLogoutTime, `level`=`level`,depotData = depotData WHERE player.id = `id`;
    END */$$
DELIMITER ;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
