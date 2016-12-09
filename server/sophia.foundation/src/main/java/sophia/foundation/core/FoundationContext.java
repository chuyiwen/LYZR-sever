/**
 * 
 */
package sophia.foundation.core;

import sophia.foundation.data.DataService;
import sophia.foundation.data.ObjectManager;
import sophia.foundation.task.TaskManager;
import sophia.foundation.task.TaskService;
import sophia.foundation.tick.LostedTimeListenerManager;
import sophia.foundation.tick.TickService;
import sophia.foundation.util.PropertiesWrapper;

import com.google.common.util.concurrent.Service;


public final class FoundationContext {
	private static final ContextResolver context = new ContextResolver();
	
	private FoundationContext() {
		
	}
	
	public static <T extends Service> T getService(Class<T> type) {
		return context.getService(type);
	}
	
	public static void addService(Service service) {
		context.addService(service);
	}
	
	public static <T> T getManager(Class<T> type) {
		return context.getManager(type);
	}
	
	public static void addManager(Object manager) {
		context.addManager(manager);
	}
	
	public static <T> T getSystem(Class<T> type) {
		return context.getSystem(type);
	}
	
	public static void addSystem(Object system) {
		context.addSystem(system);
	}
	
	public static PropertiesWrapper getProperties() {
		return context.getProperty();
	}
	
	public static TaskService getTaskService() {
		return context.getTaskService();
	}
	
	public static TaskManager getTaskManager() {
		return context.getTaskManager();
	}
	
	public static DataService getDataService() {
		return context.getDataService();
	}
	
	public static ObjectManager getObjectManager() {
		return context.getObjectManager();
	}
	
	@Deprecated
	public static TickService getTickService() {
		return context.getTickService();
	}
	
	public static LostedTimeListenerManager getLostedTimeListenerManager() {
		return context.getLostedTimeListenerManager();
	}
}
