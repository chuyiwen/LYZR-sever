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

import sophia.foundation.collect.GenericTree;
import sophia.foundation.collect.GenericTreeNode;
import sophia.game.component.GameObject;

public class PropertyEffectSmithMgr<T extends PropertyEffectSmith<? extends GameObject>> {
	private final GenericTree<T> mountSimithTree = new GenericTree<>();
	
	protected T root;
	
	public PropertyEffectSmithMgr() {
	}
	
	public final void add(T mountSmith) {
		@SuppressWarnings("unchecked")
		T parent = (T) mountSmith.getParent();
		GenericTreeNode<T> treeNode = mountSimithTree.find(parent);
		if (treeNode == null) {
			 this.root = mountSmith;
			 treeNode = new GenericTreeNode<T>(mountSmith);
			 mountSimithTree.setRoot(treeNode);
		}else{ 
			 treeNode.addChild(new GenericTreeNode<T>(mountSmith));
		}
	}

	public final T getRoot() {
		return root;
	}

	public final GenericTree<T> getMountSimithTree() {
		return mountSimithTree;
	}
}
