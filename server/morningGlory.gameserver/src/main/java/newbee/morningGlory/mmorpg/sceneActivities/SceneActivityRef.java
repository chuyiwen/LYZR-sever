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

import java.util.List;

import newbee.morningGlory.mmorpg.sceneActivities.chime.Chime;
import sophia.foundation.core.ComponentRegistry;
import sophia.foundation.core.ComponentRegistryImpl;
import sophia.game.ref.AbstractGameRefObjectBase;

public class SceneActivityRef extends AbstractGameRefObjectBase {
	private static final long serialVersionUID = -1593393520782138997L;
	
	private String sceneRefId;
	
	private String durationTime;
	
	private int preStartTime;
	
	private int preEndTime;
	
	private List<Chime> chimeList;
	
	private ComponentRegistry componentRegistry = new ComponentRegistryImpl(); 

	public String getSceneRefId() {
		return sceneRefId;
	}

	public void setSceneRefId(String sceneRefId) {
		this.sceneRefId = sceneRefId;
	}

	public String getDurationTime() {
		return durationTime;
	}

	public void setDurationTime(String durationTime) {
		this.durationTime = durationTime;
	}

	public int getPreStartTime() {
		return preStartTime;
	}

	public void setPreStartTime(int preStartTime) {
		this.preStartTime = preStartTime;
	}

	public int getPreEndTime() {
		return preEndTime;
	}

	public void setPreEndTime(int preEndTime) {
		this.preEndTime = preEndTime;
	}
	
	public void addComponentRef(Object obj) {
		componentRegistry.addComponent(obj);
	}
	
	public <T> T getComponentRef(Class<T> type) {
		return componentRegistry.getComponent(type);
	}

	public List<Chime> getChimeList() {
		return chimeList;
	}

	public void setChimeList(List<Chime> chimeList) {
		this.chimeList = chimeList;
	}
}
