package sophia.mmorpg.player.itemBag.event;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class G2C_Item_SoltUnLock extends ActionEventBase {
	
	private int remainMins;
	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		
		buffer.putInt(remainMins);
		return buffer;
	}

	@Override
	public void unpackBody(IoBuffer arg0) {
		
	}
	
	@Override
	public String getName() {
		return "解锁格";
	}

	public int getRemainMins() {
		return remainMins;
	}

	public void setRemainMins(int remainMins) {
		this.remainMins = remainMins;
	}
}
