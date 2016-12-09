package sophia.mmorpg.update.util;

import sophia.mmorpg.update.IPatch;

/**
 * 列的升级数据 Copyright (c) 2014 by 游爱.
 * 
 * @version 1.0
 */
public abstract class UpData {

	private String sql = "";
	private String tableName = "";
	
	protected static AddColPatch addColPatch = new AddColPatch();
	protected static DropColPatch dropColPath = new DropColPatch();
	protected static CreateTablePatch createTablePath = new CreateTablePatch();

	
	/**
	 * 获得升级的sql语句
	 * 
	 * @return
	 */
	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	/**
	 * 表名称
	 * 
	 * @return
	 */
	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	
	public abstract IPatch getPatch();
}
