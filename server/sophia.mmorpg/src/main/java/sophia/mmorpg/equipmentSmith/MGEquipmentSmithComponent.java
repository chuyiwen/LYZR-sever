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
package sophia.mmorpg.equipmentSmith;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import sophia.foundation.authentication.Identity;
import sophia.foundation.communication.core.ActionEventBase;
import sophia.foundation.communication.core.MessageFactory;
import sophia.foundation.property.PropertyDictionary;
import sophia.game.GameRoot;
import sophia.game.component.communication.GameEvent;
import sophia.mmorpg.base.ConcreteComponent;
import sophia.mmorpg.code.MMORPGErrorCode;
import sophia.mmorpg.equipmentSmith.smith.EquipmentSmithHelper;
import sophia.mmorpg.equipmentSmith.smith.actionEvent.C2G_BAG_Streng;
import sophia.mmorpg.equipmentSmith.smith.actionEvent.C2G_BAG_StrengScroll;
import sophia.mmorpg.equipmentSmith.smith.actionEvent.C2G_Bag_Decompose;
import sophia.mmorpg.equipmentSmith.smith.actionEvent.C2G_Bag_Wash;
import sophia.mmorpg.equipmentSmith.smith.actionEvent.C2G_Body_Wash;
import sophia.mmorpg.equipmentSmith.smith.actionEvent.C2G_Equip_Streng;
import sophia.mmorpg.equipmentSmith.smith.actionEvent.C2G_Equip_StrengScroll;
import sophia.mmorpg.equipmentSmith.smith.actionEvent.G2C_Force_Open;
import sophia.mmorpg.equipmentSmith.smith.actionEvent.G2C_Streng_Ret;
import sophia.mmorpg.equipmentSmith.smith.actionEvent.StrengEquipmentEventDefines;
import sophia.mmorpg.equipmentSmith.smith.fenJie.MGFenJieEquipmentSmith;
import sophia.mmorpg.equipmentSmith.smith.gameEvent.StrengCount_GE;
import sophia.mmorpg.equipmentSmith.smith.gameEvent.WashCount_GE;
import sophia.mmorpg.equipmentSmith.smith.qiangHua.MGQiangHuaEquipmentSmith;
import sophia.mmorpg.equipmentSmith.smith.xiLian.MGXiLianEquipmentSmith;
import sophia.mmorpg.event.ResultEvent;
import sophia.mmorpg.item.Item;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.chat.sysytem.SpecialEffectsType;
import sophia.mmorpg.player.chat.sysytem.SystemPromptFacade;
import sophia.mmorpg.player.chat.sysytem.SystemPromptPosition;
import sophia.mmorpg.player.equipment.PlayerEquipBodyArea;
import sophia.mmorpg.player.equipment.event.EquipmentEventDefines;
import sophia.mmorpg.player.equipment.event.G2C_Equip_Update;
import sophia.mmorpg.player.exp.gameevent.PlayerLevelUp_GE;
import sophia.mmorpg.player.itemBag.ItemBag;
import sophia.mmorpg.player.itemBag.ItemBagPartialRenewalData;
import sophia.mmorpg.player.itemBag.ItemCode;
import sophia.mmorpg.player.itemBag.event.G2C_Item_Update;
import sophia.mmorpg.player.itemBag.event.ItemBagEventDefines;
import sophia.mmorpg.player.scene.PlayerSceneComponent;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;
import sophia.mmorpg.stat.StatFunctions;

/**
 * 装备-锻造-什么都在这
 */
public final class MGEquipmentSmithComponent extends ConcreteComponent<Player> {
	private static final Logger logger = Logger.getLogger(MGEquipmentSmithComponent.class);
	public static final String Tag = "MGEquipmentSmithComponent";
	private MGFenJieEquipmentSmith fenJieSmith = new MGFenJieEquipmentSmith();
	private static final String StrengCount_GE_Id = StrengCount_GE.class.getSimpleName();
	private static final String WashCount_GE_Id = WashCount_GE.class.getSimpleName();
	private static final String PlayerLevelUp_GE_Id = PlayerLevelUp_GE.class.getSimpleName();
	private static final int OPEN_STRENG_LEVEL = 50;
	private static final int OPEN_WASH_LEVEL = 50;

