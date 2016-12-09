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
package newbee.morningGlory.character;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import newbee.morningGlory.mmorpg.player.talisman.MGPlayerCitta;
import newbee.morningGlory.mmorpg.player.talisman.MGPlayerTalismanComponent;
import newbee.morningGlory.mmorpg.player.talisman.MGTalisman;
import newbee.morningGlory.mmorpg.player.talisman.MGTalismanContains;
import newbee.morningGlory.mmorpg.player.talisman.MGTalismanRef;
import newbee.morningGlory.mmorpg.player.talisman.level.MGTalismanDataConfig;
import newbee.morningGlory.mmorpg.player.wing.MGPlayerWingComponent;
import newbee.morningGlory.mmorpg.player.wing.MGPlayerWingRef;
import newbee.morningGlory.mmorpg.player.wing.MGWingEffectMgr;
import newbee.morningGlory.mmorpg.vip.MGVipType;

import org.apache.log4j.Logger;

import sophia.foundation.authentication.Identity;
import sophia.foundation.property.PropertyDictionary;
import sophia.game.GameRoot;
import sophia.game.ref.GameRefObject;
import sophia.mmorpg.GameObjectFactory;
import sophia.mmorpg.MMORPGContext;
import sophia.mmorpg.base.scene.GameScene;
import sophia.mmorpg.base.scene.grid.SceneGrid;
import sophia.mmorpg.base.scene.ref.AbstractGameSceneRef;
import sophia.mmorpg.base.sprite.fightSkill.FightSkill;
import sophia.mmorpg.friend.ChatFriendSaver;
import sophia.mmorpg.friend.FriendSystemManager;
import sophia.mmorpg.friend.PlayerChatFriendMgr;
import sophia.mmorpg.gameArea.GameArea;
import sophia.mmorpg.item.Item;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.equipment.PlayerEquipBody;
import sophia.mmorpg.player.equipment.PlayerEquipBodyArea;
import sophia.mmorpg.player.fightSkill.PlayerFightSkillTree;
import sophia.mmorpg.player.fightSkill.ref.SkillRef;
import sophia.mmorpg.player.itemBag.ItemBag;
import sophia.mmorpg.player.itemBag.ItemFacade;
import sophia.mmorpg.player.itemBag.PlayerItemBagComponent;
import sophia.mmorpg.player.mount.Mount;
import sophia.mmorpg.player.mount.MountEffectMgr;
import sophia.mmorpg.player.mount.MountManager;
import sophia.mmorpg.player.mount.MountRef;
import sophia.mmorpg.player.mount.PlayerMountComponent;
import sophia.mmorpg.player.persistence.immediatelySave.PlayerImmediateDaoFacade;
import sophia.mmorpg.player.property.PlayerConfig;
import sophia.mmorpg.player.quest.PlayerQuestComponent;
import sophia.mmorpg.player.quest.PlayerQuestManager;
import sophia.mmorpg.player.quest.Quest;
import sophia.mmorpg.player.quest.QuestState;
import sophia.mmorpg.player.quest.ref.order.QuestRefOrderType;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;
import sophia.mmorpg.stat.StatFunctions;
import sophia.mmorpg.stat.logs.StatPlotQuest;

public final class CharacterCreate {
	
	private static Logger logger = Logger.getLogger(CharacterCreate.class);
	
	/** 一个场景300个人*/
	private static final int BALANCE_COUNT = 300;

	public static Player newPlayer(Identity identity, String name, byte gender, byte professionId) {
		String id = UUID.randomUUID().toString();
		Player player = GameObjectFactory.getPlayer(identity, id, name);

		PlayerConfig.setProfessionRefTo(player, professionId, gender);
		PlayerConfig.setLevelAndExpTo(player, 1, 0);
		PlayerConfig.setGoldTo(player, 0, 0, 0);
		PlayerConfig.setMeritAndAchievementTo(player, 0, 0);
		
		configBornSceneAndPosition(player);
		configBasicPropertiesTo(player);
		configQuestTo(player);
		configChatFriend(player);
		configFightSkillTreeTo(player);
		configTalismanComponentTo(player);
		// 不要注释
		configItemBagTo(player);
		configEquipBodyTo(player);
		
		// configWingTo(player);
		// configMountTo(player);

		PlayerConfig.configFightPropertiesTo(player);

		MMORPGContext.getPlayerComponent().getSaveService().insertImmediateData(player);
		PlayerImmediateDaoFacade.insert(player);
		
		return player;
	}

