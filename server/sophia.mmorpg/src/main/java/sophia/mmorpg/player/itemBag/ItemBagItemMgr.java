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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import sophia.foundation.util.ObjectNumberPair;
import sophia.mmorpg.GameObjectFactory;
import sophia.mmorpg.item.Item;

public final class ItemBagItemMgr {
	public ItemBagItemMgr() {

	}

	/**
	 * 根据物品id 来查找物品
	 * 
	 * @param itemBagSlotList
	 * @param id
	 * @return
	 */
	public ItemBagSlot findItemById(final ItemBagSlotContains itemBagSlotList, String id) {
		synchronized (itemBagSlotList) {
			for (ItemBagSlot slot : itemBagSlotList.getSlotList()) {
				if (!slot.isEmpty()) {
					Item item = slot.getItem();
					if (StringUtils.equals(item.getId(), id))
						return slot;
				}
			}
		}
		return null;
	}

	public boolean removeItemById(final ItemBagSlotContains itemBagSlotList, String id, int number) {
		synchronized (itemBagSlotList) {
			for (ItemBagSlot slot : itemBagSlotList.getSlotList()) {
				if (!slot.isEmpty()) {
					Item item = slot.getItem();
					if (StringUtils.equals(item.getId(), id))
						return removeItem(slot, number);
				}
			}
		}
		return false;
	}

	/**
	 * 移除指定物品格存放的物品
	 * 
	 * @param slot
	 *            指定物品格
	 * @return 如果能并移除成功，返回true;否则返回false.如果没有物品也返回true.
	 */
	public synchronized boolean clearItem(ItemBagSlot slot) {

		slot.clearItem();
		return true;
	}

	/**
	 * 移除指定物品格存放的指定数量物品
	 * 
	 * @param slot
	 *            指定物品格
	 * @param number
	 *            指定数量
	 * @return 如果能并移除成功，返回true;否则返回false.如果没有物品也返回true.如果物品数量不够将不移除，返回false
	 */
	public boolean removeItem(ItemBagSlot slot, int number) {
		synchronized (slot) {
			if (slot.isEmpty()) {
				return true;
			}

			if (slot.getCrtStackNumber() > number) {
				slot.subItemNumber(number);
				return true;
			} else if (slot.getCrtStackNumber() == number) {
				return clearItem(slot);
			}

			return false;
		}
	}

	/**
	 * 将物品从一格移放到另外一格
	 * 
	 * @param from
	 *            物品当前的背包格
	 * @param to
	 *            物品移放的背包格
	 */
	public void moveItem(ItemBagSlot from, ItemBagSlot to, final ItemBagPartialRenewalData partialRenewalData) {
		if (from.isEmpty()) {
			return;
		}

		if (to.isEmpty()) {
			to.setItem(from.getItem());
			from.clearItem();
		} else {
			Item fromItem = from.getItem();
			Item toItem = to.getItem();
			if (fromItem.equalItemRef(toItem)) {
				to.mergeEqualItemFrom(from);
			} else {
				Item temp = toItem;
				to.setItem(fromItem);
				from.setItem(temp);
			}
		}

		partialRenewalData.setSucceed(true);
		partialRenewalData.addItemBagSlot(from);
		partialRenewalData.addItemBagSlot(to);
	}

	/**
	 * 根据物品ID 删除指定数量的物品
	 * 
	 * @param itemBagSlotList
	 * @param itemRefId
	 * @param number
	 * @param partialRenewalData
	 * @param propertyMap
	 * @return
	 */
	public boolean removeItemByItemRefId(final ItemBagSlotContains itemBagSlotList, String itemRefId, int number, final ItemBagPartialRenewalData partialRenewalData) {
		synchronized (itemBagSlotList) {
			partialRenewalData.setSucceed(true);
			List<ItemBagSlot> slotList = getItemListBySize(itemBagSlotList, itemRefId, (byte) 0);
			for (ItemBagSlot slot : slotList) {
				if (slot.hasItem()) {
					Item item = slot.getItem();
					if (item.getItemRef().getId().equals(itemRefId)) {

						partialRenewalData.addItemBagSlot(slot);
						if (slot.getCrtStackNumber() > number) {

							slot.subItemNumber(number);
							return true;
						} else if (slot.getCrtStackNumber() == number) {
							boolean flag = clearItem(slot);
							return flag;
						} else if (slot.getCrtStackNumber() < number) {
							number = number - slot.getCrtStackNumber();
							clearItem(slot);

						}

					}
				}
			}

			return false;
		}
	}

