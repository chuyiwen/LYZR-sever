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
package sophia.mmorpg.core;

import sophia.foundation.property.PropertyDictionary;
import sophia.mmorpg.utils.RecyclePool;

public final class PropertyPool extends RecyclePool<PropertyDictionaryModifyPhase> {
	private PropertyDictionary propertyDictionary;
	
	public PropertyPool() {
		
	}
	
	public void setPropertyDictionary(PropertyDictionary propertyDictionary) {
		this.propertyDictionary = propertyDictionary;
	}
	
	public PropertyDictionary getPropertyDictionary() {
		return propertyDictionary;
	}

	@Override
	protected PropertyDictionaryModifyPhase instance() {
		return instanceImpl();
	}

	@Override
	protected void onRecycle(PropertyDictionaryModifyPhase obj) {
		obj.resetPhase();
	}

	private PropertyDictionaryModifyPhase instanceImpl() {
		return PropertyDictionaryModifyPhase.create(propertyDictionary);
	}
}
