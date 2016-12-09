package newbee.morningGlory.mmorpg.player.activity.mgr;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import newbee.morningGlory.code.MGErrorCode;
import newbee.morningGlory.mmorpg.player.activity.MGPlayerActivityComponent;
import newbee.morningGlory.mmorpg.player.activity.constant.AwardData;
import newbee.morningGlory.mmorpg.player.activity.constant.AwardState;
import newbee.morningGlory.mmorpg.player.activity.event.G2C_OT_ShowOnLineTimer;
import newbee.morningGlory.mmorpg.player.activity.event.MGActivityEventDefines;
import newbee.morningGlory.mmorpg.player.activity.ref.OnlineRef;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import sophia.foundation.communication.core.MessageFactory;
import sophia.game.GameRoot;
import sophia.game.component.communication.GameEvent;
import sophia.mmorpg.MMORPGContext;
import sophia.mmorpg.code.MMORPGErrorCode;
import sophia.mmorpg.core.timer.SFTimeChimeListener;
import sophia.mmorpg.core.timer.SFTimeUnit;
import sophia.mmorpg.core.timer.SFTimer;
import sophia.mmorpg.core.timer.SFTimerCreater;
import sophia.mmorpg.event.ResultEvent;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.itemBag.ItemFacade;
import sophia.mmorpg.player.itemBag.ItemOptSource;
import sophia.mmorpg.player.itemBag.ItemPair;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;
import sophia.mmorpg.utils.DateTimeUtil;

import com.google.common.base.Strings;

public class TotalOnlineMgr {
	private static Logger logger = Logger.getLogger(TotalOnlineMgr.class);

	public static final String first_CumulativeTimeRefId = "online_1";

	private String crtCumulativeTimeRefId = first_CumulativeTimeRefId;

	private Map<String, AwardData> onlineAwardMaps = new HashMap<String, AwardData>();

	SFTimerCreater timerCreater = MMORPGContext.getTimerCreater();

	public static final byte State_NoReward = 0;

	public static final byte State_HasReward = 1;

	private SFTimer cumulativeTimer;

	private Player owner;
	
	private SFTimer dailyResetTimer;

	// 是否有奖励可以领取
	private boolean hasAward;

	// 倒计时开始时间
	private long startAccumulativeMills;

	// 上一次累计时间
	private int lastAccumulativeSeconds;

	// 倒计时剩余累计时间
	private int remainAccumulativeTime;
	
	// 有效活动时间 yyyyMMdd
	private String timeString = null;

	public TotalOnlineMgr() {
		startAccumulativeMills = System.currentTimeMillis();
		remainAccumulativeTime = getCrtAccumulativeSeconds();
		timeString = DateTimeUtil.getTimeStringOfToday();
	}
	
	public void startTimer() {
		cumulativeTimer = timerCreater.secondInterval(new SFTimeChimeListener() {
			@Override
			public void handleServiceShutdown() {

			}

			@Override
			public void handleTimeChimeCancel() {

			}

			@Override
			public void handleTimeChime() {
				cumulativeTimer();
			}

		});
		
		dailyResetTimer = timerCreater.calendarChime(new SFTimeChimeListener() {
			
			@Override
			public void handleTimeChimeCancel() {
			}
			
			@Override
			public void handleTimeChime() {
				dailyReset();
			}
			
			@Override
			public void handleServiceShutdown() {
			}
		}, SFTimeUnit.HOUR, 0);
	}

	private void cumulativeTimer() {
		// 有奖励不倒计时
		if (isHasAward()) {
			return;
		}

		// 活动结束不倒计时
		if (Strings.isNullOrEmpty(crtCumulativeTimeRefId)) {
			return;
		}

		if (getRemainAccumulativeSeconds() <= 0) {
			// 倒计时结束,可以领取奖励
			setHasAward(true);
			if (onlineAwardMaps.get(crtCumulativeTimeRefId) != null) {
				AwardData awardData = onlineAwardMaps.get(crtCumulativeTimeRefId);
				if (awardData.getState() == AwardState.Init) {
					onlineAwardMaps.get(crtCumulativeTimeRefId).setState(AwardState.Sure);
				}
			}
		}
	}

