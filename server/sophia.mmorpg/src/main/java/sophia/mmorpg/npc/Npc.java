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

import sophia.mmorpg.base.sprite.NonFightSprite;
import sophia.mmorpg.base.sprite.SpriteTypeDefine;
import sophia.mmorpg.npc.ref.NpcJobManager;
import sophia.mmorpg.npc.ref.NpcRef;

public class Npc extends NonFightSprite {
	private static final String Npc_GameSprite_Type = Npc.class.getSimpleName();
	
	private NpcRef npcRef;
	
	public Npc() {
	}
	
	@Override
	public String getGameSpriteType() {
		return Npc_GameSprite_Type;
	}
	
	@Override
	public byte getSpriteType(){
		return SpriteTypeDefine.GameSprite_NPC;
	}

	public final NpcRef getNpcRef() {
		return npcRef;
	}

	public final void setNpcRef(NpcRef npcRef) {
		this.npcRef = npcRef;
	}

	public final NpcJobManager getNpcJobManager() {
		return npcRef.getNpcJobManager();
	}
}
