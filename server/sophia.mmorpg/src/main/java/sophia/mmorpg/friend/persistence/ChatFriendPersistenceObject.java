package sophia.mmorpg.friend.persistence;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.log4j.Logger;

import sophia.foundation.util.ByteArrayReadWriteBuffer;
import sophia.mmorpg.friend.ChatRecord;
import sophia.mmorpg.friend.FriendMember;


public class ChatFriendPersistenceObject {
	private static Logger logger = Logger.getLogger(ChatFriendPersistenceObject.class);
	
	private static ChatFriendPersistenceObject instance = new ChatFriendPersistenceObject();

	private ChatFriendPersistenceObject() {

	}

	public static ChatFriendPersistenceObject getInstance() {
		return instance;
	}
	
	public byte[] toBytes(Map<String, FriendMember> friendMapping) {
		return chatFriendInfotoBytes1000(friendMapping);
	}
	
	public ConcurrentHashMap<String, FriendMember> fromBytes(byte[] bytes) {
		ByteArrayReadWriteBuffer buffer = new ByteArrayReadWriteBuffer(bytes);
		
		int version = buffer.readInt();
		if (version == 10000) {
			return chatFriendInfofromBytes1000(buffer); 
		} else {
			logger.error("read chatFriendMember info error! version = " + version);
			return null;
		}
		
	}
	
	private byte[] chatFriendInfotoBytes1000(Map<String, FriendMember> friendMapping) {
		ByteArrayReadWriteBuffer buffer = new ByteArrayReadWriteBuffer();
		
		buffer.writeInt(10000);
		int size = friendMapping.size();
		
		buffer.writeInt(size);
		for (Entry<String, FriendMember> entry : friendMapping.entrySet()) {
			String playerId = entry.getKey();
			FriendMember friendMember = entry.getValue();
			String playerName = friendMember.getPlayerName();
			byte professionId = friendMember.getProfessionId();
			byte gender = friendMember.getGender();
			
			buffer.writeString(playerId);
			buffer.writeString(playerName);
			buffer.writeByte(professionId);
			buffer.writeByte(gender);
			
			CopyOnWriteArrayList<ChatRecord> chatRecords = friendMember.getChatRecords();
			
			int chatRecordsSize = chatRecords.size();
			
			buffer.writeInt(chatRecordsSize);
			
			for (ChatRecord chatRecord : chatRecords) {
				String senderId = chatRecord.getSenderId();
				
				String receiverId = chatRecord.getReceiverId();	
				
				String content = chatRecord.getContent();
				
				long chatTime = chatRecord.getChatTime();
				
				boolean read = chatRecord.isRead();
				
				buffer.writeString(senderId);
				buffer.writeString(receiverId);
				buffer.writeString(content);
				buffer.writeLong(chatTime);
				buffer.writeBoolean(read);
			}
		}
		
		return buffer.getData();
		
	}
	
	private ConcurrentHashMap<String, FriendMember> chatFriendInfofromBytes1000(ByteArrayReadWriteBuffer buffer) {
		ConcurrentHashMap<String, FriendMember> friendMapping = new ConcurrentHashMap<String, FriendMember>();
		
		int friednMappingSize = buffer.readInt();
		
		for (int i = 0; i < friednMappingSize; i++) {
			String playerId = buffer.readString();
			String playerName = buffer.readString();
			byte professionId = buffer.readByte();
			byte gender = buffer.readByte();
			
			CopyOnWriteArrayList<ChatRecord> chatRecords = new CopyOnWriteArrayList<>();
		
			int chatRecordsSize = buffer.readInt();
		
			for (int j = 0; j < chatRecordsSize; j++) {
				String senderId = buffer.readString();
				
				String receiverId = buffer.readString();
				
				String content = buffer.readString();
				
				long chatTime = buffer.readLong();
				
				boolean isRead = buffer.readBoolean();
				
				ChatRecord chatRecord = new ChatRecord(senderId, receiverId, content, chatTime, isRead);
				
				chatRecords.add(chatRecord);
			}
			
			FriendMember friendMember = new FriendMember(playerId, playerName, professionId, gender, chatRecords);
		
			friendMapping.put(playerId, friendMember);
		}
		
		return friendMapping;
		
	}
	
}
