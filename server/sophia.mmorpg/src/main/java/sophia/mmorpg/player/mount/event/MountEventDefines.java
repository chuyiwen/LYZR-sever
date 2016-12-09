package sophia.mmorpg.player.mount.event;

import sophia.foundation.communication.core.MessageFactory;
import sophia.mmorpg.event.MMORPGEventDefines;

public class MountEventDefines {

	public static final short C2G_Mount_IsOnMount = MMORPGEventDefines.Mount_Message_Begin + 1;
	public static final short C2G_Mount_List = MMORPGEventDefines.Mount_Message_Begin + 2;
	public static final short C2G_Mount_Feed = MMORPGEventDefines.Mount_Message_Begin + 3;
	public static final short C2G_Mount_Action = MMORPGEventDefines.Mount_Message_Begin + 4;

	public static final short G2C_Mount_IsOnMount = MMORPGEventDefines.Mount_Message_Begin + 51;
	public static final short G2C_Mount_List = MMORPGEventDefines.Mount_Message_Begin + 52;
	public static final short G2C_Mount_Feed = MMORPGEventDefines.Mount_Message_Begin + 53;

	public static void registerActionEvents() {
		MessageFactory.addMessage(C2G_Mount_IsOnMount, C2G_Mount_IsOnMount.class);
		MessageFactory.addMessage(C2G_Mount_List, C2G_Mount_List.class);
		MessageFactory.addMessage(C2G_Mount_Feed, C2G_Mount_Feed.class);
		MessageFactory.addMessage(C2G_Mount_Action, C2G_Mount_Action.class);

		MessageFactory.addMessage(G2C_Mount_IsOnMount, G2C_Mount_IsOnMount.class);
		MessageFactory.addMessage(G2C_Mount_List, G2C_Mount_List.class);
		MessageFactory.addMessage(G2C_Mount_Feed, G2C_Mount_Feed.class);
	}
}
