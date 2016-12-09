package newbee.morningGlory.mmorpg.player.offLineAI;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import newbee.morningGlory.mmorpg.player.offLineAI.gameEvent.PlayerAvatarDead_GE;
import newbee.morningGlory.mmorpg.player.offLineAI.persistence.OffLineAIDAOMgr;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.property.PropertyDictionary;
import sophia.game.component.communication.GameEvent;
import sophia.mmorpg.base.scene.GameScene;
import sophia.mmorpg.base.scene.GameSceneHelper;
import sophia.mmorpg.base.sprite.FightSprite;
import sophia.mmorpg.base.sprite.SpriteTypeDefine;
import sophia.mmorpg.base.sprite.aoi.SpriteAOIComponent;
import sophia.mmorpg.base.sprite.aoi.SpritePathComponent;
import sophia.mmorpg.base.sprite.fightProperty.FightPropertyMgr;
import sophia.mmorpg.base.sprite.fightSkill.FightSkill;
import sophia.mmorpg.base.sprite.fightSkill.FightSkillRuntimeComponent;
import sophia.mmorpg.base.sprite.state.action.IdleState;
import sophia.mmorpg.core.CDMgr;
import sophia.mmorpg.core.state.FSMStateBase;
import sophia.mmorpg.monster.Monster;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.property.PlayerConfig;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;
import sophia.mmorpg.ref.editorgen.MGPropertySymbolDefines;

public class PlayerAvatar extends FightSprite{

	private static final Logger logger = Logger.getLogger(PlayerAvatar.class);
	
	private static final String PlayerAvatar_GameSprite_Type = PlayerAvatar.class.getSimpleName();
	
	private static final int STATE_CREATE 			= 0;//刚创建出来
	private static final int STATE_ACTIVITY 		= 1;//在AI活动中
	private static final int STATE_DEATH 			= 2;//死亡
	private static final int STATE_DESTRUCTION 	= 3;//可以销毁了
	
	
	private Player player;				//代理玩家
	private GameScene aiGameScene;		//执行AI的游戏场景
	private int state = STATE_CREATE;	//当前状态 
	private long createTime;			//创建该对象的时间
	private long deathTime;				//死亡的时间
	private int offlineBagState;		//离线背包的状态  这里对应的翅膀ID
	private long use_hpitem_cd_time;	//每次回复HP的时间
	private long use_mpitem_cd_time;	//每次回复MP的时间
	
