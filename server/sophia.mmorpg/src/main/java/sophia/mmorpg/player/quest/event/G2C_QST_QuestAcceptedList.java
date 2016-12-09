package sophia.mmorpg.player.quest.event;

import java.util.List;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;
import sophia.mmorpg.player.quest.Quest;
import sophia.mmorpg.player.quest.course.ChineseModeStringQuestCourseItem;
import sophia.mmorpg.player.quest.course.CollectQuestCourseItem;
import sophia.mmorpg.player.quest.course.KillQuestCourseItem;
import sophia.mmorpg.player.quest.course.LootQuestCourseItem;
import sophia.mmorpg.player.quest.course.QuestCourseItem;
import sophia.mmorpg.player.quest.course.TalkQuestCourseItem;
import sophia.mmorpg.player.quest.ref.order.QuestRefOrderItem;
import sophia.mmorpg.player.quest.ref.order.QuestRefOrderType;

public class G2C_QST_QuestAcceptedList extends ActionEventBase {

	private Quest crtQuest;

	public G2C_QST_QuestAcceptedList() {
		this.actionEventId = QuestActionEventDefines.G2C_QST_QuestAcceptedList;
	}

	@Override
	public void unpackBody(IoBuffer buffer) {
	}

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		/**
		 * 当前任务 格式 N|( 任务Id|(N|index|count|))
		 */
		if (crtQuest == null)
			return buffer;
		buffer.put((byte) 1);
		putString(buffer, crtQuest.getQuestRef().getId());
		buffer.put((byte) crtQuest.getQuestState());

		List<QuestCourseItem> questCourseItemList = crtQuest.getQuestCourse().getQuestCourseItemList();
		if (questCourseItemList != null) {

			buffer.put((byte) questCourseItemList.size());
			for (QuestCourseItem questCourseItem : questCourseItemList) {
				if (questCourseItem != null)
					setNumber(buffer, questCourseItem);
			}
		} else {
			buffer.put((byte)0);
		}
		return buffer;
	}

	private IoBuffer setNumber(IoBuffer buffer, QuestCourseItem questCourseItem) {
		QuestRefOrderItem questRefOrderItem = questCourseItem.getQuestRefOrderItem();
		if (questRefOrderItem == null)
			return buffer;
		int courseNum = 0;
		byte orderType = questRefOrderItem.getOrderType();
		switch (orderType) {
		case QuestRefOrderType.Kill_Monster_Order_Type:
			KillQuestCourseItem killQuestCourseItem = (KillQuestCourseItem) questCourseItem;
			courseNum = killQuestCourseItem.getCourseNumber();
			if (courseNum < 0) {
				courseNum = 0;
			}
			buffer.putInt(courseNum);
			break;
		case QuestRefOrderType.Loot_Item_Order_Type:
			LootQuestCourseItem lootQuestCourseItem = (LootQuestCourseItem) questCourseItem;
			buffer.putInt(lootQuestCourseItem.getCourseNumber());
			break;
		case QuestRefOrderType.Talk_Order_Type:
			TalkQuestCourseItem talkQuestCourseItem = (TalkQuestCourseItem) questCourseItem;
			buffer.putInt(1);
			break;
		case QuestRefOrderType.Collect_Order_Type:
			CollectQuestCourseItem collectQuestCourseItem = (CollectQuestCourseItem) questCourseItem;
			courseNum = collectQuestCourseItem.getCourseNumber();
			if (courseNum < 0) {
				courseNum = 0;
			}
			buffer.putInt(courseNum);
			break;
		case QuestRefOrderType.Escort_NPC_Order_Type:
			break;
		case QuestRefOrderType.Escort_Item_Order_Type:
			break;
		case QuestRefOrderType.Give_Item_To_NPC_Order_Type:
			break;
		case QuestRefOrderType.Explore_World_Order_Type:
			break;
		case QuestRefOrderType.ChineseMode_String_Value_Order_Type:
			ChineseModeStringQuestCourseItem chineseMode = (ChineseModeStringQuestCourseItem)questCourseItem;
			courseNum = chineseMode.getCourseNumber();
			if (courseNum < 0) {
				courseNum = 0;
			}
			buffer.putInt(courseNum);
			break;
		case QuestRefOrderType.ChineseMode_Int_Value_Order_Type:
			break;
		}
		return buffer;
	}

	@Override
	public int getPriority() {
		return Immediately_Priority;
	}

	public void setCrtQuest(Quest crtQuest) {
		this.crtQuest = crtQuest;
	}

}
