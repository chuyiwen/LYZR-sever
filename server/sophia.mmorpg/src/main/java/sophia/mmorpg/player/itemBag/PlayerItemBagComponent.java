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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeSet;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import sophia.foundation.authentication.Identity;
import sophia.foundation.communication.core.ActionEventBase;
import sophia.foundation.communication.core.MessageFactory;
import sophia.foundation.data.PersistenceObject;
import sophia.foundation.util.Position;
import sophia.game.GameRoot;
import sophia.game.component.communication.GameEvent;
import sophia.mmorpg.GameObjectFactory;
import sophia.mmorpg.MMORPGContext;
import sophia.mmorpg.base.ConcreteComponent;
import sophia.mmorpg.base.scene.GameScene;
import sophia.mmorpg.base.scene.mgr.LootMgrComponent;
import sophia.mmorpg.code.MMORPGErrorCode;
import sophia.mmorpg.core.CDMgr;
import sophia.mmorpg.core.timer.SFTimeChimeListener;
import sophia.mmorpg.core.timer.SFTimer;
import sophia.mmorpg.core.timer.SFTimerCreater;
import sophia.mmorpg.equipmentSmith.smith.highestEquipment.HightestEquipmentFacade;
import sophia.mmorpg.event.ResultEvent;
import sophia.mmorpg.item.Item;
import sophia.mmorpg.loot.Loot;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.PlayerManager;
import sophia.mmorpg.player.chat.sysytem.SystemPromptFacade;
import sophia.mmorpg.player.equipment.PlayerEquipBodyArea;
import sophia.mmorpg.player.equipment.event.EquipmentEventDefines;
import sophia.mmorpg.player.equipment.event.G2C_Equip_Info;
import sophia.mmorpg.player.exp.gameevent.PlayerLevelUp_GE;
import sophia.mmorpg.player.itemBag.event.C2G_Bag_Capacity;
import sophia.mmorpg.player.itemBag.event.C2G_Item_Batch_Sell;
import sophia.mmorpg.player.itemBag.event.C2G_Item_Info;
import sophia.mmorpg.player.itemBag.event.C2G_Item_List;
import sophia.mmorpg.player.itemBag.event.C2G_Item_Sell;
import sophia.mmorpg.player.itemBag.event.C2G_Item_SoltUnLock;
import sophia.mmorpg.player.itemBag.event.C2G_Item_Use;
import sophia.mmorpg.player.itemBag.event.G2C_Bag_Capacity;
import sophia.mmorpg.player.itemBag.event.G2C_Item_Info;
import sophia.mmorpg.player.itemBag.event.G2C_Item_List;
import sophia.mmorpg.player.itemBag.event.G2C_Item_SoltUnLock;
import sophia.mmorpg.player.itemBag.event.G2C_Item_Update;
import sophia.mmorpg.player.itemBag.event.ItemBagEventDefines;
import sophia.mmorpg.player.itemBag.gameEvent.FirstTimeAddItem_GE;
import sophia.mmorpg.player.itemBag.gameEvent.QuestItemChange_GE;
import sophia.mmorpg.player.itemBag.gameEvent.RemoveItem_GE;
import sophia.mmorpg.player.useableItem.UseableItemRuntime;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;
import sophia.mmorpg.stat.StatFunctions;
import sophia.mmorpg.stat.logs.StatItemBag;
import sophia.mmorpg.utils.RuntimeResult;
import sophia.mmorpg.utils.SFRandomUtils;

import com.google.common.base.Preconditions;

/**
 * 玩家-背包组件
 */
public class PlayerItemBagComponent extends ConcreteComponent<Player> {
	private static final Logger logger = Logger.getLogger(PlayerItemBagComponent.class);
	CDMgr cdMgr = new CDMgr(500);
	private ItemBag itemBag = new ItemBag();
	private SFTimer timer;
	private static final int CAN_USE = 1;
	public static final int DEFAULT_ITEMBAGSLOT_COUNT = 50;
	private PersistenceObject persisteneceObject;
	private UseableItemRuntime useableItemRuntime;
	private ItemBagPutItemRuntime itemBagPutItemRuntime;
	public static final String QuestItemChange_GE_Id = QuestItemChange_GE.class.getSimpleName();
	private static final String FirstTimeAddItem_GE_Id = FirstTimeAddItem_GE.class.getSimpleName();
	private static final String PlayerLevelUp_GE_Id = PlayerLevelUp_GE.class.getSimpleName();

	public PlayerItemBagComponent() {
	}

	public final ItemBag getItemBag() {
		return itemBag;
	}

	public void setItemBag(ItemBag itemBag) {
		this.itemBag = itemBag;
	}

	@Override
	public void ready() {
		addActionEventListener(ItemBagEventDefines.C2G_Item_List);
		addActionEventListener(ItemBagEventDefines.C2G_Bag_Capacity);
		addActionEventListener(ItemBagEventDefines.C2G_Item_Use);
		addActionEventListener(ItemBagEventDefines.C2G_Item_Sell);
		addActionEventListener(ItemBagEventDefines.C2G_Item_SoltUnLock);
		addActionEventListener(ItemBagEventDefines.C2G_Item_Modify);
		addActionEventListener(ItemBagEventDefines.C2G_Item_Info);
		addActionEventListener(ItemBagEventDefines.C2G_Item_Batch_Sell);
		addInterGameEventListener(PlayerLevelUp_GE_Id);
		if (itemBag.getItemBagCapacity() < itemBag.getItemBagMaxCapacity()) {
			listenOnlineTimeOpenSlot();
		}
	}

