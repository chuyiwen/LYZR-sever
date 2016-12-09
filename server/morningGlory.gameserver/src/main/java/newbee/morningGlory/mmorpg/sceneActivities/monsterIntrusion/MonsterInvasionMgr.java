package newbee.morningGlory.mmorpg.sceneActivities.monsterIntrusion;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import newbee.morningGlory.mmorpg.sceneActivities.SceneActivity;
import newbee.morningGlory.mmorpg.sceneActivities.event.G2C_MonsterIntrusion_ContinuTime;
import newbee.morningGlory.mmorpg.sceneActivities.event.G2C_MonsterIntrusion_IsOpen;
import newbee.morningGlory.mmorpg.sceneActivities.event.G2C_MonsterIntrusion_LeaveMap;
import newbee.morningGlory.mmorpg.sceneActivities.event.SceneActivityEventDefines;
import newbee.morningGlory.mmorpg.sceneActivities.monsterIntrusion.ref.MonsterInvasionRef;

import org.apache.log4j.Logger;

import sophia.foundation.communication.core.ActionEventBase;
import sophia.foundation.communication.core.MessageFactory;
import sophia.foundation.util.Position;
import sophia.game.GameRoot;
import sophia.game.component.communication.GameEvent;
import sophia.mmorpg.MMORPGContext;
import sophia.mmorpg.base.scene.GameScene;
import sophia.mmorpg.base.scene.ref.SceneMonsterRefData;
import sophia.mmorpg.monster.Monster;
import sophia.mmorpg.monster.ref.MonsterRef;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.chat.sysytem.SystemPromptFacade;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;
import sophia.mmorpg.utils.DateTimeUtil;
import sophia.mmorpg.world.ActionEventFacade;

public class MonsterInvasionMgr extends SceneActivity {
	private static final Logger logger = Logger.getLogger(MonsterInvasionMgr.class);
	private boolean isOpen = false;
	private GameScene gameScene;
	
	private static Map<Long, String> bossRefreshData = new HashMap<Long, String>();
	
	private static List<Long> timeList = new ArrayList<Long>();
	
	private static Map<Long, String> timeToGameSceneRefIdMapping = new HashMap<Long, String>();
	
	@Override
	public void handleGameEvent(GameEvent<?> event) {
	}

	@Override
	public void handleActionEvent(ActionEventBase event) {
	}

	@Override
	public void checkBegin(Calendar crtCalendar) {
		super.checkBegin(crtCalendar);
	}

	@Override
	public boolean checkEnter(Player player) {
		return isOpen();
	}

	@Override
	public boolean checkLeave(Player player) {
		return true;
	}

	@Override
	public boolean onPreStart() {
		String sceneId = getRef().getSceneRefId();
		gameScene = MMORPGContext.getGameAreaComponent().getGameArea().getSceneById(sceneId);
		if (gameScene == null) {
			return false;
		}
		Collection<Monster> allMonsters = gameScene.getMonsterMgrComponent().getAllMonsters();
		for (Monster monster : allMonsters) {
			if (monster.isBoss()) {
				monster.getCrtScene().getMonsterMgrComponent().leaveWorld(monster);
				monster.getCrtScene().getMonsterMgrComponent().enterRevive(monster);
			}
		}
		return true;
	}

	@Override
	public boolean onPreEnd() {
		SystemPromptFacade.broadCastMonsterInvasionPreEnd();
		return true;
	}

	@Override
	public boolean onStart() {
		String sceneId = getRef().getSceneRefId();
		gameScene = MMORPGContext.getGameAreaComponent().getGameArea().getSceneById(sceneId);
		if (gameScene == null) {
			return false;
		}
		setGameScene(gameScene);
		setIsOpen(true);
		loadCurrentBossRefData();
		SystemPromptFacade.broadCastMonsterInvasionStart();
		broadCastMonsterIntrusionOpen();
		return true;
	}

