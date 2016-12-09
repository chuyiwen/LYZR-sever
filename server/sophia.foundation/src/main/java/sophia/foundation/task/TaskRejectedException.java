package sophia.foundation.task;


public class TaskRejectedException extends ResourceUnavailableException {
	private static final long serialVersionUID = -7862298229209850410L;

	public TaskRejectedException(String message) {
		super(message);
	}

	public TaskRejectedException(String message, Throwable cause) {
		super(message, cause);
	}
}
