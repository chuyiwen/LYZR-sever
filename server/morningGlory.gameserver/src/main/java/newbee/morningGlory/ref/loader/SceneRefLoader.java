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
package newbee.morningGlory.ref.loader;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import newbee.morningGlory.ref.RefKey;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import sophia.foundation.util.Position;
import sophia.mmorpg.base.scene.grid.SceneTerrainGridSquare;
import sophia.mmorpg.base.scene.grid.SceneTerrainLayer;
import sophia.mmorpg.base.scene.ref.SceneMonsterRefData;
import sophia.mmorpg.base.scene.ref.SceneNpcRefData;
import sophia.mmorpg.base.scene.ref.SceneRef;
import sophia.mmorpg.base.scene.ref.region.SceneBirthRegion;
import sophia.mmorpg.base.scene.ref.region.SceneReviveRegion;
import sophia.mmorpg.base.scene.ref.region.SceneSafeRegion;
import sophia.mmorpg.base.scene.ref.region.SceneTransInRegion;
import sophia.mmorpg.base.scene.ref.region.SceneTransOutRegion;
import sophia.mmorpg.monsterRefresh.BirthRegionType;
import sophia.mmorpg.monsterRefresh.MonsterGroupRefData;
import sophia.mmorpg.monsterRefresh.Rectangle;
import sophia.mmorpg.monsterRefresh.RefreshConditionType;
import sophia.mmorpg.monsterRefresh.RefreshConditionTypeData;
import sophia.mmorpg.monsterRefresh.RefreshMonsterClosureRef;
import sophia.mmorpg.monsterRefresh.RefreshMonsterRefData;

