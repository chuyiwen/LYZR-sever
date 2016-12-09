/**
 * 
 */
package sophia.mmorpg.player.quest;

import java.util.UUID;

import sophia.game.GameRoot;
import sophia.mmorpg.GameObjectProvider;
import sophia.mmorpg.player.quest.ref.QuestRef;

public class DefaultQuestProvider implements GameObjectProvider<Quest> {

	private static final GameObjectProvider<Quest> instance = new DefaultQuestProvider();

	private DefaultQuestProvider() {
	}

	public static final GameObjectProvider<Quest> getInstance() {
		return instance;
	}

	@Override
	public Quest get(Class<Quest> type) {
		Quest quest = new Quest();
		return quest;
	}

	@Override
	public Quest get(Class<Quest> type, Object... args) {
		Quest quest = new Quest();
		String questId = (String) args[0];
		QuestRef questRef = (QuestRef) GameRoot.getGameRefObjectManager().getManagedObject(questId);
		quest.setId(UUID.randomUUID().toString());
		quest.setQuestRef(questRef);
		quest.setQuestState(QuestState.VisiableQuestState);
		return quest;
	}

}
