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

import sophia.mmorpg.base.sprite.FightSprite;
import sophia.mmorpg.base.sprite.fightSkill.SkillEffect;

public class SkillEffectDefault implements SkillEffect {
	private FightSprite owner;
	
	public SkillEffectDefault(FightSprite owner) {
		super();
		this.owner = owner;
	}

	@Override
	public byte getSkillEffectType() {
		return SkillEffectType.DEFAULT;
	}

	@Override
	public void writeToBuffer(IoBuffer buffer) {
		buffer.put(getSkillEffectType());
	}

	@Override
	public FightSprite getOwner() {
		return owner;
	}
}
