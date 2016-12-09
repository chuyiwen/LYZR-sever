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

import sophia.foundation.util.ObjectPool;
import sophia.foundation.util.Position;
import sophia.mmorpg.base.sprite.Sprite;

public final class AOIMove_GE {

	private Sprite sprite;
	
	private Position srcPosition;

	public Sprite getSprite() {
		return sprite;
	}

	public void setSprite(Sprite sprite) {
		this.sprite = sprite;
	}

	public Position getSrcPosition() {
		return srcPosition;
	}

	public void setSrcPosition(Position destPosition) {
		this.srcPosition = destPosition;
	}
	
	public static final ObjectPool<AOIMove_GE> pool = new ObjectPool<AOIMove_GE>() {

		@Override
		protected AOIMove_GE instance() {
			return new AOIMove_GE();
		}

		@Override
		protected void onRecycle(AOIMove_GE obj) {
		}
	};
}
