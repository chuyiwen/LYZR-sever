package sophia.mmorpg.friend;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


public class FriendMember {
	private String playerId;
	
	private String playerName;
	
	private byte professionId;
	
	private byte gender;
	
	private CopyOnWriteArrayList<ChatRecord> chatRecords = new CopyOnWriteArrayList<ChatRecord>();

	public FriendMember(String playerId, String playerName, byte professionId, byte gender) {
		this.playerId = playerId;
		this.playerName = playerName;
		this.professionId = professionId;
		this.gender = gender;
	}
	
	public FriendMember(String playerId, String playerName, byte professionId, byte gender, CopyOnWriteArrayList<ChatRecord> chatRecords) {
		this.playerId = playerId;
		this.playerName = playerName;
		this.professionId = professionId;
		this.gender = gender;
		this.chatRecords = chatRecords;
	}
	
	public String getPlayerId() {
		return playerId;
	}
	
	
	public synchronized boolean addChatRecord(ChatRecord chatRecord) {
		return this.chatRecords.add(chatRecord);
	}

	public synchronized CopyOnWriteArrayList<ChatRecord> getChatRecords() {
		CopyOnWriteArrayList<ChatRecord> copyChatRecords = new CopyOnWriteArrayList<ChatRecord>();
		
		copyChatRecords.addAll(chatRecords);
		
		return copyChatRecords;
	}

	public synchronized void setChatRecords(CopyOnWriteArrayList<ChatRecord> chatRecords) {
		this.chatRecords = chatRecords;
	}
	
	public String getPlayerName() {
		return playerName;
	}

	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}

	public byte getProfessionId() {
		return professionId;
	}

	public void setProfessionId(byte professionId) {
		this.professionId = professionId;
	}

	public byte getGender() {
		return gender;
	}

	public void setGender(byte gender) {
		this.gender = gender;
	}

	public void setPlayerId(String playerId) {
		this.playerId = playerId;
	}

	public boolean hasUnreadChatRecord() {
		for (ChatRecord chatRecord : getChatRecords()) {
			boolean read = chatRecord.isRead();
			
			if (!read) {
				return true;
			}
		}
		
		return false;
	}
	
	public synchronized List<ChatRecord> getOfflineMessage() {
		List<ChatRecord> offlineRecords = new ArrayList<ChatRecord>();
		for (ChatRecord chatRecord : chatRecords) {
			if (!chatRecord.isRead()) {
				offlineRecords.add(chatRecord);
				chatRecord.setRead(true);
			}
		}
		
		return offlineRecords;
	}
}
