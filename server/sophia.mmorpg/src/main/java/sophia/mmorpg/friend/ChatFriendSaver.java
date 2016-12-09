/**
 * 
 */
package sophia.mmorpg.friend;

import java.sql.SQLException;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import sophia.foundation.util.DebugUtil;
import sophia.mmorpg.friend.persistence.ChatFriendDAO;

/**
 * @author Administrator
 * 
 */
public class ChatFriendSaver {
	private static Logger logger = Logger.getLogger(ChatFriendSaver.class);
	private static ChatFriendSaver instance = new ChatFriendSaver();

	private static Set<PlayerChatFriendMgr> updateImmediateSetPrimary = Collections.newSetFromMap(new ConcurrentHashMap<PlayerChatFriendMgr, Boolean>());
	private static Set<PlayerChatFriendMgr> updateImmediateSetSecondary = Collections.newSetFromMap(new ConcurrentHashMap<PlayerChatFriendMgr, Boolean>());

	private static Set<PlayerChatFriendMgr> insertImmediateSetPrimary = Collections.newSetFromMap(new ConcurrentHashMap<PlayerChatFriendMgr, Boolean>());
	private static Set<PlayerChatFriendMgr> insertImmediateSetSecondary = Collections.newSetFromMap(new ConcurrentHashMap<PlayerChatFriendMgr, Boolean>());

	private ChatFriendSaver() {
	}

	public static ChatFriendSaver getInstance() {
		return instance;
	}

	public void save() throws SQLException {
		insert();
		if (updateImmediateSetPrimary.isEmpty()) {
			return;
		}
		updateImmediateSetSecondary.addAll(updateImmediateSetPrimary);
		updateImmediateSetPrimary.removeAll(updateImmediateSetSecondary);
		for (PlayerChatFriendMgr playerChatFriendMgr : updateImmediateSetSecondary) {
			try {
				ChatFriendDAO.getInstance().update(playerChatFriendMgr);
			} catch (Exception e) {
				updateImmediateSetPrimary.add(playerChatFriendMgr);
				logger.error("update error, playerId =" + playerChatFriendMgr.getOwnerId());
				logger.error("update error, " + DebugUtil.printStack(e));
			}
		}
		updateImmediateSetSecondary.clear();
	}

	public void insert() {
		if (insertImmediateSetPrimary.isEmpty()) {
			return;
		}
		insertImmediateSetSecondary.addAll(insertImmediateSetPrimary);
		insertImmediateSetPrimary.removeAll(insertImmediateSetSecondary);
		for (PlayerChatFriendMgr playerChatFriendMgr : insertImmediateSetSecondary) {
			try {
				ChatFriendDAO.getInstance().insert(playerChatFriendMgr);
			} catch (Exception e) {
				insertImmediateSetPrimary.add(playerChatFriendMgr);
				logger.error("insert error, playerId =" + playerChatFriendMgr.getOwnerId());
				logger.error("insert error, " + DebugUtil.printStack(e));
			}
		}
		insertImmediateSetSecondary.clear();
	}

	public void shutDownSave() throws SQLException {

		save();

		if (!insertImmediateSetPrimary.isEmpty()) {
			throw new RuntimeException("shutDownInsert save Data Error!!!");
		}

		if (!updateImmediateSetPrimary.isEmpty()) {
			throw new RuntimeException("shutDownSave save Data Error!!!");
		}
	}

	public void saveImmediateData(PlayerChatFriendMgr playerChatFriendMgr) {
		updateImmediateSetPrimary.add(playerChatFriendMgr);
	}

	public void insertImmediateData(PlayerChatFriendMgr playerChatFriendMgr) {
		insertImmediateSetPrimary.add(playerChatFriendMgr);
	}

	public static Set<PlayerChatFriendMgr> getUpdateImmediateSetPrimary() {
		return updateImmediateSetPrimary;
	}

	public static Set<PlayerChatFriendMgr> getUpdateImmediateSetSecondary() {
		return updateImmediateSetSecondary;
	}

	public Set<PlayerChatFriendMgr> getInsertImmediateSetPrimary() {
		return insertImmediateSetPrimary;
	}

	public Set<PlayerChatFriendMgr> getInsertImmediateSetSecondary() {
		return insertImmediateSetSecondary;
	}

	public static PlayerChatFriendMgr getPlayerChatFriendMgrFromUpdate(String ownerId) {
		for (PlayerChatFriendMgr playerChatFriendMgr : updateImmediateSetPrimary) {
			if (StringUtils.equals(ownerId, playerChatFriendMgr.getOwnerId())) {
				return playerChatFriendMgr;
			}
		}
		return null;
	}

	public static PlayerChatFriendMgr getPlayerChatFriendMgrFromInsert(String ownerId) {
		for (PlayerChatFriendMgr playerChatFriendMgr : insertImmediateSetPrimary) {
			if (StringUtils.equals(ownerId, playerChatFriendMgr.getOwnerId())) {
				return playerChatFriendMgr;
			}
		}
		return null;
	}

}
