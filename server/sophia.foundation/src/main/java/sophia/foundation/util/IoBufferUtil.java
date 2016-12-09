package sophia.foundation.util;

import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

import org.apache.mina.core.buffer.IoBuffer;



public final class IoBufferUtil {

	private static final CharsetDecoder decoder = Charset.forName("UTF-8").newDecoder();
	
	public static IoBuffer putString( IoBuffer ioBuffer , String s )
	{
		try {
			if (s == null || s.equals("")) {
				ioBuffer.putShort((short) 0);
				return ioBuffer;
			}
			byte[] b = s.getBytes("UTF-8");
			ioBuffer.putShort((short) b.length);
			ioBuffer.put(b);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return ioBuffer;
	}
	
	public static String getString(IoBuffer ioBuffer)
	{
		short length = ioBuffer.getShort();
		try {
			return ioBuffer.getString(length, decoder);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	
	private static final byte TRUE = 1;
	private static final byte FALSE = 0;
	public static boolean getBoolean(IoBuffer in) {
		byte value = in.get();
		if (value == TRUE) {
			return true;
		}
		if (value == FALSE) {
			return false;
		}
		throw new IllegalArgumentException("无效的boolean预定义值:" + value);
	}
	
	public static void putBoolean(IoBuffer out, boolean value) {
		byte v;
		if (value) {
			v = TRUE;
		} else {
			v = FALSE;
		}
		out.put(v);
	}
}
