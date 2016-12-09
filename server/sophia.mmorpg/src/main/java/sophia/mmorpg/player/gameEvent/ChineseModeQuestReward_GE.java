package sophia.mmorpg.player.gameEvent;

public class ChineseModeQuestReward_GE {
	private short orderEventId = 0;
	private String chineseModeValue;
	private String chineseModeTarget;
	private long count;
	private int number;
	
	public ChineseModeQuestReward_GE () {
		
	}

	public short getOrderEventId() {
		return orderEventId;
	}

	public void setOrderEventId(short orderEventId) {
		this.orderEventId = orderEventId;
	}

	public String getChineseModeValue() {
		return chineseModeValue;
	}

	public void setChineseModeValue(String chineseModeValue) {
		this.chineseModeValue = chineseModeValue;
	}

	public String getChineseModeTarget() {
		return chineseModeTarget;
	}

	public void setChineseModeTarget(String chineseModeTarget) {
		this.chineseModeTarget = chineseModeTarget;
	}

	public long getCount() {
		return count;
	}

	public void setCount(long count) {
		this.count = count;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

}
