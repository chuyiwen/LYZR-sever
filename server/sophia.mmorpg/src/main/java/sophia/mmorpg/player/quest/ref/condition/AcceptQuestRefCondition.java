package sophia.mmorpg.player.quest.ref.condition;

import sophia.mmorpg.player.Player;

public class AcceptQuestRefCondition implements QuestRefConditionItem {
	private int acceptLevel;
	
	@Override
	public boolean eligibleTo(Player player) {
		return player.getExpComponent().getLevel() >= acceptLevel;
	}
	
	@Override
	public final int getNumber() {
		return acceptLevel;
	}

	public final void setAcceptLevel(int acceptLevel) {
		this.acceptLevel = acceptLevel;
	}

	@Override
	public int getType() {
		return QuestRefConditionType.Accept_Condition_Type;
	}

}
