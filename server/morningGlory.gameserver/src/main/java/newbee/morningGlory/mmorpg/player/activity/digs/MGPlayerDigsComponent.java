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
package newbee.morningGlory.mmorpg.player.activity.digs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;

import newbee.morningGlory.mmorpg.player.activity.digs.event.C2G_Digs_List;
import newbee.morningGlory.mmorpg.player.activity.digs.event.C2G_Digs_Switch;
import newbee.morningGlory.mmorpg.player.activity.digs.event.C2G_Digs_Type;
import newbee.morningGlory.mmorpg.player.activity.digs.event.G2C_Digs_List;
import newbee.morningGlory.mmorpg.player.activity.digs.event.G2C_Digs_Result;
import newbee.morningGlory.mmorpg.player.activity.digs.event.G2C_Digs_Update;
import newbee.morningGlory.mmorpg.player.activity.digs.event.MGDigsEventDefines;
import newbee.morningGlory.mmorpg.player.activity.digs.ref.MGDigsDataConfig;
import newbee.morningGlory.mmorpg.player.activity.digs.ref.MGDigsRewardRef;
import newbee.morningGlory.mmorpg.player.activity.digs.ref.MGDigsTypeRef;
import newbee.morningGlory.stat.MGStatFunctions;
import newbee.morningGlory.stat.logs.StatDigs;

import org.apache.log4j.Logger;

import sophia.foundation.authentication.Identity;
import sophia.foundation.communication.core.ActionEventBase;
import sophia.foundation.communication.core.MessageFactory;
import sophia.foundation.data.PersistenceObject;
import sophia.foundation.property.PropertyDictionary;
import sophia.game.GameRoot;
import sophia.game.ref.GameRefObject;
import sophia.mmorpg.GameObjectFactory;
import sophia.mmorpg.base.ConcreteComponent;
import sophia.mmorpg.code.MMORPGErrorCode;
import sophia.mmorpg.event.ResultEvent;
import sophia.mmorpg.item.Item;
import sophia.mmorpg.item.ref.UnPropsItemRef;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.chat.Bricks;
import sophia.mmorpg.player.chat.PlayerChatFacade;
import sophia.mmorpg.player.chat.sysytem.SystemPromptFacade;
import sophia.mmorpg.player.itemBag.ItemBag;
import sophia.mmorpg.player.itemBag.ItemBagPartialRenewalData;
import sophia.mmorpg.player.itemBag.ItemBagSlot;
import sophia.mmorpg.player.itemBag.ItemCode;
import sophia.mmorpg.player.itemBag.ItemFacade;
import sophia.mmorpg.player.itemBag.ItemOptSource;
import sophia.mmorpg.player.itemBag.ItemPair;
import sophia.mmorpg.player.itemBag.ItemQuality;
import sophia.mmorpg.player.persistence.immediatelySave.PlayerImmediateDaoFacade;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;
import sophia.mmorpg.utils.RuntimeResult;
import sophia.mmorpg.utils.SFRandomUtils;

public class MGPlayerDigsComponent extends ConcreteComponent<Player> {
	private Logger logger = Logger.getLogger(MGPlayerDigsComponent.class);
	public static final String Tag = "MGPlayerDigsComponent";
	private ItemBag digsHouse = new ItemBag();

	private static final short max_Slot_Number = 300;
	private PersistenceObject persistenceObject;

	public MGPlayerDigsComponent() {
		digsHouse.getItemBagSlotList().setMax_Slot_Number(MGPlayerDigsComponent.max_Slot_Number);
		digsHouse.expendItemBagSlot(max_Slot_Number);
	}

	@Override
	public void ready() {
		addActionEventListener(MGDigsEventDefines.C2G_Digs_List);
		addActionEventListener(MGDigsEventDefines.C2G_Digs_Switch);
		addActionEventListener(MGDigsEventDefines.C2G_Digs_Type);
	}

	@Override
	public void suspend() {
		removeActionEventListener(MGDigsEventDefines.C2G_Digs_List);
		removeActionEventListener(MGDigsEventDefines.C2G_Digs_Switch);
		removeActionEventListener(MGDigsEventDefines.C2G_Digs_Type);
	}

	@Override
	public void handleActionEvent(ActionEventBase event) {
		short actionEventId = event.getActionEventId();

		switch (actionEventId) {
		case MGDigsEventDefines.C2G_Digs_List:
			handle_Digs_List((C2G_Digs_List) event);
			break;
		case MGDigsEventDefines.C2G_Digs_Switch:
			handle_Digs_Switch((C2G_Digs_Switch) event);
			break;
		case MGDigsEventDefines.C2G_Digs_Type:
			handle_Digs_Type((C2G_Digs_Type) event);
			break;
		default:
			break;
		}
		PlayerImmediateDaoFacade.update(getConcreteParent());
	}

