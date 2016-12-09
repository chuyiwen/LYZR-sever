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

/**
 * 场景-地形-格
 */
public final class SceneTerrainGrid implements SceneGrid {
	private static final int DefaultGridWidth = 16;
	private static final int DefaultGridHeight = 16;

	private static int gridWidth = DefaultGridWidth;
	private static int gridHeight = DefaultGridHeight;
	private static int halfGridWidth = DefaultGridWidth / 2;
	private static int halfGridHeight = DefaultGridHeight / 2;

	private int sceneWidth;
	private int sceneHeight;

	private short row;
	private short column;

	// private byte terrainType;
	private byte blocked;

	public SceneTerrainGrid(short row, short column, byte blocked, int sceneWidth,
			int sceneHeight) {
		this.row = row;
		this.column = column;
		this.blocked = blocked;
		this.sceneWidth = sceneWidth;
		this.sceneHeight = sceneHeight;

	}

	public SceneTerrainGrid(short row, short column, byte blocked, int sceneWidth,
			int sceneHeight, int gridWidth, int gridHeight) {
		this.row = row;
		this.column = column;
		this.blocked = blocked;
		this.sceneWidth = sceneWidth;
		this.sceneHeight = sceneHeight;
		setGridWidth(gridWidth);
		setGridHeight(gridHeight);
	}

	private static final void setGridWidth(int gridWidth) {
		SceneTerrainGrid.gridWidth = gridWidth;
		SceneTerrainGrid.halfGridWidth = gridWidth / 2;
	}

	@Override
	public final int getGridWidth() {
		return gridWidth;
	}

	@Override
	public final int getGridHeight() {
		return gridHeight;
	}

	private static final void setGridHeight(int gridHeight) {
		SceneTerrainGrid.gridHeight = gridHeight;
		SceneTerrainGrid.halfGridHeight = gridHeight / 2;
	}

	@Override
	public final int getRow() {
		return row;
	}

	@Override
	public final int getColumn() {
		return column;
	}

	@Override
	public final boolean isBlocked() {
		return blocked != 0;
	}

	public final int getXOfCenterPoint() {
		return gridWidth * column + halfGridWidth;
	}

	public final int getYOfCenterPoint() {
		return gridHeight * row + halfGridHeight;
	}

	public final int getSceneWidth() {
		return sceneWidth;
	}

	public final int getSceneHeight() {
		return sceneHeight;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + column;
		result = prime * result + row;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SceneTerrainGrid other = (SceneTerrainGrid) obj;
		if (column != other.column)
			return false;
		if (row != other.row)
			return false;
		return true;
	}

	@Override
	public void setRow(short row) {
		this.row = row;
		
	}

	@Override
	public void setColumn(short column) {
		this.column = column;
		
	}

	@Override
	public boolean isMovable() {
		return !isBlocked();
	}

	@Override
	public String toString() {
		return "SceneTerrainGrid(" + row + "," + column + ")";
	}
}
