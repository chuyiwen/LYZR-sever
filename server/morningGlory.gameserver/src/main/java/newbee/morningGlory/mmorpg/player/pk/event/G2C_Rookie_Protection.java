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
package newbee.morningGlory.mmorpg.player.pk.event;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;
import sophia.mmorpg.player.Player;

public class G2C_Rookie_Protection extends ActionEventBase {
	private Player attacker;
	private Player target;

	public G2C_Rookie_Protection() {
		super();
		this.actionEventId = PkEventDefines.G2C_Rookie_Protection;
	}

	public G2C_Rookie_Protection(Player attacker, Player target) {
		super();
		this.setAttacker(attacker);
		this.setTarget(target);
		this.actionEventId = PkEventDefines.G2C_Rookie_Protection;
	}

	@Override
	public void unpackBody(IoBuffer buffer) {
	}

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		putString(buffer, attacker.getId());
		putString(buffer, target.getId());
		buffer.putInt(attacker.getLevel());
		buffer.putInt(target.getLevel());
		return buffer;
	}

	public Player getAttacker() {
		return attacker;
	}

	public void setAttacker(Player attacker) {
		this.attacker = attacker;
	}

	public Player getTarget() {
		return target;
	}

	public void setTarget(Player target) {
		this.target = target;
	}

	@Override
	public String toString() {
		return "G2C_Rookie_Protection [attacker=" + attacker + ", target=" + target + "]";
	}

}
