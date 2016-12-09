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
package newbee.morningGlory.mmorpg.player.fightSkill;

import org.apache.log4j.Logger;

import sophia.foundation.communication.core.ActionEventBase;
import sophia.game.GameRoot;
import sophia.mmorpg.base.sprite.fightSkill.FightSkill;
import sophia.mmorpg.code.MMORPGErrorCode;
import sophia.mmorpg.code.MMORPGSuccessCode;
import sophia.mmorpg.event.ResultEvent;
import sophia.mmorpg.item.ref.ItemRef;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.fightSkill.PlayerFightSkillComponent;
import sophia.mmorpg.player.fightSkill.PlayerFightSkillTree;
import sophia.mmorpg.player.fightSkill.PlayerFightSkillTreeActionEventDelegate;
import sophia.mmorpg.player.fightSkill.event.C2G_AddSkillExp;
import sophia.mmorpg.player.fightSkill.event.C2G_GetLearnedSkillList;
import sophia.mmorpg.player.fightSkill.event.C2G_PutdownSkill;
import sophia.mmorpg.player.fightSkill.event.FightSkillDefines;
import sophia.mmorpg.player.fightSkill.event.G2C_AddSkillExp;
import sophia.mmorpg.player.fightSkill.event.G2C_GetLearnedSkillList;
import sophia.mmorpg.player.itemBag.ItemFacade;
import sophia.mmorpg.player.itemBag.ItemOptSource;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;

public final class MGPlayerFightSkillTreeActionEventDelegate implements PlayerFightSkillTreeActionEventDelegate {
	private final static Logger logger = Logger.getLogger(MGPlayerFightSkillTreeActionEventDelegate.class);

	public MGPlayerFightSkillTreeActionEventDelegate() {

	}

	@Override
	public void handleActionEvent(ActionEventBase event, PlayerFightSkillComponent owner) {
		short eventId = event.getActionEventId();
		switch (eventId) {
		case FightSkillDefines.C2G_GetLearnedSkillList:
			handle_C2G_GetLearnedSkillList((C2G_GetLearnedSkillList) event, owner);
			break;
		case FightSkillDefines.C2G_PutdownSkill:
			handle_C2G_PutdownSkill((C2G_PutdownSkill) event, owner);
			break;
		case FightSkillDefines.C2G_AddSkillExp:
			handle_C2G_AddSkillExp((C2G_AddSkillExp) event, owner);
		default:
			break;
		}
	}

	private void handle_C2G_AddSkillExp(C2G_AddSkillExp event, PlayerFightSkillComponent owner) {
		PlayerFightSkillTree playerFightSkillTree = owner.getPlayerFightSkillTree();
		Player player = playerFightSkillTree.getPlayer();
		if (!playerFightSkillTree.isLearned(event.getSkillRefId())) {
			return;
		}

		String itemRefId = "item_jinengExp";
		ItemRef itemRef = (ItemRef) GameRoot.getGameRefObjectManager().getManagedObject(itemRefId);
		int skillExpToAdd = MGPropertyAccesser.getSkillExp(itemRef.getEffectProperty());
		FightSkill skill = playerFightSkillTree.getFightSkill(event.getSkillRefId());
		if (skill == null) {
			ResultEvent.sendResult(player.getIdentity(), event.getActionEventId(), MMORPGErrorCode.CODE_SKILL_NOT_LEARNED);
			return;
		}
		if (ItemFacade.getNumber(player, itemRefId) < 1) {
			ResultEvent.sendResult(player.getIdentity(), event.getActionEventId(), MMORPGErrorCode.CODE_ITEM_NOENOUGH);
			return;
		}

		ItemFacade.removeItem(player, itemRefId, 1, true,ItemOptSource.Skill);
		playerFightSkillTree.addExp(skill.getRefId(), skillExpToAdd);
		G2C_AddSkillExp response = new G2C_AddSkillExp(skill.getRefId(), skill.getExp(), skill.getLevel());
		GameRoot.sendMessage(player.getIdentity(), response);
	}

	private void handle_C2G_GetLearnedSkillList(C2G_GetLearnedSkillList message, PlayerFightSkillComponent owner) {
		PlayerFightSkillTree playerFightSkillTree = owner.getPlayerFightSkillTree();
		G2C_GetLearnedSkillList response = new G2C_GetLearnedSkillList(playerFightSkillTree);
		if (logger.isDebugEnabled()) {
			logger.debug("handle_C2G_GetLearnedSkillList: " + response);
		}
		GameRoot.sendMessage(message.getIdentity(), response);
	}

	private void handle_C2G_PutdownSkill(C2G_PutdownSkill message, PlayerFightSkillComponent owner) {
		String skillRefId = message.getSkillRefId();
		short slotIndex = message.getSlotIndex();
		PlayerFightSkillTree playerFightSkillTree = owner.getConcreteParent().getPlayerFightSkillComponent().getPlayerFightSkillTree();
		if (!playerFightSkillTree.isLearned(skillRefId)) {
			ResultEvent.sendResult(owner.getConcreteParent().getIdentity(), message.getActionEventId(), MMORPGErrorCode.CODE_SKILL_NOT_LEARNED);
			return;
		}

		playerFightSkillTree.setShortcutSkill(slotIndex, skillRefId);
		if (logger.isDebugEnabled()) {
			logger.debug("handle_C2G_PutdownSkill slotIndex: " + slotIndex + " skillRefId" + skillRefId);
		}

		ResultEvent.sendResult(owner.getConcreteParent().getIdentity(), message.getActionEventId(), MMORPGSuccessCode.CODE_SUCCESS);
	}
}
