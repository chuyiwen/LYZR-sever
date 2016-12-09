package sophia.mmorpg.friend;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import sophia.mmorpg.player.Player;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;

public class PlayerChatFriendMgr {
	private static final Logger logger = Logger.getLogger(PlayerChatFriendMgr.class);
	private static final int MaxFriendNum = 50;

	private String ownerId;
	
	public PlayerChatFriendMgr(String playerId) {
		this.ownerId = playerId;
	}
	
	public String getOwnerId() {
		return ownerId;
	}

	private Map<String, FriendMember> temporaryFriendMapping = new ConcurrentHashMap<String, FriendMember>();
	private Map<String, FriendMember> friendMapping = new ConcurrentHashMap<String, FriendMember>();
	private Map<String, FriendMember> blackListMapping = new ConcurrentHashMap<String, FriendMember>();
	private Map<String, FriendMember> enemyListMapping = new ConcurrentHashMap<String, FriendMember>();

	public void setFriendListMapping(Map<Byte, ConcurrentHashMap<String, FriendMember>> mapping) {
		ConcurrentHashMap<String, FriendMember>  temp = mapping.get(FriendType.TemporaryList);
		ConcurrentHashMap<String, FriendMember>  friend = mapping.get(FriendType.FriendList);
		ConcurrentHashMap<String, FriendMember>  black = mapping.get(FriendType.EnermyList);
		ConcurrentHashMap<String, FriendMember>  enemy = mapping.get(FriendType.BlackList);

		if (temp != null) {
			for (Entry<String, FriendMember> entry : temp.entrySet()) {
				temporaryFriendMapping.put(entry.getKey(), entry.getValue());
			}
		}
		
		if (friend != null) {
			for (Entry<String, FriendMember> entry : friend.entrySet()) {
				friendMapping.put(entry.getKey(), entry.getValue());
			}
		}
		
		if (black != null) {
			for (Entry<String, FriendMember> entry : black.entrySet()) {
				blackListMapping.put(entry.getKey(), entry.getValue());
			}
		}
		
		if (enemy != null) {
			for (Entry<String, FriendMember> entry : enemy.entrySet()) {
				enemyListMapping.put(entry.getKey(), entry.getValue());
			}
		}
	
	}
	
	
	public List<ChatRecord> getOfflineMessage() {
		List<ChatRecord> offlineMessages = new ArrayList<ChatRecord>();
		
		for (FriendMember friendMember : temporaryFriendMapping.values()) {
			List<ChatRecord> offlineMessage = friendMember.getOfflineMessage();
			offlineMessages.addAll(offlineMessage);
		}
		
		for (FriendMember friendMember : friendMapping.values()) {
			List<ChatRecord> offlineMessage = friendMember.getOfflineMessage();
			offlineMessages.addAll(offlineMessage);
		}
		
		for (FriendMember friendMember : enemyListMapping.values()) {
			List<ChatRecord> offlineMessage = friendMember.getOfflineMessage();
			offlineMessages.addAll(offlineMessage);
		}
		
		return offlineMessages;
	}
	
	
	public FriendMember getFriendMember(byte friendType, String playerId) {
		Map<String, FriendMember> playerListByGroupType = getPlayerListByGroupType(friendType);
		
		FriendMember friendMember = playerListByGroupType.get(playerId);
	
		return friendMember;
	}
	
	public void addChatRecord(byte friendType, String playerId, ChatRecord chatRecord) {
		FriendMember friendMember = getFriendMember(friendType, playerId);
		
		if (friendMember == null) {
			if (logger.isDebugEnabled()) {
				logger.debug("playerId = " + playerId + ", is not in list! friendType = " + friendType + " add chat fail!");
			}
			return;
		}
		
		friendMember.addChatRecord(chatRecord);
	}
	
	// ================================================
	
