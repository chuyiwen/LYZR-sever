package sophia.mmorpg.player.property.event;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;
import sophia.foundation.property.PropertyDictionary;

public class G2C_OtherPlayer_Attribute extends ActionEventBase {
	private PropertyDictionary property;
	private String charId;

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		putString(buffer, charId);
		byte[] pdData = property.toByteArray();
		buffer.putInt(pdData.length);
		buffer.put(pdData);
		return buffer;
	}

	@Override
	public void unpackBody(IoBuffer arg0) {
		// TODO Auto-generated method stub

	}

	public PropertyDictionary getProperty() {
		return property;
	}

	public void setProperty(PropertyDictionary property) {
		this.property = property;
	}

	public String getCharId() {
		return charId;
	}

	public void setCharId(String charId) {
		this.charId = charId;
	}

}
