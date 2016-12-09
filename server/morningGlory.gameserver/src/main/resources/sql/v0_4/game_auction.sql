CREATE TABLE IF NOT EXISTS game_auction(
  `id` varchar(36) NOT NULL COMMENT '拍卖物id',
  `playerId` VARCHAR(36) NOT NULL COMMENT '玩家playerId',
  price int(11) NOT NULL COMMENT '价格',
  startTime bigint(64) NOT NULL COMMENT '开始时间',
  endTime bigint(64) NOT NULL COMMENT '结束时间',
  `item` MEDIUMBLOB NOT NULL COMMENT '拍卖物',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPRESSED KEY_BLOCK_SIZE=16;