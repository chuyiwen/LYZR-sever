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
import java.util.Collection;

import newbee.morningGlory.mmorpg.monster.MGMonsterPerceiveComponent
import newbee.morningGlory.mmorpg.player.summons.PlayerSummonMonsterComponent
import newbee.morningGlory.mmorpg.sprite.MGFightProcessComponent
import newbee.morningGlory.mmorpg.sprite.MGFightProcessHelper;
import newbee.morningGlory.mmorpg.sprite.buff.MGFightSpriteBuff
import newbee.morningGlory.mmorpg.sprite.buff.MGFightSpriteBuffComponent
import newbee.morningGlory.mmorpg.sprite.buff.MGFightSpriteBuffRef
import newbee.morningGlory.mmorpg.sprite.skillEffect.effects.SkillEffectMove
import newbee.morningGlory.mmorpg.sprite.skillEffect.effects.SkillEffectSummonMonster

import org.apache.commons.lang3.StringUtils
import org.apache.log4j.Logger

import sophia.foundation.property.PropertyDictionary
import sophia.foundation.task.Task
import sophia.foundation.util.Position
import sophia.game.GameContext
import sophia.game.GameRoot
import sophia.game.component.communication.GameEvent;
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
import sophia.mmorpg.base.sprite.fightSkill.gameevent.BeforeAttack_GE;
import sophia.mmorpg.monster.Monster
import sophia.mmorpg.player.Player
import sophia.mmorpg.player.fightSkill.event.G2C_TriggerMultiTargetSkill
import sophia.mmorpg.player.fightSkill.event.G2C_TriggerSingleTargetSkill
import sophia.mmorpg.player.mount.PlayerMountComponent;
import sophia.mmorpg.player.mount.gameEvent.MGMountLevelUp_GE;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser
import sophia.mmorpg.ref.editorgen.MGPropertySymbolDefines
import sophia.mmorpg.utils.RuntimeResult
import sophia.mmorpg.utils.SFRandomUtils

/**
 * 法师职业技能
 */
class MGFightSkillRuntime_Player_2 {
	private static final Logger logger = Logger.getLogger(MGFightSkillRuntime_Player_2.class)
	private final Map<String, Closure<RuntimeResult>> map = new HashMap<>();

	public MGFightSkillRuntime_Player_2() {
		this.map.put("skill_fs_1", player_2_1_closure);
		this.map.put("skill_fs_2", player_2_2_closure);
		this.map.put("skill_fs_3", player_2_3_closure);
		this.map.put("skill_fs_3_1", player_2_3_1_closure);
		this.map.put("skill_fs_4", player_2_4_closure);
		this.map.put("skill_fs_4_1", player_2_4_1_closure);
		this.map.put("skill_fs_5", player_2_5_closure);
		this.map.put("skill_fs_6", player_2_6_closure);
		this.map.put("skill_fs_6_1", player_2_6_1_closure);
		this.map.put("skill_fs_7", player_2_7_closure);
		this.map.put("skill_fs_7_1", player_2_7_1_closure);
		this.map.put("skill_fs_8", player_2_8_closure);
		this.map.put("skill_fs_8_1", player_2_8_1_closure);
		this.map.put("skill_fs_9", player_2_9_closure);
		this.map.put("skill_fs_9_1", player_2_9_1_closure);
		this.map.put("skill_fs_10", player_2_10_closure);
		this.map.put("skill_fs_10_1", player_2_10_1_closure);
		this.map.put("skill_fs_11", player_2_11_closure);
		this.map.put("skill_fs_11_1", player_2_11_1_closure);
		this.map.put("skill_fs_12", player_2_12_closure);
		this.map.put("skill_fs_12_1", player_2_12_1_closure);
	}

	public Map<String, Closure<RuntimeResult>> getClosures() {
		return map;
	}

	public List<Monster> monsterEnterWorld(FightSprite caster, Position targetGrid, int damage, List<Monster> monsters, int skillDamageRate) {
		String skillMonsterRefId = "monster_skill_1";
		GameScene currentScene = caster.getCrtScene();
		MonsterMgrComponent monsterMgr = currentScene.getMonsterMgrComponent();
		Collection<FightSprite> spriteCollection = GameSceneHelper.getFightSprites(caster.getCrtScene(), targetGrid, 0);
		for (FightSprite fightSprite : spriteCollection) {
			if (fightSprite instanceof Monster) {
				Monster sprite = (Monster) fightSprite;
				String str = sprite.getMonsterRef().getId();
				if (str.equals(skillMonsterRefId)) {
					sprite.getCrtScene().getMonsterMgrComponent().leaveWorld(sprite);
				}
			}
		}
		Monster monster = monsterMgr.createMonster(skillMonsterRefId);
		monster.setPerceiveComponent((MGMonsterPerceiveComponent) monster.createComponent(MGMonsterPerceiveComponent.class));
		monster.setOwner(caster);
		monster.setBirthPosition(targetGrid);

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

		MGPropertyAccesser.setOrPutSkillDamage(monster.getMonsterRef().getProperty(), damage);
		// 进入场景
		monsterMgr.enterWorld(monster, currentScene, targetGrid.getX(), targetGrid.getY());
		monsters.add(monster);
		return monsters;
	}

	public void positionCheck(GameScene gameScene, Position targetGrid, List<Monster> monsters, FightSprite caster, int damage, int skillDamageRate) {
		if(!GameSceneHelper.isBlocked(gameScene, targetGrid)) {
			MonsterMgrComponent monsterMgr = gameScene.getMonsterMgrComponent()
			Collection<FightSprite> spriteCollection = GameSceneHelper.getSkillSummon(gameScene, targetGrid, 0);
			for (FightSprite sprite : spriteCollection) {
				if (sprite instanceof Monster) {
					Monster oldMonster = (Monster) sprite;
					if (logger.isDebugEnabled()) {
						logger.info(oldMonster.getMonsterRef().getId());
					}
					if (StringUtils.equals(oldMonster.getMonsterRef().getId(), "monster_skill_1")) {
						monsterMgr.leaveWorld(oldMonster)
					}
				}
			}
			monsterEnterWorld(caster, targetGrid, damage, monsters, skillDamageRate);
		}
	}

