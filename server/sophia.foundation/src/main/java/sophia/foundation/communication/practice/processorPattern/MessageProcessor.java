/**
 * 
 */
package sophia.foundation.communication.practice.processorPattern;

import java.util.Collection;

import sophia.foundation.communication.core.ActionEventBase;
import sophia.foundation.communication.core.Connection;



public interface MessageProcessor {
	<T extends ActionEventBase> void registerMessageType(Class<T> type);
	
	<T extends ActionEventBase> void registerAllMessageType(Collection<Class<T>> typeCollection);
	
	Collection<Class<? extends ActionEventBase>> getRegisterMessageTypes();
	
	<T extends ActionEventBase> boolean registered(Class<T> type);
	
	<T extends ActionEventBase> void processMessage(Connection connection, T message);
}
