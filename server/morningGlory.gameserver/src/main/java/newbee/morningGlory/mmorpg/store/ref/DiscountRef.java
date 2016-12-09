package newbee.morningGlory.mmorpg.store.ref;

import java.util.HashMap;
import java.util.Map;

import sophia.game.ref.AbstractGameRefObjectBase;

public class DiscountRef extends AbstractGameRefObjectBase {
	private static final long serialVersionUID = 9091808434958931840L;

	public static final String discount_item = "discount_item";

	// <batch, <discountRefId,DiscountItemRef>> 
	private Map<Short, Map<String, DiscountItemRef>> discountItemRefMap = new HashMap<Short, Map<String, DiscountItemRef>>();

	public void putDiscountItemRefData(short batch, String discountRefId, DiscountItemRef discountItemRef){
		Map<String, DiscountItemRef> map = discountItemRefMap.get(batch);
		if(map == null){
			map = new HashMap<String, DiscountItemRef>();
			map.put(discountRefId, discountItemRef);
			discountItemRefMap.put(batch, map);
		} else {
			map.put(discountRefId, discountItemRef);
		}
	}
	
	public Map<Short, Map<String, DiscountItemRef>> getDiscountItemRefMap() {
		return discountItemRefMap;
	}

	public void setDiscountItemRefMap(Map<Short, Map<String, DiscountItemRef>> discountItemRefMap) {
		this.discountItemRefMap = discountItemRefMap;
	}
}
