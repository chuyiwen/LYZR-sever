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

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

import newbee.morningGlory.checker.RefChecker;
import newbee.morningGlory.checker.RefCheckerManager;
import newbee.morningGlory.gameVersion.GameVersion;
import newbee.morningGlory.http.HttpService;
import newbee.morningGlory.mmorpg.gameInstance.MGGameInstance;
import newbee.morningGlory.mmorpg.gameInstance.MGGameInstanceRestore;
import newbee.morningGlory.mmorpg.player.gameInstance.GameInstanceMgr;
import newbee.morningGlory.mmorpg.player.gameInstance.PlayerGameInstanceComponent;
import newbee.morningGlory.mmorpg.sprite.player.fightSkill.FightSkillRuntimeMgr;
import newbee.morningGlory.ref.ConcreteGameRefObjetLoaderRegister;
import newbee.morningGlory.ref.JSONDataManagerContext;
import newbee.morningGlory.ref.symbol.PropertySymbolLoader;
import newbee.morningGlory.system.CloseSignalHandler;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import sophia.foundation.util.DebugUtil;
import sophia.game.GameContext;
import sophia.game.GameRoot;
import sophia.game.component.GameObject;
import sophia.game.persistence.DaoConfig;
import sophia.game.plugIns.gameObjectManager.GameObjectManager;
import sophia.game.ref.GameRefObject;
import sophia.game.ref.GameRefObjectDataServiceImpl;
import sophia.game.ref.GameRefObjectManager;
import sophia.mmorpg.MMORPGContext;
import sophia.mmorpg.base.scene.GameScene;
import sophia.mmorpg.base.scene.ref.SceneRef;
import sophia.mmorpg.core.timer.SFTimeChimeServicePlugIn;
import sophia.mmorpg.gameArea.GameArea;
import sophia.mmorpg.gameInstance.GameInstance;
import sophia.mmorpg.player.PlayerManager;
import sophia.mmorpg.update.DBUpdateManager;
import sophia.stat.ConnectionFactory;

import com.google.common.util.concurrent.Service.State;

public final class GameApp {
	private static final Logger logger = Logger.getLogger(GameApp.class.getName());
	private static RefCheckerManager refCheckerManager = new RefCheckerManager();

	private static Set<Exception> shutDownFailedSet = new HashSet<Exception>();

	public static void main(String[] args) {
		long pre = System.currentTimeMillis();
		FightSkillRuntimeMgr.loadSkillRuntime();

		// GameRefObject初始化
		PropertySymbolLoader.load();
		JSONDataManagerContext.load();

		// 插件初始化
		GameRoot.registerPlugIn(new SFTimeChimeServicePlugIn());
		GameRoot.setGameRefObjectLoaderRegister(new ConcreteGameRefObjetLoaderRegister());
		GameRoot.initialize();

		// 组件初始化
		GameContext.initialize();
		MMORPGContext.initlize();
		MorningGloryContext.initialize();
		// 监听关闭(包括Ctrl-C、kill)
		CloseSignalHandler.initialize();

		// 数据库的更新
		try {
			if (!(new DBUpdateManager()).processAll()) {
				throw new RuntimeException("DBUpdateManager.processAll() failed.");
			}
		} catch (Exception e) {
			logger.error(DebugUtil.printStack(e));
		}

		// 定时更新log4j
		DOMConfigurator.configureAndWatch(GameApp.class.getClassLoader().getResource("log4j.xml").getPath(), 1000);

		// 启动游戏
		State state = startUp();

		long post = System.currentTimeMillis();
		long use = post - pre;
		if (state == State.RUNNING) {
			if (logger.isInfoEnabled()) {
				logger.info(getSystemInfo());
				logger.info("GameServer was running.use time: " + use);
			}
		} else {
			logger.error("GameServer startUp failed.");
		}
	}

	public static void checkAllGameRefObject() {
		GameRefObjectDataServiceImpl impl = (GameRefObjectDataServiceImpl) GameRoot.getGameRefObjectDataService();
		refCheckerManager.getOutputCtrl().println("开始数据校验");
		GameRefObjectManager objectManager = impl.getGameRefObjectManager();

		List<Class<?>> temp = new ArrayList<Class<?>>();
		for (GameRefObject refObj : objectManager.getAllGameRefObject()) {
			RefChecker<?> refObjectChecker = refCheckerManager.getRefObjectChecker(refObj);
			if (refObjectChecker == null) {
				if (!temp.contains(refObj.getClass())) {
					refCheckerManager.getOutputCtrl().warn(refObj.getClass().getSimpleName() + " 没有执行数据检验! 没有对应的数据检查器!");
					temp.add(refObj.getClass());
				}
			} else {
				refObjectChecker.check(refObj);
				if (!temp.contains(refObj.getClass())) {
					refCheckerManager.getOutputCtrl().println("数据检验 @ " + refObj.getClass().getSimpleName() + "...OK");
					temp.add(refObj.getClass());
				}
			}
		}

		refCheckerManager.getOutputCtrl().println("完成数据校验");

	}

