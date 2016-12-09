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

import static com.google.common.base.Preconditions.checkArgument;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import sophia.foundation.communication.core.ActionEventBase;
import sophia.foundation.util.Pair;
import sophia.foundation.util.Position;
import sophia.game.GameRoot;
import sophia.mmorpg.base.scene.aoi.EightDirection;
import sophia.mmorpg.base.scene.aoi.SceneAOIGrid;
import sophia.mmorpg.base.scene.aoi.SceneAOILayer;
import sophia.mmorpg.base.scene.aoi.SceneAOIPiece;
import sophia.mmorpg.base.scene.grid.SceneGrid;
import sophia.mmorpg.base.scene.grid.SceneTerrainLayer;
import sophia.mmorpg.base.scene.ref.region.SceneSafeRegion;
import sophia.mmorpg.base.sprite.FightSprite;
import sophia.mmorpg.base.sprite.Sprite;
import sophia.mmorpg.loot.Loot;
import sophia.mmorpg.monster.Monster;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.utils.SFRandomUtils;

import com.google.common.base.Preconditions;

public final class GameSceneHelper {

	private static Logger logger = Logger.getLogger(GameSceneHelper.class);
	
	private static int maxBroadcastAOIPlayerCount = 200;
	
	public static void setMaxBoradcastAOIPlayerCount(int broadcastAOIPlayerCount) {
		maxBroadcastAOIPlayerCount = broadcastAOIPlayerCount;
	}
	
	public static int getMaxBoradcastAOIPlayerCount() {
		return maxBroadcastAOIPlayerCount;
	}
	
	public static boolean isInTheSameAOIRegion(Sprite sp1, Sprite sp2) {
		GameScene crtScene1 = sp1.getCrtScene();
		GameScene crtScene2 = sp2.getCrtScene();
		if (!crtScene1.equals(crtScene2)) {
			return false;
		}
		
		int pieceHeight = crtScene1.getAoiLayer().getPieceHeight() * 3;
		int pieceWidth = crtScene1.getAoiLayer().getPieceWidth() * 3;
		Position crtPosition1 = sp1.getCrtPosition();
		Position crtPosition2 = sp2.getCrtPosition();
		if (Math.abs(crtPosition2.getX() - crtPosition1.getX()) > pieceWidth) {
			return false;
		}
		if (Math.abs(crtPosition2.getY() - crtPosition1.getY()) > pieceHeight) {
			return false;
		}
		
		return true;
	}

	public static boolean isValidPosition(GameScene gameScene, Position pos) {
		if (gameScene == null || pos == null)
			return false;
		return gameScene.getRef().getTerrainLayer().isInMatrixRange(pos.getY(), pos.getX());
	}

	public static boolean isValidPosition(GameScene gameScene, int x, int y) {
		if (gameScene == null)
			return false;
		return gameScene.getRef().getTerrainLayer().isInMatrixRange(y, x);
	}
	
	public static boolean isInSafeRegion(GameScene gameScene, int x, int y) {
		Preconditions.checkNotNull(gameScene);
		SceneGrid sceneGrid = gameScene.getTerrainLayer().getSceneGrid(y, x);
		if (sceneGrid == null) {
			return false;
		}
		
		SceneSafeRegion safeRegion = gameScene.getSafeRegion();
		if (safeRegion == null) {
			return false;
		}
		
		return safeRegion.getRegion().contains(sceneGrid);
	}

	/**
	 * 坐标值转换，64px -> 16px
	 * 
	 * @param coordinate
	 * @return
	 */
	public static int AOIGrid2Position(int coordinate) {
		return coordinate * 4 + 1;
	}

	public static int position2AOIGrid(int coordinate) {
		int centerCoordinate = getCenterCoordinate(coordinate);
		return (centerCoordinate - 1) / 4;
	}

	public static void checkInAOIGridCenter(Position pos) {
		if (pos != null) {
			checkArgument((pos.getX() - 1) % 4 == 0, "position x not in AOI grid center" + pos);
			checkArgument((pos.getY() - 1) % 4 == 0, "position y not in AOI grid center" + pos);
		}
	}

