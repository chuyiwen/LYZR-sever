package newbee.morningGlory.checker;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import newbee.morningGlory.checker.refObjectChecker.GiftBag.GiftBagChecker;
import newbee.morningGlory.checker.refObjectChecker.HighestEquipmentChecker.HighestEquipmentChecker;
import newbee.morningGlory.checker.refObjectChecker.MGGiftCodeChecker.GiftCodeChecker;
import newbee.morningGlory.checker.refObjectChecker.MGOldPlayerDataChecker.MGOldPlayerDataChecker;
import newbee.morningGlory.checker.refObjectChecker.MGResDownLoadRefChecker.MGResDownRefChecker;
import newbee.morningGlory.checker.refObjectChecker.QuickRechargeChecker.QuickRechargeChecker;
import newbee.morningGlory.checker.refObjectChecker.SystemPrompt.SystemPromptChecker;
import newbee.morningGlory.checker.refObjectChecker.UnionGameInstanceChecker.UnionGameInstanceChecker;
import newbee.morningGlory.checker.refObjectChecker.achievement.AchievementRefChecker;
import newbee.morningGlory.checker.refObjectChecker.achievement.MedalRefChecker;
import newbee.morningGlory.checker.refObjectChecker.activity.ArenaRewardRefChecker;
import newbee.morningGlory.checker.refObjectChecker.activity.DayOnlineRefChecker;
import newbee.morningGlory.checker.refObjectChecker.activity.DiscountRefChecker;
import newbee.morningGlory.checker.refObjectChecker.activity.FundRefChecker;
import newbee.morningGlory.checker.refObjectChecker.activity.LevelUpRewardRefChecker;
import newbee.morningGlory.checker.refObjectChecker.activity.LimitTimeRankRefChecker;
import newbee.morningGlory.checker.refObjectChecker.activity.OnlineRefChecker;
import newbee.morningGlory.checker.refObjectChecker.activity.RankBeginEndTimeRefChecker;
import newbee.morningGlory.checker.refObjectChecker.activity.RideRewardRefChecker;
import newbee.morningGlory.checker.refObjectChecker.activity.SignRefChceker;
import newbee.morningGlory.checker.refObjectChecker.activity.WingRewardRefChecker;
import newbee.morningGlory.checker.refObjectChecker.buff.BuffRefChecker;
import newbee.morningGlory.checker.refObjectChecker.character.PlayerProfessionRefChecker;
import newbee.morningGlory.checker.refObjectChecker.dailyQuest.DailyQuestRefChecker;
import newbee.morningGlory.checker.refObjectChecker.digs.DigsChecker;
import newbee.morningGlory.checker.refObjectChecker.equipmentFenJie.EquipmentRefChecker;
import newbee.morningGlory.checker.refObjectChecker.equipmentStrength.MGQiangHuaEquipmentConfigChecker;
import newbee.morningGlory.checker.refObjectChecker.equipmentWash.MGXiLianEquipmentConfigChecker;
import newbee.morningGlory.checker.refObjectChecker.gameConstant.GameConstantRefChecker;
import newbee.morningGlory.checker.refObjectChecker.gameInstance.GameInstanceRefChecker;
import newbee.morningGlory.checker.refObjectChecker.item.ItemRefChecker;
import newbee.morningGlory.checker.refObjectChecker.item.UnPropsItemRefChecker;
import newbee.morningGlory.checker.refObjectChecker.mall.MallRefChecker;
import newbee.morningGlory.checker.refObjectChecker.monster.MonsterDropRefChecker;
import newbee.morningGlory.checker.refObjectChecker.monster.MonsterRefChecker;
import newbee.morningGlory.checker.refObjectChecker.mount.MountRefChecker;
import newbee.morningGlory.checker.refObjectChecker.npc.NpcRefChecker;
import newbee.morningGlory.checker.refObjectChecker.offLineAI.HandUpSkillRefChecker;
import newbee.morningGlory.checker.refObjectChecker.offLineAI.OfflineAIMapRefChecker;
import newbee.morningGlory.checker.refObjectChecker.operatActivity.operatActivityChecker;
import newbee.morningGlory.checker.refObjectChecker.peerage.PeerageRefChecker;
import newbee.morningGlory.checker.refObjectChecker.pkDrop.ScenePkDropChecker;
import newbee.morningGlory.checker.refObjectChecker.pluck.PluckRefChecker;
import newbee.morningGlory.checker.refObjectChecker.quest.QuestRefChecker;
import newbee.morningGlory.checker.refObjectChecker.scene.SceneRefChecker;
import newbee.morningGlory.checker.refObjectChecker.sceneActivity.MonsterInvasionScrollRefChecker;
import newbee.morningGlory.checker.refObjectChecker.sceneActivity.SceneActivityRefChecker;
import newbee.morningGlory.checker.refObjectChecker.sectonQuest.SectionQuestRefChecker;
import newbee.morningGlory.checker.refObjectChecker.skill.SkillRefChecker;
import newbee.morningGlory.checker.refObjectChecker.store.ShopRefChecker;
import newbee.morningGlory.checker.refObjectChecker.tailsman.MGCittaRefChecker;
import newbee.morningGlory.checker.refObjectChecker.tailsman.MGTalismanRefChecker;
import newbee.morningGlory.checker.refObjectChecker.vip.VipChecker;
import newbee.morningGlory.checker.refObjectChecker.vipLottery.VipLotteryChecker;
import newbee.morningGlory.checker.refObjectChecker.wing.WingRefChecker;
import newbee.morningGlory.checker.refObjectChecker.worldBossMsgRef.WorldBossMsgRefChecker;
import newbee.morningGlory.mmorpg.operatActivities.OperatActivityRef;
import newbee.morningGlory.mmorpg.player.achievement.MGPlayerAchievementRef;
import newbee.morningGlory.mmorpg.player.achievement.medal.MGMedalConfig;
import newbee.morningGlory.mmorpg.player.activity.QuickRecharge.QuickRechargeRef;
import newbee.morningGlory.mmorpg.player.activity.digs.ref.MGDigsDataConfig;
import newbee.morningGlory.mmorpg.player.activity.fund.ref.FundRef;
import newbee.morningGlory.mmorpg.player.activity.giftCode.MGGiftCodeDataTypeRef;
import newbee.morningGlory.mmorpg.player.activity.ladder.ArenaRewardRef;
import newbee.morningGlory.mmorpg.player.activity.limitTimeRank.ref.LimitTimeRankRef;
import newbee.morningGlory.mmorpg.player.activity.limitTimeRank.ref.RankBeginEndTimeRef;
import newbee.morningGlory.mmorpg.player.activity.oldPlayer.MGOldPlayerDataRef;
import newbee.morningGlory.mmorpg.player.activity.ref.DayOnlineRef;
import newbee.morningGlory.mmorpg.player.activity.ref.LevelUpRewardRef;
import newbee.morningGlory.mmorpg.player.activity.ref.OnlineRef;
import newbee.morningGlory.mmorpg.player.activity.ref.RideRewardRef;
import newbee.morningGlory.mmorpg.player.activity.ref.SignRef;
import newbee.morningGlory.mmorpg.player.activity.ref.WingRewardRef;
import newbee.morningGlory.mmorpg.player.activity.resDownload.MGResDownLoadDataRef;
import newbee.morningGlory.mmorpg.player.dailyQuest.ref.MGDailyQuestRef;
import newbee.morningGlory.mmorpg.player.itemBag.gift.MGGiftBagConfig;
import newbee.morningGlory.mmorpg.player.offLineAI.ref.HandUpSkillRef;
import newbee.morningGlory.mmorpg.player.offLineAI.ref.OfflineAIMapRef;
import newbee.morningGlory.mmorpg.player.peerage.MGPeerageRef;
import newbee.morningGlory.mmorpg.player.pk.ref.MGScenePKDropRef;
import newbee.morningGlory.mmorpg.player.sectionQuest.MGSectionQuestRef;
import newbee.morningGlory.mmorpg.player.talisman.MGCittaRef;
import newbee.morningGlory.mmorpg.player.talisman.MGTalismanRef;
import newbee.morningGlory.mmorpg.player.talisman.level.MGTalismanDataConfig;
import newbee.morningGlory.mmorpg.player.unionGameInstance.UnionGameInstanceRef;
import newbee.morningGlory.mmorpg.player.wing.MGPlayerWingRef;
import newbee.morningGlory.mmorpg.sceneActivities.SceneActivityRef;
import newbee.morningGlory.mmorpg.sceneActivities.monsterIntrusion.ref.MonsterInvasionScrollRef;
import newbee.morningGlory.mmorpg.sprite.buff.MGFightSpriteBuffRef;
import newbee.morningGlory.mmorpg.store.ref.DiscountRef;
import newbee.morningGlory.mmorpg.store.ref.MallItemRef;
import newbee.morningGlory.mmorpg.store.ref.ShopItemRef;
import newbee.morningGlory.mmorpg.vip.MGVipLevelDataRef;
import newbee.morningGlory.mmorpg.vip.lottery.ref.MGVipLotteryDataConfig;
import sophia.game.ref.GameRefObject;
import sophia.game.ref.GameRefObjectLoader;
import sophia.mmorpg.base.scene.ref.SceneRef;
import sophia.mmorpg.equipmentSmith.smith.fenJie.MGFenJieEquipmentConfig;
import sophia.mmorpg.equipmentSmith.smith.highestEquipment.HighestEquipmentRef;
import sophia.mmorpg.equipmentSmith.smith.qiangHua.MGQiangHuaEquipmentConfig;
import sophia.mmorpg.equipmentSmith.smith.xiLian.MGXiLianDataRef;
import sophia.mmorpg.gameInstance.GameInstanceRef;
import sophia.mmorpg.item.ref.ItemRef;
import sophia.mmorpg.item.ref.UnPropsItemRef;
import sophia.mmorpg.monster.ref.MonsterRef;
import sophia.mmorpg.monster.ref.drop.MonsterDropRef;
import sophia.mmorpg.npc.ref.NpcRef;
import sophia.mmorpg.player.chat.SystemPromptConfigRef;
import sophia.mmorpg.player.fightSkill.ref.SkillRef;
import sophia.mmorpg.player.mount.MountRef;
import sophia.mmorpg.player.quest.ref.QuestRef;
import sophia.mmorpg.player.ref.PlayerProfessionRef;
import sophia.mmorpg.player.worldBossMsg.WorldBossMsgRef;
import sophia.mmorpg.pluck.PluckRef;
import sophia.mmorpg.ref.GameConstantRef;

