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
import newbee.morningGlory.mmorpg.sprite.MGFightProcessHelper;
import newbee.morningGlory.mmorpg.sprite.buff.MGFightSpriteBuff
import newbee.morningGlory.mmorpg.sprite.buff.MGFightSpriteBuffComponent
import newbee.morningGlory.mmorpg.sprite.buff.MGFightSpriteBuffRef
import newbee.morningGlory.mmorpg.sprite.skillEffect.effects.SkillEffectMove

import org.apache.log4j.Logger

import sophia.foundation.property.PropertyDictionary
import sophia.foundation.util.Position
import sophia.game.GameRoot
import sophia.mmorpg.base.scene.GameScene
import sophia.mmorpg.base.scene.GameSceneHelper
import sophia.mmorpg.base.scene.aoi.SceneAOILayer;
import sophia.mmorpg.base.sprite.FightSprite
import sophia.mmorpg.base.sprite.fightProperty.FightPropertyMgr
import sophia.mmorpg.base.sprite.fightSkill.FightSkill
import sophia.mmorpg.base.sprite.fightSkill.FightSkillResult
import sophia.mmorpg.base.sprite.fightSkill.FightSkillResultImpl
import sophia.mmorpg.base.sprite.fightSkill.FightSkillRuntimeHelper
import sophia.mmorpg.core.CDMgr
import sophia.mmorpg.core.PropertyDictionaryModifyPhase
import sophia.mmorpg.monster.Monster
import sophia.mmorpg.player.Player
import sophia.mmorpg.player.fightSkill.event.G2C_TriggerMultiTargetSkill
import sophia.mmorpg.player.fightSkill.event.G2C_TriggerSingleTargetSkill
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser
import sophia.mmorpg.ref.editorgen.MGPropertySymbolDefines;
import sophia.mmorpg.utils.RuntimeResult
import sophia.mmorpg.utils.SFRandomUtils

/**
 * 战士职业技能
 */
final class MGFightSkillRuntime_Player_1 {
	private static final Logger logger = Logger.getLogger(MGFightSkillRuntime_Player_1.class)
	private final Map<String, Closure<RuntimeResult>> map = new HashMap<>();

	MGFightSkillRuntime_Player_1() {
		map.put("skill_zs_1", player_1_1_closure);
		map.put("skill_zs_2", player_1_2_closure);
		map.put("skill_zs_3", player_1_3_closure);
		map.put("skill_zs_3_1", player_1_3_1_closure);
		map.put("skill_zs_3_2", player_1_3_2_closure);
		map.put("skill_zs_3_3", player_1_3_3_closure);
		map.put("skill_zs_4", player_1_4_closure);
		map.put("skill_zs_4_1", player_1_4_1_closure);
		map.put("skill_zs_4_2", player_1_4_2_closure);
		map.put("skill_zs_5", player_1_5_closure);
		map.put("skill_zs_5_1", player_1_5_1_closure);
		map.put("skill_zs_5_2", player_1_5_2_closure);
		map.put("skill_zs_6", player_1_6_closure);
		map.put("skill_zs_6_1", player_1_6_1_closure);
		map.put("skill_zs_6_2", player_1_6_2_closure);
	}

	public Map<String, Closure<RuntimeResult>> getClosures() {
		return map;
	}


	/**
	 * 基础剑法 ——加点技能
	 * 永久增加角色X点准确
	 */
	Closure<RuntimeResult> player_1_1_closure = { FightSkill fightSkill, FightSprite caster, FightSprite target ->
		RuntimeResult result = RuntimeResult.OK()

		return result;
	}

