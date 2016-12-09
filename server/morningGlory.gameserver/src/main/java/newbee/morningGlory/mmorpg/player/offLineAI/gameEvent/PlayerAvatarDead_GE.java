package newbee.morningGlory.mmorpg.player.offLineAI.gameEvent;

import newbee.morningGlory.mmorpg.player.offLineAI.PlayerAvatar;
import sophia.mmorpg.base.sprite.FightSprite;

public class PlayerAvatarDead_GE {

	private FightSprite attacker;		//杀死替身的战斗精灵
	private PlayerAvatar playerAvatar;	//替身自身

	
	
	public PlayerAvatarDead_GE(FightSprite attacker, PlayerAvatar playerAvatar) {
		this.attacker = attacker;
		this.playerAvatar = playerAvatar;
	}
	public FightSprite getAttacker() {
		return attacker;
	}
	public PlayerAvatar getPlayerAvatar() {
		return playerAvatar;
	}
	
}
