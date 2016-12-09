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

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;
import sophia.mmorpg.base.sprite.FightSprite;

public class G2C_Attach_Buff extends ActionEventBase {
	private byte type;
	private String buffRefId;
	private long createTime;
	private FightSprite attacker;
	private FightSprite target;
	private long duration;
	private long absoluteDuration;

	

	@Override
	protected IoBuffer packBody(IoBuffer paramIoBuffer) {
		paramIoBuffer.put(attacker.getSpriteType());
		putString(paramIoBuffer, attacker.getId());
		paramIoBuffer.put(target.getSpriteType());
		putString(paramIoBuffer, target.getId());
		paramIoBuffer.put(type);
		putString(paramIoBuffer, buffRefId);
		paramIoBuffer.putLong(createTime);
		paramIoBuffer.putLong(duration);
		paramIoBuffer.putLong(absoluteDuration);
		return paramIoBuffer;
	}

	@Override
	public void unpackBody(IoBuffer paramIoBuffer) {
		// TODO Auto-generated method stub

	}

	public byte getType() {
		return type;
	}

	public void setType(byte type) {
		this.type = type;
	}

	public String getBuffRefId() {
		return buffRefId;
	}

	public void setBuffRefId(String buffRefId) {
		this.buffRefId = buffRefId;
	}

	public FightSprite getTarget() {
		return target;
	}

	public void setTarget(FightSprite target) {
		this.target = target;
	}

	public FightSprite getAttacker() {
		return attacker;
	}

	public void setAttacker(FightSprite attacker) {
		this.attacker = attacker;
	}

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	public long getDuration() {
		return duration;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}
	public long getAbsoluteDuration() {
		return absoluteDuration;
	}

	public void setAbsoluteDuration(long absoluteDuration) {
		this.absoluteDuration = absoluteDuration;
	}
}
