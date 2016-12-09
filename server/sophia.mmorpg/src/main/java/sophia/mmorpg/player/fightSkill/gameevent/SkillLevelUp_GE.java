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
package sophia.mmorpg.player.fightSkill.gameevent;


public class SkillLevelUp_GE {
	String refId; // refId of the skill to level up

	public SkillLevelUp_GE(String refId) {
		super();
		this.refId = refId;
	}

	public String getRefId() {
		return refId;
	}

	@Override
	public String toString() {
		return "SkillLevelUp_GE [refId=" + refId + "]";
	}
	
}
