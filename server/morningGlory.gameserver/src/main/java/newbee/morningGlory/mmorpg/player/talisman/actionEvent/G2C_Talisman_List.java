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

import newbee.morningGlory.mmorpg.player.talisman.MGPlayerCitta;
import newbee.morningGlory.mmorpg.player.talisman.MGTalisman;
import newbee.morningGlory.mmorpg.player.talisman.MGTalismanContains;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.communication.core.ActionEventBase;

public final class G2C_Talisman_List extends ActionEventBase {
	private MGPlayerCitta talismanMgr;

	@Override
	protected IoBuffer packBody(IoBuffer paramIoBuffer) {
		paramIoBuffer.put((byte) talismanMgr.getTalismanSystemActiveState());
		paramIoBuffer.put((byte)talismanMgr.getLevel());
		paramIoBuffer.putShort((short) talismanMgr.getTalismanList().size());
	
		for (MGTalismanContains talismanContain : talismanMgr.getTalismanList()) {
			MGTalisman talisman = talismanContain.getTalisman();
			paramIoBuffer.putShort((short) talismanContain.getIndex());
			paramIoBuffer.put(talisman.getState());
			putString(paramIoBuffer, talisman.getTalismanRef().getId());		
		}
		return paramIoBuffer;
	}

	@Override
	public void unpackBody(IoBuffer paramIoBuffer) {

	}

	@Override
	public String getName() {
		return "法宝列表请求";
	}

	public MGPlayerCitta getTalismanMgr() {
		return talismanMgr;
	}

	public void setTalismanMgr(MGPlayerCitta talismanMgr) {
		this.talismanMgr = talismanMgr;
	}
}
