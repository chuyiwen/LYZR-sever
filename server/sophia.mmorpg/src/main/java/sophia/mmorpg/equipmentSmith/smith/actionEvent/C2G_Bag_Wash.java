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
import sophia.mmorpg.base.sprite.fightProperty.FightEffectProperty;
import sophia.mmorpg.item.Item;

public final class C2G_Bag_Wash extends ActionEventBase {
	
	
	private short count;
	private short gridId;
	private boolean isUseBinded = false;
	private List<Short> symbols = new ArrayList<>();
	
	public C2G_Bag_Wash() {
		this.actionEventId = StrengEquipmentEventDefines.C2G_Bag_Wash;
	}
	@Override
	protected IoBuffer packBody(IoBuffer paramIoBuffer) {
		paramIoBuffer.putShort(gridId);
		paramIoBuffer.putShort(count);
		
		for(int i=0;i<count;i++){
			paramIoBuffer.putShort(symbols.get(i));
		}
		return null;
	}
	
	@Override
	public void unpackBody(IoBuffer paramIoBuffer) {
		
		gridId = paramIoBuffer.getShort();
		int binded = paramIoBuffer.get();
		if(binded == Item.Binded){
			isUseBinded = true;
		}
		count = paramIoBuffer.getShort();
		for(int i=0;i<count;i++){
			symbols.add(FightEffectProperty.fightEffectSymbolMaps.get(getString(paramIoBuffer)));
		}
	}
	@Override
	public String getName() {
		return "背包装备洗练";
	}
	public short getCount() {
		return count;
	}

	public void setCount(short count) {
		this.count = count;
	}

	public short getGridId() {
		return gridId;
	}

	public void setGridId(short gridId) {
		this.gridId = gridId;
	}

	public List<Short> getSymbols() {
		return symbols;
	}

	public void setSymbols(List<Short> symbols) {
		this.symbols = symbols;
	}
	public boolean isUseBinded() {
		return isUseBinded;
	}
	public void setUseBinded(boolean isUseBinded) {
		this.isUseBinded = isUseBinded;
	}
	
	
}
