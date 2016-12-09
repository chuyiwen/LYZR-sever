package sophia.mmorpg.player.itemBag.event;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class C2G_Item_Sell extends ActionEventBase {

	private short gridId;
	private short number;
	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		return null;
	}

	@Override
	public void unpackBody(IoBuffer buffer) {
		gridId = buffer.getShort();
		number = buffer.getShort();
	

	}

	public short getGridId() {
		return gridId;
	}

	public void setGridId(short gridId) {
		this.gridId = gridId;
	}

	public short getNumber() {
		return number;
	}

	public void setNumber(short number) {
		this.number = number;
	}

	

}
