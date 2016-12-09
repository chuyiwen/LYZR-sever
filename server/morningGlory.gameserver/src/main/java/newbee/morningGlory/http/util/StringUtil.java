package newbee.morningGlory.http.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

public final class StringUtil {

	public static boolean isEmpty(String str) {
		if (str == null || str.equals(""))
			return true;

		return false;
	}
	
	public static boolean isNumber(String str) {
		if (str == null)
			return false;
		return str.matches("\\d+(\\.\\d+)?");
	}

	/**
	 * 在字符串的前面加上一个字符
	 * 
	 * @param s
	 *            字符串
	 * @param length
	 *            添加字符后字符串的长度
	 * @param c
	 *            要添加的字符
	 * @return
	 */
	public static String fillHeadString(String s, int length, char c) {
		if (s.length() >= length)
			return s;
		StringBuffer sb = new StringBuffer(s.length() + length);
		for (int i = 0; i < length - s.length(); ++i)
			sb.append(c);
		sb.append(s);
		return sb.toString();
	}

	public static String fillEndString(String s, int length, char c) {
		if (s.length() >= length)
			return s;
		StringBuffer sb;
		(sb = new StringBuffer(s.length() + length)).append(s);
		for (int i = 0; i < length - s.length(); ++i)
			sb.append(c);
		return sb.toString();
	}

