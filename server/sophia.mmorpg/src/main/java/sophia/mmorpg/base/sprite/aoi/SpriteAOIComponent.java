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
package sophia.mmorpg.base.sprite.aoi;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import sophia.foundation.property.PropertyDictionary;
import sophia.foundation.util.Position;
import sophia.mmorpg.MMORPGContext;
import sophia.mmorpg.base.ConcreteComponent;
import sophia.mmorpg.base.scene.GameScene;
import sophia.mmorpg.base.scene.aoi.SceneAOIGrid;
import sophia.mmorpg.base.scene.aoi.gameEvent.AOIEnter_GE;
import sophia.mmorpg.base.scene.aoi.gameEvent.AOIJumpTo_GE;
import sophia.mmorpg.base.scene.aoi.gameEvent.AOILeave_GE;
import sophia.mmorpg.base.scene.aoi.gameEvent.AOIMoveTo_GE;
import sophia.mmorpg.base.scene.aoi.gameEvent.AOIMove_GE;
import sophia.mmorpg.base.scene.aoi.gameEvent.AOIPropertyChg_GE;
import sophia.mmorpg.base.scene.aoi.gameEvent.AOIStopMove_GE;
import sophia.mmorpg.base.scene.grid.SceneGrid;
import sophia.mmorpg.base.scene.grid.SceneTerrainLayer;
import sophia.mmorpg.base.scene.tick.SceneTick_GE;
import sophia.mmorpg.base.sprite.Sprite;
import sophia.mmorpg.gameArea.GameArea;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;

import com.google.common.base.Preconditions;

public class SpriteAOIComponent<T extends Sprite> extends ConcreteComponent<T> {

	private static final Logger logger = Logger.getLogger(SpriteAOIComponent.class);

	protected static final String SceneTick_GE_Id = SceneTick_GE.class.getSimpleName();
	protected static final String AOIEnter_GE_Id = AOIEnter_GE.class.getSimpleName();
	protected static final String AOILeave_GE_Id = AOILeave_GE.class.getSimpleName();
	protected static final String AOIMove_GE_Id = AOIMove_GE.class.getSimpleName();
	protected static final String AOIMoveTo_GE_Id = AOIMoveTo_GE.class.getSimpleName();
	protected static final String AOIStopMove_GE_Id = AOIStopMove_GE.class.getSimpleName();
	protected static final String AOIJumpTo_GE_Id = AOIJumpTo_GE.class.getSimpleName();
	protected static final String AOIPropertyChg_GE_Id = AOIPropertyChg_GE.class.getSimpleName();

	private GameArea gameArea;

	public SpriteAOIComponent() {

	}

	@Override
	public void ready() {
		gameArea = MMORPGContext.getGameAreaComponent().getGameArea();
		super.ready();
	}

	@Override
	public void suspend() {
		super.suspend();
	}
	
	protected void checkPosition(GameScene scene, int x, int y) {
		SceneTerrainLayer terrainLayer = scene.getTerrainLayer();
		Preconditions.checkArgument(terrainLayer.isInMatrixRange(y, x), "position(" + x + ", " + y +") not in matrix range of scene=" + scene.getRef().getId());
		SceneGrid srcGrid = terrainLayer.getSceneGrid(y, x);
		Preconditions.checkArgument(srcGrid.isMovable(), "sceneRefId=" + scene.getRef().getId() + " position is blocked x=" + x + " y=" + y);
	}
	
	public GameScene getSceneByRefId(String sceneRefId) {
		GameScene gameScene = gameArea.getSceneById(sceneRefId);
		if (gameScene == null) {
			gameScene = getConcreteParent().getCrtScene();		
			if (gameScene == null || !StringUtils.equals(sceneRefId, gameScene.getRef().getId())) {
				gameScene = gameArea.createScene(sceneRefId);
			} 
		}
		
		return gameScene;
	}
	
	public void switchToScene(GameScene gameScene, int x, int y) {
		checkPosition(gameScene, x, y);
		
		String sceneRefId = gameScene.getRef().getId();
		getConcreteParent().setCrtScene(gameScene);
		MGPropertyAccesser.setOrPutSceneRefId(getConcreteParent().getProperty(), sceneRefId);
		getConcreteParent().getCrtPosition().setPosition(x, y);
	}

	public void enterScene(String sceneRefId, int x, int y) {
		GameScene gameScene = getSceneByRefId(sceneRefId);
		Preconditions.checkNotNull(gameScene);
		switchToScene(gameScene, x, y);
		enterScene(gameScene, x, y);
	}
	
	public void enterScene(GameScene scene, int x, int y) {
		Preconditions.checkNotNull(scene, "scene can't null");
		
		checkPosition(scene, x, y);
		
		getConcreteParent().setCrtScene(scene);
		getConcreteParent().getCrtPosition().setPosition(x, y);
		if (getConcreteParent().getPathComponent() != null) {
			getConcreteParent().getPathComponent().setPosition(x, y);
		}
		
		enterScene(scene);
	}

	protected void enterScene(GameScene scene) {
		// fire AOIEnter_GE gameEvent to SceneAOIComponent
		if (logger.isDebugEnabled()) {
			logger.debug("EnterScene " + scene.getRef().getId());
		}
		AOIEnter_GE ge = new AOIEnter_GE();
		ge.setSprite(getConcreteParent());
		sendGameEvent(AOIEnter_GE_Id, ge, scene.getId());
	}

	public void leaveScene(GameScene scene) {
		// fire AOILeave_GE gameEvent to SceneAOIComponent
		if (logger.isDebugEnabled()) {
			logger.debug("LeaveScene " + scene.getRef().getId());
		}
		AOILeave_GE ge = new AOILeave_GE();
		ge.setSprite(getConcreteParent());
		sendGameEvent(AOILeave_GE_Id, ge, scene.getId());
	}

	public void switchScene(GameScene from, GameScene to) {
		// fire AOILeave_GE gameEvent to from.SceneAOIComponent

		// fire AOIEnter_GE gameEvent to to.SceneAOIComponent
	}

	public void move(int dx, int dy) {
		// fire AOIMove_GE gameEvent to SceneAOIComponent
		AOIMove_GE ge = new AOIMove_GE();
		ge.setSprite(getConcreteParent());
		sendGameEvent(AOIMove_GE_Id, ge, getConcreteParent().getCrtScene().getId());
	}

	public void moveTo(int x, int y) {
		// fire AOIMove_GE gameEvent to SceneAOIComponent
	}

	public void broadcastProperty(PropertyDictionary property) {
		GameScene crtScene = getConcreteParent().getCrtScene();
		Preconditions.checkNotNull(crtScene, "crtScene=null " + getConcreteParent());
		Position crtPosition = getConcreteParent().getCrtPosition();
		SceneAOIGrid aoiGrid = crtScene.getAoiLayer().getAOIGrid(crtPosition.getX(), crtPosition.getY());
		Preconditions.checkNotNull(aoiGrid, "crtPosition " + crtPosition + " can't find aoiGrid " + getConcreteParent());
		AOIPropertyChg_GE ge = new AOIPropertyChg_GE();
		ge.setSprite(getConcreteParent());
		SpriteSceneProperty spriteSceneProperty = new SpriteSceneProperty();
		SpriteInfo spriteInfo = new SpriteInfo(getConcreteParent());
		spriteSceneProperty.setSpriteInfo(spriteInfo);
		spriteSceneProperty.setProperty(property.clone());
		ge.setSpriteSceneProperty(spriteSceneProperty);
		sendGameEvent(AOIPropertyChg_GE_Id, ge, getConcreteParent().getCrtScene().getId());
	}
}
