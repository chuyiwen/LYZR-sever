package sophia.mmorpg.pluck;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import sophia.game.ref.AbstractGameRefObjectBase;
import sophia.mmorpg.player.itemBag.ItemPair;

public class PluckRef extends AbstractGameRefObjectBase {
	private static final long serialVersionUID = -8109466949959162024L;

	// itemRefId, <count, probability>
	private Map<String, HashMap<Integer, Float>> itemRewardMapping = new HashMap<String, HashMap<Integer, Float>>();
	
	// itemRefId, bindStatus
	private Map<String, Byte> itemBindStatusMapping = new HashMap<String, Byte>();
	
	private Map<String, Integer> propertyRewardMapping = new HashMap<String, Integer>();

	public PluckRef() {

	}

	public Map<String, HashMap<Integer, Float>> getItemRewardMapping() {
		return itemRewardMapping;
	}

	public void setItemRewardMapping(Map<String, HashMap<Integer, Float>> itemRewardMapping) {
		this.itemRewardMapping = itemRewardMapping;
	}

	public Map<String, Integer> getPropertyRewardMapping() {
		return propertyRewardMapping;
	}

	public void setPropertyRewardMapping(Map<String, Integer> propertyRewardMapping) {
		this.propertyRewardMapping = propertyRewardMapping;
	}

	public Map<String, Byte> getItemBindStatusMapping() {
		return itemBindStatusMapping;
	}

	public void setItemBindStatusMapping(Map<String, Byte> itemBindStatusMapping) {
		this.itemBindStatusMapping = itemBindStatusMapping;
	}

	/**
	 * 本次采集随机到的物品
	 */
	public List<ItemPair> getItemPairs() {
		List<ItemPair> itemPairs = new ArrayList<ItemPair>();
		for (Entry<String, HashMap<Integer, Float>> entry : itemRewardMapping.entrySet()) {
			String itemRefId = entry.getKey();
			int count = 0;

			Map<Integer, Float> countMap = entry.getValue();
			float[] args = new float[countMap.size()];

			Set<Integer> keySet = countMap.keySet();
			int i = 0;
			for (Integer temp : keySet) {
				args[i] = countMap.get(temp);
				i++;
			}
			float randomValue = randomBetween(args);
			for (Entry<Integer, Float> tmpEntry : countMap.entrySet()) {
				if (tmpEntry.getValue() == randomValue) {
					count = tmpEntry.getKey();
				}
			}

			if (count == 0) {
				continue;
			}
			
			byte bindStatus = this.getItemBindStatusMapping().get(itemRefId);
			ItemPair itemPair = new ItemPair(itemRefId, count, bindStatus);
			itemPairs.add(itemPair);
		}


		return itemPairs;
	}
	
	public List<ItemPair> getPropertyItemPairs() {
		List<ItemPair> itemPairs = new ArrayList<ItemPair>();
		for (Entry<String, Integer> entry : propertyRewardMapping.entrySet()) {
			if (entry.getValue() == 0) {
				continue;
			}
			ItemPair itemPair = new ItemPair(entry.getKey(), entry.getValue(), false);
			itemPairs.add(itemPair);
		}
		
		return itemPairs;
	}
	
	public List<ItemPair> getAllItemPais() {
		List<ItemPair> itemPairs = getItemPairs();
		List<ItemPair> propertyItemPairs = getPropertyItemPairs();
		itemPairs.addAll(propertyItemPairs);
		
		return itemPairs;
	}

	public static float randomBetween(float... args) {
		Arrays.sort(args);
		int[] ary = new int[args.length];
		for (int i = 0; i < args.length; i++) {
			ary[i] = (int) (args[i] * 100);
		}

		Random r = new Random();
		int ranValue = r.nextInt(100);
		int init = 0;
		for (int j = 0; j < ary.length; j++) {
			init += ary[j];
			if (ranValue <= init) {
				return ary[j] / 100.0f;
			}
		}
		return 0.0f;
	}
}
