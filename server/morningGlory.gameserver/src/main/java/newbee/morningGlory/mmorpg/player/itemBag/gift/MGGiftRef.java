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
package newbee.morningGlory.mmorpg.player.itemBag.gift;


public final class MGGiftRef  {
	
	private String key;
	private String itemRefId;
	private int number;
	private byte bindStatus;
	private int probability;
	
	public MGGiftRef() {
		super();
	}
	public MGGiftRef(String key, String itemRefId, int number,byte bindStatus, int probability) {
		this.key = key;
		this.itemRefId = itemRefId;
		this.number = number;
		this.bindStatus = bindStatus;
		this.probability = probability;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getItemRefId() {
		return itemRefId;
	}
	public void setItemRefId(String itemRefId) {
		this.itemRefId = itemRefId;
	}
	public int getNumber() {
		return number;
	}
	public void setNumber(int number) {
		this.number = number;
	}
	public int getProbability() {
		return probability;
	}
	public void setProbability(int probability) {
		this.probability = probability;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((itemRefId == null) ? 0 : itemRefId.hashCode());
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		result = prime * result + number;
		result = prime * result + probability;
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
		MGGiftRef other = (MGGiftRef) obj;
		if (itemRefId == null) {
			if (other.itemRefId != null)
				return false;
		} else if (!itemRefId.equals(other.itemRefId))
			return false;
		if (key == null) {
			if (other.key != null)
				return false;
		} else if (!key.equals(other.key))
			return false;
		if (number != other.number)
			return false;
		if (probability != other.probability)
			return false;
		return true;
	}
	public byte getBindStatus() {
		return bindStatus;
	}
	public void setBindStatus(byte bindStatus) {
		this.bindStatus = bindStatus;
	}
	
	
}
