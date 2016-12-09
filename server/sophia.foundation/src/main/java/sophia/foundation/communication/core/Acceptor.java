/**
 * 
 */
package sophia.foundation.communication.core;

import java.io.IOException;


public interface Acceptor<T> {
	void listen(AcceptorListener listener) throws IOException;
	
	ServerEndpoint<T> getEndpoint();
	
	void shutDown();
}
