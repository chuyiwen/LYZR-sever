package newbee.morningGlory.mmorpg.player.activity.mgr;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import newbee.morningGlory.MorningGloryContext;
import newbee.morningGlory.code.MGErrorCode;
import newbee.morningGlory.mmorpg.player.activity.constant.AwardData;
import newbee.morningGlory.mmorpg.player.activity.constant.AwardState;
import newbee.morningGlory.mmorpg.player.activity.constant.AwardTypeDefine;
import newbee.morningGlory.mmorpg.player.activity.constant.SignTypeDefine;
import newbee.morningGlory.mmorpg.player.activity.event.C2G_SignIn;
import newbee.morningGlory.mmorpg.player.activity.event.G2C_CangetReward;
import newbee.morningGlory.mmorpg.player.activity.event.G2C_SignIn;
import newbee.morningGlory.mmorpg.player.activity.event.G2C_SignIn_List;
import newbee.morningGlory.mmorpg.player.activity.event.G2C_getReward;
import newbee.morningGlory.mmorpg.player.activity.event.MGActivityEventDefines;
import newbee.morningGlory.mmorpg.player.activity.ref.SignRef;
import newbee.morningGlory.stat.MGStatFunctions;
import newbee.morningGlory.stat.logs.StatSign;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;
import sophia.foundation.communication.core.MessageFactory;
import sophia.foundation.util.IoBufferUtil;
import sophia.game.GameRoot;
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
import sophia.mmorpg.player.persistence.immediatelySave.PlayerImmediateDaoFacade;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;
import sophia.mmorpg.utils.DateTimeUtil;

public class SignMgr {
	private static Logger logger = Logger.getLogger(SignMgr.class);

	private Player owner;

	private List<Byte> signDateList = new ArrayList<Byte>();// date

	private Map<String, AwardData> accumulativeSignAwardMap = new HashMap<String, AwardData>();// <refId,AwardData>

	private static final int Makeup_Sign_unbindedGold = 50;

	private static final String EverydaySign_Award_RefId = "sign_1";

	private static final byte Normal_Sign_Retval = 0;

	SFTimerCreater timerCreater = MMORPGContext.getTimerCreater();

	private String signDateInfo;

	private SFTimer dayTimer;

	public SignMgr() {
		signDateInfo = "yyyyMMdd";
		
	}
	
	public void startTimer() {
		dayTimer = timerCreater.calendarChime(new SFTimeChimeListener() {
			
			@Override
			public void handleTimeChimeCancel() {
			}
			
			@Override
			public void handleTimeChime() {
				clearIfDiffMonth();
				sendSignListToClient();
			}
			
			@Override
			public void handleServiceShutdown() {
			}
		}, SFTimeUnit.HOUR, 0);
		
	}
	
	public void cancelTimer(){
		if(dayTimer != null) {
			dayTimer.cancel();
		}
	}
	
	private void clearIfDiffMonth(){
		if(logger.isDebugEnabled()) {
			logger.debug("sign time = " + getSignDateInfo().substring(0, 6) + ", crt Time = " + getCalenderInfo().substring(0, 6));
		}
		if (!StringUtils.equals(getSignDateInfo().substring(0, 6), getCalenderInfo().substring(0, 6))) {
			resetSignInfo();
		}
	}
	
	private void sendSignListToClient(){
		G2C_SignIn_List res = MessageFactory.getConcreteMessage(MGActivityEventDefines.G2C_SignIn_List);
		res.setSignMgr(this);
		GameRoot.sendMessage(owner.getIdentity(), res);
	}

	public void sign(C2G_SignIn event) {
		byte type = event.getSingType();
		
		if (type != SignTypeDefine.Normal_Sign && type != SignTypeDefine.MakeUp_Sign) {
			logger.error("error argument! type = " + type);
			return;
		}
		
		
		if (type == SignTypeDefine.Normal_Sign) {
			nomalSign(event);
		} else if (type == SignTypeDefine.MakeUp_Sign) {
			makeupSign(event);
		} 
		signDateInfo = getCalenderInfo();
		sendMsgOfReachSignCount();
		
		PlayerImmediateDaoFacade.update(owner);
	}

	private void sendMsgOfReachSignCount() {
		String refId = getTagertRefId();
		if (logger.isDebugEnabled()) {
			logger.debug("目标次数:" + getNextTargetSignCount(refId) + ":签到次数" + getSignCount());
		}
		if (getNextTargetSignCount(refId) != -1 && getNextTargetSignCount(refId) <= getSignCount()) {
			setState(refId, AwardState.Sure);
			G2C_CangetReward res = (G2C_CangetReward) MessageFactory.getMessage(MGActivityEventDefines.G2C_CangetReward);
			res.setType(AwardTypeDefine.RewardType_Singin);
			res.setRefId(refId);
			GameRoot.sendMessage(owner.getIdentity(), res);
			logger.debug("达到条件");
		}
	}

	private byte getNextTargetSignCount(String refId) {
		if (refId == null) {
			return -1;
		}
		SignRef ref = (SignRef) GameRoot.getGameRefObjectManager().getManagedObject(refId);

		return MGPropertyAccesser.getSignCount(ref.getProperty());
	}

