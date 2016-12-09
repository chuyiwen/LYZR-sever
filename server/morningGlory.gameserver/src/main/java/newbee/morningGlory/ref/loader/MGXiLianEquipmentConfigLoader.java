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
import sophia.mmorpg.equipmentSmith.smith.xiLian.MGXiLianDataRef;

import com.google.gson.JsonObject;

public final class MGXiLianEquipmentConfigLoader extends AbstractGameRefObjectLoader<MGXiLianDataRef> {

	@Override
	protected MGXiLianDataRef create() {
		return new MGXiLianDataRef();
	}

	public MGXiLianEquipmentConfigLoader() {
		super(RefKey.equipWashProperty);
	}

	@Override
	protected void fillNonPropertyDictionary(MGXiLianDataRef ref, JsonObject refData) {
		ref.setId(refData.getAsJsonObject().get("refId").getAsString()+MGXiLianDataRef.keyWord);
		fillPropertyDictionary(ref.getProperty(), refData.getAsJsonObject().get("property").getAsJsonObject());
	}
}