	public static void configBasicPropertiesTo(Player player) {
		PropertyDictionary propertyDictionary = player.getProperty();

		MGPropertyAccesser.setOrPutMerit(propertyDictionary, 0);
		MGPropertyAccesser.setOrPutUnionOfficialId(propertyDictionary, (byte)-1);
		MGPropertyAccesser.setOrPutKnight(propertyDictionary, (byte)0);
		MGPropertyAccesser.setOrPutPkValue(propertyDictionary, 0);
		MGPropertyAccesser.setOrPutWeaponModleId(propertyDictionary, 0);
		MGPropertyAccesser.setOrPutArmorModleId(propertyDictionary, 0);
		MGPropertyAccesser.setOrPutWingModleId(propertyDictionary, 0);
		MGPropertyAccesser.setOrPutMountModleId(propertyDictionary, 0);
		MGPropertyAccesser.setOrPutOnlineTime(propertyDictionary, 0);
		MGPropertyAccesser.setOrPutVipType(propertyDictionary, MGVipType.NO_VIP);
		long now = System.currentTimeMillis();
		MGPropertyAccesser.setOrPutBirthday(propertyDictionary, now);
		MGPropertyAccesser.setOrPutLastLoginTime(propertyDictionary, now);
		MGPropertyAccesser.setOrPutLastLogoutTime(propertyDictionary, now);
	}

	public static void configQuestTo(Player player) {
		PlayerQuestComponent questComponent = player.getPlayerQuestComponent();
		PlayerQuestManager questManager = questComponent.getQuestManager();
		Quest ret = GameObjectFactory.getQuest("quest_1");
		ret.setQuestState(QuestState.AcceptedQuestState);
		Map<Integer, String> list = ret.createQuestCourseItem(player);
		for (int single : list.keySet()) {
			if (single == QuestRefOrderType.Collect_Order_Type || single == QuestRefOrderType.Loot_Item_Order_Type) {
				int number = ItemFacade.getNumber(player, list.get(single));
				ret.setQuestCourseNum(list.get(single), number, player);
			}
		}
		if (ret.getQuestCourse().wasCompleted()) {
			ret.setQuestState(QuestState.SubmittableQuestState);
		}
		questManager.setCrtQuest(ret);
		StatFunctions.plotQuest(player, StatPlotQuest.Accept, ret.getQuestRef().getId());
	}
	
	public static void configChatFriend(Player player) {
		String playerId = player.getId();
		PlayerChatFriendMgr playerChatFriendMgr = new PlayerChatFriendMgr(playerId);
		
		FriendSystemManager.initPlayerChatModule(playerChatFriendMgr);
		
		ChatFriendSaver.getInstance().insertImmediateData(playerChatFriendMgr);
	}

	public static void configItemBagTo(Player player) {
		PlayerItemBagComponent itemBagComponent = player.getItemBagComponent();
		ItemBag itemBag = itemBagComponent.getItemBag();
		itemBag.expendItemBagSlot(PlayerItemBagComponent.DEFAULT_ITEMBAGSLOT_COUNT);

	}

	public static void configEquipBodyTo(Player player) {
		PlayerEquipBody playerEquipBody = player.getPlayerEquipBodyConponent().getPlayerBody();
		PlayerEquipBodyArea area1 = playerEquipBody.getBodyArea((byte) 1);
		PlayerEquipBodyArea area2 = playerEquipBody.getBodyArea((byte) 2);

		String itemRef1 = "equip_1_1000";// 1
		byte gender = MGPropertyAccesser.getGender(player.getProperty());
		String itemRef2 = gender == 1? "equip_1_2010" : "equip_1_2020";

		Item item1 = GameObjectFactory.getItem(itemRef1);
		item1.setBindStatus((byte)1);
		Item item2 = GameObjectFactory.getItem(itemRef2);
		item2.setBindStatus((byte)1);
		area1.setOrResetEquipment(item1);
		area2.setOrResetEquipment(item2);

		/**
		 * 改变绑定状态
		 */
		for (PlayerEquipBodyArea area : playerEquipBody.getBodyAreaList()) {
			if (!area.isLeftRightBodyArea()) {
				Item item = area.getEquipment();
				if (item != null && item.getBindType() == 2) {
					item.setBindStatus((byte) 1);
				}
			} else {
				Item item = area.getEquipment(PlayerEquipBodyArea.Left_Position);
				if (item != null && item.getBindType() == 2) {
					item.setBindStatus((byte) 1);
				}

				item = area.getEquipment(PlayerEquipBodyArea.Right_Position);
				if (item != null && item.getBindType() == 2) {
					item.setBindStatus((byte) 1);
				}
			}
		}

		player.getPlayerEquipBodyConponent().getEquipEffectMgr().restore();

	}

