package sophia.mmorpg.update.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

/**
 * Copyright (c) 2014 by 游爱.
 * 
 * @version 1.0
 */
public class IOUtil {

	public static boolean debugMsg = false;

	/**
	 * 用于测试，方便删除的
	 * 
	 * @param msg
	 */
	public static void testMsg(String msg) {
		if (debugMsg)
			System.out.println(msg);
	}

	public static void closeWriter(Writer writer) {
		if (writer != null) {
			try {
				writer.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

	public static void closeOs(OutputStream os) {
		if (os != null) {
			try {
				os.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

	public static void closeRead(Reader read) {
		if (read != null) {
			try {
				read.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

	public static void closeIs(InputStream is) {
		if (is != null) {
			try {
				is.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}
}
