package newbee.morningGlory.mmorpg.sortboard.persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;

import newbee.morningGlory.mmorpg.player.activity.limitTimeRank.LimitTimeActivityMgr;
import newbee.morningGlory.mmorpg.sortboard.SortboardData;
import newbee.morningGlory.mmorpg.sortboard.SortboardType;

import org.apache.log4j.Logger;

import sophia.foundation.util.DebugUtil;
import sophia.game.persistence.ConnectionManager;

import com.google.common.base.Strings;

public class SortboardDAO {

	private static Logger logger = Logger.getLogger(SortboardDAO.class);

	private SortboardPersistenceObject persistenceObject = SortboardPersistenceObject.getInstance();

	private static SortboardDAO instance = new SortboardDAO();

	private static final String table = "game_sortboardData";

	public static final int type_Difference = 1000;

	public static SortboardDAO getInstance() {
		return instance;
	}

	protected String getInstertSql() {
		String insertSql = "insert into " + table + "(id, sortboardData) values (?, ?)";
		return insertSql;
	}

	protected String getSelectSql() {
		String selectSql = "select * from " + table;
		return selectSql;
	}

	protected String getUpdateSql() {
		String insertSql = "update " + table + " set sortboardData = ? where id = ?";
		return insertSql;
	}
	
	protected String getIdCountSql() {
		String selectSql = "select count(id) FROM " + table + " WHERE id = ?";
		return selectSql;
	}

	public void insertData(SortboardData sortboardData, byte data_type) throws Exception {
		String sql = getInstertSql();
		if (Strings.isNullOrEmpty(sql)) {
			logger.error("sql sentence is null.");
			return;
		}

		SortboardType type = sortboardData.getType();
		int typeId = type.getSortboardType();
		if (data_type == LimitTimeActivityMgr.LimitRank_Type) {
			typeId = typeId + type_Difference;
		}
		byte[] boardDataBytes = persistenceObject.sortboardInfoToBytes(sortboardData);
		Connection conn = ConnectionManager.getConnection();
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(sql);
			ps.setInt(1, typeId);
			ps.setBytes(2, boardDataBytes);
			ps.executeUpdate();
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

	public void updateData(SortboardData sortboardData, byte data_type) throws Exception {
		String sql = getUpdateSql();
		if (Strings.isNullOrEmpty(sql)) {
			logger.error("sql sentence is null.");
			return;
		}

		SortboardType type = sortboardData.getType();
		int typeId = type.getSortboardType();
		if (data_type == LimitTimeActivityMgr.LimitRank_Type) {
			typeId = typeId + type_Difference;
		}
		byte[] boardDataBytes = persistenceObject.sortboardInfoToBytes(sortboardData);
		Connection conn = ConnectionManager.getConnection();
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(sql);
			ps.setInt(2, typeId);
			ps.setBytes(1, boardDataBytes);
			ps.executeUpdate();
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

	public ConcurrentHashMap<Integer, SortboardData> selectData() {
		String sql = getSelectSql();
		if (Strings.isNullOrEmpty(sql)) {
			logger.error("sql sentence is null.");
			return null;
		}

		ConcurrentHashMap<Integer, SortboardData> sbd = new ConcurrentHashMap<Integer, SortboardData>();

		Connection conn = ConnectionManager.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				int id = rs.getInt("id");
				byte[] sortboardDataBytes = rs.getBytes("sortboardData");
				if (sortboardDataBytes.length == 0) {
					return sbd;
				}
				SortboardData sortboardData = persistenceObject.sortboardInfoFromBytes(sortboardDataBytes);
				SortboardType sortboardType = SortboardType.get(id);
				if (sortboardType != null) {
					sortboardData.setType(sortboardType);
					sbd.put(id, sortboardData);
				} else {
					logger.error("can't find sortboardType=" + id);
				}
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
		return sbd;

	}

	public SortboardData selectLimitRankData(int typeId) {
		String sql = "select * from " + table + " where id=?";
		SortboardData sortboardData = null;
		Connection conn = ConnectionManager.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(sql);
			ps.setInt(1, typeId);
			rs = ps.executeQuery();
			while (rs.next()) {
				byte[] sortboardDataBytes = rs.getBytes("sortboardData");
				sortboardData = persistenceObject.sortboardInfoFromBytes(sortboardDataBytes);
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
		return sortboardData;

	}

	public int selectCount(SortboardType type) {
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
			ps.setInt(1, type.getSortboardType());
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
}
