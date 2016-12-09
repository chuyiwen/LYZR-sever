/**
 * 
 */
package sophia.foundation.communication.core;


public interface ConnectionListener {
	void connected(Connection connection);
	
	void messageReceived(Connection connection, ActionEventBase message);
	
	void exceptionThrown(Connection connection, Throwable exception);
	
	void disconnected(Connection connection, boolean graceful);
}
