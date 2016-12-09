package newbee.morningGlory.mmorpg.monster;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Collection;

import newbee.morningGlory.mmorpg.monster.gameEvent.SkillMonsterAttack_GE;
import newbee.morningGlory.mmorpg.monster.gameEvent.SkillMonsterLevelUp_GE;
import newbee.morningGlory.mmorpg.player.pk.MGPlayerPKComponent;
import newbee.morningGlory.mmorpg.player.pk.MGPlayerPKMgr;
import newbee.morningGlory.mmorpg.player.pk.MGPlayerPKModel;
import newbee.morningGlory.mmorpg.player.summons.PlayerSummonMonsterComponent;
import newbee.morningGlory.mmorpg.player.summons.SummonMonsterExpComponent;
import newbee.morningGlory.mmorpg.sprite.MGFightProcessComponent;
import newbee.morningGlory.mmorpg.sprite.MGFightProcessHelper;
import newbee.morningGlory.mmorpg.sprite.player.fightSkill.MGFightSkillRuntimeCommon;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import sophia.foundation.property.PropertyDictionary;
import sophia.foundation.util.Position;
import sophia.game.GameRoot;
import sophia.game.component.communication.GameEvent;
import sophia.mmorpg.MMORPGContext;
import sophia.mmorpg.base.scene.GameScene;
import sophia.mmorpg.base.scene.GameSceneHelper;
import sophia.mmorpg.base.scene.aoi.SceneAOILayer;
import sophia.mmorpg.base.scene.mgr.MonsterMgrComponent;
import sophia.mmorpg.base.sprite.FightSprite;
import sophia.mmorpg.base.sprite.ai.SpritePerceiveComponent;
import sophia.mmorpg.base.sprite.aoi.SpriteAOIComponent;
import sophia.mmorpg.base.sprite.fightSkill.FightSkill;
import sophia.mmorpg.base.sprite.fightSkill.FightSkillResult;
import sophia.mmorpg.base.sprite.fightSkill.FightSkillRuntimeHelper;
import sophia.mmorpg.base.sprite.fightSkill.gameevent.AfterAttack_GE;
import sophia.mmorpg.base.sprite.state.global.FightState;
import sophia.mmorpg.base.sprite.state.movement.ChaseState;
import sophia.mmorpg.base.sprite.state.movement.PatrolState;
import sophia.mmorpg.monster.Monster;
import sophia.mmorpg.monster.ai.MonsterAIHelper;
import sophia.mmorpg.monster.gameEvent.MonsterDead_GE;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.fightSkill.event.G2C_TriggerSingleTargetSkill;
import sophia.mmorpg.player.fightSkill.ref.SkillRef;
import sophia.mmorpg.player.gameEvent.PlayerDead_GE;
import sophia.mmorpg.player.gameEvent.PlayerSwitchScene_GE;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;
import sophia.mmorpg.ref.editorgen.MGPropertySymbolDefines;
import sophia.mmorpg.utils.RuntimeResult;

import com.google.common.base.Strings;

public class MGMonsterPerceiveComponent extends SpritePerceiveComponent<Monster> {
	private static final Logger logger = Logger.getLogger(MGMonsterPerceiveComponent.class);

	private static final String Monster_GameSprite_Type = Monster.class.getSimpleName();

	private static final String Player_GameSPrite_Type = Player.class.getSimpleName();

	public static final String Tag = "MGMonsterPerceiveComponent";

	public static final String MonsterDead_GE_Id = MonsterDead_GE.class.getSimpleName();

	public static final String AfterAttack_GE_Id = AfterAttack_GE.class.getSimpleName();

	public static final String SkillMonsterLevelUp_GE_Id = SkillMonsterLevelUp_GE.class.getSimpleName();

	public static final String PlayerDead_GE_ID = PlayerDead_GE.class.getSimpleName();

	public static final String PlayerSwitchScene_GE_Id = PlayerSwitchScene_GE.class.getSimpleName();
	
	private static final long waitToSwitch = 3000;
	private static final long attackTimeLag = 1000;

