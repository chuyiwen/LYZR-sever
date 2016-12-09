/**
 * 
 */
package sophia.foundation.data;


public interface SaveableObject extends ManagedObject {
	public static enum SaveableObjectState {
		
		isSaved,
		
		isNew,
		
		isDelete,
		
		isDirty;
	}
	
	public SaveableObjectState getSaveableObjectState();
	
	public void applySaved();
	
	public void applyNew();
	
	public void applyDelete();
	
	public void applyDirty();
	
	public long getSaveVersion();
}