	@Override
	public void suspend() {
		removeActionEventListener(ItemBagEventDefines.C2G_Item_List);
		removeActionEventListener(ItemBagEventDefines.C2G_Bag_Capacity);
		removeActionEventListener(ItemBagEventDefines.C2G_Item_Use);
		removeActionEventListener(ItemBagEventDefines.C2G_Item_Sell);
		removeActionEventListener(ItemBagEventDefines.C2G_Item_SoltUnLock);
		removeActionEventListener(ItemBagEventDefines.C2G_Item_Modify);
		removeActionEventListener(ItemBagEventDefines.C2G_Item_Info);
		removeActionEventListener(ItemBagEventDefines.C2G_Item_Batch_Sell);
		removeInterGameEventListener(PlayerLevelUp_GE_Id);
		if (timer != null) {
			timer.cancel();
		}
	}

	public void setPersisteneceObject(PersistenceObject persisteneceObject) {
		this.persisteneceObject = persisteneceObject;
	}

	public PersistenceObject getPersisteneceObject() {
		return persisteneceObject;
	}

	@Override
	public void handleGameEvent(GameEvent<?> event) {
		if (event.isId(PlayerLevelUp_GE_Id)) {
			ItemBagPartialRenewalData itemBagPartialRenewalData = itemBag.getItemBagPartialRenewalData();
			List<ItemBagSlot> equipmentSlotList = itemBag.getItemBagCollection();
			for (ItemBagSlot slot : equipmentSlotList) {
				if (!slot.isEmpty()) {
					Item equipment = slot.getItem();
					if (equipment.getItemType() == 1) {
						int fightValue = getConcreteParent().getFightPower(equipment.getProperty());
						MGPropertyAccesser.setOrPutFightValue(equipment.getProperty(), fightValue);
						itemBagPartialRenewalData.getPartialRenewalIndexs().add(slot);
					}
				}
			}
			if (itemBagPartialRenewalData.getPartialRenewalIndexs().size() > 0) {
				G2C_Item_Update res = MessageFactory.getConcreteMessage(ItemBagEventDefines.G2C_Item_Update);
				res.setOptype(ItemCode.MODIFY_UPDATE);
				res.setItemBagPartialRenewalData(itemBagPartialRenewalData);
				res.setItemBag(itemBag);
				res.setFightValueUpdate(2);
				res.setPlayer(getConcreteParent());
				GameRoot.sendMessage(getConcreteParent().getIdentity(), res);
			}
		}
	}

	@Override
	public void handleActionEvent(ActionEventBase event) {
		short actionEventId = event.getActionEventId();
		Identity identity = event.getIdentity();

		switch (actionEventId) {
		case ItemBagEventDefines.C2G_Bag_Capacity:
			handle_Bag_Capacity((C2G_Bag_Capacity) event, actionEventId, identity);
			break;
		case ItemBagEventDefines.C2G_Item_List:
			handle_Item_List((C2G_Item_List) event, actionEventId, identity);
			break;
		case ItemBagEventDefines.C2G_Item_Use:
			handle_Item_Use((C2G_Item_Use) event, actionEventId, identity);
			break;
		case ItemBagEventDefines.C2G_Item_Sell:
			handle_Item_Sell((C2G_Item_Sell) event, actionEventId, identity);
			break;
		case ItemBagEventDefines.C2G_Item_Batch_Sell:
			handle_Item_BatchSell((C2G_Item_Batch_Sell) event, actionEventId, identity);
			break;
		case ItemBagEventDefines.C2G_Item_SoltUnLock:
			handle_Item_SoltUnLock((C2G_Item_SoltUnLock) event, actionEventId, identity);
			break;
		case ItemBagEventDefines.C2G_Item_Info:
			handle_Item_Info((C2G_Item_Info) event, actionEventId, identity);
			break;

		default:
			break;
		}

		super.handleActionEvent(event);
	}

	/** 查看物品 */
	private void handle_Item_Info(C2G_Item_Info event, short actionEventId, Identity identity) {
		if (logger.isDebugEnabled()) {
			logger.debug("Item_Info");
		}
		PlayerManager playerManager = MMORPGContext.getPlayerComponent().getPlayerManager();
		Player player = playerManager.getOnlinePlayer(event.getPlayerId());
		if (player == null) {
			if (logger.isDebugEnabled()) {
				logger.debug("handle_Item_Info error, player is null, playerId=" + event.getPlayerId());
			}
			ResultEvent.sendResult(event.getIdentity(), event.getActionEventId(), MMORPGErrorCode.CODE_CHAT_USER_NOT_ONLIE);
			return;
		}

		ItemBagSlot itemSlot = player.getItemBagComponent().getItemBag().getItemSlot(event.getItemId());
		if (itemSlot == null || itemSlot.getItem() == null) {
			PlayerEquipBodyArea bodyArea = player.getPlayerEquipBodyConponent().getPlayerBody().getBodyAreaEquip(event.getItemId());
			if (bodyArea != null) {
				G2C_Equip_Info res = MessageFactory.getConcreteMessage(EquipmentEventDefines.G2C_Equip_Info);
				byte position = 0;
				Item equipItem = null;
				if (bodyArea.isLeftRightBodyArea()) {
					equipItem = bodyArea.getEquipment(PlayerEquipBodyArea.Right_Position);
					if (equipItem != null && StringUtils.equals(equipItem.getId(), event.getItemId())) {
						position = 1;
					} else {
						equipItem = bodyArea.getEquipment(PlayerEquipBodyArea.Left_Position);
					}
				} else {
					equipItem = bodyArea.getEquipment();
				}

				res.setBodyId(bodyArea.getId());
				res.setPosition(position);
				res.setPlayer(player);
				res.setItem(equipItem);
				GameRoot.sendMessage(identity, res);
				return;
			} else {
				ResultEvent.sendResult(identity, actionEventId, MMORPGErrorCode.CODE_ITEM_CNAT_SEE);
				return;
			}
		}
		G2C_Item_Info res = MessageFactory.getConcreteMessage(ItemBagEventDefines.G2C_Item_Info);
		res.setPlayer(player);
		res.setSlot(itemSlot);
		GameRoot.sendMessage(identity, res);

	}

