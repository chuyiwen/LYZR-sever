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
package sophia.mmorpg.base.sprite.aoi;

import sophia.foundation.util.Position;

public final class PathInfo {
	
	private long serverStamp;
	private Position startPosition = new Position(); 
	private Position endPosition = new Position();
	private SpriteInfo spriteInfo;
	
	
	public Position getStartPosition() {
		return startPosition;
	}
			
	public void setStartPosition(int x, int y) {
		startPosition.setX(x);
		startPosition.setY(y);
	}
	
	public Position getEndPosition() {
		return endPosition;
	}
	
	public void setEndPosition(int x, int y) {
		endPosition.setX(x);
		endPosition.setY(y);
	}
	
	public long getServerStamp() {
		return serverStamp;
	}
	
	public void setServerStamp(long serverStamp) {
		this.serverStamp = serverStamp;
	}

	@Override
	public String toString() {
		return "Path[SpriteInfo = " + spriteInfo + " (" + startPosition + "," + endPosition + ")]";
	}

	public SpriteInfo getSpriteInfo() {
		return spriteInfo;
	}

	public void setSpriteInfo(SpriteInfo spriteInfo) {
		this.spriteInfo = spriteInfo;
	}
}
