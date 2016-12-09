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
package sophia.mmorpg.monster.event;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class C2G_Monster_OwnerTransfer extends ActionEventBase {
	private String monsterId;

	public C2G_Monster_OwnerTransfer() {
		super();
		this.actionEventId = MonsterEventDefines.C2G_Monster_OwnerTransfer;
	}

	public C2G_Monster_OwnerTransfer(String monsterId) {
		super();
		this.setMonsterId(monsterId);
		this.actionEventId = MonsterEventDefines.C2G_Monster_OwnerTransfer;
	}

	@Override
	public void unpackBody(IoBuffer buffer) {
		this.setMonsterId(getString(buffer));

	}

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		putString(buffer, monsterId);
		return buffer;
	}

	public String getMonsterId() {
		return monsterId;
	}

	public void setMonsterId(String monsterId) {
		this.monsterId = monsterId;
	}

}
