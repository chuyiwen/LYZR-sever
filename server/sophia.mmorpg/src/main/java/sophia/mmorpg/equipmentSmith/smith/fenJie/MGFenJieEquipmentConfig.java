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
package sophia.mmorpg.equipmentSmith.smith.fenJie;

import java.util.HashMap;
import java.util.Map;

import sophia.game.ref.AbstractGameRefObjectBase;

/**
 * 装备-分解配置数据
 */
public final class MGFenJieEquipmentConfig extends AbstractGameRefObjectBase {

	private static final long serialVersionUID = 3326836935139165647L;
	public static final String FenJieScroll_Id = "MGFenJieScrollEquipmentConfig_RefId";
	public static final String FenJie_Id = "MGFenJieEquipmentConfig_RefId";
	private Map<String, MGFenJieRef> fenJieConfigMap = new HashMap<String, MGFenJieRef>();
	private Map<Integer, MGFenJieScrollRef> fenJieScrollConfigMap = new HashMap<Integer, MGFenJieScrollRef>();

	public Map<String, MGFenJieRef> getFenJieConfigMap() {
		return fenJieConfigMap;
	}

	public void setFenJieConfigMap(Map<String, MGFenJieRef> fenJieConfigMap) {
		this.fenJieConfigMap = fenJieConfigMap;
	}

	public Map<Integer, MGFenJieScrollRef> getFenJieScrollConfigMap() {
		return fenJieScrollConfigMap;
	}

	public void setFenJieScrollConfigMap(Map<Integer, MGFenJieScrollRef> fenJieScrollConfigMap) {
		this.fenJieScrollConfigMap = fenJieScrollConfigMap;
	}

}
