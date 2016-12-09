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

import org.apache.log4j.Logger;
import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;
import sophia.mmorpg.item.Item;

public final class C2G_BAG_Streng extends ActionEventBase {
	private static final Logger logger = Logger.getLogger(C2G_BAG_Streng.class);
	private short gridId;
	private int yuanbao;
	private byte strengthLevel = 0;
	private boolean isUseBinded = false;
	public C2G_BAG_Streng() {
		this.actionEventId = StrengEquipmentEventDefines.C2G_BAG_Streng;
	}

	@Override
	protected IoBuffer packBody(IoBuffer paramIoBuffer) {
		paramIoBuffer.putShort(gridId);
		paramIoBuffer.putInt(yuanbao);
		return paramIoBuffer;
	}
	
	@Override
	public void unpackBody(IoBuffer paramIoBuffer) {
		if(logger.isDebugEnabled()){
			logger.debug("------测试背包装备强化");
		}
		gridId = paramIoBuffer.getShort();
		yuanbao = paramIoBuffer.getInt();
		strengthLevel = paramIoBuffer.get();
		byte binded = paramIoBuffer.get();
		if(binded == Item.Binded){
			isUseBinded = true;
		}
	}
	
	@Override
	public String getName() {
		return "背包装备强化";
	};
	public short getGridId() {
		return gridId;
	}

	public void setGridId(short gridId) {
		this.gridId = gridId;
	}

	public int getYuanbao() {
		return yuanbao;
	}

	public void setYuanbao(int yuanbao) {
		this.yuanbao = yuanbao;
	}

	public byte getStrengthLevel() {
		return strengthLevel;
	}

	public void setStrengthLevel(byte strengthLevel) {
		this.strengthLevel = strengthLevel;
	}

	public boolean isUseBinded() {
		return isUseBinded;
	}

	public void setUseBinded(boolean isUseBinded) {
		this.isUseBinded = isUseBinded;
	}
	
	
}
