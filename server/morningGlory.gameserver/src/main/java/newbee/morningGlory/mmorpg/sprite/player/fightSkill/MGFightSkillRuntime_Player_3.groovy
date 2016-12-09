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


import newbee.morningGlory.mmorpg.monster.MGMonsterPerceiveComponent
import newbee.morningGlory.mmorpg.player.summons.PlayerSummonMonsterComponent
import newbee.morningGlory.mmorpg.player.summons.SummonMonsterExpComponent
import newbee.morningGlory.mmorpg.sprite.MGFightProcessComponent
import newbee.morningGlory.mmorpg.sprite.MGFightProcessHelper
import newbee.morningGlory.mmorpg.sprite.buff.MGFightSpriteBuff
import newbee.morningGlory.mmorpg.sprite.buff.MGFightSpriteBuffComponent
import newbee.morningGlory.mmorpg.sprite.buff.MGFightSpriteBuffRef
import newbee.morningGlory.mmorpg.sprite.skillEffect.effects.SkillEffectCureHp
import newbee.morningGlory.mmorpg.sprite.skillEffect.effects.SkillEffectDefault
import newbee.morningGlory.mmorpg.sprite.skillEffect.effects.SkillEffectHp
import newbee.morningGlory.mmorpg.sprite.skillEffect.effects.SkillEffectSummonMonster

import org.apache.log4j.Logger

import sophia.foundation.property.PropertyDictionary
import sophia.foundation.util.Position
import sophia.game.GameRoot
import sophia.game.component.communication.GameEvent
import sophia.mmorpg.base.scene.GameScene
import sophia.mmorpg.base.scene.GameSceneHelper
import sophia.mmorpg.base.scene.aoi.SceneAOILayer
import sophia.mmorpg.base.scene.grid.SceneGrid
import sophia.mmorpg.base.scene.mgr.MonsterMgrComponent
import sophia.mmorpg.base.sprite.FightSprite
import sophia.mmorpg.base.sprite.fightSkill.FightSkill
import sophia.mmorpg.base.sprite.fightSkill.FightSkillResult
import sophia.mmorpg.base.sprite.fightSkill.FightSkillResultImpl
import sophia.mmorpg.base.sprite.fightSkill.FightSkillRuntimeHelper
import sophia.mmorpg.base.sprite.fightSkill.gameevent.BeforeAttack_GE
import sophia.mmorpg.monster.Monster
import sophia.mmorpg.player.Player
import sophia.mmorpg.player.fightSkill.event.G2C_TriggerMultiTargetSkill
import sophia.mmorpg.player.fightSkill.event.G2C_TriggerSingleTargetSkill
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser
import sophia.mmorpg.ref.editorgen.MGPropertySymbolDefines
import sophia.mmorpg.utils.RuntimeResult
import sophia.mmorpg.utils.SFRandomUtils

import com.google.common.base.Strings

/**
 * 道士职业技能
 */
class MGFightSkillRuntime_Player_3 {
	private final Map<String, Closure<RuntimeResult>> map = new HashMap<>();
	private final static Logger logger = Logger.getLogger(MGFightSkillRuntime_Player_3.class)

	public MGFightSkillRuntime_Player_3() {
		super();
		this.map.put("skill_ds_1", player_3_1_closure);
		this.map.put("skill_ds_1_1", player_3_1_1_closure);
		this.map.put("skill_ds_2", player_3_2_closure);
		this.map.put("skill_ds_3", player_3_3_closure);
		this.map.put("skill_ds_3_1", player_3_3_1_closure);
		this.map.put("skill_ds_4", player_3_4_closure);
		this.map.put("skill_ds_4_1", player_3_4_1_closure);
		this.map.put("skill_ds_4_2", player_3_4_2_closure);
		this.map.put("skill_ds_5", player_3_5_closure);
		this.map.put("skill_ds_5_1", player_3_5_1_closure);
		this.map.put("skill_ds_5_2", player_3_5_2_closure);
		this.map.put("skill_ds_6", player_3_6_closure);
		this.map.put("skill_ds_7", player_3_7_closure);
		this.map.put("skill_ds_8", player_3_8_closure);
		this.map.put("skill_ds_9", player_3_9_closure);
		this.map.put("skill_ds_10", player_3_10_closure);
		this.map.put("skill_ds_11", player_3_11_closure);
		this.map.put("skill_ds_11_1", player_3_11_1_closure);
		this.map.put("skill_ds_11_2", player_3_11_2_closure);
		this.map.put("skill_ds_11_3", player_3_11_3_closure);
	}

	public Map<String, Closure<RuntimeResult>> getClosures() {
		return map;
	}

