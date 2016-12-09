package newbee.morningGlory.mmorpg.sceneActivities.castleWar.gameEvent;

import sophia.mmorpg.player.Player;

public class KillInCastleWar_GE {
	public static final byte Kill_Enemy = 1;
	
	public static final byte Kill_Boss = 2;
	
	private byte killType;

	private Player killer;

	public KillInCastleWar_GE(byte killType, Player killder) {
		this.killer = killder;
		this.killType = killType;
	}

	public byte getKillType() {
		return killType;
	}

	public void setKillType(byte killType) {
		this.killType = killType;
	}

	public Player getKiller() {
		return killer;
	}

	public void setKiller(Player killer) {
		this.killer = killer;
	}

}
