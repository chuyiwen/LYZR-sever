package sophia.mmorpg.monster.ai;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import sophia.foundation.util.Position;
import sophia.mmorpg.base.scene.GameScene;
import sophia.mmorpg.base.scene.GameSceneHelper;
import sophia.mmorpg.base.scene.aoi.EightDirection;
import sophia.mmorpg.base.scene.aoi.SceneAOILayer;
import sophia.mmorpg.base.sprite.FightSprite;
import sophia.mmorpg.base.sprite.Sprite;
import sophia.mmorpg.base.sprite.fightSkill.FightSkill;
import sophia.mmorpg.base.sprite.fightSkill.FightSkillRuntimeHelper;
import sophia.mmorpg.base.sprite.state.adjunction.StealthState;
import sophia.mmorpg.base.sprite.state.movement.ReturnToBirthState;
import sophia.mmorpg.code.MMORPGSuccessCode;
import sophia.mmorpg.monster.Monster;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;
import sophia.mmorpg.utils.SFRandomUtils;

public final class MonsterAIHelper {

	private static final Logger logger = Logger.getLogger(MonsterAIHelper.class);

	public static boolean isNeededToPatrol(Monster who, long now) {
		if (who == null) {
			return false;
		}
		if (!who.getMonsterRef().canMove()) {
			return false;
		}
		if (who.getFightSpriteStateMgr().isState(ReturnToBirthState.ReturnToBirthState_Id)) {
			return false;
		}
		boolean enoughTimeElapsed = now - who.getPerceiveComponent().getLastPatrolTime() >= MonsterPerceiveComponent.patrolInterval;
		boolean hasTarget = who.getPerceiveComponent().getLastTarget() == null;
		return enoughTimeElapsed && hasTarget;
	}

	public static boolean isNeededToSeek(Monster who, long now) {
		if (who == null) {
			return false;
		}
		if (who.getFightSpriteStateMgr().isState(ReturnToBirthState.ReturnToBirthState_Id)) {
			return false;
		}
		boolean enoughTimeElapsed = now - who.getPerceiveComponent().getLastSeekTime() >= MonsterPerceiveComponent.seekInterval;
		return enoughTimeElapsed;
	}

	public static boolean isNeededToReturn(Monster who, long now) {
		if (who == null) {
			return false;
		}
		boolean returning = who.getFightSpriteStateMgr().isState(ReturnToBirthState.ReturnToBirthState_Id);
		boolean enoughTimeElapsed = now - who.getPerceiveComponent().getLastReturnTime() >= MonsterPerceiveComponent.returnInterval;
		return returning && enoughTimeElapsed;
	}

	public static void checkMonsterHasUniquePosition(Sprite who) {
		Set<Position> positions = new HashSet<>();
		Collection<Monster> aoiInterestedMonsters = GameSceneHelper.getAOIInterestedMonsters(who);
		for (Monster monster : aoiInterestedMonsters) {
			boolean isBirthPosition = monster.getCrtPosition().equals(monster.getBirthPosition());
			if (!isBirthPosition) {
				boolean succeeded = positions.add(monster.getCrtPosition());
				if (!succeeded) {
					Collection<Monster> targetGridMonsters = GameSceneHelper.getMonsters(who.getCrtScene(), who.getCrtPosition(), 0, 0);
					for (Monster s : targetGridMonsters) {
						logger.debug("duplicate position found " + s.getId() + " current position " + s.getCrtPosition() + " birth position " + s.getBirthPosition());
					}
				}
				checkArgument(succeeded, "duplicate position found " + monster.getCrtPosition() + " birth position " + monster.getBirthPosition());
			}

		}
	}

	public static void checkMonsterCannotStandOnPlayersPosition(Monster monster) {
		Collection<Player> aoiInterestedPlayers = GameSceneHelper.getAOIInterestedPlayers(monster);
		for (Player player : aoiInterestedPlayers) {
			checkArgument(!monster.getCrtPosition().equals(player.getCrtPosition()), " player " + player + " monster " + monster);
		}
	}

