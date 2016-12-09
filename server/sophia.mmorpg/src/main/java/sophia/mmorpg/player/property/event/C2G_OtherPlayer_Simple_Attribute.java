package sophia.mmorpg.player.property.event;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class C2G_OtherPlayer_Simple_Attribute extends ActionEventBase {
	private String charId;

	@Override
	public void unpackBody(IoBuffer buffer) {
		charId = getString(buffer);
	}

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		putString(buffer, charId);
		return buffer;
	}

	public String getCharId() {
		return charId;
	}

	public void setCharId(String charId) {
		this.charId = charId;
	}
}