	public MGEquipmentSmithComponent() {

	}

	@Override
	public void ready() {
		addActionEventListener(StrengEquipmentEventDefines.C2G_BAG_Streng);
		addActionEventListener(StrengEquipmentEventDefines.C2G_BAG_StrengScroll);
		addActionEventListener(StrengEquipmentEventDefines.C2G_Equip_Streng);
		addActionEventListener(StrengEquipmentEventDefines.C2G_Equip_StrengScroll);
		addActionEventListener(StrengEquipmentEventDefines.C2G_Bag_Wash);
		addActionEventListener(StrengEquipmentEventDefines.C2G_Body_Wash);
		addActionEventListener(StrengEquipmentEventDefines.C2G_Bag_Decompose);
		addInterGameEventListener(PlayerSceneComponent.EnterWorld_SceneReady_GE_Id);
		addInterGameEventListener(PlayerLevelUp_GE_Id);
		super.ready();
	}

	@Override
	public void suspend() {
		removeActionEventListener(StrengEquipmentEventDefines.C2G_BAG_Streng);
		removeActionEventListener(StrengEquipmentEventDefines.C2G_BAG_StrengScroll);
		removeActionEventListener(StrengEquipmentEventDefines.C2G_Equip_Streng);
		removeActionEventListener(StrengEquipmentEventDefines.C2G_Equip_StrengScroll);
		removeActionEventListener(StrengEquipmentEventDefines.C2G_Bag_Wash);
		removeActionEventListener(StrengEquipmentEventDefines.C2G_Body_Wash);
		removeActionEventListener(StrengEquipmentEventDefines.C2G_Bag_Decompose);
		removeInterGameEventListener(PlayerSceneComponent.EnterWorld_SceneReady_GE_Id);
		removeInterGameEventListener(PlayerLevelUp_GE_Id);
		super.suspend();
	}

	@Override
	public void handleGameEvent(GameEvent<?> event) {
		if (event.isId(PlayerSceneComponent.EnterWorld_SceneReady_GE_Id) || event.isId(PlayerLevelUp_GE_Id)) {
			sendOpenSystemMessage();
		}  
	}

	private void sendOpenSystemMessage() {
		int level = getConcreteParent().getExpComponent().getLevel();
		byte isStrengOpen = 0;
		byte isWashOpen = 0;
		if (level >= OPEN_STRENG_LEVEL) {
			isStrengOpen = 1;
		}
		if (level >= OPEN_WASH_LEVEL) {
			isWashOpen = 1;
		}
		G2C_Force_Open res = MessageFactory.getConcreteMessage(StrengEquipmentEventDefines.G2C_Force_Open);
		res.setIsStrengOpen(isStrengOpen);
		res.setIsWashOpen(isWashOpen);
		GameRoot.sendMessage(getConcreteParent().getIdentity(), res);
	}

