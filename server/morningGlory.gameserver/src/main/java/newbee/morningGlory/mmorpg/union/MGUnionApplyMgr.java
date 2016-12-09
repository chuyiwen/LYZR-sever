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
package newbee.morningGlory.mmorpg.union;

import java.util.ArrayList;
import java.util.List;

import org.apache.mina.core.buffer.IoBuffer;

import sophia.foundation.util.ByteArrayReadWriteBuffer;
import sophia.foundation.util.IoBufferUtil;
import sophia.mmorpg.MMORPGContext;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;

public final class MGUnionApplyMgr {
	
	/** 玩家申请列表 playerId */
	private List<String> applyList = new ArrayList<String>();
	
	public synchronized void setApplyList(List<String> applyList) {
		this.applyList = applyList;
	}

	public synchronized boolean containsApplyId(String playerId) {
		return this.applyList.contains(playerId);
	}

	public synchronized boolean removeApply(String playerId) {
		return this.applyList.remove(playerId);
	}

	public synchronized boolean addApply(String playerId) {
		if (applyList.contains(playerId)) {
			return false;
		}
		
		if (applyList.size() >= MGUnionConstant.Apply_Number_UpperLimit) {
			return false;
		}

		this.applyList.add(playerId);
		return true;
	}
	
	public synchronized IoBuffer writeApplyList(IoBuffer buffer) {
		List<String> applyList = this.applyList;
		byte count = (byte) applyList.size();
		buffer.put(count);

		for (String id : applyList) {
			Player player = MMORPGContext.getPlayerComponent().getPlayerManager().getPlayer(id);
			if (player == null) {
				continue;
			}

			IoBufferUtil.putString(buffer, id);
			IoBufferUtil.putString(buffer, player.getName());
			buffer.put(player.getProfession());
			buffer.putShort((short) player.getExpComponent().getLevel());
			byte vipType = MGPropertyAccesser.getVipType(player.getProperty());	
			buffer.put(vipType);
		}

		return buffer;
	}
	
	public synchronized ByteArrayReadWriteBuffer applyListToBytes(ByteArrayReadWriteBuffer buffer) {
		buffer.writeInt(this.applyList.size());
		for (String id : this.applyList) {
			buffer.writeString(id);
		}
		
		return buffer;
	}
}
