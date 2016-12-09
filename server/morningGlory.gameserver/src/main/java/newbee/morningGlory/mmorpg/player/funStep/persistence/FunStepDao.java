package newbee.morningGlory.mmorpg.player.funStep.persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import sophia.foundation.util.DebugUtil;
import sophia.game.persistence.ConnectionManager;
import sophia.mmorpg.player.Player;

import com.google.common.base.Strings;

public class FunStepDao {
	
	private static Logger logger = Logger.getLogger(FunStepDao.class);
	
	private FunStepPersistenceObject persistenceObject = FunStepPersistenceObject.getInstance();
	
	private static FunStepDao instance = new FunStepDao();

	private static final String table = "game_funstep";

	public static FunStepDao getInstance() {
		return instance;
	}

	protected String getInstertSql() {
		String insertSql = "insert into " + table + "(playerId, funStep) values (?, ?)";
		return insertSql;
	}

	protected String getSelectSql() {
		String selectSql = "select funStep from " + table + " where playerId = ?";
		return selectSql;
	}

	protected String getUpdateSql() {
		String insertSql = "update " + table + " set funStep = ? where playerId = ?";
		return insertSql;
	}
	
	public void insertData(Player player) {
		String sql = getInstertSql();
		if (Strings.isNullOrEmpty(sql)) {
			logger.error("sql sentence is null.");
			return;
		}

		String playerId = player.getId();
		byte[] funStepDataBytes = persistenceObject.ToBytes(player);
		Connection conn = ConnectionManager.getConnection();
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(sql);
			ps.setString(1, playerId);
			ps.setBytes(2, funStepDataBytes);
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
	
		String playerId = player.getId();
		byte[] funStepDataBytes = persistenceObject.ToBytes(player);
		Connection conn = ConnectionManager.getConnection();
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(sql);
			ps.setBytes(1, funStepDataBytes);
			ps.setString(2, playerId);
			
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

		String playerId = player.getId();
		Connection conn = ConnectionManager.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		boolean result = false;
		try {
			ps = conn.prepareStatement(sql);
			ps.setString(1, playerId);
			rs = ps.executeQuery();
			while (rs.next()) {
				byte[] funStepDataBytes = rs.getBytes("funStep");
			    persistenceObject.FromBytes(funStepDataBytes,player);
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
