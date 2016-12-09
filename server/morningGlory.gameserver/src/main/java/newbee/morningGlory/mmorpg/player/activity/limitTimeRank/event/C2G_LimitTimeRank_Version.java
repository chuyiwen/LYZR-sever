package newbee.morningGlory.mmorpg.player.activity.limitTimeRank.event;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class C2G_LimitTimeRank_Version extends ActionEventBase{
	private byte limitRankType;
	
	@Override
	protected IoBuffer packBody(IoBuffer arg0) {
		return null;
	}

	@Override
	public void unpackBody(IoBuffer buffer) {
		limitRankType = buffer.get();
	}

	public byte getLimitRankType() {
		return limitRankType;
	}

	public void setLimitRankType(byte limitRankType) {
		this.limitRankType = limitRankType;
	}

}
