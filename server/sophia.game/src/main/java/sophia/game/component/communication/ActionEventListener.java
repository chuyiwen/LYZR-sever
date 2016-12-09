/**
 * 
 */
package sophia.game.component.communication;

import sophia.foundation.communication.core.ActionEventBase;


public interface ActionEventListener {

	void handleActionEvent(final ActionEventBase event);
}
