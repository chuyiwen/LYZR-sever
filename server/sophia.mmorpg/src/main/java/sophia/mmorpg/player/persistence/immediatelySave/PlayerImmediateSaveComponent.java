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
package sophia.mmorpg.player.persistence.immediatelySave;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;

import sophia.foundation.communication.core.ActionEventBase;
import sophia.foundation.data.PersistenceObject;
import sophia.foundation.data.PersistenceParameter;
import sophia.foundation.property.PropertyDictionary;
import sophia.foundation.property.symbol.PropertySymbol;
import sophia.foundation.property.symbol.SimulatorPropertySymbolContext;
import sophia.mmorpg.base.ConcreteComponent;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.property.PlayerConfig;

public class PlayerImmediateSaveComponent extends ConcreteComponent<Player> {

	private static final Logger logger = Logger.getLogger(PlayerImmediateSaveComponent.class);

	private final PlayerImmediateSaveableObject playerImmediateSaveableObject = new PlayerImmediateSaveableObject();

	private final List<PersistenceObject> persistenceObjects = new ArrayList<>();

	private final List<PersistenceParameter> independentPropertyParameter = new ArrayList<>();

	public PlayerImmediateSaveComponent() {
		
	}

	@Override
	public void handleActionEvent(ActionEventBase event) {

	}

	public PlayerImmediateSaveableObject getPlayerImmediateSaveableObject() {
		return playerImmediateSaveableObject;
	}

	public void addPersistenceParameter(PersistenceParameter persistenceParameter) {
		List<PersistenceParameter> persistenceParameters = playerImmediateSaveableObject.getPersistenceParameters();
		persistenceParameters.add(persistenceParameter);
	}

	public void addPersistenceParameters(PersistenceObject persistenceObject) {
		persistenceObjects.add(persistenceObject);
		List<PersistenceParameter> persistenceParameters = playerImmediateSaveableObject.getPersistenceParameters();
		
		persistenceParameters.addAll(persistenceObject.getPersistenceParameters());
	}

	public void addIndependentPropertyParameter(PersistenceParameter persistenceParameter) {
		List<PersistenceParameter> persistenceParameters = playerImmediateSaveableObject.getPersistenceParameters();
		persistenceParameters.add(persistenceParameter);
		independentPropertyParameter.add(persistenceParameter);
	}

	
	
	public void snapshot() {
		getPlayerImmediateSaveableObject().applyDirty();

		Player player = getConcreteParent();
		PropertyDictionary pd = player.getProperty();
		
		
		for (PersistenceObject persistenceObject : persistenceObjects) {
			persistenceObject.snapshot();
		}
		
		for (PersistenceParameter persistenceParameter : independentPropertyParameter) {
			String name = persistenceParameter.getName();
			PropertySymbol propertySymbol = SimulatorPropertySymbolContext.getPropertySymbol(name);
			Object value = pd.getValue(propertySymbol.getId());
			persistenceParameter.setValue(value);
		}
			
	}

	public void setDataFrom(Collection<PersistenceParameter> persistenceParameters) {
		getPlayerImmediateSaveableObject().applySaved();

		for (PersistenceObject persistenceObject : persistenceObjects) {
			persistenceObject.setDataFrom(persistenceParameters);
		}

		PlayerConfig.configFightPropertiesTo(getConcreteParent());
	}
}
