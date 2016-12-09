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

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public final class C2G_BAG_StrengScroll extends ActionEventBase {
	
	private short scrollGridId;
	private short gridId;
	public C2G_BAG_StrengScroll() {
		this.actionEventId = StrengEquipmentEventDefines.C2G_BAG_StrengScroll;
	}

	@Override
	protected IoBuffer packBody(IoBuffer paramIoBuffer) {
		
		paramIoBuffer.putShort(scrollGridId);
		paramIoBuffer.putShort(gridId);
		return paramIoBuffer;
	}
	
	@Override
	public void unpackBody(IoBuffer paramIoBuffer) {
		scrollGridId = paramIoBuffer.getShort();
		gridId = paramIoBuffer.getShort();
	}
	@Override
	public String getName() {
	
		return "强化卷强化";
	}
	public short getScrollGridId() {
		return scrollGridId;
	}

	public void setScrollGridId(short scrollGridId) {
		this.scrollGridId = scrollGridId;
	}

	public short getGridId() {
		return gridId;
	}

	public void setGridId(short gridId) {
		this.gridId = gridId;
	}
}
