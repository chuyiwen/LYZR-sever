CREATE TABLE IF NOT EXISTS game_storeLimit(
  `id` int(8) NOT NULL COMMENT 'id',
  `saveTime` long COMMENT '存储时间',
  `allLimitData` MEDIUMBLOB COMMENT '全服限购',
  `personalLimitData` MEDIUMBLOB COMMENT '全服限购',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPRESSED KEY_BLOCK_SIZE=16;