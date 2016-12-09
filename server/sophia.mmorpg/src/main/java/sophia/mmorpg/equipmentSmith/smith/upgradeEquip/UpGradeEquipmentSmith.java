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
package sophia.mmorpg.equipmentSmith.smith.upgradeEquip;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import sophia.foundation.property.PropertyDictionary;
import sophia.game.GameRoot;
import sophia.mmorpg.GameObjectFactory;
import sophia.mmorpg.code.MMORPGErrorCode;
import sophia.mmorpg.equipmentSmith.EqiupmentComponentProvider;
import sophia.mmorpg.equipmentSmith.EquipmentSmithComponent;
import sophia.mmorpg.equipmentSmith.smith.EquipmentSmithHelper;
import sophia.mmorpg.equipmentSmith.smith.fenJie.MGFenJieEquipmentConfig;
import sophia.mmorpg.equipmentSmith.smith.fenJie.MGFenJieScrollRef;
import sophia.mmorpg.item.Item;
import sophia.mmorpg.item.ref.ItemRef;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.itemBag.ItemBagSlot;
import sophia.mmorpg.player.itemBag.ItemFacade;
import sophia.mmorpg.player.itemBag.ItemOptSource;
import sophia.mmorpg.player.itemBag.ItemType;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;
import sophia.mmorpg.utils.RuntimeResult;

/**
 * Copyright (c) 2014 by 游爱.
 * 
 * @author 尹杏林 Create on 2014-7-9 上午11:01:43
 * @version 1.0
 */
public final class UpGradeEquipmentSmith {

	public static RuntimeResult upGrade(Player player, Item oldEquip, boolean isFirstBinded) {
		int needEquipNumber = 3;
		String equipRefId = "equip_1_1000";
		String materialRefId = oldEquip.getItemRefId();
		int subGold = 1000;
		RuntimeResult valid = valid(player);
		if (!valid.isOK()) {
			return valid;
		}
		
		Item newEquip = GameObjectFactory.getItem(equipRefId);

		attachSmith(player, oldEquip, newEquip);
		
		removeMeterial(player, materialRefId, needEquipNumber - 1);

		ItemFacade.removeItemById(player, oldEquip.getId(), 1, ItemOptSource.QiangHua);

		player.getPlayerMoneyComponent().subGold(subGold, ItemOptSource.QiangHua);
		
		ItemFacade.addItems(player, newEquip, ItemOptSource.QiangHua);

		return RuntimeResult.OK();
	}

	private static boolean removeMeterial(Player player,String materialRefId, int meterialNumber) {
		boolean flag = false;
		List<Item> qianghuaScrolls = new ArrayList<Item>(meterialNumber);
		for (int i = 0; i < meterialNumber; i++) {
			Item minFightValueEquip = getMinFightValueEquip(player, materialRefId);
			Item qiangHuaScroll = getQiangHuaScroll(minFightValueEquip);
			ItemFacade.removeItemById(player, minFightValueEquip.getId(), 1, ItemOptSource.QiangHua);
			if(qiangHuaScroll == null){
				continue;
			}else{
				qianghuaScrolls.add(qiangHuaScroll);
			}
		}
		if(qianghuaScrolls.size() > 0){
			ItemFacade.addItems(player, qianghuaScrolls, ItemOptSource.QiangHua);
		}
		return flag;

	}

	private static Item getMinFightValueEquip(Player player, String materialRefId) {
		List<ItemBagSlot> itemBagCollection = player.getItemBagComponent().getItemBag().getItemBagCollection();
		Item minFightEqup = null;
		for (ItemBagSlot slot : itemBagCollection) {
			if (slot.isEmpty()) {
				continue;
			}
			Item equip = slot.getItem();
			if (!StringUtils.equals(materialRefId, equip.getItemRefId())) {
				continue;
			}
			if (minFightEqup == null) {
				minFightEqup = equip;
				continue;
			}

			int crtfightValue = MGPropertyAccesser.getFightValue(equip.getProperty());
			int minfightValue = MGPropertyAccesser.getFightValue(minFightEqup.getProperty());
			if (crtfightValue < minfightValue) {
				minFightEqup = equip;
			}

		}

		return minFightEqup;

	}

