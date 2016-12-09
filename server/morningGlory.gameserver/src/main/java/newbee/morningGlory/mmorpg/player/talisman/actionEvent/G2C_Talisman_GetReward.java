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
package newbee.morningGlory.mmorpg.player.talisman.actionEvent;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class G2C_Talisman_GetReward extends ActionEventBase {
	private int totalBaoXiang;
	private int baoXiang;
	private int totalGold;
	private int gold;
	private int totalExp;
	private int exp;
	private int totalStone;
	private int stone;
	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		buffer.putInt(totalBaoXiang);
		buffer.putInt(baoXiang);
		buffer.putInt(totalGold);
		buffer.putInt(gold);
		buffer.putInt(totalStone);
		buffer.putInt(stone);
		buffer.putInt(totalExp);
		buffer.putInt(exp);		
		return buffer;
	}

	@Override
	public void unpackBody(IoBuffer buffer) {
		
	}

	public int getTotalBaoXiang() {
		return totalBaoXiang;
	}

	public void setTotalBaoXiang(int totalBaoXiang) {
		this.totalBaoXiang = totalBaoXiang;
	}

	public int getBaoXiang() {
		return baoXiang;
	}

	public void setBaoXiang(int baoXiang) {
		this.baoXiang = baoXiang;
	}

	public int getTotalGold() {
		return totalGold;
	}

	public void setTotalGold(int totalGold) {
		this.totalGold = totalGold;
	}

	public int getGold() {
		return gold;
	}

	public void setGold(int gold) {
		this.gold = gold;
	}

	public int getTotalExp() {
		return totalExp;
	}

	public void setTotalExp(int totalExp) {
		this.totalExp = totalExp;
	}

	public int getExp() {
		return exp;
	}

	public void setExp(int exp) {
		this.exp = exp;
	}

	public int getTotalStone() {
		return totalStone;
	}

	public void setTotalStone(int totalStone) {
		this.totalStone = totalStone;
	}

	public int getStone() {
		return stone;
	}

	public void setStone(int stone) {
		this.stone = stone;
	}
	
	
}
