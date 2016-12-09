package sophia.mmorpg.player.chat;

import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import sophia.foundation.communication.core.ActionEventBase;
import sophia.foundation.communication.core.MessageFactory;
import sophia.game.GameRoot;
import sophia.game.component.communication.GameEvent;
import sophia.mmorpg.MMORPGContext;
import sophia.mmorpg.base.ConcreteComponent;
import sophia.mmorpg.code.MMORPGErrorCode;
import sophia.mmorpg.code.MMORPGSuccessCode;
import sophia.mmorpg.core.CDMgr;
import sophia.mmorpg.event.ResultEvent;
import sophia.mmorpg.friend.ChatRecord;
import sophia.mmorpg.friend.FriendSystemManager;
import sophia.mmorpg.friend.PlayerChatFriendMgr;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.PlayerManager;
import sophia.mmorpg.player.chat.event.C2G_Chat_Bugle;
import sophia.mmorpg.player.chat.event.C2G_Chat_Current_Scene;
import sophia.mmorpg.player.chat.event.C2G_Chat_Get_ReceiverId;
import sophia.mmorpg.player.chat.event.C2G_Chat_Private;
import sophia.mmorpg.player.chat.event.C2G_Chat_World;
import sophia.mmorpg.player.chat.event.ChatEventDefines;
import sophia.mmorpg.player.chat.event.G2C_Chat_Bugle;
import sophia.mmorpg.player.chat.event.G2C_Chat_Current_Scene;
import sophia.mmorpg.player.chat.event.G2C_Chat_Get_ReceiverId;
import sophia.mmorpg.player.chat.event.G2C_Chat_Private;
import sophia.mmorpg.player.chat.event.G2C_Chat_System;
import sophia.mmorpg.player.chat.event.G2C_Is_Player_Online;
import sophia.mmorpg.player.chat.gameEvent.PlayerUseChuanYinTalisman_GE;
import sophia.mmorpg.player.gameEvent.EnterWorld_SceneReady_GE;
import sophia.mmorpg.player.itemBag.ItemFacade;
import sophia.mmorpg.player.itemBag.ItemOptSource;
import sophia.mmorpg.player.state.PlayerStateMgr;
import sophia.mmorpg.utils.RuntimeResult;
import sophia.mmorpg.world.ActionEventFacade;

/**
 * 玩家-聊天
 */
public class PlayerChatComponent extends ConcreteComponent<Player> {
	private static final Logger logger = Logger.getLogger(PlayerChatComponent.class);
	private final static long CDTIME = 1000 * 5;
	public static final String Tag = "PlayerChatComponent";
	private boolean isActiveChuanYinTalisman = false;
	private int TalismanCDTime = 0;
	private static final String Bugle_PropsItem = "item_horn";
	private PlayerManager playerManager = MMORPGContext.getPlayerComponent().getPlayerManager();
	private CDMgr reviveCDMgr = new CDMgr(0);
	private PlayerChatFriendMgr playerChatFriendMgr;
	private static final String EnterWorld_SceneReady_GE_ID = EnterWorld_SceneReady_GE.class.getSimpleName();

	private Player player;

	// ==================================================================================================================

	@Override
	public void ready() {
		player = getConcreteParent();
		playerChatFriendMgr = FriendSystemManager.getPlayerChatFriendMgr(player.getId());

		addInterGameEventListener(EnterWorld_SceneReady_GE_ID);

		addActionEventListener(ChatEventDefines.C2G_Chat_World);
		addActionEventListener(ChatEventDefines.C2G_Chat_Private);
		addActionEventListener(ChatEventDefines.C2G_Chat_Get_ReceiverId);
		addActionEventListener(ChatEventDefines.C2G_Chat_Current_Scene);
		addActionEventListener(ChatEventDefines.C2G_Chat_Bugle);
		super.ready();
	}

	@Override
	public void suspend() {
		removeInterGameEventListener(EnterWorld_SceneReady_GE_ID);

		removeActionEventListener(ChatEventDefines.C2G_Chat_World);
		removeActionEventListener(ChatEventDefines.C2G_Chat_Private);
		removeActionEventListener(ChatEventDefines.C2G_Chat_Get_ReceiverId);
		removeActionEventListener(ChatEventDefines.C2G_Chat_Current_Scene);
		removeActionEventListener(ChatEventDefines.C2G_Chat_Bugle);
		super.suspend();
	}