	private long focusTargetTime = 0;
	private Monster summonMonster;
	private FightSprite firstTarget; // 攻击目标
	private FightSprite secondTarget;

	public synchronized FightSprite getFirstTarget() {
		return firstTarget;
	}

	public synchronized void setFirstTarget(FightSprite firstTarget) {
		this.firstTarget = firstTarget;
	}

	public FightSprite getSecondTarget() {
		return secondTarget;
	}

	public void setSecondTarget(FightSprite secondTarget) {
		this.secondTarget = secondTarget;
	}

	public synchronized long getFocusTargetTime() {
		return focusTargetTime;
	}

	public synchronized void setFocusTargetTime(long focusTargetTime) {
		this.focusTargetTime = focusTargetTime;
	}

	@Override
	public void ready() {
		summonMonster = getConcreteParent();
		addInterGameEventListener(PlayerDead_GE_ID);
		addInterGameEventListener(AfterAttack_GE_Id);
		addInterGameEventListener(MonsterDead_GE_Id);
		addInterGameEventListener(SceneTick_GE_Id);
		addInterGameEventListener(FightSpriteOwnerInjured_GE_Id);
		addInterGameEventListener(FightSpriteInjured_GE_Id);
		addInterGameEventListener(SkillMonsterLevelUp_GE_Id);
		addInterGameEventListener(PlayerSwitchScene_GE_Id);
		super.ready();
	}

	@Override
	public void suspend() {
		removeInterGameEventListener(PlayerDead_GE_ID);
		removeInterGameEventListener(AfterAttack_GE_Id);
		removeInterGameEventListener(MonsterDead_GE_Id);
		removeInterGameEventListener(SceneTick_GE_Id);
		removeInterGameEventListener(FightSpriteOwnerInjured_GE_Id);
		removeInterGameEventListener(FightSpriteInjured_GE_Id);
		removeInterGameEventListener(SkillMonsterLevelUp_GE_Id);
		removeInterGameEventListener(PlayerSwitchScene_GE_Id);
		super.suspend();
	}

	@Override
	public void reset() {
		setFirstTarget(null);
		summonMonster.getCrtScene().getMonsterMgrComponent().leaveWorld(summonMonster);
		super.reset();
	}

	private boolean canCastSkill(long now) {
		return (now - getLastAttackTime()) > attackTimeLag;
	}

