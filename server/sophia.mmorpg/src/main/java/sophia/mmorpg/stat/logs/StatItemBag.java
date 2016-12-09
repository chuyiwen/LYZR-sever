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

public class StatItemBag extends AbstractStatLog{
	
	public static final byte Add = 1;
	public static final byte Remove = 2;
	public static final byte Use = 3;
	
	public static RecyclePool<StatItemBag> Pool = new RecyclePool<StatItemBag>() {

		@Override
		protected StatItemBag instance() {
			return new StatItemBag();
		}

		@Override
		protected void onRecycle(StatItemBag obj) {
			obj.clear();
		}
	};
	
	@Override
	public void recycle() {
		Pool.recycle(this);
	}
	
	@Override
	public byte getStatLogType() {
		
		return StatLogType.ItemBag;
	}
	
	
	
	public void setItemRefId(String itemRefId){
		data.s1 = itemRefId;
	}
	public void setSource(byte source){
		data.n3 = source;
	}
	public void setNumber(int number){
		data.n2 = number;
	}
	
	public void setOptType(byte optType){
		data.n1 = optType;
	}
}