	/**
	 * 增加临时好友
	 * 
	 * @param playerId
	 * @param temporaryFriendPlayerId
	 */
	private void addTemporaryFriendPlayer(Player player) {
		String temporaryFriendPlayerId = player.getId();
		String playerName = player.getName();
		byte profession = player.getProfession();
		byte gender = MGPropertyAccesser.getGender(player.getProperty());
		
		FriendMember friendMember = removeIfExit(friendMapping, temporaryFriendPlayerId);
		if (friendMember != null) {
			addIfNotExit(temporaryFriendMapping, friendMember);
			return;
		}
		
		FriendMember blackMember = removeIfExit(blackListMapping, temporaryFriendPlayerId);
		if (blackMember != null) {
			addIfNotExit(temporaryFriendMapping, blackMember);
			return;
		}
		
		FriendMember enemyMember = removeIfExit(enemyListMapping, temporaryFriendPlayerId);
		if (enemyMember != null) {
			addIfNotExit(temporaryFriendMapping, enemyMember);
			return;
		}
		
		FriendMember temporaryFriendMember = new FriendMember(temporaryFriendPlayerId, playerName, profession, gender);
		addIfNotExit(temporaryFriendMapping, temporaryFriendMember);
	}

	/**
	 * 删除临时好友
	 * 
	 * @param playerId
	 * @param temporaryFriendPlayerId
	 */
	private boolean removeTemporaryFriendPlayer(String temporaryFriendPlayerId) {
		FriendMember removeIfExit = removeIfExit(temporaryFriendMapping, temporaryFriendPlayerId);

		return removeIfExit != null;
	}

	/**
	 * 获取临时好友Set
	 * 
	 * @param playerId
	 * @return
	 */
	private Map<String, FriendMember> getTemporaryFriendPlayerSet() {
		return temporaryFriendMapping;
	}
	
	/**
	 * 增加好友
	 * 
	 * @param playerId
	 * @param friendPlayerId
	 */
	private void addFriendPlayer(Player player) {
		String friendPlayerId = player.getId();
		String playerName = player.getName();
		byte profession = player.getProfession();
		byte gender = MGPropertyAccesser.getGender(player.getProperty());
		
		FriendMember temporaryMember = removeIfExit(temporaryFriendMapping, friendPlayerId);
		if (temporaryMember != null) {
			addIfNotExit(friendMapping, temporaryMember);
			return;
		}
		
		FriendMember blackMember = removeIfExit(blackListMapping, friendPlayerId);
		if (blackMember != null) {
			addIfNotExit(friendMapping, blackMember);
			return;
		}
		
		FriendMember enemyMember = removeIfExit(enemyListMapping, friendPlayerId);
		if (enemyMember != null) {
			addIfNotExit(friendMapping, enemyMember);
			return;
		}
		
		FriendMember friendMember = new FriendMember(friendPlayerId, playerName, profession, gender);
		addIfNotExit(friendMapping, friendMember);
	}

	/**
	 * 删除好友
	 * 
	 * @param playerId
	 * @param friendPlayerId
	 */
	private boolean removeFriendPlayer(String friendPlayerId) {
		FriendMember removeIfExit = removeIfExit(friendMapping, friendPlayerId);
	
		return removeIfExit != null;
	}

	/**
	 * 获取好友Set
	 * 
	 * @param playerId
	 * @return
	 */
	private Map<String, FriendMember> getFriendPlayerSet() {
		return friendMapping;
	}

	/**
	 * 增加黑名单
	 * 
	 * @param playerId
	 * @param blackListPlayerId
	 */
	private void addBlackListPlayer(Player player) {
		String blackListPlayerId = player.getId();
		String playerName = player.getName();
		byte profession = player.getProfession();
		byte gender = MGPropertyAccesser.getGender(player.getProperty());
		
		FriendMember temporaryMember = removeIfExit(temporaryFriendMapping, blackListPlayerId);
		if (temporaryMember != null) {
			addIfNotExit(blackListMapping, temporaryMember);
			return;
		}
		
		FriendMember friendMember = removeIfExit(friendMapping, blackListPlayerId);
		if (friendMember != null) {
			addIfNotExit(blackListMapping, friendMember);
			return;
		}
		
		FriendMember enemyMember = removeIfExit(enemyListMapping, blackListPlayerId);
		if (enemyMember != null) {
			addIfNotExit(blackListMapping, enemyMember);
			return;
		}
		
		FriendMember blackMember = new FriendMember(blackListPlayerId, playerName, profession, gender);
		addIfNotExit(blackListMapping, blackMember);
	}

