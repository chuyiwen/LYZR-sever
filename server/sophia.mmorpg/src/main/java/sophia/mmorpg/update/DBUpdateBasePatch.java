package sophia.mmorpg.update;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import sophia.mmorpg.update.util.UpData;

/**
 * Copyright (c) 2014 by 游爱.
 * 
 */

public abstract class DBUpdateBasePatch implements IPatch {

	public DBUpdateBasePatch() {
	}

	public String getPatchName() {
		return super.getClass().getSimpleName();
	}

	// 工具方法
	public static boolean isExist(Connection conn, String tableName) throws Exception {
		String _tableName = tableName.toLowerCase();
		String sql = "SELECT table_name FROM information_schema.TABLES WHERE table_name ='" + _tableName+"'";
		PreparedStatement ps = null;
		ResultSet rs = null;
		String result=null;
		try {
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if(rs.next()){
				result = rs.getString(1);
			}
			
			return  result==null? false:true;
		} catch (Exception e) {
			throw e;
		} finally {
			if (rs != null)
				rs.close();
			if (ps != null)
				ps.close();
			if (conn != null)
				conn.close();
		}
	}

	public static boolean isExist(Connection conn, String tableName, String columnName) throws Exception {
		String sql = "DESCRIBE " + tableName + " " + columnName;

		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			return rs.next();
		} catch (Exception e) {
			throw e;
		} finally {
			if (rs != null)
				rs.close();
			if (ps != null)
				ps.close();
			if (conn != null)
				conn.close();
		}
	}

	public static boolean executeUpdateSql(Connection conn, String sql) throws Exception {
		Statement stat = null;

		try {
			stat = conn.createStatement();
			int c = stat.executeUpdate(sql);
			return c > 0;
		} catch (Exception e) {
			throw e;
		} finally {
			if (stat != null)
				stat.close();
			if (conn != null)
				conn.close();
		}
	}

	public static boolean dropColumn(Connection conn, UpData colData) throws Exception {
		return executeUpdateSql(conn, colData.getSql());
	}

	public static boolean addColumn(Connection conn, UpData colData) throws Exception {
		return executeUpdateSql(conn, colData.getSql());
	}

	public static boolean createTable(Connection conn, UpData colData) throws Exception {
		return executeUpdateSql(conn, colData.getSql());
	}

	// =================================
	public static String getExceptionMessage(Throwable err) {
		return getExceptionMessage(err, true);
	}

	public static String getExceptionMessage(Throwable err, boolean blRootCause) {
		if (err == null)
			return "";
		String sMsg = (err.getMessage() == null) ? "" : err.getMessage();

		if (blRootCause) {
			Throwable rootCause = getExceptionRootCause(err);
			if (rootCause != null) {
				String causeMsg = rootCause.getMessage();
				if ((causeMsg != null) && (!(causeMsg.trim().equals(""))) && (!(causeMsg.equals(sMsg))))
					sMsg = sMsg + "\nRoot Cause : " + causeMsg.trim();
			}
		}
		return sMsg;
	}

	public static Throwable getExceptionRootCause(Throwable err) {
		Throwable cause = err;
		do {
			if (cause == null)
				continue;
			cause = cause.getCause();
			if (cause == null)
				cause = err;
		} while ((cause != null) && (cause.getCause() != null) && (cause != err));

		if (cause == null) {
			return err;
		}
		return cause;
	}
}
