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
package newbee.morningGlory.mmorpg.operatActivities;

import newbee.morningGlory.mmorpg.operatActivities.impl.EveryDayRechargeGift;
import newbee.morningGlory.mmorpg.operatActivities.impl.FirstRechargeGift;
import newbee.morningGlory.mmorpg.operatActivities.impl.SevenDayLoginGift;
import newbee.morningGlory.mmorpg.operatActivities.impl.TotalRechargeGift;
import newbee.morningGlory.mmorpg.operatActivities.impl.WeekTotalConsumeGift;

public enum OperatActivityType {

	/** 首冲礼包 */
	FirstRechargeGift(1, FirstRechargeGift.class),
	/** 累计充值 */
	TotalRechargeGift(2,TotalRechargeGift.class),
	/** 日充值 */
	EveryDayRechargeGift(3,EveryDayRechargeGift.class),
	/** 周累计消费 */
	WeekTotalConsumeGift(4,WeekTotalConsumeGift.class),
	/** 开服七日登录 */
	SevenDayLoginGift(5,SevenDayLoginGift.class);
	
	private OperatActivityType(int value, Class<? extends OperatActivity> clazz) {
		this.value = value;
		this.clazz = clazz;
	}

	private int value;
	private Class<? extends OperatActivity> clazz;

	public int getValue() {
		return value;
	}

	public Class<? extends OperatActivity> getClazz() {
		return clazz;
	}

	public void setClazz(Class<? extends OperatActivity> clazz) {
		this.clazz = clazz;
	}

	public static OperatActivityType get(int type) {
		for (OperatActivityType t : values()) {
			if (t.getValue() == type)
				return t;
		}
		return null;
	}

}
