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
package sophia.mmorpg.player;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import sophia.foundation.property.PropertyDictionary;
import sophia.foundation.util.DebugUtil;
import sophia.foundation.util.MonitorClientEvent;
import sophia.game.GameRoot;
import sophia.game.component.communication.GameEvent;
import sophia.mmorpg.MMORPGContext;
import sophia.mmorpg.base.scene.GameScene;
import sophia.mmorpg.base.sprite.fightProperty.FightPropertyEffectFacade;
import sophia.mmorpg.base.sprite.fightProperty.FightPropertyMgr;
import sophia.mmorpg.base.sprite.state.action.DeadState;
import sophia.mmorpg.code.MMORPGErrorCode;
import sophia.mmorpg.code.MMORPGSuccessCode;
import sophia.mmorpg.core.PropertyDictionaryModifyPhase;
import sophia.mmorpg.core.timer.SFTimeChimeListener;
import sophia.mmorpg.core.timer.SFTimer;
import sophia.mmorpg.core.timer.SFTimerCreater;
import sophia.mmorpg.friend.FriendSystemManager;
import sophia.mmorpg.player.gameEvent.EnterWorld_GE;
import sophia.mmorpg.player.gameEvent.LeaveWorld_GE;
import sophia.mmorpg.player.persistence.PlayerDAO;
import sophia.mmorpg.player.persistence.PlayerMonitorDAO;
import sophia.mmorpg.player.persistence.immediatelySave.PlayerImmediateDAO;
import sophia.mmorpg.player.persistence.immediatelySave.PlayerImmediateDaoFacade;
import sophia.mmorpg.player.scene.PlayerAOIComponent;
import sophia.mmorpg.player.state.PlayerStateMgr;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;
import sophia.mmorpg.ref.editorgen.MGPropertySymbolDefines;
import sophia.mmorpg.stat.StatFunctions;
import sophia.mmorpg.stat.StatService;
import sophia.mmorpg.utils.DateTimeUtil;

import com.google.common.base.Preconditions;

public final class PlayerManager {
	private static final Logger logger = Logger.getLogger(PlayerManager.class.getName());

	public static final String LeaveWorld_GE_Id = LeaveWorld_GE.class.getSimpleName();
	public static final String EnterWorld_GE_Id = EnterWorld_GE.class.getSimpleName();
	
