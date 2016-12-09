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

import java.util.Collection;
import java.util.List;

import sophia.foundation.util.ObjectNumberPair;
import sophia.mmorpg.item.Item;

/**
 * 背包-管理
 */
public final class ItemBag {
	final ItemBagSlotContains itemBagSlotList;

	private ItemUseCountRecord countRecord = new ItemUseCountRecord();
	private int isFirstPutItemToBag = 0;
	private final ItemBagPartialRenewalData itemBagPartialRenewalData = new ItemBagPartialRenewalData();

	private final ItemBagPartialRenewalData equipmentSmithPartialRenewalData = new ItemBagPartialRenewalData();

	private static final ItemBagSlotMgr slotMgr = new ItemBagSlotMgr();

	private static final ItemBagItemMgr itemMgr = new ItemBagItemMgr();

	public ItemBag() {
		itemBagSlotList = new ItemBagSlotContains();
	}

	public ItemBag(final List<ItemBagSlot> slotList) {
		itemBagSlotList = new ItemBagSlotContains(slotList);
	}

	public ItemBagSlotContains getItemBagSlotList() {
		return itemBagSlotList;
	}

	/**
	 * 获取一个空的格子
	 */
	public ItemBagSlot getEmptyItemBagSlot() {
		return itemBagSlotList.getEmptyItemBagSlot();
	}

	public ItemBagPartialRenewalData removeItemByItemRefId(String itemRefId, int number, boolean isFirstBinded) {
		if (isFirstBinded)
			itemMgr.removeFirstBindedItemByItemRefId(itemBagSlotList, itemRefId, number, getItemBagPartialRenewalData());
		else
			itemMgr.removeItemByItemRefId(itemBagSlotList, itemRefId, number, getItemBagPartialRenewalData());
		return itemBagPartialRenewalData;
	}

	/**
	 * 移除指定索引的指定数量的物品
	 * 
	 * @param index
	 * @param count
	 * @return
	 */
	public ItemBagPartialRenewalData removeItemBySlot(int index, int count) {
		ItemBagSlot itemBagSlot = itemBagSlotList.getItemBagSlot(index);
		boolean removed = itemMgr.removeItem(itemBagSlot, count);
		if (removed) {
			itemBagPartialRenewalData.setSucceed(true);
			itemBagPartialRenewalData.addItemBagSlot(itemBagSlot);

		}
		return itemBagPartialRenewalData;
	}

	public boolean removeItemBySlot(int index, int count, ItemBagPartialRenewalData itemBagPartialRenewalData) {
		ItemBagSlot itemBagSlot = itemBagSlotList.getItemBagSlot(index);
		boolean removed = itemMgr.removeItem(itemBagSlot, count);
		if (removed) {
			itemBagPartialRenewalData.setSucceed(true);
			itemBagPartialRenewalData.addItemBagSlot(itemBagSlot);

		}
		return removed;
	}

	/**
	 * 移除指定索引的物品
	 * 
	 * @param index
	 * @param timeCount
	 * @return
	 */
	public ItemBagPartialRenewalData removeItemBySlot(int index) {
		ItemBagSlot itemBagSlot = itemBagSlotList.getItemBagSlot(index);
		return removeItemBySlot(index, itemBagSlot.getCrtStackNumber());
	}

	public boolean removeItemBySlot(int index, ItemBagPartialRenewalData itemBagPartialRenewalData) {
		ItemBagSlot itemBagSlot = itemBagSlotList.getItemBagSlot(index);
		return removeItemBySlot(index, itemBagSlot.getCrtStackNumber(), itemBagPartialRenewalData);
	}

	/**
	 * 指定背包索引位置是否有物品
	 */
	public boolean isHaveItem(int index) {
		return itemBagSlotList.getItemBagSlot(index).isEmpty();
	}

	/**
	 * 获取背包索引位置物品
	 */
	public Item getItemBySlot(int index) {
		return itemBagSlotList.getItemBagSlot(index).getItem();
	}

