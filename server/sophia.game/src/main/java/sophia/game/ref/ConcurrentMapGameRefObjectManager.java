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

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Collection;
import java.util.MissingResourceException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ConcurrentMapGameRefObjectManager implements GameRefObjectManager {
	private ConcurrentMap<String, GameRefObject> refs = new ConcurrentHashMap<String, GameRefObject>();

	@Override
	public GameRefObject getManagedObject(String id) {
		return refs.get(id);
	}

	@Override
	public <T extends GameRefObject> T getManagedObject(String id, Class<T> type) {
		GameRefObject ref = refs.get(id);

		if (ref == null) {
			return null;
		}

		if (type.isAssignableFrom(ref.getClass())) {
			return type.cast(ref);
		} else {
			throw new MissingResourceException("No matching GameRefObject! id = " + id + ", type = " + type, type.getName(), null);
		}
	}

	@Override
	public Collection<GameRefObject> getAllGameRefObject() {
		return refs.values();
	}

	// ///////////////////////////////////////////

	public void addGameRefObject(GameRefObject ref) {
		checkArgument(ref != null);

		this.refs.put(ref.getId(), ref);
	}

	void addGameRefObjectCollection(Collection<GameRefObject> refs) {
		checkArgument(refs != null);
		ConcurrentMap<String, GameRefObject> temp = new ConcurrentHashMap<String, GameRefObject>(this.refs);

		for (GameRefObject ref : refs) {
			temp.putIfAbsent(ref.getId(), ref);
		}

		this.refs = temp;
	}

	void modifyGameRefObject(GameRefObject ref) {
		checkArgument(ref != null);

		this.refs.replace(ref.getId(), ref);
	}

	void modifyGameRefObjectCollection(Collection<GameRefObject> refs) {
		ConcurrentMap<String, GameRefObject> temp = new ConcurrentHashMap<String, GameRefObject>(this.refs);

		for (GameRefObject ref : refs) {
			temp.replace(ref.getId(), ref);
		}

		this.refs = temp;
	}

	void updateGameRefObjectCollection(Collection<GameRefObject> refs) {
		ConcurrentMap<String, GameRefObject> temp = new ConcurrentHashMap<String, GameRefObject>(this.refs);
		synchronized (refs) {
			for (GameRefObject ref : refs) {
				if (temp.containsKey(ref.getId())) {
					temp.replace(ref.getId(), ref);
				} else {
					temp.put(ref.getId(), ref);
				}
			}
		}

		this.refs = temp;
	}

	void removeGameRefObject(String id) {
		this.refs.remove(id);
	}

	void removeGameRefObjectCollection(Collection<String> refs) {
		for (String id : refs) {
			this.refs.remove(id);
		}
	}

}
