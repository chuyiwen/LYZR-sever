/**
 * 
 */
package sophia.foundation.data;

import java.util.Collection;
import java.util.concurrent.Future;



public interface ObjectManager {
	public enum SaveMode {
		ImmediatelySave,
		ImmediatelyAndWaitSave,
		PeriodBatchSave;
	}
	
	public static enum SaveState {
		WaitSave,
		
		Saving,
		
		Saved,
		
		Failed;
	}

	public void addReadonlyObjectLoadSlaver(Class<ReadonlyObject> objectType, ManagedObjectLoadSlaver<ReadonlyObject> objectLoadSlaver);
	
	public void addSaveableObjectSaveSlaver(Class<? extends SaveableObject> objectType, SaveableObjectSaveSlaver<SaveableObject> objectSaveSlaver);
	
	public <T extends ManagedObject> Collection<T> load(Class<T> type);
	
	public <T extends SaveableObject> Future<SaveState> save(SaveMode saveMode, Class<T> type, T... objects) throws Exception;
}
