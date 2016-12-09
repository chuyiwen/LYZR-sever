package newbee.morningGlory.http.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Random;

import com.google.common.base.Strings;

public class Chinese {
	private static String[] cDigit = { "零", "十", "百", "千", "万", "亿", "兆" };

	private static String[] cNum = { "零", "一", "二", "三", "四", "五", "六", "七", "八", "九", "十" };

	private static String[] chNumber = { "零", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖", "拾" };

	private static String[] chDigit = { "零", "拾", "佰", "仟", "萬", "億", "兆" };

	private static String[] dDigit = { "初", "十", "廿", "卅", " " };

	private static String[] dNum = { "日", "一", "二", "三", "四", "五", "六", "七", "八", "九", "十" };

	private static String simpleWords = "";

	private static String multiWords = "";

	private static String[] lastNames;

	private static String[] firstNames;

	static {
		reloadDat();
	}

	public static void reloadDat() {
		InputStream is = null;
		BufferedReader br = null;
		try {
			is = Chinese.class.getResourceAsStream("cn.dat");
			br = new BufferedReader(new InputStreamReader(is));
			simpleWords = br.readLine();
			multiWords = br.readLine();
			String readLine = br.readLine();
			if (!Strings.isNullOrEmpty(readLine)) {
				lastNames = readLine.split(" ");
			}
			readLine = br.readLine();
			if (!Strings.isNullOrEmpty(readLine)) {
				firstNames = readLine.split(" ");
			}
			
		} catch (Exception e) {
			System.out.println("载入中文数据失败...");
			e.printStackTrace();
		} finally {
			try {
				if (is != null)
					is.close();
				if (br != null)
					br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 是否为英文字母
	 * 
	 * @param c
	 * @return
	 */
	public static boolean isLetter(char c) {
		if (c >= 'a' && c <= 'z')
			return true;
		if (c >= 'A' && c <= 'Z')
			return true;
		if (c >= 65345 && c <= 65370)
			return true;
		return c >= 65313 && c <= 65338;
	}

	/**
	 * 转换为半角字符
	 * 
	 * @param c
	 * @return
	 */
	public static char toDBCCase(char c) {
		if (c >= 65281 && c <= 65374)
			return (char) (c - 65248);
		return c;
	}

	/**
	 * 转换为全角字符
	 * 
	 * @param c
	 * @return
	 */
	public static char toSBCCase(char c) {
		if (c >= '!' && c <= '~')
			return (char) (c + 65248);
		return c;
	}

	/**
	 * 判断是否为中日韩文字
	 * 
	 * @param c
	 * @return
	 */
	public static boolean isCJK(char c) {
		Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
		if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS// 【4E00-9FFF】中日韩统一表意文字
				|| ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS// 【3300-33FF】中日韩兼容字符
				|| ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A// 【3400-4DBF】中日韩统一表意文字扩充A
				|| ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION// 【3000-303F】符号和标点
				|| ub == Character.UnicodeBlock.GENERAL_PUNCTUATION// 【2000-206F】一般标点符号
				|| ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS// 【FF00-FFEF】半角及全角字符
				// 韩文字符集
				|| ub == Character.UnicodeBlock.HANGUL_SYLLABLES// 【AC00-D7AF】朝鲜文音节
				|| ub == Character.UnicodeBlock.HANGUL_JAMO// 【1100-11FF】朝鲜文
				|| ub == Character.UnicodeBlock.HANGUL_COMPATIBILITY_JAMO// 【3130-318F】朝鲜文兼容字母
				// 日文字符集
				|| ub == Character.UnicodeBlock.HIRAGANA // 【3040-309F】Hiragana平假名
				|| ub == Character.UnicodeBlock.KATAKANA // 【30A0-30FF】Katakana片假名
				|| ub == Character.UnicodeBlock.KATAKANA_PHONETIC_EXTENSIONS// 【31F0-31FF】Katakana片假名音标扩充
		)
			return true;
		else
			return false;
		// return (((c >= 12352) && (c <= 12687)) || ((c >= 13056) && (c <=
		// 13183)) || ((c >= 13312) && (c <= 15661)) || ((c >= 19968) && (c <=
		// 40959)) || ((c >= 63744) && (c <= 64255)));
	}

	/**
	 * 是否有中日韩文字
	 * 
	 * @param s
	 * @return
	 */
	public static boolean hasCJK(String s) {
		for (int i = 0; i < s.length(); ++i) {
			if (isCJK(s.charAt(i)))
				return true;
		}
		return false;
	}

	/**
	 * 是否都是中日韩文字
	 * 
	 * @param s
	 * @return
	 */
	public static boolean isAllCJK(String s) {
		for (int i = 0; i < s.length(); ++i) {
			if (!isCJK(s.charAt(i)))
				return false;
		}
		return true;
	}

	/**
	 * 中日韩文字数量
	 * 
	 * @param s
	 * @return
	 */
	public static int countCJK(String s) {
		int count = 0;
		for (int i = 0; i < s.length(); ++i) {
			if (isCJK(s.charAt(i)))
				count++;
		}
		return count;
	}

	/**
	 * 繁体中文字符转为简体中文字符
	 * 
	 * @param c
	 * @return
	 */
	public static char simpleToMulti(char c) {
		int pos;
		if ((pos = simpleWords.indexOf(c)) != -1)
			c = multiWords.charAt(pos);
		return c;
	}

	/**
	 * 简体中文转繁体中文
	 * 
	 * @param str
	 * @return
	 */
	public static String simpleToMulti(String str) {
		StringBuffer sb = new StringBuffer(str.length());
		for (int i = 0; i < str.length(); ++i)
			sb.append(simpleToMulti(str.charAt(i)));
		return sb.toString();
	}

	/**
	 * 繁体中文字符转简体中文字符
	 * 
	 * @param c
	 * @return
	 */
	public static char multiToSimple(char c) {
		int pos;
		if ((pos = multiWords.indexOf(c)) != -1)
			c = simpleWords.charAt(pos);
		return c;
	}

	/**
	 * 繁体中文转简体中文
	 * 
	 * @param str
	 * @return
	 */
	public static String multiToSimple(String str) {
		StringBuffer sb = new StringBuffer(str.length());
		for (int i = 0; i < str.length(); ++i)
			sb.append(multiToSimple(str.charAt(i)));
		return sb.toString();
	}

	/**
	 * 是否是简体中文字符
	 * 
	 * @param c
	 * @return
	 */
	public static boolean isSimpleWord(char c) {
		return isCJK(c) && !isMultiWord(c);
	}

	/**
	 * 是否是繁体中文字符
	 * 
	 * @param c
	 * @return
	 */
	public static boolean isMultiWord(char c) {
		return multiWords.indexOf(c) != -1;
	}

	/**
	 * 是否是简体中文字符串
	 * 
	 * @param s
	 * @return
	 */
	public static boolean isSimpleWords(String s) {
		for (int i = 0; i < s.length(); ++i) {
			if (!(isSimpleWord(s.charAt(i))))
				return false;
		}
		return true;
	}

	/**
	 * 是否都是繁体中文
	 * 
	 * @param s
	 * @return
	 */
	public static boolean isAllMultiWords(String s) {
		for (int i = 0; i < s.length(); ++i) {
			if (!isMultiWord(s.charAt(i)))
				return false;
		}
		return true;
	}

	public static String getRandomCnName() {
		Random random = new Random();
		int r1 = random.nextInt(lastNames.length);
		int r2 = random.nextInt(firstNames.length);
		return lastNames[r1] + firstNames[r2];
	}

	/**
	 * 获取中文的数字表示（小写中文）
	 * 
	 * @param number
	 * @return
	 */
	public static String chineseNumber(long number) {
		if (number < 0L)
			return "负" + chineseNumber(-number);
		if (number < 11L)
			return cNum[(int) number];
		if (number < 20L)
			return cNum[10] + cNum[(int) (number - 10L)];
		if (number < 100L) {
			if (number % 10L != 0L)
				return cNum[(int) (number / 10L)] + cDigit[1] + cNum[(int) (number % 10L)];
			return cNum[(int) (number / 10L)] + cDigit[1];
		}
		if (number < 1000L) {
			if (number % 100L == 0L)
				return cNum[(int) (number / 100L)] + cDigit[2];
			if (number % 100L < 10L)
				return cNum[(int) (number / 100L)] + cDigit[2] + cNum[0] + chineseNumber((int) (number % 100L));
			if (number % 100L < 20L)
				return cNum[(int) (number / 100L)] + cDigit[2] + cNum[1] + chineseNumber((int) (number % 100L));
			return cNum[(int) (number / 100L)] + cDigit[2] + chineseNumber(number % 100L);
		}
		if (number < 10000L) {
			if (number % 1000L == 0L)
				return cNum[(int) (number / 1000L)] + cDigit[3];
			if (number % 1000L < 100L)
				return cNum[(int) (number / 1000L)] + cDigit[3] + cDigit[0] + chineseNumber(number % 1000L);
			return cNum[(int) (number / 1000L)] + cDigit[3] + chineseNumber(number % 1000L);
		}
		if (number < 100000000L) {
			if (number % 10000L == 0L)
				return chineseNumber(number / 10000L) + cDigit[4];
			if (number % 10000L < 1000L)
				return chineseNumber(number / 10000L) + cDigit[4] + cDigit[0] + chineseNumber(number % 10000L);
			return chineseNumber(number / 10000L) + cDigit[4] + chineseNumber(number % 10000L);
		}
		if (number < 1000000000000L) {
			if (number % 100000000L == 0L)
				return chineseNumber(number / 100000000L) + cDigit[5];
			if (number % 100000000L < 10000000L)
				return chineseNumber(number / 100000000L) + cDigit[5] + cDigit[0] + chineseNumber(number % 100000000L);
			return chineseNumber(number / 100000000L) + cDigit[5] + chineseNumber(number % 100000000L);
		}

		if (number % 1000000000000L == 0L)
			return chineseNumber(number / 1000000000000L) + cDigit[6];
		if (number % 1000000000000L < 100000000000L)
			return chineseNumber(number / 1000000000000L) + cDigit[6] + cDigit[0] + chineseNumber(number % 1000000000000L);
		return chineseNumber(number / 1000000000000L) + cDigit[6] + chineseNumber(number % 1000000000000L);
	}

	/**
	 * 获取中文的数字表示（大写中文）
	 * 
	 * @param number
	 * @return
	 */
	public static String chineseNumber2(long number) {
		if (number < 0L)
			return "负" + chineseNumber2(-number);
		if (number < 11L)
			return chNumber[(int) number];
		if (number < 20L)
			return chNumber[10] + chNumber[(int) (number - 10L)];
		if (number < 100L) {
			if (number % 10L != 0L)
				return chNumber[(int) (number / 10L)] + chDigit[1] + chNumber[(int) (number % 10L)];
			return chNumber[(int) (number / 10L)] + chDigit[1];
		}
		if (number < 1000L) {
			if (number % 100L == 0L)
				return chNumber[(int) (number / 100L)] + chDigit[2];
			if (number % 100L < 10L)
				return chNumber[(int) (number / 100L)] + chDigit[2] + chNumber[0] + chineseNumber2((int) (number % 100L));
			if (number % 100L < 20L)
				return chNumber[(int) (number / 100L)] + chDigit[2] + chNumber[1] + chineseNumber2((int) (number % 100L));
			return chNumber[(int) (number / 100L)] + chDigit[2] + chineseNumber2(number % 100L);
		}
		if (number < 10000L) {
			if (number % 1000L == 0L)
				return chNumber[(int) (number / 1000L)] + chDigit[3];
			if (number % 1000L < 100L)
				return chNumber[(int) (number / 1000L)] + chDigit[3] + chDigit[0] + chineseNumber2(number % 1000L);
			return chNumber[(int) (number / 1000L)] + chDigit[3] + chineseNumber2(number % 1000L);
		}
		if (number < 100000000L) {
			if (number % 10000L == 0L)
				return chineseNumber2(number / 10000L) + chDigit[4];
			if (number % 10000L < 1000L)
				return chineseNumber2(number / 10000L) + chDigit[4] + chDigit[0] + chineseNumber2(number % 10000L);
			return chineseNumber2(number / 10000L) + chDigit[4] + chineseNumber2(number % 10000L);
		}
		if (number < 1000000000000L) {
			if (number % 100000000L == 0L)
				return chineseNumber2(number / 100000000L) + chDigit[5];
			if (number % 100000000L < 10000000L)
				return chineseNumber2(number / 100000000L) + chDigit[5] + chDigit[0] + chineseNumber2(number % 100000000L);
			return chineseNumber2(number / 100000000L) + chDigit[5] + chineseNumber2(number % 100000000L);
		}

		if (number % 1000000000000L == 0L)
			return chineseNumber2(number / 1000000000000L) + chDigit[6];
		if (number % 1000000000000L < 100000000000L)
			return chineseNumber2(number / 1000000000000L) + chDigit[6] + chDigit[0] + chineseNumber2(number % 1000000000000L);
		return chineseNumber2(number / 1000000000000L) + chDigit[6] + chineseNumber2(number % 1000000000000L);
	}

	/**
	 * 中文的星期表示
	 * 
	 * @param week
	 * @return
	 */
	public static String getWeekDayName(int week) {
		return "星期" + dNum[java.lang.Math.abs(week % 7)];
	}

	public static String getWeekDay(int week) {
		return dNum[java.lang.Math.abs(week % 7)];
	}

	/**
	 * 某一天的中文表示
	 * 
	 * @param day
	 * @return
	 */
	public static String getLunarDayName(int day) {
		if (day < 1 || day > 31)
			return "";
		switch (day) {
		case 10:
			return "初十";
		case 20:
			return "二十";
		case 30:
			return "三十";
		}
		return dDigit[(day / 10)] + dNum[(day % 10)];
	}

	public static String[] getFirstNames() {
		return firstNames;
	}

	public static String[] getLastNames() {
		return lastNames;
	}

	/**
	 * 检查指定字符串中是否存在中文字符。
	 * 
	 * @param checkStr
	 *            指定需要检查的字符串。
	 * @return 逻辑值（True Or False）。
	 */
	public static final boolean hasChinese(String checkStr) {
		boolean checkedStatus = false;
		boolean isError = false;
		String spStr = " _-";
		int checkStrLength = checkStr.length() - 1;
		for (int i = 0; i <= checkStrLength; i++) {
			char ch = checkStr.charAt(i);
			if (ch < '\176') {
				ch = Character.toUpperCase(ch);
				if (((ch < 'A') || (ch > 'Z')) && ((ch < '0') || (ch > '9')) && (spStr.indexOf(ch) < 0)) {
					isError = true;
				}
			}
		}
		checkedStatus = !isError;
		return checkedStatus;
	}

	/**
	 * 检查是否为纯字母
	 * 
	 * @param value
	 * @return
	 */
	public final static boolean isAlphabet(String value) {
		if (value == null || value.length() == 0)
			return false;
		for (int i = 0; i < value.length(); i++) {
			char c = Character.toUpperCase(value.charAt(i));
			if ('A' <= c && c <= 'Z')
				return true;
		}
		return false;
	}

	/**
	 * 检查是否为字母与数字混合
	 * 
	 * @param value
	 * @return
	 */
	public static boolean isAlphabetNumeric(String value) {
		if (value == null || value.trim().length() == 0)
			return true;
		for (int i = 0; i < value.length(); i++) {
			char letter = value.charAt(i);
			if (('a' > letter || letter > 'z') && ('A' > letter || letter > 'Z') && ('0' > letter || letter > '9'))
				return false;
		}
		return true;
	}
}