	public static boolean isInAOIGridCenter(Position pos) {
		if (pos != null) {
			boolean xInCenter = (pos.getX() - 1) % 4 == 0;
			boolean yInCenter = (pos.getY() - 1) % 4 == 0;
			return xInCenter && yInCenter;
		}
		return false;
	}

	/**
	 * 两点之间AOIGrid格子距离
	 * 
	 * @param pos1
	 * @param pos2
	 * @return
	 */
	public static int distance(GameScene gameScene, Position pos1, Position pos2) {
		if (!isValidPosition(gameScene, pos1) || !isValidPosition(gameScene, pos2)) {
			return Integer.MAX_VALUE;
		}
		SceneAOILayer aoiLayer = gameScene.getAoiLayer();
		SceneAOIGrid aoiGrid1 = aoiLayer.getAOIGrid(pos1.getX(), pos1.getY());
		SceneAOIGrid aoiGrid2 = aoiLayer.getAOIGrid(pos2.getX(), pos2.getY());
		int c_dis = Math.abs(aoiGrid1.getColumn() - aoiGrid2.getColumn());
		int r_dis = Math.abs(aoiGrid1.getRow() - aoiGrid2.getRow());
		return c_dis > r_dis ? c_dis : r_dis;
	}

	public static List<SceneGrid> getSceneGrids(GameScene gameScene, Position pos, int radius, String excludeSpriteType) {

		List<SceneGrid> gridList = new ArrayList<>();

		SceneAOILayer aoiLayer = gameScene.getAoiLayer();
		SceneAOIGrid crtAoiGrid = aoiLayer.getAOIGrid(pos.getX(), pos.getY());
		int row = crtAoiGrid.getRow();
		int column = crtAoiGrid.getColumn();
		int minValue = -radius;
		int maxValue = radius;

		SceneTerrainLayer terrainLayer = gameScene.getTerrainLayer();

		for (int i = maxValue; i >= minValue; i--) {
			int c = column + i;
			for (int j = minValue; j <= maxValue; j++) {
				int r = row + j;

				if (!aoiLayer.isInMatrixRange(c, r)) {
					continue;
				}

				boolean existLoot = false;
				SceneAOIGrid aoiGrid = aoiLayer.getSceneAOIGrid(r, c);
				Set<Sprite> spriteSet = aoiGrid.getSpriteSet();
				if (spriteSet != null) {
					for (Sprite sprite : spriteSet) {
						if (sprite.getGameSpriteType().equals(excludeSpriteType)) {
							existLoot = true;
							break;
						}
					}
				}

				if (existLoot) {
					continue;
				}

				int x = AOIGrid2Position(c);
				int y = AOIGrid2Position(r);
				if (!terrainLayer.isInMatrixRange(y, x)) {
					continue;
				}

				SceneGrid sceneGrid = terrainLayer.getSceneGrid(y, x);
				if (!sceneGrid.isBlocked()) {
					gridList.add(sceneGrid);
				}
			}
		}

		return gridList;
	}
	
	private static SceneGrid getSingleLootSceneGrid(GameScene gameScene, Position pos) {
		SceneTerrainLayer terrainLayer = gameScene.getTerrainLayer();
		return terrainLayer.getSceneGrid(pos.getY(), pos.getX());
	}

