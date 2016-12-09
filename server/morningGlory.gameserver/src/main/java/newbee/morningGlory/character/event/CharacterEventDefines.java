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
package newbee.morningGlory.character.event;

import newbee.morningGlory.event.MGEventDefines;
import sophia.foundation.communication.core.MessageFactory;

public final class CharacterEventDefines {
	/** 获取角色 */
	public static final short C2G_CharacterGet = MGEventDefines.Character_Message_Begin + 1;
	/** 获得角色返回 */
	public static final short G2C_CharacterGet = MGEventDefines.Character_Message_Begin + 2;
	/** 创建角色 */
	public static final short C2G_CharacterCreate = MGEventDefines.Character_Message_Begin + 3;
	/** 登录角色 */
	public static final short C2G_CharacterLogin = MGEventDefines.Character_Message_Begin + 5;
	/** 登录成功返回 */
	public static final short G2C_CharacterLogin = MGEventDefines.Character_Message_Begin + 6;
	/** 删除角色 */
	public static final short C2G_CharacterDelete = MGEventDefines.Character_Message_Begin + 7;
	/** 删除角色返回 */
	public static final short G2C_CharacterDelete = MGEventDefines.Character_Message_Begin + 8;
	
	public static void registerActionEvents() {
		MessageFactory.addMessage(C2G_CharacterGet, C2G_CharacterGet.class);
		MessageFactory.addMessage(C2G_CharacterCreate, C2G_CharacterCreate.class);
		MessageFactory.addMessage(C2G_CharacterLogin, C2G_CharacterLogin.class);
		MessageFactory.addMessage(G2C_CharacterGet, G2C_CharacterGet.class);
		MessageFactory.addMessage(G2C_CharacterLogin, G2C_CharacterLogin.class);
		MessageFactory.addMessage(C2G_CharacterDelete, C2G_CharacterDelete.class);
		MessageFactory.addMessage(G2C_CharacterDelete, G2C_CharacterDelete.class);
	}
}
