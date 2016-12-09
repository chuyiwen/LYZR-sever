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
package sophia.mmorpg.player.persistence;

import java.util.ArrayList;
import java.util.Collection;

import sophia.foundation.data.AbstractPersistenceObject;
import sophia.foundation.data.PersistenceParameter;
import sophia.foundation.property.PropertyDictionary;

public final class PropertyDictionaryPersistenceObject extends AbstractPersistenceObject {
	public static final String DefaultPropertyDictionaryFieldName = "propertyData";
	
	private PropertyDictionary propertyDictionary;
	
	private PersistenceParameter persistenceParameter;
	
	public PropertyDictionaryPersistenceObject() {
		this.persistenceParameters = new ArrayList<>(1);
	}
	
	public PropertyDictionaryPersistenceObject(PropertyDictionary propertyDictionary) {
		this.persistenceParameters = new ArrayList<>(1);
		this.propertyDictionary = propertyDictionary;
		persistenceParameter = new PersistenceParameter(DefaultPropertyDictionaryFieldName, propertyDictionary.toByteArray());
		this.persistenceParameters.add(persistenceParameter);
	}

	@Override
	public void snapshot() {
		persistenceParameter.setValue(propertyDictionary.toByteArray());
	}

	@Override
	public void setDataFrom(
			Collection<PersistenceParameter> persistenceParameters) {
		for(PersistenceParameter persistenceParameter : persistenceParameters)
		{
			String name = persistenceParameter.getName();
			if(DefaultPropertyDictionaryFieldName.equals(name))
			{
				propertyDictionary.loadDictionary((byte[])persistenceParameter.getValue());
			}
		}
	}

	public final PropertyDictionary getPropertyDictionary() {
		return propertyDictionary;
	}

	public final void setPropertyDictionary(PropertyDictionary propertyDictionary) {
		this.propertyDictionary = propertyDictionary;
		persistenceParameter = new PersistenceParameter(DefaultPropertyDictionaryFieldName, propertyDictionary.toByteArray());
		if (!persistenceParameters.contains(persistenceParameter)) {
			this.persistenceParameters.add(persistenceParameter);
		}
	}
}