	/** 获取容量 */
	private void handle_Bag_Capacity(C2G_Bag_Capacity event, short actionEventId, Identity identity) {

		if (logger.isDebugEnabled()) {
			logger.debug("Bag_Capacity");
		}
		G2C_Bag_Capacity res = MessageFactory.getConcreteMessage(ItemBagEventDefines.G2C_Bag_Capacity);

		res.setItemBag(itemBag);

		GameRoot.sendMessage(identity, res);

	}

	/** 获取物品列表 */
	private void handle_Item_List(C2G_Item_List event, short actionEventId, Identity identity) {
		if (logger.isDebugEnabled()) {
			logger.debug("Item_List");
		}
		G2C_Item_List res = MessageFactory.getConcreteMessage(ItemBagEventDefines.G2C_Item_List);

		res.setItemBag(itemBag);
		res.setPlayer(getConcreteParent());
		GameRoot.sendMessage(identity, res);

	}

	/** 使用物品 */

	private void handle_Item_Use(C2G_Item_Use event, short actionEventId, Identity identity) {

		if (logger.isDebugEnabled()) {
			logger.debug("Use Item");
		}
		C2G_Item_Use gmEvent = (C2G_Item_Use) event;

		short gridId = gmEvent.getGridId();
		short number = gmEvent.getNumber();

		if (gridId < 0 || gridId >= itemBag.getItemBagCapacity()) { // 格索引是否超出范围
			ResultEvent.sendResult(identity, actionEventId, MMORPGErrorCode.CODE_ITEM_USERSLOT);
			if (logger.isDebugEnabled()) {
				logger.debug("Illegal Index ");
			}
			return;
		}
		Item useItem = itemBag.getItemBySlot(gridId);
		if (useItem == null) {
			if (logger.isDebugEnabled()) {
				logger.debug("Item not exist");
			}
			ResultEvent.sendResult(identity, actionEventId, MMORPGErrorCode.CODE_ITEM_NOTEXIEST);
			return;
		} else if (number > useItem.getNumber()) {// 使用数量是否超出已有数量
			if (logger.isDebugEnabled()) {
				logger.debug("NOT Enough Item");
			}
			ResultEvent.sendResult(identity, actionEventId, MMORPGErrorCode.CODE_ITEM_NOENOUGH);
			return;
		} else if (number <= 0) {
			ResultEvent.sendResult(identity, actionEventId, MMORPGErrorCode.CODE_ITEM_NUMBERWORNG);
			return;
		}

		int CDTime = MGPropertyAccesser.getCDTime(useItem.getItemRef().getEffectProperty()) < 0 ? 0 : MGPropertyAccesser.getCDTime(useItem.getItemRef().getEffectProperty());
		byte itemCDGroup = MGPropertyAccesser.getItemCDGroup(useItem.getItemRef().getProperty());
		String refId = useItem.getItemRefId();
		if (itemCDGroup != -1)
			refId = itemCDGroup + "";
		if (isCDTime(refId, CDTime)) {
			ResultEvent.sendResult(identity, actionEventId, MMORPGErrorCode.CODE_ITEM_IN_CD);
			return;
		} else
			cdMgr.update(refId);

		Player player = getConcreteParent();
		int result = canUse(player, useItem, number);
		if (result == CAN_USE) {
			RuntimeResult runtimeResult = useableItemRuntime.useTo(player, useItem, number);
			if (runtimeResult.isOK()) {

				StatFunctions.ItemBagStat(getConcreteParent(), StatItemBag.Use, useItem.getItemRefId(), number, ItemOptSource.ItemBagUse);
				SystemPromptFacade.sendUseItemTips(player, useItem.getName());
				removeEventNotify(gridId, number, ItemOptSource.ItemBagUse);

			} else {
				if (runtimeResult.getCode() != 2) {
					ResultEvent.sendResult(identity, actionEventId, runtimeResult.getApplicationCode());
				}

			}
		} else {
			ResultEvent.sendResult(identity, actionEventId, result);
			return;
		}

	}

	/** 出售物品 */
	private void handle_Item_Sell(C2G_Item_Sell event, short actionEventId, Identity identity) {

		if (logger.isDebugEnabled()) {
			logger.debug("Item_Sell");
		}
		short gridId = event.getGridId();
		int number = event.getNumber();

		if (number <= 0) {
			ResultEvent.sendResult(identity, actionEventId, MMORPGErrorCode.CODE_ITEM_NUMBERWORNG);
			return;
		}
		if (gridId < 0 || gridId >= itemBag.getItemBagCapacity()) { // 格索引是否超出范围
			ResultEvent.sendResult(identity, actionEventId, MMORPGErrorCode.CODE_ITEM_USERSLOT);
			if (logger.isDebugEnabled()) {
				logger.debug("Illegal Index ");
			}
			return;
		}
		Item beforeSaleItem = itemBag.getItemBySlot(gridId);
		if (beforeSaleItem == null) {
			if (logger.isDebugEnabled()) {
				logger.debug("Item not exist");
			}
			ResultEvent.sendResult(identity, actionEventId, MMORPGErrorCode.CODE_ITEM_NOTEXIEST);
			return;
		}
		if (number > beforeSaleItem.getNumber()) {// 使用数量是否超出已有数量
			if (logger.isDebugEnabled()) {
				logger.debug("NOT Enough Item");
			}
			ResultEvent.sendResult(identity, actionEventId, MMORPGErrorCode.CODE_ITEM_NOENOUGH);
			return;
		}

		int curNumber = beforeSaleItem.getNumber();
		int salePrice = beforeSaleItem.getSalePrice() * number;
		if (!sellItem(gridId, number, identity, actionEventId)) {
			return;
		}
		getConcreteParent().getPlayerMoneyComponent().addGold(salePrice, ItemOptSource.ItemBagSell);
		byte optype = ItemCode.DEL_UPDATE;
		if (curNumber <= number)
			optype = ItemCode.DEL_UPDATE;
		else if (curNumber > number)
			optype = ItemCode.MODIFY_UPDATE;

		ItemBagPartialRenewalData itemBagPartialRenewalData = itemBag.getNoResetItemBagPartialRenewalData();
		G2C_Item_Update res = MessageFactory.getConcreteMessage(ItemBagEventDefines.G2C_Item_Update);
		res.setOptype(optype);
		res.setItemBagPartialRenewalData(itemBagPartialRenewalData);
		res.setItemBag(itemBag);
		res.setPlayer(getConcreteParent());
		GameRoot.sendMessage(identity, res);
	}

