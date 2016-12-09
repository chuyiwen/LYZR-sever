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
package sophia.mmorpg.item;

import sophia.game.GameRoot;
import sophia.mmorpg.GameObjectProvider;
import sophia.mmorpg.item.ref.ItemRef;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;

public final class DefaultItemProvider implements GameObjectProvider<Item> {
	private static final GameObjectProvider<Item> instance = new DefaultItemProvider();
	
	private DefaultItemProvider() {
		
	}
	
	public static GameObjectProvider<Item> getInstance() {
		return instance;
	}
	
	@Override
	public Item get(Class<Item> type) {
		Item ret = new Item();
		return ret;
	}

	@Override
	public Item get(Class<Item> type, Object... args) {
		String itemRefId = (String) args[0];
		ItemRef itemRef = (ItemRef) GameRoot.getGameRefObjectManager().getManagedObject(itemRefId);	
		if (itemRef==null) {
			throw new RuntimeException("不存在refID="+itemRefId+"的引用数据！");
		}
		byte bindStatus = 0;
		byte bindType = MGPropertyAccesser.getBindType(itemRef.getProperty());
		if (bindType == Item.At_Once_Bind)
			bindStatus = 1;
		if(args.length == 2){
			String id = (String)args[1];
			Item item = new Item(itemRef,id);
			item.setNumber(1);
			item.setBindStatus(bindStatus);
			return item;
		}else{
			Item item = new Item(itemRef);
			item.setNumber(1);
			item.setBindStatus(bindStatus);
			return item;
		}
	}
}
