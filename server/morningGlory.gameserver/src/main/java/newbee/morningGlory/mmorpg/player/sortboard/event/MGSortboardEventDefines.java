package newbee.morningGlory.mmorpg.player.sortboard.event;

import newbee.morningGlory.event.MGEventDefines;
import sophia.foundation.communication.core.MessageFactory;

public class MGSortboardEventDefines {
	public static final short C2G_SortBoard_GetSortBoardVersion = MGEventDefines.SortBoard_Message_Begin + 1;

	public static final short G2C_SortBoard_GetSortBoardVersion = MGEventDefines.SortBoard_Message_Begin + 2;

	public static final short C2G_SortBoard_GetSortBoardData = MGEventDefines.SortBoard_Message_Begin + 3;

	public static final short G2C_SortBoard_GetSortBoardData = MGEventDefines.SortBoard_Message_Begin + 4;

	public static final short C2G_SortBoard_GetTopPlayerData = MGEventDefines.SortBoard_Message_Begin + 7;

	public static final short G2C_SortBoard_GetTopPlayerData = MGEventDefines.SortBoard_Message_Begin + 8;

	public static final short C2G_SortBoard_PFS_GetBoardList = MGEventDefines.SortBoard_Message_Begin + 9;

	public static final short G2C_SortBoard_PFS_GetBoardList = MGEventDefines.SortBoard_Message_Begin + 10;

	public static void registerActionEvents() {
		MessageFactory.addMessage(C2G_SortBoard_GetSortBoardVersion, C2G_SortBoard_GetSortBoardVersion.class);
		MessageFactory.addMessage(G2C_SortBoard_GetSortBoardVersion, G2C_SortBoard_GetSortBoardVersion.class);
		MessageFactory.addMessage(C2G_SortBoard_GetSortBoardData, C2G_SortBoard_GetSortBoardData.class);
		MessageFactory.addMessage(G2C_SortBoard_GetSortBoardData, G2C_SortBoard_GetSortBoardData.class);
		MessageFactory.addMessage(C2G_SortBoard_GetTopPlayerData, C2G_SortBoard_GetTopPlayerData.class);
		MessageFactory.addMessage(G2C_SortBoard_GetTopPlayerData, G2C_SortBoard_GetTopPlayerData.class);
		MessageFactory.addMessage(C2G_SortBoard_PFS_GetBoardList, C2G_SortBoard_PFS_GetBoardList.class);
		MessageFactory.addMessage(G2C_SortBoard_PFS_GetBoardList, G2C_SortBoard_PFS_GetBoardList.class);
	}
}