	/** 批量出售物品 */
	private void handle_Item_BatchSell(C2G_Item_Batch_Sell event, short actionEventId, Identity identity) {
		Map<Short, Short> map = event.getSellMaps();
		Preconditions.checkArgument(map != null);
		short gridId = 0;
		int number = 0;
		Map<Short, Short> deleteMap = new HashMap<Short, Short>();
		Map<Short, Short> updateMap = new HashMap<Short, Short>();
		for (Entry<Short, Short> entry : map.entrySet()) {
			gridId = entry.getKey();
			number = entry.getValue();
			if (gridId < 0 || gridId >= itemBag.getItemBagCapacity()) { // 格索引是否超出范围
				ResultEvent.sendResult(identity, actionEventId, MMORPGErrorCode.CODE_ITEM_USERSLOT);
				if (logger.isDebugEnabled()) {
					logger.debug("Illegal Index ");
				}
				return;
			}
			if (number <= 0) {
				if (logger.isDebugEnabled()) {
					logger.debug("Item number is <= 0");
				}
				continue;
			}
			Item beforeSaleItem = itemBag.getItemBySlot(gridId);
			if (beforeSaleItem == null) {
				continue;
			}
			if (number >= beforeSaleItem.getNumber()) {
				deleteMap.put(gridId, (short) beforeSaleItem.getNumber());
			} else {
				updateMap.put(gridId, (short) number);
			}
		}
		int saletotal = 0;
		saletotal += sellItem(deleteMap, identity, actionEventId);

		ItemBagPartialRenewalData itemBagPartialRenewalData = itemBag.getNoResetItemBagPartialRenewalData();
		G2C_Item_Update res = MessageFactory.getConcreteMessage(ItemBagEventDefines.G2C_Item_Update);
		res.setOptype(ItemCode.DEL_UPDATE);
		res.setItemBagPartialRenewalData(itemBagPartialRenewalData);
		res.setItemBag(itemBag);
		res.setPlayer(getConcreteParent());
		GameRoot.sendMessage(identity, res);

		saletotal += sellItem(updateMap, identity, actionEventId);
		itemBagPartialRenewalData = itemBag.getNoResetItemBagPartialRenewalData();
		res = MessageFactory.getConcreteMessage(ItemBagEventDefines.G2C_Item_Update);
		res.setOptype(ItemCode.MODIFY_UPDATE);
		res.setItemBagPartialRenewalData(itemBagPartialRenewalData);
		res.setItemBag(itemBag);
		res.setPlayer(getConcreteParent());
		GameRoot.sendMessage(identity, res);

		getConcreteParent().getPlayerMoneyComponent().addGold(saletotal, ItemOptSource.ItemBagSell);
	}

	/** 解锁格 */
	private void handle_Item_SoltUnLock(C2G_Item_SoltUnLock event, short actionEventId, Identity identity) {

		if (logger.isDebugEnabled()) {
			logger.debug("Open new Slot");
		}
		int crtMaxSlot = (itemBag.getItemBagCapacity() - DEFAULT_ITEMBAGSLOT_COUNT) + 1;
		long crtLoginTime = System.currentTimeMillis() - MGPropertyAccesser.getLastLoginTime(getConcreteParent().getProperty());
		long onlineTime = (MGPropertyAccesser.getOnlineTime(getConcreteParent().getProperty()) + crtLoginTime) / 1000;
		long openTime = ItemSomeConfigData.openSlotTimeMap.get(crtMaxSlot);
		int remainMins = 0;
		if (onlineTime < openTime) {

			remainMins = (int) (openTime - onlineTime) / 60 + 1;
			G2C_Item_SoltUnLock res = MessageFactory.getConcreteMessage(ItemBagEventDefines.G2C_Item_SoltUnLock);
			res.setRemainMins(remainMins);
			GameRoot.sendMessage(identity, res);

		} else {

			itemBag.expendItemBagSlot();
			G2C_Bag_Capacity res = MessageFactory.getConcreteMessage(ItemBagEventDefines.G2C_Bag_Capacity);
			res.setItemBag(itemBag);
			GameRoot.sendMessage(identity, res);
		}

	}

	private int sellItem(Map<Short, Short> map, Identity identity, short actionEventId) {
		int saleTotal = 0;
		Item item = null;
		for (Entry<Short, Short> entry : map.entrySet()) {
			short gridId = entry.getKey();
			int number = entry.getValue();
			if (gridId < 0 || gridId >= itemBag.getItemBagCapacity()) { // 格索引是否超出范围
				ResultEvent.sendResult(identity, actionEventId, MMORPGErrorCode.CODE_ITEM_USERSLOT);
				if (logger.isDebugEnabled()) {
					logger.debug("Illegal Index ");
				}
				return 0;
			}
			item = itemBag.getItemBySlot(gridId);
			int salePrice = item.getSalePrice(); // 出售价格
			if (sellItem(gridId, number, identity, actionEventId)) {
				saleTotal += salePrice * number;
			}

		}
		return saleTotal;
	}

