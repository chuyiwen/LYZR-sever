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

import static com.google.common.base.Preconditions.checkArgument;
import newbee.morningGlory.mmorpg.player.pk.MGPlayerPKComponent;
import newbee.morningGlory.mmorpg.player.pk.MGPlayerPKMgr;
import newbee.morningGlory.mmorpg.player.pk.MGPlayerPKModel;
import newbee.morningGlory.mmorpg.sprite.skillEffect.effects.SkillEffectCrit;
import newbee.morningGlory.mmorpg.sprite.skillEffect.effects.SkillEffectFortunate;
import newbee.morningGlory.mmorpg.sprite.skillEffect.effects.SkillEffectHp;
import newbee.morningGlory.mmorpg.sprite.skillEffect.effects.SkillEffectMiss;
import newbee.morningGlory.mmorpg.union.MGUnionHelper;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import sophia.foundation.property.PropertyDictionary;
import sophia.game.component.communication.GameEvent;
import sophia.mmorpg.MMORPGContext;
import sophia.mmorpg.base.ConcreteComponent;
import sophia.mmorpg.base.sprite.FightSprite;
import sophia.mmorpg.base.sprite.fightProperty.FightPropertyMgr;
import sophia.mmorpg.base.sprite.fightSkill.FightSkillResultImpl;
import sophia.mmorpg.base.sprite.fightSkill.gameevent.AfterAttack_GE;
import sophia.mmorpg.base.sprite.fightSkill.gameevent.BeforeAttack_GE;
import sophia.mmorpg.base.sprite.fightSkill.gameevent.RookieProtection_GE;
import sophia.mmorpg.base.sprite.state.adjunction.InvincibleState;
import sophia.mmorpg.base.sprite.state.adjunction.MagicImmunityState;
import sophia.mmorpg.base.sprite.state.adjunction.PhysicalImmunityState;
import sophia.mmorpg.base.sprite.state.global.PKState;
import sophia.mmorpg.core.PropertyDictionaryModifyPhase;
import sophia.mmorpg.monster.Monster;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.property.PlayerConfig;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;
import sophia.mmorpg.utils.RuntimeResult;

public class MGFightProcessComponent<T extends FightSprite> extends ConcreteComponent<T> {
	private static final Logger logger = Logger.getLogger(MGFightProcessComponent.class);

	public static final String Tag = "MGFightProcessComponent";

	public static final int ATTACK_INVAIN = -1;
	
	private T owner;

	private int damageFromOutside = 0;

	public MGFightProcessComponent() {
	}

	public T getOwner() {
		return owner;
	}

	public void setOwner(T owner) {
		this.owner = owner;
	}

	public int getDamageFromOutSide() {
		return damageFromOutside;
	}

	public void setDamageFromOutSide(int damageFromOutSide) {
		this.damageFromOutside += damageFromOutSide;
	}

	public RuntimeResult attack(FightSprite target, double damageRateOfSkill, int damageValueOfSkill) {
		checkArgument(target != null);

		byte attackType = MGFightProcessHelper.P_ATTACK;
		if (owner instanceof Player) {
			Player attacker = (Player) owner;
			if (PlayerConfig.isWarrior(attacker.getProfession())) {
				attackType = MGFightProcessHelper.P_ATTACK;
			} else if (PlayerConfig.isEnchanter(attacker.getProfession())) {
				attackType = MGFightProcessHelper.M_ATTACK;
			} else {
				attackType = MGFightProcessHelper.D_ATTACK;
			}
		}

		return basicAttack(target, attackType, damageRateOfSkill, damageValueOfSkill);
	}