	/**
	 * 治愈术 ——主动精灵目标技能
	 * 使目标回复X点生命，每秒回复Y点，持续Z秒
	 * 治愈效果和本次施放时取值的道术有关，每点道术提高持续时间M秒
	 * 多个治愈术作用于一个目标身上时，后者会替换前者的效果
	 * 当角色生命值回满时，效果立即取消
	 */
	Closure<RuntimeResult> player_3_1_closure = {FightSkill fightSkill, FightSprite caster, FightSprite target ->

		// buff
		PropertyDictionary parameterPd = fightSkill.getLevelRef().getRuntimeParameter()
		if(caster.isEnemyTo(target)){
			target = caster;
		}
		int value = MGPropertyAccesser.getSkillDamage(parameterPd)
		PropertyDictionary pd = caster.getFightPropertyMgrComponent().getFightPropertyMgr().getSnapshotByNew().getPropertyDictionary()
		int maxTao = MGPropertyAccesser.getMaxTao(pd)
		int minTao = MGPropertyAccesser.getMinTao(pd)
		int attack = maxTao <= minTao ? maxTao :  SFRandomUtils.random(minTao, maxTao)
		int skillCo = MGPropertyAccesser.getSkillCo(parameterPd)
		int hp = value + attack / skillCo
		String buffRefId = "buff_skill_4"
		MGFightSpriteBuffRef ref = (MGFightSpriteBuffRef) GameRoot.getGameRefObjectManager().getManagedObject(buffRefId)
		MGFightSpriteBuff buff = new MGFightSpriteBuff(ref, caster, target)
		MGPropertyAccesser.setOrPutHP(buff.getSpecialProperty(), hp)
		MGFightSpriteBuffComponent fightSpriteBuffComponent = (MGFightSpriteBuffComponent) target.getTagged(MGFightSpriteBuffComponent.Tag);
		RuntimeResult result = fightSpriteBuffComponent.getFightSpriteBuffMgr().attachFightSpriteBuff(buff)

		FightSkillResultImpl skillResult = new FightSkillResultImpl(hp, caster, target);
		skillResult.addSkillEffect(new SkillEffectCureHp(hp, target))
		result.getData().addComponent(skillResult)
		if(logger.isDebugEnabled()) {
			logger.debug("治愈术 add hp: " + hp + " value: " + value + "minTao: " + minTao + " maxTao: " + maxTao + " attack: " + attack + " skillCo: " + skillCo)
		}

		G2C_TriggerSingleTargetSkill response = new G2C_TriggerSingleTargetSkill(fightSkill.getRefId(), caster, target, result.getData().getComponent(
				FightSkillResult.class));
		GameSceneHelper.broadcastMessageToAOI(caster, response)

		return result
	}

	/**
	 * 治愈术拓展1
	 * 使用治愈术时，附加一个瞬间回复的治愈效果（数值=道术）
	 */
	Closure<RuntimeResult> player_3_1_1_closure = {FightSkill fightSkill, FightSprite caster, FightSprite target ->
		RuntimeResult result = player_3_1_closure.call(fightSkill, caster, target)
		PropertyDictionary pd = caster.getFightPropertyMgrComponent().getFightPropertyMgr().getSnapshotByNew().getPropertyDictionary()
		int maxTao = MGPropertyAccesser.getMaxTao(pd)
		int minTao = MGPropertyAccesser.getMinTao(pd)
		int attack = maxTao <= minTao ? maxTao :  SFRandomUtils.random(minTao, maxTao)
		target.applyHP(target, attack);
		return result;
	}

	/**
	 * 精神力战法 ——加点技能
	 * 永久增加角色X点准确
	 */
	Closure<RuntimeResult> player_3_2_closure = {FightSkill fightSkill, FightSprite caster, FightSprite targetSprite ->
		RuntimeResult result = RuntimeResult.OK()

		return result;
	}


	/**
	 * 施毒术 ——主动精灵目标技能
	 * 使目标每X秒减少Y点生命，并减少Z点物理和M点魔法防御，持续N秒
	 * de-buff效果和本次施放时取值的道术有关，每点道术提高持续时间M秒
	 * 多个施毒术作用于一个目标身上时，后者会替换前者的效果
	 */
	Closure<RuntimeResult> player_3_3_closure = {FightSkill fightSkill, FightSprite caster, FightSprite targetSprite ->
		// buff
		RuntimeResult result = RuntimeResult.OK();
		MGFightProcessComponent<?> fightProcessComponent = (MGFightProcessComponent<?>) caster.getTagged(MGFightProcessComponent.Tag);
		if(!fightProcessComponent.isValidAttackState(targetSprite, caster,MGFightProcessHelper.D_ATTACK)){
			return;
		}
		if(!caster.isEnemyTo(targetSprite)){
			return;
		}
		BeforeAttack_GE after = new BeforeAttack_GE(caster, targetSprite);
		sendGameEvent(BeforeAttack_GE.class.getSimpleName(), after, caster);
		sendGameEvent(BeforeAttack_GE.class.getSimpleName(), after, targetSprite);

		PropertyDictionary parameterPd = fightSkill.getLevelRef().getRuntimeParameter()
		int damageValueOfSkill = MGPropertyAccesser.getSkillDamage(parameterPd) ;
		int damageRateOfSkill = MGPropertyAccesser.getSkillDamageRate(parameterPd) ;
		PropertyDictionary pd = caster.getFightPropertyMgrComponent().getFightPropertyMgr().getSnapshotByNew().getPropertyDictionary()
		int maxTao = MGPropertyAccesser.getMaxTao(pd)
		int minTao = MGPropertyAccesser.getMinTao(pd)
		int attack = maxTao <= minTao ? maxTao :  SFRandomUtils.random(minTao, maxTao)
		int duration = 30 + attack / 50;
		duration = duration < 300 ? duration : 300;
		duration = duration * 1000;
		int hp = attack * 0.1 + damageValueOfSkill;
		if(logger.isDebugEnabled()) {
			logger.debug("施毒术 maxTao " + maxTao + " minTao " + minTao + " attack " + attack + " duration " + duration + " hp " + hp)
		}
		String buffRefId = "buff_state_10";
		MGFightSpriteBuffComponent fightSpriteBuffComponent = (MGFightSpriteBuffComponent) targetSprite.getTagged(MGFightSpriteBuffComponent.Tag);
		MGFightSpriteBuffRef ref = (MGFightSpriteBuffRef) GameRoot.getGameRefObjectManager().getManagedObject(buffRefId)
		MGFightSpriteBuff buff = new MGFightSpriteBuff(ref, caster, targetSprite, duration)
		MGPropertyAccesser.setOrPutHP(buff.getSpecialProperty(), hp)
		MGPropertyAccesser.setOrPutSkillDamageRate(buff.getSpecialProperty(), damageRateOfSkill)
		RuntimeResult result_1 = fightSpriteBuffComponent.getFightSpriteBuffMgr().attachFightSpriteBuff(buff)

		FightSkillResultImpl skillResult = new FightSkillResultImpl(0, caster, targetSprite);
		skillResult.addSkillEffect(new SkillEffectDefault(targetSprite))
		result.getData().addComponent(skillResult)

		G2C_TriggerSingleTargetSkill response = new G2C_TriggerSingleTargetSkill(fightSkill.getRefId(), caster, targetSprite, result.getData().getComponent(
				FightSkillResult.class));
		GameSceneHelper.broadcastMessageToAOI(caster, response)
		return result;
	}