	private boolean sellItem(short gridId, int number, Identity identity, short actionEventId) {
		if (number <= 0) {
			if (logger.isDebugEnabled()) {
				logger.debug("number < 0 ");
			}
			ResultEvent.sendResult(identity, actionEventId, MMORPGErrorCode.CODE_ITEM_NUMBERWORNG);
			return false;
		}
		if (gridId < 0 || gridId >= itemBag.getItemBagCapacity()) { // 格索引是否超出范围
			ResultEvent.sendResult(identity, actionEventId, MMORPGErrorCode.CODE_ITEM_USERSLOT);
			if (logger.isDebugEnabled()) {
				logger.debug("Illegal Index ");
			}
			return false;
		}
		Item beforeSaleItem = itemBag.getItemBySlot(gridId);
		if (beforeSaleItem == null) {
			if (logger.isDebugEnabled()) {
				logger.debug("Item not exist");
			}
			ResultEvent.sendResult(identity, actionEventId, MMORPGErrorCode.CODE_ITEM_NOTEXIEST);
			return false;
		}
		boolean canSale = beforeSaleItem.canSale(); // 该物品是否可以出售

		if (!canSale) {
			ResultEvent.sendResult(identity, actionEventId, MMORPGErrorCode.CODE_ITEM_CANTSALE);
			if (logger.isDebugEnabled()) {
				logger.debug("Item Can't be Sale");
			}
			return false;
		}

		return removeEventNotifyDelay(gridId, number, ItemOptSource.ItemBagSell); // 剪掉物品成功，增加金币

	}

	public int canUse(Player player, Item useItem, int number) {

		int playerLevel = player.getExpComponent().getLevel(); // 玩家等级
		byte playerKnight = MGPropertyAccesser.getKnight(player.getProperty()); // 玩家爵位
		int itemLevel = MGPropertyAccesser.getUseLevel(useItem.getItemRef().getProperty()); // 物品使用等级
		byte useKnight = MGPropertyAccesser.getUseKnight(useItem.getItemRef().getProperty()); // 物品使用爵位
		boolean canUse = useItem.canUse(); // 该物品是否可以使用
		if (!canUse) {
			if (logger.isDebugEnabled()) {
				logger.debug("Can't Use ");
			}
			return MMORPGErrorCode.CODE_ITEM_CANTUSE;
		} else if (playerLevel < itemLevel) { // 是否达到使用等级
			if (logger.isDebugEnabled()) {
				logger.debug("Player Can't Use Item,Because Of Level");
			}
			return MMORPGErrorCode.CODE_ITEM_USELEVEL;
		} else if (useKnight != 0 && playerKnight < useKnight) {
			if (logger.isDebugEnabled()) {
				logger.debug("Player Can't Use Item,Because Of Knight");
			}
			return MMORPGErrorCode.CODE_ITEM_USEKNIGHT;
		}
		return CAN_USE;
	}

	/**
	 * 检查是否可以使用指定数量物品
	 * 
	 * @param itemRefId
	 * @param number
	 * @return
	 */
	public RuntimeResult canUseItem(String itemRefId, int number) {
		int curNumber = itemBag.getItemNumber(itemRefId);
		if (number < 0) {
			if (logger.isDebugEnabled()) {
				logger.debug("Item number is <= 0");
			}
			return RuntimeResult.RuntimeApplicationError(MMORPGErrorCode.CODE_ITEM_NUMBERWORNG);
		}
		if (number > curNumber) {// 使用数量是否超出已有数量
			if (logger.isDebugEnabled()) {
				logger.debug("NOT Enough Item");
			}
			return RuntimeResult.RuntimeApplicationError(MMORPGErrorCode.CODE_ITEM_NOENOUGH);
		}
		return RuntimeResult.OK();
	}

	/**
	 * 使用指定数量物品
	 * 
	 * @param itemRefId
	 * @param number
	 * @return
	 */
	public RuntimeResult useItem(String itemRefId, int number, byte source) {
		if (number < 0) {
			if (logger.isDebugEnabled()) {
				logger.debug("Item number is <= 0");
			}
			return RuntimeResult.RuntimeApplicationError(MMORPGErrorCode.CODE_ITEM_NUMBERWORNG);
		}
		int curNumber = itemBag.getItemNumber(itemRefId);
		if (number > curNumber) {// 使用数量是否超出已有数量
			if (logger.isDebugEnabled()) {
				logger.debug("NOT Enough Item");
			}
			return RuntimeResult.RuntimeApplicationError(MMORPGErrorCode.CODE_ITEM_NOENOUGH);
		}

		Item useItem = GameObjectFactory.getItem(itemRefId);
		Player player = getConcreteParent();
		int result = canUse(player, useItem, number);
		if (result == CAN_USE) {
			RuntimeResult runtimeResult = useableItemRuntime.useTo(player, useItem, number);
			if (runtimeResult.isOK()) {
				removeEventNotify(itemRefId, number, true, source);
				StatFunctions.ItemBagStat(getConcreteParent(), StatItemBag.Use, itemRefId, number, source);
			}
			return runtimeResult;
		} else {
			return RuntimeResult.RuntimeApplicationError(result);
		}

	}

