package newbee.morningGlory.mmorpg.player.union.actionEvent;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class C2G_Union_UnionList extends ActionEventBase {
	private byte kind;
	private byte segment;

	@Override
	protected IoBuffer packBody(IoBuffer arg0) {
		return null;
	}

	@Override
	public void unpackBody(IoBuffer buffer) {
		kind = buffer.get();
		segment = buffer.get();
	}

	public byte getKind() {
		return kind;
	}

	public void setKind(byte kind) {
		this.kind = kind;
	}

	public byte getSegment() {
		return segment;
	}

	public void setSegment(byte segment) {
		this.segment = segment;
	}

}
