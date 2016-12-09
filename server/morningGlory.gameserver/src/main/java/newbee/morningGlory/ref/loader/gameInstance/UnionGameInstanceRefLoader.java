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
package newbee.morningGlory.ref.loader.gameInstance;

import org.apache.commons.lang3.StringUtils;

import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;
import newbee.morningGlory.mmorpg.player.unionGameInstance.UnionGameInstanceRef;
import newbee.morningGlory.ref.RefKey;
import newbee.morningGlory.ref.loader.AbstractGameRefObjectLoader;

import com.google.gson.JsonObject;

public class UnionGameInstanceRefLoader extends AbstractGameRefObjectLoader<UnionGameInstanceRef> {

	public UnionGameInstanceRefLoader() {
		super(RefKey.unionGameInstance);
	}

	@Override
	protected UnionGameInstanceRef create() {
		return new UnionGameInstanceRef();
	}

	@Override
	protected void fillNonPropertyDictionary(UnionGameInstanceRef ref, JsonObject refData) {	
		String openTime = MGPropertyAccesser.getOpenTime(ref.getProperty());
		String[] split = StringUtils.split(openTime, "\\|");
		ref.setOpenTime(split[0]);
		ref.setEndTime(split[1]);
		super.fillNonPropertyDictionary(ref, refData);
	}

}