	/**
	 * 攻杀剑法 ——主动精灵目标技能
	 * 特定攻击（普攻，刺杀，半月剑法状态）时，有M%概率触发
	 * 一旦触发，会强行替换成攻杀剑法效果
	 * 本次攻击对目标造成“X%+Y”点伤害
	 */
	Closure<RuntimeResult> player_1_2_closure = { FightSkill fightSkill, FightSprite caster, FightSprite target ->

		PropertyDictionary runtimeParameter = fightSkill.getLevelRef().getRuntimeParameter()
		double rate = MGPropertyAccesser.getSkillDamageRate(runtimeParameter) / 100.0
		int value = MGPropertyAccesser.getSkillDamage(runtimeParameter)

		MGFightProcessComponent<?> fightProcessComponent = (MGFightProcessComponent<?>) caster.getTagged(MGFightProcessComponent.Tag);
		if(!fightProcessComponent.isValidAttackState(target, caster,MGFightProcessHelper.P_ATTACK)){
			return;
		}
		RuntimeResult result = MGFightSkillRuntimeCommon.basicAttack(fightSkill, caster, target, rate, value);

		G2C_TriggerSingleTargetSkill response = new G2C_TriggerSingleTargetSkill(fightSkill.getRefId(), caster, target, result.getData().getComponent(
				FightSkillResult.class));
		GameSceneHelper.broadcastMessageToAOI(caster, response)

		return result;
	}

	/**
	 * 刺杀剑法 ——主动精灵目标技能
	 * 分为开启状态和关闭状态，使用本技能时只能改变这个状态
	 * 技能效果为造成身前1格100%的伤害，以及身前第2格X%的伤害，且第2格伤害无视防御
	 */
	Closure<RuntimeResult> player_1_3_closure = { FightSkill fightSkill, FightSprite caster, FightSprite target ->
		MGFightProcessComponent<?> fightProcessComponent = (MGFightProcessComponent<?>) caster.getTagged(MGFightProcessComponent.Tag);
		if(!fightProcessComponent.isValidAttackState(target, caster,MGFightProcessHelper.P_ATTACK)){
			return;
		}

		PropertyDictionary runtimeParameter = fightSkill.getLevelRef().getRuntimeParameter()
		double rate = MGPropertyAccesser.getSkillDamageRate(runtimeParameter) / 100.0

		RuntimeResult result1 = MGFightSkillRuntimeCommon.basicAttack(fightSkill, caster, target, 1, 0);

		byte direction = FightSkillRuntimeHelper.getDirection(caster.getCrtPosition(), target.getCrtPosition())
		Collection<FightSprite> fightSprites = GameSceneHelper.getFightSprites(caster.getCrtScene(), caster.getCrtPosition(), direction, 1, 2)

		G2C_TriggerMultiTargetSkill response = new G2C_TriggerMultiTargetSkill(fightSkill.getRefId(), caster, direction)
		response.addSkillResult(result1.getData().getComponent(FightSkillResult.class))

		for(FightSprite sprite : fightSprites) {
			// ignore the target since getFightSprites will return all of them
			if(sprite.equals(target) || (sprite instanceof Player && sprite.isInSafeRegion())) {
				continue;
			}
			//ignore target defense by first set target's minPDef/maxPDef to 0, and then set it back
			PropertyDictionary spritePd = sprite.fightPropertyMgrComponent.getFightPropertyMgr().getSnapshotByNew().getPropertyDictionary()
			int minPDef = MGPropertyAccesser.getMinPDef(spritePd)
			int maxPDef = MGPropertyAccesser.getMaxPDef(spritePd)
			sprite.getFightPropertyMgrComponent().getFightPropertyMgr().getModifyTransaction().modifySomeProperty(MGPropertySymbolDefines.MinPDef_Id, -minPDef)
			sprite.getFightPropertyMgrComponent().getFightPropertyMgr().getModifyTransaction().modifySomeProperty(MGPropertySymbolDefines.MaxPDef_Id, -maxPDef)
			RuntimeResult result2 = MGFightSkillRuntimeCommon.basicAttack(fightSkill, caster, sprite, rate, 0);
			FightSkillResult skillResult = result2.getData().getComponent(FightSkillResult.class)
			response.addSkillResult(skillResult)

			sprite.getFightPropertyMgrComponent().getFightPropertyMgr().getModifyTransaction().modifySomeProperty(MGPropertySymbolDefines.MinPDef_Id, minPDef)
			sprite.getFightPropertyMgrComponent().getFightPropertyMgr().getModifyTransaction().modifySomeProperty(MGPropertySymbolDefines.MaxPDef_Id, maxPDef)
		}

		GameSceneHelper.broadcastMessageToAOI(caster, response)

		return result1;
	}

