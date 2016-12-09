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
package sophia.mmorpg.monster.drop;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import sophia.foundation.communication.core.MessageFactory;
import sophia.foundation.util.Position;
import sophia.game.GameRoot;
import sophia.mmorpg.base.scene.GameScene;
import sophia.mmorpg.base.scene.mgr.LootMgrComponent;
import sophia.mmorpg.base.sprite.FightSprite;
import sophia.mmorpg.equipmentSmith.smith.xiLian.ItemWashFacade;
import sophia.mmorpg.item.Item;
import sophia.mmorpg.loot.Loot;
import sophia.mmorpg.monster.Monster;
import sophia.mmorpg.monster.ref.drop.DirectDropRef;
import sophia.mmorpg.monster.ref.drop.DropEntryRef;
import sophia.mmorpg.monster.ref.drop.LevelDropRef;
import sophia.mmorpg.monster.ref.drop.MonsterDropRef;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.itemBag.ItemPair;
import sophia.mmorpg.player.scene.event.G2C_Scene_LootInfo;
import sophia.mmorpg.player.scene.event.SceneEventDefines;
import sophia.mmorpg.player.worldBossMsg.WorldBoss;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;
import sophia.mmorpg.utils.SFRandomUtils;

import com.google.common.base.Preconditions;

public final class MonsterDropMgr {

	private static final Logger logger = Logger.getLogger(MonsterDropMgr.class);

	private static MonsterDropRef monsterDropRef;

	private static String monsterDropRefId;

	public static String getMonsterDropRefId() {
		return monsterDropRefId;
	}

	public static void setMonsterDropRefId(String monsterDropRefId) {
		MonsterDropMgr.monsterDropRefId = monsterDropRefId;
	}

	public static MonsterDropRef getMonsterDropRef() {
		return monsterDropRef;
	}

	public static void setMonsterDropRef(MonsterDropRef dropRef) {
		monsterDropRef = dropRef;
	}

	private static void levelDrop(Monster monster, List<ItemPair> itemPairList) {
		List<LevelDropRef> levelDrop = monsterDropRef.getLevelDrop();

		int i = 0;
		int level = monster.getLevel();

		do {

			LevelDropRef levelDropRef = levelDrop.get(i);
			if (levelDropRef.getMinLevel() > level) {
				break;
			}

			if (levelDropRef.getMaxLevel() < level) {
				continue;
			}

			for (DropEntryRef dropEntryRef : levelDropRef.getLevelDropRefList()) {
				if (dropEntryRef.randDropSuccess()) {
					itemPairList.addAll(dropEntryRef.randItemPairList());
				}
			}

		} while (++i < levelDrop.size());

		if (logger.isDebugEnabled()) {
			logger.debug("levelDrop item count " + itemPairList.size() + " attacker level " + level);
			logger.debug(itemPairList);
		}
	}

	private static void monsterDrop(Monster monster, List<ItemPair> itemPairList) {
		String monsterRefId = monster.getMonsterRef().getId();
		DirectDropRef directDropRef = monsterDropRef.getMonsterDrop().get(monsterRefId);

		if (directDropRef == null) {
			return;
		}

		for (DropEntryRef dropEntryRef : directDropRef.getDropRefList()) {
			if (dropEntryRef.randDropSuccess()) {
				itemPairList.addAll(dropEntryRef.randItemPairList());
			}
		}

		if (logger.isDebugEnabled()) {
			logger.debug("monsterDrop item count " + itemPairList.size());
		}
	}

	public static void generateLoot(FightSprite owner, Monster monster) {

		Preconditions.checkNotNull(owner, "owner can't be null");

		List<ItemPair> itemPairList = new ArrayList<>();

		// 怪物掉落
		monsterDrop(monster, itemPairList);

		// 没有怪物掉落, 产生等级掉落
		if (itemPairList.size() == 0) {
			levelDrop(monster, itemPairList);
		}

		// 产生掉落物
		int size = itemPairList.size();
		if (size == 0) {
			return;
		}

		GameScene crtScene = monster.getCrtScene();
		Position crtPosition = monster.getCrtPosition();
		LootMgrComponent lootManager = crtScene.getLootMgrComponent();
		List<Item> itemList = new ArrayList<>();
		Iterator<ItemPair> iterator = itemPairList.iterator();
		while (iterator.hasNext()) {
			ItemPair itemPair = iterator.next();
			if (itemPair.isUnPropsItem()) {
				continue;
			}

			Item item = itemPair.convertItem();
			if (item.isEquip()) {
				washEquipItem(item);
			}

			itemList.add(item);
			iterator.remove();
		}

		
		List<Loot> lootList = new ArrayList<>();
		if (!itemList.isEmpty()) {
			lootList.addAll(lootManager.dropItem(crtPosition, itemList, owner));
		}
		
		if (!itemPairList.isEmpty()) {
			lootList.addAll(lootManager.dropItemPair(crtPosition, itemPairList, owner));
		}

		if (!lootList.isEmpty() && owner instanceof Player) {
			notifyToClient((Player) owner, lootList);
			WorldBoss.sendCherishPropsToWorld(owner, monster, lootList);
		}
	}

	private static void washEquipItem(Item item) {
		// 30级要求
		if (MGPropertyAccesser.getEquipLevel(item.getItemRef().getProperty()) < 30) {
			return;
		}

		// 40%概率
		if (SFRandomUtils.random(1, 10000) > 4000) {
			return;
		}

		ItemWashFacade.washItem(item);
	}

	private static void notifyToClient(Player player, List<Loot> lootList) {
		G2C_Scene_LootInfo rep = MessageFactory.getConcreteMessage(SceneEventDefines.G2C_Scene_LootInfo);
		rep.setLootList(lootList);
		GameRoot.sendMessage(player.getIdentity(), rep);
	}
}
