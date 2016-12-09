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
package sophia.mmorpg.player.equipment;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import sophia.foundation.authentication.Identity;
import sophia.foundation.communication.core.ActionEventBase;
import sophia.foundation.communication.core.MessageFactory;
import sophia.foundation.data.PersistenceObject;
import sophia.foundation.property.PropertyDictionary;
import sophia.foundation.util.Position;
import sophia.game.GameRoot;
import sophia.game.component.communication.GameEvent;
import sophia.mmorpg.base.ConcreteComponent;
import sophia.mmorpg.base.scene.GameScene;
import sophia.mmorpg.base.scene.mgr.LootMgrComponent;
import sophia.mmorpg.code.MMORPGErrorCode;
import sophia.mmorpg.event.ResultEvent;
import sophia.mmorpg.item.Item;
import sophia.mmorpg.loot.Loot;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.equipment.event.C2G_Equip_List;
import sophia.mmorpg.player.equipment.event.C2G_Equip_PutOn;
import sophia.mmorpg.player.equipment.event.C2G_Equip_UnLoad;
import sophia.mmorpg.player.equipment.event.EquipmentEventDefines;
import sophia.mmorpg.player.equipment.event.G2C_Equip_List;
import sophia.mmorpg.player.equipment.event.G2C_Equip_Update;
import sophia.mmorpg.player.equipment.gameEvent.EquipPutOn_GE;
import sophia.mmorpg.player.exp.gameevent.PlayerLevelUp_GE;
import sophia.mmorpg.player.gameEvent.EnterWorld_SceneReady_GE;
import sophia.mmorpg.player.itemBag.ItemBag;
import sophia.mmorpg.player.itemBag.ItemFacade;
import sophia.mmorpg.player.itemBag.ItemOptSource;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;
import sophia.mmorpg.ref.editorgen.MGPropertySymbolDefines;
import sophia.mmorpg.stat.StatFunctions;
import sophia.mmorpg.stat.logs.StatEquip;
import sophia.mmorpg.utils.SFRandomUtils;

/**
 * 玩家-装备系统组件
 */
public final class PlayerEquipBodyConponent extends ConcreteComponent<Player> {
	private static final Logger logger = Logger.getLogger(PlayerEquipBodyConponent.class);
	Identity identity = null;
	private ItemBag itemBag;
	private EquipEffectMgr equipEffectMgr;
	private EquipMgr equipMgr;
	private byte bodyId;
	private byte position;
	private short gridId;
	private byte count = 1;
	private static final String PlayerLevelUp_GE_Id = PlayerLevelUp_GE.class.getSimpleName();
	private static final String EnterWorld_SceneReady_GE_Id = EnterWorld_SceneReady_GE.class.getSimpleName();
	private PersistenceObject persisteneceObject;
	private final PlayerEquipBody playerBody = new PlayerEquipBody();

	private PlayerEquipBodyRuntime equipmentRuntime;

	public PlayerEquipBodyConponent() {
	}

	public final PlayerEquipBody getPlayerBody() {
		return playerBody;
	}

	@Override
	public void ready() {
		itemBag = getConcreteParent().getItemBagComponent().getItemBag();
		addActionEventListener(EquipmentEventDefines.C2G_Equip_List);
		addActionEventListener(EquipmentEventDefines.C2G_Equip_PutOn);
		addActionEventListener(EquipmentEventDefines.C2G_Equip_UnLoad);
		addInterGameEventListener(EnterWorld_SceneReady_GE_Id);
		addInterGameEventListener(PlayerLevelUp_GE_Id);
		super.ready();
	}

	@Override
	public void suspend() {
		removeActionEventListener(EquipmentEventDefines.C2G_Equip_List);
		removeActionEventListener(EquipmentEventDefines.C2G_Equip_PutOn);
		removeActionEventListener(EquipmentEventDefines.C2G_Equip_UnLoad);
		removeInterGameEventListener(EnterWorld_SceneReady_GE_Id);
		removeInterGameEventListener(PlayerLevelUp_GE_Id);
		super.suspend();
	}

