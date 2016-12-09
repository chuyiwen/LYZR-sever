package newbee.morningGlory.mmorpg.player.activity.giftCode.persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import newbee.morningGlory.mmorpg.player.activity.giftCode.MGGiftCodeDataConfig;
import newbee.morningGlory.mmorpg.player.activity.giftCode.MGGiftCodeObject;

import org.apache.log4j.Logger;

import sophia.game.persistence.ConnectionManager;

import com.google.common.base.Strings;

public class GiftCodeDAO {

	private static Logger logger = Logger.getLogger(GiftCodeDAO.class);

	private static GiftCodeDAO instance = new GiftCodeDAO();

	private static final String table = "game_giftcode";

	private Set<MGGiftCodeObject> insertImmediateSetPrimary = Collections.newSetFromMap(new ConcurrentHashMap<MGGiftCodeObject, Boolean>());
	private Set<MGGiftCodeObject> insertImmediateSetSecondary = Collections.newSetFromMap(new ConcurrentHashMap<MGGiftCodeObject, Boolean>());

	public static GiftCodeDAO getInstance() {
		return instance;
	}

	protected String getInstertSql() {
		String insertSql = "insert into " + table + "(name, keyCode,groupId) values (?, ?,?)";
		return insertSql;
	}

	protected String getSelectByGroupSql() {
		String selectSql = "select count(*) from " + table + " where groupId = ? and name = ?";
		return selectSql;
	}

	protected String getSelectSql() {
		String selectSql = "select count(*) from " + table + " where keyCode = ?";
		return selectSql;
	}

	protected String getSelectBothSql() {
		String selectSql = "select count(*) from " + table + " where name = ? and keyCode = ?";
		return selectSql;
	}

	protected String getSelectAllSql() {
		String selectSql = "select * from " + table;
		return selectSql;
	}

	public void saveImmediateData(MGGiftCodeObject giftCodeObject) {
		insertImmediateSetPrimary.add(giftCodeObject);
	}

	public void save() {
		doSave();
	}

	protected void doSave() {
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
		try {
			ps = conn.prepareStatement(sql);
			for (MGGiftCodeObject giftCodeObject : insertImmediateSetSecondary) {
				ps.setString(1, giftCodeObject.getName());
				ps.setString(2, giftCodeObject.getKeyCode());
				ps.setString(3, giftCodeObject.getGroupId());
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
				String name = rs.getString("name");
				String keyCode = rs.getString("keyCode");
				String groupId = rs.getString("groupId");
				MGGiftCodeObject obj = new MGGiftCodeObject(name, keyCode, groupId);
				MGGiftCodeDataConfig.dataSet.add(obj);
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

	public void insertData(String name, String keyCode, String groupId) {
		String sql = getInstertSql();
		if (Strings.isNullOrEmpty(sql)) {
			logger.error("sql sentence is null.");
			return;
		}

		Connection conn = ConnectionManager.getConnection();
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(sql);
			ps.setString(1, name);
			ps.setString(2, keyCode);
			ps.setString(3, groupId);
			ps.executeUpdate();
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
	}

	public int selectDataByGroup(String groupId, String name) {
		String sql = getSelectByGroupSql();
		if (Strings.isNullOrEmpty(sql)) {
			logger.error("sql sentence is null.");
			return 0;
		}

		Connection conn = ConnectionManager.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		int count = 0;
		try {
			ps = conn.prepareStatement(sql);
			ps.setString(1, groupId);
			ps.setString(2, name);
			rs = ps.executeQuery();
			while (rs.next()) {
				count = rs.getInt(1);
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

		return count;
	}

	public int selectData(String name, String keyCode) {
		String sql = getSelectBothSql();
		if (Strings.isNullOrEmpty(sql)) {
			logger.error("sql sentence is null.");
			return 0;
		}
		int count = 0;
		Connection conn = ConnectionManager.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(sql);
			ps.setString(1, name);
			ps.setString(2, keyCode);
			rs = ps.executeQuery();
			while (rs.next()) {
				count = rs.getInt(1);
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

		return count;
	}

	public int selectData(String keyCode) {
		String sql = getSelectSql();
		if (Strings.isNullOrEmpty(sql)) {
			logger.error("sql sentence is null.");
			return 0;
		}
		int count = 0;
		Connection conn = ConnectionManager.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(sql);
			ps.setString(1, keyCode);
			rs = ps.executeQuery();
			while (rs.next()) {
				count = rs.getInt(1);
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

		return count;
	}

}
