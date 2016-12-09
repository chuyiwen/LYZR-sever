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
package newbee.morningGlory.mmorpg.union;

import java.util.Map;

public final class MGUnionOfficialRefMgr {
	private static final MGUnionOfficialRefMgr instance = new MGUnionOfficialRefMgr();

	private Map<Byte, MGUnionOfficialRef> unionOfficialRefMap;

	private MGUnionOfficialRefMgr() {

	}

	public static MGUnionOfficialRefMgr getInstance() {
		return instance;
	}

	public MGUnionOfficialRef getUnionOfficial(byte officialId) {
		return unionOfficialRefMap.get(officialId);
	}

	public Map<Byte, MGUnionOfficialRef> getUnionOfficialMap() {
		return unionOfficialRefMap;
	}

	public void setUnionOfficialMap(Map<Byte, MGUnionOfficialRef> unionOfficialMap) {
		this.unionOfficialRefMap = unionOfficialMap;
	}

}
