package sophia.mmorpg.monsterRefresh;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import sophia.foundation.core.ComponentRegistry;
import sophia.foundation.core.ComponentRegistryImpl;
import sophia.foundation.util.Position;
import sophia.mmorpg.base.scene.GameSceneHelper;
import sophia.mmorpg.base.scene.aoi.SceneAOILayer;
import sophia.mmorpg.base.scene.grid.SceneGrid;
import sophia.mmorpg.base.scene.grid.SceneTerrainGridContainer;
import sophia.mmorpg.utils.SFRandomUtils;

import com.google.common.base.Preconditions;

public class RefreshMonsterRefData {
	private final ComponentRegistry componentRegistry = new ComponentRegistryImpl();
	private MonsterGroupRefData monsterGroup;
	private RefreshConditionTypeData refreshConditionTypeData;
	private byte birthRegionType;

	public RefreshMonsterRefData() {
	}

	public void addRegionDataObject(Object dataObject) {
		componentRegistry.addComponent(dataObject);
	}

	public <T> T getRegionDataObject(Class<T> type) {
		return componentRegistry.getComponent(type);
	}

	public void addComponentRef(Object obj) {
		componentRegistry.addComponent(obj);
	}

	public <T> T getComponentRef(Class<T> type) {
		return componentRegistry.getComponent(type);
	}

	public MonsterGroupRefData getMonsterGroup() {
		return monsterGroup;
	}

	public void setMonsterGroup(MonsterGroupRefData monsterGroup) {
		this.monsterGroup = monsterGroup;
	}

	public RefreshConditionTypeData getRefreshConditionTypeData() {
		return refreshConditionTypeData;
	}

	public void setRefreshConditionTypeData(RefreshConditionTypeData refreshConditionTypeData) {
		this.refreshConditionTypeData = refreshConditionTypeData;
	}

	public void setBirthRegionType(byte birthRegionType) {
		this.birthRegionType = birthRegionType;
	}

	/**
	 * 出生区类型
	 * 
	 * @return
	 */
	public byte getBirthRegionType() {
		return birthRegionType;
	}

	/**
	 * 是否依赖其他怪物出生或死亡
	 * 
	 * @return
	 */
	public boolean isOnPreMonster() {
		if (this.refreshConditionTypeData == null) {
			return false;
		}
		if (this.refreshConditionTypeData.checkRefreshConditionType(RefreshConditionType.OnPreMonster_Dead_Type)
				|| this.refreshConditionTypeData.checkRefreshConditionType(RefreshConditionType.OnPreMonster_Arise_Type)) {
			return true;
		}
		return false;
	}

	// ============================================================================================================
	public List<Position> getPosition(int monsterNum) {
		List<Position> positionList = new ArrayList<>();
		if (BirthRegionType.Position_Type == birthRegionType) {
			Position position = getRegionDataObject(Position.class);
			for (int j = 0; j < monsterNum; j++) {
				positionList.add(GameSceneHelper.getCenterPosition(new Position(position.getX(), position.getY())));
			}
			return positionList;
		}

		if (BirthRegionType.Rectangle_Type == birthRegionType) {
			Rectangle rectangle = getRegionDataObject(Rectangle.class);
			positionList = getCenterPositionsCount(rectangle.getArea(), SceneAOILayer.AOIGRID_MULTIPLE);
			Preconditions.checkArgument(!(positionList.size() < monsterNum), "配置表：刷怪点数少于怪物数,gridCount = " + positionList.size(), ",monsterNum = " + monsterNum);
			return positionList;
		}

		if (BirthRegionType.Polygon_Type == birthRegionType) {
			@SuppressWarnings("unchecked")
			List<Rectangle> rectangleList = getRegionDataObject(List.class);
			Rectangle randomRectangle = getRandomRectangle(rectangleList);
			positionList = getCenterPositionsCount(randomRectangle.getArea(), SceneAOILayer.AOIGRID_MULTIPLE);
			Preconditions.checkArgument(!(positionList.size() < monsterNum), "配置表：刷怪点数少于怪物数,gridCount = " + positionList.size(), ",monsterNum = " + monsterNum);
			return positionList;
		}

		return positionList;
	}

	public static List<Position> getCenterPositionsCount(SceneTerrainGridContainer area, int ratio) {
		Collection<SceneGrid> grids = area.getUnblockedGrids();
		List<Position> positionList = new ArrayList<Position>();
		for (SceneGrid grid : grids) {
			Position position = new Position(grid.getColumn(), grid.getRow());
			position = GameSceneHelper.getCenterPosition(position);
			if (!positionList.contains(position)) {
				positionList.add(position);
			}
		}
		Collections.shuffle(positionList);
		return positionList;
	}

	public static Rectangle getRandomRectangle(List<Rectangle> rectangleList) {
		Preconditions.checkNotNull(rectangleList);
		int size = rectangleList.size() - 1;
		int random = SFRandomUtils.random(size);
		return rectangleList.get(random);
	}

}
