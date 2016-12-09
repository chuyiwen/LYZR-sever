package newbee.morningGlory.mmorpg.player.gameInstance.quest.event;

import java.util.List;

import newbee.morningGlory.mmorpg.player.gameInstance.quest.MGGameInstanceQuest;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;
import sophia.mmorpg.player.quest.course.ChineseModeStringQuestCourseItem;
import sophia.mmorpg.player.quest.course.CollectQuestCourseItem;
import sophia.mmorpg.player.quest.course.KillQuestCourseItem;
import sophia.mmorpg.player.quest.course.LootQuestCourseItem;
import sophia.mmorpg.player.quest.course.QuestCourseItem;
import sophia.mmorpg.player.quest.ref.order.ChineseModeStringQuestRefOrderItem;
import sophia.mmorpg.player.quest.ref.order.QuestRefOrderItem;
import sophia.mmorpg.player.quest.ref.order.QuestRefOrderType;

public class G2C_Instance_QuestAccepted extends ActionEventBase {
	private String gameInstanceId;
	private List<MGGameInstanceQuest> acceptQuest;

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		byte size = (byte) acceptQuest.size();
		if (size > 0) {
			putString(buffer, gameInstanceId);
			buffer.put(size);
			for (MGGameInstanceQuest quest : acceptQuest) {
				// 任务id
				putString(buffer, quest.getQuestRef().getId());
				// 任务状态
				buffer.put((byte) quest.getQuestState());
				List<QuestCourseItem> questCourseItemList = quest.getQuestCourse().getQuestCourseItemList();
				if (questCourseItemList != null) {
					// 任务条件长度
					buffer.put((byte) questCourseItemList.size());
					for (QuestCourseItem questCourseItem : questCourseItemList) {
						if (questCourseItem != null) {
							// 条件的数量
							setNumber(buffer, questCourseItem);
							
						}
					}
				} else {
					buffer.putInt(0);
				}
			}
		}
		return buffer;
	}

	@Override
	public void unpackBody(IoBuffer buffer) {
		// TODO Auto-generated method stub

	}

	public List<MGGameInstanceQuest> getAcceptQuest() {
		return acceptQuest;
	}

	public void setAcceptQuest(List<MGGameInstanceQuest> acceptQuest) {
		this.acceptQuest = acceptQuest;
	}

	private IoBuffer setNumber(IoBuffer buffer, QuestCourseItem questCourseItem) {
		QuestRefOrderItem questRefOrderItem = questCourseItem.getQuestRefOrderItem();
		if (questRefOrderItem == null)
			return buffer;

		byte orderType = questRefOrderItem.getOrderType();
		buffer.put(orderType);
		switch (orderType) {
		case QuestRefOrderType.ChineseMode_Int_Value_Order_Type:
			break;
		case QuestRefOrderType.ChineseMode_String_Value_Order_Type:
			ChineseModeStringQuestCourseItem chineseMode = (ChineseModeStringQuestCourseItem) questCourseItem;
			ChineseModeStringQuestRefOrderItem order = (ChineseModeStringQuestRefOrderItem)chineseMode.getQuestRefOrderItem();
			buffer.putInt(chineseMode.getCourseNumber());
			long starTime = chineseMode.getStarTime();
			long time = order.getCount() - (System.currentTimeMillis() - starTime) / 1000;
			buffer.putInt((int)time);
			break;
		case QuestRefOrderType.Collect_Order_Type:
			CollectQuestCourseItem collectQuestCourseItem = (CollectQuestCourseItem) questCourseItem;
			buffer.putInt(collectQuestCourseItem.getCourseNumber());
			buffer.putInt(-1);
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
			buffer.putInt(killQuestCourseItem.getCourseNumber());
			buffer.putInt(-1);
			break;
		case QuestRefOrderType.Loot_Item_Order_Type:
			LootQuestCourseItem lootQuestCourseItem = (LootQuestCourseItem) questCourseItem;
			buffer.putInt(lootQuestCourseItem.getCourseNumber());
			buffer.putInt(-1);
			break;
		case QuestRefOrderType.Talk_Order_Type:
			break;
		}
		return buffer;
	}

	public String getGameInstanceId() {
		return gameInstanceId;
	}

	public void setGameInstanceId(String gameInstanceId) {
		this.gameInstanceId = gameInstanceId;
	}
}