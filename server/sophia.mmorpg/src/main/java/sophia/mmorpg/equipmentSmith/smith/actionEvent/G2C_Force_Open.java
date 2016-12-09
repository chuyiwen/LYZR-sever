/**
 * 
 */
package sophia.mmorpg.equipmentSmith.smith.actionEvent;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

/**
 * @author Administrator
 *
 */
public class G2C_Force_Open extends ActionEventBase{
	
	private byte isStrengOpen;
	
	private byte isWashOpen;
	
	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		buffer.put(isStrengOpen);
		buffer.put(isWashOpen);
		return buffer;
	}

	@Override
	public void unpackBody(IoBuffer arg0) {
		
	}

	public byte getIsStrengOpen() {
		return isStrengOpen;
	}

	public void setIsStrengOpen(byte isStrengOpen) {
		this.isStrengOpen = isStrengOpen;
	}

	public byte getIsWashOpen() {
		return isWashOpen;
	}

	public void setIsWashOpen(byte isWashOpen) {
		this.isWashOpen = isWashOpen;
	}

	
}
