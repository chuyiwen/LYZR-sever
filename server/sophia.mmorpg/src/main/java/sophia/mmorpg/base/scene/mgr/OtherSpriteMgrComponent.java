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
package sophia.mmorpg.base.scene.mgr;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import sophia.foundation.util.DebugUtil;
import sophia.game.component.communication.GameEvent;
import sophia.mmorpg.base.ConcreteComponent;
import sophia.mmorpg.base.scene.GameScene;
import sophia.mmorpg.base.sprite.Sprite;

public class OtherSpriteMgrComponent extends ConcreteComponent<GameScene> {
	
	private static final Logger logger = Logger.getLogger(OtherSpriteMgrComponent.class);

	private Map<String, Sprite> otherSpriteMap = new ConcurrentHashMap<>();
	
	public Sprite getSprite(String spriteId) {
		return otherSpriteMap.get(spriteId);
	}
	
	public void addSprite(Sprite sprite) {
		otherSpriteMap.put(sprite.getId(), sprite);
	}
	
	public void removeSprite(Sprite sprite) {
		otherSpriteMap.remove(sprite.getId());
	}
	
	public void sceneTick(GameEvent<?> event) {
		if (otherSpriteMap.isEmpty()) {
			return;
		}
		
		for (Sprite sprite : otherSpriteMap.values()) {
			try {
				sendGameEvent(event, sprite.getId());
			} catch (Exception e) {
				logger.error(DebugUtil.printStack(e));
			}
		}
	}
}
