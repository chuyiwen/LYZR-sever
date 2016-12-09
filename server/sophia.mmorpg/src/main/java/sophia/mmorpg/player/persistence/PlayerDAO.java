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
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;

import sophia.foundation.authentication.Identity;
import sophia.foundation.data.AbstractSaveableObject;
import sophia.foundation.data.PersistenceParameter;
import sophia.foundation.property.PropertyDictionary;
import sophia.foundation.util.DebugUtil;
import sophia.game.persistence.ObjectDAO;
import sophia.mmorpg.GameObjectFactory;
import sophia.mmorpg.auth.AuthIdentity;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.property.CharacterInfo;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;

import com.google.common.base.Strings;

public class PlayerDAO extends ObjectDAO<Player> {

	private static final Logger logger = Logger.getLogger(PlayerDAO.class);

	private static final String table = "player";

	private PlayerDAO() {

	}

	private static PlayerDAO playerDAO = new PlayerDAO();

	public static PlayerDAO getInstance() {
		return playerDAO;
	}

	@Override
	protected String getInstertSql() {
		return "{call newPlayer(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?, ?, ?, ?)}";
	}

	@Override
	protected AbstractSaveableObject getInsertData(Player t) {
		return t.getPlayerSaveComponent().getPlayerSaveableObject();
	}

	@Override
	protected String getUpdateSql() {
		return "{call updatePlayer(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";
	}

	@Override
	protected AbstractSaveableObject getUpdateData(Player t) {
		return t.getPlayerSaveComponent().getPlayerSaveableObject();
	}

	@Override
	protected String getDeleteSql() {
		return "delete from player where id = ?";
	}

	@Override
	protected AbstractSaveableObject getDeleteData(Player t) {
		return null;
	}

	protected String getSelectByIdentitySql() {
		return "select id, name, propertyData from " + table + " where identityId = ?";
	}

	protected String getSelectNameSql() {
		return "select name from " + table;
	}

	protected String getSelectByPlayerIdSql() {
		return "select * from " + table + " where id = ?";
	}
	
	protected String getSelectByPlayerNameSql() {
		return "select * from " + table + " where name = ?";
	}

	protected String getSelectPlayers() {
		return "select * from "+ table +" order by lastLoginTime desc,`level` desc limit ?";
	}
	
	protected String getSelectPlayerTotalSql() {
		return "select count(id) from "+ table;
	}
	
