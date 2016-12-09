/**
 *   Copyright 2013-2015 Sophia
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package sophia.mmorpg.player.itemBag;

import org.apache.commons.lang3.StringUtils;

import sophia.mmorpg.GameObjectFactory;
import sophia.mmorpg.item.Item;
import sophia.mmorpg.player.property.PlayerConfig;

public final class ItemPair {

	public static final byte DEFAULT_PROFESSION = 0;
	public static final byte WARRIOR_MALE_PROFESSION = 1;
	public static final byte WARRIOR_FAMALE_PROFESSION = 2;
	public static final byte ENCHANTER_MALE_PROFESSION = 3;
	public static final byte ENCHANTER_FAMALE_PROFESSION = 4;
	public static final byte WARLOCK_MALE_PROFESSION = 5;
	public static final byte WARLOCK_FAMALE_PROFESSION = 6;

	private String itemRefId;
	private int number;
	private boolean bindStatus;
	private byte professsionAndGender = DEFAULT_PROFESSION;

	public ItemPair() {

	}

	/**
	 * 
	 * @param itemRefId
	 *            物品refId
	 * @param number
	 *            物品数量
	 * @param bindStatus
	 *            true = 绑定 false = 不绑定
	 */
	public ItemPair(String itemRefId, int number, boolean bindStatus) {
		this.itemRefId = itemRefId;
		this.number = number;
		this.bindStatus = bindStatus;
	}

	/**
	 * 
	 * @param itemRefId
	 *            物品refId
	 * @param number
	 *            物品数量
	 * @param bindStatus
	 *            0 = 不绑定 1 = 绑定
	 */

	public ItemPair(String itemRefId, int number, byte bindStatus) {
		this.itemRefId = itemRefId;
		this.number = number;
		if (bindStatus == Item.At_Once_Bind) {
			this.bindStatus = true;
		} else {
			this.bindStatus = false;
		}
	}

	public ItemPair(String itemRefId, int number, boolean bindStatus, byte professsionAndGender) {
		this.itemRefId = itemRefId;
		this.number = number;
		this.bindStatus = bindStatus;
		this.professsionAndGender = professsionAndGender;
	}

	public ItemPair(String itemRefId, int number, byte bindStatus, byte professsionAndGender) {
		this.itemRefId = itemRefId;
		this.number = number;
		if (bindStatus == Item.At_Once_Bind) {
			this.bindStatus = true;
		} else {
			this.bindStatus = false;
		}
		this.professsionAndGender = professsionAndGender;
	}

	public static byte getProfessionId(byte gender, byte professionId) {
		if (gender == 1) { // 性别为男
			if (professionId == PlayerConfig.WARRIOR) {
				return WARRIOR_MALE_PROFESSION;
			} else if (professionId == PlayerConfig.ENCHANTER) {
				return ENCHANTER_MALE_PROFESSION;
			} else if (professionId == PlayerConfig.WARLOCK) {
				return WARLOCK_MALE_PROFESSION;
			}
		} else {
			if (professionId == PlayerConfig.WARRIOR) {
				return WARRIOR_FAMALE_PROFESSION;
			} else if (professionId == PlayerConfig.ENCHANTER) {
				return ENCHANTER_FAMALE_PROFESSION;
			} else if (professionId == PlayerConfig.WARLOCK) {
				return WARLOCK_FAMALE_PROFESSION;
			}
		}
		return DEFAULT_PROFESSION;
	}

	public Item convertItem() {
		Item item = GameObjectFactory.getItem(getItemRefId());
		if (isBindStatus()) {
			item.setBindStatus((byte) 1);
		}
		item.setNumber(getNumber());
		return item;
	}

	public String getItemRefId() {
		return itemRefId;
	}

	public void setItemRefId(String itemRefId) {
		this.itemRefId = itemRefId;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public boolean isBindStatus() {
		return bindStatus;
	}

	/**
	 * @param bindStatus
	 *            0 = 不绑定 1 = 绑定
	 */
	public void setBindStatus(boolean bindStatus) {
		this.bindStatus = bindStatus;
	}

	/**
	 * 是绑定元宝
	 * 
	 * @return
	 */
	public boolean isBindedGold() {
		return StringUtils.equals(itemRefId, ItemCode.BindedGold_ID);
	}

	/**
	 * 是元宝
	 * 
	 * @return
	 */
	public boolean isUnBindedGold() {
		return StringUtils.equals(itemRefId, ItemCode.UnBindedGold_ID);
	}

	/**
	 * 是金币
	 * 
	 * @return
	 */
	public boolean isGold() {
		return StringUtils.equals(itemRefId, ItemCode.Gold_ID);
	}

	/**
	 * 经验值
	 * 
	 * @return
	 */
	public boolean isExp() {
		return StringUtils.equals(itemRefId, ItemCode.Exp_ID);
	}

	/**
	 * 是功勋
	 * 
	 * @return
	 */
	public boolean isMerit() {
		return StringUtils.equals(itemRefId, ItemCode.Merit_ID);
	}

	/**
	 * 是成就点
	 * 
	 * @return
	 */
	public boolean isAchievement() {
		return StringUtils.equals(itemRefId, ItemCode.Achievement_ID);
	}

	/**
	 * 是属性物品
	 * 
	 * @return
	 */
	public boolean isUnPropsItem() {
		return isBindedGold() || isUnBindedGold() || isGold() || isGold() || isExp() || isMerit() || isAchievement();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (bindStatus ? 1231 : 1237);
		result = prime * result + ((itemRefId == null) ? 0 : itemRefId.hashCode());
		result = prime * result + number;
		result = prime * result + professsionAndGender;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ItemPair other = (ItemPair) obj;
		if (bindStatus != other.bindStatus)
			return false;
		if (itemRefId == null) {
			if (other.itemRefId != null)
				return false;
		} else if (!itemRefId.equals(other.itemRefId))
			return false;
		if (number != other.number)
			return false;
		if (professsionAndGender != other.professsionAndGender)
			return false;
		return true;
	}

	public byte getProfesssionAndGender() {
		return professsionAndGender;
	}

	public void setProfesssionAndGender(byte professsionAndGender) {
		this.professsionAndGender = professsionAndGender;
	}
}
