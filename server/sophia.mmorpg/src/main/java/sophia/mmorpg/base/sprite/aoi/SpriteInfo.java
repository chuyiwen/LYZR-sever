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

import sophia.mmorpg.base.sprite.Sprite;

public final class SpriteInfo {
	
	private byte spriteType;
	
	private String spriteId;
	
	private String spriteName;
	
	public SpriteInfo(String spriteId, String spriteName, byte spriteType) {
		this.spriteId = spriteId;
		this.spriteName = spriteName;
		this.spriteType = spriteType;
	}
	
	public SpriteInfo(Sprite sprite) {
		this.spriteId = sprite.getId();
		this.spriteName = sprite.getName();
		this.spriteType = sprite.getSpriteType();
	}

	@Override
	public String toString() {
		return "SpriteInfo [spriteType=" + spriteType + ", spriteId=" + spriteId + ", spriteName=" + spriteName + "]";
	}

	public byte getSpriteType() {
		return spriteType;
	}

	public void setSpriteType(byte spriteType) {
		this.spriteType = spriteType;
	}

	public String getSpriteId() {
		return spriteId;
	}

	public void setSpriteId(String spriteId) {
		this.spriteId = spriteId;
	}

	public String getSpriteName() {
		return spriteName;
	}

	public void setSpriteName(String spriteName) {
		this.spriteName = spriteName;
	}
}
