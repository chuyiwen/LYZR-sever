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

import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import sophia.mmorpg.base.scene.grid.SceneGrid;
import sophia.mmorpg.base.scene.grid.SceneTerrainLayer;

final class Linearisation {
	private static final Logger logger = Logger.getLogger(Linearisation.class
			.getName());

	/**
	 * @param points
	 *            //AStart得到的点
	 * @param map
	 *            //地图
	 * @return //拐点坐标列表
	 */
	final static List<SceneGrid> getPeak(LinkedList<SceneGrid> points,
			SceneTerrainLayer layer) {
		if (points == null)
			return null;
		long startTime = System.currentTimeMillis();
		int iPathLen = points.size();
		List<SceneGrid> result = new LinkedList<SceneGrid>();
		result.add(points.getFirst());
		if (!isBlock(points.getFirst(), points.getLast(), layer)) {
			result.add(points.getLast());
		} else {
			int iStart, iEndS, iEndE, iEndM;
			iStart = 0;
			while (iStart < iPathLen - 1) {
				iEndS = iStart + 1;
				iEndE = iPathLen - 1;
				if (iStart == 0
						|| isBlock(points.get(iStart), points.get(iEndE), layer)) {// 中间有障碍
					// 折半查找，找到一段最大可能直线路线
					while (iEndS < iEndE) {
						iEndM = (iEndS + iEndE) / 2;
						if (isBlock(points.get(iStart), points.get(iEndM),
								layer))
							iEndE = iEndM - 1;
						else
							iEndS = iEndM + 1;
					}
					if (isBlock(points.get(iStart), points.get(iEndE), layer)) {
						iEndE--;
					}
				}
				result.add(points.get(iEndE));
				iStart = iEndE;
			}
		}
		logger.debug("直线化算法花费时间：" + (System.currentTimeMillis() - startTime));
		return result;
	}

	/**
	 * @param points
	 *            //AStart得到的点
	 * @param map
	 *            //地图
	 * @return //直线化路径
	 */
	final static List<SceneGrid> linearize(LinkedList<SceneGrid> points,
			SceneTerrainLayer layer) {
		List<SceneGrid> result = new LinkedList<SceneGrid>();
		List<SceneGrid> peekList = getPeak(points, layer);
		for (int i = 0; i < peekList.size() - 1; i++) {
			result.addAll(linearize2Point(peekList.get(i), peekList.get(i + 1),
					layer));
		}
		return result;
	}

	/**
	 * @param from
	 *            //起始拐点
	 * @param to
	 *            //目标拐点
	 * @param layer
	 * @return //两点间有阻挡返回true，否则返回false
	 */
	private static final boolean isBlock(SceneGrid from, SceneGrid to,
			SceneTerrainLayer layer) {
		if (!(from.isMovable() && to.isMovable())) {
			return false;
		}
		int dx = to.getColumn() - from.getColumn();
		int dy = to.getRow() - from.getRow();

		int sdx = getSign(dx);
		int sdy = getSign(dy);

		dx = Math.abs(dx) * 2;
		dy = Math.abs(dy) * 2;

		SceneGrid firstBlock = from;
		if (dx >= dy) {
			int e = -(dx / 2) + (dy / 2);
			firstBlock = layer.getGridMatrix()[firstBlock.getRow()][firstBlock
					.getColumn() + sdx];
			for (; firstBlock.getColumn() != to.getColumn(); firstBlock = layer
					.getGridMatrix()[firstBlock.getRow()][firstBlock
					.getColumn() + sdx]) {
				if (e != 0)
					if (firstBlock.isBlocked())
						return true;
				e += dy;
				if (e > 0) {
					firstBlock = layer.getGridMatrix()[firstBlock.getRow()
							+ sdy][firstBlock.getColumn()];
					e -= dx;
					if (firstBlock.isBlocked()) {
						return true;
					}
				}
			}
		} else {
			int e = -(dy / 2) + (dx / 2);
			firstBlock = layer.getGridMatrix()[firstBlock.getRow() + sdy][firstBlock
					.getColumn()];
			for (; firstBlock.getRow() != to.getRow(); firstBlock = layer
					.getGridMatrix()[firstBlock.getRow() + sdy][firstBlock
					.getColumn()]) {
				if (e != 0)
					if (firstBlock.isBlocked())
						return true;
				e += dx;
				if (e > 0) {
					firstBlock = layer.getGridMatrix()[firstBlock.getRow()][firstBlock
							.getColumn() + sdx];
					e -= dy;
					if (firstBlock.isBlocked())
						return true;
				}
			}
		}
		return false;
	}

	/**
	 * @param from
	 * @param to
	 * @param layer
	 * @return //返回from和to两点间连线的直线化路径，不存在则返回null
	 */
	private static final List<SceneGrid> linearize2Point(SceneGrid from,
			SceneGrid to, SceneTerrainLayer layer) {
		List<SceneGrid> resultList = new LinkedList<SceneGrid>();
		if (from.isBlocked() || to.isBlocked()) {
			return null;
		}
		int dx = to.getColumn() - from.getColumn();
		int dy = to.getRow() - from.getRow();

		int sdx = getSign(dx);
		int sdy = getSign(dy);

		dx = Math.abs(dx) * 2;
		dy = Math.abs(dy) * 2;

		SceneGrid firstBlock = from;
		resultList.add(firstBlock);
		if (dx >= dy) {
			int e = -(dx / 2) + (dy / 2);
			firstBlock = layer.getGridMatrix()[firstBlock.getRow()][firstBlock
					.getColumn() + sdx];
			for (; firstBlock.getColumn() != to.getColumn(); firstBlock = layer
					.getGridMatrix()[firstBlock.getRow()][firstBlock
					.getColumn() + sdx]) {
				if (e != 0)
					if (firstBlock.isBlocked())
						return null;
				e += dy;
				if (e > 0) {
					firstBlock = layer.getGridMatrix()[firstBlock.getRow()
							+ sdy][firstBlock.getColumn()];
					e -= dx;
					if (firstBlock.isBlocked()) {
						return null;
					}
				}
				resultList.add(firstBlock);
			}
		} else {
			int e = -(dy / 2) + (dx / 2);
			firstBlock = layer.getGridMatrix()[firstBlock.getRow() + sdy][firstBlock
					.getColumn()];
			for (; firstBlock.getRow() != to.getRow(); firstBlock = layer
					.getGridMatrix()[firstBlock.getRow() + sdy][firstBlock
					.getColumn()]) {
				if (e != 0)
					if (firstBlock.isBlocked())
						return null;
				e += dx;
				if (e > 0) {
					firstBlock = layer.getGridMatrix()[firstBlock.getRow()][firstBlock
							.getColumn() + sdx];
					e -= dy;
					if (firstBlock.isBlocked())
						return null;
				}
				resultList.add(firstBlock);
			}
		}
		return resultList;
	}

	private static final int getSign(int x) {
		return x > 0 ? 1 : (x == 0 ? 0 : -1);
	}
}
