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
package newbee.morningGlory.mmorpg.item.useableItem;

import groovy.lang.Closure;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import sophia.mmorpg.item.Item;
import sophia.mmorpg.item.ref.ItemRef;
import sophia.mmorpg.item.ref.UseableItemClosureRef;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.useableItem.UseableItemRuntime;
import sophia.mmorpg.utils.RuntimeResult;

public final class MGUseableItemRuntime implements UseableItemRuntime { 
	private static final Logger logger = Logger.getLogger(MGUseableItemRuntime.class);
	private final Map<String, Closure<RuntimeResult>> useToClosureMap;
	
	public MGUseableItemRuntime(MGUseableItemClosures mgUseableItemClosures) {
		useToClosureMap = new HashMap<>(mgUseableItemClosures.getMap());
	}
	
	@Override
	public RuntimeResult useTo(Player player, Item item,int number) {  
		if(logger.isDebugEnabled())
		{
			logger.debug("正在尝试使用物品："+item.getItemRef().getId());
		}
		ItemRef itemRef = item.getItemRef();
		String closure =itemRef.getComponentRef(UseableItemClosureRef.class).getUseItemClosure();
		
		Closure<RuntimeResult> runtime = useToClosureMap.get(closure);
		
		if (runtime == null) {
			return RuntimeResult.ParameterError("物品id=" + itemRef.getId() + ",的物品。在使用物品的时候，没有找到对应的使用调用函数。");
		} else {
			return runtime.call(player, item,number);
		}
	}
	
	
}
