package newbee.morningGlory.mmorpg.player.store.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import newbee.morningGlory.mmorpg.store.ItemPrice;
import newbee.morningGlory.mmorpg.store.ref.MallItemRef;
import newbee.morningGlory.mmorpg.store.ref.ShopItemRef;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;
import sophia.foundation.property.PropertyDictionary;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;

public class G2C_Store_LimitItemResp extends ActionEventBase {
	private Map<String, MallItemRef> mallItemList;
	private Map<String, ShopItemRef> shopItemList;
	private Map<String, Short> allLimit;
	private Map<String, Short> personalLimit;
	private String storeType;

	public G2C_Store_LimitItemResp(){
		ziped =(byte)1;
	}
	
	public String getStoreType() {
		return storeType;
	}

	public void setStoreType(String storeType) {
		this.storeType = storeType;
	}

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		if (storeType.equals("mall")) {
			packMallItem(buffer);
		} else {
			packShopItem(buffer);
		}
		return buffer;
	}

	@Override
	public void unpackBody(IoBuffer buffer) {
	}

	private Map<String, Integer> getAllItemlimit(String style) {
		Map<String, Integer> list = new HashMap<>();
		Set<String> keys = allLimit.keySet();
		for (String key : keys) {
			String a[] = key.split("_");
			if (a[0].equals(style)) {
				list.put(key, (int)allLimit.get(key));
			}
		}
		return list;
	}

	private Map<String, Integer> getPersonalItemlimit(String style) {
		Map<String, Integer> list = new HashMap<>();
		Set<String> keys = personalLimit.keySet();
		for (String key : keys) {
			String a[] = key.split("_");
			if (a[0].equals(style)) {
				list.put(key, (int)personalLimit.get(key));
			}
		}
		return list;
	}

	private void packMallItem(IoBuffer buffer) {
		Map<String, Integer> list1 = getAllItemlimit("mall");
		Map<String, Integer> list2 = getPersonalItemlimit("mall");
		Set<String> keys = list1.keySet();
		Set<String> keys2 = list2.keySet();
		putString(buffer, storeType);
		List<String> items = new ArrayList<>();
		items = getLimitList(items, keys);
		items = getLimitList(items, keys2);
		buffer.putShort((short)items.size());
		for (String key : items) {
			PropertyDictionary mallPD = mallItemList.get(key).getProperty();
			putString(buffer, key);
			putString(buffer, MGPropertyAccesser.getStoreType(mallPD));
			putString(buffer, MGPropertyAccesser.getItemId(mallPD));
			buffer.put(MGPropertyAccesser.getItemLimitType(mallPD));
			List<Integer> nowOrigenPrice = mallItemList.get(key).getNowItemPrice().getOrigenPrice();
			buffer.putInt(nowOrigenPrice.get(1));
			buffer.putInt(nowOrigenPrice.get(2));
			if (mallItemList.get(key).getOldItemPrice() != null) {
				List<Integer> oldOrigenPrice = mallItemList.get(key).getOldItemPrice().getOrigenPrice();
				buffer.putInt(oldOrigenPrice.get(1));
				buffer.putInt(oldOrigenPrice.get(2));
			} else {
				buffer.putInt(0);
				buffer.putInt(0);
			}
			buffer.put(MGPropertyAccesser.getItemLimitType(mallPD));
			int limitType = MGPropertyAccesser.getNumber(mallPD);
			int limitNum = 0;
			if (limitType == 1) {
				limitNum = personalLimit.get(key);
			} else if (limitType == 2) {
				limitNum = allLimit.get(key);
			}
			buffer.putShort((short)limitNum);
			buffer.putShort((short)limitType);
			putString(buffer, MGPropertyAccesser.getStoreLimitTime(mallPD));
		}
	}

	private void packShopItem(IoBuffer buffer) {
		Map<String, Integer> list1 = getAllItemlimit("shop");
		Map<String, Integer> list2 = getPersonalItemlimit("shop");
		Set<String> keys = list1.keySet();
		Set<String> keys2 = list2.keySet();
		List<String> items = new ArrayList<>();
		putString(buffer, storeType);
		items = getLimitList(items, keys);
		items = getLimitList(items, keys2);
		buffer.putShort((short)items.size());
		for (String item : items) {
			PropertyDictionary shopPD = shopItemList.get(item).getProperty();
			putString(buffer, item);
			putString(buffer, MGPropertyAccesser.getStoreType(shopPD));
			putString(buffer, MGPropertyAccesser.getItemId(shopPD));
			buffer.put(MGPropertyAccesser.getItemLimitType(shopPD));
			ItemPrice ItemPrice = shopItemList.get(item).getShopPrice();
			buffer.putInt(ItemPrice.getOrigenPrice().get(1));
			buffer.putInt(ItemPrice.getOrigenPrice().get(2));
			buffer.putInt(ItemPrice.getOrigenPrice().get(0));
			buffer.put(MGPropertyAccesser.getItemLimitType(shopPD));
			int limitType = MGPropertyAccesser.getNumber(shopPD);
			int limitNum = 0;
			if (limitType == 1) {
				limitNum = personalLimit.get(item);
			} else if (limitType == 2) {
				limitNum = allLimit.get(item);
			}
			buffer.putShort((short)limitNum);
			buffer.putShort((short)limitType);
		}
	}

	private List<String> getLimitList(List<String> items, Set<String> keys) {
		for (String key : keys) {
			String a[] = key.split("_");
			if (a[0].equals("mall")) {
				if (mallItemList.containsKey(key)) {
					items.add(key);
				}
			}
			if (a[0].equals("shop")) {
				if (shopItemList.containsKey(key)) {
					items.add(key);
				}
			}
		}
		return items;
	}

	public Map<String, MallItemRef> getMallItemList() {
		return mallItemList;
	}

	public void setMallItemList(Map<String, MallItemRef> mallItemList) {
		this.mallItemList = mallItemList;
	}

	public Map<String, ShopItemRef> getShopItemList() {
		return shopItemList;
	}

	public void setShopItemList(Map<String, ShopItemRef> shopItemList) {
		this.shopItemList = shopItemList;
	}

	public Map<String, Short> getAllLimit() {
		return allLimit;
	}

	public void setAllLimit(Map<String, Short> allLimit) {
		this.allLimit = allLimit;
	}

	public Map<String, Short> getPersonLimit() {
		return personalLimit;
	}

	public void setPersonLimit(Map<String, Short> personLimit) {
		this.personalLimit = personLimit;
	}

}