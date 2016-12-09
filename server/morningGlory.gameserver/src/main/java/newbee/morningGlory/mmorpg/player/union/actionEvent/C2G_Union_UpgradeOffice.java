package newbee.morningGlory.mmorpg.player.union.actionEvent;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class C2G_Union_UpgradeOffice extends ActionEventBase {
	private String upgradePlayerId;
	private String unionName;
	private byte officialId;

	@Override
	protected IoBuffer packBody(IoBuffer arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void unpackBody(IoBuffer buffer) {
		upgradePlayerId = getString(buffer);
		unionName = getString(buffer);
		officialId = buffer.get();
	}

	public String getUpgradePlayerId() {
		return upgradePlayerId;
	}

	public void setUpgradePlayerId(String upgradePlayerId) {
		this.upgradePlayerId = upgradePlayerId;
	}

	public String getUnionName() {
		return unionName;
	}

	public void setUnionName(String unionName) {
		this.unionName = unionName;
	}

	public byte getOfficialId() {
		return officialId;
	}

	public void setOfficialId(byte officialId) {
		this.officialId = officialId;
	}

}
