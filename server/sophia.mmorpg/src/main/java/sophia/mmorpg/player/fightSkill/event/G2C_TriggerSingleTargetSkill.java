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
import sophia.mmorpg.base.sprite.FightSprite;
import sophia.mmorpg.base.sprite.fightSkill.FightSkillResult;
import sophia.mmorpg.base.sprite.fightSkill.SkillEffect;

public class G2C_TriggerSingleTargetSkill extends ActionEventBase {
	private String skillRefId;
	private FightSprite attacker;
	private FightSprite target;
	private FightSkillResult result;

	public G2C_TriggerSingleTargetSkill() {
		ziped = (byte) 1;
	}

	public G2C_TriggerSingleTargetSkill(String skillRefId, FightSprite attacker, FightSprite target, FightSkillResult result) {
		super();
		this.actionEventId = FightSkillDefines.G2C_TriggerSingleTargetSkill;
		this.setSkillRefId(skillRefId);
		this.setAttacker(attacker);
		this.setTarget(target);
		this.setResult(result);
	}

	@Override
	public void unpackBody(IoBuffer buffer) {
	}

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		putString(buffer, skillRefId);
		buffer.put(attacker.getSpriteType());
		putString(buffer, attacker.getId());
		buffer.putShort((short) attacker.getCrtPosition().getX());
		buffer.putShort((short) attacker.getCrtPosition().getY());
		buffer.put(target.getSpriteType());
		putString(buffer, target.getId());
		buffer.putShort((short) target.getCrtPosition().getX());
		buffer.putShort((short) target.getCrtPosition().getY());
		buffer.put((byte) this.result.getSkillEffects().size());
		for (SkillEffect effect : this.result.getSkillEffects()) {
			buffer.put(effect.getOwner() == attacker ? (byte) 0 : (byte) 1);
			effect.writeToBuffer(buffer);
		}

		return buffer;
	}

	public String getSkillRefId() {
		return skillRefId;
	}

	public void setSkillRefId(String skillRefId) {
		this.skillRefId = skillRefId;
	}

	public FightSkillResult getResult() {
		return result;
	}

	public void setResult(FightSkillResult result) {
		this.result = result;
	}

	public FightSprite getAttacker() {
		return attacker;
	}

	public void setAttacker(FightSprite attacker) {
		this.attacker = attacker;
	}

	public FightSprite getTarget() {
		return target;
	}

	public void setTarget(FightSprite target) {
		this.target = target;
	}

}
