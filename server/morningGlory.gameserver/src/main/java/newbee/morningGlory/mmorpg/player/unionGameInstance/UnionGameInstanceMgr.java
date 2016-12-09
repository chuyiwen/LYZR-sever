package newbee.morningGlory.mmorpg.player.unionGameInstance;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import newbee.morningGlory.MorningGloryContext;
import newbee.morningGlory.mmorpg.gameInstance.MGGameInstanceOpeningTimeType;
import newbee.morningGlory.mmorpg.player.gameInstance.PlayerGameInstanceComponent;
import newbee.morningGlory.mmorpg.union.MGUnion;
import newbee.morningGlory.mmorpg.union.MGUnionHelper;
import newbee.morningGlory.mmorpg.union.MGUnionMember;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import sophia.game.GameRoot;
import sophia.mmorpg.Mail.Mail;
import sophia.mmorpg.Mail.MailMgr;
import sophia.mmorpg.base.scene.GameScene;
import sophia.mmorpg.gameInstance.GameInstance;
import sophia.mmorpg.gameInstance.GameInstanceRef;
import sophia.mmorpg.gameInstance.OpenTimeData;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.itemBag.ItemPair;

import com.google.gson.Gson;

public class UnionGameInstanceMgr {

	private static final Logger logger = Logger.getLogger(UnionGameInstanceMgr.class);
	private List<MGUnion> list = new ArrayList<MGUnion>();
	private boolean isJoinReady = true;

	public synchronized boolean join(MGUnion union) {
		if (!canJoin()) {
			return false;
		}

		union.setApplyGameInstance(true);
		list.add(union);
		return true;
	}

	public synchronized boolean canJoin() {
		return isJoinReady;
	}

	public synchronized boolean isOpenEnter() {
		return !isJoinReady;
	}

	public synchronized void clearGameInstanceUnions() {
		if (isJoinReady) {
			return;
		}

		List<MGUnion> removes = new ArrayList<MGUnion>();
		for (MGUnion union : list) {
			if (!union.isApplyGameInstance()) {
				union.setApplyGameInstance(false);
				removes.add(union);
			}
		}

		list.removeAll(removes);
	}

	public synchronized void setGameInstanceUnions(List<MGUnion> gameInstanceUnions) {
		this.list = gameInstanceUnions;
	}

	public synchronized boolean isApplyGameInstance(MGUnion union) {
		return this.list.contains(union);
	}

	public synchronized void printUnionList() {
		for (MGUnion union : list) {
			logger.error("参加公会副本活动的公会:" + union.getName());
		}
	}

	public synchronized void close() {

		for (MGUnion union : list) {
			if (union.isKilledUnionBoss()) {
				sendGiftToMember(union);
			}

			MGUnionMember create = union.getCreater();
			Player unionLeader = MGUnionHelper.getPlayerByMember(create);
			PlayerGameInstanceComponent component = (PlayerGameInstanceComponent) unionLeader.getTagged(PlayerGameInstanceComponent.Tag);
			String gameInstanceId = component.getGameInstanceId(MGUnionGameInstanceComponent.GameInstanceRefId);
			GameInstance gameInstance = MorningGloryContext.getGameInstanceSystemComponent().getGameInstanceMgr().getGameInstace(gameInstanceId);
			component.closeMultiGameInstance(gameInstance);
			close(gameInstance);
		}
		clearGameInstanceUnions();

		isJoinReady = true;
	}

	public void close(GameInstance gameInstance) {
		if (gameInstance == null) {
			return;
		}
		GameScene crtGameScene = gameInstance.getCrtGameScene();
		String refId = crtGameScene.getRef().getId();
		Collection<Player> playerList = gameInstance.getPlayerCollection();
		// 传送副本内玩家出副本
		if (playerList != null && !playerList.isEmpty()) {
			for (Player player : playerList) {
				try {
					String crtRefId = player.getCrtScene().getRef().getId();
					if (!StringUtils.equals(refId, crtRefId)) {
						continue;
					}
					PlayerGameInstanceComponent playerGameInstanceComponent = (PlayerGameInstanceComponent) player.getTagged(PlayerGameInstanceComponent.Tag);
					playerGameInstanceComponent.goBackComeFromScene(gameInstance);
				} catch (Exception e) {
					logger.error("玩家离开副本出错, player=" + player);
					e.printStackTrace();
				}
			}

			playerList.clear();
		}

		MorningGloryContext.getGameInstanceSystemComponent().clearGameInstanceResource(gameInstance);
		if (logger.isDebugEnabled()) {
			logger.debug("time limit close gameInstance, " + gameInstance);
		}

	}

