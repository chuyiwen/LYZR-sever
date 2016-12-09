/**
 * 
 */
package sophia.foundation.communication.practice.processorPattern;

import sophia.foundation.communication.core.ActionEventBase;



public interface MessageHandler {
	void registerProcessor(MessageProcessor processor);
	
	<T extends ActionEventBase> MessageProcessor getMessageProcessor(Class<T> type);
}
