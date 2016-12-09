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
package newbee.morningGlory.mmorpg.player.talisman;

public final class MGTalismanContains {
	
	private int index;
	private MGTalisman talisman;
	

	public MGTalismanContains() {
	}
	public MGTalismanContains(int index, MGTalisman talisman) {
		this.index = index;
		this.talisman = talisman;
	}
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	public MGTalisman getTalisman() {
		return talisman;
	}
	public void setTalisman(MGTalisman talisman) {
		this.talisman = talisman;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + index;
		result = prime * result + ((talisman == null) ? 0 : talisman.hashCode());
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
		MGTalismanContains other = (MGTalismanContains) obj;
		if (index != other.index)
			return false;
		if (talisman == null) {
			if (other.talisman != null)
				return false;
		} else if (!talisman.equals(other.talisman))
			return false;
		return true;
	}
	
	
}
