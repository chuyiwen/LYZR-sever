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

import newbee.morningGlory.mmorpg.player.activity.resDownload.MGResDownLoadDataRef;
import newbee.morningGlory.mmorpg.player.activity.resDownload.MGResDownLoadMgr;
import newbee.morningGlory.ref.RefKey;
import sophia.mmorpg.player.itemBag.ItemPair;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class MGResDownloadDataRefLoader extends AbstractGameRefObjectLoader<MGResDownLoadDataRef> {

	public MGResDownloadDataRefLoader() {
		super(RefKey.resDownload);
	}

	@Override
	protected MGResDownLoadDataRef create() {
		return new MGResDownLoadDataRef();
	}

	@Override
	protected void fillNonPropertyDictionary(MGResDownLoadDataRef ref, JsonObject refData) {	
		JsonArray jsonArray = refData.getAsJsonArray("reward");
		for(JsonElement jsonElement : jsonArray){
			String refId = jsonElement.getAsJsonObject().get("refId").getAsString();
			int number = jsonElement.getAsJsonObject().get("number").getAsInt();
			byte bindStatus = jsonElement.getAsJsonObject().get("bindStatus").getAsByte();
			ItemPair itemPair = new ItemPair(refId, number, bindStatus);
			ref.getReward().add(itemPair);
			
		}
		MGResDownLoadMgr.addRewardIds(ref.getId());
	}

}
