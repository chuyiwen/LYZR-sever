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
package sophia.mmorpg;

import java.util.HashMap;
import java.util.Map;

import sophia.foundation.authentication.Identity;
import sophia.game.component.GameObject;
import sophia.mmorpg.base.scene.DefaultGameSceneProvider;
import sophia.mmorpg.base.scene.GameScene;
import sophia.mmorpg.item.DefaultItemProvider;
import sophia.mmorpg.item.Item;
import sophia.mmorpg.loot.DefaultLootProvider;
import sophia.mmorpg.loot.Loot;
import sophia.mmorpg.monster.DefaultMonsterProvider;
import sophia.mmorpg.monster.Monster;
import sophia.mmorpg.npc.DefaultNpcProvider;
import sophia.mmorpg.npc.Npc;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.itemBag.ItemPair;
import sophia.mmorpg.player.mount.DefaultMountProvider;
import sophia.mmorpg.player.mount.Mount;
import sophia.mmorpg.player.quest.DefaultQuestProvider;
import sophia.mmorpg.player.quest.Quest;
import sophia.mmorpg.pluck.DefaultPluckProvider;
import sophia.mmorpg.pluck.Pluck;

public final class GameObjectFactory {
	private static final Map<Class<? extends GameObject>, GameObjectProvider<? extends GameObject>> map = new HashMap<>();

	static {
		addOrReplace(Item.class, DefaultItemProvider.getInstance());
		addOrReplace(GameScene.class, DefaultGameSceneProvider.getInstance());
		addOrReplace(Quest.class, DefaultQuestProvider.getInstance());
		addOrReplace(Monster.class, DefaultMonsterProvider.getInstance());
		addOrReplace(Mount.class, DefaultMountProvider.getInstance());
		addOrReplace(Loot.class, DefaultLootProvider.getInstance());
		addOrReplace(Npc.class, DefaultNpcProvider.getInstance());
		addOrReplace(Pluck.class, DefaultPluckProvider.getInstance());
	}

	private GameObjectFactory() {
	}

	public static <T extends GameObject> T get(Class<T> type) {
		@SuppressWarnings("unchecked")
		GameObjectProvider<T> gameObjectProvider = (GameObjectProvider<T>) map.get(type);

		if (gameObjectProvider == null) {
			throw new RuntimeException(type.getName() + " not bind provider.");
		}

		return gameObjectProvider.get(type);
	}

	public static <T extends GameObject> T get(Class<T> type, Object... args) {
		@SuppressWarnings("unchecked")
		GameObjectProvider<T> gameObjectProvider = (GameObjectProvider<T>) map.get(type);

		if (gameObjectProvider == null) {
			throw new RuntimeException(type.getName() + " not bind provider.");
		}

		return gameObjectProvider.get(type, args);
	}

	public static <T extends GameObject> void addOrReplace(Class<T> type, GameObjectProvider<T> gameObjectProvider) {
		if (map.get(type) != null) {
			map.remove(type);
		}

		map.put(type, gameObjectProvider);
	}

	public static Item getItem() {
		return get(Item.class);
	}

	public static Item getItem(String itemRefId) {
		return get(Item.class, itemRefId);
	}

	public static Item getItem(String itemRefId, String id) {
		return get(Item.class, itemRefId, id);
	}

	public static Player getPlayer() {
		return get(Player.class);
	}

	public static Player getPlayer(Identity identity, String id, String name) {
		return get(Player.class, identity, id, name);
	}

	public static GameScene getGameScene() {
		return get(GameScene.class);
	}

	public static GameScene getGameScene(String sceneRefId) {
		return get(GameScene.class, sceneRefId);
	}

	public static Monster getMonster(String monsterRefId) {
		return get(Monster.class, monsterRefId);
	}

	public static Quest getQuest() {
		return get(Quest.class);
	}

	public static Quest getQuest(String questRefId) {
		return get(Quest.class, questRefId);
	}
	
	public static Mount getMount() {
		return get(Mount.class);
	}

	public static Mount getMount(String mountRefId) {
		return get(Mount.class, mountRefId);
	}
	
	public static Loot getLoot(ItemPair itemPair) {
		return get(Loot.class, itemPair);
	}
	public static Loot getLoot(Item item) {
		return get(Loot.class, item);
	}
	public static Npc getNpc() {
		return get(Npc.class);
	}
	
	public static Npc getNpc(String NpcRefId) {
		return get(Npc.class, NpcRefId);
	}
	
	public static Pluck getPluck(String pluckRefId){
		return get(Pluck.class, pluckRefId);
	}
}
