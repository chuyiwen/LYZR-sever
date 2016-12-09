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
package sophia.mmorpg.core.state;

import java.util.HashSet;
import java.util.Set;

public abstract class FSMStateBase<T> {
	/**
	 * 状态Id
	 */
	protected short id;
	/**
	 * 可以转换的状态Id列表
	 */
	protected final Set<Short> transitionSet = new HashSet<Short>();
	/**
	 * 跨层转换阻止的状态Id列表
	 */
	protected final Set<Short> crossLayerBlockedStateSet = new HashSet<Short>();
	/**
	 * 跨层转换被替换的状态Id列表
	 */
	protected final Set<Short> crossLayerReplacedStateSet = new HashSet<Short>();

	protected FSMStateBase() {
		
	}
	
	protected FSMStateBase(short id) {
		this.id = id;
	}
	
	public final short getId() {
		return id;
	}

	public final void setId(short id) {
		this.id = id;
	}

	public final Set<Short> getTransitionSet() {
		return transitionSet;
	}

	public abstract void enter(T owner);
	public abstract void exit(T owner);
	
	public void addTransition(short... transitionIds) {
		for(short id : transitionIds) {
			transitionSet.add(id);
		}
	}
	
	public boolean canTransition (short id) {
		boolean can = this.transitionSet.contains(id);
		return can;
	}
	
	public final Set<Short> getCrossLayerBlockedStateSet() {
		return crossLayerBlockedStateSet;
	}

	public final void addCrossLayerBlockedState(short... blockedIds) {
		for(short id : blockedIds) {
			crossLayerBlockedStateSet.add(id);
		}
	}
	
	public final boolean isBlockedBy(short id) {
		boolean isBlocked = this.crossLayerBlockedStateSet.contains(id);
		return isBlocked;
	}
	
	public Set<Short> getCrossLayerReplacedStateSet() {
		return crossLayerReplacedStateSet;
	}
	
	public final void addCrossLayerReplacedState(short... replacedIds) {
		for(short id : replacedIds) {
			crossLayerReplacedStateSet.add(id);
		}
	}
	
	public final boolean isReplacedBy(short id) {
		boolean isReplaced = this.crossLayerReplacedStateSet.contains(id);
		return isReplaced;
	}
}
