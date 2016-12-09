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
import java.util.List;

import sophia.foundation.util.Position;
import sophia.game.GameRoot;
import sophia.game.ref.GameRefObject;
import sophia.mmorpg.GameObjectFactory;
import sophia.mmorpg.base.scene.GameScene;
import sophia.mmorpg.code.MMORPGErrorCode;
import sophia.mmorpg.item.Item;
import sophia.mmorpg.item.ref.ItemRef;
import sophia.mmorpg.item.ref.UnPropsItemRef;
import sophia.mmorpg.loot.Loot;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;
import sophia.mmorpg.utils.RuntimeResult;

import com.google.common.base.Preconditions;

public final class ItemFacade {

	/**
	 * drop Item
	 * 
	 * @param player
	 * @param rate
	 */
	public static List<Loot> dropItem(Player player, Player owner, int rate, int dropNumber, byte source) {
		return player.getItemBagComponent().dropItem(rate, owner, dropNumber, source);
	}

	/**
	 * 
	 * @param player
	 *            掉落玩家
	 * @param owner
	 *            掉落物拥有者
	 * @param number
	 *            数量
	 * @param itemRefId
	 *            物品refid
	 */
	public static List<Loot> dropItem(Player player, Player owner, ItemPair itemPair, byte source) {
		return player.getItemBagComponent().dropItem(player, owner, itemPair, source);
	}
	
	public static List<Loot> dropItem(Player player, Player owner, ItemPair itemPair, GameScene dstScene, Position dstPos, byte source) {
		return player.getItemBagComponent().dropItem(owner, itemPair, dstScene, dstPos, source);
	}

	public static RuntimeResult addItemCompareSlot(Player player, Collection<ItemPair> itemPairs, byte source) {
		synchronized (player) {
			if (isItemBagSlotEnough(player, itemPairs)) {
				return addItem(player, itemPairs, source);
			}
		}
		return RuntimeResult.RuntimeApplicationError(MMORPGErrorCode.CODE_ITEM_SLOT_ENOUGH);
	}

	public static RuntimeResult addItem(Player player, ItemPair itemPair, byte source) {
		Collection<ItemPair> itemPairs = new ArrayList<ItemPair>();
		itemPairs.add(itemPair);
		return addItem(player, itemPairs, source);
	}

	public static RuntimeResult addItem(Player player, Collection<ItemPair> itemPairs, byte source) {
		RuntimeResult ret = RuntimeResult.OK();
		if (itemPairs == null) {
			return ret;
		}
		Collection<Item> items = new ArrayList<Item>();
		Collection<ItemPair> unPropsItems = new ArrayList<ItemPair>();
		for (ItemPair itemPair : itemPairs) {
			String itemRefId = itemPair.getItemRefId();
			Preconditions.checkArgument(itemRefId != null, "itemRefId 为 null");
			int number = itemPair.getNumber();
			byte bindStatus = 0;
			GameRefObject itemRef = GameRoot.getGameRefObjectManager().getManagedObject(itemRefId);
			if (itemRef == null) {
				continue;
			}
			if (itemRef instanceof ItemRef) {
				byte bindType = MGPropertyAccesser.getBindType(itemRef.getProperty());
				if (bindType == Item.At_Once_Bind || itemPair.isBindStatus())
					bindStatus = 1;
				Item item = player.getItemBagComponent().getItemByItemRefId(itemRefId, number, bindStatus);
				if (item != null) {
					items.add(item);
				}
			} else if (itemRef instanceof UnPropsItemRef) {
				unPropsItems.add(itemPair);
			}

		}
		if (items.size() != 0) {
			ret = player.getItemBagComponent().addEventNotify(items, source);
		}
		if (ret.isOK()) {
			addUnPropsItem(player, unPropsItems, source);
		}
		return ret;
	}

	public static RuntimeResult addItems(Player player, Item item, byte source) {
		RuntimeResult ret = RuntimeResult.OK();
		List<Item> items = new ArrayList<Item>();
		items.add(item);
		ret = player.getItemBagComponent().addEventNotify(items, source);
		return ret;
	}

	public static RuntimeResult addItems(Player player, Collection<Item> items, byte source) {
		RuntimeResult ret = RuntimeResult.OK();
		ret = player.getItemBagComponent().addEventNotify(items, source);
		return ret;
	}

	private static void addUnPropsItem(Player player, Collection<ItemPair> unPropsItems, byte source) {
		for (ItemPair itemPair : unPropsItems) {

			int number = itemPair.getNumber();
			if (itemPair.isGold()) {
				player.getPlayerMoneyComponent().addGold(number, source);
			} else if (itemPair.isUnBindedGold()) {
				player.getPlayerMoneyComponent().addUnbindGold(number, source);
			} else if (itemPair.isBindedGold()) {
				player.getPlayerMoneyComponent().addBindGold(number, source);
			} else if (itemPair.isExp()) {
				player.getExpComponent().addExp(number);
			} else if (itemPair.isMerit()) {
				player.getItemBagComponent().getItemBagPutItemRuntime().putMerit(player, number);
			} else if (itemPair.isAchievement()) {
				player.getItemBagComponent().getItemBagPutItemRuntime().putAchievement(player, number);
			}
		}
	}

