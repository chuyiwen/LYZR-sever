package newbee.morningGlory.mmorpg.player.dailyQuest.persistence;

import java.util.List;

import newbee.morningGlory.mmorpg.player.dailyQuest.MGDailyQuest;

import org.apache.log4j.Logger;

import sophia.foundation.util.ByteArrayReadWriteBuffer;
import sophia.mmorpg.GameObjectFactory;
import sophia.mmorpg.player.persistence.AbstractPersistenceObjectReadWrite;
import sophia.mmorpg.player.quest.QuestState;
import sophia.mmorpg.player.quest.course.CollectQuestCourseItem;
import sophia.mmorpg.player.quest.course.KillQuestCourseItem;
import sophia.mmorpg.player.quest.course.LootQuestCourseItem;
import sophia.mmorpg.player.quest.course.QuestCourseItem;
import sophia.mmorpg.player.quest.course.TalkQuestCourseItem;
import sophia.mmorpg.player.quest.ref.order.QuestRefOrderItem;
import sophia.mmorpg.player.quest.ref.order.QuestRefOrderType;

public class DailyQuestPersistenceReadWrite extends AbstractPersistenceObjectReadWrite<MGDailyQuest> {

	private static final Logger logger = Logger.getLogger(DailyQuestPersistenceReadWrite.class.getName());

	private int current_version = Default_Write_Version + 2;

	public DailyQuestPersistenceReadWrite() {
	}

	@Override
	public byte[] toBytes(MGDailyQuest persistenceObject) {
		if (current_version == Default_Write_Version) {
			return toBytesVer10000(persistenceObject);
		} else if (current_version == Default_Write_Version + 1) {
			return toBytesVer10001(persistenceObject);
		}  else if (current_version == Default_Write_Version + 2) {
			return toBytesVer10002(persistenceObject);
		} else {
			logger.error("写入版本没有对应写入方法");
			return null;
		}
	}

	@Override
	public MGDailyQuest fromBytes(byte[] persistenceBytes) {
		ByteArrayReadWriteBuffer buffer = new ByteArrayReadWriteBuffer(persistenceBytes);
		int Vers = buffer.readInt();
		if (Vers == Default_Write_Version) {
			return fromBytesVers10000(buffer);
		} else if (Vers == Default_Write_Version + 1) {
			return fromBytesVers10001(buffer);
		} else if (Vers == Default_Write_Version + 2) {
			return fromBytesVers10002(buffer);
		}
		return null;
	}

	@Override
	public String toJsonString(MGDailyQuest persistenceObject) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MGDailyQuest fromJsonString(String persistenceJsonString) {
		// TODO Auto-generated method stub
		return null;
	}

	private byte[] toBytesVer10000(MGDailyQuest quest) {
		ByteArrayReadWriteBuffer buffer = new ByteArrayReadWriteBuffer();

		// 我们先写个版本号，读的时候，好用对应版本的方法。
		buffer.writeInt(Default_Write_Version);
		// 是那个任务refId
		String questRefId = quest.getDailyQuestRef().getId();
		buffer.writeString(questRefId);

		// 任务状态
		int questState = quest.getQuestState();
		buffer.writeInt(questState);

		int questNowRing = quest.getNowTime();
		buffer.writeInt(questNowRing);

		int startLevel = quest.getStartLevel();
		buffer.writeInt(startLevel);

		int randomOrderNum = quest.getRandomOrderNum();
		buffer.writeInt(randomOrderNum);

		// 如果状态是： QuestState.AcceptedQuestState， 我们需要保持进度数据
		if (questState == QuestState.AcceptedQuestState) {
			List<QuestCourseItem> questCourseItemList = quest.getDailyQuestCourse().getQuestCourseItemList();
			int size = questCourseItemList.size();
			buffer.writeInt(size);
			for (QuestCourseItem questCourseItem : questCourseItemList) {
				QuestRefOrderItem questRefOrderItem = questCourseItem.getQuestRefOrderItem();
				byte orderType = questRefOrderItem.getOrderType();
				buffer.writeByte(orderType);
				switch (orderType) {
				case QuestRefOrderType.ChineseMode_Int_Value_Order_Type:
					break;
				case QuestRefOrderType.ChineseMode_String_Value_Order_Type:
					break;
				case QuestRefOrderType.Collect_Order_Type:
					CollectQuestCourseItem collectQuestCourseItem = (CollectQuestCourseItem) questCourseItem;
					buffer.writeShort(collectQuestCourseItem.getCourseNumber());
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
					buffer.writeShort(killQuestCourseItem.getCourseNumber());
					break;
				case QuestRefOrderType.Loot_Item_Order_Type:
					LootQuestCourseItem lootQuestCourseItem = (LootQuestCourseItem) questCourseItem;
					buffer.writeShort(lootQuestCourseItem.getCourseNumber());
					break;
				case QuestRefOrderType.Talk_Order_Type:
					TalkQuestCourseItem talkQuestCourseItem = (TalkQuestCourseItem) questCourseItem;
					buffer.writeBoolean(talkQuestCourseItem.wasCompleted());
					break;
				}
			}
		}

		byte[] data = buffer.getData();
		return data;
	}
	
