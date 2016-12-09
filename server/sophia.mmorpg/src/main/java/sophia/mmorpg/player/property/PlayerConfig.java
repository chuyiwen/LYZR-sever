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
package sophia.mmorpg.player.property;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import sophia.foundation.property.PropertyDictionary;
import sophia.foundation.property.SimulatorProperty;
import sophia.foundation.util.Position;
import sophia.game.GameRoot;
import sophia.mmorpg.base.sprite.fightProperty.FightPropertyMgr;
import sophia.mmorpg.base.sprite.fightProperty.FightPropertyMgrComponent;
import sophia.mmorpg.core.PropertyDictionaryModifyPhase;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.exp.PlayerExpComponent;
import sophia.mmorpg.player.money.PlayerMoneyComponent;
import sophia.mmorpg.player.ref.PlayerProfessionLevelData;
import sophia.mmorpg.player.ref.PlayerProfessionRef;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;

public final class PlayerConfig {
	private static final Logger logger = Logger.getLogger(PlayerConfig.class);
	private static final Map<Byte, String> professionMapping = new HashMap<Byte, String>() {
		private static final long serialVersionUID = -5830371194316220435L;

		{
			put(WARRIOR, PROFESSION_WARRIOR);
			put(ENCHANTER, PROFESSION_ENCHANTER);
			put(WARLOCK, PROFESSION_WARLOCK);
		}
	};

	public static final byte WARRIOR = 1;
	public static final byte ENCHANTER = 2;
	public static final byte WARLOCK = 3;

	public static final String PROFESSION_WARRIOR = "warrior";
	public static final String PROFESSION_ENCHANTER = "enchanter";
	public static final String PROFESSION_WARLOCK = "warlock";

	public static final int MOVESPEED = 12;
	public static final int ATTACKSPEED = 1000;

	public static final String MAPID_BORN = "S001";

	public static final boolean isWarrior(byte professionId) {
		return professionMapping.get(professionId).equals(PROFESSION_WARRIOR);
	}

	public static final boolean isEnchanter(byte professionId) {
		return professionMapping.get(professionId).equals(PROFESSION_ENCHANTER);
	}

	public static final boolean isWarlock(byte professionId) {
		return professionMapping.get(professionId).equals(PROFESSION_WARLOCK);
	}

	public static String getProfessionRefId(byte professionId) {
		return professionMapping.get(professionId);
	}

	public static void setProfessionRefTo(Player player, byte professionId, byte gender) {
		MGPropertyAccesser.setOrPutGender(player.getProperty(), gender);
		MGPropertyAccesser.setOrPutProfessionId(player.getProperty(), professionId);
		PlayerProfessionRef professionRef = (PlayerProfessionRef) GameRoot.getGameRefObjectManager().getManagedObject(professionMapping.get(professionId));
		player.setPlayerProfessionRef(professionRef);
	}

	public static void setLevelAndExpTo(Player player, int level, long exp) {
		PlayerExpComponent expComponent = player.getExpComponent();
		expComponent.setLevel(level);
		expComponent.setExp(exp);

	}

	public static void setGoldTo(Player player, int gold, int bindGold, int unbindGold) {
		// 初始货币
		PlayerMoneyComponent playerMoneyComponent = player.getPlayerMoneyComponent();
		playerMoneyComponent.setGold(gold);
		playerMoneyComponent.setBindGold(bindGold);
		playerMoneyComponent.setUnbindGold(unbindGold);
		MGPropertyAccesser.setOrPutGold(player.getProperty(), gold);
		MGPropertyAccesser.setOrPutBindedGold(player.getProperty(), bindGold);
		MGPropertyAccesser.setOrPutUnbindedGold(player.getProperty(), unbindGold);
	}

	public static void setMeritAndAchievementTo(Player player, int merit, int achievement) {
		MGPropertyAccesser.setOrPutAchievement(player.getProperty(), achievement);
		MGPropertyAccesser.setOrPutAchievement(player.getProperty(), merit);
	}

	public static void setPositionTo(Player player, String sceneRefId, int x, int y) {
		Position pos = new Position(x, y);
		player.setCrtPosition(pos);
		MGPropertyAccesser.setOrPutSceneRefId(player.getProperty(), sceneRefId);
		MGPropertyAccesser.setOrPutPositionX(player.getProperty(), x);
		MGPropertyAccesser.setOrPutPositionY(player.getProperty(), y);
	}