	/**
	 * 刺杀剑法拓展1
	 * 刺杀带有流血效果，持续5秒，可以叠加3次流血：每秒造成普攻物伤*30%的伤害，持续5秒，最多叠加3个同类BUFF
	 */
	Closure<RuntimeResult> player_1_3_1_closure = {FightSkill fightSkill, FightSprite caster, FightSprite target ->

		RuntimeResult result = player_1_3_closure.call(fightSkill, caster, target)

		String bleedingBuffId = "buff_state_4"
		long durationMillis = 5000
		MGFightSpriteBuffComponent fightSpriteBuffComponent = (MGFightSpriteBuffComponent) target.getTagged(MGFightSpriteBuffComponent.Tag);
		MGFightSpriteBuffRef ref = (MGFightSpriteBuffRef) GameRoot.getGameRefObjectManager().getManagedObject(bleedingBuffId);
		MGFightSpriteBuff buff = new MGFightSpriteBuff(ref, caster, target, durationMillis);
		MGPropertyAccesser.setOrPutAttachRepeatCount(buff.getSpecialProperty(), (byte)3);
		// damage
		PropertyDictionary pd = caster.getFightPropertyMgrComponent().getFightPropertyMgr().getSnapshotByNew().getPropertyDictionary()
		int maxPAtk = MGPropertyAccesser.getMaxPAtk(pd)
		int minPAtk = MGPropertyAccesser.getMinPAtk(pd)
		int attack = maxPAtk <= minPAtk ? maxPAtk :  SFRandomUtils.random(minPAtk, maxPAtk)

		MGPropertyAccesser.setOrPutHP(buff.getSpecialProperty(), (int)(attack * 0.3));
		fightSpriteBuffComponent.getFightSpriteBuffMgr().attachFightSpriteBuff(buff);

		return result
	}

	/**
	 * 刺杀剑法拓展2
	 * 刺杀带有缓速效果，持续2秒缓速：使移动速度降低30%，多个效果叠加时，取最后的效果为唯一效果
	 */
	Closure<RuntimeResult> player_1_3_2_closure = {FightSkill fightSkill, FightSprite caster, FightSprite target ->
		MGFightProcessComponent<?> fightProcessComponent = (MGFightProcessComponent<?>) caster.getTagged(MGFightProcessComponent.Tag);
		if(!fightProcessComponent.isValidAttackState(target, caster,MGFightProcessHelper.P_ATTACK)){
			return;
		}

		RuntimeResult result = player_1_3_closure.call(fightSkill, caster, target)

		String deceleratingSpeedBuffId = "buff_state_7"
		long durationMillis = 2000
		int speedLowered = 30
		MGFightSpriteBuffComponent fightSpriteBuffComponent = (MGFightSpriteBuffComponent) target.getTagged(MGFightSpriteBuffComponent.Tag);
		MGFightSpriteBuffRef ref = (MGFightSpriteBuffRef) GameRoot.getGameRefObjectManager().getManagedObject(deceleratingSpeedBuffId);
		MGFightSpriteBuff buff = new MGFightSpriteBuff(ref, caster, target, durationMillis);
		MGPropertyAccesser.setOrPutMoveSpeedPer(buff.getSpecialProperty(), speedLowered);
		fightSpriteBuffComponent.getFightSpriteBuffMgr().attachFightSpriteBuff(buff);

		return result
	}

