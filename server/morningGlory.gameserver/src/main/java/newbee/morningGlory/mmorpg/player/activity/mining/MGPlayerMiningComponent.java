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
package newbee.morningGlory.mmorpg.player.activity.mining;

import java.util.Collections;
import java.util.List;

import newbee.morningGlory.code.MGErrorCode;
import newbee.morningGlory.mmorpg.player.activity.mining.event.C2G_Mining_EnterEvent;
import newbee.morningGlory.mmorpg.player.activity.mining.event.C2G_Mining_ExitEvent;
import newbee.morningGlory.mmorpg.player.activity.mining.event.C2G_Mining_Open;
import newbee.morningGlory.mmorpg.player.activity.mining.event.C2G_Mining_RemainRrfreshTime;
import newbee.morningGlory.mmorpg.player.activity.mining.event.C2G_Mining_RemainTime;
import newbee.morningGlory.mmorpg.player.activity.mining.event.G2C_Mining_EnterEvent;
import newbee.morningGlory.mmorpg.player.activity.mining.event.G2C_Mining_ExitEvent;
import newbee.morningGlory.mmorpg.player.activity.mining.event.G2C_Mining_Open;
import newbee.morningGlory.mmorpg.player.activity.mining.event.G2C_Mining_RemainRrfreshTime;
import newbee.morningGlory.mmorpg.player.activity.mining.event.G2C_Mining_RemainTime;
import newbee.morningGlory.mmorpg.player.activity.mining.event.G2C_Mining_Update;
import newbee.morningGlory.mmorpg.player.activity.mining.event.MGMiningEventDefines;
import newbee.morningGlory.mmorpg.sceneActivities.SceneActivity;
import newbee.morningGlory.mmorpg.sceneActivities.SceneActivityMgr;
import newbee.morningGlory.mmorpg.sceneActivities.SceneActivityType;
import newbee.morningGlory.mmorpg.sceneActivities.mining.MGMiningActivity;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import sophia.foundation.communication.core.ActionEventBase;
import sophia.foundation.communication.core.MessageFactory;
import sophia.game.GameRoot;
import sophia.game.component.communication.GameEvent;
import sophia.mmorpg.MMORPGContext;
import sophia.mmorpg.base.ConcreteComponent;
import sophia.mmorpg.base.scene.GameScene;
import sophia.mmorpg.base.scene.grid.SceneGrid;
import sophia.mmorpg.base.scene.mgr.PluckMgrComponent;
import sophia.mmorpg.core.timer.SFTimeChimeListener;
import sophia.mmorpg.core.timer.SFTimeUnit;
import sophia.mmorpg.core.timer.SFTimer;
import sophia.mmorpg.event.ResultEvent;
import sophia.mmorpg.gameArea.GameArea;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.gameEvent.EnterWorld_SceneReady_GE;
import sophia.mmorpg.player.gameEvent.PlayerSwitchScene_GE;
import sophia.mmorpg.pluck.Pluck;
import sophia.mmorpg.pluck.gameEvent.PluckRefresh_GE;
import sophia.mmorpg.pluck.gameEvent.PluckSuccess_GE;
import sophia.mmorpg.utils.RuntimeResult;

public class MGPlayerMiningComponent extends ConcreteComponent<Player> {
	private static final Logger logger = Logger.getLogger(MGPlayerMiningComponent.class);

	public static final String Tag = "MGPlayerMiningComponentTag";

	private MGPlayerMiningManager playerMiningManager = null;

	private MGMiningActivity miningActivity;

	private static final String PlayerSwitchScene_GE_Id = PlayerSwitchScene_GE.class.getSimpleName();

	private static final String PluckRefresh_GE_Id = PluckRefresh_GE.class.getSimpleName();

	private static final String EnterWorld_SceneReady_GE_Id = EnterWorld_SceneReady_GE.class.getSimpleName();

	private Player player;

	private SFTimer sfTimer;