import com.google.common.base.Preconditions;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class SceneRefLoader extends AbstractGameRefObjectLoader<SceneRef> {
	private static final Logger logger = Logger.getLogger(SceneRefLoader.class);

	public SceneRefLoader() {
		super(RefKey.scene);
	}

	@Override
	protected SceneRef create() {
		return new SceneRef();
	}

	protected void fillNonPropertyDictionary(SceneRef ref, JsonObject refData) {
		if (logger.isDebugEnabled()) {
			logger.debug("loading SceneRef refId: " + ref.getId());
		}
		// layer
		int mapId = refData.get("mapId").getAsInt();

		SceneTerrainLayer layer = MapLoader.load(mapId);
		ref.setTerrainLayer(layer);

		// scene type
		int type = refData.get("kind").getAsInt();
		ref.setType(type);
		
		//scene open fightpower
		int fightPower = refData.get("powerlimit").getAsInt();
		ref.setFightPower(fightPower);

		// SceneBirthRegion
		JsonElement birthRegionData = refData.get("birthRegion");
		// only one scene has birthRegionData
		if (birthRegionData.isJsonArray()) {
			List<SceneBirthRegion> birthRegion = new ArrayList<SceneBirthRegion>();
			Iterator<JsonElement> birthIter = birthRegionData.getAsJsonArray().iterator();
			while (birthIter.hasNext()) {
				JsonObject elem = birthIter.next().getAsJsonObject();
				int id = elem.get("birthRegionId").getAsInt();
				int x = elem.get("x").getAsInt();
				int y = elem.get("y").getAsInt();
				int height = elem.get("height").getAsInt();
				int width = elem.get("width").getAsInt();
				birthRegion.add(new SceneBirthRegion(id, new SceneTerrainGridSquare(layer, x, y, width, height)));
			}
			ref.setBirthRegion(birthRegion);
		}

		// SceneReviveRegion
		JsonElement reviveRegionData = refData.get("reviveRegion");
		if (reviveRegionData.isJsonArray()) {
			List<SceneReviveRegion> reviveRegion = new ArrayList<SceneReviveRegion>();
			Iterator<JsonElement> reviveIter = reviveRegionData.getAsJsonArray().iterator();
			while (reviveIter.hasNext()) {
				JsonObject elem = reviveIter.next().getAsJsonObject();
				byte camp = elem.get("camp").getAsByte();
				int x = elem.get("x").getAsInt();
				int y = elem.get("y").getAsInt();
				int height = elem.get("height").getAsInt();
				int width = elem.get("width").getAsInt();
				int reviveRegionId = elem.get("reviveRegionId").getAsInt();
				reviveRegion.add(new SceneReviveRegion(reviveRegionId, new SceneTerrainGridSquare(layer, x, y, width, height), camp));
			}
			ref.setReviveRegions(reviveRegion);
		}

		// SceneSafeRegion
		JsonElement safeRegionData = refData.get("safeRegion");
		if (safeRegionData.isJsonArray()) {
			List<SceneSafeRegion> safeRegion = new ArrayList<SceneSafeRegion>();
			Iterator<JsonElement> safeIter = safeRegionData.getAsJsonArray().iterator();
			while (safeIter.hasNext()) {
				JsonObject elem = safeIter.next().getAsJsonObject();
				int x = (int) elem.get("x").getAsInt();
				int y = (int) elem.get("y").getAsInt();
				int height = (int) elem.get("height").getAsInt();
				int width = (int) elem.get("width").getAsInt();
				int safeRegionId = (int) elem.get("safeRegionId").getAsInt();
				safeRegion.add(new SceneSafeRegion(safeRegionId, new SceneTerrainGridSquare(layer, x, y, width, height)));
			}
			ref.setSafeRegions(safeRegion);
		}

		// SceneTransInRegion
		JsonElement transferInData = refData.get("tranferIn");
		if (transferInData.isJsonArray()) {
			List<SceneTransInRegion> transferInRegion = new ArrayList<SceneTransInRegion>();
			Iterator<JsonElement> transferInIter = transferInData.getAsJsonArray().iterator();
			while (transferInIter.hasNext()) {
				JsonObject elem = transferInIter.next().getAsJsonObject();
				int x = elem.get("x").getAsInt();
				int y = elem.get("y").getAsInt();
				int height = elem.get("height").getAsInt();
				int width = elem.get("width").getAsInt();
				int transferInId = elem.get("tranferInId").getAsInt();
				transferInRegion.add(new SceneTransInRegion(transferInId, new SceneTerrainGridSquare(layer, x, y, width, height)));
			}
			ref.setTransInRegions(transferInRegion);
		}

		// SceneTransOutRegion
		JsonElement transferOutData = refData.get("tranferOut");
		if (transferOutData.isJsonArray()) {
			List<SceneTransOutRegion> transferOutRegion = new ArrayList<SceneTransOutRegion>();
			Iterator<JsonElement> transferOutIter = transferOutData.getAsJsonArray().iterator();
			while (transferOutIter.hasNext()) {
				JsonObject elem = transferOutIter.next().getAsJsonObject();
				int x = elem.get("x").getAsInt();
				int y = elem.get("y").getAsInt();
				int height = elem.get("height").getAsInt();
				int width = elem.get("width").getAsInt();
				int transferOutId = elem.get("tranferOutId").getAsInt();
				String targetSceneRefId = elem.get("targetSceneRefId").getAsString();
				int targetRegionId = elem.get("targetRegionId").getAsInt();
				transferOutRegion.add(new SceneTransOutRegion(transferOutId, new SceneTerrainGridSquare(layer, x, y, width, height), targetSceneRefId, targetRegionId));
			}
			ref.setTransOutRegions(transferOutRegion);
		}

		// SceneNpcRefData
		JsonElement npcData = refData.get("npc");
		if (npcData.isJsonArray()) {
			List<SceneNpcRefData> npcRefData = new ArrayList<SceneNpcRefData>();
			Iterator<JsonElement> npcIter = npcData.getAsJsonArray().iterator();
			while (npcIter.hasNext()) {
				JsonObject elem = npcIter.next().getAsJsonObject();
				int x = elem.get("x").getAsInt();
				int y = elem.get("y").getAsInt();
				String npcRefId = elem.get("npcRefId").getAsString();
				npcRefData.add(new SceneNpcRefData(npcRefId, x, y));
			}
			ref.setNpcRefDatas(npcRefData);
		}

		// SceneMonsterRefData
		JsonElement monsterData = refData.get("monster");
		if (monsterData.isJsonArray()) {
			List<SceneMonsterRefData> monsterRefData = new ArrayList<SceneMonsterRefData>();
			Iterator<JsonElement> monsterIter = monsterData.getAsJsonArray().iterator();
			while (monsterIter.hasNext()) {
				JsonObject elem = monsterIter.next().getAsJsonObject();
				int x = elem.get("x").getAsInt();
				int y = elem.get("y").getAsInt();
				int height = elem.get("height").getAsInt();
				int width = elem.get("width").getAsInt();
				String monsterRefId = elem.get("monsterRefId").getAsString();
				int monsterCount = elem.get("monsterRefreshCount").getAsInt();
				int batchId = elem.get("batchid").getAsInt();
				int batchTime = elem.get("batchTime").getAsInt();
				int refreshTime = elem.get("refleshTime").getAsInt();
				int refreshType = elem.get("refreshType").getAsInt();
				SceneTerrainGridSquare sceneTerrainGridSquare = new SceneTerrainGridSquare(layer, x, y, width, height);
				Preconditions.checkArgument(sceneTerrainGridSquare.getUnblockedGrids().size() != 0, monsterRefId + "没有非阻挡的中心点。 x:" + x + " y:" + y + " width: " + width + " height: "
						+ height + " sceneRefId: " + ref.getId());
				SceneMonsterRefData sceneMonsterRefdata = new SceneMonsterRefData(monsterRefId, sceneTerrainGridSquare, monsterCount, batchId, batchTime, refreshTime, refreshType);
				if (elem.has("timingRefresh") && elem.get("timingRefresh") != null) {
					String timingRefresh = elem.get("timingRefresh").getAsString();
					sceneMonsterRefdata.setTimingRefresh(timingRefresh);
				}
				monsterRefData.add(sceneMonsterRefdata);
			}
			ref.setMonsterRefDatas(monsterRefData);
		}

		// refreshMonsterData
		JsonElement refreshMonsterData = refData.get("refreshMonster");
		if (refreshMonsterData != null && !refreshMonsterData.isJsonNull() && refreshMonsterData.isJsonArray()) {
			List<RefreshMonsterRefData> refreshMonsterRefDatas = new ArrayList<>();
			Iterator<JsonElement> refreshMonsterIter = refreshMonsterData.getAsJsonArray().iterator();
			while (refreshMonsterIter.hasNext()) {
				RefreshMonsterRefData refreshMonsterRefData = getRefreshMonsterData(refreshMonsterIter, layer);

				refreshMonsterRefDatas.add(refreshMonsterRefData);
			}
			ref.setRefreshMonsterRefDatas(refreshMonsterRefDatas);
		}
	}

	private RefreshMonsterRefData getRefreshMonsterData(Iterator<JsonElement> refreshMonsterIter, SceneTerrainLayer layer) {
		RefreshMonsterRefData refreshMonsterRefData = new RefreshMonsterRefData();
		JsonObject elem = refreshMonsterIter.next().getAsJsonObject();
		String monsterRefId = elem.get("monsterRefId").getAsString();
		int refreshCount = elem.get("refreshCount").getAsInt();
		int delaySec = elem.get("refreshDelayTime").getAsInt();
		;
		refreshMonsterRefData.setMonsterGroup(getMonsterGroup(monsterRefId, refreshCount, delaySec));

		JsonObject monsterBirthRegionData = elem.get("birthRegion").getAsJsonObject();
		if (logger.isDebugEnabled()) {
			logger.debug("birthRegionData: " + monsterBirthRegionData);
		}

		// birthRegionData
		byte birthRegionType = elem.get("birthRegionType").getAsByte();
		Object dataObject = getBirthRegionDataObject(birthRegionType, monsterBirthRegionData, layer);
		refreshMonsterRefData.addRegionDataObject(dataObject);
		refreshMonsterRefData.setBirthRegionType(birthRegionType);

		// refreshTypeData
		byte refreshType = elem.get("refreshType").getAsByte();
		String refreshTypeData = elem.get("refreshTypeData").getAsString();
		RefreshConditionTypeData refreshConditionTypeData = getRefreshConditionTypeData(refreshType, refreshTypeData);
		String refreshTimerRange = elem.get("refreshTimeRange").getAsString();
		if (!StringUtils.isEmpty(refreshTimerRange)) {
			setRefreshMonsterTimeRange(refreshTimerRange, refreshConditionTypeData);
		}
		refreshConditionTypeData.setRefreshMonsterRefId(refreshMonsterRefData.getMonsterGroup().getMonsterRefId());
		refreshMonsterRefData.setRefreshConditionTypeData(refreshConditionTypeData);

		// refreshMonsterClosure
		String refreshMonsterClosure = elem.get("refreshMonsterClosure").getAsString();
		refreshMonsterRefData.addComponentRef(new RefreshMonsterClosureRef(refreshMonsterClosure));
		return refreshMonsterRefData;
	}

	private void setRefreshMonsterTimeRange(String refreshTimerRange, RefreshConditionTypeData refreshConditionTypeData) {
		String[] timeRangeList = refreshTimerRange.split("\\|");
		refreshConditionTypeData.setRefreshMonsterStartTime(timeRangeList[0]);
		refreshConditionTypeData.setRefreshMonsterLastTime(Integer.parseInt(timeRangeList[1]));
	}

	private RefreshConditionTypeData getRefreshConditionTypeData(byte refreshConditionType, String refreshTypeData) {
		RefreshConditionTypeData refreshConditionTypeData = new RefreshConditionTypeData();
		refreshConditionTypeData.setRefreshConditionType(refreshConditionType);
		if (RefreshConditionType.OnSceneCreated_Type == refreshConditionType) {

		} else if (RefreshConditionType.OnPreMonster_Arise_Type == refreshConditionType || RefreshConditionType.OnPreMonster_Dead_Type == refreshConditionType) {
			String conditionMonsterRefId = getMonsterRefId(refreshTypeData);
			int conditionNumber = getMonsterNum(refreshTypeData);
			refreshConditionTypeData.setConditionNumber(conditionNumber);
			refreshConditionTypeData.setConditionMonsterRefId(conditionMonsterRefId);
		} else if (RefreshConditionType.InSceneTimeRange_Type == refreshConditionType) {

		}
		return refreshConditionTypeData;
	}

	private MonsterGroupRefData getMonsterGroup(String monsterRefId, int number, int delaySec) {
		MonsterGroupRefData monsterGroup = new MonsterGroupRefData(monsterRefId, number, delaySec);
		return monsterGroup;
	}

	private Object getBirthRegionDataObject(byte birthRegionType, JsonObject birthRegionData, SceneTerrainLayer layer) {
		Preconditions.checkNotNull(birthRegionData);
		switch (birthRegionType) {
		case BirthRegionType.Position_Type:
			JsonObject positionData = birthRegionData.get("position").getAsJsonObject();
			Position position = getPosition(positionData);
			return position;
		case BirthRegionType.Rectangle_Type:
			JsonObject rectangleData = birthRegionData.get("rectangle").getAsJsonObject();
			Rectangle rectangle = getRectangle(rectangleData);
			SceneTerrainGridSquare sceneTerrainGridSquare = new SceneTerrainGridSquare(layer, rectangle.getPosition().getX(), rectangle.getPosition().getY(), rectangle.getWidth(),
					rectangle.getHeight());
			Preconditions.checkArgument(sceneTerrainGridSquare.getUnblockedGrids().size() != 0, "加载刷怪数据出错");
			rectangle.setArea(sceneTerrainGridSquare);
			return rectangle;
		case BirthRegionType.Polygon_Type:
			JsonArray rectangleJsonArray = birthRegionData.getAsJsonArray("rectangles");
			List<Rectangle> rectangleList = getRectangleList(rectangleJsonArray);
			for (Rectangle r : rectangleList) {
				SceneTerrainGridSquare sceneGridSquare = new SceneTerrainGridSquare(layer, r.getPosition().getX(), r.getPosition().getY(), r.getWidth(), r.getHeight());
				Preconditions.checkArgument(sceneGridSquare.getUnblockedGrids().size() != 0, "加载刷怪数据出错");
				r.setArea(sceneGridSquare);
			}
			return rectangleList;
		default:
			throw new NullPointerException();
		}

	}

	// ======================================================================================================================================================

	private List<Rectangle> getRectangleList(JsonArray rectangleJsonArray) {
		List<Rectangle> rectangleList = new ArrayList<>();
		Preconditions.checkNotNull(rectangleJsonArray);
		for (int i = 0; i < rectangleJsonArray.size(); i++) {
			JsonObject rectangleData = rectangleJsonArray.get(i).getAsJsonObject();
			Rectangle rectangle = getRectangle(rectangleData);
			rectangleList.add(rectangle);
		}
		return rectangleList;
	}

	private Rectangle getRectangle(JsonObject rectangleData) {
		Rectangle rectangle = new Rectangle();
		Preconditions.checkNotNull(rectangleData);
		Position position = getPosition(rectangleData);
		rectangle.setPosition(position);
		int width = rectangleData.get("width").getAsInt();
		int height = rectangleData.get("height").getAsInt();
		rectangle.setWidth(width);
		rectangle.setHeight(height);
		return rectangle;
	}

	private Position getPosition(JsonObject positionData) {
		Position position = new Position();
		Preconditions.checkNotNull(positionData);
		int x = positionData.get("x").getAsInt();
		int y = positionData.get("y").getAsInt();
		position.setPosition(x, y);
		return position;
	}

	private String getMonsterRefId(String data) {
		String monsterRefId = data.substring(0, data.indexOf("="));
		return monsterRefId;
	}

	private int getMonsterNum(String data) {
		int number = 0;
		if (data.contains("|")) {
			number = Integer.valueOf(data.substring(data.indexOf("=") + 1, data.indexOf("|")));
		} else {
			number = Integer.valueOf(data.substring(data.indexOf("=") + 1, data.length()));
		}
		return number;
	}

}