	public void getNewPosition(GameScene gameScene, Position targetGrid, List<Monster> monsters, FightSprite caster, int damage, int row, int column, int skillDamageRate, PropertyDictionary playerPd) {
		int gridSize = SceneAOILayer.AOIGRID_MULTIPLE
		for (int rowTime = row / 2;rowTime > 0;rowTime --) {
			Position grid = new Position(targetGrid.getX() - gridSize * rowTime, targetGrid.getY())
			positionCheck(gameScene, grid, monsters, caster, damage, skillDamageRate)
			Position grid1 = new Position(targetGrid.getX() + gridSize * rowTime, targetGrid.getY())
			positionCheck(gameScene, grid1, monsters, caster, damage, skillDamageRate)
			if (rowTime - 1 > 0) {
				Position preGrid = new Position(targetGrid.getX() - gridSize * (rowTime - 1), targetGrid.getY() + gridSize * (rowTime - 1))
				positionCheck(gameScene, preGrid, monsters, caster, damage, skillDamageRate)
				Position preGrid1 = new Position(targetGrid.getX() + gridSize * (rowTime - 1), targetGrid.getY() - gridSize * (rowTime - 1))
				positionCheck(gameScene, preGrid1, monsters, caster, damage, skillDamageRate)
			}
		}
		for (int columnTime = column / 2;columnTime > 0;columnTime --) {
			Position grid = new Position(targetGrid.getX(), targetGrid.getY() - gridSize * columnTime)
			positionCheck(gameScene, grid, monsters, caster, damage, skillDamageRate)
			Position grid1 = new Position(targetGrid.getX(), targetGrid.getY() + gridSize * columnTime)
			positionCheck(gameScene, grid1, monsters, caster, damage, skillDamageRate)
			if (columnTime - 1 > 0) {
				Position preGrid = new Position(targetGrid.getX() - gridSize * (columnTime - 1), targetGrid.getY() - gridSize * (columnTime - 1))
				positionCheck(gameScene, preGrid, monsters, caster, damage, skillDamageRate)
				Position preGrid1 = new Position(targetGrid.getX() + gridSize * (columnTime - 1), targetGrid.getY() + gridSize * (columnTime - 1))
				positionCheck(gameScene, preGrid1, monsters, caster, damage, skillDamageRate)
			}
		}
	}

	public void fireWallSkill(FightSkill fightSkill, FightSprite caster, Position targetGrid, int row, int column) {
		PropertyDictionary runtimeParameter = fightSkill.getLevelRef().getRuntimeParameter()
		int skillDamageRate = MGPropertyAccesser.getSkillDamageRate(runtimeParameter)
		int skillDamage = MGPropertyAccesser.getSkillDamage(runtimeParameter)

		PropertyDictionary playerPd = caster.getFightPropertyMgrComponent().getFightPropertyMgr().getSnapshotByNew().getPropertyDictionary()
		int maxMAtk = MGPropertyAccesser.getMaxMAtk(playerPd)
		int minMAtk = MGPropertyAccesser.getMinMAtk(playerPd)

		int attack = maxMAtk <= minMAtk ? maxMAtk :  SFRandomUtils.random(minMAtk, maxMAtk)
		int duration = 5 + attack / 50;
		duration = duration < 25 && duration > 0 ? duration : 25;
		duration = duration * 1000;

		String skillMonsterRefId = "monster_skill_1"
		GameScene currentScene = caster.getCrtScene()
		MonsterMgrComponent monsterMgr = currentScene.getMonsterMgrComponent()

		List<Monster> monsters = new ArrayList<>();
		List<String> monsterIds = new ArrayList<>();

		PlayerSummonMonsterComponent playerSummonComponent = (PlayerSummonMonsterComponent) caster.getTagged(PlayerSummonMonsterComponent.Tag)

		positionCheck(currentScene, targetGrid, monsters, caster, skillDamage, skillDamageRate)
		getNewPosition(currentScene, targetGrid, monsters, caster, skillDamage, row, column, skillDamageRate, playerPd)

		// notify client
		FightSkillResultImpl skillResult = new FightSkillResultImpl(0, caster, caster);
		for (Monster single : monsters) {
			monsterIds.add(single.getId());
		}
		skillResult.addSkillEffect(new SkillEffectSummonMonster(monsterIds))

		G2C_TriggerSingleTargetSkill response = new G2C_TriggerSingleTargetSkill(fightSkill.getRefId(), caster, caster, skillResult)
		GameSceneHelper.broadcastMessageToAOI(caster, response)

		Collection<FightSprite> spriteCollection = GameSceneHelper.getFightSprites(caster.getCrtScene(), targetGrid, 0);
		for (FightSprite sprite : spriteCollection) {
			BeforeAttack_GE before = new BeforeAttack_GE(caster, sprite);
			GameEvent<BeforeAttack_GE> ge = (GameEvent<BeforeAttack_GE>) GameEvent.getInstance(BeforeAttack_GE.class.getSimpleName(), before);
			caster.handleGameEvent(ge);
		}

		GameContext.getTaskManager().scheduleTask(new Task() {
					public void run() {
						for(Monster monster : monsters) {
							monsterMgr.leaveWorld(monster)
						}
					}
				}, duration)
	}

