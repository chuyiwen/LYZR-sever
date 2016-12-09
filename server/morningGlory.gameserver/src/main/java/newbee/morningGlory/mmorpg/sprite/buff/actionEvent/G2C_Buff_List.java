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
package newbee.morningGlory.mmorpg.sprite.buff.actionEvent;

import java.util.List;

import newbee.morningGlory.mmorpg.sprite.buff.MGFightSpriteBuff;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;
import sophia.mmorpg.base.sprite.FightSprite;

public class G2C_Buff_List extends ActionEventBase {
	private List<MGFightSpriteBuff> fightSpriteBuffs;

	public G2C_Buff_List(){
		ziped =(byte)1;
	}
	
	@Override
	protected IoBuffer packBody(IoBuffer paramIoBuffer) {
		int count = fightSpriteBuffs == null ? 0 : fightSpriteBuffs.size();
		paramIoBuffer.put((byte)count);
		if (fightSpriteBuffs != null) {
			for (MGFightSpriteBuff buff : fightSpriteBuffs) {
				FightSprite attacker = buff.getOwner();
				FightSprite target = buff.getAttachFightSprite();
				String buffRefId = buff.getFightSpriteBuffRef().getId();
				long createTime = buff.getCreateTime();
				paramIoBuffer.put(attacker.getSpriteType());
				putString(paramIoBuffer, attacker.getId());
				paramIoBuffer.put(target.getSpriteType());
				putString(paramIoBuffer, target.getId());
				putString(paramIoBuffer, buffRefId);
				paramIoBuffer.putLong(createTime);
				long duration = buff.getDuration();
				paramIoBuffer.putLong(duration);
				long absoluteDuration = buff.getAbsoluteDuration();
				paramIoBuffer.putLong(absoluteDuration);
			}
		}
		return paramIoBuffer;
	}

	@Override
	public void unpackBody(IoBuffer paramIoBuffer) {

	}

	public List<MGFightSpriteBuff> getFightSpriteBuffs() {
		return fightSpriteBuffs;
	}

	public void setFightSpriteBuffs(List<MGFightSpriteBuff> fightSpriteBuffs) {
		this.fightSpriteBuffs = fightSpriteBuffs;
	}

}
