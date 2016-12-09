package newbee.morningGlory.mmorpg.gameInstance.persistence;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import newbee.morningGlory.mmorpg.player.gameInstance.CountRecord;

import org.apache.log4j.Logger;

import sophia.foundation.data.AbstractSaveableObject;
import sophia.foundation.property.PropertyDictionary;
import sophia.foundation.util.DebugUtil;
import sophia.game.persistence.ObjectDAO;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;

public class GameInstanceDao extends ObjectDAO<CountRecord> {

	private static final Logger logger = Logger.getLogger(GameInstanceDao.class);

	private static final String table = "game_instance";

	private GameInstanceDao() {
	}

	private static GameInstanceDao gameInstanceDao = new GameInstanceDao();

	public static GameInstanceDao getInstance() {
		return gameInstanceDao;
	}

	private List<CountRecord> mapResultSet(ResultSet rs) throws SQLException {
		List<CountRecord> countRecords = new ArrayList<CountRecord>();
		while (rs.next()) {
			CountRecord record = new CountRecord(rs.getString("refId"));
			PropertyDictionary dictionary = new PropertyDictionary();
			dictionary.loadDictionary(rs.getBytes("data"));
			record.setString(MGPropertyAccesser.getGameInstanceData(dictionary));
			countRecords.add(record);
		}
		return countRecords;
	}

	public boolean insertRecord(CountRecord record, Player player) throws Exception {
		boolean ret = false;
		
		String sql = "insert into " + table + "(id,identityId,refId,data) values(?,?,?,?)";
		
		Connection conn = null;
		CallableStatement callableStatement = null;
		try {
			conn = getConnection();
			callableStatement = conn.prepareCall(sql);
			
			PropertyDictionary dictionary = new PropertyDictionary();
			MGPropertyAccesser.setOrPutGameInstanceData(dictionary, record.getString());

			callableStatement.setString(1, player.getId());
			callableStatement.setString(2, player.getIdentity().getId());
			callableStatement.setString(3, record.getRefId());
			callableStatement.setBytes(4, dictionary.toByteArray());
			if (callableStatement.executeUpdate() > 0) {
				ret = true;
			}
			
			if(logger.isDebugEnabled()){
				logger.debug("execute sql=["+callableStatement.toString()+"]");
			}
			
		} catch (Exception e) {
			throw e;
		} finally {
			if (callableStatement != null) {
				try {
					callableStatement.close();
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
		
		return ret;
	}

	public List<CountRecord> getRecordsByPlayerId(String playerId) {
		List<CountRecord> ret = null;
		
		String sql = "select * from " + table + " where id = '" + playerId + "'";
		
		Connection conn = null;
		CallableStatement callableStatement = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			callableStatement = conn.prepareCall(sql);
			rs = callableStatement.executeQuery();
			ret = mapResultSet(rs);
			
			if(logger.isDebugEnabled()){
				logger.debug("execute sql=["+callableStatement.toString()+"]");
			}
			
		} catch (Exception e) {
			logger.error(DebugUtil.printStack(e));
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
				}
			}
			if (callableStatement != null) {
				try {
					callableStatement.close();
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
		
		return ret;
	}

	public boolean updateRecord(CountRecord record, String playerId) throws Exception {
		boolean ret = false;
		
		String sql = "update  " + table + " set data = ? where refId = ? and id = ? ";

		Connection conn = null;
		CallableStatement callableStatement = null;
		try {
			PropertyDictionary dictionary = new PropertyDictionary();
			MGPropertyAccesser.setOrPutGameInstanceData(dictionary, record.getString());

			conn = getConnection();
			callableStatement = conn.prepareCall(sql);
			callableStatement.setBytes(1, dictionary.toByteArray());
			callableStatement.setString(2, record.getRefId());
			callableStatement.setString(3, playerId);
			if (callableStatement.executeUpdate() > 0) {
				ret = true;
			}
			
			if(logger.isDebugEnabled()){
				logger.debug("execute sql=["+callableStatement.toString()+"]");
			}
			
		} catch (Exception e) {
			throw e;
		} finally {
			if (callableStatement != null) {
				try {
					callableStatement.close();
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
		
		return ret;
	}

	// ==============================================================
	@Override
	protected String getInstertSql() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected AbstractSaveableObject getInsertData(CountRecord paramT) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String getUpdateSql() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected AbstractSaveableObject getUpdateData(CountRecord paramT) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String getDeleteSql() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected AbstractSaveableObject getDeleteData(CountRecord paramT) {
		// TODO Auto-generated method stub
		return null;
	}

}
