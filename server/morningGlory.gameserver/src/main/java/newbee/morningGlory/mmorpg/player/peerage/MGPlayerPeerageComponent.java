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
package newbee.morningGlory.mmorpg.player.peerage;

import java.text.SimpleDateFormat;

import newbee.morningGlory.code.MGErrorCode;
import newbee.morningGlory.mmorpg.player.peerage.actionEvent.C2G_CanReward;
import newbee.morningGlory.mmorpg.player.peerage.actionEvent.C2G_GetSalaryEvent;
import newbee.morningGlory.mmorpg.player.peerage.actionEvent.C2G_UpGradeEvent;
import newbee.morningGlory.mmorpg.player.peerage.actionEvent.G2C_CanReward;
import newbee.morningGlory.mmorpg.player.peerage.actionEvent.G2C_GetSalaryEvent;
import newbee.morningGlory.mmorpg.player.peerage.actionEvent.G2C_UpGradeEvent;
import newbee.morningGlory.mmorpg.player.peerage.actionEvent.PeerageEventDefines;
import newbee.morningGlory.mmorpg.player.peerage.gameEvent.MGPeerageLevelUp_GE;
import newbee.morningGlory.mmorpg.player.peerage.persistence.PeeragePersistenceObject;
import newbee.morningGlory.stat.MGStatFunctions;
import newbee.morningGlory.stat.logs.MeritPointStat;

import org.apache.log4j.Logger;

import sophia.foundation.communication.core.ActionEventBase;
import sophia.foundation.communication.core.MessageFactory;
import sophia.foundation.property.PropertyDictionary;
import sophia.game.GameRoot;
import sophia.game.component.communication.GameEvent;
import sophia.mmorpg.MMORPGContext;
import sophia.mmorpg.base.ConcreteComponent;
import sophia.mmorpg.code.MMORPGErrorCode;
import sophia.mmorpg.core.timer.SFTimeChimeListener;
import sophia.mmorpg.core.timer.SFTimeUnit;
import sophia.mmorpg.core.timer.SFTimer;
import sophia.mmorpg.core.timer.SFTimerCreater;
import sophia.mmorpg.event.ResultEvent;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.exp.gameevent.PlayerLevelUp_GE;
import sophia.mmorpg.player.gameEvent.ChineseModeQuest_GE;
import sophia.mmorpg.player.itemBag.ItemFacade;
import sophia.mmorpg.player.itemBag.ItemOptSource;
import sophia.mmorpg.player.quest.ref.order.QuestChineseOrderDefines;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;

public final class MGPlayerPeerageComponent extends ConcreteComponent<Player> {
	private static Logger logger = Logger.getLogger(MGPlayerPeerageComponent.class);

	public static final String KNIGHT_1 = "knight_1";

	private String firstKnightRefId = KNIGHT_1;

	public MGPeerageRefMgr peerageRefMgr = new MGPeerageRefMgr();

	private PeeragePersistenceObject peeragePersistenceObject;

	private String PlayerLevelUp_GE_ID = PlayerLevelUp_GE.class.getSimpleName();

	private static final String MGPeerageLevelUp_GE_ID = MGPeerageLevelUp_GE.class.getSimpleName();
	public static final String ChineseModeQuest_GE_Id = ChineseModeQuest_GE.class.getSimpleName();

	public static final String Tag = "MGPlayerPeerageComponent";

	private MeritManager meritManager = new MeritManager();

	private MGPeerageEffectMgr peerageEffectMgr;
	
	private Player player = null;
	
	private SFTimer timer;

	public MGPlayerPeerageComponent() {

	}

