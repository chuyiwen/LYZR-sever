package newbee.morningGlory.mmorpg.player.union.actionEvent;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class C2G_Union_HandleApply extends ActionEventBase {
	private String applyPlayerId;
	private String unionName;
	private byte vote;

	@Override
	protected IoBuffer packBody(IoBuffer arg0) {
		return null;
	}

	@Override
	public void unpackBody(IoBuffer buffer) {
		applyPlayerId = getString(buffer);
		unionName = getString(buffer);
		vote = buffer.get();
	}

	public String getApplyPlayerId() {
		return applyPlayerId;
	}

	public void setApplyPlayerId(String applyPlayerId) {
		this.applyPlayerId = applyPlayerId;
	}

	public byte getVote() {
		return vote;
	}

	public void setVote(byte vote) {
		this.vote = vote;
	}

	public String getUnionName() {
		return unionName;
	}

	public void setUnionName(String unionName) {
		this.unionName = unionName;
	}

}
