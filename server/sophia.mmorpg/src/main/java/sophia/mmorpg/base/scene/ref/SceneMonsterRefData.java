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
package sophia.mmorpg.base.scene.ref;

import sophia.mmorpg.base.scene.grid.SceneTerrainGridContainer;

/**
 * 场景-怪物引用的数据
 */
public final class SceneMonsterRefData {
	private String monsterRefId;
	private SceneTerrainGridContainer area;
	private int monsterCount;
	
	// refresh info
	private int batchId;
	private int batchTime;
	private int refreshTime;
	private int refreshType;
	private String timingRefresh;
	
	public SceneMonsterRefData(String monsterRefId,
			SceneTerrainGridContainer area, int monsterCount, int batchId,
			int batchTime, int refreshTime, int refreshType) {
		super();
		this.monsterRefId = monsterRefId;
		this.area = area;
		this.monsterCount = monsterCount;
		this.batchId = batchId;
		this.batchTime = batchTime;
		this.refreshTime = refreshTime;
		this.refreshType = refreshType;
	}

	public String getMonsterRefId() {
		return monsterRefId;
	}

	public SceneTerrainGridContainer getArea() {
		return area;
	}

	public int getMonsterCount() {
		return monsterCount;
	}

	public int getBatchId() {
		return batchId;
	}

	public int getBatchTime() {
		return batchTime;
	}

	public int getRefreshTime() {
		return refreshTime;
	}

	public int getRefreshType() {
		return refreshType;
	}

	public String getTimingRefresh() {
		return timingRefresh;
	}

	public void setTimingRefresh(String timingRefresh) {
		this.timingRefresh = timingRefresh;
	}
	
}
