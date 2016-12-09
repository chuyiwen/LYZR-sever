/**
 * 
 */
package sophia.foundation.tick;

import java.util.Collection;


public interface LostedTimeListenerManager {
	public int size();
	
	public Collection<LostedTimeListener> getCollection();
	
	public void addLostedTimeListener(LostedTimeListener listener);

	public void removeLostedTimeListener(LostedTimeListener listener);

	public void clear();
}
