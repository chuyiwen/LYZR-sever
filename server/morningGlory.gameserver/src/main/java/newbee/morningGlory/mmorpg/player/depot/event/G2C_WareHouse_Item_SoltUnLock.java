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
package newbee.morningGlory.mmorpg.player.depot.event;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class G2C_WareHouse_Item_SoltUnLock extends ActionEventBase {
	private int remainsMin;

	public G2C_WareHouse_Item_SoltUnLock() {
		this.actionEventId = PlayerDepotEventDefines.G2C_WareHouse_Item_SoltUnLock;
	}

	public G2C_WareHouse_Item_SoltUnLock(int remainsMin) {
		super();
		this.remainsMin = remainsMin;
		this.actionEventId = PlayerDepotEventDefines.G2C_WareHouse_Item_SoltUnLock;
	}

	@Override
	public void unpackBody(IoBuffer buffer) {
		this.setRemainsMin(buffer.getInt());

	}

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		buffer.putInt(remainsMin);
		return buffer;
	}

	public int getRemainsMin() {
		return remainsMin;
	}

	public void setRemainsMin(int remainsMin) {
		this.remainsMin = remainsMin;
	}

}
