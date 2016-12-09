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
package newbee.morningGlory.checker.refObjectChecker.equipmentFenJie;

import java.util.List;
import java.util.Map.Entry;

import newbee.morningGlory.checker.BaseRefChecker;

import org.apache.commons.lang3.StringUtils;

import sophia.foundation.property.PropertyDictionary;
import sophia.game.GameRoot;
import sophia.game.ref.GameRefObject;
import sophia.mmorpg.equipmentSmith.smith.fenJie.MGFenJieEquipmentConfig;
import sophia.mmorpg.equipmentSmith.smith.fenJie.MGFenJieItem;
import sophia.mmorpg.equipmentSmith.smith.fenJie.MGFenJieRef;
import sophia.mmorpg.equipmentSmith.smith.fenJie.MGFenJieScrollRef;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;

public class EquipmentRefChecker extends BaseRefChecker<MGFenJieEquipmentConfig> {

	@Override
	public String getDescription() {

		return "装备出售(分解)";
	}

	@Override
	public void check(GameRefObject gameRefObject) {
		MGFenJieEquipmentConfig config = (MGFenJieEquipmentConfig) gameRefObject;
		if (StringUtils.equals(MGFenJieEquipmentConfig.FenJie_Id, config.getId())) {
			for (Entry<String, MGFenJieRef> entry : config.getFenJieConfigMap().entrySet()) {
				String key = entry.getKey();
				String rkey = key.substring(1, key.length()-1);
				String[] range = rkey.split(",");
				if(range.length != 2){
					error(gameRefObject,"装备评价等级填写格式有误！请检查！");
				}
				
				if(!StringUtils.isNumeric(range[0]) || !StringUtils.isNumeric(range[1])){
					error(gameRefObject,"装备评价等级填写格式有误！请检查！");
				}
				MGFenJieRef ref = entry.getValue();
				List<MGFenJieItem> list = ref.getFenJieItem();
				for (MGFenJieItem jieItem : list) {
					String itemRefId = jieItem.getItemRefId();
					GameRefObject gameRef = GameRoot.getGameRefObjectManager().getManagedObject(itemRefId);
					if (gameRef == null) {
						error(gameRefObject, "装备分解  道具表中不存在refId： " + itemRefId + "道具");
					}
				}
			}
		}else{
			for (Entry<Integer, MGFenJieScrollRef> entry : config.getFenJieScrollConfigMap().entrySet()) {
				MGFenJieScrollRef ref = entry.getValue();
				PropertyDictionary pd = ref.getProperty();
				int strengLevel = MGPropertyAccesser.getStrengtheningLevel(pd);
				String itemRefId = MGPropertyAccesser.getItemRefId(pd);
				GameRefObject gameRef = GameRoot.getGameRefObjectManager().getManagedObject(itemRefId);
				if(strengLevel > 12 || strengLevel <0){
					error(gameRefObject, "装备分解 强化等级应该在区间[1,12]之间");
				}
				if (gameRef == null) {
					error(gameRefObject, "装备分解  道具表中不存在refId： " + itemRefId + "强化卷");
				}
			}
		}
	}

}
