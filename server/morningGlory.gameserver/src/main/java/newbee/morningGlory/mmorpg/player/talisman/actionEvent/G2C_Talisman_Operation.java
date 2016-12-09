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
package newbee.morningGlory.mmorpg.player.talisman.actionEvent;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public final class G2C_Talisman_Operation extends ActionEventBase {
	
	private byte type;
	private short index;
	private byte level;
	private byte ret;
	
	@Override
	protected IoBuffer packBody(IoBuffer paramIoBuffer) {
		
		paramIoBuffer.put(type);
		paramIoBuffer.putShort(index);
		paramIoBuffer.put(ret);
		paramIoBuffer.put(level);
		return paramIoBuffer;
	}
	@Override
	public void unpackBody(IoBuffer paramIoBuffer) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "法宝操作";
	}
	public byte getType() {
		return type;
	}
	public void setType(byte type) {
		this.type = type;
	}
	public byte getRet() {
		return ret;
	}
	public void setRet(byte ret) {
		this.ret = ret;
	}
	public short getIndex() {
		return index;
	}
	public void setIndex(short index) {
		this.index = index;
	}
	public byte getLevel() {
		return level;
	}
	public void setLevel(byte level) {
		this.level = level;
	}
	
}	
