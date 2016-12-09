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

import java.text.SimpleDateFormat;
import java.util.Calendar;

import newbee.morningGlory.auth.AuthComponent;
import newbee.morningGlory.character.CharacterComponent;
import newbee.morningGlory.code.MGErrorCode;
import newbee.morningGlory.code.MGSuccessCode;
import newbee.morningGlory.event.MGEventDefines;
import newbee.morningGlory.mmorpg.auction.AuctionSystemComponent;
import newbee.morningGlory.mmorpg.gameInstance.MGGameInstanceSystemComponent;
import newbee.morningGlory.mmorpg.ladder.LadderSystemComponent;
import newbee.morningGlory.mmorpg.monster.MGMonsterProvider;
import newbee.morningGlory.mmorpg.operatActivities.ActivitySystemComponent;
import newbee.morningGlory.mmorpg.operatActivities.utils.HttpConnection;
import newbee.morningGlory.mmorpg.operatActivities.utils.LoadOpenTimeCallBack;
import newbee.morningGlory.mmorpg.player.MGPlayerProvider;
import newbee.morningGlory.mmorpg.player.dailyQuest.MGDailyQuest;
import newbee.morningGlory.mmorpg.player.dailyQuest.MGDailyQuestProvider;
import newbee.morningGlory.mmorpg.player.offLineAI.MGPlayerAvatarProvider;
import newbee.morningGlory.mmorpg.player.offLineAI.PlayerAvatar;
import newbee.morningGlory.mmorpg.store.ref.StoreItemRefMgr;
import newbee.morningGlory.mmorpg.union.UnionSystemComponent;
import newbee.morningGlory.system.GameWorldComponent;

import org.apache.log4j.Logger;

import sophia.foundation.util.PropertiesWrapper;
import sophia.game.GameContext;
import sophia.game.plugIns.gameModule.GameModule;
import sophia.game.plugIns.gameWorld.GameWorld;
import sophia.game.plugIns.gateWay.GateWay;
import sophia.mmorpg.GameObjectFactory;
import sophia.mmorpg.MMORPGContext;
import sophia.mmorpg.monster.Monster;
import sophia.mmorpg.player.Player;

public final class MorningGloryContext {
	private static Logger logger = Logger.getLogger(MorningGloryContext.class);
	// public static final Injector MGIoC = Guice.createInjector(new
	// MGGuiceModule());
	public static final String LoadUrl = "newbee.morningGlory.http.HttpService.HttpWebUrl";

	private static long serverOpenTime = System.currentTimeMillis();

	private static long serverStartTime = System.currentTimeMillis();

	/** 副本全局组件 **/
	private static MGGameInstanceSystemComponent gameInstanceSystemComponent = null;

	private static StoreItemRefMgr storeItemRefMgr = null;

	private static UnionSystemComponent unionSystemComponent = null;

	private static ActivitySystemComponent activitySystemComponent = null;

	private static LadderSystemComponent ladderSystemComponent = null;

	private static AuctionSystemComponent auctionSystemComponent = null;
	
	private static final String gameInstanceComponentTag = "GameInstanceSystemComponent";

	private static final String storeItemRefMgrTag = "storeItemRefMgrTag";

	private static final String unionSystemComponentTag = "UnionSystemComponent";
	
	//private static final String playerAvatarMgrTag = "PlayerAvatarMgr";

	private static final String activitySystemComponentTag = "ActivitySystemComponent";

	private static final String ladderSystemComponentTag = "LadderSystemComponent";
	
	private static final String auctionSystemComponentTag = "AuctionSystemComponent";

	private static final MorningGloryContextResolver context = new MorningGloryContextResolver();

	private MorningGloryContext() {

	}

