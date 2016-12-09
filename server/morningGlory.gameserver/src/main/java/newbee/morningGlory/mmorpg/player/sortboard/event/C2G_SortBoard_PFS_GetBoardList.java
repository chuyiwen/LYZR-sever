package newbee.morningGlory.mmorpg.player.sortboard.event;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class C2G_SortBoard_PFS_GetBoardList extends ActionEventBase {

	private int sortBoartType;
	private int profession;

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void unpackBody(IoBuffer buffer) {
		sortBoartType = buffer.getInt();
		profession = buffer.getInt();
	}

	public int getSortBoartType() {
		return sortBoartType;
	}

	public int getProfession() {
		return profession;
	}
}