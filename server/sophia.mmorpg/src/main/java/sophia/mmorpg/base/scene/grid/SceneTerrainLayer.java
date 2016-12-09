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
package sophia.mmorpg.base.scene.grid;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import sophia.foundation.util.Position;
import sophia.mmorpg.base.scene.astar.PathFinder;

import com.google.common.base.Preconditions;

//@formatter:off
/**
* This class treats that a map of a scene consists of grids.
* A map of column * row with nColumn of columns and nRow of rows
* of grids seems like this: 
*   O(0,0)                A(column, 0)
*     +---+---+...+---+---+
*     |   |   |   |   |   |
*     +---+---+...+---+---+
*     |   |   |   |   |   |
*     +---+---+...+---+---+
*     |   |   |   |   |   |
*     +---+---+...+---+---+
*   B(0, row)             C(column, row)
*/
//@formatter:on

public final class SceneTerrainLayer {
	/** 地形-像素宽 */
	private int pixelWidth;
	/** 地形-像素高 */
	private int pixelHeight;
	/** 地形-格行数 */
	private int nRow;
	/** 地形-格列数 */
	private int nColumn;
	/** 地形-格矩阵 */
	private SceneTerrainGrid[][] grids;
	private PathFinder pathFinder = null;

	public SceneTerrainLayer(SceneTerrainGrid[][] grids) {
		super();
		init(grids);
	}

	public float getPixelWidth() {
		return pixelWidth;
	}

	public float getPixelHeight() {
		return pixelHeight;
	}

	public int getnRow() {
		return nRow;
	}

	public int getnColumn() {
		return nColumn;
	}

	public SceneGrid[][] getGridMatrix() {
		return grids;
	}

	public SceneGrid getSceneGrid(int row, int column) {
		Preconditions.checkArgument(isInMatrixRange(row, column), "row=" + row + " column=" + column + " nRow " + nRow + " nColumn " + this.grids[0].length);

		return this.grids[row][column];
	}

	public SceneGrid getLeft(SceneGrid sceneGrid) {
		int row = sceneGrid.getRow();
		int column = sceneGrid.getColumn();
		column -= 1;
		if (isInMatrixRange(row, column)) {
			return grids[row][column];
		} else
			return null;
	}

	public SceneGrid getRight(SceneGrid sceneGrid) {
		int row = sceneGrid.getRow();
		int column = sceneGrid.getColumn();
		column += 1;
		if (isInMatrixRange(row, column)) {
			return grids[row][column];
		} else
			return null;
	}

	public SceneGrid getTop(SceneGrid sceneGrid) {
		int row = sceneGrid.getRow();
		int column = sceneGrid.getColumn();
		row -= 1;
		if (isInMatrixRange(row, column)) {
			return grids[row][column];
		} else
			return null;
	}

	public SceneGrid getBottom(SceneGrid sceneGrid) {
		int row = sceneGrid.getRow();
		int column = sceneGrid.getColumn();
		row += 1;
		if (isInMatrixRange(row, column)) {
			return grids[row][column];
		} else
			return null;
	}

	public List<SceneGrid> getNeighbors4(SceneGrid grid) {
		List<SceneGrid> list = new ArrayList<SceneGrid>(4);
		SceneGrid top = getTop(grid);
		if (top != null) {
			list.add(top);
		}
		SceneGrid buttom = getBottom(grid);
		if (buttom != null) {
			list.add(buttom);
		}
		SceneGrid left = getLeft(grid);
		if (left != null) {
			list.add(left);
		}
		SceneGrid right = getRight(grid);
		if (right != null) {
			list.add(right);
		}
		return list;
	}

	public boolean isInMatrixRange(int row, int column) {
		return (row >= 0 && row < nRow) && (column >= 0 && column < nColumn);
	}

	public List<SceneGrid> getNeighbors8(SceneGrid grid) {
		return getNeighbors(grid, 3, 3);
	}

	public List<SceneGrid> getNeighbors(SceneGrid grid, int rowCount, int columnCount) {
		int row = grid.getRow();
		int column = grid.getColumn();
		int rf = row - rowCount / 2;
		int re = row + rowCount / 2;
		int cf = column - columnCount / 2;
		int ce = column + columnCount / 2;

		List<SceneGrid> grids = new ArrayList<SceneGrid>();
		for (int i = rf; i <= re; i++) {
			for (int j = cf; j < ce; j++) {
				if (isInMatrixRange(i, j)) {
					SceneGrid sceneGrid = getSceneGrid(i, j);
					if (!grids.contains(sceneGrid))
						grids.add(sceneGrid);
				}
			}
		}
		return grids;
	}

