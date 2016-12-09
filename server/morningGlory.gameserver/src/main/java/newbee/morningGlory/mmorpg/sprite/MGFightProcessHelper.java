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

import org.apache.log4j.Logger;

import sophia.foundation.property.PropertyDictionary;
import sophia.game.component.communication.GameEvent;
import sophia.mmorpg.base.sprite.FightSprite;
import sophia.mmorpg.base.sprite.fightSkill.gameevent.AfterAttack_GE;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.property.PlayerConfig;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;
import sophia.mmorpg.utils.SFRandomUtils;

public class MGFightProcessHelper {
	private static final Logger logger = Logger.getLogger(MGFightProcessHelper.class);
	public static final byte B_ATTACK = 0; // 普攻
	public static final byte P_ATTACK = 1; // 物攻
	public static final byte M_ATTACK = 2; // 魔攻
	public static final byte D_ATTACK = 3; // 道攻
	private static final int minimalDamage = 1;

	public static boolean isHited(byte attackType, PropertyDictionary attackerPd, PropertyDictionary targetPd, boolean targetRunning) {
		double finalHitRate = 0.0;
		int attackerHit = MGPropertyAccesser.getHit(attackerPd);
		int targetDodge = MGPropertyAccesser.getDodge(targetPd);
		double initialHitRate = adjustHitRate(attackerHit * 1.0 / targetDodge);
		if (targetRunning) {
			finalHitRate = initialHitRate - getDodgeCo(attackType, targetPd) - 0.3;
		} else {
			finalHitRate = initialHitRate - getDodgeCo(attackType, targetPd);
		}
		return SFRandomUtils.random100() <= finalHitRate * 100;
	}

	// get dodge coefficient
	private static double getDodgeCo(byte attackType, PropertyDictionary data) {
		double dodgeCo = 0.0;
		if (attackType == P_ATTACK || attackType == B_ATTACK) {
			dodgeCo = MGPropertyAccesser.getPDodgePer(data) / 100;
		} else {
			dodgeCo = MGPropertyAccesser.getMDodgePer(data) / 100;
		}
		return dodgeCo;
	}

	// adjust hitRate by range [0.5, 1.1]
	private static double adjustHitRate(double hitRate) {
		if (hitRate < 0.5)
			return 0.5;
		if (hitRate > 1.1)
			return 1.1;
		return hitRate;
	}

	public static boolean isFortunateAttack(PropertyDictionary attackerPd) {
		int fortune = MGPropertyAccesser.getFortune(attackerPd) + 1;
		logger.debug("isFortunateAttack fortune: " + fortune);
		return SFRandomUtils.random10() <= fortune;
	}

	public static int getMaxAttack(byte attackType, PropertyDictionary data) {
		if (attackType == P_ATTACK || attackType == B_ATTACK) {
			return MGPropertyAccesser.getMaxPAtk(data);
		} else if (attackType == M_ATTACK) {
			return MGPropertyAccesser.getMaxMAtk(data);
		} else {
			return MGPropertyAccesser.getMaxTao(data);
		}
	}

	public static int getMinAttack(byte attackType, PropertyDictionary data) {
		if (attackType == P_ATTACK || attackType == B_ATTACK) {
			return MGPropertyAccesser.getMinPAtk(data);
		} else if (attackType == M_ATTACK) {
			return MGPropertyAccesser.getMinMAtk(data);
		} else {
			return MGPropertyAccesser.getMinTao(data);
		}
	}

	public static int attackValue(boolean fortunateAttack, byte attackType, PropertyDictionary attackerPd) {
		int maxAttack = getMaxAttack(attackType, attackerPd);
		int minAttack = getMinAttack(attackType, attackerPd);
		if (fortunateAttack)
			return maxAttack;
		if ((maxAttack - minAttack) < 0) {
			return 0;
		}
		if (maxAttack == minAttack) {
			return maxAttack;
		}
		return SFRandomUtils.random(minAttack, maxAttack);
	}

	public static int getMaxDefence(byte attackType, PropertyDictionary targetPd) {
		if (attackType == P_ATTACK || attackType == B_ATTACK) {
			return MGPropertyAccesser.getMaxPDef(targetPd);
		} else {
			return MGPropertyAccesser.getMaxMDef(targetPd);
		}
	}

	public static int getMinDefence(byte attackType, PropertyDictionary targetPd) {
		if (attackType == P_ATTACK || attackType == B_ATTACK) {
			return MGPropertyAccesser.getMinPDef(targetPd);
		} else {
			return MGPropertyAccesser.getMinMDef(targetPd);
		}
	}

	public static double getIgnoreDefenceRate(byte attackType, PropertyDictionary attackerPd) {
		if (attackType == P_ATTACK || attackType == B_ATTACK) {
			return MGPropertyAccesser.getIgnorePDef(attackerPd) / 100;
		} else {
			return MGPropertyAccesser.getIgnoreMDef(attackerPd) / 100;
		}
	}