	private void sendGameEvent(String id, Object data, FightSprite target) {
		GameEvent<Object> event = GameEvent.getInstance(id, data);
		target.handleGameEvent(event);
		GameEvent.pool(event);
	}

	/**
	 * 施毒术拓展1
	 * 施毒术变为群体毒术，以目标为中心5*5范围的对象都会中毒（特效用毒术之前的这个，每个中毒对象都播放）中毒：之前的技能里已经有该效果描述
	 */
	Closure<RuntimeResult> player_3_3_1_closure = {FightSkill fightSkill, FightSprite caster, FightSprite target ->
		int row = 5; int column = 5;
		Collection<FightSprite> fightSprites = GameSceneHelper.getFightSprites(target.getCrtScene(), caster, target.getCrtPosition(), row, column)
		for(FightSprite sprite : fightSprites) {
			player_3_3_closure.call(fightSkill, caster, sprite)
		}

		return RuntimeResult.OK()

	}

	/**
	 * 灵魂火符 ——主动精灵目标技能
	 * 对目标造成X%+Y点道法伤害
	 */
	Closure<RuntimeResult> player_3_4_closure = {FightSkill fightSkill, FightSprite caster, FightSprite target ->
		PropertyDictionary runtimeParameter = fightSkill.getLevelRef().getRuntimeParameter()
		double rate = MGPropertyAccesser.getSkillDamageRate(runtimeParameter) / 100.0
		int value = MGPropertyAccesser.getSkillDamage(runtimeParameter)

		if(logger.isDebugEnabled()) {
			logger.debug("灵魂火符 rate " + rate + " value " + value)
		}
		MGFightProcessComponent<?> fightProcessComponent = (MGFightProcessComponent<?>) caster.getTagged(MGFightProcessComponent.Tag);
		if(!fightProcessComponent.isValidAttackState(target, caster,MGFightProcessHelper.D_ATTACK)){
			return;
		}
		RuntimeResult result = MGFightSkillRuntimeCommon.basicAttack(fightSkill, caster, target, rate, value);

		G2C_TriggerSingleTargetSkill response = new G2C_TriggerSingleTargetSkill(fightSkill.getRefId(), caster, target, result.getData().getComponent(
				FightSkillResult.class));
		GameSceneHelper.broadcastMessageToAOI(caster, response)

		return result;
	}

	/**
	 * 灵魂火符拓展1
	 * 火符带有爆炸效果，以目标为中心3*3范围内的对象都会受到（70%，80%，90%根据火符等级对应数据）的伤害（击中特效要做大）
	 */
	Closure<RuntimeResult> player_3_4_1_closure = {FightSkill fightSkill, FightSprite caster, FightSprite target ->
		int row = 3; int column = 3;
		Collection<FightSprite> fightSprites = GameSceneHelper.getFightSprites(target.getCrtScene(), caster, target.getCrtPosition(), row, column)
		for(FightSprite sprite : fightSprites) {
			if((sprite instanceof Player && sprite.isInSafeRegion()) || (sprite instanceof Monster && !((Monster)sprite).getMonsterRef().isRegularMonster())) {
				continue;
			}
			player_3_4_closure.call(fightSkill, caster, sprite)
		}
		return RuntimeResult.OK()
	}

