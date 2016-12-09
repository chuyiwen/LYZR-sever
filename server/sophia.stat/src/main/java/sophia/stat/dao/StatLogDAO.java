/**
 *   Copyright 2013-2015 Sophia
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package sophia.stat.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;

import sophia.foundation.util.DebugUtil;
import sophia.stat.ConnectionFactory;
import sophia.stat.StatLogDBData;

public class StatLogDAO {
	private static Logger logger = Logger.getLogger(StatLogDAO.class);

	public void insert(List<StatLogDBData> datas) {

		batchInsert(datas);

	}

	private static String _insertSQL = "call insert_stat(?,?,?, ?,?,?,?,?, ?,?,?,?,? ,?,?,?,?,? ,?,?,?,?,?)";

	public void batchInsert(Collection<StatLogDBData> datas) {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		try {
			connection = ConnectionFactory.getConnection();
			preparedStatement = connection.prepareStatement(_insertSQL);
			connection.setAutoCommit(false);
			preparedStatement.clearBatch();
			for (StatLogDBData data : datas) {
				preparedStatement.setDate(1, data.date);
				preparedStatement.setTime(2, data.time);

				preparedStatement.setInt(3, data.qdCode1);
				preparedStatement.setInt(4, data.qdCode2);

				preparedStatement.setString(5, data.playerId);
				preparedStatement.setString(6, data.playerName);
				preparedStatement.setString(7, data.identityName);
				preparedStatement.setInt(8, data.statType);

				preparedStatement.setInt(9, data.n1);

				if (data.n2 == Long.MIN_VALUE)
					preparedStatement.setNull(10, java.sql.Types.BIGINT);
				else
					preparedStatement.setLong(10, data.n2);

				if (data.n3 == Long.MIN_VALUE)
					preparedStatement.setNull(11, java.sql.Types.BIGINT);
				else
					preparedStatement.setLong(11, data.n3);

				if (data.n4 == Long.MIN_VALUE)
					preparedStatement.setNull(12, java.sql.Types.BIGINT);
				else
					preparedStatement.setLong(12, data.n4);

				if (data.n5 == Long.MIN_VALUE)
					preparedStatement.setNull(13, java.sql.Types.BIGINT);
				else
					preparedStatement.setLong(13, data.n5);

				if (data.n6 == Long.MIN_VALUE)
					preparedStatement.setNull(14, java.sql.Types.BIGINT);
				else
					preparedStatement.setLong(14, data.n6);

				if (data.n7 == Long.MIN_VALUE)
					preparedStatement.setNull(15, java.sql.Types.BIGINT);
				else
					preparedStatement.setLong(15, data.n7);

				if (data.n8 == Long.MIN_VALUE)
					preparedStatement.setNull(16, java.sql.Types.BIGINT);
				else
					preparedStatement.setLong(16, data.n8);

				if (data.n9 == Long.MIN_VALUE)
					preparedStatement.setNull(17, java.sql.Types.BIGINT);
				else
					preparedStatement.setLong(17, data.n9);

				if (data.n10 == Long.MIN_VALUE)
					preparedStatement.setNull(18, java.sql.Types.BIGINT);
				else
					preparedStatement.setLong(18, data.n10);

				preparedStatement.setString(19, data.s1);
				preparedStatement.setString(20, data.s2);
				preparedStatement.setString(21, data.s3);
				preparedStatement.setString(22, data.s4);
				preparedStatement.setString(23, data.s5);

				preparedStatement.addBatch();
			}

			preparedStatement.executeBatch();
			connection.commit();
		} catch (SQLException e) {
			logger.error("Batch StatBatchSaveJDBCWorker failed.");
			logger.error(preparedStatement);
			logger.error(DebugUtil.printStack(e));
			for (StatLogDBData data : datas) {
				logger.error("stat type: " + data.statType + " " + data.toString());
			}
		} catch (Exception e) {
			logger.error("execute StatBatchSaveJDBCWorker insert failed.");
			logger.error(preparedStatement);
			logger.error(DebugUtil.printStack(e));
			for (StatLogDBData data : datas) {
				logger.error("stat type: " + data.statType + " " + data.toString());
			}
		} finally {
			if (preparedStatement != null) {
				try {
					preparedStatement.close();
				} catch (SQLException e) {
					logger.error("execute StatBatchSaveJDBCWorker insert preparedStatement.close failed.", e);
				}
			}
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					logger.error("execute StatBatchSaveJDBCWorker insert connection.close failed.", e);
				}
			}
		}
	}

}
