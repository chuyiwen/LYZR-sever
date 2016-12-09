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

import newbee.morningGlory.mmorpg.sprite.buff.MGBuffEffectType;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;
import sophia.mmorpg.base.sprite.FightSprite;

public final class G2C_Effect_Buff extends ActionEventBase {

	private byte type;
	private int value;
	private int crtValue;
	private int maxValue;
	private int positionX;
	private int positionY;

	private FightSprite attacker;
	private FightSprite target;

	@Override
	protected IoBuffer packBody(IoBuffer paramIoBuffer) {
		paramIoBuffer.put(attacker.getSpriteType());
		putString(paramIoBuffer, attacker.getId());
		paramIoBuffer.put(target.getSpriteType());
		putString(paramIoBuffer, target.getId());
		paramIoBuffer.put(type);
		paramIoBuffer.putInt(value);
		if (MGBuffEffectType.HP == type || MGBuffEffectType.MP == type) {
			paramIoBuffer.putInt(crtValue);
			paramIoBuffer.putInt(maxValue);
		} else if (MGBuffEffectType.Move == type) {
			paramIoBuffer.putInt(positionX);
			paramIoBuffer.putInt(positionY);
		}

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

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
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

	public int getCrtValue() {
		return crtValue;
	}

	public void setCrtValue(int crtValue) {
		this.crtValue = crtValue;
	}

	public int getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(int maxValue) {
		this.maxValue = maxValue;
	}

	public int getPositionX() {
		return positionX;
	}

	public void setPositionX(int positionX) {
		this.positionX = positionX;
	}

	public int getPositionY() {
		return positionY;
	}

	public void setPositionY(int positionY) {
		this.positionY = positionY;
	}

}