	@Override
	public void ready() {
		sfTimer = MMORPGContext.getTimerCreater().calendarChime(new SFTimeChimeListener() {

			@Override
			public void handleTimeChimeCancel() {

			}

			@Override
			public void handleTimeChime() {

			}

			@Override
			public void handleServiceShutdown() {
				miningReset();
			}
		}, SFTimeUnit.HOUR, 0);
		player = getConcreteParent();
		miningActivity = (MGMiningActivity) SceneActivityMgr.getInstance().getSceneAcitityByType(SceneActivityType.get("sa_1"));
		addActionEventListener(MGMiningEventDefines.C2G_Mining_EnterEvent);
		addActionEventListener(MGMiningEventDefines.C2G_Mining_ExitEvent);
		addActionEventListener(MGMiningEventDefines.C2G_Mining_Open);
		addActionEventListener(MGMiningEventDefines.C2G_Mining_RemainTime);
		addActionEventListener(MGMiningEventDefines.C2G_Mining_RemainRrfreshTime);

		addInterGameEventListener(PluckMgrComponent.PluckSuccess_GE_ID);
		addInterGameEventListener(PlayerSwitchScene_GE_Id);
		addInterGameEventListener(PluckRefresh_GE_Id);
		addInterGameEventListener(EnterWorld_SceneReady_GE_Id);

		super.ready();
	}

	@Override
	public void suspend() {
		removeActionEventListener(MGMiningEventDefines.C2G_Mining_EnterEvent);
		removeActionEventListener(MGMiningEventDefines.C2G_Mining_ExitEvent);
		removeActionEventListener(MGMiningEventDefines.C2G_Mining_Open);
		removeActionEventListener(MGMiningEventDefines.C2G_Mining_RemainTime);
		removeActionEventListener(MGMiningEventDefines.C2G_Mining_RemainRrfreshTime);

		removeInterGameEventListener(PluckMgrComponent.PluckSuccess_GE_ID);
		removeInterGameEventListener(PlayerSwitchScene_GE_Id);
		removeInterGameEventListener(PluckRefresh_GE_Id);
		removeInterGameEventListener(EnterWorld_SceneReady_GE_Id);

		if (sfTimer != null) {
			sfTimer.cancel();
		}
		super.suspend();
	}

	private void miningReset() {
		playerMiningManager.resetCollectCount();
	}

	@Override
	public void handleGameEvent(GameEvent<?> event) {
		if (event.isId(PluckMgrComponent.PluckSuccess_GE_ID)) {
			if (!isPluckMine()) {
				return;
			}

			PluckSuccess_GE ge = (PluckSuccess_GE) event.getData();
			byte pluckType = ge.getPluckType();
			playerMiningManager.collectedCountIncrement(pluckType);
			playerMiningManager.setLastMiningMills(System.currentTimeMillis());

			G2C_Mining_Update res = MessageFactory.getConcreteMessage(MGMiningEventDefines.G2C_Mining_Update);
			byte totalCollectedCount = playerMiningManager.totalCollectedCount();
			res.setTotalCollectedCount(totalCollectedCount);
			res.setCollectedCount(playerMiningManager.getCollectedCount());
			GameRoot.sendMessage(player.getIdentity(), res);

		} else if (event.isId(PlayerSwitchScene_GE_Id)) {
			PlayerSwitchScene_GE ge = (PlayerSwitchScene_GE) event.getData();
			GameScene fromGameScene = ge.getFromScene();
			GameScene desGameScene = ge.getDstScene();
			if (StringUtils.equals(fromGameScene.getRef().getId(), miningActivity.getRef().getSceneRefId())
					&& !StringUtils.equals(desGameScene.getRef().getId(), miningActivity.getRef().getSceneRefId())) {
				G2C_Mining_ExitEvent res = (G2C_Mining_ExitEvent) MessageFactory.getMessage(MGMiningEventDefines.G2C_Mining_ExitEvent);
				GameRoot.sendMessage(player.getIdentity(), res);
			}
		} else if (event.isId(PluckRefresh_GE_Id)) {
			/** 高级矿刷新剩余时间通知 */
			PluckRefresh_GE ge = (PluckRefresh_GE) event.getData();
			byte pluckType = ge.getPluckType();
			if (pluckType == MineType.HighLevel_Mine) {
				logger.debug("pluck gameEvent");
				sendRemainRefreshSeconds(pluckType);
			}
		} else if (event.isId(EnterWorld_SceneReady_GE_Id)) {
			if (StringUtils.equals(player.getCrtScene().getRef().getId(), miningActivity.getRef().getSceneRefId())) {
				sendCanEnterMsg(G2C_Mining_EnterEvent.Open);
			}
		}

		super.handleGameEvent(event);
	}

