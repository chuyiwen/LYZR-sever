package sophia.mmorpg.player.persistence.update;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import sophia.game.persistence.ConnectionManager;


/**
 * Copyright (c) 2011 by 游爱.
 * 
 * @author 林悦胜 Create on 2013-12-4 下午4:52:11
 * @version 1.0
 */
public abstract class DBUpdateBasePatch implements IPatch {
	protected String errMsg;
	protected int state;

	public DBUpdateBasePatch() {
		this.errMsg = "";
		this.state = 1;
	}

	public String getErrMsg() {
		return this.errMsg;
	}

	public int getState() {
		return this.state;
	}

	public String getPatchName() {
		return super.getClass().getSimpleName();
	}

	// 工具方法
	protected boolean isExist(String tableName) throws Exception {
		StringBuilder sb = new StringBuilder();
		sb.append("select count(1) ");
		sb.append("from user_tables ");
		sb.append("where table_name = ? ");
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = ConnectionManager.getConnection().prepareStatement(sb.toString());
			ps.setString(1, tableName.toUpperCase());
			rs = ps.executeQuery();
			rs.next();
			if (rs.getInt(1) == 0) {
				return false;
			}

			return true;
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					
				}
			}
			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {
					
				}
			}
		}
	}

	protected boolean isExist(String tableName, String columnName) throws Exception {
		StringBuilder sb = new StringBuilder();
		sb.append("select count(1) ");
		sb.append("from user_tab_cols ");
		sb.append("where table_name = ? ");
		sb.append("    and column_name = ? ");
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = ConnectionManager.getConnection().prepareStatement(sb.toString());
			ps.setString(1, tableName.toUpperCase());
			ps.setString(2, columnName.toUpperCase());
			rs = ps.executeQuery();
			rs.next();
			if (rs.getInt(1) == 0) {
				return false;
			}
			return true;
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					
				}
			}
			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {
		
				}
			}

		}
	}

	public void dropColumn(String tableName, String colName) throws Exception {
		StringBuffer sb = new StringBuffer();
		sb.append(" alter table ").append(tableName);
		sb.append(" drop (").append(colName).append(")");
		Statement stat = null;
		try {
			stat = ConnectionManager.getConnection().createStatement();
			stat.execute(sb.toString());
		} finally {
			if (stat != null) {
				try {
					stat.close();
				} catch (SQLException e) {
					
				}
			}
			
		}
	}
	public static void addColumn(String tableName, String colName, String def) throws Exception
	  {
	    StringBuffer sb = new StringBuffer();
	    sb.append(" alter table ").append(tableName);
	    sb.append(" add (").append(colName);
	    sb.append(" ").append(def).append(")");
	    Statement stat = null;
	    try {
	      stat = ConnectionManager.getConnection().createStatement();
	      stat.execute(sb.toString());
	    } finally {
	    	if (stat != null) {
	    		try {
	    			stat.close();
	    		} catch (SQLException e) {
	    		}
	    	}
	    }
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
