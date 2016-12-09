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
package sophia.mmorpg.base.scene;

import java.util.UUID;

import sophia.game.GameRoot;
import sophia.mmorpg.GameObjectProvider;
import sophia.mmorpg.base.scene.aoi.SceneAOILayer;
import sophia.mmorpg.base.scene.ref.AbstractGameSceneRef;

public final class DefaultGameSceneProvider implements GameObjectProvider<GameScene> {
	private static final GameObjectProvider<GameScene> instance = new DefaultGameSceneProvider();

	private DefaultGameSceneProvider() {

	}

	public static final GameObjectProvider<GameScene> getInstance() {
		return instance;
	}

	@Override
	public GameScene get(Class<GameScene> type) {
		GameScene gameScene = new GameScene();
		return gameScene;
	}

	@Override
	public GameScene get(Class<GameScene> type, Object... args) {
		String id = (String) args[0];
		GameScene gameScene = new GameScene();
		gameScene.setId(UUID.randomUUID().toString());
		AbstractGameSceneRef ref = (AbstractGameSceneRef) (GameRoot.getGameRefObjectManager().getManagedObject(id));
		gameScene.setRef(ref);
		if (ref.getBirthRegions().size() != 0) {
			gameScene.setBirthRegion(ref.getBirthRegions().get(0));
		}
		if (ref.getReviveRegions().size() != 0) {
			gameScene.setReviveRegion(ref.getReviveRegions().get(0));
		}
		if (ref.getSafeRegions().size() != 0) {
			gameScene.setSafeRegion(ref.getSafeRegions().get(0));
		}
		gameScene.setTerrainLayer(ref.getTerrainLayer());

		if (ref.getTransInRegions().size() != 0) {
			gameScene.setTransInRegion(ref.getTransInRegions().get(0));
		}
		if (ref.getTransOutRegions().size() != 0) {
			gameScene.setTransOutRegion(ref.getTransOutRegions().get(0));
		}

		int rows = ref.getTerrainLayer().getnRow();
		int columns = ref.getTerrainLayer().getnColumn();
		SceneAOILayer layer = new SceneAOILayer(rows, columns);
		gameScene.setAoiLayer(layer);
		GameRoot.getGameObjectManager().addGameObject(gameScene);
		return gameScene;
	}

}
