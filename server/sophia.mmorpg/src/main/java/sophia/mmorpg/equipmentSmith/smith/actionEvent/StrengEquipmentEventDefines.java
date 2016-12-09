package sophia.mmorpg.equipmentSmith.smith.actionEvent;

import sophia.foundation.communication.core.MessageFactory;
import sophia.mmorpg.event.MMORPGEventDefines;

public class StrengEquipmentEventDefines {
	// 背包装备强化
	public static final short C2G_BAG_Streng = MMORPGEventDefines.Forge_Message_Begin + 1;

	/** 装备列表装备强化 */
	public static final short C2G_Equip_Streng = MMORPGEventDefines.Forge_Message_Begin + 2;

	// 背包强化卷强化
	public static final short C2G_BAG_StrengScroll = MMORPGEventDefines.Forge_Message_Begin + 3;
	// 装备列表强化卷强化
	public static final short C2G_Equip_StrengScroll = MMORPGEventDefines.Forge_Message_Begin + 4;
	// 返回强化结果
	public static final short G2C_Streng_Ret = MMORPGEventDefines.Forge_Message_Begin + 5;

	public static final short C2G_Bag_Wash = MMORPGEventDefines.Forge_Message_Begin + 6;

	public static final short C2G_Body_Wash = MMORPGEventDefines.Forge_Message_Begin + 7;

	public static final short C2G_Bag_Decompose = MMORPGEventDefines.Forge_Message_Begin + 8;

	public static final short G2C_Bag_Decompose = MMORPGEventDefines.Forge_Message_Begin + 9;

	public static final short G2C_Force_Open = MMORPGEventDefines.Forge_Message_Begin + 10;

	public static void registerActionEvents() {
		MessageFactory.addMessage(C2G_BAG_Streng, C2G_BAG_Streng.class);
		MessageFactory.addMessage(C2G_Equip_Streng, C2G_Equip_Streng.class);

		MessageFactory.addMessage(C2G_BAG_StrengScroll, C2G_BAG_StrengScroll.class);
		MessageFactory.addMessage(C2G_Equip_StrengScroll, C2G_Equip_StrengScroll.class);

		MessageFactory.addMessage(G2C_Streng_Ret, G2C_Streng_Ret.class);

		MessageFactory.addMessage(C2G_Bag_Wash, C2G_Bag_Wash.class);
		MessageFactory.addMessage(C2G_Body_Wash, C2G_Body_Wash.class);

		MessageFactory.addMessage(C2G_Bag_Decompose, C2G_Bag_Decompose.class);
		MessageFactory.addMessage(G2C_Bag_Decompose, G2C_Bag_Decompose.class);
		
		MessageFactory.addMessage(G2C_Force_Open, G2C_Force_Open.class);

	}

}
