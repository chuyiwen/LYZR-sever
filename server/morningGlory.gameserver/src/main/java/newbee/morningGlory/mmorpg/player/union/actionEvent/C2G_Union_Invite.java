package newbee.morningGlory.mmorpg.player.union.actionEvent;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class C2G_Union_Invite extends ActionEventBase {
	private String invitedPlayerId;
	private String unionName;

	@Override
	protected IoBuffer packBody(IoBuffer arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void unpackBody(IoBuffer buffer) {
		invitedPlayerId = getString(buffer);
		unionName = getString(buffer);
	}

	public String getUnionName() {
		return unionName;
	}

	public void setUnionName(String unionName) {
		this.unionName = unionName;
	}

	public String getInvitedPlayerId() {
		return invitedPlayerId;
	}

	public void setInvitedPlayerId(String invitedPlayerId) {
		this.invitedPlayerId = invitedPlayerId;
	}

}
