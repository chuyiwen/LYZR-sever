package sophia.mmorpg.player.worldBossMsg;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;

import sophia.foundation.communication.core.MessageFactory;
import sophia.game.GameRoot;
import sophia.game.ref.GameRefObject;
import sophia.mmorpg.base.scene.GameScene;
import sophia.mmorpg.base.scene.ref.SceneMonsterRefData;
import sophia.mmorpg.base.scene.ref.SceneRef;
import sophia.mmorpg.base.sprite.FightSprite;
import sophia.mmorpg.item.ref.ItemRef;
import sophia.mmorpg.item.ref.UnPropsItemRef;
import sophia.mmorpg.loot.Loot;
import sophia.mmorpg.monster.Monster;
import sophia.mmorpg.monster.ref.MonsterRef;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.chat.PlayerChatFacade;
import sophia.mmorpg.player.chat.sysytem.SystemPromptFacade;
import sophia.mmorpg.player.itemBag.ItemPair;
import sophia.mmorpg.player.worldBossMsg.event.G2C_Boss_List;
import sophia.mmorpg.player.worldBossMsg.event.G2C_Boss_Refresh;
import sophia.mmorpg.player.worldBossMsg.event.WorldBossDefines;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;
import sophia.mmorpg.utils.SFRandomUtils;
import sophia.mmorpg.world.ActionEventFacade;

public final class WorldBoss {

	public static final byte SCENE_BOSS = 0;

	public static final byte ACTIVITY_BOSS = 1;

	private static final ConcurrentHashMap<String, Long> bosses = new ConcurrentHashMap<>();

	private static final List<WorldBossMsgRef> bossSet = new ArrayList<>();

	private static final Map<String, List<Monster>> thiefs = new HashMap<String, List<Monster>>();

	private static final Set<String> sceneActivitys = new HashSet<>();

	private WorldBoss() {
	}

	public static void sendBossList() {
		G2C_Boss_List res = MessageFactory.getConcreteMessage(WorldBossDefines.G2C_Boss_List);
		ActionEventFacade.sendMessageToWorld(res);
	}

	// ---------------- 活动boss--------------------
	public static void addSceneActivityRefID(String sceneRefId) {
		sceneActivitys.add(sceneRefId);
		SceneRef sceneRef = (SceneRef) GameRoot.getGameRefObjectManager().getManagedObject(sceneRefId);
		List<SceneMonsterRefData> monsterRefDatas = sceneRef.getMonsterRefDatas();
		List<WorldBossMsgRef> worldBoss = getWorldBossBySceneRefId(sceneRefId);
		for (SceneMonsterRefData sceneMonsterRefData : monsterRefDatas) {
			String monsterRefId = sceneMonsterRefData.getMonsterRefId();
			for (WorldBossMsgRef ref : worldBoss) {
				if (StringUtils.equals(monsterRefId, ref.getMonsterRefId())) {
					String timingRefresh = sceneMonsterRefData.getTimingRefresh();
					long nextRefreshTime = getNextRefreshTime(timingRefresh, monsterRefId, sceneRefId);
					replaceMonsterRefId(monsterRefId, nextRefreshTime, sceneRefId);
				}
			}
		}

	}

	public static void removeSceneActivityRefID(String sceneRefId) {
		sceneActivitys.remove(sceneRefId);
	}

	public static boolean isContainActivityScene(String sceneRefId) {
		return sceneActivitys.contains(sceneRefId);
	}

	public static WorldBossMsgRef getWorldBossRef(String monsterRefId, String gameSceneRefId) {
		for (WorldBossMsgRef bossRef : bossSet) {
			if (StringUtils.equals(bossRef.getMonsterRefId(), monsterRefId) && StringUtils.equals(bossRef.getGameSceneRefId(), gameSceneRefId)) {
				return bossRef;
			}
		}
		return null;
	}

	/**
	 * 更新世界boss刷新时间
	 * 
	 * @param monsterRefId
	 * @param nextRefreshTime
	 * @param gameSceneRefId
	 */
	public static void replaceMonsterRefId(String monsterRefId, long nextRefreshTime, String gameSceneRefId) {
		if (StringUtils.isEmpty(monsterRefId)) {
			return;
		}

		MonsterRef ref = (MonsterRef) GameRoot.getGameRefObjectManager().getManagedObject(monsterRefId);
		if (ref == null) {
			return;
		}
		String bossRefId = null;
		for (WorldBossMsgRef bossRef : bossSet) {
			if (StringUtils.equals(bossRef.getMonsterRefId(), monsterRefId) && StringUtils.equals(bossRef.getGameSceneRefId(), gameSceneRefId)) {
				bossRefId = bossRef.getId();
				break;
			}
		}
		if (StringUtils.isNotEmpty(bossRefId)) {
			bosses.replace(bossRefId, nextRefreshTime);
		}
	}

