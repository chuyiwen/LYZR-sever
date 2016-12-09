package newbee.morningGlory.mmorpg.store.persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import sophia.foundation.util.DebugUtil;
import sophia.game.persistence.ConnectionManager;

import com.google.common.base.Strings;

public class StoreDao {

	private static Logger logger = Logger.getLogger(StoreDao.class);

	private StorePersistenceObject persistenceObject = StorePersistenceObject.getInstance();

	private static StoreDao instance = new StoreDao();

	private static final String table = "game_storeLimit";

	public static final int storeLimitData = 1;
	public static final int discountLimitData = 2;

	public static StoreDao getInstance() {
		return instance;
	}

	protected String getIdCountSql() {
		String selectSql = "select count(id) FROM " + table + " WHERE id = ?";
		return selectSql;
	}
	
	protected String getInstertSql() {
		String insertSql = "insert into " + table + "(id, saveTime, allLimitData, personalLimitData) values (?, ?, ?, ?)";
		return insertSql;
	}

	protected String getSelectAllLimitSql() {
		String selectSql = "select allLimitData from " + table + " where id = ?";
		return selectSql;
	}
	
	protected String getSelectPersonalLimitSql() {
		String selectSql = "select personalLimitData from " + table + " where id = ?";
		return selectSql;
	}
	
	protected String getSelectsaveTimeSql() {
		String selectSql = "select saveTime from " + table + " where id = ?";
		return selectSql;
	}

	protected String getUpdateSql() {
		String insertSql = "update " + table + " set saveTime = ?, allLimitData = ?, personalLimitData = ? where id = ?";
		return insertSql;
	}
	
	public int selectCount(int id) {
		String sql = getIdCountSql();
		if (Strings.isNullOrEmpty(sql)) {
			logger.error("sql sentence is null.");
			return -1;
		}

		int count = 0;
		Connection conn = ConnectionManager.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(sql);
			ps.setInt(1, id);
			rs = ps.executeQuery();
			while (rs.next()) {
				count = rs.getInt("count(id)");
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
		return count;
	}
	
	public void insertData(Map<String, Short> allLimit, Map<String, ConcurrentHashMap<String, Short>> personalLimit, int id) {
		String sql = getInstertSql();
		if (Strings.isNullOrEmpty(sql)) {
			logger.error("sql sentence is null.");
			return;
		}
		long nowTime = System.currentTimeMillis();
		byte[] allLimitData = persistenceObject.allLimitDataToBytes(allLimit);
		byte[] personalLimitData = persistenceObject.personalLimitDataToBytes(personalLimit);
		Connection conn = ConnectionManager.getConnection();
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(sql);
			ps.setInt(1, id);
			ps.setLong(2, nowTime);
			ps.setBytes(3, allLimitData);
			ps.setBytes(4, personalLimitData);
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
	
	public void updateData(Map<String, Short> allLimit, Map<String, ConcurrentHashMap<String, Short>> personalLimit, int id) {
		String sql = getUpdateSql();
		if (Strings.isNullOrEmpty(sql)) {
			logger.error("sql sentence is null.");
			return;
		}

		long nowTime = System.currentTimeMillis();
		byte[] allLimitData = persistenceObject.allLimitDataToBytes(allLimit);
		byte[] personalLimitData = persistenceObject.personalLimitDataToBytes(personalLimit);
		Connection conn = ConnectionManager.getConnection();
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(sql);
			ps.setLong(1, nowTime);
			ps.setBytes(2, allLimitData);
			ps.setBytes(3, personalLimitData);
			ps.setInt(4, id);
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
	
	public Map<String, Short> selectAllLimitData(int id) {
		String sql = getSelectAllLimitSql();
		if (Strings.isNullOrEmpty(sql)) {
			logger.error("sql sentence is null.");
			return null;
		}

		Map<String, Short> allLimit = new HashMap<String, Short>();

		Connection conn = ConnectionManager.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(sql);
			ps.setInt(1, id);
			rs = ps.executeQuery();
			while (rs.next()) {
				byte[] allLimitData = rs.getBytes("allLimitData");
				allLimit = persistenceObject.allLimitDataFromBytes(allLimitData);
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
		return allLimit;
	}

	public Map<String, ConcurrentHashMap<String, Short>> selectPersonalLimitData(int id) {
		String sql = getSelectPersonalLimitSql();
		if (Strings.isNullOrEmpty(sql)) {
			logger.error("sql sentence is null.");
			return null;
		}

		Map<String, ConcurrentHashMap<String, Short>> personalLimit = new HashMap<String, ConcurrentHashMap<String, Short>>();

		Connection conn = ConnectionManager.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(sql);
			ps.setInt(1, id);
			rs = ps.executeQuery();
			while (rs.next()) {
				byte[] personalLimitData = rs.getBytes("personalLimitData");
				personalLimit = persistenceObject.personalLimitDataFromBytes(personalLimitData);
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
		return personalLimit;
	}
	
	public long selectSaveTime(int id) {
		String sql = getSelectsaveTimeSql();
		if (Strings.isNullOrEmpty(sql)) {
			logger.error("sql sentence is null.");
			return -1;
		}

		long saveTime = 0;

		Connection conn = ConnectionManager.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(sql);
			ps.setInt(1, id);
			rs = ps.executeQuery();
			while (rs.next()) {
				saveTime = rs.getLong("saveTime");
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
		return saveTime;
	}
}