	public RuntimeResult basicAttack(FightSprite target, byte attackType, double damageRateOfSkill, int damageValueOfSkill) {
		RuntimeResult result = RuntimeResult.OK();
		checkArgument(target != null);

		// Send BeforeAttack_GE/AfterAttack_GE to allow someone else outside
		// here changes attacker's and target's PropertyDictionary to change
		// damage. We can't change target here. Consider adding hooks on
		// BeforeCastSkill_GE and AfterCastSkill_GE
		sendBeforeAttackGameEvent(owner, target);

		boolean isValid = isValidAttackState(target, getOwner(), attackType);
		if (!isValid) {
			FightSkillResultImpl skillResult = new FightSkillResultImpl(0, getOwner(), target);
			result.getData().addComponent(skillResult);
			result.setApplicationCode(ATTACK_INVAIN);
			return result;
		}

		FightPropertyMgr attackerFightPropertyMgr = owner.getFightPropertyMgrComponent().getFightPropertyMgr();
		PropertyDictionaryModifyPhase attackerPhase = attackerFightPropertyMgr.getSnapshotFromPool();
		PropertyDictionary attackerPd = attackerPhase.getPropertyDictionary();

		FightPropertyMgr targetFightPropertyMgr = target.getFightPropertyMgrComponent().getFightPropertyMgr();
		PropertyDictionaryModifyPhase targetPhase = targetFightPropertyMgr.getSnapshotFromPool();
		PropertyDictionary targetPd = targetPhase.getPropertyDictionary();

		boolean hited = MGFightProcessHelper.isHited(attackType, attackerPd, targetPd, target.getPathComponent().isRunning());
		boolean isFortunate = MGFightProcessHelper.isFortunateAttack(attackerPd);
		boolean isCritHit = MGFightProcessHelper.isCritHit(attackerPd, hited);

		int damageValue = 0;
		try {
			if (hited) {
				int atkValue = MGFightProcessHelper.attackValue(isFortunate, attackType, attackerPd);
				int defValue = MGFightProcessHelper.defenceValue(attackType, attackerPd, targetPd);
				damageValue = MGFightProcessHelper.skillDamageValue(atkValue, defValue, damageRateOfSkill, damageValueOfSkill);

				if (isCritHit) {
					damageValue = MGFightProcessHelper.critDamageValue(damageValue, attackerPd);
				}

				damageValue = MGFightProcessHelper.immunityDamage(attackType, damageValue, targetPd);
			}
		} finally {
			FightPropertyMgr.recycleSnapshotToPool(attackerPhase);
			FightPropertyMgr.recycleSnapshotToPool(targetPhase);
		}

		// send AfterAttack_GE to allow post-attack hooks
		sendAfterAttackGameEvent(owner, target, damageValue);

		damageValue = damageValue - damageFromOutside >= 0 ? damageValue - damageFromOutside : 0;

		damageFromOutside = 0;

		FightSkillResultImpl skillResult = new FightSkillResultImpl(damageValue, getOwner(), target);

		if (hited) {
			skillResult.addSkillEffect(new SkillEffectHp(damageValue, target));
			if (isFortunate) {
				skillResult.addSkillEffect(new SkillEffectFortunate(getOwner()));
				if (isCritHit) {
					skillResult.addSkillEffect(new SkillEffectCrit(damageValue, target));
				}
			}
		} else {
			skillResult.addSkillEffect(new SkillEffectMiss(target));
		}

		result.getData().addComponent(skillResult);

		if (logger.isDebugEnabled()) {
			logger.debug("attack type: " + attackType);
			logger.debug("attacker: " + owner + " target: " + target);
			logger.debug("hited = " + hited + ", fortunate = " + isFortunate + ", isCritHit = " + isCritHit + ", damage=" + damageValue);
			logger.debug("result: " + result);
		}

		return result;
	}

	private void sendBeforeAttackGameEvent(FightSprite attacker, FightSprite target) {
		BeforeAttack_GE before = new BeforeAttack_GE(attacker, target);
		sendGameEvent(BeforeAttack_GE.class.getSimpleName(), before, target);
		sendGameEvent(BeforeAttack_GE.class.getSimpleName(), before);
	}

	private void sendAfterAttackGameEvent(FightSprite attacker, FightSprite target, int damage) {
		AfterAttack_GE after = new AfterAttack_GE(attacker, target, damage);
		sendGameEvent(AfterAttack_GE.class.getSimpleName(), after, target);
		sendGameEvent(AfterAttack_GE.class.getSimpleName(), after);
	}

	private static void sendGameEvent(String id, Object data, FightSprite target) {
		GameEvent<Object> event = GameEvent.getInstance(id, data);
		target.handleGameEvent(event);
		GameEvent.pool(event);
	}

	public static void sendRookieProtectionGameEvent(Player attacker, Player target) {
		RookieProtection_GE rookie = new RookieProtection_GE(attacker, target);
		sendGameEvent(RookieProtection_GE.class.getSimpleName(), rookie, target);
		sendGameEvent(RookieProtection_GE.class.getSimpleName(), rookie, attacker);
	}

