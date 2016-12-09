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
package newbee.morningGlory.mmorpg.sprite.buff.runtime;

import groovy.lang.Closure;

import java.util.HashMap;
import java.util.Map;

import sophia.mmorpg.utils.RuntimeResult;

public final class FightSpriteBuffClosureMgr {
	private static Map<String, Closure<RuntimeResult>> skillClosures = new HashMap<>();
	
	public static final void addBuffClosures(Map<String, Closure<RuntimeResult>> closures) {
		skillClosures.putAll(closures);
	}
	
	public static final Closure<RuntimeResult> getBuffClosures(String closureName) {
		return skillClosures.get(closureName);
	}
	
	public static final void loadBuffClosures() {
		Map<String, Closure<RuntimeResult>> closures0 = (new MGFightSpriteItemBuffClosure()).getClosures();
		addBuffClosures(closures0);
		Map<String, Closure<RuntimeResult>> closures1 = (new MGFightSpriteSkillBuffClosure()).getClosures();
		addBuffClosures(closures1);		
		Map<String, Closure<RuntimeResult>> closures2 = (new MGFightSpritePeriodAffectClosure_1()).getClosures();
		addBuffClosures(closures2);	
		Map<String, Closure<RuntimeResult>> closures3 = (new MGFightSpriteStateBuffClosure()).getClosures();
		addBuffClosures(closures3);	
	}
	
}
