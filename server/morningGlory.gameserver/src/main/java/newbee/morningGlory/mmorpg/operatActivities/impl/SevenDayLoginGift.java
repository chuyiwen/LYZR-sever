package newbee.morningGlory.mmorpg.operatActivities.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import newbee.morningGlory.mmorpg.operatActivities.OperatActivity;
import newbee.morningGlory.mmorpg.operatActivities.OperatActivityComponent;
import newbee.morningGlory.mmorpg.operatActivities.OperatActivityMgr;
import newbee.morningGlory.mmorpg.operatActivities.OperatActivityRecord;
import newbee.morningGlory.mmorpg.operatActivities.OperatActivityRef;
import newbee.morningGlory.mmorpg.operatActivities.OperatActivityType;
import newbee.morningGlory.mmorpg.operatActivities.awardContent.AwardContent;
import newbee.morningGlory.mmorpg.operatActivities.awardContent.AwardItem;
import newbee.morningGlory.mmorpg.operatActivities.event.OperatActivityDefines;
import newbee.morningGlory.mmorpg.operatActivities.event.sevenLogin.G2C_OA_SevenLogin_HaveReceive;

import org.apache.commons.lang3.StringUtils;

import sophia.foundation.communication.core.MessageFactory;
import sophia.game.GameRoot;
import sophia.mmorpg.MMORPGContext;
import sophia.mmorpg.code.MMORPGSuccessCode;
import sophia.mmorpg.event.ResultEvent;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.PlayerManager;
import sophia.mmorpg.player.itemBag.ItemOptSource;
import sophia.mmorpg.player.persistence.immediatelySave.PlayerImmediateDaoFacade;
import sophia.mmorpg.utils.DateTimeUtil;
import sophia.mmorpg.utils.Type;

/**
 * 七日登陆礼包<br>
 * Copyright (c) 2012 by 游爱.
 * 
 * @author 梁广 Create on 2013-9-14 下午9:39:44
 * 
 * @version 1.0
 */
public class SevenDayLoginGift extends OperatActivity {

	@Override
	public OperatActivityRef getRef() {
		return OperatActivityMgr.getInstance().getRef(OperatActivityType.SevenDayLoginGift);
	}

	@Override
	public void onPlayerPreLogin(Player player) {
		OperatActivityComponent operatActivityComponent = (OperatActivityComponent) player.getTagged(OperatActivityComponent.Tag);
		OperatActivityRecord record = operatActivityComponent.getOperatActivityRecord();
		int whichDay = whichDay(record.getOpenServerDate());

		String hadSevenLoginStage = Type.getString(record.getHadSevenLoginStage(), "");
		List<String> list = Arrays.asList(hadSevenLoginStage.split("\\|"));
		if (!list.contains(whichDay + "") && whichDay <= 7) {

			hadSevenLoginStage = hadSevenLoginStage + "|" + whichDay;
			record.setHadSevenLoginStage(hadSevenLoginStage);
			PlayerImmediateDaoFacade.update(player);
			OperatActivityMgr.getInstance().sendAllCanReceiveMsg(player);
		}

		sendCanReceiveTimes(player);
	//	record.setOpenServerDate(record.getOpenServerDate() - 1 * 24 * 3600 * 1000l);
	}

	@Override
	public void modify(Object... objs) {
	}