	@Override
	public void handleGameEvent(GameEvent<?> event) {
		if (event.isId(PlayerLevelUp_GE_Id)) {
			for (PlayerEquipBodyArea playerEquipBodyArea : getPlayerBody().getBodyAreaList()) {
				Item[] equipArray = playerEquipBodyArea.getEquipmentArray();
				Item item = null;
				if (equipArray.length == 1) {
					if (equipArray[0] != null) {
						item = equipArray[0];
					}
				} else {
					// 左部位
					if (equipArray[0] != null) {
						item = equipArray[0];
					}
					// 右部位
					if (equipArray[1] != null) {
						item = equipArray[1];
					}
				}
				if (item != null) {
					int fightValue = getConcreteParent().getFightPower(item.getProperty());
					MGPropertyAccesser.setOrPutFightValue(item.getProperty(), fightValue);
				}
			}
			G2C_Equip_List res = MessageFactory.getConcreteMessage(EquipmentEventDefines.G2C_Equip_List);
			res.setEquipMgr(equipMgr);
			GameRoot.sendMessage(identity, res);
		} else if (event.isId(EnterWorld_SceneReady_GE_Id)) {
			for (PlayerEquipBodyArea playerEquipBodyArea : getPlayerBody().getBodyAreaList()) {
				Item[] equipArray = playerEquipBodyArea.getEquipmentArray();
				Item item = null;
				if (equipArray.length == 1) {
					if (equipArray[0] != null) {
						item = equipArray[0];
					}
				} else {
					// 左部位
					if (equipArray[0] != null) {
						item = equipArray[0];
					}
					// 右部位
					if (equipArray[1] != null) {
						item = equipArray[1];
					}
				}		
				if (item != null) {
					getConcreteParent().getPlayerEquipBodyConponent().getEquipmentRuntime().equipTo(getConcreteParent(), item);
				}
			}

		}
	}

	@Override
	public void handleActionEvent(ActionEventBase event) {
		super.handleActionEvent(event);
		identity = event.getIdentity();
		short eventId = event.getActionEventId();

		switch (eventId) {
		case EquipmentEventDefines.C2G_Equip_List:
			equipList((C2G_Equip_List) event);
			break;
		case EquipmentEventDefines.C2G_Equip_PutOn:
			putOn((C2G_Equip_PutOn) event);
			break;
		case EquipmentEventDefines.C2G_Equip_UnLoad:
			unLoad((C2G_Equip_UnLoad) event);
			break;
		}
	}

	public void equipList(C2G_Equip_List event) {
		if (logger.isDebugEnabled()) {
			logger.debug("物品列表");
		}

		G2C_Equip_List res = MessageFactory.getConcreteMessage(EquipmentEventDefines.G2C_Equip_List);
		res.setEquipMgr(equipMgr);
		GameRoot.sendMessage(identity, res);
		logger.debug("return successed!");
	}