	/**
	 * 灵魂火符拓展2
	 * 火符附带中毒效果，持续3秒中毒：每秒造成普攻道伤*20%的伤害，多个效果叠加时，取最后的效果为唯一效果
	 */
	Closure<RuntimeResult> player_3_4_2_closure = {FightSkill fightSkill, FightSprite caster, FightSprite target ->

		RuntimeResult result = player_3_4_closure.call(fightSkill, caster, target)

		PropertyDictionary pd = caster.getFightPropertyMgrComponent().getFightPropertyMgr().getSnapshotByNew().getPropertyDictionary()
		int maxTao = MGPropertyAccesser.getMaxTao(pd)
		int minTao = MGPropertyAccesser.getMinTao(pd)
		int attack = maxTao <= minTao ? maxTao : SFRandomUtils.random(minTao, maxTao)

		String buffRefId = "buff_state_5";
		int lastTime = 6000
		int eachTimekill = attack * 0.2

		MGFightSpriteBuffComponent fightSpriteBuffComponent = (MGFightSpriteBuffComponent) target.getTagged(MGFightSpriteBuffComponent.Tag);

		MGFightSpriteBuffRef ref = (MGFightSpriteBuffRef) GameRoot.getGameRefObjectManager().getManagedObject(buffRefId)
		MGFightSpriteBuff buff = new MGFightSpriteBuff(ref, caster, target, lastTime)
		MGPropertyAccesser.setOrPutHP(buff.getSpecialProperty(), eachTimekill)
		RuntimeResult result_1 = fightSpriteBuffComponent.getFightSpriteBuffMgr().attachFightSpriteBuff(buff)

		return result
	}

	/**
	 * 召唤骷髅 ——主动精灵目标技能
	 * 召唤一个骷髅帮助主角战斗
	 * 道术影响骷髅的攻击力，自身的道术下限和上限每点分别增加骷髅X和Y点攻击下限和上限
	 * 骷髅本身有基础属性，需要策划来进行配置
	 * 骷髅会进行升级（这个逻辑待定）
	 * 	1）初始1,2,3级技能最多可以让骷髅升级到3,5,7级
	 * 	2）骷髅每攻击一次增加1点经验，升级需要配置不同的经验点
	 * AI如下：
	 * 	1）骷髅不会离开主角X的范围，超过则立即传送到主角身边
	 * 	2）骷髅攻击的目标优先级如下：
	 * 		主人攻击的>攻击主人的>最近的
	 * 		骷髅每10秒（待定）重新选定目标
	 */
	Closure<RuntimeResult> player_3_5_closure = {FightSkill fightSkill, FightSprite caster, byte direction ->
		// summon monster
		int skillLevel = fightSkill.getLevelRef().getLevel();
		String skillMonsterRefId;
		skillMonsterRefId = "monster_skill_ds_5_" + skillLevel;
		summonMonster(fightSkill, caster, direction, skillMonsterRefId)
	}

	/**
	 * 召唤骷髅拓展1
	 * 骷髅变为骷髅精灵，攻击频率会提高（应该就是特配一个骷髅精灵，然后其它逻辑和骷髅一样）
	 */
	Closure<RuntimeResult> player_3_5_1_closure = {FightSkill fightSkill, FightSprite caster, byte direction ->
		int skillLevel = fightSkill.getLevelRef().getLevel() + 7;
		String skillMonsterRefId;
		skillMonsterRefId = "monster_skill_ds_5_" + skillLevel;
		summonMonster(fightSkill, caster, direction, skillMonsterRefId)
	}

	/**
	 * 召唤骷髅拓展2
	 * 骷髅变为骷髅箭手，远程攻击（应该就是特配一个箭手，然后其它逻辑和骷髅一样）
	 */
	Closure<RuntimeResult> player_3_5_2_closure = {FightSkill fightSkill, FightSprite caster, byte direction ->
		int skillLevel = fightSkill.getLevelRef().getLevel() + 14;
		String skillMonsterRefId;
		skillMonsterRefId = "monster_skill_ds_5_" + skillLevel;
		summonMonster(fightSkill, caster, direction, skillMonsterRefId)
	}

	/**
	 * 隐身术 ——主动精灵目标技能
	 * 使自己隐身，怪物会消失目标
	 * 移动会解除隐身状态
	 * 主动攻击怪物时，怪物可以重新定义玩家为目标（仅限被攻击的怪物）
	 * 效果和本次施放时取值的道术有关，每点道术提高持续时间M秒
	 * 多个隐身术叠加时，后者效果覆盖前者
	 */
	Closure<RuntimeResult> player_3_6_closure = {FightSkill fightSkill, FightSprite caster, FightSprite targetSprite ->
		RuntimeResult result = RuntimeResult.OK();
		targetSprite = caster;
		PropertyDictionary parameterPd = fightSkill.getLevelRef().getRuntimeParameter()
		PropertyDictionary pd = caster.getFightPropertyMgrComponent().getFightPropertyMgr().getSnapshotByNew().getPropertyDictionary()
		int maxTao = MGPropertyAccesser.getMaxTao(pd)
		int minTao = MGPropertyAccesser.getMinTao(pd)
		int attack = maxTao <= minTao ? maxTao :  SFRandomUtils.random(minTao, maxTao)
		int skillCo = MGPropertyAccesser.getSkillCo(parameterPd)
		int duration = skillCo + attack / 50;
		duration = duration < 300 ? duration : 300;
		duration = duration * 1000;

		if(logger.isDebugEnabled()) {
			logger.debug("隐身术 maxTao " + maxTao + " minTao " + minTao + " attack " + attack + " skillCo " + skillCo + " duration " + duration)
		}

		String buffRefId = "buff_state_13"
		MGFightSpriteBuffRef ref = (MGFightSpriteBuffRef) GameRoot.getGameRefObjectManager().getManagedObject(buffRefId)
		if(targetSprite instanceof Player){
			MGFightSpriteBuffComponent fightSpriteBuffComponent = (MGFightSpriteBuffComponent) targetSprite.getTagged(MGFightSpriteBuffComponent.Tag);
			MGFightSpriteBuff buff = new MGFightSpriteBuff(ref, caster, targetSprite,duration)
			result = fightSpriteBuffComponent.getFightSpriteBuffMgr().attachFightSpriteBuff(buff)
		}

		return result;
	}

