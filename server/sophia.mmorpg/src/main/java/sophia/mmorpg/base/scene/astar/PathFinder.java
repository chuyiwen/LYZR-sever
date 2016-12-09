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
import java.util.LinkedList;
import java.util.List;

import sophia.foundation.util.ObjectPool;
import sophia.mmorpg.base.scene.grid.SceneGrid;
import sophia.mmorpg.base.scene.grid.SceneTerrainLayer;

public final class PathFinder {
	private SceneTerrainLayer gridLayer;
//	private int width;
//	private int height;

	public PathFinder(SceneTerrainLayer gridLayer) {
		if (gridLayer.getGridMatrix() == null)
			throw new NullPointerException("GridMatrix cannot be null!");
		this.gridLayer = gridLayer;
//		this.width = gridLayer.getGridMatrix()[0].length;
//		this.height = gridLayer.getGridMatrix().length;
	}

	/**
	 * @param points
	 *            //AStart得到的点
	 * 
	 * @return //拐点列表
	 */
	public List<SceneGrid> getPeak(LinkedList<SceneGrid> points) {
		return Linearisation.getPeak(points, gridLayer);
	}

	/**
	 * @param points
	 *            //AStart得到的点
	 * @return //直线化路径
	 */
	public List<SceneGrid> linearize(LinkedList<SceneGrid> points) {
		return Linearisation.linearize(points, gridLayer);
	}
	
	/**
	 * 默认假设格子数少于1000 * 1000
	 */
	public static ObjectPool<int[][]> IntMatrixPool = new ObjectPool<int[][]>(){

		@Override
		protected int[][] instance() {
			int[][] distance = new int[1000][1000];
			for (int j = 0; j < 1000; j++) {
				for (int i = 0; i < 1000; i++) {
					distance[j][i] = Integer.MAX_VALUE;
				}
			}
			return distance;
		}

		@Override
		protected void onRecycle(int[][] obj) {
			for (int j = 0; j < 1000; j++) {
				for (int i = 0; i < 1000; i++) {
					obj[j][i] = Integer.MAX_VALUE;
				}
			}
		}
	};

	/**
	 * @param start
	 * @param goal
	 * @return //AStart得到的点
	 */
	public LinkedList<SceneGrid> findPath(SceneGrid start, SceneGrid goal) {
		SortLinkedList priorityList = new SortLinkedList();
		priorityList.clear();

		AStarNode startNode = new AStarNode(start);
		AStarNode goalNode = new AStarNode(goal);
		if (!startNode.isHit() || !goalNode.isHit())
			return null;

		// 队列最后节点，如果priorityPosition到达这个节点，就表示不可到达目标点
		AStarNode lastNode = new AStarNode(start);
		lastNode.searchParent = null;
		lastNode.G = -1;// 做标记判断
		priorityList.add(lastNode);
		// addProrityList(priorityList, lastNode);

		// 开始节点
		startNode.searchParent = null;
		priorityList.add(startNode);
		// addProrityList(priorityList, startNode);
		int[][] distance = IntMatrixPool.obtain();

		while (!priorityList.isEmpty()) {
			AStarNode curNode = priorityList.removeFirst();
			// AStarNode curNode = getFirstNode(priorityList);
			if (curNode.G == -1) {
				break;
			}
			if (curNode.equals(goalNode)) {
				IntMatrixPool.recycle(distance);
				return construct(curNode);
			} else {
				for (AStarNode n : curNode.getNeighbors(gridLayer)) {
					if (n.isHit()) {
						int h = curNode.calculateG(n);
						int d = distance[n.getLocation().getRow()][n.getLocation().getColumn()];
						if (h < d) {
							distance[n.getLocation().getRow()][n.getLocation().getColumn()] = h;
							n.searchParent = curNode;
							n.calculateF(goalNode);
							priorityList.add(n);
							// addProrityList(priorityList, n);
						}
					}
				}
			}
		}
		IntMatrixPool.recycle(distance);
		return null;
	}

	// private static void addProrityList(List<AStarNode> priorityList,
	// AStarNode node) {
	//
	// int index = 0;
	// for (int i = 0; i < priorityList.size(); i++) {
	// AStarNode curNode = priorityList.get(i);
	// if (node.compareTo(curNode) <= 0) {
	// index = i;
	// break;
	// }
	// }
	// priorityList.add(index, node);
	//
	// }

	private class SortLinkedList extends ArrayList<AStarNode> {
		private static final long serialVersionUID = 7376325045990600917L;

		public boolean add(AStarNode node) {
			int size=size();
			for (int i = 0; i < size; i++) {
				if (node.compareTo(get(i)) <= 0) {
					add(i, node);
					return true;
				}
			}
			addLast(node);
			return true;
		}

		public AStarNode removeFirst() {
			if (size() > 0) {
				return remove(0);
			}
			return null;
		}

		private void addLast(AStarNode node) {
			add(size(), node);
		}

	}

	// private static AStarNode getFirstNode(List<AStarNode> priorityList) {
	// AStarNode node = priorityList.remove(0);
	// return node;
	// }

	// 回溯路径
	private static LinkedList<SceneGrid> construct(AStarNode node) {
		LinkedList<SceneGrid> path = new LinkedList<SceneGrid>();
		while (node != null) {
			path.addFirst(node.location);
			node = node.searchParent;
		}
		return path;
	}
}
