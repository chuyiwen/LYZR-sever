/**
 * 
 */
package sophia.foundation.communication.practice.processorPattern;

import sophia.foundation.communication.core.ActionEventBase;
import sophia.foundation.communication.core.Connection;
import sophia.foundation.communication.core.ConnectionListener;
import sophia.foundation.communication.practice.processorPattern.impl.SimpleMessageHandler;


public abstract class AbstractConnectionListener implements ConnectionListener {
	private final MessageHandler messageHandler;
	
	protected AbstractConnectionListener() {
		this(new SimpleMessageHandler());
	}
	
	protected AbstractConnectionListener(MessageHandler messageHandler) {
		if (messageHandler == null) {
			throw new NullPointerException();
		}
		
		this.messageHandler = messageHandler;
	}
	
	@Override
	public void messageReceived(Connection connection, ActionEventBase message) {
		MessageProcessor messageProcessor = messageHandler.getMessageProcessor(message.getClass());
		messageProcessor.processMessage(connection, message);
	}
}
