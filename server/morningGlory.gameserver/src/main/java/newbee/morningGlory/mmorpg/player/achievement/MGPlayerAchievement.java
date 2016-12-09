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
package newbee.morningGlory.mmorpg.player.achievement;

import java.util.UUID;

import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;

public final class MGPlayerAchievement {
	private MGPlayerAchievementRef achievementRef;
	public static final byte ALREADYGET = 1;
	public static final byte NOTGET = 0;
	
	private byte success = NOTGET;// 是否领奖
	private String id;

	public MGPlayerAchievement(MGPlayerAchievementRef achievementRef) {
		setAchievementRef(achievementRef);
		setId(UUID.randomUUID().toString());
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getId() {
		return this.id;
	}

	public int getCompleteCondition(){
		return MGPropertyAccesser.getCompleteCondition(achievementRef.getProperty());
	}
	
	public MGPlayerAchievementRef getAchievementRef() {
		return achievementRef;
	}

	public void setAchievementRef(MGPlayerAchievementRef achievementRef) {
		this.achievementRef = achievementRef;
	}

	public byte getSuccess() {
		return success;
	}

	public void setSuccess(byte success) {
		this.success = success;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((achievementRef == null) ? 0 : achievementRef.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MGPlayerAchievement other = (MGPlayerAchievement) obj;
		if (achievementRef == null) {
			if (other.achievementRef != null)
				return false;
		} else if (!achievementRef.equals(other.achievementRef))
			return false;
		return true;
	}

}
