package newbee.morningGlory.mmorpg.player.activity.fund;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import newbee.morningGlory.code.MGErrorCode;
import newbee.morningGlory.mmorpg.player.activity.fund.event.C2G_Fund_ApplyVersionByType;
import newbee.morningGlory.mmorpg.player.activity.fund.event.C2G_Fund_BuyWhichFund;
import newbee.morningGlory.mmorpg.player.activity.fund.event.C2G_Fund_FundGetRewardList;
import newbee.morningGlory.mmorpg.player.activity.fund.event.C2G_Fund_GetReward;
import newbee.morningGlory.mmorpg.player.activity.fund.event.C2G_Fund_IsReceive;
import newbee.morningGlory.mmorpg.player.activity.fund.event.FundDefines;
import newbee.morningGlory.mmorpg.player.activity.fund.event.G2C_Fund_BuyWhichFund;
import newbee.morningGlory.mmorpg.player.activity.fund.event.G2C_Fund_FundGetRewardList;
import newbee.morningGlory.mmorpg.player.activity.fund.event.G2C_Fund_GetReward;
import newbee.morningGlory.mmorpg.player.activity.fund.event.G2C_Fund_IsReceive;
import newbee.morningGlory.mmorpg.player.activity.fund.event.G2C_Fund_ReturnVersion;
import newbee.morningGlory.mmorpg.player.activity.fund.ref.FundRef;
import newbee.morningGlory.stat.MGStatFunctions;
import newbee.morningGlory.stat.logs.StatFund;

import org.apache.log4j.Logger;

import com.google.gson.Gson;

import sophia.foundation.communication.core.ActionEventBase;
import sophia.foundation.communication.core.MessageFactory;
import sophia.game.GameRoot;
import sophia.game.component.communication.GameEvent;
import sophia.mmorpg.Mail.Mail;
import sophia.mmorpg.Mail.MailMgr;
import sophia.mmorpg.base.ConcreteComponent;
import sophia.mmorpg.code.MMORPGErrorCode;
import sophia.mmorpg.event.ResultEvent;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.itemBag.ItemFacade;
import sophia.mmorpg.player.itemBag.ItemOptSource;
import sophia.mmorpg.player.itemBag.ItemPair;
import sophia.mmorpg.player.money.PlayerMoneyComponent;
import sophia.mmorpg.player.persistence.immediatelySave.PlayerImmediateDaoFacade;
import sophia.mmorpg.utils.RuntimeResult;

/**
 * 基金活动组件
 * 
 * @author lixing
 * 
 */
public class FundActivityComponet extends ConcreteComponent<Player> {
	private static Logger logger = Logger.getLogger(FundActivityComponet.class);
	public static final String Tag = "FundActivityComponet";

	/**
	 * 玩家的基金数据 key 基金类型 value 基金数据
	 */
	private HashMap<FundType, FundDataByType> fundMap = new HashMap<FundType, FundDataByType>();

	public HashMap<FundType, FundDataByType> getFundMap() {
		return fundMap;
	}

	public FundDataByType getFundDataByType(FundType funType) {
		FundDataByType data = this.fundMap.get(funType);
		if (data == null) {
			data = new FundDataByType(funType);
			this.fundMap.put(funType, data);
		}
		return data;
	}

	@Override
	public void ready() {
		addActionEventListener(FundDefines.C2G_Fund_ApplyVersionByType);
		addActionEventListener(FundDefines.C2G_Fund_BuyWhichFund);
		addActionEventListener(FundDefines.C2G_Fund_FundGetRewardList);
		addActionEventListener(FundDefines.C2G_Fund_GetReward);
		addActionEventListener(FundDefines.C2G_Fund_IsReceive);
		super.ready();
	}

	@Override
	public void suspend() {
		removeActionEventListener(FundDefines.C2G_Fund_ApplyVersionByType);
		removeActionEventListener(FundDefines.C2G_Fund_BuyWhichFund);
		removeActionEventListener(FundDefines.C2G_Fund_FundGetRewardList);
		removeActionEventListener(FundDefines.C2G_Fund_GetReward);
		removeActionEventListener(FundDefines.C2G_Fund_IsReceive);
		super.suspend();
	}

	@Override
	public void handleGameEvent(GameEvent<?> event) {

		super.handleGameEvent(event);
	}

