package newbee.morningGlory.mmorpg.sprite.player.fightSkill

import newbee.morningGlory.MorningGloryContext
import newbee.morningGlory.mmorpg.sprite.buff.MGFightSpriteBuff
import newbee.morningGlory.mmorpg.sprite.buff.MGFightSpriteBuffComponent
import newbee.morningGlory.mmorpg.sprite.buff.MGFightSpriteBuffRef
import newbee.morningGlory.mmorpg.union.MGUnionHelper
import newbee.morningGlory.mmorpg.union.MGUnionMgr

import org.apache.log4j.Logger

import sophia.foundation.property.PropertyDictionary
import sophia.game.GameRoot
import sophia.mmorpg.base.scene.GameSceneHelper
import sophia.mmorpg.base.sprite.FightSprite
import sophia.mmorpg.base.sprite.fightSkill.FightSkill
import sophia.mmorpg.base.sprite.fightSkill.FightSkillResult
import sophia.mmorpg.base.sprite.fightSkill.FightSkillRuntimeHelper
import sophia.mmorpg.monster.Monster
import sophia.mmorpg.player.Player
import sophia.mmorpg.player.fightSkill.event.G2C_TriggerMultiTargetSkill
import sophia.mmorpg.player.fightSkill.event.G2C_TriggerSingleTargetSkill
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser
import sophia.mmorpg.utils.RuntimeResult
import sophia.mmorpg.utils.SFRandomUtils

class MGFightSkillRuntime_Monster {
	private static final Logger logger = Logger.getLogger(MGFightSkillRuntime_Player_0.class)
	private final Map<String, Closure<RuntimeResult>> map = new HashMap<>();

	public Map<String, Closure<RuntimeResult>> getClosures() {
		return map;
	}

	MGFightSkillRuntime_Monster() {
		map.put("skill_1", monster_1_closure);
		map.put("skill_2", monster_2_closure);
		map.put("skill_3", monster_3_closure);
		map.put("skill_4", monster_4_closure);
		map.put("skill_5", monster_5_closure);
		map.put("skill_6", monster_6_closure);
		map.put("skill_7", monster_7_closure);
		map.put("skill_8", monster_8_closure);
		map.put("skill_9", monster_9_closure);
		map.put("skill_10", monster_10_closure);
		map.put("skill_11", monster_11_closure);
		map.put("skill_12", monster_12_closure);
		map.put("skill_13", monster_13_closure);
		map.put("skill_14", monster_14_closure);
		map.put("skill_15", monster_15_closure);
		map.put("skill_16", monster_16_closure);
		map.put("skill_17", monster_17_closure);
		map.put("skill_18", monster_18_closure);
		map.put("skill_19", monster_19_closure);
		map.put("skill_20", monster_20_closure);
		map.put("skill_21", monster_21_closure);
		map.put("skill_22", monster_12_closure);
	}

	/**
	 * 掷斧头（掷斧骷髅专用，暂无特效）
	 * 
	 * 远程物攻,对目标造成105%+10的物理伤害
	 */
	Closure<RuntimeResult> monster_1_closure = {FightSkill fightSkill, FightSprite caster, FightSprite target ->
		PropertyDictionary runtimeParameter = fightSkill.getLevelRef().getRuntimeParameter()
		int skillDamage = MGPropertyAccesser.getSkillDamage(runtimeParameter)
		if (skillDamage < 0) {
			skillDamage = 0;
		}
		double skillDamageRate = MGPropertyAccesser.getSkillDamageRate(runtimeParameter) / 100.0

		RuntimeResult result = MGFightSkillRuntimeCommon.basicAttack(fightSkill, caster, target, skillDamageRate, skillDamage)
		G2C_TriggerSingleTargetSkill response = new G2C_TriggerSingleTargetSkill(fightSkill.getRefId(), caster, target, result.getData().getComponent(FightSkillResult.class));
		GameSceneHelper.broadcastMessageToAOI(caster, response)
		return result;
	}

	/**
	 * 吐刺（暗黑战士吐出一根刺，暂无特效）
	 * 
	 * 远程物攻,对目标造成110%的物理伤害
	 */
	Closure<RuntimeResult> monster_2_closure = {FightSkill fightSkill, FightSprite caster, FightSprite target ->
		PropertyDictionary runtimeParameter = fightSkill.getLevelRef().getRuntimeParameter()
		int skillDamage = MGPropertyAccesser.getSkillDamage(runtimeParameter);
		if (skillDamage < 0) {
			skillDamage = 0;
		}
		double skillDamageRate = MGPropertyAccesser.getSkillDamageRate(runtimeParameter) / 100.0

		RuntimeResult result = MGFightSkillRuntimeCommon.basicAttack(fightSkill, caster, target, skillDamageRate, skillDamage)
		G2C_TriggerSingleTargetSkill response = new G2C_TriggerSingleTargetSkill(fightSkill.getRefId(), caster, target, result.getData().getComponent(FightSkillResult.class));
		GameSceneHelper.broadcastMessageToAOI(caster, response)
		return result;
	}

