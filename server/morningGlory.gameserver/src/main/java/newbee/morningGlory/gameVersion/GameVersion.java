/**
 * 
 */
package newbee.morningGlory.gameVersion;

import java.io.Serializable;

/**
 * 
 * Copyright (c) 2012 by 游爱.
 * 
 * @author 李观武 Create on 2014-4-14 下午3:28:21
 * 
 * @version 1.0
 */
public final class GameVersion implements Serializable {
	private static final long serialVersionUID = -6796976963271915275L;

	public static byte getMajorVersion() {
		return 0;
	}

	public static byte getMinorVersion() {
		return 4;
	}

	public static byte getFractionalVersion() {
		return 6;
	}

	public static byte getLeastVersion() {
		return 78;
	}

	public static String getReleaseDate() {
		return "2014-09-23 下午:14:42";
	}

	public static int getSVNBaselineNum() {
		return 18036;
	}

	public String toString() {
		return "\r\n"+"==================================================================\r\n" + "\tGame Server Version V" + getMajorVersion() + "." + getMinorVersion() + "."
				+ getFractionalVersion() + "." + getLeastVersion() + "\r\n\tRelease Date: " + getReleaseDate() + "\r\n\tSVN Baseline: " + getSVNBaselineNum()
				+ "\r\n==================================================================";
	}
}
