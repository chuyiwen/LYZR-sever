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
package newbee.morningGlory.mmorpg.sprite;

import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import newbee.morningGlory.mmorpg.player.peerage.MGPeerageRef;
import newbee.morningGlory.mmorpg.player.peerage.MGPlayerPeerageComponent;
import newbee.morningGlory.mmorpg.player.talisman.MGPlayerCitta;
import newbee.morningGlory.mmorpg.player.talisman.MGPlayerTalismanComponent;
import newbee.morningGlory.mmorpg.player.talisman.MGTalisman;
import newbee.morningGlory.mmorpg.player.wing.MGPlayerWingComponent;
import newbee.morningGlory.mmorpg.player.wing.MGPlayerWingRef;
import newbee.morningGlory.mmorpg.sprite.buff.MGFightSpriteBuff;
import newbee.morningGlory.mmorpg.sprite.buff.MGFightSpriteBuffComponent;
import newbee.morningGlory.mmorpg.vip.MGPlayerVipComponent;
import newbee.morningGlory.mmorpg.vip.MGVipLevelMgr;

import org.apache.log4j.Logger;

import sophia.foundation.property.PropertyDictionary;
import sophia.foundation.property.SimulatorProperty;
import sophia.foundation.property.ValueProperty;
import sophia.foundation.property.symbol.SimulatorPropertySymbolContext;
import sophia.foundation.util.DebugUtil;
import sophia.mmorpg.base.sprite.fightProperty.FightEffectProperty;
import sophia.mmorpg.base.sprite.fightProperty.FightPropertyHelper;
import sophia.mmorpg.base.sprite.fightSkill.FightSkill;
import sophia.mmorpg.item.Item;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.fightSkill.ref.SkillLevelRef;
import sophia.mmorpg.player.mount.Mount;
import sophia.mmorpg.player.property.PlayerConfig;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;
import sophia.mmorpg.ref.editorgen.MGPropertySymbolDefines;

public final class MGFightPropertyMgrHelper implements FightPropertyHelper {
	private static final Logger logger = Logger.getLogger(MGFightPropertyMgrHelper.class);