	public void putOn(C2G_Equip_PutOn event) {
		short eventId = event.getActionEventId();
		
		byte position = event.getPosition();
		byte bodyId = event.getBodyId();
		short gridId = event.getGridId();
		
		if (position != 0 && position != 1) {
			logger.error("inValid argument! position = " + position);
			return;
		}
		
		
		if (gridId < 0 || gridId >= itemBag.getItemBagCapacity()) { // 格索引是否超出范围
			logger.error("inValid argument! gridId = " + gridId);
			return;
		}
		
		PlayerEquipBodyArea area = getPlayerBody().getBodyArea(bodyId);
		if (area == null) {
			logger.error("inValid argument! bodyId = " + bodyId);
			return;
		}
		
		setPosition(position);
		setGridId(gridId);
		setBodyId(bodyId);

		if (logger.isDebugEnabled()) {
			logger.debug("穿上装备");
		}

		Item item = itemBag.getItemBySlot(event.getGridId());
		if (item == null) {
			ResultEvent.sendResult(identity, eventId, MMORPGErrorCode.CODE_ITEM_NOTEXIEST);
			if (logger.isDebugEnabled()) {
				logger.debug("在背包中指定的位置没有获得装备");
			}
			return;
		}
		Player player = getConcreteParent();
		PropertyDictionary playerPd = player.getProperty();
		PropertyDictionary itemPd = item.getItemRef().getProperty();

		byte playerGender = MGPropertyAccesser.getGender(playerPd);
		byte equipGender = MGPropertyAccesser.getGender(itemPd);
		byte playerProfessionId = MGPropertyAccesser.getProfessionId(playerPd);
		byte equipProfessionId = MGPropertyAccesser.getProfessionId(itemPd);
		int playerLevel = player.getExpComponent().getLevel();
		int playerKnight = MGPropertyAccesser.getKnight(playerPd); // 玩家爵位
		int equipLevel = MGPropertyAccesser.getEquipLevel(itemPd);
		int equipKnight = MGPropertyAccesser.getEquipKnight(itemPd);
		
		if (logger.isDebugEnabled()) {
			logger.debug("装备refId:" + item.getItemRef().getId());
			logger.debug("玩家性别:" + playerGender + "," + "装备要求性别:" + equipGender);
			logger.debug("玩家职业:" + playerProfessionId + "," + "装备要求职业:" + equipProfessionId);
			logger.debug("玩家等级:" + playerLevel + "," + "装备要求等级:" + equipLevel);
		}

		if (equipGender != 0 && playerGender != equipGender) {
			ResultEvent.sendResult(identity, eventId, MMORPGErrorCode.CODE_EQUIPMENT_CANTMATCHGENDER);
			if (logger.isDebugEnabled()) {
				logger.debug("玩家性别与装备穿戴性别要求不匹配");
			}
			return;
		}

		if (0 != equipProfessionId && playerProfessionId != equipProfessionId) {
			ResultEvent.sendResult(identity, eventId, MMORPGErrorCode.CODE_EQUIPMENT_CANTMATCHPROFESSION);
			if (logger.isDebugEnabled()) {
				logger.debug("玩家职业与装备穿戴职业要求不匹配");
			}
			return;
		}

		if (playerLevel < equipLevel) {
			ResultEvent.sendResult(identity, eventId, MMORPGErrorCode.CODE_EQUIPMENT_CANTMATCHLEVEL);
			if (logger.isDebugEnabled()) {
				logger.debug("玩家等级不够");
			}
			return;
		}
	    if (equipKnight != 0 && playerKnight < equipKnight) {
	    	ResultEvent.sendResult(identity, eventId, MMORPGErrorCode.CODE_EQUIPMENT_CANTMATCHKNIGHT);
			if (logger.isDebugEnabled()) {
				logger.debug("玩家爵位等级不够");
			}
			return ;
		}
		if (!area.equalBodyAreaId(item)) {
			ResultEvent.sendResult(identity, eventId, MMORPGErrorCode.CODE_EQUIPMENT_CANTMATCHBODYID);
			if (logger.isDebugEnabled()) {
				logger.debug("装备穿戴的位置和身体部位不匹配");
			}
			return;
		}

		try {
			equip(item, event.getBodyId());
			
			sendEquipPutOnGameEvent();
		} catch (Exception e) {
			ResultEvent.sendResult(identity, eventId, MMORPGErrorCode.CODE_EQUIPMENT_CANTMATCHBODYID);
			if (logger.isDebugEnabled()) {
				logger.debug(e.getMessage());
			}
		}

		G2C_Equip_Update equipRes = MessageFactory.getConcreteMessage(EquipmentEventDefines.G2C_Equip_Update);
		equipRes.setEventType((byte) 1);
		equipRes.setCount(count);
		equipRes.setBodyId(event.getBodyId());
		equipRes.setPosition(event.getPosition());
		equipRes.setItem(item);
		equipRes.setPlayer(getConcreteParent());
		GameRoot.sendMessage(identity, equipRes);
		logger.debug("return successed!");
	}
	