	@Override
	public void ready() {
		player = getConcreteParent();
		peerageRefMgr.setPlayer(player);
		
		addInterGameEventListener(ChineseModeQuest_GE_Id);
		addInterGameEventListener(PlayerLevelUp_GE_ID);
		
		addActionEventListener(PeerageEventDefines.C2G_GetSalaryEvent);
		addActionEventListener(PeerageEventDefines.C2G_UpGradeEvent);
		addActionEventListener(PeerageEventDefines.C2G_CanReward);
		
		SFTimerCreater timerCreater = MMORPGContext.getTimerCreater();
		timer = timerCreater.calendarChime(new SFTimeChimeListener() {

			@Override
			public void handleServiceShutdown() {
			}

			@Override
			public void handleTimeChimeCancel() {
			}

			@Override
			public void handleTimeChime() {
				dayResetTimer();
			}

		}, SFTimeUnit.HOUR, 0);
	}

	@Override
	public void suspend() {
		removeInterGameEventListener(ChineseModeQuest_GE_Id);
		removeInterGameEventListener(PlayerLevelUp_GE_ID);
		
		removeActionEventListener(PeerageEventDefines.C2G_GetSalaryEvent);
		removeActionEventListener(PeerageEventDefines.C2G_UpGradeEvent);
		removeActionEventListener(PeerageEventDefines.C2G_CanReward);
		if (null != timer) {
			timer.cancel();
		}
	}

	public void dayResetTimer() {// 过了24:00
		G2C_CanReward res = (G2C_CanReward) MessageFactory.getConcreteMessage(PeerageEventDefines.G2C_CanReward);
		res.setCanGet((byte) 1);
		GameRoot.sendMessage(getConcreteParent().getIdentity(), res);
	}

	@Override
	public void handleGameEvent(GameEvent<?> event) {
		if (event.isId(PlayerLevelUp_GE_ID)) {
			PlayerLevelUp_GE playerLevelUp_GE = (PlayerLevelUp_GE) event.getData();
			int level = playerLevelUp_GE.getCurLevel();
			MGPeerageRef peerageRef = peerageRefMgr.getPeerageRef(firstKnightRefId);

			if (level >= peerageRef.getRoleGrade()) {
				byte knight = MGPropertyAccesser.getKnight(getConcreteParent().getProperty());
				if (knight <= 0) {
					peerageRefMgr.setCrtPeerageRef(peerageRef);
					sendGameEventMessage();
					saveAndNotifyKnightLevel(peerageRef.getCrtKnightLevel(), getConcreteParent());
					// 附加爵位1效果
					peerageEffectMgr.attach(peerageRef);
				}
			}
		}
		if (event.isId(ChineseModeQuest_GE_Id)) {
			ChineseModeQuest_GE chineseModeQuest_GE = (ChineseModeQuest_GE) event.getData();
			if (chineseModeQuest_GE.getType() == ChineseModeQuest_GE.AcceptType && chineseModeQuest_GE.getOrderEventId() == QuestChineseOrderDefines.PeerageLevelUp) {
				sendChineseModeGameEventMessage();
			}
		}
	}

	public void sendGameEventMessage() {
		sendPeerageLevelUpGameEventMessage();
		sendChineseModeGameEventMessage();
	}

	public void sendPeerageLevelUpGameEventMessage() {
		MGPeerageRef crtPeerageRef = peerageRefMgr.getCrtPeerageRef();
		if (crtPeerageRef != null) {
			MGPeerageLevelUp_GE mgPeerageLevelUp_GE = new MGPeerageLevelUp_GE(crtPeerageRef.getCrtKnightLevel(), crtPeerageRef.getId());
			GameEvent<MGPeerageLevelUp_GE> ge = (GameEvent<MGPeerageLevelUp_GE>) GameEvent.getInstance(MGPeerageLevelUp_GE_ID, mgPeerageLevelUp_GE);
			sendGameEvent(ge, getConcreteParent().getId());
			if (logger.isDebugEnabled()) {
				logger.debug("通知爵位升级:" + crtPeerageRef.getCrtKnightLevel());
			}
		}
	}

