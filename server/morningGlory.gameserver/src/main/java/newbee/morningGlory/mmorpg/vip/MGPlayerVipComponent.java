/**
 *   Copyright 2013-2015 Sophia
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package newbee.morningGlory.mmorpg.vip;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import newbee.morningGlory.code.MGErrorCode;
import newbee.morningGlory.mmorpg.player.wing.MGPlayerWing;
import newbee.morningGlory.mmorpg.player.wing.MGPlayerWingComponent;
import newbee.morningGlory.mmorpg.player.wing.MGPlayerWingRef;
import newbee.morningGlory.mmorpg.player.wing.MGWingEffectMgr;
import newbee.morningGlory.mmorpg.player.wing.event.G2C_Wing_RequestNowWing;
import newbee.morningGlory.mmorpg.player.wing.event.WingEventDefines;
import newbee.morningGlory.mmorpg.sprite.buff.MGFightSpriteBuff;
import newbee.morningGlory.mmorpg.sprite.buff.MGFightSpriteBuffComponent;
import newbee.morningGlory.mmorpg.sprite.buff.MGFightSpriteBuffRef;
import newbee.morningGlory.mmorpg.vip.event.C2G_Vip_GetReward;
import newbee.morningGlory.mmorpg.vip.event.C2G_Vip_GetWing;
import newbee.morningGlory.mmorpg.vip.event.C2G_Vip_Lottery;
import newbee.morningGlory.mmorpg.vip.event.C2G_Vip_OpenLottery;
import newbee.morningGlory.mmorpg.vip.event.C2G_Vip_RewardList;
import newbee.morningGlory.mmorpg.vip.event.G2C_Vip_GetReward;
import newbee.morningGlory.mmorpg.vip.event.G2C_Vip_Lottery;
import newbee.morningGlory.mmorpg.vip.event.G2C_Vip_LotteryMsg;
import newbee.morningGlory.mmorpg.vip.event.G2C_Vip_Message;
import newbee.morningGlory.mmorpg.vip.event.G2C_Vip_OpenLottery;
import newbee.morningGlory.mmorpg.vip.event.G2C_Vip_RewardList;
import newbee.morningGlory.mmorpg.vip.event.G2C_Vip_SendWing;
import newbee.morningGlory.mmorpg.vip.event.VipEventDefines;
import newbee.morningGlory.mmorpg.vip.gameEvent.VipGE;
import newbee.morningGlory.mmorpg.vip.lottery.MGVipLotteryMgr;
import newbee.morningGlory.mmorpg.vip.lottery.MGVipLotteryRecord;
import newbee.morningGlory.mmorpg.vip.lottery.ref.MGLotteryCountDataRef;
import newbee.morningGlory.mmorpg.vip.lottery.ref.MGLotteryRewardDataRef;
import newbee.morningGlory.mmorpg.vip.lottery.ref.MGLotteryVipDataRef;
import newbee.morningGlory.mmorpg.vip.lottery.ref.MGVipLotteryDataConfig;
import newbee.morningGlory.stat.MGStatFunctions;
import newbee.morningGlory.stat.logs.StatVip;
import newbee.morningGlory.stat.logs.StatWing;

import org.apache.log4j.Logger;

import sophia.foundation.communication.core.ActionEventBase;
import sophia.foundation.communication.core.MessageFactory;
import sophia.foundation.data.PersistenceObject;
import sophia.foundation.property.PropertyDictionary;
import sophia.game.GameRoot;
import sophia.game.component.communication.GameEvent;
import sophia.game.ref.GameRefObject;
import sophia.mmorpg.MMORPGContext;
import sophia.mmorpg.Mail.MailMgr;
import sophia.mmorpg.base.ConcreteComponent;
import sophia.mmorpg.code.MMORPGErrorCode;
import sophia.mmorpg.core.timer.SFTimeChimeListener;
import sophia.mmorpg.core.timer.SFTimeUnit;
import sophia.mmorpg.core.timer.SFTimer;
import sophia.mmorpg.core.timer.SFTimerCreater;
import sophia.mmorpg.event.ResultEvent;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.chat.Bricks;
import sophia.mmorpg.player.chat.sysytem.SystemPromptFacade;
import sophia.mmorpg.player.gameEvent.EnterWorld_GE;
import sophia.mmorpg.player.gameEvent.EnterWorld_SceneReady_GE;
import sophia.mmorpg.player.itemBag.ItemFacade;
import sophia.mmorpg.player.itemBag.ItemOptSource;
import sophia.mmorpg.player.itemBag.ItemPair;
import sophia.mmorpg.player.itemBag.ItemQuality;
import sophia.mmorpg.player.persistence.immediatelySave.PlayerImmediateDaoFacade;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;
import sophia.mmorpg.utils.DateTimeUtil;
import sophia.mmorpg.utils.RuntimeResult;
import sophia.mmorpg.world.ActionEventFacade;

import com.google.common.base.Preconditions;
import com.google.gson.Gson;

public class MGPlayerVipComponent extends ConcreteComponent<Player> {
	private static final Logger logger = Logger.getLogger(MGPlayerVipComponent.class);
	private MGVipLevelMgr vipMgr;
	private MGVipLotteryMgr lotteryMgr;

	public static final String Tag = "MGPlayerVipComponent";
	private MGVipEffectMgr vipEffectMgr;
	private PersistenceObject persistenceObject;
	private SFTimer timer;
	private static final String EnterWorld_SceneReady_GE_Id = EnterWorld_SceneReady_GE.class.getSimpleName();
	private static final String EnterWorld_GE_Id = EnterWorld_GE.class.getSimpleName();
	private static final String VipGE_Id = VipGE.class.getSimpleName();
	private static final String VipPrompt = "黄金VIP";
	private static final String Vip = "vip";
	public MGPlayerVipComponent() {
	}

	@Override
	public void ready() {
		addActionEventListener(VipEventDefines.C2G_Vip_Message);
		addActionEventListener(VipEventDefines.C2G_Vip_RewardList);
		addActionEventListener(VipEventDefines.C2G_Vip_GetReward);
		addActionEventListener(VipEventDefines.C2G_Vip_OpenLottery);
		addActionEventListener(VipEventDefines.C2G_Vip_Lottery);
		addActionEventListener(VipEventDefines.C2G_Vip_GetWing);
		addInterGameEventListener(EnterWorld_GE_Id);
		addInterGameEventListener(EnterWorld_SceneReady_GE_Id);
		timerExcuteLotteryCount();
	}

	@Override
	public void suspend() {
		removeActionEventListener(VipEventDefines.C2G_Vip_Message);
		removeActionEventListener(VipEventDefines.C2G_Vip_RewardList);
		removeActionEventListener(VipEventDefines.C2G_Vip_GetReward);
		removeActionEventListener(VipEventDefines.C2G_Vip_OpenLottery);
		removeActionEventListener(VipEventDefines.C2G_Vip_Lottery);
		removeActionEventListener(VipEventDefines.C2G_Vip_GetWing);
		removeInterGameEventListener(EnterWorld_GE_Id);
		removeInterGameEventListener(EnterWorld_SceneReady_GE_Id);
		if (timer != null) {
			timer.cancel();
		}
	}

	@Override
	public void handleGameEvent(GameEvent<?> event) {
		if (event.isId(EnterWorld_SceneReady_GE_Id) || event.isId(EnterWorld_GE_Id)) {
			checkUpdateLotteryCountNormal();
		}
	}

	@Override
	public void handleActionEvent(ActionEventBase event) {
		short actionEventId = event.getActionEventId();

		switch (actionEventId) {
		case VipEventDefines.C2G_Vip_RewardList:
			handle_Vip_RewardList((C2G_Vip_RewardList) event);
			break;
		case VipEventDefines.C2G_Vip_GetReward:
			handle_Vip_GetReward((C2G_Vip_GetReward) event);
			break;
		case VipEventDefines.C2G_Vip_Message:
			sendVipTypeActionEvent();
			byte vipType = this.getVipMgr().getVipType();
			MGPropertyAccesser.setOrPutVipType(getConcreteParent().getSenceProperty(), vipType);
			break;
		case VipEventDefines.C2G_Vip_OpenLottery:
			handle_Vip_OpenLottery((C2G_Vip_OpenLottery) event);
			break;
		case VipEventDefines.C2G_Vip_Lottery:
			handle_Vip_Lottery((C2G_Vip_Lottery) event);
			break;
		case VipEventDefines.C2G_Vip_GetWing:
			handle_Vip_GetWing((C2G_Vip_GetWing) event);
			break;
		default:
			break;
		}
	}

	// ------------------------vip 抽奖 活动
	// ---------------------------------------------

	private void checkUpdateLotteryCountNormal() {
		long lastUpdateLotteryCountTime = this.getLotteryMgr().getLotteryRecord().getLastUpdateLotteryCountTime();
		lastUpdateLotteryCountTime = DateTimeUtil.getLongTimeOfToday(lastUpdateLotteryCountTime);
		long crtTime = System.currentTimeMillis();
		long oneDayTime = 3600 * 1000 * 24l;
		if (crtTime - lastUpdateLotteryCountTime > oneDayTime) {
			updateLotteryCount();
		}
		if (vipMgr.getVipType() != MGVipType.NO_VIP && System.currentTimeMillis() > vipMgr.getVipEndTime()) {
			cancelVip();
		}

	}

	private void timerExcuteLotteryCount() {
		if (timer != null) {
			return;
		}
		SFTimerCreater timerCreater = MMORPGContext.getTimerCreater();
		timer = timerCreater.calendarChime(new SFTimeChimeListener() {

			@Override
			public void handleTimeChimeCancel() {

			}

			@Override
			public void handleTimeChime() {

				if (vipMgr.getVipType() != MGVipType.NO_VIP && System.currentTimeMillis() > vipMgr.getVipEndTime()) {
					cancelVip();
				}

				long lastUpdateLotteryCountTime = getLotteryMgr().getLotteryRecord().getLastUpdateLotteryCountTime();
				lastUpdateLotteryCountTime = DateTimeUtil.getLongTimeOfToday(lastUpdateLotteryCountTime);
				long crtTime = System.currentTimeMillis();
				long oneDayTime = 3600 * 1000 * 24l;
				if (crtTime - lastUpdateLotteryCountTime > oneDayTime) {
					updateLotteryCount();
					updateVipReward();
				}

			}

			@Override
			public void handleServiceShutdown() {

			}
		}, SFTimeUnit.HOUR, 0);
	}

	/**
	 * 更新抽奖次数
	 */
	public void updateLotteryCount() {
		int whichDay = whichDay();
		String day = MGVipLotteryRecord.lot + whichDay;
		MGVipLotteryDataConfig config = (MGVipLotteryDataConfig) GameRoot.getGameRefObjectManager().getManagedObject(MGVipLotteryDataConfig.LotteryCount_Id);
		MGLotteryCountDataRef countRef = config.getLotteryCountMaps().get(day);
		if (countRef != null) {
			lotteryMgr.getLotteryRecord().addResidueCount(countRef.getAddedDay());
		}
		byte vipType = vipMgr.getVipType();
		String vipRefId = MGVipLotteryRecord.lotvip + vipType;
		config = (MGVipLotteryDataConfig) GameRoot.getGameRefObjectManager().getManagedObject(MGVipLotteryDataConfig.LotteryVip_Id);
		MGLotteryVipDataRef vipRef = config.getLotteryVipMaps().get(vipRefId);
		if (vipRef != null) {
			lotteryMgr.getLotteryRecord().addResidueCount(vipRef.getAddedDays());
		}

		this.getLotteryMgr().getLotteryRecord().setLastUpdateLotteryCountTime(System.currentTimeMillis());
		PlayerImmediateDaoFacade.update(getConcreteParent());
	}

	/**
	 * 出生第几天
	 * 
	 * @return
	 */
	public int whichDay() {
		long crtTime = System.currentTimeMillis();
		long birthday = MGPropertyAccesser.getBirthday(getConcreteParent().getProperty());
		birthday = birthday < 0 ? crtTime : birthday;
		long totaltime = crtTime - DateTimeUtil.getLongTimeOfToday(birthday);
		int whichDay = (int) (totaltime) / (3600 * 1000 * 24) + 1;
		return whichDay;
	}

	/**
	 * 获取的明天的抽奖次数
	 * 
	 * @return
	 */
	public int getTomLotteryCount() {

		int count = 0;
		int whichDay = whichDay() + 1;
		String day = MGVipLotteryRecord.lot + whichDay;
		MGVipLotteryDataConfig config = (MGVipLotteryDataConfig) GameRoot.getGameRefObjectManager().getManagedObject(MGVipLotteryDataConfig.LotteryCount_Id);
		MGLotteryCountDataRef countRef = config.getLotteryCountMaps().get(day);
		if (countRef != null) {
			count = count + countRef.getAddedDay();
		}
		byte vipType = vipMgr.getVipType();
		if (vipType != MGVipType.NO_VIP) {
			String vipRefId = MGVipLotteryRecord.lotvip + vipType;
			config = (MGVipLotteryDataConfig) GameRoot.getGameRefObjectManager().getManagedObject(MGVipLotteryDataConfig.LotteryVip_Id);
			MGLotteryVipDataRef vipRef = config.getLotteryVipMaps().get(vipRefId);
			long vipRemainTimes = vipMgr.getRemainTime();
			long crtTime = System.currentTimeMillis();
			long tomStartTime = DateTimeUtil.getLongTimeOfToday(crtTime + 3600 * 1000 * 24);
			long toDayRemainTime = tomStartTime - crtTime;
			if (vipRef != null && vipRemainTimes > toDayRemainTime) {
				count = count + vipRef.getAddedDays();
			}
		}
		return count;
	}

	/**
	 * 打开VIP抽奖面板
	 * 
	 * @param event
	 */
	private void handle_Vip_OpenLottery(C2G_Vip_OpenLottery event) {
		if (this.getLotteryMgr().getLotteryRecord().getRewardMaps().isEmpty()) {
			sendLotteryList();
		} else {
			G2C_Vip_OpenLottery res = (G2C_Vip_OpenLottery) MessageFactory.getConcreteMessage(VipEventDefines.G2C_Vip_OpenLottery);
			res.setVipType(vipMgr.getVipType());
			res.setTomCount(getTomLotteryCount());
			res.setTodayCount(this.getLotteryMgr().getLotteryRecord().getResidueCount());
			res.setRewardMaps(this.getLotteryMgr().getLotteryRecord().getRewardMaps());
			GameRoot.sendMessage(getConcreteParent().getIdentity(), res);
		}
	}

	/**
	 * 抽奖
	 * 
	 * @param event
	 */
	private void handle_Vip_Lottery(C2G_Vip_Lottery event) {
		byte lotteryType = event.getLotteryType();
		if (lotteryType == 0) {
			propsLottery();
		} else if (lotteryType == 1) {
			vipLottery();
		} else {
			return;
		}

		PlayerImmediateDaoFacade.update(getConcreteParent());
	}

	private void vipLottery() {
		int lotteryCount = lotteryMgr.getLotteryRecord().getResidueCount();
		if (getVipMgr().getVipType() == MGVipType.NO_VIP) {
			ResultEvent.sendResult(getConcreteParent().getIdentity(), VipEventDefines.C2G_Vip_Lottery, MGErrorCode.CODE_VIP_NO_VIP);
		}
		if (lotteryCount < 8) {
			if (logger.isDebugEnabled()) {
				logger.debug("可抽奖次数小于8次");
			}
			ResultEvent.sendResult(getConcreteParent().getIdentity(), VipEventDefines.C2G_Vip_Lottery, MGErrorCode.CODE_VIP_LOTTERTY_NOT_ENOUGH);
			return;
		}
		if (logger.isDebugEnabled()) {
			logger.debug("抽奖次数为" + lotteryCount + "，索取所有奖励");
		}
		Map<Byte, MGLotteryRewardDataRef> refs = lotteryMgr.getLotteryRecord().getRewardMaps();
		Preconditions.checkArgument(refs != null, "抽奖数据异常");
		List<ItemPair> itempairs = new ArrayList<ItemPair>(8);
		String itemRefId = null;
		int number = 0;
		byte bindStatus = 1;
		StringBuffer sb = new StringBuffer("玩家");
		sb.append(getConcreteParent().getName());
		sb.append("通过抽奖获得");
		for (Entry<Byte, MGLotteryRewardDataRef> entry : refs.entrySet()) {
			MGLotteryRewardDataRef ref = entry.getValue();
			itemRefId = MGPropertyAccesser.getItemRefId(ref.getProperty());
			number = MGPropertyAccesser.getNumber(ref.getProperty());
			bindStatus = MGPropertyAccesser.getBindStatus(ref.getProperty());
			ItemPair itempair = new ItemPair(itemRefId, number, bindStatus);
			itempairs.add(itempair);
			GameRefObject itemRef = GameRoot.getGameRefObjectManager().getManagedObject(itemRefId);
			if (itemRef == null) {
				continue;
			}
			byte quality = MGPropertyAccesser.getQuality(itemRef.getProperty());
			if (quality == ItemQuality.PURPLE) {
				String itemName = MGPropertyAccesser.getName(itemRef.getProperty());
				sb.append(number);
				sb.append("个");
				sb.append(itemName);
			}
			MGStatFunctions.VipLotteryStat(getConcreteParent(), itemRefId, lotteryCount - 8);
		}
		G2C_Vip_Lottery res = (G2C_Vip_Lottery) MessageFactory.getConcreteMessage(VipEventDefines.G2C_Vip_Lottery);
		res.setIndex((byte) 100);

		GameRoot.sendMessage(getConcreteParent().getIdentity(), res);
		RuntimeResult result = ItemFacade.addItem(getConcreteParent(), itempairs, ItemOptSource.VipLotter);
		if (!result.isOK()) {
			String tips = "背包已满，获得的物品已通过邮件发送给您，请查收！";
			String item = (new Gson()).toJson(itempairs);
			MailMgr.sendMailById(getConcreteParent().getId(), tips, (byte) 0, item, 0, 0, 0);
			SystemPromptFacade.itemBagFullSendMail(getConcreteParent(), "抽奖");
		}

		lotteryCount = lotteryCount - 8;
		lotteryMgr.getLotteryRecord().setResidueCount(lotteryCount);
		this.getLotteryMgr().getLotteryRecord().getRewardMaps().clear();
		sendLotteryList();
		// 广播中奖消息给全服玩家

		String msg = sb.toString();
		G2C_Vip_LotteryMsg actionEvent = (G2C_Vip_LotteryMsg) MessageFactory.getConcreteMessage(VipEventDefines.G2C_Vip_LotteryMsg);
		actionEvent.setMsg(msg);
		ActionEventFacade.sendMessageToWorld(actionEvent);
	}

	/**
	 * 普通抽奖
	 */
	private void propsLottery() {
		int lotteryCount = lotteryMgr.getLotteryRecord().getResidueCount();
		if (lotteryCount <= 0) {
			if (logger.isDebugEnabled()) {
				logger.debug("可抽奖次数为0");
			}
			ResultEvent.sendResult(getConcreteParent().getIdentity(), VipEventDefines.C2G_Vip_Lottery, MGErrorCode.CODE_VIP_LOTTERTY_NOT_ENOUGH);
			return;
		}
		byte index = lotteryMgr.getLotteryIndex();
		if (logger.isDebugEnabled()) {
			logger.debug("抽奖次数为" + lotteryCount + "，中奖位置:" + index);
		}
		G2C_Vip_Lottery res = (G2C_Vip_Lottery) MessageFactory.getConcreteMessage(VipEventDefines.G2C_Vip_Lottery);
		res.setIndex(index);
		GameRoot.sendMessage(getConcreteParent().getIdentity(), res);

		MGLotteryRewardDataRef ref = lotteryMgr.getLotteryRecord().getRewardMaps().get(index);
		Preconditions.checkArgument(ref != null, "中奖位置索引异常");

		String itemRefId = MGPropertyAccesser.getItemRefId(ref.getProperty());
		int number = MGPropertyAccesser.getNumber(ref.getProperty());
		byte bindStatus = MGPropertyAccesser.getBindStatus(ref.getProperty());
		ItemPair itempair = new ItemPair(itemRefId, number, bindStatus);
		ArrayList<ItemPair> itemPairs = new ArrayList<>(1);
		itemPairs.add(itempair);
		RuntimeResult result = ItemFacade.addItem(getConcreteParent(), itempair, ItemOptSource.VipLotter);
		if (!result.isOK()) {
			String tips = "背包已满，获得的物品已通过邮件发送给您，请查收！";
			String item = (new Gson()).toJson(itemPairs);
			MailMgr.sendMailById(getConcreteParent().getId(), tips, (byte) 0, item, 0, 0, 0);
			SystemPromptFacade.itemBagFullSendMail(getConcreteParent(), "抽奖");

		}
		lotteryCount = lotteryCount - 1;
		lotteryMgr.getLotteryRecord().setResidueCount(lotteryCount);
		this.getLotteryMgr().getLotteryRecord().getRewardMaps().clear();
		sendLotteryList();
		// 广播中奖消息给全服玩家
		GameRefObject itemRef = GameRoot.getGameRefObjectManager().getManagedObject(itemRefId);

		String itemName = MGPropertyAccesser.getName(itemRef.getProperty());
		byte quality = MGPropertyAccesser.getQuality(itemRef.getProperty());
		if (quality == ItemQuality.PURPLE) {
			String msg = Bricks.getContents("system_prompt_config_2", getConcreteParent().getName(), number + "", itemName);
			G2C_Vip_LotteryMsg actionEvent = (G2C_Vip_LotteryMsg) MessageFactory.getConcreteMessage(VipEventDefines.G2C_Vip_LotteryMsg);
			actionEvent.setMsg(msg);
			ActionEventFacade.sendMessageToWorld(actionEvent);
		}

		MGStatFunctions.VipLotteryStat(getConcreteParent(), itemRefId, lotteryCount);
	}

	public void sendLotteryList() {
		this.getLotteryMgr().setLotteryList();
		if (this.getLotteryMgr().getLotteryRecord().getRewardMaps().size() == 0) {
			logger.error("抽奖列表为空");
			return;
		}
		G2C_Vip_OpenLottery res = (G2C_Vip_OpenLottery) MessageFactory.getConcreteMessage(VipEventDefines.G2C_Vip_OpenLottery);
		res.setVipType(vipMgr.getVipType());
		res.setTomCount(getTomLotteryCount());
		res.setTodayCount(this.getLotteryMgr().getLotteryRecord().getResidueCount());
		res.setRewardMaps(this.getLotteryMgr().getLotteryRecord().getRewardMaps());
		GameRoot.sendMessage(getConcreteParent().getIdentity(), res);

	}

	// ----------------------- VIP 状态领奖 ------------------------------

	private void handle_Vip_GetReward(C2G_Vip_GetReward event) {
		if (!vipMgr.isVip()) {
			if (logger.isDebugEnabled()) {
				logger.debug("非vip");
			}
			return;
		}
		byte type = event.getType();
		boolean result = false;
		switch (type) {
		case 1:
			result = rewardExpBuff();
			break;
		case 2:
			result = rewardGiftBag();
			break;
		case 3:
			result = rewardWeapons();
			break;
		case 4:
			result = rewardAll();
			break;
		default:
			break;
		}
		if (result) {
			G2C_Vip_GetReward res = MessageFactory.getConcreteMessage(VipEventDefines.G2C_Vip_GetReward);
			res.setRet(type);
			GameRoot.sendMessage(getConcreteParent().getIdentity(), res);
		} else {
			ResultEvent.sendResult(event.getIdentity(), event.getActionEventId(), MMORPGErrorCode.CODE_ITEM_SLOT_ENOUGH);
		}
	}

	public void handle_Vip_RewardList(C2G_Vip_RewardList event) {
		sendRewardList();
	}

	
	/**
	 * 更新VIP奖励
	 */
	private void updateVipReward() {
		this.getVipMgr().getVipRewardRecord().setGetGiftTime(0l);
		this.getVipMgr().getVipRewardRecord().setGetExpTime(0l);
		sendRewardList();
	}

	/**
	 * 领取VIP奖励
	 */
	private boolean rewardAll() {
		Player player = getConcreteParent();
		MGVipLevelDataRef vipLevelDataRef = this.getVipMgr().getVipLevelDataRef();
		Map<Integer, Integer> hadGetLevel = this.getVipMgr().getVipRewardRecord().getVipLevelRewardTimeMap();
		int crtLevel = player.getExpComponent().getLevel();
		List<ItemPair> itemPairs = new ArrayList<ItemPair>();
		// 领取等级武器奖励
		for (int rewardLevel : vipLevelDataRef.getViplevels()) {
			if (crtLevel >= rewardLevel && !hadGetLevel.containsKey(rewardLevel)) {
				byte prosfession = player.getProfession();
				ItemPair itemPair = vipLevelDataRef.getVipLevelWeaponReward(prosfession, rewardLevel);
				if (itemPair == null) {
					continue;
				}
				itemPairs.add(itemPair);
			}
		}

		if (itemPairs.size() + 1 <= ItemFacade.getFreeCapacity(player)) {
			rewardWeapons();
			if (ItemFacade.getFreeCapacity(getConcreteParent()) >= 1) {
				if (rewardGiftBag()) {
					rewardExpBuff();
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 领取经验buff
	 * 
	 * @return
	 */
	private boolean rewardExpBuff() {
		Player player = getConcreteParent();
		PropertyDictionary pd = this.getVipMgr().getVipLevelDataRef().getProperty();
		String expBuffId = MGPropertyAccesser.getBuffRefId(pd);
		long crtTime = System.currentTimeMillis();
		// 奖励多倍杀怪经验
		if (this.getVipMgr().getVipRewardRecord().getGetExpTime() < DateTimeUtil.getLongTimeOfToday()) {
			MGFightSpriteBuffComponent<?> fightSpriteBuffComponent = (MGFightSpriteBuffComponent<?>) player.getTagged(MGFightSpriteBuffComponent.Tag);
			MGFightSpriteBuffRef buffRef = (MGFightSpriteBuffRef) GameRoot.getGameRefObjectManager().getManagedObject(expBuffId);
			MGFightSpriteBuff buff = new MGFightSpriteBuff(buffRef, player, player);
			fightSpriteBuffComponent.getFightSpriteBuffMgr().attachFightSpriteBuff(buff);
			this.getVipMgr().getVipRewardRecord().setGetExpTime(crtTime);
		}
		PlayerImmediateDaoFacade.update(player);
		return true;
	}

	/**
	 * 领取礼包
	 * 
	 * @return
	 */
	private boolean rewardGiftBag() {
		Player player = getConcreteParent();
		PropertyDictionary pd = this.getVipMgr().getVipLevelDataRef().getProperty();
		String giftRefId = MGPropertyAccesser.getItemRefId(pd);
		long crtTime = System.currentTimeMillis();
		byte bindStatus = MGPropertyAccesser.getBindStatus(pd);
		// 奖励每日礼包
		if (this.getVipMgr().getVipRewardRecord().getGetGiftTime() < DateTimeUtil.getLongTimeOfToday()) {
			if (ItemFacade.addItem(getConcreteParent(), new ItemPair(giftRefId, 1, bindStatus), ItemOptSource.Vip).isOK()) {
				this.getVipMgr().getVipRewardRecord().setGetGiftTime(crtTime);
			} else {
				return false;
			}

		}
		PlayerImmediateDaoFacade.update(player);
		return true;
	}

	/**
	 * 领取武器
	 * 
	 * @return
	 */
	private boolean rewardWeapons() {
		Player player = getConcreteParent();
		MGVipLevelDataRef vipLevelDataRef = this.getVipMgr().getVipLevelDataRef();
		Map<Integer, Integer> hadGetLevel = this.getVipMgr().getVipRewardRecord().getVipLevelRewardTimeMap();
		int crtLevel = player.getExpComponent().getLevel();
		List<ItemPair> itemPairs = new ArrayList<ItemPair>();
		List<Integer> viplevels = new ArrayList<Integer>();
		// 领取等级武器奖励
		for (int rewardLevel : vipLevelDataRef.getViplevels()) {
			if (crtLevel >= rewardLevel && !hadGetLevel.containsKey(rewardLevel)) {
				byte prosfession = player.getProfession();
				ItemPair itemPair = vipLevelDataRef.getVipLevelWeaponReward(prosfession, rewardLevel);
				if (itemPair == null) {
					continue;
				}
				itemPairs.add(itemPair);
				viplevels.add(rewardLevel);
			}
		}
		if (ItemFacade.addItemCompareSlot(player, itemPairs, ItemOptSource.Vip).isOK()) {
			for (int level : viplevels) {
				hadGetLevel.put(level, level);
			}
		} else {
			return false;
		}
		PlayerImmediateDaoFacade.update(player);

		return true;
	}

	/**
	 * 返回VIP奖励列表
	 */
	private void sendRewardList() {
		if (logger.isDebugEnabled()) {
			logger.debug("请求VIP 奖励列表");
		}
		if (!vipMgr.isVip()) {
			if (logger.isDebugEnabled()) {
				logger.debug("非vip");
			}
			return;
		}
		byte canGetExpReward = 0;
		byte canGetGiftReward = 0;
		byte canGetLevelReward = 0;
		MGVipLevelDataRef vipLevelDataRef = this.getVipMgr().getVipLevelDataRef();
		Map<Integer, Integer> hadGetLevel = this.getVipMgr().getVipRewardRecord().getVipLevelRewardTimeMap();
		int crtLevel = getConcreteParent().getExpComponent().getLevel();
		int canGetLevelCount = 0;
		for (int rewardLevel : vipLevelDataRef.getViplevels()) {
			if (crtLevel >= rewardLevel && !hadGetLevel.containsKey(rewardLevel)) {
				canGetLevelCount++;
			}
		}
		if (canGetLevelCount > 0) {
			canGetLevelReward = 1;
		}
		if (this.getVipMgr().getVipRewardRecord().getGetExpTime() < DateTimeUtil.getLongTimeOfToday()) {
			canGetExpReward = 1;
		}
		if (this.getVipMgr().getVipRewardRecord().getGetGiftTime() < DateTimeUtil.getLongTimeOfToday()) {
			canGetGiftReward = 1;
		}

		G2C_Vip_RewardList res = MessageFactory.getConcreteMessage(VipEventDefines.G2C_Vip_RewardList);
		res.setExpMultiple(canGetExpReward);
		res.setGift(canGetGiftReward);
		res.setLevel(canGetLevelReward);
		GameRoot.sendMessage(getConcreteParent().getIdentity(), res);

	}

	/**
	 * 升级成VIP
	 * 
	 * @param vipRefId
	 * @param vipType
	 */
	public boolean becameVip(String vipRefId, byte vipType) {

		if (vipType < this.vipMgr.getVipType() || vipType > 3) {
			ResultEvent.sendResult(getConcreteParent().getIdentity(), VipEventDefines.C2G_Vip_Lottery, MGErrorCode.CODE_VIP_HIGHER_CARD);
			return false;
		}
		if (logger.isDebugEnabled()) {
			logger.debug("玩家 " + getConcreteParent().getName() + " 升级为VIP" + vipType);
		}
		if (vipMgr.getVipLevelDataRef() != null) {
			this.vipEffectMgr.detachAndSnapshot(vipMgr.getVipLevelDataRef());
		}
		byte oldVipType = vipMgr.getVipType();

		addVipLottery(vipType);

		vipMgr.setVipType(vipRefId, vipType);

		this.vipEffectMgr.attach(vipMgr.getVipLevelDataRef());// 附加战斗属性
		
		if (vipType == oldVipType) {
			return true;
		}

		Player player = getConcreteParent();
		if(vipType == MGVipType.VIP_THREE){			
			SystemPromptFacade.broadVip(player.getName(),player.getId(), VipPrompt,Vip);
		}
		player.setVipType(vipType);
		
		sendGetWingEvent();

		sendVipTypeActionEvent();
		
		sendVipGameEvent(player, vipType);

		changeVipTypeBroadcast();

		sendRewardList();

		PlayerImmediateDaoFacade.update(player);

		MGStatFunctions.vipStat(player, vipType, StatVip.LevelUp);
		return true;
	}
	
	private void sendVipGameEvent(Player player, byte vipType) {
		VipGE vipGe = new VipGE(player, vipType);
		
		GameEvent<?> ge = GameEvent.getInstance(VipGE_Id, vipGe);
		sendGameEvent(ge, player.getId());
	}

	/**
	 * 增加VIP抽奖次数
	 * 
	 * @param vipType
	 */
	public void addVipLottery(byte vipType) {
		if (vipType <= vipMgr.getVipType()) {
			return;
		}
		String lotteryRefId = MGVipLotteryRecord.lotvip + vipType;
		MGVipLotteryDataConfig config = (MGVipLotteryDataConfig) GameRoot.getGameRefObjectManager().getManagedObject(MGVipLotteryDataConfig.LotteryVip_Id);
		MGLotteryVipDataRef vipRef = config.getLotteryVipMaps().get(lotteryRefId);
		if (vipRef != null) {
			lotteryMgr.getLotteryRecord().addResidueCount(vipRef.getAddedDays());
		}
	}

	/**
	 * 增加日常任务环数
	 * 
	 * @param vipAddRingTime
	 */
	public int getAddQuestRingTime() {
		if (this.getVipMgr().getVipType() == MGVipType.NO_VIP) {
			return 0;
		}
		MGVipLevelDataRef vipRef = this.getVipMgr().getVipLevelDataRef();
		if (vipRef == null) {
			return 0;
		}
		int count = MGPropertyAccesser.getDailyProposeRing(vipRef.getProperty());
		return count;
	}

	/**
	 * 增加额外进入所有副本的次数
	 * 
	 * @return
	 */
	public int getEnterGameInstanceCount() {
		if (this.getVipMgr().getVipType() == MGVipType.NO_VIP) {
			return 0;
		}
		MGVipLevelDataRef vipRef = this.getVipMgr().getVipLevelDataRef();
		if (vipRef == null) {
			return 0;
		}
		int count = MGPropertyAccesser.getTimesADay(vipRef.getProperty());
		return count;
	}

	/**
	 * 增加额外的拍卖数量
	 * 
	 * @return
	 */
	public int getAddedMaxAuctionCount() {
		if (this.getVipMgr().getVipType() == MGVipType.NO_VIP) {
			return 0;
		}
		MGVipLevelDataRef vipRef = this.getVipMgr().getVipLevelDataRef();
		if (vipRef == null) {
			return 0;
		}
		int count = MGPropertyAccesser.getMaxAuctionCount(vipRef.getProperty());
		return count;
	}
	
	private void handle_Vip_GetWing(C2G_Vip_GetWing event) {
		byte vipType = vipMgr.getVipType();
		if (vipType == MGVipType.NO_VIP) {
			return;
		}
		if (this.getVipMgr().getVipLevelDataRef() == null) {
			return;
		}
		PropertyDictionary pd = this.getVipMgr().getVipLevelDataRef().getProperty();
		// byte wingLevel = MGPropertyAccesser.getWingLevel(pd);

		String wingRefId = MGPropertyAccesser.getWingRefId(pd);

		openPlayerWing(vipType, wingRefId);
	}

	private void sendGetWingEvent() {
		byte vipType = vipMgr.getVipType();
		if (vipType == MGVipType.NO_VIP) {
			return;
		}
		if (this.getVipMgr().getVipLevelDataRef() == null) {
			return;
		}
		PropertyDictionary pd = this.getVipMgr().getVipLevelDataRef().getProperty();

		String wingRefId = MGPropertyAccesser.getWingRefId(pd);
		MGPlayerWingRef playerWingRef = (MGPlayerWingRef) GameRoot.getGameRefObjectManager().getManagedObject(wingRefId);
		byte targetWingStageLevel = playerWingRef.getCrtWingStageLevel();

		MGPlayerWingComponent wingComponent = (MGPlayerWingComponent) getConcreteParent().getTagged(MGPlayerWingComponent.Tag);
		MGPlayerWing wing = wingComponent.getPlayerWing();
		MGPlayerWingRef crtWingRef = wing.getPlayerWingRef();
		if (crtWingRef != null) {
			int crtStageLevel = crtWingRef.getCrtWingStageLevel();
			if (crtStageLevel >= targetWingStageLevel) {
				return;
			}
		}

		G2C_Vip_SendWing res = MessageFactory.getConcreteMessage(VipEventDefines.G2C_Vip_SendWing);
		res.setCrtWingStageLevel(targetWingStageLevel);
		GameRoot.sendMessage(getConcreteParent().getIdentity(), res);
	}

	/**
	 * 开放翅膀系统并升级翅膀到指定等级
	 * 
	 * @param vipType
	 */
	private void openPlayerWing(byte vipType, String wingRefId) {
		Player player = getConcreteParent();
		MGPlayerWingComponent wingComponent = (MGPlayerWingComponent) player.getTagged(MGPlayerWingComponent.Tag);
		MGPlayerWing playerWing = wingComponent.getPlayerWing();
		MGPlayerWingRef crtWingRef = playerWing.getPlayerWingRef();

		MGWingEffectMgr wingEffectMgr = new MGWingEffectMgr(player);

		if (crtWingRef == null) {
			crtWingRef = (MGPlayerWingRef) GameRoot.getGameRefObjectManager().getManagedObject("wing_1_0");
		} else {
			wingEffectMgr.detachAndSnapshot(playerWing);
			playerWing.broadcastWingModelProperty(player);
		}

		byte crtWingStageLevel = crtWingRef.getCrtWingStageLevel();
		byte crtWingStarLevel = crtWingRef.getCrtWingStarLevel();

		MGPlayerWingRef targetWingRef = (MGPlayerWingRef) GameRoot.getGameRefObjectManager().getManagedObject(wingRefId);

		if (targetWingRef == null) {
			wingEffectMgr.attach(playerWing);
			playerWing.broadcastWingModelProperty(player);
			return;
		}

		byte targetWingStageLevel = targetWingRef.getCrtWingStageLevel();
		byte targetWingStarLevel = targetWingRef.getCrtWingStarLevel();

		if (crtWingStageLevel > targetWingStageLevel) {
			wingEffectMgr.attach(playerWing);
			playerWing.broadcastWingModelProperty(player);
			return;
		}

		if (targetWingStageLevel == crtWingStageLevel && targetWingStarLevel <= crtWingStarLevel) {
			wingEffectMgr.attach(playerWing);
			playerWing.broadcastWingModelProperty(player);
			return;
		}

		playerWing.setPlayerWingRef(targetWingRef);
		wingEffectMgr.attach(playerWing);
		playerWing.broadcastWingModelProperty(player);

		G2C_Wing_RequestNowWing res = MessageFactory.getConcreteMessage(WingEventDefines.G2C_Wing_RequestNowWing);
		res.setWingRefId(playerWing.getPlayerWingRef().getId());
		res.setCrtExp(playerWing.getExp());
		GameRoot.sendMessage(player.getIdentity(), res);

		wingComponent.sendGameEventMessage(StatWing.LevelUp);
	}

	private void sendVipTypeActionEvent() {

		byte vipType = getVipMgr().getVipType();
		int days = getVipMgr().getRemainDays();
		G2C_Vip_Message res = MessageFactory.getConcreteMessage(VipEventDefines.G2C_Vip_Message);
		res.setVip(vipType);
		res.setDay(days);
		GameRoot.sendMessage(getConcreteParent().getIdentity(), res);
		if (logger.isDebugEnabled()) {
			logger.debug("return vip:" + vipType);
		}
	}

	// 头顶VIP标志广播
	public void changeVipTypeBroadcast() {
		Player player = getConcreteParent();
		byte vipType = getVipMgr().getVipType();
		PropertyDictionary pd = new PropertyDictionary();
		MGPropertyAccesser.setOrPutVipType(pd, vipType);
		MGPropertyAccesser.setOrPutVipType(player.getSenceProperty(), vipType);
		player.getAoiComponent().broadcastProperty(pd);

	}

	/**
	 * VIP过期
	 */
	private void cancelVip() {

		if (logger.isDebugEnabled()) {
			logger.debug("玩家 " + getConcreteParent().getName() + " 退出了VIP");
		}
		Player player = getConcreteParent();
		this.vipEffectMgr.detach(vipMgr.getVipLevelDataRef());
		this.vipMgr.resetVip();
		MGPropertyAccesser.setOrPutVipType(player.getProperty(), MGVipType.NO_VIP);
		player.setVipType(MGVipType.NO_VIP);
		PlayerImmediateDaoFacade.update(player);
		sendVipTypeActionEvent();
		changeVipTypeBroadcast();
		MGStatFunctions.vipStat(player, vipMgr.getVipType(), StatVip.Cancel);
		
		sendVipGameEvent(player, MGVipType.NO_VIP);
	}

	public MGVipLevelMgr getVipMgr() {
		return vipMgr;
	}

	public void setVipMgr(MGVipLevelMgr vipMgr) {
		this.vipMgr = vipMgr;
	}

	public MGVipEffectMgr getVipEffectMgr() {
		return vipEffectMgr;
	}

	public void setVipEffectMgr(MGVipEffectMgr vipEffectMgr) {
		this.vipEffectMgr = vipEffectMgr;
	}

	public PersistenceObject getPersistenceObject() {
		return persistenceObject;
	}

	public void setPersistenceObject(PersistenceObject persistenceObject) {
		this.persistenceObject = persistenceObject;
	}

	public MGVipLotteryMgr getLotteryMgr() {
		return lotteryMgr;
	}

	public void setLotteryMgr(MGVipLotteryMgr lotteryMgr) {
		this.lotteryMgr = lotteryMgr;
	}
}
