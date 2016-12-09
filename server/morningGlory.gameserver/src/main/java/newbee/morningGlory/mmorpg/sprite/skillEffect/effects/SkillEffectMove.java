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
package newbee.morningGlory.mmorpg.sprite.skillEffect.effects;

import newbee.morningGlory.mmorpg.sprite.skillEffect.SkillEffectType;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.util.Position;
import sophia.mmorpg.base.sprite.FightSprite;
import sophia.mmorpg.base.sprite.fightSkill.SkillEffect;

public class SkillEffectMove implements SkillEffect {
	private FightSprite owner;
	private Position beforeSkillCastPos;
	private Position afterSkillCastPos;

	public SkillEffectMove(FightSprite owner, Position beforeSkillCastPos,
			Position afterSkillCastPos) {
		super();
		this.owner = owner;
		this.beforeSkillCastPos = beforeSkillCastPos;
		this.afterSkillCastPos = afterSkillCastPos;
	}

	@Override
	public byte getSkillEffectType() {
		return SkillEffectType.MOVE;
	}

	@Override
	public void writeToBuffer(IoBuffer buffer) {
		buffer.put(getSkillEffectType());
		buffer.putShort((short) beforeSkillCastPos.getX());
		buffer.putShort((short) beforeSkillCastPos.getY());
		buffer.putShort((short) afterSkillCastPos.getX());
		buffer.putShort((short) afterSkillCastPos.getY());
	}

	@Override
	public FightSprite getOwner() {
		return owner;
	}

	public Position getBeforeSkillCastPos() {
		return beforeSkillCastPos;
	}

	public Position getAfterSkillCastPos() {
		return afterSkillCastPos;
	}

	@Override
	public String toString() {
		return "SkillEffectMove [owner=" + owner + ", beforeSkillCastPos="
				+ beforeSkillCastPos + ", afterSkillCastPos="
				+ afterSkillCastPos + "]";
	}
}
