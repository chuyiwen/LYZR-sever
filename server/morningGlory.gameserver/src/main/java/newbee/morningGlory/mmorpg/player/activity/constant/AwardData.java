package newbee.morningGlory.mmorpg.player.activity.constant;

public class AwardData implements Cloneable{
	private byte type;
	private byte state;

	public AwardData() {
		this.type = 0;
		this.state = 0;
	}

	public AwardData(byte type, byte state) {
		this.type = type;
		this.state = state;
	}
	
	public AwardData clone(){
		try {
			return (AwardData)super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}

	public boolean isReceived() {
		return this.state == AwardState.Received;
	}

	public void setReceived() {
		this.state = AwardState.Received;
	}

	public void setSure() {
		this.state = AwardState.Sure;
	}

	public byte getType() {
		return type;
	}

	public void setType(byte type) {
		this.type = type;
	}

	public byte getState() {
		return state;
	}

	public void setState(byte state) {
		this.state = state;
	}

}
