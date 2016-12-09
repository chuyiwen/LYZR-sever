package newbee.morningGlory.mmorpg.player.activity.mgr;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import newbee.morningGlory.code.MGErrorCode;
import newbee.morningGlory.code.MGSuccessCode;
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

public class OnlineMgr {
	private static Logger logger = Logger.getLogger(OnlineMgr.class);

	public static final String first_CumulativeTimeRefId = "online_1";

	private String crtCumulativeTimeRefId = first_CumulativeTimeRefId;

	private Map<String, AwardData> onlineAwards = new HashMap<String, AwardData>();

	SFTimerCreater timerCreater = MMORPGContext.getTimerCreater();

	private List<SFTimer> timers = new ArrayList<SFTimer>();
	
	// 有效活动时间 yyyyMMdd
	private String timeString = null;

	private Player player;
	
	private int lastOnlineTime;
	
	private long startMillis;
	
	public void setPlayer(Player player) {
		this.player = player;
	}
	
	public OnlineMgr() {
		resetTimeString();
	}

	public void startTimer() {
		SFTimer tickTiemr = timerCreater.secondInterval(new SFTimeChimeListener() {
			
			@Override
			public void handleTimeChimeCancel() {
			}
			
			@Override
			public void handleTimeChime() {
				checkOnlineTime();
			}
			
			@Override
			public void handleServiceShutdown() {
			}
		});

		SFTimer dailyResetTimer = timerCreater.calendarChime(new SFTimeChimeListener() {
			
			@Override
			public void handleTimeChimeCancel() {
				
			}
			
			@Override
			public void handleTimeChime() {
				dailyReset();
				//发送消息到客户端
				sendOnlineStateMessage();
			}
			
			@Override
			public void handleServiceShutdown() {
				
			}
		}, SFTimeUnit.HOUR, 0);

		timers.add(tickTiemr);
		timers.add(dailyResetTimer);
	}
	
	public void destoryTimer() {
		for (SFTimer timer : timers) {
			if (timer != null) {
				timer.cancel();
			}
		}
	}
	
	private void dailyReset() {
		
		resetAllRewardState();
		resetTimeString();
		resetStartMills(System.currentTimeMillis());
		resetLastOnlineTime();
		resetAccumulativeRefId();
	}
	
	public void checkOnlineTime() {
		if (startMillis == 0 ) {
			logger.debug("startMillis == 0");
			return;
		}

		String refId = getCrtCumulativeTimeRefId();
		
		// 活动已经结束
		if (Strings.isNullOrEmpty(refId)) {
			return;
		}
		
		int remainTime = getRemainTime();
		if (remainTime <= 0) {
			// 修改领奖状态标记
			changeAwardState(refId, AwardState.Sure);
			
			// 更换crtId
			changeCrtRefId(refId);
			
			//发送消息到客户端
			sendOnlineStateMessage();
		}
		
	}
	
	private void changeAwardState(String crtRefId, byte awardState) {
		Map<String, AwardData> awards = getOnlineAwards();
		
		AwardData awardData = awards.get(crtRefId);
		
		if (awardData == null) {
			logger.error("awardData is null! refId = " + crtRefId);
			return;
		}
		
		awardData.setState(awardState);
	}
	
	private synchronized void resetAllRewardState() {
		for(Entry<String, AwardData> entry : getOnlineAwards().entrySet()) {
			entry.getValue().setState(AwardState.Init);
		}
	}
	
	private void resetTimeString() {
		timeString = DateTimeUtil.getTimeStringOfToday();
	}
	
	private void changeCrtRefId(String crtRefId) {
		OnlineRef onlineRef = (OnlineRef)GameRoot.getGameRefObjectManager().getManagedObject(crtRefId);
		if (onlineRef == null) {
			return;
		}
		
		String nextRefId;
		OnlineRef onlineNextRef = onlineRef.getOnlineNextRef();
		if (onlineNextRef == null) {
			nextRefId = "";
		} else {
			nextRefId = onlineNextRef.getId();
		}
		
		setCrtCumulativeTimeRefId(nextRefId);
		
	}
	
	public void sendOnlineStateMessage() {
		String refId = getCrtCumulativeTimeRefId();
		int remainTime = getRemainTime();
		
		byte state = getState();
		
		if (logger.isDebugEnabled()) {
			logger.debug("remainTime = " + remainTime);
		}
		
		G2C_OT_ShowOnLineTimer res = MessageFactory.getConcreteMessage(MGActivityEventDefines.G2C_OT_ShowOnLineTimer);
		res.setRefId(refId);
		res.setRemainTime(remainTime);
		res.setState(state);
		
		if (logger.isDebugEnabled()) {
			logger.debug("refId = " + refId);
			logger.debug("remainTime = " + remainTime);
			logger.debug("state = " + state);
		}

		GameRoot.sendMessage(player.getIdentity(), res);
	}
	