	@Override 
	public boolean onCheckEnd() {
		String sceneId = getRef().getSceneRefId();
		GameScene gameScene = MMORPGContext.getGameAreaComponent().getGameArea().getSceneById(sceneId);
		if (gameScene == null) {
			return false;
		}

		Map<String, Player> playerMap = gameScene.getPlayerMgrComponent().getPlayerMap();
		if(playerMap.isEmpty()){
			return false;
		}
		
		for (Entry<String, Player> entry : playerMap.entrySet()) {
			Player player = entry.getValue();
			player.getPlayerSceneComponent().goBackComeFromSceneOrGoHome();

			RecoveryExpMultiple(player);
			G2C_MonsterIntrusion_LeaveMap res = MessageFactory.getConcreteMessage(SceneActivityEventDefines.G2C_MonsterIntrusion_LeaveMap);
			GameRoot.sendMessage(player.getIdentity(), res);
		}
		
		return true;
	}
	@Override
	public boolean onEnd() {
		setIsOpen(false);
		clearBossRefreshData();
		SystemPromptFacade.broadCastMonsterInvasionEnd();
		broadCastMonsterIntrusionOpen();

		String sceneId = getRef().getSceneRefId();
		GameScene gameScene = MMORPGContext.getGameAreaComponent().getGameArea().getSceneById(sceneId);
		if (gameScene == null) {
			return true;
		}

		Map<String, Player> playerMap = gameScene.getPlayerMgrComponent().getPlayerMap();
		for (Entry<String, Player> entry : playerMap.entrySet()) {
			//leaveScene(entry.getValue());
			Player player = entry.getValue();
			player.getPlayerSceneComponent().goBackComeFromSceneOrGoHome();
			MonsterInvasionComponent invasion = (MonsterInvasionComponent) (entry.getValue()).getTagged(MonsterInvasionComponent.Tag);
			invasion.dropItem(gameScene,player);
			RecoveryExpMultiple(player);
			G2C_MonsterIntrusion_LeaveMap res = MessageFactory.getConcreteMessage(SceneActivityEventDefines.G2C_MonsterIntrusion_LeaveMap);
			GameRoot.sendMessage(player.getIdentity(), res);
		}
		
		G2C_MonsterIntrusion_ContinuTime countinuTime = MessageFactory.getConcreteMessage(SceneActivityEventDefines.G2C_MonsterIntrusion_ContinuTime);
		countinuTime.setTimeToStart(getActivityRemainingStartTime());
		countinuTime.setTimeToEnd(getActivityRemainingEndTime());
		ActionEventFacade.sendMessageToWorld(countinuTime);

		return true;
	}

	public void setIsOpen(boolean isOpen) {
		this.isOpen = isOpen;
	}

	public boolean isOpen() {
		return isOpen;
	}

	public GameScene getGameScene() {
		return gameScene;
	}

	public void setGameScene(GameScene gameScene) {
		this.gameScene = gameScene;
	}

	/**
	 * 向客户端发送怪物入侵开启或者关闭的消息
	 * 
	 * @param type
	 */
	public void broadCastMonsterIntrusionOpen() {
		G2C_MonsterIntrusion_IsOpen res = MessageFactory.getConcreteMessage(SceneActivityEventDefines.G2C_MonsterIntrusion_IsOpen);
		res.setIsOpen(isOpen());
		ActionEventFacade.sendMessageToWorld(res);
	}

	/**
	 * 距离活动开始时间
	 * 
	 * @return
	 */
	public long getActivityRemainingStartTime() {
		if (isOpen()) {
			return 0;
		}
		if (getChimeList().isEmpty()) {
			return 0;
		}
		return getChimeList().get(0).getRemainStartTime();
	}

	/**
	 * 获取当前活动剩余时间
	 * 
	 * @return
	 */
	public long getActivityRemainingEndTime() {
		if (!isOpen()) {
			return 0;
		}
		if (getChimeList().isEmpty()) {
			return 0;
		}
		return getChimeList().get(0).getRemainEndTime();
	}

	public MonsterInvasionRef getMonsterIntrusionRef() {
		return getRef().getComponentRef(MonsterInvasionRef.class);
	}

