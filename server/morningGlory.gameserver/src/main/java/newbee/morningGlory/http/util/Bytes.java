package newbee.morningGlory.http.util;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class Bytes {
	public static short bytes2short(byte[] b) {
		return bytes2short(b, null);
	}

	public static short bytes2short(byte[] b, ByteOrder bo) {
		ByteBuffer buffer = ByteBuffer.allocate(2);
		if (bo != null && !bo.equals(buffer.order()))
			buffer.order(bo);
		buffer.put(b);
		buffer.flip();
		return buffer.getShort();
	}

	public static byte[] short2bytes(short s) {
		return short2bytes(s, null);
	}

	public static byte[] short2bytes(short s, ByteOrder bo) {
		ByteBuffer buffer = ByteBuffer.allocate(2);
		if (bo != null && !bo.equals(buffer.order()))
			buffer.order(bo);
		buffer.putShort(s);
		buffer.flip();
		return buffer.array();
	}

	public static float bytes2float(byte[] b) {
		return bytes2float(b, null);
	}

	public static float bytes2float(byte[] b, ByteOrder bo) {
		ByteBuffer buffer = ByteBuffer.allocate(4);
		if (bo != null && !bo.equals(buffer.order()))
			buffer.order(bo);
		buffer.put(b);
		buffer.flip();
		return buffer.getFloat();
	}

	public static byte[] float2bytes(float f) {
		return float2bytes(f, null);
	}

	public static byte[] float2bytes(float f, ByteOrder bo) {
		ByteBuffer buffer = ByteBuffer.allocate(4);
		if (bo != null && !bo.equals(buffer.order()))
			buffer.order(bo);
		buffer.putFloat(f);
		buffer.flip();
		return buffer.array();
	}

	public static double bytes2double(byte[] b) {
		return bytes2double(b, null);
	}

	public static double bytes2double(byte[] b, ByteOrder bo) {
		ByteBuffer buffer = ByteBuffer.allocate(8);
		if (bo != null && !bo.equals(buffer.order()))
			buffer.order(bo);
		buffer.put(b);
		buffer.flip();
		return buffer.getDouble();
	}

	public static byte[] double2bytes(double d) {
		return double2bytes(d, null);
	}

	public static byte[] double2bytes(double d, ByteOrder bo) {
		ByteBuffer buffer = ByteBuffer.allocate(8);
		if (bo != null && !bo.equals(buffer.order()))
			buffer.order(bo);
		buffer.putDouble(d);
		buffer.flip();
		return buffer.array();
	}

	public static char bytes2char(byte[] b) {
		return bytes2char(b, null);
	}

	public static char bytes2char(byte[] b, ByteOrder bo) {
		ByteBuffer buffer = ByteBuffer.allocate(2);
		if (bo != null && !bo.equals(buffer.order()))
			buffer.order(bo);
		buffer.put(b);
		buffer.flip();
		return buffer.getChar();
	}

	public static byte[] char2bytes(char c) {
		return char2bytes(c, null);
	}

	public static byte[] char2bytes(char c, ByteOrder bo) {
		ByteBuffer buffer = ByteBuffer.allocate(2);
		if (bo != null && !bo.equals(buffer.order()))
			buffer.order(bo);
		buffer.putChar(c);
		buffer.flip();
		return buffer.array();
	}

	public static byte[] int2bytes(int i) {
		return int2bytes(i, null);
	}

	public static byte[] int2bytes(int i, ByteOrder bo) {
		ByteBuffer buffer = ByteBuffer.allocate(4);
		if (bo != null && !bo.equals(buffer.order()))
			buffer.order(bo);
		buffer.putInt(i);
		buffer.flip();
		return buffer.array();
	}

	public static int bytes2int(byte[] b) {
		return bytes2int(b, null);
	}

	public static int bytes2int(byte[] b, ByteOrder bo) {
		ByteBuffer buffer = ByteBuffer.allocate(4);
		if (bo != null && !bo.equals(buffer.order()))
			buffer.order(bo);
		buffer.put(b);
		buffer.flip();
		return buffer.getInt();
	}

	public static byte[] long2bytes(long l) {
		return long2bytes(l, null);
	}

	public static byte[] long2bytes(long l, ByteOrder bo) {
		ByteBuffer buffer = ByteBuffer.allocate(8);
		if (bo != null && !bo.equals(buffer.order()))
			buffer.order(bo);
		buffer.putLong(l);
		buffer.flip();
		return buffer.array();
	}

	public static long bytes2long(byte[] b) {
		return bytes2long(b, null);
	}

	public static long bytes2long(byte[] b, ByteOrder bo) {
		ByteBuffer buffer = ByteBuffer.allocate(8);
		if (bo != null && !bo.equals(buffer.order()))
			buffer.order(bo);
		buffer.put(b);
		buffer.flip();
		return buffer.getLong();
	}

	/**
	 * 将字节数组全置0
	 */
	public static void zeroBytes(byte[] data) {
		int j = data.length;
		int i = 0;
		setBytes(data, i, j, (byte) 0);
	}

	/**
	 * 将字节数组指定位置后全置0
	 * 
	 * @param data
	 * @param offset
	 */
	public static void zeroBytes(byte[] data, int offset) {
		int i = data.length - offset;
		setBytes(data, offset, i, (byte) 0);
	}

	/**
	 * 将字节数组部分置0
	 * 
	 * @param data
	 * @param offset
	 * @param len
	 */
	public static void zeroBytes(byte[] data, int offset, int len) {
		setBytes(data, offset, len, (byte) 0);
	}

	/**
	 * 将字节数组全部置为指定值
	 * 
	 * @param data
	 * @param value
	 */
	public static void setBytes(byte[] data, byte value) {
		setBytes(data, 0, data.length, value);
	}

	/**
	 * 将字节数组指定位置后置为指定值
	 * 
	 * @param data
	 * @param offset
	 * @param value
	 */
	public static void setBytes(byte[] data, int offset, byte value) {
		setBytes(data, offset, data.length - offset, value);
	}

	/**
	 * 将字节数组部分置为指定值
	 * 
	 * @param data
	 * @param offset
	 * @param len
	 * @param value
	 */
	public static void setBytes(byte[] data, int offset, int len, byte value) {
		for (int i = offset; i < len + offset; ++i)
			data[i] = value;
	}

	/**
	 * 取字节数组子数组
	 * 
	 * @param data
	 * @param offset
	 * @param len
	 * @return
	 */
	public static byte[] subBytes(byte[] data, int offset, int len) {
		byte[] bb = new byte[len];
		return subBytes(data, offset, bb);
	}

	public static byte[] subBytes(byte[] data, int offset) {
		return subBytes(data, offset, data.length - offset);
	}

	public static byte[] subBytes(byte[] data, int offset, byte[] bb) {
		int len = bb.length;
		if (data.length - offset >= len)
			System.arraycopy(data, offset, bb, 0, len);
		else {
			System.arraycopy(data, offset, bb, 0, data.length - offset);
			zeroBytes(bb, data.length - offset);
		}
		return bb;
	}

	public static byte[] joinBytes(byte[] bs1, byte[] bs2) {
		return joinBytes(bs1, 0, bs1.length, bs2, 0, bs2.length);
	}

	public static byte[] joinBytes(byte[] bs1, int offset1, int len1, byte[] bs2, int offset2, int len2) {
		byte[] bs = new byte[len1 + len2];
		bytesCopy(bs1, offset1, bs, 0, len1);
		bytesCopy(bs2, offset2, bs, len1, len2);
		return bs;
	}

	public static void bytesCopy(byte[] src, byte[] dst, int len) {
		System.arraycopy(src, 0, dst, 0, len);
	}

	public static void bytesCopy(byte[] src, int srcOffset, byte[] dst, int dstOffset, int len) {
		System.arraycopy(src, srcOffset, dst, dstOffset, len);
	}

	public static boolean bytesEquals(byte[] b1, byte[] b2) {
		if (b1 == b2)
			return true;
		if (b1 == null || b2 == null || b1.length != b2.length)
			return false;
		return bytesEquals(b1, 0, b2, 0, b1.length);
	}

	public static boolean bytesEquals(byte[] b1, int offset1, byte[] b2, int offset2) {
		if (b1 != b2 && (b1 == null || b2 == null))
			return false;
		if (b1.length - offset1 != b2.length - offset2)
			return false;
		return bytesEquals(b1, offset1, b2, offset2, b1.length - offset1);
	}

	public static boolean bytesEquals(byte[] b1, int offset1, byte[] b2, int offset2, int len) {
		if (b1 != b2 && (b1 == null || b2 == null))
			return false;
		if ((b1.length < offset1 + len) || (b2.length < offset2 + len))
			return false;
		for (int i = 0; i < len; ++i) {
			if (b1[(offset1 + i)] != b2[(offset2 + i)])
				return false;
		}
		return true;
	}

	public static byte[] stringToBytes(String s, String charset, int length) throws UnsupportedEncodingException {
		byte[] arr = new byte[length];
		zeroBytes(arr);
		if (s == null || s.length() == 0)
			return arr;
		if (charset == null || charset.length() == 0)
			arr = s.getBytes();
		else
			arr = s.getBytes(charset);
		bytesCopy(arr, arr, Math.min(length, s.length()));
		return arr;
	}

	public static byte[] stringToBytes(String s, int len) {
		try {
			return stringToBytes(s, null, len);
		} catch (UnsupportedEncodingException e) {
		}
		return null;
	}

	public static String bytesToString(byte[] bb, String charset) throws UnsupportedEncodingException {
		if (bb == null)
			return null;
		int i = 0;
		for (; i < bb.length; ++i)
			if (bb[i] == 0)
				break;
		if (charset == null || charset.length() == 0)
			return new String(bb, 0, i);
		return new String(bb, 0, i, charset);
	}

	public static String bytesToString(byte[] bb) {
		try {
			return bytesToString(bb, null);
		} catch (UnsupportedEncodingException e) {
		}
		return null;
	}

	private static final char[] DIGITS = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

	public static String toHexString(byte... bytes) {
		char[] buffer = new char[bytes.length * 2];
		for (int i = 0, j = 0; i < bytes.length; ++i) {
			int u = toUnsigned(bytes[i]);
			buffer[j++] = DIGITS[u >>> 4];
			buffer[j++] = DIGITS[u & 0xf];
		}
		return new String(buffer);
	}

	public static String toBinaryString(byte... bytes) {
		char[] buffer = new char[bytes.length * 8];
		for (int i = 0, j = 0; i < bytes.length; ++i) {
			int u = toUnsigned(bytes[i]);
			buffer[j++] = DIGITS[(u >>> 7) & 0x1];
			buffer[j++] = DIGITS[(u >>> 6) & 0x1];
			buffer[j++] = DIGITS[(u >>> 5) & 0x1];
			buffer[j++] = DIGITS[(u >>> 4) & 0x1];
			buffer[j++] = DIGITS[(u >>> 3) & 0x1];
			buffer[j++] = DIGITS[(u >>> 2) & 0x1];
			buffer[j++] = DIGITS[(u >>> 1) & 0x1];
			buffer[j++] = DIGITS[u & 0x1];
		}
		return new String(buffer);
	}

	private static int toUnsigned(byte b) {
		return b < 0 ? b + 256 : b;
	}
}