	/**
	 * 刺杀剑法拓展3
	 * 使用刺杀剑法时，自身暴击几率增加15%
	 */
	Closure<RuntimeResult> player_1_3_3_closure = {FightSkill fightSkill, FightSprite caster, FightSprite target ->
		// first grid
		RuntimeResult result0 = MGFightSkillRuntimeCommon.basicAttack(fightSkill, caster, target, 1, 0);

		// second grid
		byte direction = FightSkillRuntimeHelper.getDirection(caster.getCrtPosition(), target.getCrtPosition())
		Collection<FightSprite> fightSprites = GameSceneHelper.getFightSprites(caster.getCrtScene(), caster.getCrtPosition(), direction, 1, 2)

		G2C_TriggerMultiTargetSkill response = new G2C_TriggerMultiTargetSkill(fightSkill.getRefId(), caster, direction)
		response.addSkillResult(result0.getData().getComponent(FightSkillResult.class))

		PropertyDictionary runtimeParameter = fightSkill.getLevelRef().getRuntimeParameter()
		double rate = MGPropertyAccesser.getSkillDamageRate(runtimeParameter) / 100.0

		FightPropertyMgr attackerFightPropertyMgr = caster.getFightPropertyMgrComponent().getFightPropertyMgr()
		PropertyDictionary casterPd = attackerFightPropertyMgr.getSnapshotByNew().getPropertyDictionary();

		int crit = MGPropertyAccesser.getCrit(casterPd)

		int deltaRate = 15;

		if(logger.isDebugEnabled()) {
			logger.debug("刺杀剑法拓展3 initial crit " + crit)
		}

		for(FightSprite sprite : fightSprites) {
			// ignore the target since getFightSprites will return all of them
			if(sprite.equals(target) || (sprite instanceof Player && sprite.isInSafeRegion())) {
				continue;
			}

			if(logger.isDebugEnabled()) {
				logger.debug("刺杀剑法拓展3  crit " + MGPropertyAccesser.getCrit(casterPd))
			}

			attackerFightPropertyMgr.getModifyTransaction().modifySomeProperty(MGPropertySymbolDefines.Crit_Id, deltaRate)

			if(logger.isDebugEnabled()) {
				logger.debug("刺杀剑法拓展3 before crit " + MGPropertyAccesser.getCrit(casterPd))
			}

			RuntimeResult result = MGFightSkillRuntimeCommon.basicAttack(fightSkill, caster, target, rate, 0);
			response.addSkillResult(result.getData().getComponent(FightSkillResult.class))

			attackerFightPropertyMgr.getModifyTransaction().modifySomeProperty(MGPropertySymbolDefines.Crit_Id, -deltaRate)

			if(logger.isDebugEnabled()) {
				logger.debug("刺杀剑法拓展3 after crit " + MGPropertyAccesser.getCrit(casterPd))
			}
		}

		GameSceneHelper.broadcastMessageToAOI(caster, response)

		return result0
	}

	/**
	 * 半月剑法 ——主动朝向技能
	 * 分为开启状态和关闭状态，使用本技能时只能改变这个状态
	 * 当半月剑法和刺杀剑法同时开启时，优先施放半月剑法
	 * 技能效果为造成身前1格100%的伤害，周围4格X%的伤害（范围如图：）
	 */
	Closure<RuntimeResult> player_1_4_closure = {FightSkill fightSkill, FightSprite caster, FightSprite target ->
		byte direction = FightSkillRuntimeHelper.getDirection(caster.getCrtPosition(), target.getCrtPosition())
		Collection<FightSprite> fightSprites = FightSkillRuntimeHelper.getHalfMoonSprites(caster, direction)
		return player_1_4_closure_base.call(fightSkill, caster, target, fightSprites)
	}


	Closure<RuntimeResult> player_1_4_closure_base = {FightSkill fightSkill, FightSprite caster, FightSprite target, Collection<FightSprite> targets ->
		MGFightProcessComponent<?> fightProcessComponent = (MGFightProcessComponent<?>) caster.getTagged(MGFightProcessComponent.Tag);
		if(!fightProcessComponent.isValidAttackState(target, caster,MGFightProcessHelper.P_ATTACK)){
			return;
		}

		PropertyDictionary runtimeParameter = fightSkill.getLevelRef().getRuntimeParameter()
		double rate = MGPropertyAccesser.getSkillDamageRate(runtimeParameter) / 100.0

		byte direction = FightSkillRuntimeHelper.getDirection(caster.getCrtPosition(), target.getCrtPosition())

		G2C_TriggerMultiTargetSkill response = new G2C_TriggerMultiTargetSkill(fightSkill.getRefId(), caster, direction)
		GameScene crtScene = caster.getCrtScene()
		Position crtPosition = caster.getCrtPosition()

		// forward grid
		Collection<FightSprite> forwardSprites = GameSceneHelper.getFightSprites(crtScene, crtPosition, direction, 1, 1);
		for(FightSprite sprite : forwardSprites) {
			if((sprite instanceof Player && sprite.isInSafeRegion())) {
				continue;
			}
			RuntimeResult result = MGFightSkillRuntimeCommon.basicAttack(fightSkill, caster, sprite, 1, 0);
			FightSkillResult skillResult = result.getData().getComponent(FightSkillResult.class)
			response.addSkillResult(skillResult)
		}

		// other grids
		for(FightSprite sprite : targets) {
			if((sprite instanceof Player && sprite.isInSafeRegion())) {
				continue;
			}
			RuntimeResult result = MGFightSkillRuntimeCommon.basicAttack(fightSkill, caster, sprite, rate, 0);
			FightSkillResult skillResult = result.getData().getComponent(FightSkillResult.class)
			response.addSkillResult(skillResult)
		}

		GameSceneHelper.broadcastMessageToAOI(caster, response)
	}


