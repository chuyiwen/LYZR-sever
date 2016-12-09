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

import sophia.mmorpg.core.PropertyDictionaryModifyPhase;

public final class FightPropertyEffectTransaction {
	private final FightPropertyMgr fightPropertyMgr;
	
	public FightPropertyEffectTransaction(FightPropertyMgr fightPropertyMgr) {
		this.fightPropertyMgr = fightPropertyMgr;
	}
	
	public PropertyDictionaryModifyPhase attach(PropertyDictionaryModifyPhase modify, PropertyDictionaryModifyPhase attachEffect) {
		try {
			return fightPropertyMgr.attachAndSnapshot(null, modify, attachEffect);
		} finally {
			FightPropertyMgr.recycleModifyPhaseToPool(modify);
			FightPropertyMgr.recycleModifyPhaseToPool(attachEffect);
		}
	}
	
	public PropertyDictionaryModifyPhase attach(PropertyDictionaryModifyPhase attachEffect) {
		try {
			return fightPropertyMgr.attachAndSnapshot(null, null, attachEffect);
		} finally {
			FightPropertyMgr.recycleModifyPhaseToPool(attachEffect);
		}
	}
	
	public PropertyDictionaryModifyPhase detach(PropertyDictionaryModifyPhase detachEffect) {
		try {
			return fightPropertyMgr.detachAndSnapshot(null, detachEffect);
		} finally {
			FightPropertyMgr.recycleModifyPhaseToPool(detachEffect);
		}
	}
	
	public void recycleTranscationSnapshot(PropertyDictionaryModifyPhase snapshot) {
		FightPropertyMgr.recycleSnapshotToPool(snapshot);
	}
}
