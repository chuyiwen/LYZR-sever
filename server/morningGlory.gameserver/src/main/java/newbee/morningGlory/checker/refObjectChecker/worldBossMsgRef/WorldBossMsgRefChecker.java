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
package newbee.morningGlory.checker.refObjectChecker.worldBossMsgRef;

import newbee.morningGlory.checker.BaseRefChecker;
import sophia.game.GameRoot;
import sophia.game.ref.GameRefObject;
import sophia.mmorpg.base.scene.ref.SceneRef;
import sophia.mmorpg.monster.ref.MonsterRef;
import sophia.mmorpg.player.worldBossMsg.WorldBossMsgRef;

public class WorldBossMsgRefChecker extends BaseRefChecker<WorldBossMsgRef> {

	@Override
	public String getDescription() {
		return "世界boss";
	}

	@Override
	public void check(GameRefObject gameRefObject) {
		WorldBossMsgRef ref = (WorldBossMsgRef)gameRefObject;
		String sceneRefId = ref.getGameSceneRefId();
		String monsterRefId = ref.getMonsterRefId();
		
		MonsterRef monsterRef = (MonsterRef)GameRoot.getGameRefObjectManager().getManagedObject(monsterRefId);
		if(monsterRef == null){
			error(gameRefObject, "世界boss 怪物不存在："+monsterRefId);
		}
		SceneRef sceneRef = (SceneRef)GameRoot.getGameRefObjectManager().getManagedObject(sceneRefId);
		if(sceneRef == null){
			error(gameRefObject, "世界boss 场景不存在："+sceneRefId);
		}
	}

}
