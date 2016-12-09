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
package newbee.morningGlory;

import newbee.morningGlory.mmorpg.auction.persistence.AuctionItemDAO;
import newbee.morningGlory.mmorpg.ladder.MGLadderMgr;
import newbee.morningGlory.mmorpg.player.activity.discount.DiscountPersistenceHelper;
import newbee.morningGlory.mmorpg.player.activity.limitTimeRank.LimitTimeActivityMgr;
import newbee.morningGlory.mmorpg.sceneActivities.SceneActivityMgr;
import newbee.morningGlory.mmorpg.sceneActivities.castleWar.CastleWarApplyMgr;
import newbee.morningGlory.mmorpg.sortboard.SortboardMgr;
import newbee.morningGlory.mmorpg.store.ref.StoreItemRefMgr;
import sophia.mmorpg.MMORPGContext;
import sophia.mmorpg.Mail.GmMailMgr;
import sophia.mmorpg.friend.FriendSystemManager;
import sophia.mmorpg.gameArea.GameArea;
import sophia.mmorpg.player.PlayerManager;
import sophia.mmorpg.player.PlayerManagerComponent;
import sophia.mmorpg.stat.StatService;

public final class GameData {
	
	public static void initialize() {
		PlayerManager.initialize();
		MorningGloryContext.requestServerOpenTime();
		SceneActivityMgr.initialize();
		MMORPGContext.getGameAreaComponent().getGameArea().setSceneActivityMgr(SceneActivityMgr.getInstance());
		CastleWarApplyMgr.getInstance().initCastleWarTimer();
		SortboardMgr.getInstance().initialize();
		StoreItemRefMgr.LoadAll();
		LimitTimeActivityMgr.getInstance().init();
		MGLadderMgr.getInstance().initialize();
		MorningGloryContext.getUnionSystemComponent().configUnionData();
		MorningGloryContext.getLadderSystemComponent().configLadderData();
		GmMailMgr.getInstance().loadAll();
		GameArea gameArea = MMORPGContext.getGameAreaComponent().getGameArea();
		gameArea.loadAllGameScene();
		FriendSystemManager.startFriendSystemTimer();
		StatService.getInstance().getStatOnlineTicker().selectTotalNum();
		AuctionItemDAO.getInstance().loadData();
	}
	
	public static void shutDowAndSaveData() {
		saveCommonData();
		savePlayerPeriodData();
		savePlayerImmediateData();
	}
	
	public static void savePlayerPeriodData() {
		PlayerManagerComponent playerComponent = MMORPGContext.getPlayerComponent();
		try {
			playerComponent.getSaveService().shutDownDoInsert();
		} catch (Exception e) {
			GameApp.getShutDownFailedSet().add(e);
		}
		
		try {
			playerComponent.getSaveService().shutDownDoSave();
		} catch (Exception e) {
			GameApp.getShutDownFailedSet().add(e);
		}
	}
	
	public static void savePlayerImmediateData() {
		PlayerManagerComponent playerComponent = MMORPGContext.getPlayerComponent();
		try {
			playerComponent.getSaveImmediateService().shutDownDoInsert();
		} catch (Exception e) {
			GameApp.getShutDownFailedSet().add(e);
		}
		
		try {
			playerComponent.getSaveImmediateService().shutDownDoSave();
		} catch (Exception e) {
			GameApp.getShutDownFailedSet().add(e);
		}
	}
	
	public static void saveCommonData() {
		try {
			SortboardMgr.getInstance().newSortBoardData(); // 关服的时候排行榜排一遍
		} catch (Exception e) {
			GameApp.getShutDownFailedSet().add(e);
		}

		try {
			StoreItemRefMgr.updateDataTo();
		} catch (Exception e) {
			GameApp.getShutDownFailedSet().add(e);
		}

		try {
			DiscountPersistenceHelper.updateDataTo();
		} catch (Exception e) {
			GameApp.getShutDownFailedSet().add(e);
		}
	}
}
