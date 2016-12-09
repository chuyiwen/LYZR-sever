package newbee.morningGlory.mmorpg.player.activity.mgr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import newbee.morningGlory.code.MGErrorCode;
import newbee.morningGlory.code.MGSuccessCode;
import newbee.morningGlory.mmorpg.player.activity.MGPlayerActivityComponent;
import newbee.morningGlory.mmorpg.player.activity.constant.ActivityData;
import newbee.morningGlory.mmorpg.player.activity.constant.AwardData;
import newbee.morningGlory.mmorpg.player.activity.constant.AwardState;
import newbee.morningGlory.mmorpg.player.activity.constant.AwardTypeDefine;
import newbee.morningGlory.mmorpg.player.activity.constant.TalismanRewardTargetCondition;
import newbee.morningGlory.mmorpg.player.activity.event.G2C_CangetReward;
import newbee.morningGlory.mmorpg.player.activity.event.MGActivityEventDefines;
import newbee.morningGlory.mmorpg.player.activity.ref.RideRewardRef;
import newbee.morningGlory.mmorpg.player.activity.ref.TalisRewardRef;
import newbee.morningGlory.mmorpg.player.activity.ref.WingRewardRef;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.MessageFactory;
import sophia.foundation.util.IoBufferUtil;
import sophia.game.GameRoot;
import sophia.game.component.communication.GameEvent;
import sophia.game.ref.GameRefObject;
import sophia.mmorpg.code.MMORPGErrorCode;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.itemBag.ItemFacade;
import sophia.mmorpg.player.itemBag.ItemOptSource;
import sophia.mmorpg.player.itemBag.ItemPair;
import sophia.mmorpg.player.mount.Mount;
import sophia.mmorpg.player.mount.MountRef;
import sophia.mmorpg.player.mount.gameEvent.MGMountLevelUp_GE;

import com.google.common.base.Strings;

public class AdvancedMgr {
	private static Logger logger = Logger.getLogger(AdvancedMgr.class);
	private Player owner;
	private Map<String, AwardData> rideAwardMaps = new HashMap<String, AwardData>();
	private Map<String, AwardData> wingAwardMaps = new HashMap<String, AwardData>();
	private Map<String, AwardData> talisAwardMaps = new HashMap<String, AwardData>();

	private static final String first_WingReward_RefId = "wingReward_1";
	private static final String first_RideReward_RefId = "rideReward_1";
	private String crtWingRewardRefId = first_WingReward_RefId;
	private String crtRideRewardRefId = first_RideReward_RefId;

	public AdvancedMgr() {

	}

	public boolean canGetReward() {
		boolean flag = false;
		for (Entry<String, AwardData> entry : rideAwardMaps.entrySet()) {
			if (entry.getValue().getState() == AwardState.Sure) {
				flag = true;
				break;
			}
		}

		// for (Entry<String, AwardData> entry : wingAwardMaps.entrySet()) {
		// if (entry.getValue().getState() == AwardState.Sure) {
		// flag = true;
		// break;
		// }
		// }

		return flag;
	}

	public int getReward(String refId) {
		if (!rideAwardMaps.containsKey(refId)) {
			logger.error("error argument! refId = " + refId);
			return MGErrorCode.CODE_Award_DataErorr;
		}
		
		if (!isSureAwardState(refId)) {
			if (isReceivedState(refId)) {
				return MGErrorCode.CODE_Advanced_AlreadyGet;
			}
			return MGErrorCode.CODE_Advanced_CantGetReward;
		}
		
		if (ItemFacade.addItemCompareSlot(owner, getRewardItemPairs(refId), ItemOptSource.Advanced).isOK()) {
			return MGSuccessCode.CODE_SUCCESS;
		} else {
			return MMORPGErrorCode.CODE_ITEM_SLOT_ENOUGH;
		}
	}

	private List<ItemPair> getRewardItemPairs(String refId) {
		if (logger.isDebugEnabled()) {
			logger.debug("giftRefId =" + refId);
		}
		List<ItemPair> itemPairs = new ArrayList<ItemPair>();
		if (Strings.isNullOrEmpty(refId)) {
			return itemPairs;
		}
		GameRefObject refObject = GameRoot.getGameRefObjectManager().getManagedObject(refId);

		if (refObject instanceof RideRewardRef) {
			RideRewardRef ref = (RideRewardRef) refObject;
			itemPairs = ref.getItemPairs();
		} else if (refObject instanceof WingRewardRef) {
			WingRewardRef ref = (WingRewardRef) refObject;
			itemPairs = ref.getItemPairs();
		} else if (refObject instanceof TalisRewardRef) {
			TalisRewardRef ref = (TalisRewardRef) refObject;
			itemPairs = ref.getItemPairs();
		}
		return itemPairs;
	}

