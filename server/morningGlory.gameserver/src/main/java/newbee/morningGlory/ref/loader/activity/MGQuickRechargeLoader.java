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
package newbee.morningGlory.ref.loader.activity;

import newbee.morningGlory.mmorpg.player.activity.QuickRecharge.QuickRechargeMgr;
import newbee.morningGlory.mmorpg.player.activity.QuickRecharge.QuickRechargeRef;
import newbee.morningGlory.ref.RefKey;
import newbee.morningGlory.ref.loader.AbstractGameRefObjectLoader;

import org.apache.log4j.Logger;

import com.google.gson.JsonObject;

public class MGQuickRechargeLoader extends AbstractGameRefObjectLoader<QuickRechargeRef> {
	private static final Logger logger = Logger.getLogger(MGQuickRechargeLoader.class);

	@Override
	protected QuickRechargeRef create() {
		return new QuickRechargeRef();
	}

	public MGQuickRechargeLoader() {
		super(RefKey.yuanbao);
	}

	@Override
	protected void fillNonPropertyDictionary(QuickRechargeRef ref, JsonObject refData) {
		String refId = ref.getId();
		
		QuickRechargeMgr.quickRefIds.add(refId);
		
		int firstReward = refData.getAsJsonObject().get("firstReward").getAsInt();
		int level = refData.getAsJsonObject().get("level").getAsInt();
		int money = refData.getAsJsonObject().get("money").getAsInt();
		int reward = refData.getAsJsonObject().get("reward").getAsInt();
		int yuanBao = refData.getAsJsonObject().get("yuanBao").getAsInt();
		int rewardBound = refData.getAsJsonObject().get("rewardBound").getAsInt();
		int firstRewardBound = refData.getAsJsonObject().get("firstRewardBound").getAsInt();
		
		ref.setFirstReward(firstReward);
		ref.setLevel(level);
		ref.setUnbindedGold(yuanBao);
		ref.setReward(reward);
		ref.setMoney(money);
		ref.setFirstRewardBound(firstRewardBound);
		ref.setRewardBound(rewardBound);

	}

}
