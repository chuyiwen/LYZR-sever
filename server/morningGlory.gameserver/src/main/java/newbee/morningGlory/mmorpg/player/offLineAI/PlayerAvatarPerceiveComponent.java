package newbee.morningGlory.mmorpg.player.offLineAI;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import newbee.morningGlory.mmorpg.player.offLineAI.gameEvent.PlayerAvatarDead_GE;
import newbee.morningGlory.mmorpg.player.offLineAI.model.AILogModel;
import newbee.morningGlory.mmorpg.player.offLineAI.model.HpMpModel;
import newbee.morningGlory.mmorpg.player.offLineAI.persistence.OffLineAIDAOMgr;
import newbee.morningGlory.mmorpg.player.summons.PlayerSummonMonsterComponent;

import org.apache.log4j.Logger;

import sophia.foundation.property.PropertyDictionary;
import sophia.foundation.util.Position;
import sophia.game.component.communication.GameEvent;
import sophia.mmorpg.base.scene.GameSceneHelper;
import sophia.mmorpg.base.scene.aoi.EightDirection;
import sophia.mmorpg.base.scene.aoi.SceneAOILayer;
import sophia.mmorpg.base.scene.grid.SceneGrid;
import sophia.mmorpg.base.scene.mgr.LootMgrComponent;
import sophia.mmorpg.base.scene.tick.SceneTick_GE;
import sophia.mmorpg.base.sprite.FightSprite;
import sophia.mmorpg.base.sprite.ai.SpritePerceiveComponent;
import sophia.mmorpg.base.sprite.ai.gameEvent.FightSpriteInjured_GE;
import sophia.mmorpg.base.sprite.fightSkill.FightSkill;
import sophia.mmorpg.base.sprite.fightSkill.FightSkillRuntimeHelper;
import sophia.mmorpg.base.sprite.state.adjunction.StealthState;
import sophia.mmorpg.base.sprite.state.global.FightState;
import sophia.mmorpg.base.sprite.state.movement.ChaseState;
import sophia.mmorpg.base.sprite.state.movement.PatrolState;
import sophia.mmorpg.code.MMORPGSuccessCode;
import sophia.mmorpg.item.Item;
import sophia.mmorpg.loot.Loot;
import sophia.mmorpg.loot.LootKind;
import sophia.mmorpg.monster.Monster;
import sophia.mmorpg.monster.ai.MonsterAIHelper;
import sophia.mmorpg.monster.ai.MonsterPerceiveComponent;
import sophia.mmorpg.monster.gameEvent.MonsterDead_GE;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.fightSkill.ref.SkillLevelRef;
import sophia.mmorpg.player.gameEvent.PlayerDead_GE;
import sophia.mmorpg.player.itemBag.ItemPair;
import sophia.mmorpg.player.team.PlayerTeam;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;
import sophia.mmorpg.utils.SFRandomUtils;

public class PlayerAvatarPerceiveComponent extends SpritePerceiveComponent<PlayerAvatar> {

	private static final Logger logger = Logger.getLogger(PlayerAvatarPerceiveComponent.class);
	
	public static final String SceneTick_GE_Id = SceneTick_GE.class.getSimpleName();					//场景的时钟 150毫秒一次
	public static final String FightSpriteInjured_GE_Id = FightSpriteInjured_GE.class.getSimpleName();	//被攻击
	public static final String PlayerAvatarDead_GE_Id = PlayerAvatarDead_GE.class.getSimpleName();		//被杀死
	public static final String MonsterDead_GE_Id = MonsterDead_GE.class.getSimpleName();				//怪物被杀死
	public static final String PlayerDead_GE_Id = PlayerDead_GE.class.getSimpleName();					//玩家被杀死
	
	
	private long tick_count = 0;//时钟计数器
	
	
	@Override
	public void reset() {
		super.reset();
		this.setLastAttacker(null);
	}

