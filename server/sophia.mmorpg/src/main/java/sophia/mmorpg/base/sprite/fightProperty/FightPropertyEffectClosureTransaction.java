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
package sophia.mmorpg.base.sprite.fightProperty;

import groovy.lang.Closure;
import sophia.mmorpg.core.PropertyDictionaryModifyPhase;

public final class FightPropertyEffectClosureTransaction<T> {
	public FightPropertyEffectClosureTransaction() {
		
	}
	
	public final PropertyDictionaryModifyPhase attachAndModify(T object, FightPropertyMgr fightPropertyMgr, Closure<PropertyDictionaryModifyPhase> attachClosure) {
		PropertyDictionaryModifyPhase ret = null;

		PropertyDictionaryModifyPhase modify = FightPropertyMgr.getModifyPhaseFromPool();
		PropertyDictionaryModifyPhase effect = FightPropertyMgr.getModifyPhaseFromPool();
		try {
			attachClosure.call(object, modify, effect);
			ret = fightPropertyMgr.attachAndSnapshot(object, modify, effect);
		} finally {
			FightPropertyMgr.recycleModifyPhaseToPool(modify);
			FightPropertyMgr.recycleModifyPhaseToPool(effect);
		}

		return ret;
	}

	public final PropertyDictionaryModifyPhase detachAndModify(T object, FightPropertyMgr fightPropertyMgr, Closure<PropertyDictionaryModifyPhase> detachClosure) {
		PropertyDictionaryModifyPhase ret = null;
		
		PropertyDictionaryModifyPhase effect = FightPropertyMgr.getModifyPhaseFromPool();

		try {
			detachClosure.call(object, effect);
			ret = fightPropertyMgr.detachAndSnapshot(object, effect);
		} finally {
			FightPropertyMgr.recycleModifyPhaseToPool(effect);
		}
		
		return ret;
	}
}
