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
package newbee.morningGlory.ref.loader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import sophia.game.GameRoot;
import sophia.mmorpg.item.ref.ItemRef;
import sophia.mmorpg.monster.drop.MonsterDropMgr;
import sophia.mmorpg.monster.ref.drop.DirectDropRef;
import sophia.mmorpg.monster.ref.drop.DropEntryRef;
import sophia.mmorpg.monster.ref.drop.LevelDropRef;
import sophia.mmorpg.monster.ref.drop.MonsterDropRef;
import sophia.mmorpg.player.itemBag.ItemPair;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class MonsterDropRefLoader extends AbstractGameRefObjectLoader<MonsterDropRef> {
	
	private static final Logger logger = Logger.getLogger(MonsterDropRefLoader.class);
	
	public MonsterDropRefLoader(String refKey) {
		super(refKey);
	}

	@Override
	protected MonsterDropRef create() {
		return new MonsterDropRef();
	}
	
	protected DropEntryRef createDropEntryRef(String dropType, JsonObject jsonDrop) {
		DropEntryRef dropEntryRef = new DropEntryRef();
		dropEntryRef.setProbability(jsonDrop.get("probability").getAsInt());
		dropEntryRef.setMinUnbindedCopperNumber(jsonDrop.get("mingoldNumber").getAsInt());
		dropEntryRef.setMaxUnbindedCopperNumber(jsonDrop.get("maxgoldNumber").getAsInt());
		dropEntryRef.setMinItemNumber(jsonDrop.get("minItemNumber").getAsInt());
		dropEntryRef.setMaxItemNumber(jsonDrop.get("maxItemNumber").getAsInt());
		
		JsonElement jsonElement = jsonDrop.get("dropGroup");
		if (!jsonElement.isJsonNull()){
			JsonObject dropGroup = jsonDrop.get("dropGroup").getAsJsonObject();
			if (dropGroup == null) {
				logger.error("Load MonsterDropRef DropGroup Error " + dropType);
			}
			
			for (Map.Entry<String, JsonElement> entry : dropGroup.entrySet()) {	
				JsonObject drop = entry.getValue().getAsJsonObject();
				dropEntryRef.setDropGroupId(drop.get("groupId").getAsString());
				dropEntryRef.setCoinItemRefId(drop.get("coins").getAsString());
				dropEntryRef.setMinCoinNumber(drop.get("minNumber").getAsInt());
				dropEntryRef.setMaxCoinNumber(drop.get("maxNumber").getAsInt());
				boolean bindStatus = (drop.get("bindStatus").getAsByte() == 1);
				
				List<ItemPair> itemPairList = new ArrayList<>();
				String strItemIdList = drop.get("itemList").getAsString();
				String[] strArray = strItemIdList.split(",");
				for (String itemRefId : strArray) {
					itemRefId = itemRefId.trim();
					ItemRef itemRef = (ItemRef) GameRoot.getGameRefObjectManager().getManagedObject(itemRefId);
					if (itemRef == null) {
						logger.error("Load MonsterDropRef can't find item " + itemRefId);
						continue;
					}
					
					ItemPair itemPair = new ItemPair(itemRefId, 1, bindStatus); 
					itemPairList.add(itemPair);
				}
				
				dropEntryRef.setItemPairList(itemPairList);
				
				return dropEntryRef;
			}
		}
		// 如果dropGroup数据没有读到，则读取失败
		return null;
	}
	
	protected LevelDropRef createLevelDropRef(JsonObject jsonDrop) {
		// 匹配[1, 4]
		Pattern p = Pattern.compile("\\[{1}\\s?(\\d+)\\s?,\\s?(\\d+)\\s?\\]{1}");
		String levelRange = jsonDrop.get("levelRange").getAsString();
		Matcher match = p.matcher(levelRange);
		if (!match.find()) {
			logger.error("Load MonsterDropRef LevelRange Error " + levelRange);
			return null;
		} 
		
		LevelDropRef levelDropRef = new LevelDropRef();
		levelDropRef.setLevelRange(levelRange);
		levelDropRef.setMinLevel(Integer.valueOf(match.group(1)));
		levelDropRef.setMaxLevel(Integer.valueOf(match.group(2)));
		DropEntryRef dropEntryRef = createDropEntryRef(levelRange, jsonDrop);
		if (dropEntryRef == null) {
			logger.error("Load MonsterDropRef DropGroup Error " + levelRange);
			return null;
		}
		
		levelDropRef.getLevelDropRefList().add(dropEntryRef);
		
		return levelDropRef;
	}

	@Override
	protected void fillNonPropertyDictionary(MonsterDropRef ref, JsonObject refData) {
		JsonObject jsonObject = refData.get("drop").getAsJsonObject();
		
		// 等级掉落
		JsonObject jsonLevelDrop = jsonObject.get("level_drop").getAsJsonObject();
		List<LevelDropRef> levelDrop = ref.getLevelDrop();
		for (Map.Entry<String, JsonElement> entry : jsonLevelDrop.get("drop").getAsJsonObject().entrySet()) {
			// 单个等级掉落
			if (entry.getValue().isJsonObject()) {
				JsonObject jsonDrop = entry.getValue().getAsJsonObject();
				LevelDropRef levelDropRef = createLevelDropRef(jsonDrop);
				if (levelDropRef == null) {
					continue;
				}
				
				levelDrop.add(levelDropRef);
				continue;
			} 
			
			// 多行等级掉落
			JsonArray jsonArray = entry.getValue().getAsJsonArray();
			Iterator<JsonElement> iterator = jsonArray.iterator();
			while (iterator.hasNext()) {
				JsonObject jsonDrop = iterator.next().getAsJsonObject();
				LevelDropRef levelDropRef = createLevelDropRef(jsonDrop);
				if (levelDropRef == null) {
					continue;
				}

				levelDrop.add(levelDropRef);
			}
		}
		
		// 等级掉落排序，按照最小等级，最大等级升序排序
		Collections.sort(levelDrop, new Comparator<LevelDropRef>() {
			@Override
			public int compare(LevelDropRef o1, LevelDropRef o2) {
				Integer minLevel1 = o1.getMinLevel();
				Integer minLevel2 = o2.getMinLevel();
				int flag = minLevel1.compareTo(minLevel2);
				if (flag == 0) {
					Integer maxLevel1 = o1.getMaxLevel();
					Integer maxLevel2 = o2.getMaxLevel();
					return maxLevel1.compareTo(maxLevel2);
				}
				
				return flag;
			}
		});
		
		// 怪物掉落
		JsonObject jsonMonsterDrop = jsonObject.get("monster_drop").getAsJsonObject();
		Map<String, DirectDropRef> monsterDrop = ref.getMonsterDrop();
		for (Map.Entry<String, JsonElement> entry : jsonMonsterDrop.get("drop").getAsJsonObject().entrySet()) {
			DirectDropRef directDropRef = new DirectDropRef();
			// 单个怪物掉落
			if (entry.getValue().isJsonObject()) {
				JsonObject jsonDrop = entry.getValue().getAsJsonObject();
				String monsterRefId = entry.getKey();
				DropEntryRef dropEntryRef = createDropEntryRef(monsterRefId, jsonDrop);
				if (dropEntryRef == null) {
					logger.error("Load MonsterDropRef MonsterDrop Error " + monsterRefId);
					continue;
				}
				
				directDropRef.setMonsterRefId(monsterRefId);
				directDropRef.getDropRefList().add(dropEntryRef);
				monsterDrop.put(monsterRefId, directDropRef);
				continue;
			}

			// 多行怪物掉落
			JsonArray jsonArray = entry.getValue().getAsJsonArray();
			Iterator<JsonElement> iterator = jsonArray.iterator();
			while (iterator.hasNext()) {
				JsonObject jsonDrop = iterator.next().getAsJsonObject();
				String monsterRefId = entry.getKey();
				DropEntryRef dropEntryRef = createDropEntryRef(monsterRefId, jsonDrop);
				if (dropEntryRef == null) {
					logger.error("Load MonsterDropRef MonsterDrop Error " + monsterRefId);
					continue;
				}
		
				directDropRef.getDropRefList().add(dropEntryRef);
				directDropRef.setMonsterRefId(monsterRefId);
				monsterDrop.put(monsterRefId, directDropRef);
			}
		}
		
		MonsterDropMgr.setMonsterDropRef(ref);
	}

}