	@Override
	public void handleActionEvent(ActionEventBase event) {

		short actionEventId = event.getActionEventId();
		Identity identity = event.getIdentity();

		switch (actionEventId) {
		case StrengEquipmentEventDefines.C2G_BAG_Streng:
			handle_BAG_Streng((C2G_BAG_Streng) event, actionEventId, identity);
			break;
		case StrengEquipmentEventDefines.C2G_BAG_StrengScroll:
			handle_BAG_StrengScroll((C2G_BAG_StrengScroll) event, actionEventId, identity);
			break;
		case StrengEquipmentEventDefines.C2G_Equip_Streng:
			handle_Equip_Streng((C2G_Equip_Streng) event, actionEventId, identity);
			break;
		case StrengEquipmentEventDefines.C2G_Equip_StrengScroll:
			handle_Equip_StrengScroll((C2G_Equip_StrengScroll) event, actionEventId, identity);
			break;
		case StrengEquipmentEventDefines.C2G_Bag_Wash:
			handle_Bag_Wash((C2G_Bag_Wash) event, actionEventId, identity);
			break;
		case StrengEquipmentEventDefines.C2G_Body_Wash:
			handle_Body_Wash((C2G_Body_Wash) event, actionEventId, identity);
			break;
		case StrengEquipmentEventDefines.C2G_Bag_Decompose:
			handle_Bag_Decompose((C2G_Bag_Decompose) event, actionEventId, identity);
			break;

		default:
			break;
		}
		super.handleActionEvent(event);
	}

	/**
	 * 背包列表物品分解
	 * 
	 * @param event
	 * @param actionEventId
	 * @param identity
	 */
	private void handle_Bag_Decompose(C2G_Bag_Decompose event, short actionEventId, Identity identity) {

		Player player = getConcreteParent();
		List<Item> items = new ArrayList<Item>();
		for (short gridId : event.getGrids()) {
			ItemBag itemBag = player.getItemBagComponent().getItemBag();
			if (gridId < 0 || gridId >= itemBag.getItemBagCapacity()) { // 格索引是否超出范围
				ResultEvent.sendResult(identity, actionEventId, MMORPGErrorCode.CODE_ITEM_USERSLOT);
				if (logger.isDebugEnabled()) {
					logger.debug("Illegal Index ");
				}
				return;
			}
			Item equipment = player.getItemBagComponent().getItemBag().getItemBySlot(gridId);
			if (equipment == null) {
				ResultEvent.sendResult(identity, actionEventId, MMORPGErrorCode.CODE_ITEM_NOTEXIEST);
				continue;
			} else if (!equipment.canSale()) {
				continue;
			}
			items.add(equipment);
		}
		fenJieSmith.fenJie(player, items);

		fenJieSmith.reset();
	}

	/**
	 * 装备列表物品洗练
	 * 
	 * @param event
	 * @param actionEventId
	 * @param identity
	 */
	private void handle_Body_Wash(C2G_Body_Wash event, short actionEventId, Identity identity) {

		byte bodyAreaId = event.getBodyAreaId();
		byte posId = event.getPosId();
		boolean isUseBinded = event.isUseBined();
		List<Short> symbols = event.getSymbols();

		Player player = getConcreteParent();
		PlayerEquipBodyArea area = player.getPlayerEquipBodyConponent().getPlayerBody().getBodyArea(bodyAreaId);
		Item equipment = null;
		if (area.isLeftRightBodyArea()) {
			equipment = area.getEquipment(posId);
		} else {
			equipment = area.getEquipment();
		}
		if (equipment == null) {
			ResultEvent.sendResult(identity, actionEventId, MMORPGErrorCode.CODE_ITEM_NOTEXIEST);
			return;
		}
		EquipmentSmithComponent itemSmithCompoent = EqiupmentComponentProvider.getEquipmentSmithComponent(equipment);

		MGXiLianEquipmentSmith xiLianEquipmentSmith = itemSmithCompoent.getEquipmentSmithMgr().getXiLianEquipmentSmith();
		getConcreteParent().getPlayerEquipBodyConponent().getEquipEffectMgr().detachAndSnapshot(equipment);
		if (xiLianEquipmentSmith.xiLian(player, symbols, isUseBinded)) {

			PropertyDictionary newProperty = itemSmithCompoent.getEquipmentSmithMgr().getPropertyDictionary();
			EquipmentSmithHelper.updateEquipmentProperty(player, equipment, newProperty);

			sendEquipMessage(equipment, bodyAreaId, posId);
			sendWashCountEvent();
			boolean isPropertyChange = xiLianEquipmentSmith.isPropertyChange();
			if (!isPropertyChange) {
				String tips = "属性不变";
				SystemPromptFacade.sendMsgSpecialEffects(player, tips, SystemPromptPosition.POSITION_RIGHT_1, SpecialEffectsType.SPECIAL_EFFECTS_TYPE_GREEN);
			}
			xiLianEquipmentSmith.setPropertyChange(false);

		} else {
			if (logger.isDebugEnabled()) {
				logger.debug("洗练条件不满足");
			}

		}
		StatFunctions.XiLianStat(getConcreteParent(), equipment.getItemRefId());
		getConcreteParent().getPlayerEquipBodyConponent().getEquipEffectMgr().attach(equipment);
	}

