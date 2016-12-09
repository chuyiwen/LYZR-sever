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
package newbee.morningGlory.ref;

import newbee.morningGlory.mmorpg.player.achievement.medal.MGMedalLoader;
import newbee.morningGlory.mmorpg.player.itemBag.gift.MGGiftBagConfigLoader;
import newbee.morningGlory.ref.loader.CastleWarRefLoader;
import newbee.morningGlory.ref.loader.EquipmentRefLoader;
import newbee.morningGlory.ref.loader.FunStepDataRefLoader;
import newbee.morningGlory.ref.loader.GameConstantRefLoader;
import newbee.morningGlory.ref.loader.HandUpSkillRefLoader;
import newbee.morningGlory.ref.loader.MGCittaRefLoader;
import newbee.morningGlory.ref.loader.MGDailyQuestRefLoader;
import newbee.morningGlory.ref.loader.MGFenJieEquipmentConfigLoader;
import newbee.morningGlory.ref.loader.MGFightSpriteBuffRefLoader;
import newbee.morningGlory.ref.loader.MGGiftCodeDataLoader;
import newbee.morningGlory.ref.loader.MGHighestEquipmentLoader;
import newbee.morningGlory.ref.loader.MGPeerageRefLoader;
import newbee.morningGlory.ref.loader.MGPkDropLoader;
import newbee.morningGlory.ref.loader.MGPlayerAchievementRefLoader;
import newbee.morningGlory.ref.loader.MGQiangHuaEquipmentConfigLoader;
import newbee.morningGlory.ref.loader.MGResDownloadDataRefLoader;
import newbee.morningGlory.ref.loader.MGSectionQuestRefLoader;
import newbee.morningGlory.ref.loader.MGTalisManRefLoader;
import newbee.morningGlory.ref.loader.MGWingRefLoader;
import newbee.morningGlory.ref.loader.MGWorldBossMsgRefLoader;
import newbee.morningGlory.ref.loader.MGXiLianEquipmentConfigLoader;
import newbee.morningGlory.ref.loader.MallRefLoader;
import newbee.morningGlory.ref.loader.MonsterDropRefLoader;
import newbee.morningGlory.ref.loader.MonsterRefLoader;
import newbee.morningGlory.ref.loader.MountRefLoader;
import newbee.morningGlory.ref.loader.NpcRefLoader;
import newbee.morningGlory.ref.loader.OfflineAIMapRefLoader;
import newbee.morningGlory.ref.loader.OnlineRefLoader;
import newbee.morningGlory.ref.loader.PlayerProfessionRefLoader;
import newbee.morningGlory.ref.loader.PluckRefLoader;
import newbee.morningGlory.ref.loader.PropsItemRefLoader;
import newbee.morningGlory.ref.loader.QuestRefLoader;
import newbee.morningGlory.ref.loader.SceneRefLoader;
import newbee.morningGlory.ref.loader.ShopRefLoader;
import newbee.morningGlory.ref.loader.SignRefLoader;
import newbee.morningGlory.ref.loader.SkillMonsterLevelRefLoader;
import newbee.morningGlory.ref.loader.SkillRefLoader;
import newbee.morningGlory.ref.loader.SystemPromptConfigRefLoader;
import newbee.morningGlory.ref.loader.UnPropsItemRefLoader;
import newbee.morningGlory.ref.loader.VipLevelDataRefLoader;
import newbee.morningGlory.ref.loader.activity.ArenaRewardRefLoader;
import newbee.morningGlory.ref.loader.activity.DiscountRefLoader;
import newbee.morningGlory.ref.loader.activity.FundRefLoader;
import newbee.morningGlory.ref.loader.activity.LevelUpRewardRefLoader;
import newbee.morningGlory.ref.loader.activity.LimitTimeRankRefLoader;
import newbee.morningGlory.ref.loader.activity.MGDigsRefLoader;
import newbee.morningGlory.ref.loader.activity.MGOldPlayerDataLoader;
import newbee.morningGlory.ref.loader.activity.MGQuickRechargeLoader;
import newbee.morningGlory.ref.loader.activity.MGVipLotteryRefLoader;
import newbee.morningGlory.ref.loader.activity.MiningSceneActivityRefLoader;
import newbee.morningGlory.ref.loader.activity.MonsterInvasionLoader;
import newbee.morningGlory.ref.loader.activity.MonsterInvasionScrollLoader;
import newbee.morningGlory.ref.loader.activity.MutilExpActivityRefLoader;
import newbee.morningGlory.ref.loader.activity.OperatActivityRefLoader;
import newbee.morningGlory.ref.loader.activity.PayOnPalaceRefLoader;
import newbee.morningGlory.ref.loader.activity.RankBeginEndTimeRefLoader;
import newbee.morningGlory.ref.loader.activity.RideRewardRefLoader;
import newbee.morningGlory.ref.loader.activity.TeamBossRefLoader;
import newbee.morningGlory.ref.loader.gameInstance.GameInstanceRefLoader;
import newbee.morningGlory.ref.loader.gameInstance.UnionGameInstanceRefLoader;
import sophia.game.GameRoot;
import sophia.game.ref.GameRefObjectDataManager;
import sophia.game.ref.GameRefObjetLoaderRegister;

