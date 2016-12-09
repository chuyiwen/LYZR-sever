package newbee.morningGlory.mmorpg.sceneActivities.payonPalace;

import newbee.morningGlory.code.MGErrorCode;
import newbee.morningGlory.mmorpg.sceneActivities.SceneActivityMgr;
import newbee.morningGlory.mmorpg.sceneActivities.SceneActivityType;
import newbee.morningGlory.mmorpg.sceneActivities.event.C2G_PayonPalace_EnterMap;
import newbee.morningGlory.mmorpg.sceneActivities.event.C2G_PayonPalace_LeaveMap;
import newbee.morningGlory.mmorpg.sceneActivities.event.G2C_PayonPalace_EnterMap;
import newbee.morningGlory.mmorpg.sceneActivities.event.G2C_PayonPalace_LeaveMap;
import newbee.morningGlory.mmorpg.sceneActivities.event.SceneActivityEventDefines;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import sophia.foundation.authentication.Identity;
import sophia.foundation.communication.core.ActionEventBase;
import sophia.foundation.communication.core.MessageFactory;
import sophia.game.GameRoot;
import sophia.mmorpg.MMORPGContext;
import sophia.mmorpg.base.ConcreteComponent;
import sophia.mmorpg.base.scene.GameScene;
import sophia.mmorpg.base.scene.grid.SceneGrid;
import sophia.mmorpg.base.scene.ref.region.SceneTransInRegion;
import sophia.mmorpg.event.ResultEvent;
import sophia.mmorpg.gameArea.GameArea;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.utils.RuntimeResult;

public class MGPayonPalaceActivityComponent extends ConcreteComponent<Player> {
	private static Logger logger = Logger.getLogger(MGPayonPalaceActivityComponent.class);
	public static final String Tag = "MGPayonPalaceActivityComponent";
	private MGPayonPalaceActivity payonPalaceActivity;

	public MGPayonPalaceActivityComponent() {

	}

	@Override
	public void ready() {
		addActionEventListener(SceneActivityEventDefines.C2G_PayonPalace_EnterMap);
		addActionEventListener(SceneActivityEventDefines.C2G_PayonPalace_LeaveMap);
		payonPalaceActivity = (MGPayonPalaceActivity) SceneActivityMgr.getInstance().getSceneAcitityByType(SceneActivityType.get("payonPalace_1"));
		super.ready();
	}

	@Override
	public void suspend() {
		removeActionEventListener(SceneActivityEventDefines.C2G_PayonPalace_EnterMap);
		removeActionEventListener(SceneActivityEventDefines.C2G_PayonPalace_LeaveMap);
		super.suspend();
	}

	@Override
	public void handleActionEvent(ActionEventBase event) {
		short eventId = event.getActionEventId();
		if (logger.isDebugEnabled()) {
			logger.debug("付费地宫Id:" + eventId);
		}
		short actionEventId = event.getActionEventId();
		Identity identity = event.getIdentity();
		switch (eventId) {
		case SceneActivityEventDefines.C2G_PayonPalace_EnterMap:// 进入活动地图
			handle_PayonPalace_EnterMap((C2G_PayonPalace_EnterMap) event, actionEventId, identity);
			break;

		case SceneActivityEventDefines.C2G_PayonPalace_LeaveMap:// 离开
			handle_PayonPalace_LeaveScene((C2G_PayonPalace_LeaveMap) event, actionEventId, identity);
			break;

		}
		
		super.handleActionEvent(event);
	}

	public void handle_PayonPalace_EnterMap(C2G_PayonPalace_EnterMap event, short actionEventId, Identity identity) {

		if (payonPalaceActivity == null) {
			logger.error("payonPalaceActivity is null");
			return;
		}

		String sceneRefId = payonPalaceActivity.getFisrtSceneRefId();// 活动场景
		RuntimeResult result = payonPalaceActivity.checkEnterScene(concreteParent, sceneRefId, event.getSceneRefId());
		if (!result.isOK()) {
			ResultEvent.sendResult(identity, actionEventId, result.getApplicationCode());
			return;
		}

		GameArea gameArea = MMORPGContext.getGameAreaComponent().getGameArea();
		GameScene destScene = gameArea.getSceneById(sceneRefId);
		SceneTransInRegion sceneTransInRegion = destScene.getRef().getTransInRegions().get(0);
		SceneGrid sceneGrid = sceneTransInRegion.getRegion().getRandomUnblockedGrid();

		RuntimeResult switchTo = concreteParent.getPlayerSceneComponent().switchTo(destScene, sceneGrid.getColumn(), sceneGrid.getRow());
		if (switchTo.isError()) {
			logger.error("PayonPalace_EnterMap switchTo error, " + concreteParent);
			return;
		}

		// 扣除物品
		RuntimeResult result1 = payonPalaceActivity.consumptionItems(concreteParent);
		if (!result1.isOK()) {
			if (logger.isDebugEnabled()) {
				logger.debug("进入付费地宫扣除物品失败");
			}
		}

		G2C_PayonPalace_EnterMap enterMap = MessageFactory.getConcreteMessage(SceneActivityEventDefines.G2C_PayonPalace_EnterMap);
		GameRoot.sendMessage(identity, enterMap);

	}

	public void handle_PayonPalace_LeaveScene(C2G_PayonPalace_LeaveMap event, short actionEventId, Identity identity) {
		String firstSceneRefId = payonPalaceActivity.getFisrtSceneRefId();
		String secSceneRefId = payonPalaceActivity.getSecondSceneRefId();

		GameScene crtScene = concreteParent.getCrtScene();

		if (StringUtils.equals(crtScene.getRef().getId(), firstSceneRefId) || StringUtils.equals(crtScene.getRef().getId(), secSceneRefId)) {

			exitPayonPalaceScenc();
			G2C_PayonPalace_LeaveMap leaveMap = MessageFactory.getConcreteMessage(SceneActivityEventDefines.G2C_PayonPalace_LeaveMap);
			GameRoot.sendMessage(identity, leaveMap);
		} else {
			ResultEvent.sendResult(concreteParent.getIdentity(), actionEventId, MGErrorCode.CODE_INVASIOIN_ALREAD_LEAVE);
			return;
		}

	}

	public void payonPalaceEnd() {
		exitPayonPalaceScenc();
	}

	/**
	 * 退出付费地宫地图
	 */
	private void exitPayonPalaceScenc() {
		concreteParent.getPlayerSceneComponent().goBackComeFromSceneOrGoHome();
	}

}
