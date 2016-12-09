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

public final class C2G_Body_Wash extends ActionEventBase {
	
	private byte bodyAreaId;
	private byte posId;
	private short count;
	private boolean isUseBined = false;
	private List<Short> symbols = new ArrayList<>();
	
	@Override
	protected IoBuffer packBody(IoBuffer paramIoBuffer) {
		return null;
	}
	
	@Override
	public void unpackBody(IoBuffer paramIoBuffer) {
		
		bodyAreaId = paramIoBuffer.get();
		posId = paramIoBuffer.get();		
		int binded = paramIoBuffer.get();
		if(binded == Item.Binded){
			isUseBined = true;
		}
		count = paramIoBuffer.getShort();
		for(int i=0;i<count;i++){
			symbols.add(FightEffectProperty.fightEffectSymbolMaps.get(getString(paramIoBuffer)));
		}
	}

	public byte getBodyAreaId() {
		return bodyAreaId;
	}

	public void setBodyAreaId(byte bodyAreaId) {
		this.bodyAreaId = bodyAreaId;
	}

	public byte getPosId() {
		return posId;
	}

	public void setPosId(byte posId) {
		this.posId = posId;
	}

	public short getCount() {
		return count;
	}

	public void setCount(short count) {
		this.count = count;
	}

	public List<Short> getSymbols() {
		return symbols;
	}

	public void setSymbols(List<Short> symbols) {
		this.symbols = symbols;
	}

	public boolean isUseBined() {
		return isUseBined;
	}

	public void setUseBined(boolean isUseBined) {
		this.isUseBined = isUseBined;
	}
	
}
