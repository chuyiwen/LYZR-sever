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

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Preconditions;


/**
 * <b>场景-AOI管理</b></br>
 */
public final class SceneAOILayer {
	// 地形格大小, 单位像素
	private static final int GIRD_SIZE = 16;
	
	// AOI Grid默认大小等于格子大小
	// AOI Grid不能超过九宫格大小，并且一个九宫格大小刚好是AOI Grid的整数倍
	// AOI Grid相对于地形格的倍数
	public static final int AOIGRID_MULTIPLE = 4;
	
	// 可视区域大小, 单位像素
//	private static final int AOIWidth = 1120;
//	private static final int AOIHeight = 800;
	
	private static final int AOIWidth = 1088;
	private static final int AOIHeight = 768;
	
	// 实际可视区域大小, 单位像素
	private static int RealAOIWidth;
	private static int RealAOIHeight;

	// 九宫格的格子大小, 单位像素
	private static int AOIPieceWidth;
	private static int AOIPieceHeight;
	
	private static void init() {
		AOIPieceWidth = AOIWidth * 4 / 9;
		AOIPieceHeight = AOIHeight * 4 / 9;
		int modv = AOIPieceWidth % GIRD_SIZE;
		if (modv > 0) {
			AOIPieceWidth = AOIPieceWidth - modv + GIRD_SIZE;
		}

		modv = AOIPieceHeight % GIRD_SIZE;
		if (modv > 0) {
			AOIPieceHeight = AOIPieceHeight - modv + GIRD_SIZE;
		}

		RealAOIWidth = 3 * AOIPieceWidth;
		RealAOIHeight = 3 * AOIPieceHeight;
	}
	
	static {
		init();
	}
	
	// 地形格行数
	private int rows;
	// 地形格列数
	private int columns;
	// 九宫格行数
	private int pieceRows;
	// 九宫格列数
	private int pieceColumns;
	// 单个九宫格高度，格子数
	private int pieceHeight;
	// 单个九宫格宽度，格子数
	private int pieceWidth;
	// AOI Grid rows
	private int gridRows;
	// AOI Grid columns
	private int gridColumns;
	// AOI Grid pixel height
	private int gridHeight;
	// AOI Grid pixel width
	private int gridWidth;
	
	private SceneAOIGrid[][] matrix;
	
	private SceneAOIPiece[][] pieces;
	
	public SceneAOILayer(int rows, int columns) {
		this.rows = rows;
		this.columns = columns;
		initialize();
	}

	public final int getRows() {
		return rows;
	}

	public final int getColumns() {
		return columns;
	}
	
	public final int getPieceRows() {
		return pieceRows;
	}
	
	public final int getPieceColumns() {
		return pieceColumns;
	}
	
	public final int getGridRows() {
		return gridRows;
	}
	
	public final int getGridColumns() {
		return gridColumns;
	}
	
	public final int getPieceHeight() {
		return pieceHeight;
	}
	
	public final int getPieceWidth() {
		return pieceWidth;
	}
	
	public final boolean isInMatrixRange(final int aoi_x, final int aoi_y) {
		return (aoi_y >= 0 && aoi_y < gridRows) && (aoi_x >= 0 && aoi_x < gridColumns); 
	}

	public final SceneAOIGrid[][] getMatrix() {
		return matrix;
	}
	
	public final SceneAOIPiece[][] getPieces() {
		return pieces;
	}
	
	/**
	 * 获取AOIGrid
	 * @param x	坐标x
	 * @param y 坐标y
	 * @return
	 */
	public final SceneAOIGrid getAOIGrid(final int x, final int y) {
		int gridRow = y / AOIGRID_MULTIPLE;
		int gridColumn = x / AOIGRID_MULTIPLE;
		if (gridRow >= gridRows || gridColumn >= gridColumns) {
			return null;
		}
		
		return matrix[gridRow][gridColumn];
	}
	
	/**
	 * 获取AOIGrid
	 * @param row		AOI行
	 * @param column 	AOI列
	 * @return
	 */
	public final SceneAOIGrid getSceneAOIGrid(final int row, final int column) {
		Preconditions.checkArgument(row >= 0 && row < gridRows);
		Preconditions.checkArgument(column >= 0 && column < gridColumns);
		return matrix[row][column];
	}
	
