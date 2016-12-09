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
package newbee.morningGlory.checker.refObjectChecker.operatActivity;

import java.util.ArrayList;
import java.util.List;

import newbee.morningGlory.checker.BaseRefChecker;
import newbee.morningGlory.mmorpg.operatActivities.OperatActivityRef;
import newbee.morningGlory.mmorpg.operatActivities.awardContent.AwardItem;
import sophia.game.GameRoot;
import sophia.game.ref.GameRefObject;
import sophia.mmorpg.player.itemBag.ItemPair;

public class operatActivityChecker extends BaseRefChecker<OperatActivityRef> {

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return "充值开服类游戏";
	}

	@Override
	public void check(GameRefObject gameRefObject) {
		
		OperatActivityRef ref = (OperatActivityRef)gameRefObject;
		List<AwardItem> awardItems = ref.getAwardContent().getAwardItems();
		List<String> receiveIds = new ArrayList<String>();
		for(AwardItem awardItem : awardItems){
			String operatActivityId = awardItem.getId();
			if(!receiveIds.contains(operatActivityId)){
				receiveIds.add(operatActivityId);
			}else{
				error(gameRefObject,"开服充值类活动数据，用于领奖的唯一id 有重复，位置:"+ref.getType()+",refId:"+ref.getId());
			}
			for(ItemPair itemPair : awardItem.getItems()){
				String itemRefId = itemPair.getItemRefId();
				GameRefObject obj = GameRoot.getGameRefObjectManager().getManagedObject(itemRefId);
				if(obj == null){
					error(gameRefObject, "开服充值类活动checker时 发现奖励道具refId 不存在 ："+itemRefId);
				}
			}
		}
		
	}

}