	private static Item getQiangHuaScroll(Item meterialEquip) {
		byte strengtheningLevel = MGPropertyAccesser.getStrengtheningLevel(meterialEquip.getProperty());
		if (strengtheningLevel > 0) {
			boolean bindStatus = meterialEquip.binded();
			MGFenJieEquipmentConfig fenJieScrollConfig = (MGFenJieEquipmentConfig) GameRoot.getGameRefObjectManager().getManagedObject(MGFenJieEquipmentConfig.FenJieScroll_Id);
			MGFenJieScrollRef jieScrollRef = fenJieScrollConfig.getFenJieScrollConfigMap().get(strengtheningLevel);
			String scrollItemRefId = MGPropertyAccesser.getItemRefId(jieScrollRef.getProperty());
			Item item = GameObjectFactory.getItem(scrollItemRefId);
			item.setBindStatus(bindStatus ? (byte) 1 : (byte) 0);
			return item;
		}
		return null;
	}
	
	/**
	 * 附加
	 * @param player
	 * @param oldEquip
	 * @param newEquip
	 */
	private static void attachSmith(Player player, Item oldEquip, Item newEquip) {
		if (oldEquip.isNonPropertyItem()) {
			return;
		}
		PropertyDictionary property = oldEquip.getProperty();
		byte strengthenLevel = MGPropertyAccesser.getStrengtheningLevel(property);
		newEquip.getNewAttachPropertyIfNull().copyFrom(oldEquip.getNewAttachPropertyIfNull());
		// 替换洗练属性
		EquipmentSmithComponent itemSmithCompoent = EqiupmentComponentProvider.getEquipmentSmithComponent(oldEquip);
		PropertyDictionary xiLianPd = itemSmithCompoent.getEquipmentSmithMgr().getXiLianEquipmentSmith().getPropertyDictionary();
		itemSmithCompoent = EqiupmentComponentProvider.getEquipmentSmithComponent(newEquip);
		itemSmithCompoent.getEquipmentSmithMgr().getXiLianEquipmentSmith().getPropertyDictionary().copyFrom(xiLianPd);
		// 强化等级
		itemSmithCompoent.getEquipmentSmithMgr().getQiangHuaEquipmentSmith().qiangHuaFromSide(player, strengthenLevel);

		PropertyDictionary newProperty = itemSmithCompoent.getEquipmentSmithMgr().getPropertyDictionary();

		EquipmentSmithHelper.updateEquipmentProperty(player, newEquip, newProperty);

	}
	
	private static RuntimeResult valid(Player player) {
		int needEquipNumber = 3;
		String equipRefId = "equip_1_1000";
		int subGold = 1000;
		ItemRef equipRef = (ItemRef) GameRoot.getGameRefObjectManager().getManagedObject(equipRefId);
		if (equipRef == null) {
			return RuntimeResult.RuntimeApplicationError(MMORPGErrorCode.CODE_ITEM_NOTEXIEST);
		}
		if (MGPropertyAccesser.getItemType(equipRef.getProperty()) != ItemType.Equip) {
			return RuntimeResult.RuntimeApplicationError(MMORPGErrorCode.CODE_ITEM_NOTEXIEST);
		}
		// TODO 装备是否可进阶
		// if(!equipment.isCanUpGrade()){
		// return
		// RuntimeResult.RuntimeApplicationError(MMORPGErrorCode.CODE_ITEM_UpGRADEEQUIP_HIGHEST);
		// }
		if (!ItemFacade.isEnoughItem(player, equipRefId, needEquipNumber)) {
			return RuntimeResult.RuntimeApplicationError(MMORPGErrorCode.CODE_ITEM_UpGRADEEQUIP_LACK_MATERIAL);
		}
		if (player.getPlayerMoneyComponent().getGold() < subGold) {
			return RuntimeResult.RuntimeApplicationError(MMORPGErrorCode.CODE_ITEM_UpGRADEEQUIP_LACK_GOLD);
		}
		return RuntimeResult.OK();
	}
}