	/**
	 * 魔抗咒 ——主动地图格技能
	 * 选取之后对地图施放，以施放点为中心5*5的范围内的非敌对目标增加魔防上限X%，持续60+Y秒
	 */
	Closure<RuntimeResult> player_3_7_closure = {FightSkill fightSKill, FightSprite caster, Position targetGrid ->
		// BUFF
		RuntimeResult result = null;
		int row = 5; int column = 5;
		Collection<FightSprite> fightSprites = GameSceneHelper.getFightSprites(caster.getCrtScene(), targetGrid, row, column)
		int skillDamageRate = MGPropertyAccesser.getSkillDamageRate(fightSKill.getLevelRef().getRuntimeParameter())
		PropertyDictionary pd = caster.getFightPropertyMgrComponent().getFightPropertyMgr().getSnapshotByNew().getPropertyDictionary()
		int maxTao = MGPropertyAccesser.getMaxTao(pd)
		int minTao = MGPropertyAccesser.getMinTao(pd)
		int attack = maxTao <= minTao ? maxTao :  SFRandomUtils.random(minTao, maxTao)
		int duration = 30 + attack / 10;
		duration = duration < 600 ? duration : 600;
		duration = duration * 1000;
		String buffRefId = "buff_skill_7";
		MGFightSpriteBuffRef ref = (MGFightSpriteBuffRef) GameRoot.getGameRefObjectManager().getManagedObject(buffRefId)

		if(logger.isDebugEnabled()) {
			logger.debug("魔抗咒 maxTao " + maxTao + " minTao " + minTao + " attack " + attack + " duration " + duration)
		}
		G2C_TriggerMultiTargetSkill response = new G2C_TriggerMultiTargetSkill(fightSKill.getRefId(), caster, targetGrid)
		for(FightSprite targetSprite : fightSprites){
			if(!caster.isEnemyTo(targetSprite)){
				MGFightSpriteBuffComponent fightSpriteBuffComponent = (MGFightSpriteBuffComponent) targetSprite.getTagged(MGFightSpriteBuffComponent.Tag);
				MGFightSpriteBuff buff = new MGFightSpriteBuff(ref, caster, targetSprite,duration)
				MGPropertyAccesser.setOrPutSkillDamageRate(buff.getSpecialProperty(), skillDamageRate)
				result = fightSpriteBuffComponent.getFightSpriteBuffMgr().attachFightSpriteBuff(buff)
			}
			FightSkillResultImpl skillResult = new FightSkillResultImpl(0, caster, targetSprite);
			skillResult.addSkillEffect(new SkillEffectDefault(targetSprite))
			response.addSkillResult(skillResult)
		}

		GameSceneHelper.broadcastMessageToAOI(caster, response)
		return result;
	}

