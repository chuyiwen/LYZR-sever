package newbee.morningGlory.mmorpg.player.wing;

import sophia.foundation.property.PropertyDictionary;
import sophia.mmorpg.base.sprite.fightProperty.FightPropertyEffectFacade;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;

public class MGWingEffectMgr {
	private Player player;

	public MGWingEffectMgr(Player player) {
		this.player = player;
	}

	public void restore(MGPlayerWing playerWing) {
		attachWithoutSnapshot(playerWing);
		changeProperty();
	}

	public void attach(MGPlayerWing playerWing) {

		FightPropertyEffectFacade.attachAndNotify(player, getProperty(playerWing));
	}

	public void detach(MGPlayerWing playerWing) {

		FightPropertyEffectFacade.detachAndNotify(player, getProperty(playerWing));
	}

	public void detachAndSnapshot(MGPlayerWing playerWing) {

		FightPropertyEffectFacade.detachAndSnapshot(player, getProperty(playerWing));
	}

	public void attachWithoutSnapshot(MGPlayerWing playerWing) {

		FightPropertyEffectFacade.attachWithoutSnapshot(player, getProperty(playerWing));
	}

	/**
	 * 改变玩家装备模型属性
	 * 
	 * @param bodyAreaId
	 */
	private void changeProperty() {
		MGPlayerWingComponent wingComponent = (MGPlayerWingComponent) player.getTagged(MGPlayerWingComponent.Tag);
		MGPlayerWingRef wingRef = wingComponent.getPlayerWing().getPlayerWingRef();
		int modleId = MGPropertyAccesser.getModelId(wingRef.getProperty());
		MGPropertyAccesser.setOrPutWingModleId(player.getProperty(), modleId);
	}

	private PropertyDictionary getProperty(MGPlayerWing playerWing) {
		PropertyDictionary pd = null;
		MGPlayerWingRef playerWingRef = playerWing.getPlayerWingRef();
		pd = playerWingRef.getEffectProperty();
		return pd;
	}

}
