/**
 * 
 */
package sophia.foundation.data;

import java.util.Collection;


public abstract class AbstractManagedObjectLoadSlaver<T extends ManagedObject> implements ManagedObjectLoadSlaver<T> {
	protected abstract Collection<T> doLoad();
	
	@Override
	public final Collection<T> load() {
		return doLoad();
	}
}
