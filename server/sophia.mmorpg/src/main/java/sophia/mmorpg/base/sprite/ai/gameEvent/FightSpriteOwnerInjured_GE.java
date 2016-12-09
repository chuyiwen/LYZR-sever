package sophia.mmorpg.base.sprite.ai.gameEvent;

import sophia.mmorpg.base.sprite.FightSprite;

public class FightSpriteOwnerInjured_GE {

	private FightSprite attacker;
	
	public FightSpriteOwnerInjured_GE (FightSprite fightSprite) {
		attacker = fightSprite;
	}

	public FightSprite getAttacker() {
		return attacker;
	}

	public void setAttacker(FightSprite attacker) {
		this.attacker = attacker;
	}
	
}
