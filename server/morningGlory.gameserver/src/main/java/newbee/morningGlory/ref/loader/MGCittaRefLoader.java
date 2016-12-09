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

import newbee.morningGlory.mmorpg.player.talisman.MGCittaRef;
import newbee.morningGlory.ref.RefKey;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public final class MGCittaRefLoader extends AbstractGameRefObjectLoader<MGCittaRef> {
	public MGCittaRefLoader() {
		super(RefKey.citta);
	}

	@Override
	protected MGCittaRef create() {
		return new MGCittaRef();
	}

	@Override
	protected void fillNonPropertyDictionary(MGCittaRef ref, JsonObject refData) {
		JsonElement effectData = refData.get("effectData");
		if (!effectData.isJsonNull())
			fillPropertyDictionary(ref.getEffectData(), effectData.getAsJsonObject());

	}

}
