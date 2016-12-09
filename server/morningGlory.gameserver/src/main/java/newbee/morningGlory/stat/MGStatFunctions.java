package newbee.morningGlory.stat;

import newbee.morningGlory.mmorpg.ladder.MGLadderMember;
import newbee.morningGlory.mmorpg.player.achievement.MGPlayerAchievementRef;
import newbee.morningGlory.mmorpg.player.peerage.MGPeerageRef;
import newbee.morningGlory.mmorpg.player.wing.MGPlayerWingRef;
import newbee.morningGlory.mmorpg.union.MGUnionMember;
import newbee.morningGlory.stat.logs.AchievePointStat;
import newbee.morningGlory.stat.logs.MeritPointStat;
import newbee.morningGlory.stat.logs.StatAchievement;
import newbee.morningGlory.stat.logs.StatArena;
import newbee.morningGlory.stat.logs.StatAuction;
import newbee.morningGlory.stat.logs.StatCastleWar;
import newbee.morningGlory.stat.logs.StatDailyQuest;
import newbee.morningGlory.stat.logs.StatDigs;
import newbee.morningGlory.stat.logs.StatDigsItem;
import newbee.morningGlory.stat.logs.StatFund;
import newbee.morningGlory.stat.logs.StatGameInstance;
import newbee.morningGlory.stat.logs.StatGameInstanceQuest;
import newbee.morningGlory.stat.logs.StatGiftCode;
import newbee.morningGlory.stat.logs.StatKingCityUnion;
import newbee.morningGlory.stat.logs.StatLogin;
import newbee.morningGlory.stat.logs.StatPeerage;
import newbee.morningGlory.stat.logs.StatRecharge;
import newbee.morningGlory.stat.logs.StatSign;
import newbee.morningGlory.stat.logs.StatStore;
import newbee.morningGlory.stat.logs.StatTalisman;
import newbee.morningGlory.stat.logs.StatUnion;
import newbee.morningGlory.stat.logs.StatVip;
import newbee.morningGlory.stat.logs.StatVipLottery;
import newbee.morningGlory.stat.logs.StatWing;
import sophia.foundation.property.PropertyDictionary;
import sophia.game.GameRoot;
import sophia.mmorpg.equipmentSmith.EqiupmentComponentProvider;
import sophia.mmorpg.item.Item;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;
import sophia.mmorpg.stat.StatService;

public class MGStatFunctions {

	/**
	 * 
	 * @param player
	 * @param peerageRef
	 */
	public static void getOrUpgradeKnightStat(Player player, MGPeerageRef peerageRef) {
		StatPeerage log = StatPeerage.Pool.obtain();
		log.setPlayer(player);
		log.setPeerageRefId(peerageRef.getId());
		log.setPeerageLevel(peerageRef.getCrtKnightLevel());

		StatService.getInstance().save(log);
	}

	public static void getAchievementStat(Player player, MGPlayerAchievementRef achievementRef) {
		StatAchievement log = StatAchievement.Pool.obtain();

		PropertyDictionary pd = achievementRef.getProperty();
		log.setPlayer(player);

		log.setAchievementRefId(achievementRef.getId());
		log.setAchievementType(MGPropertyAccesser.getAchieveType(pd));
		log.setCompleteCondition(MGPropertyAccesser.getCompleteCondition(achievementRef.getProperty()));

		StatService.getInstance().save(log);
	}

	public static void unionStat(Player player, byte optType, String unionName, String unionId, byte officailId) {
		StatUnion log = StatUnion.Pool.obtain();

		log.setPlayer(player);
		log.setOptType(optType);
		log.setUnionName(unionName);
		log.setUnionId(unionId);
		log.setUnionOfficialId(officailId);

		StatService.getInstance().save(log);
	}

	public static void kingCityStat(Player player, String unionName, String createrPlayerName, long millis, MGUnionMember member) {
		StatKingCityUnion log = StatKingCityUnion.Pool.obtain();

		if (member == null) {
			return;
		}

		String memberName = member.getPlayerName();
		byte professionId = member.getProfessionId();
		int level = member.getLevel();
		int fightValue = member.getFightValue();
		byte unionOfficialId = member.getUnionOfficialId();
		long enterTime = member.getEnterTime();

		log.setPlayer(player);
		log.setUnionName(unionName);
		log.setCreaterPlayerName(createrPlayerName);
		log.setBecomeKingCityMillis(millis);

		log.setMemberName(memberName);
		log.setProfessionId(professionId);
		log.setLevel(level);
		log.setFightValue(fightValue);
		log.setUnionOfficialId(unionOfficialId);
		log.setEnterTime(enterTime);

		StatService.getInstance().save(log);
	}

