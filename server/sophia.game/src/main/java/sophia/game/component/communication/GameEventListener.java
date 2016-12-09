/**
 * 
 */
package sophia.game.component.communication;


public interface GameEventListener {
	
	void handleGameEvent(final GameEvent<?> event);
}
