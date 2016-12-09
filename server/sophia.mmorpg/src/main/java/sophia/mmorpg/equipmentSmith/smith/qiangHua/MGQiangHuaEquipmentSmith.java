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
package sophia.mmorpg.equipmentSmith.smith.qiangHua;

import java.util.Collection;

import org.apache.log4j.Logger;

import sophia.foundation.property.PropertyDictionary;
import sophia.game.GameRoot;
import sophia.mmorpg.base.sprite.fightProperty.FightEffectProperty;
import sophia.mmorpg.item.Item;
import sophia.mmorpg.item.equipment.AbstractEquipmentSmith;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.itemBag.ItemBag;
import sophia.mmorpg.player.itemBag.ItemFacade;
import sophia.mmorpg.player.itemBag.ItemOptSource;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;
import sophia.mmorpg.utils.SFRandomUtils;

/**
 * 装备-强化
 */
public final class MGQiangHuaEquipmentSmith extends AbstractEquipmentSmith {

	private static final Logger logger = Logger.getLogger(MGQiangHuaEquipmentSmith.class.getName());
	public static final byte STRENG_SUCCESS = 1;
	public static final byte STRENG_FAILED = 0;
	public static final byte CANTSTRENG = 2;

	private static final int STRENG_FIVE = 5;
	private static final int STRENG_NINE = 9;
	private static final int STRENG_TWELVE = 12;
	private int QiangHuaJieGuo = STRENG_SUCCESS;
	private boolean ChangeBindStatus = false;
	private static MGQiangHuaEquipmentConfig qiangHuaEquipmentConfig, specialQiangHuaConig, scrollQiangHuaConfig;
	private Player delegate;
	private MGQiangHuaDataRef qiangHuaDataRef;
	private PropertyDictionary qiangHuaPropertyDictionary;

	private static Collection<Short> equipEffectSymbols = FightEffectProperty.fightEffectSymbols;
	static {
		qiangHuaEquipmentConfig = (MGQiangHuaEquipmentConfig) GameRoot.getGameRefObjectManager().getManagedObject(MGQiangHuaEquipmentConfig.QiangHua_Id);
		specialQiangHuaConig = (MGQiangHuaEquipmentConfig) GameRoot.getGameRefObjectManager().getManagedObject(MGQiangHuaEquipmentConfig.Special_Id);
		scrollQiangHuaConfig = (MGQiangHuaEquipmentConfig) GameRoot.getGameRefObjectManager().getManagedObject(MGQiangHuaEquipmentConfig.QiangHuaScroll_Id);

	}

	public MGQiangHuaEquipmentSmith(Item owner) {
		super(owner);

	}

