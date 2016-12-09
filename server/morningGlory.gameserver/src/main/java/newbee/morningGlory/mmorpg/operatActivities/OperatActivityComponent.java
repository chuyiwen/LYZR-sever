package newbee.morningGlory.mmorpg.operatActivities;

import newbee.morningGlory.mmorpg.operatActivities.event.OperatActivityDefines;

import org.apache.log4j.Logger;

import sophia.foundation.communication.core.ActionEventBase;
import sophia.foundation.property.PropertyDictionary;
import sophia.game.component.communication.GameEvent;
import sophia.mmorpg.base.ConcreteComponent;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.gameEvent.EnterWorld_SceneReady_GE;
import sophia.mmorpg.player.money.gameEvent.ConsumeUnBindedGold_GE;
import sophia.mmorpg.player.mount.PlayerMountComponent;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;

public class OperatActivityComponent extends ConcreteComponent<Player> {
	private static final Logger logger = Logger.getLogger(PlayerMountComponent.class);
	public static final String Tag = "OperatActivityComponent";
	private OperatActivityRecord operatActivityRecord = new OperatActivityRecord();
	private static final String EnterWorld_SceneReady_ID = EnterWorld_SceneReady_GE.class.getSimpleName();
	private static final String ConsumeUnBindedGold_GE_ID = ConsumeUnBindedGold_GE.class.getSimpleName();

	@Override
	public void ready() {
		addActionEventListener(OperatActivityDefines.C2G_OA_FirstRechargeGiftList);
		addActionEventListener(OperatActivityDefines.C2G_OA_FirstRechargeGiftReceive);
		addActionEventListener(OperatActivityDefines.C2G_OA_TotalRechargeGiftListEvent);
		addActionEventListener(OperatActivityDefines.C2G_OA_TotalRechargeGiftReceiveEvent);
		addActionEventListener(OperatActivityDefines.C2G_OA_EveryRechargeGiftListEvent);
		addActionEventListener(OperatActivityDefines.C2G_OA_EveryRechargeGiftReceiveEvent);
		addActionEventListener(OperatActivityDefines.C2G_OA_WeekTotalConsumeGiftListEvent);
		addActionEventListener(OperatActivityDefines.C2G_OA_WeekTotalConsumeGiftReceiveEvent);
		addActionEventListener(OperatActivityDefines.C2G_OA_SevenLogin_HadReceive);
		addActionEventListener(OperatActivityDefines.C2G_OA_SevenLogin_HaveReceive);
		addActionEventListener(OperatActivityDefines.C2G_OA_SevenLogin_ReReceive);
		addActionEventListener(OperatActivityDefines.C2G_OA_SevenLogin_ReceiveState);
		addActionEventListener(OperatActivityDefines.C2G_OA_CanReceiveEvent);
		addInterGameEventListener(EnterWorld_SceneReady_ID);
		addInterGameEventListener(ConsumeUnBindedGold_GE_ID);
		operatActivityRecord.init(getConcreteParent());
	}

	@Override
	public void suspend() {
		removeActionEventListener(OperatActivityDefines.C2G_OA_FirstRechargeGiftList);
		removeActionEventListener(OperatActivityDefines.C2G_OA_FirstRechargeGiftReceive);
		removeActionEventListener(OperatActivityDefines.C2G_OA_TotalRechargeGiftListEvent);
		removeActionEventListener(OperatActivityDefines.C2G_OA_TotalRechargeGiftReceiveEvent);
		removeActionEventListener(OperatActivityDefines.C2G_OA_EveryRechargeGiftListEvent);
		removeActionEventListener(OperatActivityDefines.C2G_OA_EveryRechargeGiftReceiveEvent);
		removeActionEventListener(OperatActivityDefines.C2G_OA_WeekTotalConsumeGiftListEvent);
		removeActionEventListener(OperatActivityDefines.C2G_OA_WeekTotalConsumeGiftReceiveEvent);
		removeActionEventListener(OperatActivityDefines.C2G_OA_SevenLogin_HadReceive);
		removeActionEventListener(OperatActivityDefines.C2G_OA_SevenLogin_HaveReceive);
		removeActionEventListener(OperatActivityDefines.C2G_OA_SevenLogin_ReReceive);
		removeActionEventListener(OperatActivityDefines.C2G_OA_SevenLogin_ReceiveState);
		removeActionEventListener(OperatActivityDefines.C2G_OA_CanReceiveEvent);
		removeInterGameEventListener(EnterWorld_SceneReady_ID);
		removeInterGameEventListener(ConsumeUnBindedGold_GE_ID);
	}

	@Override
	public void handleActionEvent(ActionEventBase event) {
		if (logger.isDebugEnabled()) {
			logger.debug("enter OperatActivityComponent");
		}
		OperatActivityMgr.getInstance().handleEvent(event);
	}

	@Override
	public void handleGameEvent(GameEvent<?> event) {
		if (event.isId(EnterWorld_SceneReady_ID)) {
			Player player = getConcreteParent();
			onPreLogin(player);
		} else if (event.isId(ConsumeUnBindedGold_GE_ID)) {
			ConsumeUnBindedGold_GE consumeUnBindedGold_GE = (ConsumeUnBindedGold_GE) event.getData();
			int unBindedGold = consumeUnBindedGold_GE.getUnBindedGold();
			int bindedGold = consumeUnBindedGold_GE.getBindedGold();
			byte source = consumeUnBindedGold_GE.getSource();
			modifyConsumeValue(getConcreteParent(), unBindedGold, bindedGold,source);
		}

	}

	/** modify 累计消费 */
	public void modifyConsumeValue(Player player, int unBindedGold, int bindedGold,byte source) {
		// 周累计消费
		OperatActivity operatActivity = OperatActivityMgr.simulateMap.get(OperatActivityType.WeekTotalConsumeGift);
		if(ConsumeUnBindedGold_GE.isConsume(source)){
			operatActivity.modify(player, unBindedGold);
		}
		PropertyDictionary pd = player.getProperty();
		
		int totalConsumeBindedGold = MGPropertyAccesser.getTotalConsumeBindedGold(pd) + bindedGold;
		int totalConsumeUnBinededGold = MGPropertyAccesser.getTotalConsumeUnBindedGold(pd) + unBindedGold;
		
		totalConsumeBindedGold = totalConsumeBindedGold < 0 ? bindedGold : totalConsumeBindedGold ;
		totalConsumeUnBinededGold = totalConsumeUnBinededGold < 0 ? unBindedGold : totalConsumeUnBinededGold;
		
		MGPropertyAccesser.setOrPutTotalConsumeBindedGold(player.getProperty(), totalConsumeBindedGold);
		MGPropertyAccesser.setOrPutTotalConsumeUnBindedGold(player.getProperty(), totalConsumeUnBinededGold);
	}

	public OperatActivityRecord getOperatActivityRecord() {
		return operatActivityRecord;
	}

	public void setOperatActivityRecord(OperatActivityRecord operatActivityRecord) {
		this.operatActivityRecord = operatActivityRecord;
	}

	public static void onPreLogin(Player player) {
		OperatActivityMgr.getInstance().onPlayerPreLogin(player);
	}

}
