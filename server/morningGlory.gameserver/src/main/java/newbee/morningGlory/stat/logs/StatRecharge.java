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
package newbee.morningGlory.stat.logs;

import sophia.mmorpg.stat.StatLogType;
import sophia.mmorpg.stat.logs.AbstractStatLog;
import sophia.mmorpg.utils.RecyclePool;

public class StatRecharge extends AbstractStatLog {
	
	public static RecyclePool<StatRecharge> Pool = new RecyclePool<StatRecharge>() {

		@Override
		protected StatRecharge instance() {
			return new StatRecharge();
		}

		@Override
		protected void onRecycle(StatRecharge obj) {
			obj.clear();
		}
	};
	@Override
	public byte getStatLogType() {
		return StatLogType.Recharge;
	}

	@Override
	public void recycle() {
		Pool.recycle(this);
	}
	
	public void setRechargeUnBindedGold(int unBindedGold){
		data.n1 = unBindedGold;
	}
	
	public void setPayMoney(int money){
		data.n2 = money;
	}
	public void setPayTime(long time){
		data.n3 = time;
	}

}