	private void sendEquipPutOnGameEvent() {
		EquipPutOn_GE equipPutOnGe = new EquipPutOn_GE();
		GameEvent<EquipPutOn_GE> ge = (GameEvent<EquipPutOn_GE>)GameEvent.getInstance(EquipPutOn_GE.class.getSimpleName(), equipPutOnGe);
		sendGameEvent(ge, getConcreteParent().getId());
	}

	public final void equip(Item item, byte bodyId) {
		PlayerEquipBodyArea area = getPlayerBody().getBodyArea(bodyId);
		Item old = null;
		int number = 1;
		if (item.getBindType() == 2) {
			item.setBindStatus((byte) 1);
		}

		if (area.isLeftRightBodyArea()) {
			old = area.setOrResetEquipment(item, position);
		} else {
			old = area.setOrResetEquipment(item);
		}

		Player player = getConcreteParent();
		boolean removeItem = player.getItemBagComponent().removeEventNotify(gridId, number, ItemOptSource.DeEquip);
		if (removeItem && old != null) {
			// 替换装备，将装备放入背包中
			List<Item> list = new ArrayList<Item>();
			list.add(old);
			ItemFacade.addItems(player, list, ItemOptSource.DeEquip);
			getEquipEffectMgr().detachAndSnapshot(old);
		}

		getEquipEffectMgr().attach(item);

		equipToClosure(item);
		// 之前部位没有穿戴， 或者穿戴了不同的装备
		if (old == null || !old.getItemRefId().equals(item.getItemRefId())) {
			changePropertyAndBroadcast(bodyId);
		}

		// 穿戴装备后，移动速度发生改变了的
		if (old != null) {
			changePropertyAndBroadcast(old);
			
			StatFunctions.EquipStat(getConcreteParent(), StatEquip.Deequip, old.getItemRefId(), ItemOptSource.DeEquip);
		
			G2C_Equip_Update equipRes = MessageFactory.getConcreteMessage(EquipmentEventDefines.G2C_Equip_Update);
			equipRes.setEventType((byte)2);
			equipRes.setCount(count);
			equipRes.setBodyId(bodyId);
			equipRes.setPosition(position);
			equipRes.setItem(old);
			equipRes.setPlayer(getConcreteParent());
			GameRoot.sendMessage(identity, equipRes);
		}
		StatFunctions.EquipStat(getConcreteParent(), StatEquip.Equip, item.getItemRefId(), ItemOptSource.DeEquip);
	}

	public void unLoad(C2G_Equip_UnLoad event) {
		short eventId = event.getActionEventId();
		byte bodyId = event.getBodyId();
		byte position = event.getPosition();
		
		
		if (position != 0 && position != 1) {
			logger.error("inValid argument! position = " + position);
			return;
		}
		
		PlayerEquipBodyArea area = getPlayerBody().getBodyArea(bodyId);
		if (area == null) {
			logger.error("inValid argument! bodyId = " + bodyId);
			return;
		}
		
		if (logger.isDebugEnabled()) {
			logger.debug("卸下装备");
		}

		if (itemBag.isFull()) {
			ResultEvent.sendResult(identity, eventId, MMORPGErrorCode.CODE_ITEM_FULL);
			if (logger.isDebugEnabled()) {
				logger.debug("不能卸载此装备,背包没有多余的格子");
			}
			return;
		}

		Item downEquip = null;
		// 到装备列表中根据指定的bodyId,position位置拿到该装备
		for (PlayerEquipBodyArea temp : playerBody.getBodyAreaList()) {
			if (temp.getId() == bodyId) {
				downEquip = temp.getEquipmentArray()[position];
			}
		}

		if (downEquip == null) {
			ResultEvent.sendResult(identity, eventId, MMORPGErrorCode.CODE_EQUIPMENT_CANTGETEQUIPMENT);
			if (logger.isDebugEnabled()) {
				logger.debug("没有获得对应位置上的装备");
			}
			return;
		}

		if (logger.isDebugEnabled()) {
			logger.debug("装备refId:" + downEquip.getItemRef().getId());
		}

		try {
			deequip(downEquip, event.getPosition(),ItemOptSource.DeEquip);
		} catch (Exception e) {
			ResultEvent.sendResult(identity, eventId, MMORPGErrorCode.CODE_EQUIPMENT_CANTMATCHBODYID);
			if (logger.isDebugEnabled()) {
				logger.debug(e.getMessage());
			}
		}

		G2C_Equip_Update res = MessageFactory.getConcreteMessage(EquipmentEventDefines.G2C_Equip_Update);
		res.setEventType((byte) 2);
		res.setCount(count);
		res.setBodyId(bodyId);
		res.setPosition(position);
		res.setPlayer(getConcreteParent());
		res.setItem(downEquip);
		GameRoot.sendMessage(identity, res);

		List<Item> list = new ArrayList<Item>();
		list.add(downEquip);
		Player player = getConcreteParent();
		ItemFacade.addItems(player, list, ItemOptSource.DeEquip);
		logger.debug("return successed!");
	}

