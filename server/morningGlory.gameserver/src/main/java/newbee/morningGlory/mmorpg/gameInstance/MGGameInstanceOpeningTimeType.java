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
package newbee.morningGlory.mmorpg.gameInstance;

/**
 * 副本开放时间类型
 */
public final class MGGameInstanceOpeningTimeType {
	/** 将来某个时间开放*/
	public static final byte Future_OpeningTime = 1;
	/** 每日固定时间开放*/
	public static final byte Daily_OpeningTime = 2;
	/** 每周固定时间开放*/
	public static final byte Weekly_OpeningTime = 3;
	/** 没有时间限制*/
	public static final byte No_OpeningTime = 4;
	
	private MGGameInstanceOpeningTimeType() {
		
	}
}
