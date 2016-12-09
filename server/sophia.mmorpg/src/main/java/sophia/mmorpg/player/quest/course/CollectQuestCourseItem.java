package sophia.mmorpg.player.quest.course;

import sophia.mmorpg.player.quest.ref.order.CollectQuestRefOrderItem;
import sophia.mmorpg.player.quest.ref.order.QuestRefOrderItem;

public class CollectQuestCourseItem implements QuestCourseItem{
	private CollectQuestRefOrderItem orderItem;
	private short courseNumber;
	private int value;
	
	public CollectQuestCourseItem() {

	}

	public CollectQuestCourseItem(CollectQuestRefOrderItem orderItem) {
		this(orderItem, (short) 0);
	}

	public CollectQuestCourseItem(CollectQuestRefOrderItem orderItem, short courseNumber) {
		this.orderItem = orderItem;
		this.courseNumber = courseNumber;
	}
	
	@Override
	public int getIndex() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean wasCompleted() {
		// TODO Auto-generated method stub
		return courseNumber >= orderItem.getNumber();
	}
	
	@Override
	public QuestRefOrderItem getQuestRefOrderItem() {
		// TODO Auto-generated method stub
		return orderItem;
	}

	public CollectQuestRefOrderItem getOrderItem() {
		return orderItem;
	}

	public void setOrderItem(CollectQuestRefOrderItem orderItem) {
		this.orderItem = orderItem;
	}

	public short getCourseNumber() {
		return courseNumber;
	}

	public void setCourseNumber(short courseNumber) {
		this.courseNumber = courseNumber;
	}
	
	public synchronized void addCourseNumber() {
		this.courseNumber += 1;
	}
	
	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

}
