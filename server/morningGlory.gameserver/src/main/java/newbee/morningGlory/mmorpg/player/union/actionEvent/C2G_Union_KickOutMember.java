package newbee.morningGlory.mmorpg.player.union.actionEvent;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class C2G_Union_KickOutMember extends ActionEventBase {
	private String kickedPlayerId;
	private String unionName;

	@Override
	protected IoBuffer packBody(IoBuffer arg0) {
		return null;
	}

	@Override
	public void unpackBody(IoBuffer buffer) {
		kickedPlayerId = getString(buffer);
		unionName = getString(buffer);
	}

	public String getKickedPlayerId() {
		return kickedPlayerId;
	}

	public void setKickedPlayerId(String kickedPlayerId) {
		this.kickedPlayerId = kickedPlayerId;
	}

	public String getUnionName() {
		return unionName;
	}

	public void setUnionName(String unionName) {
		this.unionName = unionName;
	}

}
