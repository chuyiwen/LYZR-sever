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
package newbee.morningGlory.mmorpg.player;

import java.util.ArrayList;
import java.util.List;

import newbee.morningGlory.debug.DebugComponent;
import newbee.morningGlory.mmorpg.item.equipment.MGEquipmentClosures;
import newbee.morningGlory.mmorpg.item.equipment.MGEquipmentRuntime;
import newbee.morningGlory.mmorpg.item.useableItem.MGUseableItemClosures;
import newbee.morningGlory.mmorpg.item.useableItem.MGUseableItemRuntime;
import newbee.morningGlory.mmorpg.operatActivities.OperatActivityComponent;
import newbee.morningGlory.mmorpg.player.achievement.MGPlayerAchievementComponent;
import newbee.morningGlory.mmorpg.player.achievement.persistence.MGAchievementPersistenceObject;
import newbee.morningGlory.mmorpg.player.activity.MGPlayerActivityComponent;
import newbee.morningGlory.mmorpg.player.activity.QuickRecharge.MGPlayerQuickRechargeComponent;
import newbee.morningGlory.mmorpg.player.activity.digs.MGPlayerDigsComponent;
import newbee.morningGlory.mmorpg.player.activity.digs.persistence.DigsPersistenceObject;
import newbee.morningGlory.mmorpg.player.activity.fund.FundActivityComponet;
import newbee.morningGlory.mmorpg.player.activity.giftCode.MGGiftCodeComponent;
import newbee.morningGlory.mmorpg.player.activity.ladder.MGPlayerLadderComponent;
import newbee.morningGlory.mmorpg.player.activity.limitTimeRank.MGLimitTimeRankComponent;
import newbee.morningGlory.mmorpg.player.activity.mining.MGPlayerMiningComponent;
import newbee.morningGlory.mmorpg.player.activity.mining.MGPlayerMiningManager;
import newbee.morningGlory.mmorpg.player.activity.oldPlayer.MGOldPlayerComponent;
import newbee.morningGlory.mmorpg.player.activity.persistence.ActivityPersistenceObject;
import newbee.morningGlory.mmorpg.player.activity.resDownload.MGResDownLoadComponent;
import newbee.morningGlory.mmorpg.player.activity.sevenLogin.MGPlayerSevenLoginComponent;
import newbee.morningGlory.mmorpg.player.auction.MGAuctionComponent;
import newbee.morningGlory.mmorpg.player.castleWar.MGCastleWarComponent;
import newbee.morningGlory.mmorpg.player.castleWar.persistence.MGCastleWarPersistenceObject;
import newbee.morningGlory.mmorpg.player.dailyQuest.MGDailyQuestComponent;
import newbee.morningGlory.mmorpg.player.dailyQuest.persistence.DailyQuestPersistenceObject;
import newbee.morningGlory.mmorpg.player.depot.PlayerDepotComponent;
import newbee.morningGlory.mmorpg.player.depot.persistence.DepotPersistenceObject;
import newbee.morningGlory.mmorpg.player.fightSkill.MGPlayerFightSkillTreeActionEventDelegate;
import newbee.morningGlory.mmorpg.player.fightSkill.MGPlayerFightSkillTreeGameEventDelegate;
import newbee.morningGlory.mmorpg.player.funStep.FunStepComponent;
import newbee.morningGlory.mmorpg.player.gameInstance.PlayerGameInstanceComponent;
import newbee.morningGlory.mmorpg.player.gm.MGPlayerGMComponent;
import newbee.morningGlory.mmorpg.player.gm.persistence.PlayerGMPersistenceObject;
import newbee.morningGlory.mmorpg.player.itemBag.MGItemBagPutItemClosures;
import newbee.morningGlory.mmorpg.player.itemBag.MGItemBagPutItemRuntime;
import newbee.morningGlory.mmorpg.player.peerage.MGPeerageEffectMgr;
import newbee.morningGlory.mmorpg.player.peerage.MGPlayerPeerageComponent;
import newbee.morningGlory.mmorpg.player.peerage.persistence.PeeragePersistenceObject;
import newbee.morningGlory.mmorpg.player.pk.MGPlayerPKComponent;
import newbee.morningGlory.mmorpg.player.property.MGPlayerCoreComponent;
import newbee.morningGlory.mmorpg.player.property.MGPlayerSceneComponent;
import newbee.morningGlory.mmorpg.player.sectionQuest.MGSectionQuestComponent;
import newbee.morningGlory.mmorpg.player.sortboard.MGSortboardComponent;
import newbee.morningGlory.mmorpg.player.store.MGPlayerShopComponent;
import newbee.morningGlory.mmorpg.player.summons.PlayerSummonMonsterComponent;
import newbee.morningGlory.mmorpg.player.talisman.MGPlayerCitta;
import newbee.morningGlory.mmorpg.player.talisman.MGPlayerTalismanComponent;
import newbee.morningGlory.mmorpg.player.talisman.MGTalismanEffectMgr;
import newbee.morningGlory.mmorpg.player.talisman.persistence.TalismanPersistenceObject;
import newbee.morningGlory.mmorpg.player.union.MGPlayerUnionComponent;
import newbee.morningGlory.mmorpg.player.unionGameInstance.MGUnionGameInstanceComponent;
import newbee.morningGlory.mmorpg.player.wing.MGPlayerWingComponent;
import newbee.morningGlory.mmorpg.player.wing.MGWingEffectMgr;
import newbee.morningGlory.mmorpg.player.wing.persistence.WingPersistenceObject;
import newbee.morningGlory.mmorpg.player.wing.wingModule.WingManager;
import newbee.morningGlory.mmorpg.sceneActivities.monsterIntrusion.MonsterInvasionComponent;
import newbee.morningGlory.mmorpg.sceneActivities.mutilExp.MGMutilExpActivityComponent;
import newbee.morningGlory.mmorpg.sceneActivities.payonPalace.MGPayonPalaceActivityComponent;
import newbee.morningGlory.mmorpg.sceneActivities.teamBoss.MGPlayerTeamComponent;
import newbee.morningGlory.mmorpg.sprite.MGFightProcessComponent;
import newbee.morningGlory.mmorpg.sprite.MGFightPropertyMgr;
import newbee.morningGlory.mmorpg.sprite.MGFightPropertyMgrHelper;
import newbee.morningGlory.mmorpg.sprite.buff.MGFightSpriteBuffComponent;
import newbee.morningGlory.mmorpg.sprite.buff.persistence.BuffPersistenceObject;
import newbee.morningGlory.mmorpg.sprite.player.fightSkill.MGFightSkillRuntime;
import newbee.morningGlory.mmorpg.vip.MGPlayerVipComponent;
import newbee.morningGlory.mmorpg.vip.MGVipEffectMgr;
import newbee.morningGlory.mmorpg.vip.MGVipLevelMgr;
import newbee.morningGlory.mmorpg.vip.lottery.MGVipLotteryMgr;
import newbee.morningGlory.mmorpg.vip.persistence.MGVipPersistenceObject;
import sophia.foundation.authentication.Identity;
import sophia.foundation.data.PersistenceParameter;
import sophia.mmorpg.GameObjectProvider;
import sophia.mmorpg.auth.AuthIdentity;
import sophia.mmorpg.base.sprite.FightSprite;
import sophia.mmorpg.base.sprite.fightProperty.FightPropertyEffectFacade;
import sophia.mmorpg.base.sprite.fightSkill.FightSkillRuntimeComponent;
import sophia.mmorpg.equipmentSmith.MGEquipmentSmithComponent;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.equipment.EquipEffectMgr;
import sophia.mmorpg.player.equipment.EquipMgr;
import sophia.mmorpg.player.equipment.PlayerEquipBody;
import sophia.mmorpg.player.equipment.PlayerEquipBodyArea;
import sophia.mmorpg.player.equipment.PlayerEquipBodyConponent;
import sophia.mmorpg.player.equipment.persistence.EquipmentPersistenceObject;
import sophia.mmorpg.player.fightSkill.PlayerFightSkillComponent;
import sophia.mmorpg.player.fightSkill.PlayerFightSkillTree;
import sophia.mmorpg.player.fightSkill.persistence.PlayerFightSkillPersistenceObject;
import sophia.mmorpg.player.friend.PlayerFriendComponent;
import sophia.mmorpg.player.itemBag.PlayerItemBagComponent;
import sophia.mmorpg.player.itemBag.persistence.ItemBagPersistenceObject;
import sophia.mmorpg.player.mount.MountEffectMgr;
import sophia.mmorpg.player.mount.PlayerMountComponent;
import sophia.mmorpg.player.mount.persistence.MountPersistenceObject;
import sophia.mmorpg.player.persistence.PlayerSaveComponent;
import sophia.mmorpg.player.persistence.immediatelySave.PlayerImmediateSaveComponent;
import sophia.mmorpg.player.quest.PlayerQuestComponent;
import sophia.mmorpg.player.quest.persistence.QuestPersistenceObject;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;

