package sophia.mmorpg.player.team.actionEvent.info;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class G2C_PlayerTeam_Modify extends ActionEventBase {
	private byte succeed;
	
	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		buffer.put(succeed);
		return buffer;
	}

	@Override
	public void unpackBody(IoBuffer buffer) {
	}

	public byte getSucceed() {
		return succeed;
	}

	public void setSucceed(byte succeed) {
		this.succeed = succeed;
	}
	
}