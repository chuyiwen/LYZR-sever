/**
 * 
 */
package sophia.foundation.core;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.MissingResourceException;


public final class ComponentRegistryImpl implements ComponentRegistry{
    private LinkedHashSet<Object> componentSet;

    public ComponentRegistryImpl() {
        componentSet = new LinkedHashSet<Object>();
    }

    public <T> T getComponent(Class<T> type) {
        Object matchingComponent = null;

        for (Object component : componentSet) {
            if (type.isAssignableFrom(component.getClass())) {
                if (matchingComponent != null)
                    throw new MissingResourceException("More than one " +
                                                       "matching component",
                                                       type.getName(), null);
                matchingComponent = component;
            }
        }

        if (matchingComponent == null)
            throw new MissingResourceException("No matching components",
                                               type.getName(), null);

        return type.cast(matchingComponent);
    }

    public void addComponent(Object component) {

         for (Object c : componentSet) {
             if (c.getClass().equals( component.getClass())) {
                 
                     throw new MissingResourceException("exists matched component",
                                                        c.getClass().getName(), null);
             }
         }

        componentSet.add(component);
    }

    @Override
    public Iterator<Object> iterator() {
        return Collections.unmodifiableSet(componentSet).iterator();
    }
}
