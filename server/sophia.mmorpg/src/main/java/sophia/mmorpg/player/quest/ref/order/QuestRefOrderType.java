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
package sophia.mmorpg.player.quest.ref.order;

public final class QuestRefOrderType {
	/** RPG.KillQuest 杀怪任务*/
	public final static byte Kill_Monster_Order_Type = 1;
	
	/** RPG.LootQuest 杀怪掠夺任务*/
	public final static byte Loot_Item_Order_Type = 2;
	
	/** RPG.TalkQuest 对话任务*/
	public final static byte Talk_Order_Type = 3;
	
	/** RPG.CollectQuest 采集任务*/
	public static final byte Collect_Order_Type = 4;
	
	/** RPG.EscortNPCQuest 护送NPC任务*/
	public static final byte Escort_NPC_Order_Type = 5;
	
	/** RPG.EscortItemQuest 护送物品任务*/
	public static final byte Escort_Item_Order_Type = 6;
	
	/** RPG.GiveItemToNPCQuest 提交物品给NPC任务*/
	public static final byte Give_Item_To_NPC_Order_Type = 7;
	
	/** RPG.ExploreWorldQuest 探索任务*/
	public static final byte Explore_World_Order_Type = 8;
	
	
	/** 向林悦胜致敬的 任务类别： Id|OrderType:int|ChineseModeValue:String|ModeValue:int*/
	public static final byte ChineseMode_String_Value_Order_Type = 9;
	
	public static final byte ChineseMode_Int_Value_Order_Type = 10;
	
	
	private QuestRefOrderType() {
		
	}
}
