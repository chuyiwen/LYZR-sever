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
package sophia.mmorpg.base.sprite.fightSkill;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import sophia.foundation.property.PropertyDictionary;
import sophia.foundation.util.Pair;
import sophia.foundation.util.Position;
import sophia.mmorpg.base.scene.GameScene;
import sophia.mmorpg.base.scene.GameSceneHelper;
import sophia.mmorpg.base.scene.aoi.EightDirection;
import sophia.mmorpg.base.scene.grid.SceneGrid;
import sophia.mmorpg.base.scene.grid.SceneTerrainLayer;
import sophia.mmorpg.base.sprite.FightSprite;
import sophia.mmorpg.base.sprite.fightSkill.skillinfo.SkillAimType;
import sophia.mmorpg.code.MMORPGErrorCode;
import sophia.mmorpg.code.MMORPGSuccessCode;
import sophia.mmorpg.core.CDMgr;
import sophia.mmorpg.monster.Monster;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.fightSkill.ref.SkillLevelRef;
import sophia.mmorpg.player.fightSkill.ref.SkillRef;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;

public final class FightSkillRuntimeHelper {
	private final static Logger logger = Logger.getLogger(FightSkillRuntimeHelper.class);

	public static void moveByGrid(FightSprite who, byte directionToMove, int gridToMove) {
		GameScene gameScene = who.getCrtScene();
		SceneTerrainLayer terrainLayer = gameScene.getTerrainLayer();
		Pair<Integer, Integer> angleValue = EightDirection.getAngleValue(directionToMove);

		Position crtPosition = who.getCrtPosition();
		SceneGrid sceneGrid = terrainLayer.getSceneGrid(crtPosition.getY(), crtPosition.getX());
		SceneGrid dstGrid = null;
		SceneGrid lastGrid = null;
		for (int i = 1; i <= gridToMove; i++) {
			int r = sceneGrid.getRow() + angleValue.getKey() * i;
			int c = sceneGrid.getColumn() + angleValue.getValue() * i;
			if (!terrainLayer.isInMatrixRange(r, c)) {
				break;
			}

			dstGrid = terrainLayer.getSceneGrid(r, c);
			if (dstGrid.isBlocked()) {
				break;
			}

			lastGrid = dstGrid;
		}

		if (lastGrid != null) {
			who.getPathComponent().silentMoveTo(lastGrid.getColumn(), lastGrid.getRow());
		}
	}

	public static byte getBackDirection(byte direction) {
		byte directions = EightDirection.nDirection;
		byte half = (byte) (directions / 2);
		byte backDirection = (byte) (direction + half);
		if (backDirection >= EightDirection.nDirection) {
			backDirection = (byte) (backDirection % directions);
		}
		return backDirection;
	}

	/**
	 * return the new direction after turn to left by a direction
	 * 
	 * @param direction
	 * @return
	 */
	public static byte getLeftDirection(byte direction) {
		byte leftDirection = (byte) ((direction + EightDirection.nDirection - 1) % EightDirection.nDirection);
		return leftDirection;
	}

	/**
	 * return the new direction after turn to right by a direction
	 * 
	 * @param direction
	 * @return
	 */
	public static byte getRightDirection(byte direction) {
		byte rightDirection = (byte) ((direction + EightDirection.nDirection + 1) % EightDirection.nDirection);
		return rightDirection;
	}

	public static byte getDirection(Position start, Position end) {
		checkArgument(start != null);
		checkArgument(end != null);
		int deltaX = end.getX() - start.getX();
		int deltaY = end.getY() - start.getY();
		int deltaXInUnit = deltaX;
		if (deltaX != 0) {
			deltaXInUnit = deltaX / Math.abs(deltaX);
		}

		int deltaYInUnit = deltaY;
		if (deltaY != 0) {
			deltaYInUnit = deltaY / Math.abs(deltaY);
		}

		for (Entry<Byte, Pair<Integer, Integer>> entry : EightDirection.getEightangle().entrySet()) {
			byte direction = entry.getKey();
			Pair<Integer, Integer> delta = entry.getValue();
			if (delta.getValue() == deltaXInUnit && delta.getKey() == deltaYInUnit) {
				return direction;
			}
		}

		return EightDirection.Right_Direction;
	}

