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
package sophia.mmorpg.loot;

import sophia.mmorpg.GameObjectProvider;
import sophia.mmorpg.item.Item;
import sophia.mmorpg.player.itemBag.ItemPair;

public final class DefaultLootProvider implements GameObjectProvider<Loot> {
	public static final GameObjectProvider<Loot> instance = new DefaultLootProvider();
	
	private DefaultLootProvider() {
		
	}
	
	public static final GameObjectProvider<Loot> getInstance() {
		return instance;
	}

	@Override
	public Loot get(Class<Loot> type) {
		return null;
	}

	@Override
	public Loot get(Class<Loot> type, Object... args) {
		Object obj =  args[0];
		Loot loot = null;
		if(obj instanceof ItemPair){
			ItemPair itemPair = (ItemPair) obj;
			 loot = new Loot(itemPair);
		}
		else{
			Item item = (Item) obj;
			loot = new Loot(item);
		}
		
		return loot;
	}

}