	/**
	 * 以pos为中心，获取count掉落个数的周围的格子，排除阻挡点，掉落
	 * 
	 * @param gameScene
	 * @param pos
	 * @param count
	 * @return
	 */
	public static List<SceneGrid> getLootSceneGrids(GameScene gameScene, Position pos, int count) {
		List<SceneGrid> gridList = new ArrayList<>();
		if (!isValidPosition(gameScene, pos)) {
			logger.error("getLootSceneGrids error, invalid position, sceneRefId=" + gameScene.getRef().getId() + ", pos=" + pos);
			return gridList;
		}
		
		if (isBlocked(gameScene, pos)) {
			logger.error("getLootSceneGrids error, blocked position, sceneRefId=" + gameScene.getRef().getId() + ", pos=" + pos);
			return gridList;
		}
		
		if (count <= 1) {
			gridList.add(getSingleLootSceneGrid(gameScene, pos));
			return gridList;
		}

		// 计算所需格子数
		int radius = 0;
		int totalCount = 0;
		do {
			radius++;
			totalCount += radius * 8;
		} while (totalCount + 1 < count);

		int tryCount = 0;
		do {
			gridList = GameSceneHelper.getSceneGrids(gameScene, pos, radius, Loot.class.getSimpleName());

			radius++;

			if (++tryCount >= 10) {
				logger.error(gameScene + " getLootSceneGrids can't find enough free AOI grid for Loot");	
				break;
			}

		} while (gridList.size() < count);
		
		if (gridList.isEmpty()) {
			gridList.add(getSingleLootSceneGrid(gameScene, pos));
		}
		
		return gridList;
	}

	public static Collection<Loot> getLoot(GameScene scene, Position pos, int row, int column) {
		Collection<Sprite> sprites = getSprites(scene, pos, row, column);
		ArrayList<Loot> loots = new ArrayList<>();
		for (Sprite sprite : sprites) {
			if (sprite instanceof Loot) {
				Loot loot = (Loot) sprite;
				loots.add(loot);
			}
		}
		return loots;
	}

	public static Collection<Sprite> getSprites(GameScene gameScene, Position pos, int row, int column) {
		List<Sprite> collection = new ArrayList<>();

		if (gameScene == null || pos == null || row < 0 || column < 0) {
			return collection;
		}
		SceneAOILayer aoiLayer = gameScene.getAoiLayer();
		SceneAOIGrid crtAoiGrid = aoiLayer.getAOIGrid(pos.getX(), pos.getY());
		int crtRow = crtAoiGrid.getRow();
		int crtColumn = crtAoiGrid.getColumn();

		int nColumn = aoiLayer.getGridColumns();
		int nRow = aoiLayer.getGridRows();
		int halfColumn = column / 2;
		int halfRow = row / 2;
		int leftUpX = crtColumn - halfColumn;
		if (leftUpX < 0) {
			leftUpX = 0;
		}
		int leftUpY = crtRow - halfRow;
		if (leftUpY < 0) {
			leftUpY = 0;
		}
		int rightDownX = crtColumn + halfColumn;
		if (rightDownX > nColumn - 1) {
			rightDownX = nColumn - 1;
		}
		int rightDownY = crtRow + halfRow;
		if (rightDownY > nRow - 1) {
			rightDownY = nRow - 1;
		}

		for (int i = leftUpY; i <= rightDownY; i++) {
			for (int j = leftUpX; j <= rightDownX; j++) {
				SceneAOIGrid aoiGrid = aoiLayer.getSceneAOIGrid(i, j);
				Set<Sprite> spriteSet = aoiGrid.getSpriteSet();
				if (spriteSet != null) {
					for (Sprite sprite : spriteSet) {
						collection.add(sprite);
					}
				}
			}
		}

		return collection;
	}

