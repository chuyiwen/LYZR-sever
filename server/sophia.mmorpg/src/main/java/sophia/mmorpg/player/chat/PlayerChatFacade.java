package sophia.mmorpg.player.chat;

import java.util.Collection;

import org.apache.log4j.Logger;

import sophia.foundation.communication.core.ActionEventBase;
import sophia.foundation.communication.core.MessageFactory;
import sophia.game.GameRoot;
import sophia.mmorpg.MMORPGContext;
import sophia.mmorpg.item.Item;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.PlayerManager;
import sophia.mmorpg.player.chat.event.ChatEventDefines;
import sophia.mmorpg.player.chat.event.G2C_Chat_System;
import sophia.mmorpg.player.chat.event.G2C_Chat_World;
import sophia.mmorpg.world.ActionEventFacade;

import com.google.common.base.Preconditions;

public final class PlayerChatFacade {
	private static final Logger logger = Logger.getLogger(PlayerChatFacade.class);

	public final static void sendMessageToWorld(Player sender, final String msg) {
		long time = System.currentTimeMillis();
		G2C_Chat_World res = MessageFactory.getConcreteMessage(ChatEventDefines.G2C_Chat_World);
		res.setSender(sender);
		res.setMsg(msg);
		res.setTime(time);
		ActionEventFacade.sendMessageToWorld(res);
	}

	/**
	 * 发给公会
	 * 
	 * @param actionEvent
	 */
	public final static void sendMessageToSociaty(Player sender, final ActionEventBase actionEvent) {

	}

	/**
	 * 群发给特定玩家
	 * 
	 * @param actionEvent
	 */
	public final static void sendMessageToPlayers(Collection<Player> receivers, final ActionEventBase actionEvent) {
		Preconditions.checkNotNull(receivers);
		Preconditions.checkNotNull(actionEvent);
		for (Player player : receivers) {
			try {
				GameRoot.sendMessage(player.getIdentity(), actionEvent);
			} catch (Throwable t) {
				if (logger.isDebugEnabled()) {
					logger.debug(t);
				}
			}
		}
	}

	/**
	 * 发送给指定玩家
	 * 
	 * @param receiver
	 * @param actionEvent
	 */
	public final static void sendMessageToPlayer(Player receiver, final ActionEventBase actionEvent) {
		Preconditions.checkNotNull(receiver);
		Preconditions.checkNotNull(actionEvent);
		try {
			GameRoot.sendMessage(receiver.getIdentity(), actionEvent);
		} catch (Throwable t) {
			if (logger.isDebugEnabled()) {
				logger.debug(t);
			}
		}
	}

	/**
	 * 发给当前场景的玩家
	 * 
	 * @param actionEvent
	 */
	public final static void sendMessageToCurrentScene(Player sender, final ActionEventBase actionEvent) {
		sendMessage(sender, actionEvent, ChannelType.SCENE);
	}

	/**
	 * 广播公告
	 * 
	 * @param player
	 *            玩家
	 * @param bessingStatement
	 *            （如鸿运当头） 祝福语句
	 * @param sceneId
	 *            场景ID
	 * @param x
	 *            X坐标
	 * @param y
	 *            Y坐标
	 * @param item
	 *            物品
	 */
	public final static void sendMessageToSystem(Player player, String sceneName, String sceneId, int x, int y, Item item) {

	}

	/**
	 * 系统广播消息
	 * 
	 * @param msg
	 *            消息内容
	 */
	public final static void sendMessageToSystem(String msg) {
		G2C_Chat_System res = MessageFactory.getConcreteMessage(ChatEventDefines.G2C_Chat_System);
		res.setMsg(msg);
		ActionEventFacade.sendMessageToWorld(res);
	}

	private final static void sendMessage(Player sender, final ActionEventBase actionEvent, String channelType) {
		// 全服在线玩家列表
		PlayerManager playerManager = MMORPGContext.getPlayerComponent().getPlayerManager();
		Collection<Player> playerList = playerManager.getPlayerList();
		for (Player player : playerList) {
			if (!player.isOnline()) {
				continue;
			}

			switch (channelType) {
			case ChannelType.SCENE:// 场景
				if (sender == null || sender.getCrtScene() == null || player.getCrtScene() == null
						|| !sender.getCrtScene().getId().equals(player.getCrtScene().getId())) {
					continue;
				}
				break;
			// case ChannelType.Sociaty:// 公会
			// // TODO FIXME 等待公会接口
			// if (sender == null || player == null || sender.getCamp() ==
			// null || player.getCamp() == null || sender.getCamp().getId()
			// != player.getCamp().getId()) {
			// continue;
			// }
			// break;
			default:
				break;
			}
			try {
				GameRoot.sendMessage(player.getIdentity(), actionEvent);
			} catch (Throwable t) {
				if (logger.isDebugEnabled()) {
					logger.debug(t);
				}
			}
		}
	}

}
