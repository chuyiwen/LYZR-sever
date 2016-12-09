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
package sophia.mmorpg.equipmentSmith.smith.xiLian;

import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;

import sophia.foundation.property.PropertyDictionary;
import sophia.game.GameRoot;
import sophia.mmorpg.base.sprite.fightProperty.FightEffectProperty;
import sophia.mmorpg.item.Item;
import sophia.mmorpg.item.equipment.AbstractEquipmentSmith;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.itemBag.ItemFacade;
import sophia.mmorpg.player.itemBag.ItemOptSource;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;
import sophia.mmorpg.utils.SFRandomUtils;

/**
 * 装备-洗练
 */
public final class MGXiLianEquipmentSmith extends AbstractEquipmentSmith {
	private static final Logger logger = Logger.getLogger(MGXiLianEquipmentSmith.class.getName());
	private static final Collection<Short> equipEffectSymbols = FightEffectProperty.fightEffectSymbols;
	
	private List<Short> lockedPropertySymbolList;
	private boolean isPropertyChange = false;
	private PropertyDictionary propertyDictionary = new PropertyDictionary();

	public MGXiLianEquipmentSmith(Item owner) {
		super(owner);
	}
	
	public MGXiLianDataRef getXiLianDataRef() {
		return (MGXiLianDataRef) GameRoot.getGameRefObjectManager().getManagedObject(getOwner().getItemRef().getId() + MGXiLianDataRef.keyWord);
	}

	public boolean xiLian(Player delegate, List<Short> lockedPropertySymbolList, boolean isUseBinded) {
		MGXiLianDataRef xiLianDataRef = getXiLianDataRef();
		if (xiLianDataRef == null) {
			return false;
		}
		
		Item equipment = getOwner();
		this.lockedPropertySymbolList = lockedPropertySymbolList;
		
		PropertyDictionary dataProperty = xiLianDataRef.getProperty();

		int beishu = (int) Math.pow(2, lockedPropertySymbolList.size());
		int needGold = MGPropertyAccesser.getGold(dataProperty) * beishu;
		int strengStone = MGPropertyAccesser.getUseMaterialCount(dataProperty) * beishu;
		String useItemRefId = MGPropertyAccesser.getItemRefId(dataProperty);

		int haveStone = 0;
		int haveBindedStone = 0;
		int haveUnBindedStone = 0;
		if (isUseBinded) {
			haveBindedStone = ItemFacade.getNumber(delegate, useItemRefId, true);
			haveUnBindedStone = ItemFacade.getNumber(delegate, useItemRefId, false);
			haveStone = haveBindedStone + haveUnBindedStone;
		} else {
			haveStone = ItemFacade.getNumber(delegate, useItemRefId, false);
		}
		if (needGold > delegate.getPlayerMoneyComponent().getGold()) {
			if (logger.isDebugEnabled()) {
				logger.debug("gold not Enough");
			}
			return false;
		} else if (strengStone > haveStone) {
			if (logger.isDebugEnabled()) {
				logger.debug("Streng Stone not Enough");
			}
			return false;
		}

		onPropertyChange();

		if (haveBindedStone > 0) {
			equipment.setBindStatus(Item.Binded);
		}
		MGPropertyAccesser.setOrPutIsNonPropertyItem(equipment.getProperty(), Item.Is_Property_Item);
		delegate.getPlayerMoneyComponent().subGold(needGold, ItemOptSource.XiLian);
		ItemFacade.removeItem(delegate, useItemRefId, strengStone, isUseBinded, ItemOptSource.XiLian);
		MGPropertyAccesser.setOrPutWashCount(delegate.getProperty(),
				MGPropertyAccesser.getWashCount(delegate.getProperty()) < 0 ? 1 : MGPropertyAccesser.getWashCount(delegate.getProperty()) + 1);
		return true;
	}

	public boolean xiLian() {
		MGXiLianDataRef xiLianDataRef = getXiLianDataRef();
		if (xiLianDataRef == null) {
			return false;
		}

		onPropertyChange();
		Item equipment = getOwner();
		MGPropertyAccesser.setOrPutIsNonPropertyItem(equipment.getProperty(), Item.Is_Property_Item);
		return true;
	}

	@Override
	protected void calculate() {
		MGXiLianDataRef xiLianDataRef = getXiLianDataRef();
		if (xiLianDataRef == null) {
			return;
		}
		
		PropertyDictionary dataProperty = xiLianDataRef.getProperty();
		if (propertyDictionary == null) {
			propertyDictionary = new PropertyDictionary();
		}
		
		for (short symbol : equipEffectSymbols) {
			int maxValue = (int) dataProperty.getValue(symbol);
			if (maxValue > 0) {
				int newValue = SFRandomUtils.random(1, maxValue);
				int oldValue = (int) propertyDictionary.getValue(symbol) < 0 ? 0 : (int) propertyDictionary.getValue(symbol);
				boolean islocked = false;
				if (newValue < oldValue && this.lockedPropertySymbolList != null) {
					for (short lockedSymbol : lockedPropertySymbolList) {

						if (symbol == lockedSymbol) {
							islocked = true;
							break;
						}
					}
				}
				if (!islocked || newValue >= oldValue) {
					if (newValue != oldValue) {
						isPropertyChange = true;
					}
					propertyDictionary.setOrPutValue(symbol, newValue);
					islocked = false;
				}
			}

		}
		
	}

	public void setPropertyDictionary(PropertyDictionary propertyDictionary) {
		this.propertyDictionary = propertyDictionary;
	}

	@Override
	public PropertyDictionary getPropertyDictionary() {
		return this.propertyDictionary;
	}

	public boolean isPropertyChange() {
		return isPropertyChange;
	}

	public void setPropertyChange(boolean isPropertyChange) {
		this.isPropertyChange = isPropertyChange;
	}
}
