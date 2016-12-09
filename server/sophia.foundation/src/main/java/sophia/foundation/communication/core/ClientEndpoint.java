/**
 * 
 */
package sophia.foundation.communication.core;

import java.io.IOException;


public interface ClientEndpoint<T> {
	Connector<T> createConnector() throws IOException;
	
	T getAddress();
}
