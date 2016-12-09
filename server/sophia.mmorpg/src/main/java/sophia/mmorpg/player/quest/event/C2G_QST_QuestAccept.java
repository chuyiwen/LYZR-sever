package sophia.mmorpg.player.quest.event;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class C2G_QST_QuestAccept extends ActionEventBase{
	String acceptQuest;
	public C2G_QST_QuestAccept (){
		this.actionEventId = QuestActionEventDefines.C2G_QST_QuestAccept;
	}
	
	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		putString(buffer,acceptQuest);
		return buffer;
	}

	@Override
	public void unpackBody(IoBuffer buffer) {
		setAcceptQuest(getString(buffer));
	}
	
	public void setAcceptQuest(String acceptQuest){
		this.acceptQuest = acceptQuest;
	}
	
	public String getAcceptQuest(){
		return acceptQuest;
	}

}
