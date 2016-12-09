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
package newbee.morningGlory.mmorpg.vip.lottery;

import java.util.Map.Entry;

import newbee.morningGlory.mmorpg.vip.lottery.ref.MGLotteryRewardDataRef;
import newbee.morningGlory.mmorpg.vip.lottery.ref.MGVipLotteryDataConfig;
import sophia.game.GameRoot;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;
import sophia.mmorpg.utils.SFRandomUtils;

public class MGVipLotteryMgr {
	MGVipLotteryRecord lotteryRecord = new MGVipLotteryRecord();

	public MGVipLotteryRecord getLotteryRecord() {
		return lotteryRecord;
	}

	public void setLotteryRecord(MGVipLotteryRecord lotteryRecord) {
		this.lotteryRecord = lotteryRecord;
	}

	public MGVipLotteryMgr() {
	}

	/**
	 * 设置VIP抽奖列表
	 */
	public void setLotteryList() {
		
		
		MGVipLotteryDataConfig config = (MGVipLotteryDataConfig) GameRoot.getGameRefObjectManager().getManagedObject(MGVipLotteryDataConfig.LotteryReward_Id);
		if (config == null) {
			return;
		}
		byte max = 0;
		for (Entry<String, MGLotteryRewardDataRef> entry : config.getLotteryRewardMaps().entrySet()) {
			MGLotteryRewardDataRef ref = entry.getValue();
			if(ref.getItemGroup() > max){
				max = ref.getItemGroup();
			}
		}
		int itemGroup = SFRandomUtils.random(1, max);
		if(itemGroup<=0 || itemGroup > max){
			itemGroup = 1;
		}
		int j = 0;
		int index[] = getRandomIndex();
		for (Entry<String, MGLotteryRewardDataRef> entry : config.getLotteryRewardMaps().entrySet()) {
			MGLotteryRewardDataRef ref = entry.getValue();
			if(j > 7){
				break;
			}
			if (ref.getItemGroup() == itemGroup) {
				this.getLotteryRecord().getRewardMaps().put((byte)index[j], ref);
				j++;
			}
		}
	}
	/**
	 * 生成一组1-8之间的随机数
	 * @return
	 */
	public int[] getRandomIndex() {
		int[] a = new int[8];
		for (int i = 0; i < 8; i++) {
			a[i] = i + 1;
		}
		for (int j = 0; j < 8; j++) {
			int  t = SFRandomUtils.random(0, 7);
			int temp = 0;
			temp = a[j];
			a[j] = a[t];
			a[t] = temp;
		}

		return a;
	}
	
	/**
	 * 计算中奖格子索引
	 * 
	 * @return
	 */
	public byte getLotteryIndex() {
		long[] args = new long[16];
		args[0] = 0;
		int i = 1;
		int random = SFRandomUtils.random100w();
		for (Entry<Byte, MGLotteryRewardDataRef> entry : this.getLotteryRecord().getRewardMaps().entrySet()) {
			MGLotteryRewardDataRef ref = entry.getValue();
			int probability = MGPropertyAccesser.getProbability(ref.getProperty());
			args[i] = args[i - 1] + probability;
			if (random > args[i - 1] && random <= args[i]) {
				return entry.getKey();
			}
			i++;
		}
		return 1;
	}

}