	public static void configFightPropertiesTo(Player player) {
		PropertyDictionary pd = player.getProperty();
		PlayerProfessionRef professionRef = player.getPlayerProfessionRef();
		PlayerProfessionLevelData playerProfessionLevelData = professionRef.getPlayerClassLevelData(MGPropertyAccesser.getLevel(pd));
		PropertyDictionary levelPd = playerProfessionLevelData.getLevelProperties();
		if (logger.isDebugEnabled()) {
			logger.debug("configFightPropertiesTo: player pd: " + pd);
			logger.debug("configFightPropertiesTo: levelPd: " + levelPd);
		}

		int maxHP = MGPropertyAccesser.getMaxHP(levelPd);
		int maxMP = MGPropertyAccesser.getMaxMP(levelPd);

		FightPropertyMgrComponent fightPropertyMgrComponent = player.getFightPropertyMgrComponent();
		FightPropertyMgr fightPropertyMgr = fightPropertyMgrComponent.getFightPropertyMgr();
		PropertyDictionary fightPropertyDictionary = new PropertyDictionary();
		MGPropertyAccesser.setOrPutMoveSpeed(fightPropertyDictionary, MOVESPEED);
		MGPropertyAccesser.setOrPutAtkSpeed(fightPropertyDictionary, ATTACKSPEED);
		MGPropertyAccesser.setOrPutMoveSpeedPer(fightPropertyDictionary, MGPropertyAccesser.getMoveSpeedPer(levelPd));
		MGPropertyAccesser.setOrPutAtkSpeedPer(fightPropertyDictionary, MGPropertyAccesser.getAtkSpeedPer(levelPd));
		MGPropertyAccesser.setOrPutDodge(fightPropertyDictionary, MGPropertyAccesser.getDodge(levelPd));
		MGPropertyAccesser.setOrPutMaxPAtk(fightPropertyDictionary, MGPropertyAccesser.getMaxPAtk(levelPd));
		MGPropertyAccesser.setOrPutMaxMAtk(fightPropertyDictionary, MGPropertyAccesser.getMaxMAtk(levelPd));
		MGPropertyAccesser.setOrPutMaxTao(fightPropertyDictionary, MGPropertyAccesser.getMaxTao(levelPd));
		MGPropertyAccesser.setOrPutMaxPDef(fightPropertyDictionary, MGPropertyAccesser.getMaxPDef(levelPd));
		MGPropertyAccesser.setOrPutMaxMDef(fightPropertyDictionary, MGPropertyAccesser.getMaxMDef(levelPd));
		MGPropertyAccesser.setOrPutMinPAtk(fightPropertyDictionary, MGPropertyAccesser.getMinPAtk(levelPd));
		MGPropertyAccesser.setOrPutMinMAtk(fightPropertyDictionary, MGPropertyAccesser.getMinMAtk(levelPd));
		MGPropertyAccesser.setOrPutMinTao(fightPropertyDictionary, MGPropertyAccesser.getMinTao(levelPd));
		MGPropertyAccesser.setOrPutMinPDef(fightPropertyDictionary, MGPropertyAccesser.getMinPDef(levelPd));
		MGPropertyAccesser.setOrPutMinMDef(fightPropertyDictionary, MGPropertyAccesser.getMinMDef(levelPd));
		MGPropertyAccesser.setOrPutHit(fightPropertyDictionary, MGPropertyAccesser.getHit(levelPd));
		MGPropertyAccesser.setOrPutMaxHP(fightPropertyDictionary, maxHP);
		MGPropertyAccesser.setOrPutMaxMP(fightPropertyDictionary, maxMP);
		MGPropertyAccesser.setOrPutHP(fightPropertyDictionary, maxHP);
		MGPropertyAccesser.setOrPutMP(fightPropertyDictionary, maxMP);

		if (logger.isDebugEnabled()) {
			logger.debug("configFightPropertiesTo fightPropertyDictionary: " + fightPropertyDictionary);
		}

		fightPropertyMgr.setCrtPropertyDictionary(fightPropertyDictionary);

		player.getPathComponent().setMoveSpeed(player.getMoveSpeed());
	}

