package newbee.morningGlory.mmorpg.player.funStep.event;

import java.util.List;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class G2C_FunStepList_Response extends ActionEventBase{
	
	private List<String> funStepList;
	
	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		// TODO Auto-generated method stub
		buffer.put((byte)funStepList.size());
		for(String stepId : funStepList){
			putString(buffer, stepId);
		}
		
		return buffer;
	}

	@Override
	public void unpackBody(IoBuffer buffer) {
		// TODO Auto-generated method stub
		
	}

	public void setFunStepList(List<String> funStepList) {
		this.funStepList = funStepList;
	}
}
