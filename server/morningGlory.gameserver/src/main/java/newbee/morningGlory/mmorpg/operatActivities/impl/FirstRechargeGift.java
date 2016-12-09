package newbee.morningGlory.mmorpg.operatActivities.impl;

import java.util.ArrayList;
import java.util.List;

import newbee.morningGlory.code.MGErrorCode;
import newbee.morningGlory.mmorpg.operatActivities.OperatActivity;
import newbee.morningGlory.mmorpg.operatActivities.OperatActivityMgr;
import newbee.morningGlory.mmorpg.operatActivities.OperatActivityRef;
import newbee.morningGlory.mmorpg.operatActivities.OperatActivityType;
import newbee.morningGlory.mmorpg.operatActivities.awardContent.AwardItem;
import sophia.foundation.property.PropertyDictionary;
import sophia.mmorpg.code.MMORPGSuccessCode;
import sophia.mmorpg.event.ResultEvent;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.itemBag.ItemOptSource;
import sophia.mmorpg.player.itemBag.ItemPair;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;
import sophia.mmorpg.ref.editorgen.MGPropertySymbolDefines;
import sophia.mmorpg.utils.Type;
import edu.emory.mathcs.backport.java.util.Arrays;

/**
 * 首充礼包<br>
 * 
 */
public class FirstRechargeGift extends OperatActivity {
	/** 从未充值 */
	public static final byte NotRecharge = 0;
	/** 已充值，但未领取 */
	public static final byte RechargeButNotReceive = 1;
	/** 已充值，并已领取 */
	public static final byte RechargeAndReceive = 2;

	@Override
	public OperatActivityRef getRef() {
		return OperatActivityMgr.getInstance().getRef(OperatActivityType.FirstRechargeGift);
	}

	@Override
	public void modify(Object... objs) {
		Player player = (Player) objs[0];
		PropertyDictionary pd = player.getProperty();
		byte isFirstRecharge = MGPropertyAccesser.getIsFirstRecharge(pd);
		if (isFirstRecharge != RechargeAndReceive) {
			MGPropertyAccesser.setOrPutIsFirstRecharge(pd,RechargeButNotReceive);
			long crtTime = System.currentTimeMillis();
			MGPropertyAccesser.setOrPutFirstRechargeTime(player.getProperty(),crtTime);
			OperatActivityMgr.getInstance().sendAllCanReceiveMsg(player);
		}
		
	}

	@Override
	public boolean canReceiveAward(Object... objs) {
		Player player = (Player) objs[0];
		PropertyDictionary pd = player.getProperty();
		byte isFirstRecharge = 0;
		if (pd.contains(MGPropertySymbolDefines.FirstRechargeTime_Id)) {
			isFirstRecharge = MGPropertyAccesser.getIsFirstRecharge(pd);
		} else {
			isFirstRecharge = 0; 
		}
		return isFirstRecharge == RechargeButNotReceive;
	}

	@Override
	public void receiveAward(Object... objs) {
		Player player = (Player) objs[0];
		short actionEventId = Type.getShort(objs[1], (short) 0);
		int errorCode = 0;
		if (canReceiveAward(objs)) {
			List<ItemPair> list = new ArrayList<ItemPair>();
			List<AwardItem> awardItems = getRef().getAwardContent().getAwardItems();
			String itemOtherData = null;
			for (AwardItem awardItem : awardItems) {
				List<ItemPair> items = awardItem.getItems();
				list.addAll(items);
				itemOtherData = awardItem.getItemOtherData();
			}
			List<String> weapons = Arrays.asList(itemOtherData.split("\\|"));
			String weaponRefId = null;
			if (weapons.size() > 0) {
				if (weapons.size() == 1) {
					weaponRefId = weapons.get(0);
				} else if (weapons.size() == 3) {
					weaponRefId = weapons.get(player.getProfession() - 1);
				}
				ItemPair weaponPair = new ItemPair(weaponRefId, 1, false);
				list.add(weaponPair);
			}
			errorCode = rewardItems(player, list,ItemOptSource.FirstRecharge);
			if (errorCode == MMORPGSuccessCode.CODE_SUCCESS) {

				MGPropertyAccesser.setOrPutIsFirstRecharge(player.getProperty(),RechargeAndReceive);
			}
		} else {
			errorCode = MGErrorCode.CODE_OA_CANNOT_RECEIVE;

		}

		ResultEvent.sendResult(player.getIdentity(), actionEventId, errorCode);
	}

	@Override
	public boolean isOpening(Player player) {		
		PropertyDictionary pd = player.getProperty();
		byte isFirstRecharge = MGPropertyAccesser.getIsFirstRecharge(pd);
		return isFirstRecharge != RechargeAndReceive;
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
		// TODO Auto-generated method stub

	}

	@Override
	public void onOperatActivityStart() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onOperatActivityEnd() {
		// TODO Auto-generated method stub

	}

}
