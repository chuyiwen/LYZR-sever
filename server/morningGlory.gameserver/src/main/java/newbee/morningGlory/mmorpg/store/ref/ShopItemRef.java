package newbee.morningGlory.mmorpg.store.ref;

import newbee.morningGlory.mmorpg.store.ItemPrice;
import sophia.game.ref.AbstractGameRefObjectBase;

public class ShopItemRef extends AbstractGameRefObjectBase {
	private String refId;

	private static final long serialVersionUID = 7872800684597531885L;
	
	private ItemPrice shopPrice;

	public ItemPrice getShopPrice() {
		return shopPrice;
	}

	public void setShopPrice(ItemPrice shopPrice) {
		this.shopPrice = shopPrice;
	}

	public String getRefId() {
		return refId;
	}

	public void setRefId(String refId) {
		this.refId = refId;
	}

}
