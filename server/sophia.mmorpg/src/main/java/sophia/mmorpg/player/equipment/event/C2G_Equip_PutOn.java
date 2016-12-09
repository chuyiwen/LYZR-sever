package sophia.mmorpg.player.equipment.event;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class C2G_Equip_PutOn extends ActionEventBase {
	private byte bodyId;
	private byte position;
	private short gridId;// 该装备传到身上之前在背包中的格子id

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		buffer.putShort(gridId);
		buffer.put(bodyId);
		buffer.put(position);
		return null;
	}

	@Override
	public void unpackBody(IoBuffer buffer) {
		gridId = buffer.getShort();
		bodyId = buffer.get();
		position = buffer.get();
	}

	public String getName() {
		return "人物装备穿戴";
	}

	public byte getBodyId() {
		return bodyId;
	}

	public void setBodyId(byte bodyId) {
		this.bodyId = bodyId;
	}

	public byte getPosition() {
		return position;
	}

	public void setPosition(byte posIndex) {
		this.position = posIndex;
	}

	public short getGridId() {
		return gridId;
	}

	public void setGridId(short gridId) {
		this.gridId = gridId;
	}

}
