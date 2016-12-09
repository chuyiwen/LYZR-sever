package sophia.mmorpg.friend.persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import sophia.foundation.util.DebugUtil;
import sophia.game.persistence.ConnectionManager;
import sophia.mmorpg.friend.FriendMember;
import sophia.mmorpg.friend.FriendType;
import sophia.mmorpg.friend.PlayerChatFriendMgr;

import com.google.common.base.Strings;

public class ChatFriendDAO {

	private static Logger logger = Logger.getLogger(ChatFriendDAO.class);

	private static ChatFriendDAO instance = new ChatFriendDAO();

	private static final String table = "game_chatfriend";

	private ChatFriendPersistenceObject persistenceObject = ChatFriendPersistenceObject.getInstance();

	public static ChatFriendDAO getInstance() {
		return instance;
	}

	protected String getSelectSql() {
		String selectSql = "select tempFriend,friend,black,enmey from " + table + " where id = ?";
		return selectSql;
	}

	protected String getInstertSql() {
		String insertSql = "insert into " + table + " (id, tempFriend, friend, black, enmey) values(?,?,?,?,?)";
		return insertSql;
	}

	protected String getUpdateSql() {
		return "update " + table + " set tempFriend = ?,friend = ?,black = ? ,enmey = ? where id = ?";
	}

	public void update(PlayerChatFriendMgr playerChatFriendMgr) throws SQLException {
		String sql = getUpdateSql();
		if (Strings.isNullOrEmpty(sql)) {
			logger.error("sql sentence is null.");
			return;
		}

		Connection conn = ConnectionManager.getConnection();
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(sql);
			Map<String, FriendMember> tempFriend = playerChatFriendMgr.getPlayerListByGroupType(FriendType.TemporaryList);
			Map<String, FriendMember> friend = playerChatFriendMgr.getPlayerListByGroupType(FriendType.FriendList);
			Map<String, FriendMember> black = playerChatFriendMgr.getPlayerListByGroupType(FriendType.BlackList);
			Map<String, FriendMember> enemy = playerChatFriendMgr.getPlayerListByGroupType(FriendType.EnermyList);

			String ownerId = playerChatFriendMgr.getOwnerId();
			byte[] tempFriendBytes = persistenceObject.toBytes(tempFriend);
			byte[] friendBytes = persistenceObject.toBytes(friend);
			byte[] blackBytes = persistenceObject.toBytes(black);
			byte[] enemyBytes = persistenceObject.toBytes(enemy);

			ps.setBytes(1, tempFriendBytes);
			ps.setBytes(2, friendBytes);
			ps.setBytes(3, blackBytes);
			ps.setBytes(4, enemyBytes);
			ps.setString(5, ownerId);
			ps.execute();
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

	public void insert(PlayerChatFriendMgr playerChatFriendMgr) throws SQLException {

		String sql = getInstertSql();
		if (Strings.isNullOrEmpty(sql)) {
			logger.error("sql sentence is null.");
			return;
		}

		Connection conn = ConnectionManager.getConnection();
		PreparedStatement ps = null;

		try {
			ps = conn.prepareStatement(sql);
			Map<String, FriendMember> tempFriend = playerChatFriendMgr.getPlayerListByGroupType(FriendType.TemporaryList);
			Map<String, FriendMember> friend = playerChatFriendMgr.getPlayerListByGroupType(FriendType.FriendList);
			Map<String, FriendMember> black = playerChatFriendMgr.getPlayerListByGroupType(FriendType.BlackList);
			Map<String, FriendMember> enemy = playerChatFriendMgr.getPlayerListByGroupType(FriendType.EnermyList);

			String ownerId = playerChatFriendMgr.getOwnerId();
			byte[] tempFriendBytes = persistenceObject.toBytes(tempFriend);
			byte[] friendBytes = persistenceObject.toBytes(friend);
			byte[] blackBytes = persistenceObject.toBytes(black);
			byte[] enemyBytes = persistenceObject.toBytes(enemy);

			ps.setString(1, ownerId);
			ps.setBytes(2, tempFriendBytes);
			ps.setBytes(3, friendBytes);
			ps.setBytes(4, blackBytes);
			ps.setBytes(5, enemyBytes);

			ps.execute();
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

	public Map<Byte, ConcurrentHashMap<String, FriendMember>> selectFriend(String playerId) {
		String sql = getSelectSql();
		if (Strings.isNullOrEmpty(sql)) {
			logger.error("sql sentence is null.");
			return null;
		}

		Connection conn = ConnectionManager.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		Map<Byte, ConcurrentHashMap<String, FriendMember>> friendMapping = new HashMap<Byte, ConcurrentHashMap<String, FriendMember>>();
		try {

			ps = conn.prepareStatement(sql);
			ps.setString(1, playerId);
			rs = ps.executeQuery();

			while (rs.next()) {
				ConcurrentHashMap<String, FriendMember> tempFriend = persistenceObject.fromBytes(rs.getBytes(1));
				ConcurrentHashMap<String, FriendMember> friend = persistenceObject.fromBytes(rs.getBytes(2));
				ConcurrentHashMap<String, FriendMember> black = persistenceObject.fromBytes(rs.getBytes(3));
				ConcurrentHashMap<String, FriendMember> enermy = persistenceObject.fromBytes(rs.getBytes(4));

				friendMapping.put(FriendType.TemporaryList, tempFriend);
				friendMapping.put(FriendType.FriendList, friend);
				friendMapping.put(FriendType.BlackList, black);
				friendMapping.put(FriendType.EnermyList, enermy);

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

		return friendMapping;

	}

}
