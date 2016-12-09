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
package newbee.morningGlory.mmorpg.sprite;

import java.util.Map.Entry;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import sophia.foundation.property.PropertyDictionary;
import sophia.foundation.property.SimulatorProperty;
import sophia.foundation.property.ValueProperty;
import sophia.foundation.property.symbol.SimulatorPropertySymbolContext;
import sophia.foundation.util.DebugUtil;
import sophia.mmorpg.base.sprite.fightProperty.FightEffectProperty;
import sophia.mmorpg.base.sprite.fightProperty.FightPropertyMgr;
import sophia.mmorpg.core.PropertyDictionaryModifyPhase;
import sophia.mmorpg.core.PropertyModifyState;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;
import sophia.mmorpg.ref.editorgen.MGPropertySymbolDefines;

public final class MGFightPropertyMgr extends FightPropertyMgr {
	private static final Logger logger = Logger.getLogger(MGFightPropertyMgr.class);
	static {
		// 效果
		PropertyDictionary modifPhasePropertyDictionary = new PropertyDictionary();
		MGPropertyAccesser.setOrPutHealHP(modifPhasePropertyDictionary, 0);
		MGPropertyAccesser.setOrPutHealMP(modifPhasePropertyDictionary, 0);
		MGPropertyAccesser.setOrPutMP(modifPhasePropertyDictionary, 0);
		MGPropertyAccesser.setOrPutHP(modifPhasePropertyDictionary, 0);

		for (Short symbol : FightEffectProperty.fightEffectSymbols) {
			modifPhasePropertyDictionary.setOrPutValue(symbol, 0);
		}

		ModifyPhase_Pool.setPropertyDictionary(modifPhasePropertyDictionary);

		// 属性
		PropertyDictionary snapshotPropertyDictionary = new PropertyDictionary();
		for (Short symbol : FightEffectProperty.fightPropertyValueIds) {
			snapshotPropertyDictionary.setOrPutValue(symbol, 0);
		}

		MGPropertyAccesser.setOrPutMP(snapshotPropertyDictionary, 0);
		MGPropertyAccesser.setOrPutHP(snapshotPropertyDictionary, 0);
		// FIXME: 黄晓源，还需要加状态属性.比如：移动

		Snapshot_Pool.setPropertyDictionary(snapshotPropertyDictionary);

		// initialize valueIds and rateIds
		for (Short id : FightEffectProperty.fightPropertyRateIds) {
			FightPropertyMgr.addRateId(id);
		}

		for (Short id : FightEffectProperty.fightPropertyValueIds) {
			FightPropertyMgr.addValueId(id);
		}

	}

	public MGFightPropertyMgr() {

	}

	@SuppressWarnings("unchecked")
	@Override
	protected PropertyDictionaryModifyPhase crtModifyImpl(PropertyDictionary propertyDictionary) {
		PropertyDictionaryModifyPhase modifyPhase = ModifyPhase_Pool.obtain();

		try {
			Set<Entry<Short, SimulatorProperty<?>>> refEntrySet = propertyDictionary.getDictionary().entrySet();
			for (Entry<Short, SimulatorProperty<?>> entry : refEntrySet) {
				modifyPhase.modify((ValueProperty<Integer>) entry.getValue());
			}

			modifyPhase.modify(this.effectModifyPhase);

			// FIXME: 会有遗漏吗？
			modifyPhase.phaseTrueAll();
			PropertyDictionaryModifyPhase result = Snapshot_Pool.obtain();
			return doSnapshot(modifyPhase, result);
		} finally {
			ModifyPhase_Pool.recycle(modifyPhase);
		}
	}

	@Override
	protected PropertyDictionaryModifyPhase modifyImpl(PropertyDictionaryModifyPhase modifyPhase) {
		PropertyDictionaryModifyPhase result = Snapshot_Pool.obtain();

		copyPropertyDictionaryData(this.snapshot.getPropertyDictionary(), result.getPropertyDictionary());
		return doSnapshot(modifyPhase, result);
	}

