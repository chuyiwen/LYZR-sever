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
package sophia.mmorpg.player.worldBossMsg;

import sophia.game.ref.AbstractGameRefObjectBase;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;

public class WorldBossMsgRef extends AbstractGameRefObjectBase {

	private static final long serialVersionUID = 5479904171386095838L;
	
	public boolean isScrollNotice(){
		
		return MGPropertyAccesser.getIsScrollNotice(getProperty()) == 1;
	}
	
	public boolean isTwinkle(){
		
		return MGPropertyAccesser.getIsTwinkle(getProperty()) == 1;
	}
	
	public String getMonsterRefId(){
		return MGPropertyAccesser.getMonsterRefId(getProperty());
	}
	
	public String getGameSceneRefId(){
		return MGPropertyAccesser.getSceneRefId(getProperty());
	}
	
	public int getRefreshTime(){
		return MGPropertyAccesser.getRefreshTime(getProperty());
	}
	
	public boolean isActivityBoss(){
		return MGPropertyAccesser.getKind(getProperty()) == WorldBoss.ACTIVITY_BOSS;
	}
	
	public String getSceneRefId(){
		return MGPropertyAccesser.getSceneRefId(getProperty());
	}
}
