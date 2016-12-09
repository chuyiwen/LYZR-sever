package newbee.morningGlory.mmorpg.player.activity.mgr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import newbee.morningGlory.MorningGloryContext;
import newbee.morningGlory.code.MGErrorCode;
import newbee.morningGlory.code.MGSuccessCode;
import newbee.morningGlory.mmorpg.player.activity.MGPlayerActivityComponent;
import newbee.morningGlory.mmorpg.player.activity.constant.AwardData;
import newbee.morningGlory.mmorpg.player.activity.constant.AwardState;
import newbee.morningGlory.mmorpg.player.activity.constant.AwardTypeDefine;
import newbee.morningGlory.mmorpg.player.activity.event.G2C_CangetReward;
import newbee.morningGlory.mmorpg.player.activity.event.G2C_LevelUp_ActivityOver;
import newbee.morningGlory.mmorpg.player.activity.event.MGActivityEventDefines;
import newbee.morningGlory.mmorpg.player.activity.ref.LevelUpRewardRef;

import org.apache.log4j.Logger;
import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.MessageFactory;
import sophia.foundation.util.IoBufferUtil;
import sophia.game.GameRoot;
import sophia.game.component.communication.GameEvent;
import sophia.mmorpg.MMORPGContext;
import sophia.mmorpg.Mail.Mail;
import sophia.mmorpg.Mail.MailMgr;
import sophia.mmorpg.code.MMORPGErrorCode;
import sophia.mmorpg.core.timer.SFTimeChimeListener;
import sophia.mmorpg.core.timer.SFTimer;
import sophia.mmorpg.core.timer.SFTimerCreater;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.exp.gameevent.PlayerLevelUp_GE;
import sophia.mmorpg.player.itemBag.ItemFacade;
import sophia.mmorpg.player.itemBag.ItemOptSource;
import sophia.mmorpg.player.itemBag.ItemPair;

import com.google.common.base.Strings;
import com.google.gson.Gson;

public class LevelUpMgr {
	private static Logger logger = Logger.getLogger(LevelUpMgr.class);
	//private long serverBeginTime = System.currentTimeMillis();
	private static final int Default_Activity_Day = 7;
	private int Activity_Continue_Day = Default_Activity_Day;
	private Map<String, AwardData> levelUpMaps = new HashMap<String, AwardData>();
	private static final String first_levelUpReward_RefId = "levelUpReward_1";
	private Player owner;
	private SFTimer timer;

	public LevelUpMgr() {
	}

	public void startTimer() {
		SFTimerCreater timerCreater = MMORPGContext.getTimerCreater();
		timer = timerCreater.secondInterval(new SFTimeChimeListener() {
			@Override
			public void handleServiceShutdown() {

			}

			@Override
			public void handleTimeChimeCancel() {

			}

			@Override
			public void handleTimeChime() {
				isActivityOver();
			}

		});
	}

	public void cancelTimer() {
		if (null != timer) {
			timer.cancel();
		}
	}

	private String crtLevelUpRewardRefId = first_levelUpReward_RefId;

	public boolean isActivityOver() {
		if (getRemainTime() <= 0) {
			sendActivityOver();
			//addAllReward();
			return true;
		}
		return false;
	}

	private void addAllReward() {
		List<ItemPair> itemPairs = new ArrayList<ItemPair>();
		for (Entry<String, AwardData> entry : levelUpMaps.entrySet()) {
			if (entry.getValue().getState() == AwardState.Sure) {
				itemPairs.addAll(getRewardItemPairs(entry.getKey()));
			}
		}
		if (itemPairs.isEmpty()) {
			return;
		}
		String jsonString = (new Gson()).toJson(itemPairs);
		String content = "活动结束,升级奖励通过邮件发送";
		MailMgr.sendMailById(owner.getId(), content, Mail.huodong, jsonString, 0, 0, 0);
		
		if (logger.isDebugEnabled()) {
			logger.debug("开服冲级活动结束，活动奖励通过邮件发送...");
		}
	}

	private void sendActivityOver() {
		G2C_LevelUp_ActivityOver res = (G2C_LevelUp_ActivityOver) MessageFactory.getMessage(MGActivityEventDefines.G2C_LevelUp_ActivityOver);
		GameRoot.sendMessage(owner.getIdentity(), res);
	}

	public boolean canGetReward() {
		boolean flag = false;
		for (Entry<String, AwardData> entry : levelUpMaps.entrySet()) {
			if (entry.getValue().getState() == AwardState.Sure) {
				flag = true;
				break;
			}
		}
		return flag;
	}

	public int getReward(String refId) {
		if (!levelUpMaps.containsKey(refId)) {
			logger.error("error argument! refId = " + refId);
			return MGErrorCode.CODE_Award_DataErorr;
		}
		
		if (!isSureAwardState(refId)) {
			return MGErrorCode.CODE_Advanced_CantGetReward;
		}

		if (ItemFacade.addItemCompareSlot(owner, getRewardItemPairs(refId), ItemOptSource.LevelUp).isOK()) {
			return MGSuccessCode.CODE_SUCCESS;
		} else {
			return MMORPGErrorCode.CODE_ITEM_SLOT_ENOUGH;
		}

	}

