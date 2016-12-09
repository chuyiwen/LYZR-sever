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

import java.util.TreeSet;

public final class ItemBagPartialRenewalData {
	private boolean isSucceed = false;
	
	private TreeSet<ItemBagSlot> partialRenewalIndexs = new TreeSet<ItemBagSlot>();
	private int code;
	
	public ItemBagPartialRenewalData() {
		
	}
	
	public final void addItemBagSlot(ItemBagSlot itemBagSlot) {
		partialRenewalIndexs.add(itemBagSlot);
	}
	
	public final boolean isSucceed() {
		return isSucceed;
	}

	public final void setSucceed(boolean isSucceed) {
		this.isSucceed = isSucceed;
	}

	public final TreeSet<ItemBagSlot> getPartialRenewalIndexs() {
		return partialRenewalIndexs;
	}

	public final void setPartialRenewalIndexs(TreeSet<ItemBagSlot> partialRenewalIndexs) {
		this.partialRenewalIndexs = partialRenewalIndexs;
	}

	public final int getCode() {
		return code;
	}

	public final void setCode(int code) {
		this.code = code;
	}
	
	

	public void reset() {
		isSucceed = false;
		partialRenewalIndexs.clear();
		code = 0;
	}
	
	@Override
	public String toString() {
		return "ItemBagPartialRenewalData [isSucceed=" + isSucceed
				+ ", partialRenewalIndexs=" + partialRenewalIndexs + ", code="
				+ code + "]";
	}
}
