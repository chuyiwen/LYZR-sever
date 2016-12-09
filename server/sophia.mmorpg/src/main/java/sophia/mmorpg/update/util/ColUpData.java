package sophia.mmorpg.update.util;

import sophia.mmorpg.update.IPatch;

/**
 * 列的升级数据 Copyright (c) 2014 by 游爱.
 * 
 * @version 1.0
 */
public class ColUpData extends UpData {

	public static byte COL_OP_DROP = 1;// 删除操作
	public static byte COL_OP_ADD = 2;// 添加操作

	public static boolean isDrop(byte opType) {
		return opType == COL_OP_DROP;
	}

	public static boolean isAdd(byte opType) {
		return opType == COL_OP_ADD;
	}

	/**
	 * 创建一个增加列数据对象，没有默认值
	 * 
	 * @param tableName
	 * @param colName
	 * @param colDef
	 * @return
	 */
	public static ColUpData newAddColData(String sql, String tableName, String colName) {
		return new ColUpData(sql, tableName, colName, COL_OP_ADD);
	}

	/**
	 * 创建一个删除列数据对象
	 * 
	 * @param tableName
	 * @param colName
	 * @param colDef
	 * @return
	 */
	public static ColUpData newDropColData(String sql, String tableName, String colName) {
		return new ColUpData(sql, tableName, colName, COL_OP_DROP);
	}

	private byte opType = COL_OP_ADD;// 操作类型

	public byte getOpType() {
		return opType;
	}

	private String colName = "";// 列名称

	/**
	 * 该构造函数初始化类型为添加列操作
	 * 
	 * @param tableName
	 * @param colName
	 */
	public ColUpData(String sql, String tableName, String colName, byte opType) {
		setSql(sql);
		setTableName(tableName);
		this.colName = colName;
		this.opType = opType;
	}

	public void setColName(String colName) {
		this.colName = colName;
	}

	/**
	 * 列名称
	 * 
	 * @return
	 */
	public String getColName() {
		return colName;
	}

	/**
	 * 是否为删除数据
	 * 
	 * @return
	 */
	public boolean isDrop() {
		return isDrop(opType);
	}

	public boolean isAdd() {
		return isAdd(opType);
	}

	@Override
	public IPatch getPatch() {
		return isDrop() ? dropColPath : addColPatch;
	}
}
