package sophia.mmorpg.friend;

public class ChatRecord {
	private String senderId;

	private String receiverId;

	private String content;

	private long chatTime;

	private boolean isRead;

//	public ChatRecord(String senderId, String receiverId, String content, ) {
//		this.senderId = senderId;
//		this.receiverId = receiverId;
//		this.content = content;
//		this.chatTime = System.currentTimeMillis();
//		this.isRead = false;
//	}
	
	public ChatRecord(String senderId, String receiverId, String content, long chatTime, boolean isRead) {
		this.senderId = senderId;
		this.receiverId = receiverId;
		this.content = content;
		this.chatTime = chatTime;
		this.isRead = isRead;
	}

	public String getSenderId() {
		return senderId;
	}

	public void setSenderId(String senderId) {
		this.senderId = senderId;
	}

	public String getReceiverId() {
		return receiverId;
	}

	public void setReceiverId(String receiverId) {
		this.receiverId = receiverId;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public long getChatTime() {
		return chatTime;
	}

	public void setChatTime(long chatTime) {
		this.chatTime = chatTime;
	}

	public boolean isRead() {
		return isRead;
	}

	public void setRead(boolean isRead) {
		this.isRead = isRead;
	}

}
