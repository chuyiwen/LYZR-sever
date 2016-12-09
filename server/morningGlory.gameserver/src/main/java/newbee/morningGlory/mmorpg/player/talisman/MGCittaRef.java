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
package newbee.morningGlory.mmorpg.player.talisman;

import sophia.foundation.property.PropertyDictionary;
import sophia.game.ref.AbstractGameRefObjectBase;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;
import sophia.mmorpg.ref.editorgen.MGPropertySymbolDefines;

public class MGCittaRef extends AbstractGameRefObjectBase {

	private static final long serialVersionUID = 1550033027773996916L;

	private PropertyDictionary effectData = new PropertyDictionary();

	public PropertyDictionary getEffectData() {
		return effectData;
	}

	public void setEffectData(PropertyDictionary effectData) {
		this.effectData = effectData;
	}

	public int getProbability() {
		if (getProperty().contains(MGPropertySymbolDefines.Probability_Id)) {
			return MGPropertyAccesser.getProbability(getProperty());
		} else
			return 0;
	}
	public int getCittaLevel() {
		return MGPropertyAccesser.getTalisManLevel(getProperty());	
	}
	public int getLevelUpUseMaterialCount(){
		return MGPropertyAccesser.getUseMaterialCount(getProperty());	
	}
	
	public int getRoleGrade(){
		return MGPropertyAccesser.getRoleGrade(getProperty());
	}
	
	public String getPreRefId(){
		return MGPropertyAccesser.getCittaPreRefId(getProperty());
	}
	public String getNextRefId(){
		return MGPropertyAccesser.getCittaNextRefId(getProperty());
	}
}
