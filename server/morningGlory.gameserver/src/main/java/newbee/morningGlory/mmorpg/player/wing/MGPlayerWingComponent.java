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
package newbee.morningGlory.mmorpg.player.wing;

import newbee.morningGlory.mmorpg.player.sectionQuest.event.SectionQuestActionEventDefines;
import newbee.morningGlory.mmorpg.player.sectionQuest.event.wing.G2C_Wing_GetWingQuestReward;
import newbee.morningGlory.mmorpg.player.wing.actionEvent.MGWingLevelUp_GE;
import newbee.morningGlory.mmorpg.player.wing.event.C2G_Wing_RequestNowWing;
import newbee.morningGlory.mmorpg.player.wing.event.C2G_Wing_WingLevelUp;
import newbee.morningGlory.mmorpg.player.wing.event.G2C_Wing_RequestNowWing;
import newbee.morningGlory.mmorpg.player.wing.event.G2C_Wing_WingLevelUp;
import newbee.morningGlory.mmorpg.player.wing.event.WingEventDefines;
import newbee.morningGlory.mmorpg.player.wing.wingModule.WingManager;
import newbee.morningGlory.stat.MGStatFunctions;
import newbee.morningGlory.stat.logs.StatWing;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import sophia.foundation.authentication.Identity;
import sophia.foundation.communication.core.ActionEventBase;
import sophia.foundation.communication.core.MessageFactory;
import sophia.foundation.data.PersistenceObject;
import sophia.game.GameRoot;
import sophia.game.component.communication.GameEvent;
import sophia.mmorpg.base.ConcreteComponent;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.chat.sysytem.SystemPromptFacade;
import sophia.mmorpg.player.exp.gameevent.PlayerLevelUp_GE;
import sophia.mmorpg.player.gameEvent.ChineseModeQuest_GE;
import sophia.mmorpg.player.itemBag.ItemOptSource;
import sophia.mmorpg.player.quest.ref.order.QuestChineseOrderDefines;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;
import sophia.mmorpg.utils.RuntimeResult;

import com.google.common.base.Strings;

public final class MGPlayerWingComponent extends ConcreteComponent<Player> {
	private static Logger logger = Logger.getLogger(MGPlayerWingComponent.class);

	private WingManager wingManager = null;

	private MGPlayerWing playerWing = null;

	public static final String Tag = "MGPlayerWingComponent";

	public static final String MGWingLevelUp_GE_ID = MGWingLevelUp_GE.class.getSimpleName();

	private static final String PlayerLevelUp_GE_ID = PlayerLevelUp_GE.class.getSimpleName();

	public static final String ChineseModeQuest_GE_Id = ChineseModeQuest_GE.class.getSimpleName();

	private PersistenceObject persisteneceObject;

	private static final int WingSystemOpenLevel = 50;

	private static final String first_Wing_WingRefId = "wing_1_0";

	private static final String Wing = "wing";

	private MGWingEffectMgr wingEffectMgr;

	public MGPlayerWingComponent() {
	}

	public MGPlayerWing getPlayerWing() {
		return playerWing;
	}

	public void setPersisteneceObject(PersistenceObject persisteneceObject) {
		this.persisteneceObject = persisteneceObject;
	}

	public PersistenceObject getPersisteneceObject() {
		return persisteneceObject;
	}

	@Override
	public void ready() {
		// wingManager.setPlayer(getConcreteParent());
		addInterGameEventListener(PlayerLevelUp_GE_ID);
		addInterGameEventListener(ChineseModeQuest_GE_Id);
		addActionEventListener(WingEventDefines.C2G_Wing_RequestNowWing);
		addActionEventListener(WingEventDefines.C2G_Wing_WingLevelUp);
		super.ready();
	}

	@Override
	public void suspend() {
		removeInterGameEventListener(PlayerLevelUp_GE_ID);
		removeInterGameEventListener(ChineseModeQuest_GE_Id);
		removeActionEventListener(WingEventDefines.C2G_Wing_RequestNowWing);
		removeActionEventListener(WingEventDefines.C2G_Wing_WingLevelUp);
		super.suspend();
	}

