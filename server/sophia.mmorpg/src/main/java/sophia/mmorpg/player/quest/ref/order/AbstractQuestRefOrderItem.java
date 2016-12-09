package sophia.mmorpg.player.quest.ref.order;

public abstract class AbstractQuestRefOrderItem implements QuestRefOrderItem {
	protected byte index;

	protected AbstractQuestRefOrderItem() {
		super();
	}

	@Override
	public void setIndex(byte index) {
		this.index = index;
	}

	@Override
	public byte getIndex() {
		return index;
	}
}