package newbee.morningGlory.mmorpg.player.activity.limitTimeRank;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import newbee.morningGlory.code.MGSuccessCode;
import newbee.morningGlory.mmorpg.player.activity.constant.AwardData;
import newbee.morningGlory.mmorpg.player.activity.constant.AwardState;
import newbee.morningGlory.mmorpg.player.activity.constant.AwardTypeDefine;
import newbee.morningGlory.mmorpg.player.activity.limitTimeRank.event.C2G_LimitTimeRank_GetReward;
import newbee.morningGlory.mmorpg.player.activity.limitTimeRank.event.C2G_LimitTimeRank_List;
import newbee.morningGlory.mmorpg.player.activity.limitTimeRank.event.C2G_LimitTimeRank_Version;
import newbee.morningGlory.mmorpg.player.activity.limitTimeRank.event.G2C_LimitTimeRank_GetReward;
import newbee.morningGlory.mmorpg.player.activity.limitTimeRank.event.G2C_LimitTimeRank_List;
import newbee.morningGlory.mmorpg.player.activity.limitTimeRank.event.G2C_LimitTimeRank_Version;
import newbee.morningGlory.mmorpg.player.activity.limitTimeRank.event.MGLimitTimeDefines;

import org.apache.log4j.Logger;

import sophia.foundation.communication.core.ActionEventBase;
import sophia.foundation.communication.core.MessageFactory;
import sophia.game.GameRoot;
import sophia.game.component.communication.GameEvent;
import sophia.mmorpg.MMORPGContext;
import sophia.mmorpg.base.ConcreteComponent;
import sophia.mmorpg.core.timer.SFTimeChimeListener;
import sophia.mmorpg.core.timer.SFTimer;
import sophia.mmorpg.core.timer.SFTimerCreater;
import sophia.mmorpg.event.ResultEvent;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.gameEvent.EnterWorld_SceneReady_GE;
import sophia.mmorpg.player.persistence.immediatelySave.PlayerImmediateDaoFacade;

public class MGLimitTimeRankComponent extends ConcreteComponent<Player> {
	private static Logger logger = Logger.getLogger(MGLimitTimeRankComponent.class);

	public static final String Tag = "MGLimitTimeRankComponent";

	private LimitTimeRankMgr rankMgr = new LimitTimeRankMgr();

	private SFTimerCreater timerCreater = MMORPGContext.getTimerCreater();

	public static final String EnterWorld_SceneReady_ID = EnterWorld_SceneReady_GE.class.getSimpleName();

	private List<SFTimer> timers = new ArrayList<SFTimer>();

	@Override
	public void ready() {
		init();
		rankMgr.setOwner(concreteParent);
		addTimers();
		addActionEventListener(MGLimitTimeDefines.C2G_LimitTimeRank_List);
		addActionEventListener(MGLimitTimeDefines.C2G_LimitTimeRank_GetReward);
		addActionEventListener(MGLimitTimeDefines.C2G_LimitTimeRank_Version);
		addInterGameEventListener(EnterWorld_SceneReady_ID);
		super.ready();
	}

	@Override
	public void suspend() {
		cancelTimer();
		removeActionEventListener(MGLimitTimeDefines.C2G_LimitTimeRank_List);
		removeActionEventListener(MGLimitTimeDefines.C2G_LimitTimeRank_GetReward);
		removeActionEventListener(MGLimitTimeDefines.C2G_LimitTimeRank_Version);
		removeInterGameEventListener(EnterWorld_SceneReady_ID);
		super.suspend();
	}

	@Override
	public void handleGameEvent(GameEvent<?> event) {
		if (event.isId(EnterWorld_SceneReady_ID)) {
			byte activityOverState = rankMgr.getActivityOverState();
			rankMgr.sendMsgToclient(activityOverState);
		}
		super.handleGameEvent(event);
	}

	private void addTimers() {
		for (LimitRankType limitRankType : LimitTimeActivityMgr.limitRankTypes) {
			if (!LimitTimeActivityMgr.isTimeOver(limitRankType)) {
				startTimer(limitRankType);
			}
		}
	}

	private void startTimer(final LimitRankType limitRankType) {
		SFTimer timer = timerCreater.hourCalendarChime(new SFTimeChimeListener() {

			@Override
			public void handleTimeChimeCancel() {

			}

			@Override
			public void handleTimeChime() {
				handleLimitTimeRankActivity(limitRankType);
			}

			@Override
			public void handleServiceShutdown() {
			}
		});
		timers.add(timer);
	}

	private void cancelTimer() {
		for (SFTimer timer : timers) {
			if (null != timer) {
				timer.cancel();
			}
		}
	}

	private void handleLimitTimeRankActivity(LimitRankType limitRankType) {
		if (LimitTimeActivityMgr.isTimeOver(limitRankType)) {
			if (rankMgr.getActivityOverState() == LimitTimeRankMacro.Cant_Opne) {
				rankMgr.sendMsgToclient(LimitTimeRankMacro.Cant_Opne);
			}
		}
	}