	public static void arenaStat(Player player, MGLadderMember member) {
		StatArena log = StatArena.Pool.obtain();

		log.setPlayer(player);
		log.setRank(member.getRank());
		log.setStreak(member.getStreak());
		log.setRewardRank(member.getRewardRank());
		log.setRemainChallengeCount(member.getRemainChallengeCount());

		StatService.getInstance().save(log);
	}

	public static void talismanStat(Player player, byte optType, String talismanRefId, int crtLevel, int crtState) {
		StatTalisman log = StatTalisman.Pool.obtain();
		log.setPlayer(player);
		log.setCrtLevel(crtLevel);
		log.setCrtState(crtState);
		log.setOptType(optType);
		log.setTalismanRefId(talismanRefId);
		StatService.getInstance().save(log);
	}

	public static void wingStat(Player player, byte optType, String wingRefId, long exp) {
		StatWing log = StatWing.Pool.obtain();

		MGPlayerWingRef playerWingRef = (MGPlayerWingRef) GameRoot.getGameRefObjectManager().getManagedObject(wingRefId);
		byte stageLevel = playerWingRef.getCrtWingStageLevel();
		byte starLevel = playerWingRef.getCrtWingStarLevel();

		log.setPlayer(player);
		log.setOptType(optType);
		log.setStageLevel(stageLevel);
		log.setStarLevel(starLevel);
		log.setExp(exp);
		log.setWingRefId(wingRefId);

		StatService.getInstance().save(log);
	}

	public static void dailyQuestStat(Player player, byte type, String questRefId, int nowTime, int vipTime, int startlevel, String monsterRefId) {
		StatDailyQuest log = StatDailyQuest.Pool.obtain();
		log.setPlayer(player);
		log.setOptType(type);
		log.setQuestRefId(questRefId);
		log.setDailyQuestNowTime(nowTime);
		log.setVipAddRingTime(vipTime);
		log.setDailyQuestStartLevel(startlevel);
		log.setMonsterRefId(monsterRefId);
		StatService.getInstance().save(log);
	}

	public static void gameInstanceQuestStat(Player player, byte type, String questRefId, String gameInstanceRefId, String gameInstanceSceneId) {
		StatGameInstanceQuest log = StatGameInstanceQuest.Pool.obtain();
		log.setPlayer(player);
		log.setOptType(type);
		log.setQuestRefId(questRefId);
		log.setGameInstanceRefId(gameInstanceRefId);
		log.setGameInstanceSceneId(gameInstanceSceneId);
		StatService.getInstance().save(log);
	}

	public static void gameInstanceStat(Player player, byte type, String gameInstanceRefId, String gameInstanceSceneId) {
		StatGameInstance log = StatGameInstance.Pool.obtain();
		log.setPlayer(player);
		log.setOptType(type);
		log.setGameInstanceRefId(gameInstanceRefId);
		log.setGameInstanceSceneId(gameInstanceSceneId);
		StatService.getInstance().save(log);
	}

	public static void storeStat(Player player, String itemRefId, int itemNum, int type, String storeItemId, int costMoney, int moneyTpe) {
		StatStore log = StatStore.Pool.obtain();
		log.setPlayer(player);
		log.setItemRefId(itemRefId);
		log.setItemNum(itemNum);
		log.setCostMoney(costMoney);
		log.setMoneyType(moneyTpe);
		log.setOptType(type);
		log.setStoreItemId(storeItemId);
		StatService.getInstance().save(log);
	}

	public static void castleWarStat(Player player, byte type, String unionName, byte officialId) {
		StatCastleWar log = StatCastleWar.Pool.obtain();
		log.setPlayer(player);
		log.setOptType(type);
		log.setUnionName(unionName);
		log.setOfficialId(officialId);
		log.setFightPower(player.getFightPower());
		StatService.getInstance().save(log);
	}

	public static void rechargeStat(Player player, int unBindedGold, int payMoney, long time) {
		StatRecharge log = StatRecharge.Pool.obtain();
		log.setPlayer(player);
		log.setPayMoney(payMoney);
		log.setRechargeUnBindedGold(unBindedGold);
		log.setPayTime(time);
		StatService.getInstance().save(log);
	}

	public static void loginStat(Player player, int loginTimes, int loginDays, long lastLoginTimes) {

		StatLogin log = StatLogin.Pool.obtain();
		log.setCurLoginDays(loginDays);
		log.setPlayer(player);
		log.setLastLoginTime(lastLoginTimes);
		log.setLoginTimes(loginTimes);

		StatService.getInstance().save(log);
	}

	public static void vipStat(Player player, byte vipType, byte optType) {

		StatVip log = StatVip.Pool.obtain();
		log.setPlayer(player);
		log.setOptType(optType);
		log.setVipType(vipType);
		StatService.getInstance().save(log);
	}

