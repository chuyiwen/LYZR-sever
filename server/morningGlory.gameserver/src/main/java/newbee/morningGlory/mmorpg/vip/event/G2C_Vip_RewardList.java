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

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public class G2C_Vip_RewardList extends ActionEventBase {
	
	private byte expMultiple;
	private byte gift;
	private byte level;
	@Override
	protected IoBuffer packBody(IoBuffer buffer) {
		buffer.put(expMultiple);
		buffer.put(gift);
		buffer.put(level);
		return buffer;
	}
	@Override
	public void unpackBody(IoBuffer arg0) {
		// TODO Auto-generated method stub
		
	}
	
	public byte getExpMultiple() {
		return expMultiple;
	}

	public void setExpMultiple(byte expMultiple) {
		this.expMultiple = expMultiple;
	}

	public byte getGift() {
		return gift;
	}

	public void setGift(byte gift) {
		this.gift = gift;
	}

	public byte getLevel() {
		return level;
	}

	public void setLevel(byte level) {
		this.level = level;
	}
	
}