	private final void deequip(Item equipment, byte position,byte source) {
		// 脱装备，此时装备的id对应于bodyId
		PropertyDictionary pd = equipment.getItemRef().getProperty();
		byte bodyId = MGPropertyAccesser.getAreaOfBody(pd);
		PlayerEquipBodyArea area = playerBody.getBodyArea(bodyId);
		if (area.isLeftRightBodyArea()) {
			area.removeEquipment(position);
		} else {
			area.removeEquipment();
		}

		getEquipEffectMgr().detach(equipment);

		changePropertyAndBroadcast(bodyId);

		changePropertyAndBroadcast(equipment);

		deepuipFromClosure(equipment);
		
		StatFunctions.EquipStat(getConcreteParent(), StatEquip.Deequip, equipment.getItemRefId(), source);
	}

	public final boolean modifyEquipmentNotify(Item equipment,byte bodyAreaId, byte posId) {
		G2C_Equip_Update res = MessageFactory.getConcreteMessage(EquipmentEventDefines.G2C_Equip_Update);
		res.setEventType((byte) 2);
		res.setCount((short) 1);
		res.setBodyId(bodyAreaId);
		res.setPosition(posId);
		res.setItem(equipment);
		res.setPlayer(getConcreteParent());
		GameRoot.sendMessage(identity, res);
		return true;
	}

	public void equipToClosure(Item equipment) {
		equipmentRuntime.equipTo(getConcreteParent(), equipment);
	}

	public void deepuipFromClosure(Item equipment) {
		equipmentRuntime.deequipFrom(getConcreteParent(), equipment);
	}

	/**
	 * 获取外观模型ID
	 * 
	 * @param bodyAreaId
	 * @return
	 */
	public int getModleId(byte bodyAreaId) {
		int modleId = 0;
		PlayerEquipBodyArea bodyArea = getPlayerBody().getBodyArea(bodyAreaId);
		Item equipment = bodyArea.getEquipment();
		if (equipment == null) {
			return modleId;
		}

		return MGPropertyAccesser.getModelId(equipment.getItemRef().getProperty());
	}

	/**
	 * 改变玩家装备模型属性，并且广播新的外观
	 * 
	 * @param bodyAreaId
	 */
	public void changePropertyAndBroadcast(byte bodyAreaId) {
		Player player = getConcreteParent();
		if (bodyAreaId == PlayerEquipBodyArea.weaponBodyId) {
			int modleId = getModleId(bodyAreaId);
			PropertyDictionary pd = new PropertyDictionary();
			MGPropertyAccesser.setOrPutWeaponModleId(pd, modleId);
			MGPropertyAccesser.setOrPutWeaponModleId(player.getProperty(), modleId);
			player.getAoiComponent().broadcastProperty(pd);
			player.notifyPorperty(pd);
		} else if (bodyAreaId == PlayerEquipBodyArea.clothesBodyId) {
			int modleId = getModleId(bodyAreaId);
			PropertyDictionary pd = new PropertyDictionary();
			MGPropertyAccesser.setOrPutArmorModleId(pd, modleId);
			MGPropertyAccesser.setOrPutArmorModleId(player.getProperty(), modleId);
			player.getAoiComponent().broadcastProperty(pd);
			player.notifyPorperty(pd);
		}
	}

