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

import sophia.foundation.communication.core.MessageFactory;
import sophia.mmorpg.event.MMORPGEventDefines;

public final class FightSkillDefines {
	// get the list of all the learned skills 
	public static final short C2G_GetLearnedSkillList = MMORPGEventDefines.Skill_Message_Begin + 1;
	public static final short G2C_GetLearnedSkillList = MMORPGEventDefines.Skill_Message_Begin + 2;
	
	// make a skill a shortcut skill
	public static final short C2G_PutdownSkill = MMORPGEventDefines.Skill_Message_Begin + 3;
	// add skill exp
	public static final short C2G_AddSkillExp = MMORPGEventDefines.Skill_Message_Begin + 5;
	public static final short G2C_AddSkillExp = MMORPGEventDefines.Skill_Message_Begin + 6;
	
	// use skill
	public static final short C2G_UseSkill = MMORPGEventDefines.Skill_Message_Begin + 10;
	
	// skill effect
	public static final short G2C_TriggerSingleTargetSkill = MMORPGEventDefines.Skill_Message_Begin + 12;
	
	public static final short G2C_TriggerMultiTargetSkill = MMORPGEventDefines.Skill_Message_Begin + 14;
	
	public static void registerActionEvents() {
		MessageFactory.addMessage(C2G_GetLearnedSkillList, C2G_GetLearnedSkillList.class);
		MessageFactory.addMessage(G2C_GetLearnedSkillList, G2C_GetLearnedSkillList.class);
		MessageFactory.addMessage(C2G_PutdownSkill, C2G_PutdownSkill.class);
		MessageFactory.addMessage(C2G_UseSkill, C2G_UseSkill.class);
		MessageFactory.addMessage(G2C_TriggerSingleTargetSkill, G2C_TriggerSingleTargetSkill.class);
		MessageFactory.addMessage(G2C_TriggerMultiTargetSkill, G2C_TriggerMultiTargetSkill.class);
		MessageFactory.addMessage(C2G_AddSkillExp, C2G_AddSkillExp.class);
		MessageFactory.addMessage(G2C_AddSkillExp, G2C_AddSkillExp.class);
	}

}
