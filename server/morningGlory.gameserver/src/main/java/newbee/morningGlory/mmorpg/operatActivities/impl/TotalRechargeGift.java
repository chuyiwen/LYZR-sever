package newbee.morningGlory.mmorpg.operatActivities.impl;

import java.util.Arrays;
import java.util.List;

import newbee.morningGlory.code.MGErrorCode;
import newbee.morningGlory.mmorpg.operatActivities.OperatActivity;
import newbee.morningGlory.mmorpg.operatActivities.OperatActivityComponent;
import newbee.morningGlory.mmorpg.operatActivities.OperatActivityMgr;
import newbee.morningGlory.mmorpg.operatActivities.OperatActivityRecord;
import newbee.morningGlory.mmorpg.operatActivities.OperatActivityRef;
import newbee.morningGlory.mmorpg.operatActivities.OperatActivityType;
import newbee.morningGlory.mmorpg.operatActivities.awardContent.AwardContent;
import newbee.morningGlory.mmorpg.operatActivities.awardContent.AwardItem;

import org.apache.log4j.Logger;

import sophia.foundation.property.PropertyDictionary;
import sophia.mmorpg.code.MMORPGSuccessCode;
import sophia.mmorpg.event.ResultEvent;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.itemBag.ItemOptSource;
import sophia.mmorpg.player.itemBag.ItemPair;
import sophia.mmorpg.player.persistence.immediatelySave.PlayerImmediateDaoFacade;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;
import sophia.mmorpg.utils.Type;

/***
 * 累计充值<br>
 * Copyright (c) 2014 by 游爱.
 * 
 */
public class TotalRechargeGift extends OperatActivity {
	private static final Logger logger = Logger.getLogger(TotalRechargeGift.class);

	@Override
	public OperatActivityRef getRef() {
		return OperatActivityMgr.getInstance().getRef(OperatActivityType.TotalRechargeGift);
	}

	@Override
	public void onPlayerPreLogin(Player player) {
		OperatActivityComponent operatActivityComponent = (OperatActivityComponent) player.getTagged(OperatActivityComponent.Tag);
		OperatActivityRecord record = operatActivityComponent.getOperatActivityRecord();

		long lastTotalRechargeGiftsEndTime = record.getLastTotalRechargeGiftsEndTime();
		if (lastTotalRechargeGiftsEndTime != getRef().getEndTime().getTime()) {
			clearPlayerCharacterData(player);
			record.setLastTotalRechargeGiftsEndTime(getRef().getEndTime().getTime());
		}
		PlayerImmediateDaoFacade.update(player);
	}

	public void clearPlayerCharacterData(Player player) {
		OperatActivityComponent operatActivityComponent = (OperatActivityComponent) player.getTagged(OperatActivityComponent.Tag);
		OperatActivityRecord record = operatActivityComponent.getOperatActivityRecord();
		MGPropertyAccesser.setOrPutTotalRecharge(player.getProperty(), 0);
		record.setHadReceiveRechargeGiftStage("");
		PlayerImmediateDaoFacade.update(player);
	}

	@Override
	public void modify(Object... objs) {
		Player player = (Player) objs[0];
		int value = Math.abs(Type.getInt(objs[1], 0));

		PropertyDictionary pd = player.getProperty();
		int crtRechargeValue = MGPropertyAccesser.getTotalRecharge(pd);
		int crtRechargeCount = MGPropertyAccesser.getTotalRechargeCount(pd);
		crtRechargeValue = crtRechargeValue < 0 ? 0 : crtRechargeValue;
		crtRechargeCount = crtRechargeCount < 0 ? 0 : crtRechargeCount;
		crtRechargeValue += value;
		crtRechargeCount++;
		MGPropertyAccesser.setOrPutTotalRecharge(pd, crtRechargeValue);
		MGPropertyAccesser.setOrPutTotalRechargeCount(pd, crtRechargeCount);
		MGPropertyAccesser.setOrPutLastRechargeTime(pd, System.currentTimeMillis());
		if (logger.isDebugEnabled()) {
			logger.debug("玩家" + player.getName() + "已累计充值:" + crtRechargeValue + "元宝");
		}
		if (canReceiveAward(player)) {
			OperatActivityMgr.getInstance().sendAllCanReceiveMsg(player);
		}
		PlayerImmediateDaoFacade.update(player);
	}

	@Override
	public boolean canReceiveAward(Object... objs) {
		if (isOpening()) {
			Player player = (Player) objs[0];
			OperatActivityComponent operatActivityComponent = (OperatActivityComponent) player.getTagged(OperatActivityComponent.Tag);
			OperatActivityRecord record = operatActivityComponent.getOperatActivityRecord();

			PropertyDictionary pd = player.getProperty();
			int totalRechargeGiftValue = MGPropertyAccesser.getTotalRecharge(pd);
			String hadReceiveRechargeGiftStage = Type.getString(record.getHadReceiveRechargeGiftStage(), "");
			List<String> list = Arrays.asList(hadReceiveRechargeGiftStage.split("\\|"));
			AwardContent awardContent = getRef().getAwardContent();

			if (objs.length > 1) {
				String id = Type.getString(objs[1], "");
				if (!list.contains(id)) {
					AwardItem awardItem = awardContent.getAwardItem(id);
					if (awardItem != null) {
						String condValue = awardItem.getCondValue();
						if (totalRechargeGiftValue >= Type.getInt(condValue, 0))
							return true;
					}
				}
			} else {
				List<AwardItem> awardItems = awardContent.getAwardItems();
				for (AwardItem awardItem : awardItems) {
					if (!list.contains(awardItem.getId())) {
						String condValue = awardItem.getCondValue();
						if (totalRechargeGiftValue >= Type.getInt(condValue, 0))
							return true;
					}
				}
			}
		}
		return false;
	}

	@Override
	public void receiveAward(Object... objs) {
		Player player = (Player) objs[0];
		String aid = Type.getString(objs[1], "");
		short actionEventId = Type.getShort(objs[2], (short) 0);
		int errorCode = MMORPGSuccessCode.CODE_SUCCESS;
		if (canReceiveAward(objs)) {
			AwardContent awardContent = getRef().getAwardContent();
			AwardItem awardItem = awardContent.getAwardItem(aid);
			List<ItemPair> items = awardItem.getItems();
			errorCode = rewardItems(player, items,ItemOptSource.TotalRecharge);
			if (errorCode == MMORPGSuccessCode.CODE_SUCCESS) {
				OperatActivityComponent operatActivityComponent = (OperatActivityComponent) player.getTagged(OperatActivityComponent.Tag);
				OperatActivityRecord record = operatActivityComponent.getOperatActivityRecord();

				String receiveTotalRechargeGifts = Type.getString(record.getHadReceiveRechargeGiftStage(), "");
				receiveTotalRechargeGifts = receiveTotalRechargeGifts + "|" + aid;
				record.setHadReceiveRechargeGiftStage(receiveTotalRechargeGifts);
				PlayerImmediateDaoFacade.update(player);
			}
		} else {
			errorCode = MGErrorCode.CODE_OA_CANNOT_RECEIVE;
		}
		ResultEvent.sendResult(player.getIdentity(), actionEventId, errorCode);
	}

	@Override
	public void onOperatActivityStart() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onEnterNewDay() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onEnterNewWeek() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onEnterNewMonth() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onMinute() {

	}

	@Override
	public void onOperatActivityEnd() {
		// TODO Auto-generated method stub

	}

}
