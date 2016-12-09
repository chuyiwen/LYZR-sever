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

public class C2G_Scene_Sync_Time extends ActionEventBase {
	// client发送过来的时间戳，单位ms
	private long clientStamp;

	@Override
	public void unpackBody(IoBuffer buffer) {
		setClientStamp(buffer.getLong());
	}

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		buffer.putLong(System.currentTimeMillis());
		return buffer;
	}

	@Override
	public String getName() {
		return "时间同步";
	}

	public long getClientStamp() {
		return clientStamp;
	}

	public void setClientStamp(long clientStamp) {
		this.clientStamp = clientStamp;
	}
}