	/**
	 * 获取指定背包格
	 * 
	 */
	public ItemBagSlot getItemSlotByIndex(int index) {
		return itemBagSlotList.getItemBagSlot(index);
	}

	/**
	 * 获取背包的容量
	 * 
	 * @return
	 */
	public int getItemBagCapacity() {
		return itemBagSlotList.getNumberOfSlot();
	}

	/**
	 * 获取背包最大可用容量
	 */
	public short getItemBagMaxCapacity() {
		return itemBagSlotList.getMax_Slot_Number();
	}

	/**
	 * 获取背包集合
	 */
	public List<ItemBagSlot> getItemBagCollection() {
		return itemBagSlotList.getSlotList();
	}

	/**
	 * 获取背包已使用容量
	 * 
	 * @return
	 */
	public int getItemSlotNumber() {

		return getItemBagCapacity() - itemBagSlotList.getEmptySlotNumber();
	}

	/**
	 * 获取空的格子数量
	 */
	public int getEmptySlotNumber() {
		return itemBagSlotList.getEmptySlotNumber();
	}

	/**
	 * 扩包，一格
	 */
	public boolean expendItemBagSlot() {
		return expendItemBagSlot(1);
	}

	/**
	 * 扩包，指定数量
	 * 
	 * @param number
	 *            指定数量
	 */
	public boolean expendItemBagSlot(int number) {
		return slotMgr.expendItemBagSlot(itemBagSlotList, number);
	}

	/**
	 * 整理背包
	 */
	public void arrangeItemBag() {
		slotMgr.arrangeItemBag(itemBagSlotList);
	}

	/**
	 * 合并背包物品
	 */
	public void mergeItems() {
		slotMgr.mergeItems(itemBagSlotList);
	}

	/**
	 * 将物品从一格移放到另外一格
	 * 
	 * @param from
	 *            物品当前的背包格
	 * @param to
	 *            物品移放的背包格
	 */
	public void moveItem(ItemBagSlot from, ItemBagSlot to) {
		itemMgr.moveItem(from, to, getItemBagPartialRenewalData());
	}