	/**
	 * 根据物品ID 优先删除指定数量绑定的物品
	 * 
	 * @param itemBagSlotList
	 * @param itemRefId
	 * @param number
	 * @param partialRenewalData
	 * @param propertyMap
	 * @return
	 */
	public boolean removeFirstBindedItemByItemRefId(final ItemBagSlotContains itemBagSlotList, String itemRefId, int number, final ItemBagPartialRenewalData partialRenewalData) {
		synchronized (itemBagSlotList) {
			partialRenewalData.setSucceed(true);
			List<ItemBagSlot> slotList = getItemListBySize(itemBagSlotList, itemRefId, (byte) 1);
			for (ItemBagSlot slot : slotList) {
				if (slot.hasItem()) {
					Item item = slot.getItem();
					if (item.getItemRef().getId().equals(itemRefId) && item.getBindStatus() == 1) {

						partialRenewalData.addItemBagSlot(slot);
						if (slot.getCrtStackNumber() > number) {

							slot.subItemNumber(number);
							return true;
						} else if (slot.getCrtStackNumber() == number) {
							boolean flag = clearItem(slot);
							return flag;
						} else if (slot.getCrtStackNumber() < number) {
							number = number - slot.getCrtStackNumber();
							clearItem(slot);

						}

					}
				}
			}
			if (number > 0) {
				return this.removeItemByItemRefId(itemBagSlotList, itemRefId, number, partialRenewalData);
			}

			return false;
		}
	}

	/**
	 * 将refId 的物品按数量从小到大顺序排列
	 * 
	 * @param itemBagSlotList
	 * @param itemRefId
	 * @param isFirstBinded
	 * @return
	 */
	public List<ItemBagSlot> getItemListBySize(final ItemBagSlotContains itemBagSlotList, String itemRefId, byte isFirstBinded) {
		synchronized (itemBagSlotList) {
			List<ItemBagSlot> itemBagSlots = new ArrayList<>();
			List<ItemBagSlot> slotList = itemBagSlotList.getSlotList();
			for (ItemBagSlot slot : slotList) {
				if (slot.hasItem()) {
					Item item = slot.getItem();
					if (item.getItemRef().getId().equals(itemRefId) && item.getBindStatus() == isFirstBinded) {
						itemBagSlots.add(slot);
					}
				}
			}

			Collections.sort(itemBagSlots, new Comparator<ItemBagSlot>() {
				@Override
				public int compare(ItemBagSlot o1, ItemBagSlot o2) {
					Item item1 = o1.getItem();
					Item item2 = o2.getItem();
					return item1.getNumber() - item2.getNumber();
				}
			});

			return itemBagSlots;
		}
	}

	/**
	 * 获取背包中，指定物品引用id的物品数量。包括（绑定|非绑定）
	 * 
	 * @param itemRefId
	 *            指定物品引用id
	 * @return
	 */
	public int getItemNumber(final ItemBagSlotContains itemBagSlotList, String itemRefId) {
		synchronized (itemBagSlotList) {
			int ret = 0;

			List<ItemBagSlot> slotList = itemBagSlotList.getSlotList();
			for (ItemBagSlot slot : slotList) {
				if (slot.hasItem()) {
					Item item = slot.getItem();
					if (item.getItemRef().getId().equals(itemRefId)) {
						ret += item.getNumber();
					}
				}
			}

			return ret;
		}
	}

