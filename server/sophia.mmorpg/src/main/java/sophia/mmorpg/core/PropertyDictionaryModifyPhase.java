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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import sophia.foundation.property.PropertyDictionary;
import sophia.foundation.property.SimulatorProperty;
import sophia.foundation.property.ValueProperty;

/**
 * 属性字典更新标记状态类。
 */
public final class PropertyDictionaryModifyPhase {
	private PropertyDictionary propertyDictionary = new PropertyDictionary();

	private List<PropertyModifyState> phaseSet = new ArrayList<>();

	private PropertyDictionaryModifyPhase() {

	}

	public PropertyDictionaryModifyPhase(PropertyDictionaryModifyPhase other) {
		if (other == null)
			return;
		this.propertyDictionary = other.propertyDictionary.clone();
		for (PropertyModifyState state : other.phaseSet) {
			this.phaseSet.add(new PropertyModifyState(state));
		}
	}

	public static final PropertyDictionaryModifyPhase create(PropertyDictionary propertyDictionary) {
		PropertyDictionaryModifyPhase ret = new PropertyDictionaryModifyPhase();

		ret.propertyDictionary = new PropertyDictionary(propertyDictionary.getDictionary().size());

		Map<Short, SimulatorProperty<?>> dictionary = propertyDictionary.getDictionary();
		ret.phaseSet = new ArrayList<>(dictionary.size());
		Set<Entry<Short, SimulatorProperty<?>>> entrySet = dictionary.entrySet();
		for (Entry<Short, SimulatorProperty<?>> entry : entrySet) {
			Short key = entry.getKey();
			ret.phaseSet.add(new PropertyModifyState(key));
			ret.getPropertyDictionary().addOrPutValue(key, 0);
		}

		return ret;
	}

	public void resetPhase() {
		for (PropertyModifyState modifyState : phaseSet) {
			modifyState.setDirty(false);
		}
		Set<Entry<Short, SimulatorProperty<?>>> entrySet = propertyDictionary.getDictionary().entrySet();
		for (Entry<Short, SimulatorProperty<?>> entry : entrySet) {
			@SuppressWarnings("unchecked")
			ValueProperty<Integer> valueProperty = (ValueProperty<Integer>) entry.getValue();
			valueProperty.setValue(0);
		}
	}

	public void phaseTrueAll() {
		for (PropertyModifyState modifyState : phaseSet) {
			modifyState.setDirty(true);
		}
	}

	public void modify(PropertyDictionaryModifyPhase other) {
		List<PropertyModifyState> otherPhaseStateList = other.getPhaseSet();
		for (PropertyModifyState modifyState : otherPhaseStateList) {
			if (modifyState.isDirty()) {
				ValueProperty<Integer> valueProperty = other.getPropertyDictionary().getValueProperty(modifyState.getId());
				modify(valueProperty);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public void modify(PropertyDictionary property) {
		for (Map.Entry<Short, SimulatorProperty<?>> entry : property.getDictionary().entrySet()) {
			ValueProperty<Integer> valueProperty = (ValueProperty<Integer>) entry.getValue();
			this.propertyDictionary.addJustOnly(valueProperty);
			for (PropertyModifyState modifyState : this.phaseSet) {
				if (modifyState.getId().equals(valueProperty.getId())) {
					modifyState.setDirty(true);
				}
			}
		}
	}

	public void modify(ValueProperty<Integer> valueProperty) {
		propertyDictionary.add(valueProperty);

		for (PropertyModifyState modifyState : phaseSet) {
			if (modifyState.getId().equals(valueProperty.getId())) {
				modifyState.setDirty(true);
			}
		}
	}

	public void add(Short id, int value) {
		propertyDictionary.setOrPutValue(id, value);
	}

	public void modify(Short id, int delta) {
		ValueProperty<Integer> valueProperty = propertyDictionary.getValueProperty(id);
		if (valueProperty != null) {
			valueProperty.setValue(valueProperty.getValue() + delta);
			for (PropertyModifyState modifyState : phaseSet) {
				if (modifyState.getId().equals(valueProperty.getId())) {
					modifyState.setDirty(true);
				}
			}
		}

	}

	public boolean isModify(ValueProperty<Integer> valueProperty) {
		for (PropertyModifyState modifyState : phaseSet) {
			if (modifyState.getId().equals(valueProperty.getId())) {
				return modifyState.isDirty();
			}
		}

		throw new RuntimeException(valueProperty.getId() + " 不在字典内。");
	}

	public PropertyDictionary getPropertyDictionary() {
		return propertyDictionary;
	}

	public List<PropertyModifyState> getPhaseSet() {
		return phaseSet;
	}

	public void subAllValue() {
		Set<Entry<Short, SimulatorProperty<?>>> entrySet = propertyDictionary.getDictionary().entrySet();
		for (Entry<Short, SimulatorProperty<?>> entry : entrySet) {
			@SuppressWarnings("unchecked")
			ValueProperty<Integer> valueProperty = (ValueProperty<Integer>) entry.getValue();
			valueProperty.setValue(0 - valueProperty.getValue());
		}
	}

	public PropertyDictionary getModifiedAttributes() {
		PropertyDictionary property = new PropertyDictionary();
		for (PropertyModifyState modifyState : phaseSet) {
			if (modifyState.isDirty()) {
				SimulatorProperty<?> value = this.propertyDictionary.getProperty(modifyState.getId());
				property.addProperty(value);
			}
		}
		return property;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (PropertyModifyState state : this.phaseSet) {
			if (state.isDirty()) {
				sb.append("[id=" + state.getId() + ", value=" + this.propertyDictionary.getValue(state.getId()) + "]");
			}
		}
		return sb.toString();
	}
}
