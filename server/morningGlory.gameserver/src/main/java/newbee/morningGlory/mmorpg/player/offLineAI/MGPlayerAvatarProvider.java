package newbee.morningGlory.mmorpg.player.offLineAI;

import newbee.morningGlory.mmorpg.player.pk.MGPlayerPKComponent;
import newbee.morningGlory.mmorpg.player.summons.PlayerSummonMonsterComponent;
import newbee.morningGlory.mmorpg.sprite.MGFightProcessComponent;
import newbee.morningGlory.mmorpg.sprite.MGFightPropertyMgr;
import newbee.morningGlory.mmorpg.sprite.buff.MGFightSpriteBuffComponent;
import newbee.morningGlory.mmorpg.sprite.player.fightSkill.MGFightSkillRuntime;
import sophia.foundation.property.PropertyDictionary;
import sophia.mmorpg.GameObjectProvider;
import sophia.mmorpg.base.scene.GameScene;
import sophia.mmorpg.base.sprite.FightSprite;
import sophia.mmorpg.base.sprite.fightProperty.FightPropertyMgr;
import sophia.mmorpg.base.sprite.fightSkill.FightSkillRuntimeComponent;
import sophia.mmorpg.base.sprite.state.FightSpriteStateMgr;
import sophia.mmorpg.base.sprite.state.action.IdleState;
import sophia.mmorpg.base.sprite.state.movement.StopState;
import sophia.mmorpg.base.sprite.state.posture.StandedState;
import sophia.mmorpg.core.PropertyDictionaryModifyPhase;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;

public class MGPlayerAvatarProvider implements GameObjectProvider<PlayerAvatar> {

	private static MGPlayerAvatarProvider intsance = new MGPlayerAvatarProvider();

	public static MGPlayerAvatarProvider getInstance() {
		return intsance;
	}

	@Override
	public PlayerAvatar get(Class<PlayerAvatar> type) {
		return null;
	}

	@Override
	public PlayerAvatar get(Class<PlayerAvatar> type, Object... args) {
		if (!(args[0] instanceof Player)) {
			return null;
		}
		Player player = (Player) args[0];
		GameScene searchGameScene = (GameScene)args[1];
		
		
		PlayerAvatar playerAvatar = new PlayerAvatar(player,searchGameScene);
		PropertyDictionary playerPd = player.getProperty();

		
		PropertyDictionary pd = playerAvatar.getProperty();
		MGPropertyAccesser.setOrPutOwnerId(pd, player.getId());//玩家归属唯一ID
		MGPropertyAccesser.setOrPutName(pd, player.getName());//玩家名称
		MGPropertyAccesser.setOrPutProfessionId(pd, player.getProfession());//玩家职业
		MGPropertyAccesser.setOrPutLevel(pd, player.getLevel());
		MGPropertyAccesser.setOrPutGender(pd, MGPropertyAccesser.getGender(playerPd));//性别
		MGPropertyAccesser.setOrPutHP(pd, player.getHP());//当前血量
		MGPropertyAccesser.setOrPutMaxHP(pd, player.getHPMax());//当前血量上限
		MGPropertyAccesser.setOrPutMoveSpeed(pd, player.getMoveSpeed());//移动速度
		MGPropertyAccesser.setOrPutWeaponModleId(pd, MGPropertyAccesser.getWeaponModleId(playerPd));//武器ID, 0表示没有
		MGPropertyAccesser.setOrPutArmorModleId(pd, MGPropertyAccesser.getArmorModleId(playerPd));//铠甲ID, 0表示没有
		MGPropertyAccesser.setOrPutWingModleId(pd,playerAvatar.getOfflineBagState());//翅膀ID, 0表示没有
		MGPropertyAccesser.setOrPutMountModleId(pd, MGPropertyAccesser.getMountModleId(playerPd));//坐骑ID, 0表示没有
		byte knight = MGPropertyAccesser.getKnight(playerPd); 
		if (knight < 0){
			knight = 0;
		}
		MGPropertyAccesser.setOrPutKnight(pd, knight);//爵位等级
		MGPropertyAccesser.setOrPutUnionName(pd, MGPropertyAccesser.getUnionName(playerPd) == null ? "" : MGPropertyAccesser.getUnionName(playerPd));
		MGPropertyAccesser.setOrPutIsKingCity(pd, MGPropertyAccesser.getIsKingCity(playerPd));//0:不是王城公会成员,1:王城公会成员
		
		

		
		FightSpriteStateMgr fightSpriteStateMgr = playerAvatar.getFightSpriteStateMgr();
		fightSpriteStateMgr.setDefaultMovementState(StopState.StopState_Id);
		fightSpriteStateMgr.setCrtMovementState(fightSpriteStateMgr.getDefaultMovementState());
		fightSpriteStateMgr.setDefaultPostureState(StandedState.StandedState_Id);
		fightSpriteStateMgr.setCrtPostureState(fightSpriteStateMgr.getDefaultPostureState());
		fightSpriteStateMgr.setDefaultActionState(IdleState.IdleState_Id);
		fightSpriteStateMgr.setCrtActionState(fightSpriteStateMgr.getDefaultActionState());

		MGFightSpriteBuffComponent<PlayerAvatar> fightSpriteBuffComponent = (MGFightSpriteBuffComponent) playerAvatar.createComponent(MGFightSpriteBuffComponent.class,MGFightSpriteBuffComponent.Tag);
		fightSpriteBuffComponent.setParent(playerAvatar);
		MGFightProcessComponent<PlayerAvatar> processComponent = (MGFightProcessComponent) playerAvatar.createComponent(MGFightProcessComponent.class,MGFightProcessComponent.Tag);
		processComponent.setOwner(playerAvatar);
		playerAvatar.getFightPropertyMgrComponent().setFightPropertyMgr(new MGFightPropertyMgr());
		FightSkillRuntimeComponent<? extends FightSprite> fightSkillRuntimeComponent = playerAvatar.getFightSkillRuntimeComponent();
		MGFightSkillRuntime skillRuntime = new MGFightSkillRuntime();
		fightSkillRuntimeComponent.setFightSkillRuntime(skillRuntime);
		
		playerAvatar.createComponent(MGPlayerPKComponent.class, MGPlayerPKComponent.Tag);
		playerAvatar.createComponent(PlayerSummonMonsterComponent.class,PlayerSummonMonsterComponent.Tag);

		// 拷贝玩家战斗属性
		FightPropertyMgr fightPropertyMgr = player.getFightPropertyMgrComponent().getFightPropertyMgr();
		PropertyDictionaryModifyPhase fightProperty = fightPropertyMgr.getSnapshotFromPool();
		try {
			FightPropertyMgr selfFightPropertyMgr = playerAvatar.getFightPropertyMgrComponent().getFightPropertyMgr();
			selfFightPropertyMgr.setCrtPropertyDictionary(fightProperty.getPropertyDictionary());
		} finally {
			FightPropertyMgr.recycleSnapshotToPool(fightProperty);
		}
		return playerAvatar;
	}

}