	public static boolean canCastSkill(Monster who, FightSprite target, FightSkill skill, long now) {
		if (who == null || target == null || skill == null) {
			return false;
		}

		if (!who.getCrtScene().equals(target.getCrtScene())) {
			return false;
		}

		long interval = now - who.getPerceiveComponent().getLastAttackTime();
		int atkSpeed = who.getAttackSpeed();

		if (interval < atkSpeed) {
			return false;
		}

		if (who.getFightSpriteStateMgr().isState(ReturnToBirthState.ReturnToBirthState_Id)) {
			return false;
		}

		if (target.equals(who.getPerceiveComponent().getLastAttacker()) && target.getFightSpriteStateMgr().isState(StealthState.StealthState_Id)) {
			return true;
		}

		if (target.getFightSpriteStateMgr().isState(StealthState.StealthState_Id)) {
			return false;
		}

		int distance = GameSceneHelper.distance(who.getCrtScene(), who.getCrtPosition(), target.getCrtPosition());
		int skillRange = MGPropertyAccesser.getSkillRange(skill.getRef().getProperty());
		boolean inRange = distance <= skillRange;

		boolean validTarget = isValidTarget(who, skill, target);
		return inRange && validTarget;
	}

	public static boolean isValidTarget(FightSprite attacker, FightSkill skill, FightSprite target) {
		return FightSkillRuntimeHelper.canCastSkill(attacker, skill, target) == MMORPGSuccessCode.CODE_SKILL_SUCCESS;
	}

	public static boolean isNeededToChase(Monster who, FightSprite target, FightSkill skill, long now) {
		if (who == null || target == null || skill == null) {
			return false;
		}

		if (!who.getCrtScene().equals(target.getCrtScene())) {
			return false;
		}

		if (who.getFightSpriteStateMgr().isState(ReturnToBirthState.ReturnToBirthState_Id)) {
			return false;
		}

		boolean outOfAttackRange = who.isOutOfAttackRange(target);

		if (!who.getMonsterRef().canMove()) {
			return false;
		}

		boolean enoughTimeElapsed = now - who.getPerceiveComponent().getLastChaseTime() >= MonsterPerceiveComponent.chaseInterval;

		int distance = GameSceneHelper.distance(who.getCrtScene(), who.getCrtPosition(), target.getCrtPosition());
		int skillRange = MGPropertyAccesser.getSkillRange(skill.getRef().getProperty());
		boolean outOfSkillRange = distance > skillRange;

		return outOfSkillRange && enoughTimeElapsed && !outOfAttackRange;
	}

	public static Position getWalkablePositionToward(GameScene gameScene, Position pos, byte direction) {
		if (!GameSceneHelper.isValidPosition(gameScene, pos)) {
			return null;
		}

		if (GameSceneHelper.isWalkable(gameScene, pos)) {
			return pos;
		} else {
			int rounds = 3;
			for (int round = 0; round < rounds; ++round) {
				Collection<Position> neighborPositions = getNeighborPositions(gameScene, pos, round);
				for (Position position : neighborPositions) {
					if (GameSceneHelper.isWalkable(gameScene, position)) {
						return position;
					}
				}
			}
			return null;
		}
	}

	public static Collection<Position> getNeighborPositions(GameScene scene, Position pos, int degree) {
		Collection<Position> positions = new ArrayList<>();
		for (byte direction = 0; direction < EightDirection.nDirection; ++direction) {
			Position forwardPosition = GameSceneHelper.getForwardPosition(direction, pos, degree * SceneAOILayer.AOIGRID_MULTIPLE);
			if (GameSceneHelper.isValidPosition(scene, forwardPosition)) {
				positions.add(forwardPosition);
			}
		}
		return positions;
	}

