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
package sophia.game;

import java.io.File;
import java.net.URL;

import org.apache.log4j.Logger;

import sophia.foundation.authentication.Identity;
import sophia.foundation.communication.core.ActionEventBase;
import sophia.foundation.communication.core.Connection;
import sophia.foundation.communication.practice.simulatorPattern.SimulatorCommunicationService;
import sophia.foundation.util.DebugUtil;
import sophia.game.core.PlugIn;
import sophia.game.framework.ServerSimulatorImpl;
import sophia.game.plugIns.communication.CommunicationServicePlugIn;
import sophia.game.plugIns.data.DataPlugIn;
import sophia.game.plugIns.gameEvent.GameEventManagerPlugIn;
import sophia.game.plugIns.gameModule.GameModulePlugIn;
import sophia.game.plugIns.gameObjectManager.GameObjectManager;
import sophia.game.plugIns.gameObjectManager.GameObjectManagerPlugIn;
import sophia.game.plugIns.gameWorld.GameWorldPlugIn;
import sophia.game.plugIns.gateWay.GateWayPlugIn;
import sophia.game.plugIns.taskManager.TaskManagerPlugIn;
import sophia.game.ref.GameRefObjectDataManager;
import sophia.game.ref.GameRefObjectDataService;
import sophia.game.ref.GameRefObjectDataServiceImpl;
import sophia.game.ref.GameRefObjectManager;
import sophia.game.ref.GameRefObjetLoaderRegister;
import sophia.game.utils.BootClassLoader;
import sophia.game.utils.ClazzUtil;
import sophia.game.utils.ReloadableClassFactory;
import sophia.game.utils.ReloadableClassLoader;

import com.google.common.util.concurrent.Service.State;

public final class GameRoot {
	private static final Logger logger = Logger.getLogger(GameRoot.class.getName());

	private static final ServerSimulatorImpl serverSimulator = new ServerSimulatorImpl();

	private static SimulatorCommunicationService simulatorCommunicationService;

	private static GameObjectManager gameObjectManager;

	// GameRefObject
	private static GameRefObjectDataService gameRefObjectDataService;
	private static GameRefObjectManager gameRefObjectManager;
	private static GameRefObjectDataManager gameRefObjectDataManager;
	private static GameRefObjetLoaderRegister gameRefObjectLoaderRegister;

	public static ReloadableClassFactory classFactory = null;
	public static ClassLoader classLoader = null;

	public static SimulatorCommunicationService getSimulatorCommunicationService() {
		return simulatorCommunicationService;
	}

	public static GameObjectManager getGameObjectManager() {
		return gameObjectManager;
	}

	public static final void registerPlugIn(PlugIn<?> plugIn) {
		serverSimulator.registerPlugIn(plugIn);
	}

	public static final PlugIn<?> requestPlugIn(Class<PlugIn<?>> plugInClass) {
		return serverSimulator.requestPlugIn(plugInClass);
	}

	public static final Object requestModule(Class<?> moduleClass) {
		return serverSimulator.requestModule(moduleClass);
	}

	public static final void sendMessage(Identity identity, ActionEventBase message) {
		if (identity == null) {
			return;
		}

		try {
			Connection session = simulatorCommunicationService.getSession(identity);
			if (session == null || !session.isConnected()) {
				return;
			}
			
			message.setIdentity(identity);
			simulatorCommunicationService.sendActonEventMessage(message);
		} catch (Exception e) {
			logger.error("identityName = " + identity.getName() + " " + DebugUtil.printStack(e));
			simulatorCommunicationService.closeSession(identity);
		}
	}