	/**
	 * 半月剑法拓展1
	 * 半月剑法升级为全月剑法，打自己周围一圈的怪
	 */
	Closure<RuntimeResult> player_1_4_1_closure = {FightSkill fightSkill, FightSprite caster, FightSprite target ->
		byte direction = FightSkillRuntimeHelper.getDirection(caster.getCrtPosition(), target.getCrtPosition())
		Collection<FightSprite> fightSprites = FightSkillRuntimeHelper.getFullMoonSprites(caster, direction)
		return player_1_4_closure_base.call(fightSkill, caster, target, fightSprites)
	}

	/**
	 * 半月剑法拓展2
	 * 半月剑法升级为火特效，被击中的怪有30%会额外造成1次伤害。
	 * 额外造成1次伤害是指，本次攻击造成2次伤害
	 */
	Closure<RuntimeResult> player_1_4_2_closure = {FightSkill fightSkill, FightSprite caster, FightSprite target ->
		boolean secondAttack = SFRandomUtils.random100() <= 30
		if(secondAttack) {
			player_1_4_closure.call(fightSkill, caster, target)
		}

		return player_1_4_closure.call(fightSkill, caster, target)
	}


	/**
	 * 野蛮冲撞 ——主动朝向技能
	 * 以自身朝向为方向，向前方突进X格
	 * 路线上的目标会被向后推动
	 * 技能等级突进格数
	 * 如果目标等级>=自己等级，突进到目标身前为止，无法使目标后退
	 */
	Closure<RuntimeResult> player_1_5_closure = {FightSkill fightSkill, FightSprite caster, byte direction ->
		RuntimeResult result = null;
		MGFightProcessComponent<?> fightProcessComponent = (MGFightProcessComponent<?>) caster.getTagged(MGFightProcessComponent.Tag);

		PropertyDictionary effectPd = fightSkill.getLevelRef().getRuntimeParameter()
		int gridForward = MGPropertyAccesser.getMoveGridForward(effectPd)

		G2C_TriggerMultiTargetSkill response = new G2C_TriggerMultiTargetSkill(fightSkill.getRefId(), caster, direction)
		FightSkillResultImpl skillResult = new FightSkillResultImpl(0, caster, caster);

		Position casterPos = new Position(caster.getCrtPosition().getX(),caster.getCrtPosition().getY());

		// get target position caster can go to
		GameScene crtScene = caster.getCrtScene();
		Position targetPos = FightSkillRuntimeHelper.getForwardWalkablePosition(crtScene, direction, casterPos, gridForward)
		if (targetPos == null) {
			skillResult.addSkillEffect(new SkillEffectMove(caster, casterPos, caster.getCrtPosition()))
			response.addSkillResult(skillResult)
			GameSceneHelper.broadcastMessageToAOI(caster, response)
			return;
		}

		int playerLevel = caster.getLevel()
		Collection<FightSprite> spritesForward = GameSceneHelper.getFightSprites(caster.getCrtScene(), caster, casterPos, direction, 1, gridForward)
		Collection<FightSprite> hereSprites = GameSceneHelper.getFightSprites(caster.getCrtScene(), caster, casterPos, 0)
		ArrayList<FightSprite> sprites = new ArrayList<FightSprite>()
		sprites.addAll(spritesForward)
		sprites.addAll(hereSprites)
		for(FightSprite sprite : sprites) {
			if(sprite instanceof Player) {
				if(!fightProcessComponent.isValidAttackState(sprite, caster,MGFightProcessHelper.P_ATTACK)){
					continue;
				}
				if(((sprite instanceof Player && sprite.isInSafeRegion()) || sprite.equals(caster))) {
					continue;
				}
				int spriteLevel = sprite.getLevel()
				if(spriteLevel >= playerLevel) {
					byte backDirection = FightSkillRuntimeHelper.getBackDirection(direction)
					targetPos = GameSceneHelper.getForwardPosition(crtScene, backDirection, sprite.getCrtPosition(), 1)
					logger.debug("野蛮冲撞 : stoped by a player: " + sprite.getId())
				}
			}
		}

		// move all the fightSprites between casterPos and targetPos
		int distance = GameSceneHelper.distance(crtScene, casterPos, targetPos)
		Collection<FightSprite> fightSpritsForward = GameSceneHelper.getFightSprites(caster.getCrtScene(), caster, casterPos, direction, 1, distance)
		Collection<FightSprite> hereSprites2 = GameSceneHelper.getFightSprites(caster.getCrtScene(), caster, casterPos, 0)
		ArrayList<FightSprite> fightSprits = new ArrayList<FightSprite>()
		fightSprits.addAll(fightSpritsForward)
		fightSprits.addAll(hereSprites2)
		for(FightSprite sprite : fightSprits) {
			// player can't be pushed out of safeRegion
			// only regular monster can be crushed
			if(sprite instanceof Player && (sprite.isInSafeRegion() || sprite.getLevel() >= playerLevel)) {
				continue;
			}

			if(sprite instanceof Monster) {
				Monster monster = (Monster) sprite;
				if(!monster.getMonsterRef().isRegularMonster()) {
					continue;
				}

				if(!monster.getMonsterRef().canMove()) {
					continue;
				}
			}

			if(!fightProcessComponent.isValidAttackState(sprite, caster,MGFightProcessHelper.P_ATTACK)){
				continue;
			}
			Position originalPos = new Position(sprite.getCrtPosition().getX(),sprite.getCrtPosition().getY());
			logger.debug("野蛮冲撞  sprite: " + sprite.getId() + " orignal pos: " + originalPos)
			FightSkillResultImpl spriteSkillResult = new FightSkillResultImpl(0, caster, sprite);
			sprite.getPathComponent().silentMoveTo(targetPos)
			MGFightProcessHelper.sendAfterAttackGameEvent(caster, sprite, 0);
			Position spriteCurrentPosition = sprite.getCrtPosition()
			logger.debug("野蛮冲撞  sprite: " + sprite.getId() + " current pos: " + spriteCurrentPosition)
			spriteSkillResult.addSkillEffect(new SkillEffectMove(sprite, originalPos, spriteCurrentPosition))
			response.addSkillResult(spriteSkillResult);
			// for this sprite not to move in the same time with the effect of this skill
			sprite.getPerceiveComponent().updateLastMoveTime(System.currentTimeMillis())
		}

		logger.debug("野蛮冲撞  caster: " + caster.getId() + " start pos: " + casterPos + " targetPos: " + targetPos + " distance: " + distance + " direction: " + direction + " gridForward: " + gridForward)
		caster.getPathComponent().silentMoveTo(targetPos)
		skillResult.addSkillEffect(new SkillEffectMove(caster, casterPos, targetPos))
		response.addSkillResult(skillResult)
		GameSceneHelper.broadcastMessageToAOI(caster, response)

		return result;
	}

