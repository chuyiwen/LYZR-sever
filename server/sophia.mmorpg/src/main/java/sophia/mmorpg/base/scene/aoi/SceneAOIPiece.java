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
package sophia.mmorpg.base.scene.aoi;

import java.util.List;

public class SceneAOIPiece {
	
	private List<SceneAOIGrid> gridList;
	
	private int row;
	
	private int column;
	
	public SceneAOIPiece(final List<SceneAOIGrid> gridList,
			final int row, final int column) {
		this.setGridList(gridList);
		this.setRow(row);
		this.setColumn(column);
	}
	
	// 判断是否为2的幂
	public static boolean pow(int v) {
		return ((v & (v - 1)) == 0);
	}
	
	/**
	 * 九宫格包含的格子数
	 * @return
	 */
	public int getGridCount() {
		return getGridList().size();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("P(");
		builder.append(String.format("%3d", getRow())).append(",");
		builder.append(String.format("%3d", getColumn())).append(")");
		return builder.toString();
	}

	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
	}

	public int getColumn() {
		return column;
	}

	public void setColumn(int column) {
		this.column = column;
	}

	public List<SceneAOIGrid> getGridList() {
		return gridList;
	}

	public void setGridList(List<SceneAOIGrid> gridList) {
		this.gridList = gridList;
	}
}
