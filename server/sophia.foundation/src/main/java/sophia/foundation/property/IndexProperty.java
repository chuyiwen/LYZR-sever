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

import java.util.List;

// use case?
public class IndexProperty<T> extends SimulatorProperty<T> {
	private short index;
	private List<T> list;
	
	@Override
	public T getValue() {
		return list.get(index);
	}
	
	public T getValue(int index) {
		return list.get(index);
	}

	public T getAndSetValue(final short index) {
		final short oldIndex = this.index;
		this.index = index;
		return list.get(oldIndex);
	}
	
	// the caller already know the value of index
	@Deprecated
	public T setAndGetValue(final short index) {
		this.index = index;
		return list.get(this.index);
	}
	
	public final short getIndex() {
		return index;
	}

	public final void setIndex(short index) {
		this.index = index;
	}

	public final List<T> getList() {
		return list;
	}

	public final void setList(List<T> list) {
		this.list = list;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + index;
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
		IndexProperty<?> other = (IndexProperty<?>) obj;
		if (index != other.index)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "IndexProperty [id=" + id + ", index=" + index + ", "
				+ (getValue() != null ? "getValue()=" + getValue() : "") + "]";
	}
}
