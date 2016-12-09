/**
 * 
 */
package sophia.foundation.communication.core;


public interface AcceptorListener {
	ConnectionListener newConnection();
	
	void disconnected();
}
