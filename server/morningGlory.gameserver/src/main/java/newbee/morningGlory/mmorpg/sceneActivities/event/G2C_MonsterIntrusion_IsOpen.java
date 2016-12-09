package newbee.morningGlory.mmorpg.sceneActivities.event;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class G2C_MonsterIntrusion_IsOpen extends ActionEventBase {

	private boolean isOpen;
	
	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		byte is = 0;// 没有开始
		if (isOpen) {
			is = 1;// 开始
		}
		buffer.put(is);
		return buffer;
	}

	@Override
	public void unpackBody(IoBuffer arg0) {
	}

	public boolean getIsOpen() {
		return isOpen;
	}

	public void setIsOpen(boolean isOpen) {
		this.isOpen = isOpen;
	}
	
}
