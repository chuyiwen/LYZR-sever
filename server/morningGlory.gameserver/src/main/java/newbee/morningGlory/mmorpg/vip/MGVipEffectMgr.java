package newbee.morningGlory.mmorpg.vip;

import sophia.foundation.property.PropertyDictionary;
import sophia.game.GameRoot;
import sophia.mmorpg.base.sprite.fightProperty.FightEffectProperty;
import sophia.mmorpg.base.sprite.fightProperty.FightPropertyEffectFacade;
import sophia.mmorpg.player.Player;

public class MGVipEffectMgr {
	private Player player;

	public MGVipEffectMgr(Player player) {
		this.player = player;
	}

	public void restore(String vipRefId, MGVipLevelMgr vipMgr) {
		MGVipLevelDataRef vipRef = (MGVipLevelDataRef) GameRoot.getGameRefObjectManager().getManagedObject(vipRefId);
		if (vipRef == null) {
			return;
		}
		vipMgr.setVipLevelDataRef(vipRef);
		FightPropertyEffectFacade.attachWithoutSnapshot(player, getProperty(vipRef));
	}

	public void attach(MGVipLevelDataRef vipRef) {

		FightPropertyEffectFacade.attachAndNotify(player, getProperty(vipRef));
	}

	public void detach(MGVipLevelDataRef vipRef) {

		FightPropertyEffectFacade.detachAndNotify(player, getProperty(vipRef));
	}

	public void detachAndSnapshot(MGVipLevelDataRef vipRef) {

		FightPropertyEffectFacade.detachAndSnapshot(player, getProperty(vipRef));
	}

	private PropertyDictionary getProperty(MGVipLevelDataRef vipRef) {
		PropertyDictionary property = new PropertyDictionary();
		for (short symbol : FightEffectProperty.fightEffectSymbols) {
			int value = (int) vipRef.getProperty().getValue(symbol);
			if (value >= 0) {
				property.setOrPutValue(symbol, value);
			}
		}
		return property;
	}

}
