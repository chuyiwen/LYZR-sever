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
package newbee.morningGlory.mmorpg.player.pk.ref;

import java.util.List;
import java.util.Map;

import sophia.game.ref.AbstractGameRefObjectBase;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;

public class MGScenePKDropRef  extends AbstractGameRefObjectBase{

	private static final long serialVersionUID = -7801148913637577639L;
	private String dropMethod;
	private  Map<String,List<MGInvasionPair>> invasionMap;
	
	public Map<String,List<MGInvasionPair>> getInvasionMap() {
		return invasionMap;
	}
	public void setInvasionMap(Map<String,List<MGInvasionPair>> invasionMap) {
		this.invasionMap = invasionMap;
	}
	
	public int getAttackAddPkValue(){
		return MGPropertyAccesser.getAttackAddPkValue(getProperty());
	}
	public int getKillAddPkValue(){
		return MGPropertyAccesser.getKillAddPKValue(getProperty());
	}
	public boolean isEnterPKState(){
		return MGPropertyAccesser.getIsEnterPKState(getProperty()) == 1;
	}
	public boolean isUseDefaultDrop(){
		return MGPropertyAccesser.getIsUseDefaultDrop(getProperty()) == 1;
	}
	public String getSceneRefId(){
		return MGPropertyAccesser.getSceneRefId(getProperty());
	}
	public String getDropMethod() {
		return dropMethod;
	}
	public void setDropMethod(String dropMethod) {
		this.dropMethod = dropMethod;
	}
}