	/**
	 * 
	 * @param gameScene
	 * @param center
	 * @param radiusInGrid
	 * @return a collection of sprites who are in range
	 */
	public static Collection<Sprite> getSprites(GameScene gameScene, Position center, int radiusInGrid) {
		if (!isValidPosition(gameScene, center)) {
			return null;
		}
		SceneAOILayer aoiLayer = gameScene.getAoiLayer();
		SceneAOIGrid crtAoiGrid = aoiLayer.getAOIGrid(center.getX(), center.getY());
		int crtRow = crtAoiGrid.getRow();
		int crtColumn = crtAoiGrid.getColumn();

		int nColumn = aoiLayer.getGridColumns();
		int nRow = aoiLayer.getGridRows();
		int leftUpX = crtColumn - radiusInGrid;
		if (leftUpX < 0) {
			leftUpX = 0;
		}
		int leftUpY = crtRow - radiusInGrid;
		if (leftUpY < 0) {
			leftUpY = 0;
		}
		int rightDownX = crtColumn + radiusInGrid;
		if (rightDownX > nColumn - 1) {
			rightDownX = nColumn - 1;
		}
		int rightDownY = crtRow + radiusInGrid;
		if (rightDownY > nRow - 1) {
			rightDownY = nRow - 1;
		}

		List<Sprite> collection = new ArrayList<>();
		for (int i = leftUpY; i <= rightDownY; i++) {
			for (int j = leftUpX; j <= rightDownX; j++) {
				SceneAOIGrid aoiGrid = aoiLayer.getSceneAOIGrid(i, j);
				Set<Sprite> spriteSet = aoiGrid.getSpriteSet();
				if (spriteSet != null) {
					for (Sprite sprite : spriteSet) {
						collection.add(sprite);
					}
				}
			}
		}

		return collection;
	}

	public static Collection<FightSprite> getFightSprites(GameScene gameScene, Position center, int radiusInGrid) {
		List<FightSprite> res = new ArrayList<>();
		Collection<Sprite> coll = getSprites(gameScene, center, radiusInGrid);
		for (Sprite sprite : coll) {
			if (sprite instanceof FightSprite && !((FightSprite) sprite).isDead()) {
				FightSprite fightSprite = (FightSprite) sprite;
				if (sprite instanceof Monster && ((Monster) sprite).getMonsterRef().isSkillSummon()) {
					continue;
				}
				res.add(fightSprite);
			}
		}
		return res;
	}

	public static Collection<FightSprite> getSkillSummon(GameScene gameScene, Position center, int radiusInGrid) {
		List<FightSprite> res = new ArrayList<>();
		Collection<Sprite> coll = getSprites(gameScene, center, radiusInGrid);
		for (Sprite sprite : coll) {
			if (sprite instanceof FightSprite && !((FightSprite) sprite).isDead()) {
				FightSprite fightSprite = (FightSprite) sprite;
				if (sprite instanceof Monster && ((Monster) sprite).getMonsterRef().isSkillSummon()) {
					res.add(fightSprite);
				}
			}
		}
		return res;
	}

	public static Collection<FightSprite> getFightSprites(GameScene gameScene, FightSprite whoToExclude, Position center, int radiusInGrid) {
		List<FightSprite> res = new ArrayList<>();
		Collection<Sprite> coll = getSprites(gameScene, center, radiusInGrid);
		for (Sprite sprite : coll) {
			if (sprite instanceof FightSprite && !sprite.equals(whoToExclude) && !((FightSprite) sprite).isDead()) {
				FightSprite fightSprite = (FightSprite) sprite;
				if (sprite instanceof Monster && ((Monster) sprite).getMonsterRef().isSkillSummon()) {
					continue;
				}
				res.add(fightSprite);
			}
		}
		return res;
	}

	public static FightSprite getNearestEnemy(Monster monster, int radiusInGrid) {
		FightSprite ret = null;
		int minDistance = Integer.MAX_VALUE;

		Collection<FightSprite> sprites = GameSceneHelper.getFightSprites(monster.getCrtScene(), monster, monster.getCrtPosition(), radiusInGrid);
		for (FightSprite sprite : sprites) {
			int distance = GameSceneHelper.distance(monster.getCrtScene(), monster.getCrtPosition(), sprite.getCrtPosition());
			if (minDistance > distance) {
				minDistance = distance;
				ret = sprite;
			}
		}

		return ret;
	}