	/**
	 * 三味真火(小火球)
	 *
	 * 远程物攻,对目标造成100%+10魔法伤害
	 */
	Closure<RuntimeResult> monster_3_closure = {FightSkill fightSkill, FightSprite caster, FightSprite target ->
		PropertyDictionary runtimeParameter = fightSkill.getLevelRef().getRuntimeParameter()
		int skillDamage = MGPropertyAccesser.getSkillDamage(runtimeParameter);
		if (skillDamage < 0) {
			skillDamage = 0;
		}
		double skillDamageRate = MGPropertyAccesser.getSkillDamageRate(runtimeParameter) / 100.0
		RuntimeResult result = MGFightSkillRuntimeCommon.basicAttack(fightSkill, caster, target, skillDamageRate, skillDamage)

		G2C_TriggerSingleTargetSkill response = new G2C_TriggerSingleTargetSkill(fightSkill.getRefId(), caster, target, result.getData().getComponent(FightSkillResult.class));
		GameSceneHelper.broadcastMessageToAOI(caster, response)
		return result;
	}

	/**
	 * 惊雷闪
	 * 
	 * 对目标造成110%魔法伤害
	 */
	Closure<RuntimeResult> monster_4_closure = {FightSkill fightSkill, FightSprite caster, FightSprite target ->
		PropertyDictionary runtimeParameter = fightSkill.getLevelRef().getRuntimeParameter()
		int skillDamage = MGPropertyAccesser.getSkillDamage(runtimeParameter);
		if (skillDamage < 0) {
			skillDamage = 0;
		}
		double skillDamageRate = MGPropertyAccesser.getSkillDamageRate(runtimeParameter) / 100.0

		RuntimeResult result = MGFightSkillRuntimeCommon.basicAttack(fightSkill, caster, target, skillDamageRate, skillDamage)
		G2C_TriggerSingleTargetSkill response = new G2C_TriggerSingleTargetSkill(fightSkill.getRefId(), caster, target, result.getData().getComponent(FightSkillResult.class));
		GameSceneHelper.broadcastMessageToAOI(caster, response)
		return result;
	}

	/**
	 * 爆裂火焰
	 * 
	 * 对目标以及以目标为中心的3*3范围内的所有目标造成50%点法术伤害
	 */
	Closure<RuntimeResult> monster_5_closure = {FightSkill fightSkill, FightSprite caster, FightSprite target ->
		int row = 3; int column = 3;
		Collection<FightSprite> fightSprites = GameSceneHelper.getFightSprites(target.getCrtScene(), caster, target.getCrtPosition(), row, column)
		RuntimeResult result = RuntimeResult.OK();
		G2C_TriggerMultiTargetSkill response = new G2C_TriggerMultiTargetSkill(fightSkill.getRefId(), caster, target.getCrtPosition())

		PropertyDictionary runtimeParameter = fightSkill.getLevelRef().getRuntimeParameter()
		int skillDamage = MGPropertyAccesser.getSkillDamage(runtimeParameter);
		if (skillDamage < 0) {
			skillDamage = 0;
		}
		double skillDamageRate = MGPropertyAccesser.getSkillDamageRate(runtimeParameter) / 100.0
		for(FightSprite sprite : fightSprites) {
			if (((Monster)caster).getMonsterRef().isRegularMonster()) {
				if(sprite instanceof Monster && ((Monster)sprite).getMonsterRef().isRegularMonster()) {
					continue;
				}
			}
			if (sprite instanceof Player) {
				if (sprite.isInSafeRegion()) {
					continue;
				}
				boolean isSameUnion = MGUnionHelper.isInTheSameUnion(sprite, caster);
				if (isSameUnion) {
					continue;
				}
			}
			result = MGFightSkillRuntimeCommon.basicAttack(fightSkill, caster, sprite, skillDamageRate, skillDamage)
			FightSkillResult skillResult = result.getData().getComponent(FightSkillResult.class)
			response.addSkillResult(skillResult)
		}
		GameSceneHelper.broadcastMessageToAOI(caster, response)
		return result
	}

	/**
	 * 疾光电影
	 * 
	 * 以自身朝向为方向，向前方6格范围施放一个雷电柱对目标造成105%点法术伤害
	 */
	Closure<RuntimeResult> monster_6_closure = {FightSkill fightSkill, FightSprite caster, byte direction ->
		PropertyDictionary runtimeParameter = fightSkill.getLevelRef().getRuntimeParameter()
		int skillDamage = MGPropertyAccesser.getSkillDamage(runtimeParameter);
		if (skillDamage < 0) {
			skillDamage = 0;
		}
		double skillDamageRate = MGPropertyAccesser.getSkillDamageRate(runtimeParameter) / 100.0
		RuntimeResult result = RuntimeResult.OK();
		G2C_TriggerMultiTargetSkill response = new G2C_TriggerMultiTargetSkill(fightSkill.getRefId(), caster, direction)

		Collection<FightSprite> sprites = GameSceneHelper.getFightSprites(caster.getCrtScene(), caster, caster.getCrtPosition(), (byte) direction, 1, 6)
		for(FightSprite sprite : sprites) {
			if (((Monster)caster).getMonsterRef().isRegularMonster()) {
				if(sprite instanceof Monster && ((Monster)sprite).getMonsterRef().isRegularMonster()) {
					continue;
				}
			}
			if (sprite instanceof Player) {
				if (sprite.isInSafeRegion()) {
					continue;
				}
				boolean isSameUnion = MGUnionHelper.isInTheSameUnion(sprite, caster);
				if (isSameUnion) {
					continue;
				}
			}
			result = MGFightSkillRuntimeCommon.basicAttack(fightSkill, caster, sprite, skillDamageRate, skillDamage)
			FightSkillResult skillResult = result.getData().getComponent(FightSkillResult.class)
			response.addSkillResult(skillResult)
		}
		GameSceneHelper.broadcastMessageToAOI(caster, response)
		return result
	}

