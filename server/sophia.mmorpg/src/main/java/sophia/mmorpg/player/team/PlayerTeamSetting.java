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
package sophia.mmorpg.player.team;

public final class PlayerTeamSetting {
	/** 是否接受队伍加入邀请.如果false，不接受任何队伍的邀请加入*/
	private volatile boolean acceptedInvite = true;
	
	private int levelLimit = 0; // 队伍等级限制，只有在活动里面创建的队伍才有相应的队伍等级限制
	
	public PlayerTeamSetting() {
		
	}

	public boolean isAcceptedInvite() {
		return acceptedInvite;
	}

	public void setAcceptedInvite(boolean acceptedInvite) {
		this.acceptedInvite = acceptedInvite;
	}

	public int getLevelLimit() {
		return levelLimit;
	}

	public void setLevelLimit(int levelLimit) {
		this.levelLimit = levelLimit;
	}
}