	@Override
	protected PropertyDictionaryModifyPhase attachImpl(Object attachObject, PropertyDictionaryModifyPhase modify, PropertyDictionaryModifyPhase effect) {
		this.effectModifyPhase.modify(effect);

		if (modify != null) {
			modify.modify(effect);
		}

		PropertyDictionaryModifyPhase result = Snapshot_Pool.obtain();
		copyPropertyDictionaryData(this.snapshot.getPropertyDictionary(), result.getPropertyDictionary());
		if (modify != null) {
			return doSnapshot(modify, result);
		} else {
			return doSnapshot(effect, result);
		}
	}

	@Override
	protected PropertyDictionaryModifyPhase detachImpl(Object detachObject, PropertyDictionaryModifyPhase effect) {
		// 设置为0-value
		PropertyDictionaryModifyPhase result = Snapshot_Pool.obtain();

		effect.subAllValue();
		this.effectModifyPhase.modify(effect);

		copyPropertyDictionaryData(this.snapshot.getPropertyDictionary(), result.getPropertyDictionary());

		return doSnapshot(effect, result);
	}

	@SuppressWarnings("unchecked")
	private PropertyDictionaryModifyPhase doSnapshot(PropertyDictionaryModifyPhase modifyPhase, PropertyDictionaryModifyPhase result) {
		PropertyDictionary snapshotPropertyDictionary = result.getPropertyDictionary();

		// 先把属性值，加到snapshot
		PropertyDictionary modifyPhasePropertyDictionary = modifyPhase.getModifiedAttributes();
		Set<Entry<Short, SimulatorProperty<?>>> entrySet = modifyPhasePropertyDictionary.getDictionary().entrySet();
		for (Entry<Short, SimulatorProperty<?>> entry : entrySet) {
			ValueProperty<Integer> valueProperty = (ValueProperty<Integer>) entry.getValue();
			if (modifyPhase.isModify(valueProperty)) {
				snapshotPropertyDictionary.addJustOnly(valueProperty);
				for (PropertyModifyState phase : result.getPhaseSet()) {
					if (phase.getId().equals(valueProperty.getId())) {
						phase.setDirty(true);
					}
				}
			}
		}

		// 再计算加成值
		for (Entry<Short, SimulatorProperty<?>> entry : entrySet) {
			ValueProperty<Integer> valueProperty = (ValueProperty<Integer>) entry.getValue();
			short id = valueProperty.getId();
			if (rateIds.contains(id)) {
				if (id == MGPropertySymbolDefines.PerHP_Id) {
					PropertyDictionary basePropertyDictionary = getRefPropertyDictionary();
					int maxHP = MGPropertyAccesser.getMaxHP(basePropertyDictionary);
					int addMaxHP = maxHP * valueProperty.getValue() / 100;
					maxHP = MGPropertyAccesser.getMaxHP(snapshotPropertyDictionary);
					MGPropertyAccesser.setOrPutMaxHP(snapshotPropertyDictionary, maxHP + addMaxHP);
				} else if (id == MGPropertySymbolDefines.PerMP_Id) {
					PropertyDictionary basePropertyDictionary = getRefPropertyDictionary();
					int maxMP = MGPropertyAccesser.getMaxMP(basePropertyDictionary);
					int addMaxMP = maxMP * valueProperty.getValue() / 100;
					maxMP = MGPropertyAccesser.getMaxMP(snapshotPropertyDictionary);
					MGPropertyAccesser.setOrPutMaxMP(snapshotPropertyDictionary, maxMP + addMaxMP);
				} else if (id == MGPropertySymbolDefines.AtkSpeedPer_Id) {
					PropertyDictionary basePropertyDictionary = getRefPropertyDictionary();
					int attackSpeed = MGPropertyAccesser.getAtkSpeed(basePropertyDictionary);
					int addedAttackSpeed = attackSpeed * valueProperty.getValue() / 100;
					int currentAttakSpeed = MGPropertyAccesser.getAtkSpeed(snapshotPropertyDictionary);
					MGPropertyAccesser.setOrPutAtkSpeed(snapshotPropertyDictionary, currentAttakSpeed - addedAttackSpeed);
				} else if (id == MGPropertySymbolDefines.MoveSpeedPer_Id) {
					PropertyDictionary basePropertyDictionary = getRefPropertyDictionary();
					int moveSpeed = MGPropertyAccesser.getMoveSpeed(basePropertyDictionary);
					int addedMoveSpeed = moveSpeed * valueProperty.getValue() / 100;
					int currentMoveSpeed = MGPropertyAccesser.getMoveSpeed(snapshotPropertyDictionary);
					MGPropertyAccesser.setOrPutMoveSpeed(snapshotPropertyDictionary, currentMoveSpeed + addedMoveSpeed);
				}
			}
		}

		int crtHP = MGPropertyAccesser.getHP(snapshotPropertyDictionary);
		int crtMP = MGPropertyAccesser.getMP(snapshotPropertyDictionary);
		int maxHP = MGPropertyAccesser.getMaxHP(snapshotPropertyDictionary);
		int maxMP = MGPropertyAccesser.getMaxMP(snapshotPropertyDictionary);
		if (crtHP > maxHP) {
			// FIXME, 是否按比例调整当前血量, 如果是, 是否意味着maxHP增加, 当前血量也按比例调整
			MGPropertyAccesser.setOrPutHP(snapshotPropertyDictionary, maxHP);
		} else if (crtHP < 0) {
			MGPropertyAccesser.setOrPutHP(snapshotPropertyDictionary, 0);
		}

		if (crtMP > maxMP) {
			MGPropertyAccesser.setOrPutMP(snapshotPropertyDictionary, maxMP);
		} else if (crtMP < 0) {
			MGPropertyAccesser.setOrPutMP(snapshotPropertyDictionary, 0);
		}
		int foutune = MGPropertyAccesser.getFortune(snapshotPropertyDictionary);
		if (foutune > 9) {
			MGPropertyAccesser.setOrPutFortune(snapshotPropertyDictionary, 9);
		}
		PropertyDictionaryModifyPhase newSnapshot = Snapshot_Pool.obtain();
		PropertyDictionaryModifyPhase newCurrent = Snapshot_Pool.obtain();

		copyPropertyDictionaryData(snapshotPropertyDictionary, newSnapshot.getPropertyDictionary());
		newSnapshot.phaseTrueAll();
		copyPropertyDictionaryData(snapshotPropertyDictionary, newCurrent.getPropertyDictionary());
		newCurrent.phaseTrueAll();

		PropertyDictionaryModifyPhase preSnapshot = this.snapshot;
		this.snapshot = newSnapshot;
		Snapshot_Pool.recycle(preSnapshot);

		PropertyDictionaryModifyPhase preCurrent = this.current;
		this.current = newCurrent;
		Snapshot_Pool.recycle(preCurrent);

		checkDataValidity();

		return result;
	}