import com.google.common.base.Preconditions;

public final class MGPlayerProvider implements GameObjectProvider<Player> {
	public static final GameObjectProvider<Player> instance = new MGPlayerProvider();

	public static final GameObjectProvider<Player> getInstance() {
		return instance;
	}

	public MGPlayerProvider() {

	}

	@Override
	public Player get(Class<Player> type) {
		Player player = new Player();
		player.registComponents();
		player.reset();

		addAndConfigMGComponentTo(player);

		return player;
	}

	@Override
	public Player get(Class<Player> type, Object... args) {
		Player player = new Player();

		player.registComponents();
		player.reset();

		Identity identity = (Identity) args[0];
		bindedIdentityTo(player, identity);
		String id = (String) args[1];
		String name = (String) args[2];
		player.setId(id);
		player.setName(name);
		player.setFightPower(new MGPlayerFightPower(player));
		MGPropertyAccesser.setOrPutName(player.getProperty(), name);

		addAndConfigMGComponentTo(player);

		configFightSkillComponent(player);

		configSaveComponentTo(player);

		configImmediateSaveComponentTo(player);

		configEquipBodyComponent(player);

		configGameInstanceTo(player);

		return player;
	}

	private void addAndConfigMGComponentTo(Player player) {
		player.createComponent(DebugComponent.class, DebugComponent.Tag);
		MGPlayerFightProcessComponent processComponent = (MGPlayerFightProcessComponent) player.createComponent(MGPlayerFightProcessComponent.class, MGFightProcessComponent.Tag);
		processComponent.setOwner(player);
		player.createComponent(MGDailyQuestComponent.class, MGDailyQuestComponent.Tag);
		player.createComponent(MGSectionQuestComponent.class, MGSectionQuestComponent.Tag);
		player.createComponent(MGPlayerPeerageComponent.class, MGPlayerPeerageComponent.Tag);
		player.createComponent(MGPlayerWingComponent.class, MGPlayerWingComponent.Tag);
		player.createComponent(MGPlayerShopComponent.class, MGPlayerShopComponent.Tag);
		player.createComponent(MGEquipmentSmithComponent.class, MGEquipmentSmithComponent.Tag);
		player.createComponent(PlayerGameInstanceComponent.class, PlayerGameInstanceComponent.Tag);
		player.createComponent(MGPlayerAchievementComponent.class, MGPlayerAchievementComponent.Tag);
		player.createComponent(MGPlayerTalismanComponent.class, MGPlayerTalismanComponent.Tag);
		player.createComponent(MGPlayerUnionComponent.class, MGPlayerUnionComponent.Tag);
		player.createComponent(MGFightSpriteBuffComponent.class, MGFightSpriteBuffComponent.Tag).setParent(player);
		player.createComponent(PlayerSummonMonsterComponent.class, PlayerSummonMonsterComponent.Tag);
		player.createComponent(MGPlayerPKComponent.class, MGPlayerPKComponent.Tag);
		player.createComponent(MGPlayerGMComponent.class, MGPlayerGMComponent.Tag);
		player.createComponent(MGPlayerVipComponent.class, MGPlayerVipComponent.Tag);

		player.createComponent(MGPlayerActivityComponent.class, MGPlayerActivityComponent.Tag);
		player.createComponent(MGPlayerSevenLoginComponent.class, MGPlayerSevenLoginComponent.Tag);
		player.createComponent(MGPlayerDigsComponent.class, MGPlayerDigsComponent.Tag);
		player.createComponent(MGSortboardComponent.class, MGSortboardComponent.Tag);
		player.createComponent(MGLimitTimeRankComponent.class, MGLimitTimeRankComponent.Tag);
		// player.createComponent(OffLineAIComponent.class,
		// OffLineAIComponent.Tag);
		player.createComponent(OperatActivityComponent.class, OperatActivityComponent.Tag);
		player.createComponent(MGPlayerTeamComponent.class, MGPlayerTeamComponent.Tag);
		player.createComponent(MGCastleWarComponent.class, MGCastleWarComponent.Tag);
		player.createComponent(FundActivityComponet.class, FundActivityComponet.Tag);
		player.createComponent(MGPlayerLadderComponent.class, MGPlayerLadderComponent.Tag);
		player.createComponent(MGPlayerCoreComponent.class, MGPlayerCoreComponent.Tag);

		player.createComponent(MGPlayerSceneComponent.class, MGPlayerSceneComponent.Tag);
		player.createComponent(MGPlayerMiningComponent.class, MGPlayerMiningComponent.Tag);
		player.getFightPropertyMgrComponent().setFightPropertyMgr(new MGFightPropertyMgr());
		player.getPlayerFightSkillComponent().setActionEventDelegate(new MGPlayerFightSkillTreeActionEventDelegate());
		player.getPlayerFightSkillComponent().setGameEventDelegate(new MGPlayerFightSkillTreeGameEventDelegate());
		player.createComponent(MonsterInvasionComponent.class, MonsterInvasionComponent.Tag);
		player.createComponent(MGResDownLoadComponent.class, MGResDownLoadComponent.Tag);
		player.createComponent(MGGiftCodeComponent.class, MGGiftCodeComponent.Tag);
		player.createComponent(MGUnionGameInstanceComponent.class, MGUnionGameInstanceComponent.Tag);
		player.createComponent(FunStepComponent.class, FunStepComponent.Tag);
		player.createComponent(MGAuctionComponent.class, MGAuctionComponent.Tag);
		player.createComponent(MGOldPlayerComponent.class, MGOldPlayerComponent.Tag);
		player.createComponent(MGPlayerQuickRechargeComponent.class, MGPlayerQuickRechargeComponent.Tag);
		player.createComponent(MGPayonPalaceActivityComponent.class, MGPayonPalaceActivityComponent.Tag);
		player.createComponent(MGMutilExpActivityComponent.class, MGMutilExpActivityComponent.Tag);
		player.createComponent(PlayerFriendComponent.class, PlayerFriendComponent.Tag);
		// PropertyHelper
		FightPropertyEffectFacade.setPropertyHelper(new MGFightPropertyMgrHelper());

		player.createComponent(PlayerDepotComponent.class, PlayerDepotComponent.Tag);
	}