	/**
	 * 小火球 ——主动精灵目标技能
	 * 对目标造成X%+Y点法术伤害
	 */
	Closure<RuntimeResult> player_2_1_closure = {FightSkill fightSkill, FightSprite caster, FightSprite target ->

		PropertyDictionary runtimeParameter = fightSkill.getLevelRef().getRuntimeParameter()
		int value = MGPropertyAccesser.getSkillDamage(runtimeParameter)
		if(((target instanceof Player && target.isInSafeRegion()) || target.equals(caster))) {
			return RuntimeResult.OKResult
		}
		MGFightProcessComponent<?> fightProcessComponent = (MGFightProcessComponent<?>) caster.getTagged(MGFightProcessComponent.Tag);
		if(!fightProcessComponent.isValidAttackState(target, caster,MGFightProcessHelper.M_ATTACK)){
			return;
		}
		RuntimeResult result = MGFightSkillRuntimeCommon.basicAttack(fightSkill, caster, target, 1.0, value)

		G2C_TriggerSingleTargetSkill response = new G2C_TriggerSingleTargetSkill(fightSkill.getRefId(), caster, target, result.getData().getComponent(
				FightSkillResult.class));
		GameSceneHelper.broadcastMessageToAOI(caster, response)

		return result;
	}

	/**
	 * 抗拒火环 ——主动朝向技能
	 * 以自身为中心施放，作用范围为3*3,推开周围的其他目标
	 * 技能等级提高成功率，后退格数
	 * 如果目标等级>=自己等级，则成功率直接=0
	 */
	Closure<RuntimeResult> player_2_2_closure = {FightSkill fightSkill, FightSprite caster, byte direction ->
		int row = 3; int column = 3;
		Collection<FightSprite> fightSprites = GameSceneHelper.getFightSprites(caster.getCrtScene(), caster, caster.getCrtPosition(), row, column)

		PropertyDictionary effectPd = fightSkill.getLevelRef().getRuntimeParameter()
		int gridToMove = MGPropertyAccesser.getMoveGridBackward(effectPd)
		int chance = MGPropertyAccesser.getSkillDamageChance(effectPd)

		if(SFRandomUtils.random100() > chance) {
			return
		}

		if(logger.isDebugEnabled()) {
			logger.debug("抗拒火环  caster " + caster + " direction " + direction + " gridToMove " + gridToMove + " chance " + chance)
		}

		G2C_TriggerMultiTargetSkill response = new G2C_TriggerMultiTargetSkill(fightSkill.getRefId(), caster, direction)

		MGFightProcessComponent<?> fightProcessComponent = (MGFightProcessComponent<?>) caster.getTagged(MGFightProcessComponent.Tag);
		// move sprite backward
		for(FightSprite sprite : fightSprites) {
			// player can't be pushed out of safeRegion
			// only regular monster can be pushed
			// only sprite whose level is lower than caster can be pushed
			// only monster that can be moved can be pushed
			if((sprite instanceof Player && sprite.isInSafeRegion()) ||
			(sprite instanceof Monster && !((Monster)sprite).getMonsterRef().isRegularMonster()) ||
			(sprite instanceof Monster && !((Monster)sprite).getMonsterRef().canMove()) ||
			(sprite instanceof Player && sprite.getLevel() >= caster.getLevel()) ||
			(sprite instanceof Monster && ((Monster)sprite).getMonsterRef().getLevel() >= caster.getLevel()) ||
			(sprite instanceof Player && !fightProcessComponent.isValidAttackState(sprite, caster,MGFightProcessHelper.M_ATTACK))) {
				continue;
			}

			if(logger.isDebugEnabled()) {
				logger.debug("抗拒火环 before move sprite " + sprite)
			}

			FightSkillResultImpl spriteSkillResult = new FightSkillResultImpl(0, caster, sprite);
			Position beforeCastPos = new Position(sprite.getCrtPosition().getX(),sprite.getCrtPosition().getY());
			byte playerSpirteDirection = FightSkillRuntimeHelper.getDirection(caster.getCrtPosition(), beforeCastPos)
			FightSkillRuntimeHelper.moveByGrid(sprite, playerSpirteDirection, gridToMove * SceneAOILayer.AOIGRID_MULTIPLE)
			Position afterCastPos = sprite.getCrtPosition()
			spriteSkillResult.addSkillEffect(new SkillEffectMove(sprite, beforeCastPos, afterCastPos))
			response.addSkillResult(spriteSkillResult)
			// for this sprite not to move in the same time with the effect of this skill
			sprite.getPerceiveComponent().updateLastMoveTime(System.currentTimeMillis())

			if(logger.isDebugEnabled()) {
				logger.debug("抗拒火环  after move sprite " + sprite + " playerSpriteDirection " + playerSpirteDirection)
			}

		}

		GameSceneHelper.broadcastMessageToAOI(caster, response)
	}

	/**
	 * 地狱火 ——主动朝向技能
	 * 以自身朝向为方向，向前方6格范围施放一个火焰柱
	 * 对目标造成X%+Y点法术伤害
	 */
	Closure<RuntimeResult> player_2_3_closure = {FightSkill fightSkill, FightSprite caster, byte direction ->

		PropertyDictionary runtimeParameter = fightSkill.getLevelRef().getRuntimeParameter()
		double rate = MGPropertyAccesser.getSkillDamageRate(runtimeParameter) / 100.0
		int value = MGPropertyAccesser.getSkillDamage(runtimeParameter)
		if (logger.isDebugEnabled()) {
			logger.debug("player_2_3_closure rate: " + rate + " value: " + value)
		}

		G2C_TriggerMultiTargetSkill response = new G2C_TriggerMultiTargetSkill(fightSkill.getRefId(), caster, direction)

		Collection<FightSprite> sprites = GameSceneHelper.getFightSprites(caster.getCrtScene(), caster.getCrtPosition(), (byte) direction, 1, 6)
		for(FightSprite sprite : sprites) {
			if (sprite instanceof Player && caster.isInSafeRegion()) {
				continue;
			}
			if((sprite instanceof Player && sprite.isInSafeRegion()) || (sprite instanceof Monster && !((Monster)sprite).getMonsterRef().isRegularMonster())) {
				continue;
			}
			MGFightProcessComponent<?> fightProcessComponent = (MGFightProcessComponent<?>) caster.getTagged(MGFightProcessComponent.Tag);
			if(!fightProcessComponent.isValidAttackState(sprite, caster,MGFightProcessHelper.M_ATTACK)){
				continue;
			}
			RuntimeResult result = MGFightSkillRuntimeCommon.basicAttack(fightSkill, caster, sprite, rate, value)
			FightSkillResult skillResult = result.getData().getComponent(FightSkillResult.class)
			response.addSkillResult(skillResult)
		}

		GameSceneHelper.broadcastMessageToAOI(caster, response)

	}

