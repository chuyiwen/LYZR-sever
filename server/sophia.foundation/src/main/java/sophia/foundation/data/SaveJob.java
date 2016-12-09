/**
 * 
 */
package sophia.foundation.data;

import java.util.Collection;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import sophia.foundation.data.ObjectManager.SaveState;



public class SaveJob<T extends SaveableObject> {
	private final long id;
	private Collection<T> saveObjects;
	private Transcation transcation = new Transcation();
	
	public SaveJob(long id, Collection<T> saveObjects) {
		this.id = id;
		this.saveObjects = saveObjects;
		transcation.transitionWaitSave();
	}
	
	public long getId() {
		return id;
	}
	
	public Collection<T> getSaveObjects() {
		return saveObjects;
	}
	
	public Transcation getTranscation() {
		return transcation;
	}
	
	public static class Transcation implements Future<ObjectManager.SaveState> {
		private final CountDownLatch done = new CountDownLatch(1);
		private ObjectManager.SaveState result = SaveState.WaitSave;
		private Throwable failureCause;
		
		public void transitionSaving() {
			this.result = ObjectManager.SaveState.Saving;
		}
		
		public void transitionWaitSave() {
			this.result = ObjectManager.SaveState.WaitSave;
		}
		
		public void transitionSucceeded() {
			this.result = ObjectManager.SaveState.Saved;
			done.countDown();
		}

		public void transitionFailed(Throwable cause) {
			this.result = ObjectManager.SaveState.Failed;
			this.failureCause = cause;
			done.countDown();
		}

		@Override
		public boolean cancel(boolean mayInterruptIfRunning) {
			return false;
		}

		@Override
		public ObjectManager.SaveState get() throws InterruptedException, ExecutionException {
			done.await();
			return getImmediately();
		}

		@Override
		public ObjectManager.SaveState get(long timeout, TimeUnit unit)
				throws InterruptedException, ExecutionException,
				TimeoutException {
			if (done.await(timeout, unit)) {
				return getImmediately();
			}
			throw new TimeoutException();
		}

		@Override
		public boolean isCancelled() {
			return false;
		}

		@Override
		public boolean isDone() {
			return done.getCount() == 0;
		}

		private ObjectManager.SaveState getImmediately() throws ExecutionException {
			if (result == ObjectManager.SaveState.Failed) {
				throw new ExecutionException(failureCause);
			} else {
				return result;
			}
		}
	}
}