	/**
	 * 添加世界boss 和刷新时间
	 * 
	 * @param bossRefId
	 * @param nextRefreshTime
	 */
	public static void addMonsterRefId(String bossRefId, long nextRefreshTime) {
		bosses.put(bossRefId, nextRefreshTime);
	}

	/**
	 * 获取指定世界boss下一次刷新时间
	 * 
	 * @param worldBossMsgRef
	 * @return
	 */
	public static long getNextRefreshTime(WorldBossMsgRef worldBossMsgRef) {
		if (worldBossMsgRef != null) {
			return bosses.get(worldBossMsgRef.getId());
		}
		return 0l;
	}

	/**
	 * 是否包含指定场景的指定boss
	 * 
	 * @param monsterRefId
	 * @param gameSceneRefId
	 * @return
	 */
	public static boolean isContainMonster(String monsterRefId, String gameSceneRefId) {
		MonsterRef ref = (MonsterRef) GameRoot.getGameRefObjectManager().getManagedObject(monsterRefId);
		if (ref == null) {
			return false;
		}
		String bossRefId = null;
		for (WorldBossMsgRef bossRef : bossSet) {
			if (StringUtils.equals(bossRef.getMonsterRefId(), monsterRefId) && StringUtils.equals(gameSceneRefId, bossRef.getGameSceneRefId())) {
				bossRefId = bossRef.getId();
				break;
			}
		}
		if (bossRefId == null) {
			return false;
		}
		return bosses.containsKey(bossRefId);
	}

	/**
	 * 排序世界boss
	 */
	public static void sortBosses() {
		Collections.sort(bossSet, new Comparator<WorldBossMsgRef>() {
			@Override
			public int compare(WorldBossMsgRef o1, WorldBossMsgRef o2) {
				int sortId1 = MGPropertyAccesser.getItemSortId(o1.getProperty());
				int sortId2 = MGPropertyAccesser.getItemSortId(o2.getProperty());
				return sortId1 - sortId2;
			}
		});
		Collections.sort(bossSet, new Comparator<WorldBossMsgRef>() {
			@Override
			public int compare(WorldBossMsgRef o1, WorldBossMsgRef o2) {
				int sortId1 = MGPropertyAccesser.getKind(o1.getProperty());
				int sortId2 = MGPropertyAccesser.getKind(o2.getProperty());
				return sortId2 - sortId1;
			}
		});
	}

	public static void addWorldBossRef(WorldBossMsgRef ref) {
		if (ref == null) {
			return;
		}

		bossSet.add(ref);
	}

	/**
	 * 通过场景应用id 获取世界boss列表
	 * 
	 * @param sceneRefId
	 * @return
	 */
	public static List<WorldBossMsgRef> getWorldBossBySceneRefId(String sceneRefId) {
		List<WorldBossMsgRef> worldBoss = new ArrayList<WorldBossMsgRef>();
		for (WorldBossMsgRef worldBossMsgRef : bossSet) {
			if (StringUtils.equals(sceneRefId, worldBossMsgRef.getGameSceneRefId())) {
				worldBoss.add(worldBossMsgRef);
			}
		}

		return worldBoss;
	}

	/**
	 * 获取发送客户端的boss列表
	 * 
	 * @return
	 */
	public static List<WorldBossMsgRef> getBossSendList() {
		List<WorldBossMsgRef> bossSendSet = new ArrayList<>();
		for (WorldBossMsgRef worldBossMsgRef : bossSet) {
			if (worldBossMsgRef.isActivityBoss() && !WorldBoss.isContainActivityScene(worldBossMsgRef.getGameSceneRefId())) {
				continue;
			}
			bossSendSet.add(worldBossMsgRef);
		}
		return bossSendSet;
	}

