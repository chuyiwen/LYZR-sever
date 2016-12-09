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
package newbee.morningGlory.mmorpg.vip.lottery;

import java.util.HashMap;
import java.util.Map;

import newbee.morningGlory.mmorpg.vip.lottery.ref.MGLotteryRewardDataRef;

public final class MGVipLotteryRecord {
	
	private long lastUpdateLotteryCountTime = 0;
	private int residueCount = 0;
	
	private Map<Byte,MGLotteryRewardDataRef> rewardMaps = new HashMap<>();
	public static final String lot = "lot_";
	public static final String lotteryGroup = "lotteryGroup_";
	public static final String lotvip = "lotvip_";
	
	
	public int getResidueCount() {
		return residueCount;
	}
	public void setResidueCount(int residueCount) {
		this.residueCount = residueCount;
	}
	public Map<Byte,MGLotteryRewardDataRef> getRewardMaps() {
		return rewardMaps;
	}
	public void setRewardMaps(Map<Byte,MGLotteryRewardDataRef> rewardMaps) {
		this.rewardMaps = rewardMaps;
	}
	public void addResidueCount(int days){
		this.residueCount = this.residueCount + days;
	}
	public long getLastUpdateLotteryCountTime() {
		return lastUpdateLotteryCountTime;
	}
	public void setLastUpdateLotteryCountTime(long lastUpdateLotteryCountTime) {
		this.lastUpdateLotteryCountTime = lastUpdateLotteryCountTime;
	}
	
}
