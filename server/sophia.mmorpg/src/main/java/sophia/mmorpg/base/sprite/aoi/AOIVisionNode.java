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
package sophia.mmorpg.base.sprite.aoi;

/**
 * <b>角色-AOI-视野节点</b></br>
 * 
 */
public final class AOIVisionNode {
	private byte id;
	
	private short radius;
	
	public AOIVisionNode() {
		
	}

	public final byte getId() {
		return id;
	}

	public final void setId(byte id) {
		this.id = id;
	}

	public final short getRadius() {
		return radius;
	}

	public final void setRadius(short radius) {
		this.radius = radius;
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
		AOIVisionNode other = (AOIVisionNode) obj;
		if (id != other.id)
			return false;
		return true;
	}
}