	@Override
	public void handleActionEvent(ActionEventBase event) {
		short actionEventId = event.getActionEventId();
		switch (actionEventId) {
		case MGMiningEventDefines.C2G_Mining_EnterEvent:
			handleMiningEnter((C2G_Mining_EnterEvent) event);
			break;

		case MGMiningEventDefines.C2G_Mining_ExitEvent:
			handleMiningExit((C2G_Mining_ExitEvent) event);
			break;

		case MGMiningEventDefines.C2G_Mining_Open:
			handleMiningIsOpen((C2G_Mining_Open) event);
			break;

		case MGMiningEventDefines.C2G_Mining_RemainTime:
			handleMiningRemainMills((C2G_Mining_RemainTime) event);
			break;

		case MGMiningEventDefines.C2G_Mining_RemainRrfreshTime:
			handleMiningRefreshRemainMills((C2G_Mining_RemainRrfreshTime) event);
			break;

		default:
			break;
		}
		super.handleActionEvent(event);
	}

	/**
	 * 请求挖矿活动是否开启
	 * 
	 * @param event
	 * @param identity
	 */
	private void handleMiningIsOpen(C2G_Mining_Open event) {
		G2C_Mining_Open res = MessageFactory.getConcreteMessage(MGMiningEventDefines.G2C_Mining_Open);
		byte openState = miningActivity.getCrtActivityState() == SceneActivity.ACTIVITY_START ? G2C_Mining_Open.Open : G2C_Mining_Open.Close;
		res.setOpenState(openState);
		GameRoot.sendMessage(event.getIdentity(), res);
	}

	private void handleMiningRemainMills(C2G_Mining_RemainTime event) {
		G2C_Mining_RemainTime res = miningActivity.getRemainMillsEvent();
		GameRoot.sendMessage(player.getIdentity(), res);
	}

	private void handleMiningRefreshRemainMills(C2G_Mining_RemainRrfreshTime event) {
		byte miningType = event.getMiningType();

		if (miningType != MineType.LowerLevel_Mine && miningType != MineType.MiddleLevel_Mine && miningType != MineType.HighLevel_Mine) {
			if (logger.isDebugEnabled()) {
				logger.debug("illegal argument: miningType = " + miningType);
			}
			return;
		}

		sendRemainRefreshSeconds(miningType);
	}

	private void sendRemainRefreshSeconds(byte miningType) {
		GameScene crtScene = player.getCrtScene();

		if (crtScene == null) {
			logger.debug("crtScene is null");
			return;
		}

		if (!StringUtils.equals(crtScene.getRef().getId(), "S217")) {
			if (logger.isDebugEnabled()) {
				logger.debug("crtScene is invalid: crtScene = " + crtScene);
			}
			return;
		}

		PluckMgrComponent pluckMgrComponent = crtScene.getPluckMgrComponent();
		if (pluckMgrComponent == null) {
			logger.error("pluckMgrComponent is null");
			return;
		}

		List<Pluck> pluckRefreshList = pluckMgrComponent.getPluckRefreshList();
		if (pluckRefreshList == null || pluckRefreshList.isEmpty()) {
			return;
		}

		int remainRefreshSeconds = 0;
		if (!pluckMgrComponent.hasAlivePluck(MineType.HighLevel_Mine) && !pluckRefreshList.isEmpty()) {
			Collections.sort(pluckRefreshList);

			for (Pluck pluck : pluckRefreshList) {
				byte type = pluck.getType();

				if (type != MineType.HighLevel_Mine) {
					continue;
				}

				int pluckRefreshTime = pluck.getPluckRefreshTime();
				remainRefreshSeconds = pluckRefreshTime - (int) ((System.currentTimeMillis() - pluck.getPluckRefreshBeginTime()) / 1000);

				break;
			}
		}

		G2C_Mining_RemainRrfreshTime res = MessageFactory.getConcreteMessage(MGMiningEventDefines.G2C_Mining_RemainRrfreshTime);
		res.setMineType(miningType);
		res.setRemainRefreshSeconds(remainRefreshSeconds);
		GameRoot.sendMessage(player.getIdentity(), res);
		
		if (logger.isDebugEnabled()) {
			logger.debug("miningType = " + miningType + " , remainRefreshSeconds = " + remainRefreshSeconds);
		}
	}