	public static void configTalismanComponentTo(Player player) {

		MGPlayerTalismanComponent talismanComponent = (MGPlayerTalismanComponent) player.getTagged(MGPlayerTalismanComponent.Tag);
		MGTalismanDataConfig talismanDataConfig = (MGTalismanDataConfig) GameRoot.getGameRefObjectManager().getManagedObject(MGTalismanDataConfig.MGTalisman_Id);
		MGPlayerCitta talismanMgr = talismanComponent.getPlayerCitta();
		List<MGTalismanContains> talismanList = talismanMgr.getTalismanList();
		talismanComponent.setPlayerCitta(talismanMgr);
		
		MGTalismanRef talismanRef1 = talismanDataConfig.getTalismanLevelDataMap().get("title_3_0");
		MGTalismanRef talismanRef3 = talismanDataConfig.getTalismanLevelDataMap().get("title_4_0");
		MGTalismanRef talismanRef5 = talismanDataConfig.getTalismanLevelDataMap().get("title_5_0");
		MGTalismanRef talismanRef7 = talismanDataConfig.getTalismanLevelDataMap().get("title_6_0");
		MGTalismanRef talismanRef9 = talismanDataConfig.getTalismanLevelDataMap().get("title_10_0");
		
		MGTalismanRef talismanRef2 = talismanDataConfig.getTalismanLevelDataMap().get("title_1_0");
		MGTalismanRef talismanRef4 = talismanDataConfig.getTalismanLevelDataMap().get("title_2_0");
		MGTalismanRef talismanRef6 = talismanDataConfig.getTalismanLevelDataMap().get("title_7_0");
		MGTalismanRef talismanRef8 = talismanDataConfig.getTalismanLevelDataMap().get("title_8_0");
		MGTalismanRef talismanRef10 = talismanDataConfig.getTalismanLevelDataMap().get("title_9_0");
		
		MGTalisman talisman1 = new MGTalisman(talismanRef1);
		MGTalisman talisman2 = new MGTalisman(talismanRef2);
		MGTalisman talisman3 = new MGTalisman(talismanRef3);
		MGTalisman talisman4 = new MGTalisman(talismanRef4);
		MGTalisman talisman5 = new MGTalisman(talismanRef5);
		MGTalisman talisman6 = new MGTalisman(talismanRef6);
		MGTalisman talisman7 = new MGTalisman(talismanRef7);
		MGTalisman talisman8 = new MGTalisman(talismanRef8);
		MGTalisman talisman9 = new MGTalisman(talismanRef9);
		MGTalisman talisman10 = new MGTalisman(talismanRef10);
		talismanList.add(new MGTalismanContains(1, talisman1));
		talismanList.add(new MGTalismanContains(2, talisman2));
		talismanList.add(new MGTalismanContains(3, talisman3));
		talismanList.add(new MGTalismanContains(4, talisman4));
		talismanList.add(new MGTalismanContains(5, talisman5));
		talismanList.add(new MGTalismanContains(6, talisman6));
		talismanList.add(new MGTalismanContains(7, talisman7));
		talismanList.add(new MGTalismanContains(8, talisman8));
		talismanList.add(new MGTalismanContains(9, talisman9));
		talismanList.add(new MGTalismanContains(10, talisman10));

	}

