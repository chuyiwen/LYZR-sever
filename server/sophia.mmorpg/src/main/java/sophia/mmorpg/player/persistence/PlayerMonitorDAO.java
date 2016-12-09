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
package sophia.mmorpg.player.persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.util.DebugUtil;
import sophia.foundation.util.MonitorClientEvent;
import sophia.game.persistence.ConnectionManager;
import sophia.mmorpg.player.Player;

public final class PlayerMonitorDAO {
	
	private static Logger logger = Logger.getLogger(PlayerMonitorDAO.class);
	
	private static final String tableName = "monitor";
	
	private PlayerMonitorDAO() {	
	}
	
	private static PlayerMonitorDAO playerMonitorDAO = new PlayerMonitorDAO();
	
	public static PlayerMonitorDAO getInstance() {
		return playerMonitorDAO;
	}
	
	public String getSelectSql() {
		return "select playerId from " + tableName + " where playerId = ?";
	}
	
	public String getInsertSql() {
		return "insert into " + tableName + " set playerId = ?, data = ?";
	}
	
	public String getUpdateSql() {
		return "update " + tableName + " set data = ? where playerId = ?";
	}
	
	public ResultSet selectData(Player player) {
		String sql = getSelectSql();
		
		Connection conn = null;
		PreparedStatement pstat = null;
		ResultSet resultSet = null;
		
		try {
			conn = ConnectionManager.getConnection();
			pstat = conn.prepareStatement(sql);
			pstat.setObject(1, player.getId());
			resultSet = pstat.executeQuery();
			if (!resultSet.next()) {
				return null;
			}
			resultSet.previous();
		} catch (SQLException e) {
			logger.error(DebugUtil.printStack(e));
		} finally {
			if (resultSet != null) {
				try {
					resultSet.close();
				} catch (SQLException e) {
				}
			}
			if (pstat != null) {
				try {
					pstat.close();
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
		
		return resultSet;
	}
	
	public void insertData(Player player) {
		String sql = getInsertSql();
		
		Connection conn = null;
		PreparedStatement pstat = null;
		
		List<byte[]> eventList = MonitorClientEvent.getInstance().getEventList(player.getIdentity());
		if(eventList == null) {
			return;
		}
		
		IoBuffer writeBuff = IoBuffer.allocate(4096).setAutoExpand(true);
		for (byte[] bs : eventList) {
			writeBuff.put(bs);
		}
		
		writeBuff.flip();
		byte[] data = new byte[writeBuff.remaining()];
		writeBuff.get(data);
		
		try {
			conn = ConnectionManager.getConnection();
			pstat = conn.prepareStatement(sql);
			pstat.setObject(1, player.getId());
			pstat.setObject(2, data);
			pstat.executeUpdate();
		} catch (SQLException e) {
			logger.error(DebugUtil.printStack(e));
		} finally {
			if (pstat != null) {
				try {
					pstat.close();
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
		
		Connection conn = null;
		PreparedStatement pstat = null;
		
		List<byte[]> eventList = MonitorClientEvent.getInstance().getEventList(player.getIdentity());
		if(eventList == null) {
			return;
		}
		
		IoBuffer writeBuff = IoBuffer.allocate(4096).setAutoExpand(true);
		for (byte[] bs : eventList) {
			writeBuff.put(bs);
		}
		
		writeBuff.flip();
		byte[] data = new byte[writeBuff.remaining()];
		writeBuff.get(data);
		
		try {
			conn = ConnectionManager.getConnection();
			pstat = conn.prepareStatement(sql);
			pstat.setObject(1, data);
			pstat.setObject(2, player.getId());
			pstat.executeUpdate();
		} catch (SQLException e) {
			logger.error(DebugUtil.printStack(e));
		} finally {
			if (pstat != null) {
				try {
					pstat.close();
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
	
	public void save(Player player) {
		if (selectData(player) != null) {
			updateData(player);
		} else {
			insertData(player);
		}
		
		MonitorClientEvent.getInstance().getEventMap().clear();
	}
}
