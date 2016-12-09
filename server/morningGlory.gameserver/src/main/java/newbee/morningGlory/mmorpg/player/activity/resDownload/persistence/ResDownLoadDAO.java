package newbee.morningGlory.mmorpg.player.activity.resDownload.persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import sophia.foundation.util.DebugUtil;
import sophia.game.persistence.ConnectionManager;
import sophia.mmorpg.player.Player;

import com.google.common.base.Strings;

public class ResDownLoadDAO {

	private static Logger logger = Logger.getLogger(ResDownLoadDAO.class);

	private ResDownLoadPersistenceObject persistenceObject = ResDownLoadPersistenceObject.getInstance();

	private static ResDownLoadDAO instance = new ResDownLoadDAO();

	private static final String table = "game_resdownload";

	public static final int type_Difference = 1000;

	public static ResDownLoadDAO getInstance() {
		return instance;
	}

	protected String getInstertSql() {
		String insertSql = "insert into " + table + "(identityId, resDownload) values (?, ?)";
		return insertSql;
	}

	protected String getSelectSql() {
		String selectSql = "select resDownload from " + table + " where identityId = ?";
		return selectSql;
	}

	protected String getUpdateSql() {
		String insertSql = "update " + table + " set resDownload = ? where identityId = ?";
		return insertSql;
	}
	
	
	
	
	public void insertData(Player player) {
		String sql = getInstertSql();
		if (Strings.isNullOrEmpty(sql)) {
			logger.error("sql sentence is null.");
			return;
		}

		String identityId = player.getIdentity().getId();
		byte[] resDownloadDataBytes = persistenceObject.ToBytes(player);
		Connection conn = ConnectionManager.getConnection();
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(sql);
			ps.setString(1, identityId);
			ps.setBytes(2, resDownloadDataBytes);
			ps.executeUpdate();
		} catch (SQLException e) {
			logger.error(DebugUtil.printStack(e));
		} finally {
			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {
				}
			}

			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
				}
			}
		}
	}

	public void updateData(Player player) {
		String sql = getUpdateSql();
		if (Strings.isNullOrEmpty(sql)) {
			logger.error("sql sentence is null.");
			return;
		}
	
		String identityId = player.getIdentity().getId();
		byte[] resDownloadDataBytes = persistenceObject.ToBytes(player);
		Connection conn = ConnectionManager.getConnection();
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(sql);
			ps.setBytes(1, resDownloadDataBytes);
			ps.setString(2, identityId);
			
			ps.executeUpdate();
		} catch (SQLException e) {
			logger.error(DebugUtil.printStack(e));
		} finally {
			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {
				}
			}

			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
				}
			}
		}
	}

	public boolean selectData(Player player) {
		String sql = getSelectSql();
		if (Strings.isNullOrEmpty(sql)) {
			logger.error("sql sentence is null.");
			return false;
		}

		String identityId = player.getIdentity().getId();
		Connection conn = ConnectionManager.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		boolean result = false;
		try {
			ps = conn.prepareStatement(sql);
			ps.setString(1, identityId);
			rs = ps.executeQuery();
			while (rs.next()) {
				byte[] resDownloadBytes = rs.getBytes("resDownload");
			    persistenceObject.FromBytes(resDownloadBytes,player);
				result = true;
			}
		} catch (SQLException e) {
			logger.error(DebugUtil.printStack(e));
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

			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
				}
			}
		}
		
		return result;
	}

	
}