	@SuppressWarnings("unchecked")
	private void checkDataValidity() {
		PropertyDictionary currentPd = this.current.getPropertyDictionary();
		for (Map.Entry<Short, SimulatorProperty<?>> entry : currentPd.getDictionary().entrySet()) {
			Short id = entry.getKey();
			if (valueIds.contains(id) && !rateIds.contains(id)) {
				ValueProperty<Integer> value = (ValueProperty<Integer>) entry.getValue();
				String propertySymbolName = SimulatorPropertySymbolContext.getPropertySymbolName(id);
				if (value.getValue() < 0) {
					logger.error("In current: the value of symbol " + id + " " + propertySymbolName + " is negative. value " + value.getValue() + "\n" + DebugUtil.printStack());
				}
			}

		}

		PropertyDictionary snapshotPd = this.snapshot.getPropertyDictionary();
		for (Map.Entry<Short, SimulatorProperty<?>> entry : snapshotPd.getDictionary().entrySet()) {
			Short id = entry.getKey();
			if (valueIds.contains(id) && !rateIds.contains(id)) {
				ValueProperty<Integer> value = (ValueProperty<Integer>) entry.getValue();
				String propertySymbolName = SimulatorPropertySymbolContext.getPropertySymbolName(id);
				if (value.getValue() < 0) {
					logger.error("In snapshot: the value of symbol " + id + " " + propertySymbolName + " is negative. value " + value.getValue() + "\n" + DebugUtil.printStack());
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void copyPropertyDictionaryData(PropertyDictionary from, PropertyDictionary to) {
		Set<Entry<Short, SimulatorProperty<?>>> fromEntrySet = from.getDictionary().entrySet();
		for (Entry<Short, SimulatorProperty<?>> entry : fromEntrySet) {
			ValueProperty<Integer> valueProperty = (ValueProperty<Integer>) entry.getValue();
			to.setValue(valueProperty.getId(), valueProperty.getValue());
		}
	}
}
