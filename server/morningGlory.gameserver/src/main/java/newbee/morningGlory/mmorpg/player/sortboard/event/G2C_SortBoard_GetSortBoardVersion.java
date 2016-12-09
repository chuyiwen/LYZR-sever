package newbee.morningGlory.mmorpg.player.sortboard.event;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class G2C_SortBoard_GetSortBoardVersion extends ActionEventBase {
	private int sortboardType;
	private int version;

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		buffer.putInt(sortboardType);
		buffer.putInt(version);
		return buffer;
	}

	@Override
	public void unpackBody(IoBuffer buffer) {
		// TODO Auto-generated method stub

	}

	public int getSortboardType() {
		return sortboardType;
	}

	public void setSortboardType(int sortboardType) {
		this.sortboardType = sortboardType;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

}
