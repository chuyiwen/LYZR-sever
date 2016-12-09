package sophia.mmorpg.player.gameEvent;

public class ChineseModeQuest_GE {
	public static byte AcceptType = 1;
	public static byte CourseType = 2;
	private byte type = 0;
	private short orderEventId = 0;
	private String chineseModeValue;
	private String chineseModeTarget;
	private long count;
	private int number;

	public ChineseModeQuest_GE() {

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

	public short getOrderEventId() {
		return orderEventId;
	}

	public void setOrderEventId(short orderEventId) {
		this.orderEventId = orderEventId;
	}

	public byte getType() {
		return type;
	}

	public void setType(byte type) {
		this.type = type;
	}

}
