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
package sophia.mmorpg.player.ai;

import sophia.foundation.communication.core.MessageFactory;
import sophia.game.GameRoot;
import sophia.game.component.communication.GameEvent;
import sophia.mmorpg.base.sprite.ai.SpritePerceiveComponent;
import sophia.mmorpg.base.sprite.ai.gameEvent.FightSpriteInjured_GE;
import sophia.mmorpg.base.sprite.ai.gameEvent.FightSpriteOwnerInjured_GE;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.gameEvent.LeaveWorld_GE;
import sophia.mmorpg.player.gameEvent.PlayerDead_GE;
import sophia.mmorpg.player.scene.PlayerSceneComponent;
import sophia.mmorpg.player.scene.event.G2C_Scene_State_Change;
import sophia.mmorpg.player.scene.event.SceneEventDefines;

public class PlayerPerceiveComponent extends SpritePerceiveComponent<Player> {
	
	public static final String LeaveWorld_GE_Id = LeaveWorld_GE.class.getSimpleName();
	
	@Override
	public void ready() {
		addInterGameEventListener(FightSpriteInjured_GE_Id);
		addInterGameEventListener(FightSpriteAttack_GE_Id);
		addInterGameEventListener(Player.PlayerDead_GE_Id);
		addInterGameEventListener(LeaveWorld_GE_Id);
		addInterGameEventListener(PlayerSceneComponent.PlayerSwitchScene_GE_Id);
		addInterGameEventListener(PlayerSceneComponent.PlayerSameSceneJumpTo_GE_Id);
		super.ready();
	}

	@Override
	public void suspend() {
		removeInterGameEventListener(FightSpriteInjured_GE_Id);
		removeInterGameEventListener(FightSpriteAttack_GE_Id);
		removeInterGameEventListener(Player.PlayerDead_GE_Id);
		removeInterGameEventListener(LeaveWorld_GE_Id);
		removeInterGameEventListener(PlayerSceneComponent.PlayerSwitchScene_GE_Id);
		removeInterGameEventListener(PlayerSceneComponent.PlayerSameSceneJumpTo_GE_Id);
		super.suspend();
	}

	@Override
	public void handleGameEvent(GameEvent<?> event) {
		Player player = getConcreteParent();
		if (event.isId(FightSpriteInjured_GE_Id)) {
			FightSpriteInjured_GE ge = ((FightSpriteInjured_GE)event.getData());
			String spriteId = SpritePerceiveComponent.FightSpriteOwnerInjured_GE_Id;
			FightSpriteOwnerInjured_GE fightSpriteInjuredMessage_GE = new FightSpriteOwnerInjured_GE(ge.getAttacker());
			GameEvent<FightSpriteOwnerInjured_GE> gameEvent = (GameEvent<FightSpriteOwnerInjured_GE>) GameEvent.getInstance(spriteId, fightSpriteInjuredMessage_GE);
			sendGameEvent(gameEvent, getConcreteParent().getId());
		} else if (event.isId(FightSpriteAttack_GE_Id)) {
		} else if (event.isId(Player.PlayerDead_GE_Id)) {
			PlayerDead_GE playerDeadGE = (PlayerDead_GE) event.getData();
			// 自己死亡
			if (playerDeadGE.getPlayer().equals(player)) {
				player.getPlayerSceneComponent().interruptPluck();
			}
			
		} else if (event.isId(LeaveWorld_GE_Id)) {
			player.getPlayerSceneComponent().interruptPluck();
		} else if (event.isId(PlayerSceneComponent.PlayerSwitchScene_GE_Id) || event.isId(PlayerSceneComponent.PlayerSameSceneJumpTo_GE_Id)) {
			cancelPluckingStateAndNotifySelf();
		}
		
		super.handleGameEvent(event);
	}

	
	public void cancelPluckingStateAndNotifySelf() {
		Player player = getConcreteParent();
		player.getPlayerSceneComponent().interruptPluck();
		G2C_Scene_State_Change res = MessageFactory.getConcreteMessage(SceneEventDefines.G2C_Scene_State_Change);
		res.setAimType(player.getSpriteType());
		res.setCharId(player.getId());
		res.setStateList(player.getStateList());
		GameRoot.sendMessage(player.getIdentity(), res);
	}
}
