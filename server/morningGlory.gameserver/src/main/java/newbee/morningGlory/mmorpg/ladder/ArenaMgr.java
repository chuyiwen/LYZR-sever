package newbee.morningGlory.mmorpg.ladder;

import java.util.Random;

import newbee.morningGlory.mmorpg.player.activity.ladder.CombatRecord;

public class ArenaMgr {
	public static final byte Fighter = 1;
	public static final byte Target = 2;

	public static final byte Athletics_Win = 1;
	public static final byte Athletics_Defeat = 0;

	public static final int Default_HighPower_Coefficient = 97;
	public static final int Default_LowerPower_Coefficient = 85;
	public static final int Default_MaxPower_Coefficient = 100;

	/**
	 * 
	 * 
	 * @param fighter
	 * @param target
	 * @return 判定结果是针对于挑战者的
	 */
	public static byte athletics(int randomFighterFightPower, int randomTargetFightPower) {
		return randomFighterFightPower >= randomTargetFightPower ? Athletics_Win : Athletics_Defeat;
	}

	public static int[] getRandomFightValue(int fightPower, int targetPower) {
		int[] randomValue = new int[2];
		int randomFighterFightPower = 0;
		int randomTargetFightPower = 0;
		if (fightPower >= targetPower) {
			randomFighterFightPower = (int) (randomBetween(Default_HighPower_Coefficient, Default_MaxPower_Coefficient) * fightPower);
			randomTargetFightPower = (int) (randomBetween(Default_LowerPower_Coefficient, Default_MaxPower_Coefficient) * targetPower);
		} else {
			randomFighterFightPower = (int) (randomBetween(Default_LowerPower_Coefficient, Default_MaxPower_Coefficient) * fightPower);
			randomTargetFightPower = (int) (randomBetween(Default_HighPower_Coefficient, Default_MaxPower_Coefficient) * targetPower);
		}
		randomValue[0] = randomFighterFightPower;
		randomValue[1] = randomTargetFightPower;
		return randomValue;
	}

	/**
	 * 
	 * @param fighter
	 *            挑战者
	 * @param target
	 *            被挑战者
	 * @return
	 */
	public static byte getBattlefieldType(int fighterRank, int targetRank, byte fightResult) {

		byte battlefieldType = 0;

		if (fightResult == Athletics_Win) {
			if (fighterRank < targetRank)
				battlefieldType = Battlefield.Initiative_FighterHigh_TargetLower_Win;
			else
				battlefieldType = Battlefield.Initiative_FighterLower_TargetHigh_Win;

		} else if (fightResult == Athletics_Defeat) {
			if (fighterRank < targetRank)
				battlefieldType = Battlefield.Initiative_FighterHigh_TargetLower_Defeat;
			else
				battlefieldType = Battlefield.Initiative_FighterLower_TargetHigh_Defeat;
		}
		return battlefieldType;
	}

	public static CombatRecord getRecord(byte battlefieldType, MGLadderMember fighterMember, MGLadderMember targetMember) {
		String fighterName = fighterMember.getPlayerName();
		String targetName = targetMember.getPlayerName();
		int fighterRank = fighterMember.getRank();
		int targetRank = targetMember.getRank();

		CombatRecord record = null;
		switch (battlefieldType) {
		case Battlefield.Initiative_FighterHigh_TargetLower_Win:
			record = new CombatRecord(CombatRecord.Action_Initiative, targetName, Athletics_Win, 0);
			break;
		case Battlefield.Initiative_FighterLower_TargetHigh_Win:
			int newRank1 = fighterRank < targetRank ? 0 : targetRank;
			record = new CombatRecord(CombatRecord.Action_Initiative, targetName, Athletics_Win, newRank1);
			break;
		case Battlefield.Initiative_FighterHigh_TargetLower_Defeat:
			record = new CombatRecord(CombatRecord.Action_Initiative, targetName, Athletics_Defeat, 0);
			break;
		case Battlefield.Initiative_FighterLower_TargetHigh_Defeat:
			record = new CombatRecord(CombatRecord.Action_Initiative, targetName, Athletics_Defeat, 0);
			break;
		case Battlefield.Passive_FighterHigh_TargetLower_Win:
			record = new CombatRecord(CombatRecord.Action_Passive, fighterName, Athletics_Win, 0);
			break;
		case Battlefield.Passive_FighterHigh_TargetLower_Defeat:
			record = new CombatRecord(CombatRecord.Action_Passive, fighterName, Athletics_Defeat, 0);
			break;
		case Battlefield.Passive_FighterLower_TargetHigh_Win:
			record = new CombatRecord(CombatRecord.Action_Passive, fighterName, Athletics_Win, 0);
			break;
		case Battlefield.Passive_FighterLower_TargetHigh_Defeat:
			int newRank2 = fighterRank < targetRank ? 0 : -fighterRank;
			record = new CombatRecord(CombatRecord.Action_Passive, fighterName, Athletics_Defeat, newRank2);
			break;
		default:
			break;
		}
		return record;
	}

	public static float randomBetween(int lower, int upper) {
		Random r = new Random();
		int result = r.nextInt(upper - lower) + lower;
		return result / 100.0f;
	}
	
	
	public static byte getFightResult(int[] randomFightPower) {     
		byte fightResult = ArenaMgr.athletics(randomFightPower[0], randomFightPower[1]);
		return fightResult;
	}
	
}