	public static Player getNearestPlayer(Monster monster, int radiusInGrid) {
		Player ret = null;
		int minDistance = Integer.MAX_VALUE;

		if (!monster.getCrtScene().getPlayerMgrComponent().hasPlayer()) {
			return ret;
		}

		Collection<FightSprite> sprites = GameSceneHelper.getFightSprites(monster.getCrtScene(), monster.getCrtPosition(), radiusInGrid);

		for (FightSprite sprite : sprites) {
			if (sprite instanceof Player) {
				Player player = (Player) sprite;

				if (!monster.isEnemyTo(player)) {
					continue;
				}

				int distance = GameSceneHelper.distance(monster.getCrtScene(), monster.getCrtPosition(), sprite.getCrtPosition());
				if (minDistance > distance) {
					minDistance = distance;
					ret = player;
				}
			}
		}

		return ret;
	}

	/**
	 * A position is walkable if and only if the position is not blocked, by the
	 * perspective of SceneTerrainLayer, and there's no sprite on the position.
	 * 
	 * @param scene
	 * @param pos
	 * @return
	 */
	public static boolean isWalkable(GameScene scene, Position pos) {
		if (!isValidPosition(scene, pos)) {
			return false;
		}

		if (isBlocked(scene, pos)) {
			return false;
		}

		Collection<FightSprite> sprites = getFightSprites(scene, pos, 0, 0);
		return sprites.size() == 0;
	}

	public static boolean isValidPositionInScene(GameScene scene, Position pos) {
		if (scene == null || pos == null) {
			return false;
		}

		SceneTerrainLayer terrainLayer = scene.getRef().getTerrainLayer();
		boolean inRange = terrainLayer.isInMatrixRange(pos.getY(), pos.getX());
		return inRange;
	}

	public static boolean isBlocked(GameScene scene, Position pos) {
		if (!isValidPositionInScene(scene, pos)) {
			return true;
		}

		SceneTerrainLayer terrainLayer = scene.getRef().getTerrainLayer();
		SceneGrid sceneGrid = terrainLayer.getSceneGrid(pos.getY(), pos.getX());
		return sceneGrid.isBlocked();
	}

	// TODO: test this function !!!
	public static Position getFirstWalkablePositionInDirection(GameScene scene, Position start, byte direction) {
		Position forwardPosition = getForwardPosition(scene, direction, start, 1);
		if (!isValidPositionInScene(scene, forwardPosition)) {
			return null;
		} else if (isWalkable(scene, forwardPosition)) {
			return forwardPosition;
		} else {
			return getFirstWalkablePositionInDirection(scene, forwardPosition, direction);
		}
	}

	// TODO: test this function !!!
	public static Position getFirstBlockedPositionInDirection(GameScene scene, Position start, byte direction) {
		Position forwardPosition = getForwardPosition(scene, direction, start, 1);
		if (!isValidPositionInScene(scene, forwardPosition)) {
			return null;
		}

		boolean isBlocked = scene.getRef().getTerrainLayer().getSceneGrid(forwardPosition.getY(), forwardPosition.getX()).isBlocked();
		if (isBlocked) {
			return forwardPosition;
		} else {
			return getFirstBlockedPositionInDirection(scene, start, direction);
		}
	}

	/**
	 * This function will figure out the walkable position from the
	 * <code>start</code> going in <code>direction</code> by
	 * <code>gridToMove</code> grids. It will return the position before the
	 * first blocked position along the road.
	 * 
	 * @param scene
	 * @param start
	 * @param direction
	 * @param gridToMove
	 * @return
	 */
	public static Position tryToGoInDirection(GameScene scene, Position start, byte direction, int gridToMove) {
		Position forwardPosition = getForwardPosition(scene, direction, start, 1);
		if (!isValidPositionInScene(scene, forwardPosition)) {
			return null;
		} else if (isWalkable(scene, forwardPosition)) {
			return forwardPosition;
		} else {
			return getFirstWalkablePositionInDirection(scene, forwardPosition, direction);
		}
	}

