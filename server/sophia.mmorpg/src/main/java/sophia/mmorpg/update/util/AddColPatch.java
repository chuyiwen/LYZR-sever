package sophia.mmorpg.update.util;

import org.apache.log4j.Logger;

import sophia.game.persistence.ConnectionManager;
import sophia.mmorpg.update.DBTypeDefine;
import sophia.mmorpg.update.DBUpdateBasePatch;
import sophia.stat.ConnectionFactory;
/**
 * Copyright (c) 2014 by 游爱.
 * 
 * @version 1.0
 */
public class AddColPatch extends DBUpdateBasePatch {
	private final static Logger logger = Logger.getLogger(AddColPatch.class);

	public boolean upgrade(UpData colData,String dbType) throws Exception {
		ColUpData cud = (ColUpData) colData;
		try {
			String tableName = cud.getTableName();
			String colName = cud.getColName();

			// boolean succ = ;// 默认为成功
			if(dbType.endsWith(DBTypeDefine.MorningGlory_Data)){
				if (isExist(ConnectionManager.getConnection(), tableName, colName)) {
					logger.info("游戏数据库升级:忽略" + cud.getSql());
				} else {
					logger.info("游戏数据库升级:执行" + cud.getSql());
					addColumn(ConnectionManager.getConnection(), cud);
				}
			}

			if(dbType.endsWith(DBTypeDefine.MorningGlory_Log)){
				if (isExist(ConnectionFactory.getConnection(), tableName, colName)) {
					logger.info("日志数据库升级:忽略" + cud.getSql());
				} else {
					logger.info("日志数据库升级:执行" + cud.getSql());
					addColumn(ConnectionFactory.getConnection(), cud);// 两个都成功，才算成功
				}
			}
			// 没异常，则算更新成功
			return true;
		} catch (Exception e) {
			throw new RuntimeException("表" + cud.getTableName() + "增加列[" + cud.getColName() + "]失败！", e);
		}
	}
}
