package newbee.morningGlory.mmorpg.player.activity.limitTimeRank;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import newbee.morningGlory.code.MGErrorCode;
import newbee.morningGlory.code.MGSuccessCode;
import newbee.morningGlory.mmorpg.player.activity.constant.AwardData;
import newbee.morningGlory.mmorpg.player.activity.constant.AwardState;
import newbee.morningGlory.mmorpg.player.activity.limitTimeRank.event.G2C_LimitTimeRank_TimeOver;
import newbee.morningGlory.mmorpg.player.activity.limitTimeRank.event.MGLimitTimeDefines;
import newbee.morningGlory.mmorpg.player.activity.limitTimeRank.ref.LimitTimeRankRef;
import newbee.morningGlory.mmorpg.sortboard.SortboardData;
import newbee.morningGlory.mmorpg.sortboard.SortboardScoreData;
import newbee.morningGlory.mmorpg.sortboard.SortboardType;

import org.apache.log4j.Logger;
import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.MessageFactory;
import sophia.foundation.util.IoBufferUtil;
import sophia.game.GameRoot;
import sophia.mmorpg.code.MMORPGErrorCode;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.itemBag.ItemFacade;
import sophia.mmorpg.player.itemBag.ItemOptSource;
import sophia.mmorpg.player.itemBag.ItemPair;

public class LimitTimeRankMgr {
	private static Logger logger = Logger.getLogger(LimitTimeRankMgr.class);

	// <limitRankType, List<refId>
	private Map<Byte, List<String>> limitTimeDataMaps = new HashMap<Byte, List<String>>();

	// <limitRankType, yyyyMMddHHmmss>
	private Map<Byte, String> beginEndTimeMaps = new HashMap<Byte, String>();

	// <refId, AwardData>
	private Map<String, AwardData> rewardMaps = new HashMap<String, AwardData>();

	private Player owner;

	public boolean canGetRewardOfLimitRankType(byte limitRankType) {
		List<String> refIds = limitTimeDataMaps.get(limitRankType);
		boolean flag = false;
		for (String refId : refIds) {
			if (rewardMaps.get(refId).getState() == AwardState.Sure) {
				flag = true;
				break;
			}
		}
		return flag;
	}

	public boolean canGetReward() {
		boolean flag = false;
		for (LimitRankType limitRankType : LimitTimeActivityMgr.limitRankTypes) {
			if (LimitTimeActivityMgr.isTimeOver(limitRankType) && canGetRewardOfLimitRankType(limitRankType.value())) {
				flag = true;
			}
		}
		return flag;
	}

	public void writeRankListInfoToClient(byte type, IoBuffer buffer) {
		if (logger.isDebugEnabled()) {
			logger.debug("from client Type = " + type);
		}

		List<String> rankRefIdList = limitTimeDataMaps.get(type);

		if (null == rankRefIdList) {
			logger.error("error argument type = " + type);
			return;
		}

		String beginTime = LimitTimeActivityMgr.getBeginOrEndTime(type, LimitTimeActivityMgr.Begin);
		String endTime = LimitTimeActivityMgr.getBeginOrEndTime(type, LimitTimeActivityMgr.End);
		int myRanking = getRanking(type);
		IoBufferUtil.putString(buffer, beginTime);
		IoBufferUtil.putString(buffer, endTime);
		buffer.putInt(myRanking);

		short count = (short) rankRefIdList.size();

		buffer.putShort(count);
		
		for (int i = 0; i < count; i++) {
			String refId = rankRefIdList.get(i);
			LimitTimeRankRef limitRankRef = getLimitTimeRankRef(refId);
			String rankInterval = "";

			if (null != limitRankRef) {
				rankInterval = limitRankRef.getRankInterval();
			}

			String rankingName = getPlayerNameRankingInterval(type, rankInterval);
			boolean isInRankInterval = isInRankInterval(rankInterval, myRanking);
			if (isInRankInterval) {
				if (rewardMaps.get(refId).getState() != AwardState.Received) {
					resetState(type);
					rewardMaps.get(refId).setState(AwardState.Sure);
				}
			
			}
			else{
				rewardMaps.get(refId).setState(AwardState.Init);
			}
			
			IoBufferUtil.putString(buffer, refId);

			AwardData awardData = rewardMaps.get(refId);	
			if (null != awardData) {
				buffer.put(awardData.getState());
			} else {
				buffer.put(AwardState.Init);
			}
			
			IoBufferUtil.putString(buffer, rankingName);
		}
	}

	public void sendMsgToclient(byte openState) {
		G2C_LimitTimeRank_TimeOver res = (G2C_LimitTimeRank_TimeOver) MessageFactory.getMessage(MGLimitTimeDefines.G2C_LimitTimeRank_TimeOver);
		res.setOpenState(openState);
		GameRoot.sendMessage(owner.getIdentity(), res);
	}