	public List<SceneGrid> getNeighbors(SceneGrid grid, int radiusInGrid) {
		List<SceneGrid> ls = getNeighbors(grid, radiusInGrid * 2, radiusInGrid * 2);
		Iterator<SceneGrid> it = ls.iterator();
		while (it.hasNext()) {
			SceneGrid g = it.next();
			if (TerrainGridGeom.getDistance(g, grid) > radiusInGrid)
				it.remove();
		}
		return ls;
	}

	private void init(SceneTerrainGrid[][] grids) {
		Preconditions.checkNotNull(grids);

		this.grids = grids;

		this.nRow = this.grids.length;
		this.nColumn = this.grids[0].length;
		SceneTerrainGrid grid = this.grids[0][0];
		this.pixelWidth = grid.getGridWidth() * this.nColumn;
		this.pixelHeight = grid.getGridHeight() * this.nRow;
		this.pathFinder = new PathFinder(this);
	}

	public List<SceneGrid> getPath(SceneGrid start, SceneGrid end) {
		return pathFinder.findPath(start, end);
	}

	public List<SceneGrid> getPeak(List<SceneGrid> points) {
		return pathFinder.getPeak(new LinkedList<SceneGrid>(points));
	}

	public List<SceneGrid> getPeakPath(List<SceneGrid> points) {
		return pathFinder.linearize(new LinkedList<SceneGrid>(points));
	}

	public List<SceneGrid> getInRangeGrids(int row, int column, int rowCount, int columnCount) {
		int rf = row;
		int re = row + rowCount;
		if (re > getnRow() - 1) {
			rf = rf - (re - getnRow() + 1);
			re = getnRow() - 1;
		}
		if (rf < 0) {
			re = re - rf;
			rf = 0;
		}

		int cf = column;
		int ce = column + columnCount;
		if (ce > getnColumn() - 1) {
			cf = cf - (ce - getnColumn() + 1);
			ce = getnColumn() - 1;
		}
		if (cf < 0) {
			ce = ce - cf;
			cf = 0;
		}

		List<SceneGrid> grids = new ArrayList<SceneGrid>();
		for (int i = rf; i <= re; i++)
			for (int j = cf; j <= ce; j++)
				if (isInMatrixRange(i, j))
					grids.add(getSceneGrid(i, j));
		return grids;
	}

	public List<SceneGrid> getInRangeGrids(SceneGrid leftUp, SceneGrid rightDown) {
		int x = leftUp.getColumn();
		int y = leftUp.getRow();
		int width = rightDown.getColumn() - x;
		int height = rightDown.getRow() - y;
		return getInRangeGrids(y, x, height, width);
	}

	public List<SceneGrid> getInRangeGrids(SceneGrid leftUp, SceneGrid rightDown, boolean blocked) {
		List<SceneGrid> coll = getInRangeGrids(leftUp, rightDown);
		List<SceneGrid> res = new ArrayList<SceneGrid>();
		for (SceneGrid grid : coll) {
			if (blocked == grid.isBlocked()) {
				res.add(grid);
			}
		}
		return res;
	}

	public SceneGrid getRandomUnblockedGrid(SceneGrid grid, int radiusInGrid) {
		int row = grid.getRow();
		int column = grid.getColumn();
		int rf = row - radiusInGrid;
		int re = row + radiusInGrid;
		int cf = column - radiusInGrid;
		int ce = column + radiusInGrid;

		int c = 0;
		int timesToTry = 1000;
		SceneGrid sceneGrid = null;
		while (c++ < timesToTry && sceneGrid == null) {
			int i = rf + (int) (Math.random() * (re - rf));
			int j = cf + (int) (Math.random() * (ce - cf));
			if (isInMatrixRange(i, j)) {
				SceneGrid _grid = getSceneGrid(i, j);
				if (!_grid.isBlocked()) {
					sceneGrid = _grid;
					break;
				}
			}
		}
		return sceneGrid;
	}

	public SceneGrid getRandomUnblockedGrid(Position grid, int radiusInGrid) {
		int row = grid.getY();
		int column = grid.getX();
		int rf = row - radiusInGrid;
		int re = row + radiusInGrid;
		int cf = column - radiusInGrid;
		int ce = column + radiusInGrid;

		int c = 0;
		int timesToTry = 1000;
		SceneGrid sceneGrid = null;
		while (c++ < timesToTry && sceneGrid == null) {
			int i = rf + (int) (Math.random() * (re - rf));
			int j = cf + (int) (Math.random() * (ce - cf));
			if (isInMatrixRange(i, j)) {
				SceneGrid _grid = getSceneGrid(i, j);
				if (!_grid.isBlocked()) {
					sceneGrid = _grid;
					break;
				}
			}
		}
		return sceneGrid;
	}
}
