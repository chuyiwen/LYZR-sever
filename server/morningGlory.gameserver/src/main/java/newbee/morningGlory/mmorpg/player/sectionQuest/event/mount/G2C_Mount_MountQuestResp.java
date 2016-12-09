package newbee.morningGlory.mmorpg.player.sectionQuest.event.mount;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class G2C_Mount_MountQuestResp extends ActionEventBase {
	private int time;

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		buffer.putInt(time);
		return buffer;
	}

	@Override
	public void unpackBody(IoBuffer buffer) {
		// TODO Auto-generated method stub
		
	}

	public int getTime() {
		return time;
	}

	public void setTime(int time) {
		this.time = time;
	}

}