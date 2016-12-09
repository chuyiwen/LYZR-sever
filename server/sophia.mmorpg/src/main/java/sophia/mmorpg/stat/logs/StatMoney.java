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
package sophia.mmorpg.stat.logs;

import sophia.mmorpg.stat.StatLogType;
import sophia.mmorpg.utils.RecyclePool;

public class StatMoney extends AbstractStatLog{
	
	public static final byte Add = 1;
	public static final byte Sub = 2;
	
	public static final byte Gold = 1;
	public static final byte UnBindGold = 2;
	public static final byte BindGold = 3;
	
	public static RecyclePool<StatMoney> Pool = new RecyclePool<StatMoney>() {

		@Override
		protected StatMoney instance() {
			return new StatMoney();
		}

		@Override
		protected void onRecycle(StatMoney obj) {
			obj.clear();
		}
	};
	
	@Override
	public void recycle() {
		Pool.recycle(this);
	}
	@Override
	public byte getStatLogType() {		
		return StatLogType.Money;
	}
	public void setSource(byte source){
		data.n5 = source;
	}
	public void setCrtMoney(int crtMoney){
		data.n4 = crtMoney;
	}
	
	public void setNumber(int number){
		data.n3 = number;
	}
	/**
	 * "单位  1=金币 2=元宝 3=绑定元宝"
	 * @param saleCurrency
	 */
	public void setCurrency(int currency){
		data.n2 = currency;
	}
	
	public void setOptType(byte optType){
		data.n1 = optType;
	}
	
}
