package newbee.morningGlory.mmorpg.player.union.actionEvent;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class C2G_Union_AutoAgree extends ActionEventBase {
	private String unionName;
	private byte autoState;

	@Override
	protected IoBuffer packBody(IoBuffer arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void unpackBody(IoBuffer buffer) {
		unionName = getString(buffer);
		autoState = buffer.get();
	}

	public byte getAutoState() {
		return autoState;
	}

	public void setAutoState(byte autoState) {
		this.autoState = autoState;
	}

	public String getUnionName() {
		return unionName;
	}

	public void setUnionName(String unionName) {
		this.unionName = unionName;
	}

}