	@Override
	public void handleGameEvent(GameEvent<?> event) {
		if (event.isId(SceneTick_GE_Id)) {
			long now = System.currentTimeMillis();
			if (selfCheckUnactBuffer(summonMonster)) {
				return;
			}
			if (!isLegalMonster(now)) {
				reset();
				return;
			}
			if (summonMonster.getMonsterRef().getId().equals("monster_skill_1")) {
				fireWallAttack(now);
				// 火墙AI到此结束
				return;
			}

			summonMonster.recoverHP(now);
			monsterTransferToOwner();
			if (!monsterGetBack()) {
				while (!switchTarget(now) && validTarget(getFirstTarget())) {
					chaseAndAttack(getFirstTarget(), now);
					return;
				}
			}
		} else if (event.isId(AfterAttack_GE_Id)) {
			AfterAttack_GE ge = (AfterAttack_GE) event.getData();
			FightSprite target = ge.getTarget();
			if (ge.getAttacker() == summonMonster.getOwner()) {
				if (validTarget(target)) {
					setSecondTarget(target); // 主人正在打的目标
				}
			}
			/**
			 * 暂时屏蔽主人受攻击情况 } else if (event.isId(FightSpriteOwnerInjured_GE_Id))
			 * { FightSpriteOwnerInjured_GE ge = (FightSpriteOwnerInjured_GE)
			 * event.getData(); FightSprite sprite = ge.getAttacker(); if
			 * (!validTarget(sprite)) { return; } if
			 * (!validTarget(getSecondTarget())) { setSecondTarget(sprite);
			 * return; }
			 */
		} else if (event.isId(MonsterDead_GE_Id)) {
			MonsterDead_GE ge = (MonsterDead_GE) event.getData();
			FightSprite fightSprite = ge.getMonster();
			if (fightSprite.getId().equals(summonMonster.getId())) {
				reset();
			}
			ge.setAttacker(summonMonster.getOwner());
			GameEvent<MonsterDead_GE> monsterDead = GameEvent.getInstance(MonsterDead_GE_Id, ge);
			sendGameEvent(monsterDead, summonMonster.getOwner().getId());
			/**
			 * 暂时屏蔽自己受攻击情况 } else if (event.isId(FightSpriteInjured_GE_Id)) {
			 * FightSpriteInjured_GE ge = (FightSpriteInjured_GE)
			 * event.getData(); FightSprite fightSprite = ge.getAttacker(); if
			 * (!validTarget(fightSprite)) { return; } if
			 * (!validTarget(getSecondTarget())) { setSecondTarget(fightSprite);
			 * }
			 */
		} else if (event.isId(SkillMonsterLevelUp_GE_Id)) {
			SkillMonsterLevelUp_GE ge = (SkillMonsterLevelUp_GE) event.getData();
			String nextMonsterId = ge.getNextLevelRefId();
			int damage = 0;
			if (summonMonster.getMonsterRef().getProperty().contains(MGPropertySymbolDefines.SkillDamage_Id)) {
				damage = MGPropertyAccesser.getSkillDamage(summonMonster.getMonsterRef().getProperty());
			}
			// 提取升级前召唤怪信息
			FightSprite owner = summonMonster.getOwner();
			PlayerSummonMonsterComponent playerSummonComponent = (PlayerSummonMonsterComponent) owner.getTagged(PlayerSummonMonsterComponent.Tag);
			GameScene currentScene = summonMonster.getCrtScene();
			Position targetGrid = summonMonster.getCrtPosition();

			// 召唤怪移除
			String monsterRefId = summonMonster.getMonsterRef().getId();
			String skillName = "";
			int monsterSight = 0;
			if (monsterRefId.startsWith("monster_skill_ds_11")) {
				String[] j = monsterRefId.split("11_");
				monsterSight = Integer.parseInt(j[1]);
				if (monsterSight <= 7) {
					skillName = "skill_ds_11";
				} else if (monsterSight > 7 && monsterSight <= 14) {
					skillName = "skill_ds_11_1";
					monsterSight = monsterSight - 7;
				} else if (monsterSight > 14 && monsterSight <= 21) {
					skillName = "skill_ds_11_2";
					monsterSight = monsterSight - 14;
				} else if (monsterSight > 21 && monsterSight <= 28) {
					skillName = "skill_ds_11_3";
					monsterSight = monsterSight - 21;
				}
			} else if (monsterRefId.startsWith("monster_skill_ds_5")) {
				String[] j = monsterRefId.split("5_");
				String level = j[1];
				monsterSight = Integer.parseInt(level);
				if (monsterSight <= 7) {
					skillName = "skill_ds_5";
				} else if (monsterSight > 7 && monsterSight <= 14) {
					skillName = "skill_ds_5_1";
					monsterSight = monsterSight - 7;
				} else if (monsterSight > 14 && monsterSight <= 21) {
					skillName = "skill_ds_5_2";
					monsterSight = monsterSight - 14;
				}
			}
			if (StringUtils.isEmpty(skillName)) {
				logger.error("Can Not Get SummonMonster Skill Name");
				return;
			}
			Player player = (Player) owner;
			FightSkill fightSkill = player.getPlayerFightSkillComponent().getPlayerFightSkillTree().getFightSkill(skillName);
			// 技能升级：1——3， 2——5， 3——7
			if (monsterSight >= fightSkill.getLevel() * 2 + 1) {
				return;
			}
			summonMonster.getCrtScene().getMonsterMgrComponent().leaveWorld(summonMonster);
			MonsterMgrComponent monsterMgr = currentScene.getMonsterMgrComponent();
			Monster nextMonster = monsterMgr.createMonster(nextMonsterId);
			String unionName = MGPropertyAccesser.getUnionName(player.getProperty());
			if (!Strings.isNullOrEmpty(unionName)) {
				MGPropertyAccesser.setOrPutUnionName(nextMonster.getProperty(), unionName);
			} else {
				MGPropertyAccesser.setOrPutUnionName(nextMonster.getProperty(), "");
			}
			nextMonster.setPerceiveComponent((MGMonsterPerceiveComponent) nextMonster.createComponent(MGMonsterPerceiveComponent.class));
			nextMonster.setOwner(player);
			// 添加到管理器
			nextMonster.createComponent(SummonMonsterExpComponent.class, SummonMonsterExpComponent.Tag);
			playerSummonComponent.setSummonMonster(nextMonster);
			owner.setSummonMonster(nextMonster);

			PropertyDictionary runtimeParameter = fightSkill.getLevelRef().getRuntimeParameter();
			int skillDamageRate = MGPropertyAccesser.getSkillDamageRate(runtimeParameter);
			PropertyDictionary playerPd = owner.getFightPropertyMgrComponent().getFightPropertyMgr().getSnapshotByNew().getPropertyDictionary();
			int playerMaxTao = MGPropertyAccesser.getMaxTao(playerPd);
			int playerMinTao = MGPropertyAccesser.getMinTao(playerPd);
			int playerMaxModify = playerMaxTao * skillDamageRate / 100;
			int playerMinModify = playerMinTao * skillDamageRate / 100;

			/** 设置新的道法攻击 */
			nextMonster.getFightPropertyMgrComponent().getFightPropertyMgr().getModifyTransaction().modifySomeProperty(MGPropertySymbolDefines.MaxTao_Id, playerMaxModify);
			nextMonster.getFightPropertyMgrComponent().getFightPropertyMgr().getModifyTransaction().modifySomeProperty(MGPropertySymbolDefines.MinTao_Id, playerMinModify);

			/** 设置新的魔法攻击 */
			nextMonster.getFightPropertyMgrComponent().getFightPropertyMgr().getModifyTransaction().modifySomeProperty(MGPropertySymbolDefines.MaxMAtk_Id, playerMaxModify);
			nextMonster.getFightPropertyMgrComponent().getFightPropertyMgr().getModifyTransaction().modifySomeProperty(MGPropertySymbolDefines.MinMAtk_Id, playerMinModify);

			/** 设置新的物理攻击 */
			nextMonster.getFightPropertyMgrComponent().getFightPropertyMgr().getModifyTransaction().modifySomeProperty(MGPropertySymbolDefines.MaxPAtk_Id, playerMaxModify);
			nextMonster.getFightPropertyMgrComponent().getFightPropertyMgr().getModifyTransaction().modifySomeProperty(MGPropertySymbolDefines.MinPAtk_Id, playerMinModify);

			nextMonster.setBirthPosition(targetGrid);
			MGPropertyAccesser.setOrPutSkillDamage(nextMonster.getMonsterRef().getProperty(), damage);
			// 进入场景
			monsterMgr.enterWorld(nextMonster, currentScene, targetGrid.getX(), targetGrid.getY());// 新宠物生成
		} else if (event.isId(PlayerDead_GE_ID)) {
			PlayerDead_GE ge = (PlayerDead_GE) event.getData();
			Player player = ge.getPlayer();
			if (summonMonster.getOwner() == player) {
				reset();
			}
		} else if (event.isId(PlayerSwitchScene_GE_Id)) {
			PlayerSwitchScene_GE ge = (PlayerSwitchScene_GE) event.getData();
			if (summonMonster.getOwner() != null) {
				monsterSwitchToOwner(ge);
			}
		}
		super.handleGameEvent(event);
	}

