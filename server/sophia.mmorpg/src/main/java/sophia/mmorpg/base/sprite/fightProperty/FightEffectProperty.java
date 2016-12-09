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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import sophia.mmorpg.ref.editorgen.MGPropertySymbolDefines;

public final class FightEffectProperty {
	// effect
	public final static Collection<Short> fightEffectSymbols = new ArrayList<Short>();
	// property
	public final static Collection<Short> fightPropertyValueIds = new ArrayList<Short>();
	// rate
	public final static Collection<Short> fightPropertyRateIds = new ArrayList<Short>();

	public final static Map<String, Short> fightEffectSymbolMaps = new HashMap<String, Short>();

	static {

		fightEffectSymbols.add(MGPropertySymbolDefines.MinPAtk_Id);
		fightEffectSymbols.add(MGPropertySymbolDefines.MaxPAtk_Id);
		fightEffectSymbols.add(MGPropertySymbolDefines.MinPDef_Id);
		fightEffectSymbols.add(MGPropertySymbolDefines.MaxPDef_Id);
		fightEffectSymbols.add(MGPropertySymbolDefines.MinMAtk_Id);
		fightEffectSymbols.add(MGPropertySymbolDefines.MaxMAtk_Id);
		fightEffectSymbols.add(MGPropertySymbolDefines.MinMDef_Id);
		fightEffectSymbols.add(MGPropertySymbolDefines.MaxMDef_Id);
		fightEffectSymbols.add(MGPropertySymbolDefines.MinTao_Id);
		fightEffectSymbols.add(MGPropertySymbolDefines.MaxTao_Id);
		fightEffectSymbols.add(MGPropertySymbolDefines.MaxHP_Id);
		fightEffectSymbols.add(MGPropertySymbolDefines.MaxMP_Id);
		fightEffectSymbols.add(MGPropertySymbolDefines.IgnorePDef_Id);
		fightEffectSymbols.add(MGPropertySymbolDefines.IgnoreMDef_Id);
		fightEffectSymbols.add(MGPropertySymbolDefines.MoveSpeed_Id);
		fightEffectSymbols.add(MGPropertySymbolDefines.AtkSpeed_Id);
		fightEffectSymbols.add(MGPropertySymbolDefines.Crit_Id);
		fightEffectSymbols.add(MGPropertySymbolDefines.CritInjure_Id);
		fightEffectSymbols.add(MGPropertySymbolDefines.Fortune_Id);
		fightEffectSymbols.add(MGPropertySymbolDefines.Hit_Id);
		fightEffectSymbols.add(MGPropertySymbolDefines.Dodge_Id);
		fightEffectSymbols.add(MGPropertySymbolDefines.MDodge_Id);
		fightEffectSymbols.add(MGPropertySymbolDefines.PDodge_Id);

		fightEffectSymbols.add(MGPropertySymbolDefines.PerHP_Id);
		fightEffectSymbols.add(MGPropertySymbolDefines.PerMP_Id);
		fightEffectSymbols.add(MGPropertySymbolDefines.PImmunityPer_Id);
		fightEffectSymbols.add(MGPropertySymbolDefines.MImmunityPer_Id);
		fightEffectSymbols.add(MGPropertySymbolDefines.PDodgePer_Id);
		fightEffectSymbols.add(MGPropertySymbolDefines.MDodgePer_Id);
		fightEffectSymbols.add(MGPropertySymbolDefines.AtkSpeedPer_Id);
		fightEffectSymbols.add(MGPropertySymbolDefines.MoveSpeedPer_Id);

		fightPropertyValueIds.add(MGPropertySymbolDefines.MinPAtk_Id);
		fightPropertyValueIds.add(MGPropertySymbolDefines.MaxPAtk_Id);
		fightPropertyValueIds.add(MGPropertySymbolDefines.MinPDef_Id);
		fightPropertyValueIds.add(MGPropertySymbolDefines.MaxPDef_Id);
		fightPropertyValueIds.add(MGPropertySymbolDefines.MinMAtk_Id);
		fightPropertyValueIds.add(MGPropertySymbolDefines.MaxMAtk_Id);
		fightPropertyValueIds.add(MGPropertySymbolDefines.MinMDef_Id);
		fightPropertyValueIds.add(MGPropertySymbolDefines.MaxMDef_Id);
		fightPropertyValueIds.add(MGPropertySymbolDefines.MinTao_Id);
		fightPropertyValueIds.add(MGPropertySymbolDefines.MaxTao_Id);
		fightPropertyValueIds.add(MGPropertySymbolDefines.MaxHP_Id);
		fightPropertyValueIds.add(MGPropertySymbolDefines.MaxMP_Id);
		fightPropertyValueIds.add(MGPropertySymbolDefines.IgnorePDef_Id);
		fightPropertyValueIds.add(MGPropertySymbolDefines.IgnoreMDef_Id);
		fightPropertyValueIds.add(MGPropertySymbolDefines.MoveSpeed_Id);
		fightPropertyValueIds.add(MGPropertySymbolDefines.AtkSpeed_Id);
		fightPropertyValueIds.add(MGPropertySymbolDefines.Crit_Id);
		fightPropertyValueIds.add(MGPropertySymbolDefines.CritInjure_Id);
		fightPropertyValueIds.add(MGPropertySymbolDefines.Fortune_Id);
		fightPropertyValueIds.add(MGPropertySymbolDefines.Hit_Id);
		fightPropertyValueIds.add(MGPropertySymbolDefines.Dodge_Id);
		fightPropertyValueIds.add(MGPropertySymbolDefines.MDodge_Id);
		fightPropertyValueIds.add(MGPropertySymbolDefines.PDodge_Id);
		// AtkSpeedPer/MoveSpeedPer has value semantics and also rate semantics,
		// so we need to add into both the rateIds and valueIds
		fightPropertyValueIds.add(MGPropertySymbolDefines.AtkSpeedPer_Id);
		fightPropertyValueIds.add(MGPropertySymbolDefines.MoveSpeedPer_Id);
		fightPropertyValueIds.add(MGPropertySymbolDefines.PDodgePer_Id);
		fightPropertyValueIds.add(MGPropertySymbolDefines.MDodgePer_Id);
		fightPropertyValueIds.add(MGPropertySymbolDefines.PImmunityPer_Id);
		fightPropertyValueIds.add(MGPropertySymbolDefines.MImmunityPer_Id);

		fightPropertyRateIds.add(MGPropertySymbolDefines.PerHP_Id);
		fightPropertyRateIds.add(MGPropertySymbolDefines.PerMP_Id);
		fightPropertyRateIds.add(MGPropertySymbolDefines.AtkSpeedPer_Id);
		fightPropertyRateIds.add(MGPropertySymbolDefines.MoveSpeedPer_Id);
		fightPropertyRateIds.add(MGPropertySymbolDefines.PDodgePer_Id);
		fightPropertyRateIds.add(MGPropertySymbolDefines.MDodgePer_Id);
		fightPropertyRateIds.add(MGPropertySymbolDefines.PImmunityPer_Id);
		fightPropertyRateIds.add(MGPropertySymbolDefines.MImmunityPer_Id);

		// ---------------------------------------
		fightEffectSymbolMaps.put("minPAtk", MGPropertySymbolDefines.MinPAtk_Id);
		fightEffectSymbolMaps.put("maxPAtk", MGPropertySymbolDefines.MaxPAtk_Id);
		fightEffectSymbolMaps.put("minPDef", MGPropertySymbolDefines.MinPDef_Id);
		fightEffectSymbolMaps.put("maxPDef", MGPropertySymbolDefines.MaxPDef_Id);
		fightEffectSymbolMaps.put("minMAtk", MGPropertySymbolDefines.MinMAtk_Id);
		fightEffectSymbolMaps.put("maxMAtk", MGPropertySymbolDefines.MaxMAtk_Id);
		fightEffectSymbolMaps.put("minMDef", MGPropertySymbolDefines.MinMDef_Id);
		fightEffectSymbolMaps.put("maxMDef", MGPropertySymbolDefines.MaxMDef_Id);
		fightEffectSymbolMaps.put("minTao", MGPropertySymbolDefines.MinTao_Id);
		fightEffectSymbolMaps.put("maxTao", MGPropertySymbolDefines.MaxTao_Id);
		fightEffectSymbolMaps.put("mImmunityPer", MGPropertySymbolDefines.MImmunityPer_Id);
		fightEffectSymbolMaps.put("maxHP", MGPropertySymbolDefines.MaxHP_Id);
		fightEffectSymbolMaps.put("maxMP", MGPropertySymbolDefines.MaxMP_Id);
		fightEffectSymbolMaps.put("perHP", MGPropertySymbolDefines.PerHP_Id);
		fightEffectSymbolMaps.put("perMP", MGPropertySymbolDefines.PerMP_Id);
		fightEffectSymbolMaps.put("ignorePDef", MGPropertySymbolDefines.IgnorePDef_Id);
		fightEffectSymbolMaps.put("ignoreMDef", MGPropertySymbolDefines.IgnoreMDef_Id);
		fightEffectSymbolMaps.put("moveSpeed", MGPropertySymbolDefines.MoveSpeed_Id);
		fightEffectSymbolMaps.put("atkSpeed", MGPropertySymbolDefines.AtkSpeed_Id);
		fightEffectSymbolMaps.put("crit", MGPropertySymbolDefines.Crit_Id);
		fightEffectSymbolMaps.put("critInjure", MGPropertySymbolDefines.CritInjure_Id);
		fightEffectSymbolMaps.put("fortune", MGPropertySymbolDefines.Fortune_Id);
		fightEffectSymbolMaps.put("hit", MGPropertySymbolDefines.Hit_Id);
		fightEffectSymbolMaps.put("dodge", MGPropertySymbolDefines.Dodge_Id);
		fightEffectSymbolMaps.put("pDodgePer", MGPropertySymbolDefines.PDodgePer_Id);
		fightEffectSymbolMaps.put("mDodgePer", MGPropertySymbolDefines.MDodgePer_Id);

	}

}