	private boolean isValid(String removeItemRefId, int removenumber, int unbindedGold, int totalCount) {

		int haveNumber = ItemFacade.getNumber(getConcreteParent(), removeItemRefId);
		if (removenumber * totalCount <= haveNumber) {
			return true;
		} else {
			int extalNeedGoldCount = removenumber * totalCount - haveNumber;
			if (unbindedGold * extalNeedGoldCount <= getConcreteParent().getPlayerMoneyComponent().getUnbindGold()) {
				return true;
			}
		}
		return false;
	}

	public void removeConsume(String removeItemRefId, int removenumber, int unbindedGold, int totalCount) {
		int haveNumber = ItemFacade.getNumber(getConcreteParent(), removeItemRefId);
		if (removenumber * totalCount <= haveNumber) {
			ItemFacade.removeItem(getConcreteParent(), removeItemRefId, removenumber * totalCount, true, ItemOptSource.Digs);
		} else {
			ItemFacade.removeItem(getConcreteParent(), removeItemRefId, haveNumber, true, ItemOptSource.Digs);
			int extalNeedGoldCount = removenumber * totalCount - haveNumber;
			getConcreteParent().getPlayerMoneyComponent().subUnbindGold(unbindedGold * extalNeedGoldCount, ItemOptSource.Digs);
		}

	}

	private void handle_Digs_Type(C2G_Digs_Type event) {
		byte type = event.getType();
		MGDigsDataConfig config = (MGDigsDataConfig) GameRoot.getGameRefObjectManager().getManagedObject(MGDigsDataConfig.DigsType_Id);
		if (config == null) {
			return;
		}
		MGDigsTypeRef typeRef = config.getDigsTypeMaps().get(MGDigsType.DigType + type);
		if (typeRef == null) {
			if (logger.isDebugEnabled()) {
				logger.debug("挖宝类型type错误:" + type + "应该在[1,4]");
			}
			return;
		}
		Collection<Item> items = new ArrayList<Item>();
		Collection<ItemPair> unPropsItems = new ArrayList<ItemPair>();
		Collection<ItemPair> allItems = new ArrayList<ItemPair>();
		PropertyDictionary pd = typeRef.getProperty();
		int generalDigsCount = MGPropertyAccesser.getGeneralDigsCount(pd);
		int bestDigsCount = MGPropertyAccesser.getBestDigsCount(pd);
		String removeItemRefId = MGPropertyAccesser.getItemRefId(pd);
		int unbindedGold = MGPropertyAccesser.getUnbindedGold(pd);
		int removenumber = MGPropertyAccesser.getNumber(pd);
		if (!isValid(removeItemRefId, removenumber, unbindedGold, generalDigsCount + bestDigsCount)) {
			return;
		}
		String systemPromptConfigRefId = "system_prompt_config_29";// [系统]XXX通过挖宝获得XXX		
		for (int i = 0; i < generalDigsCount; i++) {
			MGDigsRewardRef rewardRef = Digs(MGDigsType.GeneralDigs);
			String itemRefId = MGPropertyAccesser.getItemRefId(rewardRef.getProperty());
			int number = MGPropertyAccesser.getNumber(rewardRef.getProperty());
			byte getBindStatus = MGPropertyAccesser.getBindStatus(rewardRef.getProperty());

			GameRefObject gameRef = GameRoot.getGameRefObjectManager().getManagedObject(itemRefId);
			if (gameRef instanceof UnPropsItemRef) {
				unPropsItems.add(new ItemPair(itemRefId, number, getBindStatus));
			} else {
				Item item = getCollectionByItemRefId(itemRefId, number, getBindStatus);
				items.add(item);
				if(item.isEquip() && item.getQuality() >= ItemQuality.PURPLE){
					String content = Bricks.getContents(systemPromptConfigRefId,getConcreteParent().getName(),item.getName(),item.getItemRefId());
					PlayerChatFacade.sendMessageToSystem(content);
				}
			}
			allItems.add(new ItemPair(itemRefId, number, getBindStatus));
		}

		for (int i = 0; i < bestDigsCount; i++) {
			MGDigsRewardRef rewardRef = Digs(MGDigsType.BestDigs);
			String itemRefId = MGPropertyAccesser.getItemRefId(rewardRef.getProperty());
			int number = MGPropertyAccesser.getNumber(rewardRef.getProperty());
			byte getBindStatus = MGPropertyAccesser.getBindStatus(rewardRef.getProperty());
			GameRefObject gameRef = GameRoot.getGameRefObjectManager().getManagedObject(itemRefId);
			if (gameRef instanceof UnPropsItemRef) {
				unPropsItems.add(new ItemPair(itemRefId, number, getBindStatus));
			} else {
				Item item = getCollectionByItemRefId(itemRefId, number, getBindStatus);
				items.add(item);
				if(item.isEquip() && item.getQuality() >= ItemQuality.PURPLE){
					String content = Bricks.getContents(systemPromptConfigRefId,getConcreteParent().getName(),item.getName(),item.getItemRefId());
					PlayerChatFacade.sendMessageToSystem(content);
				}
			}
			allItems.add(new ItemPair(itemRefId, number, getBindStatus));
		}

		if (items.size() > 0 && !addEventNotify(items)) {
			SystemPromptFacade.sendDigHouseFullTips(getConcreteParent());
			return;
		}
		ItemFacade.addItem(getConcreteParent(), unPropsItems, ItemOptSource.Digs);
		G2C_Digs_Result result = MessageFactory.getConcreteMessage(MGDigsEventDefines.G2C_Digs_Result);
		result.setDigs(allItems);
		GameRoot.sendMessage(getConcreteParent().getIdentity(), result);
		// 删除消耗物
		long crtTime = System.currentTimeMillis();
		removeConsume(removeItemRefId, removenumber, unbindedGold, generalDigsCount + bestDigsCount);
		MGStatFunctions.DigStat(getConcreteParent(), StatDigs.Dig, generalDigsCount + bestDigsCount, crtTime);
		for (ItemPair itemPair : allItems) {
			MGStatFunctions.DigItemStat(getConcreteParent(), itemPair.getItemRefId(), itemPair.getNumber(), crtTime);
		}
	}

