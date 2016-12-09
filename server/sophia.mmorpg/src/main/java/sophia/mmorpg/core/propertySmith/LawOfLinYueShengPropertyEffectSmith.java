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
package sophia.mmorpg.core.propertySmith;

import sophia.game.component.GameObject;

public abstract class LawOfLinYueShengPropertyEffectSmith<T extends GameObject> implements PropertyEffectSmith<T> {
	protected final T owner;
	
	protected PropertyEffectSmith<T> parent;
	
	protected LawOfLinYueShengPropertyEffectSmith(T owner) {
		this.owner = owner;
	}

	@Override
	public final T getOwner() {
		return owner;
	}

	@Override
	public boolean isRoot() {
		return false;
	}

	@Override
	public final PropertyEffectSmith<T> getParent() {
		return parent;
	}

	@Override
	public final void setParent(PropertyEffectSmith<T> parent) {
		this.parent = parent;
	}

	@Override
	public final void onPropertyChange() {
		calculate();
		if (!isRoot()) {
			parent.onPropertyChange();
		}
	}
	
	protected abstract void calculate();
	
	
}