	public static int getDirectionGridDistance(GameScene scene, Position start, Position target, byte direction) {
		byte startToTargetDirection = getDirection(start, target);
		if (startToTargetDirection != direction)
			return 0;
		int distance = GameSceneHelper.distance(scene, start, target);
		return distance;
	}

	// 只给半月剑法调用
	public static Collection<FightSprite> getHalfMoonSprites(FightSprite attacker, byte attackerDirection) {
		Position crtPosition = attacker.getCrtPosition();
		GameScene crtScene = attacker.getCrtScene();
		Collection<FightSprite> targets = new ArrayList<>();
		// ignore forward grid here
		// left grid
		byte leftDirection = getLeftDirection(attackerDirection);
		Collection<FightSprite> leftSprites = GameSceneHelper.getFightSprites(crtScene, crtPosition, leftDirection, 1, 1);
		targets.addAll(leftSprites);
		// right grid
		byte rightDirection = getRightDirection(attackerDirection);
		Collection<FightSprite> rightSprites = GameSceneHelper.getFightSprites(crtScene, crtPosition, rightDirection, 1, 1);
		targets.addAll(rightSprites);
		// right down grid
		byte rightDownDirection = getRightDirection(rightDirection);
		Collection<FightSprite> rightDownSprites = GameSceneHelper.getFightSprites(crtScene, crtPosition, rightDownDirection, 1, 1);
		targets.addAll(rightDownSprites);
		return targets;
	}

	// 只给半月剑法拓展1调用
	public static Collection<FightSprite> getFullMoonSprites(FightSprite attacker, byte attackerDirection) {
		Position crtPosition = attacker.getCrtPosition();
		GameScene crtScene = attacker.getCrtScene();
		Collection<FightSprite> targets = new ArrayList<>();
		// ignore forward grid here
		for (byte direction = 0; direction < EightDirection.nDirection; ++direction) {
			if (direction == attackerDirection) {
				continue;
			}

			Collection<FightSprite> sprites = GameSceneHelper.getFightSprites(crtScene, crtPosition, direction, 1, 1);
			targets.addAll(sprites);

		}
		return targets;
	}

	// 只给野蛮冲撞调用
	public static Position getForwardWalkablePosition(GameScene scene, byte direction, Position start, int nGrid) {
		int grid = 0;
		Position forwardPosition = GameSceneHelper.getForwardPosition(scene, direction, start, grid);
		boolean isBlocked = GameSceneHelper.isBlocked(scene, forwardPosition);
		while (!isBlocked && grid <= nGrid) {
			grid++;
			forwardPosition = GameSceneHelper.getForwardPosition(scene, direction, start, grid);
			isBlocked = GameSceneHelper.isBlocked(scene, forwardPosition);
		}
		if (isBlocked && grid <= 1) {
			return null;
		}
		forwardPosition = GameSceneHelper.getForwardPosition(scene, direction, start, --grid);
		logger.info("getForwardWalkablePosition isBlocked " + GameSceneHelper.isBlocked(scene, forwardPosition) + " nGrid: " + nGrid + " realGrid: " + grid + " target pos: "
				+ forwardPosition);
		return forwardPosition;
	}

	// 只给野蛮冲撞调用
	public static boolean canCrush(GameScene scene, Position start, byte attackerDirection) {
		if (!GameSceneHelper.isValidPosition(scene, start)) {
			return false;
		}
		Position firstBlockedPos = GameSceneHelper.getFirstBlockedPositionInDirection(scene, start, attackerDirection);
		if (firstBlockedPos == null) {
			return false;
		}

		int distance = getDirectionGridDistance(scene, start, firstBlockedPos, attackerDirection);
		Collection<FightSprite> sprites = GameSceneHelper.getFightSprites(scene, start, attackerDirection, 1, distance);
		// + 1 for adding this skill caster
		boolean can = sprites.size() + 1 < distance;
		return can;
	}

