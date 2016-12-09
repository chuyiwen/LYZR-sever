package newbee.morningGlory.mmorpg.player.sectionQuest.event.mount;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class G2C_Mount_GetMountQuestReward extends ActionEventBase {
	private int result;

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		buffer.put((byte)result);
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