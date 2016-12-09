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
package sophia.game.plugIns.gameWorld;

import sophia.game.core.PlugIn;

public class GameWorldPlugIn implements PlugIn<GameWorld> {

	private GameWorld gameWorld;
	
	@Override
	public GameWorld getModule() {
		return gameWorld;
	}

	@Override
	public void initialize() {
		gameWorld = new GameWorld();
	}

	@Override
	public void start() {
		gameWorld.ready();
	}

	@Override
	public void stop() {

	}

	@Override
	public void cleanUp() {
		gameWorld.destroy();
	}

}
