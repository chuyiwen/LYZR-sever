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

import java.util.HashMap;
import java.util.Map;

public class MGVipRewardRecord {
	private long GetExpTime ;
	private long GetGiftTime ;
	private Map<Integer,Integer> vipLevelRewardTimeMap = new HashMap<>(6);
	public MGVipRewardRecord() {
	}
	
	
	public long getGetExpTime() {
		return GetExpTime;
	}


	public void setGetExpTime(long getExpTime) {
		GetExpTime = getExpTime;
	}


	public long getGetGiftTime() {
		return GetGiftTime;
	}


	public void setGetGiftTime(long getGiftTime) {
		GetGiftTime = getGiftTime;
	}


	public Map<Integer, Integer> getVipLevelRewardTimeMap() {
		return vipLevelRewardTimeMap;
	}
	public void setVipLevelRewardTimeMap(Map<Integer, Integer> vipLevelRewardTimeMap) {
		this.vipLevelRewardTimeMap = vipLevelRewardTimeMap;
	}
	
	
	
}
