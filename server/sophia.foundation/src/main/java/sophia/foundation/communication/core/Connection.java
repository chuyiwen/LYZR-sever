/**
 * 
 */
package sophia.foundation.communication.core;

import java.io.IOException;
import java.net.SocketAddress;


public interface Connection {
	long getId();
	
	boolean isClosing();
	
	boolean isConnected();
	
	void sendMessage(ActionEventBase message) throws IOException;
	
	void close(boolean graceful) throws IOException;
	
	Object getAttribute(Object name);
	
	void setAttributeIfAbsent(Object name, Object value);
	
	void setAttribute(Object name, Object value);
	
	long getCreationTime();
	
	long getLastIoTime();
	
	long getLastReadTime();

	long getLastWriteTime();
	
	long getReadBytes();
	
	String getIP();
	
	SocketAddress getRemoteAddress();
}