	public static final void initialize() {
		registerPlugIn(new GameObjectManagerPlugIn());
		registerPlugIn(new GameEventManagerPlugIn());
		registerPlugIn(new TaskManagerPlugIn());
		registerPlugIn(new DataPlugIn());
		registerPlugIn(new GameWorldPlugIn());
		registerPlugIn(new GateWayPlugIn());
		registerPlugIn(new GameModulePlugIn());
		registerPlugIn(new CommunicationServicePlugIn());

		serverSimulator.initialize();
		gameObjectManager = (GameObjectManager) GameRoot.requestModule(GameObjectManager.class);
		simulatorCommunicationService = (SimulatorCommunicationService) GameRoot.requestModule(SimulatorCommunicationService.class);

		// GameRefObject
		gameRefObjectDataService = new GameRefObjectDataServiceImpl();
		gameRefObjectManager = gameRefObjectDataService.getGameRefObjectManager();
		gameRefObjectDataManager = gameRefObjectDataService.getGameRefObjectDataManager();
		gameRefObjectLoaderRegister.registAllGameRefObjectLoadSlaver();
	}

	public static final State startUp() {
		// ref
		State gameRefObjectDataServiceState = gameRefObjectDataService.startAndWait();
		if (gameRefObjectDataServiceState != State.RUNNING) {
			logger.error("GameRefObjectDataService fails to start!");
			throw new RuntimeException("GameRefObjectDataService fails to start");
		} else {
			if (logger.isDebugEnabled()) {
				logger.debug("GameRefObjectDataService is running!");
			}
		}

		try {
			String game_server_classes_path = "classes";
			String game_server_lib_path = "lib";
			System.setProperty("game_server_classes_path", game_server_classes_path);
			System.setProperty("game_server_lib_path", game_server_lib_path);

			URL[] urls = ClazzUtil.getClassPathURLs(new File(Thread.currentThread().getContextClassLoader().getResource("").getFile()).getParentFile(), game_server_classes_path,
					game_server_lib_path);
			classLoader = new BootClassLoader(urls, Thread.currentThread().getContextClassLoader());
			Thread.currentThread().setContextClassLoader(classLoader);
			classFactory = ReloadableClassFactory.newInstance(urls, Thread.currentThread().getContextClassLoader());
			classLoader = new ReloadableClassLoader(classFactory, urls, Thread.currentThread().getContextClassLoader());
			Thread.currentThread().setContextClassLoader(classLoader);
			classFactory = new ReloadableClassFactory(null, Thread.currentThread().getContextClassLoader());
			return serverSimulator.startAndWait();
		} catch (Exception e) {
			logger.error("", e);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			System.exit(0);
		}
		return null;

	}

	public static void setGameObjectManager(GameObjectManager gameObjectManager) {
		GameRoot.gameObjectManager = gameObjectManager;
	}

	public static void setGameRefObjectDataService(GameRefObjectDataService gameRefObjectDataService) {
		GameRoot.gameRefObjectDataService = gameRefObjectDataService;
	}

	public static void setGameRefObjectManager(GameRefObjectManager gameRefObjectManager) {
		GameRoot.gameRefObjectManager = gameRefObjectManager;
	}

	public static void setGameRefObjectDataManager(GameRefObjectDataManager gameRefObjectDataManager) {
		GameRoot.gameRefObjectDataManager = gameRefObjectDataManager;
	}

	public static final State shutDown() {
		return serverSimulator.stopAndWait();
	}

	public static GameRefObjetLoaderRegister getGameRefObjectLoaderRegister() {
		return gameRefObjectLoaderRegister;
	}

	public static void setGameRefObjectLoaderRegister(GameRefObjetLoaderRegister gameRefObjectLoaderRegister) {
		GameRoot.gameRefObjectLoaderRegister = gameRefObjectLoaderRegister;
	}

	public static GameRefObjectDataService getGameRefObjectDataService() {
		return gameRefObjectDataService;
	}

	public static GameRefObjectManager getGameRefObjectManager() {
		return gameRefObjectManager;
	}

	public static GameRefObjectDataManager getGameRefObjectDataManager() {
		return gameRefObjectDataManager;
	}

}
