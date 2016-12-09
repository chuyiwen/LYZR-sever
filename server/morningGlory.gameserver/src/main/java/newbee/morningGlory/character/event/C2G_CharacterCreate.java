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

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class C2G_CharacterCreate extends ActionEventBase {

	private byte gender;
	private byte profession;
	private String characterName;
	
	@Override
	public void unpackBody(IoBuffer buffer) {
		gender = buffer.get();
		profession = buffer.get();
		characterName = getString(buffer);
	}

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		buffer.put(gender);
		buffer.put(profession);
		putString(buffer, characterName);
		return buffer;
	}
	
	@Override
	public String toString() {
		return "C2G_RegEvent@gender:" + gender + ",name:" + characterName;
	}
	
	@Override
	public String getName() {
		return "创建角色";
	}

	public byte getGender() {
		return gender;
	}

	public void setGender(byte gender) {
		this.gender = gender;
	}

	public String getCharacterName() {
		return characterName;
	}

	public void setCharacterName(String characterName) {
		this.characterName = characterName;
	}

	public byte getProfession() {
		return profession;
	}

	public void setProfession(byte profession) {
		this.profession = profession;
	}
}