	/**
	 * This function will not guarantee that the returned position is walkable!
	 * So you have to call <code>getWalkablePositionToward</code> by yourself if
	 * needed.
	 * 
	 * @param owner
	 * @param direction
	 * @return
	 */
	public static synchronized Position getNextPositionInDirection(FightSprite owner, byte direction) {
		checkArgument(owner != null);
		checkArgument(direction < EightDirection.nDirection);
		int moveSpeed = owner.getPathComponent().getMoveSpeed();

		Position ownerPos = owner.getCrtPosition();
		Position forwardPosition = GameSceneHelper.getForwardCenterPosition(direction, ownerPos, moveSpeed);

		if (logger.isDebugEnabled()) {
			logger.debug("getNextPositionInDirection next position " + forwardPosition + " " + owner.getId() + " current position " + owner.getCrtPosition() + " "
					+ Thread.currentThread().getId());
			GameSceneHelper.checkInAOIGridCenter(forwardPosition);
		}

		return forwardPosition;
	}

	public static synchronized Position getNextPositionToTarget(FightSprite who, Position target) {
		checkArgument(who != null && target != null);
		checkArgument(GameSceneHelper.isValidPosition(who.getCrtScene(), target));
		checkArgument(GameSceneHelper.isInAOIGridCenter(target));

		int moveSpeed = who.getPathComponent().getMoveSpeed();
		Position crtPosition = who.getCrtPosition();
		byte direction = FightSkillRuntimeHelper.getDirection(crtPosition, target);

		int distanceToTarget = GameSceneHelper.distance(who.getCrtScene(), who.getCrtPosition(), target);
		if (distanceToTarget <= moveSpeed / SceneAOILayer.AOIGRID_MULTIPLE) {
			if (logger.isDebugEnabled()) {
				logger.debug("getNextPositionToTarget reach target " + target);
			}
			return target;
		}

		Position nextPosition = GameSceneHelper.getForwardCenterPosition(direction, crtPosition, moveSpeed);
		if (logger.isDebugEnabled()) {
			logger.debug("getNextPositionToTarget next position " + nextPosition + " " + who + " current position " + who.getCrtPosition());
			GameSceneHelper.checkInAOIGridCenter(nextPosition);
		}

		return nextPosition;

	}

	public static synchronized Position getNextPositionTowardBirthByTarget(Monster monster) {
		Position birthPosition = monster.getBirthPosition();
		Position crtPosition = monster.getCrtPosition();
		byte directionToBirth = FightSkillRuntimeHelper.getDirection(crtPosition, birthPosition);
		Position nextPositionInDirection = getNextPositionToTarget(monster, monster.getBirthPosition());
		Position nextPosition = MonsterAIHelper.getWalkablePositionToward(monster.getCrtScene(), nextPositionInDirection, directionToBirth);

		if (logger.isDebugEnabled()) {
			int distance = GameSceneHelper.distance(monster.getCrtScene(), birthPosition, crtPosition);
			byte attackDistance = MGPropertyAccesser.getAttackDistance(monster.getMonsterRef().getProperty());
			logger.debug("getNextPositionTowardBirth return to brith position " + birthPosition + " " + monster.getId() + " distance to birth: " + distance + " attackDistance: "
					+ attackDistance + " current pos " + crtPosition + " " + " next position " + nextPosition + Thread.currentThread().getId());
			GameSceneHelper.checkInAOIGridCenter(birthPosition);
		}

		if (logger.isDebugEnabled()) {
			int distance = GameSceneHelper.distance(monster.getCrtScene(), crtPosition, birthPosition);
			logger.debug("getNextPositionTowardBirth " + monster + " crtPosition " + crtPosition + " birthPosition " + birthPosition + " distance " + distance);
		}
		return nextPosition;
	}