	/**
	 * 通过refId删除指定数量的物品
	 * 
	 * @param itemRefId
	 * @param number
	 * @return
	 */
	public synchronized boolean removeEventNotify(String itemRefId, int number, boolean isFirstBinded, byte source) {
		Identity identity = getConcreteParent().getIdentity();
		if (number < 0) {
			if (logger.isDebugEnabled()) {
				logger.debug("Item number is <= 0");
			}
			return false;
		}
		if (number > itemBag.getItemNumber(itemRefId)) {
			if (logger.isDebugEnabled())
				logger.debug("物品不足");

			return false;
		}
		ItemBagPartialRenewalData itemBagPartialRenewalData = itemBag.removeItemByItemRefId(itemRefId, number, isFirstBinded);
		TreeSet<ItemBagSlot> updateSet = new TreeSet<ItemBagSlot>();
		TreeSet<ItemBagSlot> deleteSet = new TreeSet<ItemBagSlot>();
		for (ItemBagSlot itemBagSlot : itemBagPartialRenewalData.getPartialRenewalIndexs()) {
			if (itemBagSlot.isEmpty()) {
				deleteSet.add(itemBagSlot);
			} else {
				updateSet.add(itemBagSlot);
			}
		}
		if (!updateSet.isEmpty()) {
			itemBagPartialRenewalData.getPartialRenewalIndexs().clear();
			itemBagPartialRenewalData.getPartialRenewalIndexs().addAll(updateSet);
			G2C_Item_Update updateRes = MessageFactory.getConcreteMessage(ItemBagEventDefines.G2C_Item_Update);
			updateRes.setOptype(ItemCode.MODIFY_UPDATE);
			updateRes.setItemBagPartialRenewalData(itemBagPartialRenewalData);
			updateRes.setItemBag(itemBag);
			updateRes.setPlayer(getConcreteParent());
			GameRoot.sendMessage(identity, updateRes);
		}
		if (!deleteSet.isEmpty()) {
			itemBagPartialRenewalData.getPartialRenewalIndexs().clear();
			itemBagPartialRenewalData.getPartialRenewalIndexs().addAll(deleteSet);
			G2C_Item_Update deleteRes = MessageFactory.getConcreteMessage(ItemBagEventDefines.G2C_Item_Update);
			deleteRes.setOptype(ItemCode.DEL_UPDATE);
			deleteRes.setItemBagPartialRenewalData(itemBagPartialRenewalData);
			deleteRes.setItemBag(itemBag);
			deleteRes.setPlayer(getConcreteParent());
			GameRoot.sendMessage(identity, deleteRes);
		}
		sendQuestItemChangeEvent(itemRefId, (byte) 0);
		sendRemoveItemGameEvent(itemRefId, number);
		StatFunctions.ItemBagStat(getConcreteParent(), StatItemBag.Remove, itemRefId, number, source);
		return true;
	}

	/**
	 * 通过索引格删除指定数量的物品
	 * 
	 * @param gridId
	 * @param number
	 * @return
	 */
	public synchronized boolean removeEventNotify(int gridId, int number, byte source) {
		if (number < 0) {
			if (logger.isDebugEnabled()) {
				logger.debug("Item number is <= 0");
			}
			return false;
		}
		Identity identity = getConcreteParent().getIdentity();
		if (gridId < 0 || gridId >= itemBag.getItemBagCapacity()) { // 格索引是否超出范围
			if (logger.isDebugEnabled()) {
				logger.debug("Illegal Index ");
			}
			return false;
		}
		Item item = itemBag.getItemBySlot(gridId);
		if (item == null) {
			return false;
		}
		int curNumber = item.getNumber();
		byte optype = ItemCode.DEL_UPDATE;
		if (curNumber == number)
			optype = ItemCode.DEL_UPDATE;
		else if (curNumber > number)
			optype = ItemCode.MODIFY_UPDATE;
		else if (curNumber < number) {
			if (logger.isDebugEnabled()) {
				logger.debug("Item not Enough");
			}
			return false;
		}
		String itemRefId = item.getItemRefId();
		ItemBagPartialRenewalData itemBagPartialRenewalData = itemBag.removeItemBySlot(gridId, number);
		if (!itemBagPartialRenewalData.isSucceed()) {
			return false;
		}
		G2C_Item_Update res = MessageFactory.getConcreteMessage(ItemBagEventDefines.G2C_Item_Update);
		res.setOptype(optype);
		res.setItemBagPartialRenewalData(itemBagPartialRenewalData);
		res.setItemBag(itemBag);
		res.setPlayer(getConcreteParent());
		GameRoot.sendMessage(identity, res);
		sendQuestItemChangeEvent(item, (byte) 0);
		sendRemoveItemGameEvent(itemRefId, number);
		StatFunctions.ItemBagStat(getConcreteParent(), StatItemBag.Remove, item.getItemRefId(), number, source);
		return true;
	}

	/**
	 * 通过索引格删除指定数量的物品(延迟通知)
	 * 
	 * @param gridId
	 * @param number
	 * @return
	 */
	public synchronized boolean removeEventNotifyDelay(int gridId, int number, byte source) {
		if (gridId < 0 || gridId >= itemBag.getItemBagCapacity()) { // 格索引是否超出范围
			if (logger.isDebugEnabled()) {
				logger.debug("Illegal Index ");
			}
			return false;
		}
		Item item = itemBag.getItemBySlot(gridId);
		if (item == null) {
			return false;
		}
		int curNumber = item.getNumber();
		if (number < 0) {
			if (logger.isDebugEnabled()) {
				logger.debug("Item number is <= 0");
			}
			return false;
		}
		if (curNumber < number) {
			if (logger.isDebugEnabled()) {
				logger.debug("Item not Enough");
			}
			return false;
		}
		String itemRefId = item.getItemRefId();
		ItemBagPartialRenewalData removeItemBySlot = itemBag.removeItemBySlot(gridId, number);
		if (!removeItemBySlot.isSucceed()) {
			return false;
		}
		sendQuestItemChangeEvent(item, (byte) 0);
		sendRemoveItemGameEvent(itemRefId, number);
		StatFunctions.ItemBagStat(getConcreteParent(), StatItemBag.Remove, item.getItemRefId(), number, source);
		return true;
	}

