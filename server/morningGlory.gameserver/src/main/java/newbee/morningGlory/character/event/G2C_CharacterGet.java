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

import java.util.ArrayList;
import java.util.List;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;
import sophia.mmorpg.MMORPGContext;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.PlayerManager;
import sophia.mmorpg.player.property.CharacterInfo;

public class G2C_CharacterGet extends ActionEventBase {

	private List<CharacterInfo> charList = new ArrayList<>();

	@Override
	public void unpackBody(IoBuffer buffer) {
		byte size = buffer.get();
		for (int i = 0; i < size; i++) {
			CharacterInfo charInfo = new CharacterInfo();
			charInfo.setId(getString(buffer));
			charInfo.setName(getString(buffer));
			charInfo.setLevel(buffer.getInt());
			charInfo.setGender(buffer.get());
			charInfo.setProfession(buffer.get());
			
			charList.add(charInfo);
		}
	}

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		PlayerManager playerManager = MMORPGContext.getPlayerComponent().getPlayerManager();
		buffer.put((byte) charList.size());
		for (CharacterInfo charInfo : charList) {
			putString(buffer, charInfo.getId());
			putString(buffer, charInfo.getName());
			// 把玩家最新的等级给拿出来
			Player player = playerManager.getOnlinePlayer(charInfo.getId());
			if (player != null) {
				//buffer.putInt(player.getLevel());
				buffer.putShort((short) player.getLevel());
			} else {
				//buffer.putInt(charInfo.getLevel());
				buffer.putShort((short) charInfo.getLevel());
			}
			
			buffer.put(charInfo.getGender());
			buffer.put(charInfo.getProfession());
		}
		return buffer;
	}

	@Override
	public String toString() {
		return "charList size " + charList.size();
	}

	public List<CharacterInfo> getCharList() {
		return charList;
	}

	public void setCharList(List<CharacterInfo> charList) {
		this.charList = charList;
	}
}
