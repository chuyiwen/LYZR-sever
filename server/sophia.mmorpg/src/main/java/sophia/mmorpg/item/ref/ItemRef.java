/**
 *   Copyright 2013-2015 Sophia
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package sophia.mmorpg.item.ref;

import sophia.foundation.core.ComponentRegistry;
import sophia.foundation.core.ComponentRegistryImpl;
import sophia.foundation.property.PropertyDictionary;
import sophia.game.ref.AbstractGameRefObjectBase;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;


public final class ItemRef extends AbstractGameRefObjectBase implements Comparable<ItemRef> {
	private static final long serialVersionUID = 1047021520529846565L;
	private PropertyDictionary effectProperty = new PropertyDictionary(); 
	
	private ComponentRegistry componentRegistry = new ComponentRegistryImpl(); 

	public ItemRef() {

	}

	@Override
	public int compareTo(ItemRef other) {
		int itemSortId = MGPropertyAccesser.getItemSortId(getProperty());
		int otherItemSortId = MGPropertyAccesser.getItemSortId(other.getProperty());

		return itemSortId > otherItemSortId ? 1 : (itemSortId == otherItemSortId ? 0 : -1);
	}

	public PropertyDictionary getEffectProperty() {
		return effectProperty;
	}

	public void setEffectProperty(PropertyDictionary effectProperty) {
		this.effectProperty = effectProperty;
	}

	public void addComponentRef(Object obj) {
		componentRegistry.addComponent(obj);
	}
	
	public <T> T getComponentRef(Class<T> type) {
		return componentRegistry.getComponent(type);
	}
}
