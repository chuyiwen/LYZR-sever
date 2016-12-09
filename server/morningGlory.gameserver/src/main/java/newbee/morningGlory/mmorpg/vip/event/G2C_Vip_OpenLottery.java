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
package newbee.morningGlory.mmorpg.vip.event;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import newbee.morningGlory.mmorpg.vip.lottery.ref.MGLotteryRewardDataRef;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;

public class G2C_Vip_OpenLottery extends ActionEventBase {
	
	private byte vipType;
	private int tomCount;	
	private int todayCount;
	private Map<Byte,MGLotteryRewardDataRef> rewardMaps = new HashMap<>();
	
	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		
		buffer.put(vipType);
		//buffer.putInt(tomCount);
		buffer.putShort((short) tomCount);
		//buffer.putInt(todayCount);
		buffer.putShort((short) todayCount);
		for(Entry<Byte,MGLotteryRewardDataRef> entry : rewardMaps.entrySet()){
			
			MGLotteryRewardDataRef ref = entry.getValue();
			String itemRefId = MGPropertyAccesser.getItemRefId(ref.getProperty());
			buffer.put(entry.getKey());
			putString(buffer, itemRefId);
		}
		
		return buffer;
	}
	
	@Override
	public void unpackBody(IoBuffer arg0) {
		// TODO Auto-generated method stub
		
	}
	public byte getVipType() {
		return vipType;
	}

	public void setVipType(byte vipType) {
		this.vipType = vipType;
	}

	public int getTomCount() {
		return tomCount;
	}

	public void setTomCount(int tomCount) {
		this.tomCount = tomCount;
	}

	public int getTodayCount() {
		return todayCount;
	}

	public void setTodayCount(int todayCount) {
		this.todayCount = todayCount;
	}

	public Map<Byte,MGLotteryRewardDataRef> getRewardMaps() {
		return rewardMaps;
	}

	public void setRewardMaps(Map<Byte,MGLotteryRewardDataRef> rewardMaps) {
		this.rewardMaps = rewardMaps;
	}

}