	private long lastUseSkillTime;		//上一次选中技能的时间
	private FightSkill lastUseSkill;	//上一次使用的技能
	
	
	
	
	@SuppressWarnings("unchecked")
	public PlayerAvatar(Player player,GameScene aiGameScene) {
		super();
		this.player = player;
		this.state = STATE_CREATE;
		this.createTime = System.currentTimeMillis();
		this.setId(UUID.randomUUID().toString());
		this.setName(this.player.getName());
		this.fightSkillRuntimeComponent = ((FightSkillRuntimeComponent<PlayerAvatar>) createComponent(FightSkillRuntimeComponent.class));
		setAoiComponent((SpriteAOIComponent<PlayerAvatar>) createComponent(SpriteAOIComponent.class));
		setPathComponent((SpritePathComponent<PlayerAvatar>) createComponent(SpritePathComponent.class));
		// 绑定AI
		this.setPerceiveComponent((PlayerAvatarPerceiveComponent) this.createComponent(PlayerAvatarPerceiveComponent.class));
		// 设置要挂机的场景
		this.aiGameScene = aiGameScene;
		this.refreshOfflineBagState();
	}
	/** 当前时间是否可以使用HP道具 */
	boolean isCanUseHpItem(long now){
		if((now - this.use_hpitem_cd_time) >= PlayerAvatarMgr.ANSWER_CD){
			return true;
		}
		return false;
	}
	/** 当前时间是否可以使用MP道具 */
	boolean isCanUseMpItem(long now){
		if((now - this.use_mpitem_cd_time) >= PlayerAvatarMgr.ANSWER_CD){
			return true;
		}
		return false;
	}
	void setUse_hpitem_cd_time(long use_hpitem_cd_time) {
		this.use_hpitem_cd_time = use_hpitem_cd_time;
	}
	void setUse_mpitem_cd_time(long use_mpitem_cd_time) {
		this.use_mpitem_cd_time = use_mpitem_cd_time;
	}
	
	
	/**
	 * 刷新离线背包状态
	 * @return 如果状态有改变则返回true
	 */
	boolean refreshOfflineBagState(){
		PlayerAvatarData playerAvatarData = OffLineAIDAOMgr.getAndLoadPlayerAvatarData(this.player.getId());
		int size = playerAvatarData.getReadItemList().size();
		int newState = PlayerAvatarMgr.getOfflineBagState(size);
		if(this.offlineBagState != newState){
			this.offlineBagState = newState;
			return true;
		}
		return false;
	}
	/** 设置为死亡状态 */
	void setDeathState(){
		this.state = STATE_DEATH;
		this.deathTime = System.currentTimeMillis();
		PlayerAvatarMgr.leaveWorld(this);
	}
	/** 设置为活动状态 */
	void setActivityState(){
		this.state = STATE_ACTIVITY;
	}
	/** 设置为销毁状态 */
	void setDestructionState(){
		this.state = STATE_DESTRUCTION;
	}
	public int getOfflineBagState() {
		return offlineBagState;
	}
	boolean isDeathState(){
		return this.state == STATE_DEATH;
	}
	boolean isActivityState(){
		return this.state == STATE_ACTIVITY;
	}
	boolean isDestructionState(){
		return this.state == STATE_DESTRUCTION;
	}
	boolean isCreateState(){
		return this.state == STATE_CREATE;
	}
	
	
	
	FightSkill getUseSkill(FightSprite target,long now){
		//TODO：测试指定的技能
//		if(true){
//			return testGetUseSkill(target, now);
//		}
		
		byte profession = this.getProfession();
		
		FightSkill auxiliarySkill = null;
		if (PlayerConfig.isWarrior(profession)) {
		}else if (PlayerConfig.isEnchanter(profession)) {//法师
			//TODO: 是否没有魔法盾了?????????????
			boolean bor = false;
			if(bor){
				auxiliarySkill = this.player.getPlayerFightSkillComponent().getPlayerFightSkillTree().getFightSkill("skill_fs_11");//魔法盾
			}
		}else if (PlayerConfig.isWarlock(profession)) {//道士
			//TODO: 如果没有神兽了则需要召唤神兽
			
			//如果HP损失一半了 则使用治愈术
			if (this.getHP() < this.getHPMax() / 2) {
				auxiliarySkill = this.player.getPlayerFightSkillComponent().getPlayerFightSkillTree().getFightSkill("skill_ds_1");// 治愈术
			}
			
		}
		if(auxiliarySkill != null && isCanUse(auxiliarySkill)){
			return auxiliarySkill;
		}
		
		if (this.lastUseSkill != null) {
			if ((now - this.lastUseSkillTime) < PlayerAvatarMgr.RANDOMSKILLTIME_STAMP) {
				return this.lastUseSkill;
			}
		}
		
		FightSkill attackSkill = null;
		Collection<Monster> monsters = GameSceneHelper.getMonsters(this.aiGameScene, target.getCrtPosition(), 2, 2);
		int monstersSize = monsters == null ? 1 : monsters.size();
		if (PlayerConfig.isWarrior(profession)) {
			//战士
			attackSkill = getWarrior(monstersSize);
		}else if (PlayerConfig.isEnchanter(profession)) {
			//法师
			attackSkill = getEnchanter(monstersSize);
		}else if (PlayerConfig.isWarlock(profession)) {
			//道士
			attackSkill = getWarlock(monstersSize);
		}
		if (attackSkill != null) {
			if (!isCanUse(attackSkill)) {
				attackSkill = null;
			}
		}
		
		if(attackSkill == null){
			attackSkill = this.player.getPlayerFightSkillComponent().getPlayerFightSkillTree().getFightSkill("skill_0");//普通攻击
		}
		if(attackSkill == null){
			return null;
		}
		this.lastUseSkill = attackSkill;
		this.lastUseSkillTime = now;
		return this.lastUseSkill;
	}
	