	/**
	 * 通过物品id删除指定数量物品
	 * 
	 * @param id
	 * @param number
	 * @return
	 */
	public synchronized boolean removeByItemIdEventNotify(String id, int number, byte source) {
		if (number < 0) {
			if (logger.isDebugEnabled()) {
				logger.debug("Item number is <= 0");
			}
			return false;
		}
		Identity identity = getConcreteParent().getIdentity();
		ItemBagSlot slot = itemBag.getItemSlot(id);
		if (slot == null) {
			return false;
		}
		int curNumber = slot.getCrtStackNumber();
		byte optype = ItemCode.DEL_UPDATE;
		if (curNumber == number)
			optype = ItemCode.DEL_UPDATE;
		else if (curNumber > number)
			optype = ItemCode.MODIFY_UPDATE;
		else if (curNumber < number) {
			return false;
		}
		Item item = slot.getItem();
		String itemRefId = item.getItemRefId();
		if (!itemBag.removeItemById(id, number)) {
			return false;
		}
		ItemBagPartialRenewalData itemBagPartialRenewalData = itemBag.getItemBagPartialRenewalData();
		itemBagPartialRenewalData.addItemBagSlot(slot);
		G2C_Item_Update res = MessageFactory.getConcreteMessage(ItemBagEventDefines.G2C_Item_Update);
		res.setOptype(optype);
		res.setItemBagPartialRenewalData(itemBagPartialRenewalData);
		res.setItemBag(itemBag);
		res.setPlayer(getConcreteParent());
		GameRoot.sendMessage(identity, res);
		sendQuestItemChangeEvent(item, (byte) 0);
		sendRemoveItemGameEvent(itemRefId, number);
		StatFunctions.ItemBagStat(getConcreteParent(), StatItemBag.Remove, itemRefId, number, source);
		return true;
	}

	public Item getItemByItemRefId(String itemRefId, int number, byte bindStatus) {

		Item item = null;
		if (number > 0) {
			item = GameObjectFactory.getItem(itemRefId);
			item.setNumber(number);
			item.setBindStatus(bindStatus);
			if (item.getItemType() == ItemType.Equip) {
				MGPropertyAccesser.setOrPutFightValue(item.getProperty(), getConcreteParent().getFightPower(item.getItemRef().getEffectProperty()));
			}
		}

		return item;
	}

	/**
	 * 增加物品
	 * 
	 * @param items
	 * @return
	 */
	public synchronized RuntimeResult addEventNotify(Collection<Item> items, byte source) {

		Identity identity = getConcreteParent().getIdentity();
		if (items == null || items.size() <= 0) {
			return RuntimeResult.RuntimeApplicationError(MMORPGErrorCode.CODE_ITEM_ITEMS_COLLECTION_EMPTY);
		}

		ItemBagPartialRenewalData itemBagPartialRenewalData = itemBag.getItemBagPartialRenewalData();
		boolean addResult = itemBag.putItemsIfAbsent(items, itemBagPartialRenewalData);
		if (!addResult) {
			return RuntimeResult.RuntimeApplicationError(MMORPGErrorCode.CODE_ITEM_CAPATICY_NOT_ENOUGH);
		}

		for (ItemBagSlot slot : itemBagPartialRenewalData.getPartialRenewalIndexs()) {
			Item item = slot.getItem();
			HightestEquipmentFacade.changeEquipmentToBest(item, source);
			if (item.isEquip() && !item.isNonPropertyItem()) {
				int fightValue = getConcreteParent().getFightPower(item.getProperty());
				MGPropertyAccesser.setOrPutFightValue(item.getProperty(), fightValue);
			}
			sendQuestItemChangeEvent(item, (byte) 1);
			// enter bag callback
			getItemBagPutItemRuntime().putItem(getConcreteParent(), item, slot.getIndex());
		}
		for (Item item : items) {
			// 添加增加物品log
			StatFunctions.ItemBagStat(getConcreteParent(), StatItemBag.Add, item.getItemRefId(), item.getNumber(), source);
		}
		if (itemBag.getIsFirstPutItemToBag()) {
			itemBag.setIsFirstPutItemToBag(1);
			sendFirstAddItemEvent();
		}

		G2C_Item_Update res = MessageFactory.getConcreteMessage(ItemBagEventDefines.G2C_Item_Update);
		res.setOptype(ItemCode.MODIFY_UPDATE);
		res.setItemBagPartialRenewalData(itemBagPartialRenewalData);
		res.setItemBag(itemBag);
		res.setPlayer(getConcreteParent());
		GameRoot.sendMessage(identity, res);

		return RuntimeResult.OK();
	}

	private void listenOnlineTimeOpenSlot() {

		SFTimerCreater timerCreater = MMORPGContext.getTimerCreater();
		timer = timerCreater.secondInterval(new SFTimeChimeListener() {
			@Override
			public void handleServiceShutdown() {
			}

			@Override
			public void handleTimeChime() {
				long crtLoginTime = System.currentTimeMillis() - MGPropertyAccesser.getLastLoginTime(getConcreteParent().getProperty());
				long onlineTime = (MGPropertyAccesser.getOnlineTime(getConcreteParent().getProperty()) + crtLoginTime) / 1000;
				int crtMaxSlot = (itemBag.getItemBagCapacity() - DEFAULT_ITEMBAGSLOT_COUNT) + 1;
				if (crtMaxSlot < 1 || crtMaxSlot > DEFAULT_ITEMBAGSLOT_COUNT) {
					return;
				}
				long openTime = ItemSomeConfigData.openSlotTimeMap.get(crtMaxSlot);
				if (openTime < onlineTime) {
					crtMaxSlot = crtMaxSlot + 1;
					itemBag.expendItemBagSlot();
					G2C_Bag_Capacity res = MessageFactory.getConcreteMessage(ItemBagEventDefines.G2C_Bag_Capacity);
					res.setItemBag(itemBag);
					GameRoot.sendMessage(getConcreteParent().getIdentity(), res);
				}
			}

			@Override
			public void handleTimeChimeCancel() {
			}
		});
	}

