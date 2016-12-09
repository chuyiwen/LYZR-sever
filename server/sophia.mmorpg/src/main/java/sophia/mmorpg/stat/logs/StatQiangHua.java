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

public class StatQiangHua extends AbstractStatLog{
	
	public static final byte Success = 1;
	public static final byte CanQiang = 2;
	public static final byte Failed = 0;
	
	public static RecyclePool<StatQiangHua> Pool = new RecyclePool<StatQiangHua>() {

		@Override
		protected StatQiangHua instance() {
			return new StatQiangHua();
		}

		@Override
		protected void onRecycle(StatQiangHua obj) {
			obj.clear();
		}
	};
	
	@Override
	public void recycle() {
		Pool.recycle(this);
	}
	@Override
	public byte getStatLogType() {
		// TODO Auto-generated method stub
		return StatLogType.QiangHua;
	}
	
	public void setItemRefId(String itemRefId){
		data.s1 = itemRefId;
	}
	
	public void setResult(byte result){
		data.n1 = result;
	}
	
	public void setQiangHuaLevel(int qiangHuaLevel){
		data.n2 = qiangHuaLevel;
	}
	

}
