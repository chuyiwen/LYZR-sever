package sophia.mmorpg.player.chat;

public class ChatFriendInfo {
	private String playerName;
	private String playerId;
	private byte gender;
	private byte proId;
	private long latestChatTime;
	
	public ChatFriendInfo(String playerName, String playerId, byte gender, byte proId, long latestChatTime) {
		this.playerName = playerName;
		this.playerId = playerId;
		this.gender = gender;
		this.proId = proId;
		this.latestChatTime = latestChatTime;
	}
	
	public String getPlayerName() {
		return playerName;
	}
	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}
	public String getPlayerId() {
		return playerId;
	}
	public void setPlayerId(String playerId) {
		this.playerId = playerId;
	}
	public byte getGender() {
		return gender;
	}
	public void setGender(byte gender) {
		this.gender = gender;
	}
	public byte getProId() {
		return proId;
	}
	public void setProId(byte proId) {
		this.proId = proId;
	}
	public long getLatestChatTime() {
		return latestChatTime;
	}
	public void setLatestChatTime(long latestChatTime) {
		this.latestChatTime = latestChatTime;
	}
	
}
