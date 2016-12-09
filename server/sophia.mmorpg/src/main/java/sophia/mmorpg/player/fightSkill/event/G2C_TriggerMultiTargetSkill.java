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

import java.util.ArrayList;
import java.util.Collection;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;
import sophia.foundation.util.Position;
import sophia.mmorpg.base.sprite.FightSprite;
import sophia.mmorpg.base.sprite.fightSkill.FightSkillResult;
import sophia.mmorpg.base.sprite.fightSkill.SkillEffect;

public class G2C_TriggerMultiTargetSkill extends ActionEventBase {
	private String skillRefId;
	private FightSprite attacker;
	private Collection<FightSkillResult> results = new ArrayList<>();
	
	//client decides
	private byte destType;
	private int targetX;
	private int targetY;
	
	private byte direction;

	public G2C_TriggerMultiTargetSkill() {
		ziped =(byte)1;
	}

	public G2C_TriggerMultiTargetSkill(String skillRefId, FightSprite attacker) {
		super();
		this.actionEventId = FightSkillDefines.G2C_TriggerMultiTargetSkill;
		this.skillRefId = skillRefId;
		this.attacker = attacker;
	}
	
	public G2C_TriggerMultiTargetSkill(String skillRefId, FightSprite attacker, int x, int y) {
		super();
		this.actionEventId = FightSkillDefines.G2C_TriggerMultiTargetSkill;
		this.skillRefId = skillRefId;
		this.attacker = attacker;
		this.targetX = x;
		this.targetY = y;
		this.destType = 1; // 1 for grid skill
		this.direction = -1; // -1 for null
	}
	
	public G2C_TriggerMultiTargetSkill(String skillRefId, FightSprite attacker, Position pos) {
		super();
		this.actionEventId = FightSkillDefines.G2C_TriggerMultiTargetSkill;
		this.skillRefId = skillRefId;
		this.attacker = attacker;
		this.targetX = pos.getX();
		this.targetY = pos.getY();
		this.destType = 1; // 1 for grid skill
		this.direction = -1; // -1 for null
	}
	
	public G2C_TriggerMultiTargetSkill(String skillRefId, FightSprite attacker, byte direction) {
		super();
		this.actionEventId = FightSkillDefines.G2C_TriggerMultiTargetSkill;
		this.skillRefId = skillRefId;
		this.attacker = attacker;
		this.direction = direction;
		this.destType = 2; // 2 for direction skill
		this.targetX = -1; // -1 for null
		this.targetY = -1; // -1 for null 
	}

	public G2C_TriggerMultiTargetSkill(String skillRefId, FightSprite attacker, Collection<FightSkillResult> results) {
		super();
		this.actionEventId = FightSkillDefines.G2C_TriggerMultiTargetSkill;
		this.skillRefId = skillRefId;
		this.attacker = attacker;
		this.results = results;
	}

	@Override
	public void unpackBody(IoBuffer buffer) {

	}

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		putString(buffer, skillRefId);
		buffer.put(attacker.getSpriteType());
		putString(buffer, attacker.getId());
		
		buffer.put(destType);
		if(destType == 1) {
			buffer.putShort((short)targetX);
			buffer.putShort((short)targetY);
		} else if (destType == 2) {
			buffer.put(direction);
		}
		
		buffer.put((byte)results.size());
		for (FightSkillResult result : results) {
			buffer.put(result.getTarget().getSpriteType());
			putString(buffer, result.getTarget().getId());
			buffer.put((byte)result.getSkillEffects().size());
			for (SkillEffect effect : result.getSkillEffects()) {
				buffer.put(effect.getOwner() == attacker ? (byte) 0 : (byte) 1);
				effect.writeToBuffer(buffer);
			}

		}

		return buffer;
	}

	public String getSkillRefId() {
		return skillRefId;
	}

	public void setSkillRefId(String skillRefId) {
		this.skillRefId = skillRefId;
	}

	public Collection<FightSkillResult> getResults() {
		return results;
	}

	public void setResults(Collection<FightSkillResult> results) {
		this.results = results;
	}

	public FightSprite getAttacker() {
		return attacker;
	}

	public void setAttacker(FightSprite attacker) {
		this.attacker = attacker;
	}

	public void addSkillResult(FightSkillResult result) {
		this.results.add(result);
	}

	public byte getDestType() {
		return destType;
	}

	public void setDestType(byte destType) {
		this.destType = destType;
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

	public byte getDirection() {
		return direction;
	}

	public void setDirection(byte direction) {
		this.direction = direction;
	}

	@Override
	public String toString() {
		return "G2C_TriggerMultiTargetSkill [skillRefId=" + skillRefId + ", attacker=" + attacker + ", results=" + results + ", destType=" + destType + ", targetX=" + targetX
				+ ", targetY=" + targetY + ", direction=" + direction + "]";
	}

}
