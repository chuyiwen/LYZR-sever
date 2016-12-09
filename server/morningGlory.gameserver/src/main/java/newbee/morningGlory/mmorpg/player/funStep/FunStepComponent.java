package newbee.morningGlory.mmorpg.player.funStep;

import java.util.ArrayList;
import java.util.List;

import newbee.morningGlory.mmorpg.player.funStep.event.C2G_FunStep_Complete_Request;
import newbee.morningGlory.mmorpg.player.funStep.event.FunStepActionEventDefines;
import newbee.morningGlory.mmorpg.player.funStep.event.G2C_FunStepList_Response;
import newbee.morningGlory.mmorpg.player.funStep.persistence.FunStepDao;

import org.apache.log4j.Logger;

import sophia.foundation.communication.core.ActionEventBase;
import sophia.foundation.communication.core.MessageFactory;
import sophia.game.GameRoot;
import sophia.game.component.communication.GameEvent;
import sophia.mmorpg.base.ConcreteComponent;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.PlayerManager;

public class FunStepComponent extends ConcreteComponent<Player>{
	private final static Logger logger = Logger.getLogger(FunStepComponent.class);
	public static final String Tag = "FunStepComponent";

	private List<String> completeStepList = new ArrayList<String>();
	
	public FunStepComponent(){
		
	}
	@Override
	public void ready() {
		addActionEventListener(FunStepActionEventDefines.C2G_FunStep_Request);
		addActionEventListener(FunStepActionEventDefines.C2G_FunStep_Complete_Request);
		addInterGameEventListener(PlayerManager.EnterWorld_GE_Id);
		super.ready();
	}
	
	@Override
	public void suspend() {
		removeActionEventListener(FunStepActionEventDefines.C2G_FunStep_Request);
		removeActionEventListener(FunStepActionEventDefines.C2G_FunStep_Complete_Request);
		removeInterGameEventListener(PlayerManager.EnterWorld_GE_Id);
		super.suspend();
	}
	
	@Override
	public void handleGameEvent(GameEvent<?> event) {
		if (event.isId(PlayerManager.EnterWorld_GE_Id)) {
			if (!FunStepDao.getInstance().selectData(getConcreteParent())) {
				FunStepDao.getInstance().insertData(getConcreteParent());
			}
		}
	}
	
	@Override
	public void handleActionEvent(ActionEventBase event) {
		short actionEventId = event.getActionEventId();
		
		switch (actionEventId) {
		case FunStepActionEventDefines.C2G_FunStep_Request:
			handle_FunStep_Request(event);
			break;
		case FunStepActionEventDefines.C2G_FunStep_Complete_Request:
			handle_FunStep_Complete_Request(event);
			break;
			
		default:
			break;
		
		}
	}
	
	public void handle_FunStep_Request(ActionEventBase event){
		
		sendFunStepList(event);
	}
	
	private void  sendFunStepList(ActionEventBase event){
		G2C_FunStepList_Response res = MessageFactory.getConcreteMessage(FunStepActionEventDefines.G2C_FunStepList_Response);
		res.setFunStepList(completeStepList);
		GameRoot.sendMessage(event.getIdentity(), res);
	}
	
	public void handle_FunStep_Complete_Request(ActionEventBase event){
		C2G_FunStep_Complete_Request message = (C2G_FunStep_Complete_Request) event;
		String stepId = message.getStepId();
		//refid验证
		FunStepDataRef ref = (FunStepDataRef) GameRoot.getGameRefObjectManager().getManagedObject(stepId);
		if(ref==null){
			logger.info(stepId+" is Invalid");
			return;
		}
		
		if(!isExistStep(stepId)){
			completeStepList.add(stepId);
			FunStepDao.getInstance().updateData(getConcreteParent());
			
			sendFunStepList(event);
		}
		
	}
	public List<String> getCompleteStepList() {
		return completeStepList;
	}
	public void setCompleteStepList(List<String> completeStepList) {
		this.completeStepList = completeStepList;
	}
	
	public boolean isExistStep(String stepId){
		for(String id : completeStepList){
			if(id.equals(stepId))
				return true;
		}
		return false;
	}
	
	
}