	/**
	 * 改变玩家攻击速度、移动速度，广播通知
	 * 
	 * @return
	 */
	public void changePropertyAndBroadcast(Item equipment) {
		Player player = getConcreteParent();
		byte itemType = MGPropertyAccesser.getIsNonPropertyItem(equipment.getItemRef().getProperty());
		int playerCrtMoveSpeed = player.getFightPropertyMgrComponent().getFightPropertyMgr().getSnapshotValueById(MGPropertySymbolDefines.MoveSpeed_Id);
		int playerCrtAtkSpeed = player.getFightPropertyMgrComponent().getFightPropertyMgr().getSnapshotValueById(MGPropertySymbolDefines.AtkSpeed_Id);
		int additionMoveSpeedPer = 0;
		int additionAtkSpeedPer = 0;
		if (itemType == Item.Is_Property_Item) {
			additionMoveSpeedPer = MGPropertyAccesser.getMoveSpeedPer(equipment.getProperty());
			additionAtkSpeedPer = MGPropertyAccesser.getAtkSpeedPer(equipment.getProperty());
		} else {
			additionMoveSpeedPer = MGPropertyAccesser.getMoveSpeedPer(equipment.getItemRef().getProperty());
			additionAtkSpeedPer = MGPropertyAccesser.getAtkSpeedPer(equipment.getItemRef().getProperty());
		}

		if (additionMoveSpeedPer != 0) {
			PropertyDictionary property = new PropertyDictionary();
			MGPropertyAccesser.setOrPutMoveSpeed(property, playerCrtMoveSpeed);
			player.getAoiComponent().broadcastProperty(property);
		}

		if (additionAtkSpeedPer != 0) {
			PropertyDictionary property = new PropertyDictionary();
			MGPropertyAccesser.setOrPutAtkSpeed(property, playerCrtAtkSpeed);
			player.getAoiComponent().broadcastProperty(property);
		}

	}

	public List<Loot> dropItem(Player owner, int rate,byte source) {
		int random = SFRandomUtils.random100();
		if (random < rate) {
			Item minFightEquipment = getMinFightPowerEquipment();
			Item item = null;
			if (minFightEquipment == null) {
				return null;
			}
			
			PlayerEquipBodyArea area = playerBody.getBodyAreaEquip(minFightEquipment.getId());
			if (area.isLeftRightBodyArea()) {
				item = area.getEquipment(PlayerEquipBodyArea.Left_Position);
				if (item != null && StringUtils.equals(item.getId(), minFightEquipment.getId())) {
					deequip(minFightEquipment,PlayerEquipBodyArea.Left_Position,source);
					modifyEquipmentNotify(minFightEquipment,area.getId(),PlayerEquipBodyArea.Left_Position);
				} else {
					item = area.getEquipment(PlayerEquipBodyArea.Right_Position);
					if (item != null && StringUtils.equals(item.getId(), minFightEquipment.getId())) {
						deequip(minFightEquipment, PlayerEquipBodyArea.Right_Position,source);
						modifyEquipmentNotify(minFightEquipment,area.getId(),PlayerEquipBodyArea.Right_Position);
					}
				}

			} else {
				item = area.getEquipment();
				deequip(minFightEquipment, (byte)0,source);
				modifyEquipmentNotify(minFightEquipment,area.getId(),PlayerEquipBodyArea.Left_Position);
			}
			
			Player player = getConcreteParent();
			GameScene crtScene = player.getCrtScene();
			Position crtPosition = player.getCrtPosition();
			LootMgrComponent lootManager = crtScene.getLootMgrComponent();
			return lootManager.dropItem(crtPosition, item, owner);
		}

		return null;
	}

