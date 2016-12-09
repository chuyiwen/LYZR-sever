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
package newbee.morningGlory.mmorpg.gameInstance;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CopyOnWriteArrayList;

import newbee.morningGlory.MorningGloryContext;
import newbee.morningGlory.code.MGErrorCode;
import newbee.morningGlory.mmorpg.gameInstance.condition.MGGameInstanceConditionMgr;
import newbee.morningGlory.mmorpg.gameInstance.event.G2C_GameInstanceSceneFinish;
import newbee.morningGlory.mmorpg.gameInstance.event.GameInstanceEventDefines;
import newbee.morningGlory.mmorpg.player.gameInstance.GameInstanceMgr;
import newbee.morningGlory.mmorpg.player.gameInstance.PlayerGameInstanceComponent;
import newbee.morningGlory.stat.MGStatFunctions;
import newbee.morningGlory.stat.logs.StatGameInstance;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import sophia.foundation.communication.core.MessageFactory;
import sophia.foundation.util.Position;
import sophia.game.GameRoot;
import sophia.mmorpg.MMORPGContext;
import sophia.mmorpg.base.scene.GameScene;
import sophia.mmorpg.base.scene.grid.SceneGrid;
import sophia.mmorpg.base.scene.ref.AbstractGameSceneRef;
import sophia.mmorpg.base.scene.ref.region.SceneTransInRegion;
import sophia.mmorpg.core.linyuesheng.LinYueShengModeCondition;
import sophia.mmorpg.gameArea.GameArea;
import sophia.mmorpg.gameInstance.ComeFromScene;
import sophia.mmorpg.gameInstance.GameInstance;
import sophia.mmorpg.gameInstance.GameInstanceRef;
import sophia.mmorpg.gameInstance.GameInstanceSceneRef;
import sophia.mmorpg.gameInstance.GameInstanceSceneRefMgr;
import sophia.mmorpg.gameInstance.GameInstanceState;
import sophia.mmorpg.gameInstance.OpenTimeData;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.itemBag.ItemFacade;
import sophia.mmorpg.player.itemBag.ItemOptSource;
import sophia.mmorpg.utils.RuntimeResult;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

public final class MGGameInstance implements GameInstance {
	public static final byte OK = 0;
	public static final byte ERROR = -1;

	private static final Logger logger = Logger.getLogger(MGGameInstance.class.getName());

	private String id;

	private byte state = -1;

	// 记录玩家进入副本之前的场景<玩家ID,场景RefId>
	private Map<String, ComeFromScene> playerIdToComeFromSceneRefId;

	private GameScene crtGameScene;

	private GameInstanceRef gameInstanceRef;

	private Player creater;

	private CopyOnWriteArrayList<Player> playerList;

	// <玩家ID,<怪物RefId,杀怪次数>>
	private Map<String, Map<String, Short>> killMonsterRecords;

	// 副本的层数
	private short layerId;