	/**
	 * 背包列表物品洗练
	 * 
	 * @param event
	 * @param actionEventId
	 * @param identity
	 */
	private void handle_Bag_Wash(C2G_Bag_Wash event, short actionEventId, Identity identity) {

		short gridId = event.getGridId();
		List<Short> symbols = event.getSymbols();
		boolean isUseBinded = event.isUseBinded();
		Player player = getConcreteParent();
		ItemBag itemBag = player.getItemBagComponent().getItemBag();
		if (gridId < 0 || gridId >= itemBag.getItemBagCapacity()) { // 格索引是否超出范围
			ResultEvent.sendResult(identity, actionEventId, MMORPGErrorCode.CODE_ITEM_USERSLOT);
			if (logger.isDebugEnabled()) {
				logger.debug("Illegal Index ");
			}
			return;
		}
		Item equipment = itemBag.getItemBySlot(gridId);
		if (equipment == null) {
			ResultEvent.sendResult(identity, actionEventId, MMORPGErrorCode.CODE_ITEM_NOTEXIEST);
			return;
		}
		EquipmentSmithComponent itemSmithCompoent = EqiupmentComponentProvider.getEquipmentSmithComponent(equipment);

		MGXiLianEquipmentSmith xiLianEquipmentSmith = itemSmithCompoent.getEquipmentSmithMgr().getXiLianEquipmentSmith();

		if (xiLianEquipmentSmith.xiLian(player, symbols, isUseBinded)) {
			if (logger.isDebugEnabled()) {
				logger.debug("洗练完成");
			}
			PropertyDictionary newProperty = itemSmithCompoent.getEquipmentSmithMgr().getPropertyDictionary();
			EquipmentSmithHelper.updateEquipmentProperty(player, equipment, newProperty);
			// 发送改动消息
			sendItemBagMessage(itemBag, gridId);
			itemSmithCompoent.getEquipmentSmithMgr().getPropertyDictionary().clear();
			sendWashCountEvent();
			boolean isPropertyChange = xiLianEquipmentSmith.isPropertyChange();
			if (!isPropertyChange) {
				String tips = "属性不变";
				SystemPromptFacade.sendMsgSpecialEffects(player, tips, SystemPromptPosition.POSITION_RIGHT_1, SpecialEffectsType.SPECIAL_EFFECTS_TYPE_GREEN);
			}
			xiLianEquipmentSmith.setPropertyChange(false);

		} else {
			if (logger.isDebugEnabled()) {
				logger.debug("洗练条件不足");
			}
		}
		StatFunctions.XiLianStat(getConcreteParent(), equipment.getItemRefId());
	}