	/**
	 * 删除黑名单
	 * 
	 * @param playerId
	 * @param blackListPlayerId
	 */
	private boolean removeBlackListPlayer(String blackListPlayerId) {
		FriendMember removeIfExit =  removeIfExit(blackListMapping, blackListPlayerId);
		
		return removeIfExit != null;
	}

	/**
	 * 获取黑名单Set
	 * 
	 * @param playerId
	 * @return
	 */
	private Map<String, FriendMember> getBlackListPlayerSet() {
		return blackListMapping;
	}

	/**
	 * 增加仇人
	 * 
	 * @param playerId
	 * @param enermyPlayerId
	 */
	private void addEnermyPlayer(Player player) {
		String enermyPlayerId = player.getId();
		String playerName = player.getName();
		byte profession = player.getProfession();
		byte gender = MGPropertyAccesser.getGender(player.getProperty());
		
		FriendMember temporaryMember = removeIfExit(temporaryFriendMapping, enermyPlayerId);
		if (temporaryMember != null) {
			addIfNotExit(enemyListMapping, temporaryMember);
			return;
		}
		
		FriendMember friendMember = removeIfExit(friendMapping, enermyPlayerId);
		if (friendMember != null) {
			addIfNotExit(enemyListMapping, friendMember);
			return;
		}
		
		FriendMember blackMember = removeIfExit(blackListMapping, enermyPlayerId);
		if (blackMember != null) {
			addIfNotExit(enemyListMapping, blackMember);
			return;
		}
		
		FriendMember enemyMember = new FriendMember(enermyPlayerId, playerName, profession, gender);
		addIfNotExit(enemyListMapping, enemyMember);

	}

	/**
	 * 删除仇人
	 * 
	 * @param playerId
	 * @param friendPlayerId
	 */
	private boolean removeEnermyPlayer(String friendPlayerId) {
		FriendMember removeIfExit = removeIfExit(enemyListMapping, friendPlayerId);
	
		return removeIfExit != null;
	}

	/**
	 * 获取仇人Set
	 * 
	 * @param playerId
	 * @return
	 */
	private Map<String, FriendMember> getEnermyPlayerSet() {
		return enemyListMapping;
	}

	/**
	 * 移除列表中存在的玩家id
	 * 
	 * @param mapping
	 * @param toPlayerId
	 */
	private FriendMember removeIfExit(Map<String, FriendMember> mapping, String toPlayerId) {
		FriendMember friendMember = mapping.remove(toPlayerId);
		return friendMember;
	}

	/**
	 * 向列表中添加玩家id
	 * 
	 * @param mapping
	 * @param toPlayerId
	 */
	private void addIfNotExit(Map<String, FriendMember> mapping, FriendMember member) {
		Set<String> set = mapping.keySet();
		String playerId = member.getPlayerId();
		if (!set.contains(playerId)) {
			removeExcessPlayer(mapping);

			mapping.put(playerId, member);
			
		}
	}
	
	// FIXME thread safe problem
	private void removeExcessPlayer(Map<String, FriendMember> mapping) {
		int size = mapping.size();
		if (size <= MaxFriendNum - 1) {
			return;
		}
		
		List<FriendMember> friendMembers = new ArrayList<FriendMember>();
		
		for (FriendMember friendMember : mapping.values()) {
			friendMembers.add(friendMember);
		}
		
		Collections.sort(friendMembers, new FriendMemberComparator());
		
		int friendMembersSize = friendMembers.size();
		
		friendMembers.remove(friendMembersSize - 1);
	}
	
