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
package sophia.mmorpg.base.scene.mgr;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Future;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import sophia.foundation.task.Task;
import sophia.foundation.util.DebugUtil;
import sophia.foundation.util.Position;
import sophia.game.GameContext;
import sophia.game.GameRoot;
import sophia.game.component.communication.GameEvent;
import sophia.mmorpg.GameObjectFactory;
import sophia.mmorpg.base.ConcreteComponent;
import sophia.mmorpg.base.scene.GameScene;
import sophia.mmorpg.base.scene.GameSceneHelper;
import sophia.mmorpg.base.scene.grid.SceneGrid;
import sophia.mmorpg.base.sprite.FightSprite;
import sophia.mmorpg.item.Item;
import sophia.mmorpg.loot.Loot;
import sophia.mmorpg.loot.LootKind;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.itemBag.ItemPair;
import sophia.mmorpg.player.team.PlayerTeam;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;

import com.google.common.util.concurrent.AbstractIdleService;
import com.google.common.util.concurrent.Service.State;

public final class LootMgrComponent extends ConcreteComponent<GameScene> {
	private static final Logger logger = Logger.getLogger(LootMgrComponent.class);

	private Map<String, Loot> lootMap = new ConcurrentHashMap<>();
	private CopyOnWriteArrayList<Loot> lootList = new CopyOnWriteArrayList<>();
	private LootTimeService lootTimeService = new LootTimeService();

	@Override
	public void ready() {
		State state = lootTimeService.startAndWait();
		if (state == State.RUNNING) {
			logger.debug("LootTimeService started");
		}
		super.ready();
	}

	@Override
	public void suspend() {
		State state = lootTimeService.stopAndWait();
		if (state == State.TERMINATED) {
			logger.debug("LootTimeService stopped");
		}
		super.suspend();
	}

	@Override
	public void destroy() {
		suspend();
		clear();
		super.destroy();
	}

	private void clear() {
		for (String id : lootMap.keySet()) {
			Loot loot = lootMap.get(id);
			GameScene crtScene = loot.getCrtScene();
			GameRoot.getGameObjectManager().removeGameObject(loot);
			loot.getAoiComponent().leaveScene(crtScene);
		}
		lootMap.clear();

		for (Loot loot1 : lootList) {
			GameScene crtScene = loot1.getCrtScene();
			GameRoot.getGameObjectManager().removeGameObject(loot1);
			loot1.getAoiComponent().leaveScene(crtScene);
		}
		lootList.clear();

	}

	public CopyOnWriteArrayList<Loot> getLootList() {
		return lootList;
	}

	public void addLoot(Loot loot) {
		lootMap.put(loot.getId(), loot);
	}

	public void removeLoot(Loot loot) {
		lootMap.remove(loot.getId());
	}

	public Loot getLoot(String lootId) {
		return lootMap.get(lootId);
	}

	public Loot createLoot(ItemPair itemPair) {
		return GameObjectFactory.getLoot(itemPair);
	}

	public Loot createLoot(Item item) {
		return GameObjectFactory.getLoot(item);
	}

	public void enterWorld(Loot loot, GameScene gameScene, int x, int y) {
		if (logger.isDebugEnabled()) {
			logger.debug("enterWorld " + loot);
		}
		GameRoot.getGameObjectManager().addGameObject(loot);
		loot.getAoiComponent().enterScene(gameScene, x, y);
		addLoot(loot);
		lootList.add(loot);
	}

	public void leaveWorld(Loot loot) {
		if (logger.isDebugEnabled()) {
			logger.debug("leaveWorld " + loot);
		}
		GameScene crtScene = loot.getCrtScene();
		GameRoot.getGameObjectManager().removeGameObject(loot);
		loot.getAoiComponent().leaveScene(crtScene);
		removeLoot(loot);
		lootList.remove(loot);
	}

	public final long checkRemove() {
		if (lootList.isEmpty()) {
			return Loot.LifeTime;
		}

		long nextTickTime = Loot.LifeTime;
		long currentTimeMillis = System.currentTimeMillis();
		Iterator<Loot> it = lootList.iterator();
		while (it.hasNext()) {
			Loot loot = it.next();
			long leftTime = Loot.LifeTime - currentTimeMillis + loot.getBornTime();
			if (leftTime > 0) {
				nextTickTime = leftTime;
				break;
			}

			leaveWorld(loot);
		}

		return nextTickTime;
	}

	public void sceneTick(GameEvent<?> event) {
		if (lootMap.isEmpty()) {
			return;
		}

		for (Loot loot : lootMap.values()) {
			try {
				sendGameEvent(event, loot.getId());
			} catch (Exception e) {
				logger.error(DebugUtil.printStack(e));
			}
		}
	}

