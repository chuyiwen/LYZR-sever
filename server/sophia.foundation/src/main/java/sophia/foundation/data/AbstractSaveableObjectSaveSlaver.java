/**
 * 
 */
package sophia.foundation.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.log4j.Logger;

import sophia.foundation.core.FoundationContext;
import sophia.foundation.data.ObjectManager.SaveMode;
import sophia.foundation.data.ObjectManager.SaveState;
import sophia.foundation.data.SaveJob.Transcation;
import sophia.foundation.task.Task;
import sophia.foundation.util.DebugUtil;

public abstract class AbstractSaveableObjectSaveSlaver<T extends SaveableObject> extends AbstractManagedObjectLoadSlaver<T> implements SaveableObjectSaveSlaver<T> {
	private static final Logger logger = Logger.getLogger(AbstractSaveableObjectSaveSlaver.class.getName());

	private final BlockingQueue<T> saveableObjectsQueue = new LinkedBlockingQueue<T>();

	private final AtomicLong jobId = new AtomicLong(0);

	private final Transcation batchSaveTranscation = new Transcation();

	/**
	 * This method is called by {@link #save(SaveMode, SaveableObject...)}to
	 * save the <b>saveJob</b>. If save success,the invocation should cause a
	 * call to {@link #notifySaved()} If save fails, the invocation should cause
	 * a call to {@link #notifyFailed(Throwable)}
	 * 
	 * @param saveJob
	 */
	protected abstract void doSave(SaveJob<T> saveJob) throws Exception;

	@Override
	public Future<ObjectManager.SaveState> save(SaveMode saveMode, T... saveableObjects) throws Exception {
		Transcation transcation = null;

		if (saveMode == SaveMode.ImmediatelyAndWaitSave) {
			SaveJob<T> saveJob = createSaveJob(saveableObjects);

			try {
				saveJob.getTranscation().transitionSaving();
				doSave(saveJob);
			} catch (Exception e) {
				notifyFailed(saveJob, e);
				throw e;
			}

			notifySaved(saveJob);
			transcation = saveJob.getTranscation();
		} else if (saveMode == SaveMode.ImmediatelySave) {
			final SaveJob<T> saveJob = createSaveJob(saveableObjects);
			FoundationContext.getTaskManager().scheduleTask(new Task() {
				@Override
				public void run() throws Exception {
					try {
						saveJob.getTranscation().transitionSaving();
						doSave(saveJob);
					} catch (Exception e) {
						notifyFailed(saveJob, e);
						throw e;
					}

					notifySaved(saveJob);

					Future<SaveState> future = saveJob.getTranscation();
					try {
						if (future.get() != SaveState.Saved) {
							logger.error("save failed.");
						}
					} catch (InterruptedException e) {
						logger.error("InterruptedException.", e);
					} catch (ExecutionException e) {
						logger.error("ExecutionException.", e);
					}
				}

			});

			transcation = saveJob.getTranscation();
		} else if (saveMode == SaveMode.PeriodBatchSave) {
			try {
				synchronized (saveableObjectsQueue) {
					for (T saveableObject : saveableObjects) {
						if (!saveableObjectsQueue.contains(saveableObject)) {
							saveableObjectsQueue.put(saveableObject);
						}
					}
				}
			} catch (InterruptedException e) {
			}
			transcation = batchSaveTranscation;
		}

		return transcation;
	}

	@SuppressWarnings("unchecked")
	@Override
	public SaveJob<T> drainAndSaveCurrentSaveableObjects() {
		Collection<T> saveableObjects = new ArrayList<T>();
		if (logger.isDebugEnabled()) {
			logger.debug("per dain save jobs queue size: " + saveableObjectsQueue.size());
		}
		saveableObjectsQueue.drainTo(saveableObjects);
		if (logger.isDebugEnabled()) {
			logger.debug("after dain save jobs queue size: " + saveableObjectsQueue.size());
		}

		if (saveableObjects.size() <= 0) {
			if (logger.isDebugEnabled()) {
				logger.debug(this.getClass().getSimpleName() + " non saveableObject to save.");
			}
			return null;
		}

		T[] saveableObjectArray = (T[]) new SaveableObject[saveableObjects.size()];
		if (logger.isDebugEnabled()) {
			logger.debug(this.getClass().getSimpleName() + "current need save objects count: " + saveableObjects.size());
		}
		T[] array = saveableObjects.toArray(saveableObjectArray);

		SaveJob<T> saveJob = createSaveJob(array);

		try {
			saveJob.getTranscation().transitionSaving();
			batchSaveTranscation.transitionSaving();
			doSave(saveJob);
		} catch (Exception e) {
			logger.error(DebugUtil.printStack(e));
			notifyFailed(saveJob, e);
			batchSaveTranscation.transitionFailed(e);
		}

		notifySaved(saveJob);
		batchSaveTranscation.transitionSucceeded();

		Future<SaveState> future = saveJob.getTranscation();
		try {
			if (future.get() != SaveState.Saved) {
				logger.error("save failed.");
			}
		} catch (InterruptedException e) {
			logger.error("InterruptedException.", e);
		} catch (ExecutionException e) {
			logger.error("ExecutionException.", e);
		}

		return saveJob;
	}

	protected final void notifySaved(SaveJob<T> saveJob) {
		saveJob.getTranscation().transitionSucceeded();
	}

	protected final void notifyFailed(SaveJob<T> saveJob, Throwable cause) {
		saveJob.getTranscation().transitionFailed(cause);
	}

	private SaveJob<T> createSaveJob(T... saveableObjects) {
		final Collection<T> objects = new ArrayList<T>();
		for (T object : saveableObjects) {
			objects.add(object);
		}
		SaveJob<T> job = new SaveJob<T>(jobId.incrementAndGet(), objects);

		return job;
	}
}
