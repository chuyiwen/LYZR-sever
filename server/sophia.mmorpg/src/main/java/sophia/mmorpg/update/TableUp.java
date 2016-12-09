package sophia.mmorpg.update;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import sophia.mmorpg.update.util.CTUpData;
import sophia.mmorpg.update.util.ColUpData;
import sophia.mmorpg.update.util.IOUtil;

/**
 * Copyright (c) 2014 by 游爱.
 * 
 * @version 1.0
 */
public class TableUp {

	private final static Logger logger = Logger.getLogger(TableUp.class);

	public static final String defencode = "utf-8";
	public static final char line_end_sign = ';';
	/**
	 * 更改表正则
	 */
	public static final String alert_reg = "[ ]?+ALTER[ ]+TABLE[ ]+([^ ]+)[ ]+(ADD|DROP|MODIFY)[ ]+([^ ]+)[ ]?+([^ ]+)?+([ ]+DEFAULT[ ]+([^ ]+))?+";
	public static Pattern alert_pa = Pattern.compile(alert_reg, Pattern.CASE_INSENSITIVE);

	/**
	 * 创建表正则
	 */
	public static final String create_reg = "[ ]?+CREATE[ ]+TABLE[ ]+([^\\(]+)";
	public static Pattern create_pa = Pattern.compile(create_reg, Pattern.CASE_INSENSITIVE);

	/**
	 * 列升级数据列表，包括删除和增加列
	 */
	protected List<ColUpData> colUpDatas = new ArrayList<ColUpData>();// 列升级数据列表
	protected CTUpData ctUpData = null;

	public List<ColUpData> getColUpDatas() {
		return colUpDatas;
	}

	public CTUpData getCtUpData() {
		return ctUpData;
	}

	/**
	 * 版本升级处理
	 * 
	 * @return
	 * @throws Exception
	 */
	public boolean upgrade(String dbType) throws Exception {
		if (ctUpData != null) {
			IPatch patch = ctUpData.getPatch();
			if (!patch.upgrade(ctUpData,dbType)) {
				throw new RuntimeException("创建表[" + ctUpData.getTableName() + "]失败！");
			}
		}

		if (!upgradeCol(dbType))
			return false;
		// TODO 其他升级操作

		return true;
	}

	/**
	 * 升级列
	 * 
	 * @return
	 * @throws Exception
	 */
	private boolean upgradeCol(String dbType) throws Exception {
		for (ColUpData colData : colUpDatas) {

			IPatch patch = colData.getPatch();
			if (patch == null) {
				throw new RuntimeException("表" + colData.getTableName() + "的[" + colData.getColName() + "]未知的升级列类型[" + colData.getOpType() + "]");
			}

			if (!patch.upgrade(colData,dbType)) {
				throw new RuntimeException("表" + colData.getTableName() + (colData.isDrop() ? "删除" : "增加") + "列[" + colData.getColName() + "]失败！");
			}
		}
		return true;
	}

	public static ColUpData getCUD(String sql) {
		Matcher ma = alert_pa.matcher(sql);

		if (ma.find()) {
			String tableName = ma.group(1);// 表名称
			String opType = ma.group(2);// 操作类型
			String colName = ma.group(3);// 列名称
			// String colDef = ma.group(4);// 列定义 用不到，直接注释
			// String defVal = "";// 列的默认值
			// if (ma.groupCount() > 5)
			// defVal = ma.group(6);
			return "add".equalsIgnoreCase(opType) ? ColUpData.newAddColData(sql, tableName, colName) : ColUpData.newDropColData(sql, tableName, colName);
		}
		throw new RuntimeException("错误的更改表sql语句！" + sql);
	}

	/**
	 * 添加升级数据
	 * 
	 * @param sql
	 */
	public void addUpData(String sql) {
		if (alert_pa.matcher(sql).matches())// 更改表的语句
			colUpDatas.add(getCUD(sql));
		else {
			Matcher ma = create_pa.matcher(sql);
			if (ma.find()) {
				ctUpData = new CTUpData();
				ctUpData.setSql(sql);
				ctUpData.setTableName(ma.group(1).replaceAll("\n|\r|\t", ""));
			}
		}
	}

	/**
	 * 初始化
	 */
	public void init(InputStream is) {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(is, defencode));
			String line = "";
			String sql = "";
			while ((line = br.readLine()) != null) {
				if (sql.length() > 0)
					sql += "\n";
				sql += line;
				int pos = sql.indexOf(line_end_sign);
				while (pos != -1) {// 一行数据
					String sql_1 = sql.substring(0, pos);// 头一句sql
					if(!sql_1.contains("--"))
						addUpData(sql_1);

					sql = sql.substring(pos + 1, sql.length());// 剩下的sql
					pos = sql.indexOf(line_end_sign);
				}
			}

		} catch (Exception e) {
			throw new RuntimeException("错误的日志文件格式！", e);
		} finally {
			IOUtil.closeRead(br);
		}
	}

	public static void main(String[] args) {
		String sql = "alter table g_character ADD unionId bigint(64); alter table g_character ADD unionId bigint(64);";
		int pos = sql.indexOf(';');
		if (pos != -1) {
			String sql_1 = sql.substring(0, pos);
			String sql_2 = sql.substring(pos + 1, sql.length());

			System.out.println("sql_1--->" + sql_1);
			System.out.println("sql_2--->" + sql_2);
		}
	}
}
