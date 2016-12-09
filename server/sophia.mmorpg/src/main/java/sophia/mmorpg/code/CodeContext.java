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
package sophia.mmorpg.code;

import java.util.HashMap;
import java.util.Map;

import com.google.common.base.Strings;

public final class CodeContext {
	
	private static final Map<Integer, String> codeStrings = new HashMap<Integer, String>();
	
	public static int addErrorCode(int code, String desc) {
		int rcode = code | 0x80000000;
		if (codeStrings.containsKey(rcode)) {
			throw new IllegalArgumentException("重复定义了code:" + code + " " + desc);
		}
		codeStrings.put(rcode, desc);
		return rcode;
	}
	
	public static int addSuccessCode(int code, String desc) {
		if (codeStrings.containsKey(code)) {
			throw new IllegalArgumentException("重复定义了code:" + code + " " + desc);
		}
		codeStrings.put(code, desc);
		return code;
	}
	
	public static String description(int code) {
		String result = codeStrings.get(code);
		if (Strings.isNullOrEmpty(result)) {
			return "";
		} else {
			return result;
		}
	}
	
	public static Map<Integer, String> getCodeStrings() {
		return codeStrings;
	}
}
