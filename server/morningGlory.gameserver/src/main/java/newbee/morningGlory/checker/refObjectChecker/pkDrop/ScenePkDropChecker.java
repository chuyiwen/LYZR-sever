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
package newbee.morningGlory.checker.refObjectChecker.pkDrop;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;

import newbee.morningGlory.checker.BaseRefChecker;
import newbee.morningGlory.mmorpg.player.pk.ref.MGInvasionPair;
import newbee.morningGlory.mmorpg.player.pk.ref.MGScenePKDropRef;
import sophia.foundation.property.PropertyDictionary;
import sophia.game.GameRoot;
import sophia.game.ref.GameRefObject;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;

public class ScenePkDropChecker extends BaseRefChecker<MGScenePKDropRef> {

	@Override
	public String getDescription() {
		return "pk掉落";
	}

	@Override
	public void check(GameRefObject gameRefObject) {
		MGScenePKDropRef ref = (MGScenePKDropRef) gameRefObject;
		String refId = ref.getId();
		GameRefObject pkRef = GameRoot.getGameRefObjectManager().getManagedObject(refId);
		if (pkRef == null) {
			error(gameRefObject, "PKDrop refId 不存在:" + refId);
		}
		PropertyDictionary pd = ref.getProperty();
		String sceneRefId = ref.getSceneRefId();
		// GameRefObject obj =
		// GameRoot.getGameRefObjectManager().getManagedObject(sceneRefId);
		// if(obj == null){
		// error(gameRefObject,"场景 scene Id 不存在:"+sceneRefId);
		// }
		int killAddPKValue = MGPropertyAccesser.getKillAddPKValue(pd);
		int attackAddPKValue = MGPropertyAccesser.getAttackAddPkValue(pd);
		if (killAddPKValue < 0) {
			error(gameRefObject, "pk时杀死玩家增加的pk值小于 0,RefId为:" + refId);
		}
		if (attackAddPKValue < 0) {
			error(gameRefObject, "pk时攻击玩家增加的pk值小于 0,RefId为:" + refId);
		}
		if (StringUtils.equals("pkDrop_1", ref.getId())) {
			Map<String, List<MGInvasionPair>> invasionMap = ref.getInvasionMap();

			for (Entry<String, List<MGInvasionPair>> entry : invasionMap.entrySet()) {
				for (MGInvasionPair pair : entry.getValue()) {
					String itemRefID = pair.getItemRefId();
					GameRefObject itemRef = GameRoot.getGameRefObjectManager().getManagedObject(itemRefID);
					if (itemRef == null) {
						error(gameRefObject, "怪物入侵掉落物品道具RefID不存在：" + itemRefID);
					}
				}
			}
		}
		
		String dropMethod = ref.getDropMethod();
		if (!ref.isUseDefaultDrop() && StringUtils.isNotEmpty(dropMethod)) {
			String className = "newbee.morningGlory.mmorpg.player.pk.PkDropMgr";						
			try {
				Class<?> clazz = Class.forName(className);
				Method method = clazz.getMethod(dropMethod, new Class[] {Player.class,Player.class, GameRefObject.class });
				
			} catch (Exception e) {
				error(gameRefObject, "场景掉落数据找不到对应的掉落实现方法："+dropMethod);
			}
			
		}
	}

}