	/**
	 * 地狱雷光
	 * 
	 * 以自身为中心施放，作用范围为5*5对敌方目标造成40%点法术伤害
	 */
	Closure<RuntimeResult> monster_7_closure = {FightSkill fightSkill, FightSprite caster, byte direction ->
		int row = 5; int column = 5;
		Collection<FightSprite> fightSprites = GameSceneHelper.getFightSprites(caster.getCrtScene(), caster, caster.getCrtPosition(), row, column)
		RuntimeResult result = RuntimeResult.OK();
		G2C_TriggerMultiTargetSkill response = new G2C_TriggerMultiTargetSkill(fightSkill.getRefId(), caster, direction)
		PropertyDictionary runtimeParameter = fightSkill.getLevelRef().getRuntimeParameter()
		int skillDamage = MGPropertyAccesser.getSkillDamage(runtimeParameter);
		if (skillDamage < 0) {
			skillDamage = 0;
		}
		double skillDamageRate = MGPropertyAccesser.getSkillDamageRate(runtimeParameter) / 100.0
		for(FightSprite sprite : fightSprites) {
			if (((Monster)caster).getMonsterRef().isRegularMonster()) {
				if(sprite instanceof Monster && ((Monster)sprite).getMonsterRef().isRegularMonster()) {
					continue;
				}
			}
			if (sprite instanceof Player) {
				if (sprite.isInSafeRegion()) {
					continue;
				}
				boolean isSameUnion = MGUnionHelper.isInTheSameUnion(sprite, caster);
				if (isSameUnion) {
					continue;
				}
				if (!((Monster)caster).getMonsterRef().isRegularMonster() && ((Monster)caster).getOwner() != null) {
					if (sprite.equals(((Monster)caster).getOwner())) {
						continue;
					}
				}
			}
			result = MGFightSkillRuntimeCommon.basicAttack(fightSkill, caster, sprite, skillDamageRate, skillDamage)
			FightSkillResult skillResult = result.getData().getComponent(FightSkillResult.class)
			response.addSkillResult(skillResult)
		}
		GameSceneHelper.broadcastMessageToAOI(caster, response)
		return result
	}

	/**
	 * 冰咆哮
	 * 
	 * 对目标以及以目标为中心的3*3范围内的所有目标造成60%点法术伤害
	 */
	Closure<RuntimeResult> monster_8_closure = {FightSkill fightSkill, FightSprite caster, FightSprite target ->
		int row = 3; int column = 3;
		Collection<FightSprite> fightSprites = GameSceneHelper.getFightSprites(target.getCrtScene(), caster, target.getCrtPosition(), row, column)
		RuntimeResult result = RuntimeResult.OK();
		G2C_TriggerMultiTargetSkill response = new G2C_TriggerMultiTargetSkill(fightSkill.getRefId(), caster, target.getCrtPosition())

		PropertyDictionary runtimeParameter = fightSkill.getLevelRef().getRuntimeParameter()
		int skillDamage = MGPropertyAccesser.getSkillDamage(runtimeParameter);
		if (skillDamage < 0) {
			skillDamage = 0;
		}
		double skillDamageRate = MGPropertyAccesser.getSkillDamageRate(runtimeParameter) / 100.0
		for(FightSprite sprite : fightSprites) {
			if (((Monster)caster).getMonsterRef().isRegularMonster()) {
				if(sprite instanceof Monster && ((Monster)sprite).getMonsterRef().isRegularMonster()) {
					continue;
				}
			}
			if (sprite instanceof Player) {
				if (sprite.isInSafeRegion()) {
					continue;
				}
				boolean isSameUnion = MGUnionHelper.isInTheSameUnion(sprite, caster);
				if (isSameUnion) {
					continue;
				}
			}
			result = MGFightSkillRuntimeCommon.basicAttack(fightSkill, caster, sprite, skillDamageRate, skillDamage)
			FightSkillResult skillResult = result.getData().getComponent(FightSkillResult.class)
			response.addSkillResult(skillResult)
		}
		GameSceneHelper.broadcastMessageToAOI(caster, response)
		return result
	}

	/**
	 * 毒蛊术（近战魔攻，给中毒对象附加中毒效果）
	 * 
	 * 魔法攻击,100%魔法伤害,30%概率使目标进入中毒状态,使目标每2秒减少本次道术取值*5%点生命，持续时间=10+本次道术取值/1000秒，最长20秒
	 * 多个毒蛊术作用于一个目标身上时，后者会替换前者的效果
	 */
	Closure<RuntimeResult> monster_9_closure = {FightSkill fightSkill, FightSprite caster, FightSprite target ->
		PropertyDictionary runtimeParameter = fightSkill.getLevelRef().getRuntimeParameter()
		int skillDamage = MGPropertyAccesser.getSkillDamage(runtimeParameter);
		if (skillDamage < 0) {
			skillDamage = 0;
		}
		double skillDamageRate = MGPropertyAccesser.getSkillDamageRate(runtimeParameter) / 100.0
		RuntimeResult result = MGFightSkillRuntimeCommon.basicAttack(fightSkill, caster, target, skillDamageRate, skillDamage)

		double skillDamageRate1 = MGPropertyAccesser.getSkillDamageRate1(runtimeParameter) / 100.0
		int skillDamageChance = MGPropertyAccesser.getSkillDamageChance(runtimeParameter)
		int skillCo = MGPropertyAccesser.getSkillCo(runtimeParameter)
		int random = SFRandomUtils.random(1, 100)

		if (random <= skillDamageChance) {
			PropertyDictionary pd = caster.getFightPropertyMgrComponent().getFightPropertyMgr().getSnapshotByNew().getPropertyDictionary()
			int maxMAtk = MGPropertyAccesser.getMaxMAtk(pd)
			int minMAtk = MGPropertyAccesser.getMinMAtk(pd)
			int attack = maxMAtk <= minMAtk ? maxMAtk :  SFRandomUtils.random(minMAtk, maxMAtk)
			int lastTime = 10 + attack / skillCo;
			lastTime = lastTime < 20 ? lastTime : 20;
			lastTime = lastTime * 1000;

			int eachTimekill = attack * skillDamageRate1
			String buffRefId = "buff_state_5";

			MGFightSpriteBuffComponent fightSpriteBuffComponent = (MGFightSpriteBuffComponent) target.getTagged(MGFightSpriteBuffComponent.Tag);

			MGFightSpriteBuffRef ref = (MGFightSpriteBuffRef) GameRoot.getGameRefObjectManager().getManagedObject(buffRefId)
			MGFightSpriteBuff buff = new MGFightSpriteBuff(ref, caster, target, lastTime)
			MGPropertyAccesser.setOrPutHP(buff.getSpecialProperty(), eachTimekill)
			RuntimeResult result_1 = fightSpriteBuffComponent.getFightSpriteBuffMgr().attachFightSpriteBuff(buff)
		}
		G2C_TriggerSingleTargetSkill response = new G2C_TriggerSingleTargetSkill(fightSkill.getRefId(), caster, target, result.getData().getComponent(FightSkillResult.class));
		GameSceneHelper.broadcastMessageToAOI(caster, response)
		return result;
	}

