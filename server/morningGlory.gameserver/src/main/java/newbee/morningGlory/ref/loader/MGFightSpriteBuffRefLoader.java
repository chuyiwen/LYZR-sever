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

import newbee.morningGlory.mmorpg.sprite.buff.MGFightSpriteBuffRef;
import newbee.morningGlory.mmorpg.sprite.buff.runtime.FightSpriteBuffClosureMgr;
import newbee.morningGlory.ref.RefKey;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class MGFightSpriteBuffRefLoader extends AbstractGameRefObjectLoader<MGFightSpriteBuffRef> {


	@Override
	protected MGFightSpriteBuffRef create() {
		// TODO Auto-generated method stub
		return new MGFightSpriteBuffRef();
	}

	public MGFightSpriteBuffRefLoader() {
		
		super(RefKey.d_buff);
		FightSpriteBuffClosureMgr.loadBuffClosures();
	}

	protected void fillNonPropertyDictionary(MGFightSpriteBuffRef ref, JsonObject refData) {

		String buffRefId = refData.getAsJsonObject().get("refId").getAsString();
		JsonObject property = refData.getAsJsonObject().get("property").getAsJsonObject();
		JsonElement effectData = refData.getAsJsonObject().get("effectData");
		JsonElement peridAffectData = refData.getAsJsonObject().get("peridAffectData");
		String attachClosure = refData.getAsJsonObject().get("attachClosure").getAsString();
		String detachClosure = refData.getAsJsonObject().get("detachClosure").getAsString();
		String periodAffectClosure = refData.getAsJsonObject().get("periodAffectClosure").getAsString();
		
		ref.setId(buffRefId);
		if(!effectData.isJsonNull())
			fillPropertyDictionary(ref.getEffectProperty(), effectData.getAsJsonObject());
		if(!peridAffectData.isJsonNull())
			fillPropertyDictionary(ref.getPeriodEffectProperty(), peridAffectData.getAsJsonObject());
		fillPropertyDictionary(ref.getProperty(), property);
		
		ref.setAttachClosure(FightSpriteBuffClosureMgr.getBuffClosures(attachClosure));
		ref.setDetachClosure(FightSpriteBuffClosureMgr.getBuffClosures(detachClosure));
		ref.setTickAffectClosure(FightSpriteBuffClosureMgr.getBuffClosures(periodAffectClosure));
	}
}
