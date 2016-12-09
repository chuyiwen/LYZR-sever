package newbee.morningGlory.mmorpg.player.activity.resDownload;

public class MGResDownLoadData {
	
	
	/** 未领取 */
	public static final byte NotReceive = 0;
	/** 已领取 */
	public static final byte HasReceive = 1;
	
	private byte isResDownloadReceive = NotReceive;
	
	private String rewardId = "";
	
	private String identityName = "";
	
	private String rewardPlayerName = "";

	public String getIdentityName() {
		return identityName;
	}

	public void setIdentityName(String identityName) {
		this.identityName = identityName;
	}

	public String getRewardId() {
		return rewardId;
	}

	public void setRewardId(String rewardId) {
		this.rewardId = rewardId;
	}

	public String getRewardPlayerName() {
		return rewardPlayerName;
	}

	public void setRewardPlayerName(String rewardPlayerName) {
		this.rewardPlayerName = rewardPlayerName;
	}

	public byte getIsResDownloadReceive() {
		return isResDownloadReceive;
	}

	public void setIsResDownloadReceive(byte isResDownloadReceive) {
		this.isResDownloadReceive = isResDownloadReceive;
	}

	@Override
	public String toString() {
		return "MGResDownLoadData [isResDownloadReceive=" + isResDownloadReceive + ", rewardId=" + rewardId + ", identityName=" + identityName + ", rewardPlayerName="
				+ rewardPlayerName + "]";
	}

	
	
}