	/**
	 * 还原加倍经验
	 * 
	 * @param player
	 */
	public void RecoveryExpMultiple(Player player) {
		double expMultiple = getMonsterIntrusionRef().getExpMultiple();		
		player.getExpComponent().subExpMultiple((float) expMultiple);
	}

	
	public void sendMonsterRefreshTime(Collection<Player> playerList) {
		Collection<Monster> allMonsters = getAllSceneMonster();
		
		for (Monster monster : allMonsters) {
			if (monster.isBoss()) {
				if (logger.isDebugEnabled()) {
					logger.debug("boss is alive!, monster=" + monster);
				}
				String bossSceneRefId = monster.getCrtScene().getRef().getId();
				sendBossRefreshTime(bossSceneRefId, 0, monster.getMonsterRef().getId(), (byte) 1, playerList);
				return;
			} 
		}
		
		long now = System.currentTimeMillis();
		Long nextTimeMillis = getNextTimeMillis(now);
		if (nextTimeMillis == null) {
			if (timeList.isEmpty()) {
				if (logger.isDebugEnabled()) {
					logger.debug("no monster will refresh, no last timeMillis");
				}
				return;
			}
			Long timeMillis = timeList.get(timeList.size() - 1);
			String lastMonsterRefId = getMonsterRefId(timeMillis);
			String bossSceneRefId = getBossSceneRefId(timeMillis);
			sendBossRefreshTime(bossSceneRefId, 0, lastMonsterRefId, (byte) 0, playerList);
		} else {
			String monsterRefId = getMonsterRefId(nextTimeMillis);
			String bossSceneRefId = getBossSceneRefId(nextTimeMillis);
			long remainMillis = nextTimeMillis - now;
			sendBossRefreshTime(bossSceneRefId, remainMillis, monsterRefId, (byte) 0, playerList);
		}
	}
	
	private String getBossSceneRefId(Long millis) {
		String  bossSceneRefId = timeToGameSceneRefIdMapping.get(millis);
	
		if (bossSceneRefId == null) {
			logger.error("boss refresh or last boss, bossSceneRefId is null");
		}
	
		return bossSceneRefId;
	}
	
	public void sendMonsterRefreshTime(Player player) {
		Collection<Player> playerList = new ArrayList<Player>();
		playerList.add(player);
		
		sendMonsterRefreshTime(playerList);
	}
	
	public void sendMonsterRefreshTime() {
		Collection<Player> allScenePlayer = getAllScenePlayer();
		if (allScenePlayer.isEmpty()) {
			return;
		}
		
		sendMonsterRefreshTime(allScenePlayer);
	}
	
	private String getMonsterRefId(Long timeMillis) {
		String lastMonsterRefId = bossRefreshData.get(timeMillis);
	
		logger.info("lastMonsterRefId = " + lastMonsterRefId);
	
		return lastMonsterRefId;
	}
	
	
	private Long getNextTimeMillis(long millis) {
		for (Long timeMillis : timeList) {
			if (millis < timeMillis.longValue()) {
				if (logger.isDebugEnabled()) {
					logger.debug("nextMillis = " + millis);
				}
				return timeMillis;
			}
		}
		
		return null;
	}
	
	/**
	 * 获取距离现在最近的一个boss刷新时间及MonsterRefId
	 * 
	 * @param list
	 * @return
	 */
//	public Map<Long, String> getMonsterRefreshTime(List<SceneMonsterRefData> list) {
//		Map<Long, String> monsterRefresh = new HashMap<>();
//		for (SceneMonsterRefData sceneMonsterRefData : list) {
//			String monsterRefId = sceneMonsterRefData.getMonsterRefId();
//			MonsterRef monsterRef = (MonsterRef) GameRoot.getGameRefObjectManager().getManagedObject(sceneMonsterRefData.getMonsterRefId());
//			int quality = MGPropertyAccesser.getQuality(monsterRef.getProperty());
//			if (quality == 3) {// boss
//				String[] times = sceneMonsterRefData.getTimingRefresh().split("&");
//				Calendar crtCalendar = Calendar.getInstance();
//				long timeToRefresh = 0;
//				int refreshMin = 0;
//				for (String time : times) {
//					String[] refreshTime = time.split(":");
//					int hour = Integer.parseInt(refreshTime[0]);
//					int minute = Integer.parseInt(refreshTime[1]);
//					int second = Integer.parseInt(refreshTime[2]);
//					int nowHour = crtCalendar.get(Calendar.HOUR_OF_DAY);
//					int nowMinute = crtCalendar.get(Calendar.MINUTE);
//					int nowSecond = crtCalendar.get(Calendar.SECOND);
//					if (nowHour > hour) {
//						continue;
//					} else if (nowHour == hour && nowMinute > minute) {
//						if (StringUtils.isEmpty(nowMonster) || minute >= refreshMin) {
//							nowMonster = monsterRefId;
//							refreshMin = minute;
//						}
//						continue;
//					} else if (nowHour == hour && nowMinute == minute && nowSecond > second) {
//						continue;
//					} else {
//						long newTime = ((hour - nowHour) * 60 * 60 + (minute - nowMinute) * 60 + (second - nowSecond)) * 1000;
//						if (timeToRefresh == 0) {
//							timeToRefresh = newTime;
//						}
//						timeToRefresh = timeToRefresh < newTime ? timeToRefresh : newTime;
//					}
//				}
//				if (timeToRefresh > 0) {
//					monsterRefresh.put(timeToRefresh, monsterRefId);
//					nowMonster = monsterRefId;
//				}
//			}
//		}
//		return monsterRefresh;
//	}
	
