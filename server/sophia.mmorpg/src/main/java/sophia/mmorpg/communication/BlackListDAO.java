package sophia.mmorpg.communication;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import sophia.game.persistence.ConnectionManager;

import com.google.common.base.Strings;

public class BlackListDAO {

	private static final Logger logger = Logger.getLogger(BlackListDAO.class);

	private static final String tableName = "blacklist";

	private static BlackListDAO instance = new BlackListDAO();

	private Set<BlackListEntry> insertImmediateSetPrimary = Collections.newSetFromMap(new ConcurrentHashMap<BlackListEntry, Boolean>());
	private Set<BlackListEntry> insertImmediateSetSecondary = Collections.newSetFromMap(new ConcurrentHashMap<BlackListEntry, Boolean>());

	public static BlackListDAO getInstance() {
		return instance;
	}

	protected String getInstertSql() {
		String insertSql = "insert into " + tableName + "(playerId, identityId, identityName, name, blockTime) values (?, ?, ?, ?, ?)";
		return insertSql;
	}

	protected String getDeleteSql() {
		String deleteSql = "delete from " + tableName + " where playerId = ?";
		return deleteSql;
	}

	public void shutDownSave() throws SQLException {
		insertImmediateSetSecondary.addAll(insertImmediateSetPrimary);
		insertImmediateSetPrimary.removeAll(insertImmediateSetSecondary);
		for (BlackListEntry entry : insertImmediateSetSecondary) {
			try {
				doOneSave(entry);
			} catch (Exception e) {
				insertImmediateSetPrimary.add(entry);
				logger.error("BlackList Save Data Error,Data rollBacked!" + e);
			}
		}

		insertImmediateSetSecondary.clear();
		if (!insertImmediateSetPrimary.isEmpty()) {
			throw new RuntimeException("shutDownSave blackListEntry Error!!");
		}
	}

	public void save() throws SQLException {
		doSave();
	}

	protected void doOneSave(BlackListEntry entry) throws Exception {
		if (insertImmediateSetPrimary.isEmpty()) {
			return;
		}

		String sql = getInstertSql();
		if (Strings.isNullOrEmpty(sql)) {
			logger.error("sql sentence is null.");
			return;
		}

		Connection conn = ConnectionManager.getConnection();
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(sql);
			ps.setString(1, entry.getPlayerId());
			ps.setString(2, entry.getIdentityId());
			ps.setString(3, entry.getIdentityName());
			ps.setString(4, entry.getName());
			ps.setDate(5, new Date(entry.getBlockTime().getTime()));
			ps.execute();
		} catch (SQLException e) {
			throw e;
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

	protected void doSave() throws SQLException {
		if (insertImmediateSetPrimary.isEmpty()) {
			return;
		}

		String sql = getInstertSql();
		if (Strings.isNullOrEmpty(sql)) {
			logger.error("sql sentence is null.");
			return;
		}

		Connection conn = ConnectionManager.getConnection();
		PreparedStatement ps = null;

		insertImmediateSetSecondary.addAll(insertImmediateSetPrimary);
		insertImmediateSetPrimary.removeAll(insertImmediateSetSecondary);
		BlackListEntry temp = null;
		try {
			ps = conn.prepareStatement(sql);
			for (BlackListEntry entry : insertImmediateSetSecondary) {
				temp = entry;
				ps.setString(1, entry.getPlayerId());
				ps.setString(2, entry.getIdentityId());
				ps.setString(3, entry.getIdentityName());
				ps.setString(4, entry.getName());
				ps.setDate(5, new Date(entry.getBlockTime().getTime()));
				ps.addBatch();
			}

			ps.executeBatch();
		} catch (SQLException e) {
			insertImmediateSetPrimary.addAll(insertImmediateSetSecondary);
			if (temp != null) {
				logger.error("BlackListEntry =" + temp.toString());
			}
			logger.error("BlackList Save Data Error,Data rollBacked!" + e);
			throw e;
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

	public void save(BlackListEntry b) {
		insertImmediateSetPrimary.add(b);
	}

	public BlackListEntry getBlackListEntry(String playerId) {
		for (BlackListEntry entry : insertImmediateSetPrimary) {
			if (StringUtils.equals(playerId, entry.getPlayerId())) {
				return entry;
			}
		}
		return null;
	}

	public List<BlackListEntry> getBlackListEntry() {
		List<BlackListEntry> blackListEntrys = new ArrayList<BlackListEntry>();
		for (BlackListEntry entry : insertImmediateSetPrimary) {
			blackListEntrys.add(entry);
		}
		return blackListEntrys;
	}

}
