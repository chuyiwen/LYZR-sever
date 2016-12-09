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

import sophia.foundation.property.PropertyDictionary;
import sophia.mmorpg.core.PropertyDictionaryModifyPhase;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;
import sophia.mmorpg.ref.editorgen.MGPropertySymbolDefines;

public final class FightPropertyModifyTransaction {
	private final FightPropertyMgr fightPropertyMgr;
	
	public FightPropertyModifyTransaction(FightPropertyMgr fightPropertyMgr) {
		this.fightPropertyMgr = fightPropertyMgr;
	}
	
	public final int modifyHP(int hp) {
		PropertyDictionaryModifyPhase modifyPhase = FightPropertyMgr.getModifyPhaseFromPool();
		modifyPhase.modify(MGPropertySymbolDefines.HP_Id, hp);
		PropertyDictionaryModifyPhase snapshot = null;
		try {
			snapshot = fightPropertyMgr.modifyAndSnapshot(modifyPhase);
			PropertyDictionary snapshotPropertyDictionary = snapshot.getPropertyDictionary();
			return MGPropertyAccesser.getHP(snapshotPropertyDictionary);
		} finally {
			FightPropertyMgr.recycleModifyPhaseToPool(modifyPhase);
			FightPropertyMgr.recycleSnapshotToPool(snapshot);
		}
	}
	
	public final int modifyMP(int mp) {
		PropertyDictionaryModifyPhase modifyPhase = FightPropertyMgr.getModifyPhaseFromPool();
		modifyPhase.modify(MGPropertySymbolDefines.MP_Id, mp);
		PropertyDictionaryModifyPhase snapshot = null;
		try {
			snapshot = fightPropertyMgr.modifyAndSnapshot(modifyPhase);
			PropertyDictionary snapshotPropertyDictionary = snapshot.getPropertyDictionary();
			return MGPropertyAccesser.getMP(snapshotPropertyDictionary);
		} finally {
			FightPropertyMgr.recycleModifyPhaseToPool(modifyPhase);
			FightPropertyMgr.recycleSnapshotToPool(snapshot);
		}
	}
	
	public final PropertyDictionaryModifyPhase modifyHPAndMP(int hp, int mp) {
		PropertyDictionaryModifyPhase modifyPhase = FightPropertyMgr.getModifyPhaseFromPool();
		modifyPhase.modify(MGPropertySymbolDefines.HP_Id, hp);
		modifyPhase.modify(MGPropertySymbolDefines.MP_Id, mp);
		try {
			return fightPropertyMgr.modifyAndSnapshot(modifyPhase);
		} finally {
			FightPropertyMgr.recycleModifyPhaseToPool(modifyPhase);
		}
	}
	
	public final void modifySomeProperty(Short propertyId, int value) {
		PropertyDictionaryModifyPhase res = null;
		PropertyDictionaryModifyPhase modifyPhase = FightPropertyMgr.getModifyPhaseFromPool();
		modifyPhase.modify(propertyId, value);
		try {
			res = fightPropertyMgr.modifyAndSnapshot(modifyPhase);
		} finally {
			FightPropertyMgr.recycleModifyPhaseToPool(modifyPhase);
			FightPropertyMgr.recycleModifyPhaseToPool(res);
		}
	}
	
	public final PropertyDictionaryModifyPhase modify(PropertyDictionaryModifyPhase modifyPhase) {
		try {
			return fightPropertyMgr.modifyAndSnapshot(modifyPhase);
		} finally {
			FightPropertyMgr.recycleModifyPhaseToPool(modifyPhase);
		}
	}
	
	public void recycleTranscationSnapshot(PropertyDictionaryModifyPhase snapshot) {
		FightPropertyMgr.recycleSnapshotToPool(snapshot);
	}
}
