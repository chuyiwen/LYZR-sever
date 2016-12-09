package sophia.mmorpg.player.persistence.update.util;

import sophia.mmorpg.player.persistence.update.DBUpdateBasePatch;

/**
 * Copyright (c) 2011 by 游爱.
 * 
 * @author 林悦胜 Create on 2013-12-3 下午5:40:31
 * @version 1.0
 */
public abstract class AddColPatch extends DBUpdateBasePatch {
	public void upgrade() {
		try {
			String tableName = getTableName();
			String colName = getColName();
			if (isExist(tableName, colName)) {
				this.state = SKIP;
			} else {
				addColumn(tableName, colName, getColDef());
				initColumn();
				this.state = SUCCESS;
			}
		} catch (Exception e) {
			this.errMsg = getExceptionMessage(e);
			this.state = FAIL;
		}
	}

	protected abstract String getTableName();

	protected abstract String getColName();

	protected abstract String getColDef();

	protected abstract void initColumn() throws Exception;
}
