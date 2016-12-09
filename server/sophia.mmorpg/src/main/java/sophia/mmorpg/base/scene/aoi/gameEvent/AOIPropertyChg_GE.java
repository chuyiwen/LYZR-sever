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
package sophia.mmorpg.base.scene.aoi.gameEvent;

import sophia.mmorpg.base.sprite.Sprite;
import sophia.mmorpg.base.sprite.aoi.SpriteSceneProperty;

/**
 *  场景信息改变
 */
public final class AOIPropertyChg_GE {
	
	private Sprite sprite;
	
	private SpriteSceneProperty spriteSceneProperty;

	public SpriteSceneProperty getSpriteSceneProperty() {
		return spriteSceneProperty;
	}

	public void setSpriteSceneProperty(SpriteSceneProperty spriteSceneProperty) {
		this.spriteSceneProperty = spriteSceneProperty;
	}

	public Sprite getSprite() {
		return sprite;
	}

	public void setSprite(Sprite sprite) {
		this.sprite = sprite;
	}
}