	/**
	 * 强化卷强化
	 * 
	 * @param delegate
	 * @param equipment
	 * @param scrollGridId
	 * @return
	 */
	public byte qiangHuaByScroll(Player delegate, int scrollGridId) {

		this.delegate = delegate;
		Item equipment = getOwner();
		ItemBag itemBag = this.delegate.getItemBagComponent().getItemBag();
		if (scrollGridId < 0 || scrollGridId >= itemBag.getItemBagCapacity()) { // 格索引是否超出范围
			if (logger.isDebugEnabled()) {
				logger.debug("Illegal Index ");
			}
			return CANTSTRENG;
		}
		Item qiangHuaScroll = delegate.getItemBagComponent().getItemBag().getItemBySlot(scrollGridId);
		if (qiangHuaScroll == null) {
			return CANTSTRENG;
		}
		String scrollRefId = qiangHuaScroll.getItemRef().getId();
		MGQiangHuaScrollDataRef mgQiangHuaScrollDataRef = scrollQiangHuaConfig.getMgQiangHuaDScrollataRefMap().get(scrollRefId);
		if (mgQiangHuaScrollDataRef == null)
			return CANTSTRENG;

		byte canQiangHuaLevel = MGPropertyAccesser.getStrengtheningLevel(mgQiangHuaScrollDataRef.getProperty());
		int gold = MGPropertyAccesser.getGold(mgQiangHuaScrollDataRef.getProperty());
		int equipLevel = MGPropertyAccesser.getStrengtheningLevel(equipment.getProperty()) < 0 ? 0 : MGPropertyAccesser.getStrengtheningLevel(equipment.getProperty());
		if (delegate.getPlayerMoneyComponent().getGold() < gold) {
			return CANTSTRENG;
		} else if (canQiangHuaLevel <= equipLevel) {
			return CANTSTRENG;
		}
		byte equipmentBodyAreaId = MGPropertyAccesser.getAreaOfBody(equipment.getItemRef().getProperty());
		byte professionId = MGPropertyAccesser.getProfessionId(equipment.getItemRef().getProperty());
		qiangHuaDataRef = qiangHuaEquipmentConfig.getQiangHuaDataRefMap().get(MGQiangHuaRefKey.get(professionId, equipmentBodyAreaId, canQiangHuaLevel));
		onPropertyChange();
		if(qiangHuaScroll.binded()){
			equipment.setBindStatus(Item.Binded);
		}
		if (delegate.getItemBagComponent().removeEventNotify(scrollGridId, 1,ItemOptSource.QiangHua)) {
			delegate.getPlayerMoneyComponent().subGold(gold,ItemOptSource.QiangHua);
		}
		if (delegate.getItemBagComponent().removeEventNotify(scrollGridId, 1, ItemOptSource.QiangHua)) {
			delegate.getPlayerMoneyComponent().subGold(gold, ItemOptSource.FenJie);
		}
		return STRENG_SUCCESS;
	}
	/**
	 * 装备强化
	 * @param delegate
	 * @param unbindedGold
	 * @return
	 */
	public byte qiangHua(Player delegate, int unbindedGold,byte strengthLevel,boolean isUseBinded) {
		this.delegate = delegate;
		Item equipment = getOwner();
		byte playerPrefessionId = MGPropertyAccesser.getProfessionId(equipment.getItemRef().getProperty());
		byte equipmentBodyAreaId = MGPropertyAccesser.getAreaOfBody(equipment.getItemRef().getProperty());
		byte qiangHuaLevel = 0;
		if (!equipment.isNonPropertyItem())
			qiangHuaLevel = MGPropertyAccesser.getStrengtheningLevel(equipment.getProperty());
		if(strengthLevel ==0 && qiangHuaLevel != 0){
			return CANTSTRENG;
		}
		if(strengthLevel != 0 && strengthLevel < qiangHuaLevel){	//如果客户端发来的强化等级比当前武器小，那么不强化	
			return CANTSTRENG;
		}

		qiangHuaLevel += 1;

		qiangHuaDataRef = qiangHuaEquipmentConfig.getQiangHuaDataRefMap().get(MGQiangHuaRefKey.get(playerPrefessionId, equipmentBodyAreaId, qiangHuaLevel));
		if (qiangHuaDataRef == null)
			return CANTSTRENG;
		if (consumIfEligible(unbindedGold,isUseBinded)) {
			
			int subGold = MGPropertyAccesser.getGold(qiangHuaDataRef.getProperty());
			int subStoneNumber = MGPropertyAccesser.getUseMaterialCount(qiangHuaDataRef.getProperty());
			String useItemRefId = MGPropertyAccesser.getItemRefId(qiangHuaDataRef.getProperty());
			
			if (ItemFacade.removeItem(delegate, useItemRefId, subStoneNumber, isUseBinded, ItemOptSource.QiangHua)) {
				delegate.getPlayerMoneyComponent().subGold(subGold, ItemOptSource.QiangHua);
				if (unbindedGold != 0)
					delegate.getPlayerMoneyComponent().subUnbindGold(unbindedGold,ItemOptSource.QiangHua);
			}
			
			if(ChangeBindStatus){
				equipment.setBindStatus(Item.Binded);
				ChangeBindStatus = false;
			}
			int probability = getProbability(unbindedGold);
			int result = SFRandomUtils.random100();	
			
			if (result <= probability) {
				QiangHuaJieGuo = STRENG_SUCCESS;
				onPropertyChange();
				return STRENG_SUCCESS;

			} else {
				QiangHuaJieGuo = STRENG_FAILED;
				onPropertyChange();
				return STRENG_FAILED;
			}

		} else
			return CANTSTRENG;

	}
	/**
	 * 强化到指定等级，不消耗任何物品
	 * @param delegate
	 * @param strengthenLevel
	 * @return
	 */
	public byte qiangHuaFromSide(Player delegate, byte strengthenLevel) {

		this.delegate = delegate;
		Item equipment = getOwner();
		if(strengthenLevel > 12){
			return CANTSTRENG;
		}
		int equipLevel = MGPropertyAccesser.getStrengtheningLevel(equipment.getProperty()) < 0 ? 0 : MGPropertyAccesser.getStrengtheningLevel(equipment.getProperty());
		
		if (strengthenLevel <= equipLevel) {
			return CANTSTRENG;
		}
		byte equipmentBodyAreaId = MGPropertyAccesser.getAreaOfBody(equipment.getItemRef().getProperty());
		byte professionId = MGPropertyAccesser.getProfessionId(equipment.getItemRef().getProperty());
		qiangHuaDataRef = qiangHuaEquipmentConfig.getQiangHuaDataRefMap().get(MGQiangHuaRefKey.get(professionId, equipmentBodyAreaId, strengthenLevel));
		onPropertyChange();
		
		return STRENG_SUCCESS;
	}