	private void configSaveComponentTo(Player player) {
		PlayerSaveComponent saveComponent = player.getPlayerSaveComponent();
		AuthIdentity identity = (AuthIdentity) player.getIdentity();
		saveComponent.addPersistenceParameter(new PersistenceParameter("id", player.getId()));
		saveComponent.addPersistenceParameter(new PersistenceParameter("identityId", player.getIdentity().getId()));
		saveComponent.addPersistenceParameter(new PersistenceParameter("identityName", player.getIdentity().getName()));
		saveComponent.addPersistenceParameter(new PersistenceParameter("qdCode1", identity.getQdCode1()));
		saveComponent.addPersistenceParameter(new PersistenceParameter("qdCode2", identity.getQdCode2()));
		saveComponent.addIndependentPropertyParameter(new PersistenceParameter("name", player.getName()));

		saveComponent.addIndependentPropertyParameter(new PersistenceParameter("level", player.getLevel()));
		saveComponent.addIndependentPropertyParameter(new PersistenceParameter("birthday", player.getBirthDay()));
		saveComponent.addIndependentPropertyParameter(new PersistenceParameter("lastLoginTime", player.getLastLoginTime()));
		saveComponent.addIndependentPropertyParameter(new PersistenceParameter("lastLogoutTime", player.getLastLoginOutTime()));

		saveComponent.getPropertyDictionaryPersistenceObject().setPropertyDictionary(player.getProperty());
		saveComponent.addPersistenceParameters(saveComponent.getPropertyDictionaryPersistenceObject());

		// itemBag
		PlayerItemBagComponent itemBagComponent = player.getItemBagComponent();
		ItemBagPersistenceObject itemBagPersistenceObject = new ItemBagPersistenceObject(itemBagComponent.getItemBag());
		itemBagComponent.setPersisteneceObject(itemBagPersistenceObject);
		itemBagComponent.setItemBagPutItemRuntime(new MGItemBagPutItemRuntime(new MGItemBagPutItemClosures()));
		itemBagComponent.setUseableItemRuntime(new MGUseableItemRuntime(new MGUseableItemClosures()));
		saveComponent.addPersistenceParameters(itemBagComponent.getPersisteneceObject());

		// depot
		PlayerDepotComponent depotComponent = (PlayerDepotComponent) player.getTagged(PlayerDepotComponent.Tag);
		depotComponent.setPersistenceObject(new DepotPersistenceObject(depotComponent.getDepot()));
		saveComponent.addPersistenceParameters(depotComponent.getPersistenceObject());

		// quest
		PlayerQuestComponent questComponent = player.getPlayerQuestComponent();
		QuestPersistenceObject questPersistenceObject = new QuestPersistenceObject(questComponent.getQuestManager(), player);
		questComponent.setPersisteneceObject(questPersistenceObject);
		saveComponent.addPersistenceParameters(questComponent.getPersisteneceObject());

		// dailyQuest
		MGDailyQuestComponent dailyQuestComponent = (MGDailyQuestComponent) player.getTagged(MGDailyQuestComponent.Tag);
		DailyQuestPersistenceObject dailyQuestPersistence = new DailyQuestPersistenceObject(dailyQuestComponent.getDailyQuestManager());
		dailyQuestComponent.setPersisteneceObject(dailyQuestPersistence);
		saveComponent.addPersistenceParameters(dailyQuestComponent.getPersisteneceObject());

		// wing
		MGPlayerWingComponent wingComponent = (MGPlayerWingComponent) player.getTagged(MGPlayerWingComponent.Tag);
		WingManager wingManager = new WingManager();
		wingManager.setPlayer(player);
		wingComponent.setWingManager(wingManager);
		wingComponent.setPlayerWing(wingManager.getPlayerWing());
		WingPersistenceObject wingPersistence = new WingPersistenceObject(player);
		MGWingEffectMgr wingEffectMgr = new MGWingEffectMgr(player);
		wingComponent.setPersisteneceObject(wingPersistence);
		wingComponent.setWingEffectMgr(wingEffectMgr);
		saveComponent.addPersistenceParameters(wingComponent.getPersisteneceObject());

		// equipment
		PlayerEquipBodyConponent playerEquipBodyConponent = player.getPlayerEquipBodyConponent();
		EquipmentPersistenceObject equipPersistenceObject = new EquipmentPersistenceObject(player);
		EquipMgr equipMgr = new EquipMgr(player);
		EquipEffectMgr equipEffectMgr = new EquipEffectMgr(player);
		playerEquipBodyConponent.setEquipMgr(equipMgr);
		playerEquipBodyConponent.setEquipEffectMgr(equipEffectMgr);
		playerEquipBodyConponent.setEquipmentRuntime(new MGEquipmentRuntime(new MGEquipmentClosures()));
		playerEquipBodyConponent.setPersisteneceObject(equipPersistenceObject);
		saveComponent.addPersistenceParameters(playerEquipBodyConponent.getPersisteneceObject());

		// skill
		PlayerFightSkillComponent skillComponent = player.getPlayerFightSkillComponent();
		PlayerFightSkillPersistenceObject skillPersistence = new PlayerFightSkillPersistenceObject(skillComponent.getPlayerFightSkillTree(), player);
		skillComponent.setPersistenceObject(skillPersistence);
		saveComponent.addPersistenceParameters(skillPersistence);

		// mount
		PlayerMountComponent mountComponent = player.getPlayerMountComponent();
		MountPersistenceObject mountPersistenceObject = new MountPersistenceObject(mountComponent.getMountManager());
		MountEffectMgr mountEffectMgr = new MountEffectMgr(player);
		mountComponent.setMountEffectMgr(mountEffectMgr);
		mountComponent.setPersisteneceObject(mountPersistenceObject);
		saveComponent.addPersistenceParameters(mountComponent.getPersisteneceObject());
		mountComponent.getMountManager().setOwner(player);

		// peerage
		MGPlayerPeerageComponent peerageComponent = (MGPlayerPeerageComponent) player.getTagged(MGPlayerPeerageComponent.Tag);
		peerageComponent.getMeritManager().setPlayer(player);
		PeeragePersistenceObject peeragePersistenceObject = new PeeragePersistenceObject(player);
		MGPeerageEffectMgr peerageEffectMgr = new MGPeerageEffectMgr(player);
		peerageComponent.setPeerageEffectMgr(peerageEffectMgr);
		peerageComponent.setPeeragePersistenceObject(peeragePersistenceObject);
		saveComponent.addPersistenceParameters(peerageComponent.getPeeragePersistenceObject());
	}

