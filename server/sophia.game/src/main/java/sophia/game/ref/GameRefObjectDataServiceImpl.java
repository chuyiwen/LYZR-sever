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
package sophia.game.ref;

import java.util.ArrayList;
import java.util.Collection;

import com.google.common.util.concurrent.AbstractIdleService;

public class GameRefObjectDataServiceImpl extends AbstractIdleService implements GameRefObjectDataService {
	private GameRefObjectDataManager dataManager = new GameRefObjectDataManagerImpl();
	private ConcurrentMapGameRefObjectManager objectManager = new ConcurrentMapGameRefObjectManager();
	private GameRefObjectChangedListener changedListener = new GameRefObjectChangedListenerImpl();
	
	@Override
	public GameRefObjectDataManager getGameRefObjectDataManager() {
		return dataManager;
	}

	@Override
	public GameRefObjectManager getGameRefObjectManager() {
		return objectManager;
	}

	@Override
	public GameRefObjectChangedListener getGameRefObjectChangedListener() {
		return changedListener;
	}

	@Override
	public void startUp() throws Exception {
		Collection<GameRefObjectLoader<? extends GameRefObject>> gameRefObjectLoaders = dataManager.getGameRefObjectLoaders();
		for (GameRefObjectLoader<? extends GameRefObject> loader : gameRefObjectLoaders) {
			Collection<GameRefObject> allGameRefObjects = dataManager.loadGameRefObjects(loader);
			objectManager.addGameRefObjectCollection(allGameRefObjects);
		}
	}

	@Override
	protected void shutDown() throws Exception {
		// TODO Auto-generated method stub

	}

	class GameRefObjectChangedListenerImpl implements GameRefObjectChangedListener {

		@Override
		public <T extends GameRefObject> void updateGameRefObject(String key, String... ids) {
			Collection<GameRefObject> collection;
			if (ids == null || ids.length == 0) {
				collection = (Collection<GameRefObject>) dataManager.loadGameRefObjects(key);
			} else {
				collection = (Collection<GameRefObject>) dataManager.loadGameRefObjectsByIds(key, ids);
			}
			objectManager.updateGameRefObjectCollection(collection);

		}

		@Override
		public <T extends GameRefObject> void addGameRefObject(String key, String... ids) {
			Collection<GameRefObject> collection;
			if (ids == null || ids.length == 0) {
				collection = (Collection<GameRefObject>) dataManager.loadGameRefObjects(key);
			} else {
				collection = (Collection<GameRefObject>) dataManager.loadGameRefObjectsByIds(key, ids);
			}

			objectManager.addGameRefObjectCollection(collection);

		}

		@Override
		public void addGameRefObject(GameRefObject... objects) {
			Collection<GameRefObject> collection = new ArrayList<GameRefObject>(objects.length);
			for (GameRefObject object : objects) {
				collection.add(object);
			}

			objectManager.addGameRefObjectCollection(collection);

		}

		@Override
		public <T extends GameRefObject> void modifyGameRefObject(String key, String... ids) {
			Collection<GameRefObject> collection;
			if (ids == null || ids.length == 0) {
				collection = (Collection<GameRefObject>) dataManager.loadGameRefObjects(key);
			} else {
				collection = (Collection<GameRefObject>) dataManager.loadGameRefObjectsByIds(key, ids);
			}
			objectManager.modifyGameRefObjectCollection(collection);
		}

		@Override
		public void modifyGameRefObject(GameRefObject... objects) {
			Collection<GameRefObject> collection = new ArrayList<GameRefObject>(objects.length);
			for (GameRefObject object : objects) {
				collection.add(object);
			}
			objectManager.modifyGameRefObjectCollection(collection);

		}

		@Override
		public <T extends GameRefObject> void removeGameRefObject(String key, String... ids) {
			Collection<String> collection = new ArrayList<String>(ids.length);
			for (String object : ids) {
				collection.add(object);
			}

			objectManager.removeGameRefObjectCollection(collection);

		}

		@Override
		public void removeGameRefObject(GameRefObject... objects) {
			Collection<String> collection = new ArrayList<String>(objects.length);
			for (GameRefObject object : objects) {
				collection.add(object.getId());
			}

			objectManager.removeGameRefObjectCollection(collection);

		}

	}

}
