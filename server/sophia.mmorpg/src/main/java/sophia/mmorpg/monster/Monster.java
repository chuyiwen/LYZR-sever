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
package sophia.mmorpg.monster;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import sophia.foundation.communication.core.MessageFactory;
import sophia.foundation.property.PropertyDictionary;
import sophia.foundation.util.Position;
import sophia.game.GameRoot;
import sophia.game.component.communication.GameEvent;
import sophia.mmorpg.base.scene.GameSceneHelper;
import sophia.mmorpg.base.sprite.FightSprite;
import sophia.mmorpg.base.sprite.SpriteTypeDefine;
import sophia.mmorpg.base.sprite.fightSkill.FightSkill;
import sophia.mmorpg.base.sprite.fightSkill.FightSkillRuntimeComponent;
import sophia.mmorpg.base.sprite.state.adjunction.PoisoningState;
import sophia.mmorpg.base.sprite.state.adjunction.StealthState;
import sophia.mmorpg.monster.ai.MonsterPerceiveComponent;
import sophia.mmorpg.monster.drop.MonsterDropMgr;
import sophia.mmorpg.monster.gameEvent.MonsterDead_GE;
import sophia.mmorpg.monster.ref.MonsterRef;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.fightSkill.ref.SkillRef;
import sophia.mmorpg.player.scene.event.G2C_Scene_State_Change;
import sophia.mmorpg.player.scene.event.SceneEventDefines;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;
import sophia.mmorpg.ref.editorgen.MGPropertySymbolDefines;
import sophia.mmorpg.utils.SFRandomUtils;

public class Monster extends FightSprite {
	public static final String Monster_GameSprite_Type = Monster.class.getSimpleName();
	private static final Logger logger = Logger.getLogger(Monster.class);

	public static final String MonsterDead_GE_Id = MonsterDead_GE.class.getSimpleName();

	public static final int MOVESPEED = 17;
	public static final int ATTACKSPEED = 1000;

	private MonsterRef monsterRef;

	private FightSprite owner = null;

	// 怪物刷新间隔，单位秒，如果为0则不刷新
	private int monsterRefreshTime;
	private int monsterRefreshType;
	private String timingRefresh;

	private Position birthPosition = new Position();

	// 死亡时间，时间戳
	private long lastDeadTime;
	
	// 怪物死亡复活流程纠错，
	private long lastCheckTime;
	// 怪物死亡异常，客户端发送异常次数
	private int clientSendCount;

	@SuppressWarnings("unchecked")
	public Monster() {
		setId(UUID.randomUUID().toString());
		this.fightSkillRuntimeComponent = ((FightSkillRuntimeComponent<Monster>) createComponent(FightSkillRuntimeComponent.class));
	}

	@Override
	public String getGameSpriteType() {
		return Monster_GameSprite_Type;
	}
	
	@Override
	public byte getSpriteType(){
		return SpriteTypeDefine.GameSprite_Monster;
	}

	public MonsterRef getMonsterRef() {
		return monsterRef;
	}

	public void setMonsterRef(MonsterRef monsterRef) {
		this.monsterRef = monsterRef;
	}

	public void reset() {
		fightSpriteStateMgr.reset();
		configFightProperties();
	}

