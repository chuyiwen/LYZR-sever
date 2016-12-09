package sophia.mmorpg.player.quest.course;

import sophia.game.component.communication.GameEvent;
import sophia.mmorpg.player.quest.ref.order.KillMonsterQuestRefOrderItem;
import sophia.mmorpg.player.quest.ref.order.QuestRefOrderItem;

public class KillQuestCourseItem implements QuestCourseItem {
	private KillMonsterQuestRefOrderItem orderItem;
	private short courseNumber;
	
	public KillQuestCourseItem() {
		
	}
	
	public KillQuestCourseItem(KillMonsterQuestRefOrderItem orderItem) {
		this(orderItem, (short)0);
	}
	
	public KillQuestCourseItem(KillMonsterQuestRefOrderItem orderItem, short courseNumber) {
		this.orderItem = orderItem;
		this.courseNumber = courseNumber;
	}
	
	@Override
	public int getIndex() {
		return 0;
	}

	@Override
	public synchronized boolean wasCompleted() {
		return courseNumber >= orderItem.getNumber();
	}
	
	@Override
	public QuestRefOrderItem getQuestRefOrderItem() {
		return orderItem;
	}
	
	public KillMonsterQuestRefOrderItem getOrderItem() {
		return orderItem;
	}

	public void setOrderItem(KillMonsterQuestRefOrderItem orderItem) {
		this.orderItem = orderItem;
	}
	
	public synchronized short getCourseNumber() {
		return courseNumber;
	}

	public synchronized boolean setCourseNumber(short courseNumber) {
		if (courseNumber > orderItem.getNumber()) {
			this.courseNumber = (short) orderItem.getNumber();
			return false;
		}
		this.courseNumber = courseNumber;
		return true;
	}
	
	public synchronized void addCourseNumber() {
		this.courseNumber += 1;
	}
	
	public boolean handleGameEvent(GameEvent<?> event) {
		return false;
	}
}
