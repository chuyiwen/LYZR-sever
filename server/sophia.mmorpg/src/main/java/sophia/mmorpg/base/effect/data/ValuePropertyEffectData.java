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
package sophia.mmorpg.base.effect.data;

import sophia.foundation.property.PropertyDictionary;
import sophia.foundation.property.ValueProperty;
import sophia.foundation.property.symbol.SimulatorPropertySymbolContext;

public final class ValuePropertyEffectData {
	public final static byte EffectMode_Value = 0;
	public final static byte EffectMode_Total_Percentage = 1;
	public final static byte EffectMode_Base_Percentage = 2;
	
	/** 影响的属性的符号标识Id */
	private short PropertySymbolId;
	/** 影响的方式： */
	private byte effectMode;
	/** 影响的参数值 */
	private int affectValue;

	public ValuePropertyEffectData() {

	}

	public ValuePropertyEffectData(short PropertySymbolId, byte effectMode,
			int affectValue) {
		this.PropertySymbolId = PropertySymbolId;
		this.effectMode = effectMode;
		this.affectValue = affectValue;
	}

	public final short getPropertySymbolId() {
		return PropertySymbolId;
	}

	public final void setPropertySymbolId(short propertySymbolId) {
		PropertySymbolId = propertySymbolId;
	}

	public final byte getEffectMode() {
		return effectMode;
	}

	public final void setEffectMode(byte effectMode) {
		this.effectMode = effectMode;
	}

	public final int getAffectValue() {
		return affectValue;
	}

	public final void setAffectValue(int affectValue) {
		this.affectValue = affectValue;
	}
	
	public boolean affectableDictionary(PropertyDictionary dictionary) {
		String type = SimulatorPropertySymbolContext.getPropertySymbol(
				PropertySymbolId).getType();
		if (type.equals("int32")) {
			ValueProperty<Integer> valueProperty = dictionary
					.getValueProperty(PropertySymbolId);
			if (valueProperty == null)
				throw new RuntimeException("受影响目标不包含效果数据影响的符号！");
			
			return true;
		} else {
			return false;
		}
	}

	public boolean affectDictionary(PropertyDictionary dictionary) {
		String type = SimulatorPropertySymbolContext.getPropertySymbol(
				PropertySymbolId).getType();
		if (type.equals("int32")) {
			ValueProperty<Integer> valueProperty = dictionary
					.getValueProperty(PropertySymbolId);
			if (valueProperty == null)
				throw new RuntimeException("受影响目标不包含效果数据影响的符号！");
			int afterValue = getEffectedValue(valueProperty);
			
			valueProperty.setValue(afterValue);
			return true;
		}
		return true;
	}

	private int getEffectedValue(ValueProperty<Integer> valueProperty) {
		if (effectMode == ValuePropertyEffectData.EffectMode_Value) {
			return valueProperty.getValue() + affectValue;
		} else if (effectMode == ValuePropertyEffectData.EffectMode_Total_Percentage) {
			return (int) (valueProperty.getValue() * (1 + affectValue / 10000.0));
		} else {
			throw new RuntimeException("效果的影响方式（" + effectMode + "）没定义！");
		}
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + PropertySymbolId;
		result = prime * result + affectValue;
		result = prime * result + effectMode;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		return false;
	}

	@Override
	public String toString() {
		return "ValuePropertyEffectData [PropertySymbolId=" + PropertySymbolId
				+ ", effectMode=" + effectMode + ", affectValue=" + affectValue
				+ "]";
	}
}
