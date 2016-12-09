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
package newbee.morningGlory.mmorpg.player.union;

import java.util.ArrayList;
import java.util.List;

import newbee.morningGlory.mmorpg.union.MGUnionConstant;

public final class MGPlayerUnionInvitedMgr {
	
	/** 邀请玩家的帮会Id列表  */
	private List<String> inviteList = new ArrayList<String>();

	public synchronized boolean addInviteUnionId(String unionId) {
		if (inviteList.contains(unionId)) {
			return false;
		}
		
		if (inviteList.size() >= MGUnionConstant.Invited_Number_UpperLimit) {
			return false;
		}
		
		return this.inviteList.add(unionId);
	}

	public synchronized boolean removeInviteUnionId(String unionId) {
		return this.inviteList.remove(unionId);
	}

	public synchronized boolean containsInviteUnionId(String unionId) {
		return this.inviteList.contains(unionId);
	}

	public synchronized int getInvitedCount() {
		return this.inviteList.size();
	}
	
	public synchronized void clearInviteList() {
		inviteList.clear();
	}
}
