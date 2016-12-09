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
package newbee.morningGlory.mmorpg.player.talisman.level;

import java.util.HashMap;
import java.util.Map;

import newbee.morningGlory.mmorpg.player.talisman.MGTalismanRef;
import sophia.game.ref.AbstractGameRefObjectBase;

public final class MGTalismanDataConfig extends AbstractGameRefObjectBase {

	private static final long serialVersionUID = -8003614683201634099L;
	
	public static final String MGTalisman_Id = "MGTalismanDataConfig_Id";
	
	private Map<String,MGTalismanRef> talismanLevelDataMap = new HashMap<>();
	
	public MGTalismanDataConfig() {
		setId(MGTalisman_Id);
	}

	public Map<String,MGTalismanRef> getTalismanLevelDataMap() {
		return talismanLevelDataMap;
	}

	public void setTalismanLevelDataMap(Map<String,MGTalismanRef> talismanLevelDataMap) {
		this.talismanLevelDataMap = talismanLevelDataMap;
	}
	
	@Override
	public void setId(String id) {
		super.setId(id);
	}
}