	/**
	 * 麻痹术（近战魔攻，给麻痹对象附加麻痹效果）
	 * 
	 * 魔法攻击,90%魔法伤害,20%概率使目标进入麻痹状态，使目标操作不能，身体程序染色，持续时间=2+本次道术取值/1000秒，最长20秒
	 * 多个麻痹术作用于一个目标身上时，后者会替换前者的效果
	 */
	Closure<RuntimeResult> monster_10_closure = {FightSkill fightSkill, FightSprite caster, FightSprite target ->
		PropertyDictionary runtimeParameter = fightSkill.getLevelRef().getRuntimeParameter()
		int skillDamage = MGPropertyAccesser.getSkillDamage(runtimeParameter);
		if (skillDamage < 0) {
			skillDamage = 0;
		}
		double skillDamageRate = MGPropertyAccesser.getSkillDamageRate(runtimeParameter) / 100.0
		RuntimeResult result = MGFightSkillRuntimeCommon.basicAttack(fightSkill, caster, target, skillDamageRate, skillDamage)

		int skillDamageChance = MGPropertyAccesser.getSkillDamageChance(runtimeParameter)
		int skillCo = MGPropertyAccesser.getSkillCo(runtimeParameter)

		int random = SFRandomUtils.random(1, 100)
		if (random <= skillDamageChance) {
			PropertyDictionary pd = caster.getFightPropertyMgrComponent().getFightPropertyMgr().getSnapshotByNew().getPropertyDictionary()
			int maxMAtk = MGPropertyAccesser.getMaxMAtk(pd)
			int minMAtk = MGPropertyAccesser.getMinMAtk(pd)
			int attack = maxMAtk <= minMAtk ? maxMAtk :  SFRandomUtils.random(minMAtk, maxMAtk)
			int lastTime = 2 + attack / skillCo;
			lastTime = lastTime < 20 ? lastTime : 20;
			lastTime = lastTime * 1000;

			String buffRefId = "buff_state_9";

			MGFightSpriteBuffComponent fightSpriteBuffComponent = (MGFightSpriteBuffComponent) target.getTagged(MGFightSpriteBuffComponent.Tag);

			MGFightSpriteBuffRef ref = (MGFightSpriteBuffRef) GameRoot.getGameRefObjectManager().getManagedObject(buffRefId)
			MGFightSpriteBuff buff = new MGFightSpriteBuff(ref, caster, target, lastTime)
			RuntimeResult result_1 = fightSpriteBuffComponent.getFightSpriteBuffMgr().attachFightSpriteBuff(buff)
		}

		G2C_TriggerSingleTargetSkill response = new G2C_TriggerSingleTargetSkill(fightSkill.getRefId(), caster, target, result.getData().getComponent(FightSkillResult.class));
		GameSceneHelper.broadcastMessageToAOI(caster, response)

		return result;
	}