	/**
	 * 野蛮冲撞拓展1
	 * 野蛮冲撞由直线技能改为点对点技能，直接对目标冲撞撞中目标后，目标不会后退了，但会晕眩1秒晕眩：操作不能
	 */
	Closure<RuntimeResult> player_1_5_1_closure = {FightSkill fightSkill, FightSprite caster, FightSprite target ->

		// move caster
		FightSkillResultImpl skillResult = new FightSkillResultImpl(0, caster, target);
		G2C_TriggerSingleTargetSkill response = new G2C_TriggerSingleTargetSkill(fightSkill.getRefId(), caster, target, skillResult)

		byte direction = FightSkillRuntimeHelper.getDirection(target.getCrtPosition(), caster.getCrtPosition())
		Position originalPos = new Position(caster.getCrtPosition().getX(), caster.getCrtPosition().getY());
		byte targetToCastDirection = FightSkillRuntimeHelper.getDirection(target.getCrtPosition(), caster.getCrtPosition())
		Position casterPosition = GameSceneHelper.getForwardPosition(targetToCastDirection, target.getCrtPosition(), SceneAOILayer.AOIGRID_MULTIPLE)
		if (GameSceneHelper.isBlocked(target.getCrtScene(), casterPosition)) {
			casterPosition = target.getCrtPosition()
		}

		caster.getPathComponent().silentMoveTo(casterPosition)

		skillResult.addSkillEffect(new SkillEffectMove(caster, originalPos, casterPosition))

		GameSceneHelper.broadcastMessageToAOI(caster, response)
		MGFightProcessHelper.sendAfterAttackGameEvent(caster, target, 0);
		// make target dizzy
		int playerLevel = caster.getLevel()
		int spriteLevel = target.getLevel()

		if(spriteLevel < playerLevel) {
			String dizzyBuffId = "buff_state_8"
			MGFightSkillRuntimeCommon.addBuff(caster, target, dizzyBuffId, 1000)
		}
		// for this sprite not to move in the same time with the effect of this skill
		target.getPerceiveComponent().updateLastMoveTime(System.currentTimeMillis())

		return RuntimeResult.OK()
	}

