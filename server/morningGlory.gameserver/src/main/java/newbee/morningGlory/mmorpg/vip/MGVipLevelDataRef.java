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
package newbee.morningGlory.mmorpg.vip;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sophia.game.ref.AbstractGameRefObjectBase;
import sophia.mmorpg.player.itemBag.ItemPair;

public class MGVipLevelDataRef extends AbstractGameRefObjectBase{
	
	private static final long serialVersionUID = 6313785400601677531L;
	private Map<Byte,Map<Integer,ItemPair>> weaponMaps = new HashMap<Byte,Map<Integer,ItemPair>>();
	List<Integer> viplevels = new ArrayList<Integer>();
	
	public MGVipLevelDataRef() {
		
	}

	public Map<Byte, Map<Integer, ItemPair>> getWeaponMaps() {
		return weaponMaps;
	}

	public void setWeaponMaps(Map<Byte, Map<Integer, ItemPair>> weaponMaps) {
		this.weaponMaps = weaponMaps;
	}

	public List<Integer> getViplevels() {
		return viplevels;
	}

	public void setViplevels(List<Integer> viplevels) {
		this.viplevels = viplevels;
	}
	
	public ItemPair getVipLevelWeaponReward(byte profession, int level){
		Map<Integer, ItemPair> map = this.weaponMaps.get(profession);
		if(map == null){
			return null;
		}	
		return map.get(level);
	}
}
