/**
 * 
 */
package sophia.foundation.core;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

import sophia.foundation.data.DataService;
import sophia.foundation.data.DataServiceImpl;
import sophia.foundation.data.ObjectManager;
import sophia.foundation.task.TaskManager;
import sophia.foundation.task.TaskService;
import sophia.foundation.tick.LostedTimeListenerManager;
import sophia.foundation.tick.TickService;
import sophia.foundation.tick.TickServiceImpl;
import sophia.foundation.util.MonitorClientEvent;
import sophia.foundation.util.PropertiesWrapper;

import com.google.common.util.concurrent.Service;
import com.google.common.util.concurrent.Service.State;


final class ContextResolver {
	private static final Logger logger = Logger.getLogger(ContextResolver.class
			.getName());

	private static final String Core_Foundation_Properties_Path = "core-foundation.properties";

	private PropertiesWrapper properties = null;

	private TaskService taskService;

	private TaskManager taskManager;

	private DataService dataService;

	private ObjectManager objectManager;

	private TickService tickService;

	private LostedTimeListenerManager lostedTimeListenerManager;

	private final ComponentRegistry serviceComponents = new ComponentRegistryImpl();

	private final ComponentRegistry managerComponents = new ComponentRegistryImpl();

	private final ComponentRegistry systemComponents = new ComponentRegistryImpl();

	ContextResolver() {
		Properties p = new Properties();
		InputStream is = getClass().getClassLoader().getResourceAsStream(
				Core_Foundation_Properties_Path);

		try {
			p.load(is);
		} catch (IOException e) {
			logger.error("core-foundation properties load failed.", e);
		}

		properties = new PropertiesWrapper(p);

		boolean taskServiceEnable = properties.getBooleanProperty(
				TaskService.Enable_Property, true);

		if (taskServiceEnable) {
			int processorCount = properties.getIntProperty(
					TaskService.Processor_Count_Property,
					TaskService.Default_Processor_Count);

			taskService = new TaskService(processorCount);
			State state = taskService.startAndWait();
			if (state != State.RUNNING) {
				taskService = null;
				taskManager = null;
				logger.error("Task service startup failed.The foundation services will run without task service.");
				throw new RuntimeException(
						"Task service startup failed.The foundation services will run without task service.");
			} else {
				taskManager = (TaskManager) taskService;
				if (logger.isInfoEnabled()) {
					logger.info("Foundation Task Service Running......");
				}

				serviceComponents.addComponent(taskService);
				managerComponents.addComponent(taskManager);
			}
		}

		boolean dataServiceEnable = properties.getBooleanProperty(
				DataServiceImpl.Enable_Property, false);
		if (dataServiceEnable) {
			dataService = new DataServiceImpl();
			long batchSaveIntervalTime = properties.getLongProperty(
					DataServiceImpl.Interval_Time_Property,
					DataServiceImpl.Default_Interval_Time);
			dataService.setDataSaveIntervalTime(batchSaveIntervalTime);
			objectManager = dataService.getObjectManager();
			serviceComponents.addComponent(dataService);
			managerComponents.addComponent(objectManager);
		}

		boolean tickServiceEnable = properties.getBooleanProperty(
				TickServiceImpl.Enable_Property, false);
		if (tickServiceEnable) {
			tickService = new TickServiceImpl();
			long tickIntervalTime = properties.getLongProperty(
					TickServiceImpl.Interval_Time_Property,
					TickServiceImpl.Default_Interval_Time);
			tickService.setIntervalTime(tickIntervalTime);
			lostedTimeListenerManager = tickService
					.getLostedTimeListenerManager();
			serviceComponents.addComponent(tickService);
			managerComponents.addComponent(lostedTimeListenerManager);
		}
		
		boolean monitorClientEnable = properties.getBooleanProperty(MonitorClientEvent.Enable_Property, false);
		MonitorClientEvent.getInstance().setEnabled(monitorClientEnable);
		
		int len = properties.getIntProperty(MonitorClientEvent.Packet_Property, 512);
		MonitorClientEvent.getInstance().setMessageLengthLimit(len);
	}

	TaskService getTaskService() {
		return taskService;
	}

	TaskManager getTaskManager() {
		return taskManager;
	}

	DataService getDataService() {
		return dataService;
	}

	ObjectManager getObjectManager() {
		return objectManager;
	}

	TickService getTickService() {
		return tickService;
	}

	LostedTimeListenerManager getLostedTimeListenerManager() {
		return lostedTimeListenerManager;
	}

	<T extends Service> T getService(Class<T> type) {
		if (type == null) {
			logger.error("type can not be null.");
			throw new NullPointerException();
		}

		return serviceComponents.getComponent(type);
	}

	void addService(Service service) {
		if (service == null) {
			logger.error("service can not be null.");
			throw new NullPointerException();
		}

		serviceComponents.addComponent(service);
	}

	<T> T getManager(Class<T> type) {
		if (type == null) {
			logger.error("type can not be null.");
			throw new NullPointerException();
		}

		return managerComponents.getComponent(type);
	}

	void addManager(Object manager) {
		if (manager == null) {
			logger.error("manager can not be null.");
			throw new NullPointerException();
		}

		managerComponents.addComponent(manager);
	}

	<T> T getSystem(Class<T> type) {
		if (type == null) {
			logger.error("type can not be null.");
			throw new NullPointerException();
		}

		return systemComponents.getComponent(type);
	}

	void addSystem(Object system) {
		if (system == null) {
			logger.error("system can not be null.");
			throw new NullPointerException();
		}

		systemComponents.addComponent(system);
	}

	PropertiesWrapper getProperty() {
		return properties;
	}
}
