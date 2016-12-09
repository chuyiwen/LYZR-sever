package newbee.morningGlory.mmorpg.player.union.actionEvent;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class G2C_Union_ReplyInvite extends ActionEventBase {
	private String playerName;
	private byte reply;

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		putString(buffer, playerName);
		buffer.put(reply);
		return buffer;
	}

	@Override
	public void unpackBody(IoBuffer arg0) {
		// TODO Auto-generated method stub

	}

	public String getPlayerName() {
		return playerName;
	}

	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}

	public byte getReply() {
		return reply;
	}

	public void setReply(byte reply) {
		this.reply = reply;
	}

}
