package newbee.morningGlory.mmorpg.player.peerage.gameEvent;

public class MGPeerageLevelUp_GE {
	private byte peerageLevel;
	private String refId;

	public MGPeerageLevelUp_GE() {

	}

	public MGPeerageLevelUp_GE(byte peerageLevel, String refId) {
		this.peerageLevel = peerageLevel;
		this.refId = refId;
	}

	public byte getPeerageLevel() {
		return peerageLevel;
	}

	public void setPeerageLevel(byte peerageLevel) {
		this.peerageLevel = peerageLevel;
	}

	public String getRefId() {
		return refId;
	}

	public void setRefId(String refId) {
		this.refId = refId;
	}

}
