/**
 * 
 */
package sophia.game.core;


class GameFramePlugIn implements PlugIn<GameFrame> {
	
	private GameFrame gameFrame;
	
	public GameFramePlugIn(GameFrame gameFrame) {
		assert(gameFrame != null);
		this.gameFrame = gameFrame;
	}
	
	@Override
	public GameFrame getModule() {
		return gameFrame;
	}

	@Override
	public void initialize() {
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