	/**
	 * 沙虫·中毒buff·喷射
	 * 
	 * 近战魔攻，最终喷出毒液,造成100%魔法伤害,skillDamageChance%概率使目标进入中毒状态,
	 * 使目标每2秒减少本次道术取值*skillDamageRate%点生命，持续时间=10+本次道术取值/skillCo秒，最长20秒
	 * 多个中毒作用于一个目标身上时，后者会替换前者的效果
	 */
	Closure<RuntimeResult> monster_11_closure = {FightSkill fightSkill, FightSprite caster, FightSprite target ->
		PropertyDictionary runtimeParameter = fightSkill.getLevelRef().getRuntimeParameter()
		int skillDamage = MGPropertyAccesser.getSkillDamage(runtimeParameter);
		if (skillDamage < 0) {
			skillDamage = 0;
		}
		double skillDamageRate = MGPropertyAccesser.getSkillDamageRate(runtimeParameter) / 100.0
		RuntimeResult result = MGFightSkillRuntimeCommon.basicAttack(fightSkill, caster, target, skillDamageRate, skillDamage)

		double skillDamageRate1 = MGPropertyAccesser.getSkillDamageRate1(runtimeParameter) / 100.0
		int skillDamageChance = MGPropertyAccesser.getSkillDamageChance(runtimeParameter)
		int skillCo = MGPropertyAccesser.getSkillCo(runtimeParameter)
		int random = SFRandomUtils.random(1, 100)

		if (random <= skillDamageChance) {
			PropertyDictionary pd = caster.getFightPropertyMgrComponent().getFightPropertyMgr().getSnapshotByNew().getPropertyDictionary()
			int maxMAtk = MGPropertyAccesser.getMaxMAtk(pd)
			int minMAtk = MGPropertyAccesser.getMinMAtk(pd)
			int attack = maxMAtk <= minMAtk ? maxMAtk :  SFRandomUtils.random(minMAtk, maxMAtk)
			int lastTime = 10 + attack / skillCo;
			lastTime = lastTime < 20 ? lastTime : 20;
			lastTime = lastTime * 1000;

			int eachTimekill = attack * skillDamageRate1
			String buffRefId = "buff_state_5";

			MGFightSpriteBuffComponent fightSpriteBuffComponent = (MGFightSpriteBuffComponent) target.getTagged(MGFightSpriteBuffComponent.Tag);

			MGFightSpriteBuffRef ref = (MGFightSpriteBuffRef) GameRoot.getGameRefObjectManager().getManagedObject(buffRefId)
			MGFightSpriteBuff buff = new MGFightSpriteBuff(ref, caster, target, lastTime)
			MGPropertyAccesser.setOrPutHP(buff.getSpecialProperty(), eachTimekill)
			RuntimeResult result_1 = fightSpriteBuffComponent.getFightSpriteBuffMgr().attachFightSpriteBuff(buff)
		}
		G2C_TriggerSingleTargetSkill response = new G2C_TriggerSingleTargetSkill(fightSkill.getRefId(), caster, target, result.getData().getComponent(FightSkillResult.class));
		GameSceneHelper.broadcastMessageToAOI(caster, response)
		return result;
	}

	/**
	 * 普通魔攻
	 * 
	 * 造成skillDamageRate%的魔法伤害
	 */
	Closure<RuntimeResult> monster_12_closure = {FightSkill fightSkill, FightSprite caster, FightSprite target ->
		PropertyDictionary runtimeParameter = fightSkill.getLevelRef().getRuntimeParameter()
		int skillDamage = MGPropertyAccesser.getSkillDamage(runtimeParameter);
		if (skillDamage < 0) {
			skillDamage = 0;
		}
		double skillDamageRate = MGPropertyAccesser.getSkillDamageRate(runtimeParameter) / 100.0

		RuntimeResult result = MGFightSkillRuntimeCommon.basicAttack(fightSkill, caster, target, skillDamageRate, skillDamage)
		G2C_TriggerSingleTargetSkill response = new G2C_TriggerSingleTargetSkill(fightSkill.getRefId(), caster, target, result.getData().getComponent(FightSkillResult.class));
		GameSceneHelper.broadcastMessageToAOI(caster, response)
	}

	/**
	 * 飞蛾·麻痹buff·喷射
	 * 
	 * 近战魔攻,喷东西，造成skillDamageRate%魔法伤害,skillDamageChance%概率使目标进入麻痹状态，
	 * 使目标操作不能，身体程序染色，持续时间=2+本次道术取值/skillCo秒，最长20秒
	 * 多个麻痹作用于一个目标身上时，后者会替换前者的效果
	 */
	Closure<RuntimeResult> monster_13_closure = {FightSkill fightSkill, FightSprite caster, FightSprite target ->
		PropertyDictionary runtimeParameter = fightSkill.getLevelRef().getRuntimeParameter()
		int skillDamage = MGPropertyAccesser.getSkillDamage(runtimeParameter);
		if (skillDamage < 0) {
			skillDamage = 0;
		}
		double skillDamageRate = MGPropertyAccesser.getSkillDamageRate(runtimeParameter) / 100.0
		RuntimeResult result = MGFightSkillRuntimeCommon.basicAttack(fightSkill, caster, target, skillDamageRate, skillDamage)

		int skillDamageChance = MGPropertyAccesser.getSkillDamageChance(runtimeParameter)
		int skillCo = MGPropertyAccesser.getSkillCo(runtimeParameter)

		int random = SFRandomUtils.random(1, 100)
		if (random <= skillDamageChance) {
			PropertyDictionary pd = caster.getFightPropertyMgrComponent().getFightPropertyMgr().getSnapshotByNew().getPropertyDictionary()
			int maxMAtk = MGPropertyAccesser.getMaxMAtk(pd)
			int minMAtk = MGPropertyAccesser.getMinMAtk(pd)
			int attack = maxMAtk <= minMAtk ? maxMAtk :  SFRandomUtils.random(minMAtk, maxMAtk)
			int lastTime = 2 + attack / skillCo;
			lastTime = lastTime < 20 ? lastTime : 20;
			lastTime = lastTime * 1000;

			String buffRefId = "buff_state_9";

			MGFightSpriteBuffComponent fightSpriteBuffComponent = (MGFightSpriteBuffComponent) target.getTagged(MGFightSpriteBuffComponent.Tag);

			MGFightSpriteBuffRef ref = (MGFightSpriteBuffRef) GameRoot.getGameRefObjectManager().getManagedObject(buffRefId)
			MGFightSpriteBuff buff = new MGFightSpriteBuff(ref, caster, target, lastTime)
			RuntimeResult result_1 = fightSpriteBuffComponent.getFightSpriteBuffMgr().attachFightSpriteBuff(buff)
		}

		G2C_TriggerSingleTargetSkill response = new G2C_TriggerSingleTargetSkill(fightSkill.getRefId(), caster, target, result.getData().getComponent(FightSkillResult.class));
		GameSceneHelper.broadcastMessageToAOI(caster, response)

		return result;
	}

