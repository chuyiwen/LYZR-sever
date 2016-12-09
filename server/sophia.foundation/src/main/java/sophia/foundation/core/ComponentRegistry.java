/**
 * 
 */
package sophia.foundation.core;


public interface ComponentRegistry extends Iterable<Object> {
	public <T> T getComponent(Class<T> type);
	
	void addComponent(Object component);
}
