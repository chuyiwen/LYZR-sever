/**
 * 
 */
package sophia.game.plugIns.gameObjectManager;
import sophia.game.core.Dependency;
import sophia.game.core.GameFrame;
import sophia.game.core.PlugIn;
import sophia.game.plugIns.gameEvent.GameEventManager;


public class GameObjectManagerPlugIn implements PlugIn<GameObjectManager> {
	
	@Dependency private GameFrame gameFrame;
	@Dependency private GameEventManager gameEventManager;
	
	private GameObjectManager module;
	
	@Override
	public GameObjectManager getModule() {
		return module;
	}

	@Override
	public void initialize() {
		module = new GameObjectManager(gameFrame, getGameEventManager());
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

	public GameFrame getGameFrame() {
		return gameFrame;
	}

	public void setGameFrame(GameFrame gameFrame) {
		this.gameFrame = gameFrame;
	}

	public GameEventManager getGameEventManager() {
		return gameEventManager;
	}

	public void setGameEventManager(GameEventManager gameEventManager) {
		this.gameEventManager = gameEventManager;
	}

}
