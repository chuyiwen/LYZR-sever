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
package newbee.morningGlory.mmorpg.sceneActivities.event;

import newbee.morningGlory.event.MGEventDefines;
import sophia.foundation.communication.core.MessageFactory;

public final class SceneActivityEventDefines {

	public static final short C2G_MonsterIntrusion_EnterMap = MGEventDefines.Scene_Activity_Message_Begin + 1;
	public static final short C2G_MonsterIntrusion_LeaveMap = MGEventDefines.Scene_Activity_Message_Begin + 2;
	public static final short G2C_MonsterIntrusion_ContinuTime = MGEventDefines.Scene_Activity_Message_Begin + 3;
	public static final short G2C_MonsterIntrusion_EnterMap = MGEventDefines.Scene_Activity_Message_Begin + 4;
	public static final short C2G_MonsterIntrusion_ContinuTime = MGEventDefines.Scene_Activity_Message_Begin + 5;

	public static final short G2C_MonsterIntrusion_LeaveMap = MGEventDefines.Scene_Activity_Message_Begin + 6;
	public static final short G2C_MonsterIntrusion_Font = MGEventDefines.Scene_Activity_Message_Begin + 7;
	public static final short G2C_MonsterIntrusion_BossTimeRefresh = MGEventDefines.Scene_Activity_Message_Begin + 8;
	public static final short C2G_MonsterIntrusion_IsOpen = MGEventDefines.Scene_Activity_Message_Begin + 9;
	public static final short G2C_MonsterIntrusion_IsOpen = MGEventDefines.Scene_Activity_Message_Begin + 10;
	
	public static final short C2G_PayonPalace_EnterMap = MGEventDefines.Scene_Activity_Message_Begin + 31;
	public static final short G2C_PayonPalace_EnterMap = MGEventDefines.Scene_Activity_Message_Begin + 32;
	
	public static final short C2G_PayonPalace_LeaveMap = MGEventDefines.Scene_Activity_Message_Begin + 33;
	public static final short G2C_PayonPalace_LeaveMap = MGEventDefines.Scene_Activity_Message_Begin + 34;
	
	//多倍经验活动预开始
	public static final short G2C_MultiTimesExp_PreStart  = MGEventDefines.Scene_Activity_Message_Begin + 65;
	//多倍经验活动开始
	public static final short G2C_MultiTimesExp_State  	  = MGEventDefines.Scene_Activity_Message_Begin + 66;
	//请求活动剩余时间
	public static final short C2G_MultiTimesExp_RequestTime = MGEventDefines.Scene_Activity_Message_Begin + 67;
	//活动剩余时间返回
	public static final short G2C_MultiTimesExp_RequestTime = MGEventDefines.Scene_Activity_Message_Begin + 68;

	public static final void registerActionEvents() {
		MessageFactory.addMessage(C2G_MonsterIntrusion_EnterMap, C2G_MonsterIntrusion_EnterMap.class);
		MessageFactory.addMessage(C2G_MonsterIntrusion_LeaveMap, C2G_MonsterIntrusion_LeaveMap.class);
		MessageFactory.addMessage(G2C_MonsterIntrusion_ContinuTime, G2C_MonsterIntrusion_ContinuTime.class);
		MessageFactory.addMessage(G2C_MonsterIntrusion_EnterMap, G2C_MonsterIntrusion_EnterMap.class);
		MessageFactory.addMessage(C2G_MonsterIntrusion_ContinuTime, C2G_MonsterIntrusion_ContinuTime.class);

		MessageFactory.addMessage(G2C_MonsterIntrusion_LeaveMap, G2C_MonsterIntrusion_LeaveMap.class);
		MessageFactory.addMessage(G2C_MonsterIntrusion_Font, G2C_MonsterIntrusion_Font.class);
		MessageFactory.addMessage(G2C_MonsterIntrusion_BossTimeRefresh, G2C_MonsterIntrusion_BossTimeRefresh.class);
		MessageFactory.addMessage(C2G_MonsterIntrusion_IsOpen, C2G_MonsterIntrusion_IsOpen.class);
		MessageFactory.addMessage(G2C_MonsterIntrusion_IsOpen, G2C_MonsterIntrusion_IsOpen.class);
		
		//付费地宫
		MessageFactory.addMessage(C2G_PayonPalace_EnterMap, C2G_PayonPalace_EnterMap.class);
		MessageFactory.addMessage(C2G_PayonPalace_LeaveMap, C2G_PayonPalace_LeaveMap.class);
		MessageFactory.addMessage(G2C_PayonPalace_EnterMap, G2C_PayonPalace_EnterMap.class);
		MessageFactory.addMessage(G2C_PayonPalace_LeaveMap, G2C_PayonPalace_LeaveMap.class);
		
		//多倍经验
		MessageFactory.addMessage(G2C_MultiTimesExp_PreStart, G2C_MultiTimesExp_PreStart.class);
		MessageFactory.addMessage(G2C_MultiTimesExp_State, G2C_MultiTimesExp_State.class);
		MessageFactory.addMessage(C2G_MultiTimesExp_RequestTime, C2G_MultiTimesExp_RequestTime.class);
		MessageFactory.addMessage(G2C_MultiTimesExp_RequestTime, G2C_MultiTimesExp_RequestTime.class);
	}
}
