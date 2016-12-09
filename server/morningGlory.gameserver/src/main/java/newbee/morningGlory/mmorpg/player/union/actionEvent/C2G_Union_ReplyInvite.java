package newbee.morningGlory.mmorpg.player.union.actionEvent;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class C2G_Union_ReplyInvite extends ActionEventBase {
	private String invitePlayerId;
	private String unionName;
	private byte reply;

	@Override
	protected IoBuffer packBody(IoBuffer arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void unpackBody(IoBuffer buffer) {
		invitePlayerId = getString(buffer);
		unionName = getString(buffer);
		reply = buffer.get();
	}

	public String getInvitePlayerId() {
		return invitePlayerId;
	}

	public void setInvitePlayerId(String invitePlayerId) {
		this.invitePlayerId = invitePlayerId;
	}

	public byte getReply() {
		return reply;
	}

	public void setReply(byte reply) {
		this.reply = reply;
	}

	public String getUnionName() {
		return unionName;
	}

	public void setUnionName(String unionName) {
		this.unionName = unionName;
	}

}
