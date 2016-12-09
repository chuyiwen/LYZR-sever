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
package sophia.mmorpg.player.mount;

import sophia.foundation.core.ComponentRegistry;
import sophia.foundation.property.PropertyDictionary;
import sophia.game.ref.AbstractGameRefObjectBase;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;

public class MountRef extends AbstractGameRefObjectBase {
	private static final long serialVersionUID = -8580569991667914777L;

	protected ComponentRegistry componentRegistry;

	// 玩家坐骑属性
	private PropertyDictionary effect;

	//玩家上下马属性的修改（速度）
	private PropertyDictionary tmpEffect;

	public PropertyDictionary getEffect() {
		return effect;
	}

	public void setEffect(PropertyDictionary effect) {
		this.effect = effect;
	}

	public PropertyDictionary getTmpEffect() {
		return tmpEffect;
	}

	public void setTmpEffect(PropertyDictionary tmpEffect) {
		this.tmpEffect = tmpEffect;
	}

	public MountRef() {

	}

	public int getStageLevel() {
		return MGPropertyAccesser.getStageLevel(getProperty());
	}
	
	public String getName(){
		return MGPropertyAccesser.getName(getProperty());
	}
	
	public int getStartLevel(){
		return MGPropertyAccesser.getStartLevel(getProperty());
	}

}
