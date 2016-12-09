/**
 * 
 */
package sophia.game.plugIns.gameEvent;

import sophia.game.core.PlugIn;


public class GameEventManagerPlugIn implements PlugIn<GameEventManager> {
	
	private GameEventManager module;
	
	@Override
	public GameEventManager getModule() {
		return module;
	}

	@Override
	public void initialize() {
		module = new GameEventManager();
	}

	@Override
	public void start() {
	}

	@Override
	public void stop() {
	}

	@Override
	public void cleanUp() {
	}

}