	public static Position getForwardPosition(GameScene gameScene, byte direction, Position position, int nGrid) {
		if (!isValidPositionInScene(gameScene, position)) {
			return null;
		}

		SceneAOILayer aoiLayer = gameScene.getAoiLayer();

		Pair<Integer, Integer> angleValue = EightDirection.getAngleValue(direction);
		int deltaX = angleValue.getValue();
		int deltaY = angleValue.getKey();

		int x = position.getX();
		int y = position.getY();
		SceneAOIGrid aoiGrid = aoiLayer.getAOIGrid(x, y);
		int column = aoiGrid.getColumn() + deltaX * nGrid;
		int row = aoiGrid.getRow() + deltaY * nGrid;
		if (!aoiLayer.isInMatrixRange(column, row)) {
			return null;
		}

		int targetX = AOIGrid2Position(column);
		int targetY = AOIGrid2Position(row);
		if (!isValidPosition(gameScene, targetX, targetY)) {
			return null;
		}

		return new Position(targetX, targetY);
	}

	public static Position getForwardPosition(byte currentDirection, Position currentPosition, int nGrid) {
		checkArgument(currentDirection >= 0 && currentDirection < EightDirection.nDirection);
		checkArgument(currentPosition != null);
		int x = currentPosition.getX();
		int y = currentPosition.getY();
		Pair<Integer, Integer> angleValue = EightDirection.getAngleValue(currentDirection);
		int deltaX = angleValue.getValue();
		int deltaY = angleValue.getKey();

		int targetX = x + deltaX * nGrid;
		int targetY = y + deltaY * nGrid;
		return new Position(targetX, targetY);
	}

	public static SceneAOIGrid[] getTowardsSceneAOIGrid(GameScene gameScene, Position pos, byte direction, int row, int column) {
		Preconditions.checkArgument(row % 2 != 0, "calculate towards error, row must be Odd number");
		Preconditions.checkArgument(direction >= 0 && direction < EightDirection.nDirection, "calculate towards error, invalid direction=" + direction);
		Pair<Integer, Integer> angleValue = EightDirection.getAngleValue(direction);
		Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> lrAngleValue = EightDirection.getLRAngleValue(direction);
		SceneAOIGrid[] startGridArray = new SceneAOIGrid[row];
		SceneAOIGrid[] gridArray = new SceneAOIGrid[row * column];

		SceneAOILayer aoiLayer = gameScene.getAoiLayer();
		SceneAOIGrid crtAOIGrid = aoiLayer.getAOIGrid(pos.getX(), pos.getY());

		int k = 0;
		startGridArray[k] = crtAOIGrid;
		SceneAOIGrid sceneAOIGrid;

		int sideRows = (row - 1) / 2;
		for (int i = 1; i <= sideRows; i++) {
			// 左半部分
			Pair<Integer, Integer> key = lrAngleValue.getKey();
			int r = crtAOIGrid.getRow() + key.getValue() * i;
			int c = crtAOIGrid.getColumn() + key.getKey() * i;
			if (aoiLayer.isInMatrixRange(c, r)) {
				sceneAOIGrid = aoiLayer.getSceneAOIGrid(r, c);
				startGridArray[++k] = sceneAOIGrid;
			}

			// 右半部分
			Pair<Integer, Integer> value = lrAngleValue.getValue();
			r = crtAOIGrid.getRow() + value.getValue() * i;
			c = crtAOIGrid.getColumn() + value.getKey() * i;
			if (aoiLayer.isInMatrixRange(c, r)) {
				sceneAOIGrid = aoiLayer.getSceneAOIGrid(r, c);
				startGridArray[++k] = sceneAOIGrid;
			}
		}
		k = 0;
		for (int i = 0; i < row; i++) {
			SceneAOIGrid startSceneGrid = startGridArray[i];
			for (int j = 1; j <= column; j++) {
				int r = startSceneGrid.getRow() + angleValue.getKey() * j;
				int c = startSceneGrid.getColumn() + angleValue.getValue() * j;
				if (aoiLayer.isInMatrixRange(c, r) && k < (row * column)) {
					gridArray[k++] = aoiLayer.getSceneAOIGrid(r, c);
				}
			}
		}

		return gridArray;
	}

