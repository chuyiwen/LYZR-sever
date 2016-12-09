package newbee.morningGlory.mmorpg.player.activity.fund.ref;

import sophia.game.ref.AbstractGameRefObjectBase;
import sophia.mmorpg.player.itemBag.ItemPair;

/**
 * 
 * @author lixing
 * 
 */
public class FundRef extends AbstractGameRefObjectBase {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6671501338411736517L;
	private ItemPair[] giftArrays;// 领取的礼包集合
	private byte moneyType;// 货币类型
	private int buyPrice;// 购买价格

	public FundRef() {}

	public ItemPair[] getGiftArrays() {
		return giftArrays;
	}

	public ItemPair getGift(int dayIndex) {
		return giftArrays[dayIndex];
	}

	public void setGiftArrays(ItemPair[] giftArrays) {
		this.giftArrays = giftArrays;
	}

	public byte getMoneyType() {
		return moneyType;
	}

	public void setMoneyType(byte moneyType) {
		this.moneyType = moneyType;
	}

	public int getBuyPrice() {
		return buyPrice;
	}

	public void setBuyPrice(int buyPrice) {
		this.buyPrice = buyPrice;
	}

}
