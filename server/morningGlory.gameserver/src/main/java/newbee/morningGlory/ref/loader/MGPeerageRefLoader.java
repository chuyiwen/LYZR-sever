package newbee.morningGlory.ref.loader;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import newbee.morningGlory.mmorpg.player.peerage.MGPeerageRef;
import newbee.morningGlory.ref.RefKey;

import org.apache.log4j.Logger;

import sophia.mmorpg.player.itemBag.ItemPair;
import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class MGPeerageRefLoader extends AbstractGameRefObjectLoader<MGPeerageRef> {
	private static Logger logger = Logger.getLogger(MGPeerageRefLoader.class);

	public MGPeerageRefLoader() {
		super(RefKey.peerage);
	}

	@Override
	protected MGPeerageRef create() {
		return new MGPeerageRef();
	}

	protected void fillNonPropertyDictionary(MGPeerageRef ref, JsonObject refData) {
		if (logger.isDebugEnabled()) {
			logger.debug("load peerage refId:" + ref.getId());
		}
		// knightData
		JsonObject knightData = refData.get("knightData").getAsJsonObject();
		// knightSalary
		Iterator<JsonElement> knightSalary = knightData.get("knightSalary").getAsJsonArray().iterator();
		
		List<ItemPair> itemPairs = new ArrayList<ItemPair>();
		
		while(knightSalary.hasNext()){
			JsonObject jsonObject = knightSalary.next().getAsJsonObject();
			for(Entry<String, JsonElement> entry: jsonObject.entrySet()){
				if(entry.getKey().equals("gold")){
					int goldNum = entry.getValue().getAsInt();
					itemPairs.add(new ItemPair("gold", goldNum, false));
				}else if(entry.getKey().equals("unbindedGold")){
					int unbindedGoldNum = entry.getValue().getAsInt();
					itemPairs.add(new ItemPair("unbindedGold", unbindedGoldNum, false));
				}else if(entry.getKey().equals("bindedGold")){
					int bindedGoldNum = entry.getValue().getAsInt();
					itemPairs.add(new ItemPair("bindedGold", bindedGoldNum, false));
				}
			}
		}
		ref.setItemPairs(itemPairs);
		// upgradeSrcConsume
		JsonElement jsonElement = knightData.get("upgradeSrcConsume");
		if (!jsonElement.isJsonNull()) {
			JsonObject upgradeSrcConsume = jsonElement.getAsJsonArray().get(0).getAsJsonObject();
			int meritCondition = upgradeSrcConsume.get("merit").getAsInt();
			ref.setMeritCondition(meritCondition);
			int levelCondition = MGPropertyAccesser.getRoleGrade(ref.getProperty());
			ref.setLevelCondition(levelCondition);
		}
	}
}
