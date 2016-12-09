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
package sophia.mmorpg.player.fightSkill.ref;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;


public final class PlayerFightSkillRefTree {
	private static final Map<Integer, List<SkillRef>>  refTree = new HashMap<>();
	private static final Logger logger = Logger.getLogger(PlayerFightSkillRefTree.class);
	
	private PlayerFightSkillRefTree() {
		
	}
	
	public static void registerFightSkillRef(final int professionId, final SkillRef ref) {
		List<SkillRef> lst = refTree.get(professionId);
		if (lst == null) {
			lst = new ArrayList<SkillRef>();
			refTree.put(professionId, lst);
		}
		lst.add(ref);
	}
	
	public static List<SkillRef> getSkillRefTree(final int professionId) {
		return refTree.get(professionId);
	}
	
	public static List<SkillRef> getAvailableSkills(final int professionId, final int playerLevel) {
		List<SkillRef> skillRefs = getSkillRefTree(professionId);
		List<SkillRef> res = new ArrayList<SkillRef>();
		if(skillRefs != null) {
			for(SkillRef ref : skillRefs) {
				int levelRequired = MGPropertyAccesser.getSkillLearnLevel(ref.getProperty());
				int maxLevelThatAutoLearn = 35;
				if(levelRequired <= maxLevelThatAutoLearn && playerLevel >= levelRequired && !ref.isExtendedSkill())
					res.add(ref);
			}
			
		}
		return res;
	}
}
