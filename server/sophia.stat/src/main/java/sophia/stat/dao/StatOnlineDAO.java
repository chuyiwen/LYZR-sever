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

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import sophia.foundation.util.DebugUtil;
import sophia.stat.ConnectionFactory;

public class StatOnlineDAO {

	private static Logger logger = Logger.getLogger(StatLogDAO.class);

	public void insert(int year, int month, int day, int hour, int minute, int total, int total_inc, int online, int connected, int connected_ips, int logged, int logged_uids,
			int entered, int entered_uids) {

		Connection connection = null;
		CallableStatement cs = null;

		try {
			connection = ConnectionFactory.getConnection();
			cs = connection
					.prepareCall("insert into stat_player_online_log(`year`,`month`,`day`,`hour`,`minute`,`total`,`total_inc`,`online`,`connected`,`connected_ips`,`logged`,`logged_uids`,`entered`,`entered_uids`) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

			cs.setInt(1, year);
			cs.setInt(2, month);
			cs.setInt(3, day);
			cs.setInt(4, hour);
			cs.setInt(5, minute);
			cs.setInt(6, total);
			cs.setInt(7, total_inc);
			cs.setInt(8, online);
			cs.setInt(9, connected);
			cs.setInt(10, connected_ips);
			cs.setInt(11, logged);
			cs.setInt(12, logged_uids);
			cs.setInt(13, entered);
			cs.setInt(14, entered_uids);
			cs.execute();

		} catch (SQLException e) {
			logger.error(DebugUtil.printStack(e));
		} finally {
			if (cs != null) {
				try {
					cs.close();
				} catch (SQLException e) {
					logger.error("execute insert stat_player_online_log cs.close failed.", e);
				}
			}
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					logger.error("execute insert stat_player_online_log connection.close failed.", e);
				}
			}
		}
	}
}
