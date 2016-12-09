package newbee.morningGlory.mmorpg.operatActivities.impl;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
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

import sophia.mmorpg.MMORPGContext;
import sophia.mmorpg.code.MMORPGSuccessCode;
import sophia.mmorpg.event.ResultEvent;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.PlayerManager;
import sophia.mmorpg.player.itemBag.ItemOptSource;
import sophia.mmorpg.player.itemBag.ItemPair;
import sophia.mmorpg.player.persistence.immediatelySave.PlayerImmediateDaoFacade;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;
import sophia.mmorpg.ref.editorgen.MGPropertySymbolDefines;
import sophia.mmorpg.utils.Type;

/***
 * 周累计消费元宝<br>
 * Copyright (c) 2014 by 游爱.
 * 
 */
public class WeekTotalConsumeGift extends OperatActivity {
	private static final Logger logger = Logger.getLogger(WeekTotalConsumeGift.class);

	@Override
	public OperatActivityRef getRef() {
		return OperatActivityMgr.getInstance().getRef(OperatActivityType.WeekTotalConsumeGift);
	}

	@Override
	public void onPlayerPreLogin(Player player) {
		OperatActivityComponent operatActivityComponent = (OperatActivityComponent) player.getTagged(OperatActivityComponent.Tag);
		OperatActivityRecord record = operatActivityComponent.getOperatActivityRecord();

		long lastWeekTotalConsumeGiftsEndTime = record.getLastWeekTotalConsumeGiftsEndTime();
		if (lastWeekTotalConsumeGiftsEndTime != getRef().getEndTime().getTime()) {
			clearPlayerCharacterData(player);
			record.setLastWeekTotalConsumeGiftsEndTime(getRef().getEndTime().getTime());
		}

		long lastWeekConsumeStartTime = record.getLastWeekConsumeStartTime();
		long crtTime = System.currentTimeMillis();
		if (!isSameWeek(lastWeekConsumeStartTime, crtTime)) {
			clearPlayerCharacterData(player);
		}
		PlayerImmediateDaoFacade.update(player);
	}

	public void clearPlayerCharacterData(Player player) {
		OperatActivityComponent operatActivityComponent = (OperatActivityComponent) player.getTagged(OperatActivityComponent.Tag);
		OperatActivityRecord record = operatActivityComponent.getOperatActivityRecord();
		MGPropertyAccesser.setOrPutCrtWeekConsume(player.getProperty(), 0);
		record.setHadReceiveWeekConsumeGiftStage("");
		record.setLastWeekConsumeStartTime(0l);
		PlayerImmediateDaoFacade.update(player);
	}

	@Override
	public void modify(Object... objs) {
		Player player = (Player) objs[0];
		int value = Math.abs(Type.getInt(objs[1], 0));
		OperatActivityComponent operatActivityComponent = (OperatActivityComponent) player.getTagged(OperatActivityComponent.Tag);
		OperatActivityRecord record = operatActivityComponent.getOperatActivityRecord();
		if (value < 0)
			return;
		int crtWeekConsumeValue = MGPropertyAccesser.getCrtWeekConsume(player.getProperty());
		crtWeekConsumeValue = crtWeekConsumeValue < 0 ? 0 : crtWeekConsumeValue;
		crtWeekConsumeValue += value;
		if (logger.isDebugEnabled()) {
			logger.debug("本周已消费:" + crtWeekConsumeValue + "元宝");
		}
		MGPropertyAccesser.setOrPutCrtWeekConsume(player.getProperty(), crtWeekConsumeValue);
		record.setLastWeekConsumeStartTime(System.currentTimeMillis());
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
			long lastWeekConsumeStartTime = record.getLastWeekConsumeStartTime();
			long crtTime = System.currentTimeMillis();
			if (!isSameWeek(lastWeekConsumeStartTime, crtTime)) {
				clearPlayerCharacterData(player);
			}
			int crtWeekConsumeValue = 0;
			if (player.getProperty().contains(MGPropertySymbolDefines.CrtWeekConsume_Id)) {
				crtWeekConsumeValue = MGPropertyAccesser.getCrtWeekConsume(player.getProperty());
			}
			String hadReceiveWeekConsumeGiftStage = Type.getString(record.getHadReceiveWeekConsumeGiftStage(), "");
			List<String> list = Arrays.asList(hadReceiveWeekConsumeGiftStage.split("\\|"));
			AwardContent awardContent = getRef().getAwardContent();

			if (objs.length > 1) {
				String id = Type.getString(objs[1], "");
				if (!list.contains(id)) {
					AwardItem awardItem = awardContent.getAwardItem(id);
					if (awardItem != null) {
						String condValue = awardItem.getCondValue();
						if (crtWeekConsumeValue >= Type.getInt(condValue, 0))
							return true;
					}
				}
			} else {
				List<AwardItem> awardItems = awardContent.getAwardItems();
				for (AwardItem awardItem : awardItems) {
					if (!list.contains(awardItem.getId())) {
						String condValue = awardItem.getCondValue();
						if (crtWeekConsumeValue >= Type.getInt(condValue, 0))
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
			errorCode = rewardItems(player, items, ItemOptSource.WeekConsume);
			if (errorCode == MMORPGSuccessCode.CODE_SUCCESS) {
				OperatActivityComponent operatActivityComponent = (OperatActivityComponent) player.getTagged(OperatActivityComponent.Tag);
				OperatActivityRecord record = operatActivityComponent.getOperatActivityRecord();

				String receiveWeekConsumeGiftStage = Type.getString(record.getHadReceiveWeekConsumeGiftStage(), "");
				receiveWeekConsumeGiftStage = receiveWeekConsumeGiftStage + "|" + aid;
				record.setHadReceiveWeekConsumeGiftStage(receiveWeekConsumeGiftStage);
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
		PlayerManager playerManager = MMORPGContext.getPlayerComponent().getPlayerManager();
		Collection<Player> players = playerManager.getPlayerList();
		for (Player player : players) {
			if (player.isOnline()) {
				clearPlayerCharacterData(player);
			}
		}
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

	public static boolean isSameWeek(long lastTime, long crtTime) {

		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		cal1.setTimeInMillis(lastTime);
		cal2.setTimeInMillis(crtTime);
		cal1.setFirstDayOfWeek(Calendar.MONDAY);
		cal2.setFirstDayOfWeek(Calendar.MONDAY);
		int subYear = cal1.get(Calendar.YEAR) - cal2.get(Calendar.YEAR);
		// subYear==0,说明是同一年
		if (subYear == 0) {
			if (cal1.get(Calendar.WEEK_OF_YEAR) == cal2.get(Calendar.WEEK_OF_YEAR))
				return true;
		}

		return false;
	}
}