	private void fireWallAttack(long now) {
		Collection<FightSprite> spriteCollection = GameSceneHelper.getFightSprites(summonMonster.getCrtScene(), summonMonster.getCrtPosition(), 0);
		for (FightSprite sprite : spriteCollection) {
			String str = sprite.getGameSpriteType();
			if (str.equals(Monster_GameSprite_Type) || str.equals(Player_GameSPrite_Type)) {
				startAttack(sprite, now);
			}
		}
	}

	private void startAttack(FightSprite fightSprite, long now) {
		if (!validTarget((FightSprite) fightSprite)) {
			return;
		}
		if (fightSprite instanceof Monster) {
			Monster monster1 = (Monster) fightSprite;
			boolean isSkillMonster = monster1.getMonsterRef().isSkillSummon();
			boolean inSafeRegion = monster1.isInSafeRegion();
			if (isSkillMonster) {
				return;
			}
			if (inSafeRegion && !monster1.getMonsterRef().isRegularMonster()) {
				return;
			}
		}
		if (fightSprite instanceof Player) {
			Player player = (Player) fightSprite;
			boolean isInSafeRegion = player.isInSafeRegion();
			if (isInSafeRegion) {
				return;
			}
		}
		if (summonMonster.isEnemyTo(fightSprite)) {
			SkillRef ref = (SkillRef) GameRoot.getGameRefObjectManager().getManagedObject(SkillRef.basicAttackRefId);
			FightSkill skill = new FightSkill(SkillRef.basicAttackRefId, ref);
			if (canCastSkill(now)) {
				fireWallCostSkill(skill, fightSprite, now);
			}
		}
	}