	@SuppressWarnings("rawtypes")
	private void configImmediateSaveComponentTo(Player player) {
		PlayerImmediateSaveComponent saveComponent = player.getPlayerImmediateSaveComponent();
		saveComponent.addPersistenceParameter(new PersistenceParameter("id", player.getId()));
		saveComponent.addPersistenceParameter(new PersistenceParameter("identityId", player.getIdentity().getId()));
		saveComponent.addPersistenceParameter(new PersistenceParameter("identityName", player.getIdentity().getName()));
		saveComponent.addIndependentPropertyParameter(new PersistenceParameter("name", player.getName()));

		// talisman
		MGPlayerTalismanComponent talismanComponent = (MGPlayerTalismanComponent) player.getTagged(MGPlayerTalismanComponent.Tag);
		MGPlayerCitta talismanMgr = new MGPlayerCitta();
		MGTalismanEffectMgr talismanEffectMgr = new MGTalismanEffectMgr(player);
		TalismanPersistenceObject persistenceObject = new TalismanPersistenceObject(talismanMgr, player);
		talismanComponent.setPlayerCitta(talismanMgr);
		talismanComponent.setTalismanEffectMgr(talismanEffectMgr);
		talismanComponent.setPersistenceObject(persistenceObject);
		saveComponent.addPersistenceParameters(persistenceObject);

		// achievement
		MGPlayerAchievementComponent achievementComponent = (MGPlayerAchievementComponent) player.getTagged(MGPlayerAchievementComponent.Tag);
		achievementComponent.getAchievementMgr().setPlayer(player);
		achievementComponent.getAchievePointMgr().setPlayer(player);
		MGAchievementPersistenceObject achievementPersistenceObject = new MGAchievementPersistenceObject(player);
		achievementComponent.setAchievementPersistenceObject(achievementPersistenceObject);
		saveComponent.addPersistenceParameters(achievementPersistenceObject);

		// buff
		MGFightSpriteBuffComponent fightSpriteBuffComponent = (MGFightSpriteBuffComponent) player.getTagged(MGFightSpriteBuffComponent.Tag);
		BuffPersistenceObject buffPersistenceObject = new BuffPersistenceObject(fightSpriteBuffComponent.getFightSpriteBuffMgr(), player);
		fightSpriteBuffComponent.setPersistenceObject(buffPersistenceObject);
		saveComponent.addPersistenceParameters(buffPersistenceObject);

		// states(封号，禁言)由GM控制
		MGPlayerGMComponent playerGMComponent = (MGPlayerGMComponent) player.getTagged(MGPlayerGMComponent.Tag);
		PlayerGMPersistenceObject playerGMPersistenceObject = new PlayerGMPersistenceObject(playerGMComponent.getPlayerGMMgr());
		playerGMComponent.setPersistenceObject(playerGMPersistenceObject);
		saveComponent.addPersistenceParameters(playerGMPersistenceObject);

		// vip
		MGPlayerVipComponent playerVipComponent = (MGPlayerVipComponent) player.getTagged(MGPlayerVipComponent.Tag);
		MGVipLevelMgr vipMgr = new MGVipLevelMgr();
		MGVipLotteryMgr lotteryMgr = new MGVipLotteryMgr();
		MGVipEffectMgr vipEffectMgr = new MGVipEffectMgr(player);
		MGVipPersistenceObject vipPersistenceObject = new MGVipPersistenceObject(vipMgr, lotteryMgr, player);
		playerVipComponent.setPersistenceObject(vipPersistenceObject);
		playerVipComponent.setVipMgr(vipMgr);
		playerVipComponent.setLotteryMgr(lotteryMgr);
		playerVipComponent.setVipEffectMgr(vipEffectMgr);
		saveComponent.addPersistenceParameters(vipPersistenceObject);

		// activity
		MGPlayerActivityComponent playerActivityComponent = (MGPlayerActivityComponent) player.getTagged(MGPlayerActivityComponent.Tag);
		playerActivityComponent.getSignMgr().setOwner(player);
		playerActivityComponent.getAdvanceMgr().setOwner(player);
		playerActivityComponent.getOnlineMgr().setPlayer(player);
		playerActivityComponent.getLevelUpMgr().setOwner(player);
		ActivityPersistenceObject activityPersistenceObject = new ActivityPersistenceObject(player);
		playerActivityComponent.setActivityPersistenceObject(activityPersistenceObject);
		saveComponent.addPersistenceParameters(activityPersistenceObject);

		// castleWar
		MGCastleWarComponent castleWarComponent = (MGCastleWarComponent) player.getTagged(MGCastleWarComponent.Tag);
		MGCastleWarPersistenceObject castleWarPersistence = new MGCastleWarPersistenceObject(player);
		castleWarComponent.setPersisteneceObject(castleWarPersistence);
		saveComponent.addPersistenceParameters(castleWarComponent.getPersisteneceObject());

		// digs
		MGPlayerDigsComponent playerDigsComponent = (MGPlayerDigsComponent) player.getTagged(MGPlayerDigsComponent.Tag);
		DigsPersistenceObject digsPersistenceObject = new DigsPersistenceObject(playerDigsComponent.getDigsHouse());
		playerDigsComponent.setPersistenceObject(digsPersistenceObject);
		saveComponent.addPersistenceParameters(digsPersistenceObject);

		// mining
		MGPlayerMiningComponent playerMiningComponent = (MGPlayerMiningComponent) player.getTagged(MGPlayerMiningComponent.Tag);
		MGPlayerMiningManager playerMiningManager = new MGPlayerMiningManager(player);
		playerMiningComponent.setPlayerMiningManager(playerMiningManager);
	}

