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
import sophia.mmorpg.player.quest.ref.order.ChineseModeStringQuestRefOrderItem;
import sophia.mmorpg.player.quest.ref.order.CollectQuestRefOrderItem;
import sophia.mmorpg.player.quest.ref.order.KillMonsterQuestRefOrderItem;
import sophia.mmorpg.player.quest.ref.order.LootItemQuestRefOrderItem;
import sophia.mmorpg.player.quest.ref.order.QuestChineseOrderDefines;
import sophia.mmorpg.player.quest.ref.order.QuestRefOrderType;

public class G2C_QST_QuestUpdate extends ActionEventBase {
	private Quest quest;

	public G2C_QST_QuestUpdate() {
		this.actionEventId = QuestActionEventDefines.G2C_QST_QuestUpdate;
	}

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		putString(buffer, quest.getQuestRef().getId());
		List<QuestCourseItem> list = quest.getQuestCourse().getQuestCourseItemList();
		buffer.put((byte) list.size());
		for (QuestCourseItem item : list) {
			if (item.getQuestRefOrderItem().getOrderType() == QuestRefOrderType.Kill_Monster_Order_Type) {
				KillQuestCourseItem kill = (KillQuestCourseItem) item;
				KillMonsterQuestRefOrderItem questRefOrderItem = (KillMonsterQuestRefOrderItem) item.getQuestRefOrderItem();
				int number = questRefOrderItem.getNumber();
				int courseNumber = number > kill.getCourseNumber()?kill.getCourseNumber():number;
				buffer.putInt(courseNumber);
			} else if (item.getQuestRefOrderItem().getOrderType() == QuestRefOrderType.Loot_Item_Order_Type) {
				LootQuestCourseItem loot = (LootQuestCourseItem) item;
				LootItemQuestRefOrderItem questRefOrderItem = (LootItemQuestRefOrderItem) item.getQuestRefOrderItem();
				int number = questRefOrderItem.getNumber();
				int courseNumber = number > loot.getCourseNumber()?loot.getCourseNumber():number;
				buffer.putInt(courseNumber);
			} else if (item.getQuestRefOrderItem().getOrderType() == QuestRefOrderType.Collect_Order_Type) {
				CollectQuestCourseItem pluck = (CollectQuestCourseItem)item;
				CollectQuestRefOrderItem questRefOrderItem = (CollectQuestRefOrderItem) item.getQuestRefOrderItem();
				int number = questRefOrderItem.getNumber();
				int courseNumber = number > pluck.getCourseNumber()?pluck.getCourseNumber():number;
				buffer.putInt(courseNumber);
			} else if (item.getQuestRefOrderItem().getOrderType() == QuestRefOrderType.ChineseMode_String_Value_Order_Type) {
				ChineseModeStringQuestCourseItem chineseMode = (ChineseModeStringQuestCourseItem)item;
				ChineseModeStringQuestRefOrderItem questRefOrderItem = (ChineseModeStringQuestRefOrderItem) chineseMode.getQuestRefOrderItem();
				if (questRefOrderItem.getOrderEventId() == QuestChineseOrderDefines.PeerageLevelUp) {
					int number = questRefOrderItem.getNumber();
					int courseNumber = chineseMode.getCourseNumber();
					courseNumber = number > courseNumber?courseNumber:number;
					buffer.putInt(courseNumber);
				} else if (questRefOrderItem.getOrderEventId() == QuestChineseOrderDefines.MountLevelUp) {
					int number = questRefOrderItem.getNumber();
					int courseNumber = chineseMode.getCourseNumber();
					courseNumber = number > courseNumber?courseNumber:number;
					buffer.putInt(courseNumber);
				} else if (questRefOrderItem.getOrderEventId() == QuestChineseOrderDefines.WingLevelUp) {
					int number = questRefOrderItem.getNumber();
					int courseNumber = chineseMode.getCourseNumber();
					courseNumber = number > courseNumber?courseNumber:number;
					buffer.putInt(courseNumber);
				} else if (questRefOrderItem.getOrderEventId() == QuestChineseOrderDefines.BuyStoreItem) {
					int number = questRefOrderItem.getNumber();
					int courseNumber = chineseMode.getCourseNumber();
					courseNumber = number > courseNumber?courseNumber:number;
					buffer.putInt(courseNumber);
				} else if (questRefOrderItem.getOrderEventId() == QuestChineseOrderDefines.Ladder) {
					int number = questRefOrderItem.getNumber();
					int courseNumber = chineseMode.getCourseNumber();
					courseNumber = number > courseNumber?courseNumber:number;
					buffer.putInt(courseNumber);
				}
			}
		}
		return buffer;
	}

	@Override
	public void unpackBody(IoBuffer buffer) {
	}

	public Quest getQuest() {
		return quest;
	}

	public void setQuest(Quest quest) {
		this.quest = quest;
	}
}
