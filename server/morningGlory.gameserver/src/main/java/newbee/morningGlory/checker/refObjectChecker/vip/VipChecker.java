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
package newbee.morningGlory.checker.refObjectChecker.vip;

import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;

import newbee.morningGlory.checker.BaseRefChecker;
import newbee.morningGlory.mmorpg.vip.MGVipLevelDataRef;
import sophia.foundation.property.PropertyDictionary;
import sophia.game.GameRoot;
import sophia.game.ref.GameRefObject;
import sophia.mmorpg.player.itemBag.ItemPair;
import sophia.mmorpg.player.property.PlayerConfig;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;

public class VipChecker extends BaseRefChecker<MGVipLevelDataRef> {

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return "vip";
	}

	@Override
	public void check(GameRefObject gameRefObject) {
		MGVipLevelDataRef ref = (MGVipLevelDataRef)gameRefObject;
		PropertyDictionary pd = ref.getProperty();
		String buffRefId = MGPropertyAccesser.getBuffRefId(pd);
		String giftRefId = MGPropertyAccesser.getItemRefId(pd);
		GameRefObject buffref = GameRoot.getGameRefObjectManager().getManagedObject(buffRefId);
		if(buffref == null){
			error(gameRefObject, "vip 奖励加倍经验buff refId 不存在:"+buffRefId);
		}
		GameRefObject itemRef = GameRoot.getGameRefObjectManager().getManagedObject(giftRefId);
		if(itemRef == null){
			error(gameRefObject, "vip 每日礼包 道具refId 不存在:"+giftRefId);
		}
		
		for(Entry<Byte,Map<Integer,ItemPair>> entry : ref.getWeaponMaps().entrySet()){
			byte profession = entry.getKey();
			String professionRefId = PlayerConfig.getProfessionRefId(profession);
			if(StringUtils.isEmpty(professionRefId)){
				error(gameRefObject, "vip 武器 道具赠送 ，玩家职业设置错误 professionId:"+profession);
			}
			Map<Integer,ItemPair> map = entry.getValue();
			for(Entry<Integer,ItemPair> e : map.entrySet()){
				int level = e.getKey();
				ItemPair itemPair = e.getValue();
				String itemRefId = itemPair.getItemRefId();
				int number = itemPair.getNumber();
				if(number <= 0){
					error(gameRefObject, "vip 武器 道具refId 数量小于或等于 0:"+itemRefId);
				}
				GameRefObject weaponRef = GameRoot.getGameRefObjectManager().getManagedObject(itemRefId);
				if(weaponRef == null){
					error(gameRefObject, "vip 武器 道具refId 不存在:"+itemRefId);
				}
			}
		}
	}

	
}
