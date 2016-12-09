package newbee.morningGlory.mmorpg.player.talisman.gameEvent;

import java.util.HashMap;
import java.util.Map;

public class TalismanLevelUp {
	private Map<Integer, Integer> talisMap = new HashMap<Integer, Integer>();// <levle,count>

	public TalismanLevelUp() {

	}

	public Map<Integer, Integer> getTalisMap() {
		return talisMap;
	}

	public void setTalisMap(Map<Integer, Integer> talisMap) {
		this.talisMap = talisMap;
	}

}
