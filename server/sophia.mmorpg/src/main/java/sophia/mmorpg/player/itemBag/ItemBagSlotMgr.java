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
package sophia.mmorpg.player.itemBag;

import java.util.Collections;
import java.util.List;

import sophia.mmorpg.item.Item;

/**
 * 背包-格-管理
 */
public final class ItemBagSlotMgr {

	public ItemBagSlotMgr() {

	}

	/**
	 * 扩包，指定数量
	 * 
	 * @param number
	 *            指定数量
	 */
	public boolean expendItemBagSlot(final ItemBagSlotContains itemBagSlotList, int number) {
		synchronized (itemBagSlotList) {
			int crtNumber = itemBagSlotList.getNumberOfSlot();

			if (number <= 0 || crtNumber + number > itemBagSlotList.getMax_Slot_Number()) {
				return false;
			} else {
				List<ItemBagSlot> slotList = itemBagSlotList.getSlotList();
				for (int i = 0; i < number; i++) {
					short index = (short) (crtNumber + i);
					slotList.add(new ItemBagSlot(index));
				}
				return true;
			}
		}
	}

	/**
	 * 整理背包物品
	 */
	public void arrangeItemBag(final ItemBagSlotContains itemBagSlotList) {
		synchronized (itemBagSlotList) {
			arrangeIfItemEqual(itemBagSlotList);

			orderItemBag(itemBagSlotList);
		}
	}

	/**
	 * 合并背包物品
	 */
	public void mergeItems(final ItemBagSlotContains itemBagSlotList) {
		synchronized (itemBagSlotList) {
			arrangeIfItemEqual(itemBagSlotList);

			arrangeIfItemRefEqual(itemBagSlotList);

			orderItemBag(itemBagSlotList);
		}
	}

	private void arrangeIfItemEqual(final ItemBagSlotContains itemBagSlotList) {
		synchronized (itemBagSlotList) {
			List<ItemBagSlot> slotList = itemBagSlotList.getSlotList();
			int slotNumber = slotList.size();
			for (int i = 0; i < slotNumber; i++) {
				ItemBagSlot slot = slotList.get(i);
				if (slot.hasItem() && (!slot.isFull())) {
					Item item = slot.getItem();
					while (!slot.isFull()) {
						for (int j = i + 1; j < slotNumber; j++) {
							ItemBagSlot tempSlot = slotList.get(j);
							if (tempSlot.hasItem() && (!tempSlot.isFull())) {
								Item tempItem = tempSlot.getItem();
								if (item.equalItem(tempItem)) {
									slot.mergeEqualItemFrom(tempSlot);
								}
							}
						}
					}
				}
			}
		}
	}

	private void arrangeIfItemRefEqual(final ItemBagSlotContains itemBagSlotList) {
		synchronized (itemBagSlotList) {
			List<ItemBagSlot> slotList = itemBagSlotList.getSlotList();
			int slotNumber = slotList.size();
			for (int i = 0; i < slotNumber; i++) {
				ItemBagSlot slot = slotList.get(i);
				if (slot.hasItem() && (!slot.isFull())) {
					Item item = slot.getItem();
					while (!slot.isFull()) {
						for (int j = i + 1; j < slotNumber; j++) {
							ItemBagSlot tempSlot = slotList.get(j);
							if (tempSlot.hasItem() && (!tempSlot.isFull())) {
								Item tempItem = tempSlot.getItem();
								if (item.equalItemRef(tempItem)) {
									slot.mergeEqualItemFrom(tempSlot);
								}
							}
						}
					}
				}
			}
		}
	}

	private void orderItemBag(final ItemBagSlotContains itemBagSlotList) {
		synchronized (itemBagSlotList) {
			Collections.sort(itemBagSlotList.getSlotList());
		}
	}
}
