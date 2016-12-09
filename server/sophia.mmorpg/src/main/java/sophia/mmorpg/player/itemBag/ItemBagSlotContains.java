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

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Preconditions;

public final class ItemBagSlotContains {
	private static final short Default_Max_Slot_Number = 100;
	
	private  short Max_Slot_Number = Default_Max_Slot_Number;
	
	private List<ItemBagSlot> slotList;
	
	private final List<ItemBagSlot> tempSlotList = new ArrayList<>();
	
	public ItemBagSlotContains() {
		slotList = new ArrayList<>(0);
	}
	
	public ItemBagSlotContains(final List<ItemBagSlot> slotList) {
		this.slotList = new ArrayList<>(slotList);
	}

	public final List<ItemBagSlot> getSlotList() {
		return slotList;
	}

	public synchronized final void setSlotList(List<ItemBagSlot> slotList) {
		this.slotList = slotList;
	}

	public synchronized final int getNumberOfSlot() {
		return slotList.size();
	}

	/**
	 * 获取指定位置索引的背包格
	 * @param index 指定的位置索引
	 * @return
	 */
	public synchronized ItemBagSlot getItemBagSlot(int index) {
		Preconditions.checkPositionIndex(0, slotList.size());
		
		return getSlotList().get(index);
	}
	
	public synchronized final int getEmptySlotNumber() {
		int ret = 0;
		
		for(ItemBagSlot slot : slotList) {
			if (slot.isEmpty()) {
				ret += 1;
			}
		}
		
		return ret;
	}
	
	public synchronized ItemBagSlot getEmptyItemBagSlot() {
		for(ItemBagSlot slot : slotList) {
			if (slot.isEmpty()) {
				return slot;
			}
		}
		
		throw new RuntimeException("背包已經滿，没有空格。");
	}
	
	public synchronized final List<ItemBagSlot> getTempSlotList() {
		tempSlotList.clear();
		return tempSlotList;
	}

	public synchronized final boolean isEmpty() {
		return getNumberOfSlot() == getEmptySlotNumber();
	}
	
	public synchronized final boolean isFull() {
		return getEmptySlotNumber() == 0;
	}

	public  final short getMax_Slot_Number() {
		return Max_Slot_Number;
	}

	public synchronized  final void setMax_Slot_Number(short max_Slot_Number) {
		Max_Slot_Number = max_Slot_Number;
	}

	public static final short getDefaultMaxSlotNumber() {
		return Default_Max_Slot_Number;
	}
}
