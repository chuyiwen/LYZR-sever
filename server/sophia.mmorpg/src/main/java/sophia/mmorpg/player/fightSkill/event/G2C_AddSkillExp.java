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

public class G2C_AddSkillExp extends ActionEventBase {
	private String skillRefId;
	private int currentSkillExp;
	private int currentSkillLevel;
	
	public G2C_AddSkillExp() {
		super();
	}

	public G2C_AddSkillExp(String skillRefId, int currentSkillExp, int currentSkillLevel) {
		super();
		this.setSkillRefId(skillRefId);
		this.setCurrentSkillExp(currentSkillExp);
		this.setCurrentSkillLevel(currentSkillLevel);
		this.actionEventId = FightSkillDefines.G2C_AddSkillExp; 
	}

	@Override
	public void unpackBody(IoBuffer buffer) {
	}

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		putString(buffer, skillRefId);
		buffer.putInt(currentSkillExp);
		buffer.putInt(currentSkillLevel);
		return buffer;
	}

	public String getSkillRefId() {
		return skillRefId;
	}

	public void setSkillRefId(String skillRefId) {
		this.skillRefId = skillRefId;
	}

	public int getCurrentSkillExp() {
		return currentSkillExp;
	}

	public void setCurrentSkillExp(int currentSkillExp) {
		this.currentSkillExp = currentSkillExp;
	}

	public int getCurrentSkillLevel() {
		return currentSkillLevel;
	}

	public void setCurrentSkillLevel(int currentSkillLevel) {
		this.currentSkillLevel = currentSkillLevel;
	}

}
