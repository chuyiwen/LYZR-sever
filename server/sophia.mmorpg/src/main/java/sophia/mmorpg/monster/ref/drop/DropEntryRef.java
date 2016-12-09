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
package sophia.mmorpg.monster.ref.drop;

import java.util.ArrayList;
import java.util.List;

import sophia.mmorpg.player.itemBag.ItemPair;
import sophia.mmorpg.utils.SFRandomUtils;

public class DropEntryRef {
	
	private String dropGroupId;
	
	private int probability;

	private int minItemNumber;
	
	private int maxItemNumber;
	
	private List<ItemPair> itemPairList;
	
	// 虚拟物品RefId (游戏币等)
	private String coinItemRefId;
	// 最小游戏币掉落个数
	private int minUnbindedCopperNumber;
	// 最大游戏币掉落个数
	private int maxUnbindedCopperNumber;
	// 游戏币最小数目
	private int minCoinNumber;
	// 游戏币最大数目
	private int maxCoinNumber;
	
	public String getDropGroupId() {
		return dropGroupId;
	}

	public void setDropGroupId(String dropGroupId) {
		this.dropGroupId = dropGroupId;
	}

	public int getProbability() {
		return probability;
	}

	public void setProbability(int probability) {
		this.probability = probability;
	}

	public int getMinItemNumber() {
		return minItemNumber;
	}

	public void setMinItemNumber(int minItemNumber) {
		this.minItemNumber = minItemNumber;
	}
	
	public int getMaxItemNumber() {
		return maxItemNumber;
	}

	public void setMaxItemNumber(int maxItemNumber) {
		this.maxItemNumber = maxItemNumber;
	}

	public List<ItemPair> getItemPairList() {
		return itemPairList;
	}

	public void setItemPairList(List<ItemPair> itemPairList) {
		this.itemPairList = itemPairList;
	}

	public int getMinUnbindedCopperNumber() {
		return minUnbindedCopperNumber;
	}

	public void setMinUnbindedCopperNumber(int minUnbindedCopperNumber) {
		this.minUnbindedCopperNumber = minUnbindedCopperNumber;
	}

	public int getMaxUnbindedCopperNumber() {
		return maxUnbindedCopperNumber;
	}

	public void setMaxUnbindedCopperNumber(int maxUnbindedCopperNumber) {
		this.maxUnbindedCopperNumber = maxUnbindedCopperNumber;
	}

	public int getMinCoinNumber() {
		return minCoinNumber;
	}

	public void setMinCoinNumber(int minCoinNumber) {
		this.minCoinNumber = minCoinNumber;
	}

	public int getMaxCoinNumber() {
		return maxCoinNumber;
	}

	public void setMaxCoinNumber(int maxCoinNumber) {
		this.maxCoinNumber = maxCoinNumber;
	}
	
	public String getCoinItemRefId() {
		return coinItemRefId;
	}

	public void setCoinItemRefId(String coinItemRefId) {
		this.coinItemRefId = coinItemRefId;
	}
	
	/**
	 * 掉落是否成功
	 * @return
	 */
	public boolean randDropSuccess() {
		if (probability >= SFRandomUtils.random100w()) {
			return true;
		}
		
		return false;
	}
	
	/**
	 * 随机出itemCount个数的不重复的道具
	 * @param itemCount
	 * @return
	 */
	public List<ItemPair> randItemPairList() {
		List<ItemPair> list = new ArrayList<>();
		
		// 随机物品
		int count = maxItemNumber;
		if (count != 0) {
			if (minItemNumber != maxItemNumber) {
				count = SFRandomUtils.random(minItemNumber, maxItemNumber);
			} else {
				count = minItemNumber;
			}
			
			if (itemPairList.size() < count) {
				count = itemPairList.size();
			}
			
			int[] indexArray = SFRandomUtils.randomArray(0, itemPairList.size() - 1, count);
			for (int i = 0; i < indexArray.length; i++) {
				int index = indexArray[i];
				list.add(itemPairList.get(index));
			}
		}
		
		// 随机虚拟物品(游戏币)
		count = maxUnbindedCopperNumber;
		if (maxUnbindedCopperNumber != 0) {
			if (minUnbindedCopperNumber != maxUnbindedCopperNumber) {
				count = SFRandomUtils.random(minUnbindedCopperNumber, maxUnbindedCopperNumber);
			} else {
				count = minUnbindedCopperNumber;
			}
		}
		
		while (count-- > 0) {
			
			int coins = 0;
			if (minCoinNumber == maxCoinNumber) {
				coins = minCoinNumber;
			} else {
				coins = SFRandomUtils.random(minCoinNumber, maxCoinNumber);
			}

			ItemPair coinItemPair = new ItemPair(getCoinItemRefId(), coins, false);
			list.add(coinItemPair);
		}

		return list;
	}
}