	/**
	 * 物抗咒 ——主动地图格技能
	 * 选取之后对地图施放，以施放点为中心5*5的范围内的非敌对目标增加物防上限X%，持续60+Y秒
	 * 效果和本次施放时取值的道术有关，每点道术提高持续时间M秒
	 */
	Closure<RuntimeResult> player_3_8_closure = {FightSkill fightSKill, FightSprite caster, Position targetGrid ->
		// BUFF
		RuntimeResult result = null;
		int row = 5; int column = 5;
		Collection<FightSprite> fightSprites = GameSceneHelper.getFightSprites(caster.getCrtScene(), targetGrid, row, column)
		int skillDamageRate = MGPropertyAccesser.getSkillDamageRate(fightSKill.getLevelRef().getRuntimeParameter())
		PropertyDictionary pd = caster.getFightPropertyMgrComponent().getFightPropertyMgr().getSnapshotByNew().getPropertyDictionary()
		int maxTao = MGPropertyAccesser.getMaxTao(pd)
		int minTao = MGPropertyAccesser.getMinTao(pd)
		int attack = maxTao <= minTao ? maxTao :  SFRandomUtils.random(minTao, maxTao)
		int duration = 30 + attack / 10;
		duration = duration < 600 ? duration : 600;
		duration = duration * 1000;

		if(logger.isDebugEnabled()) {
			logger.debug("物抗咒 maxTao " + maxTao + " minTao " + minTao + " attack " + attack + " duration " + duration)
		}

		String buffRefId = "buff_skill_8";
		G2C_TriggerMultiTargetSkill response = new G2C_TriggerMultiTargetSkill(fightSKill.getRefId(), caster, targetGrid)

		MGFightSpriteBuffRef ref = (MGFightSpriteBuffRef) GameRoot.getGameRefObjectManager().getManagedObject(buffRefId)
		for(FightSprite targetSprite : fightSprites){
			if(!caster.isEnemyTo(targetSprite)){
				MGFightSpriteBuffComponent fightSpriteBuffComponent = (MGFightSpriteBuffComponent) targetSprite.getTagged(MGFightSpriteBuffComponent.Tag);
				MGFightSpriteBuff buff = new MGFightSpriteBuff(ref, caster, targetSprite,duration)
				MGPropertyAccesser.setOrPutSkillDamageRate(buff.getSpecialProperty(), skillDamageRate)
				result = fightSpriteBuffComponent.getFightSpriteBuffMgr().attachFightSpriteBuff(buff)
			}
			FightSkillResultImpl skillResult = new FightSkillResultImpl(0, caster, targetSprite);
			skillResult.addSkillEffect(new SkillEffectDefault(targetSprite))
			response.addSkillResult(skillResult)
		}

		GameSceneHelper.broadcastMessageToAOI(caster, response)
		return result;
	}
	/**
	 * 群体隐身术 ——主动地图格技能
	 * 选取之后对地图施放，以施放点为中心3*3的范围内的友方（宠物，队友，行会）都进入隐身状态（效果，逻辑同隐身术）
	 */
	Closure<RuntimeResult> player_3_9_closure = {FightSkill fightSKill, FightSprite caster, Position targetGrid ->
		RuntimeResult result = RuntimeResult.OK();
		int row = 3; int column = 3;
		Collection<FightSprite> fightSprites = GameSceneHelper.getFightSprites(caster.getCrtScene(), targetGrid, row, column)
		PropertyDictionary parameterPd = fightSKill.getLevelRef().getRuntimeParameter()
		PropertyDictionary pd = caster.getFightPropertyMgrComponent().getFightPropertyMgr().getSnapshotByNew().getPropertyDictionary()
		int maxTao = MGPropertyAccesser.getMaxTao(pd)
		int minTao = MGPropertyAccesser.getMinTao(pd)
		int attack = maxTao <= minTao ? maxTao :  SFRandomUtils.random(minTao, maxTao)
		int skillCo = MGPropertyAccesser.getSkillCo(parameterPd)
		int duration = skillCo + attack / 50;
		duration = duration < 300 ? duration : 300;
		duration = duration * 1000;

		if(logger.isDebugEnabled()) {
			logger.debug("群体隐身术  maxTao " + maxTao + " minTao " + minTao + " skillCo " + skillCo + " attack " + attack + " duration " + duration)
		}

		String buffRefId = "buff_state_13"
		MGFightSpriteBuffRef ref = (MGFightSpriteBuffRef) GameRoot.getGameRefObjectManager().getManagedObject(buffRefId)
		G2C_TriggerMultiTargetSkill response = new G2C_TriggerMultiTargetSkill(fightSKill.getRefId(), caster, targetGrid)

		for(FightSprite targetSprite : fightSprites){
			if(!caster.isEnemyTo(targetSprite)){
				MGFightSpriteBuffComponent fightSpriteBuffComponent = (MGFightSpriteBuffComponent) targetSprite.getTagged(MGFightSpriteBuffComponent.Tag);
				MGFightSpriteBuff buff = new MGFightSpriteBuff(ref, caster, targetSprite,duration)
				result = fightSpriteBuffComponent.getFightSpriteBuffMgr().attachFightSpriteBuff(buff)
			}
			FightSkillResultImpl skillResult = new FightSkillResultImpl(0, caster, targetSprite);
			skillResult.addSkillEffect(new SkillEffectDefault(targetSprite))
			response.addSkillResult(skillResult)

		}
		GameSceneHelper.broadcastMessageToAOI(caster, response)
		return result;
	}

	/**
	 * 群体治愈术 ——主动地图格技能
	 * 选取之后对地图施放，以施放点为中心3*3的范围内的非敌对目标都进入到治愈状态（效果，逻辑同治愈术）
	 */
	Closure<RuntimeResult> player_3_10_closure = {FightSkill fightSKill, FightSprite caster, Position targetGrid ->
		// BUFF
		RuntimeResult result = RuntimeResult.OK();
		int row = 5; int column = 5;
		Collection<FightSprite> fightSprites = GameSceneHelper.getFightSprites(caster.getCrtScene(), targetGrid, row, column)
		PropertyDictionary parameterPd = fightSKill.getLevelRef().getRuntimeParameter()
		int value = MGPropertyAccesser.getSkillDamage(parameterPd)
		PropertyDictionary pd = caster.getFightPropertyMgrComponent().getFightPropertyMgr().getSnapshotByNew().getPropertyDictionary()
		int maxTao = MGPropertyAccesser.getMaxTao(pd)
		int minTao = MGPropertyAccesser.getMinTao(pd)
		int attack = maxTao <= minTao ? maxTao :  SFRandomUtils.random(minTao, maxTao)
		int skillCo = MGPropertyAccesser.getSkillCo(parameterPd)
		int hp = value + attack / skillCo
		String buffRefId = "buff_skill_9"
		MGFightSpriteBuffRef ref = (MGFightSpriteBuffRef) GameRoot.getGameRefObjectManager().getManagedObject(buffRefId)

		if(logger.isDebugEnabled()) {
			logger.debug("群体治愈术 maxTao " + maxTao + " minTao " + minTao + " attack " + attack + " skillCo " + skillCo + " hp " + hp)
		}
		G2C_TriggerMultiTargetSkill response = new G2C_TriggerMultiTargetSkill(fightSKill.getRefId(), caster, targetGrid)
		for(FightSprite targetSprite : fightSprites){
			if(!caster.isEnemyTo(targetSprite)){
				MGFightSpriteBuffComponent fightSpriteBuffComponent = (MGFightSpriteBuffComponent) targetSprite.getTagged(MGFightSpriteBuffComponent.Tag);
				MGFightSpriteBuff buff = new MGFightSpriteBuff(ref, caster, targetSprite)
				MGPropertyAccesser.setOrPutHP(buff.getSpecialProperty(), hp)
				result = fightSpriteBuffComponent.getFightSpriteBuffMgr().attachFightSpriteBuff(buff)
			}
			FightSkillResultImpl skillResult = new FightSkillResultImpl(0, caster, targetSprite);
			skillResult.addSkillEffect(new SkillEffectHp(0,targetSprite))
			response.addSkillResult(skillResult)

		}
		GameSceneHelper.broadcastMessageToAOI(caster, response)
		return result;
	}