	private MGDailyQuest fromBytesVers10000(ByteArrayReadWriteBuffer buffer) {
		String questRefId = buffer.readString();
		MGDailyQuest crtQuest = GameObjectFactory.get(MGDailyQuest.class, questRefId);

		int questState = buffer.readInt();
		crtQuest.setQuestState(questState);

		int questNowRing = buffer.readInt();
		crtQuest.setNowTime(questNowRing);

		int startLevel = buffer.readInt();
		crtQuest.setStartLevel((byte) startLevel);

		int randomOrderNum = buffer.readInt();
		crtQuest.setRandomOrderNum(randomOrderNum);

		if (questState == QuestState.AcceptedQuestState) {
			int i = buffer.readInt();
			if (i > 0) {
				byte i2 = buffer.readByte();
				short i3 = 0;
				if (i2 == QuestRefOrderType.Collect_Order_Type || i2 == QuestRefOrderType.Kill_Monster_Order_Type) {
					i3 = buffer.readShort();
				}
				crtQuest.readQuestCourseItem(i, i2, i3);
			}
		}
		return crtQuest;
	}
	
	private byte[] toBytesVer10001(MGDailyQuest quest) {
		ByteArrayReadWriteBuffer buffer = new ByteArrayReadWriteBuffer();

		// 我们先写个版本号，读的时候，好用对应版本的方法。
		buffer.writeInt(Default_Write_Version + 1);
		// 是那个任务refId
		String questRefId = quest.getDailyQuestRef().getId();
		buffer.writeString(questRefId);

		// 任务状态
		int questState = quest.getQuestState();
		buffer.writeInt(questState);

		int questNowRing = quest.getNowTime();
		buffer.writeInt(questNowRing);

		int startLevel = quest.getStartLevel();
		buffer.writeInt(startLevel);

		int randomOrderNum = quest.getRandomOrderNum();
		buffer.writeInt(randomOrderNum);

		// 如果状态是： QuestState.AcceptedQuestState， 我们需要保持进度数据
		if (questState == QuestState.AcceptedQuestState) {
			List<QuestCourseItem> questCourseItemList = quest.getDailyQuestCourse().getQuestCourseItemList();
			int size = questCourseItemList.size();
			buffer.writeInt(size);
			for (QuestCourseItem questCourseItem : questCourseItemList) {
				QuestRefOrderItem questRefOrderItem = questCourseItem.getQuestRefOrderItem();
				byte orderType = questRefOrderItem.getOrderType();
				buffer.writeByte(orderType);
				switch (orderType) {
				case QuestRefOrderType.Collect_Order_Type:
					CollectQuestCourseItem collectQuestCourseItem = (CollectQuestCourseItem) questCourseItem;
					buffer.writeShort(collectQuestCourseItem.getCourseNumber());
					break;
				case QuestRefOrderType.Kill_Monster_Order_Type:
					KillQuestCourseItem killQuestCourseItem = (KillQuestCourseItem) questCourseItem;
					buffer.writeShort(killQuestCourseItem.getCourseNumber());
					break;
				}
			}
		}

		byte[] data = buffer.getData();
		return data;
	}

