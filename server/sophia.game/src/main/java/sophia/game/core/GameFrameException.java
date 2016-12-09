/**
 * 
 */
package sophia.game.core;

import sophia.game.framework.GameSimulator;



public final class GameFrameException extends RuntimeException {

	private static final long serialVersionUID = -2095659676599638886L;

	private static String alreadyInitializedApp(GameSimulator newGameApp, GameSimulator oldGameApp) {
		StringBuffer sb = new StringBuffer("Can not initialize GameFrame ");
		sb.append(newGameApp.toString());
		sb.append(", the GameFrame has already initialized ");
		sb.append(oldGameApp.toString());
		sb.append(".");
		return sb.toString();
	}
	
	private GameFrameException(String message) {
		super(message);
	}
	
	protected GameFrameException(GameSimulator newGameApp, GameSimulator oldGameApp) {
		this(alreadyInitializedApp(newGameApp, oldGameApp));
	}
}
