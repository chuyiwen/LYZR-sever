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
package sophia.mmorpg.communication;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.log4j.Logger;
import org.apache.mina.util.ConcurrentHashSet;

import sophia.foundation.authentication.Identity;
import sophia.foundation.communication.core.ActionEventBase;
import sophia.foundation.communication.core.MessageFactory;
import sophia.foundation.util.DebugUtil;
import sophia.game.GameContext;
import sophia.game.GameRoot;
import sophia.game.component.AbstractComponent;
import sophia.game.plugIns.communication.SocketDisconnectListener;
import sophia.game.plugIns.gameModule.SocketActionEventLister;
import sophia.mmorpg.MMORPGContext;
import sophia.mmorpg.code.MMORPGErrorCode;
import sophia.mmorpg.event.ResultEvent;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.PlayerManager;
import sophia.mmorpg.player.property.event.C2G_Player_Heartbeat;
import sophia.mmorpg.player.property.event.G2C_Player_Heartbeat;
import sophia.mmorpg.player.property.event.PlayerEventDefines;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

public final class SocketActionEventListerComponent extends AbstractComponent implements SocketActionEventLister, SocketDisconnectListener {
	
	private static final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors() + 1);

	private static final Logger logger = Logger.getLogger(SocketActionEventListerComponent.class);

	private static final ConcurrentHashSet<Short> messageFilter = new ConcurrentHashSet<>();

	private PlayerManager playerManager;

	public static PacketThrottler packetThrottler = new PacketThrottler();
	//private static BlackList blackList = new BlackList();

	public static void addMessageFilter(final int messageId) {
		Preconditions.checkArgument(messageId > 0 && messageId < Short.MAX_VALUE);
		messageFilter.add((short) messageId);
	}

	public static void addMessageFilterRange(final int minMessageId, final int maxMessageId) {
		Preconditions.checkArgument(minMessageId > 0 && minMessageId < Short.MAX_VALUE);
		Preconditions.checkArgument(maxMessageId > 0 && maxMessageId < Short.MAX_VALUE);
		Preconditions.checkArgument(minMessageId < maxMessageId);
		Preconditions.checkArgument(maxMessageId - minMessageId <= 100, "the max range of message filter can't more than 100");
		for (int i = minMessageId; i <= maxMessageId; i++) {
			messageFilter.add((short) i);
		}
	}

	public static void removeMessageFilter(final int messageId) {
		Preconditions.checkArgument(messageId > 0 && messageId < Short.MAX_VALUE);
		messageFilter.remove((short) messageId);
	}

	public static void removeMessageFilterRange(final int minMessageId, final int maxMessageId) {
		Preconditions.checkArgument(minMessageId > 0 && minMessageId < Short.MAX_VALUE);
		Preconditions.checkArgument(maxMessageId > 0 && maxMessageId < Short.MAX_VALUE);
		Preconditions.checkArgument(minMessageId < maxMessageId);
		Preconditions.checkArgument(maxMessageId - minMessageId <= 100, "the max range of message filter can't more than 100");
		for (int i = minMessageId; i <= maxMessageId; i++) {
			messageFilter.remove((short) i);
		}
	}

	public static String printMessageFilter() {
		StringBuilder strBuilder = new StringBuilder();
		for (Short messageId : messageFilter) {
			strBuilder.append(messageId).append("\n");
		}

		return strBuilder.toString();
	}

	public static boolean isFilterMessage(final short messageId) {
		return messageFilter.contains(messageId);
	}

	public SocketActionEventListerComponent() {
	}

	@Override
	public void ready() {
		playerManager = MMORPGContext.getPlayerComponent().getPlayerManager();
	}

	@Override
	public void receivedActionEvent(ActionEventBase actionEvent) {
		Identity identity = actionEvent.getIdentity();
		if (identity == null) {
			logger.error("receivedActionEvent error, identity is null");
			return;
		} 	
		
		if (!MMORPGContext.isServerRunning()) {
			logger.error("receivedActionEvent error, server is stopped");
			return;
		}
		
		try {
			receivedActionEventImpl(actionEvent);
		} catch (Exception e) {
			ResultEvent.sendResult(identity, actionEvent.getActionEventId(), MMORPGErrorCode.CODE_MESSAGE_DECODE_ERROR);
			logger.error("receivedActionEvent error," + DebugUtil.printStack(e));
		}
	}
	
	private void receivedActionEventImpl(ActionEventBase actionEvent) {
		Identity identity = actionEvent.getIdentity();
		if (Strings.isNullOrEmpty(identity.getCharId())) {
			GameContext.getGameWorld().handleActionEvent(actionEvent);
			if (logger.isDebugEnabled()) {
				logger.debug("receivedActionEventImpl message, actionEventId=" + actionEvent.getActionEventId() + ", identityName=" + identity.getName());
			}
			return;
		}

		/* 已登录 */
		Player player = playerManager.getOnlinePlayer(identity.getCharId());
		if (player == null) {
			logger.error("receivedActionEventImpl error, player is not online, playerId=" + identity.getCharId());
			return;
		}

		player.setLastHeartbeatTime(System.currentTimeMillis());

		if (actionEvent.getActionEventId() == PlayerEventDefines.C2G_Player_Heartbeat) {
			String playerId = player.getId();
			C2G_Player_Heartbeat message = (C2G_Player_Heartbeat) actionEvent;
			long now = System.currentTimeMillis() / 1000;
			long clientTime = message.getTimestamp();
			long diff = now - clientTime;
			if (logger.isDebugEnabled()) {
				logger.debug("C2G_Player_Heartbeat clientTime " + clientTime + " now " + now + " diff " + diff);
			}
			
			if (packetThrottler.isOverrate(playerId, now)) {
				packetThrottler.addTolerantCount(playerId);
			}
			if (!packetThrottler.isTolerable(playerId)) {
				//blackList.block(player, System.currentTimeMillis());
				ResultEvent.sendResult(player.getIdentity(), actionEvent.getActionEventId(), MMORPGErrorCode.CODE_MESSAGE_ILLEGAL);
				GameRoot.getSimulatorCommunicationService().closeSession(identity);
				if (logger.isInfoEnabled()) {
					logger.info("kickOut, C2G_Player_Heartbeat@receiving packets too fast, playerName=" + player.getName());
				}
			}
			
			packetThrottler.updateLastHeartbeatTime(playerId, now);
		}

		// 心跳包
		if (actionEvent.getActionEventId() == PlayerEventDefines.C2G_Player_Heartbeat) {
			if (logger.isDebugEnabled()) {
				logger.debug("C2G_Player_Heartbeat, identityId=" + identity.getId());
			}

			G2C_Player_Heartbeat res = MessageFactory.getConcreteMessage(PlayerEventDefines.G2C_Player_Heartbeat);
			GameRoot.sendMessage(identity, res);
			return;
		}

		// 包速检测
		if (GameRoot.getSimulatorCommunicationService().checkActionEventMessage(identity)) {
			ResultEvent.sendResult(player.getIdentity(), actionEvent.getActionEventId(), MMORPGErrorCode.CODE_SESSION_SPEEDUP);
			GameRoot.getSimulatorCommunicationService().closeSession(identity);
			if (logger.isInfoEnabled()) {
				logger.info("kickOut, message send too fast, playerName=" + player.getName());
			}
			return;
		}

		// 消息过滤
		if (!isFilterMessage(actionEvent.getActionEventId())) {
			submitThreadAndWaitTimeout(player, actionEvent);
			//player.performActionEvent(actionEvent);
			if (logger.isDebugEnabled()) {
				logger.debug("receivedActionEventImpl message, actionEventId=" + actionEvent.getActionEventId() + ", player=" + player);
			}
		} else {
			ResultEvent.sendResult(identity, actionEvent.getActionEventId(), MMORPGErrorCode.CODE_MESSAGE_FILTER);
			if (logger.isDebugEnabled()) {
				logger.debug("receivedActionEventImpl filter message, actionEventId=" + actionEvent.getActionEventId() + ", player=" + player);
			}
		}
	}

	@Override
	public void handleDisconnect(Identity identity) {
		String charId = identity.getCharId();
		if (!Strings.isNullOrEmpty(charId)) {
			playerManager.leaveWorld(charId);
		}
	}
	
	private void submitThreadAndWaitTimeout(final Player player, final ActionEventBase actionEvent) {
		Future<?> future = scheduledExecutorService.submit(new Runnable() {
			@Override
			public void run() {
				try {
					player.handleActionEvent(actionEvent);
				} catch (Exception e) {
					logger.error("handleActionEvent error, player=" + player);
					logger.error(DebugUtil.printStack(e));
				}
			}
		});
		
		try {
			future.get(10, TimeUnit.SECONDS);
		} catch (ExecutionException e) {
			logger.error("submitThreadAndWaitTimeout execution actionEvent=" + actionEvent + ", playerName=" + player.getName());
		} catch (TimeoutException e) {
			future.cancel(true);
			logger.error("submitThreadAndWaitTimeout timeout 10s, actionEvent=" + actionEvent + ", playerName=" + player.getName());
		} catch (InterruptedException e) {
			logger.error("submitThreadAndWaitTimeout interrupt, actionEvent=" + actionEvent + ", playerName=" + player.getName());
		}
	}
}