public final class RefCheckerManager implements RefCheckerManagerRuntime {

	private static CheckOutputCtrl out = CheckOutputCtrl.Default;
	private static DefaultRefChecker _defaultRefChecker = new DefaultRefChecker();
	private final static Map<Class<?>, RefChecker<?>> _checkersMapSlaver = new HashMap<Class<?>, RefChecker<?>>();
	public final static Map<Class<?>, RefChecker<?>> _checkersMapRef = new HashMap<Class<?>, RefChecker<?>>();

	static {
		registerRefChecker(new ShopRefChecker(), ShopItemRef.class);
		registerRefChecker(new MallRefChecker(), MallItemRef.class);
		registerRefChecker(new QuestRefChecker(), QuestRef.class);
		registerRefChecker(new DailyQuestRefChecker(), MGDailyQuestRef.class);
		registerRefChecker(new WingRefChecker(), MGPlayerWingRef.class);
		registerRefChecker(new NpcRefChecker(), NpcRef.class);
		registerRefChecker(new SectionQuestRefChecker(), MGSectionQuestRef.class);
		registerRefChecker(new PeerageRefChecker(), MGPeerageRef.class);
		registerRefChecker(new AchievementRefChecker(), MGPlayerAchievementRef.class);
		registerRefChecker(new MedalRefChecker(), MGMedalConfig.class);
		registerRefChecker(new PluckRefChecker(), PluckRef.class);
		registerRefChecker(new GameInstanceRefChecker(), GameInstanceRef.class);
		registerRefChecker(new MountRefChecker(), MountRef.class);
		registerRefChecker(new UnPropsItemRefChecker(), UnPropsItemRef.class);
		registerRefChecker(new BuffRefChecker(), MGFightSpriteBuffRef.class);
		registerRefChecker(new MGQiangHuaEquipmentConfigChecker(), MGQiangHuaEquipmentConfig.class);
		registerRefChecker(new MGXiLianEquipmentConfigChecker(), MGXiLianDataRef.class);
		registerRefChecker(new ItemRefChecker(), ItemRef.class);
		registerRefChecker(new MGTalismanRefChecker(), MGTalismanRef.class);
		registerRefChecker(new SkillRefChecker(), SkillRef.class);
		registerRefChecker(new HandUpSkillRefChecker(), HandUpSkillRef.class);
		registerRefChecker(new OfflineAIMapRefChecker(), OfflineAIMapRef.class);
		registerRefChecker(new MonsterRefChecker(), MonsterRef.class);
		registerRefChecker(new MonsterDropRefChecker(), MonsterDropRef.class);
		registerRefChecker(new SceneActivityRefChecker(), SceneActivityRef.class);
		registerRefChecker(new DigsChecker(), MGDigsDataConfig.class);
		registerRefChecker(new ScenePkDropChecker(), MGScenePKDropRef.class);
		registerRefChecker(new VipLotteryChecker(), MGVipLotteryDataConfig.class);
		registerRefChecker(new MGTalismanRefChecker(), MGTalismanDataConfig.class);
		registerRefChecker(new VipChecker(), MGVipLevelDataRef.class);
		registerRefChecker(new operatActivityChecker(), OperatActivityRef.class);
		registerRefChecker(new GiftBagChecker(), MGGiftBagConfig.class);
		registerRefChecker(new EquipmentRefChecker(), MGFenJieEquipmentConfig.class);
		registerRefChecker(new ArenaRewardRefChecker(), ArenaRewardRef.class);
		registerRefChecker(new DayOnlineRefChecker(), DayOnlineRef.class);
		registerRefChecker(new FundRefChecker(), FundRef.class);
		registerRefChecker(new LevelUpRewardRefChecker(), LevelUpRewardRef.class);
		registerRefChecker(new LimitTimeRankRefChecker(), LimitTimeRankRef.class);
		registerRefChecker(new WingRewardRefChecker(), WingRewardRef.class);
		registerRefChecker(new SignRefChceker(), SignRef.class);
		registerRefChecker(new DiscountRefChecker(), DiscountRef.class);
		registerRefChecker(new OnlineRefChecker(), OnlineRef.class);
		registerRefChecker(new RideRewardRefChecker(), RideRewardRef.class);
		registerRefChecker(new RankBeginEndTimeRefChecker(), RankBeginEndTimeRef.class);
		registerRefChecker(new SystemPromptChecker(), SystemPromptConfigRef.class);
		registerRefChecker(new PlayerProfessionRefChecker(), PlayerProfessionRef.class);
		registerRefChecker(new SceneRefChecker(), SceneRef.class);
		registerRefChecker(new MonsterInvasionScrollRefChecker(), MonsterInvasionScrollRef.class);
		registerRefChecker(new GiftCodeChecker(), MGGiftCodeDataTypeRef.class);
		registerRefChecker(new GameConstantRefChecker(), GameConstantRef.class);
		registerRefChecker(new WorldBossMsgRefChecker(), WorldBossMsgRef.class);
		registerRefChecker(new MGCittaRefChecker(), MGCittaRef.class);
		registerRefChecker(new MGResDownRefChecker(), MGResDownLoadDataRef.class);
		
		registerRefChecker(new QuickRechargeChecker(), QuickRechargeRef.class);
		registerRefChecker(new HighestEquipmentChecker(), HighestEquipmentRef.class);
		registerRefChecker(new UnionGameInstanceChecker(), UnionGameInstanceRef.class);
		registerRefChecker(new MGOldPlayerDataChecker(), MGOldPlayerDataRef.class);

	
	}

	public static void registerRefChecker(RefChecker<?> refChecker, Class<?>... refClss) {
		for (Class<?> cls : refClss)
			_checkersMapRef.put(cls, refChecker);
	}

	public CheckOutputCtrl getOutputCtrl() {
		return out;
	}
 
	public static void setOutputCtrl(CheckOutputCtrl checkOutputCtrl) {
		out = checkOutputCtrl;
	}

	public Collection<RefChecker<?>> getAllChecker() {
		return _checkersMapRef.values();
	}

	public RefChecker<?> getDefaultChecker() {
		return _defaultRefChecker;
	}

	public RefChecker<?> getRefObjectChecker(GameRefObject gameRefObject) {
		return _checkersMapRef.get(gameRefObject.getClass());
	}

	public RefChecker<?> getRefObjectChecker(GameRefObjectLoader<?> loadSlaver) {
		RefChecker<?> refChecker = _checkersMapSlaver.get(loadSlaver.getClass());
		if (refChecker == null)
			return getDefaultChecker();
		return refChecker;
	}

}