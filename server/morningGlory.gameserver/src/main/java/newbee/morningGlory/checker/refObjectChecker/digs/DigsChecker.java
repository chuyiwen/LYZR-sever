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
package newbee.morningGlory.checker.refObjectChecker.digs;

import java.util.Map;
import java.util.Map.Entry;

import newbee.morningGlory.checker.BaseRefChecker;
import newbee.morningGlory.mmorpg.player.activity.digs.ref.MGDigsDataConfig;
import newbee.morningGlory.mmorpg.player.activity.digs.ref.MGDigsRewardRef;
import newbee.morningGlory.mmorpg.player.activity.digs.ref.MGDigsTypeRef;

import org.apache.commons.lang3.StringUtils;

import sophia.game.GameRoot;
import sophia.game.ref.GameRefObject;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;

public class DigsChecker extends BaseRefChecker<MGDigsDataConfig> {

	@Override
	public String getDescription() {
		return "挖宝";
	}

	@Override
	public void check(GameRefObject gameRefObject) {
		MGDigsDataConfig config = (MGDigsDataConfig) gameRefObject;
		String id = config.getId();
		if(StringUtils.equals(id, MGDigsDataConfig.DigsType_Id)){
			Map<String,MGDigsTypeRef> digsTypeMaps = config.getDigsTypeMaps();
			for(Entry<String,MGDigsTypeRef> entry : digsTypeMaps.entrySet()){
				MGDigsTypeRef ref = entry.getValue();
				if(ref == null){
					error(gameRefObject,"挖宝类型ref对象为 null");
				}
				int unbindedGold = MGPropertyAccesser.getUnbindedGold(ref.getProperty());
				String itemRefId = MGPropertyAccesser.getItemRefId(ref.getProperty());
				
				if(unbindedGold < 0){
					error(gameRefObject,"挖宝消耗元宝数 小于 0:"+ref.getId());
				}
				GameRefObject itemRef = GameRoot.getGameRefObjectManager().getManagedObject(itemRefId);
				if(itemRef == null){
					error(gameRefObject,"挖宝消耗物refID 不存在 :"+itemRefId);
				}
			}
			
		}else if(StringUtils.equals(id, MGDigsDataConfig.DigsReward_Id)){
			Map<String,MGDigsRewardRef> digsRewardMaps = config.getDigsRewardMaps();
			for(Entry<String,MGDigsRewardRef> entry : digsRewardMaps.entrySet()){
				MGDigsRewardRef ref = entry.getValue();
				if(ref == null){
					error(gameRefObject,"挖宝奖励ref对象为 null");
				}
				
				String itemRefId = MGPropertyAccesser.getItemRefId(ref.getProperty());
				GameRefObject itemRef = GameRoot.getGameRefObjectManager().getManagedObject(itemRefId);
				if(itemRef == null){
					error(gameRefObject,"挖宝奖励refID 不存在 :"+itemRefId);
				}
				int number = MGPropertyAccesser.getNumber(ref.getProperty());
				if(number < 0){
					error(gameRefObject,"挖宝奖励物数量 小于 0:"+ref.getId());
				}
			}
		}
		
	}
	
	

}
