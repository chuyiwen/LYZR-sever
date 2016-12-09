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
package sophia.mmorpg.camp;

import com.google.common.base.Preconditions;

/**
 * 阵营
 */
public class Camp {
	private byte id;
	
	private String name;
	
	private String description;
	
	public Camp() {
		
	}

	public final byte getId() {
		return id;
	}

	public final void setId(byte id) {
		this.id = id;
	}

	public final String getName() {
		return name;
	}

	public final void setName(String name) {
		this.name = name;
	}

	public final String getDescription() {
		return description;
	}

	public final void setDescription(String description) {
		this.description = description;
	}

	public final boolean isHostileCamp(Camp camp) {
		Preconditions.checkNotNull(camp);
		
		return !(this.equals(camp));
	}
	
	@Override
	public String toString() {
		return "Camp [id=" + id + ", name=" + name + ", description="
				+ description + "]";
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
		Camp other = (Camp) obj;
		if (id != other.id)
			return false;
		return true;
	}
}
