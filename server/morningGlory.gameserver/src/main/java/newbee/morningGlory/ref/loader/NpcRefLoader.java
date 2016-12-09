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
import java.util.Iterator;
import java.util.List;

import newbee.morningGlory.ref.RefKey;

import org.apache.log4j.Logger;

import sophia.mmorpg.npc.ref.JobType;
import sophia.mmorpg.npc.ref.NpcJobManager;
import sophia.mmorpg.npc.ref.NpcRef;
import sophia.mmorpg.npc.ref.NpcShop;
import sophia.mmorpg.npc.ref.NpcTransfers;
import sophia.mmorpg.npc.ref.SingleTransfer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class NpcRefLoader extends AbstractGameRefObjectLoader<NpcRef> {

	public static final Logger logger = Logger.getLogger(NpcRefLoader.class);

	public NpcRefLoader() {
		super(RefKey.npc);
	}

	@Override
	protected NpcRef create() {
		return new NpcRef();
	}

	@Override
	protected void fillNonPropertyDictionary(NpcRef ref, JsonObject refData) {
		JsonElement jobData = refData.get("job");
		NpcJobManager npcJobManager = new NpcJobManager();
		if (jobData.isJsonArray()) {
			Iterator<JsonElement> jobs = jobData.getAsJsonArray().iterator();
			while (jobs.hasNext()) {
				JsonObject elem = jobs.next().getAsJsonObject();
				int jobType = elem.get("jobType").getAsInt();
				// 传送功能NPC
				if (jobType == JobType.Job_Type_Transfer) {
					JsonElement transListData = elem.get("transList");
					NpcTransfers npcTransfers = new NpcTransfers();
					List<SingleTransfer> transferList = new ArrayList<>();
					if (transListData.isJsonArray()) {
						Iterator<JsonElement> transList = transListData.getAsJsonArray().iterator();
						int index = 0;
						while (transList.hasNext()) {
							JsonObject temp = transList.next().getAsJsonObject();
							String name = temp.get("name").getAsString();
							String targetScene = temp.get("targetScene").getAsString();
							int tranferInId = temp.get("tranferInId").getAsInt(); // 对应scene表里的tranferInId字段
							SingleTransfer transfer = new SingleTransfer();
							transfer.setId(index);
							transfer.setName(name);
							transfer.setTargetScene(targetScene);
							transfer.setTargetTransIn(tranferInId);
							index++;
							transferList.add(transfer);
						}
						npcTransfers.setTransferData(transferList);
					}
					npcJobManager.addJob(npcTransfers);
				}
				if (jobType == JobType.Job_Type_Store) {
					JsonElement shopListData = elem.get("shopList");
					NpcShop npcShop = new NpcShop();
					List<String> List = new ArrayList<>();
					if (shopListData.isJsonArray()) {
						Iterator<JsonElement> shopList = shopListData.getAsJsonArray().iterator();
						while (shopList.hasNext()) {
							JsonObject temp = shopList.next().getAsJsonObject();
							String shopId = temp.get("shopID").getAsString();
							List.add(shopId);
						}
						npcShop.setShopList(List);
					}
					npcJobManager.addJob(npcShop);
				}
			}
		}
		ref.setNpcJobManager(npcJobManager);

		super.fillNonPropertyDictionary(ref, refData);
	}
}
