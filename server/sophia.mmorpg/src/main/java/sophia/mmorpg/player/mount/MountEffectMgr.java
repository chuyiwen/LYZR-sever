package sophia.mmorpg.player.mount;

import org.apache.log4j.Logger;

import sophia.foundation.communication.core.MessageFactory;
import sophia.foundation.property.PropertyDictionary;
import sophia.game.GameRoot;
import sophia.mmorpg.base.sprite.fightProperty.FightPropertyEffectFacade;
import sophia.mmorpg.base.sprite.state.FSMStateFactory;
import sophia.mmorpg.base.sprite.state.posture.MountedState;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.mount.event.G2C_Mount_IsOnMount;
import sophia.mmorpg.player.mount.event.MountEventDefines;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;

public class MountEffectMgr {
	private static final Logger logger = Logger.getLogger(MountEffectMgr.class);

	private Player player;

	public MountEffectMgr(Player player) {
		this.player = player;
	}

	public void restore(Mount mount) {
		if (mount == null) {
			if (logger.isDebugEnabled()) {
				logger.debug("Mount is null ");
			}
			return;
		}
		attachWithoutSnapshot(mount);
		changeProperty(mount);
	}

	/**
	 * 玩家获取第一个坐骑
	 * 
	 * @param mount
	 */
	public void firstGet(Mount mount) {
		if (mount == null || player == null) {
			if (logger.isDebugEnabled()) {
				logger.debug("Mount is null or Player is null");
			}
			return;
		}

		// 默认上马,广播属性(速度,坐骑模型)
		mount.setMountState(Mount.MOUNT_STATE_UP);
		player.getFightSpriteStateMgr().switchState(FSMStateFactory.getPostureState(MountedState.MountedState_Id));

		// 属性(通知除速度)
		PropertyDictionary effectPd = mount.getMountRef().getEffect().add(mount.getMountRef().getTmpEffect());
		FightPropertyEffectFacade.attachAndNotify(player, effectPd);

		G2C_Mount_IsOnMount response = MessageFactory.getConcreteMessage(MountEventDefines.G2C_Mount_IsOnMount);
		response.setOnMount(player.getFightSpriteStateMgr().isState(FSMStateFactory.getPostureState(MountedState.MountedState_Id)));
		GameRoot.sendMessage(player.getIdentity(), response);

		PlayerMountFacade.changePropertyAndBroadcast(player);
	}

	public void attachSpeedEffect(Mount mount) {
		FightPropertyEffectFacade.attachAndNotify(player, mount.getMountRef().getTmpEffect());
	}

	public void detach(Mount mount) {
		FightPropertyEffectFacade.detachAndNotify(player, mount.getMountRef().getTmpEffect());
	}

	public void detachEffect(Mount mount) {
		FightPropertyEffectFacade.detachAndNotify(player, mount.getMountRef().getEffect());
	}

	public void attachEffect(Mount mount) {
		FightPropertyEffectFacade.attachAndNotify(player, mount.getMountRef().getEffect());
	}

	public void detachAndSnapshot(Mount mount) {
		FightPropertyEffectFacade.detachAndSnapshot(player, mount.getMountRef().getEffect());
	}

	public void attachWithoutSnapshot(Mount mount) {
		byte mountState = mount.getMountState();
		PropertyDictionary pd = mount.getMountRef().getEffect();
		
		if(Mount.MOUNT_STATE_UP == mountState) {
			pd = pd.add(mount.getMountRef().getTmpEffect());
		}

		FightPropertyEffectFacade.attachWithoutSnapshot(player, pd);
	}

	/**
	 * 改变玩家坐骑模型属性
	 * 
	 * @param mount
	 * 
	 */
	private void changeProperty(Mount mount) {
		if (mount == null) {
			if (logger.isDebugEnabled()) {
				logger.debug("Mount is null ");
			}
			return;
		}
		byte mountState = mount.getMountState();
		int modleId = 0;
		if (Mount.MOUNT_STATE_UP == mountState) {
			MountRef mountRef = player.getPlayerMountComponent().getMountManager().getCrtMount().getMountRef();
			modleId = MGPropertyAccesser.getModelId(mountRef.getProperty());
			player.getFightSpriteStateMgr().switchState(FSMStateFactory.getPostureState(MountedState.MountedState_Id));
		}
		MGPropertyAccesser.setOrPutMountModleId(player.getProperty(), modleId);
	}

}
