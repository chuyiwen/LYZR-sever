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

public final class MMORPGSuccessCode {
	public static final int CODE_SUCCESS = addCode(0, "成功");
	
	public static final int CODE_AUTH_SUCCESS = addCode(1, "身份合法.验证成功");
	
	////////////////////MMORPG Success Code Range [300, 2000] ///////////////////////
	
	// skill
	public static final int CODE_SKILL_BEGIN = 300;
	public static final int CODE_SKILL_SUCCESS = addCode(CODE_SKILL_BEGIN + 1, "技能使用成功");
	
	public static final void initialize() {
		
	}
	public static int addCode(int code, String desc) {
		return CodeContext.addSuccessCode(code, desc);
	}
}
