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
package sophia.mmorpg.player.persistence;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;

import sophia.foundation.communication.core.ActionEventBase;
import sophia.foundation.data.PersistenceObject;
import sophia.foundation.data.PersistenceParameter;
import sophia.foundation.property.PropertyDictionary;
import sophia.foundation.property.SimulatorProperty;
import sophia.foundation.property.symbol.PropertySymbol;
import sophia.foundation.property.symbol.SimulatorPropertySymbolContext;
import sophia.foundation.util.DebugUtil;
import sophia.mmorpg.MMORPGContext;
import sophia.mmorpg.base.ConcreteComponent;
import sophia.mmorpg.base.sprite.fightProperty.FightPropertyMgr;
import sophia.mmorpg.core.PropertyDictionaryModifyPhase;
import sophia.mmorpg.event.MMORPGEventDefines;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.exp.PlayerExpComponent;
import sophia.mmorpg.player.money.PlayerMoneyComponent;
import sophia.mmorpg.player.property.PlayerConfig;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;

import com.google.common.base.Strings;

public class PlayerSaveComponent extends ConcreteComponent<Player> {

	private static final Logger logger = Logger.getLogger(PlayerSaveComponent.class);

	private final PropertyDictionaryPersistenceObject propertyDictionaryPersistenceObject;

	private final PlayerSaveableObject playerSaveableObject = new PlayerSaveableObject();

	private final List<PersistenceObject> persistenceObjects = new ArrayList<>();

	private final List<PersistenceParameter> independentPropertyParameter = new ArrayList<>();

	public PlayerSaveComponent() {
		propertyDictionaryPersistenceObject = new PropertyDictionaryPersistenceObject();
		persistenceObjects.add(propertyDictionaryPersistenceObject);
	}

	@Override
	public void ready() {
		super.ready();
		addActionEventListener(MMORPGEventDefines.System_Player_PeriodSave_ActionEvent);
	}

	@Override
	public void handleActionEvent(ActionEventBase event) {
		if (MMORPGEventDefines.System_Player_PeriodSave_ActionEvent == event.getActionEventId()) {
			Player player = getConcreteParent();

			if (logger.isDebugEnabled()) {
				logger.debug("saving data " + player);
			}
			
			try {
				snapshot();
			} catch (Exception e) {
				logger.error("handleActionEvent error, player=" + player);
				logger.error("handleActionEvent error, " + DebugUtil.printStack(e));
				return;
			}
			
			MMORPGContext.getPlayerComponent().getSaveService().handlePlayerSaveActionEvent(player);
		}
	}

	@Override
	public void suspend() {
		super.suspend();
		removeActionEventListener(MMORPGEventDefines.System_Player_PeriodSave_ActionEvent);
	}

	public final PlayerSaveableObject getPlayerSaveableObject() {
		return playerSaveableObject;
	}

	public void addPersistenceParameter(PersistenceParameter persistenceParameter) {
		List<PersistenceParameter> persistenceParameters = playerSaveableObject.getPersistenceParameters();
		persistenceParameters.add(persistenceParameter);
	}

	public void addPersistenceParameters(PersistenceObject persistenceObject) {
		persistenceObjects.add(persistenceObject);
		List<PersistenceParameter> persistenceParameters = playerSaveableObject.getPersistenceParameters();
		persistenceParameters.addAll(persistenceObject.getPersistenceParameters());
	}

	public void addIndependentPropertyParameter(PersistenceParameter persistenceParameter) {
		List<PersistenceParameter> persistenceParameters = playerSaveableObject.getPersistenceParameters();
		persistenceParameters.add(persistenceParameter);
		independentPropertyParameter.add(persistenceParameter);
	}

	public void addIndependentPropertyParameter(short propertyId) {
		PropertyDictionary propertyDictionary = propertyDictionaryPersistenceObject.getPropertyDictionary();

		String name = SimulatorPropertySymbolContext.getPropertySymbol(propertyId).getSymbol();
		Object value = propertyDictionary.getValue(propertyId);

		List<PersistenceParameter> persistenceParameters = playerSaveableObject.getPersistenceParameters();
		persistenceParameters.add(new PersistenceParameter(name, value));
	}

	public PropertyDictionaryPersistenceObject getPropertyDictionaryPersistenceObject() {
		return propertyDictionaryPersistenceObject;
	}

