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
package newbee.morningGlory.checker.refObjectChecker.HighestEquipmentChecker;

import newbee.morningGlory.checker.BaseRefChecker;
import sophia.game.ref.GameRefObject;
import sophia.mmorpg.equipmentSmith.smith.highestEquipment.HighestEquipmentRef;

public class HighestEquipmentChecker extends BaseRefChecker<HighestEquipmentRef> {

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return "极品装备";
	}

	@Override
	public void check(GameRefObject gameRefObject) {
		HighestEquipmentRef ref = (HighestEquipmentRef)gameRefObject;
		int minRate = ref.getMinRate();
		int maxRate = ref.getMaxRate();
		int[] probability = ref.getProbability();
		int[] randomCount = ref.getRandomCount();
		if(minRate > maxRate){
			error(gameRefObject, "极品装备minRate 概率错误,最小："+minRate+",最大:"+maxRate);
		}
		if(probability.length != randomCount.length){
			error(gameRefObject, "极品装备 概率数和随机属性数量不一致");
		}
	}

}
