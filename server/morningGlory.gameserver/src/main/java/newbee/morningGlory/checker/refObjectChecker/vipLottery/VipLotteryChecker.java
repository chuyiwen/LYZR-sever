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
package newbee.morningGlory.checker.refObjectChecker.vipLottery;

import newbee.morningGlory.checker.BaseRefChecker;
import newbee.morningGlory.mmorpg.vip.lottery.ref.MGLotteryRewardDataRef;
import newbee.morningGlory.mmorpg.vip.lottery.ref.MGVipLotteryDataConfig;

import org.apache.commons.lang3.StringUtils;

import sophia.game.GameRoot;
import sophia.game.ref.GameRefObject;
import sophia.mmorpg.item.ref.ItemRef;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;

public class VipLotteryChecker extends BaseRefChecker<MGVipLotteryDataConfig> {

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void check(GameRefObject gameRefObject) {
		MGVipLotteryDataConfig viplotteryObject = (MGVipLotteryDataConfig) gameRefObject;
		String id = viplotteryObject.getId();
		if(StringUtils.equals(id, "LotteryReward_Id")){
			vipLotteryRewardChecker(viplotteryObject);
		}
	}

	public void vipLotteryRewardChecker(MGVipLotteryDataConfig viplotteryObject){
		for (java.util.Map.Entry<String,MGLotteryRewardDataRef> entry : viplotteryObject.getLotteryRewardMaps().entrySet()) {
			
			MGLotteryRewardDataRef ref = entry.getValue();
			String itemRefId = MGPropertyAccesser.getItemRefId(ref.getProperty());
			ItemRef itemRef = (ItemRef)GameRoot.getGameRefObjectManager().getManagedObject(itemRefId);
			if(itemRef == null){
				error(viplotteryObject, "VIP抽奖物品道具RefID不存在：" + itemRefId);
			}
			int number = MGPropertyAccesser.getNumber(ref.getProperty());
			if(number < 1){
				error(viplotteryObject,"VIP抽奖物品道具RefID数量为0:"+itemRefId);
			}
			
		}
	}
}
