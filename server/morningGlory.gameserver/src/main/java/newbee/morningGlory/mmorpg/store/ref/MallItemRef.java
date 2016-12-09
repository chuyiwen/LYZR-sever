package newbee.morningGlory.mmorpg.store.ref;

import newbee.morningGlory.mmorpg.store.ItemPrice;
import sophia.game.ref.AbstractGameRefObjectBase;

public class MallItemRef extends AbstractGameRefObjectBase {

	private String refId;
	
	private ItemPrice nowItemPrice;

	private ItemPrice oldItemPrice;

	private static final long serialVersionUID = 1309891622845566885L;

	public ItemPrice getNowItemPrice() {
		return nowItemPrice;
	}

	public void setNowItemPrice(ItemPrice nowItemPrice) {
		this.nowItemPrice = nowItemPrice;
	}

	public ItemPrice getOldItemPrice() {
		return oldItemPrice;
	}

	public void setOldItemPrice(ItemPrice oldItemPrice) {
		this.oldItemPrice = oldItemPrice;
	}

	public String getRefId() {
		return refId;
	}

	public void setRefId(String refId) {
		this.refId = refId;
	}

}
