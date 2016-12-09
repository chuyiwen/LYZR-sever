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

public class StatAuth extends AbstractStatLog {

	public static RecyclePool<StatAuth> Pool = new RecyclePool<StatAuth>() {

		@Override
		protected StatAuth instance() {
			return new StatAuth();
		}

		@Override
		protected void onRecycle(StatAuth obj) {
			obj.clear();
		}
	};
	
	@Override
	public void recycle() {
		Pool.recycle(this);
	}
	
	public StatAuth() {
		super();
	}

	@Override
	public byte getStatLogType() {
		return StatLogType.Auth;
	}

	public void setIp(String ip) {
		data.s1 = ip;
	}

	public void setState(int state) {
		data.n1 = state;
	}

}
