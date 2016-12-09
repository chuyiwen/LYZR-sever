package sophia.mmorpg.friend;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import sophia.foundation.communication.core.MessageFactory;
import sophia.game.GameRoot;
import sophia.mmorpg.MMORPGContext;
import sophia.mmorpg.code.MMORPGErrorCode;
import sophia.mmorpg.core.timer.SFTimer;
import sophia.mmorpg.core.timer.SFTimerCreater;
import sophia.mmorpg.friend.persistence.ChatFriendDAO;
import sophia.mmorpg.friend.timer.OfflineFriendMemberChimeListener;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.PlayerManager;
import sophia.mmorpg.player.friend.event.FriendEventDefines;
import sophia.mmorpg.player.friend.event.G2C_AddOnePlayer;
import sophia.mmorpg.player.friend.event.G2C_DeleteOnePlayer;
import sophia.mmorpg.utils.RuntimeResult;

import com.google.common.base.Preconditions;

public class FriendSystemManager {
	private static Logger logger = Logger.getLogger(FriendSystemManager.class);

	private static Map<String, PlayerChatFriendMgr> idToFriendMgrMapping = new HashMap<String, PlayerChatFriendMgr>();

	// private static Map<String, Boolean> idToOnlineMapping = new
	// ConcurrentHashMap<String, Boolean>();

	private static SFTimer timer = null;

	public static void startFriendSystemTimer() {
		SFTimerCreater timerCreater = MMORPGContext.getTimerCreater();

		timer = timerCreater.minuteCalendarChime(new OfflineFriendMemberChimeListener());
	}

	public static void cancelFriendSystemTimer() {
		if (timer != null) {
			timer.cancel();
		}
	}

	public static void initPlayerChatModule(String playerId) {
		Map<Byte, ConcurrentHashMap<String, FriendMember>> mapping = ChatFriendDAO.getInstance().selectFriend(playerId);

		putIfAbsent(playerId, mapping);

		if (logger.isDebugEnabled()) {
			logger.debug("load playerChatFriendMgr info! playerId = " + playerId);
		}

	}

	public synchronized static void initPlayerChatModule(PlayerChatFriendMgr playerChatFriendMgr) {
		String ownerId = playerChatFriendMgr.getOwnerId();

		idToFriendMgrMapping.put(ownerId, playerChatFriendMgr);

		if (logger.isDebugEnabled()) {
			logger.debug("new player! init playerChatFriendMgr! playerId = " + ownerId);
		}
	}

	// online
	public static void login(String playerId) {
		// idToOnlineMapping.put(playerId, true);

		synchronized (FriendSystemManager.class) {
			if (!containFriendMgr(playerId)) {
				initPlayerChatModule(playerId);
			}
		}
	}

	public static void logout(String playerId) {
		// idToOnlineMapping.put(playerId, false);
	}

	public static boolean isOnline(String playerId) {
		PlayerManager playerManager = MMORPGContext.getPlayerComponent().getPlayerManager();
		Player player = playerManager.getOnlinePlayer(playerId);
		if (player != null) {
			return true;
		}

		return false;
	}

	public static synchronized void putIfAbsent(String playerId, Map<Byte, ConcurrentHashMap<String, FriendMember>> mapping) {
		Preconditions.checkArgument(StringUtils.isNotEmpty(playerId));

		if (mapping == null) {
			return;
		}

		if (containFriendMgr(playerId)) {
			return;
		}

		PlayerChatFriendMgr playerChatFriendMgr = new PlayerChatFriendMgr(playerId);
		playerChatFriendMgr.setFriendListMapping(mapping);

		idToFriendMgrMapping.put(playerId, playerChatFriendMgr);
	}

	public static synchronized boolean containFriendMgr(String playerId) {
		Preconditions.checkArgument(StringUtils.isNotEmpty(playerId));

		return idToFriendMgrMapping.containsKey(playerId);
	}