	public void configFightProperties() {
		PropertyDictionary refPd = this.monsterRef.getProperty();
		PropertyDictionary pd = new PropertyDictionary();
		int maxHP = MGPropertyAccesser.getMaxHP(refPd);
		int maxMP = 0;

		MGPropertyAccesser.setOrPutAtkSpeed(pd, ATTACKSPEED);
		MGPropertyAccesser.setOrPutMoveSpeed(pd, MOVESPEED);
		MGPropertyAccesser.setOrPutMoveSpeedPer(pd, MGPropertyAccesser.getMoveSpeedPer(refPd));
		MGPropertyAccesser.setOrPutAtkSpeedPer(pd, MGPropertyAccesser.getAtkSpeedPer(refPd));
		MGPropertyAccesser.setOrPutPDodgePer(pd, MGPropertyAccesser.getPDodgePer(refPd));
		MGPropertyAccesser.setOrPutMDodgePer(pd, MGPropertyAccesser.getMDodgePer(refPd));
		MGPropertyAccesser.setOrPutMDodge(pd, 0);
		MGPropertyAccesser.setOrPutMaxPAtk(pd, MGPropertyAccesser.getMaxPAtk(refPd));
		MGPropertyAccesser.setOrPutMinPAtk(pd, MGPropertyAccesser.getMinPAtk(refPd));
		MGPropertyAccesser.setOrPutMaxMAtk(pd, MGPropertyAccesser.getMaxMAtk(refPd));
		MGPropertyAccesser.setOrPutMinMAtk(pd, MGPropertyAccesser.getMinMAtk(refPd));
		MGPropertyAccesser.setOrPutMaxTao(pd, MGPropertyAccesser.getMaxTao(refPd));
		MGPropertyAccesser.setOrPutMinTao(pd, MGPropertyAccesser.getMinTao(refPd));
		MGPropertyAccesser.setOrPutMaxPDef(pd, MGPropertyAccesser.getMaxPDef(refPd));
		MGPropertyAccesser.setOrPutMinPDef(pd, MGPropertyAccesser.getMinPDef(refPd));
		MGPropertyAccesser.setOrPutMaxMDef(pd, MGPropertyAccesser.getMaxMDef(refPd));
		MGPropertyAccesser.setOrPutMinMDef(pd, MGPropertyAccesser.getMinMDef(refPd));
		MGPropertyAccesser.setOrPutDodge(pd, MGPropertyAccesser.getDodge(refPd));
		MGPropertyAccesser.setOrPutHit(pd, MGPropertyAccesser.getHit(refPd));
		MGPropertyAccesser.setOrPutCrit(pd, MGPropertyAccesser.getCrit(refPd));
		MGPropertyAccesser.setOrPutCritInjure(pd, MGPropertyAccesser.getCritInjure(refPd));
		MGPropertyAccesser.setOrPutHealHP(pd, MGPropertyAccesser.getHealHP(refPd));
		MGPropertyAccesser.setOrPutMaxHP(pd, maxHP);
		MGPropertyAccesser.setOrPutMaxMP(pd, maxMP);
		MGPropertyAccesser.setOrPutHP(pd, maxHP);
		MGPropertyAccesser.setOrPutMP(pd, maxMP);

		getFightPropertyMgrComponent().getFightPropertyMgr().setCrtPropertyDictionary(pd);

		if (getPathComponent() != null) {
			getPathComponent().setMoveSpeed(getMoveSpeed());
		}
	}

	@Override
	public boolean modifyHP(final FightSprite attacker, final int hp) {
		byte result = super.changeHP(attacker, hp);
		if (result == MODIFY_HP_FAILURE) {
			return false;
		}

		if (logger.isDebugEnabled()) {
			logger.debug("modifyHP hp=" + hp);
		}

		// 死亡
		if (result == MODIFY_HP_DEAD) {
			FightSprite lootOwner = this.getPerceiveComponent().getLootOwner();
			FightSprite who = lootOwner != null ? lootOwner : attacker;
			MonsterDead_GE monsterDeadGE = new MonsterDead_GE();
			monsterDeadGE.setAttacker(who);
			monsterDeadGE.setMonster(this);
			GameEvent<?> event = GameEvent.getInstance(MonsterDead_GE_Id, monsterDeadGE);
			this.handleGameEvent(event);
			who.handleGameEvent(event);
			GameEvent.pool(event);
			if (getMonsterRef().isRegularMonster()) {
				monsterDeadGenerateLoot(who, this);
			}
		}

		return true;
	}

	@Override
	public boolean applyHP(FightSprite attacker, final int hp) {
		return modifyHP(attacker, hp);
	}

	@Override
	public boolean applyMP(final int mp) {
		return modifyMP(mp);
	}
	
