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
package newbee.morningGlory.checker.refObjectChecker.monster;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import sophia.game.GameRoot;
import sophia.game.ref.GameRefObject;
import sophia.mmorpg.item.ref.ItemRef;
import sophia.mmorpg.monster.ref.MonsterRef;
import sophia.mmorpg.monster.ref.drop.DirectDropRef;
import sophia.mmorpg.monster.ref.drop.DropEntryRef;
import sophia.mmorpg.monster.ref.drop.LevelDropRef;
import sophia.mmorpg.monster.ref.drop.MonsterDropRef;
import sophia.mmorpg.player.itemBag.ItemPair;
import newbee.morningGlory.checker.BaseRefChecker;

public class MonsterDropRefChecker extends BaseRefChecker<MonsterDropRef> {

	@Override
	public String getDescription() {
		return "怪物掉落";
	}

	@Override
	public void check(GameRefObject gameRefObject) {
		MonsterDropRef ref = (MonsterDropRef) gameRefObject;
		Map<String, DirectDropRef> monsterDrop = ref.getMonsterDrop();
		for (String monsterRefId : monsterDrop.keySet()) {
			MonsterRef monsterRef = (MonsterRef) GameRoot.getGameRefObjectManager().getManagedObject(monsterRefId);
			if (monsterRef == null) {
				error(gameRefObject, "不存在怪物refId = " + monsterRefId);
			}
			
			DirectDropRef directDropRef = monsterDrop.get(monsterRefId);
			checkMonsterDropEntryRef(gameRefObject, monsterRefId, directDropRef.getDropRefList());
		}
		
		List<LevelDropRef> levelDrop = ref.getLevelDrop();
		for (LevelDropRef levelDropRef : levelDrop) {
			if (levelDropRef.getMinLevel() > levelDropRef.getMaxLevel()) {
				error(gameRefObject, "非法的等级掉落, 最小等级大于最大等级  minLevel = " + levelDropRef.getMinLevel() + " maxLevel = " + levelDropRef.getMaxLevel());
			}
			
			checkLevelDropEntryRef(gameRefObject, levelDropRef.getLevelRange(), levelDropRef.getLevelDropRefList());
		}
	}
	
	private void checkLevelDropEntryRef(GameRefObject gameRefObject, String levelRange, List<DropEntryRef> dropRefList) {
		for (DropEntryRef ref : dropRefList) {
			checkItemRef(gameRefObject, ref.getItemPairList());
			
			if (ref.getProbability() <= 0 || ref.getProbability() > 1000000) {
				error(gameRefObject, "levelRange = " + levelRange + " 非法的随机概率 = " + ref.getProbability() + " 合法值范围[0, 1000000]");
			}
			
			if (ref.getMinItemNumber() > ref.getMaxItemNumber()) {
				error(gameRefObject, "levelRange = " + levelRange + " 非法的掉落物品个数, 最小值大于最大值, minItemNumber = " + ref.getMinItemNumber() + " maxItemNumber " + ref.getMaxItemNumber());
			}
			if (ref.getMaxItemNumber() > ref.getItemPairList().size()) {
				error(gameRefObject, "levelRange = " + levelRange + " 非法的掉落物品个数, maxItemNumber大于对应dropGroup的物品列表个数 maxItemNumber=" + ref.getMaxItemNumber());
			}
			
			String coinItemRefId = ref.getCoinItemRefId();
			if (!StringUtils.equals(coinItemRefId, "gold") && !StringUtils.equals(coinItemRefId, "unbindedGold") && !StringUtils.equals(coinItemRefId, "bindedGold")
					&& !StringUtils.equals(coinItemRefId, "exp") && !StringUtils.equals(coinItemRefId, "merit") && !StringUtils.equals(coinItemRefId, "achievement")) {
				error(gameRefObject, "levelRange = " + levelRange + " 非法的奖励类型	" + ref.getCoinItemRefId());
			}
			
			if (ref.getMinUnbindedCopperNumber() > ref.getMaxUnbindedCopperNumber()) {
				error(gameRefObject, "levelRange = " + levelRange + " 非法的金币掉落数量, 最小值大于最大值, mingoldNumber = " + ref.getMinUnbindedCopperNumber() + " maxgoldNumber = " + ref.getMaxUnbindedCopperNumber());
			}
			
			if (ref.getMinCoinNumber() > ref.getMaxCoinNumber()) {
				error(gameRefObject, "levelRange = " + levelRange + " 非法的金币掉落数目, 最小值大于最大值, minNumber = " + ref.getMinCoinNumber() + " maxNumber = " + ref.getMaxCoinNumber());
			}
		}
	}
	
	private void checkMonsterDropEntryRef(GameRefObject gameRefObject, String monsterRefId, List<DropEntryRef> dropRefList) {
		for (DropEntryRef ref : dropRefList) {
			checkItemRef(gameRefObject, ref.getItemPairList());
			
			if (ref.getProbability() <= 0 || ref.getProbability() > 1000000) {
				error(gameRefObject, "monsterRefId = " + monsterRefId + " 非法的随机概率 = " + ref.getProbability() + " 合法值范围[0, 1000000]");
			}
			
			if (ref.getMinItemNumber() > ref.getMaxItemNumber()) {
				error(gameRefObject, "monsterRefId = " + monsterRefId + " 非法的掉落物品个数, 最小值大于最大值, minItemNumber = " + ref.getMinItemNumber() + " maxItemNumber " + ref.getMaxItemNumber());
			}
			if (ref.getMaxItemNumber() > ref.getItemPairList().size()) {
				error(gameRefObject, "monsterRefId = " + monsterRefId + " 非法的掉落物品个数, maxItemNumber大于对应dropGroup的物品列表个数 maxItemNumber=" + ref.getMaxItemNumber());
			}
			
			String coinItemRefId = ref.getCoinItemRefId();
			if (!StringUtils.equals(coinItemRefId, "gold") && !StringUtils.equals(coinItemRefId, "unbindedGold") && !StringUtils.equals(coinItemRefId, "bindedGold")
					&& !StringUtils.equals(coinItemRefId, "exp") && !StringUtils.equals(coinItemRefId, "merit") && !StringUtils.equals(coinItemRefId, "achievement")) {
				error(gameRefObject, "monsterRefId = " + monsterRefId + " 非法的奖励类型	" + ref.getCoinItemRefId());
			}
			
			if (ref.getMinUnbindedCopperNumber() > ref.getMaxUnbindedCopperNumber()) {
				error(gameRefObject, "monsterRefId = " + monsterRefId + " 非法的金币掉落数量, 最小值大于最大值, mingoldNumber = " + ref.getMinUnbindedCopperNumber() + " maxgoldNumber = " + ref.getMaxUnbindedCopperNumber());
			}
			
			if (ref.getMinCoinNumber() > ref.getMaxCoinNumber()) {
				error(gameRefObject, "monsterRefId = " + monsterRefId + " 非法的金币掉落数目, 最小值大于最大值, minNumber = " + ref.getMinCoinNumber() + " maxNumber = " + ref.getMaxCoinNumber());
			}
		}
	}
	
	private void checkItemRef(GameRefObject gameRefObject, List<ItemPair> itemPairList) {
		for (ItemPair itemPair : itemPairList) {
			ItemRef itemRef = (ItemRef) GameRoot.getGameRefObjectManager().getManagedObject(itemPair.getItemRefId());
			if (itemRef == null) {
				error(gameRefObject, "不存在物品refId = " + itemPair.getItemRefId());
			}
		}
	}

}
