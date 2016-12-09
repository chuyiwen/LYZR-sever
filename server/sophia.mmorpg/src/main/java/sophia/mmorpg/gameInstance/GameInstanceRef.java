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
package sophia.mmorpg.gameInstance;

import java.util.ArrayList;
import java.util.List;

import sophia.foundation.core.ComponentRegistry;
import sophia.foundation.core.ComponentRegistryImpl;
import sophia.game.ref.AbstractGameRefObjectBase;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;

public final class GameInstanceRef extends AbstractGameRefObjectBase {
	private static final long serialVersionUID = -7339862495942153894L;

	public static final List<String> gameInstanceRefIdList = new ArrayList<String>();

	/** 副本 **/
	public static final String GAME_INSTANCE = "game_instance";
	/** 副本层 **/
	public static final String GAME_INSTANCE_SCENE = "game_instance_scene";
	/** 副本任务 **/
	public static final String GAME_INSTANCE_QUEST = "game_instance_quest";

	private final ComponentRegistry componentRegistry = new ComponentRegistryImpl();

	private OpenByTime open;// 开启条件对象

	public GameInstanceRef() {

	}

	public void addGameInstanceDataObject(Object dataObject) {
		componentRegistry.addComponent(dataObject);
	}

	public <T> T getGameInstanceDataObject(Class<T> type) {
		return componentRegistry.getComponent(type);
	}

	public OpenByTime getOpen() {
		return open;
	}

	public static void addGameInstanceRefId(String gameInstanceRefId) {
		gameInstanceRefIdList.add(gameInstanceRefId);
	}

	public static List<String> getGameInstanceRefIdList() {
		return gameInstanceRefIdList;
	}

	public void setOpenDetailsData(String openConditions) {
		open = new OpenByTime();
		open.setOpenDetails(openConditions);
	}

	public int getCountsADay(Player player, int vipRewardTime) {
		return MGPropertyAccesser.getTimesADay(getProperty()) + vipRewardTime;
	}
	public int getRefCountsADay(Player player) {
		return MGPropertyAccesser.getTimesADay(getProperty());
	}
	public int getCountsAWeek(Player player) {
		return MGPropertyAccesser.getTimesAWeek(getProperty());
	}

	public int getLevel() {
		return MGPropertyAccesser.getLevel(getProperty());
	}

	public String getQuestRefId() {
		return MGPropertyAccesser.getQuestRefId(getProperty());
	}
	
	public boolean isMultiPlayerGameInstance(){
		return MGPropertyAccesser.getIsTeam(getProperty()) == 1;
	}
}
