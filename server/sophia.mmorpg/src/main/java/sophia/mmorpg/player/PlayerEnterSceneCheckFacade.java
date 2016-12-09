package sophia.mmorpg.player;

import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import sophia.foundation.util.Position;
import sophia.game.GameRoot;
import sophia.mmorpg.MMORPGContext;
import sophia.mmorpg.base.scene.grid.SceneGrid;
import sophia.mmorpg.base.scene.grid.SceneTerrainLayer;
import sophia.mmorpg.base.scene.ref.AbstractGameSceneRef;
import sophia.mmorpg.base.scene.ref.SceneRef;
import sophia.mmorpg.gameInstance.ComeFromScene;
import sophia.mmorpg.player.gameInstance.GameInstanceManager;
import sophia.mmorpg.player.property.PlayerConfig;
import sophia.mmorpg.sceneActivities.SceneActivityInterface;
import sophia.mmorpg.sceneActivities.SceneActivityMgrInterface;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

public class PlayerEnterSceneCheckFacade {
	private static Logger logger = Logger.getLogger(PlayerEnterSceneCheckFacade.class);

	private static GameInstanceManager gameInstanceManager;
	
	private static void recoverSceneAndPosition(Player player) {
		ComeFromScene comeFromScene = player.getPlayerSceneComponent().getComeFromScene();
		String sceneRefId = comeFromScene.getComeFromSceneRefId();
		int x = comeFromScene.getX();
		int y = comeFromScene.getY();
		if (Strings.isNullOrEmpty(sceneRefId)) {
			sceneRefId = player.getReviveSceneRefId();
			AbstractGameSceneRef sceneRef = (AbstractGameSceneRef) GameRoot.getGameRefObjectManager().getManagedObject(sceneRefId);
			SceneGrid sceneGrid = sceneRef.getRandomReviveGrid();
			x = sceneGrid.getColumn();
			y = sceneGrid.getRow();
		}
		
		PlayerConfig.setPositionTo(player, sceneRefId, x, y);
	}
	
	private static List<String> specialActivityScenes = Arrays.asList("S012", "S035", "S036", "S053", "S054", "S045", "S046", "S063", "S064", "S031", "S032", "S049", "S050", "S041", "S042", "S059", "S060", "S039", "S040", "S057", "S058", "S033", "S034", "S051", "S052", "S037", "S038", "S055", "S056", "S043", "S044", "S061", "S062");
	private static boolean isSpecialActivityScene(AbstractGameSceneRef sceneRef) {
		return specialActivityScenes.contains(sceneRef.getId());
	}

	public static void checkEnterScene(Player player) {
		// 对活动场景、副本进行判定，处理玩家没有出副本的情况
		String sceneRefId = player.getSceneRefId();
		AbstractGameSceneRef sceneRef = (AbstractGameSceneRef) GameRoot.getGameRefObjectManager().getManagedObject(sceneRefId);
		if (sceneRef == null) {
			sceneRef = (AbstractGameSceneRef) GameRoot.getGameRefObjectManager().getManagedObject(PlayerConfig.MAPID_BORN);
			SceneGrid sceneGrid = sceneRef.getRandomBirthGrid();
			PlayerConfig.setPositionTo(player, PlayerConfig.MAPID_BORN, sceneGrid.getColumn(), sceneGrid.getRow());
			logger.error("scene is null, sceneRefId = " + sceneRefId + " " + player);
		}
		// 副本
		else if (sceneRef.getType() == SceneRef.FuBen) {
			// 检查副本是否在缓存中存在
			Preconditions.checkNotNull(gameInstanceManager, "gameInstanceManager is null");
			if (!gameInstanceManager.hasGameInstanceCache(player)) {
				recoverSceneAndPosition(player);
			}
		}
		// 活动场景
		else if (sceneRef.getType() == SceneRef.Activity) {
			SceneActivityMgrInterface sceneActivityMgr = MMORPGContext.getGameAreaComponent().getGameArea().getSceneActivityMgr();
			SceneActivityInterface sceneActivity = sceneActivityMgr.getSceneAcitityBySceneRefId(sceneRefId);
			byte crtActivityState = sceneActivity.getCrtActivityState();
			if (crtActivityState == SceneActivityInterface.ACTIVITY_END && !isSpecialActivityScene(sceneRef)) {
				recoverSceneAndPosition(player);
			} else if (crtActivityState == SceneActivityInterface.ACTIVITY_START) {
				if (!sceneActivity.checkEnter(player)) {
					recoverSceneAndPosition(player);
				}
			}
		}

		// 校验坐标是否合法
		sceneRefId = player.getSceneRefId();
		sceneRef = (AbstractGameSceneRef) GameRoot.getGameRefObjectManager().getManagedObject(sceneRefId);
		SceneTerrainLayer terrainLayer = sceneRef.getTerrainLayer();
		Position crtPosition = player.getCrtPosition();
		if (!isValidPosition(sceneRef.getId(), crtPosition.getX(), crtPosition.getY())) {
			int nColumn = terrainLayer.getnColumn();
			int nRow = terrainLayer.getnRow();
			nColumn /= 2;
			nRow /= 2;

			SceneGrid centerGrid = terrainLayer.getSceneGrid(nRow, nColumn);
			SceneGrid randomUnblockedGrid = terrainLayer.getRandomUnblockedGrid(centerGrid, terrainLayer.getnRow());
			if (randomUnblockedGrid == null) {
				logger.error("can't find an valid sceneGrid, i fu le you (:");
				sceneRef = (AbstractGameSceneRef) GameRoot.getGameRefObjectManager().getManagedObject(PlayerConfig.MAPID_BORN);
				SceneGrid sceneGrid = sceneRef.getRandomBirthGrid();
				PlayerConfig.setPositionTo(player, PlayerConfig.MAPID_BORN, sceneGrid.getColumn(), sceneGrid.getRow());
				return;
			}

			nColumn = randomUnblockedGrid.getColumn();
			nRow = randomUnblockedGrid.getRow();
			PlayerConfig.setPositionTo(player, sceneRef.getId(), nColumn, nRow);
			logger.error("invalid position, assign x=" + nColumn + ", y=" + nRow);
		}
	}

	public static boolean isValidPosition(String sceneRefId, int x, int y) {
		AbstractGameSceneRef sceneRef = (AbstractGameSceneRef) GameRoot.getGameRefObjectManager().getManagedObject(sceneRefId);
		SceneTerrainLayer terrainLayer = sceneRef.getTerrainLayer();
		if (!terrainLayer.isInMatrixRange(y, x)) {
			logger.error("invalid position(x=" + x + ", y=" + y + ") not int this sceneRefId = " + sceneRefId);
			return false;
		} else if (terrainLayer.getSceneGrid(y, x).isBlocked()) {
			logger.error("invalid position(x=" + x + ", y=" + y + ") is Blocked sceneRefId = " + sceneRefId);
			return false;
		}

		return true;
	}

	public GameInstanceManager getGameInstanceManager() {
		return gameInstanceManager;
	}

	public static void setGameInstanceManager(GameInstanceManager manager) {
		gameInstanceManager = manager;
	}

}