	private MGDailyQuest fromBytesVers10001(ByteArrayReadWriteBuffer buffer) {
		String questRefId = buffer.readString();
		MGDailyQuest crtQuest = GameObjectFactory.get(MGDailyQuest.class, questRefId);

		int questState = buffer.readInt();
		crtQuest.setQuestState(questState);

		int questNowRing = buffer.readInt();
		crtQuest.setNowTime(questNowRing);

		int startLevel = buffer.readInt();
		crtQuest.setStartLevel((byte) startLevel);

		int randomOrderNum = buffer.readInt();
		crtQuest.setRandomOrderNum(randomOrderNum);

		if (questState == QuestState.AcceptedQuestState) {
			int i = buffer.readInt();
			if (i > 0) {
				byte i2 = buffer.readByte();
				short i3 = 0;
				if (i2 == QuestRefOrderType.Collect_Order_Type || i2 == QuestRefOrderType.Kill_Monster_Order_Type) {
					i3 = buffer.readShort();
					crtQuest.readQuestCourseItem(i, i2, i3);
				}
			}
		}
		return crtQuest;
	}
	
	private byte[] toBytesVer10002(MGDailyQuest quest) {
		ByteArrayReadWriteBuffer buffer = new ByteArrayReadWriteBuffer();

		// 我们先写个版本号，读的时候，好用对应版本的方法。
		buffer.writeInt(Default_Write_Version + 2);
		// 是那个任务refId
		String questRefId = quest.getDailyQuestRef().getId();
		buffer.writeString(questRefId);
		long lastRefreshTime = quest.getLastRefreshTime();
		buffer.writeLong(lastRefreshTime);

		// 任务状态
		int questState = quest.getQuestState();
		buffer.writeInt(questState);

		int questNowRing = quest.getNowTime();
		buffer.writeInt(questNowRing);

		int startLevel = quest.getStartLevel();
		buffer.writeInt(startLevel);

		int randomOrderNum = quest.getRandomOrderNum();
		buffer.writeInt(randomOrderNum);

		// 如果状态是： QuestState.AcceptedQuestState， 我们需要保持进度数据
		if (questState == QuestState.AcceptedQuestState) {
			List<QuestCourseItem> questCourseItemList = quest.getDailyQuestCourse().getQuestCourseItemList();
			int size = questCourseItemList.size();
			buffer.writeInt(size);
			for (QuestCourseItem questCourseItem : questCourseItemList) {
				QuestRefOrderItem questRefOrderItem = questCourseItem.getQuestRefOrderItem();
				byte orderType = questRefOrderItem.getOrderType();
				buffer.writeByte(orderType);
				switch (orderType) {
				case QuestRefOrderType.Collect_Order_Type:
					CollectQuestCourseItem collectQuestCourseItem = (CollectQuestCourseItem) questCourseItem;
					buffer.writeShort(collectQuestCourseItem.getCourseNumber());
					break;
				case QuestRefOrderType.Kill_Monster_Order_Type:
					KillQuestCourseItem killQuestCourseItem = (KillQuestCourseItem) questCourseItem;
					buffer.writeShort(killQuestCourseItem.getCourseNumber());
					break;
				}
			}
		}

		byte[] data = buffer.getData();
		return data;
	}

	private MGDailyQuest fromBytesVers10002(ByteArrayReadWriteBuffer buffer) {
		String questRefId = buffer.readString();
		MGDailyQuest crtQuest = GameObjectFactory.get(MGDailyQuest.class, questRefId);
		long lastRefreshTime = buffer.readLong();
		crtQuest.setLastRefreshTime(lastRefreshTime);

		int questState = buffer.readInt();
		crtQuest.setQuestState(questState);

		int questNowRing = buffer.readInt();
		crtQuest.setNowTime(questNowRing);

		int startLevel = buffer.readInt();
		crtQuest.setStartLevel((byte) startLevel);

		int randomOrderNum = buffer.readInt();
		crtQuest.setRandomOrderNum(randomOrderNum);

		if (questState == QuestState.AcceptedQuestState) {
			int i = buffer.readInt();
			if (i > 0) {
				byte i2 = buffer.readByte();
				short i3 = 0;
				if (i2 == QuestRefOrderType.Collect_Order_Type || i2 == QuestRefOrderType.Kill_Monster_Order_Type) {
					i3 = buffer.readShort();
					crtQuest.readQuestCourseItem(i, i2, i3);
				}
			}
		}
		return crtQuest;
	}
}
