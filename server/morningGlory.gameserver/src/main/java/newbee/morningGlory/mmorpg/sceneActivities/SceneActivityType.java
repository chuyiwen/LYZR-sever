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

import newbee.morningGlory.mmorpg.sceneActivities.castleWar.CastleWarMgr;
import newbee.morningGlory.mmorpg.sceneActivities.mining.MGMiningActivity;
import newbee.morningGlory.mmorpg.sceneActivities.monsterIntrusion.MonsterInvasionMgr;
import newbee.morningGlory.mmorpg.sceneActivities.mutilExp.MGMutilExpActivity;
import newbee.morningGlory.mmorpg.sceneActivities.payonPalace.MGPayonPalaceActivity;
import newbee.morningGlory.mmorpg.sceneActivities.teamBoss.TeamBossMgr;

import org.apache.commons.lang3.StringUtils;

public enum SceneActivityType {
	/** 添加活动类型 */
	MGMiningActivity("sa_1", MGMiningActivity.class),
	CastleWarMgr("castleWar", CastleWarMgr.class),
	MonsterIntrusionMgr1("monsterInvasion1", MonsterInvasionMgr.class),
	MonsterIntrusionMgr2("monsterInvasion2", MonsterInvasionMgr.class),
	TeamBoss1("teamBoss_111", TeamBossMgr.class),
	TeamBoss2("teamBoss_112", TeamBossMgr.class),
	TeamBoss3("teamBoss_113", TeamBossMgr.class),
	TeamBoss4("teamBoss_114", TeamBossMgr.class),
	TeamBoss5("teamBoss_121", TeamBossMgr.class),
	TeamBoss6("teamBoss_122", TeamBossMgr.class),
	TeamBoss7("teamBoss_123", TeamBossMgr.class),
	TeamBoss8("teamBoss_124", TeamBossMgr.class),
	TeamBoss9("teamBoss_211", TeamBossMgr.class),
	TeamBoss10("teamBoss_212", TeamBossMgr.class),
	TeamBoss11("teamBoss_213", TeamBossMgr.class),
	TeamBoss12("teamBoss_214", TeamBossMgr.class),
	TeamBoss13("teamBoss_221", TeamBossMgr.class),
	TeamBoss14("teamBoss_222", TeamBossMgr.class),
	TeamBoss15("teamBoss_223", TeamBossMgr.class),
	TeamBoss16("teamBoss_224", TeamBossMgr.class),
	TeamBoss17("teamBoss_231", TeamBossMgr.class),
	TeamBoss18("teamBoss_232", TeamBossMgr.class),
	TeamBoss19("teamBoss_233", TeamBossMgr.class),
	TeamBoss20("teamBoss_234", TeamBossMgr.class),
	TeamBoss21("teamBoss_311", TeamBossMgr.class),
	TeamBoss22("teamBoss_312", TeamBossMgr.class),
	TeamBoss23("teamBoss_313", TeamBossMgr.class),
	TeamBoss24("teamBoss_314", TeamBossMgr.class),
	TeamBoss25("teamBoss_321", TeamBossMgr.class),
	TeamBoss26("teamBoss_322", TeamBossMgr.class),
	TeamBoss27("teamBoss_323", TeamBossMgr.class),
	TeamBoss28("teamBoss_324", TeamBossMgr.class),
	TeamBoss29("teamBoss_331", TeamBossMgr.class),
	TeamBoss30("teamBoss_332", TeamBossMgr.class),
	TeamBoss31("teamBoss_333", TeamBossMgr.class),
	TeamBoss32("teamBoss_334", TeamBossMgr.class),
	PayonPalaceActivity("payonPalace_1", MGPayonPalaceActivity.class),
	MutilExpActivity("multiExp", MGMutilExpActivity.class),
	;

	private String refId;
	private Class<? extends SceneActivity> clazz;

	private SceneActivityType(String refId, Class<? extends SceneActivity> clazz) {
		this.refId = refId;
		this.clazz = clazz;
	}

	public Class<? extends SceneActivity> getClazz() {
		return clazz;
	}

	public String getRefId() {
		return refId;
	}

	public void setClazz(Class<? extends SceneActivity> clazz) {
		this.clazz = clazz;
	}

	public static SceneActivityType get(String refId) {
		for (SceneActivityType t : values()) {
			if (StringUtils.equals(t.getRefId(), refId)) {
				return t;
			}
		}
		return null;
	}
}