	@SuppressWarnings("rawtypes")
	public boolean isValidAttackState(FightSprite target, FightSprite owner, byte attackType) {
		if (target == null) {
			return false;
		}
		if (owner == null) {
			return false;
		}
		// handle states wherein player can't fight.
		if ((target.getFightSpriteStateMgr().isState(PhysicalImmunityState.PhysicalImmunityState_Id) && attackType == MGFightProcessHelper.P_ATTACK)
				|| (target.getFightSpriteStateMgr().isState(MagicImmunityState.MagicImmunityState_Id) && attackType == MGFightProcessHelper.M_ATTACK)) {
			return false;
		} else if (target.getFightSpriteStateMgr().isState(InvincibleState.InvincibleState_Id)) {
			return false;
		}

		// rookie protection
		if (owner instanceof Player && target instanceof Player) {
			Player attackerP = (Player) owner;
			Player targetP = (Player) target;
			boolean neededToProtectRookie = MGPlayerPKComponent.isNeededToProtectRookie(attackerP, targetP);
			if (neededToProtectRookie) {
				sendRookieProtectionGameEvent(attackerP, targetP);
				return false;
			}
		}

		if (owner instanceof Player && target instanceof Monster) {
			Player attackerP = (Player) owner;
			Monster targetM = (Monster) target;
			if (targetM.getMonsterRef().isSkillSummon() || targetM.getMonsterRef().isSummonMonster()) {
				boolean neededToProtectRookie = MGPlayerPKComponent.isNeededToProtectRookie(attackerP, (Player) targetM.getOwner());
				if (neededToProtectRookie) {
					sendRookieProtectionGameEvent(attackerP, (Player) targetM.getOwner());
					return false;
				}
			}
		}

		if (owner instanceof Monster && target instanceof Monster) {
			Monster monsterTarget = (Monster) target;
			Monster monsterAttacker = (Monster) owner;
			if (monsterAttacker.getMonsterRef().isRegularMonster() && monsterTarget.getMonsterRef().isRegularMonster()) {
				return false;
			}
		}

		FightSprite sprite = owner;
		if (owner instanceof Monster) {
			Monster monster = (Monster) owner;
			FightSprite monsterOwner = monster.getOwner();
			if (monsterOwner == null) {
				return true;
			}
			sprite = monsterOwner;
		}
		if (target instanceof Monster) {
			Monster monster = (Monster) target;
			FightSprite monsterOwner = monster.getOwner();
			String unionName = MGPropertyAccesser.getUnionName(monster.getProperty());
			if (StringUtils.isEmpty(unionName)) {
				if (!monster.getMonsterRef().isRegularMonster()) {
					target = monsterOwner;
					if (StringUtils.equals(target.getId(), owner.getId())) {
						return false;
					}
				} else {
					return true;
				}
			}
		}

		MGPlayerPKComponent pkComponent = (MGPlayerPKComponent) sprite.getTagged(MGPlayerPKComponent.Tag);
		MGPlayerPKMgr pkMgr = pkComponent.getPlayerPKMgr();
		if (pkMgr.isModel(MGPlayerPKModel.PeaceModel)) {
			if (target instanceof Monster) {
				boolean isSameUnion = MGUnionHelper.isInTheSameUnion(sprite, target);
				if (isSameUnion) {
					return false;
				}
				Monster monster = (Monster) target;
				if (monster.getMonsterRef().isRegularMonster()) {
					return true;
				}

			}
			return false;
		} else if (pkMgr.isModel(MGPlayerPKModel.GoodEvilModel)) {
			int pkValue = MGPropertyAccesser.getPkValue(target.getProperty());
			if (target.getFightSpriteStateMgr().isState(PKState.PKState_Id) || pkValue > 200) {
				return true;
			}
		} else if (pkMgr.isModel(MGPlayerPKModel.TeamModel)) {
			if (target instanceof Monster || sprite instanceof Monster) {
				return true;
			}
			boolean isSameTeam = MMORPGContext.playerTeamManagerComponent().isSameTeam((Player) target, (Player) sprite);
			if (isSameTeam) {
				return false;
			}
			return true;

		} else if (pkMgr.isModel(MGPlayerPKModel.UnionModel)) {
			boolean isSameUnion = MGUnionHelper.isInTheSameUnion(sprite, target);
			if (isSameUnion) {
				return false;
			}
			return true;
		} else if (pkMgr.isModel(MGPlayerPKModel.EntityModel)) {
			return true;
		}

		return false;
	}

}