	@Override
	public void handleGameEvent(GameEvent<?> event) {
		if (event.isId(ChineseModeQuest_GE_Id)) {
			ChineseModeQuest_GE chineseModeQuest_GE = (ChineseModeQuest_GE) event.getData();
			if (chineseModeQuest_GE.getType() == ChineseModeQuest_GE.AcceptType && chineseModeQuest_GE.getOrderEventId() == QuestChineseOrderDefines.WingLevelUp) {
				sendChineseModeGameEventMessage();
			}
		} else if (event.isId(PlayerLevelUp_GE_ID)) {
			PlayerLevelUp_GE playerLevelUp_GE = (PlayerLevelUp_GE) event.getData();
			int level = playerLevelUp_GE.getCurLevel();

			if (level >= WingSystemOpenLevel && playerWing.getPlayerWingRef() == null) {
				MGPlayerWingRef wingRef = (MGPlayerWingRef) GameRoot.getGameRefObjectManager().getManagedObject(first_Wing_WingRefId);
				playerWing.setPlayerWingRef(wingRef);
				MGWingEffectMgr wingEffectMgr = new MGWingEffectMgr(getConcreteParent());
				wingEffectMgr.attach(playerWing);
				playerWing.broadcastWingModelProperty(getConcreteParent());

				G2C_Wing_GetWingQuestReward questReward = MessageFactory.getConcreteMessage(SectionQuestActionEventDefines.G2C_Wing_GetWingQuestReward);
				questReward.setResult(1);
				GameRoot.sendMessage(getConcreteParent().getIdentity(), questReward);
				sendGameEventMessage(StatWing.Add);
				sendChineseModeGameEventMessage();
			}
		}
	}

	@Override
	public void handleActionEvent(ActionEventBase event) {
		short actionEventId = event.getActionEventId();
		Identity identity = event.getIdentity();
		switch (actionEventId) {
		case WingEventDefines.C2G_Wing_RequestNowWing:
			handle_Wing_RequestNowWing((C2G_Wing_RequestNowWing) event, actionEventId, identity);
			break;
		case WingEventDefines.C2G_Wing_WingLevelUp:
			handle_Wing_WingLevelUp((C2G_Wing_WingLevelUp) event, actionEventId, identity);
			break;
		}
		super.handleActionEvent(event);
	}

	private void handle_Wing_WingLevelUp(C2G_Wing_WingLevelUp event, short actionEventId, Identity identity) {
		G2C_Wing_WingLevelUp res = MessageFactory.getConcreteMessage(WingEventDefines.G2C_Wing_WingLevelUp);
		String itemRefId = event.getItemRefId();
		int num = event.getItemNum();

		logger.debug("wing level up event!");

		MGPlayerWingRef playerWingRef = playerWing.getPlayerWingRef();
		if (playerWingRef == null) {
			logger.error("crt wing not exist!");
			return;
		}

		if (Strings.isNullOrEmpty(itemRefId) || !StringUtils.equals(itemRefId, WingManager.WingLevelUpItemRefId)) {
			logger.error("wrong wing levelup item! itemRefId = " + itemRefId);
			return;
		}

		if (num <= 0 || num > 100000) {
			logger.error("valid num! num = " + num);
			return;
		}
		byte oldWingStateLevel = playerWingRef.getCrtWingStageLevel();
		Player player = getConcreteParent();
		RuntimeResult result = player.getItemBagComponent().useItem(itemRefId, num, ItemOptSource.Wing);

		int errorCode = result.getCode();
		if (errorCode != RuntimeResult.OKResult) {
			if (logger.isDebugEnabled()) {
				logger.debug("eat wrong!! errorCode:" + errorCode);
			}
			return;
		}
		// 暴击的倍率
		String detail = result.getDetails();
		byte critMultipleType = 1;

		if (detail != null && !Strings.isNullOrEmpty(detail.trim())) {
			try {
				critMultipleType = Byte.valueOf(detail);
			} catch (Exception e) {
				logger.error("", e);
				critMultipleType = 1;
			}
		}

		String crtWingRefId = playerWing.getPlayerWingRef().getId();
		if (logger.isDebugEnabled()) {
			logger.debug("wing level up critMultipleType = " + critMultipleType);
			logger.debug("after wing level up. crtWingRefId = " + crtWingRefId);
		}
		byte crtWingStageLevel = playerWing.getPlayerWingRef().getCrtWingStageLevel();
		String wingName = MGPropertyAccesser.getName(playerWing.getPlayerWingRef().getProperty());
		if (crtWingStageLevel > 5 && crtWingStageLevel > oldWingStateLevel) {
			SystemPromptFacade.broadLevelUpWing(player.getName(), player.getId(), crtWingStageLevel + "", wingName, Wing);
		}
		long exp = playerWing.getExp();
		res.setCritMultipleType(critMultipleType);
		res.setCrtWingRefId(crtWingRefId);
		res.setExp(exp);
		GameRoot.sendMessage(event.getIdentity(), res);
		sendGameEventMessage(StatWing.LevelUp);
		sendChineseModeGameEventMessage();
	}