	public synchronized void open() {
		if (logger.isDebugEnabled()) {
			logger.debug("正在创建公会副本");
		}

		isJoinReady = false;

		for (MGUnion union : list) {
			union.setApplyGameInstance(false);
			MGUnionMember create = union.getCreater();
			Player unionLeader = MGUnionHelper.getPlayerByMember(create);
			openUnionGameInstance(unionLeader);
		}
	}

	public void openUnionGameInstance(Player unionLeader) {
		PlayerGameInstanceComponent component = (PlayerGameInstanceComponent) unionLeader.getTagged(PlayerGameInstanceComponent.Tag);
		String gameInstanceId = component.getGameInstanceId(MGUnionGameInstanceComponent.GameInstanceRefId);
		if (StringUtils.isNotEmpty(gameInstanceId)) {
			return;
		}
		GameInstanceRef gameInstanceRef = (GameInstanceRef) GameRoot.getGameRefObjectManager().getManagedObject(MGUnionGameInstanceComponent.GameInstanceRefId);
		long now = System.currentTimeMillis();
		OpenTimeData openTimeData = gameInstanceRef.getOpen().getOpenTime(now);
		// openTimeData不为空证明有副本开放
		if (openTimeData != null) {
			long duringTime = openTimeData.getLastTime();
			long openingTime = gameInstanceRef.getOpen().getTimestamp(openTimeData) / 1000;
			if (duringTime > 0) {
				component.opening(unionLeader, gameInstanceRef, openingTime, duringTime, MGGameInstanceOpeningTimeType.Daily_OpeningTime);
			}
		}
	}

	private void sendGiftToMember(MGUnion union) {
		union.setKilledUnionBoss(false);
		UnionGameInstanceRef unionGameInstanceRef = (UnionGameInstanceRef) GameRoot.getGameRefObjectManager().getManagedObject(MGUnionGameInstanceComponent.unionGameInstanceRefId);
		for (MGUnionMember member : union.getMemberMgr().getMemberList()) {
			String playerId = member.getPlayerId();
			ItemPair itemPair = new ItemPair(unionGameInstanceRef.getGiftBag(), 1, unionGameInstanceRef.getBindStatus());
			List<ItemPair> itemPairs = new ArrayList<ItemPair>(1);
			itemPairs.add(itemPair);
			String json = new Gson().toJson(itemPairs);
			MailMgr.sendMailById(playerId, "击杀公会副本BOSS礼包奖励", "击杀公会副本BOSS礼包奖励", Mail.huodong, json, 0, 0, 0, "");
		}

	}

	public long getStartTime() {
		long now = System.currentTimeMillis();
		long startTime = getGameInstanceRef().getOpen().getStartTime();
		long endTime = getGameInstanceRef().getOpen().getEndTime();

		if (endTime < now) {
			startTime = startTime + 24 * 3600 * 1000l;
		}
		return startTime;
	}

	public long getEndTime() {
		long now = System.currentTimeMillis();
		long endTime = getGameInstanceRef().getOpen().getEndTime();
		if (endTime < now) {
			endTime = endTime + 24 * 3600 * 1000l;
		}
		return endTime;
	}

	public int getRemainStartTime() {
		long now = System.currentTimeMillis();
		return (int) (getStartTime() - now) / 1000;
	}

	public int getRemainEndTime() {
		long now = System.currentTimeMillis();
		return (int) (getEndTime() - now) / 1000;
	}

	private GameInstanceRef getGameInstanceRef() {
		GameInstanceRef gameInstanceRef = (GameInstanceRef) GameRoot.getGameRefObjectManager().getManagedObject(MGUnionGameInstanceComponent.GameInstanceRefId);
		return gameInstanceRef;
	}

}
