/**
 * 
 */
package sophia.foundation.tick;

import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;


public class SimpleLostedTimeListenerManager implements LostedTimeListenerManager {
	private final CopyOnWriteArrayList<LostedTimeListener> list = new CopyOnWriteArrayList<LostedTimeListener>(); 
	
	@Override
	public void addLostedTimeListener(LostedTimeListener listener) {
		if (listener == null) {
			throw new NullPointerException();
		}
		
		list.add(listener);
	}

	@Override
	public void clear() {
		list.clear();
	}

	@Override
	public void removeLostedTimeListener(LostedTimeListener listener) {
		list.remove(listener);
	}

	@Override
	public Collection<LostedTimeListener> getCollection() {
		return list;
	}

	@Override
	public int size() {
		return list.size();
	}
}
