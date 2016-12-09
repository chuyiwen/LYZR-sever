package newbee.morningGlory.mmorpg.operatActivities.impl;

import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import newbee.morningGlory.code.MGErrorCode;
import newbee.morningGlory.mmorpg.operatActivities.OperatActivity;
import newbee.morningGlory.mmorpg.operatActivities.OperatActivityComponent;
import newbee.morningGlory.mmorpg.operatActivities.OperatActivityMgr;
import newbee.morningGlory.mmorpg.operatActivities.OperatActivityRecord;
import newbee.morningGlory.mmorpg.operatActivities.OperatActivityRef;
import newbee.morningGlory.mmorpg.operatActivities.OperatActivityType;
import newbee.morningGlory.mmorpg.operatActivities.awardContent.AwardContent;
import newbee.morningGlory.mmorpg.operatActivities.awardContent.AwardItem;
import sophia.mmorpg.MMORPGContext;
import sophia.mmorpg.code.MMORPGSuccessCode;
import sophia.mmorpg.event.ResultEvent;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.PlayerManager;
import sophia.mmorpg.player.itemBag.ItemOptSource;
import sophia.mmorpg.player.itemBag.ItemPair;
import sophia.mmorpg.player.persistence.immediatelySave.PlayerImmediateDaoFacade;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;
import sophia.mmorpg.utils.Type;

/**
 * 日充值礼包<br>
 * 
 */
public class EveryDayRechargeGift extends OperatActivity {
	/** 今日未充值 */
	public static final byte NotRecharge = 0;
	/** 今日已充值，但未领取 */
	public static final byte RechargeButNotReceive = 1;
	/** 今日已充值，并已领取 */
	public static final byte RechargeAndReceive = 2;

	@Override
	public OperatActivityRef getRef() {
		return OperatActivityMgr.getInstance().getRef(OperatActivityType.EveryDayRechargeGift);
	}

	@Override
	public void onPlayerPreLogin(Player player) {
		OperatActivityComponent operatActivityComponent = (OperatActivityComponent) player.getTagged(OperatActivityComponent.Tag);
		OperatActivityRecord record = operatActivityComponent.getOperatActivityRecord();

		long lastDayRechargeGiftsEndTime = record.getLastDayRechargeGiftsEndTime();
		if (lastDayRechargeGiftsEndTime != getRef().getEndTime().getTime()) {
			clearPlayerCharacterData(player);
			record.setLastDayRechargeGiftsEndTime(getRef().getEndTime().getTime());
		}
		long lastLoginTime = MGPropertyAccesser.getLastLoginTime(player.getProperty());
		if (!isOpening(lastLoginTime)) {
			clearPlayerCharacterData(player);
		}

		if (!isSameDay(player)) {
			clearPlayerCharacterData(player);
		}
	}

	@Override
	public void modify(Object... objs) {
		Player player = (Player) objs[0];
		OperatActivityComponent operatActivityComponent = (OperatActivityComponent) player.getTagged(OperatActivityComponent.Tag);
		OperatActivityRecord record = operatActivityComponent.getOperatActivityRecord();
		if (record.getDayRecharge() != RechargeAndReceive) {
			record.setDayRecharge(RechargeButNotReceive);
			OperatActivityMgr.getInstance().sendAllCanReceiveMsg(player);
			PlayerImmediateDaoFacade.update(player);
		}

	}

	@Override
	public boolean canReceiveAward(Object... objs) {
		Player player = (Player) objs[0];
		OperatActivityComponent operatActivityComponent = (OperatActivityComponent) player.getTagged(OperatActivityComponent.Tag);
		OperatActivityRecord record = operatActivityComponent.getOperatActivityRecord();

		return record.getDayRecharge() == RechargeButNotReceive;
	}

	@Override
	public void receiveAward(Object... objs) {
		Player player = (Player) objs[0];
		short actionEventId = Type.getShort(objs[1], (short) 0);
		OperatActivityComponent operatActivityComponent = (OperatActivityComponent) player.getTagged(OperatActivityComponent.Tag);
		OperatActivityRecord record = operatActivityComponent.getOperatActivityRecord();
		String aid = record.getEveryDayrewardId().split("\\|")[0];
		int errorCode = 0;
		if (canReceiveAward(objs)) {
			AwardContent awardContent = getRef().getAwardContent();
			AwardItem awardItem = awardContent.getAwardItem(aid);
			List<ItemPair> items = awardItem.getItems();
			errorCode = rewardItems(player, items, ItemOptSource.DayRecharge);
			if (errorCode == MMORPGSuccessCode.CODE_SUCCESS) {

				record.setDayRecharge(RechargeAndReceive);
				PlayerImmediateDaoFacade.update(player);
			}
		} else {
			errorCode = MGErrorCode.CODE_OA_CANNOT_RECEIVE;

		}

		ResultEvent.sendResult(player.getIdentity(), actionEventId, errorCode);
	}

	@Override
	public boolean isOpening(Player player) {
		OperatActivityComponent operatActivityComponent = (OperatActivityComponent) player.getTagged(OperatActivityComponent.Tag);
		OperatActivityRecord record = operatActivityComponent.getOperatActivityRecord();
		return record.getDayRecharge() != RechargeAndReceive;
	}

	@Override
	public void onEnterNewDay() {
		PlayerManager playerManager = MMORPGContext.getPlayerComponent().getPlayerManager();
		Collection<Player> players = playerManager.getPlayerList();
		for (Player player : players) {
			clearPlayerCharacterData(player);
			OperatActivityComponent operatActivityComponent = (OperatActivityComponent) player.getTagged(OperatActivityComponent.Tag);
			OperatActivityRecord record = operatActivityComponent.getOperatActivityRecord();
			if (!StringUtils.isEmpty(record.getEveryDayrewardId())) {
				String[] rewardId = record.getEveryDayrewardId().split("\\|");
				int day = Type.getInt(rewardId[1], 0);
				day++;
				if (day > 5) {
					record.setEveryDayrewardId("");
				}
				PlayerImmediateDaoFacade.update(player);
			}
		}
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

	@Override
	public void clearPlayerCharacterData(Player player) {
		OperatActivityComponent operatActivityComponent = (OperatActivityComponent) player.getTagged(OperatActivityComponent.Tag);
		OperatActivityRecord record = operatActivityComponent.getOperatActivityRecord();
		record.setDayRecharge(NotRecharge);
		PlayerImmediateDaoFacade.update(player);

	}

	public boolean isSameDay(Player player) {
		long lastLogoutTime = MGPropertyAccesser.getLastLogoutTime(player.getProperty());
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(lastLogoutTime);
		int year1 = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

		calendar.setTimeInMillis(System.currentTimeMillis());
		int crtDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
		int crtMonth = calendar.get(Calendar.MONTH);
		int year2 = calendar.get(Calendar.YEAR);
		if (year1 != year2) {
			return false;
		}
		if (month != crtMonth) {
			return false;
		}
		if (dayOfMonth != crtDayOfMonth) {
			return false;
		}
		return true;
	}
}