	private boolean isCanUse(FightSkill attackSkill){
		// 魔法不够 则使用普通攻击
		int mpRequired = MGPropertyAccesser.getMP(attackSkill.getLevelRef().getProperty());
		if (this.getMP() < mpRequired) {
			return false;
		}
		// CD不够则使用普通攻击
		CDMgr basicCDMgr = this.getFightSkillRuntimeComponent().getBasicCDMgr();
		if (basicCDMgr.isCDStarted(attackSkill.getRefId())&& !basicCDMgr.isOutOfCD(attackSkill.getRefId())) {
			return false;
		}
		return true;
	}
	
	private FightSkill getWarrior(int targetNum){//战士
		FightSkill skill = null;
		if(targetNum > 2){
			skill = this.player.getPlayerFightSkillComponent().getPlayerFightSkillTree().getFightSkill("skill_zs_4");//半月
		}else{
			skill = this.player.getPlayerFightSkillComponent().getPlayerFightSkillTree().getFightSkill("skill_zs_6");//烈火
		}
		return skill;
	}
	private FightSkill getEnchanter(int targetNum){
		FightSkill skill = null;
		if(targetNum > 2){
			skill = this.player.getPlayerFightSkillComponent().getPlayerFightSkillTree().getFightSkill("skill_fs_12");//冰咆哮
			if(skill == null){
				skill = this.player.getPlayerFightSkillComponent().getPlayerFightSkillTree().getFightSkill("skill_fs_7");//爆裂火焰
			}
		}else{
			skill = this.player.getPlayerFightSkillComponent().getPlayerFightSkillTree().getFightSkill("skill_fs_4");//雷电
			if(skill == null){
				skill = this.player.getPlayerFightSkillComponent().getPlayerFightSkillTree().getFightSkill("skill_fs_6");//大火球
			}
			if(skill == null){
				skill = this.player.getPlayerFightSkillComponent().getPlayerFightSkillTree().getFightSkill("skill_fs_1");//小火球
			}
		}
		return skill;
	}
	private FightSkill getWarlock(int targetNum){
		FightSkill skill = this.player.getPlayerFightSkillComponent().getPlayerFightSkillTree().getFightSkill("skill_ds_4");//符咒
		return skill;
	}
	private FightSkill testGetUseSkill(FightSprite target,long now){
			FightSkill skill = null;
			
//			skill =  this.player.getPlayerFightSkillComponent().getPlayerFightSkillTree().get("skill_fs_7");//爆裂火焰
//			skill =  this.player.getPlayerFightSkillComponent().getPlayerFightSkillTree().get("skill_fs_4");//雷电
//			skill =  this.player.getPlayerFightSkillComponent().getPlayerFightSkillTree().get("skill_fs_6");//大火球
//			skill =  this.player.getPlayerFightSkillComponent().getPlayerFightSkillTree().get("skill_fs_1");//小火球
//			skill =  this.player.getPlayerFightSkillComponent().getPlayerFightSkillTree().get("skill_fs_11");// 魔法盾
			
			
//			skill = this.player.getPlayerFightSkillComponent().getPlayerFightSkillTree().get("skill_zs_4");//半月
//			skill = this.player.getPlayerFightSkillComponent().getPlayerFightSkillTree().get("skill_zs_6");//烈火
			
			
//			skill = this.player.getPlayerFightSkillComponent().getPlayerFightSkillTree().get("skill_ds_1");// 治愈术
//			skill = this.player.getPlayerFightSkillComponent().getPlayerFightSkillTree().get("skill_ds_4");//符咒
			skill = this.player.getPlayerFightSkillComponent().getPlayerFightSkillTree().getFightSkill("skill_ds_11");//召唤
			
			
			//CD不够则使用普通攻击
			CDMgr basicCDMgr = this.getFightSkillRuntimeComponent().getBasicCDMgr();
			if (basicCDMgr.isCDStarted(skill.getRefId()) && !basicCDMgr.isOutOfCD(skill.getRefId())) {
				return this.player.getPlayerFightSkillComponent().getPlayerFightSkillTree().getFightSkill("skill_0");
			}
			return skill;
	}
	
