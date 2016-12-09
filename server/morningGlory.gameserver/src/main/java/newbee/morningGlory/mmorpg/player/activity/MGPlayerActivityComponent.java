package newbee.morningGlory.mmorpg.player.activity;

import java.util.Map;
import java.util.Map.Entry;

import newbee.morningGlory.code.MGSuccessCode;
import newbee.morningGlory.mmorpg.player.activity.constant.ActivityData;
import newbee.morningGlory.mmorpg.player.activity.constant.AwardData;
import newbee.morningGlory.mmorpg.player.activity.constant.AwardState;
import newbee.morningGlory.mmorpg.player.activity.constant.AwardTypeDefine;
import newbee.morningGlory.mmorpg.player.activity.event.C2G_Activity_CanRecieve;
import newbee.morningGlory.mmorpg.player.activity.event.C2G_Advanced_GetReward;
import newbee.morningGlory.mmorpg.player.activity.event.C2G_Advanced_List;
import newbee.morningGlory.mmorpg.player.activity.event.C2G_LevelUp_GetReward;
import newbee.morningGlory.mmorpg.player.activity.event.C2G_LevelUp_List;
import newbee.morningGlory.mmorpg.player.activity.event.C2G_OT_ShowOnLineTimer;
import newbee.morningGlory.mmorpg.player.activity.event.C2G_SignIn;
import newbee.morningGlory.mmorpg.player.activity.event.C2G_SignIn_List;
import newbee.morningGlory.mmorpg.player.activity.event.C2G_getReward;
import newbee.morningGlory.mmorpg.player.activity.event.G2C_Activity_CanRecieve;
import newbee.morningGlory.mmorpg.player.activity.event.G2C_Advanced_List;
import newbee.morningGlory.mmorpg.player.activity.event.G2C_LevelUp_List;
import newbee.morningGlory.mmorpg.player.activity.event.G2C_SignIn_List;
import newbee.morningGlory.mmorpg.player.activity.event.MGActivityEventDefines;
import newbee.morningGlory.mmorpg.player.activity.limitTimeRank.MGLimitTimeRankComponent;
import newbee.morningGlory.mmorpg.player.activity.mgr.AdvancedMgr;
import newbee.morningGlory.mmorpg.player.activity.mgr.LevelUpMgr;
import newbee.morningGlory.mmorpg.player.activity.mgr.OnlineMgr;
import newbee.morningGlory.mmorpg.player.activity.mgr.SignMgr;
import newbee.morningGlory.mmorpg.player.activity.persistence.ActivityPersistenceObject;
import newbee.morningGlory.mmorpg.player.wing.actionEvent.MGWingLevelUp_GE;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import sophia.foundation.communication.core.ActionEventBase;
import sophia.foundation.communication.core.MessageFactory;
import sophia.game.GameRoot;
import sophia.game.component.communication.GameEvent;
import sophia.mmorpg.base.ConcreteComponent;
import sophia.mmorpg.event.ResultEvent;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.exp.gameevent.PlayerLevelUp_GE;
import sophia.mmorpg.player.gameEvent.EnterWorld_SceneReady_GE;
import sophia.mmorpg.player.gameEvent.LeaveWorld_GE;
import sophia.mmorpg.player.mount.gameEvent.MGMountLevelUp_GE;
import sophia.mmorpg.player.persistence.immediatelySave.PlayerImmediateDaoFacade;

public class MGPlayerActivityComponent extends ConcreteComponent<Player> {
	private static Logger logger = Logger.getLogger(MGPlayerActivityComponent.class);

	public static final String Tag = "MGPlayerActivityComponent";

	public static final String LeaveWorld_GE_ID = LeaveWorld_GE.class.getSimpleName();

	public static final String EnterWorld_SceneReady_ID = EnterWorld_SceneReady_GE.class.getSimpleName();

	public static final String MOUNT_GE_ID = MGMountLevelUp_GE.class.getSimpleName();

	public static final String WING_GE_ID = MGWingLevelUp_GE.class.getSimpleName();

	public static final String PlayerLevelUp_GE_Id = PlayerLevelUp_GE.class.getSimpleName();

	private ActivityPersistenceObject activityPersistenceObject;
	
