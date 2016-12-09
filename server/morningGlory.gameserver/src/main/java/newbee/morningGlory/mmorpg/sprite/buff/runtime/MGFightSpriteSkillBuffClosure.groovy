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
package newbee.morningGlory.mmorpg.sprite.buff.runtime


import newbee.morningGlory.mmorpg.sprite.buff.MGFightSpriteBuff

import org.apache.log4j.Logger

import sophia.foundation.property.PropertyDictionary
import sophia.mmorpg.base.sprite.FightSprite
import sophia.mmorpg.base.sprite.fightProperty.FightPropertyEffectFacade
import sophia.mmorpg.base.sprite.state.adjunction.MagicShieldState
import sophia.mmorpg.core.PropertyDictionaryModifyPhase
import sophia.mmorpg.player.Player
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser
import sophia.mmorpg.ref.editorgen.MGPropertySymbolDefines
import sophia.mmorpg.utils.RuntimeResult

class MGFightSpriteSkillBuffClosure {
	private static final Logger logger = Logger.getLogger(MGFightSpriteSkillBuffClosure.class);
	Map<String, Closure<RuntimeResult>> map = new HashMap<>();
	MGFightSpriteSkillBuffClosure(){
		map.put("attach_skill_EndureMagic", attach_skill_EndureMagic);
		map.put("detach_skill_EndureMagic", detach_skill_EndureMagic);
		map.put("attach_skill_EndurePhysical", attach_skill_EndurePhysical);
		map.put("detach_skill_EndurePhysical", detach_skill_EndurePhysical);		
		map.put("attach_skill_magicShield", attach_skill_magicShield);
		map.put("detach_skill_magicShield", detach_skill_magicShield);
		map.put("attach_skill_magicShield_ext", attach_skill_magicShield_ext);
		map.put("detach_skill_magicShield_ext", detach_skill_magicShield_ext);
	}

	public Map<String, Closure<RuntimeResult>> getClosures() {
		return map;
	}



	
	
	/**
	 * 魔抗咒buff
	 */
	Closure<RuntimeResult> attach_skill_EndureMagic = { FightSprite from, FightSprite to, MGFightSpriteBuff buff ->
		RuntimeResult result = RuntimeResult.OK();
		if(to instanceof Player) {
			to = (Player) to;
			double percent = MGPropertyAccesser.getSkillDamageRate(buff.getSpecialProperty()) / 100.0
			percent = percent < 0 ? 0 : percent;
			PropertyDictionary playerPd = to.getFightPropertyMgrComponent().getFightPropertyMgr().getSnapshotByNew().getPropertyDictionary()
			int maxMDef = MGPropertyAccesser.getMaxMDef(playerPd) ;
			int addMaxMDef =  maxMDef * percent;			
			PropertyDictionary pd = new PropertyDictionary();
			MGPropertyAccesser.setOrPutMaxMDef(pd, addMaxMDef)
			MGPropertyAccesser.setOrPutMaxMDef(buff.getSpecialProperty(), addMaxMDef)
			FightPropertyEffectFacade.attachAndNotify(to, pd);
		}
		return result;
	}
	/**
	 * 取消魔抗咒buff
	 */
	Closure<RuntimeResult> detach_skill_EndureMagic = { FightSprite from, FightSprite to, MGFightSpriteBuff buff ->
		RuntimeResult result = RuntimeResult.OK();
		if(to instanceof Player) {
			to = (Player) to;
			double percent = MGPropertyAccesser.getSkillDamageRate(buff.getSpecialProperty()) / 100.0
			percent = percent < 0 ? 0 : percent;
			PropertyDictionary playerPd = to.getFightPropertyMgrComponent().getFightPropertyMgr().getSnapshotByNew().getPropertyDictionary()
			int subMaxMDef = MGPropertyAccesser.getMaxMDef(buff.getSpecialProperty());			
			PropertyDictionary pd = new PropertyDictionary();
			MGPropertyAccesser.setOrPutMaxMDef(pd, subMaxMDef)		
			FightPropertyEffectFacade.detachAndNotify(to, pd);
		}
		return result;
	}

