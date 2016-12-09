package sophia.mmorpg.player.friend;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import sophia.foundation.communication.core.ActionEventBase;
import sophia.foundation.communication.core.MessageFactory;
import sophia.game.GameRoot;
import sophia.game.component.communication.GameEvent;
import sophia.mmorpg.MMORPGContext;
import sophia.mmorpg.base.ConcreteComponent;
import sophia.mmorpg.base.sprite.FightSprite;
import sophia.mmorpg.code.MMORPGErrorCode;
import sophia.mmorpg.event.ResultEvent;
import sophia.mmorpg.friend.FriendMember;
import sophia.mmorpg.friend.FriendSystemManager;
import sophia.mmorpg.friend.FriendType;
import sophia.mmorpg.monster.Monster;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.PlayerManager;
import sophia.mmorpg.player.friend.event.C2G_AddOnePlayer;
import sophia.mmorpg.player.friend.event.C2G_DeleteOnePlayer;
import sophia.mmorpg.player.friend.event.C2G_GetPlayerList;
import sophia.mmorpg.player.friend.event.FriendEventDefines;
import sophia.mmorpg.player.friend.event.G2C_GetPlayerList;
import sophia.mmorpg.player.gameEvent.LeaveWorld_GE;
import sophia.mmorpg.player.gameEvent.PlayerDead_GE;
import sophia.mmorpg.utils.RuntimeResult;

public class PlayerFriendComponent extends ConcreteComponent<Player>{
	private static Logger logger = Logger.getLogger(PlayerFriendComponent.class);
	
	public static final String Tag = "PlayerFriendComponent";
	
	private PlayerManager playerManager = MMORPGContext.getPlayerComponent().getPlayerManager();
	
	private static final String PlayerDead_GE_ID = PlayerDead_GE.class.getSimpleName();
	
	private static final String LeaveWorld_GE_ID = LeaveWorld_GE.class.getSimpleName();
	
	private Player player;
	
	
	@Override
	public void ready() {
		player = getConcreteParent();
		addActionEventListener(FriendEventDefines.C2G_AddOnePlayer);
		addActionEventListener(FriendEventDefines.C2G_DeleteOnePlayer);
		addActionEventListener(FriendEventDefines.C2G_GetPlayerList);
		
		addInterGameEventListener(PlayerDead_GE_ID);
		addInterGameEventListener(LeaveWorld_GE_ID);
		super.ready();
	}
	
	@Override
	public void suspend() {
		removeActionEventListener(FriendEventDefines.C2G_AddOnePlayer);
		removeActionEventListener(FriendEventDefines.C2G_DeleteOnePlayer);
		removeActionEventListener(FriendEventDefines.C2G_GetPlayerList);
		
		removeInterGameEventListener(PlayerDead_GE_ID);
		removeInterGameEventListener(LeaveWorld_GE_ID);
		super.suspend();
	}
	
	@Override
	public void handleGameEvent(GameEvent<?> event) {
		if (event.isId(PlayerDead_GE_ID)) {
			PlayerDead_GE dead = (PlayerDead_GE) event.getData();
			String deadPlayerId = dead.getPlayer().getId();
			FightSprite attacker = dead.getAttacker();
			if (!StringUtils.equals(deadPlayerId, player.getId())) {
				return;
			}
			
			if (attacker instanceof Monster) {
				Monster baobao = (Monster) attacker;
				if (baobao.getMonsterRef().isRegularMonster()) {
					return;
				}
				
				if (baobao.getOwner() != null) {
					attacker = baobao.getOwner();
				}
			}
			
			if (attacker == null) {
				logger.error("attacker owner is null!");
				return;
			}
			
			Player slaver = (Player)attacker;
			
			FriendSystemManager.addFriend(player, slaver, FriendType.EnermyList);
		} else if (event.isId(LeaveWorld_GE_ID)) {
			FriendSystemManager.logout(player.getId());
		}
		
		super.handleGameEvent(event);
	}
	