	private void fireWallCostSkill(FightSkill skill, FightSprite fightSprite, long now) {
		int damage = MGPropertyAccesser.getSkillDamage(summonMonster.getMonsterRef().getProperty());
		RuntimeResult result = MGFightSkillRuntimeCommon.basicAttack(skill, summonMonster, fightSprite, MGFightProcessHelper.M_ATTACK, 1, damage);
		this.updateLastAttackTime(now);
		updateLastAttackTime(now);

		G2C_TriggerSingleTargetSkill response = new G2C_TriggerSingleTargetSkill(skill.getRefId(), summonMonster, fightSprite, result.getData()
				.getComponent(FightSkillResult.class));
		GameSceneHelper.broadcastMessageToAOI(summonMonster, response);
	}

	/**
	 * 距离判断, 同场景召唤怪超过500px就传送回玩家身边， 不同场景，传送到玩家身边
	 * 
	 * @param summonMonster
	 */
	private void monsterTransferToOwner() {
		FightSprite owner = summonMonster.getOwner();
		GameScene selfScene = owner.getCrtScene();
		GameScene monsterScene = summonMonster.getCrtScene();
		Position selfPosition = owner.getCrtPosition();
		Position monsterPosition = summonMonster.getCrtPosition();
		if (selfScene.equals(monsterScene)) {
			int distance = GameSceneHelper.distance(monsterScene, monsterPosition, selfPosition);
			if (distance >= 10) {
				if (logger.isDebugEnabled()) {
					logger.debug("monster Transfer To Owner: " + " monster's Position: " + summonMonster.getCrtPosition() + "  owner's Position: " + selfPosition);
				}
				summonMonster.getPathComponent().jumpTo(selfPosition);
			}
		} else {
			summonMonster.getPathComponent().silentStop();
			SpriteAOIComponent<?> aoiComponent = summonMonster.getAoiComponent();
			aoiComponent.leaveScene(monsterScene);
			aoiComponent.enterScene(selfScene, selfPosition.getX(), selfPosition.getY());
			monsterScene.getMonsterMgrComponent().removeMonster(summonMonster); // 由于怪物没有重载leaveScene方法，这里手动移除旧场景怪物管理类中的召唤怪
			selfScene.getMonsterMgrComponent().addMonster(summonMonster);// 由于怪物没有重载leaveScene方法，这里手动在新场景怪物管理类中添加召唤怪
		}
	}

