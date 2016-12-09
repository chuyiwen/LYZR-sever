package newbee.morningGlory.mmorpg.ladder;

public class Battlefield {
	public static final byte Default_Constance = 20;
	public static final byte Initiative_FighterLower_TargetHigh_Win = 1;
	public static final byte Initiative_FighterLower_TargetHigh_Defeat = 2;
	public static final byte Initiative_FighterHigh_TargetLower_Win = 3;
	public static final byte Initiative_FighterHigh_TargetLower_Defeat = 4;
	public static final byte Passive_FighterHigh_TargetLower_Win = Default_Constance - Initiative_FighterHigh_TargetLower_Defeat;
	public static final byte Passive_FighterHigh_TargetLower_Defeat = Default_Constance - Initiative_FighterHigh_TargetLower_Win;
	public static final byte Passive_FighterLower_TargetHigh_Win = Default_Constance - Initiative_FighterLower_TargetHigh_Defeat;
	public static final byte Passive_FighterLower_TargetHigh_Defeat = Default_Constance - Initiative_FighterLower_TargetHigh_Win;

	public static String getBattlefield(byte type, Object... args) {
		String battlefield = null;
		switch (type) {
		case Initiative_FighterLower_TargetHigh_Win:
			battlefield = String.format("你对%s发起挑战,你胜利了，排名上升到%d", args[0], args[1]);
			break;
		case Initiative_FighterLower_TargetHigh_Defeat:
			battlefield = String.format("你对%s发起挑战,你失败了，排名不变", args[0]);
			break;
		case Initiative_FighterHigh_TargetLower_Win:
			battlefield = String.format("你对%s发起挑战,你胜利了，排名不变", args[0]);
			break;
		case Initiative_FighterHigh_TargetLower_Defeat:
			battlefield = String.format("你对%s发起挑战,你失败了，排名不变", args[0]);
			break;
		case Passive_FighterHigh_TargetLower_Win:
			battlefield = String.format("%s对你发起挑战,你胜利了，排名不变", args[0]);
			break;
		case Passive_FighterHigh_TargetLower_Defeat:
			battlefield = String.format("%s对你发起挑战,你失败了，排名不变", args[0]);
			break;
		case Passive_FighterLower_TargetHigh_Win:
			battlefield = String.format("%s对你发起挑战,你胜利了，排名不变", args[0]);
			break;
		case Passive_FighterLower_TargetHigh_Defeat:
			battlefield = String.format("%s对你发起挑战,你失败了，排名下降到%d", args[0], args[1]);
			break;
		}
		return battlefield;
	}
	
	public static byte reverseBattlefield(final byte battlefield) {
		return (byte) (Default_Constance - battlefield);
	}
}
