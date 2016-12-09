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
package newbee.morningGlory.mmorpg.player.pk.ref;

public final class MGInvasionPair {
	private String key;
	private int number;
	private String itemRefId;
	private int probability;
	private byte bindStatus;
	public MGInvasionPair(String key, int number, int probability,String itemRefId,byte bindStatus) {
		this.key = key;
		this.setItemRefId(itemRefId);
		this.number = number;
		this.probability = probability;
		this.bindStatus = bindStatus;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
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
	public String getItemRefId() {
		return itemRefId;
	}
	public void setItemRefId(String itemRefId) {
		this.itemRefId = itemRefId;
	}
	public byte getBindStatus() {
		return bindStatus;
	}
	public void setBindStatus(byte bindStatus) {
		this.bindStatus = bindStatus;
	}
	
	
}
