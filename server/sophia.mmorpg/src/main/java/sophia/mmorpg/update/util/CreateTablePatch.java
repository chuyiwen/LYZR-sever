package sophia.mmorpg.update.util;

import org.apache.log4j.Logger;

import sophia.game.persistence.ConnectionManager;
import sophia.mmorpg.update.DBTypeDefine;
import sophia.mmorpg.update.DBUpdateBasePatch;
import sophia.stat.ConnectionFactory;

/**
 * 创建表 Copyright (c) 2011 by 游爱.
 * 
 * @version 1.0
 */
public class CreateTablePatch extends DBUpdateBasePatch {
	private final static Logger logger = Logger.getLogger(CreateTablePatch.class);

	public boolean upgrade(UpData colData,String dbType) throws Exception {
		CTUpData cud = (CTUpData) colData;
		try {
			String tableName = cud.getTableName();

			// boolean succ = true;// 默认为成功
			if(dbType.endsWith(DBTypeDefine.MorningGlory_Data)){
				if (isExist(ConnectionManager.getConnection(), tableName)) {
					logger.info("游戏数据库升级:忽略" + cud.getSql());
				} else {
					logger.info("游戏数据库升级:执行" + cud.getSql());
					createTable(ConnectionManager.getConnection(), cud);
				}
			}

			if(dbType.endsWith(DBTypeDefine.MorningGlory_Log)){
				if (isExist(ConnectionFactory.getConnection(), tableName)) {
					logger.info("日志数据库升级:忽略" + cud.getSql());
				} else {
					logger.info("日志数据库升级:执行" + cud.getSql());
					createTable(ConnectionFactory.getConnection(), cud);// 两个都成功，才算成功
				}
			}
			// 没异常，则算更新成功
			return true;
		} catch (Exception e) {
			throw new RuntimeException("创建" + cud.getTableName() + "失败！", e);
		}
	}
}