	// temporarily usage
	public static void leanSkillByProfession(Player player) {
		byte professionId = player.getProfession();
		PlayerFightSkillTree playerFightSkillTree = player.getPlayerFightSkillComponent().getPlayerFightSkillTree();
		if (PlayerConfig.isWarrior(professionId)) {
			// 烈火剑法
			SkillRef ref2 = (SkillRef) GameRoot.getGameRefObjectManager().getManagedObject("skill_zs_6");
			playerFightSkillTree.learn(new FightSkill("skill_zs_6", ref2));
			// 攻杀剑法
			SkillRef ref14 = (SkillRef) GameRoot.getGameRefObjectManager().getManagedObject("skill_zs_2");
			playerFightSkillTree.learn(new FightSkill("skill_zs_2", ref14));
			// 刺杀剑法
			SkillRef ref15 = (SkillRef) GameRoot.getGameRefObjectManager().getManagedObject("skill_zs_3");
			playerFightSkillTree.learn(new FightSkill("skill_zs_2", ref15));
			// 野蛮冲撞
			SkillRef ref16 = (SkillRef) GameRoot.getGameRefObjectManager().getManagedObject("skill_zs_5");
			playerFightSkillTree.learn(new FightSkill("skill_zs_5", ref16));
			// 半月剑法
			SkillRef ref17 = (SkillRef) GameRoot.getGameRefObjectManager().getManagedObject("skill_zs_4");
			playerFightSkillTree.learn(new FightSkill("skill_zs_4", ref17));
		} else if (PlayerConfig.isEnchanter(professionId)) {
			// 小火球
			SkillRef ref3 = (SkillRef) GameRoot.getGameRefObjectManager().getManagedObject("skill_fs_1");
			playerFightSkillTree.learn(new FightSkill("skill_fs_1", ref3));
			// 地狱火
			SkillRef ref4 = (SkillRef) GameRoot.getGameRefObjectManager().getManagedObject("skill_fs_3");
			playerFightSkillTree.learn(new FightSkill("skill_fs_3", ref4));
			// 雷电术
			SkillRef ref5 = (SkillRef) GameRoot.getGameRefObjectManager().getManagedObject("skill_fs_4");
			playerFightSkillTree.learn(new FightSkill("skill_fs_4", ref5));
			// 瞬间移动
			SkillRef ref6 = (SkillRef) GameRoot.getGameRefObjectManager().getManagedObject("skill_fs_5");
			playerFightSkillTree.learn(new FightSkill("skill_fs_5", ref6));
			// 大火球
			SkillRef ref7 = (SkillRef) GameRoot.getGameRefObjectManager().getManagedObject("skill_fs_6");
			playerFightSkillTree.learn(new FightSkill("skill_fs_6", ref7));
			// 爆裂火焰
			SkillRef ref8 = (SkillRef) GameRoot.getGameRefObjectManager().getManagedObject("skill_fs_7");
			playerFightSkillTree.learn(new FightSkill("skill_fs_7", ref8));
			// 疾光电影
			SkillRef ref9 = (SkillRef) GameRoot.getGameRefObjectManager().getManagedObject("skill_fs_9");
			playerFightSkillTree.learn(new FightSkill("skill_fs_9", ref9));
			// 地狱雷光
			SkillRef ref10 = (SkillRef) GameRoot.getGameRefObjectManager().getManagedObject("skill_fs_10");
			playerFightSkillTree.learn(new FightSkill("skill_fs_10", ref10));
			// 冰咆哮
			SkillRef ref11 = (SkillRef) GameRoot.getGameRefObjectManager().getManagedObject("skill_fs_12");
			playerFightSkillTree.learn(new FightSkill("skill_fs_12", ref11));
			// 抗拒火环
			SkillRef ref17 = (SkillRef) GameRoot.getGameRefObjectManager().getManagedObject("skill_fs_2");
			playerFightSkillTree.learn(new FightSkill("skill_fs_2", ref17));
			// 魔法盾
			SkillRef ref18 = (SkillRef) GameRoot.getGameRefObjectManager().getManagedObject("skill_fs_11");
			playerFightSkillTree.learn(new FightSkill("skill_fs_11", ref18));
			// 火墙
			// SkillRef ref20 = (SkillRef)
			// GameRoot.getGameRefObjectManager().getManagedObject("skill_fs_8");
			// playerFightSkillTree.learn(new FightSkill("skill_fs_8", ref20));
		} else if (PlayerConfig.isWarlock(professionId)) {
			// 治愈术
			SkillRef ref12 = (SkillRef) GameRoot.getGameRefObjectManager().getManagedObject("skill_ds_1");
			playerFightSkillTree.learn(new FightSkill("skill_ds_1", ref12));
			// 灵魂火符
			SkillRef ref13 = (SkillRef) GameRoot.getGameRefObjectManager().getManagedObject("skill_ds_4");
			playerFightSkillTree.learn(new FightSkill("skill_ds_4", ref13));
			// 施毒术
			SkillRef ref14 = (SkillRef) GameRoot.getGameRefObjectManager().getManagedObject("skill_ds_3");
			playerFightSkillTree.learn(new FightSkill("skill_ds_3", ref14));
			// 魔抗咒
			SkillRef ref15 = (SkillRef) GameRoot.getGameRefObjectManager().getManagedObject("skill_ds_7");
			playerFightSkillTree.learn(new FightSkill("skill_ds_7", ref15));
			// 物抗咒
			SkillRef ref16 = (SkillRef) GameRoot.getGameRefObjectManager().getManagedObject("skill_ds_8");
			playerFightSkillTree.learn(new FightSkill("skill_ds_8", ref16));
			// 群体治愈术
			SkillRef ref17 = (SkillRef) GameRoot.getGameRefObjectManager().getManagedObject("skill_ds_10");
			playerFightSkillTree.learn(new FightSkill("skill_ds_10", ref17));
			// 隐身术
			SkillRef ref19 = (SkillRef) GameRoot.getGameRefObjectManager().getManagedObject("skill_ds_6");
			playerFightSkillTree.learn(new FightSkill("skill_ds_6", ref19));
			// 召唤神兽
			SkillRef ref20 = (SkillRef) GameRoot.getGameRefObjectManager().getManagedObject("skill_ds_11");
			playerFightSkillTree.learn(new FightSkill("skill_ds_11", ref20));
			// 召唤骷髅
			SkillRef ref21 = (SkillRef) GameRoot.getGameRefObjectManager().getManagedObject("skill_ds_5");
			playerFightSkillTree.learn(new FightSkill("skill_ds_5", ref21));

		}
	}

