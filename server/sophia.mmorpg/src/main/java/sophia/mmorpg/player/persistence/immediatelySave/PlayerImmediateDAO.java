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
package sophia.mmorpg.player.persistence.immediatelySave;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

import org.apache.log4j.Logger;

import sophia.foundation.data.AbstractSaveableObject;
import sophia.foundation.data.PersistenceParameter;
import sophia.foundation.util.DebugUtil;
import sophia.game.persistence.ObjectDAO;
import sophia.mmorpg.player.Player;

import com.google.common.base.Strings;

public class PlayerImmediateDAO extends ObjectDAO<Player> {

	private static final Logger logger = Logger.getLogger(PlayerImmediateDAO.class);

	private static final String table = "no_delay_player";

	private PlayerImmediateDAO() {

	}

	private static PlayerImmediateDAO playerDAO = new PlayerImmediateDAO();

	public static PlayerImmediateDAO getInstance() {
		return playerDAO;
	}

	@Override
	protected String getInstertSql() {
		return "{call newNoDelayPlayer(?, ?, ?, ?, ?, ?, ?, ?, ?, ? ,?, ?)}";
	}

	@Override
	protected AbstractSaveableObject getInsertData(Player t) {
		return t.getPlayerImmediateSaveComponent().getPlayerImmediateSaveableObject();
	}

	@Override
	protected String getUpdateSql() {
		return "{call updateNoDelayPlayer(?, ?, ?, ?, ?, ? ,?,?, ?, ? ,?, ?)}";
	}

	@Override
	protected AbstractSaveableObject getUpdateData(Player t) {
		return t.getPlayerImmediateSaveComponent().getPlayerImmediateSaveableObject();
	}

	@Override
	protected String getDeleteSql() {
		return null;
	}

	@Override
	protected AbstractSaveableObject getDeleteData(Player t) {
		return null;
	}

	protected String getSelectByIdSql() {
		return "select * from " + table + " where id = ?";
	}

	public boolean selectPlayerImmediateData(Player player) {
		String sql = getSelectByIdSql();
		if (Strings.isNullOrEmpty(sql)) {
			logger.error("selectPlayerImmediateData error, sql sentence is null.");
			return false;
		}

		Connection conn = null;
		PreparedStatement pstat = null;
		ResultSet resultSet = null;
		try {
			conn = getConnection();
			pstat = conn.prepareStatement(sql);
			pstat.setObject(1, player.getId());
			
			if(logger.isDebugEnabled()){
				logger.debug("execute sql=["+ pstat.toString() +"]");
			}
			
			boolean hasResult = false;
			resultSet = pstat.executeQuery();
			while (resultSet.next()) {
				PlayerImmediateSaveComponent saveComponent = player.getPlayerImmediateSaveComponent();
				Collection<PersistenceParameter> persistenceParameters = saveComponent.getPlayerImmediateSaveableObject().getPersistenceParameters();
				for (PersistenceParameter persistenceParameter : persistenceParameters) {
					persistenceParameter.setValue(resultSet.getObject(persistenceParameter.getName()));
				}
				
				try {
					saveComponent.setDataFrom(persistenceParameters);
				} catch (Exception e) {
					logger.error("selectPlayerImmediateData setDataFrom error " + player);
					logger.error("selectPlayerImmediateData setDataFrom error " + DebugUtil.printStack(e));
					return false;
				}
				
				hasResult = true;
			}
			
			if (hasResult) {
				return true;
			}
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
		
		return false;
	}
}