	private void bindedIdentityTo(Player player, Identity identity) {
		Preconditions.checkNotNull(identity, "identity can not be null.");
		player.setIdentity(identity);
	}

	private void configEquipBodyComponent(Player player) {
		PlayerEquipBody playerEquipBody = player.getPlayerEquipBodyConponent().getPlayerBody();
		List<PlayerEquipBodyArea> bodyAreaList = new ArrayList<PlayerEquipBodyArea>();
		playerEquipBody.setBodyAreaList(bodyAreaList);

		PlayerEquipBodyArea area1 = PlayerEquipBodyArea.singleBodyArea(PlayerEquipBodyArea.weaponBodyId);
		PlayerEquipBodyArea area2 = PlayerEquipBodyArea.singleBodyArea(PlayerEquipBodyArea.clothesBodyId);
		PlayerEquipBodyArea area3 = PlayerEquipBodyArea.singleBodyArea(PlayerEquipBodyArea.helmetBodyId);
		PlayerEquipBodyArea area4 = PlayerEquipBodyArea.singleBodyArea(PlayerEquipBodyArea.beltBodyId);
		PlayerEquipBodyArea area5 = PlayerEquipBodyArea.singleBodyArea(PlayerEquipBodyArea.shoesBodyId);
		PlayerEquipBodyArea area6 = PlayerEquipBodyArea.singleBodyArea(PlayerEquipBodyArea.necklaceBodyId);
		PlayerEquipBodyArea area7 = PlayerEquipBodyArea.leftRightBodyArea(PlayerEquipBodyArea.braceletBodyId);
		PlayerEquipBodyArea area8 = PlayerEquipBodyArea.leftRightBodyArea(PlayerEquipBodyArea.ringBodyId);
		PlayerEquipBodyArea area9 = PlayerEquipBodyArea.singleBodyArea(PlayerEquipBodyArea.medalBodyId);

		bodyAreaList.add(area1);
		bodyAreaList.add(area2);
		bodyAreaList.add(area3);
		bodyAreaList.add(area4);
		bodyAreaList.add(area5);
		bodyAreaList.add(area6);
		bodyAreaList.add(area7);
		bodyAreaList.add(area8);
		bodyAreaList.add(area9);
	}

	private void configFightSkillComponent(Player player) {
		PlayerFightSkillTree playerFightSkillTree = new PlayerFightSkillTree(player);
		PlayerFightSkillComponent playerFightSkillComponent = player.getPlayerFightSkillComponent();
		playerFightSkillComponent.setPlayerFightSkillTree(playerFightSkillTree);

		FightSkillRuntimeComponent<? extends FightSprite> fightSkillRuntimeComponent = player.getFightSkillRuntimeComponent();
		MGFightSkillRuntime skillRuntime = new MGFightSkillRuntime();
		fightSkillRuntimeComponent.setFightSkillRuntime(skillRuntime);
	}

	public static void configGameInstanceTo(Player player) {
	}

}
