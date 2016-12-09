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

import java.util.ArrayList;
import java.util.List;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public final class C2G_Bag_Decompose extends ActionEventBase {
	
	private short count;
	private List<Short> grids = new ArrayList<Short>();
	public C2G_Bag_Decompose() {
		this.actionEventId = StrengEquipmentEventDefines.C2G_Bag_Decompose;
	}
	@Override
	protected IoBuffer packBody(IoBuffer paramIoBuffer) {
		paramIoBuffer.putShort(count);
		for(int i=0;i<count ; i++){
			paramIoBuffer.putShort(grids.get(i));
		}
		return null;
	}
	
	@Override
	public void unpackBody(IoBuffer paramIoBuffer) {
		count = paramIoBuffer.getShort();
		for(int i=0;i<count ; i++){
			grids.add(paramIoBuffer.getShort());
		}
	}

	public short getCount() {
		return count;
	}

	public void setCount(short count) {
		this.count = count;
	}

	public List<Short> getGrids() {
		return grids;
	}

	public void setGrids(List<Short> grids) {
		this.grids = grids;
	}
	
	@Override
	public String getName() {
		return "背包装备分解";
	}
	
}