	public void writeInfoToClient(IoBuffer buffer) {
		byte rideCount = (byte) rideAwardMaps.size();
		buffer.put(rideCount);
		for (Entry<String, AwardData> entry : rideAwardMaps.entrySet()) {
			IoBufferUtil.putString(buffer, entry.getKey());
			buffer.put(entry.getValue().getState());
		}

		/*
		 * short wingCount = (short) wingAwardMaps.size();
		 * buffer.putShort(wingCount); for (Entry<String, AwardData> entry :
		 * wingAwardMaps.entrySet()) { IoBufferUtil.putString(buffer,
		 * entry.getKey()); buffer.put(entry.getValue().getState()); }
		 */

		/*
		 * short talisCount = (short) talisAwardMaps.size();
		 * buffer.putShort(talisCount); for (Entry<String, AwardData> entry :
		 * talisAwardMaps.entrySet()) { IoBufferUtil.putString(buffer,
		 * entry.getKey()); buffer.put(entry.getValue().getState()); }
		 */
		if (logger.isDebugEnabled()) {
			logger.debug("rideCount = " + rideCount);
			// logger.debug("wingCount = " + wingCount);
			// logger.debug("talisCount = " + talisCount);
		}
	}

	public void handleGameEvent(GameEvent<?> event) {
		if (event.isId(MGPlayerActivityComponent.MOUNT_GE_ID)) {
			MGMountLevelUp_GE ge = (MGMountLevelUp_GE) event.getData();
			String rideRefId = ge.getMountRefId();
			if (canGetRideReward(rideRefId)) {
				changeRewardState(crtRideRewardRefId, AwardState.Sure);
				sendMsgToClient(crtRideRewardRefId);
				resetCrtRideRewardRefId();
			}
		} else if (event.isId(MGPlayerActivityComponent.EnterWorld_SceneReady_ID)) {
			Mount crtMount = owner.getPlayerMountComponent().getMountManager().getCrtMount();
			if (crtMount != null) {
				MountRef crtMountRef = crtMount.getMountRef(); 
				if (crtMountRef == null) {
					return;
				}
				String crtMountRefId = crtMountRef.getId();
				
				while (canGetRideReward(crtMountRefId)) {
					changeRewardState(crtRideRewardRefId, AwardState.Sure);
					sendMsgToClient(crtRideRewardRefId);
					resetCrtRideRewardRefId();
				}
			}
		}
		
		/*
		 * else if (event.isId(MGPlayerActivityComponent.WING_GE_ID)) {
		 * MGWingLevelUp_GE ge = (MGWingLevelUp_GE) event.getData(); if
		 * (canGetWingReward(ge.getWingLevel())) {
		 * changeRewardState(crtWingRewardRefId, AwardState.Sure);
		 * sendMsgToClient(crtWingRewardRefId); resetCrtWingRewardRefId(); } }
		 */
		/*
		 * else if (event.isId(MGPlayerActivityComponent.TalismanLevleUp_GE_Id))
		 * { TalismanLevelUp ge = (TalismanLevelUp) event.getData();
		 * 
		 * for (Entry<Integer, Integer> entry : ge.getTalisMap().entrySet()) {
		 * logger.debug("实际拥有的法宝等级:" + entry.getKey() + ":" + "实际拥有的相应的个数:" +
		 * entry.getValue()); } List<String> refIdList =
		 * canGetTalisReward(ge.getTalisMap()); for (String refId : refIdList) {
		 * changeRewardState(refId, AwardState.Sure); sendMsgToClient(refId); }
		 * 
		 * }
		 */
	}

	private boolean isSureAwardState(String refId) {
		byte state = -1;
		if (rideAwardMaps.get(refId) != null) {
			state = rideAwardMaps.get(refId).getState();
		}
		/*
		 * else if (wingAwardMaps.get(refId) != null) { state =
		 * wingAwardMaps.get(refId).getState(); } else if
		 * (talisAwardMaps.get(refId) != null) { state =
		 * talisAwardMaps.get(refId).getState(); }
		 */
		return state == AwardState.Sure;
	}

	private boolean isReceivedState(String refId) {
		byte state = -1;
		if (rideAwardMaps.get(refId) != null) {
			state = rideAwardMaps.get(refId).getState();
		}
		return state == AwardState.Received;
	}

	private boolean canGetWingReward(int wingLevel) {
		if (StringUtils.equals("", crtWingRewardRefId)) {
			return false;
		}

		WingRewardRef wingRef = (WingRewardRef) GameRoot.getGameRefObjectManager().getManagedObject(crtWingRewardRefId);
		if (logger.isDebugEnabled()) {
			logger.debug("翅膀要求等级:" + wingRef.getWingLevel());
		}
		if (wingRef.getWingLevel() <= wingLevel) {
			return true;
		}

		return false;
	}