	/**
	 * 召唤神兽 ——主动目标精灵技能
	 * 召唤一个神兽帮助主角战斗
	 * 道术影响神兽的攻击力，自身的道术下限和上限每点分别增加神兽X和Y点攻击下限和上限
	 * 神兽本身有基础属性，需要策划来进行配置
	 * 神兽会进行升级（这个逻辑待定）
	 * 	1）初始1,2,3级技能最多可以让骷髅升级到3,5,7级
	 * 	2）神兽每攻击一次增加1点经验，升级需要配置不同的经验点
	 * 神兽和骷髅互斥，只能存在一个，后使用的覆盖前面的
	 * AI如下：
	 * 	1）神兽不会离开主角X的范围，超过则立即传送到主角身边
	 * 	2）神兽攻击的目标优先级如下：
	 * 		主人攻击的>攻击主人的>最近的
	 * 		骷髅每10秒（待定）重新选定目标
	 */
	Closure<RuntimeResult> player_3_11_closure = {FightSkill fightSkill, FightSprite caster, byte direction ->
		// summon monster
		int skillLevel = fightSkill.getLevelRef().getLevel();
		String skillMonsterRefId;
		skillMonsterRefId = "monster_skill_ds_11_" + skillLevel;
		summonMonster(fightSkill, caster, direction, skillMonsterRefId)
	}

	/**
	 * 召唤神兽拓展1
	 * 神兽吐息变成冰效果（特效新做），附加减速效果，持续2秒缓速：使移动速度降低30%持续2秒，多个效果叠加时，取最后的效果为唯一效果
	 */
	Closure<RuntimeResult> player_3_11_1_closure = {FightSkill fightSkill, FightSprite caster, byte direction ->
		// summon monster
		int skillLevel = fightSkill.getLevelRef().getLevel() + 7;
		String skillMonsterRefId;
		skillMonsterRefId = "monster_skill_ds_11_" + skillLevel;
		summonMonster(fightSkill, caster, direction, skillMonsterRefId)
	}

	/**
	 * 召唤神兽拓展2
	 * 神兽吐息变成大火球（伤害不变），远程攻击
	 */
	Closure<RuntimeResult> player_3_11_2_closure = {FightSkill fightSkill, FightSprite caster, byte direction ->
		// summon monster
		int skillLevel = fightSkill.getLevelRef().getLevel() + 14 ;
		String skillMonsterRefId;
		skillMonsterRefId = "monster_skill_ds_11_" + skillLevel;
		summonMonster(fightSkill, caster, direction, skillMonsterRefId)
	}

	/**
	 * 召唤神兽拓展3
	 * 神兽变成沃玛教主（最好是骷髅形态的），附带闪电近身，和群杀技能（应该就是特配一个教主，然后其它逻辑和神兽一样）
	 */
	Closure<RuntimeResult> player_3_11_3_closure = {FightSkill fightSkill, FightSprite caster, byte direction ->
		// summon monster
		int skillLevel = fightSkill.getLevelRef().getLevel() + 21 ;
		String skillMonsterRefId;
		skillMonsterRefId = "monster_skill_ds_11_" + skillLevel;
		summonMonster(fightSkill, caster, direction, skillMonsterRefId)
	}

