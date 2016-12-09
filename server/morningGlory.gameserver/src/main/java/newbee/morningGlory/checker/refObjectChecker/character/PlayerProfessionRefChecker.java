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
package newbee.morningGlory.checker.refObjectChecker.character;

import newbee.morningGlory.checker.BaseRefChecker;

import org.apache.commons.lang3.StringUtils;

import sophia.foundation.property.PropertyDictionary;
import sophia.game.ref.GameRefObject;
import sophia.mmorpg.player.ref.PlayerProfessionLevelData;
import sophia.mmorpg.player.ref.PlayerProfessionRef;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;

public class PlayerProfessionRefChecker extends BaseRefChecker<PlayerProfessionRef> {

	@Override
	public String getDescription() {
		return "角色";
	}

	@Override
	public void check(GameRefObject gameRefObject) {
		PlayerProfessionRef ref = (PlayerProfessionRef) gameRefObject;
		for (PlayerProfessionLevelData levelData : ref.getLevelDataList()) {
			if (StringUtils.equals(levelData.getPlayerProfessionId(), "entanter")) {
				PropertyDictionary levelProperties = levelData.getLevelProperties();
				int minPAtk = MGPropertyAccesser.getMinPAtk(levelProperties);
				int maxPAtk = MGPropertyAccesser.getMaxPAtk(levelProperties);
				int level = levelData.getLevel();
				if (minPAtk > maxPAtk) {
					error(gameRefObject, "最小物攻比最大物攻还大! 最小物攻： " + minPAtk + " 最大物攻： " + maxPAtk + " 等级: " + level);
				}

				int minPDef = MGPropertyAccesser.getMinPDef(levelProperties);
				int maxPDef = MGPropertyAccesser.getMaxPDef(levelProperties);
				if (minPDef > maxPDef) {
					error(gameRefObject, "最小物防比最大物防还大! 最小物防： " + minPAtk + " 最大物防： " + maxPAtk + " 等级: " + level);
				}

				int minMAtk = MGPropertyAccesser.getMinMAtk(levelProperties);
				int maxMAtk = MGPropertyAccesser.getMaxMAtk(levelProperties);
				if (minMAtk > maxMAtk) {
					error(gameRefObject, "最小物攻比最大魔攻还大! 最小魔攻： " + minPAtk + " 最大魔攻： " + maxPAtk + " 等级: " + level);
				}

				int minMDef = MGPropertyAccesser.getMinMDef(levelProperties);
				int maxMDef = MGPropertyAccesser.getMaxMDef(levelProperties);
				if (minMDef > maxMDef) {
					error(gameRefObject, "最小魔防比最大物防还大! 最小魔防： " + minPAtk + " 最大魔防： " + maxPAtk + " 等级: " + level);
				}

				int minTao = MGPropertyAccesser.getMinTao(levelProperties);
				int maxTao = MGPropertyAccesser.getMaxTao(levelProperties);
				if (minTao > maxTao) {
					error(gameRefObject, "最小道攻比最大物攻还大! 最小道攻： " + minPAtk + " 最大道攻： " + maxPAtk + " 等级: " + level);

				}
				
				if(level == ref.maxLevel()) {
					long maxExp = MGPropertyAccesser.getMaxExp(levelProperties);
					if(maxExp != 0) {
						error(gameRefObject, "最高等级还填了最大经验值maxExp!");
					}
				}
			}
		}
	}

}
