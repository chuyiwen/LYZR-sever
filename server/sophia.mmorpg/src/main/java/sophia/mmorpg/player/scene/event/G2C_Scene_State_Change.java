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

import java.util.List;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;
import sophia.mmorpg.base.sprite.FightSprite;
import sophia.mmorpg.core.state.FSMStateBase;

public class G2C_Scene_State_Change extends ActionEventBase {

//	private String aimType;
	private byte aimType;
	
	private String charId;
	private List<FSMStateBase<FightSprite>> stateList;
	
	@Override
	public void unpackBody(IoBuffer buffer) {
	}

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		buffer.put(aimType);
		putString(buffer, charId);
		buffer.putShort((short)stateList.size());
		for (FSMStateBase<FightSprite> state : stateList) {
			buffer.putShort(state.getId());
		}
		
		return buffer;
	}

	public String getCharId() {
		return charId;
	}

	public void setCharId(String charId) {
		this.charId = charId;
	}

	public List<FSMStateBase<FightSprite>> getStateList() {
		return stateList;
	}

	public void setStateList(List<FSMStateBase<FightSprite>> stateList) {
		this.stateList = stateList;
	}

	public byte getAimType() {
		return aimType;
	}

	public void setAimType(byte aimType) {
		this.aimType = aimType;
	}

	
}
