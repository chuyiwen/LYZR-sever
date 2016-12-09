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
package sophia.mmorpg.player.worldBossMsg.event;

import java.util.List;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;
import sophia.mmorpg.player.worldBossMsg.WorldBoss;
import sophia.mmorpg.player.worldBossMsg.WorldBossMsgRef;

public class G2C_Boss_List extends ActionEventBase {

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		List<WorldBossMsgRef> bossSet = WorldBoss.getBossSendList();
		buffer.put((byte) bossSet.size());
		for (WorldBossMsgRef worldBossMsgRef : bossSet) {			
			String monsterRefId = worldBossMsgRef.getId();
			long nextRefreshTime = WorldBoss.getNextRefreshTime(worldBossMsgRef);
			putString(buffer, monsterRefId);			
			long crtTime = System.currentTimeMillis();
			long remainTime = nextRefreshTime - crtTime;
			remainTime = remainTime < 0 ? 0 : remainTime;
			remainTime = remainTime / 1000l;				
			buffer.putInt((int)remainTime);
				
		}
		return buffer;
	}

	@Override
	public void unpackBody(IoBuffer arg0) {

	}

}