	/**
	 * 请求离开挖矿
	 * 
	 * @param event
	 * @param actionEventId
	 * @param identity
	 */
	private void handleMiningExit(C2G_Mining_ExitEvent event) {
		if (miningActivity == null) {
			return;
		}

		String mingSceneRefId = miningActivity.getRef().getSceneRefId();
		GameScene crtScene = player.getCrtScene();

		if (!StringUtils.equals(crtScene.getRef().getId(), mingSceneRefId)) {
			ResultEvent.sendResult(player.getIdentity(), MGMiningEventDefines.C2G_Mining_EnterEvent, MGErrorCode.CODE_Mining_TransferError);
			return;
		}
		
		exitMiningScenc();
	}

	/**
	 * 请求进入挖矿
	 * 
	 * @param event
	 * @param actionEventId
	 * @param identity
	 */
	private void handleMiningEnter(C2G_Mining_EnterEvent event) {

		if (miningActivity == null) {
			return;
		}

		if (!checkEnter()) {
			return;
		}

		if (!miningActivity.checkEnter(player)) {
			sendCanEnterMsg(G2C_Mining_EnterEvent.Close);
			return;
		}

		String mingSceneRefId = miningActivity.getRef().getSceneRefId();
		GameArea gameArea = MMORPGContext.getGameAreaComponent().getGameArea();
		GameScene destScene = gameArea.getSceneById(mingSceneRefId);
		SceneGrid sceneGrid = destScene.getRef().getRadomSceneTransInRegion().getRegion().getRandomUnblockedGrid();
		if (switchToScene(player, mingSceneRefId, sceneGrid.getColumn(), sceneGrid.getRow()).isError()) {
			return;
		}

		long lastMiningMills = playerMiningManager.getLastMiningMills();
		long now = System.currentTimeMillis();

		if (!miningActivity.isTheSameMiningRound(lastMiningMills, now)) {
			miningReset();
		}

		sendCanEnterMsg(G2C_Mining_EnterEvent.Open);

	}

	private boolean checkEnter() {
		String mingSceneRefId = miningActivity.getRef().getSceneRefId();

		GameScene crtScene = player.getCrtScene();

		if (player.getExpComponent().getLevel() < miningActivity.getMiningLimitLevel()) {
			ResultEvent.sendResult(player.getIdentity(), MGMiningEventDefines.C2G_Mining_EnterEvent, MGErrorCode.CODE_MINING_LVERROR);
			return false;
		}

		if (StringUtils.equals(crtScene.getRef().getId(), mingSceneRefId)) {
			ResultEvent.sendResult(player.getIdentity(), MGMiningEventDefines.C2G_Mining_EnterEvent, MGErrorCode.CODE_Mining_AlreadyInScene);
			return false;
		}

		return true;

	}

	private void sendCanEnterMsg(byte enterType) {
		G2C_Mining_EnterEvent res = (G2C_Mining_EnterEvent) MessageFactory.getMessage(MGMiningEventDefines.G2C_Mining_EnterEvent);
		res.setEnterType(enterType);
		if (enterType == G2C_Mining_EnterEvent.Open) {
			res.setLeaveTime(miningActivity.getLeaveTime());
			res.setCount(playerMiningManager.totalCollectedCount());
			res.setCollectedCount(playerMiningManager.getCollectedCount());
		} else {
			String nextOpenTimeString = miningActivity.getNextMiningTimeString();
			res.setNextTimeString(nextOpenTimeString);
		}
		GameRoot.sendMessage(player.getIdentity(), res);
	}

	/**
	 * 判断是否是采矿
	 * 
	 * @param event
	 * @return
	 */
	private boolean isPluckMine() {
		return StringUtils.equals(player.getCrtScene().getRef().getId(), miningActivity.getRef().getSceneRefId());
	}

	/**
	 * 退出挖矿地图
	 */
	private void exitMiningScenc() {
		player.getPlayerSceneComponent().interruptPluck();
		player.getPlayerSceneComponent().goBackComeFromSceneOrGoHome();
	}

	private RuntimeResult switchToScene(Player player, String sceneId, int x, int y) {
		return player.getPlayerSceneComponent().switchTo(sceneId, x, y);
	}

	public void miningEnd() {
		exitMiningScenc();
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public MGPlayerMiningManager getPlayerMiningManager() {
		return playerMiningManager;
	}

	public void setPlayerMiningManager(MGPlayerMiningManager playerMiningManager) {
		this.playerMiningManager = playerMiningManager;
	}

	public MGMiningActivity getMiningActivity() {
		return miningActivity;
	}

}
