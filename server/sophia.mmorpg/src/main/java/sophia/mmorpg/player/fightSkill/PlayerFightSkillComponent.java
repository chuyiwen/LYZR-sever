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
package sophia.mmorpg.player.fightSkill;

import sophia.foundation.communication.core.ActionEventBase;
import sophia.foundation.data.PersistenceObject;
import sophia.game.component.communication.GameEvent;
import sophia.mmorpg.base.ConcreteComponent;
import sophia.mmorpg.base.scene.mgr.PluckMgrComponent;
import sophia.mmorpg.base.sprite.FightSprite;
import sophia.mmorpg.base.sprite.fightSkill.gameevent.AfterAttack_GE;
import sophia.mmorpg.base.sprite.state.action.PluckingState;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.ai.PlayerPerceiveComponent;
import sophia.mmorpg.player.exp.gameevent.PlayerLevelUp_GE;
import sophia.mmorpg.player.fightSkill.event.FightSkillDefines;
import sophia.mmorpg.player.fightSkill.gameevent.SkillLevelUp_GE;
import sophia.mmorpg.player.fightSkill.gameevent.SkillUseSkill_GE;
import sophia.mmorpg.pluck.Pluck;

public final class PlayerFightSkillComponent extends ConcreteComponent<Player> {
	private final static String SkillLevelUp_GE_Id = SkillLevelUp_GE.class.getSimpleName();
	private final static String SkillUseSkill_GE_Id = SkillUseSkill_GE.class.getSimpleName();
	private final static String PlayerLevelUp_GE_Id = PlayerLevelUp_GE.class.getSimpleName();
	private final static String AfterAttack_GE_Id = AfterAttack_GE.class.getSimpleName();
	private PlayerFightSkillTree playerFightSkillTree = null;
	private PersistenceObject persistenceObject;

	private PlayerFightSkillTreeActionEventDelegate actionEventDelegate = null;
	private PlayerFightSkillTreeGameEventDelegate gameEventDelegate = null;

	public PlayerFightSkillComponent() {
	};

	public PlayerFightSkillTreeActionEventDelegate getActionEventDelegate() {
		return actionEventDelegate;
	}

	public void setActionEventDelegate(PlayerFightSkillTreeActionEventDelegate actionEventDelegate) {
		this.actionEventDelegate = actionEventDelegate;
	}

	public PlayerFightSkillTreeGameEventDelegate getGameEventDelegate() {
		return gameEventDelegate;
	}

	public void setGameEventDelegate(PlayerFightSkillTreeGameEventDelegate gameEventDelegate) {
		this.gameEventDelegate = gameEventDelegate;
	}

	public PlayerFightSkillTree getPlayerFightSkillTree() {
		return playerFightSkillTree;
	}

	public void setPlayerFightSkillTree(PlayerFightSkillTree playerFightSkillTree) {
		this.playerFightSkillTree = playerFightSkillTree;
	}

	@Override
	public void ready() {
		super.ready();
		// action event
		addActionEventListener(FightSkillDefines.C2G_GetLearnedSkillList);
		addActionEventListener(FightSkillDefines.C2G_PutdownSkill);
		addActionEventListener(FightSkillDefines.C2G_AddSkillExp);

		// game event
		addInterGameEventListener(PlayerLevelUp_GE_Id);
		addInterGameEventListener(SkillLevelUp_GE_Id);
		addInterGameEventListener(SkillUseSkill_GE_Id);
		addInterGameEventListener(AfterAttack_GE_Id);
	}

	@Override
	public void suspend() {
		super.suspend();
		// action event
		removeActionEventListener(FightSkillDefines.C2G_GetLearnedSkillList);
		removeActionEventListener(FightSkillDefines.C2G_PutdownSkill);
		removeActionEventListener(FightSkillDefines.C2G_AddSkillExp);

		// game event
		removeInterGameEventListener(PlayerLevelUp_GE_Id);
		removeInterGameEventListener(SkillLevelUp_GE_Id);
		removeInterGameEventListener(SkillUseSkill_GE_Id);
		removeInterGameEventListener(AfterAttack_GE_Id);
	}

	@Override
	public void handleActionEvent(ActionEventBase event) {
		actionEventDelegate.handleActionEvent(event, this);
	}

	@Override
	public void handleGameEvent(GameEvent<?> event) {
		gameEventDelegate.handleGameEvent(event, this);
		if (event.isId(AfterAttack_GE_Id)) {
			AfterAttack_GE ge = (AfterAttack_GE) event.getData();
			FightSprite attacker = ge.getAttacker();
			FightSprite tagert = ge.getTarget();
			if (attacker.getFightSpriteStateMgr().isState(PluckingState.PluckingState_Id)) {
				Player player = (Player) attacker;
				PlayerPerceiveComponent perceiveComponent = (PlayerPerceiveComponent) player.getPerceiveComponent();
				perceiveComponent.cancelPluckingStateAndNotifySelf();
			}

			if (tagert.getFightSpriteStateMgr().isState(PluckingState.PluckingState_Id)) {
				Player player = (Player) tagert;
				PluckMgrComponent pluckMgrComponent = player.getCrtScene().getPluckMgrComponent();
				Pluck pluck = pluckMgrComponent.getPlucking(player);
				if (pluck != null && pluck.canInterrupteed()) {
					PlayerPerceiveComponent perceiveComponent = (PlayerPerceiveComponent) player.getPerceiveComponent();
					perceiveComponent.cancelPluckingStateAndNotifySelf();
				}
			}
		}
	}

	public PersistenceObject getPersistenceObject() {
		return persistenceObject;
	}

	public void setPersistenceObject(PersistenceObject persistenceObject) {
		this.persistenceObject = persistenceObject;
	}
}