	/**
	 * 物抗咒buff
	 */
	Closure<RuntimeResult> attach_skill_EndurePhysical = { FightSprite from, FightSprite to, MGFightSpriteBuff buff ->
		RuntimeResult result = RuntimeResult.OK();
		if(to instanceof Player) {
			to = (Player) to;
			double percent = MGPropertyAccesser.getSkillDamageRate(buff.getSpecialProperty()) / 100.0
			percent = percent < 0 ? 0 : percent;
			PropertyDictionary playerPd = to.getFightPropertyMgrComponent().getFightPropertyMgr().getSnapshotByNew().getPropertyDictionary()
			int maxPDef = MGPropertyAccesser.getMaxPDef(playerPd) ;
			int addMaxPDef =  maxPDef * percent;
		
			PropertyDictionary pd = new PropertyDictionary();
			MGPropertyAccesser.setOrPutMaxPDef(pd, addMaxPDef)
			MGPropertyAccesser.setOrPutMaxPDef(buff.getSpecialProperty(), addMaxPDef)
			FightPropertyEffectFacade.attachAndNotify(to, pd);
		}
		return result;
	}

	/**
	 * 取消物抗咒buff
	 */
	Closure<RuntimeResult> detach_skill_EndurePhysical = { FightSprite from, FightSprite to,MGFightSpriteBuff buff ->
		RuntimeResult result = RuntimeResult.OK();
		if(to instanceof Player) {
			to = (Player) to;

			double percent = MGPropertyAccesser.getSkillDamageRate(buff.getSpecialProperty()) /100.0
			percent = percent < 0 ? 0 : percent;
			int subMaxPDef = MGPropertyAccesser.getMaxPDef(buff.getSpecialProperty());
			PropertyDictionary pd = new PropertyDictionary();
			MGPropertyAccesser.setOrPutMaxPDef(pd, subMaxPDef)
			FightPropertyEffectFacade.detachAndNotify(to, pd);
		}
		return result;
	}
	/**
	 * 魔法盾buff
	 */
	Closure<RuntimeResult> attach_skill_magicShield = { FightSprite from, FightSprite to, MGFightSpriteBuff buff ->
		RuntimeResult result = RuntimeResult.OK();
		if(to instanceof Player) {
			to.changeState(MagicShieldState.MagicShieldState_Id);
		}
		return result;
	}
	/**
	 * 取消魔法盾buff
	 */
	Closure<RuntimeResult> detach_skill_magicShield = { FightSprite from, FightSprite to, MGFightSpriteBuff buff ->
		RuntimeResult result = RuntimeResult.OK();
		if(to instanceof Player) {
			to.cancelState(MagicShieldState.MagicShieldState_Id);
		}
		return result;
	}
	/**
	 * 魔法盾拓展buff
	 */
	Closure<RuntimeResult> attach_skill_magicShield_ext = { FightSprite from, FightSprite to, MGFightSpriteBuff buff ->
		RuntimeResult result = RuntimeResult.OK();
		if(to instanceof Player) {
			to = (Player) to;
			to.changeState(MagicShieldState.MagicShieldState_Id);
			int pDodgeAdded = MGPropertyAccesser.getPDodgePer(buff.getSpecialProperty())
			int mDodgeAdded = MGPropertyAccesser.getMDodgePer(buff.getSpecialProperty())
			PropertyDictionary pd = new PropertyDictionary();
			MGPropertyAccesser.setOrPutMDodgePer(pd, pDodgeAdded)
			MGPropertyAccesser.setOrPutPDodgePer(pd, mDodgeAdded)
			FightPropertyEffectFacade.attachAndNotify(to, pd);
		}
		return result;
	}

	/**
	 * 取消魔法盾拓展buff
	 */
	Closure<RuntimeResult> detach_skill_magicShield_ext = { FightSprite from, FightSprite to, MGFightSpriteBuff buff ->
		RuntimeResult result = RuntimeResult.OK();
		if(to instanceof Player) {
			to = (Player) to;
			to.cancelState(MagicShieldState.MagicShieldState_Id);
			int pDodgeAdded = MGPropertyAccesser.getPDodgePer(buff.getSpecialProperty())
			int mDodgeAdded = MGPropertyAccesser.getMDodgePer(buff.getSpecialProperty())

			PropertyDictionary pd = new PropertyDictionary();
			MGPropertyAccesser.setOrPutMDodgePer(pd, pDodgeAdded)
			MGPropertyAccesser.setOrPutPDodgePer(pd, pDodgeAdded)
			FightPropertyEffectFacade.detachAndNotify(to, pd);
		}
		return result;
	}
}
