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

import java.util.Collection;

public interface GameRefObjectDataManager {
	<T extends GameRefObject> void addGameRefObjectLoader(String key, GameRefObjectLoader<T> loader);

	public Collection<String> getGameRefObjectKeys();
	
	public Collection<GameRefObjectLoader<?>> getGameRefObjectLoaders();
	
	public Collection<GameRefObject> loadGameRefObjects(GameRefObjectLoader<? extends GameRefObject> loader);

	public Collection<GameRefObject> loadGameRefObjects(String key);
	
	public Collection<GameRefObject> loadGameRefObjectsByIds(String key, String... ids);
}
