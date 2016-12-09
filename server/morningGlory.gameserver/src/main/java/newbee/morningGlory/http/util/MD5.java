package newbee.morningGlory.http.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class MD5 {


	/**
	 * 返回MD5摘要字串( 大写字串 )
	 * @param txt
	 * @return
	 */
	public static String digest(String txt) {
		MessageDigest md;
		try {

			md = MessageDigest.getInstance("MD5");
			md.update(txt.getBytes("UTF-8"));

			return toHex(md.digest());

		} catch (NoSuchAlgorithmException e) {
			return "";
		} catch (UnsupportedEncodingException e) {
			return "";
		}
	}
	
	/**
	 * 返回MD5摘要字串
	 * @param txt
	 * @return
	 */
	public static String digest(String txt , boolean toLowerCase) {
		if( toLowerCase )
			return digest(txt).toLowerCase();
		else
			return digest(txt);
	}

	private static final char[] hexDigits = { '0', '1', '2', '3', '4', '5',
			'6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

	private static String toHex(byte[] byteArray) {

		char[] resultCharArray = new char[byteArray.length * 2];
		int index = 0;
		for (byte b : byteArray) {
			resultCharArray[index++] = hexDigits[b >>> 4 & 0xf];
			resultCharArray[index++] = hexDigits[b & 0xf];
		}
		return new String(resultCharArray);
	}

}