	private Item getMinFightPowerEquipment(){
		Item minFightEquipment = null;
		for (PlayerEquipBodyArea area : playerBody.getBodyAreaList()) {
			if (area.isLeftRightBodyArea()) {
				Item equipItem1 = area.getEquipment(PlayerEquipBodyArea.Left_Position);
				Item equipItem2 = area.getEquipment(PlayerEquipBodyArea.Right_Position);
				if ((equipItem1 != null && !equipItem1.binded()) || (equipItem2 != null && !equipItem2.binded())) {
					if (equipItem1 != null) {
						int fight1 = getEquipmentFightPower(equipItem1);
						if (minFightEquipment == null) {
							minFightEquipment = equipItem1;
						} else if (minFightEquipment != null) {
							int minFight = getEquipmentFightPower(minFightEquipment);
							if (minFight > fight1) {
								minFightEquipment = equipItem1;
							}
						}
					}
					if (equipItem2 != null) {
						int fight1 = getEquipmentFightPower(equipItem2);
						if (minFightEquipment == null) {
							minFightEquipment = equipItem2;
						} else if (minFightEquipment != null) {
							int minFight = getEquipmentFightPower(minFightEquipment);
							if (minFight > fight1) {
								minFightEquipment = equipItem2;
							}
						}
					}

				}

			} else {
				Item equipItem = area.getEquipment();
				if (equipItem != null && !equipItem.binded()) {
					int fight1 = getEquipmentFightPower(equipItem);
					if (minFightEquipment == null) {
						minFightEquipment = equipItem;
					} else if (minFightEquipment != null) {
						int minFight = getEquipmentFightPower(minFightEquipment);
						if (minFight > fight1) {
							minFightEquipment = equipItem;
						}
					}
				}

			}
		}
		return  minFightEquipment;
	}
	
	private int getEquipmentFightPower(Item equipItem) {
		PropertyDictionary pd = equipItem.isNonPropertyItem() ? equipItem.getItemRef().getEffectProperty() : equipItem.getProperty();
		int fight = getConcreteParent().getFightPower(pd);
		return fight;
	}

	public PersistenceObject getPersisteneceObject() {
		return persisteneceObject;
	}

	public void setPersisteneceObject(PersistenceObject persisteneceObject) {
		this.persisteneceObject = persisteneceObject;
	}

	public byte getBodyId() {
		return bodyId;
	}

	public void setBodyId(byte bodyId) {
		this.bodyId = bodyId;
	}

	public short getGridId() {
		return gridId;
	}

	public void setGridId(short gridId) {
		this.gridId = gridId;
	}

	public byte getPosition() {
		return position;
	}

	public void setPosition(byte position) {
		this.position = position;
	}

	public PlayerEquipBodyRuntime getEquipmentRuntime() {
		return equipmentRuntime;
	}

	public void setEquipmentRuntime(PlayerEquipBodyRuntime equipmentRuntime) {
		this.equipmentRuntime = equipmentRuntime;
	}

	public EquipEffectMgr getEquipEffectMgr() {
		return equipEffectMgr;
	}

	public void setEquipEffectMgr(EquipEffectMgr equipEffectMgr) {
		this.equipEffectMgr = equipEffectMgr;
	}

	public EquipMgr getEquipMgr() {
		return equipMgr;
	}

	public void setEquipMgr(EquipMgr equipMgr) {
		this.equipMgr = equipMgr;
	}

	
	public List<Item> getEquipedItems() {
		List<Item> items = new ArrayList<>();
		PlayerEquipBody equipBody = getPlayerBody();
		for (PlayerEquipBodyArea playerEquipBodyArea : equipBody.getBodyAreaList()) {
			Item[] equipArray = playerEquipBodyArea.getEquipmentArray();
			for (Item item : equipArray) {
				if (item != null) {
					items.add(item);
				}
			}

		}

		return items;
	}

}