import com.google.common.base.Preconditions;

public class ConcreteGameRefObjetLoaderRegister implements GameRefObjetLoaderRegister {

	@Override
	public void registAllGameRefObjectLoadSlaver() {
		GameRefObjectDataManager gameRefObjectDataManager = GameRoot.getGameRefObjectDataManager();
		registGameInstanceGameRefObjectLoadSlaver(RefKey.instance_List, gameRefObjectDataManager);
		gameRefObjectDataManager.addGameRefObjectLoader(RefKey.propsitem, new PropsItemRefLoader());
		gameRefObjectDataManager.addGameRefObjectLoader(RefKey.equipment, new EquipmentRefLoader());
		gameRefObjectDataManager.addGameRefObjectLoader(RefKey.skill, new SkillRefLoader());
		gameRefObjectDataManager.addGameRefObjectLoader(RefKey.scene, new SceneRefLoader());
		gameRefObjectDataManager.addGameRefObjectLoader(RefKey.monster, new MonsterRefLoader());
		gameRefObjectDataManager.addGameRefObjectLoader(RefKey.monsterDrop, new MonsterDropRefLoader(RefKey.monsterDrop));
		gameRefObjectDataManager.addGameRefObjectLoader(RefKey.character, new PlayerProfessionRefLoader());
		gameRefObjectDataManager.addGameRefObjectLoader(RefKey.questData, new QuestRefLoader());
		gameRefObjectDataManager.addGameRefObjectLoader(RefKey.dailyQuest, new MGDailyQuestRefLoader());
		gameRefObjectDataManager.addGameRefObjectLoader(RefKey.sectionQuest, new MGSectionQuestRefLoader());
		gameRefObjectDataManager.addGameRefObjectLoader(RefKey.mount, new MountRefLoader());
		gameRefObjectDataManager.addGameRefObjectLoader(RefKey.peerage, new MGPeerageRefLoader());
		gameRefObjectDataManager.addGameRefObjectLoader(RefKey.equipStrengthening, new MGQiangHuaEquipmentConfigLoader());
		gameRefObjectDataManager.addGameRefObjectLoader(RefKey.equipWashProperty, new MGXiLianEquipmentConfigLoader());
		gameRefObjectDataManager.addGameRefObjectLoader(RefKey.equipResolve, new MGFenJieEquipmentConfigLoader());
		gameRefObjectDataManager.addGameRefObjectLoader(RefKey.wing, new MGWingRefLoader());
		gameRefObjectDataManager.addGameRefObjectLoader(RefKey.monsterLevel, new SkillMonsterLevelRefLoader());
		gameRefObjectDataManager.addGameRefObjectLoader(RefKey.talisman, new MGTalisManRefLoader());
		gameRefObjectDataManager.addGameRefObjectLoader(RefKey.npc, new NpcRefLoader());
		gameRefObjectDataManager.addGameRefObjectLoader(RefKey.unPropsitem, new UnPropsItemRefLoader());
		gameRefObjectDataManager.addGameRefObjectLoader(RefKey.achievement, new MGPlayerAchievementRefLoader());
		gameRefObjectDataManager.addGameRefObjectLoader(RefKey.medal, new MGMedalLoader());
		gameRefObjectDataManager.addGameRefObjectLoader(RefKey.d_buff, new MGFightSpriteBuffRefLoader());
		gameRefObjectDataManager.addGameRefObjectLoader(RefKey.gift, new MGGiftBagConfigLoader());
		gameRefObjectDataManager.addGameRefObjectLoader(RefKey.mall, new MallRefLoader());
		gameRefObjectDataManager.addGameRefObjectLoader(RefKey.shop, new ShopRefLoader());
		gameRefObjectDataManager.addGameRefObjectLoader(RefKey.collect, new PluckRefLoader());
		gameRefObjectDataManager.addGameRefObjectLoader(RefKey.vip, new VipLevelDataRefLoader());
		gameRefObjectDataManager.addGameRefObjectLoader(RefKey.viplottery, new MGVipLotteryRefLoader());
		gameRefObjectDataManager.addGameRefObjectLoader(RefKey.systemPromptConfig, new SystemPromptConfigRefLoader());
		gameRefObjectDataManager.addGameRefObjectLoader(RefKey.sign, new SignRefLoader());
		gameRefObjectDataManager.addGameRefObjectLoader(RefKey.online, new OnlineRefLoader());
		// gameRefObjectDataManager.addGameRefObjectLoader(RefKey.dailyOnline, new DayOnlineRefLoader());
		// gameRefObjectDataManager.addGameRefObjectLoader(RefKey.wingReward, new WingRewardRefLoader());
		gameRefObjectDataManager.addGameRefObjectLoader(RefKey.rideReward, new RideRewardRefLoader());
		// gameRefObjectDataManager.addGameRefObjectLoader(RefKey.talisReward, new TalisRewardRefLoader());
		gameRefObjectDataManager.addGameRefObjectLoader(RefKey.levelUpReward, new LevelUpRewardRefLoader());
		gameRefObjectDataManager.addGameRefObjectLoader(RefKey.limitTimeRank, new LimitTimeRankRefLoader());
		gameRefObjectDataManager.addGameRefObjectLoader(RefKey.rankBeginEndTime, new RankBeginEndTimeRefLoader());
		gameRefObjectDataManager.addGameRefObjectLoader(RefKey.discount, new DiscountRefLoader());
		gameRefObjectDataManager.addGameRefObjectLoader(RefKey.fund, new FundRefLoader());
		gameRefObjectDataManager.addGameRefObjectLoader(RefKey.digs, new MGDigsRefLoader());
		gameRefObjectDataManager.addGameRefObjectLoader(RefKey.mining, new MiningSceneActivityRefLoader(RefKey.mining));
		gameRefObjectDataManager.addGameRefObjectLoader(RefKey.arenaReward, new ArenaRewardRefLoader());
		gameRefObjectDataManager.addGameRefObjectLoader(RefKey.pkdrop, new MGPkDropLoader());
		gameRefObjectDataManager.addGameRefObjectLoader(RefKey.castle, new CastleWarRefLoader());
		gameRefObjectDataManager.addGameRefObjectLoader(RefKey.offlineAIMap, new OfflineAIMapRefLoader());
		gameRefObjectDataManager.addGameRefObjectLoader(RefKey.handUpSkill, new HandUpSkillRefLoader());
		gameRefObjectDataManager.addGameRefObjectLoader(RefKey.operatActivity, new OperatActivityRefLoader());
		gameRefObjectDataManager.addGameRefObjectLoader(RefKey.monsterInvasion, new MonsterInvasionLoader());
		gameRefObjectDataManager.addGameRefObjectLoader(RefKey.monsterInvasionScroll, new MonsterInvasionScrollLoader());
		gameRefObjectDataManager.addGameRefObjectLoader(RefKey.worldBoss, new MGWorldBossMsgRefLoader());
		gameRefObjectDataManager.addGameRefObjectLoader(RefKey.resDownload, new MGResDownloadDataRefLoader());
		gameRefObjectDataManager.addGameRefObjectLoader(RefKey.giftCode, new MGGiftCodeDataLoader());
		gameRefObjectDataManager.addGameRefObjectLoader(RefKey.citta, new MGCittaRefLoader());
		gameRefObjectDataManager.addGameRefObjectLoader(RefKey.unionGameInstance, new UnionGameInstanceRefLoader());
		gameRefObjectDataManager.addGameRefObjectLoader(RefKey.funStep, new FunStepDataRefLoader());
		gameRefObjectDataManager.addGameRefObjectLoader(RefKey.teamBoss, new TeamBossRefLoader());
		gameRefObjectDataManager.addGameRefObjectLoader(RefKey.oldPlayer, new MGOldPlayerDataLoader());
		gameRefObjectDataManager.addGameRefObjectLoader(RefKey.yuanbao, new MGQuickRechargeLoader());
		gameRefObjectDataManager.addGameRefObjectLoader(RefKey.gameConstant, new GameConstantRefLoader());
		gameRefObjectDataManager.addGameRefObjectLoader(RefKey.payOnPalace, new PayOnPalaceRefLoader());
		gameRefObjectDataManager.addGameRefObjectLoader(RefKey.multiExp, new MutilExpActivityRefLoader());
		gameRefObjectDataManager.addGameRefObjectLoader(RefKey.hightestEquipment, new MGHighestEquipmentLoader());
	}

	public void registGameInstanceGameRefObjectLoadSlaver(String gameInstancs, GameRefObjectDataManager gameRefObjectDataManager) {
		Preconditions.checkNotNull(gameInstancs);
		String[] gameInstancsArr = gameInstancs.split(",");
		for (String instanceRefKey : gameInstancsArr) {
			gameRefObjectDataManager.addGameRefObjectLoader(instanceRefKey, new GameInstanceRefLoader(instanceRefKey));
		}
	}

}