	/**
	 * 是否走马灯
	 * 
	 * @param monsterRefId
	 * @param gameSceneRefId
	 * @return
	 */
	public static boolean isScrollNotice(String monsterRefId, String gameSceneRefId) {
		for (Iterator<WorldBossMsgRef> iter = bossSet.iterator(); iter.hasNext();) {
			WorldBossMsgRef bossRef = iter.next();
			if (StringUtils.equals(bossRef.getMonsterRefId(), monsterRefId) && StringUtils.equals(gameSceneRefId, bossRef.getGameSceneRefId())) {
				return bossRef.isScrollNotice();
			}
		}
		return false;
	}

	/**
	 * 是否闪烁
	 * 
	 * @param monsterRefId
	 * @param gameSceneRefId
	 * @return
	 */
	public static boolean isTwinkle(String monsterRefId, String gameSceneRefId) {
		for (Iterator<WorldBossMsgRef> iter = bossSet.iterator(); iter.hasNext();) {
			WorldBossMsgRef bossRef = iter.next();
			if (StringUtils.equals(bossRef.getMonsterRefId(), monsterRefId) && StringUtils.equals(gameSceneRefId, bossRef.getGameSceneRefId())) {
				return bossRef.isTwinkle();
			}
		}
		return false;
	}

	public static long getNextRefreshTime(String timingRefresh, String monsterRefId, String sceneRefId) {
		long crtTime = System.currentTimeMillis();
		long nextRefreshTime = WorldBoss.getNextRefreshTime(timingRefresh, crtTime);

		if (nextRefreshTime == 0) {
			WorldBossMsgRef worldBossRef = WorldBoss.getWorldBossRef(monsterRefId, sceneRefId);
			int refreshTime = worldBossRef.getRefreshTime() * 60 * 1000;
			long setTime = refreshTime + crtTime;
			nextRefreshTime = WorldBoss.getNextRefreshTime(timingRefresh, setTime);
		}
		return nextRefreshTime;
	}

	private static long getNextRefreshTime(String timingRefresh, long setTime) {
		if (StringUtils.isEmpty(timingRefresh)) {
			return 0;
		}
		String[] times = timingRefresh.split("&");
		if (times.length == 0) {
			return 0;
		}
		Calendar crtCalendar = Calendar.getInstance();
		crtCalendar.setTimeInMillis(setTime);
		long nextRefreshTime = 0l;
		long subTime = 0l;
		long min = 0l;
		long crtTime = System.currentTimeMillis();
		int year = crtCalendar.get(Calendar.YEAR);
		int month = crtCalendar.get(Calendar.MONTH);
		int date = crtCalendar.get(Calendar.DAY_OF_MONTH);
		for (String time : times) {
			String[] refreshTimes = time.split(":");
			int hour = Integer.parseInt(refreshTimes[0]);
			int minute = Integer.parseInt(refreshTimes[1]);
			int second = Integer.parseInt(refreshTimes[2]) + 3; // 怪物刷新复活有3秒容忍时间

			crtCalendar.set(year, month, date, hour, minute, second);
			long nextTime = crtCalendar.getTimeInMillis();

			subTime = nextTime - crtTime;
			if (subTime >= 0 && (min == 0 || subTime < min)) {
				min = subTime;
				nextRefreshTime = nextTime;
			}

		}
		return nextRefreshTime;
	}

	/**
	 * 广播世界boss刷新消息
	 * 
	 * @param monster
	 */
	public static void sendWorldBossRefreshScrollNotice(Monster monster) {

		if (monster == null) {
			return;
		}
		GameScene scene = monster.getCrtScene();
		if (scene == null) {
			return;
		}
		String sceneRefId = scene.getRef().getId();
		// TODO 针对玩家级别有针对性的使用走马灯推送野外BOSS刷新消息,现阶段还未作要求
		String monsterRefId = monster.getMonsterRef().getId();
		if (isContainMonster(monsterRefId, sceneRefId)) {
			if (isScrollNotice(monsterRefId, sceneRefId)) {
				String sceneName = MGPropertyAccesser.getName(scene.getRef().getProperty());
				String monsterName = MGPropertyAccesser.getName(monster.getMonsterRef().getProperty());
				if (StringUtils.isNotEmpty(sceneName) && StringUtils.isNotEmpty(monsterName)) {
					// XXX（BOSS名）出现在XXX（地图名），可前往击杀
					SystemPromptFacade.broadWorldBossRefresh(sceneName, monsterName);
				}
			}
			if (isTwinkle(monsterRefId, sceneRefId)) {
				G2C_Boss_Refresh res = MessageFactory.getConcreteMessage(WorldBossDefines.G2C_Boss_Refresh);
				res.setMonsterRefId(monsterRefId);
				ActionEventFacade.sendMessageToWorld(res);
			}
		}

	}

