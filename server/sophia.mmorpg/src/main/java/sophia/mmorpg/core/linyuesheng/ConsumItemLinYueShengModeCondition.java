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
package sophia.mmorpg.core.linyuesheng;

import sophia.foundation.util.ObjectNumberPair;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.itemBag.ItemBag;
import sophia.mmorpg.utils.RuntimeResult;

public final class ConsumItemLinYueShengModeCondition implements LinYueShengModePlayerConsumCondition {
	private ObjectNumberPair<String> itemRefIdNumberPair;
	
	public ConsumItemLinYueShengModeCondition() {
		
	}

	public ObjectNumberPair<String> getItemRefIdNumberPair() {
		return itemRefIdNumberPair;
	}


	public void setItemRefIdNumberPair(ObjectNumberPair<String> itemRefIdNumberPair) {
		this.itemRefIdNumberPair = itemRefIdNumberPair;
	}

	@Override
	public RuntimeResult eligible(Player player) {
		ItemBag itemBag = player.getItemBagComponent().getItemBag();
		String itemRefId = itemRefIdNumberPair.getObject();
		int number = itemRefIdNumberPair.getNumber();
		
		if (number <= itemBag.getItemNumber(itemRefId)) {
			return RuntimeResult.OK();
		} else {
			return RuntimeResult.RuntimeError();
		}
	}

	@Override
	public boolean consumed(Player player) {
		ItemBag itemBag = player.getItemBagComponent().getItemBag();
		return itemBag.takeItemIfEnough(itemRefIdNumberPair);
	}
}
