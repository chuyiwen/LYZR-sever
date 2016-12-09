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
package newbee.morningGlory.checker.refObjectChecker.scene;

import java.util.Collection;
import java.util.List;

import newbee.morningGlory.checker.BaseRefChecker;
import sophia.foundation.util.Position;
import sophia.game.GameRoot;
import sophia.game.ref.GameRefObject;
import sophia.mmorpg.base.scene.GameSceneHelper;
import sophia.mmorpg.base.scene.grid.SceneGrid;
import sophia.mmorpg.base.scene.grid.SceneTerrainGridContainer;
import sophia.mmorpg.base.scene.ref.SceneMonsterRefData;
import sophia.mmorpg.base.scene.ref.SceneNpcRefData;
import sophia.mmorpg.base.scene.ref.SceneRef;
import sophia.mmorpg.base.scene.ref.region.AbstractSceneRegion;
import sophia.mmorpg.base.scene.ref.region.SceneBirthRegion;
import sophia.mmorpg.base.scene.ref.region.SceneReviveRegion;
import sophia.mmorpg.base.scene.ref.region.SceneSafeRegion;
import sophia.mmorpg.base.scene.ref.region.SceneTransInRegion;
import sophia.mmorpg.monster.ref.MonsterRef;
import sophia.mmorpg.npc.ref.NpcRef;
import sophia.mmorpg.pluck.PluckRef;

public class SceneRefChecker extends BaseRefChecker<SceneRef> {

	@Override
	public String getDescription() {
		return "场景";
	}

	@Override
	public void check(GameRefObject gameRefObject) {
		SceneRef ref = (SceneRef) gameRefObject;

		// birth region
		List<SceneBirthRegion> birthRegions = ref.getBirthRegions();
		for (SceneBirthRegion region : birthRegions) {
			if (!hasAtLeastAnUnblockedAOIGrid(region)) {
				error(gameRefObject, "出生区域没有一个可以站的中心点。");
			}
		}

		// revive region
		List<SceneReviveRegion> reviveRegions = ref.getReviveRegions();
		for (SceneReviveRegion region : reviveRegions) {
			if (!hasAtLeastAnUnblockedAOIGrid(region)) {
				error(gameRefObject, "复活区域没有一个可以站的中心点。");
			}
		}

		// safe region
		List<SceneSafeRegion> safeRegions = ref.getSafeRegions();
		for (SceneSafeRegion region : safeRegions) {
			if (!hasAtLeastAnUnblockedAOIGrid(region)) {
				error(gameRefObject, "安全区没有一个可以站的中心点。");
			}
		}

		// there must have a transInRegion
		List<SceneTransInRegion> transInRegions = ref.getTransInRegions();
		if (transInRegions.size() == 0) {
			error(gameRefObject, "没有跳入点");
		}
		for (SceneTransInRegion region : transInRegions) {
			if (!hasAtLeastAnUnblockedAOIGrid(region)) {
				error(gameRefObject, "跳入区没有一个可以站的中心点。");
			}
		}

		// transOutRegion can be null, e.g. game instance's scene, so we not
		// check here

		// npc
		List<SceneNpcRefData> npcRefDatas = ref.getNpcRefDatas();
		for (SceneNpcRefData npc : npcRefDatas) {
			String npcRefId = npc.getNpcRefId();
			int x = npc.getX();
			int y = npc.getY();
			Position pos = new Position(x, y);
			if (npcRefId.startsWith("npc_") && !npcRefId.startsWith("npc_collect_")) { // npc
				// check there exists the npc data
				NpcRef npcRef = (NpcRef) GameRoot.getGameRefObjectManager().getManagedObject(npcRefId);
				if (npcRef == null) {
					error(gameRefObject, "不存在的NPC数据。 NpcRef id " + npcRefId);
				}

				// check centerPosition is not blocked.
				Position centerPosition = GameSceneHelper.getCenterPosition(pos);
				boolean blocked = ref.getTerrainLayer().getSceneGrid(centerPosition.getY(), centerPosition.getX()).isBlocked();
				if (blocked) {
					error(gameRefObject, "NPC " + npcRefId + " 当前位置 " + pos + " 对应的的中心点为 " + centerPosition + " 是阻挡点， 所在场景 " + ref.getId());
				}
			} else if (npcRefId.startsWith("npc_collect_") && !npcRefId.startsWith("npc_")) {
				// check there exists the pluck data
				PluckRef pluckRef = (PluckRef) GameRoot.getGameRefObjectManager().getManagedObject(npcRefId);
				if (pluckRef == null) {
					error(gameRefObject, "不存在的采集物数据。 id " + npcRefId);
				}

			}

		}

		// monster
		List<SceneMonsterRefData> monsterRefDatas = ref.getMonsterRefDatas();
		for (SceneMonsterRefData monster : monsterRefDatas) {
			String monsterRefId = monster.getMonsterRefId();
			// check there exists the monster data
			MonsterRef monsterRef = (MonsterRef) GameRoot.getGameRefObjectManager().getManagedObject(monsterRefId);
			if (monsterRef == null) {
				error(gameRefObject, "不存在的怪物数据。 monsterRefId " + monsterRefId);
			}

			// check monster has a place to born
			if (!hasAtLeastAnUnblockedAOIGrid(monster.getArea())) {
				error(gameRefObject, "怪物没有合法的出生点。 monsterRefId " + monsterRefId);
			}
		}

	}

	private boolean hasAtLeastAnUnblockedAOIGrid(AbstractSceneRegion region) {
		Collection<SceneGrid> grids = region.getRegion().getAllGrids();
		for (SceneGrid grid : grids) {
			Position centerPosition = GameSceneHelper.getCenterPosition(new Position(grid.getColumn(), grid.getRow()));
			for (SceneGrid g : grids) {
				if (centerPosition.getX() == g.getColumn() && centerPosition.getY() == g.getRow() && g.isMovable()) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean hasAtLeastAnUnblockedAOIGrid(SceneTerrainGridContainer region) {
		Collection<SceneGrid> grids = region.getAllGrids();
		for (SceneGrid grid : grids) {
			Position centerPosition = GameSceneHelper.getCenterPosition(new Position(grid.getColumn(), grid.getRow()));
			for (SceneGrid g : grids) {
				if (centerPosition.getX() == g.getColumn() && centerPosition.getY() == g.getRow() && g.isMovable()) {
					return true;
				}
			}
		}
		return false;
	}

}
