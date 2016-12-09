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
package newbee.morningGlory.checker.refObjectChecker.UnionGameInstanceChecker;

import newbee.morningGlory.checker.BaseRefChecker;
import newbee.morningGlory.mmorpg.player.unionGameInstance.UnionGameInstanceRef;
import sophia.game.GameRoot;
import sophia.game.ref.GameRefObject;
import sophia.mmorpg.base.scene.ref.SceneRef;
import sophia.mmorpg.item.ref.ItemRef;
import sophia.mmorpg.monster.ref.MonsterRef;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;

public class UnionGameInstanceChecker extends BaseRefChecker<UnionGameInstanceRef> {

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return "公会副本";
	}

	@Override
	public void check(GameRefObject gameRefObject) {
		UnionGameInstanceRef ref = (UnionGameInstanceRef) gameRefObject;
		String giftBag = ref.getGiftBag();
		GameRefObject managedObject = GameRoot.getGameRefObjectManager().getManagedObject(giftBag);
		if (managedObject == null || !(managedObject instanceof ItemRef)) {
			error(gameRefObject, "公会副本礼包不存在:" + giftBag);
		}

		String bossId = ref.getBossId();
		GameRefObject monster = GameRoot.getGameRefObjectManager().getManagedObject(bossId);
		if (monster == null || !(monster instanceof MonsterRef)) {
			error(gameRefObject, "公会副本Boss不存在:" + bossId);
		}
		String sceneRefId = MGPropertyAccesser.getSceneRefId(ref.getProperty());

		GameRefObject sceneRef = GameRoot.getGameRefObjectManager().getManagedObject(sceneRefId);
		if (sceneRef == null || !(sceneRef instanceof SceneRef)) {
			error(gameRefObject, "公会副本场景不存在:" + sceneRefId);
		}
	}

}