	public int getSelectPlayerTotal() {
		String sql = getSelectPlayerTotalSql();
		if (Strings.isNullOrEmpty(sql)) {
			logger.error("sql sentence is null.");
			return 0;
		}
		Connection conn = null;
		PreparedStatement pstat = null;
		ResultSet resultSet = null;
		int count = 0;
		try {
			conn = getConnection();
			pstat = conn.prepareStatement(sql);
					
			if(logger.isDebugEnabled()){
				logger.debug("execute sql=[" + pstat.toString() + "]");
			}
			
			resultSet = pstat.executeQuery();
			while (resultSet.next()) {
				count = resultSet.getInt(1);
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

		return count;
	}
	public List<CharacterInfo> selectPlayerListByIdentity(Identity identity) {
		String sql = getSelectByIdentitySql();
		if (Strings.isNullOrEmpty(sql)) {
			logger.error("sql sentence is null.");
			return null;
		}

		List<CharacterInfo> charList = new ArrayList<>();

		Connection conn = null;
		PreparedStatement pstat = null;
		ResultSet resultSet = null;
		try {
			conn = getConnection();
			pstat = conn.prepareStatement(sql);
			pstat.setObject(1, identity.getId());
			
			if(logger.isDebugEnabled()){
				logger.debug("execute sql=[" + pstat.toString() + "]");
			}
			
			resultSet = pstat.executeQuery();
			while (resultSet.next()) {
				byte[] propertyData = resultSet.getBytes("propertyData");
				PropertyDictionary pd = new PropertyDictionary();
				pd.loadDictionary(propertyData);
				CharacterInfo charInfo = new CharacterInfo();
				charInfo.setId(resultSet.getString("id"));
				charInfo.setName(resultSet.getString("name"));
				charInfo.setLevel(MGPropertyAccesser.getLevel(pd));
				charInfo.setGender(MGPropertyAccesser.getGender(pd));
				charInfo.setProfession(MGPropertyAccesser.getProfessionId(pd));
				long lastLoginTime = MGPropertyAccesser.getLastLoginTime(pd);
				if (lastLoginTime == PropertyDictionary.LongNull) {
					lastLoginTime = 0;
				}
				charInfo.setLastLoginTime(lastLoginTime);
				charList.add(charInfo);
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

		return charList;
	}

	public Player selectPlayer(String playerId) {
		Player ret = null;

		String sql = getSelectByPlayerIdSql();
		if (Strings.isNullOrEmpty(sql)) {
			logger.error("selectPlayer sql sentence is null.");
			return null;
		}

		Connection conn = null;
		PreparedStatement pstat = null;
		ResultSet resultSet = null;
		try {
			conn = getConnection();
			pstat = conn.prepareStatement(sql);
			pstat.setObject(1, playerId);
			
			if(logger.isDebugEnabled()){
				logger.debug("execute sql=[" + pstat.toString() + "]");
			}
			
			resultSet = pstat.executeQuery();
			while (resultSet.next()) {
				String id = resultSet.getString("id");
				String name = resultSet.getString("name");
				AuthIdentity identity = new AuthIdentity();
				identity.setCharId(id);
				identity.setId(resultSet.getString("identityId"));
				identity.setName(resultSet.getString("identityName"));
				identity.setQdCode1(resultSet.getInt("qdCode1"));
				identity.setQdCode2(resultSet.getInt("qdCode2"));
				ret = GameObjectFactory.getPlayer(identity, id, name);
				ret.setId(id);
				ret.setName(name);
				MGPropertyAccesser.setOrPutName(ret.getProperty(), name);
				PlayerSaveComponent saveComponent = ret.getPlayerSaveComponent();
				Collection<PersistenceParameter> persistenceParameters = saveComponent.getPlayerSaveableObject().getPersistenceParameters();
				for (PersistenceParameter persistenceParameter : persistenceParameters) {
					persistenceParameter.setValue(resultSet.getObject(persistenceParameter.getName()));
				}
				
				try {
					saveComponent.setDataFrom(persistenceParameters);
				} catch (Exception e) {
					logger.error("selectPlayer setDataFrom error, playerName=" + name);
					logger.error("selectPlayer setDataFrom error, " + DebugUtil.printStack(e));
					return null;
				}
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

		return ret;
	}

	public boolean deletePlayer(String playerId) {
		boolean ret = false;

		String sql = getDeleteSql();
		if (Strings.isNullOrEmpty(sql)) {
			logger.error("sql sentence is null.");
			return false;
		}

		Connection conn = null;
		PreparedStatement pstat = null;
		try {
			conn = getConnection();
			pstat = conn.prepareStatement(sql);
			pstat.setObject(1, playerId);
			ret = pstat.executeUpdate() > 0;
			
			if(logger.isDebugEnabled()){
				logger.debug("execute sql=["+pstat.toString()+"]");
			}
			
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

		return ret;
	}

	public List<String> selectPlayerName() {
		String sql = getSelectNameSql();
		if (Strings.isNullOrEmpty(sql)) {
			logger.error("sql sentence is null.");
			return null;
		}

		Connection conn = null;
		ResultSet resultSet = null;
		Statement stat = null;

		List<String> playerNameList = new ArrayList<>();
		try {
			conn = getConnection();
			stat = conn.prepareCall(sql);
			
			if(logger.isDebugEnabled()){
				logger.debug("execute sql=["+ stat.toString() + "]");
			}
			
			resultSet = stat.executeQuery(sql);
			while (resultSet.next()) {
				playerNameList.add(resultSet.getString("name"));
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
			if (stat != null) {
				try {
					stat.close();
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
		return playerNameList;
	}
	
	public Player selectPlayerByName(String playerName) {
		Player ret = null;

		String sql = getSelectByPlayerNameSql();
		if (Strings.isNullOrEmpty(sql)) {
			logger.error("sql sentence is null.");
			return null;
		}

		Connection conn = null;
		PreparedStatement pstat = null;
		ResultSet resultSet = null;
		try {
			conn = getConnection();
			pstat = conn.prepareStatement(sql);
			pstat.setObject(1, playerName);
			
			if(logger.isDebugEnabled()){
				logger.debug("execute sql=[" + pstat.toString() + "]");
			}
			
			resultSet = pstat.executeQuery();
			while (resultSet.next()) {
				String id = resultSet.getString("id");
				String name = resultSet.getString("name");
				AuthIdentity identity = new AuthIdentity();
				identity.setCharId(id);
				identity.setId(resultSet.getString("identityId"));
				identity.setName(resultSet.getString("identityName"));
				identity.setQdCode1(resultSet.getInt("qdCode1"));
				identity.setQdCode2(resultSet.getInt("qdCode2"));
				ret = GameObjectFactory.getPlayer(identity, id, name);
				ret.setId(id);
				ret.setName(name);
				MGPropertyAccesser.setOrPutName(ret.getProperty(), name);
				PlayerSaveComponent saveComponent = ret.getPlayerSaveComponent();
				Collection<PersistenceParameter> persistenceParameters = saveComponent.getPlayerSaveableObject().getPersistenceParameters();
				for (PersistenceParameter persistenceParameter : persistenceParameters) {
					persistenceParameter.setValue(resultSet.getObject(persistenceParameter.getName()));
				}
				
				try {
					saveComponent.setDataFrom(persistenceParameters);
				} catch (Exception e) {
					logger.error("selectPlayerByName setDataFrom error, playerName=" + name);
					logger.error("selectPlayerByName setDataFrom error, " + DebugUtil.printStack(e));
					return null;
				}
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

		return ret;
	}
	
	public List<Player> selectPlayers(int number) {
		List<Player> ret = new ArrayList<>();

		String sql = getSelectPlayers();
		if (Strings.isNullOrEmpty(sql)) {
			logger.error("sql sentence is null.");
			return null;
		}

		Connection conn = null;
		PreparedStatement pstat = null;
		ResultSet resultSet = null;
		try {
			conn = getConnection();
			pstat = conn.prepareStatement(sql);
			pstat.setObject(1, number);
			resultSet = pstat.executeQuery();
			while (resultSet.next()) {
				Player temp = null;
				String id = resultSet.getString("id");
				String name = resultSet.getString("name");
				long birthday = resultSet.getLong("birthday");
				long lastLoginTime = resultSet.getLong("lastLoginTime");
				long lastLogoutTime = resultSet.getLong("lastLogoutTime");
				int level = resultSet.getInt("level");
				AuthIdentity identity = new AuthIdentity();
				identity.setCharId(id);
				identity.setId(resultSet.getString("identityId"));
				identity.setName(resultSet.getString("identityName"));
				identity.setQdCode1(resultSet.getInt("qdCode1"));
				identity.setQdCode2(resultSet.getInt("qdCode2"));
				temp = GameObjectFactory.getPlayer(identity, id, name);
				temp.setId(id);
				temp.setName(name);
				MGPropertyAccesser.setOrPutName(temp.getProperty(), name);
				MGPropertyAccesser.setOrPutBirthday(temp.getProperty(), birthday);
				MGPropertyAccesser.setOrPutLastLoginTime(temp.getProperty(), lastLoginTime);
				MGPropertyAccesser.setOrPutLastLogoutTime(temp.getProperty(), lastLogoutTime);
				MGPropertyAccesser.setOrPutLevel(temp.getProperty(), level);
				PlayerSaveComponent saveComponent = temp.getPlayerSaveComponent();
				Collection<PersistenceParameter> persistenceParameters = saveComponent.getPlayerSaveableObject().getPersistenceParameters();
				for (PersistenceParameter persistenceParameter : persistenceParameters) {
					persistenceParameter.setValue(resultSet.getObject(persistenceParameter.getName()));
				}
				
				// 确保预加载成功
				try {
					saveComponent.setDataFrom(persistenceParameters);
				} catch (Exception e) {
					logger.error("setDataFrom error " + temp);
					logger.error("setDataFrom error, " + DebugUtil.printStack(e));
					continue;
				}
				
				ret.add(temp);
			}
			
			if(logger.isDebugEnabled()){
				logger.debug("execute sql=["+pstat.toString()+"]");
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

		return ret;
	}
}
