package sophia.mmorpg.communication;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.lang3.StringUtils;

import sophia.mmorpg.player.Player;

public class BlackList {
	private final List<BlackListEntry> blacklist = new CopyOnWriteArrayList<>();

	public BlackList() {
		super();
	}

	public boolean block(Player player, Date blockTime) {
		BlackListEntry e = new BlackListEntry(player, blockTime);
		BlackListDAO.getInstance().save(e);
		return blacklist.add(e);
	}

	public boolean block(Player player, long blockTime) {
		BlackListEntry e = new BlackListEntry(player, new Date(blockTime));
		BlackListDAO.getInstance().save(e);
		return blacklist.add(e);
	}

	public boolean isBlocked(Player player) {
		for (BlackListEntry entry : blacklist) {
			if (StringUtils.equals(entry.getPlayerId(), player.getId())) {
				return true;
			}
		}
		return false;
	}

	// TODO: We can block a player multiple times, here we unblock him all at
	// once. Try to think about a reasonable solution here.
	public void unblock(Player player) {
		Iterator<BlackListEntry> iter = blacklist.iterator();
		while (iter.hasNext()) {
			BlackListEntry next = iter.next();
			if (StringUtils.equals(next.getPlayerId(), player.getId())) {
				iter.remove();
			}
		}
	}

	public List<BlackListEntry> getBlacklist() {
		return blacklist;
	}

	

}
