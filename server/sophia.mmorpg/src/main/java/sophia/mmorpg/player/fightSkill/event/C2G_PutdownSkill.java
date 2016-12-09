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
package sophia.mmorpg.player.fightSkill.event;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class C2G_PutdownSkill extends ActionEventBase {
	private String skillRefId;
	private short slotIndex;

	@Override
	public void unpackBody(IoBuffer buffer) {
		skillRefId = getString(buffer);
		slotIndex = buffer.getShort();
	}

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		putString(buffer, skillRefId);
		buffer.putShort(slotIndex);
		return buffer;
	}

	public short getSlotIndex() {
		return slotIndex;
	}

	public void setSlotIndex(short slotIndex) {
		this.slotIndex = slotIndex;
	}

	public String getSkillRefId() {
		return skillRefId;
	}

	public void setSkillRefId(String skillRefId) {
		this.skillRefId = skillRefId;
	}

	@Override
	public String getName() {
		return "快捷技能";
	}
	
	

}
