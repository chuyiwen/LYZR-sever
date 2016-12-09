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
package newbee.morningGlory.mmorpg.player.wing;

import sophia.foundation.property.PropertyDictionary;
import sophia.game.GameRoot;
import sophia.game.ref.AbstractGameRefObjectBase;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;

public final class MGPlayerWingRef extends AbstractGameRefObjectBase {
	private static final long serialVersionUID = -62543832708012431L;
	
	private PropertyDictionary effectProperty = new PropertyDictionary(); 
	
	//Map<String, Integer> updataNeed = new LinkedHashMap<>();
	
//	public Map<String, Integer> getUpdataNeed() {
//		return updataNeed;
//	}
//
//	public void setUpdataNeed(Map<String, Integer> updataNeed) {
//		this.updataNeed = updataNeed;
//	}

	private MGPlayerWingRef nextPlayerWingRef;
	
	public MGPlayerWingRef() {
	}

	public MGPlayerWingRef getNextPlayerWingRef() {
		return nextPlayerWingRef;
	}

	public MGPlayerWingRef getPrePlayerWingRef() {
		String preWingRefId = MGPropertyAccesser.getWingPreRefId(getProperty());
		return (MGPlayerWingRef)GameRoot.getGameRefObjectManager().getManagedObject(preWingRefId);
	}
	
	public void setNextPlayerWingRef(MGPlayerWingRef nextPlayerWingRef) {
		this.nextPlayerWingRef = nextPlayerWingRef;
	}
	
	public byte getCrtWingStageLevel() {
		return MGPropertyAccesser.getStageLevel(getProperty());
	}
	
	public byte getCrtWingStarLevel() {
		return MGPropertyAccesser.getStartLevel(getProperty());
	}
	
	public PropertyDictionary getEffectProperty() {
		return effectProperty;
	}

	public void setEffectProperty(PropertyDictionary effectProperty) {
		this.effectProperty = effectProperty;
	}
}