	public void cancelTimer() {
		if (null != cumulativeTimer) {
			cumulativeTimer.cancel();
		}
		
		if(null != dailyResetTimer) {
			dailyResetTimer.cancel();
		}
	}
	

	private int getCrtAccumulativeSeconds() {
		OnlineRef ref = (OnlineRef) GameRoot.getGameRefObjectManager().getManagedObject(crtCumulativeTimeRefId);
		if (ref == null) {
			return 0;
		}
		return MGPropertyAccesser.getOnlineSecond(ref.getProperty());
	}
	
	private void dailyReset() {
		resetAccumulativeRefId();
		resetLastAccumulativeSeconds();
		resetStartAccumulativeMills();
		resetAllRewardState();
		resetTimeString();
		setHasAward(false);
	}

	public void handleGameEvent(GameEvent<?> event) {
		if (event.isId(MGPlayerActivityComponent.LeaveWorld_GE_ID)) {
			// 玩家下线时，计算本次累计时间
			calculateAlreadyAccumulativeSeconds();
		} else if (event.isId(MGPlayerActivityComponent.EnterWorld_SceneReady_ID)) {
			String nowTimeString = DateTimeUtil.getTimeStringOfToday();
			if (!StringUtils.equals(nowTimeString, timeString)) {
				dailyReset();
			}
			
			resetStartAccumulativeMills();
			loginSendTimeToClient();
		}
	}

	public void loginSendTimeToClient() {
		//sendOnlineMsgToClient(crtCumulativeTimeRefId, getRemainAccumulativeSeconds());
	}

	public int getRemainAccumulativeSeconds() {
		int crtTargetAccumulativeSeconds = getCrtAccumulativeSeconds();
			
		int remainAccumulativeSeconds = crtTargetAccumulativeSeconds - lastAccumulativeSeconds - getCurrentValidTime();

		remainAccumulativeSeconds = remainAccumulativeSeconds <= 0 ? 0 : remainAccumulativeSeconds;
		if (logger.isDebugEnabled()) {
			logger.debug("本次倒计时总时间 = " + crtTargetAccumulativeSeconds + " 秒, " + " 剩余累计时间: " + remainAccumulativeSeconds);
		}
		return remainAccumulativeSeconds;
	}

	private int calculateAlreadyAccumulativeSeconds() {
		lastAccumulativeSeconds += getCurrentValidTime();
		return lastAccumulativeSeconds;
	}
	
	private int getCurrentValidTime() {
		return (int)((System.currentTimeMillis() - startAccumulativeMills)/1000);
	}

	public void sendOnlineMsgToClient(String refId, int time) {
		G2C_OT_ShowOnLineTimer res = (G2C_OT_ShowOnLineTimer) MessageFactory.getMessage(MGActivityEventDefines.G2C_OT_ShowOnLineTimer);
		byte state = time <= 0 ? State_HasReward : State_NoReward;
		res.setRefId(refId);
		res.setRemainTime(time);
		res.setState(state);
		
		if(logger.isDebugEnabled()) {
			logger.debug("发送到客户端的剩余累计时间为 : " + time + " awardStat = " + state);
		}
		GameRoot.sendMessage(owner.getIdentity(), res);
	}

	private void resetStartAccumulativeMills() {
		startAccumulativeMills = System.currentTimeMillis();
	}

	private void resetLastAccumulativeSeconds() {
		lastAccumulativeSeconds = 0;
	}
	
	private void resetAccumulativeRefId() {
		crtCumulativeTimeRefId = first_CumulativeTimeRefId;
	}

	public boolean isHasAward() {
		return hasAward;
	}

	public void setHasAward(boolean hasAward) {
		this.hasAward = hasAward;
	}

	public Map<String, AwardData> getOnlineAwardMaps() {
		return onlineAwardMaps;
	}

	public void setOnlineAwardMaps(Map<String, AwardData> onlineAwardMaps) {
		this.onlineAwardMaps = onlineAwardMaps;
	}

