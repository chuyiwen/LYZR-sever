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
package sophia.mmorpg.player.money.gameEvent;

import sophia.mmorpg.player.itemBag.ItemOptSource;

public class ConsumeUnBindedGold_GE {
	private int unBindedGold;
	private int bindedGold;
	private byte source;
	public ConsumeUnBindedGold_GE() {
		// TODO Auto-generated constructor stub
	}

	public ConsumeUnBindedGold_GE(int unBindedGold, int bindedGold,byte source) {
		this.unBindedGold = unBindedGold;
		this.bindedGold = bindedGold;
		this.source = source;
	}

	public int getUnBindedGold() {
		return unBindedGold;
	}

	public void setUnBindedGold(int unBindedGold) {
		this.unBindedGold = unBindedGold;
	}

	public int getBindedGold() {
		return bindedGold;
	}

	public void setBindedGold(int bindedGold) {
		this.bindedGold = bindedGold;
	}

	public static boolean isConsume(byte source) {
		return source == ItemOptSource.Store || source == ItemOptSource.Digs || source == ItemOptSource.FundActivity || source == ItemOptSource.QiangHua
				|| source == ItemOptSource.Ladder || source == ItemOptSource.Sign;
	}

	public byte getSource() {
		return source;
	}

	public void setSource(byte source) {
		this.source = source;
	}
}
