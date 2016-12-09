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
package newbee.morningGlory.mmorpg.vip.lottery.ref;

import java.util.Map;

import sophia.game.ref.AbstractGameRefObjectBase;

public class MGVipLotteryDataConfig extends AbstractGameRefObjectBase {
	
	private static final long serialVersionUID = -8192192444176572654L;

	private static final String  id = "VipLottery_Id"; 
	
	public static final String  LotteryVip_Id = "LotteryVip_Id"; 
	
	public static final String  LotteryReward_Id = "LotteryReward_Id"; 
	
	public static final String  LotteryCount_Id = "LotteryCount_Id"; 
	
	private Map<String,MGLotteryCountDataRef> lotteryCountMaps ;
	
	private Map<String,MGLotteryVipDataRef> lotteryVipMaps ;
	
	private Map<String,MGLotteryRewardDataRef> lotteryRewardMaps ;
	
	public Map<String, MGLotteryCountDataRef> getLotteryCountMaps() {
		return lotteryCountMaps;
	}

	public void setLotteryCountMaps(Map<String, MGLotteryCountDataRef> lotteryCountMaps) {
		this.lotteryCountMaps = lotteryCountMaps;
	}

	public Map<String, MGLotteryVipDataRef> getLotteryVipMaps() {
		return lotteryVipMaps;
	}

	public void setLotteryVipMaps(Map<String, MGLotteryVipDataRef> lotteryVipMaps) {
		this.lotteryVipMaps = lotteryVipMaps;
	}

	public Map<String, MGLotteryRewardDataRef> getLotteryRewardMaps() {
		return lotteryRewardMaps;
	}

	public void setLotteryRewardMaps(Map<String, MGLotteryRewardDataRef> lotteryRewardMaps) {
		this.lotteryRewardMaps = lotteryRewardMaps;
	}

	public MGVipLotteryDataConfig() {
	}
	
	
}
