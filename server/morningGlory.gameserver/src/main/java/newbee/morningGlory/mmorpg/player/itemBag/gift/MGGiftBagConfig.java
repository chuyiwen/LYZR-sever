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
package newbee.morningGlory.mmorpg.player.itemBag.gift;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sophia.game.ref.AbstractGameRefObjectBase;

/**
 * 礼包配置数据
 */
public final class MGGiftBagConfig extends AbstractGameRefObjectBase {

	private static final long serialVersionUID = 3326836935139165647L;
	public static final String Gift_Data_Id = "MGGiftBagConfig_RefId";
	private Map<String, List<MGGiftRef>> giftConfigMap = new HashMap<String, List<MGGiftRef>>();
	public Map<String, List<MGGiftRef>> getGiftConfigMap() {
		return giftConfigMap;
	}
	public void setGiftConfigMap(Map<String, List<MGGiftRef>> giftConfigMap) {
		this.giftConfigMap = giftConfigMap;
	}

	

}