	protected int getProbability(int unbindedGold) {

		int probability = MGPropertyAccesser.getProbability(qiangHuaDataRef.getProperty());

		int tenPercentCost = MGPropertyAccesser.getSucceedUpConsume(qiangHuaDataRef.getProperty());

		int extraProbability = unbindedGold / tenPercentCost;

		probability = probability + extraProbability * 10 > 100 ? 100 : probability + extraProbability * 10;

		return probability;
	}

	/**
	 * 强化计算
	 */
	@Override
	protected void calculate() {

		Item equipment = getOwner();

		byte playerPrefessionId = MGPropertyAccesser.getProfessionId(equipment.getItemRef().getProperty());
		byte equipmentBodyAreaId = MGPropertyAccesser.getAreaOfBody(equipment.getItemRef().getProperty());
		byte qiangHuaLevel = 0;
		if (!equipment.isNonPropertyItem())
			qiangHuaLevel = MGPropertyAccesser.getStrengtheningLevel(equipment.getProperty());

		if (qiangHuaDataRef == null) {
			qiangHuaDataRef = qiangHuaEquipmentConfig.getQiangHuaDataRefMap().get(MGQiangHuaRefKey.get(playerPrefessionId, equipmentBodyAreaId, qiangHuaLevel));

		} else {
			qiangHuaLevel = MGPropertyAccesser.getStrengtheningLevel(qiangHuaDataRef.getProperty());
		}
		//如果强化失败
		if (QiangHuaJieGuo == STRENG_FAILED) {
			qiangHuaLevel = MGPropertyAccesser.getFailedDownToLevel(qiangHuaDataRef.getProperty()) < 0 ? 0 : MGPropertyAccesser.getFailedDownToLevel(qiangHuaDataRef.getProperty());
			qiangHuaDataRef = qiangHuaEquipmentConfig.getQiangHuaDataRefMap().get(MGQiangHuaRefKey.get(playerPrefessionId, equipmentBodyAreaId, qiangHuaLevel));
		}

		if (qiangHuaDataRef == null)
			return;
		if (qiangHuaPropertyDictionary == null)
			qiangHuaPropertyDictionary = new PropertyDictionary();
		else
			qiangHuaPropertyDictionary.clear();

		for (short symbol : equipEffectSymbols) {
			int newValue = ((Integer) qiangHuaDataRef.getProperty().getValue(symbol)) < 0 ? 0 : (Integer) qiangHuaDataRef.getProperty().getValue(symbol);
			if (newValue > 0)
				qiangHuaPropertyDictionary.setOrPutValue(symbol, newValue);
		}

		if (qiangHuaLevel >= STRENG_FIVE) {
			int extraAddLevel = STRENG_FIVE;
			if (qiangHuaLevel >= STRENG_FIVE && qiangHuaLevel < STRENG_NINE) {
				extraAddLevel = STRENG_FIVE;
				addExtraProperty(equipment, extraAddLevel);
			} else if (qiangHuaLevel >= STRENG_NINE && qiangHuaLevel < STRENG_TWELVE) {
				extraAddLevel = STRENG_NINE;
				addExtraProperty(equipment, STRENG_FIVE);
				addExtraProperty(equipment, extraAddLevel);
			} else if (qiangHuaLevel == STRENG_TWELVE) {
				extraAddLevel = STRENG_TWELVE;
				addExtraProperty(equipment, STRENG_FIVE);
				addExtraProperty(equipment, STRENG_NINE);
				addExtraProperty(equipment, extraAddLevel);

			}

		}
		MGPropertyAccesser.setOrPutStrengtheningLevel(equipment.getProperty(), MGPropertyAccesser.getStrengtheningLevel(qiangHuaDataRef.getProperty()));
		MGPropertyAccesser.setOrPutIsNonPropertyItem(equipment.getProperty(), Item.Is_Property_Item);
		if (delegate != null) {
			MGPropertyAccesser.setOrPutStrengTheningCount(delegate.getProperty(),
					MGPropertyAccesser.getStrengTheningCount(delegate.getProperty()) < 0 ? 1 : MGPropertyAccesser.getStrengTheningCount(delegate.getProperty()) + 1);
		}
		QiangHuaJieGuo = STRENG_SUCCESS;
	}
	
