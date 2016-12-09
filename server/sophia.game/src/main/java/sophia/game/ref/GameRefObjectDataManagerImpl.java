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
import java.util.LinkedHashMap;

public class GameRefObjectDataManagerImpl implements GameRefObjectDataManager {
//	private final LinkedHashMap<Class<? extends GameRefObject>, GameRefObjectLoader<? extends GameRefObject>> loaders = new LinkedHashMap<Class<? extends GameRefObject>, GameRefObjectLoader<? extends GameRefObject>>();

	private final LinkedHashMap<String, GameRefObjectLoader<? extends GameRefObject>> loaders = new LinkedHashMap<>();

	@Override
	public <T extends GameRefObject> void addGameRefObjectLoader(String key, GameRefObjectLoader<T> loader) {
		loaders.put(key, loader);
	}

	@Override
	public Collection<String> getGameRefObjectKeys() {
		return loaders.keySet();
	}

	@Override
	public Collection<GameRefObjectLoader<?>> getGameRefObjectLoaders() {
		return loaders.values();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection<GameRefObject> loadGameRefObjects(GameRefObjectLoader<? extends GameRefObject> loader) {
		Collection<GameRefObject> collection = new ArrayList<GameRefObject>();
		Collection<GameRefObject> objects = (Collection<GameRefObject>) loader.loadAll();
		collection.addAll(objects);
		return collection;
	}

	@Override
	public Collection<GameRefObject> loadGameRefObjects(String key) {
		GameRefObjectLoader<? extends GameRefObject> loader = loaders.get(key);
		return loadGameRefObjects(loader);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection<GameRefObject> loadGameRefObjectsByIds(String key, String... ids) {
		GameRefObjectLoader<? extends GameRefObject> loader = loaders.get(key);
		Collection<GameRefObject> collection = (Collection<GameRefObject>) loader.load(ids);
		return collection;
	}

}