	/**
	 * 获取背包中，指定物品引用id的物品数量。
	 * 
	 * @param itemRefId
	 *            指定物品引用id
	 * @param isBinded
	 *            是否绑定。true 绑定物品；false 非绑定物品
	 * @return
	 */
	public int getItemNumber(final ItemBagSlotContains itemBagSlotList, String itemRefId, boolean isBinded) {
		synchronized (itemBagSlotList) {
			int ret = 0;

			List<ItemBagSlot> slotList = itemBagSlotList.getSlotList();
			for (ItemBagSlot slot : slotList) {
				if (slot.hasItem()) {
					Item item = slot.getItem();
					if (item.getItemRef().getId().equals(itemRefId) && item.binded() == isBinded) {
						ret += item.getNumber();
					}
				}
			}

			return ret;
		}
	}

	/**
	 * 背包为空？
	 * 
	 * @return 空，返回true;否则，返回false
	 */
	public boolean isEmpty(final ItemBagSlotContains itemBagSlotList) {
		return itemBagSlotList.isEmpty();
	}

	/**
	 * 背包满了？
	 * 
	 * @return 满，返回true,否则，返回false
	 */
	public boolean isFull(final ItemBagSlotContains itemBagSlotList) {
		return itemBagSlotList.isFull();
	}

	/**
	 * 指定的物品是否可放入背包。如果可以，返回true,否则返回false。
	 * 
	 * @param itemBagSlotList
	 * @param item
	 *            指定的物品
	 * @return 指定的物品是否可放入背包。如果可以，返回true,否则返回false。
	 */
	public boolean canPut(final ItemBagSlotContains itemBagSlotList, final Item item) {
		synchronized (itemBagSlotList) {
			int usedSlotNumber = 0;
			usedSlotNumber += getUsedSlotNumber(itemBagSlotList, item);

			if (usedSlotNumber <= itemBagSlotList.getEmptySlotNumber()) {
				return true;
			} else {
				return false;
			}
		}
	}

	/**
	 * 指定的物品集合是否可放入背包。如果可以，返回true,否则返回false。
	 * 
	 * @param items
	 *            指定的物品集合
	 * @return 指定的物品集合是否可放入背包。如果可以，返回true,否则返回false。
	 */
	public boolean canPut(final ItemBagSlotContains itemBagSlotList, final Collection<Item> items) {
		synchronized (itemBagSlotList) {
			int usedSlotNumber = 0;

			for (Item item : items) {
				usedSlotNumber += getUsedSlotNumber(itemBagSlotList, item);
			}

			if (usedSlotNumber <= itemBagSlotList.getEmptySlotNumber()) {
				return true;
			} else {
				return false;
			}
		}
	}

	private int getContainItem(List<Item> items, Item item) {
		for (Item item1 : items) {
			if (item1.equalItem(item)) {
				items.remove(item1);
				return item1.getNumber();
			}
		}
		return 0;
	}

	public boolean putItemIfAbsent(final ItemBagSlotContains itemBagSlotList, final Item item, ItemBagPartialRenewalData itemBagPartialRenewalData) {
		synchronized (itemBagSlotList) {
			if (item.getNumber() <= 0) {
				return false;
			}
			boolean ret = canPut(itemBagSlotList, item);
			putItem(itemBagSlotList, item, itemBagPartialRenewalData);
			return ret;
		}
	}

	public void putItem(final ItemBagSlotContains itemBagSlotList, final Item item, final ItemBagPartialRenewalData itemBagPartialRenewalData) {
		synchronized (itemBagSlotList) {
			if (item == null) {
				return;
			}
			if (!item.canStack()) {
				if (item.getNumber() > 1) {
					for (int i = 0; i < item.getNumber(); i++) {
						Item temp = GameObjectFactory.getItem(item.getItemRefId());
						temp.setBindStatus(item.getBindStatus());
						ItemBagSlot emptyItemBagSlot = itemBagSlotList.getEmptyItemBagSlot();
						emptyItemBagSlot.setItem(temp);
						itemBagPartialRenewalData.addItemBagSlot(emptyItemBagSlot);
					}
				} else {
					ItemBagSlot emptyItemBagSlot = itemBagSlotList.getEmptyItemBagSlot();
					emptyItemBagSlot.setItem(item);
					itemBagPartialRenewalData.addItemBagSlot(emptyItemBagSlot);
				}
			} else {
				putCanStackItem(itemBagSlotList, item, itemBagPartialRenewalData);
			}
		}
	}