	private void monsterDeadGenerateLoot(FightSprite attacker, Monster monster) {
		checkArgument(attacker != null);
		checkArgument(monster != null);
		
		if (logger.isDebugEnabled()) {
			logger.debug("monsterDeadGenerateLoot, " + monster);
		}

		if (attacker instanceof Monster) {
			Monster killer = (Monster) attacker;
			if (!killer.getMonsterRef().isRegularMonster() && killer.getOwner() != null) {
				attacker = killer.getOwner();
			}
		}

		if (attacker instanceof Player) {
			Player player = (Player) attacker;
//			PlayerTeam team = player.getPlayerTeamComponent().getTeam();   //暂时屏蔽组队经验分配，防止后期策划改需求时添加
//			if (team != null) {
//				addTeamMembersExp(player, monster);
//			} else {
				addExp(player, monster);
//			}
		}
		
		getCrtScene().getMonsterMgrComponent().enterRevive(monster);
		
		try {
			MonsterDropMgr.generateLoot(attacker, monster);
		} catch (Exception e) {
			logger.error("monsterDeadGenerateLoot error, ", e);
		}
		
		// do some cleaning
		this.getPerceiveComponent().reset();
	}

	public void addExp(Player player, Monster monster) {
		int exp = (int) MGPropertyAccesser.getExp(monster.getMonsterRef().getProperty());
		float expMultiple = player.getExpComponent().getExpMultiple();
		int expGot = exp;
		if (expMultiple != 0.0) {
			expGot = (int) (exp * expMultiple);
		}
		
		player.getExpComponent().addExp(expGot);
	}
	
//	public void addTeamMembersExp(Player player, Monster monster) {
//		PlayerTeam team = player.getPlayerTeamComponent().getTeam();
//		if (team != null) {
//			List<Player> crtSceneOtherMember = team.getCrtSceneOtherMember(player);
//			addTeamExp(player, monster, crtSceneOtherMember.size() + 1);
//			for (Player member : crtSceneOtherMember) {
//				addTeamExp(member, monster, crtSceneOtherMember.size() + 1);
//			}
//		}
//	}
//
//	private void addTeamExp(Player player, Monster monster, int Num) {
//		int exp = (int) MGPropertyAccesser.getExp(monster.getMonsterRef().getProperty());
//		float expMultiple = MGPropertyAccesser.getExpMultiple(player.getProperty());
//		float a = (float) ((1 + 0.2 * Num) / Num);
//		float expGot = exp * a;
//		if (expMultiple != 0.0) {
//			expGot = (int) (expGot * expMultiple);
//		}
//		
//		player.getExpComponent().addExp((int)expGot);
//	}
	
	@Override
	public String toString() {
		return "Monster [refId=" + monsterRef.getId() + ", name=" + name + ", crtScene=" + crtScene + ", crtPosition=" + crtPosition + ", getId()=" + getId() + "]";
	}

	public boolean isBoss() {
		byte quality = MGPropertyAccesser.getQuality(getMonsterRef().getProperty());
		
		return quality == 3;
	}
	
	public int getMonsterRefreshTime() {
		return monsterRefreshTime;
	}

	public void setMonsterRefreshTime(int monsterRefreshTime) {
		this.monsterRefreshTime = monsterRefreshTime;
	}

	public long getLastDeadTime() {
		return lastDeadTime;
	}

	public void setLastDeadTime(long lastDeadTime) {
		this.lastDeadTime = lastDeadTime;
	}

	public Position getBirthPosition() {
		return birthPosition;
	}

	public void setBirthPosition(Position birthPosition) {
		this.birthPosition = birthPosition;
	}

	public FightSprite getOwner() {
		return owner;
	}

	public void setOwner(FightSprite owner) {
		this.owner = owner;
	}