	@Override
	public void handleActionEvent(ActionEventBase event) {
		short eventId = event.getActionEventId();
		if (logger.isDebugEnabled()) {
			logger.debug("基金活动事件Id:" + eventId);
		}

		switch (eventId) {
		case FundDefines.C2G_Fund_ApplyVersionByType:
			handle_ApplyVersionByType((C2G_Fund_ApplyVersionByType) event);
			break;
		case FundDefines.C2G_Fund_BuyWhichFund:
			handle_BuyWhichFund((C2G_Fund_BuyWhichFund) event);
			break;
		case FundDefines.C2G_Fund_FundGetRewardList:
			handle_FundGetRewardList((C2G_Fund_FundGetRewardList) event);
			break;
		case FundDefines.C2G_Fund_GetReward:
			handle_GetReward((C2G_Fund_GetReward) event);
			break;
		case FundDefines.C2G_Fund_IsReceive:
			handle_IsReceive((C2G_Fund_IsReceive) event);
			break;
		}
		PlayerImmediateDaoFacade.update(getConcreteParent());
		super.handleActionEvent(event);
	}

	private void handle_IsReceive(C2G_Fund_IsReceive event) {
		G2C_Fund_IsReceive res = MessageFactory.getConcreteMessage(FundDefines.G2C_Fund_IsReceive);
		if (this.fundMap.isEmpty()) {
			res.setCount(0);
			GameRoot.sendMessage(getConcreteParent().getIdentity(), res);
			return;
		}
		res.setCount(this.fundMap.size());// 基金数量
		Map<Byte, Byte> isReceiveMap = new HashMap<Byte, Byte>();
		for (Map.Entry<FundType, FundDataByType> entry : fundMap.entrySet()) {
			FundDataByType data = entry.getValue();
			if (!data.isHaveRightGetReward()) {// 判断玩家是否有领奖权限（是否有该基金，是否过期）
				isReceiveMap.put(((byte) entry.getKey().getType()), (byte) 1);// 不可领取
				continue;
			}
			int day = data.getBettwenDays();// 相差几天
			byte record = data.getGetRewardRecordByDay(day + 1);// 当天领奖记录
			if (record == 1) {// 已经领取奖励
				isReceiveMap.put(((byte) entry.getKey().getType()), (byte) 1);// 不可领取
				continue;
			}
			isReceiveMap.put(((byte) entry.getKey().getType()), (byte) 0);// 可领取
		}
		res.setMap(isReceiveMap);
		GameRoot.sendMessage(getConcreteParent().getIdentity(), res);
	}

	private void handle_GetReward(C2G_Fund_GetReward event) {
		byte type = event.getType();
		int day = event.getDay();
		Player player = getConcreteParent();

		FundType fundType = FundType.getFundType((int) type);
		if (fundType == null) {
			logger.error("getReward:error argument! type = " + event.getType());
			return;
		}

		if (day < 1 || day > 30) {
			logger.error("inValid argument! day = " + day);
			return;
		}

		RuntimeResult result = getReward(type, day);
		G2C_Fund_GetReward res = MessageFactory.getConcreteMessage(FundDefines.G2C_Fund_GetReward);
		res.setType(event.getType());

		if (result.isOK()) {
			MGStatFunctions.fundStat(player, event.getType(), StatFund.Get, (byte) event.getDay(), (byte) -1, -1L);
			res.setResult((byte) 1);
			GameRoot.sendMessage(player.getIdentity(), res);
		} else {
			ResultEvent.sendResult(player.getIdentity(), event.getActionEventId(), result.getApplicationCode());
		}

	}

	private RuntimeResult getReward(int type, int day) {
		Player player = getConcreteParent();
		RuntimeResult result = RuntimeResult.ParameterError();
		FundType funType = FundType.getFundType(type);
		FundDataByType data = this.getFundDataByType(funType);
		if (!data.isHaveRightGetReward()) {
			result = RuntimeResult.RuntimeApplicationError(MGErrorCode.CODE_OA_NO_BUY);
			return result;
		}

		byte record = data.getGetRewardRecordByDay(day);
		if (data.isOverDay(day)) {
			result = RuntimeResult.RuntimeApplicationError(MGErrorCode.CODE_OA_OVERDAY);
			return result;
		} else if (data.isGetRewardTimeout(day)) {
			result = RuntimeResult.RuntimeApplicationError(MGErrorCode.CODE_OA_INVALID);
			return result;
		} else if (record == 1) {
			result = RuntimeResult.RuntimeApplicationError(MGErrorCode.CODE_OA_HAD_RECEIVED);
			return result;
		} else {
			int dayIndex = data.getBettwenDays();

			ItemPair pair = FundMgr.getGiftMapByFundType(funType).getGift(dayIndex);
			List<ItemPair> itemPairs = new ArrayList<ItemPair>();
			itemPairs.add(pair);
			
			if(ItemFacade.isItemBagSlotEnough(getConcreteParent(), itemPairs)) {
				ItemFacade.addItemCompareSlot(getConcreteParent(), itemPairs, ItemOptSource.FundActivity);
			} else {
				String content = "遮天基金奖励通过邮件发送";
				String jsonString = (new Gson()).toJson(itemPairs);
				MailMgr.sendMailById(getConcreteParent().getId(), content, Mail.huodong, jsonString, 0, 0, 0);
				ResultEvent.sendResult(player.getIdentity(), FundDefines.C2G_Fund_GetReward, MMORPGErrorCode.CODE_ITEM_SendBYMail);
			}

			data.recordGetReward();
			result = RuntimeResult.OK();
		}
		return result;
	}

