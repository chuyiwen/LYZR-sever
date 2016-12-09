package sophia.mmorpg.communication;

import java.util.Date;

import sophia.mmorpg.player.Player;

public class BlackListEntry {
	private String playerId;
	private String name;
	private String identityId;
	private String identityName;
	private Date blockTime;
	

	public BlackListEntry(Player player, Date blockTime) {
		super();
		this.playerId = player.getId();
		this.name = player.getName();
		this.identityId = player.getIdentity().getId();
		this.identityName = player.getIdentity().getName();
		this.blockTime = blockTime;
	}

	public String getPlayerId() {
		return playerId;
	}

	public void setPlayerId(String playerId) {
		this.playerId = playerId;
	}

	public Date getBlockTime() {
		return blockTime;
	}

	public void setBlockTime(Date blockTime) {
		this.blockTime = blockTime;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIdentityId() {
		return identityId;
	}

	public void setIdentityId(String identityId) {
		this.identityId = identityId;
	}

	public String getIdentityName() {
		return identityName;
	}

	public void setIdentityName(String identityName) {
		this.identityName = identityName;
	}

	@Override
	public String toString() {
		return "BlackListEntry [playerId=" + playerId + ", playerName=" + name + ", blockTime=" + blockTime + "]";
	}

}