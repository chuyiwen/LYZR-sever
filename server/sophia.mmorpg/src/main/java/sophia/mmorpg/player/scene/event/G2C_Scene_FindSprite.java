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
package sophia.mmorpg.player.scene.event;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class G2C_Scene_FindSprite extends ActionEventBase {
	
	private byte spriteType;
	private String charId;
	private int code;

	@Override
	public void unpackBody(IoBuffer buffer) {
		spriteType = buffer.get();
		charId = getString(buffer);
		code = buffer.getInt();
	}

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		buffer.put(spriteType);
		putString(buffer, charId);
		buffer.putInt(code);
		return buffer;
	}

	public byte getSpriteType() {
		return spriteType;
	}

	public void setSpriteType(byte spriteType) {
		this.spriteType = spriteType;
	}

	public String getCharId() {
		return charId;
	}

	public void setCharId(String charId) {
		this.charId = charId;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}
}
