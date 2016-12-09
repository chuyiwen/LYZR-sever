package newbee.morningGlory.mmorpg.sceneActivities.mutilExp;

import newbee.morningGlory.mmorpg.sceneActivities.SceneActivity;
import newbee.morningGlory.mmorpg.sceneActivities.SceneActivityMgr;
import newbee.morningGlory.mmorpg.sceneActivities.SceneActivityType;
import newbee.morningGlory.mmorpg.sceneActivities.event.C2G_MultiTimesExp_RequestTime;
import newbee.morningGlory.mmorpg.sceneActivities.event.G2C_MultiTimesExp_RequestTime;
import newbee.morningGlory.mmorpg.sceneActivities.event.G2C_MultiTimesExp_State;
import newbee.morningGlory.mmorpg.sceneActivities.event.SceneActivityEventDefines;

import org.apache.log4j.Logger;

import sophia.foundation.authentication.Identity;
import sophia.foundation.communication.core.ActionEventBase;
import sophia.foundation.communication.core.MessageFactory;
import sophia.game.GameRoot;
import sophia.game.component.communication.GameEvent;
import sophia.mmorpg.base.ConcreteComponent;
import sophia.mmorpg.monster.Monster;
import sophia.mmorpg.monster.gameEvent.MonsterDead_GE;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.PlayerManager;
import sophia.mmorpg.player.gameEvent.EnterWorld_SceneReady_GE;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;

public class MGMutilExpActivityComponent extends ConcreteComponent<Player> {
	private static Logger logger = Logger.getLogger(MGMutilExpActivityComponent.class);
	private String EnterWorld_SceneReady_GE_Id = EnterWorld_SceneReady_GE.class.getSimpleName();
	public static final String Tag = "MGMutilExpActivityComponent";
	private MGMutilExpActivity mutilExpActivity;

	public MGMutilExpActivityComponent() {

	}

	@Override
	public void ready() {
		addActionEventListener(SceneActivityEventDefines.C2G_MultiTimesExp_RequestTime);
		addInterGameEventListener(Monster.MonsterDead_GE_Id);
		addInterGameEventListener(PlayerManager.LeaveWorld_GE_Id);
		addInterGameEventListener(EnterWorld_SceneReady_GE_Id);
		mutilExpActivity = (MGMutilExpActivity) SceneActivityMgr.getInstance().getSceneAcitityByType(SceneActivityType.get("multiExp"));
		super.ready();
	}

	@Override
	public void suspend() {
		removeActionEventListener(SceneActivityEventDefines.C2G_MultiTimesExp_RequestTime);
		removeInterGameEventListener(Monster.MonsterDead_GE_Id);
		removeInterGameEventListener(PlayerManager.LeaveWorld_GE_Id);
		removeInterGameEventListener(EnterWorld_SceneReady_GE_Id);
		super.suspend();
	}

	@Override
	public void handleGameEvent(GameEvent<?> event) {

		if (event.isId(Monster.MonsterDead_GE_Id)) { // 杀怪GameEvent
			MonsterDead_GE monsterDead_GE = (MonsterDead_GE) event.getData();
			Monster monster = monsterDead_GE.getMonster();
			addMutilExp(concreteParent, monster);
		} else if (event.isId(EnterWorld_SceneReady_GE_Id)) {
			if (mutilExpActivity != null && mutilExpActivity.getCrtActivityState() == SceneActivity.ACTIVITY_START) {
				G2C_MultiTimesExp_State res = MessageFactory.getConcreteMessage(SceneActivityEventDefines.G2C_MultiTimesExp_State);
				res.setState(G2C_MultiTimesExp_State.Open);
				GameRoot.sendMessage(concreteParent.getIdentity(), res);

			}
		} else if (event.isId(PlayerManager.LeaveWorld_GE_Id)) {

		}

		super.handleGameEvent(event);
	}

	@Override
	public void handleActionEvent(ActionEventBase event) {
		short eventId = event.getActionEventId();
		if (logger.isDebugEnabled()) {
			logger.debug("多倍经验Id:" + eventId);
		}
		short actionEventId = event.getActionEventId();
		Identity identity = event.getIdentity();
		switch (eventId) {
		case SceneActivityEventDefines.C2G_MultiTimesExp_RequestTime:
			handle_MultiTimesExp_RequestTime((C2G_MultiTimesExp_RequestTime) event, actionEventId, identity);
			break;
		}
		super.handleActionEvent(event);
	}

	private void handle_MultiTimesExp_RequestTime(C2G_MultiTimesExp_RequestTime event, short actionEventId, Identity identity) {

		if (mutilExpActivity == null) {
			logger.error("mutilExpActivity is null");
			return;
		}

		G2C_MultiTimesExp_RequestTime res = MessageFactory.getConcreteMessage(SceneActivityEventDefines.G2C_MultiTimesExp_RequestTime);
		res.setTimeToStar(mutilExpActivity.getActivityRemainingStartTime());
		res.setTimeToEnd(mutilExpActivity.getActivityRemainingEndTime());
		GameRoot.sendMessage(identity, res);
	}

	public void addMutilExp(Player player, Monster monster) {
		// 判断等级
		if (player.getLevel() < 40) {
			if (logger.isDebugEnabled()) {
				logger.debug("addMutilExpActivity player has not enough level=" + player.getLevel());
			}
			return;
		}

		// 是否活动期间
		if (mutilExpActivity.getCrtActivityState() != SceneActivity.ACTIVITY_START) {
			return;
		}

		int expMultiple = mutilExpActivity.getMGMutilExpActivityRef().getRate();
		if (expMultiple < 1) {
			if (logger.isDebugEnabled()) {
				logger.debug("MutilExpActivity rate=" + expMultiple + "is error");
			}
			return;
		}

		int exp = (int) MGPropertyAccesser.getExp(monster.getMonsterRef().getProperty());
		int expGot = exp;
		if (expMultiple != 0) {
			expGot = exp * (expMultiple - 1);
		}

		player.getExpComponent().addExp(expGot);

	}

}