	public static Position getForwardCenterPosition(byte direction, Position start, int nGrid) {
		Position forwardPosition = getForwardPosition(direction, start, nGrid);
		Position centerPosition = getCenterPosition(forwardPosition);
		return centerPosition;
	}

	public static Collection<FightSprite> getFightSprites(GameScene gameScene, Position pos, byte direction, int row, int column) {
		SceneAOIGrid[] gridArray = new SceneAOIGrid[row * column];
		SceneAOIGrid sceneAOIGrid;
		gridArray = getTowardsSceneAOIGrid(gameScene, pos, direction, row, column);
		List<FightSprite> collection = new ArrayList<>();
		for (int i = 0; i < gridArray.length; i++) {
			sceneAOIGrid = gridArray[i];
			if (sceneAOIGrid != null) {
				Set<Sprite> spriteSet = sceneAOIGrid.getSpriteSet();
				if (spriteSet != null) {
					for (Sprite sprite : spriteSet) {
						if (sprite instanceof FightSprite && !((FightSprite) sprite).isDead()) {
							if (sprite instanceof Monster && ((Monster) sprite).getMonsterRef().isSkillSummon()) {
								continue;
							}
							FightSprite fightSprite = (FightSprite) sprite;
							collection.add(fightSprite);
						}
					}
				}
			}
		}

		return collection;
	}

	public static Collection<FightSprite> getFightSprites(GameScene gameScene, Position pos, int row, int column) {
		List<FightSprite> collection = new ArrayList<>();
		Collection<Sprite> spriteCollection = getSprites(gameScene, pos, row, column);
		for (Sprite sprite : spriteCollection) {
			if (sprite instanceof FightSprite && !((FightSprite) sprite).isDead()) {
				FightSprite fightSprite = (FightSprite) sprite;
				if (fightSprite instanceof Monster && ((Monster) sprite).getMonsterRef().isSkillSummon()) {
					continue;
				}
				collection.add(fightSprite);
			}
		}

		return collection;
	}

	public static Collection<Monster> getMonsters(GameScene scene, Position pos, int row, int column) {
		List<Monster> collection = new ArrayList<>();
		Collection<FightSprite> fightSprites = getFightSprites(scene, pos, row, column);
		for (FightSprite sprite : fightSprites) {
			if (sprite instanceof Monster) {
				Monster monster = (Monster) sprite;
				collection.add(monster);
			}
		}
		return collection;
	}

	public static Collection<FightSprite> getFightSprites(GameScene gameScene, FightSprite whoToExclude, Position pos, int row, int column) {
		List<FightSprite> collection = new ArrayList<>();
		Collection<Sprite> spriteCollection = getSprites(gameScene, pos, row, column);
		for (Sprite sprite : spriteCollection) {
			if (sprite instanceof FightSprite && !sprite.equals(whoToExclude) && !((FightSprite) sprite).isDead()) {
				FightSprite fightSprite = (FightSprite) sprite;
				if (fightSprite instanceof Monster && ((Monster) sprite).getMonsterRef().isSkillSummon()) {
					continue;
				}
				collection.add(fightSprite);
			}
		}

		return collection;
	}

	public static Collection<FightSprite> getFightSprites(GameScene gameScene, FightSprite whoToExclude, Position pos, byte direction, int row, int column) {
		List<FightSprite> collection = new ArrayList<>();
		Collection<FightSprite> spriteCollection = getFightSprites(gameScene, pos, direction, row, column);
		for (FightSprite sprite : spriteCollection) {
			if (!sprite.equals(whoToExclude) && !collection.contains(sprite)) {
				collection.add(sprite);
			}
		}

		return collection;
	}