	private void monsterSwitchToOwner(PlayerSwitchScene_GE ge) {
		GameScene selfScene = ge.getDstScene();
		GameScene monsterScene = summonMonster.getCrtScene();
		Position selfPosition = new Position(ge.getDstX(), ge.getDstY());
		summonMonster.getPathComponent().silentStop();
		SpriteAOIComponent<?> aoiComponent = summonMonster.getAoiComponent();
		aoiComponent.leaveScene(monsterScene);
		aoiComponent.enterScene(selfScene, selfPosition.getX(), selfPosition.getY());
		monsterScene.getMonsterMgrComponent().removeMonster(summonMonster); 
		selfScene.getMonsterMgrComponent().addMonster(summonMonster);
	}

	/**
	 * 如果召唤怪不在战斗状态且与玩家距离超过4格，则跑到玩家身边
	 * 
	 * @param summonMonster
	 */
	private boolean monsterGetBack() {
		Position patrolPosition = summonMonster.getOwner().getCrtPosition();
		if (patrolPosition == null) {
			return false;
		}
		int distance = GameSceneHelper.distance(summonMonster.getCrtScene(), summonMonster.getCrtPosition(), patrolPosition);
		if (distance > 4 && getFirstTarget() == null) {
			summonMonster.changeState(PatrolState.PatrolState_Id);
			Position crtPosition = summonMonster.getCrtPosition();
			if (patrolPosition.getX() != crtPosition.getX() || patrolPosition.getY() != crtPosition.getY()) {
				if (logger.isDebugEnabled()) {
					logger.debug("monster startMove To Owner: " + " monster's Position: " + crtPosition + "  owner's Position: " + patrolPosition);
				}
				summonMonster.getPathComponent().startMove(crtPosition, patrolPosition);
				return true;
			}
		}
		return false;
	}

	@SuppressWarnings("rawtypes")
	public FightSprite getNearestEnemy(int radiusInGrid) {
		FightSprite ret = null;
		int minDistance = Integer.MAX_VALUE;

		Collection<FightSprite> sprites = GameSceneHelper.getFightSprites(summonMonster.getCrtScene(), summonMonster, summonMonster.getCrtPosition(), radiusInGrid);
		for (FightSprite sprite : sprites) {
			if (sprite instanceof Player) {
				Player temp = (Player) sprite;
				if (temp.equals(summonMonster.getOwner())) {
					continue;
				}
				MGPlayerPKComponent pkComponent = (MGPlayerPKComponent) summonMonster.getOwner().getTagged(MGPlayerPKComponent.Tag);
				MGPlayerPKMgr pkMgr = pkComponent.getPlayerPKMgr();
				if (pkMgr.isModel(MGPlayerPKModel.PeaceModel)) {
					continue;
				}
			}
			if (sprite instanceof Monster) {
				if (((Monster) sprite).getMonsterRef().isSummonMonster()) {
					MGPlayerPKComponent pkComponent = (MGPlayerPKComponent) summonMonster.getOwner().getTagged(MGPlayerPKComponent.Tag);
					MGPlayerPKMgr pkMgr = pkComponent.getPlayerPKMgr();
					if (pkMgr.isModel(MGPlayerPKModel.PeaceModel)) {
						continue;
					}
				}
			}
			int distance = GameSceneHelper.distance(summonMonster.getCrtScene(), summonMonster.getCrtPosition(), sprite.getCrtPosition());
			if (minDistance > distance) {
				minDistance = distance;
				ret = sprite;
			}
		}

		return ret;
	}

	/**
	 * 判断目标是否需要切换
	 * 
	 * @param now
	 */
	private boolean switchTarget(long now) {
		if (!validTarget(getFirstTarget())) { // 正在攻击的目标不存在
			setFirstTarget(null);
			if (validTarget(getSecondTarget())) {
				setFirstTarget(getSecondTarget());
				setSecondTarget(null);
				setFocusTargetTime(now);
				return true;
			}
		}
		if ((now - getFocusTargetTime()) >= waitToSwitch) {
			if (validTarget(getSecondTarget())) {
				setFirstTarget(getSecondTarget());
				setSecondTarget(null);
				setFocusTargetTime(now);
				return true;
			} else if (!validTarget(getFirstTarget()) && !validTarget(getSecondTarget())) {
				byte attackDistance = summonMonster.getMonsterRef().getAttackDistance();
				FightSprite nearestEnemy = getNearestEnemy(attackDistance);
				if (validTarget(nearestEnemy)) {
					setFirstTarget(nearestEnemy);
					setFocusTargetTime(now);
					return true;
				}
			}
//		else if (!validTarget(getSecondTarget())) {
//			byte attackDistance = summonMonster.getMonsterRef().getAttackDistance();
//			FightSprite nearestEnemy = getNearestEnemy(attackDistance);
//			if (validTarget(nearestEnemy)) {
//				setFirstTarget(nearestEnemy);
//				setFocusTargetTime(now);
//			}
//		}
		}
		return false;
	}

