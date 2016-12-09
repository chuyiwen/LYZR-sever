package sophia.mmorpg.player.quest.ref.condition;

import com.google.common.base.Preconditions;

public abstract class AbstractQuestRefCondition implements QuestRefCondition {
	protected byte conditionType;

	protected AbstractQuestRefCondition() {
		super();
	}

	@Override
	public byte getConditionType() {
		return conditionType;
	}

	public final void setConditionType(byte conditionType) {
		Preconditions.checkNotNull(conditionType);
		
		this.conditionType = conditionType;
	}
}