	/**
	 * 地狱火拓展1
	 * 地狱火除了烧直线外，对相邻的两列也能造成（30%，35%，40%根据地狱火等级对应数据）的伤害
	 */
	Closure<RuntimeResult> player_2_3_1_closure = {FightSkill fightSkill, FightSprite caster, byte direction ->
		PropertyDictionary runtimeParameter = fightSkill.getLevelRef().getRuntimeParameter()
		double rate = MGPropertyAccesser.getSkillDamageRate(runtimeParameter) / 100.0
		double rate1 = MGPropertyAccesser.getSkillDamageRate1(runtimeParameter) / 100.0
		int level = MGPropertyAccesser.getSkillLevel(runtimeParameter)
		int value = MGPropertyAccesser.getSkillDamage(runtimeParameter)
		if (logger.isDebugEnabled()) {
			logger.debug("player_2_3_1_closure rate: " + rate + " value: " + value)
		}

		G2C_TriggerMultiTargetSkill response = new G2C_TriggerMultiTargetSkill(fightSkill.getRefId(), caster, direction)

		Collection<FightSprite> sprites = GameSceneHelper.getFightSprites(caster.getCrtScene(), caster, caster.getCrtPosition(), (byte) direction, 3, 6)
		Collection<FightSprite> majorSprites = GameSceneHelper.getFightSprites(caster.getCrtScene(), caster, caster.getCrtPosition(), (byte) direction, 1, 6)
		for(FightSprite sprite : sprites) {
			if (sprite instanceof Player && caster.isInSafeRegion()) {
				continue;
			}
			if((sprite instanceof Player && sprite.isInSafeRegion()) || (sprite instanceof Monster && !((Monster)sprite).getMonsterRef().isRegularMonster())) {
				continue;
			}
			MGFightProcessComponent<?> fightProcessComponent = (MGFightProcessComponent<?>) caster.getTagged(MGFightProcessComponent.Tag);
			if(!fightProcessComponent.isValidAttackState(sprite, caster,MGFightProcessHelper.M_ATTACK)){
				continue;
			}
			if (majorSprites.contains(sprite)) {
				RuntimeResult result = MGFightSkillRuntimeCommon.basicAttack(fightSkill, caster, sprite, rate, value)
				FightSkillResult skillResult = result.getData().getComponent(FightSkillResult.class)
				response.addSkillResult(skillResult)
				continue;
			}
			RuntimeResult result = MGFightSkillRuntimeCommon.basicAttack(fightSkill, caster, sprite, rate1, value)
			FightSkillResult skillResult = result.getData().getComponent(FightSkillResult.class)
			response.addSkillResult(skillResult)
		}
		GameSceneHelper.broadcastMessageToAOI(caster, response)

	}

	/**
	 * 雷电术 ——主动目标精灵技能
	 * 对目标造成X%+Y点法术伤害
	 */
	Closure<RuntimeResult> player_2_4_closure = { FightSkill fightSkill, FightSprite caster, FightSprite target ->
		MGFightProcessComponent<?> fightProcessComponent = (MGFightProcessComponent<?>) caster.getTagged(MGFightProcessComponent.Tag);
		if(!fightProcessComponent.isValidAttackState(target, caster, MGFightProcessHelper.M_ATTACK)){
			return;
		}
		PropertyDictionary parameterPd = fightSkill.getLevelRef().getRuntimeParameter()
		int damageValueOfSkill = MGPropertyAccesser.getSkillDamage(parameterPd) ;
		double damageRateOfSkill = MGPropertyAccesser.getSkillDamageRate(parameterPd) / 100.0;
		RuntimeResult result = MGFightSkillRuntimeCommon.basicAttack(fightSkill, caster, target, damageRateOfSkill, damageValueOfSkill);

		G2C_TriggerSingleTargetSkill response = new G2C_TriggerSingleTargetSkill(fightSkill.getRefId(), caster, target, result.getData().getComponent(
				FightSkillResult.class));
		GameSceneHelper.broadcastMessageToAOI(caster, response)

		return result
	}

	/**
	 * 雷电术拓展1
	 * 雷电术附加麻痹效果，被击中的目标有15%的概率被麻痹2秒麻痹：操作无效，身体程序染色成灰色
	 */
	Closure<RuntimeResult> player_2_4_1_closure = { FightSkill fightSkill, FightSprite caster, FightSprite target ->
		MGFightProcessComponent<?> fightProcessComponent = (MGFightProcessComponent<?>) caster.getTagged(MGFightProcessComponent.Tag);
		if(!fightProcessComponent.isValidAttackState(target, caster, MGFightProcessHelper.M_ATTACK)){
			return;
		}

		boolean dumbed = SFRandomUtils.random100() <= 15
		if(dumbed) {
			String dumbedBuffId = "buff_state_9"
			MGFightSkillRuntimeCommon.addBuff(caster, target, dumbedBuffId, 2000)
		}

		return player_2_4_closure.call(fightSkill, caster, target)
	}