	private boolean isLegalMonster(long now) {
		if (!summonMonster.getMonsterRef().isRegularMonster()) {
			if (summonMonster.getOwner() == null) {
				return false;
			}
			if (summonMonster.isDead() || !summonMonster.getOwner().isOnline()) {
				return false;
			}
			return true;
		}
		return false;
	}

	@SuppressWarnings("rawtypes")
	public synchronized void chaseAndAttack(FightSprite target, long now) {
		FightSkill skill = summonMonster.getRandomSkill();
		// attack
		boolean canCastSkill = MonsterAIHelper.canCastSkill(summonMonster, target, skill, now);
		if (canCastSkill) {
			if (target instanceof Player) {
				MGPlayerPKComponent pkComponent = (MGPlayerPKComponent) summonMonster.getOwner().getTagged(MGPlayerPKComponent.Tag);
				MGPlayerPKMgr pkMgr = pkComponent.getPlayerPKMgr();
				if (pkMgr.isModel(MGPlayerPKModel.PeaceModel)) {
					setFirstTarget(null);
					return;
				}
				if (pkMgr.isModel(MGPlayerPKModel.TeamModel)) {
					if (MMORPGContext.playerTeamManagerComponent().isSameTeam((Player) summonMonster.getOwner(), (Player) target)) {
						setFirstTarget(null);
					}
				}
			}
			int distance = GameSceneHelper.distance(summonMonster.getCrtScene(), summonMonster.getCrtPosition(), target.getCrtPosition());
			if (skill.getRef().isTargetSkill()) {
				summonMonster.getFightSkillRuntimeComponent().castingSkill(skill, target);
				logger.debug("CastTargetSkill, skillName:" + skill.getRefId() + ";  distance:" + distance + ";  target Position:" + target.getCrtPosition() + "; own Position:"
						+ summonMonster.getCrtPosition());
			} else if (skill.getRef().isDirectionSkill()) {
				byte direction = FightSkillRuntimeHelper.getDirection(summonMonster.getCrtPosition(), target.getCrtPosition());
				summonMonster.getFightSkillRuntimeComponent().castingSkill(skill, direction);
				logger.debug("CastDirectionSkill, skillName:" + skill.getRefId() + ";  distance:" + distance + ";  direction:" + direction + "';  targetName:" + target.getName()
						+ "; target Position:" + target.getCrtPosition() + "; monster Position:" + summonMonster.getCrtPosition());
			} else if (skill.getRef().isGridSkill()) {
				summonMonster.getFightSkillRuntimeComponent().castingSkill(skill, target.getCrtPosition());
				logger.debug("CastGridSkill, skillName:" + skill.getRefId() + ";  distance:" + distance + ";  target Position:" + target.getCrtPosition() + ";  monster Position:"
						+ summonMonster.getCrtPosition());
			}
			String SkillMonsterAttack_GE_Id = SkillMonsterAttack_GE.class.getSimpleName();
			SkillMonsterAttack_GE skillMonsterAttack_GE = new SkillMonsterAttack_GE(summonMonster.getId());
			GameEvent<SkillMonsterAttack_GE> ge2 = (GameEvent<SkillMonsterAttack_GE>) GameEvent.getInstance(SkillMonsterAttack_GE_Id, skillMonsterAttack_GE);
			sendGameEvent(ge2, summonMonster.getOwner().getId());
			updateLastAttackTime(now);
			summonMonster.changeState(FightState.FightState_Id);
			if (target.isDead()) {
				setFirstTarget(null);
			}
		}

		// chase
		boolean neededToChase = MonsterAIHelper.isNeededToChase(summonMonster, target, skill, now);
		if (neededToChase) {
			checkArgument(summonMonster != null);
			checkArgument(target != null);
			Position targetPos = target.getCrtPosition();
			Position monsterPos = summonMonster.getCrtPosition();
			int distance = GameSceneHelper.distance(summonMonster.getCrtScene(), summonMonster.getCrtPosition(), target.getCrtPosition());
			int moveSpeed = summonMonster.getPathComponent().getMoveSpeed();
			Position nextPosition = null;
			if (distance < moveSpeed / SceneAOILayer.AOIGRID_MULTIPLE) {
				nextPosition = target.getCrtPosition();
			} else {
				byte direction = FightSkillRuntimeHelper.getDirection(monsterPos, targetPos);
				nextPosition = MonsterAIHelper.getNextPositionInDirection(summonMonster, direction);
				if (GameSceneHelper.isBlocked(summonMonster.getCrtScene(), nextPosition)) {
					nextPosition = MonsterAIHelper.getNextPosition(summonMonster, target);
				}
			}

			if (logger.isDebugEnabled()) {
				logger.debug("ChaseAndAttack distance:" + distance + "; nextPosition:" + nextPosition + "; monster Position:" + summonMonster.getCrtPosition()
						+ "; target's position:" + nextPosition);
			}
			summonMonster.getPathComponent().startMove(monsterPos, nextPosition);
			updateLastChaseTime(now);
			summonMonster.changeState(ChaseState.ChaseState_Id);
		}
	}

