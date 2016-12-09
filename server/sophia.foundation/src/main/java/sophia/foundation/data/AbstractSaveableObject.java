/**
 * 
 */
package sophia.foundation.data;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;


public abstract class AbstractSaveableObject implements SaveableObject {
	private static final long serialVersionUID = 8185282735067307223L;

	protected List<PersistenceParameter> persistenceParameters = new ArrayList<>();
	
	private final ReentrantLock lock = new ReentrantLock();

	protected SaveableObjectState saveableObjectState = SaveableObjectState.isNew;
	protected AtomicLong saveVersion = new AtomicLong(0);

	@Override
	public void applyDelete() {
		if (saveableObjectState == SaveableObjectState.isDelete) {
			throw new TransformSaveStateException("Current State was isDelete.can not transform to isDelete.");
		}
		lock.lock();
		try {
			saveableObjectState = SaveableObjectState.isDelete;
			saveVersion.incrementAndGet();
		} finally {
			lock.unlock();
		}
	}

	@Override
	public void applyDirty() {
		lock.lock();
		try {
			if (saveableObjectState == SaveableObjectState.isDirty
					|| saveableObjectState == SaveableObjectState.isSaved) {
				saveableObjectState = SaveableObjectState.isDirty;
				saveVersion.incrementAndGet();
			}
		} finally {
			lock.unlock();
		}
	}

	@Override
	public void applyNew() {
		lock.lock();
		try {
			saveableObjectState = SaveableObjectState.isNew;
		} finally {
			lock.unlock();
		}
	}

	@Override
	public void applySaved() {
		lock.lock();
		try {
			saveableObjectState = SaveableObjectState.isSaved;
		} finally {
			lock.unlock();
		}
	}

	@Override
	public SaveableObjectState getSaveableObjectState() {
		lock.lock();
		try {
			return saveableObjectState;
		} finally {
			lock.unlock();
		}
	}

	@Override
	public long getSaveVersion() {
		return saveVersion.get();
	}
	
	public final List<PersistenceParameter> getPersistenceParameters() {
		return persistenceParameters;
	}
}
