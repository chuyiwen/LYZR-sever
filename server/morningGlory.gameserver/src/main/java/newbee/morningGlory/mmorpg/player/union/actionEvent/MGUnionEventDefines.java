package newbee.morningGlory.mmorpg.player.union.actionEvent;

import newbee.morningGlory.event.MGEventDefines;
import sophia.foundation.communication.core.MessageFactory;

public class MGUnionEventDefines {
	public static final short C2G_Union_UnionList = MGEventDefines.Union_Message_Begin + 1;
	public static final short G2C_Union_UnionList = MGEventDefines.Union_Message_Begin + 2;

	public static final short C2G_Union_CreateUnion = MGEventDefines.Union_Message_Begin + 3;
	public static final short G2C_Union_CreateUnion = MGEventDefines.Union_Message_Begin + 4;

	public static final short C2G_Union_JoinUnion = MGEventDefines.Union_Message_Begin + 5;
	public static final short G2C_Union_JoinUnion = MGEventDefines.Union_Message_Begin + 6;

	public static final short C2G_Union_CancelJoin = MGEventDefines.Union_Message_Begin + 7;
	public static final short G2C_Union_CancelJoin = MGEventDefines.Union_Message_Begin + 8;

	public static final short C2G_Union_Exit = MGEventDefines.Union_Message_Begin + 9;
	public static final short G2C_Union_Exit = MGEventDefines.Union_Message_Begin + 10;

	public static final short C2G_Union_ApplyList = MGEventDefines.Union_Message_Begin + 11;
	public static final short G2C_Union_ApplyList = MGEventDefines.Union_Message_Begin + 12;

	public static final short C2G_Union_HandleApply = MGEventDefines.Union_Message_Begin + 13;
	public static final short G2C_Union_HandleApply = MGEventDefines.Union_Message_Begin + 14;

	public static final short C2G_Union_KickOutMember = MGEventDefines.Union_Message_Begin + 15;
	public static final short G2C_Union_KickOutMember = MGEventDefines.Union_Message_Begin + 16;

	public static final short C2G_Union_UpgradeOffice = MGEventDefines.Union_Message_Begin + 17;
	public static final short G2C_Union_UpgradeOffice = MGEventDefines.Union_Message_Begin + 18;

	public static final short C2G_Union_EditNotice = MGEventDefines.Union_Message_Begin + 19;
	public static final short G2C_Union_EditNotice = MGEventDefines.Union_Message_Begin + 20;

	public static final short C2G_Union_AutoAgree = MGEventDefines.Union_Message_Begin + 21;

	public static final short C2G_Union_Invite = MGEventDefines.Union_Message_Begin + 22;

	public static final short C2G_Union_ReplyInvite = MGEventDefines.Union_Message_Begin + 23;

	public static final short C2G_Union_Chat = MGEventDefines.Union_Message_Begin + 24;

	public static final short G2C_Union_Chat = MGEventDefines.Union_Message_Begin + 25;
	
	public static final short G2C_Union_Invite = MGEventDefines.Union_Message_Begin + 26;
	
	public static final short G2C_Union_ReplyInvite = MGEventDefines.Union_Message_Begin + 27;
	
	public static final short G2C_Union_Update = MGEventDefines.Union_Message_Begin + 28;

	public static void registerActionEvents() {
		MessageFactory.addMessage(C2G_Union_UnionList, C2G_Union_UnionList.class);
		MessageFactory.addMessage(G2C_Union_UnionList, G2C_Union_UnionList.class);
		MessageFactory.addMessage(C2G_Union_CreateUnion, C2G_Union_CreateUnion.class);
		MessageFactory.addMessage(G2C_Union_CreateUnion, G2C_Union_CreateUnion.class);
		MessageFactory.addMessage(C2G_Union_JoinUnion, C2G_Union_ApplyJoinUnion.class);
		MessageFactory.addMessage(G2C_Union_JoinUnion, G2C_Union_ApplyJoinUnion.class);
		MessageFactory.addMessage(C2G_Union_CancelJoin, C2G_Union_CancelJoin.class);
		MessageFactory.addMessage(G2C_Union_CancelJoin, G2C_Union_CancelJoin.class);
		MessageFactory.addMessage(C2G_Union_Exit, C2G_Union_Exit.class);
		MessageFactory.addMessage(G2C_Union_Exit, G2C_Union_Exit.class);

		MessageFactory.addMessage(C2G_Union_ApplyList, C2G_Union_ApplyList.class);
		MessageFactory.addMessage(G2C_Union_ApplyList, G2C_Union_ApplyList.class);
		MessageFactory.addMessage(C2G_Union_HandleApply, C2G_Union_HandleApply.class);
		MessageFactory.addMessage(G2C_Union_HandleApply, G2C_Union_HandleApply.class);

		MessageFactory.addMessage(C2G_Union_KickOutMember, C2G_Union_KickOutMember.class);
		MessageFactory.addMessage(G2C_Union_KickOutMember, G2C_Union_KickOutMember.class);

		MessageFactory.addMessage(C2G_Union_UpgradeOffice, C2G_Union_UpgradeOffice.class);
		MessageFactory.addMessage(G2C_Union_UpgradeOffice, G2C_Union_UpgradeOffice.class);

		MessageFactory.addMessage(C2G_Union_EditNotice, C2G_Union_EditNotice.class);
		MessageFactory.addMessage(G2C_Union_EditNotice, G2C_Union_EditNotice.class);
		MessageFactory.addMessage(C2G_Union_AutoAgree, C2G_Union_AutoAgree.class);
		MessageFactory.addMessage(C2G_Union_Invite, C2G_Union_Invite.class);
		MessageFactory.addMessage(C2G_Union_ReplyInvite, C2G_Union_ReplyInvite.class);
		MessageFactory.addMessage(G2C_Union_Invite, G2C_Union_Invite.class);
		MessageFactory.addMessage(G2C_Union_ReplyInvite, G2C_Union_ReplyInvite.class);
		
		MessageFactory.addMessage(C2G_Union_Chat, C2G_Union_Chat.class);
		MessageFactory.addMessage(G2C_Union_Chat, G2C_Union_Chat.class);
		
		MessageFactory.addMessage(G2C_Union_Update, G2C_Union_Update.class);
	}

}
