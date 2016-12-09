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
package newbee.morningGlory.mmorpg.player.itemBag;

import groovy.lang.Closure;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import sophia.mmorpg.item.Item;
import sophia.mmorpg.item.ref.ItemRef;
import sophia.mmorpg.item.ref.PutItemClosureRef;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.itemBag.ItemBagPutItemRuntime;
import sophia.mmorpg.utils.RuntimeResult;


public final class MGItemBagPutItemRuntime implements ItemBagPutItemRuntime {
	private static final Logger logger = Logger.getLogger(MGItemBagPutItemRuntime.class);
	private final Map<String, Closure<RuntimeResult>> putItemToClosureMap;
	public MGItemBagPutItemRuntime(MGItemBagPutItemClosures mgItemBagPutItemClosures) {
		putItemToClosureMap = new HashMap<>(mgItemBagPutItemClosures.getMap());
	}
	@Override
	public RuntimeResult putItem(Player player, Item item,int index) {
		
		if(logger.isDebugEnabled())
		{
			logger.debug("正在尝试放入物品："+item.getItemRef().getId());
		}
		ItemRef itemRef = item.getItemRef();
		String closure =itemRef.getComponentRef(PutItemClosureRef.class).getPutItemClosure();
		
		Closure<RuntimeResult> runtime = putItemToClosureMap.get(closure);
		
		if (runtime == null) {
			return RuntimeResult.ParameterError("物品id=" + itemRef.getId() + ",的物品。在放入物品的时候，没有找到对应的使用调用函数。");
		} else {
			return runtime.call(player, item,index);
		}
	}
	@Override
	public RuntimeResult putMerit(Player player, int number) {
		
		Closure<RuntimeResult> runtime = putItemToClosureMap.get("addPlayerMerit");
		
		if (runtime == null) {
			return RuntimeResult.ParameterError("增加功勋点的时候，没有找到对应的使用调用函数。");
		} else {
			return runtime.call(player, number);
		}
	}
	
	@Override
	public RuntimeResult putAchievement(Player player, int number) {
		Closure<RuntimeResult> runtime = putItemToClosureMap.get("addPlayerAchievement");
		
		if (runtime == null) {
			return RuntimeResult.ParameterError("增加成就值点的时候，没有找到对应的使用调用函数。");
		} else {
			return runtime.call(player, number);
		}
	}
	
}
