package newbee.morningGlory.mmorpg.vip.gameEvent;

import sophia.mmorpg.player.Player;

public class VipGE {
	private Player player;
	// vip等级
	private byte vipType;
	
	public VipGE(Player player, byte vipType) {
		this.player = player;
		this.vipType = vipType;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public byte getVipType() {
		return vipType;
	}

	public void setVipType(byte vipType) {
		this.vipType = vipType;
	}
	
}
