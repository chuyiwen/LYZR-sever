/**
 * 
 */
package sophia.mmorpg.player.itemBag.event;

import sophia.foundation.communication.core.MessageFactory;
import sophia.mmorpg.event.MMORPGEventDefines;

/**
 * @author Administrator
 * 
 */
public final class ItemBagEventDefines {

	/** 背包容量请求 */
	public static final short C2G_Bag_Capacity = MMORPGEventDefines.Bag_Message_Begin + 1;
	/** 背包容量返回 */
	public static final short G2C_Bag_Capacity = MMORPGEventDefines.Bag_Message_Begin + 2;
	/** 物品列表 */
	public static final short C2G_Item_List = MMORPGEventDefines.Bag_Message_Begin + 3;
	/** 物品列表返回 */
	public static final short G2C_Item_List = MMORPGEventDefines.Bag_Message_Begin + 4;
	/** 查看物品 */
	public static final short C2G_Item_Info = MMORPGEventDefines.Bag_Message_Begin + 6;

	/** 物品使用请求 */
	public static final short C2G_Item_Use = MMORPGEventDefines.Bag_Message_Begin + 9;

	/** 解锁格请求 */
	public static final short C2G_Item_SoltUnLock = MMORPGEventDefines.Bag_Message_Begin + 11;
	/** 解锁格返回 */
	public static final short G2C_Item_SoltUnLock = MMORPGEventDefines.Bag_Message_Begin + 12;

	/** 物品出售请求 */
	public static final short C2G_Item_Sell = MMORPGEventDefines.Bag_Message_Begin + 20;
	/** 批量物品出售请求 */
	public static final short C2G_Item_Batch_Sell = MMORPGEventDefines.Bag_Message_Begin + 21;
	/** 物品增删改返回 */
	public static final short G2C_Item_Update = MMORPGEventDefines.Bag_Message_Begin + 8;
	/** 增加物品 */
	public static final short C2G_Item_Add = MMORPGEventDefines.Bag_Message_Begin + 14;
	/** 修改物品 */
	public static final short C2G_Item_Modify = MMORPGEventDefines.Bag_Message_Begin + 15;
	/** 删除物品 */
	public static final short C2G_Item_Drop = MMORPGEventDefines.Bag_Message_Begin + 13;
	/** 查看物品返回 */
	public static final short G2C_Item_Info = MMORPGEventDefines.Bag_Message_Begin + 56;

	public static void registerActionEvents() {
		MessageFactory.addMessage(C2G_Item_List, C2G_Item_List.class);
		MessageFactory.addMessage(G2C_Item_List, G2C_Item_List.class);
		MessageFactory.addMessage(C2G_Bag_Capacity, C2G_Bag_Capacity.class);
		MessageFactory.addMessage(G2C_Bag_Capacity, G2C_Bag_Capacity.class);
		MessageFactory.addMessage(C2G_Item_Use, C2G_Item_Use.class);
		MessageFactory.addMessage(C2G_Item_SoltUnLock, C2G_Item_SoltUnLock.class);
		MessageFactory.addMessage(G2C_Item_SoltUnLock, G2C_Item_SoltUnLock.class);
		MessageFactory.addMessage(C2G_Item_Sell, C2G_Item_Sell.class);
		MessageFactory.addMessage(C2G_Item_Batch_Sell, C2G_Item_Batch_Sell.class);
		MessageFactory.addMessage(C2G_Item_Add, C2G_Item_Add.class);
		MessageFactory.addMessage(C2G_Item_Modify, C2G_Item_Modify.class);
		MessageFactory.addMessage(C2G_Item_Drop, C2G_Item_Drop.class);
		MessageFactory.addMessage(G2C_Item_Update, G2C_Item_Update.class);
		MessageFactory.addMessage(C2G_Item_Info, C2G_Item_Info.class);
		MessageFactory.addMessage(G2C_Item_Info, G2C_Item_Info.class);
	}

}
