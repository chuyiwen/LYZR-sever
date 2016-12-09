package newbee.morningGlory.mmorpg.sceneActivities.mutilExp;

import newbee.morningGlory.mmorpg.sceneActivities.SceneActivity;
import newbee.morningGlory.mmorpg.sceneActivities.event.G2C_MultiTimesExp_State;
import newbee.morningGlory.mmorpg.sceneActivities.event.SceneActivityEventDefines;
import newbee.morningGlory.mmorpg.sceneActivities.mutilExp.ref.MGMutilExpActivityRef;

import org.apache.log4j.Logger;

import sophia.foundation.communication.core.ActionEventBase;
import sophia.foundation.communication.core.MessageFactory;
import sophia.game.component.communication.GameEvent;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.chat.sysytem.SystemPromptFacade;
import sophia.mmorpg.world.ActionEventFacade;

public class MGMutilExpActivity extends SceneActivity {

	private static Logger logger = Logger.getLogger(MGMutilExpActivity.class);

	public MGMutilExpActivityRef getMGMutilExpActivityRef() {
		return getRef().getComponentRef(MGMutilExpActivityRef.class);
	}
	
	@Override
	public boolean checkEnter(Player player) {
		return true;
	}
	

	@Override
	public boolean checkLeave(Player player) {
		return true;
	}

	@Override
	public boolean onPreStart() {		
		return true;
	}

	@Override
	public boolean onPreEnd() {
		return true;
	}

	@Override
	public boolean onStart() {
		//走马灯公告
		SystemPromptFacade.broadCastMultiExpStart();
		
		G2C_MultiTimesExp_State res =  MessageFactory.getConcreteMessage(SceneActivityEventDefines.G2C_MultiTimesExp_State);
		res.setState(G2C_MultiTimesExp_State.Open);
		ActionEventFacade.sendMessageToWorld(res);
		
		return true;
	}

	@Override 
	public boolean onCheckEnd() {
		return true;
	}
	
	@Override
	public boolean onEnd() {
		//走马灯公告
		SystemPromptFacade.broadCastMultiExpEnd();

		G2C_MultiTimesExp_State res =  MessageFactory.getConcreteMessage(SceneActivityEventDefines.G2C_MultiTimesExp_State);
		res.setState(G2C_MultiTimesExp_State.Close);
		ActionEventFacade.sendMessageToWorld(res);
		
		return true;
	}

	@Override
	public void handleGameEvent(GameEvent<?> event) {

	}

	@Override
	public void handleActionEvent(ActionEventBase event) {

	}
	
	@Override
	public boolean onEnter(Player player) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onLeave(Player player) {
		// TODO Auto-generated method stub
		return false;
	}
	
}
