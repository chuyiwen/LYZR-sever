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
package sophia.mmorpg.player.quest.persistence;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import sophia.foundation.util.ByteArrayReadWriteBuffer;
import sophia.mmorpg.GameObjectFactory;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.itemBag.ItemFacade;
import sophia.mmorpg.player.persistence.AbstractPersistenceObjectReadWrite;
import sophia.mmorpg.player.quest.PlayerQuestComponent;
import sophia.mmorpg.player.quest.Quest;
import sophia.mmorpg.player.quest.QuestState;
import sophia.mmorpg.player.quest.course.ChineseModeStringQuestCourseItem;
import sophia.mmorpg.player.quest.course.CollectQuestCourseItem;
import sophia.mmorpg.player.quest.course.KillQuestCourseItem;
import sophia.mmorpg.player.quest.course.LootQuestCourseItem;
import sophia.mmorpg.player.quest.course.QuestCourseItem;
import sophia.mmorpg.player.quest.ref.order.ChineseModeStringQuestRefOrderItem;
import sophia.mmorpg.player.quest.ref.order.QuestChineseOrderDefines;
import sophia.mmorpg.player.quest.ref.order.QuestRefOrderItem;
import sophia.mmorpg.player.quest.ref.order.QuestRefOrderType;

public final class QuestPersistenceReadWrite extends AbstractPersistenceObjectReadWrite<Quest> {

	private static final Logger logger = Logger.getLogger(QuestPersistenceReadWrite.class.getName());

	private int current_version = Default_Write_Version + 7;

	private Player player;

	public QuestPersistenceReadWrite(Player player) {
		this.player = player;
	}

	@Override
	public byte[] toBytes(Quest persistenceObject) {
		if (current_version == Default_Write_Version) {
			return toBytesVer10000(persistenceObject);
		} else if (current_version == Default_Write_Version + 7) {
			return toBytesVer10007(persistenceObject);
		} else {
			logger.error("写入版本没有对应写入方法");
			return null;
		}
	}

	@Override
	public Quest fromBytes(byte[] persistenceBytes) {
		if (persistenceBytes != null) {
			ByteArrayReadWriteBuffer buffer = new ByteArrayReadWriteBuffer(persistenceBytes);
			int Vers = buffer.readInt();
			if (Vers == current_version) {
				return fromBytesVers10007(buffer);
			}
		}
		return configQuestToPlayer();
	}

	public Quest configQuestToPlayer() {
		Quest ret = GameObjectFactory.getQuest("quest_1");
		ret.setQuestState(QuestState.AcceptedQuestState);
		Map<Integer, String> list = ret.createQuestCourseItem(null);
		for (int single : list.keySet()) {
			if (single == QuestRefOrderType.Collect_Order_Type || single == QuestRefOrderType.Loot_Item_Order_Type) {
				int number = ItemFacade.getNumber(player, list.get(single));
				ret.setQuestCourseNum(list.get(single), number, player);
			}
		}
		if (ret.getQuestCourse().wasCompleted()) {
			ret.setQuestState(QuestState.SubmittableQuestState);
		}
		return ret;
	}

	@Override
	public String toJsonString(Quest persistenceObject) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Quest fromJsonString(String persistenceJsonString) {
		// TODO Auto-generated method stub
		return null;
	}

