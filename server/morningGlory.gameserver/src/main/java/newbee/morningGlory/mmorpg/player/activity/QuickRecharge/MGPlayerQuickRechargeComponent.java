/**
 * 
 */
package newbee.morningGlory.mmorpg.player.activity.QuickRecharge;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import newbee.morningGlory.mmorpg.player.activity.QuickRecharge.event.G2C_QuickRecharge_List;
import newbee.morningGlory.mmorpg.player.activity.QuickRecharge.event.QuickRechargeDefines;
import sophia.foundation.communication.core.ActionEventBase;
import sophia.foundation.communication.core.MessageFactory;
import sophia.game.GameRoot;
import sophia.mmorpg.base.ConcreteComponent;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.itemBag.ItemOptSource;

/**
 * @author yinxinglin 2014-6-18
 * 
 */
public class MGPlayerQuickRechargeComponent extends ConcreteComponent<Player> {

	public static final String Tag = "MGPlayerQuickRechargeComponent";

	private QuickRechargeMgr quickRechargeMgr = new QuickRechargeMgr();

	@Override
	public void ready() {
		addActionEventListener(QuickRechargeDefines.C2G_QuickRecharge_List);
	}

	@Override
	public void suspend() {
		removeActionEventListener(QuickRechargeDefines.C2G_QuickRecharge_List);
	}

	@Override
	public void handleActionEvent(ActionEventBase event) {
		short actionEventId = event.getActionEventId();
		switch (actionEventId) {
		case QuickRechargeDefines.C2G_QuickRecharge_List:
			handle_QuickRecharge_List(event);
			break;
		default:
			return;
		}
	}

	private void handle_QuickRecharge_List(ActionEventBase event) {
		Set<String> noFirstRecharges = new HashSet<>(QuickRechargeMgr.quickRefIds.size());
		for (String quickRefId : QuickRechargeMgr.quickRefIds) {
			if (quickRechargeMgr.isNeverRecharge(quickRefId)) {
				QuickRechargeRef ref = (QuickRechargeRef) GameRoot.getGameRefObjectManager().getManagedObject(quickRefId);
				if (ref.getFirstReward() > 0 || ref.getFirstRewardBound() > 0) {
					noFirstRecharges.add(quickRefId);
				}
			}
		}

		G2C_QuickRecharge_List res = MessageFactory.getConcreteMessage(QuickRechargeDefines.G2C_QuickRecharge_List);
		res.setQuickRefSet(noFirstRecharges);
		GameRoot.sendMessage(getConcreteParent().getIdentity(), res);

	}

	public int getQuickRechargeReward(int unbindedGold) {

		QuickRechargeRef ref = getRef(unbindedGold);

		if (ref == null) {
			return 0;
		}

		if (ref.getUnbindedGold() != unbindedGold) {
			return 0;
		}

		int reward = 0;
		int rewardBound = 0;

		if (quickRechargeMgr.isNeverRecharge(ref.getId())) {
			if (ref.getFirstReward() > 0) {
				reward = ref.getFirstReward();
			} else {
				reward = ref.getReward();
			}
			if (ref.getFirstRewardBound() > 0) {
				rewardBound = ref.getFirstRewardBound();
			} else {
				rewardBound = ref.getRewardBound();
			}
			quickRechargeMgr.add(ref.getId());
		} else {
			reward = ref.getReward();
			rewardBound = ref.getRewardBound();
		}

		Player player = getConcreteParent();

		if (rewardBound > 0) {
			player.getPlayerMoneyComponent().addBindGold(rewardBound, ItemOptSource.Recharge);
		}

		if (reward < 0) {
			return 0;
		}

		handle_QuickRecharge_List(null);

		return reward;

	}

	private QuickRechargeRef getRef(int unbindedGold) {
		QuickRechargeRef ref = null;
		for (String quickRefId : QuickRechargeMgr.quickRefIds) {
			ref = (QuickRechargeRef) GameRoot.getGameRefObjectManager().getManagedObject(quickRefId);
			if (ref.getUnbindedGold() == unbindedGold) {
				break;
			}
		}
		return ref;
	}

	public boolean isValid(String refId, int unbindedGold) {
		if (StringUtils.isEmpty(refId)) {
			return true;
		}
		QuickRechargeRef ref = (QuickRechargeRef) GameRoot.getGameRefObjectManager().getManagedObject(refId);
		if (ref == null) {
			return false;
		}
		if (unbindedGold != ref.getUnbindedGold()) {
			return false;
		}

		return true;
	}

	public QuickRechargeMgr getQuickRechargeMgr() {
		return quickRechargeMgr;
	}

	public void setQuickRechargeMgr(QuickRechargeMgr quickRechargeMgr) {
		this.quickRechargeMgr = quickRechargeMgr;
	}

}
