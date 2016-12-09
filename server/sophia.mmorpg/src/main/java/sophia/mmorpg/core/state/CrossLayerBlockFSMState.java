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


public abstract class CrossLayerBlockFSMState<T> extends FSMStateBase<T> {
	/**
	 * 跨层转换阻止的状态Id列表
	 */
//	protected final Set<Short> crossLayerBlockedStateSet = new HashSet<Short>();
	
	protected CrossLayerBlockFSMState() {
		
	}
	
	protected CrossLayerBlockFSMState(short id) {
		super(id);
	}
	
//	public final Set<Short> getCrossLayerBlockedStateSet() {
//		return crossLayerBlockedStateSet;
//	}
//
//	public final void addCrossLayerBlockedState(short... blockedIds) {
//		for(short id : blockedIds) {
//			crossLayerBlockedStateSet.add(id);
//		}
//	}
//	
//	public final boolean isBlockedBy(short id) {
//		boolean isBlocked = this.crossLayerBlockedStateSet.contains(id);
//		return isBlocked;
//	}
}
