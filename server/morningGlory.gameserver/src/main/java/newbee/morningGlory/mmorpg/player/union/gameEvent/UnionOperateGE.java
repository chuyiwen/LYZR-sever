package newbee.morningGlory.mmorpg.player.union.gameEvent;

import sophia.mmorpg.player.Player;

public class UnionOperateGE {
	private byte operateType;

	private Player player;

	private boolean isOfficer;

	public UnionOperateGE(byte operateType, Player player, boolean isOfficer) {
		this.operateType = operateType;
		this.player = player;
		this.isOfficer = isOfficer;
	}

	public byte getOperateType() {
		return operateType;
	}

	public void setOperateType(byte operateType) {
		this.operateType = operateType;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public boolean isOfficer() {
		return isOfficer;
	}

	public void setOfficer(boolean isOfficer) {
		this.isOfficer = isOfficer;
	}

}