	/**
	 * 获得所在九宫格格子
	 * @param aoi_x
	 * @param aoi_y
	 * @return
	 */
	public final SceneAOIPiece getPiece(final SceneAOIGrid aoiGrid) {
		// 第几列几行，从0开始
		int column = aoiGrid.getColumn() / pieceWidth;
		int row = aoiGrid.getRow() / pieceHeight;
		if (row >= pieceRows || column >= pieceColumns) {
			return null;
		}
		
		return pieces[row][column];
	}
	
	/**
	 * 计算坐标x,y的所在的九宫格
	 * @param aoi_x
	 * @param aoi_y
	 * @return
	 */
	public final List<SceneAOIPiece> getPieceSquare(final SceneAOIGrid aoiGrid) {
		// 第几列几行，从0开始
		int column = aoiGrid.getColumn() / pieceWidth ;
		int row = aoiGrid.getRow() / pieceHeight;
		
		List<SceneAOIPiece> pieceList = new ArrayList<SceneAOIPiece>(9);
		
		int c0 = column - 1;
		int r0 = row - 1;
		int c1 = column + 1;
		int r1 = row + 1;
		if (c0 < 0) {
			c0 = 0;
		} 
		if (r0 < 0) {
			r0 = 0;
		}
		if (c1 > pieceColumns - 1) {
			c1 = column;
		} 
		if (r1 > pieceRows - 1) {
			r1 = row;
		}
		
		for (int i = r0; i <= r1; i++) {
			for (int j = c0; j <= c1; j++) {
				pieceList.add(pieces[i][j]);
			}
		}
		
		return pieceList;
	}
	
	private final List<SceneAOIPiece> getPieceChange(int pieceRow, int pieceColumn, int dx, int dy) {
		
		int column, row;
		
		List<SceneAOIPiece> pieceList = new ArrayList<SceneAOIPiece>(5);
		
		if (dx != 0 && dy != 0) {
			row = pieceRow + 2 * dy;
			if (row >= 0 && row < pieceRows) {
				for (int i = 0; i <= 2; i++) {
					column = pieceColumn + i * dx;
					if (column >= 0 && column < pieceColumns) {
						pieceList.add(pieces[row][column]);
					}
				}
			}
			column = pieceColumn + 2 * dx;
			if (column >= 0 && column < pieceColumns) {
				for (int i = 0; i <= 1; i++) {
					row = pieceRow + i * dy;
					if (row >= 0 && row < pieceRows) {
						pieceList.add(pieces[row][column]);
					}
				}
			}
		} else if (dx != 0) {
			column = pieceColumn + 2 * dx;
			if (column >= 0 && column < pieceColumns) {
				for (int i = -1; i <= 1; i++) {
					row = pieceRow + i;
					if (row >= 0 && row < pieceRows) {
						pieceList.add(pieces[row][column]);
					}
				}
			}
		} else if (dy != 0) {
			row = pieceRow + 2 * dy;
			if (row >= 0 && row < pieceRows) {
				for (int i = -1; i <= 1; i++) {
					column = pieceColumn + i;
					if (column >= 0 && column < pieceColumns) {
						pieceList.add(pieces[row][column]);
					}
				}
			}
		}

		return pieceList;
	}
	

	public final List<SceneAOIPiece> getJoinPieceSet(final SceneAOIGrid src,
			final SceneAOIGrid now) {
		return getPieceSet(src, now, true);
	}
	
	public final List<SceneAOIPiece> getRemovePieceSet(final SceneAOIGrid src,
			final SceneAOIGrid now) {
		return getPieceSet(src, now, false);
	}
	