	/** 获取攻击速度 */
	public int getAttackSpeed(){
		return this.player.getFightPropertyMgrComponent().getFightPropertyMgr().getSnapshotValueById(MGPropertySymbolDefines.AtkSpeed_Id);
	}
	/** 获取攻击范围 */
	public byte getAttackDistance(){
		return MGPropertyAccesser.getAttackDistance(this.player.getProperty());
	}
	/** 是否超出攻击范围 */
	boolean isOutOfAttackRange(FightSprite target) {
		byte attackDistance = this.getAttackDistance();
		int distance = GameSceneHelper.distance(this.getCrtScene(), this.getCrtPosition(), target.getCrtPosition());
		return distance > attackDistance;
	}
	public GameScene getAiGameScene() {
		return aiGameScene;
	}
	public long getCreateTime() {
		return createTime;
	}
	public long getDeathTime() {
		return deathTime;
	}
	String getPlayerId(){
		return this.player.getId();
	}
	PropertyDictionary getPlayerPropertyDictionary(){
		return this.player.getProperty();
	}
	
	/**
	 * 复活
	 */
	@Override
	public void revive() {
		fightSpriteStateMgr.switchState(IdleState.IdleState_Id);
		FightPropertyMgr fightPropertyMgr = fightPropertyMgrComponent.getFightPropertyMgr();
		int maxHP = fightPropertyMgr.getSnapshotValueById(MGPropertySymbolDefines.MaxHP_Id);
		int maxMP = fightPropertyMgr.getSnapshotValueById(MGPropertySymbolDefines.MaxMP_Id);
		fightPropertyMgr.setSnapshotValueById(MGPropertySymbolDefines.HP_Id, maxHP);
		fightPropertyMgr.setSnapshotValueById(MGPropertySymbolDefines.MP_Id, maxMP);
		PropertyDictionary pd = new PropertyDictionary();
		MGPropertyAccesser.setOrPutHP(pd, maxHP);
		MGPropertyAccesser.setOrPutMP(pd, maxMP);
		notifyPorperty(pd);
		changeState(IdleState.IdleState_Id);
		super.revive();
	}
	
	void notifyPorperty(PropertyDictionary pd) {
		getAoiComponent().broadcastProperty(pd);
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
			PlayerAvatarDead_GE playerAvatarDead_GE = new PlayerAvatarDead_GE(attacker,this);
			GameEvent<?> event = GameEvent.getInstance(PlayerAvatarDead_GE.class.getSimpleName(), playerAvatarDead_GE);
			this.handleGameEvent(event);
			attacker.handleGameEvent(event);
			GameEvent.pool(event);
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
	
	@Override
	public boolean isEnemyTo(FightSprite fightSprite) {
		if (!StringUtils.equals(fightSprite.getId(), this.getId())) {
			return true;
		}
		return false;
	}
	@Override
	public String getGameSpriteType() {
		return PlayerAvatar_GameSprite_Type;
	}
	
	@Override
	public byte getSpriteType(){
		return SpriteTypeDefine.GameSprite_PlayerAvatar;
	}

	public Player getPlayer() {
		return player;
	}
	
	/**
	 * 打包该精灵的AOI消息
	 */
	public void packSceneData(IoBuffer buffer) {
		byte[] pdData = getProperty().toByteArray();
		buffer.putShort((short)pdData.length);
		buffer.put(pdData);
		List<FSMStateBase<FightSprite>> stateList = this.getStateList();
		buffer.put((byte) stateList.size());
		for (FSMStateBase<FightSprite> state : stateList) {
			buffer.putShort(state.getId());
		}
	}
	
}
