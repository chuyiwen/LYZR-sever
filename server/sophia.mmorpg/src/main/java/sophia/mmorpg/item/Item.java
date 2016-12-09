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
package sophia.mmorpg.item;

import groovy.lang.Closure;

import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

import sophia.foundation.property.PropertyDictionary;
import sophia.game.GameRoot;
import sophia.game.component.GameObject;
import sophia.mmorpg.GameObjectFactory;
import sophia.mmorpg.base.sprite.fightProperty.FightEffectProperty;
import sophia.mmorpg.equipmentSmith.EqiupmentComponentProvider;
import sophia.mmorpg.equipmentSmith.EquipmentSmithComponent;
import sophia.mmorpg.item.ref.ItemRef;
import sophia.mmorpg.player.itemBag.ItemType;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;
import sophia.mmorpg.utils.RuntimeResult;

import com.google.common.base.Preconditions;

public class Item extends GameObject implements ItemBagProperty {

	public static final byte Is_Property_Item = 1;
	/**
	 * 绑定类型 ： 0 = 不绑定 1 = 一入背包立马绑定 2 = 装备绑定
	 */
	public static final byte Never_Bind = 0;
	public static final byte At_Once_Bind = 1;
	public static final byte Equip_Bind = 2;

	/**
	 * 绑定状态
	 */
	public static final byte UnBinded = 0;
	public static final byte Binded = 1;

	public static final byte IsHighestEquipment = 1;

	private PropertyDictionary attachProperty;
	private String itemRefId;

	public Item() {
		setId(UUID.randomUUID().toString());
	}

	public Item(ItemRef itemRef) {
		this.itemRefId = itemRef.getId();
		setId(UUID.randomUUID().toString());
	}

	public Item(ItemRef itemRef, String id) {
		this.itemRefId = itemRef.getId();
		setId(id);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + getNumber();
		result = prime * result + getBindType();
		result = prime * result + ((itemRefId == null) ? 0 : itemRefId.hashCode());
		result = prime * result + getBindStatus();
		result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
		result = prime * result + (isNonPropertyItem() ? 1231 : 1237);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		Item other = (Item) obj;
		if (itemRefId == null) {
			if (other.itemRefId != null)
				return false;
		} else if (!StringUtils.equals(itemRefId, other.itemRefId))
			return false;
		return true;
	}

	/**
	 * ItemRef相等
	 * 
	 * @param other
	 * @return
	 */
	public final boolean equalItemRef(Item other) {
		Preconditions.checkNotNull(other);

		return StringUtils.equals(itemRefId, other.itemRefId);
	}

	/**
	 * ItemRef相等+绑定状态相等；如果是有属性的物品+id相等
	 * 
	 * @param other
	 * @return
	 */
	public final boolean equalItem(Item other) {
		if (!StringUtils.equals(itemRefId, other.itemRefId)) // TODO 考虑null的情况
			return false;
		if (isNonPropertyItem()) {
			if (getBindStatus() != other.getBindStatus())
				return false;
		} else {
			if (!StringUtils.equals(getId(), other.getId()))
				return false;
			if (getBindStatus() != other.getBindStatus())
				return false;
		}
		return true;
	}

	public final String getItemRefId() {
		return itemRefId;
	}

	public final void setItemRef(String itemRef) {
		this.itemRefId = itemRef;
	}

	public RuntimeResult callClosure(Closure<RuntimeResult> closure, Object... objects) {
		return closure.call(objects);
	}

	@Override
	public String getName() {
		return MGPropertyAccesser.getName(getItemRef().getProperty());
	}

	@Override
	public String getDescription() {
		return MGPropertyAccesser.getDescription(getItemRef().getProperty());
	}

	@Override
	public String getIconId() {
		return MGPropertyAccesser.getIconId(getItemRef().getProperty());
	}

	@Override
	public byte getQuality() {
		return MGPropertyAccesser.getQuality(getItemRef().getProperty());
	}

	@Override
	public byte getBindType() {
		return MGPropertyAccesser.getBindType(getItemRef().getProperty());
	}

	@Override
	public byte getBindStatus() {
		return MGPropertyAccesser.getBindStatus(getProperty()) < 0 ? 0 : MGPropertyAccesser.getBindStatus(getProperty());
	}

	@Override
	public void setBindStatus(byte bindStatus) {
		MGPropertyAccesser.setOrPutBindStatus(getProperty(), bindStatus);
	}

	@Override
	public int getItemOrderId() {
		return MGPropertyAccesser.getItemSortId(getItemRef().getProperty());
	}

	@Override
	public byte getItemType() {
		return MGPropertyAccesser.getItemType(getItemRef().getProperty());
	}

	@Override
	public boolean usable() {
		return MGPropertyAccesser.getCanUse(getItemRef().getProperty()) > 0;
	}