	private void init() {
		Map<String, Byte> limitTimeRefIdRankTypeMapping = LimitTimeActivityMgr.getLimitTimeDataMaps();

		Map<Byte, List<String>> limitTimeDataMaps = new HashMap<Byte, List<String>>();

		Map<String, AwardData> rewardMaps = rankMgr.getRewardMaps();

		Map<Byte, String> beginEndTimeMaps = LimitTimeActivityMgr.getBeginEndTimeMaps();

		for (Entry<Byte, String> entry : beginEndTimeMaps.entrySet()) {
			if (logger.isDebugEnabled()) {
				logger.debug("limitRankType = " + entry.getKey() + ", time" + entry.getValue());
			}
		}

		rankMgr.setBeginEndTimeMaps(beginEndTimeMaps);

		if (rewardMaps == null || rewardMaps.size() == 0) {
			for (Entry<String, Byte> entry : limitTimeRefIdRankTypeMapping.entrySet()) {
				AwardData awardData = new AwardData(AwardTypeDefine.RewardType_LimitTimeRank, AwardState.Init);
				rewardMaps.put(entry.getKey(), awardData);
			}
		}

		List<String> list = null;

		List<LimitRankType> limitRankTypes = LimitTimeActivityMgr.getLimitRankTypes();

		for (LimitRankType limitRankType : limitRankTypes) {

			// refId , rankType
			for (Entry<String, Byte> entry : limitTimeRefIdRankTypeMapping.entrySet()) {
				String limitRankRefId = entry.getKey();
				byte rankType = entry.getValue();

				if (rankType == limitRankType.value()) {
					if (limitTimeDataMaps.get(rankType) == null) {
						list = new ArrayList<String>();
						list.add(limitRankRefId);
						limitTimeDataMaps.put(rankType, list);
					} else {
						limitTimeDataMaps.get(rankType).add(limitRankRefId);
					}
				}
			}
		}
		
		rankMgr.setLimitTimeDataMaps(limitTimeDataMaps);
	}

	@Override
	public void handleActionEvent(ActionEventBase event) {
		short eventId = event.getActionEventId();
		if (logger.isDebugEnabled()) {
			logger.debug("限时冲榜事件Id:" + eventId);
		}

		switch (eventId) {
		case MGLimitTimeDefines.C2G_LimitTimeRank_List:
			handle_LimitTimeRank_List((C2G_LimitTimeRank_List) event);
			break;
		case MGLimitTimeDefines.C2G_LimitTimeRank_GetReward:
			handle_LimitTimeRank_GetReward((C2G_LimitTimeRank_GetReward) event);
			break;
		case MGLimitTimeDefines.C2G_LimitTimeRank_Version:
			handle_LimitTimeRank_Version((C2G_LimitTimeRank_Version) event);
			break;
		}
		super.handleActionEvent(event);
	}

	private void handle_LimitTimeRank_List(C2G_LimitTimeRank_List event) {
		byte sortBoardType = event.getSortBoardType();
		G2C_LimitTimeRank_List res = MessageFactory.getConcreteMessage(MGLimitTimeDefines.G2C_LimitTimeRank_List);
		res.setRankMgr(rankMgr);
		res.setSortBoardType(sortBoardType);
		GameRoot.sendMessage(getConcreteParent().getIdentity(), res);
	}

	private void handle_LimitTimeRank_GetReward(C2G_LimitTimeRank_GetReward event) {
		String rewardRefId = event.getRefId();

		int resultCode = rankMgr.getReward(rewardRefId);

		if (resultCode == MGSuccessCode.CODE_SUCCESS) {
			logger.debug("限时冲榜领取奖励成功");

			rankMgr.changeAwardReceivedState(rewardRefId);
			G2C_LimitTimeRank_GetReward res = MessageFactory.getConcreteMessage(MGLimitTimeDefines.G2C_LimitTimeRank_GetReward);
			res.setRefId(rewardRefId);
			GameRoot.sendMessage(getConcreteParent().getIdentity(), res);
		}

		if (rankMgr.getActivityOverState() == LimitTimeRankMacro.Cant_Opne) {
			rankMgr.sendMsgToclient(LimitTimeRankMacro.Cant_Opne);
		}

		ResultEvent.sendResult(getConcreteParent().getIdentity(), event.getActionEventId(), resultCode);
		PlayerImmediateDaoFacade.update(getConcreteParent());
	}

	private void handle_LimitTimeRank_Version(C2G_LimitTimeRank_Version event) {
		byte type = event.getLimitRankType();
		int ver = 1;

		Map<Byte, Integer> versionMapping = LimitTimeActivityMgr.getVersion();

		if (null != versionMapping.get(type)) {
			ver = versionMapping.get(type);
		}

		if (logger.isDebugEnabled()) {
			logger.debug("get version version : from client type = " + type + ", version = " + ver);
		}

		G2C_LimitTimeRank_Version res = MessageFactory.getConcreteMessage(MGLimitTimeDefines.G2C_LimitTimeRank_Version);
		res.setLimitRankType(type);
		res.setVersion(ver);
		GameRoot.sendMessage(getConcreteParent().getIdentity(), res);
	}

	public LimitTimeRankMgr getRankMgr() {
		return rankMgr;
	}

	public void setRankMgr(LimitTimeRankMgr rankMgr) {
		this.rankMgr = rankMgr;
	}

}