	public void sendBossRefreshTime(String bossSceneRefId, long time, String monsterRefId, byte isDead, Collection<Player> playerList) {
		if (playerList == null || playerList.isEmpty()) {
			return;
		}
		
		for (Player player : playerList) {
			MonsterInvasionComponent invasion = (MonsterInvasionComponent) (player).getTagged(MonsterInvasionComponent.Tag);
			invasion.bossRefreshTime(monsterRefId, bossSceneRefId, time, isDead);
		}
	}
	
	private void clearBossRefreshData() {
		bossRefreshData.clear();
		timeList.clear();
		timeToGameSceneRefIdMapping.clear();
	}
	

	private void loadCurrentBossRefData() {
		logger.info("load current activity boss Data...");
		List<SceneMonsterRefData> list = gameScene.getRef().getMonsterRefDatas();
		String sceneRefId = gameScene.getRef().getId();
		
		for (SceneMonsterRefData monsterRefData : list) {
			String monsterRefId = monsterRefData.getMonsterRefId();
			MonsterRef monsterRef = (MonsterRef) GameRoot.getGameRefObjectManager().getManagedObject(monsterRefId);
			int quality = MGPropertyAccesser.getQuality(monsterRef.getProperty());
			if (quality != 3) {
				continue;
			}
			
			String[] times = monsterRefData.getTimingRefresh().split("&");
			for (String time : times) {
				long timeMillis = getTimeMillis(time);
				if (isInPeriod(timeMillis)) {
					logger.info("time = " + time + ", monsterRefId = " + monsterRefId);
					bossRefreshData.put(timeMillis, monsterRefId);
					
					timeToGameSceneRefIdMapping.put(timeMillis, sceneRefId);
				}
			}
		}
		
		timeList.addAll(bossRefreshData.keySet());
		Collections.sort(timeList);
	}
	
	private long getTimeMillis(String timeString) {
		String[] refreshTime = timeString.split(":");
		int hour = Integer.parseInt(refreshTime[0]);
		int minute = Integer.parseInt(refreshTime[1]);
		int second = Integer.parseInt(refreshTime[2]);
		long timeMillis = DateTimeUtil.getTimeMillis(hour, minute, second);
		
		return timeMillis;
	}

	public List<Monster> getAllSceneMonster() {
		List<Monster> monsterList = new ArrayList<Monster>();
		
		List<String> sceneRefIdList = getMonsterIntrusionRef().getSceneRefIdList();
		for (String sceneId : sceneRefIdList) {
			GameScene gameScene = MMORPGContext.getGameAreaComponent().getGameArea().getSceneById(sceneId);
			if (gameScene == null) {
				return monsterList;
			}
			
			monsterList.addAll(gameScene.getMonsterMgrComponent().getAllMonsters());
		}

		return monsterList;
	}
	
	public Collection<Player> getAllScenePlayer() {
		List<String> sceneRefIdList = getMonsterIntrusionRef().getSceneRefIdList();

		Collection<Player> playerList = new ArrayList<>(); 
		for (String sceneId : sceneRefIdList) {
			GameScene gameScene = MMORPGContext.getGameAreaComponent().getGameArea().getSceneById(sceneId);
			if (gameScene == null) {
				return playerList;
			}
		
			playerList.addAll(gameScene.getPlayerMgrComponent().getPlayerMap().values());
		}

		return playerList;
	}

	public void setCrtActivityState(byte temp) {
		this.crtActivityState = temp;
	}

	public void setPreActivityState(byte temp) {
		this.preActivityState = temp;
	}

	@Override
	public boolean onEnter(Player player) {
		return false;
	}

	@Override
	public boolean onLeave(Player player) {
		return false;
	}
}