	private void putCanStackItem(final ItemBagSlotContains itemBagSlotList, final Item item, final ItemBagPartialRenewalData itemBagPartialRenewalData) {
		synchronized (itemBagSlotList) {
			int itemNumber = item.getNumber();
			int putNumber = itemNumber;

			List<ItemBagSlot> canStackSlotList = getCanStackSlotList(itemBagSlotList, item);
			for (ItemBagSlot slot : canStackSlotList) {
				if (putNumber <= 0) {
					break;
				}

				int freeStackNumber = slot.getFreeStackNumber();
				if (putNumber > freeStackNumber) {
					putNumber -= freeStackNumber;
					slot.addItemNumber(freeStackNumber);
				} else {
					slot.addItemNumber(putNumber);
					putNumber = 0;

				}
				itemBagPartialRenewalData.addItemBagSlot(slot);
			}

			if (putNumber > 0) {
				int maxStackNumber = item.getMaxStackNumber();
				int slotNumber = putNumber / maxStackNumber;
				if (putNumber % maxStackNumber != 0) {
					slotNumber += 1;
				}
				for (int i = 0; i < slotNumber; i++) {
					if (putNumber <= 0)
						break;
					ItemBagSlot emptyItemBagSlot = itemBagSlotList.getEmptyItemBagSlot();

					int newItemNumber = 0;
					if (putNumber > maxStackNumber) {
						newItemNumber = maxStackNumber;
					} else {
						newItemNumber = putNumber;
					}

					Item newItem = GameObjectFactory.getItem(item.getItemRef().getId());
					newItem.setNumber(newItemNumber);
					newItem.setBindStatus(item.getBindStatus());
					emptyItemBagSlot.setItem(newItem);
					putNumber -= newItemNumber;
					itemBagPartialRenewalData.addItemBagSlot(emptyItemBagSlot);

				}
			}
		}
	}

	/**
	 * 放入指定的物品到背包。如果可以，将全部放入物品，返回true,否则，不放入任何物品，返回false。
	 * 
	 * @param items
	 *            指定的放入物品
	 * @param propertyMap
	 * @return 放入指定的物品到背包。如果可以，将全部放入物品，返回true,否则，不放入任何物品，返回false。
	 */
	public boolean putItemsIfAbsent(final ItemBagSlotContains itemBagSlotList, final Collection<Item> items, final ItemBagPartialRenewalData itemBagPartialRenewalData) {
		synchronized (itemBagSlotList) {
			List<Item> newItems = new ArrayList<Item>();
			for (Item item : items) {
				if (item.getNumber() <= 0) {
					continue;
				}
				int number1 = getContainItem(newItems, item);
				int number2 = item.getNumber();
				item.setNumber(number1 + number2);
				newItems.add(item);
			}
			boolean ret = canPut(itemBagSlotList, newItems);

			if (ret) {
				itemBagPartialRenewalData.setSucceed(true);
				for (Item item : newItems) {
					putItem(itemBagSlotList, item, itemBagPartialRenewalData);
				}
			}

			return ret;
		}
	}

	/**
	 * 指定数量的物品背包是否足够获取.如果足够，返回true;否则，返回false;
	 * 
	 * @param itemRefIdNumberPair
	 *            指定数量的物品
	 * @return
	 */
	public boolean canTake(final ItemBagSlotContains itemBagSlotList, final ObjectNumberPair<String> itemRefIdNumberPair) {
		synchronized (itemBagSlotList) {
			String takeItemItemRefId = itemRefIdNumberPair.getObject();
			int takeItemNumber = itemRefIdNumberPair.getNumber();

			int number = getItemNumber(itemBagSlotList, takeItemItemRefId);

			if (number >= takeItemNumber) {
				return true;
			} else {
				return false;
			}
		}
	}

	/**
	 * 获取指定数量的物品.如果足够，获取，返回true;否则，返回false.
	 * 
	 * @return
	 */
	public boolean takeItemIfEnough(final ItemBagSlotContains itemBagSlotList, final ObjectNumberPair<String> itemRefIdNumberPair) {
		synchronized (itemBagSlotList) {
			if (canTake(itemBagSlotList, itemRefIdNumberPair)) {
				takeItem(itemBagSlotList, itemRefIdNumberPair);
				return true;
			} else {
				return false;
			}
		}
	}