	public void sendChineseModeGameEventMessage() {
		MGPeerageRef crtPeerageRef = peerageRefMgr.getCrtPeerageRef();
		if (crtPeerageRef != null) {
			ChineseModeQuest_GE chineseModeQuest_GE = new ChineseModeQuest_GE();
			chineseModeQuest_GE.setType(ChineseModeQuest_GE.CourseType);
			chineseModeQuest_GE.setOrderEventId(QuestChineseOrderDefines.PeerageLevelUp);
			chineseModeQuest_GE.setNumber(crtPeerageRef.getCrtKnightLevel());
			GameEvent<ChineseModeQuest_GE> event = GameEvent.getInstance(ChineseModeQuest_GE_Id, chineseModeQuest_GE);
			sendGameEvent(event, getConcreteParent().getId());
		}
	}

	private void handle_Peerage_UpGradeEvent(C2G_UpGradeEvent event) {
		if (logger.isDebugEnabled()) {
			logger.debug("===============升级爵位===============");
		}

		MGPeerageRef peerageRef = peerageRefMgr.getCrtPeerageRef();
		if (peerageRef == null) {
			if (logger.isDebugEnabled()) {
				logger.debug("当前玩家没有爵位等级");
			}
			return;
		}

		MGPeerageRef nextPeerageRef = peerageRef.getNextPeerageRef();
		// 当前爵位等级已经达到最高级
		if (null == nextPeerageRef) {
			ResultEvent.sendResult(event.getIdentity(), event.getActionEventId(), MGErrorCode.CODE_PEERAGE_ALREADYHIGHEST);
			if (logger.isDebugEnabled()) {
				logger.debug("玩家当前爵位等级已经达到最高");
			}
			return;
		}

		// 玩家当前等级和功勋值
		int playerLevel = getConcreteParent().getExpComponent().getLevel();
		int playerMerit = meritManager.getMerit();
		// 下一个爵位所需等级和功勋
		int roleLevel = peerageRef.getLevelCondition();
		int merit = peerageRef.getMeritCondition();

		if (playerLevel < roleLevel) {
			ResultEvent.sendResult(event.getIdentity(), event.getActionEventId(), MGErrorCode.CODE_PEERAGE_NOTENOUGHPEERAGELEVEL);
			if (logger.isDebugEnabled()) {
				logger.debug("玩家的等级不够:当前玩家等级=" + playerLevel + "," + "需要等级:" + roleLevel);
			}
			return;
		}

		if (playerMerit < merit) {
			ResultEvent.sendResult(event.getIdentity(), event.getActionEventId(), MGErrorCode.CODE_PEERAGE_NOTENOUGHMERIT);
			if (logger.isDebugEnabled()) {
				logger.debug("玩家的功勋不够:当前玩家功勋=" + playerMerit + "," + "需要等级:" + merit);
			}
			return;
		}

		saveAndNotifyKnightLevel(nextPeerageRef.getCrtKnightLevel(), getConcreteParent());

		// 升级成功之后的处理
		peerageEffectMgr.detachAndSnapshot(peerageRef);
		peerageRefMgr.setCrtPeerageRef(nextPeerageRef);
		sendGameEventMessage();

		meritManager.subMerit(merit);
		MGStatFunctions.meritPointStat(getConcreteParent(), MeritPointStat.Delete, MeritPointStat.Knight_LevelUp, merit);
		peerageEffectMgr.attach(nextPeerageRef);

		G2C_UpGradeEvent res = MessageFactory.getConcreteMessage(PeerageEventDefines.G2C_UpGradeEvent);
		GameRoot.sendMessage(event.getIdentity(), res);

		MGStatFunctions.getOrUpgradeKnightStat(getConcreteParent(), peerageRef);

		if (logger.isDebugEnabled()) {
			logger.debug("===============return successed!===============");
		}

	}