	private String getOwnerId(FightSprite owner) {
		if (owner instanceof Player) {
			Player player = (Player) owner;
			GameScene crtScene = player.getCrtScene();
			if (StringUtils.equals(crtScene.getRef().getId(), "S013")) {
				return MGPropertyAccesser.getUnionName(player.getProperty());
			}

			PlayerTeam team = player.getPlayerTeamComponent().getTeam();
			if (team != null) {
				return team.getId();
			}
		}

		return owner.getId();
	}

	private int getLootKind(FightSprite owner) {
		if (owner instanceof Player) {
			Player player = (Player) owner;
			GameScene crtScene = player.getCrtScene();
			if (StringUtils.equals(crtScene.getRef().getId(), "S013")) {
				return LootKind.UnionPlayerLoot;
			}

			PlayerTeam team = player.getPlayerTeamComponent().getTeam();
			if (team != null) {
				return LootKind.TeamPlayerLoot;
			}
		}

		return LootKind.SinglePlayerLoot;
	}

	public List<Loot> dropItem(Position center, Item item, FightSprite owner) {
		List<Item> itemList = new ArrayList<>(1);
		itemList.add(item);
		return dropItem(center, itemList, owner);
	}

	public List<Loot> dropItem(Position center, List<Item> itemList, FightSprite owner) {
		GameScene crtScene = getConcreteParent();
		List<Loot> lootList = new ArrayList<>();

		int count = itemList.size();
		List<SceneGrid> gridList = GameSceneHelper.getLootSceneGrids(crtScene, center, count);
		int gridSize = gridList.size();
		if (gridSize == 0) {
			logger.error("dropItem error, can't find lootGrids" + DebugUtil.printStack());
			return lootList;
		}

		int lootKind = getLootKind(owner);
		String ownerId = getOwnerId(owner);

		for (int j = 0; j < count; j++) {
			Item item = itemList.get(j);
			Loot loot = createLoot(item);
			loot.setOwnerId(ownerId);
			loot.setLootKind(lootKind);
			// 若空闲的AOIGrid不够，则放到最后一个AOIGrid
			int index = j > gridSize ? gridSize - 1 : j;
			SceneGrid sceneGrid = gridList.get(index);
			enterWorld(loot, crtScene, sceneGrid.getColumn(), sceneGrid.getRow());
			lootList.add(loot);
		}

		return lootList;
	}

	public List<Loot> dropItemPair(Position center, ItemPair itemPair, int count, FightSprite owner) {
		List<ItemPair> itemPairList = new ArrayList<>(count);
		for (int i = 0; i < count; i++) {
			itemPairList.add(itemPair);
		}

		return dropItemPair(center, itemPairList, owner);
	}

	public List<Loot> dropItemPair(Position center, List<ItemPair> itemPairList, FightSprite owner) {
		GameScene crtScene = getConcreteParent();
		List<Loot> lootList = new ArrayList<>();

		int count = itemPairList.size();
		List<SceneGrid> gridList = GameSceneHelper.getLootSceneGrids(crtScene, center, count);
		int gridSize = gridList.size();
		if (gridSize == 0) {
			logger.error("dropItemPair error, can't find lootGrids" + DebugUtil.printStack());
			return lootList;
		}

		int lootKind = getLootKind(owner);
		String ownerId = getOwnerId(owner);

		for (int j = 0; j < count; j++) {
			ItemPair itemPair = itemPairList.get(j);
			Loot loot = createLoot(itemPair);
			loot.setOwnerId(ownerId);
			loot.setLootKind(lootKind);
			// 若空闲的AOIGrid不够，则放到最后一个AOIGrid
			int index = j > gridSize ? gridSize - 1 : j;
			SceneGrid sceneGrid = gridList.get(index);
			enterWorld(loot, crtScene, sceneGrid.getColumn(), sceneGrid.getRow());
			lootList.add(loot);
		}

		return lootList;
	}

	private final class LootTimeService extends AbstractIdleService {

		private Future<?> scheduleTask;

		@Override
		protected void startUp() throws Exception {
			logger.debug("LootTimeService starting");
			try {
				scheduleTask = GameContext.getTaskManager().scheduleTask(new Task() {
					@Override
					public void run() throws Exception {
						long nextTickTime = 1000;

						try {
							nextTickTime = checkRemove();
						} catch (Exception e) {
							logger.error("LootTimeService error, " + DebugUtil.printStack(e));
						}

						try {
							scheduleTask = GameContext.getTaskManager().scheduleTask(this, nextTickTime);
						} catch (Exception e) {
							logger.error("LootTimeService error, " + DebugUtil.printStack(e));
						}
					}
				}, Loot.LifeTime);
			} catch (Exception e) {
				logger.error("LootTimeService error, " + DebugUtil.printStack(e));
			}
		}

		@Override
		protected void shutDown() throws Exception {
			logger.debug("LootTimeService stopping");
			if (scheduleTask != null) {
				scheduleTask.cancel(true);
			}
		}
	}
}
