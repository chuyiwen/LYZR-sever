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
package newbee.morningGlory.system;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import newbee.morningGlory.mmorpg.player.auction.MGAuctionComponent;
import sophia.game.component.AbstractComponent;
import sophia.game.component.ComponentShield;

public final class ComponentShieldMgr {
	
	private static final Map<String, Class<? extends AbstractComponent>> components = new HashMap<>();
	
	private static void addComponents(String componentName, Class<? extends AbstractComponent> clazz) {
		components.put(componentName, clazz);
	}
	
	public static Map<String, Boolean> getComponentStateList() {
		Map<String, Boolean> retComponents = new HashMap<>();
		Set<Entry<String,Class<? extends AbstractComponent>>> entrySet = components.entrySet();
		Iterator<Entry<String, Class<? extends AbstractComponent>>> iterator = entrySet.iterator();
		boolean openFlag = true;
		while (iterator.hasNext()) {
			openFlag = true;
			Entry<String, Class<? extends AbstractComponent>> entry = iterator.next();
			if (ComponentShield.containComponent(entry.getValue().getSimpleName())) {
				openFlag = false;
			}
			
			retComponents.put(entry.getKey(), openFlag);
		}

		return retComponents;
	}
	
	public static boolean changeComponentState(String componentName, boolean offFlag) {
		if (!components.containsKey(componentName)) {
			return false;
		}
		
		String componentClassName = components.get(componentName).getSimpleName(); 
		if (offFlag == true) {
			return ComponentShield.addComponent(componentClassName);
		} else {
			return ComponentShield.removeComponent(componentClassName);
		}
	}
	
	static {
		addComponents("拍卖行", MGAuctionComponent.class);
	}
}