	public static Collection<Sprite> getAOIInterestedSprites(Sprite who) {
		if (who == null) {
			return null;
		}

		List<Sprite> sprites = new ArrayList<>();
		int x = who.getCrtPosition().getX();
		int y = who.getCrtPosition().getY();
		SceneAOILayer aoiLayer = who.getCrtScene().getAoiLayer();
		SceneAOIGrid aoiGrid = aoiLayer.getAOIGrid(x, y);
		List<SceneAOIPiece> pieceSquare = who.getCrtScene().getAoiLayer().getPieceSquare(aoiGrid);
		for (SceneAOIPiece sceneAOIPiece : pieceSquare) {
			List<SceneAOIGrid> gridList = sceneAOIPiece.getGridList();
			for (SceneAOIGrid sceneAOIGrid : gridList) {
				Set<Sprite> spriteSet = sceneAOIGrid.getSpriteSet();
				if (spriteSet != null) {
					sprites.addAll(spriteSet);
				}

			}
		}

		return sprites;
	}

	public static Collection<FightSprite> getAOIInterestedFightSprites(Sprite who) {
		Collection<Sprite> sprites = getAOIInterestedSprites(who);
		List<FightSprite> fightSprites = new ArrayList<>();
		for (Sprite sprite : sprites) {
			if (sprite instanceof FightSprite) {
				FightSprite fightSprite = (FightSprite) sprite;
				fightSprites.add(fightSprite);
			}
		}
		return fightSprites;
	}

	public static Collection<Monster> getAOIInterestedMonsters(Sprite who) {
		Collection<Sprite> sprites = getAOIInterestedSprites(who);
		List<Monster> monsters = new ArrayList<>();
		for (Sprite sprite : sprites) {
			if (sprite instanceof Monster) {
				Monster monster = (Monster) sprite;
				monsters.add(monster);
			}
		}
		return monsters;
	}

	public static Collection<Player> getAOIInterestedPlayers(Sprite who) {
		Collection<Sprite> sprites = getAOIInterestedSprites(who);
		List<Player> players = new ArrayList<>();
		for (Sprite sprite : sprites) {
			if (sprite instanceof Player) {
				Player player = (Player) sprite;
				players.add(player);
			}
		}
		return players;
	}

	public static void broadcastMessageToAOI(Sprite sprite, ActionEventBase actionEvent) {
		int count = 0;
		int maxBoradcastAOIPlayerLimit = getMaxBoradcastAOIPlayerCount();
		
		Collection<Player> interestedPlayers = getAOIInterestedPlayers(sprite);
		for (Player player : interestedPlayers) {
			if (++count > maxBoradcastAOIPlayerLimit) {
				break;
			}
			
			GameRoot.sendMessage(player.getIdentity(), actionEvent);
		}
	}

	public static SceneGrid getRandomWalkableGrid(GameScene scene) {
		if (scene == null) {
			return null;
		}
		SceneTerrainLayer terrainLayer = scene.getRef().getTerrainLayer();
		int row = SFRandomUtils.random(terrainLayer.getnRow()) - 1;
		int column = SFRandomUtils.random(terrainLayer.getnColumn()) - 1;
		SceneGrid grid = terrainLayer.getRandomUnblockedGrid(new Position(column, row), Math.max(row, column));
		return grid;
	}

	public static SceneGrid getRandomUnblockedGrid(GameScene scene, Position center, int radiusInGrid) {
		if (scene == null || center == null || radiusInGrid < 0) {
			return null;
		}
		SceneTerrainLayer terrainLayer = scene.getRef().getTerrainLayer();
		SceneGrid grid = terrainLayer.getRandomUnblockedGrid(center, radiusInGrid);
		return grid;
	}

	public static Position getCenterPosition(Position position) {
		if (position == null) {
			return null;
		}
		int gridRow = position.getY() / SceneAOILayer.AOIGRID_MULTIPLE;
		int gridColumn = position.getX() / SceneAOILayer.AOIGRID_MULTIPLE;
		return new Position(AOIGrid2Position(gridColumn), AOIGrid2Position(gridRow));
	}

	public static int getCenterCoordinate(int coordinate) {
		int newCoord = coordinate / SceneAOILayer.AOIGRID_MULTIPLE;
		return AOIGrid2Position(newCoord);
	}

}