	private boolean isSureAwardState(String refId) {
		return levelUpMaps.get(refId).getState() == AwardState.Sure;
	}

	private List<ItemPair> getRewardItemPairs(String refId) {
		if (logger.isDebugEnabled()) {
			logger.debug("giftRefId =" + refId);
		}
		if (Strings.isNullOrEmpty(refId)) {
			return new ArrayList<ItemPair>();
		}
		LevelUpRewardRef ref = (LevelUpRewardRef) GameRoot.getGameRefObjectManager().getManagedObject(refId);

		return ref.getItemPairs();
	}

	public void handleGameEvent(GameEvent<?> event) {
		logger.debug(event.getId());
		if (event.isId(MGPlayerActivityComponent.PlayerLevelUp_GE_Id)) {
			PlayerLevelUp_GE ge = (PlayerLevelUp_GE) event.getData();
			while (!isActivityOver() && canGetLevelUpReward(ge.getCurLevel())) {
				
				AwardData awardData = levelUpMaps.get(crtLevelUpRewardRefId);
				if (awardData !=null && awardData.getState() == AwardState.Init) {
					sendMsgToClient(crtLevelUpRewardRefId);
					changeRewardState(crtLevelUpRewardRefId, AwardState.Sure);
					resetCrtLevelRewardRefId();
				}
			}
		} else if (event.isId(MGPlayerActivityComponent.EnterWorld_SceneReady_ID)) {
			isActivityOver();
		}
	}

	public void changeRewardState(String refId, byte state) {
		AwardData awardData = null;
		if (levelUpMaps.get(refId) != null) {
			awardData = levelUpMaps.get(refId);
			awardData.setState(state);
		}
	}

	private void sendMsgToClient(String refId) {
		G2C_CangetReward res = (G2C_CangetReward) MessageFactory.getMessage(MGActivityEventDefines.G2C_CangetReward);
		res.setType(AwardTypeDefine.RewardType_LevelUp);
		res.setRefId(refId);
		GameRoot.sendMessage(owner.getIdentity(), res);
	}

	private void resetCrtLevelRewardRefId() {
		LevelUpRewardRef crtLevelRef = (LevelUpRewardRef) GameRoot.getGameRefObjectManager().getManagedObject(crtLevelUpRewardRefId);
		LevelUpRewardRef nextLevelRef = crtLevelRef.getLevelUpRewardNextRef();
		crtLevelUpRewardRefId = nextLevelRef == null ? "" : nextLevelRef.getId();
	}

	private boolean canGetLevelUpReward(int crtLevel) {
		// if (getRemainTime() <= 0) {
		// return false;
		// }
		LevelUpRewardRef crtRef = (LevelUpRewardRef) GameRoot.getGameRefObjectManager().getManagedObject(crtLevelUpRewardRefId);
		if (crtRef == null) {
			return false;
		}
		if (logger.isDebugEnabled()) {
			logger.debug("needLevel : " + crtRef.getCurNeedLevel() + "," + "curLevel:" + crtLevel);
		}
		return crtLevel >= crtRef.getCurNeedLevel();
	}

	public void writeInfoToClient(IoBuffer buffer) {
		buffer.putInt(getRemainTime());
		if (logger.isDebugEnabled()) {
			logger.debug("remainTime  = " + getRemainTime());
		}
		buffer.put((byte) levelUpMaps.size());
		for (Entry<String, AwardData> entry : levelUpMaps.entrySet()) {
			IoBufferUtil.putString(buffer, entry.getKey());
			buffer.put(entry.getValue().getState());
			if (logger.isDebugEnabled()) {
				logger.debug("升级" + entry.getKey() + ":" + entry.getValue().getState());
			}
		}
	}

	public int getRemainTime() {
		long serverBeginTime = MorningGloryContext.getServerOpenTime();
		int remainTime = 3600 * 24 * Activity_Continue_Day - (int) ((System.currentTimeMillis() - serverBeginTime) / 1000);
		remainTime = remainTime <= 0 ? 0 : remainTime;
		return remainTime;
	}

//	public long getServerBeginTime() {
//		return serverBeginTime;
//	}
//
//	public void setServerBeginTime(long serverBeginTime) {
//		this.serverBeginTime = serverBeginTime;
//	}

	public Player getOwner() {
		return owner;
	}

	public void setOwner(Player owner) {
		this.owner = owner;
	}

	public Map<String, AwardData> getLevelUpMaps() {
		return levelUpMaps;
	}

	public void setLevelUpMaps(Map<String, AwardData> levelUpMaps) {
		this.levelUpMaps = levelUpMaps;
	}

	public String getCrtLevelUpRewardRefId() {
		return crtLevelUpRewardRefId;
	}

	public void setCrtLevelUpRewardRefId(String crtLevelUpRewardRefId) {
		this.crtLevelUpRewardRefId = crtLevelUpRewardRefId;
	}

}
