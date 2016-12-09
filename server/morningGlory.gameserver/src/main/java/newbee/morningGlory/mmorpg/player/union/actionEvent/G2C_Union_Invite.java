package newbee.morningGlory.mmorpg.player.union.actionEvent;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class G2C_Union_Invite extends ActionEventBase {
	private String invitePlayerId;
	private String invitePlayerName;
	private String unionName;
	private int level;

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		putString(buffer, invitePlayerId);
		putString(buffer, invitePlayerName);
		putString(buffer, unionName);
		buffer.putInt(level);
		return buffer;
	}

	@Override
	public void unpackBody(IoBuffer arg0) {
		// TODO Auto-generated method stub

	}

	public String getInvitePlayerId() {
		return invitePlayerId;
	}

	public void setInvitePlayerId(String invitePlayerId) {
		this.invitePlayerId = invitePlayerId;
	}

	public String getInvitePlayerName() {
		return invitePlayerName;
	}

	public void setInvitePlayerName(String invitePlayerName) {
		this.invitePlayerName = invitePlayerName;
	}

	public String getUnionName() {
		return unionName;
	}

	public void setUnionName(String unionName) {
		this.unionName = unionName;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

}
