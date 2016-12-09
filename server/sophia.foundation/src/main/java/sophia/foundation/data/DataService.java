/**
 * 
 */
package sophia.foundation.data;

import com.google.common.util.concurrent.Service;


public interface DataService extends Service {
	
	public static final String Enable_Property = "sophia.data.DataService.enable";
	public static final String Interval_Time_Property = "sophia.data.DataService.intervalTime";
	public static final long Default_Interval_Time = 2000 * 10l;
	public static final boolean Default_Enable = false;
	
	public void setDataSaveIntervalTime(long intervalTime);
	
	public long getDataSaveIntervalTime();
	
	public boolean isEnabled();
	
	public void setEnabled(boolean enabled);
	
	public boolean isSaving();
	
	public ObjectManager.SaveState saveState();
	
	public long getSavedCount();
	
	public ObjectManager getObjectManager();
}