	public boolean addPlayerInListByGroupType(Player player, byte groupType) {
		if (groupType == FriendType.TemporaryList) {
			addTemporaryFriendPlayer(player);
			return true;
		} else if (groupType == FriendType.FriendList) {
			addFriendPlayer(player);
			return true;
		} else if (groupType == FriendType.BlackList) {
			addBlackListPlayer(player);
			return true;
		} else if (groupType == FriendType.EnermyList) {
			addEnermyPlayer(player);
			return true;
		}
		return false;
	}
	
	public boolean removePlayerInListByGroupType(String playerId, byte groupType) {
		if (groupType == FriendType.TemporaryList) {
			return removeTemporaryFriendPlayer(playerId);
		} else if (groupType == FriendType.FriendList) {
			return removeFriendPlayer(playerId);
		} else if (groupType == FriendType.BlackList) {
			return removeBlackListPlayer(playerId);
		} else if (groupType == FriendType.EnermyList) {
			return removeEnermyPlayer(playerId);
		}
		return false;
	}
	
	public Map<String, FriendMember> getPlayerListByGroupType(byte groupType) {
		if (groupType == FriendType.TemporaryList) {
			return getTemporaryFriendPlayerSet();
		} else if (groupType == FriendType.FriendList) {
			return getFriendPlayerSet();
		} else if (groupType == FriendType.BlackList) {
			return getBlackListPlayerSet();
		} else if (groupType == FriendType.EnermyList) {
			return getEnermyPlayerSet();
		}
		return null;
	}
	
	public boolean ifListContainsPlayer(String playerId) {
		if (temporaryFriendMapping.keySet().contains(playerId)) {
			logger.info("!!!!!  TemporaryFriendMapping contains playerId.............");
			return true;
		} else if (friendMapping.keySet().contains(playerId)) {
			logger.info("!!!!!  FriendMapping contains playerId.............");
			return true;
		} else if (blackListMapping.keySet().contains(playerId)) {
			logger.info("!!!!!  BlackListMapping contains playerId.............");
			return true;
		} else if (enemyListMapping.keySet().contains(playerId)) {
			logger.info("!!!!!  EnemyListMapping contains playerId.............");
			return true;
		}
		return false;
	}
	
	public synchronized boolean isInBlackList(String playerId) {
		if (blackListMapping.keySet().contains(playerId)) {
			return true;
		}
		return false;
	}
	
	public byte getPlayerInWhitchList(String playerId) {
		if (temporaryFriendMapping.keySet().contains(playerId)) {
			return FriendType.TemporaryList;
		} else if (friendMapping.keySet().contains(playerId)) {
			return FriendType.FriendList;
		} else if (blackListMapping.keySet().contains(playerId)) {
			return FriendType.BlackList;
		} else if (enemyListMapping.keySet().contains(playerId)) {
			return FriendType.EnermyList;
		}
		return -1;
	}
	
	public void setFriendListByType(byte type, Map<String, FriendMember> friendList) {
		if (type == FriendType.TemporaryList) {
			temporaryFriendMapping = new ConcurrentHashMap<String, FriendMember>(friendList);
		} else if (type == FriendType.FriendList) {
			friendMapping = new ConcurrentHashMap<String, FriendMember>(friendList);
		} else if (type == FriendType.EnermyList) {
			enemyListMapping = new ConcurrentHashMap<String, FriendMember>(friendList);
		} else if (type == FriendType.BlackList) {
			blackListMapping = new ConcurrentHashMap<String, FriendMember>(friendList);
		}
	}

	@Override
	public String toString() {
		return "PlayerChatFriendMgr [ownerId=" + ownerId + ", temporaryFriendMapping=" + temporaryFriendMapping + ", friendMapping=" + friendMapping + ", blackListMapping="
				+ blackListMapping + ", enemyListMapping=" + enemyListMapping + "]";
	}
	
	
}