	private boolean canGetRideReward(String rideRefId) {
		if (StringUtils.equals("", crtRideRewardRefId)) {
			return false;
		}

		MountRef mountRef = (MountRef) GameRoot.getGameRefObjectManager().getManagedObject(rideRefId);

		if (mountRef == null) {
			logger.error("error mountRefId : " + rideRefId);
			return false;
		}
		RideRewardRef rideRewardRef = (RideRewardRef) GameRoot.getGameRefObjectManager().getManagedObject(crtRideRewardRefId);
		if (logger.isDebugEnabled()) {
			logger.debug("crtRideRewardRefId = " + crtRideRewardRefId + ":" + "rideRefId = " + rideRefId);
			logger.debug(mountRef.getStageLevel() + ":" + rideRewardRef.getRewardRideStage());
		}

		return mountRef.getStageLevel() >= rideRewardRef.getRewardRideStage();
	}

	private List<String> canGetTalisReward(Map<Integer, Integer> talisMap) {
		List<String> canGetRewardRefIdList = new ArrayList<String>();
		for (Entry<String, TalismanRewardTargetCondition> entry : ActivityData.getTalisConditionMap().entrySet()) {
			String talisRefId = entry.getKey();
			int targetLevel = entry.getValue().getLevel();
			int targetNumber = entry.getValue().getNumber();

			if (talisAwardMaps.get(talisRefId).getState() != AwardState.Init) {
				continue;
			}

			if (talisMap.get(targetLevel) == null) {
				continue;
			}

			int totalNumber = 0;
			for (Entry<Integer, Integer> talisMapEntry : talisMap.entrySet()) {
				if (talisMapEntry.getKey() >= targetLevel) {
					totalNumber += talisMapEntry.getValue();
				}
			}

			if (totalNumber >= targetNumber) {
				canGetRewardRefIdList.add(talisRefId);
			}
		}
		return canGetRewardRefIdList;
	}

	private void resetCrtWingRewardRefId() {
		WingRewardRef wingRef = (WingRewardRef) GameRoot.getGameRefObjectManager().getManagedObject(crtWingRewardRefId);
		WingRewardRef nextWingRef = (WingRewardRef) wingRef.getNextWingRewardRef();
		crtWingRewardRefId = nextWingRef == null ? "" : nextWingRef.getId();
	}

	private void resetCrtRideRewardRefId() {
		RideRewardRef rideRef = (RideRewardRef) GameRoot.getGameRefObjectManager().getManagedObject(crtRideRewardRefId);
		RideRewardRef nextRideRef = (RideRewardRef) rideRef.getNextRideRewardRef();
		crtRideRewardRefId = nextRideRef == null ? "" : nextRideRef.getId();
	}

	public void changeRewardState(String refId, byte state) {
		AwardData awardData = null;
		if (rideAwardMaps.get(refId) != null) {
			awardData = rideAwardMaps.get(refId);
			awardData.setState(state);
		} else if (wingAwardMaps.get(refId) != null) {
			awardData = wingAwardMaps.get(refId);
			awardData.setState(state);
		} else if (talisAwardMaps.get(refId) != null) {
			awardData = talisAwardMaps.get(refId);
			awardData.setState(state);
		}

	}

	private void sendMsgToClient(String refId) {
		G2C_CangetReward res = (G2C_CangetReward) MessageFactory.getMessage(MGActivityEventDefines.G2C_CangetReward);
		res.setType(AwardTypeDefine.RewardType_Advanced);
		res.setRefId(refId);
		GameRoot.sendMessage(owner.getIdentity(), res);
	}

	public Player getOwner() {
		return owner;
	}

	public void setOwner(Player owner) {
		this.owner = owner;
	}

	public Map<String, AwardData> getRideAwardMaps() {
		return rideAwardMaps;
	}

	public void setRideAwardMaps(Map<String, AwardData> rideAwardMaps) {
		this.rideAwardMaps = rideAwardMaps;
	}

	public Map<String, AwardData> getWingAwardMaps() {
		return wingAwardMaps;
	}

	public void setWingAwardMaps(Map<String, AwardData> wingAwardMaps) {
		this.wingAwardMaps = wingAwardMaps;
	}

	public Map<String, AwardData> getTalisAwardMaps() {
		return talisAwardMaps;
	}

	public void setTalisAwardMaps(Map<String, AwardData> talisAwardMaps) {
		this.talisAwardMaps = talisAwardMaps;
	}

	public String getCrtWingRewardRefId() {
		return crtWingRewardRefId;
	}

	public void setCrtWingRewardRefId(String crtWingRewardRefId) {
		this.crtWingRewardRefId = crtWingRewardRefId;
	}

	public String getCrtRideRewardRefId() {
		return crtRideRewardRefId;
	}

	public void setCrtRideRewardRefId(String crtRideRewardRefId) {
		this.crtRideRewardRefId = crtRideRewardRefId;
	}

}
