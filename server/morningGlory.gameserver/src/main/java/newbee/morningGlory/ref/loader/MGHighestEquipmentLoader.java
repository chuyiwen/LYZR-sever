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

import newbee.morningGlory.ref.RefKey;

import org.apache.log4j.Logger;

import sophia.mmorpg.equipmentSmith.smith.highestEquipment.HighestEquipmentRef;

import com.google.gson.JsonObject;

public class MGHighestEquipmentLoader extends AbstractGameRefObjectLoader<HighestEquipmentRef> {
	private static final Logger logger = Logger.getLogger(MGHighestEquipmentLoader.class);

	@Override
	protected HighestEquipmentRef create() {
		return new HighestEquipmentRef();
	}

	public MGHighestEquipmentLoader() {
		super(RefKey.hightestEquipment);
	}

	@Override
	protected void fillNonPropertyDictionary(HighestEquipmentRef ref, JsonObject refData) {

		String probability = refData.getAsJsonObject().get("probability").getAsString();
		String randomCount = refData.getAsJsonObject().get("randomCount").getAsString();
		int minRate = refData.getAsJsonObject().get("minRate").getAsInt();
		int maxRate = refData.getAsJsonObject().get("maxRate").getAsInt();
		String[] proStr = probability.split(",");
		String[] countStr = randomCount.split(",");
		int[] pro = new int[proStr.length];
		int[] count = new int[countStr.length];
		for (int i = 0; i < proStr.length; i++) {
			pro[i] = Integer.parseInt(proStr[i]);
		}

		for (int j = 0; j < countStr.length; j++) {
			count[j] = Integer.parseInt(countStr[j]);
		}
		ref.setProbability(pro);
		ref.setRandomCount(count);
		ref.setMinRate(minRate);
		ref.setMaxRate(maxRate);
	}

}