	private static final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors() + 1);

	// charId, Player
	private ConcurrentMap<String, Player> playerMap = new ConcurrentHashMap<String, Player>();
	// charName
	private static Set<String> charNameSet = new HashSet<String>();

	// 当前在线玩家计数
	private static AtomicInteger onlineTotalCount = new AtomicInteger(0);
	// 离线玩家清除Timer
	private static SFTimer kickOutOfflinePlayerTimer;
	// 离线玩家缓存清除间隔
	private static final long KickOutOfflinePlayerIntervalTime = (long) (1.5 * 60 * 60 * 1000L);
	// 最大在线玩家计数
	private static volatile int maxOnlinePlayerCount = 3000;
	// 最大预加载玩家个数
	private static int preloadPlayerCount = 500;
	// 最大缓存玩家个数
	private static int maxCachePlayerCount = 1000;

	public static int getOnlineTotalCount() {
		return onlineTotalCount.get();
	}

	public static boolean isMaxOnlinePlayerCount() {
		return onlineTotalCount.get() >= getMaxOnlinePlayerCount();
	}

	public static boolean addCharName(String charName) {
		synchronized (charNameSet) {
			if (!charNameSet.contains(charName)) {
				charNameSet.add(charName);
				return true;
			}
		}
		return false;
	}

	public static void initialize() {
		logger.info("loading character name list");
		List<String> playerNameList = PlayerDAO.getInstance().selectPlayerName();
		if (playerNameList != null) {
			charNameSet.addAll(playerNameList);
		}
		logger.info("finish load character name list, count=" + charNameSet.size());

		initCachePlayerList(preloadPlayerCount);

		SFTimerCreater timerCreater = MMORPGContext.getTimerCreater();
		kickOutOfflinePlayerTimer = timerCreater.minuteCalendarChime(new SFTimeChimeListener() {
			@Override
			public void handleServiceShutdown() {
			}

			@Override
			public void handleTimeChimeCancel() {
			}

			@Override
			public void handleTimeChime() {
				MMORPGContext.getPlayerComponent().getPlayerManager().checkPlayerConnectState();
				MMORPGContext.getPlayerComponent().getPlayerManager().kickOutInactivityPlayer();
				MMORPGContext.getPlayerComponent().getPlayerManager().setPlayerOnlineTime();
			}
		});
	}

	public void setPlayerOnlineTime() {
		Calendar calendar = Calendar.getInstance();
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		int minute = calendar.get(Calendar.MINUTE);
		if (hour == 23 && minute == 59) {
			for (Player player : getOnlinePlayerList()) {
				try {
					long nowTime = System.currentTimeMillis();
					long nowOnlineTime = nowTime - MGPropertyAccesser.getLastLoginTime(player.getProperty());
					StatFunctions.OnlineStat(player, (int) (nowOnlineTime / 1000) + 60);
				} catch (Exception e) {
					logger.error(DebugUtil.printStack(e));
				}
			}
		}

	}

	public void checkPlayerConnectState() {
		long now = System.currentTimeMillis();
		for (Player player : getOnlinePlayerList()) {
			try {
				if (player.getLastHeartbeatTime() != 0 && now - player.getLastHeartbeatTime() > 5 * 60 * 1000) {
					kickoutPlayerCharacter(player);
					if (logger.isInfoEnabled()) {
						logger.info("kickOut, offline timeout, playerName=" + player);
					}
				}

			} catch (Exception e) {
				logger.error(DebugUtil.printStack(e));
			}
		}
	}

	public static void destoryTimer() {
		if (kickOutOfflinePlayerTimer != null) {
			kickOutOfflinePlayerTimer.cancel();
		}
	}

	public static void initCachePlayerList(int number) {
		logger.info("loading player cache list, count=" + preloadPlayerCount);
		PlayerManager playerManager = MMORPGContext.getPlayerComponent().getPlayerManager();
		long now = System.currentTimeMillis();
		List<Player> players = PlayerDAO.getInstance().selectPlayers(number);
		for (Player player : players) {
			if (PlayerImmediateDAO.getInstance().selectPlayerImmediateData(player)) {
				recoverPlayerAttribute(player);
				MGPropertyAccesser.setOrPutLastLogoutTime(player.getProperty(), now);
				playerManager.addPlayer(player);
			}

			FriendSystemManager.initPlayerChatModule(player.getId());
		}
		logger.info("finish load player cache list, count=" + preloadPlayerCount);
	}

	private static Player selectPlayerByCharId(String charId) {
		Player player = PlayerDAO.getInstance().selectPlayer(charId);
		if (player == null) {
			return null;
		}

		if (!PlayerImmediateDAO.getInstance().selectPlayerImmediateData(player)) {
			PlayerImmediateDaoFacade.insert(player);
		}

		FriendSystemManager.initPlayerChatModule(player.getId());
		recoverPlayerAttribute(player);

		return player;
	}

	private static Player selectPlayerByName(String playerName) {
		Player player = PlayerDAO.getInstance().selectPlayerByName(playerName);
		if (player == null) {
			return null;
		}

		if (!PlayerImmediateDAO.getInstance().selectPlayerImmediateData(player)) {
			PlayerImmediateDaoFacade.insert(player);
		}

		FriendSystemManager.initPlayerChatModule(player.getId());
		recoverPlayerAttribute(player);

		return player;
	}

	private static void recoverPlayerAttribute(Player player) {
		// 恢复血量、魔法量
		PropertyDictionary pd = player.getProperty();
		FightPropertyMgr fightPropertyMgr = player.getFightPropertyMgrComponent().getFightPropertyMgr();
		PropertyDictionaryModifyPhase fightProperty = null;
		try {
			fightProperty = fightPropertyMgr.getSnapshotFromPool();
			int hp = MGPropertyAccesser.getHP(pd);
			int mp = MGPropertyAccesser.getMP(pd);
			int maxHP = MGPropertyAccesser.getMaxHP(fightProperty.getPropertyDictionary());
			int maxMP = MGPropertyAccesser.getMaxMP(fightProperty.getPropertyDictionary());
			if (hp > maxHP) {
				hp = maxHP;
			}
			if (mp > maxMP) {
				mp = maxMP;
			}
			fightPropertyMgr.setSnapshotValueById(MGPropertySymbolDefines.HP_Id, hp);
			fightPropertyMgr.setSnapshotValueById(MGPropertySymbolDefines.MP_Id, mp);
			if (hp <= 0) {
				player.getFightSpriteStateMgr().setCrtActionState(DeadState.DeadState_Id);
				player.setDeadReady(true);
				player.setReviveReady(false);
				if (logger.isDebugEnabled()) {
					logger.debug("recoverPlayerAttribute player dead, player=" + player);
				}
			}
		} finally {
			FightPropertyMgr.recycleSnapshotToPool(fightProperty);
		}
	}

	public Collection<Player> getPlayerList() {
		return playerMap.values();
	}

	public ConcurrentMap<String, Player> getPlayerMap() {
		return playerMap;
	}

	public Collection<Player> getOnlinePlayerList() {
		List<Player> onlinePlayerList = new ArrayList<>();
		Collection<Player> playerList = getPlayerList();
		for (Player player : playerList) {
			if (player.isOnline()) {
				onlinePlayerList.add(player);
			}
		}

		return onlinePlayerList;
	}

	public int getCachePlayerCount() {
		return playerMap.size() - onlineTotalCount.get();
	}

	/**
	 * 根据角色名查询玩家
	 * 
	 * @param playerName
	 * @return
	 */
	public Player getPlayerByName(String playerName) {
		Preconditions.checkNotNull(playerName);
		for (Player player : this.playerMap.values()) {
			if (StringUtils.equals(player.getName(), playerName)) {
				return player;
			}
		}

		// player not in cache
		// load player from database
		return selectPlayerByName(playerName);
	}

	/**
	 * 获取在线玩家
	 * 
	 * @param playerName
	 * @return
	 */
	public Player getOnlinePlayerByName(String playerName) {
		Preconditions.checkNotNull(playerName);
		for (Player player : this.playerMap.values()) {
			if (StringUtils.equals(player.getName(), (playerName)) && player.isOnline()) {
				return player;
			}
		}

		return null;
	}

	/**
	 * 获取在线玩家
	 * 
	 * @param charId
	 * @return
	 */
	public Player getOnlinePlayer(String charId) {
		Player player = playerMap.get(charId);
		if (player != null && player.isOnline()) {
			return player;
		}

		return null;
	}

	/**
	 * 获取玩家
	 * 
	 * @param charId
	 * @return
	 */
	public Player getPlayer(String charId) {
		Preconditions.checkNotNull(charId);
		Player player = playerMap.get(charId);
		if (player != null) {
			return player;
		}

		// player not in cache
		// load player from database
		return selectPlayerByCharId(charId);
	}

	public void kickOutInactivityPlayer() {
		logger.debug("kickOutInactivityPlayer");
		Collection<Player> playerList = getPlayerList();
		long nowTime = System.currentTimeMillis();
		for (Player player : playerList) {
			if (player.isOnline()) {
				continue;
			}

			PropertyDictionary pd = player.getProperty();
			long lastLogoutTime = MGPropertyAccesser.getLastLogoutTime(pd);
			if (nowTime - lastLogoutTime >= KickOutOfflinePlayerIntervalTime) {
				clearPlayer(player);
				if (logger.isDebugEnabled()) {
					logger.debug("kickOutInactivityPlayer player=" + player);
				}
			}
		}
	}

	public void clearAllPlayerAOICache() {
		logger.debug("clearAllPlayerAOICache");
		for (Player player : getPlayerList()) {
			// 防止某个玩家出错影响其他玩家
			try {
				if (player.isOnline()) {
					((PlayerAOIComponent) player.getAoiComponent()).reset();
				}
			} catch (Exception e) {
				logger.error(DebugUtil.printStack(e));
			}
		}
	}

	public void kickoutAllCachePlayer() {
		logger.debug("kickoutAllCachePlayer");
		for (Player player : getPlayerList()) {
			// 防止某个玩家出错影响其他玩家
			try {
				clearPlayer(player);
			} catch (Exception e) {
				logger.error(DebugUtil.printStack(e));
			}
		}
	}

	/**
	 * 踢所有在线玩家下线
	 * 
	 * @param player2
	 */
	public void kickoutAllOnlinePlayer() {
		logger.debug("kickoutAllOnlinePlayer");
		for (Player player : getPlayerList()) {
			// 防止某个玩家出错影响其他玩家
			try {
				if (player.isOnline()) {
					kickoutPlayerCharacter(player);
					if (logger.isInfoEnabled()) {
						logger.info("kickOut, kickoutAllOnlinePlayer, playerName=" + player.getName());
					}
				}
			} catch (Exception e) {
				logger.error(DebugUtil.printStack(e));
			}
		}
	}

	/**
	 * 踢玩家下线
	 * 
	 * @param player
	 */
	public void kickoutPlayerCharacter(Player player) {
		GameRoot.getSimulatorCommunicationService().closeSession(player.getIdentity());
		leaveWorld(player.getId());
	}

	public int enterWorld(Player player) {
		// 把之前的玩家踢下线
		leaveWorld(player.getId());

		AtomicBoolean isEnterWorlding = player.getIsEnterWorlding();

		boolean compareAndSet = false;
		try {
			compareAndSet = isEnterWorlding.compareAndSet(false, true);
			if (compareAndSet == false) {
				return MMORPGErrorCode.CODE_PLAYER_ALREADY_ENTERWORLD;
			}

			if (player.isOnline()) {
				return MMORPGErrorCode.CODE_PLAYER_ALREADY_ONLINE;
			}
			
			if (onlineTotalCount.incrementAndGet() > maxOnlinePlayerCount) {
				onlineTotalCount.decrementAndGet();
				return MMORPGErrorCode.CODE_PLAYER_ONLINEPLAYER_LIMIT;
			}
			
			addPlayer(player);
			
			addGameObjectSafe(player);
			
			player.getPlayerStateMgr().setState(PlayerStateMgr.Online);
			player.setSceneReady(false);

			checkEnterScene(player);

			sendPlayerEnterWorldGameEvent(player);

			StatService.getInstance().getStatOnlineTicker().onEntered(player.getIdentity().getId());
			
			player.setOnline(true);
		} catch (Exception e) {
			logger.error("enterWorld error, player=" + player);
			logger.error("enterWorld error, " + DebugUtil.printStack(e));
			return MMORPGErrorCode.CODE_PLAYER_ENTERWORLD_EXCEPTION;
		} finally {
			if (compareAndSet) {
				if (!player.isOnline()) {
					removeGameObjectSafe(player);
				}
				isEnterWorlding.set(false);
			}
		}

		if (logger.isDebugEnabled()) {
			logger.debug("enterWorld player=" + player);
			FightPropertyEffectFacade.getPropertyHelper().checkAttributeValidity(player);
		}

		return MMORPGSuccessCode.CODE_SUCCESS;
	}

	public void leaveWorld(String charId) {
		Player player = getPlayer(charId);
		if (player == null) {
			return;
		}

		AtomicBoolean isLeaveWorlding = player.getIsLeaveWorlding();
		boolean compareAndSet = false;
		try {
			compareAndSet = isLeaveWorlding.compareAndSet(false, true);
			if (compareAndSet == false) {
				return;
			}

			if (!player.isOnline()) {
				return;
			}
			
			player.setSceneReady(false);

			sendPlayerLeaveWorldGameEvent(player);
			
			leaveSceneSafe(player);

			removeGameObjectSafe(player);
			
			playerTimeCaculateAndStat(player);
			
			player.getPlayerStateMgr().setState(PlayerStateMgr.OffLine);
			
			savePlayerData(player);

			if (MonitorClientEvent.getInstance().isEnabled()) {
				PlayerMonitorDAO.getInstance().save(player);
			}
			
			onlineTotalCount.decrementAndGet();

			// 离开世界后，这个连接的当前角色为空
			player.getIdentity().setCharId("");

			player.setOnline(false);
		} catch (Exception e) {
			logger.error("leaveWorld error, player=" + player);
			logger.error("leaveWorld error, " + DebugUtil.printStack(e));
		} finally {
			if (compareAndSet) {
				isLeaveWorlding.set(false);
			}
		}

		if (logger.isDebugEnabled()) {
			logger.debug("leaveWorld player=" + player);
		}
	}

	public void clearPlayer(Player player) {
		AtomicBoolean isEnterWorlding = player.getIsEnterWorlding();
		boolean compareAndSet = false;
		try {
			compareAndSet = isEnterWorlding.compareAndSet(false, true);
			if (compareAndSet == false) {
				return;
			}

			if (player.isOnline()) {
				return;
			}

			removePlayer(player);
		} finally {
			if (compareAndSet) {
				isEnterWorlding.set(false);
			}
		}
	}

	private void addPlayer(Player player) {
		playerMap.put(player.getId(), player);
	}

	private void removePlayer(Player player) {
		playerMap.remove(player.getId());
	}
	
	public void removeGameObjectSafe(Player player) {
		if (logger.isDebugEnabled()) {
			logger.debug("removeGameObjectSafe, player=" + player);
		}
		
		try {
			GameRoot.getGameObjectManager().removeGameObject(player);
		} catch (Exception e) {
			logger.error("removeGameObjectSafe error, player=" + player);
			logger.error("removeGameObjectSafe error, " + DebugUtil.printStack(e));
		}
	}
	
	public void addGameObjectSafe(Player player) {
		if (logger.isDebugEnabled()) {
			logger.debug("addGameObjectSafe, player=" + player);
		}
		
		try {
			GameRoot.getGameObjectManager().addGameObject(player);
		} catch (Exception e) {
			logger.error("addGameObjectSafe error, player=" + player);
			logger.error("addGameObjectSafe error, " + DebugUtil.printStack(e));
		}
	}
	
	public void leaveSceneSafe(Player player) {
		if (logger.isDebugEnabled()) {
			logger.debug("leaveSceneSafe, player=" + player);
		}
		
		GameScene scene = player.getCrtScene();
		if (scene == null) {
			return;
		}
		
		try {
			player.getAoiComponent().leaveScene(scene);
		} catch (Exception e) {
			logger.error("leaveSceneSafe error, player=" + player);
			logger.error("leaveSceneSafe error, " + DebugUtil.printStack(e));
		}
	}
	
	public void playerTimeCaculateAndStat(Player player) {
		if (logger.isDebugEnabled()) {
			logger.debug("playerTimeCaculateAndStat, player=" + player);
		}
		
		long nowTime = System.currentTimeMillis();
		long nowOnlineTime = nowTime - MGPropertyAccesser.getLastLoginTime(player.getProperty());
		long allOnlineTime = MGPropertyAccesser.getOnlineTime(player.getProperty()) + nowOnlineTime;
		MGPropertyAccesser.setOrPutOnlineTime(player.getProperty(), allOnlineTime);
		MGPropertyAccesser.setOrPutLastLogoutTime(player.getProperty(), nowTime);
		if (DateTimeUtil.isTheSameDay(MGPropertyAccesser.getLastLoginTime(player.getProperty()), nowTime)) {
			StatFunctions.OnlineStat(player, (int) (nowOnlineTime / 1000));
		} else {
			long startTimeToday = DateTimeUtil.getLongTimeOfToday(nowTime);
			nowOnlineTime = nowTime - startTimeToday;
			StatFunctions.OnlineStat(player, (int) (nowOnlineTime / 1000));
		}
	}
	
	public void savePlayerData(Player player) {
		if (logger.isDebugEnabled()) {
			logger.debug("savePlayerData, player=" + player);
		}
		
		MMORPGContext.getPlayerComponent().getSaveService().saveImmediateData(player);
		PlayerImmediateDaoFacade.update(player);
	}

	private void sendPlayerLeaveWorldGameEvent(final Player player) {
		if (logger.isDebugEnabled()) {
			logger.debug("sendPlayerLeaveWorldGameEvent, player=" + player);
		}
		
		Future<?> future = scheduledExecutorService.submit(new Runnable() {
			@Override
			public void run() {
				LeaveWorld_GE leaveWorld_GE = new LeaveWorld_GE();
				GameEvent<LeaveWorld_GE> event = GameEvent.getInstance(LeaveWorld_GE_Id, leaveWorld_GE);
				player.handleGameEvent(event);
				GameEvent.pool(event);
			}
		});
		
		try {
			future.get(5, TimeUnit.SECONDS);
		} catch (ExecutionException e) {
			logger.error("sendPlayerLeaveWorldGameEvent execution exception, playerName=" + player.getName());
		} catch (TimeoutException e) {
			future.cancel(true);
			logger.error("sendPlayerLeaveWorldGameEvent timeout 5s, playerName=" + player.getName());
		} catch (InterruptedException e) {
			logger.error("sendPlayerLeaveWorldGameEvent interrupt, playerName=" + player.getName());
		}
	}

	private void sendPlayerEnterWorldGameEvent(final Player player) {
		if (logger.isDebugEnabled()) {
			logger.debug("sendPlayerEnterWorldGameEvent, player=" + player);
		}
		
		Future<?> future = scheduledExecutorService.submit(new Runnable() {
			@Override
			public void run() {
				EnterWorld_GE enterWorld_GE = new EnterWorld_GE(player);
				GameEvent<EnterWorld_GE> event = GameEvent.getInstance(EnterWorld_GE_Id, enterWorld_GE);
				player.handleGameEvent(event);
				GameEvent.pool(event);
			}
		});
		
		try {
			future.get(5, TimeUnit.SECONDS);
		} catch (ExecutionException e) {
			logger.error("sendPlayerEnterWorldGameEvent execution exception, playerName=" + player.getName());
		} catch (TimeoutException e) {
			future.cancel(true);
			logger.error("sendPlayerEnterWorldGameEvent timeout 5s, playerName=" + player.getName());
		} catch (InterruptedException e) {
			logger.error("sendPlayerEnterWorldGameEvent interrupt, playerName=" + player.getName());
		}
	}

	private void checkEnterScene(Player player) {
		if (logger.isDebugEnabled()) {
			logger.debug("checkEnterScene, player=" + player);
		}
		
		PlayerEnterSceneCheckFacade.checkEnterScene(player);
	}

	public static int getMaxOnlinePlayerCount() {
		return maxOnlinePlayerCount;
	}

	public static void setMaxOnlinePlayerCount(int maxOnlinePlayerCount) {
		PlayerManager.maxOnlinePlayerCount = maxOnlinePlayerCount;
	}

	public static int getPreloadPlayerCount() {
		return preloadPlayerCount;
	}

	public static void setPreloadPlayerCount(int preloadPlayerCount) {
		PlayerManager.preloadPlayerCount = preloadPlayerCount;
	}

	public static int getMaxCachePlayerCount() {
		return maxCachePlayerCount;
	}

	public static void setMaxCachePlayerCount(int maxCachePlayerCount) {
		PlayerManager.maxCachePlayerCount = maxCachePlayerCount;
	}
}
