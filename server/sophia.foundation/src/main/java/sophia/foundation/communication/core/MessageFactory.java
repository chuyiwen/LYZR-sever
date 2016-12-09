/**
 * 
 */
package sophia.foundation.communication.core;

import java.util.LinkedHashMap;

import org.apache.log4j.Logger;

import sophia.foundation.util.DebugUtil;


public final class MessageFactory {
	private static final Logger logger = Logger.getLogger(MessageFactory.class);

	private static final LinkedHashMap<Short, Class<? extends ActionEventBase>> IdToMessageClassMap = new LinkedHashMap<Short, Class<? extends ActionEventBase>>();

	private static final Class<?>[] EMPTY_PARAMS = new Class[0];

	public static final void addMessage(short messageId, Class<? extends ActionEventBase> messageClass) {
		if (messageClass == null) {
			throw new NullPointerException();
		}

		try {
			messageClass.getConstructor(EMPTY_PARAMS);
		} catch (NoSuchMethodException e) {
			throw new IllegalArgumentException("The specified class doesn't have a public default constructor.");
		}

		if (IdToMessageClassMap.containsKey(messageId) ) {
			throw new IllegalArgumentException("The messageClasses has already the commandId:[" + messageId + "] mapping class @ " + IdToMessageClassMap.get(messageId) );
		}
		
		if( IdToMessageClassMap.containsValue(messageClass) )
		{
			throw new IllegalArgumentException("The messageClasses has already the commandId mapping class @ " + messageClass );
		}

		if (!ActionEventBase.class.isAssignableFrom(messageClass)) {
			throw new IllegalArgumentException("Unregisterable type: " + messageClass);
		}

		IdToMessageClassMap.put(messageId, messageClass);
	}

	public static final ActionEventBase getMessage(short messageId) {
		if (logger.isDebugEnabled()) {
			logger.debug("messageId:" + messageId);
		}

		Class<? extends ActionEventBase> messageClass = IdToMessageClassMap
				.get(messageId);
		try {
			if (messageClass == null) {
				logger.error("getMessage error, can't find message object, messageId=" + messageId);
				return null;
			} 
			
			ActionEventBase actionEvent = messageClass.newInstance();
			actionEvent.setActionEventId(messageId);
			return actionEvent;
		} catch (Exception e) {
			logger.error(DebugUtil.printStack(e));
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends ActionEventBase> T getConcreteMessage(short messageId) {
		return (T) getMessage(messageId);
	}

	public static final int getMessageNumber() {
		return IdToMessageClassMap.size();
	}
	
	public static void resetMessageClass(short messageId, String className) {
		if (logger.isInfoEnabled()) {
			logger.info("resetMessageClass, messageId=" + messageId + ", className=" + className);
		}
		
		try {
			Class<?> clazz = Class.forName(className);
			if (!ActionEventBase.class.isAssignableFrom(clazz)) {
				throw new IllegalArgumentException("Unregisterable type: " + clazz);
			}
			
			@SuppressWarnings("unchecked")
			Class<? extends ActionEventBase> clz = (Class<? extends ActionEventBase>) clazz;
			IdToMessageClassMap.put(messageId, clz);
		} catch (ClassNotFoundException e) {
			logger.error(DebugUtil.printStack(e));
		}
	}
}
