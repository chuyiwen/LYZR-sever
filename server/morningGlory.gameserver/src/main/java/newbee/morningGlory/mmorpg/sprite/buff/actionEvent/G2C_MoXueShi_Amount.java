package newbee.morningGlory.mmorpg.sprite.buff.actionEvent;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class G2C_MoXueShi_Amount extends ActionEventBase {
	private String buffRefId;
	private long createTime;
	private int amount;
	
	

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		putString(buffer, buffRefId);
		buffer.putLong(createTime);
		buffer.putInt(amount);
		return buffer;
	}

	@Override
	public void unpackBody(IoBuffer buffer) {
		

	}

	public String getBuffRefId() {
		return buffRefId;
	}

	public void setBuffRefId(String buffRefId) {
		this.buffRefId = buffRefId;
	}

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}
}
