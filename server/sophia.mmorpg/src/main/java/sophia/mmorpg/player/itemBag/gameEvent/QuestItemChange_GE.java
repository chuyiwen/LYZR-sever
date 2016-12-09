package sophia.mmorpg.player.itemBag.gameEvent;

public class QuestItemChange_GE {
	
	private String questItemRefId;
	private int curNumber;
	private byte optType;	// 0 = 减  ;   1 = 加
	
	public QuestItemChange_GE(String questItemRefId, int curNumber,byte optType) {
		this.questItemRefId = questItemRefId;
		this.curNumber = curNumber;
		this.optType = optType;
	}
	public String getQuestItemRefId() {
		return questItemRefId;
	}
	public void setQuestItemRefId(String questItemRefId) {
		this.questItemRefId = questItemRefId;
	}
	public int getCurNumber() {
		return curNumber;
	}
	public void setCurNumber(int curNumber) {
		this.curNumber = curNumber;
	}
	public byte getOptType() {
		return optType;
	}
	public void setOptType(byte optType) {
		this.optType = optType;
	}
	
}