	/**
	 * 瞬间移动 ——主动朝向技能
	 * 在当前地图随机移动,技能等级减少冷却时间
	 */
	Closure<RuntimeResult> player_2_5_closure = {FightSkill fightSkill, FightSprite caster, byte direction ->
		SceneGrid grid = GameSceneHelper.getRandomWalkableGrid(caster.getCrtScene())
		if(grid == null) {
			return;
		}
		// call G2C_TriggerSingleTargetSkill before caster jumpTo to set caster's casting position
		FightSkillResultImpl skillResult = new FightSkillResultImpl(0, caster, caster);
		G2C_TriggerSingleTargetSkill response = new G2C_TriggerSingleTargetSkill(fightSkill.getRefId(), caster, caster, skillResult)

		Player player = (Player) caster;
		Position beforeCastPos = new Position(player.getCrtPosition().getX(),player.getCrtPosition().getY());
		player.getPlayerSceneComponent().switchTo(caster.getCrtScene(),grid.getColumn(), grid.getRow())

		if (logger.isDebugEnabled()) {
			logger.debug("瞬间移动  caster current pos: " + caster.getCrtPosition())
		}

		// notify client
		skillResult.addSkillEffect(new SkillEffectMove(caster, beforeCastPos, new Position(grid.getColumn(), grid.getRow())))
		RuntimeResult result = RuntimeResult.OK()
		result.getData().addComponent(skillResult)

		GameSceneHelper.broadcastMessageToAOI(caster, response)

		return result
	}

	/**
	 * 大火球 ——主动目标精灵技能
	 * 对目标造成X%+Y点法术伤害
	 */
	Closure<RuntimeResult> player_2_6_closure = {FightSkill fightSkill, FightSprite caster, FightSprite target ->
		PropertyDictionary runtimeParameter = fightSkill.getLevelRef().getRuntimeParameter()
		double rate = MGPropertyAccesser.getSkillDamageRate(runtimeParameter) / 100.0
		int value = MGPropertyAccesser.getSkillDamage(runtimeParameter)
		int attackSpeedPer = MGPropertyAccesser.getAtkSpeedPer(runtimeParameter)

		MGFightProcessComponent<?> fightProcessComponent = (MGFightProcessComponent<?>) caster.getTagged(MGFightProcessComponent.Tag);
		if(!fightProcessComponent.isValidAttackState(target, caster,MGFightProcessHelper.M_ATTACK)){
			return;
		}

		RuntimeResult result = MGFightSkillRuntimeCommon.basicAttack(fightSkill, caster, target, rate, value);

		G2C_TriggerSingleTargetSkill response = new G2C_TriggerSingleTargetSkill(fightSkill.getRefId(), caster, target, result.getData().getComponent(
				FightSkillResult.class));
		GameSceneHelper.broadcastMessageToAOI(caster, response)

		return result;
	}

	/**
	 * 大火球拓展1
	 * 大火球附带灼烧效果灼烧：每秒造成普攻魔伤*30%的伤害，持续5秒，最多叠加3个同类BUFF
	 */
	Closure<RuntimeResult> player_2_6_1_closure = {FightSkill fightSkill, FightSprite caster, FightSprite target ->

		RuntimeResult result = player_2_6_closure.call(fightSkill, caster, target)

		String burningBuffId = "buff_state_6"
		long durationMillis = 5000
		MGFightSpriteBuffComponent fightSpriteBuffComponent = (MGFightSpriteBuffComponent) target.getTagged(MGFightSpriteBuffComponent.Tag);
		MGFightSpriteBuffRef ref = (MGFightSpriteBuffRef) GameRoot.getGameRefObjectManager().getManagedObject(burningBuffId);
		MGFightSpriteBuff buff = new MGFightSpriteBuff(ref, caster, target, durationMillis);
		MGPropertyAccesser.setOrPutAttachRepeatCount(buff.getSpecialProperty(), (byte)3);
		// damage
		PropertyDictionary pd = caster.getFightPropertyMgrComponent().getFightPropertyMgr().getSnapshotByNew().getPropertyDictionary()
		PropertyDictionary pd1 = target.getFightPropertyMgrComponent().getFightPropertyMgr().getSnapshotByNew().getPropertyDictionary()

		int defens = MGFightProcessHelper.defenceValue(MGFightProcessHelper.M_ATTACK, pd, pd1)
		int attack = MGFightProcessHelper.attackValue(true, MGFightProcessHelper.M_ATTACK, pd)

		attack = (attack-defens) * 0.3 > 0 ? (attack-defens) * 0.3 : 1;

		MGPropertyAccesser.setOrPutHP(buff.getSpecialProperty(),attack);
		fightSpriteBuffComponent.getFightSpriteBuffMgr().attachFightSpriteBuff(buff);

		return result

	}

	/**
	 * 爆裂火焰 ——主动目标精灵技能
	 * 对目标以及以目标为中心的3*3范围内的所有目标造成X%+Y点法术伤害
	 */
	Closure<RuntimeResult> player_2_7_closure = {FightSkill fightSkill, FightSprite caster, FightSprite targetSprite ->
		int row = 3; int column = 3;
		return player_2_7_closure_base.call(fightSkill, caster, targetSprite, row, column)
	}

