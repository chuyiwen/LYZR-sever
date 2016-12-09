package newbee.morningGlory.mmorpg.player.offLineAI;

import java.util.List;

import newbee.morningGlory.mmorpg.player.offLineAI.event.C2G_DrawOffLineAIReward;
import newbee.morningGlory.mmorpg.player.offLineAI.event.C2G_OffLineAISeting;
import newbee.morningGlory.mmorpg.player.offLineAI.event.C2G_ViewOffLineAIReward;
import newbee.morningGlory.mmorpg.player.offLineAI.event.G2C_DrawOffLineAIReward;
import newbee.morningGlory.mmorpg.player.offLineAI.event.G2C_ViewOffLineAIReward;
import newbee.morningGlory.mmorpg.player.offLineAI.event.OffLineAIEventDefines;
import newbee.morningGlory.mmorpg.player.offLineAI.persistence.OffLineAIDAOMgr;

import org.apache.log4j.Logger;

import sophia.foundation.communication.core.ActionEventBase;
import sophia.foundation.communication.core.MessageFactory;
import sophia.game.GameRoot;
import sophia.game.component.communication.GameEvent;
import sophia.mmorpg.GameObjectFactory;
import sophia.mmorpg.base.ConcreteComponent;
import sophia.mmorpg.base.scene.GameScene;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.gameEvent.EnterWorld_GE;
import sophia.mmorpg.player.gameEvent.LeaveWorld_GE;
import sophia.mmorpg.player.itemBag.ItemOptSource;
import sophia.mmorpg.player.itemBag.ItemPair;

public class OffLineAIComponent extends ConcreteComponent<Player> {
	
	private static final Logger logger = Logger.getLogger(OffLineAIComponent.class);
	public static final String Tag = "OffLineAIComponent";
	
	public static final String EnterWorld_GE_ID = EnterWorld_GE.class.getSimpleName();//玩家上线事件
	public static final String LeaveWorld_GE_ID = LeaveWorld_GE.class.getSimpleName();//玩家离线事件
	
	
	@Override
	public void ready() {
		//添加监听网络事件 
		addActionEventListener(OffLineAIEventDefines.C2G_ViewOffLineAIReward);
		addActionEventListener(OffLineAIEventDefines.C2G_DrawOffLineAIReward);
		addActionEventListener(OffLineAIEventDefines.C2G_OffLineAISeting);
		//添加监听内部事件
//		addInterGameEventListener(EnterWorld_GE_ID);
//		addInterGameEventListener(LeaveWorld_GE_ID);
	}

	@Override
	public void suspend() {
		removeActionEventListener(OffLineAIEventDefines.C2G_ViewOffLineAIReward);
		removeActionEventListener(OffLineAIEventDefines.C2G_DrawOffLineAIReward);
		removeActionEventListener(OffLineAIEventDefines.C2G_OffLineAISeting);
		
//		removeInterGameEventListener(EnterWorld_GE_ID);
//		removeInterGameEventListener(LeaveWorld_GE_ID);
	}

