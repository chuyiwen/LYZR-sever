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

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.List;
import java.util.Random;

public class SceneTerrainGridSquare implements SceneTerrainGridContainer {
	private static final Random random = new Random(System.currentTimeMillis());
	private List<SceneGrid> allGrids;
	private List<SceneGrid> unblockedGrids;
	private SceneGrid leftUp;
	private SceneGrid rightDown;

	public SceneTerrainGridSquare(SceneTerrainLayer layer, SceneGrid leftUp, SceneGrid rightDown) {
		super();
		checkNotNull(layer, "SceneTerrainLayer layer not initiliazed");

		init(layer, leftUp, rightDown);
	}

	private void init(SceneTerrainLayer layer, SceneGrid leftUp, SceneGrid rightDown) {
		this.leftUp = leftUp;
		this.rightDown = rightDown;
		allGrids = layer.getInRangeGrids(leftUp, rightDown);
		unblockedGrids = layer.getInRangeGrids(leftUp, rightDown, false);
	}

	// width relative to x, height relative to y
	public SceneTerrainGridSquare(SceneTerrainLayer layer, int x, int y, int width, int height) {
		checkNotNull(layer, "SceneTerrainLayer layer not initiliazed");
		SceneGrid leftUp = layer.getSceneGrid(y, x);
		int _x = x + width;
		int _y = y + height;
		while (_x >= layer.getnColumn()) {
			_x--;
		}
		while (_y >= layer.getnRow()) {
			_y--;
		}
		SceneGrid rightDown = layer.getSceneGrid(_y, _x);

		init(layer, leftUp, rightDown);
	}

	@Override
	public int numberOfGrids() {
		return allGrids.size();
	}

	@Override
	public boolean contains(SceneGrid sceneGrid) {
		return allGrids.contains(sceneGrid);
	}

	@Override
	public Collection<SceneGrid> getAllGrids() {
		return allGrids;
	}

	@Override
	public Collection<SceneGrid> getUnblockedGrids() {
		return unblockedGrids;
	}

	@Override
	public SceneGrid getRandomGrid() {
		int index = random.nextInt(allGrids.size());
		return allGrids.get(index);
	}

	@Override
	public SceneGrid getRandomUnblockedGrid() {
		int index = random.nextInt(unblockedGrids.size());
		return unblockedGrids.get(index);
	}

	public SceneGrid getLeftUp() {
		return leftUp;
	}

	public void setLeftUp(SceneGrid leftUp) {
		this.leftUp = leftUp;
	}

	public SceneGrid getRightDown() {
		return rightDown;
	}

	public void setRightDown(SceneGrid rightDown) {
		this.rightDown = rightDown;
	}

	@Override
	public SceneGrid getUnblockedGrid(int index) {
		return unblockedGrids.get(index);
	}
}