	/**
	 * 判断强化合法性
	 * 
	 * @return
	 */
	private boolean consumIfEligible(int unbindedGold,boolean isUserBinded) {

		int StrengtheningLevel = MGPropertyAccesser.getStrengtheningLevel(getOwner().getProperty());

		int needGold = MGPropertyAccesser.getGold(qiangHuaDataRef.getProperty());

		int needStoneNumber = MGPropertyAccesser.getUseMaterialCount(qiangHuaDataRef.getProperty());

		int strengStoneNumber = 0;
		
		int BindedStrengStoneNumber = 0;
		if(isUserBinded){
			BindedStrengStoneNumber = ItemFacade.getNumber(delegate, MGPropertyAccesser.getItemRefId(qiangHuaDataRef.getProperty()),true);			
			strengStoneNumber = BindedStrengStoneNumber +  ItemFacade.getNumber(delegate, MGPropertyAccesser.getItemRefId(qiangHuaDataRef.getProperty()),false);
		}else{
			strengStoneNumber = ItemFacade.getNumber(delegate, MGPropertyAccesser.getItemRefId(qiangHuaDataRef.getProperty()),false);
		}

		int haveGold = delegate.getPlayerMoneyComponent().getGold();

		int haveUnBindedGold = delegate.getPlayerMoneyComponent().getUnbindGold();

		if (StrengtheningLevel == 12) {
			if (logger.isDebugEnabled()) {
				logger.debug("已强化到满级");
			}
			return false;
		} else if (needGold > haveGold) {
			if (logger.isDebugEnabled()) {
				logger.debug("金币不足");
			}
			return false;
		} else if (needStoneNumber > strengStoneNumber) {
			if (logger.isDebugEnabled()) {
				logger.debug("强化石不足");
			}
			return false;
		} else if (haveUnBindedGold < unbindedGold) {
			if (logger.isDebugEnabled()) {
				logger.debug("元宝不足");
			}
			return false;
		}

		if(BindedStrengStoneNumber > 0){
			ChangeBindStatus = true;
		}
		
		return true;
	}

	/**
	 * 增加额外的强化等级属性
	 * 
	 * @param equipment
	 * @param extraAddLevel
	 */
	public void addExtraProperty(Item equipment, int extraAddLevel) {
		MGSpecialEquipmentQiangHuaDataRef mgSpecialEquipmentQiangHuaDataRef = specialQiangHuaConig.getSpecialEquipmentQiangHuaDataRefMap().get(
				MGSpecialEquipmentQiangHuaDataRefKey.get(equipment.getItemRef().getId(), extraAddLevel));
		if (mgSpecialEquipmentQiangHuaDataRef != null) {
			for (short symbol : equipEffectSymbols) {

				int strengValue = ((Integer) mgSpecialEquipmentQiangHuaDataRef.getProperty().getValue(symbol)) < 0 ? 0 : (Integer) mgSpecialEquipmentQiangHuaDataRef.getProperty()
						.getValue(symbol);
				if (strengValue > 0) {
					int newValue = (Integer) qiangHuaPropertyDictionary.getValue(symbol) < 0 ? 0 : (Integer) qiangHuaPropertyDictionary.getValue(symbol);
					qiangHuaPropertyDictionary.setOrPutValue(symbol, newValue + strengValue);
				}

			}
		}
	}

	public static MGQiangHuaEquipmentConfig getQiangHuaEquipmentConfig() {
		return qiangHuaEquipmentConfig;
	}

	public static void setQiangHuaEquipmentConfig(MGQiangHuaEquipmentConfig qiangHuaEquipmentConfig) {
		MGQiangHuaEquipmentSmith.qiangHuaEquipmentConfig = qiangHuaEquipmentConfig;
	}

	public Player getDelegate() {
		return delegate;
	}

	public void setDelegate(Player delegate) {
		this.delegate = delegate;
	}

	@Override
	public PropertyDictionary getPropertyDictionary() {

		return qiangHuaPropertyDictionary;
	}

}