	public static int canCastSkill(FightSprite attacker, FightSkill skill) {
		if (attacker == null) {
			return MMORPGErrorCode.CODE_SKILL_CASTER_NULL;
		}

		if (skill == null) {
			return MMORPGErrorCode.CODE_SKILL_SKILL_NULL;
		}

		// attacker hp
		if (attacker.getHP() <= 0) {
			return MMORPGErrorCode.CODE_SKILL_ATTACKER_DEAD;
		}

		// attacker has learned skill
		if (attacker instanceof Player) {
			Player player = (Player) attacker;
			boolean learned = player.getPlayerFightSkillComponent().getPlayerFightSkillTree().isLearned(skill.getRefId());
			if (!learned) {
				return MMORPGErrorCode.CODE_SKILL_NOT_LEARNED;
			}
		} else if (attacker instanceof Monster) {
			Monster monster = (Monster) attacker;
			boolean learned = monster.getMonsterRef().isLearned(skill.getRefId());
			if (!learned) {
				return MMORPGErrorCode.CODE_SKILL_NOT_LEARNED;
			}
		}

		// basic skill and monster skills won't cost MP
		if (!skill.getRef().isBasicSkill() && (attacker instanceof Player)) {
			PropertyDictionary levelData = skill.getLevelRef().getProperty();
			// mp
			int mpRequired = MGPropertyAccesser.getMP(levelData);
			PropertyDictionary playerPd = attacker.getFightPropertyMgrComponent().getFightPropertyMgr().getSnapshotByNew().getPropertyDictionary();
			int currentMp = MGPropertyAccesser.getMP(playerPd);
			if (currentMp < mpRequired) {
				return MMORPGErrorCode.CODE_SKILL_NOT_ENOUGH_MP;
			}

		}

		if (skill.getRefId().equals(SkillRef.basicAttackRefId)) {
			CDMgr basicCDMgr = attacker.getFightSkillRuntimeComponent().getBasicCDMgr();
			if (basicCDMgr.isCDStarted(skill.getRefId()) && !basicCDMgr.isOutOfCD(skill.getRefId())) {
				return MMORPGErrorCode.CODE_SKILL_IN_CD;
			}
		} else {
			CDMgr fightRuntimeCDMgr = attacker.getFightSkillRuntimeComponent().getSkillCDMgr();
			if (fightRuntimeCDMgr.isCDStarted(skill.getRefId()) && !fightRuntimeCDMgr.isOutOfCD(skill.getRefId())) {
				return MMORPGErrorCode.CODE_SKILL_IN_CD;
			}
		}

		return MMORPGSuccessCode.CODE_SKILL_SUCCESS;
	}

	public static int canCastSkill(FightSprite attacker, FightSkill skill, Position targetGrid) {
		boolean validPosition = GameSceneHelper.isValidPosition(attacker.getCrtScene(), targetGrid);
		if (!validPosition) {
			return MMORPGErrorCode.CODE_SKILL_DATA_LOGIC_ERROR;
		}
		return canCastSkill(attacker, skill);
	}

	public static int canCastSkill(FightSprite attacker, FightSkill skill, byte direction) {
		if (direction < EightDirection.Right_Direction || direction >= EightDirection.nDirection) {
			return MMORPGErrorCode.CODE_SKILL_DATA_LOGIC_ERROR;
		}
		return canCastSkill(attacker, skill);
	}

	public static int canCastSkill(FightSprite attacker, FightSkill skill, FightSprite target) {

		int code = isValidTarget(attacker, skill, target);
		if (code != MMORPGSuccessCode.CODE_SKILL_SUCCESS) {
			return code;
		}

		PropertyDictionary skillData = skill.getRef().getProperty();
		Position playerPosition = attacker.getCrtPosition();
		Position spritePosition = target.getCrtPosition();
		int distanceRequired = MGPropertyAccesser.getSkillRange(skillData);
		int distance = GameSceneHelper.distance(attacker.getCrtScene(), playerPosition, spritePosition);
		if (distance - 1 > distanceRequired && distanceRequired > 0) {
			return MMORPGErrorCode.CODE_SKILL_TARGET_TOO_FAR;
		}

		return canCastSkill(attacker, skill);

	}