	/**
	 * 野蛮冲撞拓展2
	 * 野蛮冲撞成功后，所有所冲撞到的目标受到150%的伤害
	 */
	Closure<RuntimeResult> player_1_5_2_closure = {FightSkill fightSkill, FightSprite caster, byte direction ->
		RuntimeResult result = RuntimeResult.OK()
		PropertyDictionary effectPd = fightSkill.getLevelRef().getRuntimeParameter()
		double rate = MGPropertyAccesser.getSkillDamageRate(effectPd) / 100.0
		int gridForward = MGPropertyAccesser.getMoveGridForward(effectPd)

		G2C_TriggerMultiTargetSkill response = new G2C_TriggerMultiTargetSkill(fightSkill.getRefId(), caster, direction)

		Position casterPos = new Position(caster.getCrtPosition().getX(),caster.getCrtPosition().getY())

		// get target position caster can go to
		GameScene crtScene = caster.getCrtScene();
		Position targetPos = FightSkillRuntimeHelper.getForwardWalkablePosition(crtScene, direction, casterPos, gridForward)
		if (targetPos == null) {
			return;
		}

		int playerLevel = caster.getLevel()
		Collection<FightSprite> sprites = GameSceneHelper.getFightSprites(caster.getCrtScene(), caster, casterPos, direction, 1, gridForward)
		for(FightSprite sprite : sprites) {
			if(sprite instanceof Player) {
				int spriteLevel = sprite.getLevel()
				if(spriteLevel >= playerLevel) {
					byte backDirection = FightSkillRuntimeHelper.getBackDirection(direction)
					targetPos = GameSceneHelper.getForwardPosition(crtScene, backDirection, sprite.getCrtPosition(), 1)
					logger.debug("野蛮冲撞拓展2 : stoped by a player: " + sprite.getId())
				}
			}
		}

		int distance = GameSceneHelper.distance(crtScene, casterPos, targetPos)
		Collection<FightSprite> fightSprits = GameSceneHelper.getFightSprites(caster.getCrtScene(), caster, casterPos, direction, 1, distance)
		logger.debug("野蛮冲撞拓展2 fightSprits " + fightSprits)
		for(FightSprite sprite : fightSprits) {
			// player can't be pushed out of safeRegion
			// only regular monster can be crushed
			// only monster that can be moved can be crushed
			if(sprite instanceof Player && sprite.isInSafeRegion()) {
				continue;
			}
			if(sprite instanceof Monster) {
				Monster monster = (Monster) sprite;
				if(!monster.getMonsterRef().isRegularMonster()) {
					continue;
				}
				//
				//				if(!monster.getMonsterRef().canMove()) {
				//					continue;
				//				}
			}
			MGFightProcessComponent<?> fightProcessComponent = (MGFightProcessComponent<?>) caster.getTagged(MGFightProcessComponent.Tag);
			if(!fightProcessComponent.isValidAttackState(sprite, caster,MGFightProcessHelper.P_ATTACK)){
				continue;
			}

			RuntimeResult resultAttack = MGFightSkillRuntimeCommon.basicAttack(fightSkill, caster, sprite, rate, 0)
			FightSkillResultImpl skillResultAttack = resultAttack.getData().getComponent(FightSkillResult.class)
			response.addSkillResult(skillResultAttack)

			Position orignalPos = new Position(sprite.getCrtPosition().getX(),sprite.getCrtPosition().getY());
			logger.debug("野蛮冲撞拓展2  sprite: " + sprite.getId() + " orignal pos: " + orignalPos)
			if(!sprite.isDead()){
				if(sprite instanceof Monster && ((Monster)sprite).getMonsterRef().canMove()) {
					sprite.getPathComponent().silentMoveTo(targetPos)
				} else {
					sprite.getPathComponent().silentMoveTo(targetPos)
				}
			}
			Position spriteCurrentPosition = sprite.getCrtPosition()
			logger.debug("野蛮冲撞拓展2  sprite: " + sprite.getId() + " current pos: " + spriteCurrentPosition)

			FightSkillResultImpl spriteSkillResult = new FightSkillResultImpl(0, caster, sprite);
			spriteSkillResult.addSkillEffect(new SkillEffectMove(sprite, orignalPos, spriteCurrentPosition))
			response.addSkillResult(spriteSkillResult);

			// for this sprite not to move in the same time with the effect of this skill
			sprite.getPerceiveComponent().updateLastMoveTime(System.currentTimeMillis())
		}

		logger.debug("野蛮冲撞拓展2  caster: " + caster.getId() + " start pos: " + casterPos + " targetPos: " + targetPos + " direction: " + direction + " gridForward: " + gridForward)
		caster.getPathComponent().silentMoveTo(targetPos)
		FightSkillResultImpl skillResult = new FightSkillResultImpl(0, caster, caster);
		skillResult.addSkillEffect(new SkillEffectMove(caster,casterPos, targetPos))
		response.addSkillResult(skillResult)
		GameSceneHelper.broadcastMessageToAOI(caster, response)
		logger.debug("野蛮冲撞拓展2 response " + response)
		return result;
	}