	@Override
	public void handleGameEvent(GameEvent<?> event) {
		if (event.isId(EnterWorld_SceneReady_GE_ID)) {
			FriendSystemManager.login(player.getId());
			// 是否有离线消息
			List<ChatRecord> offlineMessage = playerChatFriendMgr.getOfflineMessage();

			for (ChatRecord chatRecord : offlineMessage) {
				String senderId = chatRecord.getSenderId();
				String receiverId = chatRecord.getReceiverId();
				String content = chatRecord.getContent();
				long chatTime = chatRecord.getChatTime();

				if (!StringUtils.equals(player.getId(), receiverId)) {
					logger.error("error message receiver! receiverId = " + receiverId);
					continue;
				}

				Player sender = playerManager.getPlayer(senderId);
				if (sender == null) {
					logger.error("playerId = " + senderId + " is not exist!");
					continue;
				}

				G2C_Chat_Private res = MessageFactory.getConcreteMessage(ChatEventDefines.G2C_Chat_Private);
				res.setSender(sender);
				res.setReceiver(player);
				res.setMsg(content);
				res.setTime(chatTime);
				GameRoot.sendMessage(player.getIdentity(), res);
			}

		}

	}

	@Override
	public void handleActionEvent(final ActionEventBase actionEvent) {
		if (logger.isDebugEnabled()) {
			logger.debug("enter PlayerChatComponent");
		}

		if (player.getPlayerStateMgr().hasState(PlayerStateMgr.DisallowTalk)) {
			ResultEvent.sendResult(actionEvent.getIdentity(), actionEvent.getActionEventId(), MMORPGErrorCode.CODE_CHAT_USER_DISALLOW_TALK);
			logger.info(String.format("你被禁言了 Identity=%s", actionEvent.getIdentity()));
			return;
		}
		switch (actionEvent.getActionEventId()) {
		case ChatEventDefines.C2G_Chat_World:
			// 世界频道聊天
			handle_Chat_World((C2G_Chat_World) actionEvent);
			break;
		case ChatEventDefines.C2G_Chat_Private:
			// 私聊频道聊天
			handle_Chat_Private((C2G_Chat_Private) actionEvent);
			break;
		case ChatEventDefines.C2G_Chat_Get_ReceiverId:
			// 根据姓名获取玩家ID
			handle_Chat_GetReceiverId((C2G_Chat_Get_ReceiverId) actionEvent);
			break;
		case ChatEventDefines.C2G_Chat_Current_Scene:
			// 当前场景频道聊天
			handle_Chat_CurrentScene((C2G_Chat_Current_Scene) actionEvent);
			break;
		case ChatEventDefines.C2G_Chat_Bugle:
			// 喇叭
			handle_Chat_Bugle((C2G_Chat_Bugle) actionEvent);
			break;
		default:
			break;
		}
		super.handleActionEvent(actionEvent);
	}

	// ==================================================================================================================

