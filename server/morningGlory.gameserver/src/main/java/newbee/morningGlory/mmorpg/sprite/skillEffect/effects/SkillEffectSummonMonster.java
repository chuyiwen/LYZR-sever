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

import java.util.ArrayList;
import java.util.List;

import newbee.morningGlory.mmorpg.sprite.skillEffect.SkillEffectType;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.util.IoBufferUtil;
import sophia.mmorpg.base.sprite.FightSprite;
import sophia.mmorpg.base.sprite.fightSkill.SkillEffect;

public class SkillEffectSummonMonster implements SkillEffect {
	private FightSprite owner;
	private List<String> monsters = new ArrayList<>();

	public SkillEffectSummonMonster(List<String> mosterIds) {
		this.monsters = mosterIds;
	}
	
	@Override
	public byte getSkillEffectType() {
		return SkillEffectType.SUMMON_MONSTER;
	}

	@Override
	public void writeToBuffer(IoBuffer buffer) {
		buffer.put(getSkillEffectType());
		buffer.put((byte)monsters.size());
		for (String monsterId : monsters) {
			IoBufferUtil.putString(buffer, monsterId);
		}

	}

	@Override
	public FightSprite getOwner() {
		return owner;
	}

	public List<String> getMonsters() {
		return monsters;
	}

	public void setMonsters(List<String> monsters) {
		this.monsters = monsters;
	}

}