	@SuppressWarnings("unchecked")
	public void checkAttributeValidity(Player player) {
		PropertyDictionary pd = new PropertyDictionary();

		// 角色基础属性
		int level = MGPropertyAccesser.getLevel(player.getProperty());
		PropertyDictionary levelPd = player.getPlayerProfessionRef().getPlayerClassLevelData(level).getLevelProperties();
		addFightProperty(pd, levelPd);

		// 移动速度，攻击速度
		MGPropertyAccesser.setOrPutMoveSpeed(pd, PlayerConfig.MOVESPEED);
		MGPropertyAccesser.setOrPutAtkSpeed(pd, PlayerConfig.ATTACKSPEED);

		// 法宝
		MGPlayerTalismanComponent playerTalismanComponent = (MGPlayerTalismanComponent) player.getTagged(MGPlayerTalismanComponent.Tag);
		MGPlayerCitta playerCitta = playerTalismanComponent.getPlayerCitta();
		if(playerCitta.getCittaRef() != null){
			addFightProperty(pd, playerCitta.getCittaRef().getEffectData());
		}
		MGTalisman talisman = playerCitta.getCrtActiveTalisman();
		if(talisman != null && talisman.isActive()){
			addFightProperty(pd, talisman.getTalismanRef().getEffectData());
		}
		// 坐骑
		Mount crtMount = player.getPlayerMountComponent().getMountManager().getCrtMount();
		if (crtMount != null) {
			PropertyDictionary mountEffectPd = crtMount.getMountRef().getEffect();
			addFightProperty(pd, mountEffectPd);

			if (player.isOnMount()) {
				PropertyDictionary speedEffect = crtMount.getMountRef().getTmpEffect();
				addFightProperty(pd, speedEffect);
			}

		}

		// 爵位
		MGPlayerPeerageComponent playerPeerageComponent = (MGPlayerPeerageComponent) player.getTagged(MGPlayerPeerageComponent.Tag);
		MGPeerageRef crtPeerageRef = playerPeerageComponent.getPeeragerefMgr().getCrtPeerageRef();
		if (crtPeerageRef != null) {
			PropertyDictionary peeragePd = crtPeerageRef.getProperty();
			addFightProperty(pd, peeragePd);
		}

		// 翅膀
		MGPlayerWingComponent playerWingComponent = (MGPlayerWingComponent) player.getTagged(MGPlayerWingComponent.Tag);
		MGPlayerWingRef playerWingRef = playerWingComponent.getPlayerWing().getPlayerWingRef();
		if (playerWingRef != null) {
			PropertyDictionary wingPd = playerWingRef.getEffectProperty();
			addFightProperty(pd, wingPd);
		}

		// 装备
		List<Item> equipedItems = player.getPlayerEquipBodyConponent().getEquipedItems();
		for (Item item : equipedItems) {
			PropertyDictionary itemPd = new PropertyDictionary();
			if (item.isNonPropertyItem()) {
				itemPd = item.getItemRef().getEffectProperty();
			} else {
				itemPd = item.getProperty();
			}
			addFightProperty(pd, itemPd);
		}

		// BUFF
		@SuppressWarnings({ "unchecked", "rawtypes" })
		MGFightSpriteBuffComponent<Player> buffComponent = (MGFightSpriteBuffComponent) player.getTagged(MGFightSpriteBuffComponent.Tag);
		List<MGFightSpriteBuff> buffs = buffComponent.getFightSpriteBuffMgr().getFightSpriteBuffList();
		for (MGFightSpriteBuff buff : buffs) {
			if (buff.getFightSpriteBuffRef().isChangeFightValueBuff() && player.isOnline()) {
				addFightProperty(pd, buff.getSpecialProperty());
			}
		}

		// 技能
		Collection<FightSkill> allLearnedSkills = player.getPlayerFightSkillComponent().getPlayerFightSkillTree().getAllLearnedSkills();
		for (FightSkill skill : allLearnedSkills) {
			if (skill.getRef().isAttributeSkill()) {
				SkillLevelRef levelRef = skill.getLevelRef();
				if (levelRef != null && levelRef.getRuntimeParameter() != null) {
					PropertyDictionary effectPd = skill.getLevelRef().getRuntimeParameter();
					addFightProperty(pd, effectPd);
				}
			}
		}

		// VIP
		MGPlayerVipComponent playerVipComponent = (MGPlayerVipComponent) player.getTagged(MGPlayerVipComponent.Tag);
		MGVipLevelMgr vipMgr = playerVipComponent.getVipMgr();
		if (vipMgr != null && vipMgr.getVipLevelDataRef() != null) {
			PropertyDictionary vipPd = vipMgr.getVipLevelDataRef().getProperty();
			addFightProperty(pd, vipPd);
		}

		// 计算加成
		Set<Short> rateIds = player.getFightPropertyMgrComponent().getFightPropertyMgr().getRateIds();
		Set<Entry<Short, SimulatorProperty<?>>> entrySet = pd.getDictionary().entrySet();
		for (Entry<Short, SimulatorProperty<?>> entry : entrySet) {
			ValueProperty<Integer> valueProperty = (ValueProperty<Integer>) entry.getValue();
			short id = valueProperty.getId();
			if (rateIds.contains(id)) {
				if (id == MGPropertySymbolDefines.PerHP_Id) {
					int maxHP = MGPropertyAccesser.getMaxHP(pd);
					int addMaxHP = maxHP * valueProperty.getValue() / 100;
					maxHP = MGPropertyAccesser.getMaxHP(pd);
					MGPropertyAccesser.setOrPutMaxHP(pd, maxHP + addMaxHP);
				} else if (id == MGPropertySymbolDefines.PerMP_Id) {
					int maxMP = MGPropertyAccesser.getMaxMP(pd);
					int addMaxMP = maxMP * valueProperty.getValue() / 100;
					maxMP = MGPropertyAccesser.getMaxMP(pd);
					MGPropertyAccesser.setOrPutMaxMP(pd, maxMP + addMaxMP);
				} else if (id == MGPropertySymbolDefines.AtkSpeedPer_Id) {
					int attackSpeed = MGPropertyAccesser.getAtkSpeed(pd);
					int newAttackSpeed = attackSpeed * (100 - valueProperty.getValue()) / 100;
					MGPropertyAccesser.setOrPutAtkSpeed(pd, newAttackSpeed);
				} else if (id == MGPropertySymbolDefines.MoveSpeedPer_Id) {
					int moveSpeed = MGPropertyAccesser.getMoveSpeed(pd);
					int addedMoveSpeed = moveSpeed * valueProperty.getValue() / 100;
					int currentMoveSpeed = MGPropertyAccesser.getMoveSpeed(pd);
					MGPropertyAccesser.setOrPutMoveSpeed(pd, currentMoveSpeed + addedMoveSpeed);
				}
			}
		}

		PropertyDictionary snapshotPd = player.getFightPropertyMgrComponent().getFightPropertyMgr().getSnapshotByNew().getPropertyDictionary();
		checkFightProperty(player, pd, snapshotPd);

	}

	@SuppressWarnings("unchecked")
	private static void addFightProperty(PropertyDictionary result, PropertyDictionary input) {
		PropertyDictionary clone = input.clone();
		for (Short symbolId : FightEffectProperty.fightEffectSymbols) {
			ValueProperty<Integer> inputValue = (ValueProperty<Integer>) clone.getDictionary().get(symbolId);
			if (inputValue != null) {
				result.add(inputValue);
			}

		}
	}

	@SuppressWarnings("unchecked")
	private static void checkFightProperty(Player player, PropertyDictionary supposed, PropertyDictionary snapshot) {
		for (Short symbolId : FightEffectProperty.fightEffectSymbols) {
			ValueProperty<Integer> supposedValue = (ValueProperty<Integer>) supposed.getDictionary().get(symbolId);
			ValueProperty<Integer> currentValue = (ValueProperty<Integer>) snapshot.getDictionary().get(symbolId);
			String propertySymbolName = SimulatorPropertySymbolContext.getPropertySymbolName(symbolId);
			if (supposedValue != null && currentValue != null) {
				int value = supposedValue.getValue();
				int value2 = currentValue.getValue();
				if (value != value2) {
					logger.error("Attribute " + symbolId + " " + propertySymbolName + " not correct! " + "supposed value: " + value + " current value " + value2 + player + "\n"
							+ DebugUtil.printStack());
				}

			}
		}
	}
}