	private void handle_Digs_Switch(C2G_Digs_Switch event) {
		long crtTime = System.currentTimeMillis();
		short index = event.getIndex();
		if (index == -1) { // 全部提取到背包
			List<ItemBagSlot> itemBagSlots = digsHouse.getItemBagCollection();
			List<Short> indexs = new ArrayList<>();
			for (ItemBagSlot slot : itemBagSlots) {
				if (!slot.isEmpty()) {
					RuntimeResult result = ItemFacade.addItems(getConcreteParent(), slot.getItem(), ItemOptSource.Digs);
					if (result.isOK()) {
						indexs.add(slot.getIndex());
					}
				}
			}

			if (itemBagSlots.size() !=0 && indexs.size() == 0) {
				if (logger.isDebugEnabled()) {
					logger.debug("背包已满,请清理后再提取");
				}
				ResultEvent.sendResult(event.getIdentity(), event.getActionEventId(), MMORPGErrorCode.CODE_ITEM_SLOT_ENOUGH);
				return;
			}
			removeEventNotify(indexs);
			MGStatFunctions.DigStat(getConcreteParent(), StatDigs.Get, -1, crtTime);
		} else {
			if (index < -1 || index > 300) {
				if (logger.isDebugEnabled()) {
					logger.debug("索引位置超出范围,为:" + index);
				}
				return;
			}
			Item item = digsHouse.getItemBySlot(index);
			if (item == null) {
				if (logger.isDebugEnabled()) {
					logger.debug("索引位置:" + index + " 不存在物品，请检查");
				}
				return;
			}
			RuntimeResult result = ItemFacade.addItems(getConcreteParent(), item, ItemOptSource.Digs);
			if (!result.isOK()) {
				if (logger.isDebugEnabled()) {
					logger.debug("背包已满,请清理后再提取");
				}
				ResultEvent.sendResult(event.getIdentity(), event.getActionEventId(), MMORPGErrorCode.CODE_ITEM_SLOT_ENOUGH);
				return;
			}
			removeEventNotify(index);
			MGStatFunctions.DigStat(getConcreteParent(), StatDigs.Get, index, crtTime);
		}
	}

	private void handle_Digs_List(C2G_Digs_List event) {
		if (logger.isDebugEnabled()) {
			logger.debug("请求挖宝仓库列表");
		}
		G2C_Digs_List res = MessageFactory.getConcreteMessage(MGDigsEventDefines.G2C_Digs_List);
		res.setDigsHouse(digsHouse);
		res.setPlayer(getConcreteParent());
		GameRoot.sendMessage(getConcreteParent().getIdentity(), res);
	}

