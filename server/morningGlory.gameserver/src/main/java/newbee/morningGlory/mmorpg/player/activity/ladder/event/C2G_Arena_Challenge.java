package newbee.morningGlory.mmorpg.player.activity.ladder.event;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class C2G_Arena_Challenge extends ActionEventBase{
	private int targetRank;
	
	public void setTargetRank(int targetRank) {
		this.targetRank = targetRank;
	}

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		buffer.putInt(targetRank);
		return buffer;
	}

	@Override
	public void unpackBody(IoBuffer buffer) {
		targetRank = buffer.getInt();
	}

	public int getTargetRank() {
		return targetRank;
	}

	
}
