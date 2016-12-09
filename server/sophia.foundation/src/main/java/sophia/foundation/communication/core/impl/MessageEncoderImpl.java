/**
 * 
 */
package sophia.foundation.communication.core.impl;


import org.apache.log4j.Logger;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.apache.mina.filter.codec.demux.MessageEncoder;

import sophia.foundation.communication.core.ActionEventBase;


public final class MessageEncoderImpl<T extends ActionEventBase> implements MessageEncoder<T> {
	private static final Logger logger = Logger.getLogger(MessageEncoderImpl.class.getName());
	
	public MessageEncoderImpl() {
		
	}
	
//	private static long sendCount=0;
//	private static long time=System.currentTimeMillis();
	
	@Override
	public void encode(IoSession session, T message, ProtocolEncoderOutput out) throws Exception {
		IoBuffer ioBuffer = message.packToBuffer();
		
		if (logger.isDebugEnabled()) {
			logger.debug("send data: " + ioBuffer.getHexDump());
		}
		
		out.write(ioBuffer);
		
		// if(sendCount++%1000==0)
		// System.out.println("sendCount:"+sendCount+"----"+(System.currentTimeMillis()-time));
	}
}
