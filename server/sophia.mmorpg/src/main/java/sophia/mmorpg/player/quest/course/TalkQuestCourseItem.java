package sophia.mmorpg.player.quest.course;

import sophia.mmorpg.player.quest.ref.order.QuestRefOrderItem;
import sophia.mmorpg.player.quest.ref.order.TalkQuestRefOrderItem;

public class TalkQuestCourseItem implements QuestCourseItem {
	private TalkQuestRefOrderItem orderItem;
	private boolean completed = true;
	
	public TalkQuestCourseItem() {
		
	}
	
	public TalkQuestCourseItem(TalkQuestRefOrderItem orderItem) {
		this(orderItem, false);
	}
	
	public TalkQuestCourseItem(TalkQuestRefOrderItem orderItem, boolean completed) {
		this.orderItem = orderItem;
		this.completed = completed;
	}
	
	@Override
	public int getIndex() {
		return orderItem.getIndex();
	}

	@Override
	public boolean wasCompleted() {
		return completed;
	}
	
	@Override
	public QuestRefOrderItem getQuestRefOrderItem() {
		return orderItem;
	}
	
	public TalkQuestRefOrderItem getOrderItem() {
		return orderItem;
	}

	public void setOrderItem(TalkQuestRefOrderItem orderItem) {
		this.orderItem = orderItem;
	}

	public void setCompleted(boolean completed) {
		this.completed = completed;
	}

}
