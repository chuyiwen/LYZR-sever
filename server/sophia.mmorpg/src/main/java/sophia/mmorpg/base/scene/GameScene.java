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
package sophia.mmorpg.base.scene;

import org.apache.commons.lang3.StringUtils;

import sophia.game.component.GameObject;
import sophia.mmorpg.base.scene.aoi.SceneAOIComponent;
import sophia.mmorpg.base.scene.aoi.SceneAOILayer;
import sophia.mmorpg.base.scene.grid.SceneTerrainLayer;
import sophia.mmorpg.base.scene.mgr.LootMgrComponent;
import sophia.mmorpg.base.scene.mgr.MonsterMgrComponent;
import sophia.mmorpg.base.scene.mgr.MonsterRefreshMgrComponent;
import sophia.mmorpg.base.scene.mgr.NpcMgrComponent;
import sophia.mmorpg.base.scene.mgr.OtherSpriteMgrComponent;
import sophia.mmorpg.base.scene.mgr.PlayerMgrComponent;
import sophia.mmorpg.base.scene.mgr.PluckMgrComponent;
import sophia.mmorpg.base.scene.ref.AbstractGameSceneRef;
import sophia.mmorpg.base.scene.ref.region.SceneBirthRegion;
import sophia.mmorpg.base.scene.ref.region.SceneReviveRegion;
import sophia.mmorpg.base.scene.ref.region.SceneSafeRegion;
import sophia.mmorpg.base.scene.ref.region.SceneTransInRegion;
import sophia.mmorpg.base.scene.ref.region.SceneTransOutRegion;
import sophia.mmorpg.base.scene.tick.SceneTickComponent;


public final class GameScene extends GameObject {
	protected AbstractGameSceneRef ref;
	
	protected SceneBirthRegion birthRegion;
	protected SceneReviveRegion reviveRegion;
	protected SceneSafeRegion safeRegion;
	protected SceneTransInRegion transInRegion;
	protected SceneTransOutRegion transOutRegion;
	
	protected SceneTerrainLayer terrainLayer;
	protected SceneAOILayer aoiLayer;
	protected SceneAOIComponent aoiComponent;
	
	private SceneTickComponent sceneTickComponent;
	private LootMgrComponent lootMgrComponent;
	private PluckMgrComponent pluckMgrComponent;
	private MonsterMgrComponent monsterMgrComponent;
	private MonsterRefreshMgrComponent refreshMonsterMgrComponent;
	private NpcMgrComponent npcMgrComponent;
	private PlayerMgrComponent playerMgrComponent;
	private OtherSpriteMgrComponent otherSpriteMgrComponent;
	
	public GameScene() {
		sceneTickComponent = (SceneTickComponent) createComponent(SceneTickComponent.class);
		aoiComponent = (SceneAOIComponent) createComponent(SceneAOIComponent.class);
		lootMgrComponent = (LootMgrComponent) createComponent(LootMgrComponent.class);
		pluckMgrComponent = (PluckMgrComponent) createComponent(PluckMgrComponent.class);
		npcMgrComponent = (NpcMgrComponent) createComponent(NpcMgrComponent.class);
		
		monsterMgrComponent = (MonsterMgrComponent) createComponent(MonsterMgrComponent.class);
		refreshMonsterMgrComponent = (MonsterRefreshMgrComponent) createComponent(MonsterRefreshMgrComponent.class);
		
		playerMgrComponent = (PlayerMgrComponent) createComponent(PlayerMgrComponent.class);
		otherSpriteMgrComponent = (OtherSpriteMgrComponent) createComponent(OtherSpriteMgrComponent.class);
	}
	
	public void clear(){
		aoiComponent.destroy();
		lootMgrComponent.destroy();
		pluckMgrComponent.destroy();
		npcMgrComponent.destroy();
		monsterMgrComponent.destroy();
		refreshMonsterMgrComponent.destroy();
		playerMgrComponent.destroy();
		otherSpriteMgrComponent.destroy();
	}

	public final AbstractGameSceneRef getRef() {
		return ref;
	}