	/**
	 * 广播世界boss击杀消息
	 * 
	 * @param owner
	 * @param monster
	 * @param lootList
	 */
	public static void sendCherishPropsToWorld(FightSprite owner, Monster monster, List<Loot> lootList) {

		if (!(owner instanceof Player)) {
			return;
		}
		if (monster == null) {
			return;
		}
		GameScene scene = monster.getCrtScene();
		if (scene == null) {
			return;
		}
		String sceneRefId = scene.getRef().getId();
		Player player = (Player) owner;
		String monsterName = MGPropertyAccesser.getName(monster.getMonsterRef().getProperty());
		List<String> list = new ArrayList<>();
		String monsterRefId = monster.getMonsterRef().getId();
		if (!isContainMonster(monsterRefId, sceneRefId)) {
			return;
		}
		for (Loot loot : lootList) {
			ItemPair itemPair = loot.getItemPair();
			String itemRefId = null;
			if(itemPair == null){
				itemRefId = loot.getItem().getItemRefId();
			}else{
				itemRefId= itemPair.getItemRefId();
			}
			GameRefObject itemRef = GameRoot.getGameRefObjectManager().getManagedObject(itemRefId);

			if (itemRef == null || itemRef instanceof UnPropsItemRef) {
				continue;
			}
			byte quality = MGPropertyAccesser.getQuality(itemRef.getProperty());
			if (quality == 3 || quality == 4) {
				if (!list.contains(itemRefId)) {
					list.add(itemRefId);
				}
			}
			byte itemType = MGPropertyAccesser.getItemType(itemRef.getProperty());
			int level = MGPropertyAccesser.getLevel(itemRef.getProperty());
			if (level >= 60 && itemType == 2) {
				if (!list.contains(itemRefId)) {
					list.add(itemRefId);
				}
			}
		}
		// 【玩家名字】杀死了【boss名字】并爆出了【道具名1】【道具名2】【道具名3】
		// {p=playername,<playerId>}杀死了 {m=monsterrefid},并爆出了{g=111,<name>....}"
		StringBuffer sb = new StringBuffer("{p=" + player.getName() + "<" + player.getId() + ">}杀死了 {m=" + monsterName + "<" + monster.getMonsterRef().getId() + ">},并爆出了");
		if (list.size() == 0) {
			return;
		}
		for (String itemRefId : list) {
			ItemRef itemRef = (ItemRef) GameRoot.getGameRefObjectManager().getManagedObject(itemRefId);
			String name = MGPropertyAccesser.getName(itemRef.getProperty());
			sb.append("{g=" + name + "<" + itemRefId + ">} ");
		}
		PlayerChatFacade.sendMessageToSystem(sb.toString());

	}

	public static boolean isWorldThief(Monster monster) {
		String monsterRefId = monster.getMonsterRef().getId();
		if (StringUtils.equals(monsterRefId, "monster_72") || StringUtils.equals(monsterRefId, "monster_73")) {
			return true;
		}
		return false;
	}

	public static Monster getRandomWorldThief(GameScene gameScene) {
		if (gameScene == null) {
			return null;
		}
		String sceneRefId = gameScene.getRef().getId();
		List<Monster> list = thiefs.get(sceneRefId);
		if (list == null) {
			return null;
		}
		if (list.size() > 0) {
			int max = list.size() - 1;
			int min = 0;
			int random = SFRandomUtils.random(min, max);
			Monster monster = list.get(random);
			return monster;
		}

		return null;
	}

	public static boolean isShouldRefreshThief(GameScene gameScene) {
		if (gameScene == null) {
			return false;
		}
		String sceneRefId = gameScene.getRef().getId();
		List<Monster> list = thiefs.get(sceneRefId);
		if (list == null) {
			return true;
		}
		return false;

	}

	public static void putThief(GameScene gameScene, Monster monster) {
		if (gameScene == null) {
			return;
		}
		if (monster == null) {
			return;
		}
		String sceneRefId = gameScene.getRef().getId();
		List<Monster> list = thiefs.get(sceneRefId);
		if (list == null) {
			list = new ArrayList<Monster>();
			list.add(monster);
			thiefs.put(sceneRefId, list);
		} else {
			list.add(monster);
		}
	}

	public static Map<String, Long> getBossMap() {
		return bosses;
	}

	public static List<WorldBossMsgRef> getBossList() {
		return bossSet;
	}
}