	private void handle_Equip_StrengScroll(C2G_Equip_StrengScroll event, short actionEventId, Identity identity) {
		short scrollGridId = event.getScrollGridId();
		byte bodyAreaId = event.getBodyAreaId();
		byte posId = event.getPosId();
		Player player = getConcreteParent();
		PlayerEquipBodyArea area = player.getPlayerEquipBodyConponent().getPlayerBody().getBodyArea(bodyAreaId);
		Item equipment = null;
		if (area.isLeftRightBodyArea()) {
			equipment = area.getEquipment(posId);
		} else {
			equipment = area.getEquipment();
		}
		if (equipment == null) {
			ResultEvent.sendResult(identity, actionEventId, MMORPGErrorCode.CODE_ITEM_NOTEXIEST);
			return;
		}
		EquipmentSmithComponent itemSmithCompoent = EqiupmentComponentProvider.getEquipmentSmithComponent(equipment);

		MGQiangHuaEquipmentSmith qiangHuaEquipmentSmith = itemSmithCompoent.getEquipmentSmithMgr().getQiangHuaEquipmentSmith();
		getConcreteParent().getPlayerEquipBodyConponent().getEquipEffectMgr().detachAndSnapshot(equipment);
		byte result = qiangHuaEquipmentSmith.qiangHuaByScroll(player, scrollGridId);
		if (result == MGQiangHuaEquipmentSmith.STRENG_SUCCESS) {
			if (logger.isDebugEnabled()) {
				logger.debug("强化完成");
			}
			PropertyDictionary newProperty = itemSmithCompoent.getEquipmentSmithMgr().getPropertyDictionary();
			EquipmentSmithHelper.updateEquipmentProperty(player, equipment, newProperty);

			// 发送强化结果
			sendStrengResult(result);
			sendEquipMessage(equipment, bodyAreaId, posId);
			sendStrengCountEvent();

			itemSmithCompoent.getEquipmentSmithMgr().getPropertyDictionary().clear();
			int level = MGPropertyAccesser.getStrengtheningLevel(equipment.getProperty());
			StatFunctions.QiangHuaStat(player, result, level, equipment.getItemRefId());
		} else {
			if (logger.isDebugEnabled()) {
				logger.debug("强化条件不满足");
			}
			int level = MGPropertyAccesser.getStrengtheningLevel(equipment.getProperty());
			level = level < 0 ? 0 : level;
			StatFunctions.QiangHuaStat(player, result, level, equipment.getItemRefId());
		}
		getConcreteParent().getPlayerEquipBodyConponent().getEquipEffectMgr().attach(equipment);

	}

	private void handle_Equip_Streng(C2G_Equip_Streng event, short actionEventId, Identity identity) {
		byte bodyAreaId = event.getBodyAreaId();
		byte posId = event.getPosId();
		int yuanbao = event.getYuanbao();
		byte strengthLevel = event.getStrengthLevel();
		boolean isUseBinded = event.isUseBinded();
		if (yuanbao < 0) {
			return;
		}
		if (strengthLevel < 0 || strengthLevel > 12) {
			return;
		}
		Player player = getConcreteParent();
		PlayerEquipBodyArea area = player.getPlayerEquipBodyConponent().getPlayerBody().getBodyArea(bodyAreaId);
		Item equipment = null;
		if (area.isLeftRightBodyArea()) {
			equipment = area.getEquipment(posId);
		} else {
			equipment = area.getEquipment();
		}
		if (equipment == null) {
			ResultEvent.sendResult(identity, actionEventId, MMORPGErrorCode.CODE_ITEM_NOTEXIEST);
			return;
		}
		EquipmentSmithComponent itemSmithCompoent = EqiupmentComponentProvider.getEquipmentSmithComponent(equipment);

		MGQiangHuaEquipmentSmith qiangHuaEquipmentSmith = itemSmithCompoent.getEquipmentSmithMgr().getQiangHuaEquipmentSmith();
		getConcreteParent().getPlayerEquipBodyConponent().getEquipEffectMgr().detachAndSnapshot(equipment);
		byte result = qiangHuaEquipmentSmith.qiangHua(player, yuanbao, strengthLevel, isUseBinded);

		if (result == MGQiangHuaEquipmentSmith.STRENG_SUCCESS || result == MGQiangHuaEquipmentSmith.STRENG_FAILED) {
			if (logger.isDebugEnabled()) {
				logger.debug("强化完成");
			}
			PropertyDictionary newProperty = itemSmithCompoent.getEquipmentSmithMgr().getPropertyDictionary();
			EquipmentSmithHelper.updateEquipmentProperty(player, equipment, newProperty);

			// 发送强化结果
			sendStrengResult(result);
			sendEquipMessage(equipment, bodyAreaId, posId);
			sendStrengCountEvent();
			itemSmithCompoent.getEquipmentSmithMgr().getPropertyDictionary().clear();

		} else {
			if (logger.isDebugEnabled()) {
				logger.debug("强化条件不满足");
			}

		}
		getConcreteParent().getPlayerEquipBodyConponent().getEquipEffectMgr().attach(equipment);

		int level = MGPropertyAccesser.getStrengtheningLevel(equipment.getProperty());
		level = level < 0 ? 0 : level;
		StatFunctions.QiangHuaStat(player, result, level, equipment.getItemRefId());
	}