	public void summonMonster(FightSkill fightSkill, FightSprite caster, byte direction, String skillMonsterRefId) {
		PlayerSummonMonsterComponent playerSummonComponent = (PlayerSummonMonsterComponent) caster.getTagged(PlayerSummonMonsterComponent.Tag)
		if (playerSummonComponent != null) {
			if (playerSummonComponent.hasSummonMonster() && !playerSummonComponent.getSummonMonster().isDead()) {
				Monster oldMonster = playerSummonComponent.getSummonMonster();
				oldMonster.getCrtScene().getMonsterMgrComponent().leaveWorld(oldMonster);
			}
		}
		PropertyDictionary runtimeParameter = fightSkill.getLevelRef().getRuntimeParameter()
		GameScene currentScene = caster.getCrtScene()
		MonsterMgrComponent monsterMgr = currentScene.getMonsterMgrComponent()
		// 取玩家左前方的位置
		byte leftDirection = FightSkillRuntimeHelper.getLeftDirection(direction)
		SceneGrid sceneGrid = GameSceneHelper.getRandomUnblockedGrid(currentScene, caster.getCrtPosition(), SceneAOILayer.AOIGRID_MULTIPLE)
		Position targetGrid = GameSceneHelper.getCenterPosition(new Position(sceneGrid.getColumn(), sceneGrid.getRow()))
		while (targetGrid.equals(caster.getCrtPosition()) || GameSceneHelper.isBlocked(currentScene, targetGrid)){
			sceneGrid = GameSceneHelper.getRandomUnblockedGrid(currentScene, caster.getCrtPosition(), SceneAOILayer.AOIGRID_MULTIPLE)
			targetGrid = GameSceneHelper.getCenterPosition(new Position(sceneGrid.getColumn(), sceneGrid.getRow()))
		}
		Monster monster = monsterMgr.createMonster(skillMonsterRefId)
		String unionName = MGPropertyAccesser.getUnionName(caster.getProperty());
		if (!Strings.isNullOrEmpty(unionName)) {
			MGPropertyAccesser.setOrPutUnionName(monster.getProperty(), unionName);
		} else {
			MGPropertyAccesser.setOrPutUnionName(monster.getProperty(), "");
		}
		List<String> monsterIds = new ArrayList<>();
		monsterIds.add(monster.getId());
		monster.setPerceiveComponent((MGMonsterPerceiveComponent) monster.createComponent(MGMonsterPerceiveComponent.class));
		monster.setOwner(caster)
		caster.setSummonMonster(monster)
		// 设置伤害
		int skillDamageRate = MGPropertyAccesser.getSkillDamageRate(runtimeParameter)
		PropertyDictionary playerPd = caster.getFightPropertyMgrComponent().getFightPropertyMgr().getSnapshotByNew().getPropertyDictionary()
		PropertyDictionary monsterPd = monster.getFightPropertyMgrComponent().getFightPropertyMgr().getSnapshotByNew().getPropertyDictionary();

		/**   设置新的道法攻击   */
		int playerMaxTao = MGPropertyAccesser.getMaxTao(playerPd)
		int playerMinTao = MGPropertyAccesser.getMinTao(playerPd)
		int playerMaxTaoModify = playerMaxTao * skillDamageRate /100
		int playerMinTaoModify = playerMinTao * skillDamageRate /100
		monster.getFightPropertyMgrComponent().getFightPropertyMgr().getModifyTransaction().modifySomeProperty(MGPropertySymbolDefines.MaxTao_Id, playerMaxTaoModify)
		monster.getFightPropertyMgrComponent().getFightPropertyMgr().getModifyTransaction().modifySomeProperty(MGPropertySymbolDefines.MinTao_Id, playerMinTaoModify)


		/**   设置新的魔法攻击  */
		int playerMaxMAtk = MGPropertyAccesser.getMaxMAtk(playerPd)
		int playerMinMAtk = MGPropertyAccesser.getMinMAtk(playerPd)
		int playerMaxMAtkModify = playerMaxMAtk * skillDamageRate /100
		int playerMinMAtkModify = playerMinMAtk * skillDamageRate /100
		monster.getFightPropertyMgrComponent().getFightPropertyMgr().getModifyTransaction().modifySomeProperty(MGPropertySymbolDefines.MaxMAtk_Id, playerMaxMAtkModify)
		monster.getFightPropertyMgrComponent().getFightPropertyMgr().getModifyTransaction().modifySomeProperty(MGPropertySymbolDefines.MinMAtk_Id, playerMinMAtkModify)


		/**   设置新的物理攻击  */
		int playerMaxPAtk = MGPropertyAccesser.getMaxPAtk(playerPd)
		int playerMinPAtk = MGPropertyAccesser.getMinPAtk(playerPd)
		int playerMaxPAtkModify = playerMaxPAtk * skillDamageRate /100
		int playerMinPAtkModify = playerMinPAtk * skillDamageRate /100
		monster.getFightPropertyMgrComponent().getFightPropertyMgr().getModifyTransaction().modifySomeProperty(MGPropertySymbolDefines.MaxPAtk_Id, playerMaxPAtkModify)
		monster.getFightPropertyMgrComponent().getFightPropertyMgr().getModifyTransaction().modifySomeProperty(MGPropertySymbolDefines.MinPAtk_Id, playerMinPAtkModify)


		// 添加到管理器
		monster.createComponent(SummonMonsterExpComponent.class, SummonMonsterExpComponent.Tag);
		if (playerSummonComponent != null) {
			playerSummonComponent.setSummonMonster(monster)
		}
		monster.setBirthPosition(targetGrid);
		// 进入场景
		monsterMgr.enterWorld(monster, currentScene, targetGrid.getX(), targetGrid.getY())

		// notify client
		FightSkillResultImpl skillResult = new FightSkillResultImpl(0, caster, caster);
		skillResult.addSkillEffect(new SkillEffectSummonMonster(monsterIds))

		G2C_TriggerSingleTargetSkill response = new G2C_TriggerSingleTargetSkill(fightSkill.getRefId(), caster, caster, skillResult)
		GameSceneHelper.broadcastMessageToAOI(caster, response)
	}
}