	private void handle_Chat_Bugle(C2G_Chat_Bugle actionEvent) {
		// 判断玩家是否激活了千里传音法宝 或喇叭道具是否足够

		boolean canBugle = false;
		if (isActiveChuanYinTalisman) {
			if (checkCDTime(ChannelType.CHUANYIN)) {
				canBugle = true;
				refreshCDTime(ChannelType.CHUANYIN);
				PlayerUseChuanYinTalisman_GE event = new PlayerUseChuanYinTalisman_GE();
				GameEvent<PlayerUseChuanYinTalisman_GE> ge = (GameEvent<PlayerUseChuanYinTalisman_GE>) GameEvent.getInstance(PlayerUseChuanYinTalisman_GE.class.getSimpleName(),
						event);
				sendGameEvent(ge, player.getId());
				sendSuccessToClient(actionEvent);
			}
		}
		if (!canBugle) {
			if (ItemFacade.getNumber(player, Bugle_PropsItem) > 0) {
				if (checkCDTime(ChannelType.BUGLE)) {
					canBugle = true;
					ItemFacade.removeItem(player, Bugle_PropsItem, 1, true, ItemOptSource.Chat);
					refreshCDTime(ChannelType.BUGLE);
					sendSuccessToClient(actionEvent);
				}
			} else {
				ResultEvent.sendResult(actionEvent.getIdentity(), actionEvent.getActionEventId(), MMORPGErrorCode.CODE_ITEM_NOENOUGH);
				return;
			}
		}
		if (canBugle) {
			if (player == null) {
				if (logger.isDebugEnabled()) {
					logger.error(actionEvent.getIdentity() + " User does not exist.");
				}
				return;
			}
			long time = System.currentTimeMillis();
			G2C_Chat_Bugle res = MessageFactory.getConcreteMessage(ChatEventDefines.G2C_Chat_Bugle);
			res.setSender(player);
			res.setMsg(actionEvent.getText());
			res.setTime(time);
			ActionEventFacade.sendMessageToWorld(res);

			if (logger.isDebugEnabled()) {
				logger.debug("return successed!");
			}
			return;
		}

	}

	private void handle_Chat_CurrentScene(C2G_Chat_Current_Scene c2g_ChatEvent) {
		if (logger.isDebugEnabled()) {
			logger.debug("handleCurrentScene");
		}

		if (checkCDTime(ChannelType.SCENE)) {
			refreshCDTime(ChannelType.SCENE);
			long time = System.currentTimeMillis();
			G2C_Chat_Current_Scene res = MessageFactory.getConcreteMessage(ChatEventDefines.G2C_Chat_Current_Scene);
			res.setSender(player);
			res.setMsg(c2g_ChatEvent.getMsg());
			res.setTime(time);
			PlayerChatFacade.sendMessageToCurrentScene(player, res);
			sendSuccessToClient(c2g_ChatEvent);
		}
		if (logger.isDebugEnabled()) {
			logger.debug("return successed!");
		}
	}

	private void handle_Chat_GetReceiverId(C2G_Chat_Get_ReceiverId c2g_ChatEvent) {
		if (logger.isDebugEnabled()) {
			logger.debug("handleGetReceiverId");
		}

		// 全服在线玩家列表
		String receiverName = c2g_ChatEvent.getReceiverName();
		Player receiverNamePlayer = null;
		PlayerManager playerManager = MMORPGContext.getPlayerComponent().getPlayerManager();
		Collection<Player> playerList = playerManager.getPlayerList();
		for (Player player : playerList) {
			if (player.isOnline() && player.getName().equals(receiverName)) {
				receiverNamePlayer = player;
				break;
			}
		}

		if (receiverNamePlayer != null) {
			G2C_Chat_Get_ReceiverId res = MessageFactory.getConcreteMessage(ChatEventDefines.G2C_Chat_Get_ReceiverId);
			res.setReceiver(receiverNamePlayer);
			GameRoot.sendMessage(c2g_ChatEvent.getIdentity(), res);
		} else {
			// 玩家不存在或者不在线
			ResultEvent.sendResult(c2g_ChatEvent.getIdentity(), c2g_ChatEvent.getActionEventId(), MMORPGErrorCode.CODE_CHAT_USER_NOT_EXIST);
		}

		if (logger.isDebugEnabled()) {
			logger.debug("return successed!");
		}
	}

	private void handle_Chat_World(C2G_Chat_World c2g_ChatEvent) {
		if (logger.isDebugEnabled()) {
			logger.debug("handleWorld");
		}

		if (checkCDTime(ChannelType.WORLD)) {
			if (player == null) {
				if (logger.isDebugEnabled()) {
					logger.error(c2g_ChatEvent.getIdentity() + " User does not exist.");
				}
				return;
			}
			refreshCDTime(ChannelType.WORLD);
			PlayerChatFacade.sendMessageToWorld(player, c2g_ChatEvent.getMsg());
			if (logger.isDebugEnabled()) {
				logger.debug("return successed!");
			}
			sendSuccessToClient(c2g_ChatEvent);
			return;
		} else {
			G2C_Chat_System res = MessageFactory.getConcreteMessage(ChatEventDefines.G2C_Chat_System);
			String msg = "话唠了吧,喝点茶休息下!";
			res.setMsg(msg);
			PlayerChatFacade.sendMessageToPlayer(getConcreteParent(), res);
		}

	}

