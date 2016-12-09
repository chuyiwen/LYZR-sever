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
package sophia.mmorpg.base.sprite.aoi;

import sophia.foundation.util.Pair;
import sophia.mmorpg.base.scene.aoi.EightDirection;

/**
 * 转折点 <br>
 * 保存当前转折点到下一个转折点的行与列的格子数
 */
public final class PeakInfo {
	// 45度角
	private boolean isAngle45;
	// 格子数
	private int gridCount;
	// 补偿格子数
	private int extendCount;
	// 总的格子数=格子数+补偿格子数
	private int totalCount;
	// 当前已走格子数
	private int crtGridCount;
	// 转折起始行
	private int row;
	// 转折起始列
	private int column;
	// 当前朝向
	private Pair<Integer, Integer> angle;
	
	public PeakInfo(int row, int column, int dx, int dy) {
		this.row = row;
		this.column = column;

		if (dx != 0 && dy != 0) {
			this.isAngle45 = true;
		} else {
			this.isAngle45 = false;
		}
		
		this.crtGridCount = 0;
		this.extendCount = 0;
		this.totalCount = 0;
		
		byte direction = 0;
		if (dx > 0) {
			if (dy == 0) {
				direction = EightDirection.Right_Direction;
			} else if (dy > 0) {
				direction = EightDirection.RightDown_Direction;
			} else {
				direction = EightDirection.RightUp_Dicretion;
			}
		} else if (dx < 0) {
			if (dy < 0) {
				direction = EightDirection.LeftUp_Dicretion;
			} else if (dy == 0) {
				direction = EightDirection.Left_Dicretion;
			} else {
				direction = EightDirection.LeftDown_Dicretion;
			}
		} else if (dx == 0) {
			if (dy < 0) {
				direction = EightDirection.Up_Dicretion;
			} else {
				direction = EightDirection.Down_Dicretion;
			}
		} 
		
		setAngle(EightDirection.getAngleValue((byte) direction));
	}

	public PeakInfo(int row, int column) {
		this.isAngle45 = false;
		this.gridCount = 0;
		this.crtGridCount = 0;
		this.totalCount = 0;
		this.extendCount = 0;
		this.angle = null;
		this.row = row;
		this.column = column;
	}
	
	public int move(int moveGrids) {
		crtGridCount += moveGrids;
		if (crtGridCount > totalCount) {
			crtGridCount = totalCount;
		}
		
		if (!isAngle45) {
			return crtGridCount;
		}
		
		return crtGridCount / 2 + crtGridCount % 2;
	}

	public boolean isAngle45() {
		return isAngle45;
	}

	public void setAngle45(boolean isAngle45) {
		this.isAngle45 = isAngle45;
	}

	public int getGridCount() {
		return gridCount;
	}

	public void setGridCount(int gridCount) {
		this.gridCount = gridCount;
		
		if (this.isAngle45) {
			this.extendCount = gridCount / 2;
		} 
		
		this.totalCount = gridCount + extendCount;
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

	public Pair<Integer, Integer> getAngle() {
		return angle;
	}

	public void setAngle(Pair<Integer, Integer> angle) {
		this.angle = angle;
	}

	@Override
	public String toString() {
		return "Peak(" + row + "," + column + ") [gridCount:" + gridCount
				+ "] [isAngle45:" + isAngle45 + "]";
	}

	public int getCrtGridCount() {
		return crtGridCount;
	}

	public void setCrtGridCount(int crtGridCount) {
		this.crtGridCount = crtGridCount;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}
}
