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
import sophia.mmorpg.base.sprite.aoi.PositionInfo;

public final class AOIStopMove_GE {
	
	private Position srcPosition = new Position(); 
	
	private Sprite sprite;
	
	private PositionInfo positionInfo;
	
	public PositionInfo getPositionInfo() {
		return positionInfo;
	}

	public void setPositionInfo(PositionInfo positionInfo) {
		this.positionInfo = positionInfo;
	}
	
	public Position getSrcPosition() {
		return srcPosition;
	}

	public void setSrcPosition(Position srcPosition) {
		this.srcPosition.setPosition(srcPosition.getX(), srcPosition.getY());
	}

	public Sprite getSprite() {
		return sprite;
	}

	public void setSprite(Sprite sprite) {
		this.sprite = sprite;
	}

	public static final ObjectPool<AOIStopMove_GE> pool = new ObjectPool<AOIStopMove_GE>() {

		@Override
		protected AOIStopMove_GE instance() {
			return new AOIStopMove_GE();
		}

		@Override
		protected void onRecycle(AOIStopMove_GE obj) {
			
		}
	};
}