	private void handle_Chat_Private(C2G_Chat_Private c2g_ChatEvent) {
		logger.debug("handlePrivate");

		String receiverId = c2g_ChatEvent.getReceiverId();
		String content = c2g_ChatEvent.getMsg();
		String playerId = player.getId();

		if (StringUtils.equals(receiverId, playerId)) {
			ResultEvent.sendResult(player.getIdentity(), c2g_ChatEvent.getActionEventId(), MMORPGErrorCode.CODE_CHAT_CANT_CHAT_SELF);
			return;
		}

		if (checkCDTime(ChannelType.PRIVATE)) {
			PlayerManager playerManager = MMORPGContext.getPlayerComponent().getPlayerManager();
			Player receiver = playerManager.getPlayer(receiverId);
			// 玩家不存在
			if (receiver == null) {
				if (logger.isDebugEnabled()) {
					logger.error("player receiverId = " + receiverId + " does not exist.");
				}
				ResultEvent.sendResult(c2g_ChatEvent.getIdentity(), c2g_ChatEvent.getActionEventId(), MMORPGErrorCode.CODE_CHAT_USER_NOT_EXIST);
				return;
			}

			// update
			if (!receiver.isOnline()) {
				G2C_Is_Player_Online onlineRes = MessageFactory.getConcreteMessage(ChatEventDefines.G2C_Is_Player_Online);
				onlineRes.setPlayerId(playerId);
				onlineRes.setOnline(false);
				GameRoot.sendMessage(player.getIdentity(), onlineRes);
			}

			RuntimeResult result = FriendSystemManager.privateChat(player, receiver, content);
			if (result.isError()) {
				ResultEvent.sendResult(player.getIdentity(), c2g_ChatEvent.getActionEventId(), result.getApplicationCode());
				return;
			}

			refreshCDTime(ChannelType.PRIVATE);
			long time = System.currentTimeMillis();
			G2C_Chat_Private res = MessageFactory.getConcreteMessage(ChatEventDefines.G2C_Chat_Private);
			res.setSender(player);
			res.setReceiver(receiver);
			res.setMsg(content);
			res.setTime(time);
			GameRoot.sendMessage(receiver.getIdentity(), res);
			GameRoot.sendMessage(player.getIdentity(), res);
			sendSuccessToClient(c2g_ChatEvent);
		}

		if (logger.isDebugEnabled()) {
			logger.debug("return successed!");
		}
	}

	public void sendSuccessToClient(ActionEventBase event) {
		ResultEvent.sendResult(player.getIdentity(), event.getActionEventId(), MMORPGSuccessCode.CODE_SUCCESS);
	}

	// ==================================================================================================================

	public boolean checkCDTime(String channelType) {
		if (reviveCDMgr.isCDStarted(channelType)) {
			return reviveCDMgr.isOutOfCD(channelType);
		}
		return true;
	}

	public void refreshCDTime(String channelType) {
		if (!reviveCDMgr.isCDStarted(channelType)) {
			if (StringUtils.equals(channelType, ChannelType.CHUANYIN)) {
				reviveCDMgr.startCD(channelType, getTalismanCDTime());
			} else {
				reviveCDMgr.startCD(channelType, PlayerChatComponent.CDTIME);
			}
		}
		reviveCDMgr.update(channelType);
	}

	public int getTalismanCDTime() {
		return TalismanCDTime;
	}

	public void setTalismanCDTime(int talismanCDTime) {
		TalismanCDTime = talismanCDTime;
	}

	public boolean isActiveChuanYinTalisman() {
		return isActiveChuanYinTalisman;
	}

	public void setActiveChuanYinTalisman(boolean isActiveChuanYinTalisman) {
		this.isActiveChuanYinTalisman = isActiveChuanYinTalisman;
	}

}
