package newbee.morningGlory.mmorpg.player.activity.limitTimeRank.event;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class G2C_LimitTimeRank_Version extends ActionEventBase {
	private byte limitRankType;
	private int version;

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		buffer.put(limitRankType);
		buffer.putInt(version);
		return buffer;
	}

	@Override
	public void unpackBody(IoBuffer buffer) {
	}

	public byte getLimitRankType() {
		return limitRankType;
	}

	public void setLimitRankType(byte limitRankType) {
		this.limitRankType = limitRankType;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

}
