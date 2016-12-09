/**
 * 
 */
package sophia.foundation.communication.practice.processorPattern.impl;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.MissingResourceException;

import sophia.foundation.communication.core.ActionEventBase;
import sophia.foundation.communication.practice.processorPattern.MessageHandler;
import sophia.foundation.communication.practice.processorPattern.MessageProcessor;

public class SimpleMessageHandler implements MessageHandler {
	private final LinkedHashSet<MessageProcessor> messageProcessorSet = new LinkedHashSet<MessageProcessor>();

	@Override
	public <T extends ActionEventBase> MessageProcessor getMessageProcessor(Class<T> type) {
		if (type == null) {
			throw new NullPointerException();
		}

		synchronized (messageProcessorSet) {
			for (MessageProcessor messageProcessor : messageProcessorSet) {
				if (messageProcessor.registered(type)) {
					return messageProcessor;
				}
			}
		}

		throw new MissingResourceException("No matching processor for the message type.", type.getName(), null);
	}

	@Override
	public void registerProcessor(MessageProcessor processor) {
		if (processor == null) {
			throw new NullPointerException();
		}

		Collection<Class<? extends ActionEventBase>> messageTypes = processor.getRegisterMessageTypes();

		synchronized (messageProcessorSet) {
			for (Class<? extends ActionEventBase> type : messageTypes) {
				for (MessageProcessor messageProcessor : messageProcessorSet) {
					if (messageProcessor.registered(type)) {
						throw new MissingResourceException("More then one matching message type registered.", type.getName(), null);
					}
				}
			}

			messageProcessorSet.add(processor);
		}
	}
}