	/**
	 * 祖玛教主·普通魔攻 
	 * 
	 * 造成skillDamageRate%的魔法伤害
	 */
	Closure<RuntimeResult> monster_14_closure = {FightSkill fightSkill, FightSprite caster, FightSprite target ->
		PropertyDictionary runtimeParameter = fightSkill.getLevelRef().getRuntimeParameter()
		int skillDamage = MGPropertyAccesser.getSkillDamage(runtimeParameter)
		if (skillDamage < 0) {
			skillDamage = 0;
		}
		double skillDamageRate = MGPropertyAccesser.getSkillDamageRate(runtimeParameter) / 100.0

		RuntimeResult result = MGFightSkillRuntimeCommon.basicAttack(fightSkill, caster, target, skillDamageRate, skillDamage)
		G2C_TriggerSingleTargetSkill response = new G2C_TriggerSingleTargetSkill(fightSkill.getRefId(), caster, target, result.getData().getComponent(FightSkillResult.class));
		GameSceneHelper.broadcastMessageToAOI(caster, response)
	}

	/**
	 * 触龙神·中毒buff· 辐射
	 * 
	 * 范围魔法攻击，攻击自己正面左上6*6范围敌人，造成skillDamageRate%魔法伤害，伤害范围如下图中蓝色部分，红色为怪物所在格.
	 * 并有skillDamageChance%概率使目标进入中毒状态,使目标每2秒减少本次道术取值*skillDamageRate1%点生命，
	 * 持续时间=10+本次道术取值/skillCo秒，最长20秒
	 * 多个中毒作用于一个目标身上时，后者会替换前者的效果
	 */
	Closure<RuntimeResult> monster_15_closure = {FightSkill fightSkill, FightSprite caster,  byte direction ->
		byte Down_Dicretion = 2;
		byte Right_Direction = 0;
		PropertyDictionary runtimeParameter = fightSkill.getLevelRef().getRuntimeParameter()
		int skillDamage = MGPropertyAccesser.getSkillDamage(runtimeParameter);
		if (skillDamage < 0) {
			skillDamage = 0;
		}
		double skillDamageRate = MGPropertyAccesser.getSkillDamageRate(runtimeParameter) / 100.0
		double skillDamageRate1 = MGPropertyAccesser.getSkillDamageRate1(runtimeParameter) / 100.0
		int skillDamageChance = MGPropertyAccesser.getSkillDamageChance(runtimeParameter)
		int skillCo = MGPropertyAccesser.getSkillCo(runtimeParameter)
		G2C_TriggerMultiTargetSkill response = new G2C_TriggerMultiTargetSkill(fightSkill.getRefId(), caster, direction)

		int row = 7; int column = 7;
		byte leftDirection = FightSkillRuntimeHelper.getLeftDirection(direction)
		Collection<FightSprite> spriteCollection = GameSceneHelper.getFightSprites(caster.getCrtScene(), caster, caster.getCrtPosition(), Down_Dicretion, row*2-1, column)
		Collection<FightSprite> spriteCollection1 = GameSceneHelper.getFightSprites(caster.getCrtScene(), caster, caster.getCrtPosition(), Right_Direction, row*2-1, column)
		Collection<FightSprite> spriteCollection2 = GameSceneHelper.getFightSprites(caster.getCrtScene(), caster, caster.getCrtPosition(), 1);
		
		Collection<FightSprite> spriteCollection3 = GameSceneHelper.getFightSprites(caster.getCrtScene(), caster, caster.getCrtPosition(), Down_Dicretion, 1, column);
		Collection<FightSprite> spriteCollection4 = GameSceneHelper.getFightSprites(caster.getCrtScene(), caster, caster.getCrtPosition(), Right_Direction, 1, column);
		
		List<FightSprite> collection = spriteCollection2;
		collection.addAll(spriteCollection3)
		collection.addAll(spriteCollection4)
		
		for (FightSprite sprite1 : spriteCollection) {
			if (spriteCollection1.contains(sprite1)) {
				collection.add(sprite1);
			}
		}
		for (FightSprite target : collection) {
			if (((Monster)caster).getMonsterRef().isRegularMonster()) {
				if(target instanceof Monster && ((Monster)target).getMonsterRef().isRegularMonster()) {
					continue;
				}
			}
			if (target instanceof Player) {
				if (target.isInSafeRegion()) {
					continue;
				}
				boolean isSameUnion = MGUnionHelper.isInTheSameUnion(caster, target);
				if (isSameUnion) {
					continue;
				}
			}
			RuntimeResult result = MGFightSkillRuntimeCommon.basicAttack(fightSkill, caster, target, skillDamageRate, skillDamage)
			FightSkillResult skillResult = result.getData().getComponent(FightSkillResult.class)
			response.addSkillResult(skillResult)

			int random = SFRandomUtils.random(1, 100)
			if (random <= skillDamageChance) {
				PropertyDictionary pd = caster.getFightPropertyMgrComponent().getFightPropertyMgr().getSnapshotByNew().getPropertyDictionary()
				int maxMAtk = MGPropertyAccesser.getMaxMAtk(pd)
				int minMAtk = MGPropertyAccesser.getMinMAtk(pd)
				int attack = maxMAtk <= minMAtk ? maxMAtk :  SFRandomUtils.random(minMAtk, maxMAtk)
				int lastTime = 10 + attack / skillCo;
				lastTime = lastTime < 20 ? lastTime : 20;
				lastTime = lastTime * 1000;

				int eachTimekill = attack * skillDamageRate1
				String buffRefId = "buff_state_5";

				MGFightSpriteBuffComponent fightSpriteBuffComponent = (MGFightSpriteBuffComponent) target.getTagged(MGFightSpriteBuffComponent.Tag);

				MGFightSpriteBuffRef ref = (MGFightSpriteBuffRef) GameRoot.getGameRefObjectManager().getManagedObject(buffRefId)
				MGFightSpriteBuff buff = new MGFightSpriteBuff(ref, caster, target, lastTime)
				MGPropertyAccesser.setOrPutHP(buff.getSpecialProperty(), eachTimekill)
				RuntimeResult result_1 = fightSpriteBuffComponent.getFightSpriteBuffMgr().attachFightSpriteBuff(buff)
			}
		}
		GameSceneHelper.broadcastMessageToAOI(caster, response)
	}

