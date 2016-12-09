/**
 * 
 */
package sophia.foundation.communication.core.impl;

import org.apache.log4j.Logger;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.apache.mina.filter.codec.demux.MessageDecoder;
import org.apache.mina.filter.codec.demux.MessageDecoderResult;

import sophia.foundation.authentication.Identity;
import sophia.foundation.communication.core.ActionEventBase;
import sophia.foundation.communication.core.MessageFactory;
import sophia.foundation.util.ByteArrayReadWriteBuffer;
import sophia.foundation.util.DebugUtil;
import sophia.foundation.util.MonitorClientEvent;

public final class MessageDecoderImpl implements MessageDecoder {
	private static final Logger logger = Logger.getLogger(sophia.foundation.communication.core.impl.MessageDecoderImpl.class.getName());

	public MessageDecoderImpl() {

	}

	@Override
	public MessageDecoderResult decodable(IoSession session, IoBuffer in) {
		if (session == null || in == null)
			return MessageDecoderResult.NOT_OK;
		if (!session.isConnected())
			return MessageDecoderResult.NOT_OK;

		int currentBytes = in.remaining();
		if (currentBytes < 2) {
			return MessageDecoderResult.NEED_DATA;
		}

		int length = in.getUnsignedShort(in.position());
		if (length < 0 || length > MonitorClientEvent.getInstance().getMessageLengthLimit()) {
			logger.error("-------------------------------------------------------------");
			logger.error("decodable error, ip=" + session.getRemoteAddress().toString() + ", buffer.getHexDump=" + in.getHexDump(MonitorClientEvent.getInstance().getMessageLengthLimit()));
			logger.error("-------------------------------------------------------------");
			session.close(true);
			return MessageDecoderResult.NOT_OK;
		}

		if (currentBytes - 2 < length) {
			return MessageDecoderResult.NEED_DATA;
		}

		// if (!in.prefixedDataAvailable(2, 1024)) {
		// return MessageDecoderResult.NEED_DATA;
		// }

		return MessageDecoderResult.OK;
	}

	@Override
	public MessageDecoderResult decode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
		// if (session == null || in == null)
		// return MessageDecoderResult.NOT_OK;
		// if (!session.isConnected())
		// return MessageDecoderResult.NOT_OK;
		//
		if (in.remaining() < 2) {
			return MessageDecoderResult.NEED_DATA;
		}
		//
		// if (!in.prefixedDataAvailable(2)) {
		// return MessageDecoderResult.NOT_OK;
		// }
		short msgLength = in.getShort();
		if (in.remaining() < msgLength) {
			return MessageDecoderResult.NEED_DATA;
		} else if (msgLength > MonitorClientEvent.getInstance().getMessageLengthLimit()) {
			logger.error("message is error, close session, messageData.length = " + msgLength);
			session.close(true);
			return MessageDecoderResult.NOT_OK;
		}
		byte[] messageData;
		byte[] data = new byte[msgLength];
		in.get(data);
		messageData = data;
		IoBuffer buffer = IoBuffer.allocate(messageData.length);
		buffer.put(messageData);
		buffer.flip();
		short messageId = -1;
		byte zip = -1;
		String perGetShort = "";
		try {
			perGetShort = buffer.getHexDump();
			zip = buffer.get();
			messageId = buffer.getShort();
		} catch (Exception ex) {
			logger.error("-------------------------------------------------------------");
			logger.error("messageData.length = " + messageData.length + " postGetShort buffer.getHexDump=" + perGetShort + " postGetShort buffer.getHexDump=" + buffer.getHexDump()
					+ " msgLength:" + msgLength);
			logger.error("-------------------------------------------------------------");
			session.close(true);
			throw ex;
		}
		if (logger.isDebugEnabled()) {
			logger.debug("message id " + messageId + " message data " + buffer.getHexDump());
		}

		ActionEventBase message = MessageFactory.getMessage(messageId);
		if (message != null) {
			Identity identity = (Identity) session.getAttribute("identity");
			if (identity != null) {
				message.setIdentity(identity);
				if (logger.isDebugEnabled()) {
					logger.debug("getIdentity:" + message.getIdentity().getName());
				}
			} 
		} else {
			logger.error("can't find message object, close session, messageId=" + messageId);
			session.close(true);
			return MessageDecoderResult.NOT_OK;
		}
		
		message.setZiped(zip);
		
		try {
			message.unpackFromBuffer(buffer);
		} catch (Exception ex) {
			logger.error("message decode failure, close session, messageId=" + messageId);
			logger.error(DebugUtil.printStack(ex));
			session.close(true);
			throw ex;
		}
		
		message.setActionEventId(messageId);
		try {
			out.write(message);
		} catch (Exception ex) {
			logger.error("message deliver failure, close session, messageId=" + messageId);
			logger.error(DebugUtil.printStack(ex));
			session.close(true);
			throw ex;
		}

		if (MonitorClientEvent.getInstance().isEnabled()) {
			long now = System.currentTimeMillis();
			long nTime = 0;
			if (MonitorClientEvent.getInstance().getLastTime() != 0) {
				nTime = now - MonitorClientEvent.getInstance().getLastTime();
			}

			ByteArrayReadWriteBuffer buf = new ByteArrayReadWriteBuffer();
			buf.writeLong(nTime);
			buf.writeInt(messageData.length);
			buf.writeBytes(messageData);

			MonitorClientEvent.getInstance().addEvent(message, buf.getData());
			MonitorClientEvent.getInstance().setLastTime(now);
		}

		return MessageDecoderResult.OK;
	}

	@Override
	public void finishDecode(IoSession session, ProtocolDecoderOutput out) throws Exception {

	}
}
