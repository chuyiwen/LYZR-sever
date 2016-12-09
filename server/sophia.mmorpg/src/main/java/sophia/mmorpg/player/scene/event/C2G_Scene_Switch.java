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
package sophia.mmorpg.player.scene.event;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class C2G_Scene_Switch extends ActionEventBase {
	private int switchId = 0;
	
	@Override
	public void unpackBody(IoBuffer buffer) {
		switchId = buffer.getInt();
	}

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		buffer.putInt(switchId);
		return buffer;
	}

	@Override
	public String getName() {
		return "切换场景";
	}

	public int getSwitchId() {
		return switchId;
	}

	public void setSwitchId(int switchId) {
		this.switchId = switchId;
	}

}
