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
package newbee.morningGlory.character;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import newbee.morningGlory.character.event.CharacterEventDefines;
import newbee.morningGlory.character.event.G2C_CharacterGet;
import sophia.foundation.authentication.Identity;
import sophia.foundation.communication.core.MessageFactory;
import sophia.game.GameRoot;
import sophia.mmorpg.player.persistence.PlayerDAO;
import sophia.mmorpg.player.property.CharacterInfo;

public final class CharacterGet {
	
	public static void characterGet(Identity identity) {
		List<CharacterInfo> charList = PlayerDAO.getInstance().selectPlayerListByIdentity(identity);
		G2C_CharacterGet res = MessageFactory.getConcreteMessage(CharacterEventDefines.G2C_CharacterGet);
		Collections.sort(charList, new Comparator<CharacterInfo>() {
			@Override
			public int compare(CharacterInfo o1, CharacterInfo o2) {
				Long lastLoginTime0 = o1.getLastLoginTime();
				Long lastLoginTime1 = o2.getLastLoginTime();
				return lastLoginTime1.compareTo(lastLoginTime0);
			}
		});
		res.setCharList(charList);
		GameRoot.sendMessage(identity, res);
	}
}
