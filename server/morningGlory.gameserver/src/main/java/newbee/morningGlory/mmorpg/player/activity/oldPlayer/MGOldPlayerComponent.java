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
package newbee.morningGlory.mmorpg.player.activity.oldPlayer;

import java.util.ArrayList;
import java.util.List;

import newbee.morningGlory.mmorpg.player.activity.oldPlayer.persistence.OldPlayerDAO;

import org.apache.log4j.Logger;

import sophia.foundation.communication.core.ActionEventBase;
import sophia.game.GameRoot;
import sophia.game.component.communication.GameEvent;
import sophia.mmorpg.Mail.Mail;
import sophia.mmorpg.Mail.MailMgr;
import sophia.mmorpg.auth.AuthIdentity;
import sophia.mmorpg.base.ConcreteComponent;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.gameEvent.EnterWorld_SceneReady_GE;
import sophia.mmorpg.player.itemBag.ItemPair;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;

import com.google.gson.Gson;

/**
 * 已停用
 * @author Administrator
 *
 */
public class MGOldPlayerComponent extends ConcreteComponent<Player> {
	private static final Logger logger = Logger.getLogger(MGOldPlayerComponent.class);
	public static final String Tag = "MGOldPlayerComponent";
	private static final String OldPlayerKey = "oldUser_1";
	public static final String EnterWorld_SceneReady_GE_Id = EnterWorld_SceneReady_GE.class.getSimpleName();
	private static final int QdCode = 8;  // uc渠道
	@Override
	public void ready() {
		addInterGameEventListener(EnterWorld_SceneReady_GE_Id);
	}

	@Override
	public void suspend() {
		removeInterGameEventListener(EnterWorld_SceneReady_GE_Id);
	}

	@Override
	public void handleGameEvent(GameEvent<?> event) {
//		if (event.isId(EnterWorld_SceneReady_GE_Id)) {
//			oldPlayerReward();
//		}
	}

	@Override
	public void handleActionEvent(ActionEventBase event) {

	}

	private void oldPlayerReward() {
		Player player = getConcreteParent();
		String identityId = player.getIdentity().getId();

		MGOldPlayerDataRef ref = (MGOldPlayerDataRef) GameRoot.getGameRefObjectManager().getManagedObject(OldPlayerKey);
		if (ref == null) {
			return;
		}
		
		if (!timeValid(ref)) {
			return;
		}
		
		AuthIdentity authIdentity = (AuthIdentity) player.getIdentity();
		int qdCode = authIdentity.getQdCode1();
		
		if(qdCode != QdCode){
			return;
		}		

		if (MGOldPlayerData.isContain(identityId)) {
			return;
		}
		int bindedGold = 0;
		int gold = 0;
		List<ItemPair> rewards = new ArrayList<ItemPair>();
		for (ItemPair itemPair : ref.getRewards()) {
			if (itemPair.isBindedGold()) {
				bindedGold = itemPair.getNumber();
			} else if (itemPair.isGold()) {
				gold = itemPair.getNumber();
			} else {
				rewards.add(itemPair);
			}
		}

		String json = new Gson().toJson(rewards);
		// 发送邮件礼包
		String contents = MGPropertyAccesser.getDescription(ref.getProperty());
		MailMgr.sendMailById(player.getId(), contents, Mail.huodong, json, gold, bindedGold, 0);
		if (MGOldPlayerData.addOldPlayer(identityId)) {
			OldPlayerDAO.getInstance().saveImmediateData(identityId);
		}

	}

	private boolean timeValid(MGOldPlayerDataRef ref) {
		long now = System.currentTimeMillis();
		return now >= ref.getOpenTime() && now <= ref.getExpiredTime();
	}

}
