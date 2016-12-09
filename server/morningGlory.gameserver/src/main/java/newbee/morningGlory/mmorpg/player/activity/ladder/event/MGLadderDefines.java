package newbee.morningGlory.mmorpg.player.activity.ladder.event;

import newbee.morningGlory.event.MGEventDefines;
import sophia.foundation.communication.core.MessageFactory;

public class MGLadderDefines {
	public static final short C2G_Arena_ShowArenaView = MGEventDefines.Activity_Message_Begin + 48;
	public static final short G2C_Arena_ShowArenaView = MGEventDefines.Activity_Message_Begin + 49;
	public static final short G2C_Arena_UpadateNotice = MGEventDefines.Activity_Message_Begin + 50;
	public static final short G2C_Arena_UpadateChallengeTarget = MGEventDefines.Activity_Message_Begin + 51;
	public static final short G2C_Arena_UpadateFightRecord = MGEventDefines.Activity_Message_Begin + 52;
	public static final short G2C_Arena_UpadateHeroInfo = MGEventDefines.Activity_Message_Begin + 53;
	public static final short C2G_Arena_ReceiveReward = MGEventDefines.Activity_Message_Begin + 54;
	public static final short G2C_Arena_UpdateReceiveRewardTime = MGEventDefines.Activity_Message_Begin + 55;
	public static final short C2G_Arena_Challenge = MGEventDefines.Activity_Message_Begin + 56;
	public static final short G2C_Arena_UpdateChallengeCDTime = MGEventDefines.Activity_Message_Begin + 57;
	public static final short C2G_Ladder_Select = MGEventDefines.Activity_Message_Begin + 58;
	public static final short G2C_Ladder_Select = MGEventDefines.Activity_Message_Begin + 59;
	public static final short C2G_Arena_Challenge_Award = MGEventDefines.Activity_Message_Begin + 93;
	public static final short G2C_Arena_Challenge_Award = MGEventDefines.Activity_Message_Begin + 94;
	public static final short C2G_Arena_CanReceive = MGEventDefines.Activity_Message_Begin + 97;
	public static final short G2C_Arena_CanReceive = MGEventDefines.Activity_Message_Begin + 98;
	public static final short G2C_Arena_Challenge = MGEventDefines.Activity_Message_Begin + 91;
	public static final short C2G_Arena_ClearCDTime = MGEventDefines.Activity_Message_Begin + 92;// 冲突了

	public static void registerActionEvents() {
		MessageFactory.addMessage(C2G_Arena_ShowArenaView, C2G_Arena_ShowArenaView.class);
		MessageFactory.addMessage(G2C_Arena_ShowArenaView, G2C_Arena_ShowArenaView.class);
		MessageFactory.addMessage(G2C_Arena_UpadateNotice, G2C_Arena_UpadateNotice.class);
		MessageFactory.addMessage(G2C_Arena_UpadateChallengeTarget, G2C_Arena_UpadateChallengeTarget.class);
		MessageFactory.addMessage(G2C_Arena_UpadateFightRecord, G2C_Arena_UpadateFightRecord.class);
		MessageFactory.addMessage(G2C_Arena_UpadateHeroInfo, G2C_Arena_UpadateHeroInfo.class);
		MessageFactory.addMessage(C2G_Arena_ReceiveReward, C2G_Arena_ReceiveReward.class);
		MessageFactory.addMessage(G2C_Arena_UpdateReceiveRewardTime, G2C_Arena_UpdateReceiveRewardTime.class);
		MessageFactory.addMessage(C2G_Arena_Challenge, C2G_Arena_Challenge.class);
		MessageFactory.addMessage(G2C_Arena_UpdateChallengeCDTime, G2C_Arena_UpdateChallengeCDTime.class);
		MessageFactory.addMessage(C2G_Ladder_Select, C2G_Ladder_Select.class);
		MessageFactory.addMessage(G2C_Ladder_Select, G2C_Ladder_Select.class);
		MessageFactory.addMessage(C2G_Arena_ClearCDTime, C2G_Arena_ClearCDTime.class);
		MessageFactory.addMessage(C2G_Arena_CanReceive, C2G_Arena_CanReceive.class);
		MessageFactory.addMessage(G2C_Arena_CanReceive, G2C_Arena_CanReceive.class);
		MessageFactory.addMessage(G2C_Arena_Challenge, G2C_Arena_Challenge.class);
		MessageFactory.addMessage(C2G_Arena_Challenge_Award, C2G_Arena_Challenge_Award.class);
		MessageFactory.addMessage(G2C_Arena_Challenge_Award, G2C_Arena_Challenge_Award.class);

	}
}