	/**
	 * 获取当前翅膀 （每次登陆请求一次，如果有翅膀，装备上）
	 * 
	 * @param event
	 * @param actionEventId
	 * @param identity
	 */
	private void handle_Wing_RequestNowWing(C2G_Wing_RequestNowWing event, short actionEventId, Identity identity) {

		MGPlayerWingRef playerWingRef = playerWing.getPlayerWingRef();
		if (playerWingRef == null) {
			logger.debug("have no wing!");
			return;
		}

		// long crtExp = 0;
		// MGPlayerWingRef prePlayerWingRef =
		// playerWingRef.getPrePlayerWingRef();
		// if (prePlayerWingRef == null) {
		// crtExp = playerWing.getExp();
		// } else {
		// long preMaxExp =
		// MGPropertyAccesser.getMaxExp(prePlayerWingRef.getProperty());
		// crtExp = totalExp - preMaxExp;
		// }
		// crtExp = crtExp < 0? 0: crtExp;

		long crtExp = playerWing.getExp();
		G2C_Wing_RequestNowWing res = MessageFactory.getConcreteMessage(WingEventDefines.G2C_Wing_RequestNowWing);
		res.setWingRefId(playerWingRef.getId());
		res.setCrtExp(crtExp);
		GameRoot.sendMessage(identity, res);
	}

	public void sendGameEventMessage(byte type) {
		String wingRefId = playerWing.getPlayerWingRef().getId();
		long exp = playerWing.getExp();
		MGWingLevelUp_GE mgWingLevelUp_GE = new MGWingLevelUp_GE(wingRefId);
		GameEvent<MGWingLevelUp_GE> ge = (GameEvent<MGWingLevelUp_GE>) GameEvent.getInstance(MGWingLevelUp_GE_ID, mgWingLevelUp_GE);
		sendGameEvent(ge, getConcreteParent().getId());
		MGStatFunctions.wingStat(getConcreteParent(), type, wingRefId, exp);
	}

	public void sendChineseModeGameEventMessage() {
		if (playerWing.getPlayerWingRef() != null) {
			ChineseModeQuest_GE chineseModeQuest_GE = new ChineseModeQuest_GE();
			chineseModeQuest_GE.setType(ChineseModeQuest_GE.CourseType);
			chineseModeQuest_GE.setOrderEventId(QuestChineseOrderDefines.WingLevelUp);
			int crtStageLevel = playerWing.getCrtStageLevel();
			int crtStarLevel = playerWing.getCrtStarLevel();

			// int wingLevel =
			// MGPropertyAccesser.getWingLevel(playerWing.getPlayerWingRef().getProperty());
			chineseModeQuest_GE.setNumber(crtStageLevel);
			chineseModeQuest_GE.setCount(crtStarLevel);
			GameEvent<ChineseModeQuest_GE> event = GameEvent.getInstance(ChineseModeQuest_GE_Id, chineseModeQuest_GE);
			sendGameEvent(event, getConcreteParent().getId());
		}
	}

	public MGWingEffectMgr getWingEffectMgr() {
		return wingEffectMgr;
	}

	public void setWingEffectMgr(MGWingEffectMgr wingEffectMgr) {
		this.wingEffectMgr = wingEffectMgr;
	}

	public WingManager getWingManager() {
		return wingManager;
	}

	public void setWingManager(WingManager wingManager) {
		this.wingManager = wingManager;
	}

	public void setPlayerWing(MGPlayerWing playerWing) {
		this.playerWing = playerWing;
	}

}
