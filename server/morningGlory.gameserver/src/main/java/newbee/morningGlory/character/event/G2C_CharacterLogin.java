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

import org.apache.log4j.Logger;
import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;
import sophia.foundation.property.PropertyDictionary;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.property.PlayerConfig;

public class G2C_CharacterLogin extends ActionEventBase {

	private static final Logger logger = Logger.getLogger(G2C_CharacterLogin.class);
	
	private PropertyDictionary result = new PropertyDictionary();

	private Player player;

	@Override
	public void unpackBody(IoBuffer buffer) {
		getString(buffer);
		short length = buffer.getShort();
		byte[] pdData = new byte[length];
		buffer.get(pdData);
		result.loadDictionary(pdData);
	}

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		putString(buffer, player.getId());		
		result = PlayerConfig.getPdToClientFromPlayerPd(player);		
		if (logger.isDebugEnabled()) {
			logger.debug("G2C_Character_Login " + result);
		}
		
		byte[] pdData = result.toByteArray();
		buffer.putShort((short) pdData.length);
		buffer.put(pdData);
	
		return buffer;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}
	
	public PropertyDictionary getProperty() {
		return result;
	}
}
