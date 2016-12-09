package newbee.morningGlory.mmorpg.player.store.event;

import sophia.foundation.communication.core.MessageFactory;
import newbee.morningGlory.event.MGEventDefines;

public class StoreEventDefines {
	public static final short C2G_Store_VersonReq = MGEventDefines.Shop_Message_Begin + 1;

	public static final short G2C_Store_VersonResp = MGEventDefines.Shop_Message_Begin + 2;

	public static final short C2G_Store_ItemListReq = MGEventDefines.Shop_Message_Begin + 3;

	public static final short G2C_Store_ItemListResp = MGEventDefines.Shop_Message_Begin + 4;

	public static final short C2G_Store_LimitItemReq = MGEventDefines.Shop_Message_Begin + 5;

	public static final short G2C_Store_LimitItemResp = MGEventDefines.Shop_Message_Begin + 6;

	public static final short C2G_Store_BuyItemReq = MGEventDefines.Shop_Message_Begin + 7;

	public static final short G2C_Store_BuyItemResp = MGEventDefines.Shop_Message_Begin + 8;

	public static final short C2G_Discount_GetShopList = MGEventDefines.Shop_Message_Begin + 9;

	public static final short G2C_Discount_GetShopList = MGEventDefines.Shop_Message_Begin + 10;

	public static final short G2C_Discount_BeginOrEndNotify = MGEventDefines.Shop_Message_Begin + 11;
	
	public static final short C2G_ExchangeCode = MGEventDefines.Shop_Message_Begin + 12;

	public static final short G2C_ExchangeCode = MGEventDefines.Shop_Message_Begin + 13;

	public static void registerActionEvents() {
		MessageFactory.addMessage(C2G_Store_VersonReq, C2G_Store_VersonReq.class);
		MessageFactory.addMessage(G2C_Store_VersonResp, G2C_Store_VersonResp.class);
		MessageFactory.addMessage(C2G_Store_ItemListReq, C2G_Store_ItemListReq.class);
		MessageFactory.addMessage(G2C_Store_ItemListResp, G2C_Store_ItemListResp.class);
		MessageFactory.addMessage(C2G_Store_LimitItemReq, C2G_Store_LimitItemReq.class);
		MessageFactory.addMessage(G2C_Store_LimitItemResp, G2C_Store_LimitItemResp.class);
		MessageFactory.addMessage(C2G_Store_BuyItemReq, C2G_Store_BuyItemReq.class);
		MessageFactory.addMessage(G2C_Store_BuyItemResp, G2C_Store_BuyItemResp.class);
		MessageFactory.addMessage(C2G_Discount_GetShopList, C2G_Discount_GetShopList.class);
		MessageFactory.addMessage(G2C_Discount_GetShopList, G2C_Discount_GetShopList.class);
		MessageFactory.addMessage(G2C_Discount_BeginOrEndNotify, G2C_Discount_BeginOrEndNotify.class);	
		MessageFactory.addMessage(C2G_ExchangeCode, C2G_ExchangeCode.class);
		MessageFactory.addMessage(G2C_ExchangeCode, G2C_ExchangeCode.class);
	}
}
