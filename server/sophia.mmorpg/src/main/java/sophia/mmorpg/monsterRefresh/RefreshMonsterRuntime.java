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
package sophia.mmorpg.monsterRefresh;

import groovy.lang.Closure;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import sophia.mmorpg.base.scene.GameScene;
import sophia.mmorpg.utils.RuntimeResult;

public final class RefreshMonsterRuntime {
	private static final Logger logger = Logger.getLogger(RefreshMonsterRuntime.class);
	private final Map<String, Closure<RuntimeResult>> refreshMonsterToClosureMap;

	public RefreshMonsterRuntime(RefreshMonsterClosures refreshMonsterClosures) {
		refreshMonsterToClosureMap = new HashMap<>(refreshMonsterClosures.getMap());
	}

	public RuntimeResult createScene(GameScene scene, RefreshMonsterRefData refreshMonsterRefData) {

		if (logger.isDebugEnabled()) {
			logger.debug("场景创建：" + refreshMonsterRefData.getMonsterGroup().getMonsterRefId());
		}
		String closure = refreshMonsterRefData.getComponentRef(RefreshMonsterClosureRef.class).getRefreshMonsterClosure();

		Closure<RuntimeResult> runtime = refreshMonsterToClosureMap.get(closure);

		if (runtime == null) {
			return RuntimeResult.ParameterError("刷怪id=" + refreshMonsterRefData.getMonsterGroup().getMonsterRefId() + "在场景创建的时候，没有找到对应的使用调用函数。");
		} else {
			return runtime.call(scene, refreshMonsterRefData);
		}
	}

	public RuntimeResult monsterGroupAppear(GameScene scene, RefreshMonsterRefData refreshMonsterRefData) {

		if (logger.isDebugEnabled()) {
			logger.debug("怪物组出现激活：" + refreshMonsterRefData.getMonsterGroup().getMonsterRefId());
		}
		String closure = refreshMonsterRefData.getComponentRef(RefreshMonsterClosureRef.class).getRefreshMonsterClosure();

		Closure<RuntimeResult> runtime = refreshMonsterToClosureMap.get(closure);

		if (runtime == null) {
			return RuntimeResult.ParameterError("刷怪id=" + refreshMonsterRefData.getMonsterGroup().getMonsterRefId() + "在怪物组出现的时候，没有找到对应的使用调用函数。");
		} else {
			return runtime.call(scene, refreshMonsterRefData);
		}
	}

	public RuntimeResult monsterGroupDead(GameScene scene, RefreshMonsterRefData refreshMonsterRefData) {

		if (logger.isDebugEnabled()) {
			logger.debug("怪物组死亡激活：" + refreshMonsterRefData.getMonsterGroup().getMonsterRefId());
		}
		String closure = refreshMonsterRefData.getComponentRef(RefreshMonsterClosureRef.class).getRefreshMonsterClosure();

		Closure<RuntimeResult> runtime = refreshMonsterToClosureMap.get(closure);

		if (runtime == null) {
			return RuntimeResult.ParameterError("刷怪id=" + refreshMonsterRefData.getMonsterGroup().getMonsterRefId() + "在怪物组死亡的时候，没有找到对应的使用调用函数。");
		} else {
			return runtime.call(scene, refreshMonsterRefData);
		}
	}

}
