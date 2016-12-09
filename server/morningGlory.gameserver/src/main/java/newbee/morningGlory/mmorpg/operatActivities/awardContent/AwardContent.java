package newbee.morningGlory.mmorpg.operatActivities.awardContent;

import java.util.List;

/**
 * 运营活动奖励内容<br>
 */
public class AwardContent {
	private AwardSendType sendType;
	private String desc;
	private String otherData;
	private List<AwardItem> awardItems;

	public AwardSendType getSendType() {
		return sendType;
	}

	public void setSendType(AwardSendType sendType) {
		this.sendType = sendType;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getOtherData() {
		return otherData;
	}

	public void setOtherData(String otherData) {
		this.otherData = otherData;
	}

	public List<AwardItem> getAwardItems() {
		return awardItems;
	}

	public void setAwardItems(List<AwardItem> awardItems) {
		this.awardItems = awardItems;
	}

	public AwardItem getAwardItem(String id) {
		for (AwardItem awardItem : awardItems) {
			if (awardItem.getId().equals(id))
				return awardItem;
		}
		return null;
	}

}
