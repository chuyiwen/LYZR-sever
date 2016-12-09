/**
 * 
 */
package sophia.foundation.tick;

import com.google.common.util.concurrent.Service;


public interface TickService extends Service {
	public long getStartTime();
	
	public long getStopTime();
	
	public void setIntervalTime(long intervalTime);
	
	public long getIntervalTime();
	
	public LostedTimeListenerManager getLostedTimeListenerManager();
}
