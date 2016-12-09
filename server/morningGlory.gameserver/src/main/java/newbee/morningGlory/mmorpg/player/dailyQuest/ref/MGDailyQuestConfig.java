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
package newbee.morningGlory.mmorpg.player.dailyQuest.ref;

public final class MGDailyQuestConfig {
	public static final int Default_Max_DailyQuest_Number = 10;
	
	public static final int Default_Max_DailyQuest_Level = 10;
	
	public static final int[] Default_DailyQuest_LevelExp_Rate = {10,20,30,40,50,60,70,80,90,100};
	
	public static final int Default_RandomLevel_ConsumMoney = 20;
	
	public static final int DoubleExp_ConsumMoney = 20;
	
	private int maxDailyQuestNumber = Default_Max_DailyQuest_Number;
	
	private int maxDailyQuestLevel = 10;
	
	private int[] levelExpRate = Default_DailyQuest_LevelExp_Rate;
	
	private int randomLevelConsumMoney = Default_RandomLevel_ConsumMoney;
	
	private int doubleExpConsumMoney = DoubleExp_ConsumMoney;
	
	public MGDailyQuestConfig() {
		
	}

	public int getMaxDailyQuestNumber() {
		return maxDailyQuestNumber;
	}

	public void setMaxDailyQuestNumber(int maxDailyQuestNumber) {
		this.maxDailyQuestNumber = maxDailyQuestNumber;
	}

	public int getMaxDailyQuestLevel() {
		return maxDailyQuestLevel;
	}

	public void setMaxDailyQuestLevel(int maxDailyQuestLevel) {
		this.maxDailyQuestLevel = maxDailyQuestLevel;
	}

	public int[] getLevelExpRate() {
		return levelExpRate;
	}

	public void setLevelExpRate(int[] levelExpRate) {
		this.levelExpRate = levelExpRate;
	}

	public int getRandomLevelConsumMoney() {
		return randomLevelConsumMoney;
	}

	public void setRandomLevelConsumMoney(int randomLevelConsumMoney) {
		this.randomLevelConsumMoney = randomLevelConsumMoney;
	}

	public int getDoubleExpConsumMoney() {
		return doubleExpConsumMoney;
	}

	public void setDoubleExpConsumMoney(int doubleExpConsumMoney) {
		this.doubleExpConsumMoney = doubleExpConsumMoney;
	}
}
