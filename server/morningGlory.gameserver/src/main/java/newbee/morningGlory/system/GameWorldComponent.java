/**
 * 
 */
package newbee.morningGlory.system;

import org.apache.log4j.Logger;

import newbee.morningGlory.mmorpg.sceneActivities.gameEvent.SceneActivityClose_GE;
import newbee.morningGlory.mmorpg.sceneActivities.gameEvent.SceneActivityOpen_GE;
import sophia.game.component.communication.GameEvent;
import sophia.game.plugIns.gameWorld.GameWorld;
import sophia.mmorpg.base.ConcreteComponent;
import sophia.mmorpg.player.worldBossMsg.WorldBoss;

/**
 * @author yinxinglin
 * 
 */
public class GameWorldComponent extends ConcreteComponent<GameWorld> {
	private static final Logger logger = Logger.getLogger(GameWorldComponent.class);
	private static final String SceneActivityClose_GE_ID = SceneActivityClose_GE.class.getSimpleName();
	private static final String SceneActivityOpen_GE_ID = SceneActivityOpen_GE.class.getSimpleName();

	@Override
	public void ready() {
		addInterGameEventListener(SceneActivityClose_GE_ID);
		addInterGameEventListener(SceneActivityOpen_GE_ID);
	}

	@Override
	public void suspend() {
		removeInterGameEventListener(SceneActivityClose_GE_ID);
		removeInterGameEventListener(SceneActivityOpen_GE_ID);
	}

	@Override
	public void handleGameEvent(GameEvent<?> event) {
		if (event.isId(SceneActivityOpen_GE_ID)) {
			SceneActivityOpen_GE ge = (SceneActivityOpen_GE) event.getData();
			WorldBoss.addSceneActivityRefID(ge.getSceneRefId());
			if(logger.isDebugEnabled()){
				logger.debug("活动开启，场景:"+ge.getSceneRefId());
			}
			WorldBoss.sendBossList();
		} else if (event.isId(SceneActivityClose_GE_ID)) {
			SceneActivityClose_GE ge = (SceneActivityClose_GE) event.getData();
			WorldBoss.removeSceneActivityRefID(ge.getSceneRefId());
			if(logger.isDebugEnabled()){
				logger.debug("活动关闭，场景:"+ge.getSceneRefId());
			}
			WorldBoss.sendBossList();
		}

	}

}
