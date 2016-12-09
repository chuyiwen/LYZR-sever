
package sophia.game.plugIns.taskManager;

import org.apache.log4j.Logger;

import sophia.foundation.core.FoundationContext;
import sophia.foundation.task.TaskManager;
import sophia.foundation.task.TaskService;
import sophia.game.core.PlugIn;

import com.google.common.util.concurrent.Service.State;


public class TaskManagerPlugIn implements PlugIn<TaskManager> {

	private static final Logger logger = Logger.getLogger(TaskManagerPlugIn.class);
	
	private TaskManager taskManager;
	
	@Override
	public TaskManager getModule() {
		return taskManager;
	}

	@Override
	public void initialize() {
		taskManager = FoundationContext.getTaskManager();
	}

	@Override
	public void start() {
		TaskService taskService = FoundationContext.getTaskService();
		State taskServiceState = taskService.state();
		if (taskServiceState == State.RUNNING) {
			logger.info("TaskService was running.");
		} else {
			logger.error("TaskService start failed. the server will run without TaskService.");
			throw new RuntimeException("TaskService start failed. the server will run without TaskService.");
		}
	}

	@Override
	public void stop() {
		TaskService taskService = FoundationContext.getTaskService();
		State taskServiceState = taskService.stopAndWait();
		if (taskServiceState == State.TERMINATED) {
			logger.info("TaskService was terminated.");
		} else {
			logger.error("TaskService stop failed.");
		}
	}

	@Override
	public void cleanUp() {
	}

}