	public static PropertyDictionary getPdToClientFromPlayerPd(Player player) {
		PropertyDictionaryModifyPhase snapshot = player.getFightPropertyMgrComponent().getFightPropertyMgr().getSnapshotFromPool();
		PropertyDictionary result = new PropertyDictionary();
		try {
			PropertyDictionary pd = player.getProperty();
			if (logger.isDebugEnabled()) {
				logger.debug("basicData " + pd);
			}

			MGPropertyAccesser.setOrPutKnight(result, MGPropertyAccesser.getKnight(pd));
			MGPropertyAccesser.setOrPutAchievement(result, MGPropertyAccesser.getAchievement(pd));
			MGPropertyAccesser.setOrPutProfessionId(result, MGPropertyAccesser.getProfessionId(pd));
			MGPropertyAccesser.setOrPutPkValue(result, MGPropertyAccesser.getPkValue(pd));
			MGPropertyAccesser.setOrPutMerit(result, MGPropertyAccesser.getMerit(pd));
			MGPropertyAccesser.setOrPutArmorModleId(result, MGPropertyAccesser.getArmorModleId(pd));
			MGPropertyAccesser.setOrPutWingModleId(result, MGPropertyAccesser.getWingModleId(pd));
			MGPropertyAccesser.setOrPutWeaponModleId(result, MGPropertyAccesser.getWeaponModleId(pd));
			MGPropertyAccesser.setOrPutGender(result, MGPropertyAccesser.getGender(pd));
			MGPropertyAccesser.setOrPutName(result, MGPropertyAccesser.getName(pd));
			MGPropertyAccesser.setOrPutHP(result, MGPropertyAccesser.getHP(pd));
			MGPropertyAccesser.setOrPutMP(result, MGPropertyAccesser.getMP(pd));
			MGPropertyAccesser.setOrPutLastLoginTime(result, MGPropertyAccesser.getLastLoginTime(pd));
			MGPropertyAccesser.setOrPutOnlineTime(result, MGPropertyAccesser.getOnlineTime(pd));
			MGPropertyAccesser.setOrPutLastLogoutTime(result, MGPropertyAccesser.getLastLogoutTime(pd));
			MGPropertyAccesser.setOrPutBirthday(result, MGPropertyAccesser.getBirthday(pd));
			MGPropertyAccesser.setOrPutMountModleId(result, MGPropertyAccesser.getMountModleId(pd));
			MGPropertyAccesser.setOrPutUnionOfficialId(result, MGPropertyAccesser.getUnionOfficialId(pd));
			MGPropertyAccesser.setOrPutUnionName(result, MGPropertyAccesser.getUnionName(pd));
			MGPropertyAccesser.setOrPutIsKingCity(result, MGPropertyAccesser.getIsKingCity(pd));

			// 不能使用MGPropertyAccesser.getPositionX(pd)去拿位置
			MGPropertyAccesser.setOrPutSceneRefId(result, player.getSceneRefId());
			MGPropertyAccesser.setOrPutPositionX(result, player.getCrtPosition().getX());
			MGPropertyAccesser.setOrPutPositionY(result, player.getCrtPosition().getY());

			pd = player.getExpComponent().getProperty();
			MGPropertyAccesser.setOrPutExp(result, player.getExpComponent().getExp());
			MGPropertyAccesser.setOrPutLevel(result, player.getExpComponent().getLevel());
			pd = player.getPlayerMoneyComponent().getProperty();
			MGPropertyAccesser.setOrPutGold(result, player.getPlayerMoneyComponent().getGold());
			MGPropertyAccesser.setOrPutBindedGold(result, player.getPlayerMoneyComponent().getBindGold());
			MGPropertyAccesser.setOrPutUnbindedGold(result, player.getPlayerMoneyComponent().getUnbindGold());

			// set fightPower
			MGPropertyAccesser.setOrPutFightValue(result, player.getFightPower());

			if (logger.isDebugEnabled()) {
				logger.debug("fightPropertyData " + snapshot.getPropertyDictionary());
			}

			Map<Short, SimulatorProperty<?>> dict = snapshot.getPropertyDictionary().getDictionary();
			Set<Entry<Short, SimulatorProperty<?>>> entrySet = dict.entrySet();
			for (Entry<Short, SimulatorProperty<?>> entry : entrySet) {
				result.setOrPutValue(entry.getKey(), entry.getValue().getValue());
			}
		} finally {
			FightPropertyMgr.recycleSnapshotToPool(snapshot);
		}

		return result;
	}
}