	public static boolean removeItem(Player player, Collection<ItemPair> itemPairs, byte source) {

		for (ItemPair itemPair : itemPairs) {
			String itemRefId = itemPair.getItemRefId();
			int number = itemPair.getNumber();
			if (!player.getItemBagComponent().removeEventNotify(itemRefId, number, false, source))
				continue;
		}
		return false;
	}

	/**
	 * 通过物品id删除物品
	 * 
	 * @param player
	 * @param id
	 * @param number
	 * @return
	 */
	public static boolean removeItemById(Player player, String id, int number, byte source) {

		return player.getItemBagComponent().removeByItemIdEventNotify(id, number, source);
	}

	/**
	 * 通过refId删除物品
	 * 
	 * @param player
	 * @param itemRefId
	 * @param number
	 * @return
	 */
	public static boolean removeItem(Player player, String itemRefId, int number, boolean isFirstBinded, byte source) {

		return player.getItemBagComponent().removeEventNotify(itemRefId, number, isFirstBinded, source);
	}

	/**
	 * 使用指定数量物品
	 * 
	 * @param itemRefId
	 * @param number
	 * @return
	 */
	public static RuntimeResult useItem(Player player, String itemRefId, int number, byte source) {
		PlayerItemBagComponent itemBagComponent = player.getItemBagComponent();
		ItemBag itemBag = itemBagComponent.getItemBag();
		int curNumber = itemBag.getItemNumber(itemRefId);
		if (number > curNumber) {// 使用数量是否超出已有数量
			return RuntimeResult.RuntimeApplicationError(MMORPGErrorCode.CODE_ITEM_NOENOUGH);
		}

		Item useItem = GameObjectFactory.getItem(itemRefId);
		int result = itemBagComponent.canUse(player, useItem, number);
		if (result == 1) {
			RuntimeResult runtimeResult = itemBagComponent.getUseableItemRuntime().useTo(player, useItem, number);
			itemBagComponent.removeEventNotify(itemRefId, number, false, source);

			return runtimeResult;
		} else {
			return RuntimeResult.RuntimeApplicationError(result);
		}

	}

	/**
	 * 获取背包中，指定物品引用id的物品数量。(包括绑定 | 不绑定)
	 * 
	 * @param itemRefId
	 *            指定物品引用id
	 * @return
	 */
	public static int getNumber(Player player, String itemRefId) {
		return player.getItemBagComponent().getItemBag().getItemNumber(itemRefId);
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
	public static int getNumber(Player player, String itemRefId, boolean isBinded) {
		return player.getItemBagComponent().getItemBag().getItemNumber(itemRefId, isBinded);
	}

	/**
	 * 通过物品id拿到物品Item
	 * 
	 * @param player
	 * @param id
	 * @return
	 */
	public static Item getItemById(Player player, String id) {
		ItemBagSlot itemSlot = player.getItemBagComponent().getItemBag().getItemSlot(id);
		if (itemSlot == null) {
			return null;
		}
		return itemSlot.getItem();
	}
	
	/**
	 * 指定的物品引用id 的物品数量是否足够
	 * @param player
	 * @param itemRefId
	 * @param number
	 * @param isBinded
	 * 			 是否绑定。true 绑定物品；false 非绑定物品
	 * @return
	 */
	public static boolean isEnoughItem(Player player, String itemRefId, int number, boolean isBinded) {
		return getNumber(player, itemRefId, isBinded) >= number;
	}
	
	/**
	 * 指定的物品引用id 的物品数量是否足够(包括绑定 | 不绑定)
	 * @param player
	 * @param itemRefId
	 * @param number
	 * @return
	 */
	public static boolean isEnoughItem(Player player, String itemRefId, int number) {
		return getNumber(player, itemRefId) >= number;
	}

	/**
	 * 获取背包剩余容量数
	 * 
	 * @param player
	 * @return
	 */
	public static int getFreeCapacity(Player player) {
		return player.getItemBagComponent().getItemBag().getItemBagCapacity() - player.getItemBagComponent().getItemBag().getItemSlotNumber();
	}

	public static Collection<Item> getRealItemSlotNumber(Player player, Collection<ItemPair> itemPairs) {
		Collection<Item> itemAll = new ArrayList<Item>();
		for (ItemPair itemPair : itemPairs) {
			String itemRefId = itemPair.getItemRefId();
			int number = itemPair.getNumber();
			byte bindStatus = 0;
			byte bindType = MGPropertyAccesser.getBindType(((ItemRef) GameRoot.getGameRefObjectManager().getManagedObject(itemRefId)).getProperty());
			if (bindType == Item.At_Once_Bind || itemPair.isBindStatus())
				bindStatus = 1;
			itemAll.add(player.getItemBagComponent().getItemByItemRefId(itemRefId, number, bindStatus));

		}
		return itemAll;
	}

	public static boolean isItemBagSlotEnough(Player player, Collection<ItemPair> itemPairs) {
		int count = 0;
		for (ItemPair itemPair : itemPairs) {
			if (!itemPair.isUnPropsItem()) {
				count++;
			}
		}
		return ItemFacade.getFreeCapacity(player) >= count;
	}
}
