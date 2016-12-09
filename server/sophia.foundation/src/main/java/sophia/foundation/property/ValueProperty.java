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
package sophia.foundation.property;


public class ValueProperty<T> extends SimulatorProperty<T> {
	private T value;

	public ValueProperty() {
	}

	public ValueProperty(Short id, T value) {
		this.id = id;
		this.value = value;
	}

//	public String getName() {
//		return SimulatorPropertySymbolContext.getPropertySymbol(getId()).getName();
//	}

	@Override
	public T getValue() {
		return value;
	}

	public void setValue(final T value) {
		this.value = value;
	}

	public T getAndSetValue(final T value) {
		T oldValue = this.value;
		this.value = value;
		return oldValue;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		ValueProperty<?> other = (ValueProperty<?>) obj;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ValueProperty [id=" + id + ", "
				+ (value != null ? "value=" + value : "") + "]";
	}
}
