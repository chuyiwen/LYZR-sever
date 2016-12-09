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
package sophia.game.plugIns.gameModule;

import sophia.game.core.Dependency;
import sophia.game.core.PlugIn;
import sophia.game.plugIns.gameObjectManager.GameObjectManager;

public final class GameModulePlugIn implements PlugIn<GameModule> {
	
	@Dependency private GameObjectManager gameObjectManager;
	
	public void setGameObjectManager(GameObjectManager gameObjectManager) {
		this.gameObjectManager = gameObjectManager;
	}
	
	private GameModule module;
	
	@Override
	public GameModule getModule() {
		return module;
	}

	@Override
	public void initialize() {
		module = new GameModule();
		//module.setManager(gameObjectManager);
	}

	@Override
	public void start() {
		module.startUp();
		//module.ready();
		gameObjectManager.addGameObject(module);
	}

	@Override
	public void stop() {
		module.shutDown();
	}

	@Override
	public void cleanUp() {
		try {
			gameObjectManager.destroyGameObject(module);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
