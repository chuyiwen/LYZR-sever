package newbee.morningGlory.mmorpg.store.ref;

import sophia.game.ref.AbstractGameRefObjectBase;

/**
 * 打折道具映射
 * 
 * @author Administrator
 * 
 */
public class DiscountItemRef extends AbstractGameRefObjectBase {
	private static final long serialVersionUID = 9091808434958931840L;

	private int originalsaleCurrency;
	private int originalsalePrice;
	private String itemRefId;
	private short itemLimitNum;
	private short personalLimitNum;
	private int newSaleCurrency;
	private int newSalePrice;
	private byte bindStatus;

	public DiscountItemRef() {

	}

	public int getOriginalsaleCurrency() {
		return originalsaleCurrency;
	}

	public void setOriginalsaleCurrency(int originalsaleCurrency) {
		this.originalsaleCurrency = originalsaleCurrency;
	}

	public int getOriginalsalePrice() {
		return originalsalePrice;
	}

	public void setOriginalsalePrice(int originalsalePrice) {
		this.originalsalePrice = originalsalePrice;
	}

	public String getItemRefId() {
		return itemRefId;
	}

	public void setItemRefId(String itemRefId) {
		this.itemRefId = itemRefId;
	}

	public short getItemLimitNum() {
		return itemLimitNum;
	}

	public void setItemLimitNum(short itemLimitNum) {
		this.itemLimitNum = itemLimitNum;
	}

	public short getPersonalLimitNum() {
		return personalLimitNum;
	}

	public void setPersonalLimitNum(short personalLimitNum) {
		this.personalLimitNum = personalLimitNum;
	}

	public int getNewSaleCurrency() {
		return newSaleCurrency;
	}

	public void setNewSaleCurrency(int newSaleCurrency) {
		this.newSaleCurrency = newSaleCurrency;
	}

	public int getNewSalePrice() {
		return newSalePrice;
	}

	public void setNewSalePrice(int newSalePrice) {
		this.newSalePrice = newSalePrice;
	}

	public byte getBindStatus() {
		return bindStatus;
	}

	public void setBindStatus(byte bindStatus) {
		this.bindStatus = bindStatus;
	}
}
