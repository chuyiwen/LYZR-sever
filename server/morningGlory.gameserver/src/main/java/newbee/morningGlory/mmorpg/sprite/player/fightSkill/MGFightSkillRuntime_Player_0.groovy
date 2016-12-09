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
package newbee.morningGlory.mmorpg.sprite.player.fightSkill

import newbee.morningGlory.mmorpg.sprite.MGFightProcessComponent
import newbee.morningGlory.mmorpg.sprite.MGFightProcessHelper
import newbee.morningGlory.mmorpg.sprite.skillEffect.effects.SkillEffectDie

import org.apache.log4j.Logger

import sophia.foundation.property.PropertyDictionary
import sophia.mmorpg.base.scene.GameSceneHelper
import sophia.mmorpg.base.sprite.FightSprite
import sophia.mmorpg.base.sprite.fightSkill.FightSkill
import sophia.mmorpg.base.sprite.fightSkill.FightSkillResult
import sophia.mmorpg.base.sprite.fightSkill.FightSkillResultImpl
import sophia.mmorpg.core.CDMgr
import sophia.mmorpg.player.fightSkill.event.G2C_TriggerSingleTargetSkill
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser
import sophia.mmorpg.utils.RuntimeResult
import sophia.mmorpg.base.sprite.fightSkill.FightSkillRuntimeComponent

final class MGFightSkillRuntime_Player_0 {
	private static final Logger logger = Logger.getLogger(MGFightSkillRuntime_Player_0.class)
	private final Map<String, Closure<RuntimeResult>> map = new HashMap<>();

	MGFightSkillRuntime_Player_0() {
		map.put("skill_0", player_0_0_closure);
	}

	public Map<String, Closure<RuntimeResult>> getClosures() {
		return map;
	}

	/**
	 * 普通攻击（不分职业）
	 */
	Closure<RuntimeResult> player_0_0_closure = {FightSkill fightSkill, FightSprite caster, FightSprite target ->

		CDMgr runtimeCDManager = caster.getFightSkillRuntimeComponent().getBasicCDMgr()
		String refId = fightSkill.getRefId()
		if(!runtimeCDManager.isCDStarted(refId)) {
			runtimeCDManager.startCD(refId, FightSkillRuntimeComponent.basicCD);
		}

		runtimeCDManager.update(refId)

		MGFightProcessComponent fightProcessComponent = caster.getTagged(MGFightProcessComponent.Tag);
		if(!fightProcessComponent.isValidAttackState(target, caster,MGFightProcessHelper.B_ATTACK)){
			return;
		}
		
		RuntimeResult result = fightProcessComponent.basicAttack(target, MGFightProcessHelper.B_ATTACK, 1, 0);
		FightSkillResult skillResult = result.getData().getComponent(FightSkillResult.class);
		
		if(target.applyHP(caster, -skillResult.getDamage())){
			if(target.isDead()){
				((FightSkillResultImpl)skillResult).addSkillEffect(new SkillEffectDie(target));
			}
			if(logger.isDebugEnabled()) {
				PropertyDictionary targetPd = target.getFightPropertyMgrComponent().getFightPropertyMgr().getSnapshotByNew().getPropertyDictionary()
				int targetCurrentHp = MGPropertyAccesser.getHP(targetPd)
				logger.info("basic attack skill: " + fightSkill)
				logger.info("basic attack attacker: " + caster + " damge: " + skillResult.getDamage())
				logger.info("basic attack target: " + target + " target current HP: " + targetCurrentHp)
			}

			G2C_TriggerSingleTargetSkill response = new G2C_TriggerSingleTargetSkill(fightSkill.getRefId(), caster, target, result.getData().getComponent(
					FightSkillResult.class));
			GameSceneHelper.broadcastMessageToAOI(caster, response);
		}
		return result;
	}
}