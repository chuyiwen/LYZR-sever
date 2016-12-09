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
package sophia.mmorpg.base.scene.ref;

import sophia.mmorpg.ref.editorgen.MGPropertyAccesser;

public class SceneRef extends AbstractGameSceneRef {
	private static final long serialVersionUID = 4816020179028700640L;

	/** 城市地图 */
	public static final int City = 0;
	/** 危险区域 */
	public static final int Danger = 1;
	/** 活动地图 */
	public static final int Activity = 2;
	/** 副本地图 */
	public static final int FuBen = 3;
	/** 新手村地图*/
	public static final int XinShouCun = 4;
	
	@Override
	public String toString() {
		return "SceneRef [id=" + id + ", name=" + MGPropertyAccesser.getName(getProperty()) + ", getType()=" + getType() + "]";
	}
}
