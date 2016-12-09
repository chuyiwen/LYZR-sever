package newbee.morningGlory.mmorpg.operatActivities.event;

import java.util.Set;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class G2C_OA_CanReceiveEvent extends ActionEventBase {

	@Override
	public void unpackBody(IoBuffer buffer) {
	}

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		short count = (short) types.size();
		buffer.putShort(count);
		for (Integer i : types) {
			buffer.putShort(i.shortValue());
		}
		return buffer;
	}

	private Set<Integer> types;

	public Set<Integer> getTypes() {
		return types;
	}

	public void setTypes(Set<Integer> types) {
		this.types = types;
	}

}
