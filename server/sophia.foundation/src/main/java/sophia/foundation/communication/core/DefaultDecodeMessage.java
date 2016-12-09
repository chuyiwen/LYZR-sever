/**
 * 
 */
package sophia.foundation.communication.core;

import org.apache.mina.core.buffer.IoBuffer;



public class DefaultDecodeMessage extends ActionEventBase {
	public static final short MessageId = Short.MAX_VALUE;
	
	private String messageContext = "1234";
	
	public DefaultDecodeMessage() {
		setActionEventId(MessageId);
	}
	
	public String printString() {
		return "It's my message.:)";
	}
	
	@Override
	public void unpackBody(IoBuffer buffer) {
		messageContext = buffer.getHexDump();;
	}

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		putString(buffer, messageContext);
		return buffer;
	}

	@Override
	public int getPriority() {
		return Immediately_Priority;
	}

	@Override
	public String toString() {
		return "DefaultDecodeMessage [actionEventId="
				+ actionEventId
				+ ", "
				+ (messageContext != null ? "messageContext=" + messageContext
						: "") + "]";
	}
}