	public synchronized byte getState() {
		for(Entry<String, AwardData> entry : getOnlineAwards().entrySet()) {
			byte state = entry.getValue().getState();
			if (state == AwardState.Sure) {
				return (byte)1;
			}
		}
		
		return (byte)0;
	}

	public int getOnlineTime() {
		
		int crtOnlineTime = (int)((System.currentTimeMillis() - startMillis)/1000);
		
		int onlineTime = lastOnlineTime + crtOnlineTime;
		
		if (logger.isDebugEnabled()) {
			logger.debug("lastOnlineTime = " + lastOnlineTime + ", crtOnlineTime = " + crtOnlineTime);
		}
		
		return onlineTime;
	}
	
	public int getNeedTime() {
		String refId = getCrtCumulativeTimeRefId();
		
		if (Strings.isNullOrEmpty(refId)) {
			return 0;
		}
		
		OnlineRef onlineRef = (OnlineRef)GameRoot.getGameRefObjectManager().getManagedObject(refId);
		if (onlineRef == null) {
			logger.error("error refId = " + refId);
			return 0;
		}
		
		int needTime = MGPropertyAccesser.getOnlineSecond(onlineRef.getProperty());
	
		return needTime;
	}
	
	public int getRemainTime() {
		int needTime = getNeedTime();
		
		int onlineTime = getOnlineTime();
		
		int remainTime = needTime - onlineTime;
		
		remainTime = remainTime <= 0 ? 0 : remainTime;
		
		return remainTime;
	}
	
	public synchronized void getAllReward() {
		Map<String, AwardData> awards = getOnlineAwards();
		
		List<OnlineRef> refs = new ArrayList<OnlineRef>();
		for (Entry<String, AwardData> entry : awards.entrySet()) {
			String refId = entry.getKey();
			AwardData value = entry.getValue();
			
			if (value.getState() == AwardState.Sure) {
				OnlineRef onlineRef = (OnlineRef)GameRoot.getGameRefObjectManager().getManagedObject(refId);
				refs.add(onlineRef);
			}
		}
		
		if (refs.isEmpty()) {
			ResultEvent.sendResult(player.getIdentity(), MGActivityEventDefines.C2G_getReward, MGErrorCode.CODE_Advanced_CantGetReward);
			return;
		}
		
		Collections.sort(refs);
		String refId = null;
		List<ItemPair> itemPairs = new ArrayList<ItemPair>();
		
		if (refs.size() > 0 && refs.get(0) != null) {
			OnlineRef onlineRef = refs.get(0);
			refId = onlineRef.getId();
			itemPairs = onlineRef.getItemPairs();
		}
		
		if (ItemFacade.isItemBagSlotEnough(player, itemPairs)) {
			ItemFacade.addItem(player, itemPairs, ItemOptSource.TotalOnline);
			
			ResultEvent.sendResult(player.getIdentity(), MGActivityEventDefines.C2G_getReward, MGSuccessCode.CODE_SUCCESS);

			changeAwardState(refId, AwardState.Received);
			
			sendOnlineStateMessage();
		} else {
			ResultEvent.sendResult(player.getIdentity(), MGActivityEventDefines.C2G_getReward, MGErrorCode.CODE_Activity_ItemBagIsFull);
		}
	}

	public synchronized String getCrtCumulativeTimeRefId() {
		return crtCumulativeTimeRefId;
	}

	public synchronized void setCrtCumulativeTimeRefId(String crtCumulativeTimeRefId) {
		this.crtCumulativeTimeRefId = crtCumulativeTimeRefId;
	}
	
	private synchronized void resetAccumulativeRefId() {
		crtCumulativeTimeRefId = first_CumulativeTimeRefId;
	}

	public synchronized Map<String, AwardData> getOnlineAwards() {
		return onlineAwards;
	}

	public synchronized void setOnlineAwards(Map<String, AwardData> onlineAwards) {
		this.onlineAwards = onlineAwards;
	}
	
	public String getTimeString() {
		return timeString;
	}

	public void setTimeString(String timeString) {
		this.timeString = timeString;
	}
	
	public int getLastOnlineTime() {
		return lastOnlineTime;
	}

	public void setLastOnlineTime(int lastOnlineTime) {
		this.lastOnlineTime = lastOnlineTime;
	}

	public void resetLastOnlineTime() {
		this.lastOnlineTime = 0;
	}
	
	public void resetStartMills(long millis) {
		this.startMillis = millis;
	}

	public void handleGameEvent(GameEvent<?> event) {
		if (event.isId(MGPlayerActivityComponent.EnterWorld_SceneReady_ID)) {
			
			resetStartMills(System.currentTimeMillis());
			
			String nowTimeString = DateTimeUtil.getTimeStringOfToday();
			if (!StringUtils.equals(nowTimeString, timeString)) {
				dailyReset();
			}
			
			sendOnlineStateMessage();
		} else if (event.isId(MGPlayerActivityComponent.LeaveWorld_GE_ID)) {
			this.lastOnlineTime = getOnlineTime();
			resetStartMills(0);
		}
	}

}
