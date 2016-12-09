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
package newbee.morningGlory.mmorpg.player.gameInstance;

public class MGPlayerGameInstanceRecord {
	private String gameInstanceRefId;

	private CountRecord countRecord;// 次数相关记录

	public MGPlayerGameInstanceRecord(String gameInstanceRefId) {
		this.gameInstanceRefId = gameInstanceRefId;
	}

	public void setCountRecord(CountRecord countRecord) {
		this.countRecord = countRecord;
	}

	public String getGameInstanceRefId() {
		return gameInstanceRefId;
	}

	public CountRecord getCountRecord() {
		return countRecord;
	}

	public void reset() {
		if (countRecord != null) {
			countRecord.clearTimesInDay();
			countRecord.clearTimesInThisWeek();
		}

	}
	
	public void resetDayRecord() {
		if(countRecord != null) {
			countRecord.clearTimesInDay();
		}
	}

}
