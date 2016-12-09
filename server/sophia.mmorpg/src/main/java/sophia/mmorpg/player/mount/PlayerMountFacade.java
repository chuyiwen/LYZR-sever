package sophia.mmorpg.player.mount;

import org.apache.log4j.Logger;

import sophia.foundation.property.PropertyDictionary;
import sophia.mmorpg.base.sprite.state.FSMStateFactory;
import sophia.mmorpg.base.sprite.state.posture.MountedState;
import sophia.mmorpg.base.sprite.state.posture.StandedState;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;
import sophia.mmorpg.ref.editorgen.MGPropertySymbolDefines;
import sophia.mmorpg.utils.SFRandomUtils;

public final class PlayerMountFacade {
	public static final Logger logger = Logger.getLogger(PlayerMountFacade.class);

	/**
	 * 让玩家下马
	 */
	public final static boolean playerMount_Down(Player player) {
		if (player.changeState(StandedState.StandedState_Id)) {
			if(logger.isDebugEnabled()){    
				logger.debug("下马成功！");
			}
			player.getPlayerMountComponent().getMountEffectMgr().detach(player.getPlayerMountComponent().getMountManager().getCrtMount());
			player.getPlayerMountComponent().getMountManager().getCrtMount().down();

			return true;
		}
		return false;
	}

	/**
	 * 让玩家上马
	 */
	public final static boolean playerMount_Up(Player player) {
		if (player.changeState(MountedState.MountedState_Id)) {
			if(logger.isDebugEnabled()){   
				logger.debug("上马成功！");
			}
			player.getPlayerMountComponent().getMountManager().getCrtMount().up();
			player.getPlayerMountComponent().getMountEffectMgr().attachSpeedEffect(player.getPlayerMountComponent().getMountManager().getCrtMount());
			return true;
		}
		return false;
	}

	/**
	 * 广播改变玩家坐骑模型属性,速度属性
	 * 
	 * @param player
	 */
	public final static void changePropertyAndBroadcast(Player player) {
		int modleId = getMondleId(player);
		MGPropertyAccesser.setOrPutMountModleId(player.getProperty(), getMondleId(player));
		// 广播属性
		PropertyDictionary pd = new PropertyDictionary();
		// 坐骑模型
		MGPropertyAccesser.setOrPutMountModleId(pd, modleId);

		// 移动速度
		int crtSpeed = player.getFightPropertyMgrComponent().getFightPropertyMgr().getSnapshotValueById(MGPropertySymbolDefines.MoveSpeed_Id);
		MGPropertyAccesser.setOrPutMoveSpeed(pd, crtSpeed);
		// 攻击速度
		int crtAtkSpeed = player.getFightPropertyMgrComponent().getFightPropertyMgr().getSnapshotValueById(MGPropertySymbolDefines.AtkSpeed_Id);
		MGPropertyAccesser.setOrPutAtkSpeed(pd, crtAtkSpeed);

		player.getPathComponent().setMoveSpeed(crtSpeed);
		if (player.getCrtScene() != null && player.isSceneReady()) {
			player.getAoiComponent().broadcastProperty(pd);
		} else {
			if (logger.isInfoEnabled()) {
				logger.info("can't broadcast  The cause of CrtScene= " + "player.getCrtScene()" + ", SceneReady = " + player.isSceneReady());
			}
		}
		player.notifyPorperty(pd);
	}

	private final static int getMondleId(Player player) {
		if (!player.getFightSpriteStateMgr().isState(FSMStateFactory.getPostureState(MountedState.MountedState_Id))) {
			return 0;
		}
		MountRef mountRef = player.getPlayerMountComponent().getMountManager().getCrtMount().getMountRef();
		int modleId = MGPropertyAccesser.getModelId(mountRef.getProperty());
		return modleId;
	}

	public static int baoJiRate() {
		int rdm = SFRandomUtils.random100();// [1,100]
		int baoJiRate = getBaoJiRate(rdm);
		// 代码保护
		if (baoJiRate < 1 || baoJiRate > 3) {
			baoJiRate = 1;
		}
		return baoJiRate;
	}

	public static int getBaoJiRate(int rdm) {
		int baoJiRate = 1;
		if (rdm > 0 && rdm <= 10) {
			baoJiRate = 3;
		} else if (rdm > 10 && rdm <= 50) {
			baoJiRate = 2;
		}
		return baoJiRate;
	}

}