	private byte[] toBytesVer10000(Quest quest) {
		ByteArrayReadWriteBuffer buffer = new ByteArrayReadWriteBuffer();

		// 我们先写个版本号，读的时候，好用对应版本的方法。
		buffer.writeInt(Default_Write_Version);
		// 是那个任务refId
		String questRefId = quest.getQuestRef().getId();
		buffer.writeString(questRefId);

		// 任务状态
		int questState = quest.getQuestState();
		buffer.writeInt(questState);

		// 如果状态是： QuestState.AcceptedQuestState， 我们需要保持进度数据
		if (questState == QuestState.AcceptedQuestState) {
			List<QuestCourseItem> questCourseItemList = quest.getQuestCourse().getQuestCourseItemList();
			buffer.writeInt(questCourseItemList.size());
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
					break;
				}
			}
		}

		byte[] data = buffer.getData();
		return data;
	}

	private Quest fromBytesVers10000(ByteArrayReadWriteBuffer buffer) {
		String questRefId = buffer.readString();
		Quest crtQuest = GameObjectFactory.getQuest(questRefId);

		int questState = buffer.readInt();
		crtQuest.setQuestState(questState);

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

	private byte[] toBytesVer10007(Quest quest) {
		ByteArrayReadWriteBuffer buffer = new ByteArrayReadWriteBuffer();

		// 我们先写个版本号，读的时候，好用对应版本的方法。
		buffer.writeInt(Default_Write_Version + 7);
		// 是那个任务refId
		String questRefId = quest.getQuestRef().getId();
		buffer.writeString(questRefId);

		// 任务状态
		int questState = quest.getQuestState();
		buffer.writeInt(questState);

		// 如果状态是： QuestState.AcceptedQuestState， 我们需要保持进度数据
		if (questState == QuestState.AcceptedQuestState) {
			List<QuestCourseItem> questCourseItemList = quest.getQuestCourse().getQuestCourseItemList();
			buffer.writeInt(questCourseItemList.size());
			for (QuestCourseItem questCourseItem : questCourseItemList) {
				QuestRefOrderItem questRefOrderItem = questCourseItem.getQuestRefOrderItem();
				byte orderType = questRefOrderItem.getOrderType();
				buffer.writeByte(orderType);
				switch (orderType) {
				case QuestRefOrderType.Kill_Monster_Order_Type:
					KillQuestCourseItem killQuestCourseItem = (KillQuestCourseItem) questCourseItem;
					buffer.writeShort(killQuestCourseItem.getCourseNumber());
					break;
				case QuestRefOrderType.Loot_Item_Order_Type:
					LootQuestCourseItem lootQuestCourseItem = (LootQuestCourseItem) questCourseItem;
					buffer.writeShort(lootQuestCourseItem.getCourseNumber());
					break;
				case QuestRefOrderType.Collect_Order_Type:
					CollectQuestCourseItem collectQuestCourseItem = (CollectQuestCourseItem) questCourseItem;
					buffer.writeShort(collectQuestCourseItem.getCourseNumber());
					break;
				case QuestRefOrderType.ChineseMode_String_Value_Order_Type:
					ChineseModeStringQuestCourseItem chineseCourse = (ChineseModeStringQuestCourseItem) questCourseItem;
					ChineseModeStringQuestRefOrderItem chineseRefOrderItem = (ChineseModeStringQuestRefOrderItem) chineseCourse.getQuestRefOrderItem();
					buffer.writeShort(chineseRefOrderItem.getOrderEventId());
					if (chineseRefOrderItem.getOrderEventId() == QuestChineseOrderDefines.PeerageLevelUp) {
						int number = chineseCourse.getCourseNumber();
						buffer.writeInt(number);
					} else if (chineseRefOrderItem.getOrderEventId() == QuestChineseOrderDefines.MountLevelUp) {
						int number = chineseCourse.getCourseNumber();
						buffer.writeInt(number);
					} else if (chineseRefOrderItem.getOrderEventId() == QuestChineseOrderDefines.WingLevelUp) {
						int number = chineseCourse.getCourseNumber();
						buffer.writeInt(number);
					} else if (chineseRefOrderItem.getOrderEventId() == QuestChineseOrderDefines.BuyStoreItem) {
						int number = chineseCourse.getCourseNumber();
						buffer.writeInt(number);
					} else if (chineseRefOrderItem.getOrderEventId() == QuestChineseOrderDefines.Ladder) {
						int number = chineseCourse.getCourseNumber();
						buffer.writeInt(number);
					}
					break;
				}
			}
		}
		byte[] data = buffer.getData();
		return data;
	}

	private Quest fromBytesVers10007(ByteArrayReadWriteBuffer buffer) {
		String questRefId = buffer.readString();
		Quest crtQuest = GameObjectFactory.getQuest(questRefId);

		int questState = buffer.readInt();
		crtQuest.setQuestState(questState);

		if (questState == QuestState.AcceptedQuestState) {
			crtQuest.createQuestCourseItem(player);
			int i = buffer.readInt();
			if (i > 0) {
				byte i2 = buffer.readByte();
				short i3 = 0;
				if (i2 == QuestRefOrderType.Collect_Order_Type || i2 == QuestRefOrderType.Loot_Item_Order_Type || i2 == QuestRefOrderType.Kill_Monster_Order_Type) {
					i3 = buffer.readShort();
				} else if (i2 == QuestRefOrderType.ChineseMode_String_Value_Order_Type) {
					short orderEventId = buffer.readShort();
					if (orderEventId == PlayerQuestComponent.peerageLevelUp) {
						int number = buffer.readInt();
						crtQuest.readQuestCourseInChineseModeString(orderEventId, number);
					} else if (orderEventId == PlayerQuestComponent.mountLevelUp || orderEventId == PlayerQuestComponent.wingLevelUp
							|| orderEventId == PlayerQuestComponent.buyStoreItem || orderEventId == PlayerQuestComponent.ladder) {
						int number = buffer.readInt();
						crtQuest.readQuestCourseInChineseModeString(orderEventId, number);
					}
					return crtQuest;
				}
				crtQuest.readQuestCourseItem(i, i2, i3);
				if (crtQuest.getQuestCourse().wasCompleted()) {
					crtQuest.setQuestState(QuestState.SubmittableQuestState);
				}
			}
		}
		return crtQuest;
	}
}
