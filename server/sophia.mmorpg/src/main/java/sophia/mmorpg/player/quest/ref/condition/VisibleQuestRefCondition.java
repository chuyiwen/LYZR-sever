package sophia.mmorpg.player.quest.ref.condition;

import sophia.mmorpg.player.Player;

public final class VisibleQuestRefCondition implements QuestRefConditionItem {
	private int visibleLevel;
	
	@Override
	public boolean eligibleTo(Player player) {
		return player.getExpComponent().getLevel() >= visibleLevel;
	}
	
	public final int getNumber() {
		return visibleLevel;
	}

	public final void setVisibleLevel(int visibleLevel) {
		this.visibleLevel = visibleLevel;
	}

	@Override
	public int getType() {
		return QuestRefConditionType.Visiable_Condition_Type;
	}

}
