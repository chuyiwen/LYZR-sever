package sophia.mmorpg.Mail.event;

import sophia.foundation.communication.core.MessageFactory;
import sophia.mmorpg.event.MMORPGEventDefines;

/**
 * Copyright (c) 2011 by 游爱.
 * 
 * @author 林悦胜 Create on 2013-12-11 下午5:31:01
 * @version 1.0
 */
public final class MailEventDefines {
	/** 请求邮件列表 */
	public static final short C2G_Mail_List = MMORPGEventDefines.Mail_Message_Begin + 1;
	/** 邮件设置已读 */
	public static final short C2G_Mail_Read = MMORPGEventDefines.Mail_Message_Begin + 2;
	/** 发送邮件 */
	public static final short C2G_Mail_Pickup = MMORPGEventDefines.Mail_Message_Begin + 3;
	
	/** 发送客服邮件 */
	public static final short C2G_GM_Mail_Send = MMORPGEventDefines.Mail_Message_Begin + 5;
	
	public static final short C2G_Mail_Pickup_LeftTime = MMORPGEventDefines.Mail_Message_Begin + 7;
	
	/** 邮件新增事件 */
	public static final short G2C_Mail_Add = MMORPGEventDefines.Mail_Message_Begin + 4;
	/** 请求邮件列表 返回*/
	public static final short G2C_Mail_List = MMORPGEventDefines.Mail_Message_Begin + 51;
	
	public static final short C2G_Mail_Content = MMORPGEventDefines.Mail_Message_Begin + 6;
	
	public static final short G2C_Mail_Content = MMORPGEventDefines.Mail_Message_Begin + 52;
	
	public static final short G2C_Mail_Pickup_LeftTime = MMORPGEventDefines.Mail_Message_Begin + 53;
	
	
	public static void registerActionEvents() {
		MessageFactory.addMessage(C2G_Mail_List, C2G_Mail_List.class);
		MessageFactory.addMessage(C2G_Mail_Read, C2G_Mail_Read.class);
		MessageFactory.addMessage(C2G_Mail_Pickup, C2G_Mail_Pickup.class);
		MessageFactory.addMessage(C2G_GM_Mail_Send, C2G_GM_Mail_Send.class);
		
		MessageFactory.addMessage(G2C_Mail_List, G2C_Mail_List.class);
		MessageFactory.addMessage(G2C_Mail_Add, G2C_Mail_Add.class);
		
		MessageFactory.addMessage(C2G_Mail_Content, C2G_Mail_Content.class);
		MessageFactory.addMessage(G2C_Mail_Content, G2C_Mail_Content.class);
		
		MessageFactory.addMessage(C2G_Mail_Pickup_LeftTime, C2G_Mail_Pickup_LeftTime.class);
		MessageFactory.addMessage(G2C_Mail_Pickup_LeftTime, G2C_Mail_Pickup_LeftTime.class);
	}
}
