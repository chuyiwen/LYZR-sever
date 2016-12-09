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
package sophia.mmorpg.player.quest.ref.npc;

public final class QuestRefNpcData {
	private String acceptedNpcSceneRefId;
	
	private String acceptedNpcRefId;
	
	private String acceptedTalkContext;
	
	private String takeRewardNpcSceneRefId;
	
	private String takeRewardNpcRefId;
	
	private String takeRewardTalkContext;
	
	public QuestRefNpcData() {
		
	}

	public final String getAcceptedNpcSceneRefId() {
		return acceptedNpcSceneRefId;
	}

	public final void setAcceptedNpcSceneRefId(String acceptedNpcSceneRefId) {
		this.acceptedNpcSceneRefId = acceptedNpcSceneRefId;
	}

	public final String getAcceptedNpcRefId() {
		return acceptedNpcRefId;
	}

	public final void setAcceptedNpcRefId(String acceptedNpcRefId) {
		this.acceptedNpcRefId = acceptedNpcRefId;
	}

	public final String getAcceptedTalkContext() {
		return acceptedTalkContext;
	}

	public final void setAcceptedTalkContext(String acceptedTalkContext) {
		this.acceptedTalkContext = acceptedTalkContext;
	}

	public final String getTakeRewardNpcSceneRefId() {
		return takeRewardNpcSceneRefId;
	}

	public final void setTakeRewardNpcSceneRefId(String takeRewardNpcSceneRefId) {
		this.takeRewardNpcSceneRefId = takeRewardNpcSceneRefId;
	}

	public final String getTakeRewardNpcRefId() {
		return takeRewardNpcRefId;
	}

	public final void setTakeRewardNpcRefId(String takeRewardNpcRefId) {
		this.takeRewardNpcRefId = takeRewardNpcRefId;
	}

	public final String getTakeRewardTalkContext() {
		return takeRewardTalkContext;
	}

	public final void setTakeRewardTalkContext(String takeRewardTalkContext) {
		this.takeRewardTalkContext = takeRewardTalkContext;
	}
}
