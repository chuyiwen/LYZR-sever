package newbee.morningGlory.mmorpg.player.sectionQuest.event.wing;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class G2C_Wing_GetWingQuestReward extends ActionEventBase {
	
	private int result;
	
	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		buffer.putInt(result);
		return buffer;
	}

	@Override
	public void unpackBody(IoBuffer buffer) {
		// TODO Auto-generated method stub
		
	}

	public int getResult() {
		return result;
	}

	public void setResult(int result) {
		this.result = result;
	}

}