	private OnlineMgr onlineMgr = new OnlineMgr();

	private AdvancedMgr advanceMgr = new AdvancedMgr();

	private LevelUpMgr levelUpMgr = new LevelUpMgr();

	private SignMgr signMgr = new SignMgr();

	private static final byte Can_GetReward = 1;

	private static final byte Cant_GetReward = 0;

	@Override
	public void ready() {
		init();
		addActionEventListener(MGActivityEventDefines.C2G_SignIn_List);
		addActionEventListener(MGActivityEventDefines.C2G_SignIn);
		addActionEventListener(MGActivityEventDefines.C2G_getReward);
		addActionEventListener(MGActivityEventDefines.C2G_Advanced_List);
		addActionEventListener(MGActivityEventDefines.C2G_Advanced_GetReward);
		addActionEventListener(MGActivityEventDefines.C2G_LevelUp_List);
		addActionEventListener(MGActivityEventDefines.C2G_LevelUp_GetReward);
		addActionEventListener(MGActivityEventDefines.C2G_Activity_CanRecieve);
		addActionEventListener(MGActivityEventDefines.C2G_OT_ShowOnLineTimer);
		addInterGameEventListener(EnterWorld_SceneReady_ID);
		addInterGameEventListener(LeaveWorld_GE_ID);
		addInterGameEventListener(WING_GE_ID);
		addInterGameEventListener(MOUNT_GE_ID);
		addInterGameEventListener(PlayerLevelUp_GE_Id);

		super.ready();
	}

	@Override
	public void suspend() {
		removeActionEventListener(MGActivityEventDefines.C2G_SignIn);
		removeActionEventListener(MGActivityEventDefines.C2G_getReward);
		removeActionEventListener(MGActivityEventDefines.C2G_SignIn_List);
		removeActionEventListener(MGActivityEventDefines.C2G_Advanced_List);
		removeActionEventListener(MGActivityEventDefines.C2G_Advanced_GetReward);
		removeActionEventListener(MGActivityEventDefines.C2G_LevelUp_List);
		removeActionEventListener(MGActivityEventDefines.C2G_LevelUp_GetReward);
		removeActionEventListener(MGActivityEventDefines.C2G_Activity_CanRecieve);
		removeActionEventListener(MGActivityEventDefines.C2G_OT_ShowOnLineTimer);
		removeInterGameEventListener(EnterWorld_SceneReady_ID);
		removeInterGameEventListener(LeaveWorld_GE_ID);
		removeInterGameEventListener(WING_GE_ID);
		removeInterGameEventListener(MOUNT_GE_ID);
		removeInterGameEventListener(PlayerLevelUp_GE_Id);
		onlineMgr.destoryTimer();
		signMgr.cancelTimer();
		super.suspend();
	}

	@Override
	public void handleGameEvent(GameEvent<?> event) {
		onlineMgr.handleGameEvent(event);
		advanceMgr.handleGameEvent(event);
		levelUpMgr.handleGameEvent(event);
		super.handleGameEvent(event);
	}

	@Override
	public void handleActionEvent(ActionEventBase event) {
		short eventId = event.getActionEventId();
		if (logger.isDebugEnabled()) {
			logger.debug("活动事件请求Id" + eventId);
		}
		switch (eventId) {
		case MGActivityEventDefines.C2G_SignIn_List:
			handle_SignIn_List((C2G_SignIn_List) event);
			break;
			
		case MGActivityEventDefines.C2G_OT_ShowOnLineTimer:
			handle_ShowOnLineTimer((C2G_OT_ShowOnLineTimer) event);
			break;

		case MGActivityEventDefines.C2G_SignIn:
			handle_SignIn((C2G_SignIn) event);
			break;

		case MGActivityEventDefines.C2G_getReward:
			handle_getReward((C2G_getReward) event);
			break;

		case MGActivityEventDefines.C2G_Advanced_List:
			handle_Advanced_List((C2G_Advanced_List) event);
			break;

		case MGActivityEventDefines.C2G_LevelUp_List:
			handle_LevelUp_List((C2G_LevelUp_List) event);
			break;

		case MGActivityEventDefines.C2G_Advanced_GetReward:
			handle_Advanced_GetReward((C2G_Advanced_GetReward) event);
			break;

		case MGActivityEventDefines.C2G_LevelUp_GetReward:
			handle_LevelUp_GetReward((C2G_LevelUp_GetReward) event);
			break;
			
		case MGActivityEventDefines.C2G_Activity_CanRecieve:
			handle_Activity_CanGetReward((C2G_Activity_CanRecieve) event);
			break;
		}
		super.handleActionEvent(event);
	}
	
