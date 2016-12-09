package newbee.morningGlory.mmorpg.player.sectionQuest.event.wing;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class G2C_Wing_WingQuestResp extends ActionEventBase {
	
	private int peerageLevel;
	
	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		buffer.putInt(peerageLevel);
		return buffer;
	}

	@Override
	public void unpackBody(IoBuffer buffer) {
		// TODO Auto-generated method stub
		
	}

	public int getPeerageLevel() {
		return peerageLevel;
	}

	public void setPeerageLevel(int peerageLevel) {
		this.peerageLevel = peerageLevel;
	}
}