	/**
	 * 判断目标的合法性
	 * 
	 * @param target
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	protected boolean validTarget(FightSprite target) {
		if (target == null) {
			return false;
		}
		if (target.isDead()) {
			return false;
		}

		if (summonMonster.getOwner().isInSafeRegion()) {
			return false;
		}

		MGPlayerPKComponent pkComponent = (MGPlayerPKComponent) summonMonster.getOwner().getTagged(MGPlayerPKComponent.Tag);
		MGPlayerPKMgr pkMgr = pkComponent.getPlayerPKMgr();
		if (pkMgr.isModel(MGPlayerPKModel.UnionModel)) {
			PropertyDictionary pd1 = summonMonster.getProperty();
			PropertyDictionary pd2 = target.getProperty();

			if (pd1.contains(MGPropertySymbolDefines.UnionName_Id) && pd2.contains(MGPropertySymbolDefines.UnionName_Id)) {
				String unionName1 = MGPropertyAccesser.getUnionName(pd1);
				String unionName2 = MGPropertyAccesser.getUnionName(pd2);
				if (unionName1 != null && unionName2 != null && StringUtils.equals(unionName1, unionName2)) {
					return false;
				}
			}
		}

		if (target instanceof Monster) {
			Monster skillMonster = (Monster) target;
			boolean isSummonMonster = skillMonster.getMonsterRef().isSkillSummon();
			if (isSummonMonster || StringUtils.equals(target.getId(), summonMonster.getId())) {
				return false;
			}
		}

		FightSprite owner = summonMonster.getOwner();

		if (target instanceof Player) {
			Player targetP = (Player) target;
			if (!targetP.isOnline() || targetP.isInSafeRegion()) {
				return false;
			}

			// protect rookie
			if (owner instanceof Player) {
				boolean neededToProtectRookie = MGPlayerPKComponent.isNeededToProtectRookie((Player) owner, targetP);
				if (neededToProtectRookie) {
					MGFightProcessComponent.sendRookieProtectionGameEvent((Player) owner, targetP);
					return false;
				}
			}
		}

		if (owner != null && StringUtils.equals(target.getId(), owner.getId())) {
			return false;
		}
		if (!target.getCrtScene().equals(summonMonster.getCrtScene())) {
			return false;
		}
		return true;
	}
}