	private void handle_ShowOnLineTimer(C2G_OT_ShowOnLineTimer event){
		onlineMgr.sendOnlineStateMessage();
	}

	private void handle_SignIn_List(C2G_SignIn_List event) {
		G2C_SignIn_List res = MessageFactory.getConcreteMessage(MGActivityEventDefines.G2C_SignIn_List);
		res.setSignMgr(signMgr);
		GameRoot.sendMessage(getConcreteParent().getIdentity(), res);
	}

	private void handle_Advanced_List(C2G_Advanced_List event) {
		G2C_Advanced_List res = MessageFactory.getConcreteMessage(MGActivityEventDefines.G2C_Advanced_List);
		res.setAdvancedMgr(advanceMgr);
		GameRoot.sendMessage(getConcreteParent().getIdentity(), res);
	}

	private void handle_LevelUp_List(C2G_LevelUp_List event) {
		G2C_LevelUp_List res = MessageFactory.getConcreteMessage(MGActivityEventDefines.G2C_LevelUp_List);
		res.setLevelUpMgr(levelUpMgr);
		GameRoot.sendMessage(getConcreteParent().getIdentity(), res);
	}

	private void handle_SignIn(C2G_SignIn event) {
		signMgr.sign(event);
	}

	private void handle_getReward(C2G_getReward event) {
		byte type = event.getType();
		String refId = event.getGiftRefId();
		if (logger.isDebugEnabled()) {
			logger.debug("活动类型 = " + type + ",refId = " + refId);
		}

		switch (type) {
		case AwardTypeDefine.RewardType_Singin:
			signMgr.getReward(refId);
			break;
		case AwardTypeDefine.RewardType_CumulativeTime:
			onlineMgr.getAllReward();
			break;
		default:
			break;
		}
		PlayerImmediateDaoFacade.update(getConcreteParent());

	}

	private void handle_Advanced_GetReward(C2G_Advanced_GetReward event) {
		int resultCode = advanceMgr.getReward(event.getRefId());
		ResultEvent.sendResult(getConcreteParent().getIdentity(), event.getActionEventId(), resultCode);
		if (resultCode == MGSuccessCode.CODE_SUCCESS) {
			if (logger.isDebugEnabled()) {
				logger.debug("进阶奖励领取成功");
			}
			advanceMgr.changeRewardState(event.getRefId(), AwardState.Received);
			PlayerImmediateDaoFacade.update(getConcreteParent());
		}

	}

	private void handle_LevelUp_GetReward(C2G_LevelUp_GetReward event) {
		int resultCode = levelUpMgr.getReward(event.getRefId());
		ResultEvent.sendResult(getConcreteParent().getIdentity(), event.getActionEventId(), resultCode);
		if (resultCode == MGSuccessCode.CODE_SUCCESS) {
			logger.debug("升级奖励领取成功");
			levelUpMgr.changeRewardState(event.getRefId(), AwardState.Received);
			PlayerImmediateDaoFacade.update(getConcreteParent());
		}

	}

	private void resetSignInfo() {
		signMgr.resetSignInfo();
	}

