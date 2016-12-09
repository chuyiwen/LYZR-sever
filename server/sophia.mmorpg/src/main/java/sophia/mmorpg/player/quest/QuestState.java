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
package sophia.mmorpg.player.quest;

public final class QuestState {
	/** 任务不可见*/
	public static final int UnvisiableQuestState = 1;
	
	/** 任务仅可见*/
	public static final int VisiableQuestState = 2;
	
	/** 任务可接*/
	public static final int AcceptableQuestState = 3;
	
	/** 任务已接，但未完成*/
	public static final int AcceptedQuestState = 4;
	
	/** 任务已经可提交。但还没提交*/
	public static final int SubmittableQuestState = 5;
	
	/** 任务已经完成。已经提交领取奖励*/
	public static final int CompletedQuestState = 6;
	
	private QuestState() {
		
	}
}
