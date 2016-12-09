package sophia.mmorpg.stat;

import sophia.mmorpg.player.Player;
import sophia.mmorpg.stat.logs.StatAuth;
import sophia.mmorpg.stat.logs.StatCreateCharacter;
import sophia.mmorpg.stat.logs.StatEquip;
import sophia.mmorpg.stat.logs.StatFenJie;
import sophia.mmorpg.stat.logs.StatItemBag;
import sophia.mmorpg.stat.logs.StatLevel;
import sophia.mmorpg.stat.logs.StatMoney;
import sophia.mmorpg.stat.logs.StatMount;
import sophia.mmorpg.stat.logs.StatOnline;
import sophia.mmorpg.stat.logs.StatPlotQuest;
import sophia.mmorpg.stat.logs.StatQiangHua;
import sophia.mmorpg.stat.logs.StatSkillLevel;
import sophia.mmorpg.stat.logs.StatXiLian;

public class StatFunctions {

	public static void authStat(String identityId, String identityName, int state, int qdCode1, int qdCode2, String ip) {
		StatAuth log = StatAuth.Pool.obtain();
		log.setPlayer(null);
		log.setIdentityName(identityName);
		log.setPlayerId("");
		log.setPlayerName("");
		log.setState(state);
		log.setQdCode1(qdCode1);
		log.setQdCode2(qdCode2);
		log.setIp(ip);

		StatService.getInstance().save(log);
	}

	public static void createCharacterStat(Player player) {
		StatCreateCharacter log = StatCreateCharacter.Pool.obtain();

		log.setPlayer(player);

		StatService.getInstance().save(log);
	}
	
	public static void ItemBagStat(Player player,byte optType,String itemRefId,int number,byte source) {
		StatItemBag log = StatItemBag.Pool.obtain();
		
		log.setPlayer(player);
		log.setItemRefId(itemRefId);
		log.setOptType(optType);
		log.setNumber(number);
		log.setSource(source);
		StatService.getInstance().save(log);
	}
	
	public static void MoneyStat(Player player,byte optType,int crtMoney,int number,int currency,byte source) {
		StatMoney log = StatMoney.Pool.obtain();
		log.setPlayer(player);
		log.setCrtMoney(crtMoney);
		log.setCurrency(currency);
		log.setNumber(number);
		log.setOptType(optType);
		log.setSource(source);
		StatService.getInstance().save(log);
	}
	
	public static void QiangHuaStat(Player player,byte result,int level,String itemRefId) {
		StatQiangHua log = StatQiangHua.Pool.obtain();
		log.setPlayer(player);
		log.setItemRefId(itemRefId);
		log.setQiangHuaLevel(level);
		log.setResult(result);
		StatService.getInstance().save(log);
	}
	public static void XiLianStat(Player player,String itemRefId) {
		StatXiLian log = StatXiLian.Pool.obtain();
		log.setPlayer(player);
		log.setItemRefId(itemRefId);		
		StatService.getInstance().save(log);
	}
	
	public static void FenJieStat(Player player,String itemRefId) {
		StatFenJie log = StatFenJie.Pool.obtain();
		log.setPlayer(player);
		log.setItemRefId(itemRefId);		
		StatService.getInstance().save(log);
	}
	
	public static void plotQuest(Player player, byte type, String questRefId) {
		StatPlotQuest log = StatPlotQuest.Pool.obtain();
		log.setPlayer(player);
		log.setOptType(type);
		log.setQuestRefId(questRefId);
		StatService.getInstance().save(log);
	}
	public static void levelStat(Player player, int level, long exp) {
		StatLevel log = StatLevel.Pool.obtain();
		log.setPlayer(player);
		log.setLevel(level);
		log.setExp(exp);
		StatService.getInstance().save(log);
	}

	public static void mountStat(Player player,String crtRefId,String name,int startLevel,long exp){
		StatMount log = StatMount.Pool.obtain();
		log.setPlayer(player);
		log.setCrtRefId(crtRefId);
		log.setName(name);
		log.setStartLevel(startLevel);
		log.setCrtExp(exp);
		StatService.getInstance().save(log);
	}
	public static void EquipStat(Player player,byte optType,String itemRefId,byte source) {
		StatEquip log = StatEquip.Pool.obtain();	
		log.setPlayer(player);
		log.setItemRefId(itemRefId);
		log.setOptType(optType);
		log.setSource(source);
		StatService.getInstance().save(log);
	}
	
	public static void OnlineStat(Player player,int onlineTime) {
		StatOnline log = StatOnline.Pool.obtain();	
		log.setPlayer(player);
		log.setOnlineTime(onlineTime);
		StatService.getInstance().save(log);
	}
	
	public static void SkillLevelStat(Player player,String skillName,int level,int exp) {
		StatSkillLevel log = StatSkillLevel.Pool.obtain();	
		log.setPlayer(player);
		log.setSkillName(skillName);
		log.setLevel(level);
		log.setExp(exp);
		StatService.getInstance().save(log);
	}
}
