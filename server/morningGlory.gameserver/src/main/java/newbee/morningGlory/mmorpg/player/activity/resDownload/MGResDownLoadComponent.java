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
package newbee.morningGlory.mmorpg.player.activity.resDownload;

import java.util.ArrayList;
import java.util.List;

import newbee.morningGlory.mmorpg.player.activity.resDownload.event.C2G_GetReward;
import newbee.morningGlory.mmorpg.player.activity.resDownload.event.G2C_CanGetReward;
import newbee.morningGlory.mmorpg.player.activity.resDownload.event.ResDownloadDefines;
import newbee.morningGlory.mmorpg.player.activity.resDownload.persistence.ResDownLoadDAO;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import sophia.foundation.communication.core.ActionEventBase;
import sophia.foundation.communication.core.MessageFactory;
import sophia.game.GameRoot;
import sophia.game.component.communication.GameEvent;
import sophia.mmorpg.base.ConcreteComponent;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.PlayerManager;
import sophia.mmorpg.player.itemBag.ItemFacade;
import sophia.mmorpg.player.itemBag.ItemOptSource;
import sophia.mmorpg.player.itemBag.ItemPair;
import sophia.mmorpg.player.scene.PlayerSceneComponent;
import sophia.mmorpg.utils.RuntimeResult;

public class MGResDownLoadComponent extends ConcreteComponent<Player> {

	private static final Logger logger = Logger.getLogger(MGResDownLoadComponent.class);

	public static final String Tag = "MGResDownLoadComponent";

	private MGResDownLoadMgr resDownLoadMgr = new MGResDownLoadMgr();

	public MGResDownLoadMgr getResDownLoadMgr() {
		return resDownLoadMgr;
	}

	public void setResDownLoadMgr(MGResDownLoadMgr resDownLoadMgr) {
		this.resDownLoadMgr = resDownLoadMgr;
	}

	public MGResDownLoadComponent() {

	}

	@Override
	public void ready() {
		addActionEventListener(ResDownloadDefines.C2G_GetReward);
		addActionEventListener(ResDownloadDefines.C2G_CanGetReward);
		addInterGameEventListener(PlayerManager.EnterWorld_GE_Id);
		addInterGameEventListener(PlayerSceneComponent.LeaveWorld_GE_Id);
	}

	@Override
	public void suspend() {
		removeActionEventListener(ResDownloadDefines.C2G_GetReward);
		removeActionEventListener(ResDownloadDefines.C2G_CanGetReward);
		removeInterGameEventListener(PlayerManager.EnterWorld_GE_Id);
		removeInterGameEventListener(PlayerSceneComponent.LeaveWorld_GE_Id);
	}

	@Override
	public void handleGameEvent(GameEvent<?> event) {
		if (event.isId(PlayerManager.EnterWorld_GE_Id)) {
			if (!ResDownLoadDAO.getInstance().selectData(getConcreteParent())) {
				ResDownLoadDAO.getInstance().insertData(getConcreteParent());

			}
		}
	}

	@Override
	public void handleActionEvent(ActionEventBase event) {
		short actionEventId = event.getActionEventId();
		switch (actionEventId) {
		case ResDownloadDefines.C2G_GetReward:
			handle_GetReward(event);
			break;
		case ResDownloadDefines.C2G_CanGetReward:
			handle_CanGetReward(event);
			break;
		default:
			break;
		}
	}

	private void handle_CanGetReward(ActionEventBase event) {
		List<String> canRewards = new ArrayList<String>(10);
		for (String rewardId : MGResDownLoadMgr.getRewardids()) {
			if (isCanReward(rewardId)) {
				canRewards.add(rewardId);
			}
		}
		sendCanRewardEvent(canRewards);
	}

	private void handle_GetReward(ActionEventBase event) {
		C2G_GetReward message = (C2G_GetReward) event;
		String rewardId = message.getRewardId();

		if (getReward(rewardId)) {
			MGResDownLoadData resDownLoadData = resDownLoadMgr.getResDownLoadData(rewardId);
			if (resDownLoadData == null) {
				resDownLoadData = new MGResDownLoadData();
				resDownLoadMgr.addResDownLoadData(resDownLoadData);
			}
			resDownLoadData.setRewardId(rewardId);
			resDownLoadData.setIsResDownloadReceive(MGResDownLoadData.HasReceive);
			resDownLoadData.setIdentityName(getConcreteParent().getIdentity().getName());
			resDownLoadData.setRewardPlayerName(getConcreteParent().getName());
			ResDownLoadDAO.getInstance().updateData(getConcreteParent());
		}

	}

	private boolean getReward(String rewardId) {
		MGResDownLoadDataRef ref = (MGResDownLoadDataRef) GameRoot.getGameRefObjectManager().getManagedObject(rewardId);

		if (!isCanReward(rewardId)) {
			return false;
		}

		List<ItemPair> reward = ref.getReward();
		RuntimeResult result = ItemFacade.addItem(getConcreteParent(), reward, ItemOptSource.resDownLoad);

		return result.isOK();
	}

	private boolean isCanReward(String rewardId) {
		if (StringUtils.isEmpty(rewardId)) {
			if (logger.isDebugEnabled()) {
				logger.debug("分包下载奖励 Id 为 空:" + rewardId);
			}
			return false;
		}
		MGResDownLoadData resDownLoadData = resDownLoadMgr.getResDownLoadData(rewardId);
		MGResDownLoadDataRef ref = (MGResDownLoadDataRef) GameRoot.getGameRefObjectManager().getManagedObject(rewardId);
		if (ref == null) {
			if (logger.isDebugEnabled()) {
				logger.debug("不存在分包下载奖励:" + rewardId);
			}
			return false;
		}
		if (resDownLoadData == null) {
			return true;
		}
		if (resDownLoadData.getIsResDownloadReceive() == MGResDownLoadData.NotReceive) {
			return true;
		}

		return false;
	}

	private void sendCanRewardEvent(List<String> canRewards) {
		G2C_CanGetReward res = MessageFactory.getConcreteMessage(ResDownloadDefines.G2C_CanGetReward);
		res.setCanRewards(canRewards);
		GameRoot.sendMessage(getConcreteParent().getIdentity(), res);
	}

}
