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
package sophia.mmorpg.equipmentSmith.smith.actionEvent;

import java.util.Map;
import java.util.Map.Entry;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public final class G2C_Bag_Decompose extends ActionEventBase {
	
	private int totalGold;
	private long totalExp;
	private short itemCount;
	private short count;
	private Map<String,Integer> posMap;
	
	public G2C_Bag_Decompose(){
		ziped =(byte)1;
	}
	
	@Override
	public void unpackBody(IoBuffer paramIoBuffer) {
		
	}

	@Override
	protected IoBuffer packBody(IoBuffer paramIoBuffer) {
		paramIoBuffer.putInt(totalGold);
		paramIoBuffer.putLong(totalExp);
		paramIoBuffer.putShort(itemCount);
		paramIoBuffer.putShort(count);
		for(Entry<String,Integer> entry : posMap.entrySet()){
			String refId = entry.getKey();
			int number = entry.getValue();
			putString(paramIoBuffer, refId);
			paramIoBuffer.putShort((short)number);
		}
		return null;
	}
	
	public int getTotalGold() {
		return totalGold;
	}

	public void setTotalGold(int totalGold) {
		this.totalGold = totalGold;
	}

	public long getTotalExp() {
		return totalExp;
	}

	public void setTotalExp(long totalExp) {
		this.totalExp = totalExp;
	}

	public short getItemCount() {
		return itemCount;
	}

	public void setItemCount(short itemCount) {
		this.itemCount = itemCount;
	}

	public short getCount() {
		return count;
	}

	public void setCount(short count) {
		this.count = count;
	}

	public Map<String, Integer> getPosMap() {
		return posMap;
	}

	public void setPosMap(Map<String, Integer> posMap) {
		this.posMap = posMap;
	}

	
	
	

}
