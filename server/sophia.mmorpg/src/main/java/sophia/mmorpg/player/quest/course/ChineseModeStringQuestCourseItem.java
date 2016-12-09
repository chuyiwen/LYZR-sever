package sophia.mmorpg.player.quest.course;

import sophia.mmorpg.player.quest.ref.order.ChineseModeStringQuestRefOrderItem;
import sophia.mmorpg.player.quest.ref.order.QuestChineseOrderDefines;
import sophia.mmorpg.player.quest.ref.order.QuestRefOrderItem;

public class ChineseModeStringQuestCourseItem implements QuestCourseItem {
	private ChineseModeStringQuestRefOrderItem orderItem;
	private String modeValue;
	private String ModeTarget;
	private int courseNumber;
	private long starTime;
	private boolean completed = false;

	public void setCompleted(boolean completed) {
		this.completed = completed;
	}

	public ChineseModeStringQuestCourseItem() {
	}

	public ChineseModeStringQuestCourseItem(ChineseModeStringQuestRefOrderItem orderItem, String modeValue, int number) {
		this.orderItem = orderItem;
		this.modeValue = modeValue;
		this.courseNumber = number;
		this.starTime = System.currentTimeMillis();
	}
	
	public ChineseModeStringQuestCourseItem(ChineseModeStringQuestRefOrderItem orderItem, String modeValue, long x, int y) {
		this.orderItem = orderItem;
		this.modeValue = modeValue;
		this.starTime = x;
		this.courseNumber = y;
	}

	@Override
	public int getIndex() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public QuestRefOrderItem getQuestRefOrderItem() {
		return orderItem;
	}
	
	public void setQuestRefOrderItem(ChineseModeStringQuestRefOrderItem orderItem) {
		this.orderItem = orderItem;
	}

	@Override
	public boolean wasCompleted() {
		if (completed == true) {
			return completed;
		} else {
			if (orderItem.getOrderEventId() == QuestChineseOrderDefines.PeerageLevelUp) {
				if (courseNumber >= orderItem.getNumber()) {
					completed = true;
				}
			} else if (orderItem.getOrderEventId() == QuestChineseOrderDefines.MountLevelUp) {
				int orderNumber = (int) (orderItem.getNumber() * 10 + orderItem.getCount());
				if (courseNumber >= orderNumber) {
					completed = true;
				}
			} else if (orderItem.getOrderEventId() == QuestChineseOrderDefines.WingLevelUp) {
				int orderNumber = (int) (orderItem.getNumber() * 10 + orderItem.getCount());
				if (courseNumber >= orderNumber) {
					completed = true;
				}
			} else if (orderItem.getOrderEventId() == QuestChineseOrderDefines.BuyStoreItem) {
				if (courseNumber >= orderItem.getNumber()) {
					completed = true;
				}
			} else if (orderItem.getOrderEventId() == QuestChineseOrderDefines.Ladder) {
				if (courseNumber >= orderItem.getNumber()) {
					completed = true;
				}
			}
			else {
				long time = (System.currentTimeMillis() - starTime) / 1000;
				if (courseNumber >= orderItem.getNumber() && (time <= orderItem.getCount())) {
					completed = true;
				}
			}
		}
		return completed;
	}

	public String getModeValue() {
		return modeValue;
	}

	public void setModeValue(String modeValue) {
		this.modeValue = modeValue;
	}

	public int getCourseNumber() {
		return courseNumber;
	}

	public void setCourseNumber(int number) {
		this.courseNumber = number;
	}
	
	public synchronized void addCourseNumber() {
		this.courseNumber += 1;
	}

	public long getStarTime() {
		return starTime;
	}

	public void setStarTime(long time) {
		this.starTime = time;
	}

	public long getTimeCount() {
		long time = (System.currentTimeMillis() - starTime) / 1000;
		return time;
	}

	public String getModeTarget() {
		return ModeTarget;
	}

	public void setModeTarget(String modeTarget) {
		ModeTarget = modeTarget;
	}

}