	Closure<RuntimeResult> player_2_7_closure_base = {FightSkill fightSkill, FightSprite caster, FightSprite targetSprite, int row, int column ->
		Collection<FightSprite> fightSprites = GameSceneHelper.getFightSprites(targetSprite.getCrtScene(), caster, targetSprite.getCrtPosition(), row, column)

		RuntimeResult result = RuntimeResult.OK();

		G2C_TriggerMultiTargetSkill response = new G2C_TriggerMultiTargetSkill(fightSkill.getRefId(), caster, targetSprite.getCrtPosition())

		PropertyDictionary parameterPd = fightSkill.getLevelRef().getRuntimeParameter()
		int damageValueOfSkill = MGPropertyAccesser.getSkillDamage(parameterPd) ;
		double damageRateOfSkill = MGPropertyAccesser.getSkillDamageRate(parameterPd) / 100.0;
		for(FightSprite sprite : fightSprites) {
			if((sprite instanceof Player && sprite.isInSafeRegion()) || (sprite instanceof Monster && !((Monster)sprite).getMonsterRef().isRegularMonster())) {
				continue;
			}
			MGFightProcessComponent<?> fightProcessComponent = (MGFightProcessComponent<?>) caster.getTagged(MGFightProcessComponent.Tag);
			if(!fightProcessComponent.isValidAttackState(sprite, caster,MGFightProcessHelper.M_ATTACK)){
				continue;
			}

			result = MGFightSkillRuntimeCommon.basicAttack(fightSkill, caster, sprite, damageRateOfSkill, damageValueOfSkill)

			FightSkillResult skillResult = result.getData().getComponent(FightSkillResult.class)
			response.addSkillResult(skillResult)
		}

		GameSceneHelper.broadcastMessageToAOI(caster, response)
		return result

	}

	/**
	 * 爆裂火焰拓展1
	 * 爆裂火焰攻击范围由3*3变成5*5，伤害不变
	 */
	Closure<RuntimeResult> player_2_7_1_closure = {FightSkill fightSkill, FightSprite caster, FightSprite targetSprite ->
		int row = 5; int column = 5;
		return player_2_7_closure_base.call(fightSkill, caster, targetSprite, row, column)
	}

	/**
	 * 火墙 ——主动地图格技能
	 * 选取之后对地图施放，以施放点为中心3*3的范围内的敌方目标每秒造成X%+Y点法术伤害
	 * 每点法术可以提高N点上限伤害
	 * 火墙每次计算伤害都=玩家进行一次伤害判断（要算技能效果，幸运，暴击等）
	 * 本次施放时取值的法术提高持续时间M秒
	 */
	Closure<RuntimeResult> player_2_8_closure = {FightSkill fightSkill, FightSprite caster, Position targetGrid ->
		int row = 3; int column = 3;
		fireWallSkill(fightSkill, caster, targetGrid, row, column)
	}

	/**
	 * 火墙拓展1
	 * 火墙范围变成5*5，每秒伤害提高为（50%，60%，70%根据火墙等级对应数据）的伤害
	 */
	Closure<RuntimeResult> player_2_8_1_closure = {FightSkill fightSkill, FightSprite caster, Position targetGrid ->
		int row = 5; int column = 5;
		fireWallSkill(fightSkill, caster, targetGrid, row, column)
		return RuntimeResult.OK()
	}

	/** 
	 * 疾光电影 ——主动朝向技能
	 * 逻辑同地狱火
	 */
	Closure<RuntimeResult> player_2_9_closure = {FightSkill fightSkill, FightSprite caster, byte direction ->
		int number = 6;

		PropertyDictionary parameterPd = fightSkill.getLevelRef().getRuntimeParameter()
		int damageValueOfSkill = MGPropertyAccesser.getSkillDamage(parameterPd) ;
		double damageRateOfSkill = MGPropertyAccesser.getSkillDamageRate(parameterPd) / 100.0;
		if (logger.isDebugEnabled()) {
			logger.debug("疾光电影 rate: " + damageRateOfSkill)
		}

		G2C_TriggerMultiTargetSkill response = new G2C_TriggerMultiTargetSkill(fightSkill.getRefId(), caster, direction)

		Collection<FightSprite> sprites = GameSceneHelper.getFightSprites(caster.getCrtScene(), caster.getCrtPosition(), (byte) direction, 1, 6)
		for(FightSprite sprite : sprites) {
			if (sprite instanceof Player && caster.isInSafeRegion()) {
				continue;
			}
			if((sprite instanceof Player && sprite.isInSafeRegion()) || (sprite instanceof Monster && !((Monster)sprite).getMonsterRef().isRegularMonster())) {
				continue;
			}
			MGFightProcessComponent<?> fightProcessComponent = (MGFightProcessComponent<?>) caster.getTagged(MGFightProcessComponent.Tag);
			if(!fightProcessComponent.isValidAttackState(sprite, caster,MGFightProcessHelper.M_ATTACK)){
				continue;
			}
			RuntimeResult result = MGFightSkillRuntimeCommon.basicAttack(fightSkill, caster, sprite, damageRateOfSkill, damageValueOfSkill)

			FightSkillResult skillResult = result.getData().getComponent(FightSkillResult.class)
			response.addSkillResult(skillResult)
			if (logger.isDebugEnabled()) {
				logger.debug("疾光电影  target: " + sprite.getId() + " damge: " + skillResult.getDamage())
			}
		}

		GameSceneHelper.broadcastMessageToAOI(caster, response)

		return RuntimeResult.OK()

	}

