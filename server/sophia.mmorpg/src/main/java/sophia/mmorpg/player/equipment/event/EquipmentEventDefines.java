package sophia.mmorpg.player.equipment.event;

import sophia.foundation.communication.core.MessageFactory;
import sophia.mmorpg.event.MMORPGEventDefines;

public class EquipmentEventDefines {
	// 装备列表
	public static final short C2G_Equip_List = MMORPGEventDefines.Equip_Message_Begin + 1;

	// 返回装备列表
	public static final short G2C_Equip_List = MMORPGEventDefines.Equip_Message_Begin + 2;

	// 人物装备穿戴
	public static final short C2G_Equip_PutOn = MMORPGEventDefines.Equip_Message_Begin + 3;

	// 卸下装备
	public static final short C2G_Equip_UnLoad = MMORPGEventDefines.Equip_Message_Begin + 4;

	// 更新装备返回
	public static final short G2C_Equip_Update = MMORPGEventDefines.Equip_Message_Begin + 5;
	
	// 更新装备返回
	public static final short G2C_Equip_Info = MMORPGEventDefines.Equip_Message_Begin + 6;

	public static void registerActionEvents() {
		MessageFactory.addMessage(C2G_Equip_List, C2G_Equip_List.class);
		MessageFactory.addMessage(G2C_Equip_List, G2C_Equip_List.class);

		MessageFactory.addMessage(C2G_Equip_PutOn, C2G_Equip_PutOn.class);
		MessageFactory.addMessage(C2G_Equip_UnLoad, C2G_Equip_UnLoad.class);
		MessageFactory.addMessage(G2C_Equip_Update, G2C_Equip_Update.class);
		MessageFactory.addMessage(G2C_Equip_Info, G2C_Equip_Info.class);

	}

}
