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
package sophia.mmorpg.monster;

import sophia.foundation.property.PropertyDictionary;
import sophia.game.GameRoot;
import sophia.mmorpg.GameObjectProvider;
import sophia.mmorpg.base.sprite.aoi.SpriteAOIComponent;
import sophia.mmorpg.monster.ref.MonsterRef;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;

public final class DefaultMonsterProvider implements GameObjectProvider<Monster> {
	public static final GameObjectProvider<Monster> instance = new DefaultMonsterProvider();
	
	private DefaultMonsterProvider() {
		
	}
	
	public static final GameObjectProvider<Monster> getInstance() {
		return instance;
	}
	
	@Override
	public Monster get(Class<Monster> type) {
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Monster get(Class<Monster> type, Object... args) {
		Monster monster = new Monster(); 
		monster.setAoiComponent((SpriteAOIComponent<Monster>) monster.createComponent(SpriteAOIComponent.class));
		monster.setPathComponent((MonsterPathComponent) monster.createComponent(MonsterPathComponent.class));
		String monsterRefId = (String) args[0];
		MonsterRef monsterRef = (MonsterRef) GameRoot.getGameRefObjectManager().getManagedObject(monsterRefId);
		monster.setMonsterRef(monsterRef);
		PropertyDictionary refPd = monsterRef.getProperty();
		PropertyDictionary pd = monster.getProperty();
		String name = MGPropertyAccesser.getName(refPd);
		MGPropertyAccesser.setOrPutName(pd, name);
		monster.setName(name);
		int maxHP = MGPropertyAccesser.getMaxHP(refPd);
		int maxMP = MGPropertyAccesser.getMaxMP(refPd);
		MGPropertyAccesser.setOrPutHP(pd, maxHP);
		MGPropertyAccesser.setOrPutMP(pd, maxMP);
		MGPropertyAccesser.setOrPutMaxHP(pd, maxHP);
		MGPropertyAccesser.setOrPutMaxMP(pd, maxMP);
		return monster;
	}

}