	/**
	 * 一次挖宝
	 * 
	 * @param generalOrBest
	 *            极品挖宝 或 普通挖宝
	 * @return
	 */
	public MGDigsRewardRef Digs(byte generalOrBest) {
		MGDigsDataConfig config = (MGDigsDataConfig) GameRoot.getGameRefObjectManager().getManagedObject(MGDigsDataConfig.DigsReward_Id);
		if (config == null) {
			return null;
		}
		List<Integer> args = new ArrayList<Integer>();
		args.add(0);
		int i = 1;
		int random = SFRandomUtils.random100w();
		for (Entry<String, MGDigsRewardRef> entry : config.getDigsRewardMaps().entrySet()) {
			MGDigsRewardRef ref = entry.getValue();
			byte digsType = MGPropertyAccesser.getItemGroup(ref.getProperty());
			if (digsType == generalOrBest) {
				int probability = MGPropertyAccesser.getProbability(ref.getProperty());
				args.add(args.get(i - 1) + probability);
				if (random > args.get(i - 1) && random <= args.get(i)) {
					return ref;
				}
				i++;
			}
		}
		return null;
	}

	/**
	 * 通过索引格删除指定数量的物品
	 * 
	 * @param gridId
	 * @param number
	 * @return
	 */
	public boolean removeEventNotify(List<Short> indexs) {

		Identity identity = getConcreteParent().getIdentity();

		for (int i = 0; i < indexs.size(); i++) {
			digsHouse.removeItemBySlot(indexs.get(i));
		}
		ItemBagPartialRenewalData itemBagPartialRenewalData = digsHouse.getNoResetItemBagPartialRenewalData();
		G2C_Digs_Update res = MessageFactory.getConcreteMessage(MGDigsEventDefines.G2C_Digs_Update);
		res.setOptype((byte) 2);
		res.setItemBagPartialRenewalData(itemBagPartialRenewalData);
		res.setDigsHouse(digsHouse);
		res.setPlayer(getConcreteParent());
		GameRoot.sendMessage(identity, res);
		return true;
	}

	/**
	 * 通过索引格删除指定数量的物品
	 * 
	 * @param gridId
	 * @param number
	 * @return
	 */
	public boolean removeEventNotify(int gridId) {

		Identity identity = getConcreteParent().getIdentity();
		Item item = digsHouse.getItemBySlot(gridId);
		if (item == null) {
			return false;
		}

		ItemBagPartialRenewalData itemBagPartialRenewalData = digsHouse.removeItemBySlot(gridId);
		G2C_Digs_Update res = MessageFactory.getConcreteMessage(MGDigsEventDefines.G2C_Digs_Update);
		res.setOptype(ItemCode.DEL_UPDATE);
		res.setItemBagPartialRenewalData(itemBagPartialRenewalData);
		res.setDigsHouse(digsHouse);
		res.setPlayer(getConcreteParent());
		GameRoot.sendMessage(identity, res);
		return true;
	}

	public Item getCollectionByItemRefId(String itemRefId, int number, byte bindStatus) {

		Item item = null;
		if (number > 0) {
			item = GameObjectFactory.getItem(itemRefId);
			item.setNumber(number);
			if (bindStatus == Item.Binded) {
				item.setBindStatus(Item.Binded);
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
	public boolean addEventNotify(Collection<Item> items) {

		Identity identity = getConcreteParent().getIdentity();
		if (items == null || items.size() <= 0) {
			return false;
		}
		if (digsHouse.isFull()) {
			return false;
		}

		ItemBagPartialRenewalData itemBagPartialRenewalData = digsHouse.getItemBagPartialRenewalData();
		boolean addResult = digsHouse.putItemsIfAbsent(items, itemBagPartialRenewalData);
		if (!addResult) {
			return false;
		}

		G2C_Digs_Update res = MessageFactory.getConcreteMessage(MGDigsEventDefines.G2C_Digs_Update);
		res.setOptype(ItemCode.MODIFY_UPDATE);
		res.setItemBagPartialRenewalData(itemBagPartialRenewalData);
		res.setDigsHouse(digsHouse);
		res.setPlayer(getConcreteParent());
		GameRoot.sendMessage(identity, res);

		return true;
	}

	public PersistenceObject getPersistenceObject() {
		return persistenceObject;
	}

	public void setPersistenceObject(PersistenceObject persistenceObject) {
		this.persistenceObject = persistenceObject;
	}

	public ItemBag getDigsHouse() {
		return digsHouse;
	}

	public void setDigsHouse(ItemBag digsHouse) {
		this.digsHouse = digsHouse;
	}

}
