package newbee.morningGlory.mmorpg.store;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemPrice {

	// origenPrice[gold, unbindedGold, bindedGold]
	private List<Integer> origenPrice = new ArrayList<>(Arrays.asList(0, 0, 0)); 
	
	private Map<String, Integer> experPrice = new HashMap<>();

	public List<Integer> getOrigenPrice() {
		return origenPrice;
	}

	public void setOrigenPrice(List<Integer> origenPrice) {
		this.origenPrice = origenPrice;
	}

	public Map<String, Integer> getExperPrice() {
		return experPrice;
	}

	public void addExperPrice(String itemRefId, int num) {
		experPrice.put(itemRefId, num);
	}

	public void setExperPrice(Map<String, Integer> experPrice) {
		this.experPrice = experPrice;
	}

}
