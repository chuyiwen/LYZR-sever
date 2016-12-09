package sophia.mmorpg.friend;

import java.util.Comparator;
import java.util.concurrent.CopyOnWriteArrayList;

import sophia.mmorpg.MMORPGContext;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.PlayerManager;

public class FriendMemberComparator implements Comparator<FriendMember> {

	/**
	 * 比较规则: 第一优先：有无最近消息； 第二优先：在线/离线； 第三优先：等级高低；
	 * 如果都一样，则按照聊天时间先后顺序
	 */
	@Override
	public int compare(FriendMember o1, FriendMember o2) {
		boolean hasUnreadChatRecord1 = o1.hasUnreadChatRecord();
		boolean hasUnreadChatRecord2 = o2.hasUnreadChatRecord();
		
		if (hasUnreadChatRecord1 && !hasUnreadChatRecord2) {
			return -1;
		}
		
		if (!hasUnreadChatRecord1 && hasUnreadChatRecord2) {
			return 1;
		}

		PlayerManager playerManager = MMORPGContext.getPlayerComponent().getPlayerManager();
		String playerId1 = o1.getPlayerId();
		String playerId2 = o2.getPlayerId();
		
		Player player1 = playerManager.getPlayer(playerId1);
		Player player2 = playerManager.getPlayer(playerId2);
		
		boolean online1 = player1.isOnline();
		boolean online2 = player2.isOnline();
		
		if (online1 && !online2) {
			return -1;
		}
		
		if (!online1 && online2) {
			return 1;
		}
		
		int level1 = player1.getExpComponent().getLevel();
		int level2 = player2.getExpComponent().getLevel();
		
		if (level1 != level2) {
			return level2 - level1;
		}
		

		long chatTime1 = 0;
		long chatTime2 = 0;
		
		CopyOnWriteArrayList<ChatRecord> chatRecords1 = o1.getChatRecords();
		if(chatRecords1.size()>=1){
			ChatRecord chatRecord1 = chatRecords1.get(chatRecords1.size() - 1);
			chatTime1 = chatRecord1.getChatTime();
		}
		
		CopyOnWriteArrayList<ChatRecord> chatRecords2 = o2.getChatRecords();
		if(chatRecords2.size()>=1){
			ChatRecord chatRecord2 = chatRecords2.get(chatRecords2.size() - 1);
			chatTime2 = chatRecord2.getChatTime();
		}
		
		return (int)(chatTime2 - chatTime1);
	}

}
