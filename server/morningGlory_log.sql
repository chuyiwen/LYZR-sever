/*
SQLyog 企业版- MySQL GUI v8.14 
MySQL - 5.1.71 : Database - morningGlory_log
*********************************************************************
*/
/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`morningGlory_log` /*!40100 DEFAULT CHARACTER SET utf8 */;

USE `morningGlory_log`;

/*Table structure for table `stat_player_log_2014-06-17` */



/*Table structure for table `stat_player_log_template` */

DROP TABLE IF EXISTS `stat_player_log_template`;

CREATE TABLE `stat_player_log_template` (
  `index` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `date` date NOT NULL,
  `time` time NOT NULL,
  `qdCode1` int(11) NOT NULL,
  `qdCode2` int(11) NOT NULL,
  `playerId` varchar(36) COLLATE utf8_bin NOT NULL,
  `playerName` varchar(12) COLLATE utf8_bin NOT NULL,
  `identityName` varchar(60) COLLATE utf8_bin NOT NULL,
  `statType` tinyint(4) NOT NULL,
  `n1` int(11) NOT NULL,
  `n2` bigint(20) DEFAULT NULL,
  `n3` bigint(20) DEFAULT NULL,
  `n4` bigint(20) DEFAULT NULL,
  `n5` bigint(20) DEFAULT NULL,
  `n6` bigint(20) DEFAULT NULL,
  `n7` bigint(20) DEFAULT NULL,
  `n8` bigint(20) DEFAULT NULL,
  `n9` bigint(20) DEFAULT NULL,
  `n10` bigint(20) DEFAULT NULL,
  `s1` varchar(128) COLLATE utf8_bin DEFAULT NULL,
  `s2` varchar(128) COLLATE utf8_bin DEFAULT NULL,
  `s3` varchar(128) COLLATE utf8_bin DEFAULT NULL,
  `s4` varchar(128) COLLATE utf8_bin DEFAULT NULL,
  `s5` varchar(128) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`index`),
  KEY `idx_date` (`date`),
  KEY `idx_time` (`time`),
  KEY `idx_qdCode` (`qdCode1`,`qdCode2`),
  KEY `idx_identityName` (`identityName`),
  KEY `idx_playerName` (`playerName`),
  KEY `idx_type_iName` (`statType`,`identityName`),
  KEY `idx_type_name` (`statType`,`playerName`),
  KEY `idx_type_n1` (`statType`,`n1`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

/*Table structure for table `stat_player_online_log` */

DROP TABLE IF EXISTS `stat_player_online_log`;

CREATE TABLE `stat_player_online_log` (
  `year` int(4) NOT NULL,
  `month` tinyint(2) NOT NULL,
  `day` tinyint(2) NOT NULL,
  `hour` tinyint(2) NOT NULL,
  `minute` tinyint(2) NOT NULL,
  `total` int(11) DEFAULT NULL,
  `total_inc` int(11) NOT NULL COMMENT '新增角色总数',
  `online` int(11) DEFAULT NULL,
  `connected` int(11) NOT NULL DEFAULT '0',
  `connected_ips` int(11) NOT NULL DEFAULT '0',
  `logged` int(11) NOT NULL DEFAULT '0',
  `logged_uids` int(11) NOT NULL DEFAULT '0',
  `entered` int(11) NOT NULL DEFAULT '0',
  `entered_uids` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`year`,`month`,`day`,`hour`,`minute`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

/*Table structure for table `stat_player_recharge_log` */

DROP TABLE IF EXISTS `stat_player_recharge_log`;

CREATE TABLE `stat_player_recharge_log` (
  `id` varchar(36) COLLATE utf8_bin DEFAULT NULL COMMENT '玩家ID',
  `playerName` varchar(100) COLLATE utf8_bin DEFAULT '',
  `game_gold` int(16) DEFAULT NULL COMMENT '本次充值元宝数',
  `pay_money` decimal(10,2) DEFAULT NULL COMMENT '本次充值金额',
  `pay_time` timestamp NULL DEFAULT NULL COMMENT '充值时间',
  `qdCode1` int(11) NOT NULL DEFAULT '5' COMMENT '主渠道',
  `qdCode2` int(11) NOT NULL DEFAULT '5' COMMENT '副渠道',
  `identityName` varchar(60) COLLATE utf8_bin NOT NULL,
  KEY `idx_identityName` (`identityName`),
  KEY `idx_playerName` (`playerName`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='玩家充值统计，author:cjz';

/*Table structure for table `stat_tables` */

DROP TABLE IF EXISTS `stat_tables`;

CREATE TABLE `stat_tables` (
  `date` date NOT NULL,
  PRIMARY KEY (`date`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

/* Procedure structure for procedure `insert_recharge` */

/*!50003 DROP PROCEDURE IF EXISTS  `insert_recharge` */;

DELIMITER $$

/*!50003 CREATE DEFINER=`root`@`%` PROCEDURE `insert_recharge`(IN playerId VARCHAR(36),IN identityName VARCHAR(36),IN playerName VARCHAR(12),IN qdCode1 INT(11),IN qdCode2 INT(11), IN game_gold INT,IN pay_money INT,IN pay_time TIMESTAMP)
BEGIN
	INSERT INTO stat_player_recharge_log(id,identityName,playerName ,qdCode1,qdCode2,game_gold, pay_money, pay_time) VALUES(playerId, identityName,playerName ,qdCode1,qdCode2,game_gold, pay_money ,pay_time);
    END */$$
DELIMITER ;

/* Procedure structure for procedure `insert_stat` */

/*!50003 DROP PROCEDURE IF EXISTS  `insert_stat` */;

DELIMITER $$

/*!50003 CREATE DEFINER=`root`@`localhost` PROCEDURE `insert_stat`(_date DATE ,_time TIME ,_qdCode1 INT,_qdCode2 INT, _playerId VARCHAR(128), _playerName VARCHAR(128),_identityName VARCHAR(128), _statType TINYINT, _n1 INT, _n2 BIGINT, _n3 BIGINT, _n4 BIGINT, _n5 BIGINT,_n6 BIGINT, _n7 BIGINT, _n8 BIGINT, _n9 BIGINT, _n10 BIGINT, _s1 VARCHAR(128), _s2 VARCHAR(128), _s3 VARCHAR(128), _s4 VARCHAR(128), _s5 VARCHAR(128) )
BEGIN
	DECLARE _created BOOLEAN; /** 表是否已经创建*/
	DECLARE _table VARCHAR(36); /** 表名 */
	SET _created = FALSE;
	SET _table = CONCAT('`stat_player_log_', _date ,'`');
	
	/** 创建表*/
	SELECT TRUE INTO _created FROM stat_tables WHERE `date` = _date;
	IF( _created = FALSE ) THEN
		SET @_sql =CONCAT('CREATE TABLE IF NOT EXISTS' ,_table,'(
			  `index` int(10) unsigned NOT NULL AUTO_INCREMENT,
			  `date` date NOT NULL,
			  `time` time NOT NULL,
			  `qdCode1` int(11) NOT NULL,
			  `qdCode2` int(11) NOT NULL,
			  `playerId` varchar(36) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
			  `playerName` varchar(12) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
			  `identityName` varchar(36) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
			  `statType` tinyint(4) NOT NULL,
			  `n1` int(11) NOT NULL,
			  `n2` bigint(20) DEFAULT NULL,
			  `n3` bigint(20) DEFAULT NULL,
			  `n4` bigint(20) DEFAULT NULL,
			  `n5` bigint(20) DEFAULT NULL,
			  `n6` bigint(20) DEFAULT NULL,
			  `n7` bigint(20) DEFAULT NULL,
			  `n8` bigint(20) DEFAULT NULL,
			  `n9` bigint(20) DEFAULT NULL,
			  `n10` bigint(20) DEFAULT NULL,
			  `s1` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
			  `s2` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
			  `s3` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
			  `s4` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
			  `s5` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
			  PRIMARY KEY (`index`),
			  KEY `idx_date` (`date`),
			  KEY `idx_time` (`time`),
			  KEY `idx_qdCode` (`qdCode1`,`qdCode2`),
			  KEY `idx_identityName` (`identityName`),
			  KEY `idx_type_iName` (`statType`,`identityName`),
			  KEY `idx_type_name` (`statType`,`playerName`),
			  KEY `idx_playerName` (`playerName`),
			  KEY `idx_type_n1` (`statType`,`n1`)
			) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin ROW_FORMAT=COMPRESSED KEY_BLOCK_SIZE=16;');
		PREPARE SSQL FROM @_sql;
		EXECUTE SSQL;
		DEALLOCATE PREPARE SSQL;
		INSERT INTO stat_tables(`date`)VALUES(_date);
	END IF;
	
        SET @_date = _date;
	SET @_time = _time;
	SET @_qdCode1 = _qdCode1;
	SET @_qdCode2 = _qdCode2;
	SET @_playerId = _playerId;
	SET @_playerName = _playerName;
	SET @_identityName = _identityName;
	SET @_statType = _statType;
	SET @_n1 = _n1;
	SET @_n2 = _n2;
	SET @_n3 = _n3;
	SET @_n4 = _n4;
	SET @_n5 = _n5;
	SET @_n6 = _n6;
	SET @_n7 = _n7;
	SET @_n8 = _n8;
	SET @_n9 = _n9;
	SET @_n10 = _n10;
	SET @_s1 = _s1;
	SET @_s2 = _s2;
	SET @_s3 = _s3;
	SET @_s4 = _s4;
	SET @_s5 = _s5;
	SET @_sql = CONCAT( 'insert into ', _table , ' ( `date`,`time`,`qdCode1`,`qdCode2`,`playerId`,`playerName`,`identityName`,statType,n1,n2,n3,n4,n5,n6,n7,n8,n9,n10,s1,s2,s3,s4,s5 ) values(?,?,?,?,?,?,?,?,?,?, ?,?,?,?,?,?,?,?,?,?, ?,?,?) ');
	PREPARE SSQL FROM @_sql;
	EXECUTE SSQL USING @_date,@_time,@_qdCode1,@_qdCode2,@_playerId,@_playerName,@_identityName,@_statType, @_n1,@_n2,@_n3,@_n4,@_n5,@_n6,@_n7,@_n8,@_n9,@_n10, @_s1,@_s2,@_s3,@_s4,@_s5;
	DEALLOCATE PREPARE SSQL;
    END */$$
DELIMITER ;

/* Procedure structure for procedure `is_exist_table` */

/*!50003 DROP PROCEDURE IF EXISTS  `is_exist_table` */;

DELIMITER $$

/*!50003 CREATE DEFINER=`root`@`localhost` PROCEDURE `is_exist_table`(IN `the_date` DATE,OUT `is_exist` BOOLEAN)
BEGIN		DECLARE tag INT;	SELECT COUNT(1) INTO tag FROM stat_tables WHERE DATE = the_date;		IF tag > 0 THEN		SET is_exist = TRUE;	ELSE		SET is_exist = FALSE;	END IF;END */$$
DELIMITER ;

/* Procedure structure for procedure `preserve_statistics` */

/*!50003 DROP PROCEDURE IF EXISTS  `preserve_statistics` */;

DELIMITER $$

/*!50003 CREATE DEFINER=`root`@`localhost` PROCEDURE `preserve_statistics`(IN `begin_date` DATE,IN `end_date` DATE,IN `qdCode1` INT,IN `qdCode2` INT,`channelStr` VARCHAR(9999))
BEGIN			DECLARE created_date DATE;		DECLARE created_count INT;		DECLARE max_online_count INT;		DECLARE next_day_retention VARCHAR(50);		DECLARE two_day_retention VARCHAR(50);		DECLARE three_day_retention VARCHAR(50);		DECLARE four_day_retention VARCHAR(50);		DECLARE five_day_retention VARCHAR(50);		DECLARE six_day_retention VARCHAR(50);		DECLARE seven_day_retention VARCHAR(50);		DECLARE fourteen_day_retention VARCHAR(50);		DECLARE thirty_day_retention VARCHAR(50);	 	DECLARE sixty_day_retention VARCHAR(50);		DECLARE ninety_day_retention VARCHAR(50);		DECLARE pre_table_name VARCHAR(20);			DECLARE is_exist BOOLEAN;		DECLARE next_date DATE;		DECLARE the_retention VARCHAR(50);		CREATE TEMPORARY TABLE IF NOT EXISTS preserve_statistics_tem(		created_date DATE,		created_count INT DEFAULT 0,		max_online_count INT DEFAULT 0,		next_day_retention VARCHAR(50) DEFAULT '',		two_day_retention VARCHAR(50) DEFAULT '',		three_day_retention VARCHAR(50) DEFAULT '',		four_day_retention VARCHAR(50) DEFAULT '',		five_day_retention VARCHAR(50) DEFAULT '',		six_day_retention VARCHAR(50) DEFAULT '',		seven_day_retention VARCHAR(50) DEFAULT '',		fourteen_day_retention VARCHAR(50) DEFAULT '',		thirty_day_retention VARCHAR(50) DEFAULT '',		sixty_day_retention VARCHAR(50) DEFAULT '',		ninety_day_retention VARCHAR(50) DEFAULT ''	) ENGINE = INNODB DEFAULT CHARSET = utf8;		SET pre_table_name = "stat_player_log_";		WHILE DATEDIFF(end_date,begin_date) >= 0 DO				SET created_date = begin_date;				CALL is_exist_table(begin_date,is_exist);				IF is_exist = TRUE THEN				SET @create_count_sql = CONCAT("select count(1) into @created_count from `",CONCAT(pre_table_name,DATE_FORMAT(begin_date,'%Y-%m-%d')),"` where statType = 3");		IF qdCode1 <> 0 THEN			SET @create_count_sql = CONCAT(@create_count_sql," and qdCode1 = ",qdCode1);		END IF;		IF qdCode2 <> 0 THEN			SET @create_count_sql = CONCAT(@create_count_sql," and qdCode2 = ",qdCode2);		END IF;		PREPARE create_count_sql FROM @create_count_sql;		EXECUTE create_count_sql;		SET created_count = @created_count;		ELSE			SET created_count = 0;		END IF;				SET @YEAR = YEAR(begin_date);		SET @MONTH = MONTH(begin_date);		SET @DAY = DAY(begin_date);		SET @get_max_online_count_sql = "select max(online) into @max_online_count from stat_player_online_log  where year = ? and month = ? and day = ?";		PREPARE get_max_online_count_sql FROM @get_max_online_count_sql;		EXECUTE get_max_online_count_sql USING @YEAR,@MONTH,@DAY;		SET max_online_count = @max_online_count;		IF(is_exist = FALSE OR created_count IS NULL OR created_count = 0) THEN						INSERT INTO preserve_statistics_tem(created_date,max_online_count) VALUES (created_date,max_online_count);		ELSE						SET next_date = ADDDATE(begin_date,INTERVAL 1 DAY);			CALL retention(begin_date,next_date,created_count,qdCode1,qdCode2,channelStr,the_retention);			SET next_day_retention = the_retention;			SET next_date = ADDDATE(next_date,INTERVAL 1 DAY);			CALL retention(begin_date,next_date,created_count,qdCode1,qdCode2,channelStr,the_retention);			SET two_day_retention = the_retention;			SET next_date = ADDDATE(next_date,INTERVAL 1 DAY);			CALL retention(begin_date,next_date,created_count,qdCode1,qdCode2,channelStr,the_retention);			SET three_day_retention = the_retention;			SET next_date = ADDDATE(next_date,INTERVAL 1 DAY);			CALL retention(begin_date,next_date,created_count,qdCode1,qdCode2,channelStr,the_retention);			SET four_day_retention = the_retention;			SET next_date = ADDDATE(next_date,INTERVAL 1 DAY);			CALL retention(begin_date,next_date,created_count,qdCode1,qdCode2,channelStr,the_retention);			SET five_day_retention = the_retention;			SET next_date = ADDDATE(next_date,INTERVAL 1 DAY);			CALL retention(begin_date,next_date,created_count,qdCode1,qdCode2,channelStr,the_retention);			SET six_day_retention = the_retention;			SET next_date = ADDDATE(next_date,INTERVAL 1 DAY);			CALL retention(begin_date,next_date,created_count,qdCode1,qdCode2,channelStr,the_retention);			SET seven_day_retention = the_retention;			SET next_date = ADDDATE(next_date,INTERVAL 7 DAY);			CALL retention(begin_date,next_date,created_count,qdCode1,qdCode2,channelStr,the_retention);			SET fourteen_day_retention = the_retention;			SET next_date = ADDDATE(next_date,INTERVAL 16 DAY);			CALL retention(begin_date,next_date,created_count,qdCode1,qdCode2,channelStr,the_retention);			SET thirty_day_retention = the_retention;			SET next_date = ADDDATE(next_date,INTERVAL 30 DAY);			CALL retention(begin_date,next_date,created_count,qdCode1,qdCode2,channelStr,the_retention);			SET sixty_day_retention = the_retention;			SET next_date = ADDDATE(next_date,INTERVAL 30 DAY);			CALL retention(begin_date,next_date,created_count,qdCode1,qdCode2,channelStr,the_retention);			SET ninety_day_retention = the_retention;						INSERT INTO preserve_statistics_tem VALUES(created_date,created_count,max_online_count,next_day_retention,two_day_retention,three_day_retention,four_day_retention,five_day_retention,six_day_retention,seven_day_retention,fourteen_day_retention,thirty_day_retention,sixty_day_retention,ninety_day_retention);		END IF;				SELECT ADDDATE(begin_date,INTERVAL 1 DAY) INTO begin_date;	END WHILE;		SELECT * FROM preserve_statistics_tem;	TRUNCATE TABLE preserve_statistics_tem;	DROP TABLE preserve_statistics_tem;END */$$
DELIMITER ;

/* Procedure structure for procedure `retention` */

/*!50003 DROP PROCEDURE IF EXISTS  `retention` */;

DELIMITER $$

/*!50003 CREATE DEFINER=`root`@`localhost` PROCEDURE `retention`(IN `cur_date` DATE,IN `after_date` DATE,IN `created_count` INT,IN `qdCode1` INT,IN `qdCode2` INT,IN `channelStr` VARCHAR(9999),OUT `the_retention` VARCHAR(50))
BEGIN
	
	
	DECLARE is_exist BOOLEAN;
	
	DECLARE pre_table_name VARCHAR(50);
	SET pre_table_name = "stat_player_log_";
	
	CALL is_exist_table(after_date,is_exist);
	
	IF is_exist = TRUE THEN
		
		
		
		
		SET @rate_sql = CONCAT("select count(distinct a.playerId) into @tem_retention from `",CONCAT(pre_table_name,DATE_FORMAT(after_date,'%Y-%m-%d')), "`a left join `",CONCAT(pre_table_name,DATE_FORMAT(cur_date,'%Y-%m-%d')),"` c on a.playerName = c.playerName where a.statType = 2 and c.statType = 3");
		
		IF qdCode1 <> 0 THEN
			SET @rate_sql = CONCAT(@rate_sql," and c.qdCode1 = ",qdCode1);
		END IF;
		IF qdCode2 <> 0 THEN
			SET @rate_sql = CONCAT(@rate_sql," and c.qdCode2 = ",qdCode2);
		END IF;
		IF channelStr <> '' THEN
		SET @rate_sql = CONCAT(@rate_sql," and c.qdCode1 in(",channelStr,")");
		SET @rate_sql = CONCAT(@rate_sql," and c.qdCode2 in(",channelStr,")");
		END IF;
		PREPARE rate_sql FROM @rate_sql;
		EXECUTE rate_sql;
		
		SET the_retention = CONCAT(@tem_retention,',',FORMAT((@tem_retention*100)/created_count,2),'%');
	ELSE
		SET the_retention = '';
	END IF;
END */$$
DELIMITER ;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