	public static int defenceValue(byte attackType, PropertyDictionary attackerPd, PropertyDictionary targetPd) {
		int maxDefence = getMaxDefence(attackType, targetPd);
		int minDefence = getMinDefence(attackType, targetPd);
		double ignoreDefenceRate = getIgnoreDefenceRate(attackType, attackerPd);
		if ((maxDefence - minDefence) < 0) {
			return 0;
		}
		if (maxDefence == minDefence) {
			return maxDefence;
		}
		return (int) (SFRandomUtils.random(minDefence, maxDefence) * (1 - ignoreDefenceRate));
	}

	public static boolean isCritHit(PropertyDictionary attackerPd) {
		int crit = MGPropertyAccesser.getCrit(attackerPd);
		logger.debug("isCritHit crit: " + crit);
		return SFRandomUtils.random100() <= crit;
	}

	public static boolean isCritHit(PropertyDictionary attackerPd, boolean isHited) {
		boolean isCrit = isCritHit(attackerPd);
		return isCrit && isHited;
	}

	private static int baseDamage(int attackValue, int defenceValue) {
		int baseDamageValue = attackValue - defenceValue;
		logger.debug("baseDamage: attackValue: " + attackValue + " defenceValue: " + defenceValue);
		if (baseDamageValue <= 0)
			return minimalDamage;
		return baseDamageValue;
	}

	public static int skillDamageValue(int attackValue, int defenceValue, double damageRateOfSkill, int damageValueOfSkill) {
		int baseDamageValue = baseDamage(attackValue, defenceValue);
		int damage = (int) (baseDamageValue * damageRateOfSkill + damageValueOfSkill);
		if (damage <= 0) {
			damage = minimalDamage;
		}
		logger.debug("skillDamageValue attackValue: " + attackValue + " defenceValue: " + defenceValue + " damageRateOfSkill: " + damageRateOfSkill + " damageValueOfSkill: "
				+ damageValueOfSkill + "  base damage: " + damage);
		return damage;

	}

	public static int skillDamageValue(int attackValue, int defenceValue, double damageRateOfSkill, int damageValueOfSkill, double damageChanceOfSkill) {
		int damage = 0;
		boolean happened = SFRandomUtils.random100() <= damageChanceOfSkill * 100;
		if (happened) {
			damage = skillDamageValue(attackValue, defenceValue, damageRateOfSkill, damageValueOfSkill);
		} else {
			damage = baseDamage(attackValue, defenceValue);
		}
		return damage;

	}

	public static int critDamageValue(int damageValue, PropertyDictionary attackerPd) {
		int critInjure = MGPropertyAccesser.getCritInjure(attackerPd);
		logger.debug("critDamageValue critInjure: " + critInjure);
		return (int) (damageValue * 1.5 + critInjure);
	}

	public static double getImmunityRate(byte attackType, PropertyDictionary targetPd) {
		double immunityRate = 0;
		if (attackType == P_ATTACK || attackType == B_ATTACK) {
			immunityRate = MGPropertyAccesser.getPImmunityPer(targetPd) / 100;
		} else {
			immunityRate = MGPropertyAccesser.getMImmunityPer(targetPd) / 100;
		}
		if (immunityRate < 0)
			immunityRate = 0;
		logger.debug("getImmunityRate immunityRate: " + immunityRate);
		return immunityRate;
	}

	public static int immunityDamage(byte attackType, int damageValue, PropertyDictionary targetPd) {
		double immunityRate = getImmunityRate(attackType, targetPd);
		int damage = (int) (damageValue * (1 - immunityRate));
		logger.debug("immunityRate: immunityRate: " + immunityRate + " damage: " + damage);
		return damage;
	}

	public static void sendAfterAttackGameEvent(FightSprite attacker, FightSprite target, int damage) {
		AfterAttack_GE after = new AfterAttack_GE(attacker, target, damage);
		sendGameEvent(AfterAttack_GE.class.getSimpleName(), after, target);
		sendGameEvent(AfterAttack_GE.class.getSimpleName(), after, attacker);
	}

	public static void sendGameEvent(String id, Object data, FightSprite target) {
		GameEvent<Object> event = GameEvent.getInstance(id, data);
		target.handleGameEvent(event);
		GameEvent.pool(event);
	}

	public static byte getAttackTypeByProfession(Player attacker) {
		byte attackType = MGFightProcessHelper.P_ATTACK;
		if (PlayerConfig.isWarrior(attacker.getProfession())) {
			attackType = MGFightProcessHelper.P_ATTACK;
		} else if (PlayerConfig.isEnchanter(attacker.getProfession())) {
			attackType = MGFightProcessHelper.M_ATTACK;
		} else {
			attackType = MGFightProcessHelper.D_ATTACK;
		}
		return attackType;
	}
}
