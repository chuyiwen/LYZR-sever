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

import org.junit.Test;

import sophia.foundation.util.Position;

public class SceneAOILayerTest {
	
	public void print(int rows, int columns) {
		SceneAOILayer layer = new SceneAOILayer(rows, columns);
//		SceneAOIGrid[][] matrix = layer.getMatrix();
//		for (int i = 0; i < layer.getGridRows(); i++) {
//			System.out.println();
//			for (int j = 0; j < layer.getGridColumns(); j++) {
//				System.out.print(matrix[i][j]);
//			}
//		}
		
		SceneAOIPiece[][] pieces = layer.getPieces();
		for (int i = 0; i < layer.getPieceRows(); i++) {
			System.out.println();
			for (int j = 0; j < layer.getPieceColumns(); j++) {
				System.out.print(pieces[i][j]);
			}
		}
		
		System.out.println();
		System.out.println(layer);
	}
	
	@Test
	public void split() {
		// 相当于x坐标有300个格子
		int pixelWidth = 4800;
		// 相当于y坐标有240个格子
		int pixelHeight = 3840;
		int modv = (pixelWidth % 16 == 0) ? 0 : 1;
		int columns = pixelWidth / 16 + modv;
		modv = (pixelHeight % 16 == 0) ? 0 : 1;
		int rows = pixelHeight / 16 + modv;
		print(rows, columns);
	}
	
	@Test
	public void splitNormal() {
		int pixelWidth = 5900;
		int pixelHeight = 4950;
		int modv = (pixelWidth % 16 == 0) ? 0 : 1;
		int columns = pixelWidth / 16 + modv;
		modv = (pixelHeight % 16 == 0) ? 0 : 1;
		int rows = pixelHeight / 16 + modv;
		print(rows, columns);
	}
	
	@Test
	public void pieceChange() {
		// 相当于x坐标有300个格子
		int pixelWidth = 4800;
		// 相当于y坐标有240个格子
		int pixelHeight = 3840;
		int modv = (pixelWidth % 16 == 0) ? 0 : 1;
		int columns = pixelWidth / 16 + modv;
		modv = (pixelHeight % 16 == 0) ? 0 : 1;
		int rows = pixelHeight / 16 + modv;
		SceneAOILayer layer = new SceneAOILayer(rows, columns);
		SceneAOIPiece[][] pieces = layer.getPieces();
		for (int i = 0; i < layer.getPieceRows(); i++) {
			System.out.println();
			for (int j = 0; j < layer.getPieceColumns(); j++) {
				System.out.print(pieces[i][j]);
			}
		}
		
		System.out.println();
		// test1
		//SFPoint2D src = new SFPoint2D(10, 10);
		//SFPoint2D dst = new SFPoint2D(11, 11);
		
		// test2
		//SFPoint2D src = new SFPoint2D(10, 10);
		//SFPoint2D dst = new SFPoint2D(11, 10);
		
		// test3
		Position src = new Position(27, 18);
		Position dst = new Position(54, 36);
		
		SceneAOIGrid srcAoiGrid = layer.getAOIGrid(src.getX(), src.getY());
		SceneAOIGrid dstAoiGrid = layer.getAOIGrid(dst.getX(), dst.getY());
		List<SceneAOIPiece> pieceList = layer.getJoinPieceSet(srcAoiGrid, dstAoiGrid);
		if (pieceList != null) {
			for (SceneAOIPiece piece : pieceList) {
				System.out.print("(" + piece.getRow() + "," + piece.getColumn()
						+ ") ");
			}
		}
		
		System.out.println();
		pieceList = layer.getRemovePieceSet(srcAoiGrid, dstAoiGrid);
		if (pieceList != null) {
			for (SceneAOIPiece piece : pieceList) {
				System.out.print("(" + piece.getRow() + "," + piece.getColumn()
						+ ") ");
			}
		}
	}
}
