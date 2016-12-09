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
package sophia.mmorpg.base.scene.astar;

import java.util.ArrayList;
import java.util.List;

import sophia.mmorpg.base.scene.grid.SceneGrid;
import sophia.mmorpg.base.scene.grid.SceneTerrainLayer;

final class AStarNode implements Comparable<AStarNode> {

	/*
	 * F = G + H;
	 * 
	 * F: 路径的开销下限。 G：从起始节点到当前节点的代价。 H：启发因子，即当前节点到目标节点的估计代价。
	 */

	int F = Integer.MAX_VALUE;

	int G = 0;// Integer.MAX_VALUE;

	int getG() {
		return G;
	}

	SceneGrid location;

	SceneGrid getLocation() {
		return location;
	}

	void setLocation(SceneGrid location) {
		this.location = location;
	}

	AStarNode searchParent;

	AStarNode(SceneGrid location) {
		this.location = location;
	}

	AStarNode(SceneGrid location, int G) {
		this.location = location;
		this.G = G;
	}

	/*
	 * 计算当前节点的F值
	 */
	void calculateF(AStarNode target) {
		if (searchParent != null)
			this.G = searchParent.G + predict(searchParent);
		this.F = this.G + this.predict(target);
	}

	/*
	 * 计算当前节点的G值
	 */
	int calculateG(AStarNode target) {
		return this.G + predict(target);
	}

	/*
	 * 计算当前节点到目标节点的启发因子代价（H）
	 */
	private int predict(AStarNode target) {
		int dx = Math.abs(this.getLocation().getColumn()
				- target.getLocation().getColumn());
		int dy = Math.abs(this.getLocation().getRow()
				- target.getLocation().getRow());
		return Math.abs(dx - dy) * 5 + Math.min(dx, dy) * 7;

		// return dx + dy;// 曼哈顿距离

		// return dx * dx + dy * dy; // 欧氏距离的平方
	}

	/*
	 * 获取当前节点的邻居
	 */
	List<AStarNode> getNeighbors(SceneTerrainLayer gridLayer) {
		List<AStarNode> neighbors = new ArrayList<AStarNode>();
		int x = location.getColumn();
		int y = location.getRow();

		// 上
		int ty = y - 1;
		if (gridLayer.isInMatrixRange(ty, x)) {
			neighbors.add(new AStarNode(gridLayer.getSceneGrid(ty, x)));
		}

		// 右上
		int tx = x + 1;
		if (gridLayer.isInMatrixRange(ty, tx)) {
			neighbors.add(new AStarNode(gridLayer.getSceneGrid(ty, tx)));
		}
		
		// 左上
		tx = x - 1;
		ty = y - 1;
		if (gridLayer.isInMatrixRange(ty, tx)) {
			neighbors.add(new AStarNode(gridLayer.getSceneGrid(ty, tx)));
		}
		
		// 左
		if (gridLayer.isInMatrixRange(y, tx)) {
			neighbors.add(new AStarNode(gridLayer.getSceneGrid(y, tx)));
		}
		
		// 右
		tx = x + 1;
		if (gridLayer.isInMatrixRange(y, tx)) {
			neighbors.add(new AStarNode(gridLayer.getSceneGrid(y, tx)));
		}
		
		// 右下
		ty = y + 1;
		if (gridLayer.isInMatrixRange(ty, tx)) {
			neighbors.add(new AStarNode(gridLayer.getSceneGrid(ty, tx)));
		}

		// 左下
		tx = x - 1;
		if (gridLayer.isInMatrixRange(ty, tx)) {
			neighbors.add(new AStarNode(gridLayer.getSceneGrid(ty, tx)));
		}
		
		// 下		
		if (gridLayer.isInMatrixRange(ty, x)) {
			neighbors.add(new AStarNode(gridLayer.getSceneGrid(ty, x)));
		}

		return neighbors;
	}

	// 判断当前节点是否可走（没有阻塞）0=没阻塞
	boolean isHit() {
		return location.isMovable();
	}

	public boolean equals(Object obj) {
		if (obj instanceof AStarNode) {
			AStarNode n = (AStarNode) obj;
			if (this.location.equals(n.location)) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	public int compareTo(AStarNode o) {
		// return this.F < o.F ? -1 : (this.F == o.F ? (this.G < o.G ? -1
		// : (this.G == o.G ? 0 : 1)) : 1);
		return this.F < o.F ? -1 : (this.F == o.F ? 0 : 1);
	}

}