	public static int isValidTarget(FightSprite attacker, FightSkill skill, FightSprite target) {
		if (attacker == null) {
			return MMORPGErrorCode.CODE_SKILL_CASTER_NULL;
		}

		if (skill == null) {
			return MMORPGErrorCode.CODE_SKILL_NOT_LEARNED;
		}

		if (target == null) {
			return MMORPGErrorCode.CODE_SKILL_TARGET_NULL;
		}

		// player can't attack his own skill monster when the skill is harmful
		if (target.getGameSpriteType().equals(Monster.class.getSimpleName()) && !skill.getRef().isTargetFriend()) {
			Monster monster = (Monster) target;
			if (monster.getOwner() != null && monster.getOwner().getId().equals(attacker.getId())) {
				return MMORPGErrorCode.CODE_SKILL_TARGET_OWNMONSTER;
			}
		}

		if (target.isDead()) {
			return MMORPGErrorCode.CODE_SKILL_TARGET_DEAD;
		}

		if (attacker.getCrtScene() == null || ((attacker.getCrtScene() != null) && !attacker.getCrtScene().equals(target.getCrtScene()))) {
			return MMORPGErrorCode.CODE_SKILL_TARGET_NOT_IN_SAME_SCENE;
		}

		PropertyDictionary skillData = skill.getRef().getProperty();
		byte skillAimType = MGPropertyAccesser.getSkillAimType(skillData);
		if (skillAimType != SkillAimType.TARGET && skillAimType != SkillAimType.DIRECTION) {
			return MMORPGErrorCode.CODE_SKILL_DATA_LOGIC_ERROR;
		}

		if (target instanceof Player && target.isInSafeRegion() && !skill.getRef().isTargetFriend()) {
			return MMORPGErrorCode.CODE_SKILL_TARGET_IN_SAFE_ZONE;
		}

		if (attacker instanceof Player && attacker.isInSafeRegion() && !skill.getRef().isTargetFriend() && target instanceof Player) {
			return MMORPGErrorCode.CODE_SKILL_CASTER_IN_SAFE_ZONE;
		}

		if (target instanceof Monster && target.isInSafeRegion() && !skill.getRef().isTargetFriend() && !((Monster) target).getMonsterRef().isRegularMonster()) {
			return MMORPGErrorCode.CODE_SKILL_TARGET_IN_SAFE_ZONE;
		}

		if (!target.isOnline()) {
			return MMORPGErrorCode.CODE_SKILL_TARGET_OFFLINE;
		}

		return MMORPGSuccessCode.CODE_SKILL_SUCCESS;
	}

	/**
	 * Given an extended skill, we can get the skill that the extended skill is
	 * based on. Rule is: the refId of an extended skill will append its base
	 * skill refId by a "_" and a number.
	 * 
	 * @param player
	 * @param extendedSkill
	 * @return
	 */
	public static FightSkill getBaseSkill(Player player, FightSkill extendedSkill) {
		if (extendedSkill == null || !extendedSkill.getRef().isExtendedSkill()) {
			return null;
		} else {
			String extendedSkillRefId = extendedSkill.getRefId();
			String baseSkillRefId = StringUtils.substringBeforeLast(extendedSkillRefId, "_");
			FightSkill fightSkill = player.getPlayerFightSkillComponent().getPlayerFightSkillTree().getBaseSkill(baseSkillRefId);
			return fightSkill;
		}
	}

	public static void startOrUpdateCDTime(Player player, FightSkill skill) {
		if (skill.getRef().isBasicSkill()) {
			CDMgr basicCDManager = player.getFightSkillRuntimeComponent().getBasicCDMgr();
			if (!basicCDManager.isCDStarted(skill.getRefId())) {
				// 0 here to only use public basic CD time, not individual basic
				// CD time
				basicCDManager.startCD(skill.getRefId(), 0);
			}
			basicCDManager.update(skill.getRefId());
		} else {
			SkillLevelRef levelRef = skill.getLevelRef();
			checkArgument(levelRef != null);
			PropertyDictionary property = levelRef.getProperty();
			checkArgument(property != null);
			int CDTime = MGPropertyAccesser.getSkillCDTime(property);
			CDMgr cdManager = player.getFightSkillRuntimeComponent().getSkillCDMgr();
			String refId = skill.getRefId();
			if (!cdManager.isCDStarted(refId)) {
				cdManager.startCD(refId, CDTime);
			}

			if (cdManager.isCDStarted(refId) && cdManager.getFightCDMillis(refId) != CDTime) {
				cdManager.setFightCDMillis(refId, CDTime);
			}

			cdManager.update(refId);
		}
	}

}