	/**
	 * 重复连接字符串
	 * 
	 * @param o
	 *            需要连接的对象
	 * @param count
	 *            重复次数
	 * @return
	 */
	public static String join(Object o, int count) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < count; ++i)
			sb.append(o);
		return sb.toString();
	}

	public static String join(Object[] arr, String obj) {
		return join(arr, obj, arr.length);
	}

	public static String join(Collection<?> c, String obj) {
		return join(c, obj, c.size());
	}

	public static String join(Object[] arr, Object obj, int maxLen) {
		return join(arr, 0, ((maxLen > arr.length) || (maxLen <= 0)) ? arr.length : maxLen, obj);
	}

	public static String join(Collection<?> c, Object obj, int maxLen) {
		return join(c, 0, ((maxLen > c.size()) || (maxLen <= 0)) ? c.size() : maxLen, obj);
	}

	public static String join(Object[] arr, int from, Object obj) {
		return join(arr, from, arr.length, obj);
	}

	public static String join(Collection<?> c, int from, Object obj) {
		return join(c, from, c.size(), obj);
	}

	public static String join(Collection<?> c, int from, int to, Object obj) {
		return join(c.toArray(), from, to, obj);
	}

	/**
	 * 列表中连接字符串
	 * 
	 * @param arr
	 *            对象列表，可以是任意对象列表
	 * @param from
	 *            列表的起始位置
	 * @param to
	 *            列表的结束位置
	 * @param obj
	 *            连接对象，可以是任意对象
	 * @return 返回连接后的字符串
	 */
	public static String join(Object[] arr, int from, int to, Object obj) {
		if (from >= 0 && to <= arr.length && to - from >= 0) {
			StringBuffer sb = new StringBuffer();
			if (to - from > 0) {
				sb.append(arr[from]);
				from += 1;
			}
			if (to - from > 0) {
				while (true) {
					sb.append(obj + "" + arr[from]);
					++from;
					if (from >= to) {
						return sb.toString();
					}
				}
			} else
				return sb.toString();
		}
		throw new IndexOutOfBoundsException();
	}

	/**
	 * 分割字符串
	 * 
	 * @param s
	 *            原字符串
	 * @param num
	 *            每个子串的字符数
	 * @return 返回s中以num个字符为一组字符串的数组
	 */
	public static String[] splitString(String s, int num) {
		if (s == null)
			return null;
		if (num <= 0 || s.length() <= num)
			return new String[] { s };
		Vector<String> v = new Vector<String>();
		for (int i = 0; i < s.length() / num; ++i)
			v.add(s.substring(i * num, (i + 1) * num));
		if (s.length() % num != 0)
			v.add(s.substring(s.length() - (s.length() % num)));
		String[] arr = new String[v.size()];
		v.copyInto(arr);
		return arr;
	}

	public static String quote(Object s, String left, String right) {
		return left + s + right;
	}

	/**
	 * 将对象数组的每一项成员用左右字符串括起来
	 */
	public static String[] quote(Object[] ss, String left, String right) {
		String[] ar = new String[ss.length];
		for (int i = 0; i < ar.length; ++i)
			ar[i] = quote(ss[i], left, right);
		return ar;
	}

	/**
	 * 判断是否为数字
	 * 
	 * @param s
	 *            原字符串
	 * @param ignoreChar
	 *            忽略的字符，如".-"，则忽略'.','-'字符
	 * @return
	 */
	public static boolean isNumber(String s, String ignoreChar) {
		int length = s.length();
		for (int i = 0; i < length; ++i) {
			char each_char = s.charAt(i);
			if (each_char >= '0' && each_char <= '9')
				continue;
			if (ignoreChar == null || ignoreChar.length() == 0)
				return false;
			if (ignoreChar.indexOf(each_char) == -1)
				return false;
		}
		return true;
	}

	/**
	 * 整理字符串,去掉两边的指定字符
	 * 
	 * @param s
	 * @param left
	 * @param right
	 * @return
	 */
	public static String trim(String s, String left, String right) {
		int i = 0;
		int j = s.length() - 1;
		if (left != null && left.length() > 0) {
			for (; i < s.length(); ++i) {
				if (left.indexOf(s.charAt(i)) == -1)
					break;
			}
		}
		if (right != null && right.length() > 0) {
			for (; j >= i; --j) {
				if (right.indexOf(s.charAt(j)) == -1)
					break;
			}
		}
		return s.substring(i, j + 1);
	}

	public static String trim(String s, String trimChars) {
		return trim(s, trimChars, trimChars);
	}

	public static String trimLeft(String s, String left) {
		return trim(s, left, "");
	}

	public static String trimRight(String s, String right) {
		return trim(s, "", right);
	}

	/**
	 * 替换字符串中的数字
	 * 
	 * @param str
	 * @param news
	 * @return
	 */
	public static String replaceDigits(String str, int[] news) {
		return replaceDigits(str, getDigits(str), news);
	}

	/**
	 * 替换字符串中的数字
	 * 
	 * @param str
	 * @param olds
	 * @param news
	 * @return
	 */
	public static String replaceDigits(String str, int[] olds, int[] news) {
		for (int i = 0; i < olds.length; i++) {
			int pos = str.indexOf(String.valueOf(olds[i]));
			String tmp = null;
			if (pos > 0)
				tmp = str.substring(0, pos);
			str = str.substring(pos).replace(String.valueOf(olds[i]), String.valueOf(news[i]));
			if (tmp != null)
				str = tmp + str;
		}
		return str;
	}

	/**
	 * 获得字符串中的所有数字
	 * 
	 * @param str
	 * @return
	 */
	public static int[] getDigits(String str) {
		List<String> ls = new ArrayList<String>();
		StringBuffer s = new StringBuffer();
		for (int i = 0; i < str.length(); ++i) {
			char c = str.charAt(i);
			if (Character.isDigit(c))
				s.append(c);
			else {
				if (s.length() > 0) {
					ls.add(s.toString());
					s = new StringBuffer();
				}
			}
		}
		if (s.length() > 0)
			ls.add(s.toString());
		return Type.getInts(ls.toArray(), null);
	}

	/**
	 * 获得字符串开头的数字
	 * 
	 * @param s
	 * @return
	 */
	public static String getStartDigit(String s) {
		s = s.trim();
		StringBuffer sb = new StringBuffer(s.length());
		for (int i = 0; i < s.length(); ++i) {
			if (!Character.isDigit(s.charAt(i)))
				break;
			sb.append(s.charAt(i));
		}
		return sb.toString();
	}

	public static long getStartDigit(String src, long dv) {
		return Type.getLong(getStartDigit(src), dv);
	}

	/**
	 * 获得字符串结尾的数字
	 * 
	 * @param s
	 * @return
	 */
	public static String getEndDigit(String s) {
		s = s.trim();
		StringBuffer sb = new StringBuffer(s.length());
		for (int i = s.length() - 1; i >= 0; --i) {
			if (!Character.isDigit(s.charAt(i)))
				break;
			sb.insert(0, s.charAt(i));
		}
		return sb.toString();
	}

	public static long getEndDigit(String src, long dv) {
		return Type.getLong(getEndDigit(src), dv);
	}

	public static String toStartUpperCase(String str, int len) {
		return toStartCase(str, len, true);
	}

	public static String toStartLowerCase(String str, int len) {
		return toStartCase(str, len, false);
	}

	public static String toStartCase(String str, int len, boolean upperCase) {
		return toCase(str, 0, len, upperCase);
	}

	public static String toEndUpperCase(String str, int len) {
		return toEndCase(str, len, true);
	}

	public static String toEndLowerCase(String str, int len) {
		return toEndCase(str, len, false);
	}

	public static String toEndCase(String str, int len, boolean upperCase) {
		return toCase(str, str.length() - len, str.length(), upperCase);
	}

	public static String toUpperCase(String str, int start, int end) {
		return toCase(str, start, end, true);
	}

	public static String toLowerCase(String str, int start, int end) {
		return toCase(str, start, end, false);
	}

	public static String toCase(String str, int start, int end, boolean upperCase) {
		int strLen = str.length();
		char[] chars = new char[strLen];
		for (int i = 0; i < strLen; i++) {
			char c = str.charAt(i);
			if (i >= start && i < end)
				chars[i] = upperCase ? Character.toUpperCase(c) : Character.toLowerCase(c);
			else
				chars[i] = c;

		}
		return new String(chars);
	}

	/**
	 * 将byte序列转换成十六进制字符串序列，不分隔每个byte
	 * 
	 * @param arr
	 * @return
	 */
	public static String toHexString(byte[] arr) {
		return toHexString(arr, "");
	}

	/**
	 * 将byte序列转换成十六进制字符串序列，不分隔每个byte
	 * 
	 * @param arr
	 * @param seq
	 *            分隔符
	 * @return
	 */
	public static String toHexString(byte[] arr, String seq) {
		StringBuffer s = new StringBuffer(arr.length << 2);
		if (arr.length > 0) {
			s.append(toHexString(arr[0]));
			for (int i = 1; i < arr.length; ++i)
				s.append(seq + toHexString(arr[i]));
		}
		return s.toString();
	}

	/**
	 * 将char序列转换成十六进制字符串序列
	 * 
	 * @param s
	 * @return
	 */
	public static String toHexString(String s) {
		return toHexString(s, "");
	}

	/**
	 * 将char序列转换成十六进制字符串序列，以sep分隔每一个char
	 * 
	 * @param s
	 * @param seq
	 * @return
	 */
	public static String toHexString(String s, String seq) {
		StringBuffer a = new StringBuffer(s.length() * 6);
		if (s.length() > 0) {
			a.append(toHexString((short) s.charAt(0)));
			for (int i = 1; i < s.length(); ++i)
				a.append(seq + toHexString((short) s.charAt(i)));
		}
		return a.toString();
	}

	public static String toHexString(long n) {
		return toHexString(Bytes.long2bytes(n));
	}

	public static String toHexString(int n) {
		return fillHeadString(Integer.toHexString(n).toUpperCase(), 8, '0');
	}

	public static String toHexString(short n) {
		return fillHeadString(Integer.toHexString(n & 0xFFFF).toUpperCase(), 4, '0');
	}

	public static String toHexString(byte n) {
		return fillHeadString(Integer.toHexString(n & 0xFF).toUpperCase(), 2, '0');
	}

	/**
	 * 字符串是否不为空
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isNotEmpty(String str) {
		if (str != null && str.trim().length() > 0)
			return true;
		return false;
	}

	public static String replaceBlank(String str) {
		Pattern p = Pattern.compile("\\s*|\t|\r|\n");
		Matcher m = p.matcher(str);
		String rStr = m.replaceAll("");
		return rStr;
	}

	public static String cut(String str, int length) {
		return cut(str, length, 3);
	}

	public static String cut(String str, int length, int dot) {
		if (str == null)
			return null;
		str = str.replaceAll("[\t\n|\n]", "");
		if (str.length() < length)
			return str;
		int cutLength = length * 2;
		for (int i = 0; i < cutLength && i < str.length(); i++)
			if (Chinese.isCJK(str.charAt(i)))
				cutLength--;

		if (str.length() < cutLength)
			return str;
		return str.substring(0, cutLength) + (dot == 3 ? "..." : dot == 2 ? ".." : dot == 1 ? "." : "");
	}

	public static String subBytesString(String src, int start, int end) {
		byte[] sb = src.getBytes();
		if (end <= 0)
			end = sb.length;
		return new String(Bytes.subBytes(sb, start, end - start));
	}

	public static String remove(String str, char remove) {
		if (StringUtils.isEmpty(str) || str.indexOf(remove) == -1)
			return str;
		char[] chars = str.toCharArray();
		int pos = 0;
		for (int i = 0; i < chars.length; ++i)
			if (chars[i] != remove)
				chars[(pos++)] = chars[i];
		return new String(chars, 0, pos);
	}

	public static String removeStart(String str, String remove) {
		if (StringUtils.isEmpty(str) || StringUtils.isEmpty(remove))
			return str;
		if (str.startsWith(remove))
			return str.substring(remove.length());
		return str;
	}

	public static String removeEnd(String str, String remove) {
		if (StringUtils.isEmpty(str) || StringUtils.isEmpty(remove))
			return str;
		if (str.endsWith(remove))
			return str.substring(0, str.length() - remove.length());
		return str;
	}

	public static String remove(String str, String remove) {
		if (StringUtils.isEmpty(str) || StringUtils.isEmpty(remove))
			return str;
		return replace(str, remove, "", -1);
	}

	public static String replaceOnce(String text, String repl, String with) {
		return replace(text, repl, with, 1);
	}

	public static String replace(String text, String repl, String with) {
		return replace(text, repl, with, -1);
	}

	public static String replace(String text, String repl, String with, int max) {
		if (StringUtils.isEmpty(text) || StringUtils.isEmpty(repl) || with == null || max == 0)
			return text;
		int start = 0;
		int end = text.indexOf(repl, start);
		if (end == -1)
			return text;
		int replLength = repl.length();
		int increase = with.length() - replLength;
		increase = (increase < 0) ? 0 : increase;
		increase *= ((max > 64) ? 64 : (max < 0) ? 16 : max);
		StringBuffer buf = new StringBuffer(text.length() + increase);
		while (end != -1) {
			buf.append(text.substring(start, end)).append(with);
			start = end + replLength;
			if (--max == 0)
				break;
			end = text.indexOf(repl, start);
		}
		buf.append(text.substring(start));
		return buf.toString();
	}

	public static String replaceChars(String str, char searchChar, char replaceChar) {
		if (str == null)
			return null;
		return str.replace(searchChar, replaceChar);
	}

	public static String replaceChars(String str, String searchChars, String replaceChars) {
		if (StringUtils.isEmpty(str) || StringUtils.isEmpty(searchChars))
			return str;
		if (replaceChars == null)
			replaceChars = "";
		boolean modified = false;
		int replaceCharsLength = replaceChars.length();
		int strLength = str.length();
		StringBuffer buf = new StringBuffer(strLength);
		for (int i = 0; i < strLength; ++i) {
			char ch = str.charAt(i);
			int index = searchChars.indexOf(ch);
			if (index >= 0) {
				modified = true;
				if (index < replaceCharsLength)
					buf.append(replaceChars.charAt(index));
			} else
				buf.append(ch);
		}
		if (modified)
			return buf.toString();
		return str;
	}

	public static boolean equalsIgnoreCase(String str1, String str2) {
		return (str1 == null) ? false : (str2 == null) ? true : str1.equalsIgnoreCase(str2);
	}

	public static String format(String struct, Object params) {
		return format(struct, new Object[] { params });
	}

	public static String format(String struct, Object[] params) {
		if (struct == null || params == null)
			return "";
		MessageFormat format = new MessageFormat(struct);
		return format.format(params);
	}

	public static String format(String struct, List<?> params) {
		if (params == null)
			return "";
		return format(struct, params.toArray());
	}

	public static String format(String struct, Set<?> params) {
		if (params == null)
			return "";
		return format(struct, params.toArray());
	}

	public static final char[] digits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q',
			'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X',
			'Y', 'Z' };

	/**
	 * 数字转字符串
	 * 
	 * @param num
	 *            数字
	 * @param radix
	 *            基数
	 * @return
	 */
	public static String numberToString(long num, int radix) {
		if (radix > digits.length)
			radix = digits.length;
		if (radix <= 36)
			return Long.toString(num, radix);
		char[] chars = new char[65];
		int k = 64;
		int j = num < 0L ? 1 : 0;
		if (j == 0)
			num = -num;
		while (num <= -radix) {
			chars[k--] = digits[(int) (-(num % radix))];
			num /= radix;
		}
		chars[k] = digits[(int) (-num)];

		if (j != 0)
			chars[--k] = '-';
		return new String(chars, k, 65 - k);
	}

	/**
	 * 显示指定编码下的字符长度
	 * 
	 * @param encoding
	 * @param str
	 * @return
	 */
	public static int getBytesLengthOfEncoding(String encoding, String str) {
		if (str == null || str.length() == 0)
			return 0;
		try {
			byte bytes[] = str.getBytes(encoding);
			int length = bytes.length;
			return length;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * 转化指定字符串为指定编码格式
	 * 
	 * @param context
	 * @param encoding
	 * @return
	 */
	public static String getSpecialString(String context, String encoding) {
		try {
			ByteArrayInputStream in = new ByteArrayInputStream(context.getBytes());
			InputStreamReader isr = new InputStreamReader(in, encoding);
			BufferedReader reader = new BufferedReader(isr);
			StringBuffer buffer = new StringBuffer();
			String result;
			while ((result = reader.readLine()) != null) {
				buffer.append(result);
			}
			return buffer.toString();
		} catch (Exception ex) {
			return context;
		}
	}

	/**
	 * 返回指定字符串长度
	 * 
	 * @param s
	 * @return
	 */
	public static int length(String s) {
		if (s == null)
			return 0;
		else
			return s.getBytes().length;
	}

	/**
	 * 获得特定字符总数
	 * 
	 * @param str
	 * @param chr
	 * @return
	 */
	public static int charCount(String str, char chr) {
		int count = 0;
		if (str != null) {
			int length = str.length();
			for (int i = 0; i < length; i++) {
				if (str.charAt(i) == chr) {
					count++;
				}
			}
			return count;
		}
		return count;
	}

	private static Pattern PATTERN_XML = Pattern.compile("[&<>'\"]");
	private static Map<String, String> ESCAPE_XML_MAP = new HashMap<String, String>();
	private static Pattern PATTERN_XML_UN = Pattern.compile("(\\&amp;)|(&lt;)|(&gt;)|(&#39;)|(&quot;)|(\\$\\$)|(&nbsp;)");
	private static Map<String, String> UNESCAPE_XML_MAP = new HashMap<String, String>();
	static {
		ESCAPE_XML_MAP = stringToMap("&:&amp;,<:&lt;,>:&gt;,':&#39;,\":&quot;,$:$$, :&nbsp;");
		UNESCAPE_XML_MAP = stringToMap("&amp;:&,&lt;:<,&gt;:>,&#39;:',&quot;:\",$$:$,&nbsp;: ");
	}

	public static String escapeReplacementSpecialChars(String s) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < s.length(); ++i) {
			char c = s.charAt(i);
			if (c == '\\' || c == '$')
				sb.append('\\');
			sb.append(c);
		}
		return sb.toString();
	}

	public static String escapeXML(String s) {
		return replaceAll(s, PATTERN_XML, ESCAPE_XML_MAP);
	}

	public static String unescapeXML(String s) {
		return replaceAll(s, PATTERN_XML_UN, UNESCAPE_XML_MAP);
	}

	private static Pattern PATTERN_WML = Pattern.compile("[&<>'\" \\$]");

	public static String escapeWML(String s) {
		s = s.replaceAll("[\1- &&[^\n ]]", "");
		s = replaceAll(s, PATTERN_WML, ESCAPE_XML_MAP);
		s = s.replaceAll("\n", "<br/>\r\n");
		return s;
	}

	private static Pattern PATTERN_HTML = Pattern.compile("[&<>'\" ]");

	public static String escapeHTML(String s) {
		s = replaceAll(s, PATTERN_HTML, ESCAPE_XML_MAP);
		s = s.replaceAll("\n", "<br>\r\n");
		return s;
	}

	private static Pattern PATTERN_CHAR = Pattern.compile("(\\&#\\d{1,5};)|(\\&#x[0-9a-fA-F]{1,4};)");

	public static String unescape(String s) {
		Matcher matcher = PATTERN_CHAR.matcher(s);
		StringBuffer sb = new StringBuffer();
		while (matcher.find()) {
			String grp = matcher.group();
			String str = "";
			if (grp.startsWith("&#x"))
				str = String.valueOf((char) Integer.parseInt(grp.substring(3, grp.length() - 1), 16));
			str = String.valueOf((char) Integer.parseInt(grp.substring(2, grp.length() - 1)));
			if (str == null)
				str = "";
			else
				str = escapeReplacementSpecialChars(str);

			matcher.appendReplacement(sb, str);
		}
		matcher.appendTail(sb);
		return sb.toString();
	}

	private static Map<String, String> mapToStringSpecialChars = new HashMap<String, String>();
	private static Map<String, String> stringToMapSpecialChars = new HashMap<String, String>();
	static {
		mapToStringSpecialChars.put("%", "%25");
		mapToStringSpecialChars.put(":", "%3A");
		mapToStringSpecialChars.put(",", "%2C");

		stringToMapSpecialChars.put("%25", "%");
		stringToMapSpecialChars.put("%3A", ":");
		stringToMapSpecialChars.put("%2C", ",");
	}

	public static String escapeMapToStringSpecialChars(String s) {
		return replaceAll(s, Pattern.compile("(%)|(:)|(,)"), mapToStringSpecialChars);
	}

	public static String mapToString(Map<?, ?> map) {
		List<Object> ls = new ArrayList<Object>();
		Iterator<?> it = map.entrySet().iterator();
		while (it.hasNext()) {
			@SuppressWarnings("rawtypes")
			Map.Entry e = (Map.Entry) it.next();
			String str = escapeMapToStringSpecialChars((String) e.getKey());
			String str2 = escapeMapToStringSpecialChars((String) e.getValue());
			ls.add(str + ":" + str2);
		}
		return join(ls, ",");
	}

	public static String unescapeStringToMapSpecialChars(String s) {
		return replaceAll(s, Pattern.compile("(%25)|(%3A)|(%2C)"), stringToMapSpecialChars);
	}

	public static void stringToMap(String str, Map<String, String> paramMap, boolean trim) {
		if (str.length() == 0)
			return;
		String[] arr = str.split(",");
		for (int i = 0; i < arr.length; ++i) {
			int j = arr[i].indexOf(58);
			String key;
			if (j != -1) {
				key = unescapeStringToMapSpecialChars(key = arr[i].substring(0, j));
				String value = unescapeStringToMapSpecialChars(value = arr[i].substring(j + 1));
				paramMap.put(trim ? key.trim() : key, trim ? value.trim() : value);
			} else if ((key = trim ? arr[i].trim() : arr[i]).length() > 0)
				paramMap.put(key, key);
		}
	}

	public static void stringToMap(String s, Map<String, String> map) {
		stringToMap(s, map, true);
	}

	public static Map<String, String> stringToMap(String s, boolean trim) {
		Map<String, String> map = new LinkedHashMap<String, String>();
		stringToMap(s, map, trim);
		return map;
	}

	public static Map<String, String> stringToMap(String s) {
		return stringToMap(s, false);
	}

	public static String replaceAll(String string, Pattern pattern, Map<String, String> m) {
		Matcher matcher = pattern.matcher(string);
		StringBuffer s = new StringBuffer();
		while (matcher.find()) {
			String str = (String) m.get(matcher.group());
			if (str == null)
				str = "";
			else
				str = escapeReplacementSpecialChars(str);

			matcher.appendReplacement(s, str);
		}
		matcher.appendTail(s);
		return s.toString();
	}

	/**
	 * 子串在字符串中出现的次数
	 * 
	 * @param str
	 * @param subStr
	 * @return
	 */
	public static int subStringCount(String str, String subStr) {
		int count = 0;
		int pos = -1;
		int from = 0;
		while ((pos = str.indexOf(subStr, from)) != -1) {
			from = pos + subStr.length();
			count++;
		}
		return count;
	}

}