	public static synchronized PlayerChatFriendMgr getPlayerChatFriendMgr(String playerId) {
		Preconditions.checkArgument(StringUtils.isNotEmpty(playerId));

		PlayerChatFriendMgr playerChatFriendMgr = idToFriendMgrMapping.get(playerId);

		if (playerChatFriendMgr != null) {
			return playerChatFriendMgr;
		}

		initPlayerChatModule(playerId);

		playerChatFriendMgr = idToFriendMgrMapping.get(playerId);

		return playerChatFriendMgr;
	}

	public static synchronized void removePlayerChatFriendMgr(String playerId) {
		Preconditions.checkArgument(StringUtils.isNotEmpty(playerId));

		if (!containFriendMgr(playerId)) {
			return;
		}

		idToFriendMgrMapping.remove(playerId);
	}

	// ===================================================================================

	/**
	 * 私聊
	 * 
	 * @param sender
	 * @param receiver
	 * @param content
	 */
	public static RuntimeResult privateChat(Player sender, Player receiver, String content) {
		Preconditions.checkArgument(sender != null);
		Preconditions.checkArgument(receiver != null);

		String senderId = sender.getId();
		String receiverId = receiver.getId();

		PlayerChatFriendMgr senderChatFriendMgr = getPlayerChatFriendMgr(senderId);
		PlayerChatFriendMgr receiverChatFriendMgr = getPlayerChatFriendMgr(receiverId);

		byte playerInWhitchList = senderChatFriendMgr.getPlayerInWhitchList(receiverId);
		byte senderInWhitchList = receiverChatFriendMgr.getPlayerInWhitchList(senderId);

		if (playerInWhitchList == -1) {
			return RuntimeResult.RuntimeApplicationError(MMORPGErrorCode.CODE_CHAT_NOT_IN_LIST);
		}

		if (playerInWhitchList == FriendType.BlackList) {
			return RuntimeResult.RuntimeApplicationError(MMORPGErrorCode.CODE_CHAT_IN_BLACKLIST);
		}

		if (senderInWhitchList == FriendType.BlackList) {
			return RuntimeResult.RuntimeApplicationError(MMORPGErrorCode.CODE_CHAT_IN_OTHERBLACKLIST);
		}

		boolean online = receiver.isOnline();
		boolean isRead = online;

		ChatRecord chatRecord = new ChatRecord(senderId, receiverId, content, System.currentTimeMillis(), isRead);
		addPrivateChatRecord(senderChatFriendMgr, receiver, chatRecord);
		if (senderInWhitchList == -1) {
			RuntimeResult addFriend = addFriend(receiver, sender, FriendType.TemporaryList);

			if (addFriend.isOK()) {
				addPrivateChatRecord(receiverChatFriendMgr, sender, chatRecord);
			}
		}

		return RuntimeResult.OK();
	}

