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
package sophia.mmorpg.player.itemBag;

public final class ItemLuckDataPair {
	private int fortune;
	private int sprobability;
	private int nprobability;
	private int fprobability;
	public int getFortune() {
		return fortune;
	}
	public void setFortune(int fortune) {
		this.fortune = fortune;
	}
	public int getSprobability() {
		return sprobability;
	}
	public void setSprobability(int sprobability) {
		this.sprobability = sprobability;
	}
	public int getNprobability() {
		return nprobability;
	}
	public void setNprobability(int nprobability) {
		this.nprobability = nprobability;
	}
	public int getFprobability() {
		return fprobability;
	}
	public void setFprobability(int fprobability) {
		this.fprobability = fprobability;
	}
	public ItemLuckDataPair(int fortune, int sprobability, int nprobability, int fprobability) {
		this.fortune = fortune;
		this.sprobability = sprobability;
		this.nprobability = nprobability;
		this.fprobability = fprobability;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + fortune;
		result = prime * result + fprobability;
		result = prime * result + nprobability;
		result = prime * result + sprobability;
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
		ItemLuckDataPair other = (ItemLuckDataPair) obj;
		if (fortune != other.fortune)
			return false;
		if (fprobability != other.fprobability)
			return false;
		if (nprobability != other.nprobability)
			return false;
		if (sprobability != other.sprobability)
			return false;
		return true;
	}
	
	
}
