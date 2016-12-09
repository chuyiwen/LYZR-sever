package sophia.mmorpg.player.equipment.event;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class C2G_Equip_UnLoad extends ActionEventBase {
	private byte bodyId;
	private byte position;

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		buffer.put(bodyId);
		buffer.put(position);
		return null;
	}

	@Override
	public void unpackBody(IoBuffer buffer) {
		bodyId = buffer.get();
		position = buffer.get();
	}

	public String getName() {
		return "人物装备摘除";
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

	public void setPosition(byte position) {
		this.position = position;
	}

}