	private void handle_BAG_StrengScroll(C2G_BAG_StrengScroll event, short actionEventId, Identity identity) {
		short scrollGridId = event.getScrollGridId();
		short gridId = event.getGridId();

		Player player = getConcreteParent();
		ItemBag itemBag = player.getItemBagComponent().getItemBag();
		if (gridId < 0 || gridId >= itemBag.getItemBagCapacity()) { // 格索引是否超出范围
			ResultEvent.sendResult(identity, actionEventId, MMORPGErrorCode.CODE_ITEM_USERSLOT);
			if (logger.isDebugEnabled()) {
				logger.debug("Illegal Index ");
			}
			return;
		}
		Item equipment = itemBag.getItemBySlot(gridId);
		if (equipment == null) {
			ResultEvent.sendResult(identity, actionEventId, MMORPGErrorCode.CODE_ITEM_NOTEXIEST);
			return;
		}
		EquipmentSmithComponent itemSmithCompoent = EqiupmentComponentProvider.getEquipmentSmithComponent(equipment);

		MGQiangHuaEquipmentSmith qiangHuaEquipmentSmith = itemSmithCompoent.getEquipmentSmithMgr().getQiangHuaEquipmentSmith();

		byte result = qiangHuaEquipmentSmith.qiangHuaByScroll(player, scrollGridId);

		if (result == MGQiangHuaEquipmentSmith.STRENG_SUCCESS) {
			if (logger.isDebugEnabled()) {
				logger.debug("强化完成");
			}
			PropertyDictionary newProperty = itemSmithCompoent.getEquipmentSmithMgr().getPropertyDictionary();
			EquipmentSmithHelper.updateEquipmentProperty(player, equipment, newProperty);

			sendStrengResult(result);
			sendItemBagMessage(itemBag, gridId);

			itemSmithCompoent.getEquipmentSmithMgr().getPropertyDictionary().clear();
			sendStrengCountEvent();
		} else {
			if (logger.isDebugEnabled()) {
				logger.debug("强化条件不满足");
			}
		}
		int level = MGPropertyAccesser.getStrengtheningLevel(equipment.getProperty());
		level = level < 0 ? 0 : level;
		StatFunctions.QiangHuaStat(player, result, level, equipment.getItemRefId());
	}

