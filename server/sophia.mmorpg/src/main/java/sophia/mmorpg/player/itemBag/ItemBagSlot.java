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

import sophia.mmorpg.item.Item;

public final class ItemBagSlot implements Comparable<ItemBagSlot>{
	private short index = -1;
	
	private Item item = null;
	
	public ItemBagSlot() {
		
	}
	
	public ItemBagSlot(short index) {
		this.index = index;
	}
	
	public ItemBagSlot(short index, Item item) {
		this.index = index;
		this.item = item;
	}
	
	public synchronized final void clearItem() {
		item = null;
	}
	
	public synchronized final boolean isEmpty() {
		return item == null;
	}
	
	public synchronized final boolean hasItem() {
		return item != null;
	}
	
	public synchronized final boolean isFull() {
		if (item.canStack()) {
			if (item.getNumber() == item.getMaxStackNumber())
			{
				return true;
			} else {
				return false;
			}
		} else {
			return true;
		}
	}
	
	public synchronized int getCrtStackNumber() {
		return item.getNumber();
	}
	
	public synchronized int getFreeStackNumber() {
		int freeNumber = item.getMaxStackNumber() - item.getNumber();
		
		if (freeNumber < 0) {
			freeNumber = 0;
			item.setNumber(item.getMaxStackNumber());
		}
		
		return freeNumber;
	}
	
	public synchronized void addItemNumber(int delta) {
		int number = item.getNumber();
		number += delta;
		if (number > item.getMaxStackNumber()) {
			number = item.getMaxStackNumber();
		}
		item.setNumber(number);
	}
	
	public synchronized void subItemNumber(int subtrahend) {
		int number = item.getNumber();
		number -= subtrahend;
		if (number <= 0) {
			item = null;
		} else {
			item.setNumber(number);
		}
	}

	public synchronized final short getIndex() {
		return index;
	}

	public synchronized final void setIndex(short index) {
		this.index = index;
	}

	public synchronized final Item getItem() {
		return item;
	}

	public synchronized final void setItem(Item item) {
		this.item = item;
	}

	@Override
	public int compareTo(ItemBagSlot other) {
		boolean thisIsEmpty = this.isEmpty();
		boolean otherIsEmpty = other.isEmpty();
		if (thisIsEmpty && otherIsEmpty) {
			return this.getIndex() > other.getIndex() ? 1 : -1;
		}
		
		if (thisIsEmpty && (!otherIsEmpty)) {
			return 1;
		}
		
		if ((!thisIsEmpty) && otherIsEmpty) {
			return -1;
		}
		
		Item thisItem = this.getItem();
		Item otherItem = other.getItem();
		if (!thisItem.equalItemRef(otherItem)) {
			return thisItem.getItemRef().compareTo(otherItem.getItemRef());
		}
		
		if (thisItem.binded() != otherItem.binded()) {
			return thisItem.binded() ? -1 : 1;
		}
		if(this.getIndex() > other.getIndex())
			return 1;
		else
			return -1;
//		if (thisItem.isNonPropertyItem()) {
//			int thisItemNumber = thisItem.getNumber();
//			int otherItemNumber = otherItem.getNumber();
//			if (thisItemNumber < otherItemNumber) {
//				return 1;
//			} else if (thisItemNumber > otherItemNumber) {
//				return -1;
//			} else {
//				return 0;
//			}
//		}
//		
//		return 0;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + index;
		result = prime * result + ((item == null) ? 0 : item.hashCode());
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
		ItemBagSlot other = (ItemBagSlot) obj;
		if (index != other.index)
			return false;
		if (item == null) {
			if (other.item != null)
				return false;
		} else if (!item.equals(other.item))
			return false;
		return true;
	}

	public synchronized int mergeEqualItemFrom(ItemBagSlot from) {
		int ret = 0;
		
		int crtStackNumberOfFrom = from.getCrtStackNumber();
		
		int freeStackNumberOfThis = getFreeStackNumber();
		
		if (crtStackNumberOfFrom >= freeStackNumberOfThis) {
			ret = freeStackNumberOfThis;
		} else {
			ret = crtStackNumberOfFrom;
		}
		
		this.addItemNumber(ret);
		
		from.subItemNumber(ret);
		
		return ret;
	}
}