	public static synchronized Position getNextPositionTowardBirth(Monster monster) {
		Position birthPosition = monster.getBirthPosition();
		Position crtPosition = monster.getCrtPosition();
		
		// return to birth position
		int distance = GameSceneHelper.distance(monster.getCrtScene(), birthPosition, crtPosition);
		int moveSpeed = monster.getPathComponent().getMoveSpeed();
		boolean withinRange = moveSpeed >= distance;
		if (withinRange) {
			return birthPosition;
		}
		
		byte directionToBirth = FightSkillRuntimeHelper.getDirection(crtPosition, birthPosition);
		Position nextPositionInDirection = getNextPositionInDirection(monster, directionToBirth);
		Position nextPosition = MonsterAIHelper.getWalkablePositionToward(monster.getCrtScene(), nextPositionInDirection, directionToBirth);

		if (logger.isDebugEnabled()) {
			distance = GameSceneHelper.distance(monster.getCrtScene(), birthPosition, crtPosition);
			byte attackDistance = MGPropertyAccesser.getAttackDistance(monster.getMonsterRef().getProperty());
			logger.debug("getNextPositionTowardBirth return to brith position " + birthPosition + " " + monster.getId() + " distance to birth: " + distance + " attackDistance: "
					+ attackDistance + " current pos " + crtPosition + " " + " next position " + nextPosition + Thread.currentThread().getId());
			GameSceneHelper.checkInAOIGridCenter(birthPosition);
		}

		if (logger.isDebugEnabled()) {
			distance = GameSceneHelper.distance(monster.getCrtScene(), crtPosition, birthPosition);
			logger.debug("getNextPositionTowardBirth " + monster + " crtPosition " + crtPosition + " birthPosition " + birthPosition + " distance " + distance);
		}
		return nextPosition;
	}

	public static synchronized Position getNextPosition(FightSprite owner, FightSprite target) {
		checkArgument(owner != null);
		checkArgument(target != null);
		Position targetPos = target.getCrtPosition();
		Position ownerPos = owner.getCrtPosition();
		byte direction = FightSkillRuntimeHelper.getDirection(ownerPos, targetPos);
		Position nextPositionInDirection = getNextPositionInDirection(owner, direction);
		Position nextPosition = MonsterAIHelper.getWalkablePositionToward(owner.getCrtScene(), nextPositionInDirection, direction);

		// int currentDistance = GameSceneHelper.distance(owner.getCrtScene(),
		// ownerPos, targetPos);
		// int nextDistance = GameSceneHelper.distance(owner.getCrtScene(),
		// nextPosition, targetPos);
		// if (currentDistance <= nextDistance) {
		// return null;
		// }

		if (logger.isDebugEnabled()) {
			int distance = GameSceneHelper.distance(owner.getCrtScene(), nextPosition, owner.getCrtPosition());
			int moveSpeed = owner.getMoveSpeed();
			logger.debug("getNextPosition next position " + nextPosition + " " + owner.getId() + " current position " + owner.getCrtPosition() + " distanceToNextPosition "
					+ distance + " move speed " + moveSpeed + " " + Thread.currentThread().getId());
			GameSceneHelper.checkInAOIGridCenter(nextPosition);
		}

		return nextPosition;
	}

	public static synchronized Position getNextPatrolPosition(Monster who) {
		byte direction = (byte) SFRandomUtils.random(0, EightDirection.nDirection - 1);
		Position nextPosition = getNextPositionInDirection(who, direction);

		int timesToTry = 10;
		int times = 1;
		while (times < timesToTry && !GameSceneHelper.isWalkable(who.getCrtScene(), nextPosition)) {
			byte newDirection = (byte) SFRandomUtils.random(0, EightDirection.nDirection - 1);
			nextPosition = getNextPositionInDirection(who, newDirection);
			++times;
			if (logger.isDebugEnabled()) {
				int moveSpeed = who.getPathComponent().getMoveSpeed();
				int distance = GameSceneHelper.distance(who.getCrtScene(), nextPosition, who.getCrtPosition());
				logger.debug("getNextPatrolPosition trying different direction: newDirection " + newDirection + " nextPosition " + nextPosition + " current position "
						+ who.getCrtPosition() + " distance " + distance + " moveSpeed " + moveSpeed);
			}
		}

		if (logger.isDebugEnabled()) {
			int moveSpeed = who.getPathComponent().getMoveSpeed();
			int distance = GameSceneHelper.distance(who.getCrtScene(), nextPosition, who.getCrtPosition());
			logger.debug("getNextPatrolPosition next position " + nextPosition + " " + who.getId() + " current position " + who.getCrtPosition() + " " + " distance " + distance
					+ " moveSpeed " + moveSpeed + " thread id " + Thread.currentThread().getId());
			GameSceneHelper.checkInAOIGridCenter(nextPosition);
		}

		return nextPosition;
	}

}
