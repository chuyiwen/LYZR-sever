package newbee.morningGlory.mmorpg.sceneActivities;

import sophia.foundation.communication.core.ActionEventBase;
import sophia.game.component.communication.GameEvent;
import sophia.mmorpg.base.ConcreteComponent;
import sophia.mmorpg.player.Player;

public class MGPlayerSceneActivityComponent extends ConcreteComponent<Player> {

	
	@Override
	public void ready() {
		super.ready();
	}

	@Override
	public void suspend() {
		super.suspend();
	}

	@Override
	public void handleGameEvent(GameEvent<?> event) {
		super.handleGameEvent(event);
	}

	@Override
	public void handleActionEvent(ActionEventBase event) {
		super.handleActionEvent(event);
	}

	
}