	/**
	 * 疾光电影 拓展1
	 * 变成连锁闪电，给每个击中的对象造成（80%，90%，100%根据疾光电影等级对应数据，弹跳不会造成伤害递减，最多弹跳5个对象，弹跳范围为自身为中心半径6格的范围
	 */
	Closure<RuntimeResult> player_2_9_1_closure = {FightSkill fightSkill, FightSprite caster, byte direction ->
		PropertyDictionary runtimeParameter = fightSkill.getLevelRef().getRuntimeParameter()
		double rate = MGPropertyAccesser.getSkillDamageRate(runtimeParameter) / 100.0

		RuntimeResult result = RuntimeResult.OK()
		G2C_TriggerMultiTargetSkill response = new G2C_TriggerMultiTargetSkill(fightSkill.getRefId(), caster, direction)
		FightSkillResultImpl skillResult = new FightSkillResultImpl(0, caster, caster);

		Collection<FightSprite> spriteCollection = GameSceneHelper.getFightSprites(caster.getCrtScene(), caster, caster.getCrtPosition(), 6);
		List<FightSprite> spriteList = spriteCollection.toArray();
		int size = spriteCollection.size();
		int number = size > 5 ? 5 : size;
		int[] randomIndexArray = SFRandomUtils.randomArray(0, size - 1, number);
		for (int i : randomIndexArray) {
			FightSprite sprite = spriteList.get(i)
			if (sprite instanceof Player && caster.isInSafeRegion()) {
				continue;
			}
			if((sprite instanceof Player && sprite.isInSafeRegion()) || (sprite instanceof Monster && !((Monster)sprite).getMonsterRef().isRegularMonster())) {
				continue;
			}
			MGFightProcessComponent<?> fightProcessComponent = (MGFightProcessComponent<?>) caster.getTagged(MGFightProcessComponent.Tag);
			if(!fightProcessComponent.isValidAttackState(sprite, caster,MGFightProcessHelper.M_ATTACK)){
				continue;
			}
			result = MGFightSkillRuntimeCommon.basicAttack(fightSkill, caster, sprite, rate, 0)
			skillResult = result.getData().getComponent(FightSkillResult.class)
			response.addSkillResult(skillResult)
		}
		GameSceneHelper.broadcastMessageToAOI(caster, response)
	}

	/**
	 * 地狱雷光 ——主动地图格技能
	 * 以自身为中心施放，作用范围为3*3
	 * 对敌方目标造成X%+Y点法术伤害
	 */
	Closure<RuntimeResult> player_2_10_closure = {FightSkill fightSkill, FightSprite caster, byte direction ->
		int row = 3; int column = 3;
		Collection<FightSprite> fightSprites = GameSceneHelper.getFightSprites(caster.getCrtScene(), caster, caster.getCrtPosition(), row, column)

		G2C_TriggerMultiTargetSkill response = new G2C_TriggerMultiTargetSkill(fightSkill.getRefId(), caster, direction)

		PropertyDictionary parameterPd = fightSkill.getLevelRef().getRuntimeParameter()
		int damageValueOfSkill = MGPropertyAccesser.getSkillDamage(parameterPd) ;
		double damageRateOfSkill = MGPropertyAccesser.getSkillDamageRate(parameterPd) / 100.0;
		for(FightSprite sprite : fightSprites) {
			if (sprite instanceof Player && caster.isInSafeRegion()) {
				continue;
			}
			if((sprite instanceof Player && sprite.isInSafeRegion()) || (sprite instanceof Monster && !((Monster)sprite).getMonsterRef().isRegularMonster())) {
				continue;
			}
			MGFightProcessComponent<?> fightProcessComponent = (MGFightProcessComponent<?>) caster.getTagged(MGFightProcessComponent.Tag);
			if(!fightProcessComponent.isValidAttackState(sprite, caster,MGFightProcessHelper.M_ATTACK)){
				continue;
			}
			RuntimeResult result = MGFightSkillRuntimeCommon.basicAttack(fightSkill, caster, sprite, damageRateOfSkill, damageValueOfSkill)

			FightSkillResult skillResult = result.getData().getComponent(FightSkillResult.class)
			response.addSkillResult(skillResult)
		}

		GameSceneHelper.broadcastMessageToAOI(caster, response)

		return RuntimeResult.OK()
	}

	/**
	 * 地狱雷光拓展1
	 * 地狱雷光施放时，有30%的概率额外施放1次（2次施放，第二个比第一个慢大约100MS出来）    
	 * PS:此处是判定流程走2次
	 */
	Closure<RuntimeResult> player_2_10_1_closure = {FightSkill fightSkill, FightSprite caster, byte direction ->
		boolean secondTrue = SFRandomUtils.random100() <= 30
		if(secondTrue) {
			player_2_10_closure.call(fightSkill, caster, direction)
		}

		return player_2_10_closure.call(fightSkill, caster, direction)
	}

	/**
	 * 魔法盾 ——主动目标精灵技能
	 * 施放后自身有一个保护盾，持续一定的时间
	 * 保护盾有生命值，生命值=1000+N点，N=本次施放时取值的法术*修正值
	 * 保护盾每次会吸收X%的所有伤害，生命值耗尽则会消失
	 * 本次施放时取值的法术提高持续时间M秒
	 */
	Closure<RuntimeResult> player_2_11_closure = {FightSkill fightSkill, FightSprite caster, FightSprite targetSprite->
		PropertyDictionary parameterPd = fightSkill.getLevelRef().getRuntimeParameter()
		double skillDamageRate = MGPropertyAccesser.getSkillDamageRate(parameterPd) / 100.0
		PropertyDictionary pd = caster.getFightPropertyMgrComponent().getFightPropertyMgr().getSnapshotByNew().getPropertyDictionary()
		int maxMAtk = MGPropertyAccesser.getMaxMAtk(pd)
		int minMAtk = MGPropertyAccesser.getMinMAtk(pd)
		int attack = maxMAtk <= minMAtk ? maxMAtk :  SFRandomUtils.random(minMAtk, maxMAtk)
		int duration = 30 + attack / 50;
		duration = duration < 300 ? duration : 300;
		duration = duration * 1000;
		int maxHp = attack * skillDamageRate + 1000

		return player_2_11_closure_base.call(fightSkill, caster, targetSprite, duration, maxHp);
	}

