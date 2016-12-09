package newbee.morningGlory.mmorpg.player.dailyQuest.event;

import java.util.ArrayList;
import java.util.List;

import newbee.morningGlory.mmorpg.player.dailyQuest.MGDailyQuest;

import org.apache.log4j.Logger;
import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;
import sophia.mmorpg.player.quest.course.CollectQuestCourseItem;
import sophia.mmorpg.player.quest.course.KillQuestCourseItem;
import sophia.mmorpg.player.quest.course.LootQuestCourseItem;
import sophia.mmorpg.player.quest.course.QuestCourseItem;
import sophia.mmorpg.player.quest.course.TalkQuestCourseItem;
import sophia.mmorpg.player.quest.ref.order.QuestRefOrderItem;
import sophia.mmorpg.player.quest.ref.order.QuestRefOrderType;

public class G2C_QST_DailyQuestAcceptedList extends ActionEventBase {
	
	private static final Logger logger = Logger.getLogger(G2C_QST_DailyQuestAcceptedList.class);
	
	private List<MGDailyQuest> acceptDailyQuestList = new ArrayList<>();

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		int size = acceptDailyQuestList.size();
		if (size>0){
			buffer.put((byte)size);
			for (MGDailyQuest quest : acceptDailyQuestList){
				//任务id
				putString(buffer, quest.getDailyQuestRef().getId());
				//当前环数
				//buffer.putInt(quest.getNowTime());
				buffer.putShort((short) quest.getNowTime());
				//日常任务等级
				buffer.put(quest.getStartLevel());
				//日常任务状态
				buffer.put((byte)quest.getQuestState());
				List<QuestCourseItem> questCourseItemList = quest.getDailyQuestCourse().getQuestCourseItemList();
				if (questCourseItemList != null){
					//日常任务条件长度
					buffer.put((byte)questCourseItemList.size());
					for(QuestCourseItem questCourseItem : questCourseItemList) {
						if (questCourseItem != null) {
							//条件的数量
							buffer.put((byte)quest.getRandomOrderNum());
							//buffer.putShort((short) quest.getRandomOrderNum());
							setNumber(buffer, questCourseItem);
						}
					}
				}
				else{
					buffer.putInt(0);
				}
			}
		}
		return buffer;
	}
	
	private IoBuffer setNumber(IoBuffer buffer, QuestCourseItem questCourseItem){
		QuestRefOrderItem questRefOrderItem = questCourseItem.getQuestRefOrderItem();
		if (questRefOrderItem == null) return buffer;
		int courseNum = 0;
		byte orderType = questRefOrderItem.getOrderType();
		switch(orderType) {
		case QuestRefOrderType.ChineseMode_Int_Value_Order_Type:
			
			break;
		case QuestRefOrderType.ChineseMode_String_Value_Order_Type:
			break;
		case QuestRefOrderType.Collect_Order_Type:
			CollectQuestCourseItem collectQuestCourseItem = (CollectQuestCourseItem) questCourseItem;
			courseNum = collectQuestCourseItem.getCourseNumber();
			if (courseNum < 0) {
				courseNum = 0;
			}
			//buffer.putInt(courseNum);
			buffer.putShort((short)courseNum);
			break;
		case QuestRefOrderType.Escort_Item_Order_Type:
			break;
		case QuestRefOrderType.Escort_NPC_Order_Type:
			break;
		case QuestRefOrderType.Explore_World_Order_Type:
			break;
		case QuestRefOrderType.Give_Item_To_NPC_Order_Type:
			break;
		case QuestRefOrderType.Kill_Monster_Order_Type:
			KillQuestCourseItem killQuestCourseItem = (KillQuestCourseItem) questCourseItem;
			courseNum = killQuestCourseItem.getCourseNumber();
			if (courseNum < 0) {
				courseNum = 0;
			}
			buffer.putShort((short)courseNum);
			break;
		case QuestRefOrderType.Loot_Item_Order_Type:
			LootQuestCourseItem lootQuestCourseItem = (LootQuestCourseItem) questCourseItem;
			buffer.putShort((short)lootQuestCourseItem.getCourseNumber());
			break;
		case QuestRefOrderType.Talk_Order_Type:
			TalkQuestCourseItem talkQuestCourseItem = (TalkQuestCourseItem) questCourseItem;
			break;
		}
		return buffer;
	}
	
	@Override
	public void unpackBody(IoBuffer buffer) {
		// TODO Auto-generated method stub
	}

	public List<MGDailyQuest> getAcceptDailyQuestList() {
		return acceptDailyQuestList;
	}

	public void setAcceptDailyQuestList(List<MGDailyQuest> acceptBleDailyQuestList) {
		this.acceptDailyQuestList = acceptBleDailyQuestList;
	}

}
