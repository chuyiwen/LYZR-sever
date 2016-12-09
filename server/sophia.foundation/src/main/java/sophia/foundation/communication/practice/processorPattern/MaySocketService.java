/**
 * 
 */
package sophia.foundation.communication.practice.processorPattern;

import sophia.foundation.communication.core.AcceptorListener;
import sophia.foundation.communication.core.ConnectionListener;


public interface MaySocketService {
	public String getHost();
	
	public void setHost(String host);
	
	public int[] getBindPorts();
	
	public void setBindPorts();
	
	public AcceptorListener getAcceptorListener();
	
	public void setAcceptorListener(AcceptorListener acceptorListener);
	
	public ConnectionListener getConnectionListener();
	
	public void setConnectionListener(ConnectionListener connectionListener);
}
