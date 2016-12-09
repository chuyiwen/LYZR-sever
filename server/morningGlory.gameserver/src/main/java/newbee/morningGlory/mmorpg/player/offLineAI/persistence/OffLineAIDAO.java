package newbee.morningGlory.mmorpg.player.offLineAI.persistence;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import newbee.morningGlory.mmorpg.player.offLineAI.PlayerAvatarData;

import org.apache.log4j.Logger;

import sophia.game.persistence.ConnectionManager;

public class OffLineAIDAO {

	private static final Logger logger = Logger.getLogger(OffLineAIDAO.class);
	
	private static final String SELECT_SQL = "select * from offline_ai where playerid = ?";
	private static final String UPDATE_SQL = "{call updateOffline_ai(?,?)}";
	private static final String INSTERT_SQL = "{call newOffline_ai(?,?)}";
	

	/**
	 * 从数据库获取指定玩家替身数据
	 * @param playerId	玩家ID
	 * @return
	 */
	public static PlayerAvatarData selectPlayerAvatarData(String playerId) throws Exception{
		Connection conn = null;
		PreparedStatement pstat = null;
		ResultSet resultSet = null;
		try {
			conn = ConnectionManager.getConnection();
			pstat = conn.prepareStatement(SELECT_SQL);
			pstat.setObject(1, playerId);
			resultSet = pstat.executeQuery();
			if (!resultSet.next()) {
				return null;
			}
			PlayerAvatarData playerAvatarData = new PlayerAvatarData(playerId);
			byte[] dbData = resultSet.getBytes("dbData");
			playerAvatarData.decode(dbData);
			return playerAvatarData;
		}finally {
			if (resultSet != null) {
				try {
					resultSet.close();
				} catch (SQLException e) {
					logger.error(e.getMessage(), e);
				}
			}
			if (pstat != null) {
				try {
					pstat.close();
				} catch (SQLException e) {
					logger.error(e.getMessage(), e);
				}
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					logger.error(e.getMessage(), e);
				}
			}
		}
	}

	/**
	 * 更新指定的玩家替身数据
	 * @param list
	 */
	public static void updatePlayerAvatarData(List<PlayerAvatarData> list) {
		if(list == null || list.size() <= 0){
			return;
		}
		Connection conn = null;
		CallableStatement callableStatement = null;
		try {
			conn = ConnectionManager.getConnection();
			conn.setAutoCommit(false);
			callableStatement = conn.prepareCall(UPDATE_SQL);
			for (PlayerAvatarData playerAvatarData : list) {
				byte[] encoded = null;
				try{
					encoded = playerAvatarData.encoded();
				}catch (Exception e) {
					logger.error(e.getMessage(), e);
					continue;
				}
				callableStatement.setBytes(1, encoded);
				callableStatement.setString(2, playerAvatarData.getPlayerId());
				callableStatement.addBatch();
			}
			callableStatement.executeBatch();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			try {
				conn.commit();
				conn.setAutoCommit(true);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
			if (callableStatement != null) {
				try {
					callableStatement.close();
				} catch (SQLException e) {
					logger.error(e.getMessage(), e);
				}
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					logger.error(e.getMessage(), e);
				}
			}
		}

	}

	public static PlayerAvatarData instertPlayerAvatarData(String playerId) throws Exception {
		Connection conn = null;
		CallableStatement callableStatement = null;
		try {
			conn = ConnectionManager.getConnection();
			callableStatement = conn.prepareCall(INSTERT_SQL);
			callableStatement.setString(1, playerId);
			callableStatement.setBytes(2, null);
			callableStatement.executeUpdate();
			
			PlayerAvatarData playerAvatarData = new PlayerAvatarData(playerId);
			return playerAvatarData;
		} finally {
			if (callableStatement != null) {
				try {
					callableStatement.close();
				} catch (SQLException e) {
					logger.error(e.getMessage(), e);
				}
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					logger.error(e.getMessage(), e);
				}
			}
		}
	}

}
