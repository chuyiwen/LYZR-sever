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
package newbee.morningGlory.character.event;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class C2G_CharacterLogin extends ActionEventBase {
	private String charId;
	private short width;
	private short height;
	
	@Override
	public void unpackBody(IoBuffer buffer) {
		charId = getString(buffer);
		width = buffer.getShort();
		height = buffer.getShort();
		width = 960 + 150;
		height = 640 + 150;
	}

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		putString(buffer, charId);
		buffer.putShort(width);
		buffer.putShort(height);
		return buffer;
	}
	
	@Override
	public String toString() {
		return "C2G_LoginEvent@id:" + charId + ",width:" + width + ",height:" + height;
	}

	public String getCharId() {
		return charId;
	}

	public void setCharId(String charId) {
		this.charId = charId;
	}

	public short getWidth() {
		return width;
	}

	public void setWidth(short width) {
		this.width = width;
	}

	public short getHeight() {
		return height;
	}

	public void setHeight(short height) {
		this.height = height;
	}
	
	@Override
	public String getName() {
		return "角色登录";
	}
}
