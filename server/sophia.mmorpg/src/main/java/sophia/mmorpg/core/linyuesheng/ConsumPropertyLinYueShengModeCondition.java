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

import sophia.mmorpg.player.Player;
import sophia.mmorpg.utils.RuntimeResult;

public final class ConsumPropertyLinYueShengModeCondition implements LinYueShengModePlayerConsumCondition {
	private short propertyId;
	
	private int number;
	
	public ConsumPropertyLinYueShengModeCondition() {
		
	}

	public short getPropertyId() {
		return propertyId;
	}

	public void setPropertyId(short propertyId) {
		this.propertyId = propertyId;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	@Override
	public RuntimeResult eligible(Player gameObject) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean consumed(Player player) {
		// TODO Auto-generated method stub
		return false;
	}
}
