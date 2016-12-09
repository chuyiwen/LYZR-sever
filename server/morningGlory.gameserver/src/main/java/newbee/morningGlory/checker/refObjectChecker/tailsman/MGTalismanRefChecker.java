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
package newbee.morningGlory.checker.refObjectChecker.tailsman;

import java.util.Map;

import newbee.morningGlory.checker.BaseRefChecker;
import newbee.morningGlory.mmorpg.player.talisman.MGPlayerCitta;
import newbee.morningGlory.mmorpg.player.talisman.MGTalismanRef;
import newbee.morningGlory.mmorpg.player.talisman.level.MGTalismanDataConfig;
import sophia.game.GameRoot;
import sophia.game.ref.GameRefObject;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;

public class MGTalismanRefChecker extends BaseRefChecker<MGTalismanDataConfig> {
	@Override
	public void check(GameRefObject gameRefObject) {
		MGTalismanDataConfig config = (MGTalismanDataConfig)gameRefObject;
		Map<String,MGTalismanRef> map = config.getTalismanLevelDataMap();
		for(java.util.Map.Entry<String, MGTalismanRef> entry : map.entrySet()){
			MGTalismanRef info = entry.getValue();
			if (!info.getId().startsWith("title_")) {
				error(gameRefObject, "法宝<refId>错误 , 请以title_开头!!! 错误的refId为: " + info.getId());
			}
			int level = MGPropertyAccesser.getTalisManLevel(info.getProperty());
			if(level > MGPlayerCitta.MaxLevel || level <0){
				error(gameRefObject, "法宝<level>错误 , 法宝等级不能超过60或者小于0!!! 错误的refId为: " + info.getId()+"当前等级:"+level);
			}
			String key = info.getId() +"_"+ level;
			MGTalismanDataConfig talismanDataConfig = (MGTalismanDataConfig) GameRoot.getGameRefObjectManager().getManagedObject(MGTalismanDataConfig.MGTalisman_Id);
			MGTalismanRef talismanRef = talismanDataConfig.getTalismanLevelDataMap().get(key);
			if(talismanRef == null){
				error(gameRefObject, "法宝<key>错误 , 法宝key应该为[refId_level] !!! 错误的key为: " + key+",法宝refId："+info.getId());
			}
		}
		
	}
	
	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return "法宝数据";
	}
}