	/**
	 * 烈火剑法 ——主动精灵目标技能
	 * 使用后，下一次攻击必定触发该技能，使用以后进入到冷却时间，技能10秒触发都有效，超过10秒后则不会触发了
	 * 技能效果为对目标造成“X%+Y”点伤害
	 */
	Closure<RuntimeResult> player_1_6_closure = {FightSkill fightSkill, FightSprite caster, FightSprite target ->
		return player_1_6_closure_base.call(fightSkill, caster, target)
	}

	Closure<RuntimeResult> player_1_6_closure_base = {FightSkill fightSkill, FightSprite caster, FightSprite target ->

		PropertyDictionary effectPd = fightSkill.getLevelRef().getRuntimeParameter()
		int rateMin = MGPropertyAccesser.getSkillDamageRateMin(effectPd)
		int rateMax = MGPropertyAccesser.getSkillDamageRateMax(effectPd)
		int value = MGPropertyAccesser.getSkillDamage(effectPd)
		double rate = SFRandomUtils.random(rateMin, rateMax) / 100.0;

		RuntimeResult result = MGFightSkillRuntimeCommon.basicAttack(fightSkill, caster, target, rate, value);

		G2C_TriggerSingleTargetSkill response = new G2C_TriggerSingleTargetSkill(fightSkill.getRefId(), caster, target, result.getData().getComponent(
				FightSkillResult.class));
		GameSceneHelper.broadcastMessageToAOI(caster, response)

		return result;
	}

	/**
	 * 烈火剑法拓展1
	 * 烈火剑法冷却时间缩短4秒,只有6秒CD
	 */
	Closure<RuntimeResult> player_1_6_1_closure = {FightSkill fightSkill, FightSprite caster, FightSprite target ->
		return player_1_6_closure_base.call(fightSkill, caster, target)
	}

	/**
	 * 烈火剑法拓展2
	 * 烈火剑法升级为雷霆剑法（特效要加）被击中的目标有20%概率被麻痹2秒麻痹：操作无效，身体程序染色成灰色
	 */
	Closure<RuntimeResult> player_1_6_2_closure = {FightSkill fightSkill, FightSprite caster, FightSprite target ->


		boolean dumbed = SFRandomUtils.random100() <= 20
		if(dumbed) {
			String dumbedBuffId = "buff_state_9"
			MGFightSkillRuntimeCommon.addBuff(caster, target, dumbedBuffId, 2000)
		}

		return player_1_6_closure.call(fightSkill, caster, target)
	}
}