	/**
	 * 
	 * @param hoster
	 *            执行添加好友player
	 * @param slaver
	 *            被添加的player
	 * @param friendType
	 *            添加好友的类型
	 */
	public static RuntimeResult addFriend(Player hoster, Player slaver, byte friendType) {
		Preconditions.checkArgument(hoster != null);
		Preconditions.checkArgument(slaver != null);

		String hosterId = hoster.getId();
		String slaverId = slaver.getId();

		PlayerChatFriendMgr hosterChatFriendMgr = getPlayerChatFriendMgr(hosterId);
		PlayerChatFriendMgr slaverChatFriendMgr = getPlayerChatFriendMgr(slaverId);

		boolean removePlayerInListByGroupType;
		boolean addPlayerInListByGroupType;
		// 玩家已经存在于该分组中
		byte playerInWhitchList;
		synchronized (FriendSystemManager.class) {
			playerInWhitchList = hosterChatFriendMgr.getPlayerInWhitchList(slaverId);
			if (playerInWhitchList == friendType) {
				return RuntimeResult.RuntimeApplicationError(MMORPGErrorCode.CODE_CHAT_CANT_REPEATADD);
			}

			// 添加到黑名单
			if (friendType == FriendType.EnermyList) {
				hosterChatFriendMgr.addPlayerInListByGroupType(slaver, FriendType.EnermyList);
				return RuntimeResult.OK();
			}

			// 添加临时好友、好友、仇人,
			// is in blacklist
			boolean inBlackList = slaverChatFriendMgr.isInBlackList(hosterId);
			if (inBlackList) {
				if (logger.isDebugEnabled()) {
					logger.debug("hoterId = " + hosterId + " in blackList of Player. slaverId = " + slaverId);
				}
				return RuntimeResult.RuntimeApplicationError(MMORPGErrorCode.CODE_CHAT_BLACK_LIST);
			}

			// 如果存在于其他分组中则删除
			removePlayerInListByGroupType = hosterChatFriendMgr.removePlayerInListByGroupType(slaverId, playerInWhitchList);

			// 添加
			addPlayerInListByGroupType = hosterChatFriendMgr.addPlayerInListByGroupType(slaver, friendType);
		}

		if (removePlayerInListByGroupType) {
			G2C_DeleteOnePlayer deleteRes = MessageFactory.getConcreteMessage(FriendEventDefines.G2C_DeleteOnePlayer);
			deleteRes.setGroupType(playerInWhitchList);
			deleteRes.setPlayerId(slaverId);
			GameRoot.sendMessage(hoster.getIdentity(), deleteRes);
		}

		if (addPlayerInListByGroupType) {
			G2C_AddOnePlayer addRes = MessageFactory.getConcreteMessage(FriendEventDefines.G2C_AddOnePlayer);
			addRes.setPlayer(slaver);
			addRes.setGroupType(friendType);
			GameRoot.sendMessage(hoster.getIdentity(), addRes);
		}

		// save
		if (removePlayerInListByGroupType || addPlayerInListByGroupType) {
			ChatFriendSaver.getInstance().saveImmediateData(hosterChatFriendMgr);
			ChatFriendSaver.getInstance().saveImmediateData(slaverChatFriendMgr);
		}

		return RuntimeResult.OK();
	}

	/**
	 * 
	 * @param hoster
	 *            执行删除好友player
	 * @param slaver
	 *            被删除的player
	 * @param friendType
	 *            添加好友的类型
	 */
	public static RuntimeResult deleteFriend(Player hoster, Player slaver, byte friendType) {
		Preconditions.checkArgument(hoster != null);
		Preconditions.checkArgument(slaver != null);

		String hosterId = hoster.getId();
		String slaverId = slaver.getId();

		PlayerChatFriendMgr hosterChatFriendMgr = getPlayerChatFriendMgr(hosterId);

		synchronized (FriendSystemManager.class) {
			boolean removePlayerInListByGroupType = hosterChatFriendMgr.removePlayerInListByGroupType(slaverId, friendType);

			if (!removePlayerInListByGroupType) {
				return RuntimeResult.RuntimeApplicationError(MMORPGErrorCode.CODE_CHAT_NOT_IN_LIST);
			}
		}

		G2C_DeleteOnePlayer res = MessageFactory.getConcreteMessage(FriendEventDefines.G2C_DeleteOnePlayer);
		res.setGroupType(friendType);
		res.setPlayerId(slaverId);
		GameRoot.sendMessage(hoster.getIdentity(), res);

		ChatFriendSaver.getInstance().saveImmediateData(hosterChatFriendMgr);

		return RuntimeResult.OK();
	}

	public static Map<String, FriendMember> getFriendList(Player hoster, byte friendType) {
		Preconditions.checkArgument(hoster != null);

		String hosterId = hoster.getId();
		PlayerChatFriendMgr hosterChatFriendMgr = getPlayerChatFriendMgr(hosterId);

		Map<String, FriendMember> playerListByGroupType = hosterChatFriendMgr.getPlayerListByGroupType(friendType);

		return playerListByGroupType;
	}

	/**
	 * 
	 * @param playerChatFriendMgr
	 * @param player
	 *            被添加玩家
	 * @param content
	 *            聊天内容
	 */
	private static void addPrivateChatRecord(PlayerChatFriendMgr playerChatFriendMgr, Player player, ChatRecord chatRecord) {
		String playerId = player.getId();
		// 添加聊天记录
		playerChatFriendMgr.addChatRecord(FriendType.TemporaryList, playerId, chatRecord);

	}

}
