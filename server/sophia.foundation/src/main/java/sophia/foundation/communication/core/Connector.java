/**
 * 
 */
package sophia.foundation.communication.core;

import java.io.IOException;


public interface Connector<T> {
	void connect(ConnectionListener listener) throws IOException;

	boolean waitForConnect(long timeout) throws IOException,
			InterruptedException;
	
	boolean isConnected();
	
	Connection getConnection();
	
	ClientEndpoint<T> getEndpoint();
	
	void shutDown();
}