	/**
	 * 计算移动后九宫格的变化格子 <br>
	 * 当前的做法是根据坐标变换，找出改变的格子，另外一种做法是获得之前所在的PieceSquare，与当前所在的PieceSqure进行比较，
	 * 这样就可以找出新增和减少的格子
	 * 
	 * @param src
	 * @param now
	 * @param isJoin
	 * @return
	 */
	private final List<SceneAOIPiece> getPieceSet(final SceneAOIGrid src,
			final SceneAOIGrid now, boolean isJoin) {

		int srcPieceColumn = src.getColumn() / pieceWidth;
		int srcPieceRow = src.getRow() / pieceHeight;
		int nowPieceColumn = now.getColumn() / pieceWidth;
		int nowPieceRow = now.getRow() / pieceHeight;
		// 计算九宫格横向移动格子数
		int dx = nowPieceColumn - srcPieceColumn;
		// 计算九宫格纵向移动格子数
		int dy = nowPieceRow - srcPieceRow;

		// 九宫格没有发生改变
		if (dx == 0 && dy == 0) {
			return null;
		}
		// 若两点之间移动的距离达到2个以上九宫格格子，则直接返回周围的9个九宫格格子
		else if (Math.abs(dx) >= 2 || Math.abs(dy) >= 2) {
			if (!isJoin) {
				return getPieceSquare(src);
			} else {
				return getPieceSquare(now);
			}
		}

		int pieceColumn;
		int pieceRow;
		// 如果是计算离开的九宫格
		if (!isJoin) {
			dx = -dx;
			dy = -dy;
			pieceColumn = nowPieceColumn;
			pieceRow = nowPieceRow;
		} else {
			pieceColumn = srcPieceColumn;
			pieceRow = srcPieceRow;
		}

		return getPieceChange(pieceRow, pieceColumn, dx, dy);
	}
	
	private List<SceneAOIGrid> findAOIGrid(int pieceRow, int pieceColumn) {
		int gridColumn = pieceColumn * pieceWidth;
		int gridRow = pieceRow * pieceHeight;
		int gridColumnEnd = gridColumn + pieceWidth;
		int gridRowEnd = gridRow + pieceHeight;
		ArrayList<SceneAOIGrid> arrList = new ArrayList<SceneAOIGrid>();
		for (int i = gridRow; i < gridRowEnd; i++) {
			for (int j = gridColumn; j < gridColumnEnd; j++) {
				if (i < gridRows && j < gridColumns) {
					arrList.add(matrix[i][j]);
				}
			}
		}
		
		arrList.trimToSize();
		return arrList;
	}
	
	
	private void initialize() {		
		gridHeight = GIRD_SIZE * AOIGRID_MULTIPLE;
		gridWidth = GIRD_SIZE * AOIGRID_MULTIPLE;
		
		// 初始化AOI Grid
		gridRows = rows / AOIGRID_MULTIPLE;
		if (rows % AOIGRID_MULTIPLE != 0) {
			gridRows += 1;
		}
		
		gridColumns = columns / AOIGRID_MULTIPLE;
		if (columns % AOIGRID_MULTIPLE != 0) {
			gridColumns += 1;
		}
		
		matrix = new SceneAOIGrid[gridRows][gridColumns];
		for (int i = 0; i < gridRows; i++) {
			for (int j = 0; j < gridColumns; j++) {
				matrix[i][j] = new SetImplSceneAOIGrid(i, j);
			}
		}
		
		pieceWidth = AOIPieceWidth / gridWidth;
		int modv = (columns % pieceWidth == 0) ? 0 : 1;
		pieceColumns = columns / pieceWidth + modv;
		pieceHeight = AOIPieceHeight / gridHeight;
		modv = (rows % pieceHeight == 0) ? 0 : 1;
		pieceRows = rows / pieceHeight + modv;
		
		pieces = new SceneAOIPiece[pieceRows][pieceColumns];
		for (int i = 0; i < pieceRows; i++) {
			for (int j = 0; j < pieceColumns; j++) {
				pieces[i][j] = new SceneAOIPiece(findAOIGrid(i, j), i, j);
			}
		}
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("------------------------------AOILayer------------------------------\n");
		builder.append(String.format("Scene Gird Size:\t")).append(GIRD_SIZE).append("p rows:")
				.append(rows).append(" columns:").append(columns).append("\n");
		builder.append(String.format("Visible Area Size:\t")).append(AOIWidth).append("p*")
				.append(AOIHeight).append("p\n");
		builder.append(String.format("Visible Area Real Size:\t")).append(RealAOIWidth)
				.append("p*").append(RealAOIHeight).append("p\n");
		builder.append(String.format("Piece Squared Size:\t")).append(AOIPieceWidth)
				.append("p*").append(AOIPieceHeight).append("p\n");
		builder.append(String.format("Piece Squared Gird:\t")).append("rows:")
				.append(AOIPieceHeight / gridHeight).append(" columns:")
				.append(AOIPieceWidth / gridWidth).append("\n");
		builder.append(String.format("AOIGrid:\t\t")).append("rows:").append(gridRows).append(" columns:")
				.append(gridColumns).append("\n");
		return builder.toString();
	}
}
