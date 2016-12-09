package sophia.foundation.communication.core.impl;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.log4j.Logger;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.apache.mina.filter.codec.demux.MessageDecoder;
import org.apache.mina.filter.codec.demux.MessageDecoderResult;

/**
 * Copyright (c) 2013 by 游爱.
 * 
 */

public class MessageDecoderFilter implements MessageDecoder {

	private static final Logger logger = Logger.getLogger(MessageDecoderFilter.class);
	private static boolean speedingFilterEnable = true;
	private static int speedingLimit = 200;
	private static int speedingPeriodTime = 10 * 1000; // 毫秒

	public static void setSpeedingFilterEnable(boolean enable) {
		speedingFilterEnable = enable;
	}

	public static void setSpeedingLimit(int limit) {
		speedingLimit = limit;
	}

	public static void setSpeedingPeriod(int period) {
		speedingPeriodTime = period;
	}

	private MessageDecoder messageDecoder = new MessageDecoderImpl();

	@Override
	public MessageDecoderResult decodable(IoSession session, IoBuffer in) {
		return messageDecoder.decodable(session, in);
	}

	@Override
	public MessageDecoderResult decode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
		try {
			MessageDecoderResult decode = messageDecoder.decode(session, in, out);
			if (speedingFilterEnable && decode==MessageDecoderResult.OK)
				onDecode(session);
			return decode;
		} catch (Exception ex) {
			logger.error("非法数据包!\r\n", ex);
			throw ex;
		}
	}

	@Override
	public void finishDecode(IoSession session, ProtocolDecoderOutput out) throws Exception {
		messageDecoder.finishDecode(session, out);
	}

	private void onDecode(IoSession session) {
		long now = System.currentTimeMillis();
		AtomicLong time = (AtomicLong) session.getAttribute("SpeedingPeriodTime");
		AtomicInteger count = (AtomicInteger) session.getAttribute("SpeedingCount");
		AtomicInteger isSpeedUp = (AtomicInteger) session.getAttribute("isSpeedUp");
		if (time == null || count == null || isSpeedUp==null) {
			time = new AtomicLong(now);
			count = new AtomicInteger(0);
			isSpeedUp = new AtomicInteger(0);
			session.setAttribute("SpeedingPeriodTime", time);
			session.setAttribute("SpeedingCount", count);
			session.setAttribute("isSpeedUp", isSpeedUp);
		}

		if (now / speedingPeriodTime == time.get() / speedingPeriodTime) {
			count.addAndGet(1);
			if (count.get() >= speedingLimit) {
				logger.info("speeding="+count.get());
				isSpeedUp.set(1);
			}
		} else {
			time.set(now);
			count.set(1);
			isSpeedUp.set(0);
		}
	}

}
