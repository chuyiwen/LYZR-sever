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

/**
 * 场景-NPC引用的数据
 */
public final class SceneNpcRefData {
	private String npcRefId;

	private int x;
	private int y;
	
	public SceneNpcRefData(String npcRefId, int x, int y) {
		super();
		this.npcRefId = npcRefId;
		this.x = x;
		this.y = y;
	}

	public String getNpcRefId() {
		return npcRefId;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
	
	
	

}
