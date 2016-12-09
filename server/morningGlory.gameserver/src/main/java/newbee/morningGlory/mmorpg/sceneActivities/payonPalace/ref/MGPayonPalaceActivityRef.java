package newbee.morningGlory.mmorpg.sceneActivities.payonPalace.ref;

import java.util.HashMap;
import java.util.Map;

import sophia.game.ref.AbstractGameRefObjectBase;

public class MGPayonPalaceActivityRef extends AbstractGameRefObjectBase{
	private static final long serialVersionUID = 4939265624833744459L;
	
	private Map<String, Integer> consumptionItems = new HashMap<>();
	private int transferIn;
	
	public Map<String, Integer> getConsumptionItems() {
		return consumptionItems;
	}

	public void setConsumptionItems(Map<String, Integer> consumptionItems) {
		this.consumptionItems = consumptionItems;
	}

	public int getTransferIn() {
		return transferIn;
	}

	public void setTransferIn(int transferIn) {
		this.transferIn = transferIn;
	}
	
}