	@Override
	public void handleActionEvent(ActionEventBase event) {

		switch (event.getActionEventId()) {
		case FriendEventDefines.C2G_AddOnePlayer:
			// 添加好友
			handle_Chat_AddOnePlayer((C2G_AddOnePlayer) event);
			break;
		case FriendEventDefines.C2G_DeleteOnePlayer:
			// 删除好友
			handle_Chat_DeleteOnePlayer((C2G_DeleteOnePlayer) event);
			break;
		case FriendEventDefines.C2G_GetPlayerList:
			// 获取分组列表
			handle_Chat_GetPlayerList((C2G_GetPlayerList) event);
			break;
		default:
			break;
		}
		super.handleActionEvent(event);
	}
	
	/**
	 * 添加好友
	 * 
	 * @param actionEvent
	 */
	private void handle_Chat_AddOnePlayer(C2G_AddOnePlayer actionEvent) {
		String playerName = actionEvent.getPlayerName();
		Player recievePlayer = playerManager.getPlayerByName(playerName);
		byte groupType = actionEvent.getGroupType();
		
		if (StringUtils.equals(playerName, player.getName())) {
			ResultEvent.sendResult(player.getIdentity(), actionEvent.getActionEventId(), MMORPGErrorCode.CODE_CHAT_ADD_SELF);
			return;
		}
		
		if (recievePlayer == null) {
			logger.error("error argument! playerId = " + playerName);
			ResultEvent.sendResult(player.getIdentity(), actionEvent.getActionEventId(), MMORPGErrorCode.CODE_CHAT_PlayerIsNotExist);
			return;
		}
		
		if (groupType == FriendType.EnermyList) {
			logger.error("invalid argument! enemy can not be add Initiative");
			return;
		}
		
		if (groupType != FriendType.TemporaryList && groupType != FriendType.FriendList && groupType != FriendType.BlackList) {
			logger.error("invalid argument! groupType = " + groupType);
			return;
		}
		
		RuntimeResult result = FriendSystemManager.addFriend(player, recievePlayer, groupType);
		if (result.isError()) {
			ResultEvent.sendResult(player.getIdentity(), actionEvent.getActionEventId(), result.getApplicationCode());
			return;
		}

	}

	/**
	 * 删除好友
	 * 
	 * @param actionEvent
	 */
	private void handle_Chat_DeleteOnePlayer(C2G_DeleteOnePlayer actionEvent) {
		byte groupType = actionEvent.getGroupType();
		String playerName = actionEvent.getPlayerName();
		Player recievePlayer = playerManager.getPlayerByName(playerName);
		
		if (StringUtils.equals(playerName, player.getName())) {
			logger.error("can not delete self!");
			return;
		}
		
		if (recievePlayer == null) {
			logger.error("error argument! playerId = " + playerName);
			ResultEvent.sendResult(player.getIdentity(), actionEvent.getActionEventId(), MMORPGErrorCode.CODE_CHAT_PlayerIsNotExist);
			return;
		}
		
		RuntimeResult result = FriendSystemManager.deleteFriend(player, recievePlayer, groupType);
		if (result.isError()) {
			ResultEvent.sendResult(player.getIdentity(), actionEvent.getActionEventId(), result.getApplicationCode());
			return;
		}

	}

	/**
	 * 获取好友列表
	 * 
	 * @param actionEvent
	 */
	private void handle_Chat_GetPlayerList(C2G_GetPlayerList actionEvent) {
		byte groupType = actionEvent.getGroupType();
		Map<String, FriendMember> playerListByGroupType = FriendSystemManager.getFriendList(player, groupType);
		
		G2C_GetPlayerList res = MessageFactory.getConcreteMessage(FriendEventDefines.G2C_GetPlayerList);
		res.setGroupType(groupType);
		res.setPlayerList(playerListByGroupType);
		GameRoot.sendMessage(player.getIdentity(), res);
	}
}
