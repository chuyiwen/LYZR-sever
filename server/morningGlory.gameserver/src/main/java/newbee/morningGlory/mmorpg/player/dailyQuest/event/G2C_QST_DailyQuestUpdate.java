package newbee.morningGlory.mmorpg.player.dailyQuest.event;

import java.util.List;

import newbee.morningGlory.mmorpg.player.dailyQuest.MGDailyQuest;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;
import sophia.mmorpg.player.quest.course.CollectQuestCourseItem;
import sophia.mmorpg.player.quest.course.KillQuestCourseItem;
import sophia.mmorpg.player.quest.course.QuestCourseItem;
import sophia.mmorpg.player.quest.ref.order.CollectQuestRefOrderItem;
import sophia.mmorpg.player.quest.ref.order.KillMonsterQuestRefOrderItem;
import sophia.mmorpg.player.quest.ref.order.QuestRefOrderType;

public class G2C_QST_DailyQuestUpdate extends ActionEventBase {
	private MGDailyQuest quest;

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		putString(buffer, quest.getDailyQuestRef().getId());
		List<QuestCourseItem> list = quest.getDailyQuestCourse().getQuestCourseItemList();
		buffer.put((byte) list.size());
		for (QuestCourseItem item : list) {
			if (item.getQuestRefOrderItem().getOrderType() == QuestRefOrderType.Kill_Monster_Order_Type) {
				buffer.put((byte) quest.getRandomOrderNum());
				KillQuestCourseItem kill = (KillQuestCourseItem) item;
				KillMonsterQuestRefOrderItem questRefOrderItem = (KillMonsterQuestRefOrderItem) item.getQuestRefOrderItem();
				int number = questRefOrderItem.getNumber();
				int courseNumber = number > kill.getCourseNumber() ? kill.getCourseNumber() : number;
				buffer.putInt(courseNumber);
			} else if (item.getQuestRefOrderItem().getOrderType() == QuestRefOrderType.Collect_Order_Type) {
				buffer.put((byte) quest.getRandomOrderNum());
				CollectQuestCourseItem pluck = (CollectQuestCourseItem) item;
				CollectQuestRefOrderItem questRefOrderItem = (CollectQuestRefOrderItem) item.getQuestRefOrderItem();
				int number = questRefOrderItem.getNumber();
				int courseNumber = number > pluck.getCourseNumber() ? pluck.getCourseNumber() : number;
				buffer.putInt(courseNumber);
			}
		}
		return buffer;
	}

	@Override
	public void unpackBody(IoBuffer buffer) {
		// TODO Auto-generated method stub

	}

	public MGDailyQuest getQuest() {
		return quest;
	}

	public void setQuest(MGDailyQuest quest) {
		this.quest = quest;
	}

}
