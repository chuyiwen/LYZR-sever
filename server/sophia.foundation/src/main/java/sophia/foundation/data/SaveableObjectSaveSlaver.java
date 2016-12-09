/**
 * 
 */
package sophia.foundation.data;

import java.util.concurrent.Future;

import sophia.foundation.data.ObjectManager.SaveMode;



public interface SaveableObjectSaveSlaver<T extends SaveableObject> extends ManagedObjectLoadSlaver<T>{
	public Future<ObjectManager.SaveState> save(SaveMode saveMode, T... saveableObjects) throws Exception;
	
	public SaveJob<T> drainAndSaveCurrentSaveableObjects();
}