	@Override
	public boolean isEnemyTo(FightSprite fightSprite) {
		if (owner != null && StringUtils.equals(fightSprite.getId(), owner.getId())) {
			return false;
		}
		if (fightSprite instanceof Player) {
			Player player = (Player) fightSprite;
			PropertyDictionary pd1 = getProperty();
			PropertyDictionary pd2 = player.getProperty();
			if (pd1.contains(MGPropertySymbolDefines.UnionName_Id) && pd2.contains(MGPropertySymbolDefines.UnionName_Id)) {
				String unionName1 = MGPropertyAccesser.getUnionName(pd1);
				String unionName2 = MGPropertyAccesser.getUnionName(pd2);
				if (unionName1 != null && unionName2 != null && StringUtils.equals(unionName1, unionName2)) {
					return false;
				}
			}
			// monster can't attack a player when he is in StealthState
			if (player.getFightSpriteStateMgr().isState(StealthState.StealthState_Id)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public void broadcastState() {
		G2C_Scene_State_Change res = MessageFactory.getConcreteMessage(SceneEventDefines.G2C_Scene_State_Change);
		res.setAimType(this.getSpriteType());
		res.setCharId(getId());
		res.setStateList(getStateList());
		GameSceneHelper.broadcastMessageToAOI(this, res);
	}

	public int getAttackSpeed() {
		return this.getFightPropertyMgrComponent().getFightPropertyMgr().getSnapshotValueById(MGPropertySymbolDefines.AtkSpeed_Id);
	}

	public int getMonsterRefreshType() {
		return monsterRefreshType;
	}

	public void setMonsterRefreshType(int monsterRefreshType) {
		this.monsterRefreshType = monsterRefreshType;
	}

	public String getTimingRefresh() {
		return timingRefresh;
	}

	public void setTimingRefresh(String timingRefresh) {
		this.timingRefresh = timingRefresh;
	}

	public FightSkill getRandomSkill() {
		List<FightSkill> skills = getMonsterRef().getFightSkillList();
		if (skills.size() == 0) {
			SkillRef ref = (SkillRef) GameRoot.getGameRefObjectManager().getManagedObject(SkillRef.basicAttackRefId);
			return new FightSkill(SkillRef.basicAttackRefId, ref);
		}

		int randomSkillIndex = SFRandomUtils.random(skills.size()) - 1;
		return skills.get(randomSkillIndex);
	}

	public boolean isWithinBirthRange() {
		int distanceToBirth = GameSceneHelper.distance(getCrtScene(), getBirthPosition(), getCrtPosition());
		int maxDistanceToBirth = MGPropertyAccesser.getAttackDistance(getMonsterRef().getProperty());
		boolean withinBirthRange = distanceToBirth <= maxDistanceToBirth;

		return withinBirthRange;
	}

	public boolean isOutOfAttackRange(FightSprite target) {
		byte attackDistance = MGPropertyAccesser.getAttackDistance(getMonsterRef().getProperty());
		int distance = GameSceneHelper.distance(getCrtScene(), getCrtPosition(), target.getCrtPosition());
		boolean outOfAttackRange = distance > attackDistance;
		if (outOfAttackRange) {
			perceiveComponent.forsakeTarget();
		}
		return outOfAttackRange;
	}

	public void recoverHP(long now) {
		// 每10秒回复血量，中毒时不恢复
		if (!this.getFightSpriteStateMgr().isState(PoisoningState.PoisoningState_Id) && now - perceiveComponent.getLastRecoverTime() >= MonsterPerceiveComponent.recoverInterval) {
			perceiveComponent.updateLastRecoverTime(now);
			PropertyDictionary data = this.getMonsterRef().getProperty();
			int healHP = MGPropertyAccesser.getHealHP(data);
			if (healHP > 0) {
				modifyHP(this, healHP);
			}
		}
	}

	public long getLastCheckTime() {
		return lastCheckTime;
	}

	public void setLastCheckTime(long lastCheckTime) {
		this.lastCheckTime = lastCheckTime;
	}

	public int getClientSendCount() {
		return clientSendCount;
	}

	public void setClientSendCount(int clientSendCount) {
		this.clientSendCount = clientSendCount;
	}
}
