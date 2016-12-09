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

public class StatEquip extends AbstractStatLog{
	
	public static final byte Equip = 1;
	public static final byte Deequip = 2;
	
	public static RecyclePool<StatEquip> Pool = new RecyclePool<StatEquip>() {

		@Override
		protected StatEquip instance() {
			return new StatEquip();
		}

		@Override
		protected void onRecycle(StatEquip obj) {
			obj.clear();
		}
	};
	
	@Override
	public void recycle() {
		Pool.recycle(this);
	}
	
	@Override
	public byte getStatLogType() {
		
		return StatLogType.Equip;
	}
	
	
	
	public void setItemRefId(String itemRefId){
		data.s1 = itemRefId;
	}
	public void setSource(byte source){
		data.n2 = source;
	}	
	public void setOptType(byte optType){
		data.n1 = optType;
	}
}
