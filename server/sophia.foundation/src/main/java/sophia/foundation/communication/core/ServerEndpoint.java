/**
 * 
 */
package sophia.foundation.communication.core;

import java.io.IOException;
import java.util.Collection;


public interface ServerEndpoint<T> {
	Acceptor<T> createAcceptor() throws IOException;
	
	Collection<T> getAddresses();
}
