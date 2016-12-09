package newbee.morningGlory.mmorpg.sceneActivities.castleWar.ref;

import sophia.game.ref.AbstractGameRefObjectBase;

public class CastleWarRef extends AbstractGameRefObjectBase {
	private static final long serialVersionUID = 4939265624833744459L;
	private String giftRefID;
	private CastleWarInstanceTransfer castleWarInstanceTransfer;
	private CastleWarOutSceneTransfer castleWarOutSceneTransfer;
	private CastleWarSceneTransfer castleWarSceneTransfer;
	private String startApplyTime;
	private String endApplyTime;
	
	private int firstIntervalDays;
	private int rangeIntervalDays;
	private String openAndEndTime;
	
	
	public String getStartApplyTime() {
		return startApplyTime;
	}
	public void setStartApplyTime(String startApplyTime) {
		this.startApplyTime = startApplyTime;
	}
	public String getEndApplyTime() {
		return endApplyTime;
	}
	public void setEndApplyTime(String endApplyTime) {
		this.endApplyTime = endApplyTime;
	}
	public String getGiftRefID() {
		return giftRefID;
	}
	public void setGiftRefID(String giftRefID) {
		this.giftRefID = giftRefID;
	}
	public CastleWarInstanceTransfer getCastleWarInstanceTransfer() {
		return castleWarInstanceTransfer;
	}
	public void setCastleWarInstanceTransfer(CastleWarInstanceTransfer castleWarInstanceTransfer) {
		this.castleWarInstanceTransfer = castleWarInstanceTransfer;
	}
	public CastleWarOutSceneTransfer getCastleWarOutSceneTransfer() {
		return castleWarOutSceneTransfer;
	}
	public void setCastleWarOutSceneTransfer(CastleWarOutSceneTransfer castleWarOutSceneTransfer) {
		this.castleWarOutSceneTransfer = castleWarOutSceneTransfer;
	}
	public CastleWarSceneTransfer getCastleWarSceneTransfer() {
		return castleWarSceneTransfer;
	}
	public void setCastleWarSceneTransfer(CastleWarSceneTransfer castleWarSceneTransfer) {
		this.castleWarSceneTransfer = castleWarSceneTransfer;
	}
	public int getFirstIntervalDays() {
		return firstIntervalDays;
	}
	public void setFirstIntervalDays(int firstIntervalDays) {
		this.firstIntervalDays = firstIntervalDays;
	}
	public int getRangeIntervalDays() {
		return rangeIntervalDays;
	}
	public void setRangeIntervalDays(int rangeIntervalDays) {
		this.rangeIntervalDays = rangeIntervalDays;
	}
	public String getOpenAndEndTime() {
		return openAndEndTime;
	}
	public void setOpenAndEndTime(String openAndEndTime) {
		this.openAndEndTime = openAndEndTime;
	}
}