	private String getTagertRefId() {
		List<String> refList = new ArrayList<String>();
		for (Entry<String, AwardData> entry : accumulativeSignAwardMap.entrySet()) {
			if (entry.getValue().getState() == AwardState.Init) {
				refList.add(entry.getKey());
			}
		}

		if (refList.size() == 0)
			return null;
		Collections.sort(refList, new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				return o1.compareTo(o2);
			}

		});
		return refList.get(0);
	}

	private void nomalSign(C2G_SignIn event) {
		byte today = getDateOfToday();

		if (0 == canNormalSign()) {
			sendErrorMsgToClient(event, MGErrorCode.CODE_Sign_RepeatSameDay);
			return;
		}

		if (!addAward()) {
			sendErrorMsgToClient(event, MGErrorCode.CODE_Award_BagAlreadyFull);
			return;
		}

		if (logger.isDebugEnabled()) {
			logger.debug("正常签到:今天是" + getDateOfToday() + "号");
		}

		if(today == (byte)1) {
			resetSignInfo();
		}
		
		addSignday(today);

		sendSignMsgToClient(Normal_Sign_Retval);
		
		MGStatFunctions.signStat(owner, StatSign.Normal_Sign, today, getSignCount());
	}

	public void getReward(String refId) {
		if (!accumulativeSignAwardMap.containsKey(refId)) {
			logger.error("error argument! refId = " + refId);
			return;
		}
		
		if (!canGetReward(refId)) {
			return;
		}

		List<ItemPair> itemPairs = getRewardItemPairs(refId);
		if (ItemFacade.isItemBagSlotEnough(owner, itemPairs)) {
			ItemFacade.addItem(owner, itemPairs, ItemOptSource.Sign);
			setState(refId, AwardState.Received);
			G2C_getReward res = (G2C_getReward) MessageFactory.getMessage(MGActivityEventDefines.G2C_getReward);
			res.setRefId(refId);
			GameRoot.sendMessage(owner.getIdentity(), res);
		} else {
			ResultEvent.sendResult(owner.getIdentity(), MGActivityEventDefines.C2G_getReward, MMORPGErrorCode.CODE_ITEM_SLOT_ENOUGH);
		}
	}

	private boolean canGetReward(String refId) {
		return getState(refId) == AwardState.Sure;
	}

	private boolean addAward() {
		List<ItemPair> itemPairs = getRewardItemPairs(EverydaySign_Award_RefId);
		if (ItemFacade.addItemCompareSlot(owner, itemPairs,ItemOptSource.Sign).isOK()) {
			return true;
		} else {
			return false;
		}
	}

	private void makeupSign(C2G_SignIn event) {
		List<Byte> makeupDays = getMakeupDay();
		Collections.sort(makeupDays);

		boolean flag = false;
		for (Byte makeupDay : makeupDays) {
			if (isValidTime(MorningGloryContext.getServerOpenTime(), makeupDay)) {
				if (0 == makeupDay) {
					sendErrorMsgToClient(event, MGErrorCode.CODE_Sign_NONeedSign);
					return;
				}

				
				if (!owner.getPlayerMoneyComponent().subUnbindGold(Makeup_Sign_unbindedGold,ItemOptSource.Sign)) {
					sendErrorMsgToClient(event, MGErrorCode.CODE_Sign_NOT_ENOUGHT_GOLD);
					return;
				}
				
				if (!addAward()) {
					owner.getPlayerMoneyComponent().addUnbindGold(Makeup_Sign_unbindedGold, ItemOptSource.Sign);
					sendErrorMsgToClient(event, MMORPGErrorCode.CODE_ITEM_SLOT_ENOUGH);
					return;
				}

				if (logger.isDebugEnabled()) {
					logger.debug("补签:今天是" + makeupDay + "号");
				}

				signDateList.add(makeupDay);

				sendSignMsgToClient(makeupDay);

				flag = true;
				
				MGStatFunctions.signStat(owner, StatSign.Makeup_Sign, makeupDay, getSignCount());
				
				break;
			}
		}

		if (!flag) {
			sendErrorMsgToClient(event, MGErrorCode.CODE_Sign_ValidTime);
		}
	}

	private boolean isValidTime(long timeStamp, byte makeupDay) {
		// yyyy.MM.dd
		String openServerDateString = DateTimeUtil.getDateString(timeStamp);
		String birthDayString = DateTimeUtil.getDateString(MGPropertyAccesser.getBirthday(owner.getProperty()));

		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(System.currentTimeMillis());
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH) + 1;

		String makeupMonthString = month < 10 ? "0" + month : "" + month;
		String makeupDayString = makeupDay < 10 ? "0" + makeupDay : "" + makeupDay;
		String makeupTimeString = String.format("%1$d.%2$s.%3$s", year, makeupMonthString, makeupDayString);

		if (makeupTimeString.compareTo(openServerDateString) < 0 || makeupTimeString.compareTo(birthDayString) < 0) {
			return false;
		}

		return true;
	}

	private boolean isTheSameDay(byte finalSignDate, byte today) {
		return finalSignDate == today;
	}

	private int canNormalSign() {
		byte finalSignDate = getFinalSignDate();
		byte today = getDateOfToday();

		return isTheSameDay(finalSignDate, today) ? 0 : 1;
	}


	private int canMakeupSign() {
		int today = getDateOfToday();
		for(byte i =1;i< today; i++) {
			if(signDateList.contains(i)){
				continue;
			}
			if(isValidTime(MorningGloryContext.getServerOpenTime(), i)){
				return 1;
			}
		}
		return 0;
	}

	private byte getFinalSignDate() {
		if (signDateList.size() == 0) {
			return 0;
		}
		sort();
		return signDateList.get(signDateList.size() - 1);
	}
	

	private void addSignday(byte signDate) {
		signDateList.add(signDate);
	}

	public List<Byte> getMakeupDay() {
		List<Byte> makeupDays = new ArrayList<Byte>();
		byte today = getDateOfToday();
		for (byte i = 1; i < today; i++) {
			if (!signDateList.contains(i)) {
				makeupDays.add(i);
			}
		}
		return makeupDays;
	}

	private List<ItemPair> getRewardItemPairs(String refId) {
		SignRef ref = (SignRef) GameRoot.getGameRefObjectManager().getManagedObject(refId);
		List<ItemPair> itemPairs = ref.getItemPairs();
		if (ref.getMap().get(owner.getProfession()) != null) {
			itemPairs.addAll(ref.getMap().get(owner.getProfession()));
		}
		return itemPairs;
	}

	public byte getDaysOfMonth(long millisTime) {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(millisTime);

		return (byte) c.getActualMaximum(Calendar.DAY_OF_MONTH);
	}

	private void sendErrorMsgToClient(ActionEventBase event, int code) {
		ResultEvent.sendResult(owner.getIdentity(), event.getActionEventId(), code);
	}

	private void sendSignMsgToClient(byte day) {
		G2C_SignIn res = (G2C_SignIn) MessageFactory.getMessage(MGActivityEventDefines.G2C_SignIn);
		res.setDay(day);
		GameRoot.sendMessage(owner.getIdentity(), res);
	}

	public void writeSignListInfo(IoBuffer buffer) {
		IoBufferUtil.putString(buffer, getCalenderInfo());
		byte daysOfMonth = getDaysOfMonth(System.currentTimeMillis());
		byte count = getSignCount();
		byte canNormalSign = (byte) canNormalSign();
		byte canMakeupSign = (byte) canMakeupSign();
		buffer.put(daysOfMonth);
		buffer.put(canNormalSign);
		buffer.put(canMakeupSign);
		buffer.put(count);

		for (byte i = 0; i < signDateList.size(); i++) {
			buffer.put(signDateList.get(i));
		}

		buffer.put((byte) accumulativeSignAwardMap.size());
		for (Entry<String, AwardData> entry : accumulativeSignAwardMap.entrySet()) {
			IoBufferUtil.putString(buffer, entry.getKey());
			buffer.put(entry.getValue().getState());
			if (logger.isDebugEnabled()) {
				logger.debug("累计签到refId:" + entry.getKey() + "," + "领奖状态:" + entry.getValue().getState());
			}
		}
	}

	public String getCalenderInfo() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		return sdf.format(new Date(System.currentTimeMillis()));
	}

	public Player getOwner() {
		return owner;
	}

	public void setOwner(Player owner) {
		this.owner = owner;
	}

	public Map<String, AwardData> getAccumulativeSignAwardMap() {
		return accumulativeSignAwardMap;
	}

	public void setAccumulativeSignAwardMap(Map<String, AwardData> AccumulativeSignAwardMap) {
		this.accumulativeSignAwardMap = AccumulativeSignAwardMap;
	}

	private byte getSignCount() {
		return (byte) signDateList.size();
	}

	public void resetSignInfo() {
		if(signDateList.isEmpty()) {
			return;
		}
		
		signDateList.clear();
		for (Entry<String, AwardData> entry : accumulativeSignAwardMap.entrySet()) {
			entry.getValue().setState(AwardState.Init);
		}
	}

	public List<Byte> getSignDataList() {
		return signDateList;
	}

	public void setSignDataList(List<Byte> signDataList) {
		this.signDateList = signDataList;
	}

	private byte getDateOfToday() {
		return getDateOfDay(System.currentTimeMillis());
	}

	private byte getDateOfDay(long time) {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(time);
		return (byte) c.get(Calendar.DAY_OF_MONTH);
	}

	public String getSignDateInfo() {
		return signDateInfo;
	}

	public void setSignDateInfo(String signDateInfo) {
		this.signDateInfo = signDateInfo;
	}

	private byte getState(String refId) {
		return accumulativeSignAwardMap.get(refId).getState();
	}

	private void setState(String refId, byte state) {
		accumulativeSignAwardMap.get(refId).setState(state);
	}

	private void sort() {
		Collections.sort(signDateList, new Comparator<Byte>() {
			@Override
			public int compare(Byte o1, Byte o2) {
				return o1 - o2;
			}

		});
	}
}