	private void takeItem(final ItemBagSlotContains itemBagSlotList, final ObjectNumberPair<String> itemRefIdNumberPair) {
		synchronized (itemBagSlotList) {
			int number = 0;

			int takeNumber = itemRefIdNumberPair.getNumber();
			String takeItemItemRefId = itemRefIdNumberPair.getObject();
			List<ItemBagSlot> slotList = itemBagSlotList.getSlotList();
			for (ItemBagSlot slot : slotList) {
				if (number >= takeNumber)
					break;

				String itemRefId = slot.getItem().getItemRef().getId();
				if (takeItemItemRefId.equals(itemRefId)) {
					int crtStackNumber = slot.getCrtStackNumber();
					if (takeNumber >= number + crtStackNumber) {
						number += crtStackNumber;
						slot.subItemNumber(crtStackNumber);
					} else {
						int lastTakeNumber = takeNumber - number;
						slot.subItemNumber(lastTakeNumber);
						number = takeNumber;
					}
				}
			}

			if (number != takeNumber) {
				throw new RuntimeException(" 背包存放的物品，不足够获取。take number=" + takeNumber + ", number=" + number);
			}
		}
	}

	/**
	 * 指定数量的物品背包是否足够获取.如果足够，返回true;否则，返回false;
	 * 
	 * @param collection
	 * @return
	 */
	public boolean canTake(final ItemBagSlotContains itemBagSlotList, final Collection<ObjectNumberPair<String>> collection) {
		synchronized (itemBagSlotList) {
			for (ObjectNumberPair<String> itemRefIdNumberPair : collection) {
				if (!canTake(itemBagSlotList, itemRefIdNumberPair))
					return false;
			}

			return true;
		}
	}

	/**
	 * 获取指定数量的物品.如果足够，获取，返回true;否则，返回false.
	 * 
	 * @param collection
	 * @return
	 */
	public boolean takeItemsIfEnough(final ItemBagSlotContains itemBagSlotList, final Collection<ObjectNumberPair<String>> collection) {
		synchronized (itemBagSlotList) {
			if (canTake(itemBagSlotList, collection)) {
				for (ObjectNumberPair<String> itemRefIdNumberPair : collection) {
					takeItem(itemBagSlotList, itemRefIdNumberPair);
				}
				return true;
			} else {
				return false;
			}
		}
	}

	private final int getUsedSlotNumber(final ItemBagSlotContains itemBagSlotContains, Item item) {
		synchronized (itemBagSlotContains) {
			int ret = 0;

			if (item == null) {
				return ret;
			}
			if (item.canStack()) {
				int canStackNumber = 0;
				List<ItemBagSlot> slotList = itemBagSlotContains.getSlotList();

				for (ItemBagSlot slot : slotList) {
					if (!slot.isEmpty() && !slot.isFull()) {
						Item slotItem = slot.getItem();
						if (slotItem.equalItem(item)) {
							canStackNumber += slot.getFreeStackNumber();
						}
					}
				}

				int itemNumber = item.getNumber();
				itemNumber -= canStackNumber;

				if (itemNumber > 0) {
					ret = itemNumber / item.getMaxStackNumber();
					if (itemNumber % item.getMaxStackNumber() > 0) {
						ret += 1;
					}
				}
			} else {
				ret = item.getNumber();
			}

			return ret;
		}
	}

	private final List<ItemBagSlot> getCanStackSlotList(final ItemBagSlotContains itemBagSlotContains, Item item) {
		synchronized (itemBagSlotContains) {
			if (!item.canStack()) {
				return null;
			}

			List<ItemBagSlot> tempSlotList = itemBagSlotContains.getTempSlotList();

			List<ItemBagSlot> slotList = itemBagSlotContains.getSlotList();
			for (ItemBagSlot slot : slotList) {
				if (!slot.isEmpty() && !slot.isFull()) {
					Item slotItem = slot.getItem();
					if (slotItem.equalItem(item)) {
						tempSlotList.add(slot);
					}
				}
			}

			return tempSlotList;
		}
	}

}
