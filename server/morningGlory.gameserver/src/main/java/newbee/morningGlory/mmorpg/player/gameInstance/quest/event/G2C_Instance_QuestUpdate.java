package newbee.morningGlory.mmorpg.player.gameInstance.quest.event;

import java.util.List;

import newbee.morningGlory.mmorpg.player.gameInstance.quest.MGGameInstanceQuest;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;
import sophia.mmorpg.player.quest.course.ChineseModeStringQuestCourseItem;
import sophia.mmorpg.player.quest.course.KillQuestCourseItem;
import sophia.mmorpg.player.quest.course.QuestCourseItem;
import sophia.mmorpg.player.quest.ref.order.QuestRefOrderType;

public class G2C_Instance_QuestUpdate extends ActionEventBase {
	private MGGameInstanceQuest quest;

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		List<QuestCourseItem> list = quest.getQuestCourse().getQuestCourseItemList();
		putString(buffer, quest.getQuestRef().getId());
		buffer.put((byte) list.size());
		for (QuestCourseItem item : list) {
			if (item.getQuestRefOrderItem().getOrderType() == QuestRefOrderType.Kill_Monster_Order_Type) {
				KillQuestCourseItem kill = (KillQuestCourseItem) item;
				buffer.putInt(kill.getCourseNumber());
				buffer.putInt(-1);
			}
			if (item.getQuestRefOrderItem().getOrderType() == QuestRefOrderType.ChineseMode_String_Value_Order_Type) {
				ChineseModeStringQuestCourseItem chineseOrder = (ChineseModeStringQuestCourseItem) item;
				String target = chineseOrder.getModeValue();
				if (target != "survivalQuest" && target != "timeLimiteQuest") {
					buffer.putInt(chineseOrder.getCourseNumber());
					buffer.putInt((int)chineseOrder.getTimeCount());
				}
			}

		}
		return buffer;
	}

	@Override
	public void unpackBody(IoBuffer buffer) {
		// TODO Auto-generated method stub

	}

	public MGGameInstanceQuest getQuest() {
		return quest;
	}

	public void setQuest(MGGameInstanceQuest quest) {
		this.quest = quest;
	}

}