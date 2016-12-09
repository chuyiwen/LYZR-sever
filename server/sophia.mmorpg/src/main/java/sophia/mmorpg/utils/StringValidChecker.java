package sophia.mmorpg.utils;

import sophia.foundation.util.SFStringUtil;

public class StringValidChecker {
	private static final char[] filterChars = new char[]{
		'　',	// 非英文空格
		'，',	// 中文逗号
		'?', '\'', '\"', '(', '（', ')', '）', '[', ']', '【', '】', '“', '”', '‘', '’'};
	
	public static boolean isValid(String name) {
		if (SFStringUtil.contains(name) || SFStringUtil.contains(name, filterChars) || SFStringUtil.containsSurrogate(name)) {
			return false;
		}
		
		return true;
	}

}
