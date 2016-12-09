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
package newbee.morningGlory.mmorpg.player.talisman.level;

import newbee.morningGlory.mmorpg.player.talisman.MGTalisman;
import newbee.morningGlory.mmorpg.player.talisman.MGTalismanRef;
import sophia.foundation.property.PropertyDictionary;
import sophia.game.GameRoot;
import sophia.mmorpg.code.MMORPGErrorCode;
import sophia.mmorpg.player.Player;
import sophia.mmorpg.player.itemBag.ItemCode;
import sophia.mmorpg.player.itemBag.ItemFacade;
import sophia.mmorpg.player.itemBag.ItemOptSource;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;
import sophia.mmorpg.utils.RuntimeResult;

/**
 * 玩家-法宝-升级
 */
public final class MGTalismanLevelSmith {
	private MGTalisman talisman;
	private static final MGTalismanDataConfig talismanDataConfig;
	
	public MGTalismanLevelSmith() {
		
	}
	
	public MGTalismanLevelSmith(MGTalisman talisman) {
		this.talisman = talisman;
	}
	static{
		talismanDataConfig = (MGTalismanDataConfig) GameRoot.getGameRefObjectManager().getManagedObject(MGTalismanDataConfig.MGTalisman_Id);
	}
	
	
	public RuntimeResult acquire(Player player){
		RuntimeResult ret = RuntimeResult.RuntimeError();
		String refId = talisman.getTalismanRef().getId()+"_"+0;
		MGTalismanRef talismanRef = talismanDataConfig.getTalismanLevelDataMap().get(refId);
		if(talismanRef == null){
			return ret;
		}
		PropertyDictionary questData = talismanRef.getQuestData();	
		int needAllCanNumber = 0;
		if(questData == null){
			return ret;
		}
		String itemRefId = MGPropertyAccesser.getItemRefId(questData);
		int number = MGPropertyAccesser.getNumber(questData);
		int curNumber = ItemFacade.getNumber(player, itemRefId);
		int removeNumber = 0;
		if(curNumber < number){
			int allCanNumber = ItemFacade.getNumber(player, ItemCode.AllCanSuiPian);
			if(allCanNumber + curNumber < number){
				return RuntimeResult.RuntimeApplicationError(MMORPGErrorCode.CODE_ITEM_NOENOUGH);
			}else{
				needAllCanNumber = number - curNumber;
				removeNumber = curNumber;
			}
		}else{
			removeNumber = number;
		}	
		if(ItemFacade.removeItem(player, itemRefId,removeNumber,true,ItemOptSource.Talisman)){
			ItemFacade.removeItem(player, ItemCode.AllCanSuiPian, needAllCanNumber, true,ItemOptSource.Talisman);
			int level = talisman.getLevel();
			level = level < 1 ? 1 : level;
			String key = talisman.getTalismanRef().getId()+"_"+level;
			talismanRef =  talismanDataConfig.getTalismanLevelDataMap().get(key);
			talisman.setTalismanRef(talismanRef);
			ret = RuntimeResult.OK();
		}
		
		return ret;
	}
	public MGTalisman getTalisman() {
		return talisman;
	}

	public void setTalisman(MGTalisman talisman) {
		this.talisman = talisman;
	}
}
