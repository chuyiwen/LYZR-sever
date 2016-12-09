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

package sophia.mmorpg.monsterRefresh

import sophia.mmorpg.base.scene.GameScene
import sophia.mmorpg.utils.RuntimeResult

class RefreshMonsterClosures {

	Map<String, Closure<RuntimeResult>> map = new HashMap<>();
	RefreshMonsterClosures(){
		map.put("create_scene_closure", create_scene_closure);
		map.put("monster_group_appear_closure", monster_group_appear_closure);
		map.put("monster_group_dead_closure", monster_group_dead_closure);
	}

	
	/**
	 * 场景创建
	 */
	Closure<RuntimeResult> create_scene_closure = { GameScene scene, RefreshMonsterRefData refreshMonsterRefData ->
		RuntimeResult result = RuntimeResult.OK();
		
		return result;
	}
	
	/**
	 * 怪物组出现
	 */
	Closure<RuntimeResult> monster_group_appear_closure = { GameScene scene, RefreshMonsterRefData refreshMonsterRefData ->
		RuntimeResult result = RuntimeResult.OK();
		
		return result;
	}

	/**
	 * 怪物组死亡
	 */
	Closure<RuntimeResult> monster_group_dead_closure = { GameScene scene, RefreshMonsterRefData refreshMonsterRefData ->
		RuntimeResult result = RuntimeResult.OK();
		
		return result;
	}

}
