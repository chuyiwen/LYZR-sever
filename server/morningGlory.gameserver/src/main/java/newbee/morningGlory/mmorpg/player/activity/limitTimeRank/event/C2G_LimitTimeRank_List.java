package newbee.morningGlory.mmorpg.player.activity.limitTimeRank.event;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class C2G_LimitTimeRank_List extends ActionEventBase {
	private byte SortBoardType;

	@Override
	protected IoBuffer packBody(IoBuffer arg0) {
		return null;
	}

	@Override
	public void unpackBody(IoBuffer buffer) {
		SortBoardType = buffer.get();
	}

	public byte getSortBoardType() {
		return SortBoardType;
	}

	public void setSortBoardType(byte sortBoardType) {
		SortBoardType = sortBoardType;
	}

}
