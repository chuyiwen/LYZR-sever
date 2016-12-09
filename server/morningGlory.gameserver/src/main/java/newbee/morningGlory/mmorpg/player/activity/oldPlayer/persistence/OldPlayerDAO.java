package newbee.morningGlory.mmorpg.player.activity.oldPlayer.persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import newbee.morningGlory.mmorpg.player.activity.oldPlayer.MGOldPlayerData;

import org.apache.log4j.Logger;

import sophia.game.persistence.ConnectionManager;

import com.google.common.base.Strings;

public class OldPlayerDAO {

	private static Logger logger = Logger.getLogger(OldPlayerDAO.class);

	private static OldPlayerDAO instance = new OldPlayerDAO();

	private static final String table = "game_oldplayer";

	private Set<String> insertImmediateSetPrimary = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());
	private Set<String> insertImmediateSetSecondary = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());

	public static OldPlayerDAO getInstance() {
		return instance;
	}

	protected String getInstertSql() {
		String insertSql = "insert into " + table + "(identityId) values (?)";
		return insertSql;
	}

	protected String getSelectAllSql() {
		String selectSql = "select identityId from " + table;
		return selectSql;
	}

	public void save() {
		if (isNeedSave()) {
			doSave();
		}

	}

	public void saveImmediateData(String identityId) {
		insertImmediateSetPrimary.add(identityId);
	}

	protected boolean isNeedSave() {
		return !insertImmediateSetPrimary.isEmpty();
	}

	protected void doSave() {
		String sql = getInstertSql();
		if (Strings.isNullOrEmpty(sql)) {
			logger.error("sql sentence is null.");
			return;
		}

		Connection conn = ConnectionManager.getConnection();
		PreparedStatement ps = null;

		insertImmediateSetSecondary.addAll(insertImmediateSetPrimary);
		insertImmediateSetPrimary.removeAll(insertImmediateSetSecondary);
		try {
			ps = conn.prepareStatement(sql);
			for (String identityId : insertImmediateSetSecondary) {
				ps.setString(1, identityId);			
				ps.addBatch();
			}
			ps.executeBatch();
		} catch (SQLException e) {
			logger.error(e);
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
		insertImmediateSetSecondary.clear();
	}

	public void loadData() {
		String sql = getSelectAllSql();
		if (Strings.isNullOrEmpty(sql)) {
			logger.error("sql sentence is null.");
			return;
		}

		Connection conn = ConnectionManager.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				String identityId = rs.getString("identityId");
				MGOldPlayerData.addOldPlayer(identityId);
			}
		} catch (SQLException e) {
			logger.error(e);
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

	}

	

}
