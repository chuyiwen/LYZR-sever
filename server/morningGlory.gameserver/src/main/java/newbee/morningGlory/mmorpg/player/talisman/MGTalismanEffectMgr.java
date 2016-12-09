package newbee.morningGlory.mmorpg.player.talisman;

import java.util.List;

import sophia.foundation.property.PropertyDictionary;
import sophia.mmorpg.base.sprite.fightProperty.FightEffectProperty;
import sophia.mmorpg.base.sprite.fightProperty.FightPropertyEffectFacade;
import sophia.mmorpg.player.Player;

public class MGTalismanEffectMgr {
	private Player player;

	public MGTalismanEffectMgr(Player player) {
		this.player = player;
	}

	public void restore() {
		MGPlayerTalismanComponent playerTalismanComponent = (MGPlayerTalismanComponent) player.getTagged(MGPlayerTalismanComponent.Tag);
		FightPropertyEffectFacade.attachWithoutSnapshot(player, getProperty(playerTalismanComponent.getPlayerCitta().getCittaRef()));
		List<MGTalismanContains> list = playerTalismanComponent.getPlayerCitta().getTalismanList();
		for (MGTalismanContains talismanContains : list) {
			MGTalisman talisman = talismanContains.getTalisman();			
			
			if (!talisman.isPassiveTalisman() && talisman.isActive()) {
				playerTalismanComponent.getPlayerCitta().setCrtActiveTalisman(talisman);				
			}
		}
		
			
	}

	public void attach(MGTalisman talisman) {

		FightPropertyEffectFacade.attachAndNotify(player, getProperty(talisman));

	}

	public void detach(MGTalisman talisman) {

		FightPropertyEffectFacade.detachAndNotify(player, getProperty(talisman));
	}

	public void detachAndSnapshot(MGTalisman talisman) {

		FightPropertyEffectFacade.detachAndSnapshot(player, getProperty(talisman));
	}

	private PropertyDictionary getProperty(MGTalisman talisman) {
		PropertyDictionary property = new PropertyDictionary();
		for (short symbol : FightEffectProperty.fightEffectSymbols) {
			int value = (int) talisman.getTalismanRef().getProperty().getValue(symbol);
			if (value >= 0) {
				property.setOrPutValue(symbol, value);
			}
		}
		return property;
	}
	
	//====================== 新版效果附加 ========================
	
	public void attach(MGCittaRef ref) {

		FightPropertyEffectFacade.attachAndNotify(player, getProperty(ref));

	}

	public void detach(MGCittaRef ref) {

		FightPropertyEffectFacade.detachAndNotify(player, getProperty(ref));
	}

	public void detachAndSnapshot(MGCittaRef ref) {

		FightPropertyEffectFacade.detachAndSnapshot(player, getProperty(ref));
	}

	private PropertyDictionary getProperty(MGCittaRef ref) {
		
		PropertyDictionary property = new PropertyDictionary();
		if(ref == null){
			return property;
		}
		for (short symbol : FightEffectProperty.fightEffectSymbols) {
			int value = (int) ref.getEffectData().getValue(symbol);
			if (value >= 0) {
				property.setOrPutValue(symbol, value);
			}
		}
		return property;
	}

}
