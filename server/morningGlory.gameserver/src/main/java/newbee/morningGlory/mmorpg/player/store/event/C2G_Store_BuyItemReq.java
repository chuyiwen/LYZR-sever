package newbee.morningGlory.mmorpg.player.store.event;

import newbee.morningGlory.mmorpg.store.StoreMgr;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class C2G_Store_BuyItemReq extends ActionEventBase {
	private String storeId;
	private String itemRefId;
	private int count;
	private String npcRefId;
	private String shopId;

	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		// TODO Auto-generated method stub
		return buffer;
	}

	@Override
	public void unpackBody(IoBuffer buffer) {
		storeId = getString(buffer);
		byte storeType = StoreMgr.getStoreType(storeId);
		if (storeType == StoreMgr.Mall_type) {
			itemRefId = getString(buffer);
			count = buffer.getInt();
		} else if (storeType == StoreMgr.Shop_type) {
			npcRefId = getString(buffer);
			shopId = storeId;
			itemRefId = getString(buffer);
			count = buffer.getInt();
		} else if (storeType == StoreMgr.Discount_type) {
			itemRefId = getString(buffer);
			count = buffer.getInt();
		}
	}

	public String getStoreId() {
		return storeId;
	}

	public void setStoreId(String storeType) {
		this.storeId = storeType;
	}

	public String getItemRefId() {
		return itemRefId;
	}

	public void setItemRefId(String itemRefId) {
		this.itemRefId = itemRefId;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public String getNpcRefId() {
		return npcRefId;
	}

	public void setNpcRefId(String npcRefId) {
		this.npcRefId = npcRefId;
	}

	public String getShopId() {
		return shopId;
	}

	public void setShopId(String shopId) {
		this.shopId = shopId;
	}

}