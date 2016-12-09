/**
 *   Copyright 2013-2015 Sophia
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package newbee.morningGlory.mmorpg.operatActivities.event;

import newbee.morningGlory.event.MGEventDefines;
import newbee.morningGlory.mmorpg.operatActivities.event.everyRecharge.C2G_OA_EveryRechargeGiftListEvent;
import newbee.morningGlory.mmorpg.operatActivities.event.everyRecharge.C2G_OA_EveryRechargeGiftReceiveEvent;
import newbee.morningGlory.mmorpg.operatActivities.event.everyRecharge.G2C_OA_EveryRechargeGiftListEvent;
import newbee.morningGlory.mmorpg.operatActivities.event.sevenLogin.C2G_OA_SevenLogin_HadReceive;
import newbee.morningGlory.mmorpg.operatActivities.event.sevenLogin.C2G_OA_SevenLogin_HaveReceive;
import newbee.morningGlory.mmorpg.operatActivities.event.sevenLogin.C2G_OA_SevenLogin_ReReceive;
import newbee.morningGlory.mmorpg.operatActivities.event.sevenLogin.C2G_OA_SevenLogin_ReceiveState;
import newbee.morningGlory.mmorpg.operatActivities.event.sevenLogin.G2C_OA_SevenLogin_HaveReceive;
import newbee.morningGlory.mmorpg.operatActivities.event.sevenLogin.G2C_OA_SevenLogin_ReceiveState;
import newbee.morningGlory.mmorpg.operatActivities.event.weekConsume.C2G_OA_WeekTotalConsumeGiftListEvent;
import newbee.morningGlory.mmorpg.operatActivities.event.weekConsume.C2G_OA_WeekTotalConsumeGiftReceiveEvent;
import newbee.morningGlory.mmorpg.operatActivities.event.weekConsume.G2C_OA_WeekTotalConsumeGiftListEvent;
import sophia.foundation.communication.core.MessageFactory;

public final class OperatActivityDefines {

	
	/** 开服七日登录 */
	public static final short C2G_OA_SevenLogin_ReceiveState = MGEventDefines.Activity_Message_Begin + 9;	
	public static final short G2C_OA_SevenLogin_ReceiveState = MGEventDefines.Activity_Message_Begin + 10;	
	public static final short G2C_OA_SevenLogin_HaveReceive = MGEventDefines.Activity_Message_Begin + 11;
	public static final short C2G_OA_SevenLogin_HadReceive = MGEventDefines.Activity_Message_Begin + 12;	
	public static final short C2G_OA_SevenLogin_HaveReceive = MGEventDefines.Activity_Message_Begin + 47;
	public static final short C2G_OA_SevenLogin_ReReceive = MGEventDefines.Activity_Message_Begin + 105;
	/** 首冲礼包 */
	public static final short C2G_OA_FirstRechargeGiftReceive = MGEventDefines.Activity_Message_Begin + 37;
	public static final short C2G_OA_FirstRechargeGiftList = MGEventDefines.Activity_Message_Begin + 38;
	public static final short G2C_OA_FirstRechargeGiftList = MGEventDefines.Activity_Message_Begin + 39;

	/** 充值礼包 */
	public static final short C2G_OA_TotalRechargeGiftListEvent = MGEventDefines.Activity_Message_Begin + 40;
	public static final short G2C_OA_TotalRechargeGiftListEvent = MGEventDefines.Activity_Message_Begin + 41;
	public static final short C2G_OA_TotalRechargeGiftReceiveEvent = MGEventDefines.Activity_Message_Begin + 42;

	/** 每日礼包 */
	public static final short C2G_OA_EveryRechargeGiftListEvent = MGEventDefines.Activity_Message_Begin + 44;
	public static final short G2C_OA_EveryRechargeGiftListEvent = MGEventDefines.Activity_Message_Begin + 45;
	public static final short C2G_OA_EveryRechargeGiftReceiveEvent = MGEventDefines.Activity_Message_Begin + 46;

	
	public static final short G2C_OA_CanReceiveEvent = MGEventDefines.Activity_Message_Begin + 60;
	public static final short G2C_OA_ClosedActivityEvent = MGEventDefines.Activity_Message_Begin + 61;
	public static final short G2C_OA_OpenedActivityEvent = MGEventDefines.Activity_Message_Begin + 62;
	public static final short G2C_OA_OpeningEvent = MGEventDefines.Activity_Message_Begin + 63;
	public static final short C2G_OA_CanReceiveEvent = MGEventDefines.Activity_Message_Begin + 83;
	
	/** 周充值消费礼包 */
	public static final short C2G_OA_WeekTotalConsumeGiftListEvent = MGEventDefines.Activity_Message_Begin + 71;
	public static final short G2C_OA_WeekTotalConsumeGiftListEvent = MGEventDefines.Activity_Message_Begin + 72;
	public static final short C2G_OA_WeekTotalConsumeGiftReceiveEvent = MGEventDefines.Activity_Message_Begin + 73;

	public static void registerActionEvents() {

		MessageFactory.addMessage(C2G_OA_SevenLogin_ReceiveState, C2G_OA_SevenLogin_ReceiveState.class);
		MessageFactory.addMessage(G2C_OA_SevenLogin_ReceiveState, G2C_OA_SevenLogin_ReceiveState.class);
		MessageFactory.addMessage(G2C_OA_SevenLogin_HaveReceive, G2C_OA_SevenLogin_HaveReceive.class);
		MessageFactory.addMessage(C2G_OA_SevenLogin_HadReceive, C2G_OA_SevenLogin_HadReceive.class);
		MessageFactory.addMessage(C2G_OA_SevenLogin_HaveReceive, C2G_OA_SevenLogin_HaveReceive.class);
		MessageFactory.addMessage(C2G_OA_SevenLogin_ReReceive, C2G_OA_SevenLogin_ReReceive.class);
		
		MessageFactory.addMessage(G2C_OA_CanReceiveEvent, G2C_OA_CanReceiveEvent.class);
		MessageFactory.addMessage(C2G_OA_CanReceiveEvent, C2G_OA_CanReceiveEvent.class);
		MessageFactory.addMessage(G2C_OA_ClosedActivityEvent, G2C_OA_ClosedActivityEvent.class);
		MessageFactory.addMessage(G2C_OA_OpenedActivityEvent, G2C_OA_OpenedActivityEvent.class);
		MessageFactory.addMessage(G2C_OA_OpeningEvent, G2C_OA_OpeningEvent.class);

		MessageFactory.addMessage(C2G_OA_FirstRechargeGiftReceive, C2G_OA_FirstRechargeGiftReceive.class);
		MessageFactory.addMessage(C2G_OA_FirstRechargeGiftList, C2G_OA_FirstRechargeGiftList.class);
		MessageFactory.addMessage(G2C_OA_FirstRechargeGiftList, G2C_OA_FirstRechargeGiftList.class);
		
		MessageFactory.addMessage(C2G_OA_TotalRechargeGiftListEvent, C2G_OA_TotalRechargeGiftListEvent.class);
		MessageFactory.addMessage(G2C_OA_TotalRechargeGiftListEvent, G2C_OA_TotalRechargeGiftListEvent.class);
		MessageFactory.addMessage(C2G_OA_TotalRechargeGiftReceiveEvent, C2G_OA_TotalRechargeGiftReceiveEvent.class);
		

		MessageFactory.addMessage(C2G_OA_EveryRechargeGiftListEvent, C2G_OA_EveryRechargeGiftListEvent.class);
		MessageFactory.addMessage(G2C_OA_EveryRechargeGiftListEvent, G2C_OA_EveryRechargeGiftListEvent.class);
		MessageFactory.addMessage(C2G_OA_EveryRechargeGiftReceiveEvent, C2G_OA_EveryRechargeGiftReceiveEvent.class);
		
		MessageFactory.addMessage(C2G_OA_WeekTotalConsumeGiftListEvent, C2G_OA_WeekTotalConsumeGiftListEvent.class);
		MessageFactory.addMessage(G2C_OA_WeekTotalConsumeGiftListEvent, G2C_OA_WeekTotalConsumeGiftListEvent.class);
		MessageFactory.addMessage(C2G_OA_WeekTotalConsumeGiftReceiveEvent, C2G_OA_WeekTotalConsumeGiftReceiveEvent.class);

	}
}
