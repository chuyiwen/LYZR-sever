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

import groovy.lang.Closure;
import sophia.foundation.property.PropertyDictionary;
import sophia.game.ref.AbstractGameRefObjectBase;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;
import sophia.mmorpg.utils.RuntimeResult;

/**
 * 法宝-引用
 */
public final class MGTalismanRef extends AbstractGameRefObjectBase {
	private static final long serialVersionUID = -3665839132612144919L;
	private PropertyDictionary effectData = new PropertyDictionary();
	private PropertyDictionary questData;
	private Closure<RuntimeResult> acquireClosure;

	private Closure<RuntimeResult> activeClosure;

	private Closure<RuntimeResult> unactiveClosure;
	public MGTalismanRef() {
		
	}
	public PropertyDictionary getEffectData() {
		return effectData;
	}
	public void setEffectData(PropertyDictionary effectData) {
		this.effectData = effectData;
	}
	public PropertyDictionary getQuestData() {
		return questData;
	}
	public void setQuestData(PropertyDictionary questData) {
		this.questData = questData;
	}
	public Closure<RuntimeResult> getAcquireClosure() {
		return acquireClosure;
	}
	public void setAcquireClosure(Closure<RuntimeResult> acquireClosure) {
		this.acquireClosure = acquireClosure;
	}
	public Closure<RuntimeResult> getActiveClosure() {
		return activeClosure;
	}
	public void setActiveClosure(Closure<RuntimeResult> activeClosure) {
		this.activeClosure = activeClosure;
	}
	public Closure<RuntimeResult> getUnactiveClosure() {
		return unactiveClosure;
	}
	public void setUnactiveClosure(Closure<RuntimeResult> unactiveClosure) {
		this.unactiveClosure = unactiveClosure;
	}
	
	public byte getTailsmanType(){
		return MGPropertyAccesser.getTailsmanType(getProperty());
	}
	
	public boolean isPassiveTalisman(){
		return MGPropertyAccesser.getTailsmanType(getProperty()) == 0;
	}
	
	public String getName(){
		return MGPropertyAccesser.getName(getProperty());
	}
}
