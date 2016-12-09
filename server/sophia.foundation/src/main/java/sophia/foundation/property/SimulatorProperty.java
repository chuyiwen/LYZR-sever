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

// TODO: consider not to use clone!
// see http://www.artima.com/intv/bloch13.html and Item 11 of Effective Java
public abstract class SimulatorProperty<T> implements Cloneable {
	protected Short id;

	protected SimulatorProperty() {
	}

	public final Short getId() {
		return id;
	}

	public final void setId(Short id) {
		this.id = id;
	}

	public abstract T getValue();

	@SuppressWarnings("unchecked")
	@Override
	public SimulatorProperty<T> clone() {
		try {
			return (SimulatorProperty<T>) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SimulatorProperty<?> other = (SimulatorProperty<?>) obj;
		if (id.equals(other.id))
			return false;
		return true;
	}

}
