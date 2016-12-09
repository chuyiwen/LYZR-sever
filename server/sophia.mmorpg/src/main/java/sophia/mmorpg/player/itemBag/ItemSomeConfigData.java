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

import java.util.HashMap;
import java.util.Map;

public final class ItemSomeConfigData {
	public static final Map<Integer,Integer> openSlotTimeMap = new HashMap<>(50);
	public static final Map<Integer,ItemLuckDataPair> itemLuckDataMap = new HashMap<>(7);
	static{
		openSlotTimeMap.put(1, 360);
		openSlotTimeMap.put(2, 1080);
		openSlotTimeMap.put(3, 2160);
		openSlotTimeMap.put(4, 3600);
		openSlotTimeMap.put(5,5400);
		openSlotTimeMap.put(6, 7560);
		openSlotTimeMap.put(7, 10080);
		openSlotTimeMap.put(8, 12960);
		openSlotTimeMap.put(9, 16200);
		openSlotTimeMap.put(10, 19800);
		openSlotTimeMap.put(11, 23760);
		openSlotTimeMap.put(12, 28080);
		openSlotTimeMap.put(13, 32760);
		openSlotTimeMap.put(14, 37800);
		openSlotTimeMap.put(15, 43200);
		openSlotTimeMap.put(16, 48960);
		openSlotTimeMap.put(17, 55080);
		openSlotTimeMap.put(18, 61560);
		openSlotTimeMap.put(19, 68400);
		openSlotTimeMap.put(20, 75600);
		openSlotTimeMap.put(21, 83160);
		openSlotTimeMap.put(22, 91080);
		openSlotTimeMap.put(23, 99360);
		openSlotTimeMap.put(24, 108000);
		openSlotTimeMap.put(25, 117000);
		openSlotTimeMap.put(26, 126360);
		openSlotTimeMap.put(27, 136080);
		openSlotTimeMap.put(28, 146160);
		openSlotTimeMap.put(29, 156600);
		openSlotTimeMap.put(30, 167400);
		openSlotTimeMap.put(31, 178560);
		openSlotTimeMap.put(32, 190080);
		openSlotTimeMap.put(33, 201960);
		openSlotTimeMap.put(34, 214200);
		openSlotTimeMap.put(35, 226800);
		openSlotTimeMap.put(36, 239760);
		openSlotTimeMap.put(37, 253080);
		openSlotTimeMap.put(38, 266760);
		openSlotTimeMap.put(39, 280800);
		openSlotTimeMap.put(40, 295200);
		openSlotTimeMap.put(41, 309960);
		openSlotTimeMap.put(42, 325080);
		openSlotTimeMap.put(43, 340560);
		openSlotTimeMap.put(44, 356400);
		openSlotTimeMap.put(45, 372600);
		openSlotTimeMap.put(46, 389160);
		openSlotTimeMap.put(47, 406080);
		openSlotTimeMap.put(48, 423360);
		openSlotTimeMap.put(49, 441000);
		openSlotTimeMap.put(50, 459000);
	
		itemLuckDataMap.put(0, new ItemLuckDataPair(0, 80, 20, 0));
		itemLuckDataMap.put(1, new ItemLuckDataPair(1, 40, 20, 40));
		itemLuckDataMap.put(2, new ItemLuckDataPair(2, 30, 20, 50));
		itemLuckDataMap.put(3, new ItemLuckDataPair(3, 20, 20, 60));
		itemLuckDataMap.put(4, new ItemLuckDataPair(4, 15, 20, 65));
		itemLuckDataMap.put(5, new ItemLuckDataPair(5, 10, 20, 70));
		itemLuckDataMap.put(6, new ItemLuckDataPair(6, 5,  20, 75));
	}
	
}