	@Override
	public void handleGameEvent(GameEvent<?> event) {
		try {
			if (event.isId(EnterWorld_GE_ID)) {
				enterWorld_GE(event);
			} else if (event.isId(LeaveWorld_GE_ID)) {
				leaveWorld_GE(event);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}
	@Override
	public void handleActionEvent(ActionEventBase actionEvent) {
		try {
			switch (actionEvent.getActionEventId()) {
			case OffLineAIEventDefines.C2G_ViewOffLineAIReward:
				C2G_ViewOffLineAIReward((C2G_ViewOffLineAIReward) actionEvent);
				break;
			case OffLineAIEventDefines.C2G_DrawOffLineAIReward:
				C2G_DrawOffLineAIReward((C2G_DrawOffLineAIReward) actionEvent);
				break;
			case OffLineAIEventDefines.C2G_OffLineAISeting:
				C2G_OffLineAISeting((C2G_OffLineAISeting) actionEvent);
				break;
			default:
				break;
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}
	
	//玩家上线
	private void enterWorld_GE(GameEvent<?> event) {
		if (logger.isDebugEnabled()) {
			logger.debug("OffLineAIComponent enterWorld_GE");
		}
		Player player = this.getConcreteParent();
		PlayerAvatar playerAvatar = PlayerAvatarMgr.removePlayerAvatar(player.getId());
		if(playerAvatar != null){
			PlayerAvatarMgr.leaveWorld(playerAvatar);
		}
		OffLineAIDAOMgr.getAndLoadPlayerAvatarData(player.getId());
	}
	//玩家下线
	private void leaveWorld_GE(GameEvent<?> event) {
		if (logger.isDebugEnabled()) {
			logger.debug("OffLineAIComponent leaveWorld_GE");
		}
		
		Player player = this.getConcreteParent();
		//TODO： 测试 正式的时候需要打开
//		PlayerAvatarData playerAvatarData = OffLineAIDAOMgr.getAndLoadPlayerAvatarData(player.getId());
//		if(!playerAvatarData.isHaveItemByItemRefId(PlayerAvatarMgr.MAGICBLOODSTONE_ITEMREFID, 1)){
//			OffLineAIDAOMgr.removePlayerAvatarData(playerAvatarData);
//			return;
//		}
		GameScene searchGameScene = PlayerAvatarMgr.searchGameScene(player);
		if(searchGameScene == null){
			return;
		}
		PlayerAvatar playerAvatar = GameObjectFactory.get(PlayerAvatar.class,player,searchGameScene);
		if(playerAvatar != null){
			PlayerAvatarMgr.putPlayerAvatar(playerAvatar);
		}
	}
	
	//查看离线背包奖励
	private void C2G_ViewOffLineAIReward(C2G_ViewOffLineAIReward actionEvent) {
		if (logger.isDebugEnabled()) {
			logger.debug("C2G_ViewOffLineAIReward");
		}
		Player player = this.getConcreteParent();
		G2C_ViewOffLineAIReward res = MessageFactory.getConcreteMessage(OffLineAIEventDefines.G2C_ViewOffLineAIReward);
		PlayerAvatarData playerAvatarData = OffLineAIDAOMgr.getAndLoadPlayerAvatarData(player.getId());
		if(playerAvatarData == null || res == null){
			return;
		}
		res.setPlayerAvatarData(playerAvatarData);
		GameRoot.sendMessage(player.getIdentity(),res);
	}
	//领取离线背包奖励
	private void C2G_DrawOffLineAIReward(C2G_DrawOffLineAIReward actionEvent) {
		if (logger.isDebugEnabled()) {
			logger.debug("C2G_DrawOffLineAIReward");
		}
		Player player = this.getConcreteParent();
		try {
			PlayerAvatarData playerAvatarData = OffLineAIDAOMgr.getAndLoadPlayerAvatarData(player.getId());
			// 领取经验
			int expAndClear = playerAvatarData.getExpAndClear();
			try {
				player.getExpComponent().addExp(expAndClear);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				playerAvatarData.addExp(expAndClear);
			}
			// 领取金币
			int moneyAndClear = playerAvatarData.getMoneyAndClear();
			try {
				player.getPlayerMoneyComponent().addGold(moneyAndClear,ItemOptSource.Avatar);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				playerAvatarData.addMoney(moneyAndClear);
			}
			// 领取道具
			List<ItemPair> itemListAndClear = playerAvatarData.getItemListAndClear();
			PlayerAvatarMgr.putItemAndEmail(player, itemListAndClear);

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		G2C_DrawOffLineAIReward res = MessageFactory.getConcreteMessage(OffLineAIEventDefines.G2C_DrawOffLineAIReward);
		GameRoot.sendMessage(player.getIdentity(),res);
	}

	//离线挂机AI设置
	private void C2G_OffLineAISeting(C2G_OffLineAISeting actionEvent) {
		Player player = this.getConcreteParent();
		PlayerAvatarData playerAvatarData = OffLineAIDAOMgr.getAndLoadPlayerAvatarData(player.getId());
		OffLineAISeting offLineAISeting = playerAvatarData.getOffLineAISeting();
		offLineAISeting.clientOffLineAISeting(actionEvent.getHp(), actionEvent.getMp(), 
				actionEvent.getEquipLv(), actionEvent.getQualityList(), actionEvent.getProfessionIdList());
	}
	
	
}
