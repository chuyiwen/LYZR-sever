package newbee.morningGlory.mmorpg.player.property;

import newbee.morningGlory.code.MGErrorCode;
import newbee.morningGlory.mmorpg.player.activity.mining.MGPlayerMiningComponent;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import sophia.foundation.communication.core.ActionEventBase;
import sophia.mmorpg.base.ConcreteComponent;
import sophia.mmorpg.base.scene.mgr.PluckMgrComponent;
import sophia.mmorpg.code.CodeContext;
import sophia.mmorpg.code.MMORPGSuccessCode;
import sophia.mmorpg.event.ResultEvent;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.scene.event.C2G_Scene_StartoPluck;
import sophia.mmorpg.player.scene.event.SceneEventDefines;
import sophia.mmorpg.pluck.Pluck;

public class MGPlayerSceneComponent extends ConcreteComponent<Player> {
	private static Logger logger = Logger.getLogger(MGPlayerSceneComponent.class);

	public static final String Tag = "MGPlayerSceneComponentTag";

	@Override
	public void ready() {
		addActionEventListener(SceneEventDefines.C2G_Scene_StartoPluck);
		super.ready();
	}

	@Override
	public void suspend() {
		removeActionEventListener(SceneEventDefines.C2G_Scene_StartoPluck);
		super.suspend();
	}

	public void handleActionEvent(ActionEventBase event) {
		Player player = getConcreteParent();
		if (event.getActionEventId() == SceneEventDefines.C2G_Scene_StartoPluck) {
			// 清楚之前的采集
			player.getPlayerSceneComponent().interruptPluck();
			
			C2G_Scene_StartoPluck req = (C2G_Scene_StartoPluck) event;
			String charId = req.getCharId();
			PluckMgrComponent pluckMgrComponent = player.getCrtScene().getPluckMgrComponent();
			Pluck pluck = pluckMgrComponent.getPluck(charId);
			if (null == pluck) {
				if (logger.isDebugEnabled()) {
					logger.debug("采集物不存在， player=" + player);
				}
				return;
			}

			if (!canPluckMine()) {
				ResultEvent.sendResult(player.getIdentity(), event.getActionEventId(), MGErrorCode.CODE_MINING_COUNT_UP);
				if (logger.isDebugEnabled()) {
					logger.debug("挖矿失败, code=" + CodeContext.description(MGErrorCode.CODE_MINING_COUNT_UP) + ", player=" + player);
				}
				return;
			}
			
			int returnCode = pluck.beginPluck(player);
			if (MMORPGSuccessCode.CODE_SUCCESS == returnCode) {// 可以采集
				if (logger.isDebugEnabled()) {
					logger.debug("开始采集, pluck=" + pluck + ", player=" + player);
				}
			} else {
				ResultEvent.sendResult(player.getIdentity(), event.getActionEventId(), returnCode);
			}
		}
	}

	private boolean canPluckMine() {
		MGPlayerMiningComponent playerMiningComponent = (MGPlayerMiningComponent) getConcreteParent().getTagged(MGPlayerMiningComponent.Tag);
		String playerCrtSceneRefId = getConcreteParent().getCrtScene().getRef().getId();
		String miningSceneRefId = playerMiningComponent.getMiningActivity().getRef().getSceneRefId();
		if (!StringUtils.equals(playerCrtSceneRefId, miningSceneRefId)) {
			return true;
		}

		long lastMiningMills = playerMiningComponent.getPlayerMiningManager().getLastMiningMills();
		if(playerMiningComponent.getMiningActivity().isTheSameMiningRound(lastMiningMills, System.currentTimeMillis())) {
			int count = playerMiningComponent.getPlayerMiningManager().totalCollectedCount();
			if (count >= 20) {
				if (logger.isDebugEnabled()) {
					logger.debug("今天已经采矿 " + count + "次,已满");
				}
				return false;
			}
		}

		return true;
	}
	
}
