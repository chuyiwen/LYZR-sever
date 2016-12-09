package sophia.mmorpg.player.quest.course;

import sophia.mmorpg.player.quest.ref.order.LootItemQuestRefOrderItem;
import sophia.mmorpg.player.quest.ref.order.QuestRefOrderItem;

public class LootQuestCourseItem implements QuestCourseItem {
	private LootItemQuestRefOrderItem orderItem;
	private short courseNumber;

	public LootQuestCourseItem() {
	}

	public LootQuestCourseItem(LootItemQuestRefOrderItem orderItem) {
		this(orderItem, (short) 0);
	}

	public LootQuestCourseItem(LootItemQuestRefOrderItem orderItem, short courseNumber) {
		this.orderItem = orderItem;
		this.courseNumber = courseNumber;
	}

	@Override
	public int getIndex() {
		return 0;
	}

	@Override
	public boolean wasCompleted() {
		return courseNumber >= orderItem.getNumber();
	}

	@Override
	public QuestRefOrderItem getQuestRefOrderItem() {
		return orderItem;
	}

	public LootItemQuestRefOrderItem getOrderItem() {
		return orderItem;
	}

	public void setOrderItem(LootItemQuestRefOrderItem orderItem) {
		this.orderItem = orderItem;
	}

	public short getCourseNumber() {
		return courseNumber;
	}

	public void setCourseNumber(short courseNumber) {
		this.courseNumber = courseNumber;
	}

}
