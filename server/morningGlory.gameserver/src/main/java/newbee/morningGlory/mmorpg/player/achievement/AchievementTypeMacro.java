package newbee.morningGlory.mmorpg.player.achievement;

import java.util.HashMap;
import java.util.Map;

import newbee.morningGlory.mmorpg.player.achievement.gameEvent.ExchangeOrLevelUpMedal_GE;
import newbee.morningGlory.mmorpg.player.peerage.gameEvent.MGPeerageLevelUp_GE;
import newbee.morningGlory.mmorpg.player.talisman.gameEvent.HeartLevelUP_GE;
import newbee.morningGlory.mmorpg.player.talisman.gameEvent.TalismanAcquire_GE;
import newbee.morningGlory.mmorpg.player.union.gameEvent.UnionOperateGE;
import newbee.morningGlory.mmorpg.player.wing.actionEvent.MGWingLevelUp_GE;
import newbee.morningGlory.mmorpg.sceneActivities.castleWar.gameEvent.CastleWarEnd_GE;
import newbee.morningGlory.mmorpg.sceneActivities.castleWar.gameEvent.JoinCastleWar_GE;
import newbee.morningGlory.mmorpg.sceneActivities.castleWar.gameEvent.KillInCastleWar_GE;
import sophia.game.GameRoot;
import sophia.mmorpg.equipmentSmith.smith.gameEvent.StrengCount_GE;
import sophia.mmorpg.equipmentSmith.smith.gameEvent.WashCount_GE;
import sophia.mmorpg.monster.gameEvent.MonsterDead_GE;
import sophia.mmorpg.player.equipment.gameEvent.EquipPutOn_GE;
import sophia.mmorpg.player.gameEvent.PlayerFightPowerChange_GE;
import sophia.mmorpg.player.itemBag.gameEvent.FirstTimeAddItem_GE;
import sophia.mmorpg.player.mount.gameEvent.MGMountLevelUp_GE;
import sophia.mmorpg.player.team.gameEvent.TeamGameEvent;

public class AchievementTypeMacro {
	/** 杀怪成就类型 */
	public static final byte KILLMONSTER = 1;
	/** 收集道具成就类型 */
	public static final byte COLLECTITEM = 2;
	/** 组队成就类型 */
	public static final byte TEAM = 3;
	/** 爵位成就类型 */
	public static final byte PEERAGE = 4;
	/** 强化成就类型 */
	public static final byte STRENGTHEN = 5;
	/** 洗练成就类型 */
	public static final byte WASHING = 6;
	/** 坐骑成就类型 */
	public static final byte MOUNT = 7;
	/** 击杀BOSS成就类型 */
	public static final byte KILLBOSS = 8;
	/** 翅膀成就类型 */
	public static final byte WING = 9;
	/** 勋章成就类型 */
	public static final byte MEDAL = 11;
	/** 修炼心法成就类型 */
	public static final byte PRACTICEHEART = 12;
	/** 参加攻城战 */
	public static final byte JOINCASTLEWAR = 13;
	/** 赢得攻城战 */
	public static final byte WINCASTLEWAR = 14;
	/** 连续赢得攻城战 */
	public static final byte CASTLEWARSTREAK = 15;
	/** 攻城战杀敌数 */
	public static final byte CASTLEWARKILLENMEY = 16;
	/** 攻城战杀死祥瑞麒麟次数 */
	public static final byte CASTLEWARKILLBOSS = 17;
	/** 创建公会次数*/
	public static final byte CREATEUNION = 18;
	/** 加入公会次数 */
	public static final byte ADDUNION = 19;
	/** 公会满员次数 */
	public static final byte FULLUNION = 20;
	/** 战力提升 */
	public static final byte UPFIGHTPOWER = 23;
	/** 所有装备强化等级 */
	public static final byte ALLEQUIPSTRENGH = 25;
	/** 心法等级 */
	public static final byte HEARTLEVEL = 26;
	/** 法宝数量 */
	public static final byte TALISMANCOUNT = 27;
	
	
	// ========================================================================================
	
	/** 杀怪 */
	public static final String KILLMONSTER_GE_ID = MonsterDead_GE.class.getSimpleName();
	/** 收集道具 */
	public static final String COLLECTITEM_GE_ID = FirstTimeAddItem_GE.class.getSimpleName();
	/** 组队 */
	public static final String TEAM_GE_ID = TeamGameEvent.class.getSimpleName();
	/** 爵位等级提升 */
	public static final String PEERAGE_GE_ID = MGPeerageLevelUp_GE.class.getSimpleName();
	/** 强化 */
	public static final String STRENGTHEN_GE_ID = StrengCount_GE.class.getSimpleName();
	/** 洗练 */
	public static final String WASHING_GE_ID = WashCount_GE.class.getSimpleName();
	/** 坐骑 */
	public static final String MOUNT_GE_ID = MGMountLevelUp_GE.class.getSimpleName();
	/** 翅膀 */
	public static final String WING_GE_ID = MGWingLevelUp_GE.class.getSimpleName();
	/** 勋章 */
	public static final String MEDAL_GE_ID = ExchangeOrLevelUpMedal_GE.class.getSimpleName();
	/**参加攻城战*/
	public static final String JoinCastleWar_GE_ID = JoinCastleWar_GE.class.getSimpleName();
	/** 攻城战结束*/
	public static final String CastleWarEnd_GE_ID = CastleWarEnd_GE.class.getSimpleName();
	
	/**攻城战击杀*/
	public static final String KillInCastleWar_GE_ID = KillInCastleWar_GE.class.getSimpleName();
	
