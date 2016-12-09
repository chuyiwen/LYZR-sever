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
package newbee.morningGlory.mmorpg.player.depot;

import java.util.ArrayList;

import newbee.morningGlory.mmorpg.player.depot.event.C2G_WareHouse_Capacity;
import newbee.morningGlory.mmorpg.player.depot.event.C2G_WareHouse_Item_List;
import newbee.morningGlory.mmorpg.player.depot.event.C2G_WareHouse_Item_SoltUnLock;
import newbee.morningGlory.mmorpg.player.depot.event.C2G_WareHouse_Item_Update;
import newbee.morningGlory.mmorpg.player.depot.event.G2C_WareHouse_Capacity;
import newbee.morningGlory.mmorpg.player.depot.event.G2C_WareHouse_Item_List;
import newbee.morningGlory.mmorpg.player.depot.event.G2C_WareHouse_Item_Update;
import newbee.morningGlory.mmorpg.player.depot.event.PlayerDepotEventDefines;

import org.apache.log4j.Logger;

import sophia.foundation.communication.core.ActionEventBase;
import sophia.foundation.data.PersistenceObject;
import sophia.game.GameRoot;
import sophia.game.component.communication.GameEvent;
import sophia.mmorpg.base.ConcreteComponent;
import sophia.mmorpg.code.CodeContext;
import sophia.mmorpg.code.MMORPGErrorCode;
import sophia.mmorpg.event.ResultEvent;
import sophia.mmorpg.item.Item;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.gameEvent.ChineseModeQuest_GE;
import sophia.mmorpg.player.itemBag.ItemBag;
import sophia.mmorpg.player.itemBag.ItemBagPartialRenewalData;
import sophia.mmorpg.player.itemBag.ItemCode;
import sophia.mmorpg.player.itemBag.ItemOptSource;
import sophia.mmorpg.player.quest.event.C2G_COM_ActionToSucceed;
import sophia.mmorpg.player.quest.event.QuestActionEventDefines;
import sophia.mmorpg.player.quest.ref.order.QuestChineseOrderDefines;
import sophia.mmorpg.utils.RuntimeResult;

public class PlayerDepotComponent extends ConcreteComponent<Player> {
	private static final Logger logger = Logger.getLogger(PlayerDepotComponent.class);
	public static final String Tag = "PlayerDepotComponent";
	private static final String ChineseModeQuest_GE_Id = ChineseModeQuest_GE.class.getSimpleName();

	private static final short MAX_SLOT_COUNT = 25;
	private static final byte DEPOT_OPT_ADD = 1;
	private static final byte DEPOT_OPT_DEL = 2;

	private PersistenceObject persistenceObject;

	private ItemBag depot = new ItemBag();

	public PlayerDepotComponent() {
		super();
		depot.getItemBagSlotList().setMax_Slot_Number(MAX_SLOT_COUNT);
		depot.expendItemBagSlot(MAX_SLOT_COUNT);
	}

	@Override
	public void ready() {
		super.ready();
		addActionEventListener(QuestActionEventDefines.C2G_COM_ActionToSucceed);
		addActionEventListener(PlayerDepotEventDefines.C2G_WareHouse_Capacity);
		addActionEventListener(PlayerDepotEventDefines.C2G_WareHouse_Item_List);
		addActionEventListener(PlayerDepotEventDefines.C2G_WareHouse_Item_Update);
		addActionEventListener(PlayerDepotEventDefines.C2G_WareHouse_Item_SoltUnLock);
	}

	@Override
	public void suspend() {
		super.suspend();
		removeActionEventListener(QuestActionEventDefines.C2G_COM_ActionToSucceed);
		removeActionEventListener(PlayerDepotEventDefines.C2G_WareHouse_Capacity);
		removeActionEventListener(PlayerDepotEventDefines.C2G_WareHouse_Item_List);
		removeActionEventListener(PlayerDepotEventDefines.C2G_WareHouse_Item_Update);
		removeActionEventListener(PlayerDepotEventDefines.C2G_WareHouse_Item_SoltUnLock);
	}

	@Override
	public void handleActionEvent(ActionEventBase event) {
		short actionEventId = event.getActionEventId();
		switch (actionEventId) {
		case PlayerDepotEventDefines.C2G_WareHouse_Capacity:
			handle_C2G_WareHouse_Capacity((C2G_WareHouse_Capacity) event);
			break;
		case PlayerDepotEventDefines.C2G_WareHouse_Item_List:
			handle_C2G_WareHouse_Item_List((C2G_WareHouse_Item_List) event);
			break;
		case PlayerDepotEventDefines.C2G_WareHouse_Item_Update:
			handle_C2G_WareHouse_Item_Update((C2G_WareHouse_Item_Update) event);
			break;
		case PlayerDepotEventDefines.C2G_WareHouse_Item_SoltUnLock:
			handle_C2G_WareHouse_Item_SoltUnLock((C2G_WareHouse_Item_SoltUnLock) event);
			break;
		case QuestActionEventDefines.C2G_COM_ActionToSucceed:
			handle_C2G_COM_ActionToSucceed((C2G_COM_ActionToSucceed) event);
		default:
			break;

		}

		super.handleActionEvent(event);
	}