	public MGGameInstance() {
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	@Override
	public byte getState() {
		return state;
	}

	@Override
	public short getLayerId() {
		return layerId;
	}

	@Override
	public int nextLayer() {
		if (!checkOpen()) {
			if (logger.isDebugEnabled()) {
				logger.debug("副本未开放,....副本当前状态：" + state);
			}

			return MGErrorCode.CODE_GAME_INSTANCE_NOT_OPEN;
		}

		GameInstanceSceneRefMgr gameInstanceSceneRefMgr = gameInstanceRef.getGameInstanceDataObject(GameInstanceSceneRefMgr.class);
		List<GameInstanceSceneRef> instanceSceneList = gameInstanceSceneRefMgr.getInstanceSceneList();
		if ((layerId + 1) > instanceSceneList.size() - 1) {
			if (logger.isDebugEnabled()) {
				logger.debug("该副本不存在下一层,crtSceneRefId:" + instanceSceneList.get(layerId).getSceneRefId());
			}

			return MGErrorCode.CODE_GAME_INSTANCE_NOT_EXIST_NEXT_LAYER;
		}

		GameInstanceSceneRef gameInstanceSceneRef = instanceSceneList.get(layerId);
		Map<String, Integer> consumptionItems = gameInstanceSceneRef.getConsumptionItems();
		// 判断是否可以进入下一层
		List<LinYueShengModeCondition<Player>> gameInstanceSceneFinishConditionList = gameInstanceSceneRef.getSceneFinishConditionList();
		if (gameInstanceSceneFinishConditionList != null) {
			for (LinYueShengModeCondition<Player> linYueShengModeCondition : gameInstanceSceneFinishConditionList) {
				for (Player player : playerList) {
					RuntimeResult result = linYueShengModeCondition.eligible(player);
					if (!result.isOK()) {
						if (logger.isDebugEnabled()) {
							logger.debug("副本进入下一层,条件不满足..." + " crtSceneRefId:" + instanceSceneList.get(layerId).getSceneRefId());
						}

						return result.getApplicationCode();
					}
				}
			}
		}

		RuntimeResult result = this.consumptionItems(consumptionItems, creater);
		if (!result.isOK()) {
			if (logger.isDebugEnabled()) {
				logger.debug("扣除镇魔令失败，不能进入下一层");
			}

			return MGErrorCode.CODE_GAME_INSTANCE_NOT_ENOUGH_ZHENMOLING;
		}

		layerId++;
		GameScene lastGameScene = this.getCrtGameScene();
		String dstSceneRefId = instanceSceneList.get(layerId).getSceneRefId();
		Position dstTransPosition = getDstTransPosition(dstSceneRefId);
		this.creater.getPlayerSceneComponent().switchTo(dstSceneRefId, dstTransPosition.getX(), dstTransPosition.getY());
		GameScene createrCrtGameScene = creater.getCrtScene();
		// 发送玩家进入副本事件（创建副本任务）
		PlayerGameInstanceComponent playerGameInstanceComponent = (PlayerGameInstanceComponent) creater.getTagged(PlayerGameInstanceComponent.Tag);
		playerGameInstanceComponent.createQuest(this.getGameInstanceRef().getId(), dstSceneRefId);
		MGStatFunctions.gameInstanceStat(creater, StatGameInstance.NextLayer, gameInstanceRef.getId(), gameInstanceSceneRef.getId());

		if (logger.isDebugEnabled()) {
			logger.debug("副本进入下一层," + createrCrtGameScene);
		}

		this.crtGameScene = createrCrtGameScene;
		// 全部玩家同时进入下一层
		for (Player player : playerList) {

			if (StringUtils.equals(this.creater.getId(), player.getId())) {
				continue;
			}

			// 扣除消耗道具（镇魔令）
			result = this.consumptionItems(consumptionItems, player);
			if (!result.isOK()) {
				if (logger.isDebugEnabled()) {
					logger.debug("扣除镇魔令失败，进入下一层失败，player=" + player);
				}

				continue;
			}

			playerGameInstanceComponent = (PlayerGameInstanceComponent) player.getTagged(PlayerGameInstanceComponent.Tag);
			player.getPlayerSceneComponent().switchTo(createrCrtGameScene.getId(), dstTransPosition.getX(), dstTransPosition.getY());
			if (logger.isDebugEnabled()) {
				logger.debug("副本进入下一层, player=" + player + ", dstSceneRefId=" + dstSceneRefId);
			}

			// 发送玩家进入副本事件（创建副本任务）
			playerGameInstanceComponent.createQuest(this.getGameInstanceRef().getId(), dstSceneRefId);
			MGStatFunctions.gameInstanceStat(player, StatGameInstance.NextLayer, gameInstanceRef.getId(), gameInstanceSceneRef.getId());
		}

		// 销毁上一层副本的场景
		if (lastGameScene != null) {
			if (logger.isDebugEnabled()) {
				logger.debug("销毁副本场景,sceneId:" + lastGameScene.getId());
			}

			getGameArea().removeGameInstanceScene(lastGameScene);
		}

		this.crtGameScene = creater.getCrtScene();
		clearResourceForNextLayer();
		return (int) MGGameInstance.OK;
	}

	@Override
	public byte create(Player player, GameInstanceRef gameInstanceRef) {
		// create game instance but not scene
		if (checkCreate()) {
			if (logger.isDebugEnabled()) {
				logger.debug("副本创建失败....,playerId:" + player.getId() + ",副本当前状态：" + state);
			}

			return MGGameInstance.ERROR;
		}

		// 判断玩家是否可以进入副本
		MGGameInstanceConditionMgr gameInstanceConditionMgr = gameInstanceRef.getGameInstanceDataObject(MGGameInstanceConditionMgr.class);
		List<LinYueShengModeCondition<Player>> gameInstanceEnterConditionList = gameInstanceConditionMgr.getGameInstanceEnterConditionList(gameInstanceRef.getId());
		for (LinYueShengModeCondition<Player> linYueShengModeCondition : gameInstanceEnterConditionList) {
			RuntimeResult result = linYueShengModeCondition.eligible(player);
			if (!result.isOK()) {
				if (logger.isDebugEnabled()) {
					logger.debug("创建副本条件不满足....");
				}
				return -1;
			}
		}

		this.gameInstanceRef = gameInstanceRef;
		this.creater = player;
		this.playerList = new CopyOnWriteArrayList<Player>();
		this.playerList.add(player);
		this.layerId = 0;// 0：代表副本第一层
		this.killMonsterRecords = new HashMap<String, Map<String, Short>>();
		this.playerIdToComeFromSceneRefId = new HashMap<String, ComeFromScene>();
		this.state = GameInstanceState.Created_State;
		if (logger.isDebugEnabled()) {
			logger.debug("副本创建完毕....,playerId:" + player.getId());
		}

		return MGGameInstance.OK;
	}

	@Override
	public RuntimeResult open() {
		if (checkOpen()) {
			if (logger.isDebugEnabled()) {
				logger.debug("副本开放失败....副本当前状态：" + state);
			}

			return RuntimeResult.RuntimeApplicationError(MGErrorCode.CODE_GAME_INSTANCE_NOT_OPEN);
		}

		// 判断玩家是否可以进入副本
		RuntimeResult canEnter = checkPlayerCanEnterGameInstance();
		if (canEnter.isError()) {
			return canEnter;
		}

		GameInstance gameInstance = getGameInstanceMgr().getGameInstace(this.id);
		String gameInstanceRefId = gameInstance.getGameInstanceRef().getId();
		// create scene and all player enter scene
		GameInstanceSceneRef gameInstanceSceneRef = getGameInstanceSceneRef(0);// 第一层
		String dstSceneRefId = gameInstanceSceneRef.getSceneRefId();

		// 记录玩家当前的场景与坐标
		String sceneRefId = creater.getCrtScene().getRef().getId();
		int x = creater.getCrtPosition().getX();
		int y = creater.getCrtPosition().getY();

		// 先把creater 传进去
		Position dstTransPosition = getDstTransPosition(dstSceneRefId);
		RuntimeResult switchTo = creater.getPlayerSceneComponent().switchTo(dstSceneRefId, dstTransPosition.getX(), dstTransPosition.getY());
		if (switchTo.isError()) {
			return switchTo;
		}

		// 记录进入副本时的场景和坐标
		this.savePlayerComeFromSceneAndPos(creater, sceneRefId, x, y);
		this.state = GameInstanceState.Opened_State;

		GameScene createrCrtGameScene = creater.getCrtScene();
		this.crtGameScene = createrCrtGameScene;
		if (logger.isDebugEnabled()) {
			logger.debug("createScene, gameScene=" + createrCrtGameScene);
		}

		playerEnterGameInstance(creater, gameInstanceRefId, gameInstanceSceneRef, dstSceneRefId);
		// 传送副本其他玩家进去
		sendAllPlayerEnterScene(gameInstanceRefId, gameInstanceSceneRef, dstTransPosition, createrCrtGameScene);
		// 副本时间限制
		managedGameInstance();
		if (logger.isDebugEnabled()) {
			logger.debug("副本开始完毕....");
		}

		return RuntimeResult.OK();
	}

	public RuntimeResult enter(Player player) {
		if (!isOpen()) {
			if (logger.isDebugEnabled()) {
				logger.debug("副本未开放....副本当前状态：" + state);
			}

			return RuntimeResult.RuntimeApplicationError(MGErrorCode.CODE_GAME_INSTANCE_NOT_OPEN);
		}

		// 判断玩家是否可以进入副本
		RuntimeResult canEnter = checkPlayerCanEnterGameInstance(player);
		if (canEnter.isError()) {
			return canEnter;
		}

		GameInstance gameInstance = getGameInstanceMgr().getGameInstace(this.id);
		String gameInstanceRefId = gameInstance.getGameInstanceRef().getId();
		// create scene and all player enter scene
	
		GameInstanceSceneRef gameInstanceSceneRef = getGameInstanceSceneRef(0);// 第一层
		String dstSceneRefId = gameInstanceSceneRef.getSceneRefId();

		// 记录玩家当前的场景与坐标
		String sceneRefId = player.getCrtScene().getRef().getId();
		int x = player.getCrtPosition().getX();
		int y = player.getCrtPosition().getY();

		// 把player 传进去
		Position dstTransPosition = getDstTransPosition(dstSceneRefId);
		RuntimeResult switchTo = RuntimeResult.OK();
		if(crtGameScene == null){
			switchTo = player.getPlayerSceneComponent().switchTo(dstSceneRefId, dstTransPosition.getX(), dstTransPosition.getY());			
		}else{
			switchTo = player.getPlayerSceneComponent().switchTo(crtGameScene, dstTransPosition.getX(), dstTransPosition.getY());
		}
		
		if (switchTo.isError()) {
			return switchTo;
		}

		// 记录进入副本时的场景和坐标
		this.savePlayerComeFromSceneAndPos(player, sceneRefId, x, y);
		GameScene createrCrtGameScene = player.getCrtScene();
		this.crtGameScene = createrCrtGameScene;
		if (logger.isDebugEnabled()) {
			logger.debug("createScene, gameScene=" + createrCrtGameScene);
		}

		playerEnterGameInstance(player, gameInstanceRefId, gameInstanceSceneRef, dstSceneRefId);
		// 副本时间限制
		managedGameInstance();
		if (logger.isDebugEnabled()) {
			logger.debug("副本开始完毕....");
		}

		return RuntimeResult.OK();
	}

	private void managedGameInstance() {
		long now = System.currentTimeMillis();
		OpenTimeData openTimeData = this.getGameInstanceRef().getOpen().getOpenTime(now);
		// openTimeData不为空证明有副本开放
		if (openTimeData != null) {
			long duringTime = openTimeData.getLastTime();
			long openingTime = this.getGameInstanceRef().getOpen().getTimestamp(openTimeData) / 1000;
			if (duringTime > 0) {
				// (定时检测 是否超过副本限制时间)
				MorningGloryContext.getGameInstanceSystemComponent().getGameInstanceOpeningTimeMgr()
						.managedGameInstance(this, openingTime, duringTime, MGGameInstanceOpeningTimeType.Daily_OpeningTime);
			}
		}
	}

	@Override
	public byte close() {
		if (!checkClose()) {
			if (logger.isDebugEnabled()) {
				logger.debug("副本关闭失败....副本当前状态：" + state);
			}
			return MGGameInstance.ERROR;
		}
		this.state = GameInstanceState.Closed_State;
		this.creater = null;
		this.crtGameScene = null;
		this.gameInstanceRef = null;
		this.layerId = 0;
		if (playerIdToComeFromSceneRefId != null) {
			this.playerIdToComeFromSceneRefId.clear();
		}
		if (killMonsterRecords != null) {
			this.killMonsterRecords.clear();// 销毁玩家的杀怪记录
		}
		if (logger.isDebugEnabled()) {
			logger.debug("副本关闭成功....");
		}
		return 0;
	}

	@Override
	public GameScene getCrtGameScene() {
		return crtGameScene;
	}

	@Override
	public GameInstanceRef getGameInstanceRef() {
		return gameInstanceRef;
	}

	@Override
	public boolean isMultiPlayerGameInstance() {
		return gameInstanceRef.isMultiPlayerGameInstance();
	}

	@Override
	public void addPlayer(Player player) {
		if (!isOpen()) {
			if (logger.isDebugEnabled()) {
				logger.debug("只有副本处于开放阶段才能请求加入副本,playerId:" + player.getId() + "....副本当前状态：" + state);
			}
			return;
		}

		if (isClose()) {
			if (logger.isDebugEnabled()) {
				logger.debug("该副本已经关闭,playerId:" + player.getId() + "....副本当前状态：" + state);
			}
			return;
		}

		
		// 请求加入别人的副本
		RuntimeResult result = checkPlayerCanEnterGameInstance(player);
		if (!result.isOK()){
			return;
		}
		
		if (playerList.contains(player)) {
			if (logger.isDebugEnabled()) {
				logger.debug("重复加入副本,playerId:" + player.getId() + "....副本当前状态：" + state);
			}

			return;
		}

		// 把对应的GameInstanceId 挂到玩家的PlayerGameInstanceComponent上
		PlayerGameInstanceComponent playerGameInstanceComponent = (PlayerGameInstanceComponent) player.getTagged(PlayerGameInstanceComponent.Tag);
		if (playerGameInstanceComponent.addGameInstanceIdIfAbsent(getGameInstanceRef().getId(), this.getId())) {
			playerList.add(player);
			if (logger.isDebugEnabled()) {
				logger.debug("玩家加入该副本,gameInstanceId" + id + ",playerId:" + player.getId());
			}
		}

	}

	@Override
	public Player getCreater() {
		return creater;
	}

	@Override
	public Collection<Player> getPlayerCollection() {
		return playerList;
	}

	@Override
	public void addKillRecord(String playerId, String monsterRefId, short count) {
		if (Strings.isNullOrEmpty(playerId) || Strings.isNullOrEmpty(monsterRefId) || count <= 0) {
			if (logger.isDebugEnabled()) {
				logger.debug("addKillRecord fail");
			}
			return;
		}

		if (logger.isDebugEnabled()) {
			logger.debug("addKillRecord playerId:" + playerId + "，monsterRefId：" + monsterRefId + "，count：" + count);
		}

		if (killMonsterRecords.containsKey(playerId)) {
			Map<String, Short> records = killMonsterRecords.get(playerId);
			if (records == null) {
				records = new HashMap<String, Short>();
			}
			short record = 0;
			if (records.containsKey(monsterRefId)) {
				record = records.get(monsterRefId);
			}

			records.put(monsterRefId, (short) (record + count));
			killMonsterRecords.put(playerId, records);
		} else {
			Map<String, Short> records = new HashMap<String, Short>();
			records.put(monsterRefId, count);
			killMonsterRecords.put(playerId, records);
		}

		// 检查副本层的任务是否完成
		GameInstanceSceneRefMgr gameInstanceSceneRefMgr = gameInstanceRef.getGameInstanceDataObject(GameInstanceSceneRefMgr.class);
		GameInstanceSceneRef gameInstanceSceneRef = gameInstanceSceneRefMgr.getInstanceSceneList().get(layerId);
		List<LinYueShengModeCondition<Player>> gameInstanceSceneFinishConditionList = gameInstanceSceneRef.getSceneFinishConditionList();
		for (LinYueShengModeCondition<Player> linYueShengModeCondition : gameInstanceSceneFinishConditionList) {
			for (Player player : playerList) {
				RuntimeResult result = linYueShengModeCondition.eligible(player);
				if (result.isOK()) {
					if (logger.isDebugEnabled()) {
						logger.debug("副本层的任务完成");
					}
					// 通知客户端
					G2C_GameInstanceSceneFinish response = MessageFactory.getConcreteMessage(GameInstanceEventDefines.G2C_GameInstanceSceneFinish);
					GameRoot.sendMessage(player.getIdentity(), response);
					MGStatFunctions.gameInstanceStat(player, StatGameInstance.Finish, gameInstanceRef.getId(), gameInstanceSceneRef.getId());
				}
			}
		}
	}

	@Override
	public Map<String, Short> getKillRecord(String playerId) {
		Map<String, Short> records = killMonsterRecords.get(playerId);
		if (records == null) {
			records = new HashMap<String, Short>();
		}
		return records;
	}

	@Override
	public Map<String, ComeFromScene> getComeFromSceneRefId() {
		return playerIdToComeFromSceneRefId;
	}

	public void setGameInstanceRef(GameInstanceRef gameInstanceRef) {
		this.gameInstanceRef = gameInstanceRef;
	}

	public GameArea getGameArea() {
		return MMORPGContext.getGameAreaComponent().getGameArea();
	}

	private Position getDstTransPosition(String dstSceneRefId) {
		AbstractGameSceneRef dstRef = (AbstractGameSceneRef) (GameRoot.getGameRefObjectManager().getManagedObject(dstSceneRefId));
		Preconditions.checkNotNull(dstRef);
		SceneTransInRegion transIn = null;
		List<SceneTransInRegion> transInRegionLst = dstRef.getTransInRegions();
		if (transInRegionLst != null && transInRegionLst.size() > 0) {
			transIn = transInRegionLst.get(0);
		}
		int x = 100, y = 100;
		if (transIn != null) {
			SceneGrid sceneGrid = transIn.getRegion().getRandomUnblockedGrid();
			if (sceneGrid != null) {
				x = sceneGrid.getColumn();
				y = sceneGrid.getRow();
			}
		}

		return new Position(x, y);
	}

	private boolean checkCreate() {
		if (GameInstanceState.Created_State == state) {
			return true;
		}
		return false;
	}

	private boolean checkOpen() {
		if (GameInstanceState.Opened_State == state) {
			return true;
		}
		return false;
	}
	
	private boolean checkClose() {
		if (GameInstanceState.Closed_State != this.state) {
			return true;
		}
		return false;
	}

	private boolean isOpen() {
		if (GameInstanceState.Opened_State == state) {
			return true;
		}
		return false;
	}

	private boolean isClose() {
		if (GameInstanceState.Closed_State == state) {
			return true;
		}
		return false;
	}
	
	@Override
	public void setState(byte state) {
		this.state = state;
	}
	private void savePlayerComeFromSceneAndPos(Player player, String sceneRefId, int x, int y) {
//		ComeFromScene comeFromScene = new ComeFromScene(sceneRefId, x, y);
//		this.playerIdToComeFromSceneRefId.put(player.getId(), comeFromScene);
	}

	private RuntimeResult checkPlayerCanEnterGameInstance() {
		RuntimeResult result = RuntimeResult.OK();
		for (Player player : playerList) {
			result = checkPlayerCanEnterGameInstance(player);
			if (!result.isOK()) {
				return result;
			}
		}
		return result;
	}

	private RuntimeResult checkPlayerCanEnterGameInstance(Player player) {
		RuntimeResult result = RuntimeResult.OK();
		MGGameInstanceConditionMgr gameInstanceConditionMgr = gameInstanceRef.getGameInstanceDataObject(MGGameInstanceConditionMgr.class);
		List<LinYueShengModeCondition<Player>> gameInstanceEnterConditionList = gameInstanceConditionMgr.getGameInstanceEnterConditionList(gameInstanceRef.getId());
		for (LinYueShengModeCondition<Player> linYueShengModeCondition : gameInstanceEnterConditionList) {
			result = linYueShengModeCondition.eligible(player);
			if (!result.isOK()) {
				if (logger.isDebugEnabled()) {
					logger.debug("玩家加入该副本失败,条件不满足,gameInstanceId" + id + ",playerId:" + player.getId() + ", 副本当前状态:" + state);
				}
				return result;
			}

		}
		return result;
	}

	private void playerEnterGameInstance(Player player, String gameInstanceRefId, GameInstanceSceneRef gameInstanceSceneRef, String dstSceneRefId) {
		MGStatFunctions.gameInstanceStat(player, StatGameInstance.Enter, gameInstanceRefId, dstSceneRefId);
		// 记录玩家场景进入次数
		getGameInstanceMgr().getScheduleManager().addInstanceRecord(player, gameInstanceRefId);
		// 发送玩家进入副本事件（创建副本任务）
		PlayerGameInstanceComponent playerGameInstanceComponent = (PlayerGameInstanceComponent) player.getTagged(PlayerGameInstanceComponent.Tag);
		playerGameInstanceComponent.createQuest(gameInstanceRefId, gameInstanceSceneRef.getId());
		// 把玩家传送进副本场景
		playerGameInstanceComponent.setCrtGameInstanceId(this.id);
		if (logger.isDebugEnabled()) {
			logger.debug("传送玩家进入副本,player=" + player + ",dstSceneRefId:" + dstSceneRefId);
		}
	}

	private void sendAllPlayerEnterScene(String gameInstanceRefId, GameInstanceSceneRef gameInstanceSceneRef, Position dstTransPosition, GameScene dstGameScene) {

		String dstSceneRefId = dstGameScene.getRef().getId();
		int dstX = dstTransPosition.getX();
		int dstY = dstTransPosition.getY();

		for (Player player : this.playerList) {

			if (StringUtils.equals(player.getId(), creater.getId())) {
				continue;
			}

			// 记录玩家当前的场景与坐标
			String sceneRefId = player.getCrtScene().getRef().getId();
			int x = player.getCrtPosition().getX();
			int y = player.getCrtPosition().getY();

			RuntimeResult switchTo = creater.getPlayerSceneComponent().switchTo(dstGameScene, dstX, dstY);
			if (switchTo.isError()) {
				logger.error("sendAllPlayerEnterScene switchTo error, " + player);
				continue;
			}

			// 记录进入副本时的场景和坐标
			this.savePlayerComeFromSceneAndPos(player, sceneRefId, x, y);

			playerEnterGameInstance(player, gameInstanceRefId, gameInstanceSceneRef, dstSceneRefId);
		}
	}

	private GameInstanceMgr getGameInstanceMgr() {
		GameInstanceMgr gameInstanceMgr = MorningGloryContext.getGameInstanceSystemComponent().getGameInstanceMgr();
		return gameInstanceMgr;
	}

	public GameInstanceSceneRef getGameInstanceSceneRef(int layer) {
		GameInstanceSceneRefMgr gameInstanceSceneRefMgr = gameInstanceRef.getGameInstanceDataObject(GameInstanceSceneRefMgr.class);
		GameInstanceSceneRef gameInstanceSceneRef = gameInstanceSceneRefMgr.getInstanceSceneList().get(layer);
		return gameInstanceSceneRef;
	}

	/**
	 * 进入下一层之前清理资源
	 */
	private void clearResourceForNextLayer() {
		killMonsterRecords.clear();
	}

	private RuntimeResult consumptionItems(Map<String, Integer> consumptionItems, Player player) {
		for (Entry<String, Integer> consumptionItem : consumptionItems.entrySet()) {
			boolean remove = ItemFacade.removeItem(player, consumptionItem.getKey(), consumptionItem.getValue(), true, ItemOptSource.GameInstance);
			if (remove) {
				return RuntimeResult.OK();
			}
		}

		return RuntimeResult.ParameterError();
	}

	@Override
	public String toString() {
		return "MGGameInstance [id=" + id + ", gameInstanceRef=" + gameInstanceRef + ", creater=" + creater + "]";
	}
}