	/**
	 * 获取背包中，指定物品引用id的物品数量。包括（绑定|非绑定）
	 * 
	 * @param itemRefId
	 *            指定物品引用id
	 * @return
	 */
	public int getItemNumber(String itemRefId) {
		return itemMgr.getItemNumber(itemBagSlotList, itemRefId);
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
	public int getItemNumber(String itemRefId, boolean isBinded) {
		return itemMgr.getItemNumber(itemBagSlotList, itemRefId, isBinded);
	}

	/**
	 * 背包为空？
	 * 
	 * @return 空，返回true;否则，返回false
	 */
	public boolean isEmpty() {
		return itemMgr.isEmpty(itemBagSlotList);
	}

	/**
	 * 背包满了？
	 * 
	 * @return 满，返回true,否则，返回false
	 */
	public boolean isFull() {
		return itemMgr.isFull(itemBagSlotList);
	}

	/**
	 * 指定的物品集合是否可放入背包。如果可以，返回true,否则返回false。
	 * 
	 * @param items
	 *            指定的物品集合
	 * @return 指定的物品集合是否可放入背包。如果可以，返回true,否则返回false。
	 */
	public boolean canPut(Collection<Item> items) {
		return itemMgr.canPut(itemBagSlotList, items);
	}

	/**
	 * 
	 * @param item
	 * @param propertyMap
	 * @return
	 */
	public boolean putItemIfAbsent(Item item) {
		synchronized (ItemBagItemMgr.class) {
			if (itemMgr.canPut(itemBagSlotList, item)) {
				itemMgr.putItem(itemBagSlotList, item, getItemBagPartialRenewalData());
				return true;
			} else {
				return false;
			}
		}

	}

	public boolean putItem(Item item, ItemBagPartialRenewalData itemBagPartialRenewalData) {
		synchronized (ItemBagItemMgr.class) {
			if (itemMgr.canPut(itemBagSlotList, item)) {
				itemMgr.putItem(itemBagSlotList, item, itemBagPartialRenewalData);
				return true;
			} else {
				return false;
			}
		}
	}

	/**
	 * 放入指定物品到指定格子
	 * 
	 * @param item
	 * @param index
	 * @return
	 */

	public boolean putItemBySlot(Item item, int index) {
		ItemBagSlot slot = itemBagSlotList.getItemBagSlot(index);
		if (slot.isEmpty()) {
			slot.setItem(item);
			return true;
		}
		return false;
	}

	/**
	 * 放入指定的物品到背包。如果可以，将全部放入物品，返回true,否则，不放入任何物品，返回false。
	 * 
	 * @param items
	 *            指定的放入物品
	 * @param propertyMap
	 * @return 放入指定的物品到背包。如果可以，将全部放入物品，返回true,否则，不放入任何物品，返回false。
	 */
	public boolean putItemsIfAbsent(Collection<Item> items, ItemBagPartialRenewalData itemBagPartialRenewalData) {

		return itemMgr.putItemsIfAbsent(itemBagSlotList, items, itemBagPartialRenewalData);

	}

	/**
	 * 指定数量的物品背包是否足够获取.如果足够，返回true;否则，返回false;
	 * 
	 * @param itemRefIdNumberPair
	 *            指定数量的物品
	 * @return
	 */
	public boolean canTake(ObjectNumberPair<String> itemRefIdNumberPair) {
		return itemMgr.canTake(itemBagSlotList, itemRefIdNumberPair);
	}

	/**
	 * 获取指定数量的物品.如果足够，获取，返回true;否则，返回false.
	 * 
	 * @return
	 */
	public boolean takeItemIfEnough(ObjectNumberPair<String> itemRefIdNumberPair) {
		return itemMgr.takeItemIfEnough(itemBagSlotList, itemRefIdNumberPair);
	}

	/**
	 * 指定数量的物品背包是否足够获取.如果足够，返回true;否则，返回false;
	 * 
	 * @param collection
	 * @return
	 */
	public boolean canTake(Collection<ObjectNumberPair<String>> collection) {
		return itemMgr.canTake(itemBagSlotList, collection);
	}

	/**
	 * 获取指定数量的物品.如果足够，获取，返回true;否则，返回false.
	 * 
	 * @param collection
	 * @return
	 */
	public boolean takeItemsIfEnough(Collection<ObjectNumberPair<String>> collection) {
		return itemMgr.takeItemsIfEnough(itemBagSlotList, collection);
	}

	public final ItemBagPartialRenewalData getItemBagPartialRenewalData() {
		itemBagPartialRenewalData.reset();
		return itemBagPartialRenewalData;
	}

	public final ItemBagPartialRenewalData getEquipmentSmithPartialRenewalData() {
		equipmentSmithPartialRenewalData.reset();
		return equipmentSmithPartialRenewalData;
	}

	public final ItemBagPartialRenewalData getNoResetItemBagPartialRenewalData() {
		return itemBagPartialRenewalData;
	}

	/**
	 * 根据物品id查找itemBagSlot
	 * 
	 * @param id
	 * @return
	 */
	public ItemBagSlot getItemSlot(String id) {
		return itemMgr.findItemById(itemBagSlotList, id);
	}

	/**
	 * 根据物品id删除物品
	 * 
	 * @param id
	 * @param number
	 * @return
	 */
	public boolean removeItemById(String id, int number) {
		return itemMgr.removeItemById(itemBagSlotList, id, number);
	}

	public boolean getIsFirstPutItemToBag() {
		return isFirstPutItemToBag == 0;
	}

	public int getIsFirstPutItemToBagValue() {
		return isFirstPutItemToBag;
	}

	public void setIsFirstPutItemToBag(int isFirstPutItemToBag) {
		this.isFirstPutItemToBag = isFirstPutItemToBag;
	}

	public ItemUseCountRecord getCountRecord() {
		return countRecord;
	}

	public void setCountRecord(ItemUseCountRecord countRecord) {
		this.countRecord = countRecord;
	}

}
