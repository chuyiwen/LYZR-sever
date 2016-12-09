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
package newbee.morningGlory.mmorpg.sceneActivities;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import newbee.morningGlory.mmorpg.sceneActivities.chime.Chime;
import newbee.morningGlory.mmorpg.sceneActivities.gameEvent.SceneActivityClose_GE;
import newbee.morningGlory.mmorpg.sceneActivities.gameEvent.SceneActivityOpen_GE;
import sophia.foundation.communication.core.ActionEventBase;
import sophia.game.GameContext;
import sophia.game.GameRoot;
import sophia.game.component.communication.GameEvent;
import sophia.game.plugIns.gameWorld.GameWorld;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.sceneActivities.SceneActivityInterface;

public abstract class SceneActivity implements SceneActivityInterface {

	protected byte crtActivityState = ACTIVITY_END;

	protected byte preActivityState = ACTIVITY_PRE_DEFAULT;

	private SceneActivityType activityType;

	private List<Chime> chimeList = new ArrayList<>();

	public abstract boolean checkEnter(Player player);

	public abstract boolean checkLeave(Player player);

	public abstract boolean onPreStart();

	public abstract boolean onPreEnd();

	public abstract boolean onStart();

	public abstract boolean onEnd();
	
	public abstract boolean onEnter(Player player);
	
	public abstract boolean onLeave(Player player);
	
	public abstract boolean onCheckEnd();

	public abstract void handleGameEvent(GameEvent<?> event);

	public abstract void handleActionEvent(ActionEventBase event);

	public SceneActivityType getActivityType() {
		return activityType;
	}

	public void setActivityType(SceneActivityType activityType) {
		this.activityType = activityType;
	}

	public List<Chime> getChimeList() {
		return chimeList;
	}

	public void setChimeList(List<Chime> chimeList) {
		this.chimeList = chimeList;
	}

	public byte getCrtActivityState() {
		return crtActivityState;
	}
	
	public byte getPreActivityState() {
		return preActivityState;
	}

	public SceneActivityRef getRef() {
		String refId = activityType.getRefId();
		return (SceneActivityRef) GameRoot.getGameRefObjectManager().getManagedObject(refId);
	}

	public void refreshChime() {
		chimeList.clear();
		Calendar crtCalendar = new GregorianCalendar();
		SceneActivityRef ref = getRef();
		List<Chime> refChimeList = ref.getChimeList();
		if(refChimeList==null)
			return;
		
		for (Chime chime : refChimeList) {
			if (chime.checkEnd(crtCalendar)) {
				continue;
			}
			if (!chime.isTheSameDay(crtCalendar)) {
				continue;
			}
			chimeList.add(chime);
		}
	}

	public void checkBegin(final Calendar crtCalendar) {
		if (crtActivityState == ACTIVITY_START) {
			return;
		}

		if (chimeList.size() <= 0) {
			return;
		}

		Chime chime = chimeList.get(0);
		if (chime.checkPreStart(getRef().getPreStartTime()) && preActivityState != ACTIVITY_PRE_START) {
			preActivityState = ACTIVITY_PRE_START;
			onPreStart();
		}
		
		if (chime.checkBegin(crtCalendar)) {
			crtActivityState = ACTIVITY_START;
			preActivityState = ACTIVITY_PRE_DEFAULT;
			onStart();
			sendSceneActivityOpen();
		}
	}

	public void checkEnd(final Calendar crtCalendar) {
		if (crtActivityState == ACTIVITY_END) {
			onCheckEnd();
			return;
		}

		if (chimeList.size() <= 0) {
			return;
		}
		Chime chime = chimeList.get(0);
		if (chime.checkPreEnd(getRef().getPreEndTime()) && preActivityState != ACTIVITY_PRE_END) {
			preActivityState = ACTIVITY_PRE_END;
			onPreEnd();
		}
		
		if (chime.checkEnd(crtCalendar)) {
			onEnd();
			chimeList.remove(0);
			crtActivityState = ACTIVITY_END;
			preActivityState = ACTIVITY_PRE_DEFAULT;
			sendSceneActivityClose();
		}
	}
	
	public boolean isInPeriod(long timeMillis) {
		if (crtActivityState != ACTIVITY_START) {
			return false;
		}
		if (getChimeList().isEmpty()) {
			return false;
		}
		
		Chime chime = getChimeList().get(0);
		long beginTimeStamp = chime.getStartTimeStamp();
		long endTimeStamp = chime.getEndTimeStamp();


		boolean isInperiod = beginTimeStamp <= timeMillis && timeMillis <= endTimeStamp; 
		
		return isInperiod;
	}
	
	/**
	 * 距离活动开始时间
	 * 
	 * @return
	 */
	public long getActivityRemainingStartTime() {
		if (crtActivityState == ACTIVITY_START) {
			return 0;
		}
		if (getChimeList().isEmpty()) {
			return 0;
		}
		return getChimeList().get(0).getRemainStartTime();
	}

	/**
	 * 距离活动结束时间
	 * 
	 * @return
	 */
	public long getActivityRemainingEndTime() {
		if (crtActivityState == ACTIVITY_END) {
			return 0;
		}
		if (getChimeList().isEmpty()) {
			return 0;
		}
		return getChimeList().get(0).getRemainEndTime();
	}

	
	public void sendSceneActivityOpen(){
		
		if(getActivityType()==SceneActivityType.MutilExpActivity || getActivityType()==SceneActivityType.PayonPalaceActivity)
			return;
		
		SceneActivityRef ref = getRef();
		String sceneRefId = ref.getSceneRefId();
		int preStartTime = ref.getPreStartTime();
		int preEndTime = ref.getPreEndTime();
		GameWorld gameWorld = GameContext.getGameWorld();
		SceneActivityOpen_GE sceneActivityOpen_GE = new SceneActivityOpen_GE(sceneRefId, preStartTime, preEndTime);
		GameEvent<SceneActivityOpen_GE> event = (GameEvent<SceneActivityOpen_GE>) GameEvent.getInstance(SceneActivityOpen_GE.class.getSimpleName(), sceneActivityOpen_GE);
		gameWorld.handleGameEvent(event);
		GameEvent.pool(event);
	}
	
	public void sendSceneActivityClose(){
		
		if(getActivityType()==SceneActivityType.MutilExpActivity || getActivityType()==SceneActivityType.PayonPalaceActivity)
			return;
		
		SceneActivityRef ref = getRef();
		String sceneRefId = ref.getSceneRefId();
		int preStartTime = ref.getPreStartTime();
		int preEndTime = ref.getPreEndTime();
		GameWorld gameWorld = GameContext.getGameWorld();
		SceneActivityClose_GE sceneActivityClose_GE = new SceneActivityClose_GE(sceneRefId, preStartTime, preEndTime);
		GameEvent<SceneActivityClose_GE> event = (GameEvent<SceneActivityClose_GE>) GameEvent.getInstance(SceneActivityClose_GE.class.getSimpleName(), sceneActivityClose_GE);
		gameWorld.handleGameEvent(event);
		GameEvent.pool(event);
	}

}