	private void handle_FundGetRewardList(C2G_Fund_FundGetRewardList event) {
		FundType funType = FundType.getFundType((int) event.getType());
		if (funType == null) {
			logger.error("rewardList : error argument! type = " + event.getType());
			return;
		}

		FundDataByType data = this.getFundDataByType(funType);
		G2C_Fund_FundGetRewardList res = MessageFactory.getConcreteMessage(FundDefines.G2C_Fund_FundGetRewardList);
		res.setType(event.getType());
		res.setGetRecord(data.getGetRewardRecord());
		res.setCount(data.getBettwenDays() + 1);
		GameRoot.sendMessage(getConcreteParent().getIdentity(), res);
	}

	private void handle_BuyWhichFund(C2G_Fund_BuyWhichFund event) {
		FundType fundType = FundType.getFundType((int) event.getType());
		if (fundType == null) {
			logger.error("buy fund : error argument! type = " + event.getType());
			return;
		}

		RuntimeResult result = payMoney(event.getType(), getConcreteParent());
		G2C_Fund_BuyWhichFund res = MessageFactory.getConcreteMessage(FundDefines.G2C_Fund_BuyWhichFund);
		res.setType(event.getType());
		if (!result.isOK()) {
			ResultEvent.sendResult(getConcreteParent().getIdentity(), event.getActionEventId(), result.getCode());
			res.setResult((byte) 0);
		} else {
			FundType funType = FundType.getFundType((int) event.getType());
			this.getFundDataByType(funType).setBuyFundTime(System.currentTimeMillis());
			this.getFundDataByType(funType).setGetRewardRecord(new byte[FundMgr.getGiftMapByFundType(funType).getGiftArrays().length]);

			res.setResult((byte) 1);

			FundRef fundRef = FundMgr.getGiftMap().get(funType);
			byte moneyType = fundRef.getMoneyType();
			long moneyNum = fundRef.getBuyPrice();
			MGStatFunctions.fundStat(getConcreteParent(), event.getType(), StatFund.Buy, (byte) 0, moneyType, moneyNum);
		}
		GameRoot.sendMessage(getConcreteParent().getIdentity(), res);

	}

	private void handle_ApplyVersionByType(C2G_Fund_ApplyVersionByType event) {
		FundType fundType = FundType.getFundType((int) event.getType());

		if (fundType == null) {
			logger.error("version : error argument! type = " + event.getType());
			return;
		}

		G2C_Fund_ReturnVersion res = MessageFactory.getConcreteMessage(FundDefines.G2C_Fund_ReturnVersion);
		res.setType(event.getType());
		res.setVersion(this.getFundDataByType(fundType).getVersion());
		GameRoot.sendMessage(getConcreteParent().getIdentity(), res);
	}

	/**
	 * 购买基金
	 * 
	 * @param type
	 * @param player
	 * @return
	 */
	private static RuntimeResult payMoney(int type, Player player) {
		RuntimeResult absent = RuntimeResult.OK();
		FundType fundType = FundType.getFundType(type);
		FundRef ref = FundMgr.getGiftMapByFundType(fundType);
		int buyPrice = ref.getBuyPrice();
		int moneyType = ref.getMoneyType();
		PlayerMoneyComponent playMoneyCompoent = player.getPlayerMoneyComponent();
		switch (moneyType) {
		case 1:
			int ownGold = playMoneyCompoent.getGold();
			if (ownGold >= buyPrice) {
				playMoneyCompoent.subGold(buyPrice, ItemOptSource.FundActivity);
				if (logger.isDebugEnabled()) {
					logger.warn("购买基金，扣除金币" + buyPrice);
				}
			} else {
				absent = RuntimeResult.RuntimeApplicationError(MGErrorCode.CODE_STORE_NOT_ENOUGHT_GOLD);
			}
			break;
		case 2:
			int ownMoney = playMoneyCompoent.getUnbindGold();
			if (ownMoney >= buyPrice) {
				playMoneyCompoent.subUnbindGold(buyPrice, ItemOptSource.FundActivity);
				if (logger.isDebugEnabled()) {
					logger.warn("购买基金，扣除元宝" + buyPrice);
				}
			} else {
				absent = RuntimeResult.RuntimeApplicationError(MGErrorCode.CODE_STORE_NOT_ENOUGHT_UNBINDGOLD);
			}
			break;
		default:
			absent = RuntimeResult.ParameterError();
			break;
		}
		return absent;
	}
}