	@Override
	public void ready() {
		addInterGameEventListener(SceneTick_GE_Id);
		addInterGameEventListener(FightSpriteInjured_GE_Id);
		addInterGameEventListener(PlayerAvatarDead_GE_Id);
		addInterGameEventListener(MonsterDead_GE_Id);
		super.ready();
	}

	@Override
	public void suspend() {
		removeInterGameEventListener(SceneTick_GE_Id);
		removeInterGameEventListener(FightSpriteInjured_GE_Id);
		removeInterGameEventListener(PlayerAvatarDead_GE_Id);
		removeInterGameEventListener(MonsterDead_GE_Id);
		super.suspend();
	}
	
	
	@Override
	public void handleGameEvent(GameEvent<?> event) {
		try {
			if (event.isId(SceneTick_GE_Id)) {
				sceneTick_GE(event);
			}

			else if (event.isId(FightSpriteInjured_GE_Id)) {
				fightSpriteInjured_GE(event);
			}

			else if (event.isId(PlayerAvatarDead_GE_Id)) {
				playerAvatarDead_GE(event);
			}

			else if (event.isId(MonsterDead_GE_Id)) {
				monsterDead_GE_Id(event);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}
	
	private void sceneTick_GE(GameEvent<?> event){
		this.tick_count ++;
		PlayerAvatar playerAvatar = this.getConcreteParent();
		if(!playerAvatar.isActivityState()){
			return;
		}
		
		long now = System.currentTimeMillis();
		
		//这里每间隔两秒左右会自动拾取一次
		if(this.tick_count % 20 == 0){
			automaticPick(playerAvatar);
		}
		
		//这里每间隔三秒左右  更新一次离线背包的状态
		if(this.tick_count % 30 == 0){
			boolean refreshOfflineBagState = playerAvatar.refreshOfflineBagState();
			if(refreshOfflineBagState){
				PropertyDictionary pd = new PropertyDictionary();
				MGPropertyAccesser.setOrPutWingModleId(pd, playerAvatar.getOfflineBagState());//离线背包的状态 这里就是翅膀ID
				playerAvatar.getAoiComponent().broadcastProperty(pd);
			}
		}
		
		PlayerAvatarData playerAvatarData = OffLineAIDAOMgr.getAndLoadPlayerAvatarData(playerAvatar.getPlayerId());
		if (this.tick_count % 30 == 0) {
			if(logger.isDebugEnabled()){
				logger.debug(playerAvatarData.toString());
			}
		}
		
		//检查HP MP
		if(playerAvatar.getHP() <= playerAvatarData.getOffLineAISeting().getHp()){
			if (playerAvatar.isCanUseHpItem(now)) {
				HpMpModel reomveHpItem = playerAvatarData.reomveHpItem();
				if(reomveHpItem == null){
					//如果没有可以恢复血量的道具了 则停止挂机 TODO:这里需要打开
//					playerAvatar.setDestructionState();
				}else{
					playerAvatar.setUse_hpitem_cd_time(now);
					if(reomveHpItem.getHp() > 0){
						playerAvatar.modifyHP(playerAvatar, reomveHpItem.getHp());
					}
					if(reomveHpItem.getMp() > 0){
						playerAvatar.modifyMP(reomveHpItem.getMp());
					}
				}
			}
		}
		if(playerAvatar.getMP() <= playerAvatarData.getOffLineAISeting().getMp()){
			if (playerAvatar.isCanUseMpItem(now)) {
				HpMpModel reomveMpItem = playerAvatarData.reomveMpItem();
				if(reomveMpItem == null){
					//如果没有可以恢复血量的道具了 则停止挂机 TODO:这里需要打开
//					playerAvatar.setDestructionState();
				}else{
					playerAvatar.setUse_hpitem_cd_time(now);
					if(reomveMpItem.getHp() > 0){
						playerAvatar.modifyHP(playerAvatar, reomveMpItem.getHp());
					}
					if(reomveMpItem.getMp() > 0){
						playerAvatar.modifyMP(reomveMpItem.getMp());
					}
				}
			}
		}
		
		//如果当前没有可以攻击的目标则搜索攻击目标
		if(this.getLastAttacker() == null){
			Monster nearestMonster = getNearestMonster(playerAvatar,10);
			if(nearestMonster != null){
				this.setLastAttacker(nearestMonster);
			}
		}
		//如果当前没有搜索到攻击目标则随机走动
		if(this.getLastAttacker() == null){
			this.patrol(playerAvatar);
			return;
		}
		//追捕 并 攻击
		chaseAndAttack(playerAvatar,this.getLastAttacker(), now);
	}
	private void fightSpriteInjured_GE(GameEvent<?> event){
		FightSpriteInjured_GE ge = (FightSpriteInjured_GE) event.getData();
		FightSprite attacker = ge.getAttacker();
		if (attacker instanceof Player && !(this.getLastAttacker() instanceof Player) ) {
			//如果攻击者是玩家则将最后目标者设置为玩家
			this.setLastAttacker(attacker);
		}
	}
	private void playerAvatarDead_GE(GameEvent<?> event){
		this.reset();
		
		PlayerAvatarDead_GE playerAvatarDead_GE = (PlayerAvatarDead_GE) event.getData();
		FightSprite attacker = playerAvatarDead_GE.getAttacker();
		
		//如果该替身有携带宝宝 则移除
		PlayerSummonMonsterComponent playerSummonComponent = (PlayerSummonMonsterComponent) this.getConcreteParent().getTagged(PlayerSummonMonsterComponent.Tag);
		Monster monster = playerSummonComponent.getSummonMonster();
		if(monster != null){
			monster.getCrtScene().getMonsterMgrComponent().leaveWorld(monster);
			playerSummonComponent.setSummonMonster(null);
		}
		
		if (attacker instanceof Monster) {
			//被怪物杀死
			Monster killer = (Monster) attacker;
			if (!killer.getMonsterRef().isRegularMonster() && monster.getOwner() != null) {
				attacker = killer.getOwner();
			}
		}
		//设置死亡的时间  等一段时间之后 再放置到指定的场景开始AI
		PlayerAvatar playerAvatar = this.getConcreteParent();
		playerAvatar.setDeathState();
		this.setLastTarget(null);
				
		//掉落物品
		//替身死亡之后的掉落惩罚 (离线背包的 道具)
		PlayerAvatarData andLoadPlayerAvatarData = OffLineAIDAOMgr.getAndLoadPlayerAvatarData(playerAvatar.getPlayerId());
		Map<String, ItemPair> dropItem = andLoadPlayerAvatarData.dropItem();
		int dropItemSize = dropItem == null ? 0 : dropItem.size();
		if(dropItem != null && dropItemSize > 0){
			List<SceneGrid> gridList = GameSceneHelper.getLootSceneGrids(playerAvatar.getAiGameScene(),playerAvatar.getCrtPosition(), dropItemSize);
			LootMgrComponent lootManager = playerAvatar.getAiGameScene().getLootMgrComponent();
			Iterator<Entry<String, ItemPair>> iterator = dropItem.entrySet().iterator();
			int i = 0;
			while(iterator.hasNext()){
				Entry<String, ItemPair> next = iterator.next();
				ItemPair value = next.getValue();
				Loot loot = lootManager.createLoot(value);
				if (attacker instanceof Player) {
					Player player = (Player) attacker;
					PlayerTeam team = player.getPlayerTeamComponent().getTeam();
					if (team != null) {
						loot.setLootKind(LootKind.TeamPlayerLoot);
						loot.setOwnerId(team.getId());
					} else {
						loot.setOwnerId(attacker.getId());
					}
				}else{
					loot.setBornTime(0);
				}
				SceneGrid sceneGrid = gridList.get(i);
				lootManager.enterWorld(loot, playerAvatar.getAiGameScene(), sceneGrid.getColumn(), sceneGrid.getRow());
				i++;
			}
		}
		List<ItemPair> dorpItems = null;
		if(dropItem != null){
			dorpItems = new ArrayList<ItemPair>(dropItem.values());
		}
		
		if (attacker instanceof Player) {
			//被玩家杀死 记录日志
			Player player = (Player) attacker;
			AILogModel aiLogModel = AILogModel.createDeadLogModel(player.getId(),player.getName(),dorpItems);
			PlayerAvatarData playerAvatarData = OffLineAIDAOMgr.getAndLoadPlayerAvatarData(player.getId());
			playerAvatarData.addAILogModel(aiLogModel);
		}else{
			//什么都不用记录
		}
		
	}
	private void monsterDead_GE_Id(GameEvent<?> event){
		MonsterDead_GE monsterDead_GE = (MonsterDead_GE) event.getData();
		FightSprite attacker = monsterDead_GE.getAttacker();
		if(!attacker.getId().equals(this.getConcreteParent().getId())){
			return;
		}
		this.setLastAttacker(null);
		Monster monster = monsterDead_GE.getMonster();
		
		if(!monster.getMonsterRef().isRegularMonster()){
			return;
		}
		//获取经验值
		PlayerAvatar playerAvatar = this.getConcreteParent();
		int exp = (int) MGPropertyAccesser.getExp(monster.getMonsterRef().getProperty());
		float expMultiple = MGPropertyAccesser.getExpMultiple(playerAvatar.getPlayerPropertyDictionary());
		int expGot = exp;
		if (expMultiple != 0.0) {
			expGot = (int) (exp * expMultiple);
		}
		PlayerAvatarData playerAvatarData = OffLineAIDAOMgr.getAndLoadPlayerAvatarData(playerAvatar.getPlayerId());
		playerAvatarData.addExp(expGot);
		
		//自动拾取金币 和 道具
		automaticPick(playerAvatar);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	private void automaticPick(PlayerAvatar playerAvatar){
		PlayerAvatarData playerAvatarData = OffLineAIDAOMgr.getAndLoadPlayerAvatarData(playerAvatar.getPlayerId());
		Collection<Loot> lootList = GameSceneHelper.getLoot(playerAvatar.getAiGameScene(),playerAvatar.getCrtPosition(),10,10);
		
		Iterator<Loot> iterator = lootList.iterator();
		while(iterator.hasNext()){
			Loot loot = iterator.next();
			Item item = loot.getItem();
			ItemPair itemPair = loot.getItemPair();
			if(item != null && playerAvatarData.getOffLineAISeting().isCanPick(item)){
				if(playerAvatarData.addItem(item.getItemRefId(),item.getNumber())){
					playerAvatar.getAiGameScene().getLootMgrComponent().leaveWorld(loot);
				}
			}else if(itemPair != null){
				if(itemPair.getItemRefId().equals("gold")){
					playerAvatarData.addMoney(itemPair.getNumber());
					playerAvatar.getAiGameScene().getLootMgrComponent().leaveWorld(loot);
				}else{
					if (playerAvatarData.getOffLineAISeting().isCanPick(itemPair)) {
						if (playerAvatarData.addItem(itemPair.getItemRefId(),itemPair.getNumber())) {
							playerAvatar.getAiGameScene().getLootMgrComponent().leaveWorld(loot);
						}
					}
				}
			}
		}
	}
	
	/**
	 * 搜寻指定的替身在指定的半径范围内可以找到的最近怪物
	 * @param playerAvatar	替身
	 * @param radiusInGrid	半径范围
	 * @return
	 */
	private Monster getNearestMonster(PlayerAvatar playerAvatar, int radiusInGrid) {
		Monster ret = null;
		int minDistance = Integer.MAX_VALUE;

		Collection<FightSprite> sprites = GameSceneHelper.getFightSprites(
				playerAvatar.getCrtScene(), playerAvatar.getCrtPosition(),
				radiusInGrid);

		for (FightSprite sprite : sprites) {
			if (!(sprite instanceof Monster)) {
				continue;
			}
			int distance = GameSceneHelper.distance(playerAvatar.getCrtScene(),
					playerAvatar.getCrtPosition(), sprite.getCrtPosition());
			if (minDistance <= distance) {
				continue;
			}
			minDistance = distance;
			ret = (Monster) sprite;
			break;
		}
		return ret;
	}
	
	/**
	 * 追捕并攻击
	 * @param playerAvatar	替身
	 * @param target		目标
	 * @param now			当前时间
	 */
	private void chaseAndAttack(PlayerAvatar playerAvatar, FightSprite target, long now) {
		// 追捕
		FightSkill skill = playerAvatar.getUseSkill(target,now);
		boolean neededToChase = isNeededToChase(playerAvatar, target, skill, now);
		if (neededToChase) {
			Position nextPosition = MonsterAIHelper.getNextPosition(playerAvatar, target);
			boolean isWalkable = GameSceneHelper.isWalkable(playerAvatar.getCrtScene(), nextPosition);
			if (isWalkable) {
				playerAvatar.changeState(ChaseState.ChaseState_Id);
				playerAvatar.getPathComponent().stopMove(nextPosition);
				this.updateLastChaseTime(now);
			}else{
				//又要追击 又不能行走 则巡逻
				this.patrol(playerAvatar);
			}
		}

		// 攻击 
//		boolean canCastSkill = canCastSkill(playerAvatar, target, skill, now);
//		if (canCastSkill) {
		boolean canCastSkill = false;//是否成功使用了技能
		if (skill.getRef().isTargetSkill()) {
			int code = FightSkillRuntimeHelper.canCastSkill(playerAvatar,skill, target);
			if (code == MMORPGSuccessCode.CODE_SKILL_SUCCESS) {
				canCastSkill = true;
				playerAvatar.getFightSkillRuntimeComponent().castingSkill(skill, target);
			}
		} else if (skill.getRef().isDirectionSkill()) {
			int code = FightSkillRuntimeHelper.canCastSkill(playerAvatar, skill);
			if (code == MMORPGSuccessCode.CODE_SKILL_SUCCESS) {
				canCastSkill = true;
				byte direction = FightSkillRuntimeHelper.getDirection(playerAvatar.getCrtPosition(), target.getCrtPosition());
				playerAvatar.getFightSkillRuntimeComponent().castingSkill(skill, direction);
			}
		} else if (skill.getRef().isGridSkill()) {
			int code = FightSkillRuntimeHelper.canCastSkill(playerAvatar, skill);
			if (code == MMORPGSuccessCode.CODE_SKILL_SUCCESS) {
				canCastSkill = true;
				playerAvatar.getFightSkillRuntimeComponent().castingSkill(skill, target.getCrtPosition());
			}
		}
		if (canCastSkill) {
			costSkillMp(playerAvatar, skill);
			this.updateLastAttackTime(now);
			playerAvatar.changeState(FightState.FightState_Id);
			if (target.isDead()) {
				Collection<Monster> monsters = GameSceneHelper.getAOIInterestedMonsters(target);
				for (Monster m : monsters) {
					if (m.getMonsterRef().isRegularMonster()) {
						if (target.equals(getLastTarget())) {
							forsakeTarget();
						}
					}
				}
			}
		}
//		}
		
		if(!neededToChase && !canCastSkill){
			this.patrol(playerAvatar);
		}
	}
	/** 消耗技能MP */
	private void costSkillMp(FightSprite caster, FightSkill skill) {
		checkArgument(caster != null);
		checkArgument(skill != null);
		SkillLevelRef levelRef = skill.getLevelRef();
		if (levelRef != null) {
			PropertyDictionary property = levelRef.getProperty();
			checkArgument(property != null);
			int mpRequired = MGPropertyAccesser.getMP(property);
			int currentMp = caster.getMP();
			if (currentMp >= mpRequired) {
				caster.modifyMP(-mpRequired);
				PropertyDictionary pd = new PropertyDictionary();
				int newMp = caster.getMP();
				MGPropertyAccesser.setOrPutMP(pd, newMp);
				if (caster instanceof PlayerAvatar) {
					PlayerAvatar playerAvatar = (PlayerAvatar) caster;
					playerAvatar.notifyPorperty(pd);
				}
			}
		}

	}
	private boolean canCastSkill(PlayerAvatar who, FightSprite target, FightSkill skill, long now) {
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

		if (target.equals(who.getPerceiveComponent().getLastAttacker()) && target.getFightSpriteStateMgr().isState(StealthState.StealthState_Id)) {
			return true;
		}

		if (target.getFightSpriteStateMgr().isState(StealthState.StealthState_Id)) {
			return false;
		}

		int distance = GameSceneHelper.distance(who.getCrtScene(), who.getCrtPosition(), target.getCrtPosition());
		int skillRange = MGPropertyAccesser.getSkillRange(skill.getRef().getProperty());
		boolean inRange = distance <= skillRange;

		boolean validTarget = MonsterAIHelper.isValidTarget(who, skill, target);
		return inRange && validTarget;
	}
	private boolean isNeededToChase(PlayerAvatar who, FightSprite target, FightSkill skill, long now) {
		if (who == null || target == null || skill == null) {
			return false;
		}

		if (!who.getCrtScene().equals(target.getCrtScene())) {
			return false;
		}
		
		boolean enoughTimeElapsed = now - who.getPerceiveComponent().getLastChaseTime() >= MonsterPerceiveComponent.chaseInterval;

		int distance = GameSceneHelper.distance(who.getCrtScene(), who.getCrtPosition(), target.getCrtPosition());
		int skillRange = MGPropertyAccesser.getSkillRange(skill.getRef().getProperty());
		boolean outOfSkillRange = distance > skillRange;

		boolean outOfAttackRange = who.isOutOfAttackRange(target);
		return outOfSkillRange && enoughTimeElapsed && outOfAttackRange;
	}
	
	
	
	
	
	/** 巡逻 
	 *  随机到处走   
	 */
	private void patrol(PlayerAvatar who) {
		if (this.tick_count % 10 == 0) {
			Position patrolTo = getNextPatrolPosition(who);
			boolean isWalkable = GameSceneHelper.isWalkable(who.getCrtScene(),
					patrolTo);
			if (isWalkable && !patrolTo.equals(who.getCrtPosition())) {
				who.getPathComponent().stopMove(patrolTo);
				who.changeState(PatrolState.PatrolState_Id);
			}
		}
	}
	private Position getNextPatrolPosition(PlayerAvatar who) {
		byte direction = (byte) SFRandomUtils.random(0, EightDirection.nDirection - 1);
		int moveSpeed = who.getPathComponent().getMoveSpeed();
		int moveSpeedInAOIGrid = moveSpeed / SceneAOILayer.AOIGRID_MULTIPLE;
		Position nextPosition = GameSceneHelper.getForwardPosition(who.getCrtScene(), direction, who.getCrtPosition(), moveSpeedInAOIGrid / 2);
		if (!GameSceneHelper.isWalkable(who.getCrtScene(), nextPosition)) {
			nextPosition = MonsterAIHelper.getWalkablePositionToward(who.getCrtScene(), nextPosition, direction);
		}
		if (logger.isDebugEnabled()) {
			logger.debug("getNextPosition next position " + nextPosition + " " + who.getId() + " current position " + who.getCrtPosition() + " " + Thread.currentThread().getId());
			GameSceneHelper.checkInAOIGridCenter(nextPosition);
		}
		return nextPosition;
	}
	
	
}