	private void handle_BAG_Streng(C2G_BAG_Streng event, short actionEventId, Identity identity) {
		short gridId = event.getGridId();
		int unbindedGold = event.getYuanbao();
		byte strengthLevel = event.getStrengthLevel();
		boolean isUseBinded = event.isUseBinded();
		if (unbindedGold < 0) {
			return;
		}
		if (strengthLevel < 0 || strengthLevel > 12) {
			return;
		}
		Player player = getConcreteParent();
		ItemBag itemBag = player.getItemBagComponent().getItemBag();
		if (gridId < 0 || gridId >= itemBag.getItemBagCapacity()) { // 格索引是否超出范围
			ResultEvent.sendResult(identity, actionEventId, MMORPGErrorCode.CODE_ITEM_USERSLOT);
			if (logger.isDebugEnabled()) {
				logger.debug("Illegal Index ");
			}
			return;
		}
		Item equipment = itemBag.getItemBySlot(gridId);
		if (equipment == null) {
			ResultEvent.sendResult(identity, actionEventId, MMORPGErrorCode.CODE_ITEM_NOTEXIEST);
			return;
		}
		EquipmentSmithComponent itemSmithCompoent = EqiupmentComponentProvider.getEquipmentSmithComponent(equipment);
		MGQiangHuaEquipmentSmith qiangHuaEquipmentSmith = itemSmithCompoent.getEquipmentSmithMgr().getQiangHuaEquipmentSmith();
		byte result = qiangHuaEquipmentSmith.qiangHua(player, unbindedGold, strengthLevel, isUseBinded);
		if (result == MGQiangHuaEquipmentSmith.STRENG_SUCCESS || result == MGQiangHuaEquipmentSmith.STRENG_FAILED) {
			if (logger.isDebugEnabled()) {
				logger.debug("强化完成");
			}

			PropertyDictionary newProperty = itemSmithCompoent.getEquipmentSmithMgr().getPropertyDictionary();

			EquipmentSmithHelper.updateEquipmentProperty(player, equipment, newProperty);

			sendStrengResult(result);

			sendItemBagMessage(itemBag, gridId);

			sendStrengCountEvent();

			itemSmithCompoent.getEquipmentSmithMgr().getPropertyDictionary().clear();

		} else {
			if (logger.isDebugEnabled()) {
				logger.debug("强化条件不满足");
			}
		}
		int level = MGPropertyAccesser.getStrengtheningLevel(equipment.getProperty());
		level = level < 0 ? 0 : level;
		StatFunctions.QiangHuaStat(player, result, level, equipment.getItemRefId());

	}

	/**
	 * 外部强化调用接口
	 * 
	 * @param equipment
	 * @param strengthenLevel
	 */
	public void handle_BAG_Streng(Item equipment, byte strengthenLevel, int gridId) {
		Player player = getConcreteParent();
		ItemBag itemBag = player.getItemBagComponent().getItemBag();
		if (strengthenLevel < 0 || strengthenLevel > 12) {
			return;
		}
		if (equipment == null) {
			return;
		}
		EquipmentSmithComponent itemSmithCompoent = EqiupmentComponentProvider.getEquipmentSmithComponent(equipment);
		MGQiangHuaEquipmentSmith qiangHuaEquipmentSmith = itemSmithCompoent.getEquipmentSmithMgr().getQiangHuaEquipmentSmith();
		byte result = qiangHuaEquipmentSmith.qiangHuaFromSide(player, strengthenLevel);

		if (result == MGQiangHuaEquipmentSmith.STRENG_SUCCESS || result == MGQiangHuaEquipmentSmith.STRENG_FAILED) {
			if (logger.isDebugEnabled()) {
				logger.debug("强化完成");
			}

			PropertyDictionary newProperty = itemSmithCompoent.getEquipmentSmithMgr().getPropertyDictionary();
			EquipmentSmithHelper.updateEquipmentProperty(player, equipment, newProperty);
			sendStrengResult(result);

			sendItemBagMessage(itemBag, gridId);

			sendStrengCountEvent();

			itemSmithCompoent.getEquipmentSmithMgr().getPropertyDictionary().clear();

		}

	}

