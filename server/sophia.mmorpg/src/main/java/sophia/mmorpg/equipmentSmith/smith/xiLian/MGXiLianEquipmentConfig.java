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
package sophia.mmorpg.equipmentSmith.smith.xiLian;


/**
 * 装备-洗练配置数据
 */
public final class MGXiLianEquipmentConfig {

	private static final int Locked_Property_Deposit = 5;

	private int lockedPropertyDeposit = Locked_Property_Deposit;

	private int randomLowValue = 1;

	public MGXiLianEquipmentConfig() {

	}

	public int getLockedPropertyDeposit() {
		return lockedPropertyDeposit;
	}

	public void setLockedPropertyDeposit(int lockedPropertyDeposit) {
		this.lockedPropertyDeposit = lockedPropertyDeposit;
	}

	public int getRandomLowValue() {
		return randomLowValue;
	}

	public void setRandomLowValue(int randomLowValue) {
		this.randomLowValue = randomLowValue;
	}
}
