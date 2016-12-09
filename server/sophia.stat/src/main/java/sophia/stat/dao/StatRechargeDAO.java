package sophia.stat.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Set;

import org.apache.log4j.Logger;

import sophia.foundation.util.DebugUtil;
import sophia.stat.ConnectionFactory;
import sophia.stat.StatRechargeData;

public class StatRechargeDAO {
	private static Logger logger = Logger.getLogger(StatRechargeDAO.class);

	public void insert(Set<StatRechargeData> datas) {
		batchInsertRecharge(datas);
	}

	private static String _insertSQL = "call insert_recharge(?,?,?,?,?,?,?,?)";

	private void batchInsertRecharge(Set<StatRechargeData> datas) {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		try {
			connection = ConnectionFactory.getConnection();
			preparedStatement = connection.prepareStatement(_insertSQL);
			connection.setAutoCommit(false);
			preparedStatement.clearBatch();
			for (StatRechargeData data : datas) {
				preparedStatement.setString(1, data.getId());
				preparedStatement.setString(2, data.getIdentityName());
				preparedStatement.setString(3, data.getPlayerName());
				preparedStatement.setInt(4, data.getQdCode1());
				preparedStatement.setInt(5, data.getQdCode2());
				preparedStatement.setInt(6, data.getGame_Gold());
				preparedStatement.setFloat(7, data.getPay_money());
				preparedStatement.setTimestamp(8, new Timestamp(data.getPay_time()));

				preparedStatement.addBatch();
			}

			preparedStatement.executeBatch();
			connection.commit();
		} catch (SQLException e) {
			logger.error("Batch StatRechargeBatchSaveJDBCWorker failed.");
			logger.error(preparedStatement);
			logger.error(DebugUtil.printStack(e));
		} catch (Exception e) {
			logger.error("execute StatRechargeBatchSaveJDBCWorker insert failed.");
			logger.error(preparedStatement);
			logger.error(DebugUtil.printStack(e));
		} finally {
			if (preparedStatement != null) {
				try {
					preparedStatement.close();
				} catch (SQLException e) {
					logger.error("execute StatRechargeBatchSaveJDBCWorker insert preparedStatement.close failed.", e);
				}
			}
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					logger.error("execute StatRechargeBatchSaveJDBCWorker insert connection.close failed.", e);
				}
			}
		}

	}

}
