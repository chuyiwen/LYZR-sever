CREATE TABLE IF NOT EXISTS blacklist (
  `id` INT NOT NULL AUTO_INCREMENT, 
  `playerId` varchar(36) NOT NULL COMMENT '角色ID',
  `identityId` varchar(60) NOT NULL COMMENT '账号ID',
  `identityName` varchar(60) NOT NULL COMMENT "账号名",
  `name` varchar(36) NOT NULL COMMENT "角色名",
  `blockTime` date NOT NULL COMMENT "屏蔽时间",
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPRESSED KEY_BLOCK_SIZE=16;