	@Override
	public boolean canReceiveAward(Object... objs) {
		Player player = (Player) objs[0];
		OperatActivityComponent operatActivityComponent = (OperatActivityComponent) player.getTagged(OperatActivityComponent.Tag);
		OperatActivityRecord record = operatActivityComponent.getOperatActivityRecord();

		String receivedSevenLoginStage = Type.getString(record.getHadReceiveSevenLoginStage(), "");
		List<String> list = Arrays.asList(receivedSevenLoginStage.split("\\|"));

		String hadsevenLoginStage = Type.getString(record.getHadSevenLoginStage(), "");
		List<String> loginList = Arrays.asList(hadsevenLoginStage.split("\\|"));
		if (getRef() == null) {
			return false;
		}
		AwardContent awardContent = getRef().getAwardContent();
		if (objs.length == 2) {
			int whichDay = Type.getInt(objs[1], 1);
			for (int i = 1; i <= whichDay; i++) {
				if (loginList.contains(i + "")) {
					for (AwardItem awardItem : awardContent.getAwardItems()) {
						if (awardItem != null && Type.getInt(awardItem.getCondValue(), 0) == i) {
							if (!list.contains(awardItem.getId())) {
								return true;
							}
						}
					}
				}
			}
		}
		if (objs.length == 3) {
			String id = Type.getString(objs[1], "");
			int whichDay = Type.getInt(objs[2], 1);
			if (!list.contains(id)) {
				AwardItem awardItem = awardContent.getAwardItem(id);
				if (awardItem != null) {
					int day = Type.getInt(awardItem.getCondValue(), 0);
					if (day <= whichDay && loginList.contains(day + "")) {
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
		OperatActivityComponent operatActivityComponent = (OperatActivityComponent) player.getTagged(OperatActivityComponent.Tag);
		OperatActivityRecord record = operatActivityComponent.getOperatActivityRecord();
		String awardId = Type.getString(objs[1], "");
		int whichDay = Type.getInt(objs[2], 1);
		int errorCode = 0;
		if (canReceiveAward(player, awardId, whichDay)) {
			List<AwardItem> awardItems = getRef().getAwardContent().getAwardItems();
			AwardItem awardItem = null;
			for (AwardItem aim : awardItems) {
				if (StringUtils.equals(awardId, Type.getString(aim.getId(), ""))) {
					awardItem = aim;
					break;
				}
			}
			if (awardItem != null) {
				errorCode = rewardItems(player, awardItem.getItems(player), ItemOptSource.SevenLogin);
				if (errorCode == MMORPGSuccessCode.CODE_SUCCESS) {
					String receivedSevenLoginStage = Type.getString(record.getHadReceiveSevenLoginStage(), "");
					receivedSevenLoginStage = receivedSevenLoginStage + "|" + awardItem.getId();
					record.setHadReceiveSevenLoginStage(receivedSevenLoginStage);
					PlayerImmediateDaoFacade.update(player);
				}
			}
		}
		ResultEvent.sendResult(player.getIdentity(), OperatActivityDefines.C2G_OA_SevenLogin_HadReceive, errorCode);
	
	}

	@Override
	public boolean isOpening(Player player) {
		OperatActivityComponent operatActivityComponent = (OperatActivityComponent) player.getTagged(OperatActivityComponent.Tag);
		OperatActivityRecord record = operatActivityComponent.getOperatActivityRecord();

		String receivedSevenLoginStage = Type.getString(record.getHadReceiveSevenLoginStage(), "");
		List<String> list = Arrays.asList(receivedSevenLoginStage.split("\\|"));
		if (list.size() >= OperatActivityRecord.SevenLoginDurationDay)
			return false;
		else
			return true;
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
	public void onEnterNewDay() {
		PlayerManager playerManager = MMORPGContext.getPlayerComponent().getPlayerManager();
		Collection<Player> players = playerManager.getPlayerList();
		for (Player player : players) {
			if (player.isOnline()) {
				onPlayerPreLogin(player);

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

	/**
	 * 距离开服过了多少天
	 * 
	 * @return
	 */
	public int whichDay(long openServerTime) {		
		int whichDay = DateTimeUtil.getIntervalDays(System.currentTimeMillis(),openServerTime) + 1;
		return whichDay;
	}

	private void sendCanReceiveTimes(Player player) {
		OperatActivityComponent operatActivityComponent = (OperatActivityComponent) player.getTagged(OperatActivityComponent.Tag);
		OperatActivityRecord record = operatActivityComponent.getOperatActivityRecord();
		long openServerTime = record.getOpenServerDate();
		int whichDay = whichDay(openServerTime);
		byte ret = 0;
		String receivedSevenLoginStage = Type.getString(record.getHadReceiveSevenLoginStage(), "");
		List<String> receivelist = Arrays.asList(receivedSevenLoginStage.split("\\|"));
		String hadsevenLoginStage = Type.getString(record.getHadSevenLoginStage(), "");
		List<String> loginList = Arrays.asList(hadsevenLoginStage.split("\\|"));
		AwardContent awardContent = getRef().getAwardContent();
		for (int i = 1; i <= whichDay; i++) {
			if (loginList.contains(i + "")) {
				for (AwardItem awardItem : awardContent.getAwardItems()) {
					if (awardItem != null && Type.getInt(awardItem.getCondValue(), 0) == i) {
						if (!receivelist.contains(awardItem.getId())) {
							ret++;
						}
					}
				}
			}
		}
		if(whichDay > 7 && ret == 0){
			ret = 8;
		}
		G2C_OA_SevenLogin_HaveReceive res = MessageFactory.getConcreteMessage(OperatActivityDefines.G2C_OA_SevenLogin_HaveReceive);
		res.setRet(ret);
		GameRoot.sendMessage(player.getIdentity(), res);
	}
	
}