	@Override
	public byte usableType() {
		return MGPropertyAccesser.getCanUse(getItemRef().getProperty());
	}

	@Override
	public int getNumber() {
		return MGPropertyAccesser.getNumber(getProperty()) < 0 ? 1 : MGPropertyAccesser.getNumber(getProperty());
	}

	@Override
	public void setNumber(int number) {
		MGPropertyAccesser.setOrPutNumber(getProperty(), number);
	}

	@Override
	public int getMaxStackNumber() {
		return MGPropertyAccesser.getMaxStackNumber(getItemRef().getProperty());
	}

	@Override
	public boolean isNonPropertyItemRef() {
		return MGPropertyAccesser.getIsNonPropertyItem(getItemRef().getProperty()) == 0;
	}

	@Override
	public boolean isNonPropertyItem() {
		boolean ret = isNonPropertyItemRef();
		if (ret)
			return ret;
		if (MGPropertyAccesser.getIsNonPropertyItem(this.getProperty()) <= 0)
			return true;
		else
			return false;
	}

	@Override
	public boolean canSale() {
		return MGPropertyAccesser.getIsCanSale(getItemRef().getProperty()) > 0;
	}

	public boolean canUse() {
		return MGPropertyAccesser.getCanUse(getItemRef().getProperty()) > 0;
	}

	@Override
	public int getSalePrice() {
		return MGPropertyAccesser.getSalePrice(getItemRef().getProperty());
	}

	@Override
	public byte getSaleCurrency() {
		return MGPropertyAccesser.getSaleCurrency(getItemRef().getProperty());
	}

	@Override
	public boolean discardable() {
		return MGPropertyAccesser.getIsNonThrow(getItemRef().getProperty()) > 0;
	}

	@Override
	public boolean binded() {
		return MGPropertyAccesser.getBindStatus(getProperty()) > 0;
	}

	@Override
	public boolean canStack() {
		return MGPropertyAccesser.getMaxStackNumber(getItemRef().getProperty()) > 1;
	}

	@Override
	public boolean isNonThrow() {
		return MGPropertyAccesser.getIsNonThrow(getItemRef().getProperty()) > 0;
	}

	public ItemRef getItemRef() {
		return (ItemRef) GameRoot.getGameRefObjectManager().getManagedObject(itemRefId);
	}

	public PropertyDictionary getNewAttachPropertyIfNull() {
		if (attachProperty == null)
			attachProperty = new PropertyDictionary();
		return attachProperty;
	}

	public PropertyDictionary getAttachProperty() {
		return attachProperty;
	}

	public void setAttachProperty(PropertyDictionary attachProperty) {
		this.attachProperty = attachProperty;
	}

	public boolean isEquip() {
		return getItemType() == ItemType.Equip;
	}

	public final boolean isSameBodyAreaId(short bodyAreaId) {
		byte requiredAreaId = MGPropertyAccesser.getAreaOfBody(getItemRef().getProperty());
		return requiredAreaId == bodyAreaId;
	}

	public boolean isHighestEquipment() {
		return MGPropertyAccesser.getIsHighestEquipment(getProperty()) == 1;
	}

	public void changePropertyItem() {
		if (!isNonPropertyItem()) {
			return;
		}

		for (Short symbol : FightEffectProperty.fightEffectSymbols) {
			int value = this.getItemRef().getEffectProperty().getValue(symbol);
			if (value > 0) {
				this.getProperty().setOrPutValue(symbol, this.getItemRef().getEffectProperty().getValue(symbol));
			}
		}
		MGPropertyAccesser.setOrPutIsNonPropertyItem(this.getProperty(), (byte) 1);

	}

	public Item getNewItem() {
		Item item = GameObjectFactory.getItem(getItemRefId());
		item.setNumber(this.getNumber());
		item.setBindStatus(this.getBindStatus());

		if (!this.isNonPropertyItem()) {
			item.getProperty().copyFrom(getProperty());

			if (item.isEquip() && EqiupmentComponentProvider.isHadSmithEquipment(this)) {

				EquipmentSmithComponent thisSmithComponent = EqiupmentComponentProvider.getEquipmentSmithComponent(this);
				EquipmentSmithComponent itemSmithComponent = EqiupmentComponentProvider.getEquipmentSmithComponent(item);
				PropertyDictionary washProperty = thisSmithComponent.getEquipmentSmithMgr().getXiLianEquipmentSmith().getPropertyDictionary();
				itemSmithComponent.getEquipmentSmithMgr().getXiLianEquipmentSmith().getPropertyDictionary().copyFrom(washProperty);
				itemSmithComponent.getEquipmentSmithMgr().getQiangHuaEquipmentSmith().onPropertyChange();
			}

			if (getAttachProperty() != null) {
				item.getNewAttachPropertyIfNull().copyFrom(getAttachProperty());
			}
		}

		return item;

	}
}