	public int getReward(String refId) {
		LimitTimeRankRef ref = getLimitTimeRankRef(refId);
		if (null == ref) {
			logger.error("from client error refId = " + refId);
			return MGErrorCode.CODE_Advanced_CantGetReward;
		}

		byte rankType = ref.getRankType();
		int rank = getRanking(rankType);
		String rankInterval = ref.getRankInterval();
		if(!isInRankInterval(rankInterval, rank)){
			logger.error(refId+"is not match my rank ="+rank);
			return MGErrorCode.CODE_Advanced_CantGetReward;
		}
		
		List<ItemPair> itemPairs = getRewardItemPairs(refId);
		

		if (!LimitTimeActivityMgr.isTimeOver(LimitTimeActivityMgr.getLimitRankType(rankType))) {
			return MGErrorCode.CODE_LimitTimeRank_TimeNotOver;
		}

		if (rewardMaps.get(refId).getState() != AwardState.Sure) {
			return MGErrorCode.CODE_Advanced_CantGetReward;
		}
		if (ItemFacade.addItemCompareSlot(owner, itemPairs, ItemOptSource.LimitTimeRank).isOK()) {
		} else {
			return MMORPGErrorCode.CODE_ITEM_SLOT_ENOUGH;
		}
		return MGSuccessCode.CODE_SUCCESS;
	}

	public void changeAwardReceivedState(String refId) {
		rewardMaps.get(refId).setState(AwardState.Received);
	}

	private List<ItemPair> getRewardItemPairs(String refId) {
		List<ItemPair> itemPairs = new ArrayList<ItemPair>();
		LimitTimeRankRef ref = getLimitTimeRankRef(refId);
		if (ref != null) {
			itemPairs = ref.getItemPairs();
			if (ref.professionRewardMap().get(owner.getProfession()) != null) {
				itemPairs.addAll(ref.professionRewardMap().get(owner.getProfession()));
			}
		}

		return itemPairs;
	}

	private int getRanking(byte sortboardType) {
		return LimitTimeActivityMgr.getInstance().getRanking(LimitTimeActivityMgr.getSortboardType(sortboardType), owner);
	}

	private LimitTimeRankRef getLimitTimeRankRef(String refId) {
		LimitTimeRankRef limitTimeRankRef = (LimitTimeRankRef) GameRoot.getGameRefObjectManager().getManagedObject(refId);
		if (null == limitTimeRankRef) {
			logger.error("limitTimeRankRef is null! error refId = " + refId);
		}

		return limitTimeRankRef;
	}

	private boolean isInRankInterval(String rankInterval, int myRank) {
		String[] value = rankInterval.split("-");
		int value1 = Integer.parseInt(value[0]);
		int value2 = Integer.parseInt(value[1]);

		if (value.length != 2) {
			logger.error("data error");
			return false;
		}

		if (value1 == value2) {
			return value1 == myRank;
		}

		return myRank <= value2 && myRank >= value1;

	}

	private String getPlayerNameRankingInterval(byte type, String rankInterval) {
		SortboardType sortboardType = LimitTimeActivityMgr.getSortboardType(type);

		if (sortboardType == null) {
			logger.error("error type = " + type);
			return "";
		}

		SortboardData sortboardData = LimitTimeActivityMgr.getCopySBD().get(sortboardType.getSortboardType());

		if (sortboardData == null) {
			return "";
		}

		List<SortboardScoreData> scoreDatas = sortboardData.getScoreData();

		String[] value = rankInterval.split("-");
		int value1 = Integer.parseInt(value[0]);
		int value2 = Integer.parseInt(value[1]);

		if (scoreDatas == null || value1 != value2 || scoreDatas.size() < value1) {
			return "";
		}
		return scoreDatas.get(value1 - 1).getName();
	}

	public Map<Byte, List<String>> getLimitTimeDataMaps() {
		return limitTimeDataMaps;
	}

	public void setLimitTimeDataMaps(Map<Byte, List<String>> limitTimeDataMaps) {
		this.limitTimeDataMaps = limitTimeDataMaps;
	}

	public Player getOwner() {
		return owner;
	}

	public void setOwner(Player owner) {
		this.owner = owner;
	}

	public Map<String, AwardData> getRewardMaps() {
		return rewardMaps;
	}

	public void setRewardMaps(Map<String, AwardData> rewardMaps) {
		this.rewardMaps = rewardMaps;
	}

	public Map<Byte, String> getBeginEndTimeMaps() {
		return beginEndTimeMaps;
	}

	public void setBeginEndTimeMaps(Map<Byte, String> beginEndTimeMaps) {
		this.beginEndTimeMaps = beginEndTimeMaps;
	}

	/**
	 * 
	 * @param LimitRankType
	 * @return
	 */
	public byte getActivityOverState() {
		if (!isAllActivityTimeOver()) {
			return LimitTimeRankMacro.Can_Open;
		}

		if (!isGetAllAwardBysortBoardType()) {
			return LimitTimeRankMacro.Can_Open;
		}

		return LimitTimeRankMacro.Cant_Opne;
	}

	private boolean isGetAllAwardBysortBoardType() {
		for (Entry<String, AwardData> entry : rewardMaps.entrySet()) {
			if (entry.getValue().getState() == AwardState.Sure)
				return false;
		}
		return true;
	}

	private boolean isAllActivityTimeOver() {
		for (LimitRankType limitRankType : LimitTimeActivityMgr.limitRankTypes) {
			if (!LimitTimeActivityMgr.isTimeOver(limitRankType)) {
				return false;
			}
		}
		return true;
	}

	private void resetState(byte limitRankType) {
		List<String> refIdList = limitTimeDataMaps.get(limitRankType);

		for (String refId : refIdList) {
			AwardData awardData = rewardMaps.get(refId);
			if (awardData.getState() == AwardState.Sure) {
				awardData.setState(AwardState.Init);
			}

		}
	}

}