	private void handle_Peerage_GetSalaryEvent(C2G_GetSalaryEvent event) {
		if (logger.isDebugEnabled()) {
			logger.debug("===============领取俸禄===============");
		}
		MGPeerageRef crtPeerageRef = peerageRefMgr.getCrtPeerageRef();
		if (crtPeerageRef == null) {
			ResultEvent.sendResult(event.getIdentity(), event.getActionEventId(), MGErrorCode.CODE_PEERAGE_HAVENOPEERAGE);
			if (logger.isDebugEnabled()) {
				logger.debug("没有爵位不能领取俸禄");
			}
			return;
		}

		String now = timeFormatter(System.currentTimeMillis());
		byte success = -1;
		if (now.equals(peerageRefMgr.getDateTime())) {
			if (logger.isDebugEnabled()) {
				logger.debug("今天已经领取过了奖励");
			}
			success = 0;
		} else {
			if (ItemFacade.addItemCompareSlot(getConcreteParent(), crtPeerageRef.getItemPairs(), ItemOptSource.Peerage).isOK()) {
				peerageRefMgr.setDateTime(now);
				success = 1;
			} else {
				success = 0;
				ResultEvent.sendResult(event.getIdentity(), event.getActionEventId(), MMORPGErrorCode.CODE_ITEM_FULL);
			}
		}

		G2C_GetSalaryEvent res = MessageFactory.getConcreteMessage(PeerageEventDefines.G2C_GetSalaryEvent);
		res.setSuccess(success);
		GameRoot.sendMessage(event.getIdentity(), res);
		logger.debug("===============return successed!===============");
	}

	private void handle_Peerage_CanReward(C2G_CanReward event) {
		if (logger.isDebugEnabled()) {
			logger.debug("===============是否可以俸禄===============");
		}

		byte canGet = 0;
		String loginTime = timeFormatter(System.currentTimeMillis());
		if (logger.isDebugEnabled()) {
			logger.debug(loginTime + "," + peerageRefMgr.getDateTime());
		}
		if (!loginTime.equals(peerageRefMgr.getDateTime())) {
			canGet = 1;
		}
		G2C_CanReward res = (G2C_CanReward) MessageFactory.getConcreteMessage(PeerageEventDefines.G2C_CanReward);
		res.setCanGet(canGet);
		GameRoot.sendMessage(event.getIdentity(), res);
		if (logger.isDebugEnabled()) {
			logger.debug("===============return successed!===============");
		}
	}

	@Override
	public void handleActionEvent(ActionEventBase event) {
		short eventId = event.getActionEventId();
		switch (eventId) {
		case PeerageEventDefines.C2G_UpGradeEvent:
			handle_Peerage_UpGradeEvent((C2G_UpGradeEvent) event);
			break;
		case PeerageEventDefines.C2G_CanReward:
			handle_Peerage_CanReward((C2G_CanReward) event);
			break;
		case PeerageEventDefines.C2G_GetSalaryEvent:
			handle_Peerage_GetSalaryEvent((C2G_GetSalaryEvent) event);
			break;
		}

	}

	public void saveAndNotifyKnightLevel(byte crtLevel, Player player) {
		MGPropertyAccesser.setOrPutKnight(player.getProperty(), crtLevel);
		PropertyDictionary pd = new PropertyDictionary();
		MGPropertyAccesser.setOrPutKnight(pd, crtLevel);
		player.getAoiComponent().broadcastProperty(pd);
		player.notifyPorperty(pd);
	}

	public final String timeFormatter(long time) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		return sdf.format(time);
	}

	public MGPeerageRefMgr getPeeragerefMgr() {
		peerageRefMgr.setPlayer(getConcreteParent());
		return peerageRefMgr;
	}

	public PeeragePersistenceObject getPeeragePersistenceObject() {
		return peeragePersistenceObject;
	}

	public void setPeeragePersistenceObject(PeeragePersistenceObject peeragePersistenceObject) {
		this.peeragePersistenceObject = peeragePersistenceObject;
	}

	public MeritManager getMeritManager() {
		return meritManager;
	}

	public void setMeritManager(MeritManager meritManager) {
		this.meritManager = meritManager;
	}

	public MGPeerageEffectMgr getPeerageEffectMgr() {
		return peerageEffectMgr;
	}

	public void setPeerageEffectMgr(MGPeerageEffectMgr peerageEffectMgr) {
		this.peerageEffectMgr = peerageEffectMgr;
	}

}