	public void snapshot() {
		getPlayerSaveableObject().applyDirty();

		Player player = getConcreteParent();
		PropertyDictionary pd = player.getProperty();
		// 保存最新的等级经验到玩家的pd
		PlayerExpComponent expComponent = player.getExpComponent();
		MGPropertyAccesser.setOrPutExp(pd, expComponent.getExp());
		MGPropertyAccesser.setOrPutLevel(pd, expComponent.getLevel());
		// 保存游戏币到玩家的pd
		PlayerMoneyComponent playerMoneyComponent = player.getPlayerMoneyComponent();
		MGPropertyAccesser.setOrPutGold(pd, playerMoneyComponent.getGold());
		MGPropertyAccesser.setOrPutBindedGold(pd, playerMoneyComponent.getBindGold());
		MGPropertyAccesser.setOrPutUnbindedGold(pd, playerMoneyComponent.getUnbindGold());

		// 保存最新的血量、魔法到玩家的pd
		FightPropertyMgr fightPropertyMgr = getConcreteParent().getFightPropertyMgrComponent().getFightPropertyMgr();
		PropertyDictionaryModifyPhase fightProperty = null;
		try {
			fightProperty = fightPropertyMgr.getSnapshotFromPool();
			
			Map<Short, SimulatorProperty<?>> dict = fightProperty.getPropertyDictionary().getDictionary();
			Set<Entry<Short, SimulatorProperty<?>>> entrySet = dict.entrySet();
			for (Entry<Short, SimulatorProperty<?>> entry : entrySet) {
				pd.setOrPutValue(entry.getKey(), entry.getValue().getValue());
			}
		} finally {
			FightPropertyMgr.recycleSnapshotToPool(fightProperty);
		}

		// 保存最新的位置到玩家的pd
		if (player.getCrtScene() != null) {
			MGPropertyAccesser.setOrPutSceneRefId(pd, player.getCrtScene().getRef().getId());
		}
		MGPropertyAccesser.setOrPutPositionX(pd, player.getCrtPosition().getX());
		MGPropertyAccesser.setOrPutPositionY(pd, player.getCrtPosition().getY());
		logger.debug("Save Position " + player.getCrtPosition());

		// 保存复活场景RefId
		MGPropertyAccesser.setOrPutReviveSceneRefId(pd, player.getReviveSceneRefId());
		
		// 保存战力
		MGPropertyAccesser.setOrPutFightValue(pd, player.getFightPower());
		
		// 保存最大经验值
		MGPropertyAccesser.setOrPutMaxExp(pd, player.getExpComponent().maxExp(player.getLevel()));

		for (PersistenceObject persistenceObject : persistenceObjects) {
			persistenceObject.snapshot();
		}

		PropertyDictionary propertyDictionary = propertyDictionaryPersistenceObject.getPropertyDictionary();
		for (PersistenceParameter persistenceParameter : independentPropertyParameter) {
			String name = persistenceParameter.getName();
			PropertySymbol propertySymbol = SimulatorPropertySymbolContext.getPropertySymbol(name);
			Object value = propertyDictionary.getValue(propertySymbol.getId());
			persistenceParameter.setValue(value);
		}
	}

	public void setDataFrom(Collection<PersistenceParameter> persistenceParameters) {
		getPlayerSaveableObject().applySaved();

		for (PersistenceObject persistenceObject : persistenceObjects) {
			persistenceObject.setDataFrom(persistenceParameters);
		}

		Player player = getConcreteParent();
		PropertyDictionary pd = player.getProperty();
		PlayerConfig.setProfessionRefTo(player, MGPropertyAccesser.getProfessionId(pd), MGPropertyAccesser.getGender(pd));
		PlayerConfig.setLevelAndExpTo(player, MGPropertyAccesser.getLevel(pd), MGPropertyAccesser.getExp(pd));
		PlayerConfig.configFightPropertiesTo(player);

		// 恢复位置
		PlayerConfig.setPositionTo(player, MGPropertyAccesser.getSceneRefId(pd), MGPropertyAccesser.getPositionX(pd), MGPropertyAccesser.getPositionY(pd));
		logger.debug("Recove Position " + player.getCrtPosition());

		// 恢复等级经验
		PlayerConfig.setLevelAndExpTo(player, MGPropertyAccesser.getLevel(pd), MGPropertyAccesser.getExp(pd));
		// 恢复游戏币
		PlayerConfig.setGoldTo(player, MGPropertyAccesser.getGold(pd), MGPropertyAccesser.getBindedGold(pd), MGPropertyAccesser.getUnbindedGold(pd));
		// 恢复复活场景RefId
		String reviveSceneRefId = MGPropertyAccesser.getReviveSceneRefId(pd);
		if (!Strings.isNullOrEmpty(reviveSceneRefId)) {
			player.setReviveSceneRefId(reviveSceneRefId);
		}
	}
}