	private void handle_C2G_WareHouse_Capacity(C2G_WareHouse_Capacity event) {
		short maxCapacity = this.depot.getItemBagMaxCapacity();
		int currentCapacity = this.depot.getItemBagCapacity();
		G2C_WareHouse_Capacity res = new G2C_WareHouse_Capacity(maxCapacity, (short) currentCapacity);
		GameRoot.sendMessage(event.getIdentity(), res);

	}

	private void handle_C2G_WareHouse_Item_List(C2G_WareHouse_Item_List event) {
		G2C_WareHouse_Item_List res = new G2C_WareHouse_Item_List(getConcreteParent(), depot);
		GameRoot.sendMessage(event.getIdentity(), res);
	}

	private void handle_C2G_COM_ActionToSucceed(C2G_COM_ActionToSucceed event) {
		sendChineseModeQuest_GE(QuestChineseOrderDefines.Depot, getConcreteParent().getSceneRefId());
	}

	private void sendChineseModeQuest_GE(short orderEventId, String value) {
		ChineseModeQuest_GE chineseModeQuest_GE = new ChineseModeQuest_GE();
		chineseModeQuest_GE.setType(ChineseModeQuest_GE.CourseType);
		chineseModeQuest_GE.setOrderEventId(orderEventId);
		chineseModeQuest_GE.setChineseModeValue(value);
		GameEvent<ChineseModeQuest_GE> chinese = GameEvent.getInstance(ChineseModeQuest_GE_Id, chineseModeQuest_GE);
		getConcreteParent().handleGameEvent(chinese);
		GameEvent.pool(chinese);
	}

	private void handle_C2G_WareHouse_Item_Update(C2G_WareHouse_Item_Update event) {
		byte type = event.getType();
		switch (type) {
		case DEPOT_OPT_ADD:
			handle_depot_add(event);
			break;
		case DEPOT_OPT_DEL:
			handle_depot_del(event);
			break;
		default:
			break;
		}

	}

	private void handle_depot_add(C2G_WareHouse_Item_Update event) {
		short gridId = event.getGridId();
		Player player = getConcreteParent();
		Item item = player.getItemBagComponent().getItemBag().getItemBySlot(gridId);
		if (item == null) {
			ResultEvent.sendResult(event.getIdentity(), event.getActionEventId(), MMORPGErrorCode.CODE_ITEM_DEPOT_NOT_EXIST);
			return;
		}

		ArrayList<Item> items = new ArrayList<>();
		items.add(item);
		boolean canPutIntoDepot = depot.canPut(items);
		if (!canPutIntoDepot) {
			ResultEvent.sendResult(event.getIdentity(), event.getActionEventId(), MMORPGErrorCode.CODE_ITEM_DEPOT_NOT_ENOUGH_CAPACITY);
			return;
		} else {
			ItemBagPartialRenewalData itemBagPartialRenewalData = new ItemBagPartialRenewalData();
			depot.putItem(item, itemBagPartialRenewalData);
			player.getItemBagComponent().removeEventNotify(gridId, item.getNumber(), ItemOptSource.depot);

			G2C_WareHouse_Item_Update res = new G2C_WareHouse_Item_Update(ItemCode.MODIFY_UPDATE, player, itemBagPartialRenewalData);
			GameRoot.sendMessage(event.getIdentity(), res);
		}

	}

	private void handle_depot_del(C2G_WareHouse_Item_Update event) {
		short gridId = event.getGridId();
		Player player = getConcreteParent();
		Item item = depot.getItemBySlot(gridId);
		if (item == null) {
			ResultEvent.sendResult(event.getIdentity(), event.getActionEventId(), MMORPGErrorCode.CODE_ITEM_DEPOT_NOT_EXIST);
			return;
		}

		ArrayList<Item> items = new ArrayList<>();
		items.add(item);

		RuntimeResult result = player.getItemBagComponent().addEventNotify(items, ItemOptSource.depot);
		if (!result.isOK()) {
			if (logger.isDebugEnabled()) {
				logger.debug("handle_depot_del fail to add item into ItemBag. error " + CodeContext.description(result.getApplicationCode()));
			}
			ResultEvent.sendResult(event.getIdentity(), event.getActionEventId(), result.getApplicationCode());
			return;
		} else {
			ItemBagPartialRenewalData itemBagPartialRenewalData = new ItemBagPartialRenewalData();
			depot.removeItemBySlot(gridId, itemBagPartialRenewalData);

			G2C_WareHouse_Item_Update res = new G2C_WareHouse_Item_Update(event.getType(), player, itemBagPartialRenewalData);
			GameRoot.sendMessage(event.getIdentity(), res);
		}

	}

	private void handle_C2G_WareHouse_Item_SoltUnLock(C2G_WareHouse_Item_SoltUnLock event) {
		// TODO Auto-generated method stub

	}

	public PersistenceObject getPersistenceObject() {
		return persistenceObject;
	}

	public void setPersistenceObject(PersistenceObject persistenceObject) {
		this.persistenceObject = persistenceObject;
	}

	public ItemBag getDepot() {
		return depot;
	}

	public void setDepot(ItemBag depot) {
		this.depot = depot;
	}

}