	/**
	 * 外部调用的已装备的强化接口，属性的附加(attach, detach)由调用者去执行
	 * 
	 * @param equipment
	 * @param strengthenLevel
	 * @param bodyAreaId
	 * @param posId
	 */
	public void handle_Equip_Streng(Item equipment, byte strengthenLevel, byte bodyAreaId, byte posId) {
		if (strengthenLevel < 0 || strengthenLevel > 12) {
			return;
		}
		Player player = getConcreteParent();
		if (equipment == null) {
			return;
		}
		EquipmentSmithComponent itemSmithCompoent = EqiupmentComponentProvider.getEquipmentSmithComponent(equipment);
		MGQiangHuaEquipmentSmith qiangHuaEquipmentSmith = itemSmithCompoent.getEquipmentSmithMgr().getQiangHuaEquipmentSmith();

		byte result = qiangHuaEquipmentSmith.qiangHuaFromSide(player, strengthenLevel);

		if (result == MGQiangHuaEquipmentSmith.STRENG_SUCCESS || result == MGQiangHuaEquipmentSmith.STRENG_FAILED) {
			if (logger.isDebugEnabled()) {
				logger.debug("强化完成");
			}
			PropertyDictionary newProperty = itemSmithCompoent.getEquipmentSmithMgr().getPropertyDictionary();
			EquipmentSmithHelper.updateEquipmentProperty(player, equipment, newProperty);
			sendStrengResult(result);
			sendEquipMessage(equipment, bodyAreaId, posId);
			itemSmithCompoent.getEquipmentSmithMgr().getPropertyDictionary().clear();
			sendStrengCountEvent();

		}

	}

	
	// ------------------------------------ send
	// GameEvent--------------------------------//
	private void sendStrengCountEvent() {

		int strengTheningCount = MGPropertyAccesser.getStrengTheningCount(getConcreteParent().getProperty());
		StrengCount_GE strengAndWashCount_GE = new StrengCount_GE(strengTheningCount);
		GameEvent<StrengCount_GE> ge = (GameEvent<StrengCount_GE>) GameEvent.getInstance(StrengCount_GE_Id, strengAndWashCount_GE);
		sendGameEvent(ge, getConcreteParent().getId());

	}

	private void sendWashCountEvent() {

		int washCount = MGPropertyAccesser.getWashCount(getConcreteParent().getProperty());
		WashCount_GE washCount_GE = new WashCount_GE(washCount);
		GameEvent<WashCount_GE> ge = (GameEvent<WashCount_GE>) GameEvent.getInstance(WashCount_GE_Id, washCount_GE);
		sendGameEvent(ge, getConcreteParent().getId());

	}

	// ---------------------------------- send updateMessage
	// -----------------------------//
	private void sendStrengResult(byte result) {
		G2C_Streng_Ret ret = MessageFactory.getConcreteMessage(StrengEquipmentEventDefines.G2C_Streng_Ret);
		ret.setRet(result);
		GameRoot.sendMessage(getConcreteParent().getIdentity(), ret);
	}

	private void sendItemBagMessage(ItemBag itemBag, int gridId) {
		Player player = getConcreteParent();
		// 发送改动消息
		ItemBagPartialRenewalData itemBagPartialRenewalData = itemBag.getEquipmentSmithPartialRenewalData();
		itemBagPartialRenewalData.setSucceed(true);
		itemBagPartialRenewalData.addItemBagSlot(itemBag.getItemSlotByIndex(gridId));

		G2C_Item_Update update = MessageFactory.getConcreteMessage(ItemBagEventDefines.G2C_Item_Update);
		update.setItemBagPartialRenewalData(itemBagPartialRenewalData);
		update.setOptype(ItemCode.MODIFY_UPDATE);
		update.setItemBag(itemBag);
		update.setPlayer(player);
		GameRoot.sendMessage(player.getIdentity(), update);
	}

	private void sendEquipMessage(Item equipment, byte bodyAreaId, byte posId) {
		Player player = getConcreteParent();
		G2C_Equip_Update update = MessageFactory.getConcreteMessage(EquipmentEventDefines.G2C_Equip_Update);
		update.setEventType(ItemCode.MODIFY_UPDATE);
		update.setBodyId(bodyAreaId);
		update.setCount((short) 1);
		update.setPosition(posId);
		update.setItem(equipment);
		update.setPlayer(player);
		GameRoot.sendMessage(player.getIdentity(), update);
	}
}