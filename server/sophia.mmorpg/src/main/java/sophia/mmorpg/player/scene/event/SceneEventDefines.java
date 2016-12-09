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
package sophia.mmorpg.player.scene.event;

import sophia.foundation.communication.core.MessageFactory;
import sophia.mmorpg.event.MMORPGEventDefines;

public final class SceneEventDefines {

	/** 时间同步请求 */
	public static final short C2G_Scene_Sync_Time = MMORPGEventDefines.Scene_Message_Begin + 1;
	/** 时间同步返回 */
	public static final short G2C_Scene_Sync_Time = MMORPGEventDefines.Scene_Message_Begin + 2;
	/** 开始移动 */
	public static final short C2G_Scene_Start_Move = MMORPGEventDefines.Scene_Message_Begin + 3;
	/** 广播开始移动 */
	public static final short G2C_Scene_Start_Move = MMORPGEventDefines.Scene_Message_Begin + 4;
	/** 结束移动 */
	public static final short C2G_Scene_Stop_Move = MMORPGEventDefines.Scene_Message_Begin + 5;
	/** 广播结束移动 */
	public static final short G2C_Scene_Stop_Move = MMORPGEventDefines.Scene_Message_Begin + 6;
	
	public static final short C2G_Scene_Move      = MMORPGEventDefines.Scene_Message_Begin + 7;

	/** 查询精灵是否在当前场景 */
	public static final short C2G_Scene_FindSprite = MMORPGEventDefines.Scene_Message_Begin + 21;
	/** 返回精灵是否在当前场景 */
	public static final short G2C_Scene_FindSprite = MMORPGEventDefines.Scene_Message_Begin + 22;
	
	/** 场景切换 */
	public static final short C2G_Scene_Switch = MMORPGEventDefines.Scene_Message_Begin + 51;
	/** 通知场景切换，客户端收到消息后切换场景 */
	public static final short G2C_Scene_Switch = MMORPGEventDefines.Scene_Message_Begin + 52;
	/** 客户端场景加载完成 */
	public static final short C2G_Scene_Ready = MMORPGEventDefines.Scene_Message_Begin + 53;
	/** 客户端场景加载完成确认 */
	public static final short G2C_Scene_Ready = MMORPGEventDefines.Scene_Message_Begin + 54;
	/** 传送 */
	public static final short C2G_Scene_Transfer = MMORPGEventDefines.Scene_Message_Begin + 55;
	/** 场景AOI */
	public static final short G2C_Scene_AOI = MMORPGEventDefines.Scene_Message_Begin + 72;
	/** NPC传送请求接口 */
	public static final short C2G_Npc_Transfer = MMORPGEventDefines.Scene_Message_Begin + 73;
	/** 传送石*/
	public static final short C2G_Use_TransferStone = MMORPGEventDefines.Scene_Message_Begin + 74;
	/** 通知玩家掉落信息 */
	public static final short G2C_Scene_LootInfo = MMORPGEventDefines.Scene_Message_Begin + 80;
	/** 拾取 */
	public static final short C2G_Scene_PickUp = MMORPGEventDefines.Scene_Message_Begin + 81;
	/** 采集 */
	public static final short C2G_Scene_StartoPluck = MMORPGEventDefines.Scene_Message_Begin + 85;
	/** 采集打断 */
	public static final short G2C_Scene_InterruptPluck = MMORPGEventDefines.Scene_Message_Begin + 88;
	/** 状态改变  */
	public static final short G2C_Scene_State_Change = MMORPGEventDefines.Scene_Message_Begin + 90;
	/** 战斗力不足  */
	public static final short G2C_Scene_FightPower_NotEnought = MMORPGEventDefines.Scene_Message_Begin + 99;
	
	
	public static void registerActionEvents() {
		MessageFactory.addMessage(C2G_Scene_Sync_Time, C2G_Scene_Sync_Time.class);
		MessageFactory.addMessage(G2C_Scene_Sync_Time, G2C_Scene_Sync_Time.class);
		MessageFactory.addMessage(C2G_Scene_Start_Move, C2G_Scene_Start_Move.class);
		MessageFactory.addMessage(G2C_Scene_Start_Move, G2C_Scene_Start_Move.class);
		MessageFactory.addMessage(C2G_Scene_Stop_Move, C2G_Scene_Stop_Move.class);
		MessageFactory.addMessage(G2C_Scene_Stop_Move, G2C_Scene_Stop_Move.class);
		MessageFactory.addMessage(C2G_Scene_Switch, C2G_Scene_Switch.class);
		MessageFactory.addMessage(G2C_Scene_Switch, G2C_Scene_Switch.class);
		MessageFactory.addMessage(C2G_Scene_Ready, C2G_Scene_Ready.class);
		MessageFactory.addMessage(C2G_Scene_Transfer, C2G_Scene_Transfer.class);
		MessageFactory.addMessage(G2C_Scene_AOI, G2C_Scene_AOI.class);
		MessageFactory.addMessage(C2G_Npc_Transfer, C2G_Npc_Transfer.class);
		MessageFactory.addMessage(C2G_Scene_PickUp, C2G_Scene_PickUp.class);
		MessageFactory.addMessage(C2G_Scene_StartoPluck, C2G_Scene_StartoPluck.class);
		MessageFactory.addMessage(G2C_Scene_InterruptPluck, G2C_Scene_InterruptPluck.class);
		MessageFactory.addMessage(G2C_Scene_State_Change, G2C_Scene_State_Change.class);
		MessageFactory.addMessage(G2C_Scene_LootInfo, G2C_Scene_LootInfo.class);
		MessageFactory.addMessage(G2C_Scene_Ready, G2C_Scene_Ready.class);
		MessageFactory.addMessage(C2G_Use_TransferStone, C2G_Use_TransferStone.class);
		MessageFactory.addMessage(C2G_Scene_Move, C2G_Scene_Move.class);
		MessageFactory.addMessage(G2C_Scene_FightPower_NotEnought, G2C_Scene_FightPower_NotEnought.class);
		MessageFactory.addMessage(C2G_Scene_FindSprite, C2G_Scene_FindSprite.class);
		MessageFactory.addMessage(G2C_Scene_FindSprite, G2C_Scene_FindSprite.class);
	}
}