	public static void fundStat(Player player, byte fundType, byte optType, byte day, byte moneyType, long moneyNum) {
		StatFund log = StatFund.Pool.obtain();
		log.setPlayer(player);
		log.setFundType(fundType);
		log.setOptType(optType);
		log.setGetFundDay(day);
		log.setMoneyType(moneyType);
		log.setMoneyNum(moneyNum);

		StatService.getInstance().save(log);
	}

	public static void DigStat(Player player, byte optType, int count, long time) {
		StatDigs log = StatDigs.Pool.obtain();
		log.setPlayer(player);
		log.setOptType(optType);
		log.setCount(count);
		log.setTime(time);
		StatService.getInstance().save(log);
	}

	public static void VipLotteryStat(Player player, String itemRefId, int count) {
		StatVipLottery log = StatVipLottery.Pool.obtain();
		log.setPlayer(player);
		log.setItemRefId(itemRefId);
		log.setCount(count);
		StatService.getInstance().save(log);
	}

	public static void DigItemStat(Player player, String itemRefId, int number, long time) {
		StatDigsItem log = StatDigsItem.Pool.obtain();
		log.setPlayer(player);
		log.setItemRefId(itemRefId);
		log.setNumber(number);
		log.setDigTime(time);
		StatService.getInstance().save(log);
	}

	public static void GiftCodeStat(Player player, String keyCode, int result) {
		StatGiftCode log = StatGiftCode.Pool.obtain();
		log.setPlayer(player);
		log.setKeyCode(keyCode);
		log.setResult(result);
		StatService.getInstance().save(log);
	}

	public static void achievePointStat(Player player, byte optType, byte sourceType, int number) {
		AchievePointStat log = AchievePointStat.Pool.obtain();
		log.setPlayer(player);
		log.setOptType(optType);
		log.setSourceType(sourceType);
		log.addOrDeleteNumber(number);

		StatService.getInstance().save(log);
	}

	public static void meritPointStat(Player player, byte optType, byte sourceType, int number) {
		MeritPointStat log = MeritPointStat.Pool.obtain();
		log.setPlayer(player);
		log.setOptType(optType);
		log.setSourceType(sourceType);
		log.addOrDeleteNumber(number);

		StatService.getInstance().save(log);
	}

	public static void signStat(Player player, byte optType, byte day, byte count) {
		StatSign log = StatSign.Pool.obtain();
		log.setPlayer(player);
		log.setOptType(optType);
		log.setSignDay(day);
		log.setSignCount(count);

		StatService.getInstance().save(log);
	}

	public static void AuctionStat(Player player, byte optType, String seller, Item oldItem, Item newItem) {
		String itemRefId = newItem.getItemRefId();
		int number = newItem.getNumber();
		byte strengThenLevel = MGPropertyAccesser.getStrengtheningLevel(newItem.getProperty());
		String oldItemId = oldItem.getId();
		String newItemId = newItem.getId();
		String washPd = "";
		if (newItem.isEquip() && EqiupmentComponentProvider.isHadSmithEquipment(newItem)) {
			PropertyDictionary washProperties = EqiupmentComponentProvider.getEquipmentSmithComponent(newItem).getEquipmentSmithMgr().getXiLianEquipmentSmith()
					.getPropertyDictionary();
			washPd = washProperties.toString();

		}
		StatAuction log = StatAuction.Pool.obtain();
		log.setPlayer(player);
		log.setOptType(optType);
		log.setItemRefId(itemRefId);
		log.setNumber(number);
		log.setSellerName(seller);
		log.setStrengThenLevel(strengThenLevel);
		log.setOldItemId(oldItemId);
		log.setNewItemId(newItemId);
		log.setWashPd(washPd);
		StatService.getInstance().save(log);
	}

	public static void AuctionStat(Player player, byte optType, int money, String seller, Item newItem) {
		String itemRefId = newItem.getItemRefId();
		int number = newItem.getNumber();
		byte strengThenLevel = MGPropertyAccesser.getStrengtheningLevel(newItem.getProperty());
		String newItemId = newItem.getId();
		String washPd = "";
		if (newItem.isEquip() && EqiupmentComponentProvider.isHadSmithEquipment(newItem)) {
			PropertyDictionary washProperties = EqiupmentComponentProvider.getEquipmentSmithComponent(newItem).getEquipmentSmithMgr().getXiLianEquipmentSmith()
					.getPropertyDictionary();
			washPd = washProperties.toString();

		}
		StatAuction log = StatAuction.Pool.obtain();
		log.setPlayer(player);
		log.setOptType(optType);
		log.setItemRefId(itemRefId);
		log.setNumber(number);
		log.setMoney(money);
		log.setSellerName(seller);
		log.setStrengThenLevel(strengThenLevel);
		log.setNewItemId(newItemId);
		log.setWashPd(washPd);
		StatService.getInstance().save(log);
	}
}
