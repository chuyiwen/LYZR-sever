/**
 * 
 */
package sophia.foundation.communication.practice.simulatorPattern;

import sophia.foundation.communication.core.ActionEventBase;
import sophia.foundation.communication.core.Connection;


public interface ActionEventMessageHandler {
	void messageReceived(Connection connection, ActionEventBase actionEventMessage);
	
	void exceptionThrown(Connection connection, Throwable exception);
	
	void disconnected(Connection connection, boolean graceful);
}