	/**
	 * 月魔蜘蛛·麻痹buff·喷射 
	 * 
	 * 近战魔攻,喷东西，造成skillDamageRate%魔法伤害,skillDamageChance%概率使目标进入麻痹状态，
	 * 使目标操作不能，身体程序染色，持续时间=2+本次道术取值/skillCo秒，最长20秒
	 * 多个麻痹作用于一个目标身上时，后者会替换前者的效果
	 */
	Closure<RuntimeResult> monster_16_closure = {FightSkill fightSkill, FightSprite caster, FightSprite target ->
		PropertyDictionary runtimeParameter = fightSkill.getLevelRef().getRuntimeParameter()
		int skillDamage = MGPropertyAccesser.getSkillDamage(runtimeParameter);
		if (skillDamage < 0) {
			skillDamage = 0;
		}
		double skillDamageRate = MGPropertyAccesser.getSkillDamageRate(runtimeParameter) / 100.0
		RuntimeResult result = MGFightSkillRuntimeCommon.basicAttack(fightSkill, caster, target, skillDamageRate, skillDamage)

		int skillDamageChance = MGPropertyAccesser.getSkillDamageChance(runtimeParameter)
		int skillCo = MGPropertyAccesser.getSkillCo(runtimeParameter)

		int random = SFRandomUtils.random(1, 100)
		if (random <= skillDamageChance) {
			PropertyDictionary pd = caster.getFightPropertyMgrComponent().getFightPropertyMgr().getSnapshotByNew().getPropertyDictionary()
			int maxMAtk = MGPropertyAccesser.getMaxMAtk(pd)
			int minMAtk = MGPropertyAccesser.getMinMAtk(pd)
			int attack = maxMAtk <= minMAtk ? maxMAtk :  SFRandomUtils.random(minMAtk, maxMAtk)
			int lastTime = 2 + attack / skillCo;
			lastTime = lastTime < 20 ? lastTime : 20;
			lastTime = lastTime * 1000;

			String buffRefId = "buff_state_9";

			MGFightSpriteBuffComponent fightSpriteBuffComponent = (MGFightSpriteBuffComponent) target.getTagged(MGFightSpriteBuffComponent.Tag);

			MGFightSpriteBuffRef ref = (MGFightSpriteBuffRef) GameRoot.getGameRefObjectManager().getManagedObject(buffRefId)
			MGFightSpriteBuff buff = new MGFightSpriteBuff(ref, caster, target, lastTime)
			RuntimeResult result_1 = fightSpriteBuffComponent.getFightSpriteBuffMgr().attachFightSpriteBuff(buff)
		}

		G2C_TriggerSingleTargetSkill response = new G2C_TriggerSingleTargetSkill(fightSkill.getRefId(), caster, target, result.getData().getComponent(FightSkillResult.class));
		GameSceneHelper.broadcastMessageToAOI(caster, response)

		return result;
	}

	/**
	 * 赤月恶魔·地狱雷光 
	 * 
	 * 以自身为中心施放，作用范围为5*5
	 * 对敌方目标造成skillDamageRate%魔法伤害
	 */
	Closure<RuntimeResult> monster_17_closure = {FightSkill fightSkill, FightSprite caster, byte direction ->
		int row = 15; int column = 15;
		Collection<FightSprite> fightSprites = GameSceneHelper.getFightSprites(caster.getCrtScene(), caster, caster.getCrtPosition(), row, column)

		G2C_TriggerMultiTargetSkill response = new G2C_TriggerMultiTargetSkill(fightSkill.getRefId(), caster)

		PropertyDictionary runtimeParameter = fightSkill.getLevelRef().getRuntimeParameter()
		int skillDamage = MGPropertyAccesser.getSkillDamage(runtimeParameter)
		if (skillDamage < 0) {
			skillDamage = 0;
		}
		double skillDamageRate = MGPropertyAccesser.getSkillDamageRate(runtimeParameter) / 100.0;
		for(FightSprite sprite : fightSprites) {
			if (((Monster)caster).getMonsterRef().isRegularMonster()) {
				if(sprite instanceof Monster && ((Monster)sprite).getMonsterRef().isRegularMonster()) {
					continue;
				}
			}
			if (sprite instanceof Player) {
				if (sprite.isInSafeRegion()) {
					continue;
				}
				boolean isSameUnion = MGUnionHelper.isInTheSameUnion(caster, sprite);
				if (isSameUnion) {
					continue;
				}
			}
			RuntimeResult result = MGFightSkillRuntimeCommon.basicAttack(fightSkill, caster, sprite, skillDamageRate, skillDamage)

			FightSkillResult skillResult = result.getData().getComponent(FightSkillResult.class)
			response.addSkillResult(skillResult)
		}

		GameSceneHelper.broadcastMessageToAOI(caster, response)
	}