	Closure<RuntimeResult> player_2_11_closure_base = {FightSkill fightSkill, FightSprite caster, FightSprite targetSprite, int durationMillis, int maxHp ->
		RuntimeResult result = RuntimeResult.OK();
		targetSprite = caster;

		PropertyDictionary parameterPd = fightSkill.getLevelRef().getRuntimeParameter()
		int skillDamageRate = MGPropertyAccesser.getSkillDamageRate(parameterPd)

		String buffRefId = "buff_skill_3";
		MGFightSpriteBuffComponent fightSpriteBuffComponent = (MGFightSpriteBuffComponent) targetSprite.getTagged(MGFightSpriteBuffComponent.Tag);
		MGFightSpriteBuffRef ref = (MGFightSpriteBuffRef) GameRoot.getGameRefObjectManager().getManagedObject(buffRefId)
		MGFightSpriteBuff buff = new MGFightSpriteBuff(ref, caster, targetSprite, durationMillis)
		MGPropertyAccesser.setOrPutTotalValue(buff.getSpecialProperty(), maxHp)
		MGPropertyAccesser.setOrPutSkillDamageRate(buff.getSpecialProperty(), skillDamageRate)
		result = fightSpriteBuffComponent.getFightSpriteBuffMgr().attachFightSpriteBuff(buff)

		return result;

	}

	/**
	 * 魔法盾拓展1
	 * 魔法盾状态时，主角自身附加25%的物理和魔法闪避百分比
	 */
	Closure<RuntimeResult> player_2_11_1_closure = {FightSkill fightSkill, FightSprite caster, FightSprite target ->
		//魔法盾
		PropertyDictionary parameterPd = fightSkill.getLevelRef().getRuntimeParameter()
		int rate = MGPropertyAccesser.getSkillDamageRate(parameterPd)
		double skillDamageRate = rate/ 100.0
		PropertyDictionary pd = caster.getFightPropertyMgrComponent().getFightPropertyMgr().getSnapshotByNew().getPropertyDictionary()
		int maxMAtk = MGPropertyAccesser.getMaxMAtk(pd)
		int minMAtk = MGPropertyAccesser.getMinMAtk(pd)
		int attack = maxMAtk <= minMAtk ? maxMAtk :  SFRandomUtils.random(minMAtk, maxMAtk)
		int durationMillis = 30 + attack / 50;
		durationMillis = durationMillis < 300 ? durationMillis : 300;
		durationMillis = durationMillis * 1000;
		int maxHp = attack * skillDamageRate + 1000

		// 增加自身物理和魔法闪避
		String effectBuffId = "buff_skill_3_1"

		MGFightSpriteBuffComponent fightSpriteBuffComponent = (MGFightSpriteBuffComponent) target.getTagged(MGFightSpriteBuffComponent.Tag);
		MGFightSpriteBuffRef ref = (MGFightSpriteBuffRef) GameRoot.getGameRefObjectManager().getManagedObject(effectBuffId)
		MGFightSpriteBuff buff = new MGFightSpriteBuff(ref, caster, target, durationMillis)
		MGPropertyAccesser.setOrPutMDodgePer(buff.getSpecialProperty(), 25)
		MGPropertyAccesser.setOrPutPDodgePer(buff.getSpecialProperty(), 25)
		MGPropertyAccesser.setOrPutTotalValue(buff.getSpecialProperty(), maxHp)
		MGPropertyAccesser.setOrPutSkillDamageRate(buff.getSpecialProperty(), rate)
		fightSpriteBuffComponent.getFightSpriteBuffMgr().attachFightSpriteBuff(buff)

		return RuntimeResult.OK();
	}

	/**
	 * 冰咆哮 ——主动目标精灵技能
	 * 效果同爆裂火焰
	 */
	Closure<RuntimeResult> player_2_12_closure = {FightSkill fightSkill, FightSprite caster, FightSprite targetSprite ->
		int row = 3; int column = 3;
		Collection<FightSprite> fightSprites = GameSceneHelper.getFightSprites(targetSprite.getCrtScene(), caster, targetSprite.getCrtPosition(), row, column)
		G2C_TriggerMultiTargetSkill response = new G2C_TriggerMultiTargetSkill(fightSkill.getRefId(), caster, targetSprite.getCrtPosition())
		RuntimeResult result = RuntimeResult.OK()

		PropertyDictionary parameterPd = fightSkill.getLevelRef().getRuntimeParameter()
		int damageValueOfSkill = MGPropertyAccesser.getSkillDamage(parameterPd) ;
		if (damageValueOfSkill <= 0) {
			damageValueOfSkill = 0
		}
		double damageRateOfSkill = MGPropertyAccesser.getSkillDamageRate(parameterPd) / 100.0;
		for(FightSprite sprite : fightSprites) {
			if((sprite instanceof Player && sprite.isInSafeRegion()) || (sprite instanceof Monster && !((Monster)sprite).getMonsterRef().isRegularMonster())) {
				continue;
			}
			MGFightProcessComponent<?> fightProcessComponent = (MGFightProcessComponent<?>) caster.getTagged(MGFightProcessComponent.Tag);
			if(!fightProcessComponent.isValidAttackState(sprite, caster,MGFightProcessHelper.M_ATTACK)){
				continue;
			}
			result = MGFightSkillRuntimeCommon.basicAttack(fightSkill, caster, sprite, damageRateOfSkill, damageValueOfSkill)

			FightSkillResult skillResult = result.getData().getComponent(FightSkillResult.class)
			response.addSkillResult(skillResult)
		}

		GameSceneHelper.broadcastMessageToAOI(caster, response)
		return result
	}

	/**
	 * 冰咆哮拓展1
	 * 冰咆哮附加缓速效果，持续2秒缓速：使移动速度降低30%，多个效果叠加时，取最后的效果为唯一效果
	 */
	Closure<RuntimeResult> player_2_12_1_closure = {FightSkill fightSkill, FightSprite caster, FightSprite target ->

		RuntimeResult result = player_2_12_closure.call(fightSkill, caster, target)

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
}
