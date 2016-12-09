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
package sophia.mmorpg.player.fightSkill.event;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public final class C2G_UseSkill extends ActionEventBase {
	private String skillRefId;
	private byte destType;

	private String targetType;
	private String targetId;
	private int targetX;
	private int targetY;

	private byte direction;

	public C2G_UseSkill() {
		this.actionEventId = FightSkillDefines.C2G_UseSkill;
	}

	@Override
	public void unpackBody(IoBuffer buffer) {
		skillRefId = getString(buffer);
		this.destType = buffer.get();
		if (this.destType == 0) {
			this.setTargetType(getString(buffer));
			this.setTargetId(getString(buffer));
		} else if (this.destType == 1) {
			this.setTargetX(buffer.getInt());
			this.setTargetY(buffer.getInt());
		} else if (this.destType == 2) {
			this.setDirection(buffer.get());
		}
	}

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		putString(buffer, this.skillRefId);
		buffer.put(this.destType);
		if (this.destType == 0) {
			putString(buffer, this.targetType);
			putString(buffer, this.targetId);
		} else if (this.destType == 1) {
			buffer.putInt(targetX);
			buffer.putInt(targetY);
		} else if (this.destType == 2) {
			buffer.put(this.direction);
		}
		return buffer;
	}

	public String getSkillRefId() {
		return skillRefId;
	}

	public void setSkillRefId(String skillRefId) {
		this.skillRefId = skillRefId;
	}

	public byte getDestType() {
		return destType;
	}

	public void setDestType(byte destType) {
		this.destType = destType;
	}

	public String getTargetType() {
		return targetType;
	}

	public void setTargetType(String targetType) {
		this.targetType = targetType;
	}

	public int getTargetX() {
		return targetX;
	}

	public void setTargetX(int targetX) {
		this.targetX = targetX;
	}

	public int getTargetY() {
		return targetY;
	}

	public void setTargetY(int targetY) {
		this.targetY = targetY;
	}

	public String getTargetId() {
		return targetId;
	}

	public void setTargetId(String targetId) {
		this.targetId = targetId;
	}

	@Override
	public String getName() {
		return "使用技能";
	}

	public byte getDirection() {
		return direction;
	}

	public void setDirection(byte direction) {
		this.direction = direction;
	}

	@Override
	public String toString() {
		return "C2G_UseSkill [skillRefId=" + skillRefId + ", destType=" + destType + ", targetType=" + targetType + ", targetId=" + targetId + ", targetX=" + targetX
				+ ", targetY=" + targetY + ", direction=" + direction + "]";
	}
	
}
