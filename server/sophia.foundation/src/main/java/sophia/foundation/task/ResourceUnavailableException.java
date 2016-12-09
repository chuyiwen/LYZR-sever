package sophia.foundation.task;


public class ResourceUnavailableException extends RuntimeException implements ExceptionRetryStatus{
	private static final long serialVersionUID = 4585407118763520213L;

	public ResourceUnavailableException(String message) {
		super(message);
	}

	public ResourceUnavailableException(String message, Throwable cause) {
		super(message, cause);
	}

    @Override
	public boolean shouldRetry() {
		return true;
	}
}