	public static void configFightSkillTreeTo(Player player) {
		PlayerFightSkillTree playerFightSkillTree = player.getPlayerFightSkillComponent().getPlayerFightSkillTree();
		// 普通攻击
		SkillRef ref = (SkillRef) GameRoot.getGameRefObjectManager().getManagedObject(SkillRef.basicAttackRefId);
		playerFightSkillTree.learn(new FightSkill(SkillRef.basicAttackRefId, ref));

		// leanSkillByProfession(player);

		logger.info("configFightSkillTreeTo: learned skill: skill_0: " + ref);
	}

	public static void configMountTo(Player player) {
		String mountRefId = "ride_1";
		MountRef mountRef = (MountRef) GameRoot.getGameRefObjectManager().getManagedObject(mountRefId);
		Mount mount = GameObjectFactory.getMount();
		mount.setMountRef(mountRef);
		mount.setExp(0);
		mount.setId(UUID.randomUUID().toString());
		PlayerMountComponent mountComponent = player.getPlayerMountComponent();
		MountEffectMgr mountEffectMgr = new MountEffectMgr(player);
		mountComponent.setMountEffectMgr(mountEffectMgr);
		MountManager mountManager = mountComponent.getMountManager();
		mountManager.setCrtMount(mount);
		mountManager.setOwner(player);
		mountEffectMgr.restore(mount);
	}

	public static void configWingTo(Player player) {
		MGPlayerWingComponent wingComponent = (MGPlayerWingComponent) player.getTagged(MGPlayerWingComponent.Tag);
		GameRefObject wing_1 = GameRoot.getGameRefObjectManager().getManagedObject("wing_1");
		MGWingEffectMgr wingEffectMgr = new MGWingEffectMgr(player);
		wingComponent.setWingEffectMgr(wingEffectMgr);
		wingComponent.getPlayerWing().setPlayerWingRef((MGPlayerWingRef) wing_1);
		wingComponent.getWingEffectMgr().restore(wingComponent.getPlayerWing());
	}
	
	private static void configBornSceneAndPosition(Player player) {
		GameArea gameArea = MMORPGContext.getGameAreaComponent().getGameArea();
		GameScene baseScene = gameArea.getSceneById(PlayerConfig.MAPID_BORN);
		AbstractGameSceneRef ref;
		if (baseScene.getPlayerMgrComponent().getPlayerMap().size() >= BALANCE_COUNT) {
			GameScene gameScene = balanceXinShouCunGameScene();
			ref = gameScene.getRef();
		} else {
			ref = (AbstractGameSceneRef) GameRoot.getGameRefObjectManager().getManagedObject(PlayerConfig.MAPID_BORN);
		}
		
		SceneGrid sceneGrid = ref.getRandomBirthGrid();
		PlayerConfig.setPositionTo(player, ref.getId(), sceneGrid.getColumn(), sceneGrid.getRow());
	}
	
	private static GameScene balanceXinShouCunGameScene() {
		GameArea gameArea = MMORPGContext.getGameAreaComponent().getGameArea();
		List<GameScene> xinShouCunSceneList = gameArea.getXinShouCunSceneList();
		int size = xinShouCunSceneList.size();
		GameScene gameScene = xinShouCunSceneList.get(0);
		int crtPlayerCount = gameScene.getPlayerMgrComponent().getPlayerMap().size();
		for (int i = 1; i < size; i ++) {
			GameScene tmpScene = xinShouCunSceneList.get(i);
			int playerCount = tmpScene.getPlayerMgrComponent().getPlayerMap().size();
			if (playerCount < crtPlayerCount) {
				gameScene = tmpScene;
				crtPlayerCount = playerCount;
			}
		}
		
		return gameScene;
	}
}
