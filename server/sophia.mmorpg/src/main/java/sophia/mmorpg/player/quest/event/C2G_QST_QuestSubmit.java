package sophia.mmorpg.player.quest.event;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class C2G_QST_QuestSubmit extends ActionEventBase {
	private String questId;
	
	public C2G_QST_QuestSubmit(){
		this.actionEventId = QuestActionEventDefines.C2G_QST_QuestSubmit;
	}
	
	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		putString(buffer,questId);
		return buffer;
	}

	@Override
	public void unpackBody(IoBuffer buffer) {
		// TODO Auto-generated method stub
		setQuestId(getString(buffer));
	}
	
	public void setQuestId(String questId){
		this.questId = questId;
	}
	
	public String getQuestId(){
		return questId;
	}

}