	/**公会操作*/
	public static final String UnionOperateGE_ID = UnionOperateGE.class.getSimpleName();
	
	/**战力改变*/
	public static final String PlayerFightPowerChange_GE_ID = PlayerFightPowerChange_GE.class.getSimpleName();
	
	/**获取法宝*/
	public static final String TalismanAcquire_GE_ID = TalismanAcquire_GE.class.getSimpleName();
	
	/**心法升级*/
	public static final String HeartLevelUP_GE_ID = HeartLevelUP_GE.class.getSimpleName();
	
	/** 修炼心法 */
	public static final String PRACTICEHEART_GE_ID = "";
	
	/**穿装备*/
	public static final String EquipPutOn_GE_ID = EquipPutOn_GE.class.getSimpleName();

	// ==============================================================================
	/** 杀怪起始成就refId */
	public static final String KILLMONSTERBEGIN = "achieve_1";
	/** 收集道具起始成就refId */
	public static final String COLLECTITEMBEGIN = "achieve_2";
	/** 组队起始成就refId */
	public static final String TEAMBEGIN = "achieve_3";
	/** 爵位起始成就refId */
	public static final String PEERAGEBEGIN = "achieve_4";
	/** 强化起始成就refId */
	public static final String STRENGTHENBEGIN = "achieve_5";
	/** 洗练起始成就refId */
	public static final String WASHINGBEGIN = "achieve_6";
	/** 坐骑起始成就refId */
	public static final String MOUNTBEGIN = "achieve_7";
	/** 击杀BOSS起始成就refId */
	public static final String KILLBOSSBEGIN = "achieve_8";
	/** 翅膀起始成就refId */
	public static final String WINGBEGIN = "achieve_9";
	/** 勋章起始成就refId */
	public static final String MEDALBEGIN = "achieve_11";
	/** 修炼心法起始成就refId */
	public static final String PRACTIECHEARTBEGIN = "achieve_119";
	
	public static final String JOINCASTLEWARBEGIN = "achieve_138";
	
	public static final String WINCASTLEWARBEGIN = "achieve_139";
	
	public static final String CASTLEWARSTREAKBEGIN = "achieve_140";
	
	public static final String CASTLEWARKILLENMEYBEGIN = "achieve_141";
	
	public static final String CASTLEWARKILLBOSSBEGING = "achieve_144";
	
	public static final String ADDUNIONBEGING = "achieve_146";
	
	public static final String CREATEUNIONBEGING = "achieve_145";
	
	public static final String FULLUNIONBEGING = "achieve_147";
	
	public static final String UPFIGHTPOWERBEGIN = "achieve_148";
	
	public static final String ALLEQUIPSTRENGHBEGIN = "achieve_154";
	
	public static final String TALISMANCOUNTBEGING = "achieve_164";
	
	public static final String HEARTLEVELBEGIN = "achieve_158";

	public static final Map<String, String> map = new HashMap<String, String>();

	static {
		map.put("achieve_8", ((MGPlayerAchievementRef) GameRoot.getGameRefObjectManager().getManagedObject("achieve_8")).getTargetRefId());
//		map.put("achieve_77", ((MGPlayerAchievementRef) GameRoot.getGameRefObjectManager().getManagedObject("achieve_77")).getTargetRefId());
//		map.put("achieve_78", ((MGPlayerAchievementRef) GameRoot.getGameRefObjectManager().getManagedObject("achieve_78")).getTargetRefId());
		map.put("achieve_79", ((MGPlayerAchievementRef) GameRoot.getGameRefObjectManager().getManagedObject("achieve_79")).getTargetRefId());
		map.put("achieve_80", ((MGPlayerAchievementRef) GameRoot.getGameRefObjectManager().getManagedObject("achieve_80")).getTargetRefId());
		map.put("achieve_81", ((MGPlayerAchievementRef) GameRoot.getGameRefObjectManager().getManagedObject("achieve_81")).getTargetRefId());
		map.put("achieve_82", ((MGPlayerAchievementRef) GameRoot.getGameRefObjectManager().getManagedObject("achieve_82")).getTargetRefId());
		map.put("achieve_83", ((MGPlayerAchievementRef) GameRoot.getGameRefObjectManager().getManagedObject("achieve_83")).getTargetRefId());
		map.put("achieve_84", ((MGPlayerAchievementRef) GameRoot.getGameRefObjectManager().getManagedObject("achieve_84")).getTargetRefId());
		map.put("achieve_85", ((MGPlayerAchievementRef) GameRoot.getGameRefObjectManager().getManagedObject("achieve_85")).getTargetRefId());
		map.put("achieve_86", ((MGPlayerAchievementRef) GameRoot.getGameRefObjectManager().getManagedObject("achieve_86")).getTargetRefId());
		map.put("achieve_87", ((MGPlayerAchievementRef) GameRoot.getGameRefObjectManager().getManagedObject("achieve_87")).getTargetRefId());
		map.put("achieve_88", ((MGPlayerAchievementRef) GameRoot.getGameRefObjectManager().getManagedObject("achieve_88")).getTargetRefId());
		map.put("achieve_89", ((MGPlayerAchievementRef) GameRoot.getGameRefObjectManager().getManagedObject("achieve_89")).getTargetRefId());
		map.put("achieve_90", ((MGPlayerAchievementRef) GameRoot.getGameRefObjectManager().getManagedObject("achieve_90")).getTargetRefId());
		map.put("achieve_91", ((MGPlayerAchievementRef) GameRoot.getGameRefObjectManager().getManagedObject("achieve_91")).getTargetRefId());
	}

}
