package newbee.morningGlory.mmorpg.player.sortboard.event;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class C2G_SortBoard_GetSortBoardVersion extends ActionEventBase {

	private int sortBoartType;

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void unpackBody(IoBuffer buffer) {
		setSortBoartType(buffer.getInt());
	}

	public int getSortBoartType() {
		return sortBoartType;
	}

	public void setSortBoartType(int sortBoartType) {
		this.sortBoartType = sortBoartType;
	}

}
