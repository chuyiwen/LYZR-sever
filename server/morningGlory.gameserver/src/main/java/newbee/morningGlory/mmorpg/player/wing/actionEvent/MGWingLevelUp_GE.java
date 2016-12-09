package newbee.morningGlory.mmorpg.player.wing.actionEvent;

public class MGWingLevelUp_GE {
	private String wingRefId;
//	private byte crtStageLevel;
//	private byte crtStarLevel;
//	private long exp;

	public MGWingLevelUp_GE() {

	}

	public MGWingLevelUp_GE(String wingRefId) {
		this.wingRefId = wingRefId;
//		this.crtStageLevel = crtStageLevel;
//		this.crtStarLevel = crtStarLevel;
//		this.exp = exp;
	}

//	public int getCrtStageLevel() {
//		return crtStageLevel;
//	}
//
//	public void setCrtStageLevel(byte crtStageLevel) {
//		this.crtStageLevel = crtStageLevel;
//	}

	public String getWingRefId() {
		return wingRefId;
	}

	public void setWingRefId(String wingRefId) {
		this.wingRefId = wingRefId;
	}

//	public byte getCrtStarLevel() {
//		return crtStarLevel;
//	}
//
//	public void setCrtStarLevel(byte crtStarLevel) {
//		this.crtStarLevel = crtStarLevel;
//	}

//	public long getExp() {
//		return exp;
//	}
//
//	public void setExp(long exp) {
//		this.exp = exp;
//	}

}
