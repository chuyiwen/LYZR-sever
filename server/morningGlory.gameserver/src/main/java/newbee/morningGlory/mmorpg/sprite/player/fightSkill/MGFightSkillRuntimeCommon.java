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
package newbee.morningGlory.mmorpg.sprite.player.fightSkill;

import newbee.morningGlory.mmorpg.sprite.MGFightProcessComponent;
import newbee.morningGlory.mmorpg.sprite.MGFightProcessHelper;
import newbee.morningGlory.mmorpg.sprite.buff.MGFightSpriteBuff;
import newbee.morningGlory.mmorpg.sprite.buff.MGFightSpriteBuffComponent;
import newbee.morningGlory.mmorpg.sprite.buff.MGFightSpriteBuffRef;
import newbee.morningGlory.mmorpg.sprite.skillEffect.effects.SkillEffectDie;

import org.apache.log4j.Logger;

import sophia.foundation.property.PropertyDictionary;
import sophia.game.GameRoot;
import sophia.mmorpg.base.sprite.FightSprite;
import sophia.mmorpg.base.sprite.fightSkill.FightSkill;
import sophia.mmorpg.base.sprite.fightSkill.FightSkillResult;
import sophia.mmorpg.base.sprite.fightSkill.FightSkillResultImpl;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.property.PlayerConfig;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;
import sophia.mmorpg.utils.RuntimeResult;

public final class MGFightSkillRuntimeCommon {
	private static final Logger logger = Logger.getLogger(MGFightSkillRuntimeCommon.class);

	public static RuntimeResult basicAttack(FightSkill fightSkill, FightSprite caster, FightSprite target, double damageRateOfSkill, int damageValueOfSkill) {

		byte attackType = MGFightProcessHelper.P_ATTACK;
		if (caster instanceof Player) {
			Player attacker = (Player) caster;
			if (PlayerConfig.isWarrior(attacker.getProfession())) {
				attackType = MGFightProcessHelper.P_ATTACK;
			} else if (PlayerConfig.isEnchanter(attacker.getProfession())) {
				attackType = MGFightProcessHelper.M_ATTACK;
			} else {
				attackType = MGFightProcessHelper.D_ATTACK;
			}
		}

		return basicAttack(fightSkill, caster, target, attackType, damageRateOfSkill, damageValueOfSkill);
	}

	public static RuntimeResult basicAttack(FightSkill fightSkill, FightSprite caster, FightSprite target, byte attackType, double damageRateOfSkill, int damageValueOfSkill) {
		MGFightProcessComponent<?> fightProcessComponent = (MGFightProcessComponent<?>) caster.getTagged(MGFightProcessComponent.Tag);
		RuntimeResult result = fightProcessComponent.basicAttack(target, attackType, damageRateOfSkill, damageValueOfSkill);
		FightSkillResult skillResult = result.getData().getComponent(FightSkillResult.class);
		if (result.getApplicationCode() != MGFightProcessComponent.ATTACK_INVAIN) {
			if (target.applyHP(caster, -skillResult.getDamage())) {
				if (target.isDead()) {
					((FightSkillResultImpl) skillResult).addSkillEffect(new SkillEffectDie(target));
				}
				if (logger.isDebugEnabled()) {
					PropertyDictionary targetPd = target.getFightPropertyMgrComponent().getFightPropertyMgr().getSnapshotByNew().getPropertyDictionary();
					int targetCurrentHp = MGPropertyAccesser.getHP(targetPd);
					logger.info("basic attack skill: " + fightSkill);
					logger.info("basic attack attacker: " + caster + " damge: " + skillResult.getDamage());
					logger.info("basic attack target: " + target + " target current HP: " + targetCurrentHp);
				}
			}
		}
		return result;
	}

	@SuppressWarnings("rawtypes")
	public static RuntimeResult addBuff(FightSprite attacker, FightSprite target, String buffRefId, int durationMillis) {
		MGFightSpriteBuffComponent fightSpriteBuffComponent = (MGFightSpriteBuffComponent) target.getTagged(MGFightSpriteBuffComponent.Tag);
		MGFightSpriteBuffRef ref = (MGFightSpriteBuffRef) GameRoot.getGameRefObjectManager().getManagedObject(buffRefId);
		MGFightSpriteBuff buff = new MGFightSpriteBuff(ref, attacker, target, durationMillis);
		// MGPropertyAccesser.setOrPutDuration(buff.getSpecialProperty(),
		// durationMillis);
		RuntimeResult result = fightSpriteBuffComponent.getFightSpriteBuffMgr().attachFightSpriteBuff(buff);
		return result;
	}

}