	public void getReward(String refId) {
		OnlineRef ref = (OnlineRef) GameRoot.getGameRefObjectManager().getManagedObject(refId);
		if (ref == null) {
			logger.error("客户端发送过来的错误refId = " + refId);
			return;
		}

		if (!isSureAwardState(refId)) {
			if (logger.isDebugEnabled()) {
				logger.debug("不能领取奖励的refId　:" + refId + " state = " + onlineAwardMaps.get(refId).getState());
			}
			ResultEvent.sendResult(owner.getIdentity(), MGActivityEventDefines.C2G_Advanced_GetReward, MGErrorCode.CODE_TimeNotOver);
			return;
		}

		int remainSeconds = getRemainAccumulativeSeconds();
		if (remainSeconds > 0) {
			if (logger.isDebugEnabled()) {
				logger.debug("本次倒计时还有 " + remainSeconds + ", 不能领取奖励");
			}
			ResultEvent.sendResult(owner.getIdentity(), MGActivityEventDefines.C2G_Advanced_GetReward, MGErrorCode.CODE_TimeNotOver);
			return;
		}

		List<ItemPair> itemPairs = ref.getItemPairs();
		if (ItemFacade.addItemCompareSlot(getOwner(), itemPairs, ItemOptSource.TotalOnline).isOK()) {
			handleSuccessGetReward(ref, ref.getOnlineNextRef());
		} else {
			ResultEvent.sendResult(owner.getIdentity(), MGActivityEventDefines.C2G_getReward, MMORPGErrorCode.CODE_ITEM_SLOT_ENOUGH);
		}

	}

	private void handleSuccessGetReward(OnlineRef crtRef, OnlineRef nextRef) {
		if (null == crtRef) {
			return;
		}

		if (logger.isDebugEnabled()) {
			logger.debug("成功领取奖励的refId : " + crtRef.getId());
		}

		changeRewardState(crtRef.getId(), AwardState.Received);
		resetStartAccumulativeMills();
		resetLastAccumulativeSeconds();
		
		if (null == nextRef) {
			resetCrtCumulativeTimeRefId("");
			sendOnlineMsgToClient("", 0);
		} else {
			resetCrtCumulativeTimeRefId(nextRef.getId());
			sendOnlineMsgToClient(nextRef.getId(), nextRef.getOnlineSeconds());
		}

		setHasAward(false);
	}

	private void resetCrtCumulativeTimeRefId(String accumulativeRefId) {
		crtCumulativeTimeRefId = accumulativeRefId;
	}

	private boolean isSureAwardState(String refId) {
		return onlineAwardMaps.get(refId) != null && onlineAwardMaps.get(refId).getState() == AwardState.Sure;
	}

	public void changeRewardState(String refId, byte state) {
		AwardData awardData = null;
		if (onlineAwardMaps.get(refId) != null) {
			awardData = onlineAwardMaps.get(refId);
			awardData.setState(state);
		}
	}
	
	private void resetAllRewardState() {
		for(Entry<String, AwardData> entry : onlineAwardMaps.entrySet()) {
			entry.getValue().setState(AwardState.Init);
		}
	}
	
	private void resetTimeString() {
		timeString = DateTimeUtil.getTimeStringOfToday();
	}

	public Player getOwner() {
		return owner;
	}

	public void setOwner(Player owner) {
		this.owner = owner;
	}

	public String getCrtCumulativeTimeRefId() {
		return crtCumulativeTimeRefId;
	}

	public void setCrtCumulativeTimeRefId(String crtCumulativeTimeRefId) {
		this.crtCumulativeTimeRefId = crtCumulativeTimeRefId;
	}

	public int getLastAccumulativeSeconds() {
		return lastAccumulativeSeconds;
	}

	public void setLastAccumulativeSeconds(int lastAccumulativeSeconds) {
		this.lastAccumulativeSeconds = lastAccumulativeSeconds;
	}

	public int getRemainAccumulativeTime() {
		return remainAccumulativeTime;
	}

	public void setRemainAccumulativeTime(int remainAccumulativeTime) {
		this.remainAccumulativeTime = remainAccumulativeTime;
	}

	public String getTimeString() {
		return timeString;
	}

	public void setTimeString(String timeString) {
		this.timeString = timeString;
	}
	

}
