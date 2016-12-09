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

public final class TerrainGridGeom {
	private TerrainGridGeom() {
		
	}
	
	public static int getDistance(SceneGrid g1, SceneGrid g2) {
		return getDistance(g1.getColumn(), g1.getRow(), g2.getColumn(), g2.getRow());
	}

	public static int getDistance(int x1, int y1, int x2, int y2) {
		return (int) Math.hypot(Math.abs(x1 - x2), Math.abs(y1 - y2));
	}
}
