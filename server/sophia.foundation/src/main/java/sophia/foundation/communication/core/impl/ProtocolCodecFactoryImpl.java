/**
 * 
 */
package sophia.foundation.communication.core.impl;


import org.apache.mina.filter.codec.demux.DemuxingProtocolCodecFactory;

import sophia.foundation.communication.core.ActionEventBase;


public final class ProtocolCodecFactoryImpl extends DemuxingProtocolCodecFactory {
	public ProtocolCodecFactoryImpl() {
		super.addMessageDecoder(MessageDecoderFilter.class);
		super.addMessageEncoder(ActionEventBase.class, MessageEncoderImpl.class);
	}
}