	public static final void initialize() {

		int serverId = getProperties().getIntProperty("newbee.morningGlory.http.HttpService.serverId", -1);
		MMORPGContext.setServerId(serverId);
		if (serverId < 0)
			throw new Error("配置读取错误!没有配置服务器id!");
		
		int playerCountLimit = getProperties().getIntProperty("newbee.morningGlory.onlinePlayerLimit", 1500);
		MMORPGContext.setMaxOnlinePlayerCount(playerCountLimit);
		int playerAOICacheLimit = getProperties().getIntProperty("newbee.morningGlory.playerAOICacheLimit", 50);
		MMORPGContext.setPlayerAOICacheCount(playerAOICacheLimit);
		int preloadPlayerLimit = getProperties().getIntProperty("newbee.morningGlory.preloadPlayerLimit", 500);
		MMORPGContext.setPreloadPlayerCount(preloadPlayerLimit);
		int cachePlayerLimit = getProperties().getIntProperty("newbee.morningGlory.cachePlayerLimit", 1000, preloadPlayerLimit + 1, Integer.MAX_VALUE);
		MMORPGContext.setMaxCachePlayerCount(cachePlayerLimit);
		int maxBroadcastAOIPlayerLimit = getProperties().getIntProperty("newbee.morningGlory.broadcastAOILimit", 50);
		MMORPGContext.setMaxBoradcastAOIPlayerCount(maxBroadcastAOIPlayerLimit);

		GameObjectFactory.addOrReplace(Player.class, MGPlayerProvider.getInstance());
		GameObjectFactory.addOrReplace(Monster.class, MGMonsterProvider.getInstance());
		GameObjectFactory.addOrReplace(MGDailyQuest.class, MGDailyQuestProvider.getInstance());
		GameObjectFactory.addOrReplace(PlayerAvatar.class, MGPlayerAvatarProvider.getInstance());

		// 注册消息
		MGEventDefines.registerActionEvents();
		MGErrorCode.initialize();
		MGSuccessCode.initialize();

		GateWay gateWay = GameContext.getGateWay();
		gateWay.createComponent(AuthComponent.class);

		GameWorld gameWorld = GameContext.getGameWorld();
		gameWorld.createComponent(CharacterComponent.class);
		gameWorld.createComponent(GameWorldComponent.class);
		
		GameModule gameModule = GameContext.getGameModule();
		gameInstanceSystemComponent = (MGGameInstanceSystemComponent) gameModule.createComponent(MGGameInstanceSystemComponent.class, gameInstanceComponentTag);
		storeItemRefMgr = (StoreItemRefMgr) gameModule.createComponent(StoreItemRefMgr.class, storeItemRefMgrTag);
		unionSystemComponent = (UnionSystemComponent) gameModule.createComponent(UnionSystemComponent.class, unionSystemComponentTag);
		// gameModule.createComponent(PlayerAvatarMgr.class,playerAvatarMgrTag);
		activitySystemComponent = (ActivitySystemComponent) gameModule.createComponent(ActivitySystemComponent.class, activitySystemComponentTag);
		ladderSystemComponent = (LadderSystemComponent) gameModule.createComponent(LadderSystemComponent.class, ladderSystemComponentTag);
		auctionSystemComponent = (AuctionSystemComponent) gameModule.createComponent(AuctionSystemComponent.class, auctionSystemComponentTag);
	}

	public static void requestServerOpenTime() {
		PropertiesWrapper properties = MorningGloryContext.getProperties();
		LoadOpenTimeCallBack loadCallBack = new LoadOpenTimeCallBack();

		String url = properties.getProperty(LoadUrl, "") + "?action=serverOpenTime&fid=" //
				+ properties.getProperty("newbee.morningGlory.http.HttpService.serverId");
		if (logger.isDebugEnabled()) {
			logger.debug(url);
		}

		HttpConnection httpConnection = HttpConnection.create(url, loadCallBack);
		httpConnection.exec(false);
		synchronized (loadCallBack) {
			while (!loadCallBack.isCallback()) {
				try {
					loadCallBack.wait(100);
				} catch (InterruptedException e) {
					logger.error("", e);
				}
			}
		}
		serverOpenTime = loadCallBack.getServerOpenTime();
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(serverOpenTime);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String date = format.format(c.getTime());
		logger.info("开服时间 ：" + date);
		if (serverOpenTime == 0) {
			serverOpenTime = System.currentTimeMillis();
			logger.error("请求开服时间失败，请检查GM后台");
			throw new RuntimeException("请求开服时间异常，开服时间为0,请检查GM后台");
		}
	}

	public static MGGameInstanceSystemComponent getGameInstanceSystemComponent() {
		return gameInstanceSystemComponent;
	}

	public static StoreItemRefMgr getStoreItemRefMgr() {
		return storeItemRefMgr;
	}

	public static UnionSystemComponent getUnionSystemComponent() {
		return unionSystemComponent;
	}

	public static PropertiesWrapper getProperties() {
		return context.getProperty();
	}

	public static ActivitySystemComponent getActivitySystemComponent() {
		return activitySystemComponent;
	}

	public static void setActivitySystemComponent(ActivitySystemComponent activitySystemComponent) {
		MorningGloryContext.activitySystemComponent = activitySystemComponent;
	}

	public static long getServerOpenTime() {
		return serverOpenTime;
	}

	public static long getServerStartTime() {
		return serverStartTime;
	}

	public static AuctionSystemComponent getAuctionSystemComponent() {
		return auctionSystemComponent;
	}

	public static void setAuctionSystemComponent(AuctionSystemComponent auctionSystemComponent) {
		MorningGloryContext.auctionSystemComponent = auctionSystemComponent;
	}

	public static LadderSystemComponent getLadderSystemComponent() {
		return ladderSystemComponent;
	}

	public static void setLadderSystemComponent(LadderSystemComponent ladderSystemComponent) {
		MorningGloryContext.ladderSystemComponent = ladderSystemComponent;
	}
	
	
}