	public static void clearAllGameScene() {
		try {
			GameArea gameArea = MMORPGContext.getGameAreaComponent().getGameArea();
			gameArea.clearAllGameScene();
		} catch (Exception e) {
			logger.error(DebugUtil.printStack(e));
		}
	}

	public static void kickoutAllOnlinePlayer() {
		PlayerManager playerManager = MMORPGContext.getPlayerComponent().getPlayerManager();
		playerManager.kickoutAllOnlinePlayer();
	}

	public static State startUp() {
		// 启动游戏
		State state = GameRoot.startUp();
		checkAllGameRefObject();
		GameData.initialize();
		GameService.startStatLogService();
		GameService.startSaveGameDataService();
		GameService.startSendCreateLoginService();
		GameService.startPlayerPeriodSaveService();
		GameService.startPlayerImmediateSaveService();

		GameService.startHttpService();
		while (!HttpService.isHttpStarted()) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				logger.error("startUp error, " + DebugUtil.printStack(e));
			}

			logger.info("waiting for http service start!!!");
		}

		// 网络服务启动，放到最后
		GameService.startCommunicationService();
		MMORPGContext.setServerRunning(true);
		return state;
	}

	public static State shutDown(boolean force) {
		// http服务器关闭游戏服务器请求使用
		logger.info("ShutDown GameServer");
		try {
			GameService.stopCommunicationService();
			clearAllGameScene();
			GameService.stopCommonTimer();
			kickoutAllOnlinePlayer();
			GameService.stopStatLogService();
			GameService.stopSendCreateLoginService();
			GameService.stopSaveGameDataService();
			GameService.stopPlayerPeriodSaveService();
			GameService.stopPlayerImmediateSaveService();
			GameData.shutDowAndSaveData();

			if (!shutDownFailedSet.isEmpty()) {
				for (Exception e : shutDownFailedSet) {
					logger.error("shutDownFailedSet is not Empty ! shutDown error, " + DebugUtil.printStack(e));
				}
				shutDownFailedSet.clear();
				if (!force) {
					return State.FAILED;
				}
			}

			shutDownFailedSet.clear();
			State state = GameRoot.shutDown();
			if (state != State.TERMINATED) {
				if (!force) {
					return state;
				}
			}
		} catch (Exception e) {
			logger.error(DebugUtil.printStack(e));
			if (!force) {
				return State.FAILED;
			}
		}

		return State.TERMINATED;
	}

	public static String getSystemInfo() {
		Mbeans.obtain();

		ThreadGroup group = Thread.currentThread().getThreadGroup();
		ThreadGroup topGroup = group;
		// 遍历线程组树，获取根线程组
		while (group != null) {
			topGroup = group;
			group = group.getParent();
		}

		Runtime runtime = Runtime.getRuntime();

		long usedMemory = runtime.totalMemory() - runtime.freeMemory();

		String info = String.format("\r\n"
				+ "================================================================== \r\n"//
				+ "当前时区:%s\r\n" + "当前时间:%s\r\n" + "------------------------------------------------------------------ \r\n" + "虚拟机可用处理器:%s\r\n" + "当前的活动线程总数:%s 线程组总数:%s \r\n"
				+ "虚拟机可用最大内存:%s K = %s M \r\n" + "虚拟机占用总内存:%s K = %s M \r\n" + "虚拟机空闲内存:%s K = %s M \r\n" + "当前使用内存:%s K = %s M \r\n"
				+ "GC总次数:%s次  总耗时:%s毫秒   上次记录间隔%s毫秒 GC:%s次 耗时:%s毫秒 \r\n" + "ObjectPendingFinalizationCount:%s \r\n" + "heapMemoryUsage:%s \r\n" + "nonHeapMemoryUsage:%s \r\n"
				+ "------------------------------------------------------------------ \r\n" + "游戏服ID:%s \r\n" + "游戏服开服时间:%s \r\n" + "游戏服启动时间:%s \r\n" + "data数据库:%s \r\n"
				+ "log数据库:%s \r\n" + "游戏服版本:\r\n%s \r\n", TimeZone.getDefault().getDisplayName() + " " + TimeZone.getDefault().getID(), (new SimpleDateFormat(
				"yyyy年MM月dd日 hh:mm:ss")).format(new Date()), runtime.availableProcessors(), topGroup.activeCount(), topGroup.activeGroupCount(), runtime.maxMemory() / 1024,
				runtime.maxMemory() / 1024 / 1024, runtime.totalMemory() / 1024, runtime.totalMemory() / 1024 / 1024, runtime.freeMemory() / 1024,
				runtime.freeMemory() / 1024 / 1024, usedMemory / 1024, usedMemory / 1024 / 1024, Mbeans.currObtain.gcCounts, Mbeans.currObtain.gcTimes,
				Mbeans.currObtain.obtainTime - Mbeans.lastObtain.obtainTime, Mbeans.currObtain.gcCounts - Mbeans.lastObtain.gcCounts, Mbeans.currObtain.gcTimes
						- Mbeans.lastObtain.gcTimes, Mbeans.currObtain.getFinallzationCount, Mbeans.currObtain.heapMemoryUsage, Mbeans.currObtain.nonHeapMemoryUsage,
				// ----------
				MMORPGContext.getServerId(), (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new Date(MorningGloryContext.getServerOpenTime())), (new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss")).format(new Date(MorningGloryContext.getServerStartTime())), DaoConfig.getInfo(), ConnectionFactory.getInfo(), new GameVersion()
						+ "\r\n================================================================== ");

		return info;

	}

	public static String traceInstances() {
		GameObjectManager gameObjManager = (GameObjectManager) GameRoot.getGameObjectManager();
		Map<String, GameObject> objects = gameObjManager.getObjects();
		if (objects == null || objects.size() == 0) {
			return null;
		}

		Map<Class<?>, AtomicLong> _instanceCounts = new HashMap<Class<?>, AtomicLong>();

		for (String id : objects.keySet()) {
			GameObject obj = objects.get(id);
			Class<?> objcls = obj.getClass();
			if (obj instanceof GameScene) {
				GameScene sc = (GameScene) obj;
				if (sc.getRef().getType() == SceneRef.FuBen) {
					objcls = PlayerGameInstanceComponent.class;
				}
			}

			AtomicLong atomicLong = _instanceCounts.get(objcls);
			if (atomicLong == null) {
				atomicLong = new AtomicLong();
				_instanceCounts.put(objcls, atomicLong);
			}

			atomicLong.addAndGet(1);
		}

		GameInstanceMgr gameInstanceMgr = MorningGloryContext.getGameInstanceSystemComponent().getGameInstanceMgr();
		ConcurrentMap<String, GameInstance> idToGameInstanceMap = gameInstanceMgr.getIdToGameInstanceMap();
		AtomicLong atomicLongInstances = new AtomicLong(idToGameInstanceMap.size());
		_instanceCounts.put(MGGameInstance.class, atomicLongInstances);

		ConcurrentHashMap<String, MGGameInstanceRestore> gameInstanceCache = gameInstanceMgr.getGameInstanceCache();
		AtomicLong atomicLongRestore = new AtomicLong(gameInstanceCache.size());
		_instanceCounts.put(MGGameInstanceRestore.class, atomicLongRestore);

		String traces = "内存中实例数量:\r\n";
		for (Class<?> obj : _instanceCounts.keySet()) {
			String item = String.format("内存中实例:%-5s @%s\r\n", _instanceCounts.get(obj).get(), obj.toString());
			traces += item;
		}

		_instanceCounts.clear();

		return traces;
	}

	public static String tracePlayerInfo() {
		// 在哪个场景，正在战斗/正在跑动，玩家AOI内存实例个数
		String traces = "内存中实例数量:\r\n";
		return traces;
	}

	public static Set<Exception> getShutDownFailedSet() {
		return shutDownFailedSet;
	}

	private static class Mbeans {
		public static Mbeans lastObtain = null;
		public static Mbeans currObtain = null;

		public long obtainTime = 0;
		public long gcCounts = 0;
		public long gcTimes = 0;
		public long getFinallzationCount;
		public String heapMemoryUsage;
		public String nonHeapMemoryUsage;

		public static Mbeans obtain() {
			lastObtain = currObtain;
			currObtain = new Mbeans();
			if (lastObtain == null)
				lastObtain = currObtain;

			currObtain.obtainTime = System.currentTimeMillis();

			for (final GarbageCollectorMXBean garbageCollector : ManagementFactory.getGarbageCollectorMXBeans()) {
				currObtain.gcCounts += garbageCollector.getCollectionCount();
			}

			for (final GarbageCollectorMXBean garbageCollector : ManagementFactory.getGarbageCollectorMXBeans()) {
				currObtain.gcTimes += garbageCollector.getCollectionTime();
			}

			MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
			currObtain.getFinallzationCount = memoryMXBean.getObjectPendingFinalizationCount();
			currObtain.heapMemoryUsage = memoryMXBean.getHeapMemoryUsage().toString();
			currObtain.nonHeapMemoryUsage = memoryMXBean.getNonHeapMemoryUsage().toString();

			return currObtain;
		}
	}
}
