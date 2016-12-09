/**
 *   Copyright 2013-2015 Sophia
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
*/
package sophia.foundation.util;

import org.apache.commons.lang3.StringUtils;


public final class SFStringUtil {
	/**
	 * 判断是否中日韩字符
	 * @param c
	 * @return
	 * true: 是
	 * false: 否
	 */
	public static boolean isCJK(char c) {
		Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
		return ((ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS)
				|| (ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS)
				|| (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A)
				|| (ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION)
				|| (ub == Character.UnicodeBlock.GENERAL_PUNCTUATION)
				|| (ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS)
				|| (ub == Character.UnicodeBlock.HANGUL_SYLLABLES)
				|| (ub == Character.UnicodeBlock.HANGUL_JAMO)
				|| (ub == Character.UnicodeBlock.HANGUL_COMPATIBILITY_JAMO)
				|| (ub == Character.UnicodeBlock.HIRAGANA)
				|| (ub == Character.UnicodeBlock.KATAKANA) || (ub == Character.UnicodeBlock.KATAKANA_PHONETIC_EXTENSIONS));
	}

	/**
	 * 计算字符串中字符的个数，一个汉字算2个字符
	 * @param str
	 * @return	字符的个数
	 */
	public static int charCount(String str) {
		int len = 0;
		if (str!= null) {
			for (int i = 0; i < str.length(); ++i) {
				char c = str.charAt(i);
				if (isCJK(c))
					len += 2;
				else
					len += 1;
			}
		}
		return len;
	}
	
	private static char[] defaultFilterChars = new char[]{'"','\'','\\',',',' ','\r','\t','\n'};
	private static char defaultReplacement = '_';
	
	public static boolean contains( String str )
	{
		return contains(str,defaultFilterChars);
	}

	public static boolean contains( String str , char[] filterChars)
	{
		for( char c : filterChars )
		{
			if( str.indexOf(c) > -1 )
				return true;
		}
		return false;
	}

	public static String filtering( String str )
	{
		return filtering(str,defaultFilterChars,defaultReplacement);
	}
	
	public static String filtering( String str , char[] filterChars , char replacement )
	{
		if( !contains(str,filterChars) )
		{
			return str;
		}
		else
		{
			for( char c : filterChars )
			{
				str = str.replace(c, replacement);
			}
			return str;
		}
		
	}	
	
	/**
	 * 检测是否有特殊字符
	 * 
	 * @param source
	 * @return true表示有
	 */
	public static boolean containsSurrogate(String source) {
		if (StringUtils.isBlank(source)) {
			return false;
		}
		
		for (int i = 0; i < source.length(); i++) {
			if (Character.isSurrogate(source.charAt(i))) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * 过滤特殊字符或者其他非文字类型的字符
	 * 
	 * @param source
	 * @return
	 */
	public static String filterBySurrogate(String source) {
		// 如果不包含，直接返回
		if (!containsSurrogate(source)) {
			return source;
		}

		StringBuilder buf = null;

		int len = source.length();

		for (int i = 0; i < len; i++) {
			
			char codePoint = source.charAt(i);
			if (!Character.isSurrogate(codePoint)) {
				if (buf == null) {
					buf = new StringBuilder(source.length());
				}

				buf.append(codePoint);
			} 
		}

		// 如果没有找到 emoji表情，则返回源字符串
		if (buf == null) {
			return source;
		}
		// 这里的意义在于尽可能少的toString，因为会重新生成字符串
		else if (buf.length() == len) {
			buf = null;
			return source;
		} 
		else {
			return buf.toString();
		}
	}
}