	public final void setRef(AbstractGameSceneRef ref) {
		this.ref = ref;
	}
	
	public final SceneBirthRegion getBirthRegion() {
		return birthRegion;
	}

	public final void setBirthRegion(SceneBirthRegion birthRegion) {
		this.birthRegion = birthRegion;
	}

	public final SceneReviveRegion getReviveRegion() {
		return reviveRegion;
	}

	public final void setReviveRegion(SceneReviveRegion reviveRegion) {
		this.reviveRegion = reviveRegion;
	}

	public final SceneSafeRegion getSafeRegion() {
		return safeRegion;
	}

	public final void setSafeRegion(SceneSafeRegion safeRegion) {
		this.safeRegion = safeRegion;
	}

	public final SceneTransInRegion getTransInRegion() {
		return transInRegion;
	}

	public final void setTransInRegion(SceneTransInRegion transInRegion) {
		this.transInRegion = transInRegion;
	}

	public final SceneTransOutRegion getTransOutRegion() {
		return transOutRegion;
	}

	public final void setTransOutRegion(SceneTransOutRegion transOutRegion) {
		this.transOutRegion = transOutRegion;
	}

	public final SceneTerrainLayer getTerrainLayer() {
		return terrainLayer;
	}

	public final void setTerrainLayer(SceneTerrainLayer terrainLayer) {
		this.terrainLayer = terrainLayer;
	}

	public final SceneAOILayer getAoiLayer() {
		return aoiLayer;
	}

	public final void setAoiLayer(SceneAOILayer aoiLayer) {
		this.aoiLayer = aoiLayer;
	}
	
	public final SceneTickComponent getSceneTickComponent() {
		return sceneTickComponent;
	}
	
	public final SceneAOIComponent getAoiComponent() {
		return aoiComponent;
	}

	public LootMgrComponent getLootMgrComponent() {
		return lootMgrComponent;
	}

	public void setLootMgrComponent(LootMgrComponent lootMgrComponent) {
		this.lootMgrComponent = lootMgrComponent;
	}

	public PluckMgrComponent getPluckMgrComponent() {
		return pluckMgrComponent;
	}
	

	public void setPluckMgrComponent(PluckMgrComponent pluckMgrComponent) {
		this.pluckMgrComponent = pluckMgrComponent;
	}
	
	public NpcMgrComponent getNpcMgrComponent() {
		return npcMgrComponent;
	} 
	
	public void setNpcMgrComponent(NpcMgrComponent npcMgrComponent) {
		this.npcMgrComponent = npcMgrComponent;
	}

	public MonsterMgrComponent getMonsterMgrComponent() {
		return monsterMgrComponent;
	}

	public void setMonsterMgrComponent(MonsterMgrComponent monsterMgrComponent) {
		this.monsterMgrComponent = monsterMgrComponent;
	}

	public MonsterRefreshMgrComponent getRefreshMonsterMgrComponent() {
		return refreshMonsterMgrComponent;
	}

	public void setRefreshMonsterMgrComponent(MonsterRefreshMgrComponent refreshMonsterMgrComponent) {
		this.refreshMonsterMgrComponent = refreshMonsterMgrComponent;
	}
	
	public PlayerMgrComponent getPlayerMgrComponent() {
		return playerMgrComponent;
	}

	public void setPlayerMgrComponent(PlayerMgrComponent playerMgrComponent) {
		this.playerMgrComponent = playerMgrComponent;
	}
	
	public OtherSpriteMgrComponent getOtherSpriteMgrComponent() {
		return otherSpriteMgrComponent;
	}

	public void setOtherSpriteMgrComponent(OtherSpriteMgrComponent otherSpriteMgrComponent) {
		this.otherSpriteMgrComponent = otherSpriteMgrComponent;
	}
	
	@Override
	public int hashCode() {
		return getId().hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GameScene other = (GameScene) obj;
		if (!StringUtils.equals(other.getId(), getId()))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return "GameScene [refId=" + ref.getId() + ", getId()=" + getId() + "]";
	}
}
