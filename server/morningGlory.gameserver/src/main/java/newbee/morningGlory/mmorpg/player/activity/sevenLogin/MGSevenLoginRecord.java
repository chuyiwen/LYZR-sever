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
package newbee.morningGlory.mmorpg.player.activity.sevenLogin;

import java.util.HashMap;
import java.util.Map;

public class MGSevenLoginRecord {

	public static final int DurationDay = 7;
	private Map<Integer, Byte> sevenLoginMaps = new HashMap<>(7);
	private long openServerDate = 0;

	public MGSevenLoginRecord() {
		init();
	}

	public void init() {
		for (int i = 1; i <= DurationDay; i++) {
			sevenLoginMaps.put(i, MGSevenLoginRewardType.CantReceive);
		}

		this.setOpenServerDate(System.currentTimeMillis() - 3600 * 1000 * 24 * 1l);
	}

	public long getOpenServerDate() {
		return openServerDate;
	}

	public void setOpenServerDate(long openServerDate) {
		this.openServerDate = openServerDate;
	}

	public Map<Integer, Byte> getSevenLoginMaps() {
		return sevenLoginMaps;
	}

	public void setSevenLoginMaps(Map<Integer, Byte> sevenLoginMaps) {
		this.sevenLoginMaps = sevenLoginMaps;
	}

	public boolean isHadReceiveThisDay(int whichDay) {
		return this.getSevenLoginMaps().get(whichDay) == MGSevenLoginRewardType.HadReceive || this.getSevenLoginMaps().get(whichDay) == MGSevenLoginRewardType.CantReceive;
	}

	public void updateRewardStatus(int whichDay, byte status) {
		if (this.getSevenLoginMaps().containsKey(whichDay)) {
			this.getSevenLoginMaps().put(whichDay, status);
		}
	}
}
