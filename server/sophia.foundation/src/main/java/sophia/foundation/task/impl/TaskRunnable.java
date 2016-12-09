package sophia.foundation.task.impl;


public interface TaskRunnable {
	public String getBaseTaskType();
	
	public void run() throws Exception;
}
