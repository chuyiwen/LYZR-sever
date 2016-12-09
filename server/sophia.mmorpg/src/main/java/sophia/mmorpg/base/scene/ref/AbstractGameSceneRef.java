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

import java.util.ArrayList;
import java.util.List;

import sophia.game.ref.AbstractGameRefObjectBase;
import sophia.mmorpg.base.scene.grid.SceneGrid;
import sophia.mmorpg.base.scene.grid.SceneTerrainLayer;
import sophia.mmorpg.base.scene.ref.region.SceneBirthRegion;
import sophia.mmorpg.base.scene.ref.region.SceneReviveRegion;
import sophia.mmorpg.base.scene.ref.region.SceneSafeRegion;
import sophia.mmorpg.base.scene.ref.region.SceneTransInRegion;
import sophia.mmorpg.base.scene.ref.region.SceneTransOutRegion;
import sophia.mmorpg.monsterRefresh.RefreshMonsterRefData;
import sophia.mmorpg.utils.SFRandomUtils;

/**
 * 场景-引用数据
 */
public abstract class AbstractGameSceneRef extends AbstractGameRefObjectBase {
	private static final long serialVersionUID = 1315808017827900782L;

	protected String refId;

	protected int type;

	protected SceneTerrainLayer terrainLayer;
	
	protected int fightPower;

	protected List<SceneBirthRegion> birthRegions = new ArrayList<SceneBirthRegion>();
	protected List<SceneReviveRegion> reviveRegions = new ArrayList<SceneReviveRegion>();
	protected List<SceneSafeRegion> safeRegions = new ArrayList<SceneSafeRegion>();
	protected List<SceneTransInRegion> transInRegions = new ArrayList<SceneTransInRegion>();
	protected List<SceneTransOutRegion> transOutRegions = new ArrayList<SceneTransOutRegion>();

	protected List<SceneNpcRefData> npcRefDatas = new ArrayList<SceneNpcRefData>();
	protected List<SceneMonsterRefData> monsterRefDatas = new ArrayList<SceneMonsterRefData>();

	protected List<RefreshMonsterRefData> refreshMonsterRefDatas;

	protected AbstractGameSceneRef() {

	}

	public SceneTransInRegion getRadomSceneTransInRegion(){
		return transInRegions.get(SFRandomUtils.random(transInRegions.size()) - 1);
	}
	public SceneTransInRegion getSceneTransInRegionByTransInId(int transInId){
		for (int i = 0; i < transInRegions.size(); i++) {
			SceneTransInRegion transInRegion = transInRegions.get(i);
			if(transInRegion.getId() == transInId){
				return transInRegion;
			}
		}
		return this.getRadomSceneTransInRegion();
	}
	
	public SceneGrid getRandomBirthGrid() {
		return getBirthRegions().get(0).getRegion().getRandomUnblockedGrid();
	}
	
	public SceneGrid getRandomReviveGrid() {
		return getReviveRegions().get(0).getRegion().getRandomUnblockedGrid();
	}
	
	public final int getType() {
		return type;
	}

	public final void setType(int type) {
		this.type = type;
	}

	public final String getRefId() {
		return refId;
	}

	public final void setRefId(String refId) {
		this.refId = refId;
	}

	public final SceneTerrainLayer getTerrainLayer() {
		return terrainLayer;
	}

	public final void setTerrainLayer(SceneTerrainLayer terrainLayer) {
		this.terrainLayer = terrainLayer;
	}

	public final List<SceneBirthRegion> getBirthRegions() {
		return birthRegions;
	}

	public final void setBirthRegion(List<SceneBirthRegion> birthRegions) {
		this.birthRegions = birthRegions;
	}

	public final List<SceneReviveRegion> getReviveRegions() {
		return reviveRegions;
	}

	public final void setReviveRegions(List<SceneReviveRegion> reviveRegions) {
		this.reviveRegions = reviveRegions;
	}

	public final List<SceneSafeRegion> getSafeRegions() {
		return safeRegions;
	}

	public final void setSafeRegions(List<SceneSafeRegion> safeRegions) {
		this.safeRegions = safeRegions;
	}

	public final List<SceneTransInRegion> getTransInRegions() {
		return transInRegions;
	}

	public final void setTransInRegions(List<SceneTransInRegion> transInRegions) {
		this.transInRegions = transInRegions;
	}

	public final List<SceneTransOutRegion> getTransOutRegions() {
		return transOutRegions;
	}

	public final void setTransOutRegions(List<SceneTransOutRegion> transOutRegions) {
		this.transOutRegions = transOutRegions;
	}

	public final List<SceneNpcRefData> getNpcRefDatas() {
		return npcRefDatas;
	}

	public final void setNpcRefDatas(List<SceneNpcRefData> npcRefDatas) {
		this.npcRefDatas = npcRefDatas;
	}

	public final List<SceneMonsterRefData> getMonsterRefDatas() {
		return monsterRefDatas;
	}

	public final void setMonsterRefDatas(List<SceneMonsterRefData> monsterRefDatas) {
		this.monsterRefDatas = monsterRefDatas;
	}

	public List<RefreshMonsterRefData> getRefreshMonsterRefDatas() {
		return refreshMonsterRefDatas;
	}

	public void setRefreshMonsterRefDatas(List<RefreshMonsterRefData> refreshMonsterRefDatas) {
		this.refreshMonsterRefDatas = refreshMonsterRefDatas;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((refId == null) ? 0 : refId.hashCode());
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
		AbstractGameSceneRef other = (AbstractGameSceneRef) obj;
		if (refId == null) {
			if (other.refId != null)
				return false;
		} else if (!refId.equals(other.refId))
			return false;
		return true;
	}

	public final int getFightPower() {
		return fightPower;
	}

	public final void setFightPower(int fightPower) {
		this.fightPower = fightPower;
	}
}
