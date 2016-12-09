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
package sophia.mmorpg.player.worldBossMsg;

import org.apache.commons.lang3.StringUtils;

import sophia.foundation.authentication.Identity;
import sophia.foundation.communication.core.ActionEventBase;
import sophia.foundation.communication.core.MessageFactory;
import sophia.game.GameRoot;
import sophia.game.component.communication.GameEvent;
import sophia.mmorpg.base.ConcreteComponent;
import sophia.mmorpg.base.scene.GameScene;
import sophia.mmorpg.base.sprite.FightSprite;
import sophia.mmorpg.monster.Monster;
import sophia.mmorpg.monster.gameEvent.MonsterDead_GE;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.chat.sysytem.SystemPromptFacade;
import sophia.mmorpg.player.worldBossMsg.event.C2G_Boss_List;
import sophia.mmorpg.player.worldBossMsg.event.G2C_Boss_List;
import sophia.mmorpg.player.worldBossMsg.event.WorldBossDefines;

public class PlayerWorldBossMsgComponent extends ConcreteComponent<Player> {

	public static final String Tag = "MGPlayerWorldBossMsgComponent";

	@Override
	public void ready() {
		addActionEventListener(WorldBossDefines.C2G_Boss_List);
		addInterGameEventListener(Monster.MonsterDead_GE_Id);
	}

	@Override
	public void suspend() {
		removeActionEventListener(WorldBossDefines.C2G_Boss_List);
		removeInterGameEventListener(Monster.MonsterDead_GE_Id);
	}

	private void updateWorldBossNextReviveTime(Monster monster) {
		GameScene crtScene = monster.getCrtScene();
		if (crtScene == null) {
			return;
		}
		if (monster.getMonsterRefreshType() == 0) {
			return;
		}
		String timingRefresh = monster.getTimingRefresh();
		if (!StringUtils.isNotEmpty(timingRefresh)) {
			return;
		}

		long nextRefreshTime = WorldBoss.getNextRefreshTime(timingRefresh, monster.getMonsterRef().getId(), crtScene.getRef().getId());

		if (!monster.isDead()) {
			nextRefreshTime = 0;
		}
		
		String monsterRefId = monster.getMonsterRef().getId();
		WorldBoss.replaceMonsterRefId(monsterRefId, nextRefreshTime, crtScene.getRef().getId());
		WorldBoss.sendBossList();
	}

	@Override
	public void handleGameEvent(GameEvent<?> event) {
		if (event.isId(Monster.MonsterDead_GE_Id)) {
			MonsterDead_GE ge = (MonsterDead_GE) event.getData();
			Monster monster = ge.getMonster();
			FightSprite attacker = ge.getAttacker();
			if (attacker instanceof Monster) {
				Monster baobao = (Monster) attacker;
				if (!baobao.getMonsterRef().isRegularMonster() && baobao.getOwner() != null) {
					attacker = baobao.getOwner();
				}
			}
			GameScene crtScene = monster.getCrtScene();
			if (WorldBoss.isContainMonster(monster.getMonsterRef().getId(), crtScene.getRef().getId())) {
				updateWorldBossNextReviveTime(monster);
			}
			if (StringUtils.equals(monster.getMonsterRef().getId(), "monster_72") || StringUtils.equals(monster.getMonsterRef().getId(), "monster_73")) {
				Player player = (Player) attacker;
				GameScene gameScene = player.getCrtScene();
				SystemPromptFacade.broadWorldThiefDead(gameScene);
			}
		}
	}

	@Override
	public void handleActionEvent(ActionEventBase event) {
		short actionEventId = event.getActionEventId();
		Identity identity = event.getIdentity();
		switch (actionEventId) {
		case WorldBossDefines.C2G_Boss_List:
			handle_Boss_List((C2G_Boss_List) event, actionEventId, identity);
			break;
		default:
			break;
		}
	}

	private void handle_Boss_List(C2G_Boss_List event, int actionEventId, Identity identity) {
		G2C_Boss_List res = MessageFactory.getConcreteMessage(WorldBossDefines.G2C_Boss_List);
		GameRoot.sendMessage(identity, res);
	}

}
