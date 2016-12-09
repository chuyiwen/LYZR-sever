package newbee.morningGlory.mmorpg.player.peerage;

import sophia.foundation.property.PropertyDictionary;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;

public class MeritManager {
	private Player player;
	private int merit;

	public MeritManager() {

	}

	public void notifyProperty() {
		PropertyDictionary property = new PropertyDictionary();
		MGPropertyAccesser.setOrPutMerit(player.getProperty(), getMerit());
		MGPropertyAccesser.setOrPutMerit(property, getMerit());
		player.notifyPorperty(property);
	}

	public int getMerit() {
		return this.merit;
	}

	public void setMerit(int merit) {
		if (merit < 0)
			merit = 0;
		this.merit = merit;
	}

	private void setMeritImpl(int merit) {
		if (merit < 0)
			merit = 0;
		this.merit = merit;
		notifyProperty();
	}

	public void addMerit(int value) {
		if (value < 0)
			return;
		setMeritImpl(this.merit + value);
	}

	public void subMerit(int value) {
		if (value < 0 || value > getMerit())
			return;
		setMeritImpl(this.merit - value);
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

}
