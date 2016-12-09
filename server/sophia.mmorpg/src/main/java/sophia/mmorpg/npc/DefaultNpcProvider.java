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
package sophia.mmorpg.npc;

import java.util.UUID;

import sophia.game.GameRoot;
import sophia.mmorpg.GameObjectProvider;
import sophia.mmorpg.npc.ref.NpcRef;

public final class DefaultNpcProvider implements GameObjectProvider<Npc> {
	private static final GameObjectProvider<Npc> instance = new DefaultNpcProvider();

	private DefaultNpcProvider() {

	}

	public static final GameObjectProvider<Npc> getInstance() {
		return instance;
	}

	@Override
	public Npc get(Class<Npc> type) {
		Npc npc = new Npc();
		return npc;
	}

	@Override
	public Npc get(Class<Npc> type, Object... args) {
		Npc ret = new Npc();
		String npcRefId = (String) args[0];
		NpcRef npcRef = (NpcRef) GameRoot.getGameRefObjectManager().getManagedObject(npcRefId);
		ret.setId(UUID.randomUUID().toString());
		ret.setNpcRef(npcRef);
		return ret;
	}

}
