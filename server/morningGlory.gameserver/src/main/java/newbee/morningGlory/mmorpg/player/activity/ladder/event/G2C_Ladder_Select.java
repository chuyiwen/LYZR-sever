package newbee.morningGlory.mmorpg.player.activity.ladder.event;

import newbee.morningGlory.mmorpg.ladder.MGLadderMemberMgr;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class G2C_Ladder_Select extends ActionEventBase{
	public G2C_Ladder_Select(){
		ziped =(byte)1;
	}
	
	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		byte[] bytes = MGLadderMemberMgr.writeLadderMessge();
		buffer.put(bytes);
		return buffer;
	}

	@Override
	public void unpackBody(IoBuffer arg0) {
		
	}
}