	public void handle_Activity_CanGetReward(C2G_Activity_CanRecieve event) {
		byte activityType = event.getActivityType();
		byte result = Cant_GetReward;

		if (activityType == AwardTypeDefine.RewardType_LevelUp) {
			result = levelUpMgr.canGetReward() ? Can_GetReward : Cant_GetReward;
		} else if (activityType == AwardTypeDefine.RewardType_Advanced) {
			result = advanceMgr.canGetReward() ? Can_GetReward : Cant_GetReward;
		} else if (activityType == AwardTypeDefine.RewardType_LimitTimeRank) {
			MGLimitTimeRankComponent limitTimeRankComponent = (MGLimitTimeRankComponent) getConcreteParent().getTagged(MGLimitTimeRankComponent.Tag);
			result = limitTimeRankComponent.getRankMgr().canGetReward() ? Can_GetReward : Cant_GetReward;
		} else {
			logger.error("error argument! activityType = " + activityType);
			return;
		}
		
		G2C_Activity_CanRecieve res = MessageFactory.getConcreteMessage(MGActivityEventDefines.G2C_Activity_CanRecieve);
		res.setType(activityType);
		res.setResult(result);
		GameRoot.sendMessage(event.getIdentity(), res);
	}
	
	private void init() {
		signMgr.startTimer();
		onlineMgr.startTimer();

		if (logger.isDebugEnabled()) {
			logger.debug("现在时间:" + signMgr.getCalenderInfo() + ",最后签到时间" + signMgr.getSignDateInfo());
		}

		if (!StringUtils.equals(signMgr.getSignDateInfo().substring(0, 6), signMgr.getCalenderInfo().substring(0, 6))) {
			resetSignInfo();
		}

		if (signMgr.getAccumulativeSignAwardMap() == null || signMgr.getAccumulativeSignAwardMap().size() == 0) {
			for (Entry<String, AwardData> entry : ActivityData.getSignAwardMap().entrySet()) {
				signMgr.getAccumulativeSignAwardMap().put(entry.getKey(), ((AwardData) entry.getValue()).clone());
			}
		}

		if (advanceMgr.getRideAwardMaps() == null || advanceMgr.getRideAwardMaps().size() == 0) {
			for (Entry<String, AwardData> entry : ActivityData.getRideAwardMaps().entrySet()) {
				advanceMgr.getRideAwardMaps().put(entry.getKey(), ((AwardData) entry.getValue()).clone());
			}
		}

		if (advanceMgr.getWingAwardMaps() == null || advanceMgr.getWingAwardMaps().size() == 0) {
			for (Entry<String, AwardData> entry : ActivityData.getWingAwardMaps().entrySet()) {
				advanceMgr.getWingAwardMaps().put(entry.getKey(), ((AwardData) entry.getValue()).clone());
			}
		}
		
		Map<String, AwardData> onlineAwards = onlineMgr.getOnlineAwards();
		if (onlineAwards == null || onlineAwards.size() == 0) {
			for (Entry<String, AwardData> entry : ActivityData.getOnlineAwardMaps().entrySet()) {
				onlineAwards.put(entry.getKey(), ((AwardData) entry.getValue()).clone());
			}
		}
		
		if (levelUpMgr.getLevelUpMaps() == null || levelUpMgr.getLevelUpMaps().size() == 0) {
			for (Entry<String, AwardData> entry : ActivityData.getLevelAwardMaps().entrySet()) {
				logger.debug(entry.getKey() + "|||" + entry.getValue().getState());
			}
			for (Entry<String, AwardData> entry : ActivityData.getLevelAwardMaps().entrySet()) {
				levelUpMgr.getLevelUpMaps().put(entry.getKey(), ((AwardData) entry.getValue()).clone());
			}
		}
	}

	public SignMgr getSignMgr() {
		return signMgr;
	}

	public void setSignMgr(SignMgr signMgr) {
		this.signMgr = signMgr;
	}

	public AdvancedMgr getAdvanceMgr() {
		return advanceMgr;
	}

	public OnlineMgr getOnlineMgr() {
		return onlineMgr;
	}

	public void setAdvanceMgr(AdvancedMgr advanceMgr) {
		this.advanceMgr = advanceMgr;
	}

	public LevelUpMgr getLevelUpMgr() {
		return levelUpMgr;
	}

	public void setLevelUpMgr(LevelUpMgr levelUpMgr) {
		this.levelUpMgr = levelUpMgr;
	}

	public ActivityPersistenceObject getActivityPersistenceObject() {
		return activityPersistenceObject;
	}

	public void setActivityPersistenceObject(ActivityPersistenceObject activityPersistenceObject) {
		this.activityPersistenceObject = activityPersistenceObject;
	}

}