	private boolean isCDTime(String refId, long CDTime) {

		if (!cdMgr.isCDStarted(refId)) {
			cdMgr.startCD(refId, (long) CDTime * 1000);
			return false;
		}

		if (!cdMgr.isOutOfCD(refId)) {
			return true;
		}
		return false;
	}

	/**
	 * 指定数量物品掉落
	 * 
	 * @param player
	 * @param owner
	 * @param number
	 * @param itemRefId
	 */
	public List<Loot> dropItem(Player player, Player owner, ItemPair itemPair, byte source) {
		return dropItem(owner, itemPair, player.getCrtScene(), player.getCrtPosition(), source);
	}

	/**
	 * itemPair 分为多个掉落物，每个掉落物数量为1
	 * 
	 * @param owner
	 * @param itemPair
	 * @param dstScene
	 * @param dstPos
	 * @param source
	 * @return
	 */
	public List<Loot> dropItem(Player owner, ItemPair itemPair, GameScene dstScene, Position dstPos, byte source) {
		boolean remove = removeEventNotify(itemPair.getItemRefId(), itemPair.getNumber(), true, source);
		if (remove) {
			int number = itemPair.getNumber();
			itemPair.setNumber(1);
			LootMgrComponent lootManager = dstScene.getLootMgrComponent();
			return lootManager.dropItemPair(dstPos, itemPair, number, owner);
		}
		return null;
	}

	/**
	 * 物品掉落
	 * 
	 * @param rate
	 * @param owner
	 */
	public List<Loot> dropItem(int rate, Player owner, int dropNumber, byte source) {
		int random = SFRandomUtils.random100();
		if (random < rate) {
			List<ItemBagSlot> itemBagSlots = itemBag.getItemBagCollection();
			List<ItemBagSlot> equipmentList = new ArrayList<>();
			List<ItemBagSlot> drugList = new ArrayList<>();
			List<Item> itemList = new ArrayList<>();
			for (ItemBagSlot slot : itemBagSlots) {
				if (slot.hasItem()) {
					Item item = slot.getItem();
					if (item.binded() == false && item.getItemType() == ItemType.Equip) {
						equipmentList.add(slot);
					} else if (item.binded() == false && item.getItemType() == ItemType.Drug) {
						drugList.add(slot);
					}
				}
			}

			int count = SFRandomUtils.random(1, 3);
			for (int i = 0; i < count; i++) {
				int itemRandom = SFRandomUtils.random100();
				int itemType = ItemType.Equip;
				if (itemRandom < 75 && drugList.size() > 1) {
					itemType = ItemType.Drug;
				}
				int size = 0;
				if (itemType == ItemType.Equip) {
					size = equipmentList.size();
				} else {
					size = drugList.size();
				}
				int index = 0;
				if (size > 1) {
					index = SFRandomUtils.random(size) - 1;
				} else if (size == 1) {
					index = 0;
				} else {
					break;
				}
				ItemBagSlot slot = null;
				if (itemType == ItemType.Equip) {
					slot = equipmentList.remove(index);
				} else {
					slot = drugList.remove(index);
				}

				Item tmpItem = slot.getItem();
				if (removeEventNotify(slot.getIndex(), tmpItem.getNumber(), source)) {
					itemList.add(tmpItem);
				}

			}

			Player player = getConcreteParent();
			GameScene crtScene = player.getCrtScene();
			Position crtPosition = player.getCrtPosition();
			LootMgrComponent lootManager = crtScene.getLootMgrComponent();
			return lootManager.dropItem(crtPosition, itemList, owner);
		}

		return null;
	}

	// --------------------gameEvent-----------------------------//

	private void sendQuestItemChangeEvent(Item item, byte optType) {
		if (item.getItemType() == ItemType.Quest) {
			String itemRefId = item.getItemRefId();
			sendQuestItemChangeEvent(itemRefId, optType);
		}
	}

	private void sendQuestItemChangeEvent(String itemRefId, byte optType) {
		int number = itemBag.getItemNumber(itemRefId);
		QuestItemChange_GE questItemChange_GE = new QuestItemChange_GE(itemRefId, number, optType);
		GameEvent<QuestItemChange_GE> event = (GameEvent<QuestItemChange_GE>) GameEvent.getInstance(QuestItemChange_GE_Id, questItemChange_GE);
		getConcreteParent().handleGameEvent(event);
		GameEvent.pool(event);
	}

	private void sendFirstAddItemEvent() {

		FirstTimeAddItem_GE firstTimeAddItem_GE = new FirstTimeAddItem_GE();
		GameEvent<FirstTimeAddItem_GE> event = (GameEvent<FirstTimeAddItem_GE>) GameEvent.getInstance(FirstTimeAddItem_GE_Id, firstTimeAddItem_GE);
		getConcreteParent().handleGameEvent(event);
		GameEvent.pool(event);

	}

	private void sendRemoveItemGameEvent(String itemRefId, int number) {
		RemoveItem_GE removeItem_GE = new RemoveItem_GE(itemRefId, number);
		GameEvent<RemoveItem_GE> event = (GameEvent<RemoveItem_GE>) GameEvent.getInstance(RemoveItem_GE.class.getSimpleName(), removeItem_GE);
		getConcreteParent().handleGameEvent(event);
		GameEvent.pool(event);
	}

	public UseableItemRuntime getUseableItemRuntime() {
		return useableItemRuntime;
	}

	public void setUseableItemRuntime(UseableItemRuntime useableItemRuntime) {
		this.useableItemRuntime = useableItemRuntime;
	}

	public ItemBagPutItemRuntime getItemBagPutItemRuntime() {
		return itemBagPutItemRuntime;
	}

	public void setItemBagPutItemRuntime(ItemBagPutItemRuntime itemBagPutItemRuntime) {
		this.itemBagPutItemRuntime = itemBagPutItemRuntime;
	}

}