	/**
	 * 黄泉教主·普通魔攻 
	 * 
	 * 造成skillDamageRate%的魔法伤害
	 */
	Closure<RuntimeResult> monster_18_closure = {FightSkill fightSkill, FightSprite caster, FightSprite target ->
		PropertyDictionary runtimeParameter = fightSkill.getLevelRef().getRuntimeParameter()
		int skillDamage = MGPropertyAccesser.getSkillDamage(runtimeParameter)
		if (skillDamage < 0) {
			skillDamage = 0;
		}
		double skillDamageRate = MGPropertyAccesser.getSkillDamageRate(runtimeParameter) / 100.0

		RuntimeResult result = MGFightSkillRuntimeCommon.basicAttack(fightSkill, caster, target, skillDamageRate, skillDamage)
		G2C_TriggerSingleTargetSkill response = new G2C_TriggerSingleTargetSkill(fightSkill.getRefId(), caster, target, result.getData().getComponent(FightSkillResult.class));
		GameSceneHelper.broadcastMessageToAOI(caster, response)
	}

	/**
	 * 骷髅箭手宝宝·投射
	 * 
	 * 远程物攻,射箭,对目标造成skillDamageRate%+skillDamage的物理伤害
	 */
	Closure<RuntimeResult> monster_19_closure = {FightSkill fightSkill, FightSprite caster, FightSprite target ->
		PropertyDictionary runtimeParameter = fightSkill.getLevelRef().getRuntimeParameter()
		int skillDamage = MGPropertyAccesser.getSkillDamage(runtimeParameter)
		if (skillDamage < 0) {
			skillDamage = 0;
		}
		double skillDamageRate = MGPropertyAccesser.getSkillDamageRate(runtimeParameter) / 100.0

		RuntimeResult result = MGFightSkillRuntimeCommon.basicAttack(fightSkill, caster, target, skillDamageRate, skillDamage)
		G2C_TriggerSingleTargetSkill response = new G2C_TriggerSingleTargetSkill(fightSkill.getRefId(), caster, target, result.getData().getComponent(FightSkillResult.class));
		GameSceneHelper.broadcastMessageToAOI(caster, response)
	}

	/**
	 * 寒冰神兽宝宝·喷射（冰）
	 * 
	 * 远程物攻,对目标造成skillDamageRate%+skillDamage的物理伤害
	 * 神兽吐息能降低目标移速30%,持续2秒
	 */
	Closure<RuntimeResult> monster_20_closure = {FightSkill fightSkill, FightSprite caster, FightSprite target ->
		PropertyDictionary runtimeParameter = fightSkill.getLevelRef().getRuntimeParameter()
		int skillDamage = MGPropertyAccesser.getSkillDamage(runtimeParameter)
		if (skillDamage < 0) {
			skillDamage = 0;
		}
		double skillDamageRate = MGPropertyAccesser.getSkillDamageRate(runtimeParameter) / 100.0

		String deceleratingSpeedBuffId = "buff_state_7"
		long durationMillis = 2000
		int speedLowered = 30
		MGFightSpriteBuffComponent fightSpriteBuffComponent = (MGFightSpriteBuffComponent) target.getTagged(MGFightSpriteBuffComponent.Tag);
		MGFightSpriteBuffRef ref = (MGFightSpriteBuffRef) GameRoot.getGameRefObjectManager().getManagedObject(deceleratingSpeedBuffId);
		MGFightSpriteBuff buff = new MGFightSpriteBuff(ref, caster, target, durationMillis);
		MGPropertyAccesser.setOrPutMoveSpeedPer(buff.getSpecialProperty(), speedLowered);
		fightSpriteBuffComponent.getFightSpriteBuffMgr().attachFightSpriteBuff(buff);
		
		RuntimeResult result = MGFightSkillRuntimeCommon.basicAttack(fightSkill, caster, target, skillDamageRate, skillDamage)
		G2C_TriggerSingleTargetSkill response = new G2C_TriggerSingleTargetSkill(fightSkill.getRefId(), caster, target, result.getData().getComponent(FightSkillResult.class));
		GameSceneHelper.broadcastMessageToAOI(caster, response)
	}

	/**
	 * 火球神兽宝宝·喷射（火）
	 * 
	 * 远程魔攻，对目标造成skillDamageRate%+skillDamage魔法伤害
	 */
	Closure<RuntimeResult> monster_21_closure = {FightSkill fightSkill, FightSprite caster, FightSprite target ->
		PropertyDictionary runtimeParameter = fightSkill.getLevelRef().getRuntimeParameter()
		int skillDamage = MGPropertyAccesser.getSkillDamage(runtimeParameter)
		if (skillDamage < 0) {
			skillDamage = 0;
		}
		double skillDamageRate = MGPropertyAccesser.getSkillDamageRate(runtimeParameter) / 100.0

		RuntimeResult result = MGFightSkillRuntimeCommon.basicAttack(fightSkill, caster, target, skillDamageRate, skillDamage)
		G2C_TriggerSingleTargetSkill response = new G2C_TriggerSingleTargetSkill(fightSkill.getRefId(), caster, target, result.getData().getComponent(FightSkillResult.class));
		GameSceneHelper.broadcastMessageToAOI(caster, response)
		return result;
	}
}