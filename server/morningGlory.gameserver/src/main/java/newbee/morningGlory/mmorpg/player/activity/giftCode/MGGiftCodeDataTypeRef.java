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
package newbee.morningGlory.mmorpg.player.activity.giftCode;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import sophia.game.ref.AbstractGameRefObjectBase;
import sophia.mmorpg.player.itemBag.ItemPair;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;

public class MGGiftCodeDataTypeRef extends AbstractGameRefObjectBase {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3750044940163649939L;

	private List<ItemPair> rewards = new ArrayList<ItemPair>();

	private Set<String> giftCodeSingleSet = new HashSet<String>();

	private String doubleGiftKeyCode = null;
	private long openTime;
	private long expiredTime;

	public long getOpenTime() {
		return openTime;
	}

	public void setOpenTime(long openTime) {
		this.openTime = openTime;
	}

	public long getExpiredTime() {
		return expiredTime;
	}

	public void setExpiredTime(long expiredTime) {
		this.expiredTime = expiredTime;
	}

	public List<ItemPair> getRewards() {
		return rewards;
	}

	public void setRewards(List<ItemPair> rewards) {
		this.rewards = rewards;
	}

	public Set<String> getGiftCodeSingleSet() {
		return giftCodeSingleSet;
	}

	public void setGiftCodeSingleSet(Set<String> giftCodeSingleSet) {
		this.giftCodeSingleSet = giftCodeSingleSet;
	}

	public String getDoubleGiftKeyCode() {
		return doubleGiftKeyCode;
	}

	public void setDoubleGiftKeyCode(String doubleGiftKeyCode) {
		this.doubleGiftKeyCode = doubleGiftKeyCode;
	}

	public byte getCodeType() {
		return MGPropertyAccesser.getItemType(getProperty());
	}

	@Override
	public String toString() {
		return "MGGiftCodeDataTypeRef [rewards=" + rewards + ", id=" + id + ", property=" + property + "]";
	}

}
